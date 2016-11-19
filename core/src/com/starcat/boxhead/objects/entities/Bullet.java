package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pool;
import com.starcat.boxhead.objects.DynamicGameObject;
import com.starcat.boxhead.utils.Flags;

/**
 * Created by Vincent on 8/17/2016.
 */
public class Bullet extends DynamicGameObject implements Pool.Poolable {

    private Vector3 speed;
    private int damage;
    private boolean shouldPool = false;


    public Bullet() {
        speed = new Vector3();
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody, int direction, float velocity) {
        super.init(transform, modelInstance, rigidBody);

        speed.set(velocity, 0, velocity);

        switch (direction) {
            case 1: {
                speed.rotate(rotationVector, MathUtils.random(-5, 5));
                break;
            } case 2: {
                speed.rotate(rotationVector, 315 + MathUtils.random(-5, 5));
                break;
            } case 3: {
                speed.rotate(rotationVector, 270 + MathUtils.random(-5, 5));
                break;
            } case 4: {
                speed.rotate(rotationVector, 225 + MathUtils.random(-5, 5));
                break;
            } case 5: {
                speed.rotate(rotationVector, 180 + MathUtils.random(-5, 5));
                break;
            } case 6: {
                speed.rotate(rotationVector, 135 + MathUtils.random(-5, 5));
                break;
            } case 7: {
                speed.rotate(rotationVector, 90 + MathUtils.random(-5, 5));
                break;
            } case 8: {
                speed.rotate(rotationVector, 45 + MathUtils.random(-5, 5));
                break;
            }
        }
    }



    @Override
    public void update(float delta) {
        super.update(delta);

        rigidBody.translate(speed);

        if ((position.x > 50 || position.x < -50) || (position.z > 50 || position.z < -50)) {
            rigidBody.setUserValue(Flags.SHOULD_POOL_FLAG);
        }
    }

    @Override
    public void reset() {
        rigidBody.setUserValue(0);
        rigidBody.userData = null;
        shouldPool = false;
    }



    public boolean getShouldPool() {
        return shouldPool;
    }

    public void setShouldPool(boolean shouldPool) {
        this.shouldPool = shouldPool;
    }

    public int getDamage() {
        return damage;
    }

}
