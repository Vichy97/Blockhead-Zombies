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
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;

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
    protected Sound fireSound;
    protected AnimationController animationController;

    protected boolean altFire = false;
    protected String fireAnimationAlt;
    protected String playerFireAnimationAlt;
    protected Vector3 bulletCasingTranslationAlt;
    protected Vector3 bulletTranslationAlt;

    protected String fireAnimation;
    protected String walkAnimation;
    protected String poseAnimation;
    protected String playerFireAnimation;

    protected Sprite silhouette, ammoSilhouette;

    protected boolean canShoot = true;
    protected boolean reloading = false;
    protected boolean sound;

    protected int flags = 0;



    public Gun(Player player) {
        this.player = player;

        sound = Gdx.app.getPreferences("settings").getBoolean("sound");
    }

    public void fire() {
        if (canShoot) {
            bulletCasingExpulsionImpulse.set(-2 + MathUtils.random(-.5f, .5f), 2 + MathUtils.random(-.2f, .2f), -.2f + MathUtils.random(-.5f, .5f));
            bulletCasingExpulsionImpulse.rotate(player.getCurrentRotationAngle(), 0, 1, 0);

            if (sound) {
                fireSound.play();
            }
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

            player.getShootAnimationController().setAnimation(playerFireAnimationAlt);

            if (altFire) {
                animationController.setAnimation(fireAnimationAlt);
                player.getShootAnimationController().setAnimation(playerFireAnimationAlt);
            } else {
                animationController.setAnimation(fireAnimation);
                player.getShootAnimationController().setAnimation(playerFireAnimation);
            }

            if (altFire) {
                EntityManager.instance().spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslationAlt), new ModelInstance(bulletModel), player.getCurrentRotationAngle(), bulletSpeed, damage, accuracy);
                //EntityManager.instance().spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslationAlt), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
            } else {
                EntityManager.instance().spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslation), new ModelInstance(bulletModel), player.getCurrentRotationAngle(), bulletSpeed, damage, accuracy);
                //EntityManager.instance().spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslation), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
            }

            altFire = !altFire;
        }
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void renderUI(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        if ((flags & WeaponConstants.FLAG_DUAL) == WeaponConstants.FLAG_DUAL) {
            renderUIDual(spriteBatch, shapeRenderer);
        } else {
            renderUISingle(spriteBatch, shapeRenderer);
        }
    }

    public void renderUIDual(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        int halfAmmo = ammoInClip / 2;
        int halfClip = clipSize / 2;
        float startX = Dimensions.getHalfScreenWidth() - (ammoSilhouette.getWidth() * .375f * clipSize);
        float widthX = ammoSilhouette.getWidth() * 1.5f;

        spriteBatch.begin();
        silhouette.draw(spriteBatch);
        if (!reloading) {
            AssetLoader.tinyFont.draw(spriteBatch, " x" + extraClips, startX + (clipSize * ammoSilhouette.getWidth() * .75f), Dimensions.getScreenHeight() / 8);

            ammoSilhouette.setY(Dimensions.getScreenHeight() / 8.5f);
            ammoSilhouette.setX(startX);
            for (int i = 0; i < halfClip; i++) {

                if (i < halfAmmo) {
                    ammoSilhouette.draw(spriteBatch);
                } else {
                    ammoSilhouette.draw(spriteBatch, .2f);
                }

                ammoSilhouette.translateX(widthX);
            }

            ammoSilhouette.setY(Dimensions.getScreenHeight() / 10);
            ammoSilhouette.setX(startX);
            for (int i = 0; i < halfClip; i++) {

                if (i < ammoInClip - halfAmmo) {
                    ammoSilhouette.draw(spriteBatch);
                } else {
                    ammoSilhouette.draw(spriteBatch, .2f);
                }

                ammoSilhouette.translateX(widthX);
            }
        }
        spriteBatch.end();

        if (reloading) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(startX, Dimensions.getScreenHeight() / 12, halfClip * widthX, Dimensions.getScreenHeight() / 100);
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(startX, Dimensions.getScreenHeight() / 12, halfClip * widthX * (timer / reloadTime), Dimensions.getScreenHeight() / 100);
            shapeRenderer.end();
        }
    }

    public void renderUISingle(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        float startX = Dimensions.getHalfScreenWidth() - (ammoSilhouette.getWidth() * .75f * clipSize);
        float widthX = ammoSilhouette.getWidth() * 1.5f;

        spriteBatch.begin();
        silhouette.draw(spriteBatch);

        if (!reloading) {
            AssetLoader.tinyFont.draw(spriteBatch, " x" + extraClips, startX + (clipSize * ammoSilhouette.getWidth() * 1.5f), Dimensions.getScreenHeight() / 8);
            ammoSilhouette.setX(startX);
            ammoSilhouette.setY(Dimensions.getScreenHeight() / 10);

            for (int i = 0; i < clipSize; i++) {
                if (i < ammoInClip) {
                    ammoSilhouette.draw(spriteBatch);
                } else {
                    ammoSilhouette.draw(spriteBatch, .2f);
                }
                ammoSilhouette.translateX(widthX);
            }
        }
        spriteBatch.end();

        if (reloading) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(Dimensions.getHalfScreenWidth() - (ammoSilhouette.getWidth()  * clipSize), Dimensions.getScreenHeight() / 15, clipSize * ammoSilhouette.getWidth() * 2, Dimensions.getScreenHeight() / 100);
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(Dimensions.getHalfScreenWidth() - (ammoSilhouette.getWidth()  * clipSize), Dimensions.getScreenHeight() / 15, clipSize * ammoSilhouette.getWidth() * 2 * (timer / reloadTime), Dimensions.getScreenHeight() / 100);
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
