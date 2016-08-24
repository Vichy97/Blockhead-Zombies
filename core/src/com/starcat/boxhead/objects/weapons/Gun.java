package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Vincent on 8/17/2016.
 */
public abstract class Gun {
    private static Timer timer = new Timer();

    protected int damage;
    protected float reloadTime;
    protected boolean canShoot = true;
    protected ModelInstance modelInstance;
    protected Vector3 translation;
    protected Vector3 bulletTranslation;
    protected Vector3 bulletCasingTranslation;
    protected Vector3 bulletCasingExpulsionImpulse;

    public void fire() {
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                canShoot = true;
            }
        }, reloadTime);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void setTransform(Matrix4 transform) {
        modelInstance.transform.set(transform);
        modelInstance.transform.translate(translation);
    }

    public float getDamage() {
        return damage;
    }

    public float getReloadTime() {
        return reloadTime;
    }
}
