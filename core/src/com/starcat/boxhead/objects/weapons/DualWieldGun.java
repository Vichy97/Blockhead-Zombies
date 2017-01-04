package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 1/1/2017.
 */

public abstract class DualWieldGun extends Gun {

    protected boolean altFire = false;
    protected String fireAnimationAlt;
    protected String playerFireAnimationAlt;
    protected Vector3 bulletCasingTranslationAlt;
    protected Vector3 bulletTranslationAlt;

    public DualWieldGun(Player player) {
        super(player);
    }



    @Override
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

            if (altFire) {
                animationController.setAnimation(fireAnimationAlt);
                player.getShootAnimationController().setAnimation(playerFireAnimationAlt);
            } else {
                animationController.setAnimation(fireAnimation);
                player.getShootAnimationController().setAnimation(playerFireAnimation);
            }

            if (altFire) {
                EntityManager.instance().spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslationAlt), new ModelInstance(bulletModel), player.getDirection(), bulletSpeed, damage, accuracy);
                //EntityManager.instance().spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslationAlt), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
                altFire = false;
            } else {
                EntityManager.instance().spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslation), new ModelInstance(bulletModel), player.getDirection(), bulletSpeed, damage, accuracy);
                //EntityManager.instance().spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslation), new ModelInstance(bulletCasingModel), bulletCasingExpulsionImpulse.cpy());
                altFire = true;
            }
        }
    }

    @Override
    public void renderUI(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        int halfAmmo = ammoInClip / 2;
        int halfClip = clipSize / 2;
        float startX = (Gdx.graphics.getWidth() / 2) - (ammoSilhouette.getWidth() * .375f * clipSize);
        float widthX = ammoSilhouette.getWidth() * 1.5f;

        spriteBatch.begin();
        silhouette.draw(spriteBatch);
        if (!reloading) {
            AssetLoader.tinyFont.draw(spriteBatch, " x" + extraClips, startX + (clipSize * ammoSilhouette.getWidth() * .75f), Gdx.graphics.getHeight() / 8);


            ammoSilhouette.setY(Gdx.graphics.getHeight() / 8.5f);
            ammoSilhouette.setX(startX);
            for (int i = 0; i < halfClip; i++) {
                ammoSilhouette.draw(spriteBatch, .2f);

                if (i < halfAmmo) {
                    ammoSilhouette.draw(spriteBatch);
                }

                ammoSilhouette.translateX(widthX);
            }

            ammoSilhouette.setY(Gdx.graphics.getHeight() / 10);
            ammoSilhouette.setX(startX);
            for (int i = 0; i < halfClip; i++) {
                ammoSilhouette.draw(spriteBatch, .2f);

                if (i < ammoInClip - halfAmmo) {
                    ammoSilhouette.draw(spriteBatch);
                }

                ammoSilhouette.translateX(widthX);
            }
        }
        spriteBatch.end();

        if (reloading) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(startX, Gdx.graphics.getHeight() / 12, halfClip * widthX, Gdx.graphics.getHeight() / 100);
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(startX, Gdx.graphics.getHeight() / 12, halfClip * widthX * (timer / reloadTime), Gdx.graphics.getHeight() / 100);
            shapeRenderer.end();
        }
    }



    public boolean isAltFire() {
        return altFire;
    }
}
