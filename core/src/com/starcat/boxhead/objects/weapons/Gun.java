package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.starcat.boxhead.objects.entities.EntityManager;

/**
 * Created by Vincent on 8/17/2016.
 */
public abstract class Gun {

    private static Timer timer = new Timer();

    protected float bulletSpeed;
    protected int damage;
    protected float reloadTime;
    protected boolean canShoot = true;

    //offset from player position
    protected Vector3 translation;
    protected Vector3 bulletTranslation;
    protected Vector3 bulletCasingTranslation;
    //impulse for casing ejection
    protected Vector3 bulletCasingExpulsionImpulse;
    //lower value means greater accuracy
    protected float accuracy;

    protected ModelInstance modelInstance;
    protected Model bulletModel;
    protected Model bulletCasingModel;
    protected Sound sound;



    public void fire() {
        if (canShoot) {
            EntityManager.spawnBullet(EntityManager.getPlayer().getModelInstance().transform.cpy().translate(bulletTranslation), new ModelInstance(bulletModel), EntityManager.getPlayer().getDirection(), bulletSpeed, damage, accuracy);

            //bulletCasingExpulsionImpulse.set(-2 + MathUtils.random(-.5f, .5f), 2 + MathUtils.random(-.2f, .2f), -.2f + MathUtils.random(-.5f, .5f));
            //bulletCasingExpulsionImpulse.rotate(EntityManager.getPlayer().getCurrentRotationAngle(), 0, 1, 0);
            //EntityManager.spawnBulletCasing(EntityManager.getPlayer().getModelInstance().transform.cpy().translate(bulletCasingTranslation), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
            sound.play();
            canShoot = false;

            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    canShoot = true;
                }
            }, reloadTime);
        }
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
