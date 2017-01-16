package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import com.badlogic.gdx.utils.Pool;
import com.starcat.boxhead.objects.Bullet;
import com.starcat.boxhead.physics.CollisionFlags;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.physics.CollisionMasks;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Vincent on 8/12/2016.
 *
 * This class handles all entity and bullet spawning, updating, rendering, and pooling
 */
public final class EntityManager {

    private static final EntityManager instance = new EntityManager();

    private btDiscreteDynamicsWorld dynamicsWorld;

    private Player player;
    private ArrayList<Entity> entities;
    private ArrayList<com.starcat.boxhead.objects.Bullet> bullets;
    private ArrayList<com.starcat.boxhead.objects.BulletCasing> bulletCasings;

    private btRigidBody.btRigidBodyConstructionInfo constructionInfo;
    private Vector3 tempVec3;
    private Vector3 linearFactor;
    private Vector3 angularFactor;
    private Vector3 localInertia;
    private btVector3 btLocalInertia;
    private BoundingBox boundingBox;

    private Pool<com.starcat.boxhead.objects.Bullet> bulletPool;
    private Pool<Zombie> zombiePool;

    private float currentTime = 0;
    private WaveDesc currentWave;
    private Vector3[] spawnPoints;
    private int zombiesLeftToSpawn = 5;



    private EntityManager() {
        entities = new ArrayList<Entity>();
        bullets = new ArrayList<com.starcat.boxhead.objects.Bullet>();
        bulletCasings = new ArrayList<com.starcat.boxhead.objects.BulletCasing>();

        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(1, null, null, Vector3.Zero);
        tempVec3 = new Vector3();
        linearFactor = Vector3.X.add(Vector3.Z);
        angularFactor = Vector3.Y;
        localInertia = new Vector3();
        btLocalInertia = new btVector3();
        boundingBox = new BoundingBox();

        bulletPool = new Pool<com.starcat.boxhead.objects.Bullet>() {
            @Override
            protected com.starcat.boxhead.objects.Bullet newObject() {
                return new com.starcat.boxhead.objects.Bullet();
            }
        };

        zombiePool = new Pool<Zombie>() {
            @Override
            protected Zombie newObject() {
                return new Zombie();
            }
        };
    }



    public static EntityManager instance() {
        return instance;
    }

    public void setDynamicsWorld(btDiscreteDynamicsWorld world) {
        dynamicsWorld = world;
    }



    public void update(float delta) {

        Iterator<Entity> entityIterator = entities.iterator();
        while(entityIterator.hasNext()) {
            Entity entity = entityIterator.next();

            if (entity.getShouldPool()) {
                if (entity instanceof Zombie) {
                    zombiePool.free((Zombie)entity);
                    entityIterator.remove();
                } 
            } else {
                entity.update(delta);
            }

            if (entity.getShouldRemoveBody()) {
                dynamicsWorld.removeRigidBody(entity.getRigidBody());
            }
        }

        Iterator<com.starcat.boxhead.objects.Bullet> bulletIterator = bullets.iterator();
        while(bulletIterator.hasNext()) {
            com.starcat.boxhead.objects.Bullet bullet = bulletIterator.next();

            if (bullet.getShouldPool()) {
                bulletPool.free(bullet);
                bulletIterator.remove();
            } else {
                bullet.update(delta);
            }

            if (bullet.getShouldRemoveBody()) {
                dynamicsWorld.removeRigidBody(bullet.getRigidBody());
            }
        }

        Iterator<com.starcat.boxhead.objects.BulletCasing> bulletCasingIterator = bulletCasings.iterator();
        while(bulletCasingIterator.hasNext()) {
            com.starcat.boxhead.objects.BulletCasing bulletCasing = bulletCasingIterator.next();

            bulletCasing.update(delta);

            if (!bulletCasing.getRigidBody().isActive()) {
                bulletCasing.setShouldRemoveBody(true);
            }

            if (bulletCasing.getShouldRemoveBody()) {
                dynamicsWorld.removeRigidBody(bulletCasing.getRigidBody());
            }
        }

    }

    public void renderEntities(ModelBatch modelBatch, Environment environment) {
        for (Entity entity : entities) {
            entity.render(modelBatch, environment);
        }
    }

    public void renderBullets(ModelBatch modelBatch, Environment environment) {
        for (com.starcat.boxhead.objects.Bullet bullet : bullets) {
            bullet.render(modelBatch, environment);
        }
        for (com.starcat.boxhead.objects.BulletCasing bulletCasing : bulletCasings) {
            bulletCasing.render(modelBatch, environment);
        }
    }



    public void spawnPlayer(Vector3 position, float maxSpeed) {
        AssetLoader.player.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        constructionInfo.setCollisionShape(collisionShape);
        btRigidBody rigidBody = new btRigidBody(constructionInfo);
        rigidBody.setAngularFactor(angularFactor);
        rigidBody.setLinearFactor(linearFactor);
        rigidBody.setContactCallbackFlag(CollisionFlags.ENTITY_FLAG);
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);

        player = new Player();
        player.init(position, maxSpeed, rigidBody);

