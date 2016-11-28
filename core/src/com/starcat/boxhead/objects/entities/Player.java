package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.objects.weapons.Gun;
import com.starcat.boxhead.objects.weapons.Pistol;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Player extends Entity {

    private Gun currentWeapon;
    private Vector3 speed;
    private float maxSpeed;
    private float currentRotationAngle = 0;
    private int direction = 2;
    private AnimationController walkAnimationController;
    private AnimationController shootAnimationController;



    public void init(Vector3 position, float maxSpeed, btRigidBody rigidBody) {
        super.init(position, new ModelInstance(AssetLoader.boxhead), rigidBody);
        this.maxSpeed = maxSpeed;

        speed = new Vector3(maxSpeed, 0, maxSpeed);
        speed.rotate(rotationVector, -45);

        currentWeapon = new Pistol();
        currentWeapon.setTransform(modelInstance.transform);

        walkAnimationController = new AnimationController(modelInstance);
        shootAnimationController = new AnimationController(modelInstance);
        shootAnimationController.allowSameAnimation = true;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        rigidBody.setAngularVelocity(Vector3.Zero);
        rigidBody.setLinearVelocity(Vector3.Zero);

        if (moving) {
            rigidBody.translate(speed);
        }
        currentWeapon.setTransform(rigidBody.getWorldTransform());

        if(!moving) {
            walkAnimationController.setAnimation(null);
        } else {
            walkAnimationController.setAnimation("walk", -1).speed = .5f;
        }
        walkAnimationController.update(delta);
        shootAnimationController.update(delta);

        currentWeapon.update(delta);
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        super.render(modelBatch, environment);
        currentWeapon.render(modelBatch, environment);
    }

    public void fire() {
        currentWeapon.fire();
        shootAnimationController.setAnimation("shoot_pistol");
    }

    public void setDirection(int direction) {
        if (direction != -1) {
            this.direction = direction;
        }

        moving = true;
        switch (direction) {
            case 1: {
                speed.rotate(rotationVector, 45 - currentRotationAngle);
                rotation.set(rotationVector, 45 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 45;
                break;
            } case 2: {
                speed.rotate(rotationVector, 360 - currentRotationAngle);
                rotation.set(rotationVector, 360 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 360;
                break;
            } case 3: {
                speed.rotate(rotationVector, 315 - currentRotationAngle);
                rotation.set(rotationVector, 315 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 315;
                break;
            } case 4: {
                speed.rotate(rotationVector, 270 - currentRotationAngle);
                rotation.set(rotationVector, 270 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 270;
                break;
            } case 5: {
                speed.rotate(rotationVector, 225 - currentRotationAngle);
                rotation.set(rotationVector, 225- currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 225;
                break;
            } case 6: {
                speed.rotate(rotationVector, 180 - currentRotationAngle);
                rotation.set(rotationVector, 180 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 180;
                break;
            } case 7: {
                speed.rotate(rotationVector, 135 - currentRotationAngle);
                rotation.set(rotationVector, 135 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 135;
                break;
            } case 8: {
                speed.rotate(rotationVector, 90 - currentRotationAngle);
                rotation.set(rotationVector, 90 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 90;
                break;
            } default: {
                moving = false;
            }
        }
    }

    public int getDirection() {
        return direction;
    }

    public float getCurrentRotationAngle() {
        return currentRotationAngle;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public boolean isMoving() {
        return moving;
    }

}
