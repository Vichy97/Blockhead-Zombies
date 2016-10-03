package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.starcat.boxhead.physics.MotionState;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Entity implements Steerable<Vector3>, Disposable {
    protected static Vector3 rotationVector = Vector3.Y;

    protected ModelInstance modelInstance;
    protected MotionState motionState;
    protected btRigidBody rigidBody;

    protected Quaternion rotation;
    protected Vector3 position;
    protected Vector3 temp;

    protected float boundingRadius;
    protected float maxLinearSpeed, maxLinearAcceleration;
    protected float maxAngularSpeed, maxAngularAcceleration;
    protected boolean tagged;
    float currentRotation = -90;

    protected SteeringBehavior<Vector3> behavior;
    protected SteeringAcceleration<Vector3> steeringOutput;

    public Entity() {
        rotation = new Quaternion();
        motionState = new MotionState();
        position = new Vector3();
        temp = new Vector3();

        this.maxLinearSpeed = 10;
        this.maxLinearAcceleration = 100;
        this.maxAngularSpeed = 10;
        this.maxAngularAcceleration = 100;

        this.tagged = false;
        this.steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());
    }

    public void init(Vector3 position, ModelInstance modelInstance,  btRigidBody rigidBody) {
        this.modelInstance = modelInstance;
        this.rigidBody = rigidBody;

        motionState.transform = modelInstance.transform.setTranslation(position);
        rigidBody.setMotionState(motionState);

        rigidBody.setWorldTransform(rigidBody.getWorldTransform());
        rigidBody.setAngularVelocity(Vector3.Zero);
        rigidBody.setLinearVelocity(Vector3.Zero);
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody) {
        this.modelInstance = modelInstance;
        this.rigidBody = rigidBody;

        motionState.transform = modelInstance.transform;
        rigidBody.setMotionState(motionState);

        rigidBody.setWorldTransform(transform);
        rigidBody.setAngularVelocity(Vector3.Zero);
        rigidBody.setLinearVelocity(Vector3.Zero);
    }

    public void update(float delta) {
        modelInstance.transform.getTranslation(position);

        if(behavior != null) {
            behavior.calculateSteering(steeringOutput);
            applySteering(delta);
        }
    }

    public void applySteering(float delta) {
        boolean anyAcceleration  = false;

        if(!steeringOutput.linear.isZero()) {
            Vector3 force = steeringOutput.linear.scl(delta);
            rigidBody.setLinearVelocity(force);
            anyAcceleration = true;
        }

        if(!getLinearVelocity().isZero(getZeroLinearSpeedThreshold())) {
                //float newOrientation = vectorToAngle(getLinearVelocity());]
                //Vector3 vel = getLinearVelocity();
                //vel.x /= delta;
                //vel.z /= delta;
                setOrientation(vectorToAngle(steeringOutput.linear) / Gdx.graphics.getDeltaTime());
                anyAcceleration = true;
                //currentRotation = newOrientation;
        }

        //TODO: possibly use vector.rotate
        /*if(anyAcceleration) {
            Vector3 velocity = rigidBody.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                rigidBody.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
            }
            Vector3 angVelocity = rigidBody.getAngularVelocity();
            if (angVelocity.y > getMaxAngularSpeed()) {
                angVelocity.y = getMaxAngularSpeed();
                rigidBody.setAngularVelocity(angVelocity);
            }
        }*/
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(modelInstance);
    }

    public btRigidBody getRigidBody() {
        return rigidBody;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }



    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public float getOrientation() {
        return rigidBody.getOrientation().getYawRad();
    }

    @Override
    public void setOrientation(float orientation) {
        rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(0, 1, 0, orientation - currentRotation));
        currentRotation = orientation;
    }

    @Override
    public float vectorToAngle(Vector3 vector) {
        return (float)Math.atan2(-vector.z, vector.x);
    }

    @Override
    public Vector3 angleToVector(Vector3 outVector, float angle) {
        outVector.z = -(float)Math.sin(angle);
        outVector.y = 0;
        outVector.x = (float)Math.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector3> newLocation() {
        return null;
    }

    @Override
    public Vector3 getLinearVelocity() {
        return rigidBody.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return rigidBody.getAngularVelocity().y;
    }

    @Override
    public float getBoundingRadius() {
        return 0;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return .001f;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    public void setBehavior(SteeringBehavior<Vector3> behavior) {
        this.behavior = behavior;
    }

    public SteeringBehavior<Vector3> getBehavior() {
        return behavior;
    }


    public float getCurrentRotation() {
        return currentRotation;
    }


    @Override
    public void dispose() {
        rigidBody.getCollisionShape().dispose();
        rigidBody.dispose();
        motionState.dispose();
    }
}
