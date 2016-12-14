package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.EntityManager;

/**
 * Created by Vincent on 8/17/2016.
 */
public abstract class Gun {

    protected float bulletSpeed;
    protected int damage;
    protected float reloadTime;
    protected float currentTime = 0;
    protected float range;
    protected float accuracy;
    protected boolean autofire = false;

    //offset from player position
    protected Vector3 bulletTranslation;
    protected Vector3 bulletTranslationAlt;
    protected Vector3 bulletCasingTranslation;
    //impulse for casing ejection
    protected Vector3 bulletCasingExpulsionImpulse;
    //lower value means greater accuracy

    protected ModelInstance modelInstance;
    protected Model bulletModel;
    protected Model bulletCasingModel;
    protected Sound sound;
    protected AnimationController shootAnimationController;

    protected String fireAnimation;
    protected String altFireAnimation;

    protected boolean altFire = false;
    protected boolean canShoot = true;



    public void fire() {
        if (canShoot) {

            //bulletCasingExpulsionImpulse.set(-2 + MathUtils.random(-.5f, .5f), 2 + MathUtils.random(-.2f, .2f), -.2f + MathUtils.random(-.5f, .5f));
            //bulletCasingExpulsionImpulse.rotate(EntityManager.getPlayer().getCurrentRotationAngle(), 0, 1, 0);
            //EntityManager.spawnBulletCasing(EntityManager.getPlayer().getModelInstance().transform.cpy().translate(bulletCasingTranslation), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
            sound.play();
            canShoot = false;
            if (altFire) {
                shootAnimationController.setAnimation(altFireAnimation);
            } else {
                shootAnimationController.setAnimation(fireAnimation);
            }

          /*  Timer.instance().scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    canShoot = true;
                }
            }, reloadTime); */

            if (altFire) {
                EntityManager.spawnBullet(EntityManager.getPlayer().getModelInstance().transform.cpy().translate(bulletTranslationAlt), new ModelInstance(bulletModel), EntityManager.getPlayer().getDirection(), bulletSpeed, damage, accuracy);
                altFire = false;
            } else {
                EntityManager.spawnBullet(EntityManager.getPlayer().getModelInstance().transform.cpy().translate(bulletTranslation), new ModelInstance(bulletModel), EntityManager.getPlayer().getDirection(), bulletSpeed, damage, accuracy);
                altFire = true;
            }

        }
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void update(float delta) {
        shootAnimationController.update(delta);

        if (!canShoot) {
            currentTime += Gdx.graphics.getDeltaTime();
        }

        if (currentTime >= reloadTime) {
            canShoot = true;
            currentTime = 0;
        }
    }

    public void setTransform(Matrix4 transform) {
        modelInstance.transform.set(transform);
    }



    public float getDamage() {
        return damage;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public boolean isAltFire() {
        return altFire;
    }

    public boolean isAutofire() {
        return autofire;
    }

    public boolean canShoot() {
        return canShoot;
    }

}
