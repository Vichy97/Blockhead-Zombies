package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
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
import com.starcat.boxhead.objects.BulletCasing;
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

    private Pool<Bullet> bulletPool;
    private Pool<Zombie> zombiePool;

    private float currentTime = 0;
    private WaveDesc currentWaveDesc;
    private int currentWave = 0;
    private ArrayList<Vector3> spawnPoints;
    private int currentSpawnPoint = 0;
    private int zombiesLeftToSpawn = 10;
    private Vector3 zombieSpawnPoint = new Vector3();

    public int visible;
    public int total;

    private ModelCache modelCache;



    private EntityManager() {
        entities = new ArrayList<Entity>();
        bullets = new ArrayList<com.starcat.boxhead.objects.Bullet>();
        bulletCasings = new ArrayList<com.starcat.boxhead.objects.BulletCasing>();

        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(1, null, null, Vector3.Zero);
        tempVec3 = new Vector3();
        linearFactor = new Vector3(1, 0, 1);
        angularFactor = new Vector3(0, 1, 0);
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


        spawnPoints = new ArrayList<Vector3>();
        spawnPoints.add(new Vector3(5.5f, 0, 20));
        spawnPoints.add(new Vector3(5.5f, 0, -20));
        spawnPoints.add(new Vector3(20, 0, 5));
        spawnPoints.add(new Vector3(-20, 0, 5));

        currentWaveDesc = new WaveDesc();
        currentWaveDesc.spawnRate = 1;
        currentWaveDesc.numberOfZombies = 10;
        currentWaveDesc.zombieSpeed = 50;

        modelCache = new ModelCache();
    }

    public static EntityManager instance() {
        return instance;
    }



    public void update(float delta, Camera camera) {

        entities.get(0).update(delta);

        modelCache.begin();
        Iterator<Entity> entityIterator = entities.iterator();
        entityIterator.next();
        while(entityIterator.hasNext()) {
            Entity entity = entityIterator.next();

            if (entity.getShouldPool()) {
                if (entity instanceof Zombie) {
                    zombiePool.free((Zombie)entity);
                    entityIterator.remove();
                } 
            } else {
                entity.update(delta);
                if (entity.isVisible(camera)) {
                    modelCache.add(entity.getModelInstance());
                }
            }

            if (entity.getShouldRemoveBody()) {
                dynamicsWorld.removeRigidBody(entity.getRigidBody());
            }
        }
        modelCache.end();

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

        updateWave(delta);
    }

    private void updateWave(float delta) {
        currentTime += delta;

        if (zombiesLeftToSpawn > 0) {
            if (currentTime >= currentWaveDesc.spawnRate) {
                currentTime = 0;
                zombieSpawnPoint.set(spawnPoints.get(currentSpawnPoint));
                zombieSpawnPoint.add(MathUtils.random(-3, 3), 0, MathUtils.random(-3, 3));
                spawnZombie(zombieSpawnPoint);
                zombiesLeftToSpawn--;
                currentSpawnPoint++;
                if (currentSpawnPoint >= spawnPoints.size()) {
                    currentSpawnPoint = 0;
                }
            }
        } else {
            if (entities.size() <= 1) {
                currentWave++;
                currentTime = 0;
                currentWaveDesc.numberOfZombies += 10;
                currentWaveDesc.zombieSpeed += 5;
                currentWaveDesc.spawnRate -= .05f;
                zombiesLeftToSpawn = currentWaveDesc.numberOfZombies;
            }
        }
    }

    public void renderEntities(ModelBatch modelBatch, Environment environment) {
        visible = 0;
        total = entities.size() - 1;
        modelBatch.render(modelCache, environment);
        entities.get(0).render(modelBatch, environment);
    }

    public void renderBullets(ModelBatch modelBatch, Environment environment, Camera camera) {
        for (com.starcat.boxhead.objects.Bullet bullet : bullets) {
            if (bullet.isVisible(camera)) {
                bullet.render(modelBatch, environment);
            }
        }
        for (com.starcat.boxhead.objects.BulletCasing bulletCasing : bulletCasings) {
            bulletCasing.render(modelBatch, environment);
        }
    }



    public void spawnPlayer(Vector3 position, float maxSpeed, Texture skin) {
        AssetLoader.player.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        constructionInfo.setCollisionShape(collisionShape);
        btRigidBody rigidBody = new btRigidBody(constructionInfo);
        rigidBody.setAngularFactor(angularFactor);
        rigidBody.setLinearFactor(Vector3.Y);
        rigidBody.setContactCallbackFlag(CollisionFlags.ENTITY_FLAG);
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);

        player = new Player();
        player.init(position.add(0, .8f, 0), maxSpeed, rigidBody, skin);

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

        zombie.init(position.add(0, .8f, 0), rigidBody, currentWaveDesc.zombieSpeed);
        position.add(0, -.8f, 0);

        entities.add(zombie);
    }

    public void spawnBullet(Matrix4 transform, ModelInstance modelInstance, float angle, float velocity, int damage, float accuracy) {
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
            velocity += player.getMaxSpeed() * Gdx.graphics.getDeltaTime();
        }

        bullet.init(transform, modelInstance, rigidBody, angle, velocity, damage, accuracy);

        bullets.add(bullet);
    }

    //FIXME: causes weird AI behavior and also slows the game down
    public void spawnBulletCasing(Matrix4 transform, ModelInstance modelInstance, Vector3 expulsionImpulse) {
        com.starcat.boxhead.objects.BulletCasing bulletCasing = new BulletCasing();

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



    public void setDynamicsWorld(btDiscreteDynamicsWorld world) {
        dynamicsWorld = world;
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
