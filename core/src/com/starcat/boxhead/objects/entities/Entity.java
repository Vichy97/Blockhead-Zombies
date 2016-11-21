package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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

    protected float currentRotation = -45;
    protected float currentRotationAngle = 45;
    protected int direction = 0;
    //to prevent the entity from rapidly switching directions
    protected boolean canChangeDirection = true;
    protected boolean moving = false;

    protected Arrive<Vector3> behavior;
    protected SteeringAcceleration<Vector3> steeringOutput;
    protected Timer timer;

    private int hitpoints = 100;
    private boolean drawHitpoints = false;



    public Entity() {
        super();

        this.maxLinearSpeed = 50;
        this.maxLinearAcceleration = 50;
        this.maxAngularSpeed = 0;
        this.maxAngularAcceleration = 0;

        this.tagged = false;
        this.steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());

        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                canChangeDirection = true;
            }
        }, .5f, .5f);
    }

    public void init(Vector3 position, ModelInstance modelInstance, btRigidBody rigidBody) {
        super.init(position, modelInstance, rigidBody);
        hitpoints = 100;
        this.maxLinearSpeed = 50;
        this.maxLinearAcceleration = 50;
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody) {
        super.init(transform, modelInstance, rigidBody);
        hitpoints = 100;
        this.maxLinearSpeed = 50;
        this.maxLinearAcceleration = 50;
    }



    public void update(float delta) {
        modelInstance.transform.getTranslation(position);
    }

    public void applySteering(float delta) {

        if(!steeringOutput.linear.isZero()) {
            Vector3 speed = temp.set(maxLinearSpeed, 0, maxLinearSpeed);
            float angle = (int)(vectorToAngle(steeringOutput.linear) / delta + 180);
            float targetAngle = 0;
            moving = true;

            if(canChangeDirection) {
                direction = GameUtils.getDirectionFromAngle(angle);
                canChangeDirection = false;
            }

            switch(direction) {
                case 1: {
                    targetAngle = 135;
                    break;
                } case 2: {
                    targetAngle = 90;
                    break;
                } case 3: {
                    targetAngle = 45;
                    break;
                } case 4: {
                    targetAngle = 360;
                    break;
                } case 5: {
                    targetAngle = 315;
                    break;
                } case 6: {
                    targetAngle = 270;
                    break;
                } case 7: {
                    targetAngle = 225;
                    break;
                } case 8: {
                    targetAngle = 180;
                    break;
                }
            }
            speed.rotate(rotationVector, targetAngle);
            rigidBody.setLinearVelocity(speed.scl(delta));
            setOrientation(targetAngle);
            currentRotationAngle = targetAngle;
        } else {
            moving = false;
        }

    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(modelInstance);
    }



    public void damage(int hitpoints) {
        this.hitpoints -= hitpoints;
    }

    public void heal(int hitpoints) {
        this.hitpoints += hitpoints;
    }

    public void drawHitpoints() {

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

    public int getHitpoints() {
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
        currentRotation = -45;
        currentRotationAngle = 45;
        super.reset();
    }

    @Override
    public void dispose() {

    }
}
