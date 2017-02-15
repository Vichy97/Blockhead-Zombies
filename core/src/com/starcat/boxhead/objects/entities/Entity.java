package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Timer;
import com.starcat.boxhead.objects.DynamicGameObject;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 8/12/2016.
 *
 * All Entities (player and enemies) extend this class
 */
public class Entity extends DynamicGameObject implements Steerable<Vector3> {

    /*
     * required fields for the Steerable interface
     * however I don't make use of these because speed
     * is calculated manually (with instant acceleration)
     * because entities are limited to 8 directions
     */
    protected float maxLinearSpeed, maxLinearAcceleration;
    protected float maxAngularSpeed, maxAngularAcceleration;
    protected boolean tagged;

    protected float currentRotation = -90;
    //to prevent the entity from rapidly switching directions
    protected boolean canChangeDirection = true;
    protected boolean moving = false;

    protected SteeringBehavior<Vector3> behavior;
    protected SteeringAcceleration<Vector3> steeringOutput;

    protected float hitpoints = 100;
    protected float maxHitpoints = 100;



    public Entity() {
        super();

        this.maxLinearSpeed = 200;
        this.maxLinearAcceleration = 100;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 5;

        this.tagged = false;
        this.steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());

        Timer.instance().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                canChangeDirection = true;
            }
        }, .5f, .5f);
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
        boolean anyAccelerations = false;

        if(!steeringOutput.linear.isZero()) {
            steeringOutput.linear.y = 0;
            Vector3 force  = steeringOutput.linear.scl(delta);
            rigidBody.setLinearVelocity(force);
            anyAccelerations = true;
        }

        setOrientation((float)Math.toDegrees(vectorToAngle(getLinearVelocity())));

        if (anyAccelerations) {
            Vector3 velocity = steeringOutput.linear;
            float currentSpeedSquared = velocity.len2();
            if (currentSpeedSquared > maxLinearSpeed * maxLinearSpeed) {
                rigidBody.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquared)));
            }
        }
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



    public void setBehavior(SteeringBehavior<Vector3> behavior) {
        this.behavior = behavior;
    }

    public SteeringBehavior<Vector3> getBehavior() {
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
