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
        spriteBatch.begin();
        silhouette.draw(spriteBatch);

        AssetLoader.tinyFont.draw(spriteBatch, " x" + extraClips, Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize / 2 * .75f) + (clipSize * ammoSilhouette.getWidth() * .75f), Gdx.graphics.getHeight() / 8);

        int halfClip = ammoInClip / 2;

        for (int i = 0; i < halfClip; i++) {
            ammoSilhouette.setX(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize / 2 * .75f) + (i * ammoSilhouette.getWidth() * 1.5f));
            ammoSilhouette.setY(Gdx.graphics.getHeight() / 8.5f);
            ammoSilhouette.draw(spriteBatch);
        }

        for (int i = 0; i < ammoInClip - halfClip; i++) {
            ammoSilhouette.setX(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize / 2 * .75f) + (i * ammoSilhouette.getWidth() * 1.5f));
            ammoSilhouette.setY(Gdx.graphics.getHeight() / 10f);
            ammoSilhouette.draw(spriteBatch);
        }

        if (!reloading) {
            for (int i = 0; i < clipSize / 2; i++) {
                ammoSilhouette.setX(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth() * clipSize / 2 * .75f) + (i * ammoSilhouette.getWidth() * 1.5f));
                ammoSilhouette.setY(Gdx.graphics.getHeight() / 8.5f);
                ammoSilhouette.draw(spriteBatch, .2f);
            }

            for (int i = 0; i < clipSize / 2; i++) {
                ammoSilhouette.setX(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth() * clipSize / 2 * .75f) + (i * ammoSilhouette.getWidth() * 1.5f));
                ammoSilhouette.setY(Gdx.graphics.getHeight() / 10);
                ammoSilhouette.draw(spriteBatch, .2f);
            }
        }
        spriteBatch.end();

        if (reloading) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize / 2), Gdx.graphics.getHeight() / 12, clipSize / 2 * ammoSilhouette.getWidth() * 2, Gdx.graphics.getHeight() / 100);
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - (ammoSilhouette.getWidth()  * clipSize / 2), Gdx.graphics.getHeight() / 12, clipSize / 2 * ammoSilhouette.getWidth() * 2 * (timer / reloadTime), Gdx.graphics.getHeight() / 100);
            shapeRenderer.end();

            spriteBatch.begin();
            AssetLoader.tinyFont.draw(spriteBatch, "reloading...", Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 8);
            spriteBatch.end();
        }
    }



    public boolean isAltFire() {
        return altFire;
    }
}
