package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pool;
import com.starcat.boxhead.objects.DynamicGameObject;

/**
 * Created by Vincent on 8/17/2016.
 */
public class Bullet extends DynamicGameObject implements Pool.Poolable {

    private Vector3 speed;
    private int damage;



    public Bullet() {
        speed = new Vector3();
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody, float angle, float velocity, int damage, float accuracy) {
        super.init(transform, modelInstance, rigidBody);

        speed.set(velocity, 0, velocity);
        this.damage = damage;

        speed.rotate(rotationVector, angle + MathUtils.random(-accuracy, accuracy));

        /*switch (direction) {
            case 1: {
                speed.rotate(rotationVector, MathUtils.random(-accuracy, accuracy));
                break;
            } case 2: {
                speed.rotate(rotationVector, 315 + MathUtils.random(-accuracy, accuracy));
                break;
            } case 3: {
                speed.rotate(rotationVector, 270 + MathUtils.random(-accuracy, accuracy));
                break;
            } case 4: {
                speed.rotate(rotationVector, 225 + MathUtils.random(-accuracy, accuracy));
                break;
            } case 5: {
                speed.rotate(rotationVector, 180 + MathUtils.random(-accuracy, accuracy));
                break;
            } case 6: {
                speed.rotate(rotationVector, 135 + MathUtils.random(-accuracy, accuracy));
                break;
            } case 7: {
                speed.rotate(rotationVector, 90 + MathUtils.random(-accuracy, accuracy));
                break;
            } case 8: {
                speed.rotate(rotationVector, 45 + MathUtils.random(-accuracy, accuracy));
                break;
            }
        } */
    }



    @Override
    public void update(float delta) {
        super.update(delta);

        rigidBody.translate(speed);

        if ((position.x > 50 || position.x < -50) || (position.z > 50 || position.z < -50)) {
            setShouldRemoveBody(true);
            setShouldPool(true);
        }
    }



    public int getDamage() {
        return damage;
    }

    public Vector3 getCurrentSpeed() {
        return speed;
    }
}
