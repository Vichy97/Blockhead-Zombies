package com.starcat.boxhead.entity;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.objects.Gun;
import com.starcat.boxhead.objects.Pistol;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Player extends Entity {

    private Vector3 speed;
    private Vector2 diagonalSpeed;
    private float maxSpeed = .05f;
    private AnimationController walkAnimationController;
    private Gun currentWeapon;



    public void init(Vector3 position, float maxSpeed, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        super.init(position, new ModelInstance(AssetLoader.boxhead), constructionInfo);

        speed = new Vector3();
        setMaxSpeed(maxSpeed);

        walkAnimationController = new AnimationController(modelInstance);

        currentWeapon = new Pistol(this);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        rigidBody.translate(speed);
        walkAnimationController.update(delta);
        currentWeapon.setTransform(rigidBody.getWorldTransform());
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        super.render(modelBatch, environment);
        currentWeapon.render(modelBatch, environment);
    }

    public void fire() {
        currentWeapon.fire();
    }

    public void setDirection(int direction) {
        if (direction != -1) {
            this.direction = direction;
        }

        walkAnimationController.setAnimation("walk", -1).speed = .35f;
        switch (direction) {
            case 1: {
                rotation.set(rotationVector, 45 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 45;
                speed.set(diagonalSpeed.x, 0, diagonalSpeed.y);
                break;
            } case 2: {
                rotation.set(rotationVector, 360 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 360;
                speed.set(0, 0, maxSpeed);
                break;
            } case 3: {
                rotation.set(rotationVector, 315 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 315;
                speed.set(-diagonalSpeed.x, 0, diagonalSpeed.y);
                break;
            } case 4: {
                rotation.set(rotationVector, 270 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 270;
                speed.set(-maxSpeed, 0, 0);
                break;
            } case 5: {
                rotation.set(rotationVector, 225- currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 225;
                speed.set(-diagonalSpeed.x, 0, -diagonalSpeed.y);
                break;
            } case 6: {
                rotation.set(rotationVector, 180 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 180;
                speed.set(0, 0, -maxSpeed);
                break;
            } case 7: {
                rotation.set(rotationVector, 135 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 135;
                speed.set(diagonalSpeed.x, 0, -diagonalSpeed.y);
                break;
            } case 8: {
                rotation.set(rotationVector, 90 - currentRotationAngle);
                rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                currentRotationAngle = 90;
                speed.set(maxSpeed, 0, 0);
                break;
            } default: {
                speed.setZero();
                walkAnimationController.setAnimation(null);
            }
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        float radians = (float)Math.toRadians(45);
        diagonalSpeed = new Vector2((float)(Math.sin(radians) * maxSpeed),  (float)(Math.cos(radians) * maxSpeed));
    }

}
