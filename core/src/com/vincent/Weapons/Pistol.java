package com.vincent.Weapons;

import com.vincent.entity.Entity;
import com.vincent.projectiles.ProjectileManager;

/**
 * Created by Vincent on 8/30/2015.
 *
 * Its a pistol...
 */
public class Pistol extends Gun {

    ProjectileManager projectileManager;
    Entity entity;

    public Pistol(ProjectileManager projectileManager, Entity entity) {
        this.projectileManager = projectileManager;
        this.entity = entity;
        damage = 25;
        fireRate = .5f;
        bulletSpeed = 3f;
    }

    @Override
    public void fire() {
        float speed;
        if (entity.getDirection() != -1) {
            speed = entity.getMaxSpeed() + bulletSpeed;
        } else {
            speed = bulletSpeed;
        }
        projectileManager.spawnBullet(speed, entity.getPosition(), entity.getDirection(), entity.getAngle());
    }
}
