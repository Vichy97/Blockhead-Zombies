package com.vincent.projectiles;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.vincent.World.ObjectManager;

import java.util.ArrayList;

/**
 * Created by Vincent on 8/17/2015.
 *
 * manager for all projectiles/bullets
 * this class handles updating and spawning all projectiles,
 * projectile collision is handle by the BodyManager (they are box2d bodies)
 */
public class ProjectileManager {

    //pool for the bullets so they can be deactivated instead of destroyed (so you dont need garbage collection)
    public final Pool<Bullet> bulletPool = new Pool<Bullet>(0, 30) {

        @Override
        protected Bullet newObject() {
            return new Bullet(world, bodyDef, fixtureDef, bulletPool);
        }
    };

    private World world;
    private FixtureDef fixtureDef;
    private BodyDef bodyDef;

    private ArrayList<Bullet> projectiles;

    public ProjectileManager(World world) {
        this.world = world;
        fixtureDef = new FixtureDef();
        bodyDef = new BodyDef();

        projectiles = new ArrayList<>();
    }

    public void updateProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).update();
        }
    }

    public void spawnBullet(float speed, Vector3 position, int direction) {
        Bullet bullet = bulletPool.obtain();
        bullet.spawn(position, speed, direction);
        projectiles.add(bullet);
        ObjectManager.addObject(bullet);
    }

    public void dispose() {
        projectiles.clear();
    }
}