        dynamicsWorld.addRigidBody(rigidBody, (short) CollisionFlags.ENTITY_FLAG, (short) CollisionMasks.PLAYER_MASK);
        entities.add(player);
    }

    public void spawnZombie(Vector3 position) {
        Zombie zombie = zombiePool.obtain();

        btRigidBody rigidBody;
        if (zombie.getRigidBody() == null) {
            AssetLoader.zombie.calculateBoundingBox(boundingBox);
            btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
            constructionInfo.setCollisionShape(collisionShape);
            rigidBody = new btRigidBody(constructionInfo);
            rigidBody.setAngularFactor(angularFactor);
            rigidBody.setLinearFactor(linearFactor);
            rigidBody.setContactCallbackFlag(CollisionFlags.ENTITY_FLAG | CollisionFlags.ENEMY_FLAG);
            rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        } else {
            rigidBody = zombie.getRigidBody();
        }

        dynamicsWorld.addRigidBody(rigidBody, (short)(CollisionFlags.ENEMY_FLAG | CollisionFlags.ENTITY_FLAG), (short) CollisionMasks.ENEMY_MASK);

        if (zombie.getBehavior() == null) {
            Arrive<Vector3> arrive = new Arrive<Vector3>(zombie, player).setEnabled(true)
                    .setTimeToTarget(.001f)
                    .setArrivalTolerance(1.5f)
                    .setDecelerationRadius(0);
            zombie.setBehavior(arrive);
        }

        zombie.init(position, rigidBody);

        entities.add(zombie);
    }

    public void spawnBullet(Matrix4 transform, ModelInstance modelInstance, int direction, float velocity, int damage, float accuracy) {
        com.starcat.boxhead.objects.Bullet bullet = bulletPool.obtain();

        btRigidBody rigidBody;
        if (bullet.getRigidBody() == null) {
            modelInstance.calculateBoundingBox(boundingBox);
            btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
            constructionInfo.setCollisionShape(collisionShape);
            rigidBody = new btRigidBody(constructionInfo);
            rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | btRigidBody.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
            rigidBody.setContactCallbackFlag(CollisionFlags.BULLET_FLAG);
            rigidBody.setContactCallbackFilter(CollisionFlags.OBJECT_FLAG | CollisionFlags.ENTITY_FLAG | CollisionFlags.ENEMY_FLAG);
            rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
            rigidBody.setAngularFactor(angularFactor);
            rigidBody.setLinearFactor(linearFactor);
        } else {
            rigidBody = bullet.getRigidBody();
        }

        dynamicsWorld.addRigidBody(rigidBody, (short) CollisionFlags.BULLET_FLAG, (short) CollisionMasks.BULLET_MASK);

        if (player.isMoving()) {
            velocity += player.getMaxSpeed();
        }

        bullet.init(transform, modelInstance, rigidBody, direction, velocity, damage, accuracy);

        bullets.add(bullet);
    }

    //FIXME: causes weird AI behavior and also slows the game down
    public void spawnBulletCasing(Matrix4 transform, ModelInstance modelInstance, Vector3 expulsionImpulse) {
        com.starcat.boxhead.objects.BulletCasing bulletCasing = new com.starcat.boxhead.objects.BulletCasing();

        btRigidBody rigidBody;
        modelInstance.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        constructionInfo.setCollisionShape(collisionShape);
        constructionInfo.setMass(1f);
        collisionShape.calculateLocalInertia(1f, localInertia);
        btLocalInertia.setValue(localInertia.x, localInertia.y, localInertia.z);
        constructionInfo.setLocalInertia(btLocalInertia);
        rigidBody = new btRigidBody(constructionInfo);
        rigidBody.setRestitution(.5f);
        rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | btRigidBody.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        rigidBody.setContactCallbackFlag(CollisionFlags.BULLET_CASING_FLAG);
        rigidBody.setDeactivationTime(0);
        //rigidBody.setContactCallbackFilter(CollisionFlags.OBJECT_FLAG | CollisionFlags.GROUND_FLAG);

        dynamicsWorld.addRigidBody(rigidBody, (short) CollisionFlags.BULLET_CASING_FLAG, (short) CollisionMasks.BULLET_CASING_MASK);

        bulletCasing.init(transform, modelInstance, rigidBody, expulsionImpulse);

        bulletCasings.add(bulletCasing);
    }

    public void updateWave(float delta) {
        currentTime += delta;

        if (zombiesLeftToSpawn > 0) {
            if (currentTime >= currentWave.spawnRate) {
                currentTime = 0;
                spawnZombie(spawnPoints[0]);
            }
        }
    }


    public Player getPlayer() {
        return player;
    }

    public void clear() {
        Iterator<Entity> entityIterator = entities.iterator();
        while(entityIterator.hasNext()) {
            Entity entity = entityIterator.next();

            entity.dispose();

            entityIterator.remove();
        }

        Iterator<com.starcat.boxhead.objects.Bullet> bulletIterator = bullets.iterator();
        while(bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            bullet.dispose();

            bulletIterator.remove();
        }

        Iterator<com.starcat.boxhead.objects.BulletCasing> bulletCasingIterator = bulletCasings.iterator();
        while(bulletCasingIterator.hasNext()) {
            com.starcat.boxhead.objects.BulletCasing bulletCasing = bulletCasingIterator.next();

            bulletCasing.dispose();

            bulletCasingIterator.remove();
        }

        bulletPool.clear();
        zombiePool.clear();
    }
}
