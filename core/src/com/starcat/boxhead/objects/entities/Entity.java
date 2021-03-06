package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.objects.DynamicGameObject;

/**
 * Created by Vincent on 8/12/2016.
 *
 * All Entities (player and enemies) extend this class
 */
public class Entity extends DynamicGameObject implements Steerable<Vector3> {

    protected float maxLinearSpeed, maxLinearAcceleration;
    protected float maxAngularSpeed, maxAngularAcceleration;
    protected boolean tagged;

    protected float currentRotation = -90;
    protected boolean moving = false;

    protected Arrive<Vector3> behavior;
    protected SteeringAcceleration<Vector3> steeringOutput;

    protected float hitpoints = 100;
    protected float maxHitpoints = 100;



    public Entity() {
        super();

        this.maxLinearSpeed = 25;
        this.maxLinearAcceleration = 25;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 5;
        this.tagged = false;
        this.steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());
    }



    public void init(Vector3 position, ModelInstance modelInstance, btRigidBody rigidBody) {
        super.init(position, modelInstance, rigidBody);
        hitpoints = 100;
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody) {
        super.init(transform, modelInstance, rigidBody);
        hitpoints = 100;
    }



    public void applySteering(float delta) {
        if(!steeringOutput.linear.isZero()) {
            steeringOutput.linear.y = 0;
            Vector3 force = steeringOutput.linear.scl(delta);
            rigidBody.setLinearVelocity(force);
        }

        setOrientation((float)Math.toDegrees(vectorToAngle(getLinearVelocity())));
    }



    public void damage(float hitpoints) {
        this.hitpoints -= hitpoints;
    }

    public void heal(float hitpoints) {
        this.hitpoints += hitpoints;

        if (this.hitpoints > maxHitpoints) {
            this.hitpoints = maxHitpoints;
        }
    }



    public void setBehavior(Arrive<Vector3> behavior) {
        this.behavior = behavior;
    }

    public Arrive<Vector3> getBehavior() {
        return behavior;
    }

    public SteeringAcceleration<Vector3> getSteeringOutput() {
        return steeringOutput;
    }

    public float getHitpoints() {
        return hitpoints;
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



    @Override
    public void reset() {
        currentRotation = -90;
        super.reset();
    }
}
