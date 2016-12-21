package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.objects.entities.Player;

/**
 * Created by Vincent on 8/17/2016.
 */
public abstract class Gun {

    protected Player player;

    protected float bulletSpeed;
    protected int damage;
    protected float reloadTime;
    protected float currentTime = 0;
    protected float range;
    //lower value means greater accuracy
    protected float accuracy;
    protected boolean autofire = false;

    //offset from player position
    protected Vector3 bulletTranslation;
    protected Vector3 bulletTranslationAlt;
    protected Vector3 bulletCasingTranslation;
    protected Vector3 bulletCasingTranslationAlt;
    //impulse for casing ejection
    protected Vector3 bulletCasingExpulsionImpulse;

    protected ModelInstance modelInstance;
    protected Model bulletModel;
    protected Model bulletCasingModel;
    protected Sound sound;
    protected AnimationController animationController;

    protected String fireAnimation;
    protected String fireAnimationAlt;
    protected String walkAnimation;
    protected String poseAnimation;
    protected String playerFireAnimation;
    protected String playerFireAnimationAlt;

    protected boolean altFire = false;
    protected boolean canShoot = true;



    public Gun(Player player) {
        this.player = player;
    }

    public void fire() {
        if (canShoot) {
            bulletCasingExpulsionImpulse.set(-2 + MathUtils.random(-.5f, .5f), 2 + MathUtils.random(-.2f, .2f), -.2f + MathUtils.random(-.5f, .5f));
            bulletCasingExpulsionImpulse.rotate(player.getCurrentRotationAngle(), 0, 1, 0);

            sound.play();
            canShoot = false;
            if (altFire) {
                animationController.setAnimation(fireAnimationAlt);
                player.getShootAnimationController().setAnimation(playerFireAnimationAlt);
            } else {
                animationController.setAnimation(fireAnimation);
                player.getShootAnimationController().setAnimation(playerFireAnimation);
            }

            if (altFire) {
                EntityManager.spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslationAlt), new ModelInstance(bulletModel), player.getDirection(), bulletSpeed, damage, accuracy);
                EntityManager.spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslationAlt), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
                altFire = false;
            } else {
                EntityManager.spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslation), new ModelInstance(bulletModel), player.getDirection(), bulletSpeed, damage, accuracy);
                EntityManager.spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslation), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
                altFire = true;
            }

        }
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void update(float delta) {
        setTransform(player.getRigidBody().getWorldTransform());

        if (!player.isMoving()) {
            player.getWalkAnimationController().setAnimation(poseAnimation);
        } else {
            player.getWalkAnimationController().setAnimation(walkAnimation, -1).speed = .5f;
        }
        player.getWalkAnimationController().update(delta);
        player.getShootAnimationController().update(delta);
        animationController.update(delta);

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
