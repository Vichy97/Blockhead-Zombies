package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    //lower value means greater accuracy
    protected float accuracy;
    protected float bulletSpeed;
    protected int damage;
    protected int clipSize;
    protected int ammoInClip;
    protected int extraClips;
    protected float rateOfFire;
    protected float reloadTime;
    protected float timer = 0;
    protected float range;
    protected boolean autofire = false;

    //offset from player position
    protected Vector3 bulletTranslation;
    protected Vector3 bulletCasingTranslation;
    //impulse for casing ejection
    protected Vector3 bulletCasingExpulsionImpulse;

    protected ModelInstance modelInstance;
    protected Model bulletModel;
    protected Model bulletCasingModel;
    protected Sound sound;
    protected AnimationController animationController;

    protected String fireAnimation;
    protected String walkAnimation;
    protected String poseAnimation;
    protected String playerFireAnimation;

    protected Sprite silhouette, ammoSilhouette;

    protected boolean canShoot = true;
    protected boolean reloading = false;



    public Gun(Player player) {
        this.player = player;
    }

    public void fire() {
        if (canShoot) {
            bulletCasingExpulsionImpulse.set(-2 + MathUtils.random(-.5f, .5f), 2 + MathUtils.random(-.2f, .2f), -.2f + MathUtils.random(-.5f, .5f));
            bulletCasingExpulsionImpulse.rotate(player.getCurrentRotationAngle(), 0, 1, 0);

            sound.play();
            canShoot = false;
            ammoInClip -= 1;

            if (ammoInClip == 0) {
                timer = reloadTime;
                if (extraClips > 0) {
                    reloading = true;
                }
            } else {
                timer = rateOfFire;
            }

            animationController.setAnimation(fireAnimation);
            player.getShootAnimationController().setAnimation(playerFireAnimation);

            EntityManager.instance().spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslation), new ModelInstance(bulletModel), player.getDirection(), bulletSpeed, damage, accuracy);
            //EntityManager.instance().spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslation), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
        }
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void renderUI(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        spriteBatch.begin();
        silhouette.draw(spriteBatch);

        for (int i = 0; i < ammoInClip; i++) {
            ammoSilhouette.setX(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize * .75f) + (i * ammoSilhouette.getWidth() * 1.5f));
            ammoSilhouette.setY(Gdx.graphics.getHeight() / 15);
            ammoSilhouette.draw(spriteBatch);
        }
        spriteBatch.end();
        if (reloading) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize), Gdx.graphics.getHeight() / 15, clipSize * ammoSilhouette.getWidth() * 2, Gdx.graphics.getHeight() / 100);
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize), Gdx.graphics.getHeight() / 15, clipSize * ammoSilhouette.getWidth() * 2 * (timer / reloadTime), Gdx.graphics.getHeight() / 100);
            shapeRenderer.end();
        }
    }

    public void update(float delta) {
        setTransform(player.getRigidBody().getWorldTransform());

        if (!player.isMoving()) {
            player.getWalkAnimationController().setAnimation(poseAnimation);
        } else {
            player.getWalkAnimationController().setAnimation(walkAnimation, -1).speed = .5f;
        }
        //player.getWalkAnimationController().update(delta);
        //player.getShootAnimationController().update(delta);
        animationController.update(delta);

        if (!canShoot && timer > 0) {
            timer -= Gdx.graphics.getDeltaTime();

            if (timer <= 0) {
                if (ammoInClip == 0) {
                    if (extraClips <= 0) {
                        canShoot = false;
                    } else if (extraClips > 0) {
                        extraClips--;
                        ammoInClip = clipSize;
                        canShoot = true;
                        reloading = false;
                    }
                } else {
                    canShoot = true;
                }
            }
        }
    }

    public void setTransform(Matrix4 transform) {
        modelInstance.transform.set(transform);
    }



    public void addReserveAmmo(int reserveAmmo) {
        this.extraClips += reserveAmmo;
    }

    public float getDamage() {
        return damage;
    }

    public float getRateOfFire() {
        return rateOfFire;
    }

    public boolean isAutofire() {
        return autofire;
    }

    public boolean canShoot() {
        return canShoot;
    }

}
