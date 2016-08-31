package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 8/17/2016.
 */
public class Pistol extends Gun {
    private Player player;
    private float bulletSpeed;

    public Pistol(Player player) {
        this.player = player;
        modelInstance = new ModelInstance(AssetLoader.pistol);
        translation = new Vector3(-.4f, .53f, .88f);
        bulletTranslation = new Vector3(-.4f, .667f, 1f);
        bulletCasingTranslation = new Vector3(-.4f, .667f, .845f);
        bulletCasingExpulsionImpulse = new Vector3(-2, 2, .2f);
        bulletSpeed = .1f;
        reloadTime = .1f;
        if (player.isMoving()) {
            bulletSpeed += player.getMaxSpeed();
        }
    }

    @Override
    public void fire() {
        if (canShoot) {
            bulletCasingExpulsionImpulse.set(-2 + MathUtils.random(-.5f, .5f), 2 + MathUtils.random(-.2f, .2f), -.2f + MathUtils.random(-.5f, .5f));
            bulletCasingExpulsionImpulse.rotate(player.getCurrentRotationAngle(), 0, 1, 0);
            EntityManager.spawnBullet(player.getModelInstance().transform.cpy().translate(bulletTranslation), new ModelInstance(AssetLoader.pistolBullet), player.getDirection(), bulletSpeed);
            EntityManager.spawnBulletCasing(player.getModelInstance().transform.cpy().translate(bulletCasingTranslation), new ModelInstance(AssetLoader.pistolBulletCasing), bulletCasingExpulsionImpulse);
            AssetLoader.pistolSound.play();
            canShoot = false;
            super.fire();
        }
    }
}
