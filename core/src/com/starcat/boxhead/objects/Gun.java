package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 8/17/2016.
 */
public abstract class Gun {
    protected int damage;
    protected float fireRate;
    protected ModelInstance modelInstance;
    protected Vector3 translation;
    protected Vector3 bulletTranslation;

    public abstract void fire();

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void setTransform(Matrix4 transform) {
        modelInstance.transform.set(transform.translate(translation));
    }

    public float getDamage() {
        return damage;
    }

    public float getFireRate() {
        return fireRate;
    }
}
