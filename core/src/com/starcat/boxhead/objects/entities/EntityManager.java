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
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.physics.CollisionFlags;
import com.starcat.boxhead.physics.CollisionMasks;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Vincent on 8/12/2016.
 *
 * This class handles all entity and bullet spawning, updating, rendering, and pooling
 */
public class EntityManager {

    private static btDiscreteDynamicsWorld dynamicsWorld;
    private static ArrayList<Entity> entities = new ArrayList<Entity>();
    private static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private static ArrayList<BulletCasing> bulletCasings = new ArrayList<BulletCasing>();

    private static Player player;

    private static btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(1, null, null, Vector3.Zero);
    private static Vector3 tempVec3 = new Vector3();
    private static Vector3 linearFactor = Vector3.X.add(Vector3.Z);
    private static Vector3 angularFactor = Vector3.Y;
    private static Vector3 localInertia = new Vector3();
    private static btVector3 btLocalInertia = new btVector3();
    private static BoundingBox boundingBox = new BoundingBox();

    private static Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet();
        }
    };
    private static Pool<Zombie> zombiePool = new Pool<Zombie>() {
        @Override
        protected Zombie newObject() {
            return new Zombie();
        }
    };
    private static Pool<btRigidBody> bulletCasingCollisionPool = new Pool<btRigidBody>() {
        @Override
        protected btRigidBody newObject() {
            return new btRigidBody(constructionInfo);
        }
    };



    public static void setDynamicsWorld(btDiscreteDynamicsWorld world) {
        dynamicsWorld = world;
    }

    public static void update(float delta) {

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
        }

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while(bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            if (bullet.getShouldPool()) {
                bulletPool.free(bullet);
                bulletIterator.remove();
            } else {
                bullet.update(delta);
            }
        }
        /*
        Iterator<BulletCasing> bulletCasingIterator = bulletCasings.iterator();

        while(bulletCasingIterator.hasNext()) {
            BulletCasing bulletCasing = bulletCasingIterator.next();
            if (!bulletCasing.getRigidBody().isActive()) {
                dynamicsWorld.removeRigidBody(bulletCasing.getRigidBody());
                bulletCasingCollisionPool.free(bulletCasing.getRigidBody());
            }
        }
        */
    }

    public static void renderEntities(ModelBatch modelBatch, Environment environment) {
        for (Entity entity : entities) {
            entity.render(modelBatch, environment);
        }
    }

    public static void renderBullets(ModelBatch modelBatch, Environment environment) {
        for (Bullet bullet : bullets) {
            bullet.render(modelBatch);
        }
        for (BulletCasing bulletCasing : bulletCasings) {
            bulletCasing.render(modelBatch, environment);
        }
    }



    public static void spawnEntitiy(Vector3 position, ModelInstance modelInstance) {
        modelInstance.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        constructionInfo.setCollisionShape(collisionShape);
        btRigidBody rigidBody = new btRigidBody(constructionInfo);
        rigidBody.setAngularFactor(angularFactor);
        rigidBody.setLinearFactor(linearFactor);
        rigidBody.setContactCallbackFlag(CollisionFlags.ENTITY_FLAG);
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);

        Entity entity = new Entity();
        entity.init(position, modelInstance, rigidBody);

        dynamicsWorld.addRigidBody(rigidBody);
        entities.add(entity);
    }

    public static void spawnPlayer(Vector3 position, float maxSpeed) {
        AssetLoader.boxhead.calculateBoundingBox(boundingBox);
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

    public static void spawnZombie(Vector3 position) {
        Zombie zombie = zombiePool.obtain();

        btRigidBody rigidBody;
        if (zombie.getRigidBody() == null) {
            AssetLoader.zombie.calculateBoundingBox(boundingBox);
            btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
            constructionInfo.setCollisionShape(collisionShape);
            rigidBody =new btRigidBody(constructionInfo);
            rigidBody.setAngularFactor(angularFactor);
            rigidBody.setLinearFactor(linearFactor);
            rigidBody.setContactCallbackFlag(CollisionFlags.ENTITY_FLAG | CollisionFlags.ENEMY_FLAG);
            rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        } else {
            rigidBody = zombie.getRigidBody();
        }

        zombie.init(position, rigidBody);

        Arrive<Vector3> arrive = new Arrive<Vector3>(zombie, player).setEnabled(true)
                .setTimeToTarget(.001f)
                .setArrivalTolerance(1.5f)
                .setDecelerationRadius(0);
        zombie.setBehavior(arrive);

        dynamicsWorld.addRigidBody(rigidBody, (short)(CollisionFlags.ENEMY_FLAG | CollisionFlags.ENTITY_FLAG), (short) CollisionMasks.ENEMY_MASK);
        entities.add(zombie);
    }

    public static void spawnBullet(Matrix4 transform, ModelInstance modelInstance, int direction, float velocity, int damage, float accuracy) {
        Bullet bullet = bulletPool.obtain();

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
        } else {
            rigidBody = bullet.getRigidBody();
        }
        rigidBody.setAngularFactor(angularFactor);
        rigidBody.setLinearFactor(linearFactor);

        if (bullet.getRigidBody() == null) {
            dynamicsWorld.addRigidBody(rigidBody, (short) CollisionFlags.BULLET_FLAG, (short) CollisionMasks.BULLET_MASK);
        }

        if (player.isMoving()) {
            velocity += player.getMaxSpeed();
        }

        bullet.init(transform, modelInstance, rigidBody, direction, velocity, damage, accuracy);

        bullets.add(bullet);
    }

    public static void spawnBulletCasing(Matrix4 transform, ModelInstance modelInstance, Vector3 expulsionImpulse) {
        BulletCasing bulletCasing = new BulletCasing();

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
        rigidBody.setContactCallbackFilter(CollisionFlags.OBJECT_FLAG);

        dynamicsWorld.addRigidBody(rigidBody, (short) CollisionFlags.BULLET_CASING_FLAG, (short) CollisionMasks.BULLET_CASING_MASK);

        bulletCasing.init(transform, modelInstance, rigidBody, expulsionImpulse);

        bulletCasings.add(bulletCasing);
    }



    public static Player getPlayer() {
        return player;
    }


    public static void dispose() {
        constructionInfo.dispose();
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).dispose();
        }
    }
}
