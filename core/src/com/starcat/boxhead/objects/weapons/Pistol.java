package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
        bulletTranslation = new Vector3(-.4f, .67f, 1f);
        bulletSpeed = .1f;
        if (player.isMoving()) {
            bulletSpeed += player.getMaxSpeed();
        }
    }

    @Override
    public void fire() {
        EntityManager.spawnBullet(player.getModelInstance().transform.translate(bulletTranslation),  new ModelInstance(AssetLoader.pistolBullet), player.getDirection(), bulletSpeed);
    }
}
