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
import com.starcat.boxhead.utils.Flags;
import com.starcat.boxhead.utils.Masks;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Vincent on 8/12/2016.
 *
 * This class handles all entity and bullet spawning, updating, and rendering
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
    private static Pool<Entity> entityPool = new Pool<Entity>() {
        @Override
        protected Entity newObject() {
            return new Entity();
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
                entityPool.free(entity);
                entityIterator.remove();
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
        rigidBody.setContactCallbackFlag(Flags.ENTITY_FLAG);
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
        rigidBody.setContactCallbackFlag(Flags.ENTITY_FLAG);
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);

        player = new Player();
        player.init(position, maxSpeed, rigidBody);

        dynamicsWorld.addRigidBody(rigidBody, (short)Flags.ENTITY_FLAG, (short)Masks.PLAYER_MASK);
        entities.add(player);
    }

    public static void spawnZombie(Vector3 position) {
        AssetLoader.zombie.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        constructionInfo.setCollisionShape(collisionShape);
        btRigidBody rigidBody = new btRigidBody(constructionInfo);
        rigidBody.setAngularFactor(angularFactor);
        rigidBody.setLinearFactor(linearFactor);
        rigidBody.setContactCallbackFlag(Flags.ENTITY_FLAG | Flags.ENEMY_FLAG);
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);

        Zombie zombie = new Zombie();
        zombie.init(position, rigidBody);

        Arrive<Vector3> arrive = new Arrive<Vector3>(zombie, player).setEnabled(true)
                .setTimeToTarget(.001f)
                .setArrivalTolerance(1.5f)
                .setDecelerationRadius(0);
        zombie.setBehavior(arrive);

        dynamicsWorld.addRigidBody(rigidBody, (short)(Flags.ENEMY_FLAG | Flags.ENTITY_FLAG), (short)Masks.ENEMY_MASK);
        entities.add(zombie);
    }

    public static Bullet spawnBullet(Matrix4 transform, ModelInstance modelInstance, int direction, float velocity) {
        Bullet bullet = bulletPool.obtain();

        btRigidBody rigidBody;
        if (bullet.getRigidBody() == null) {
            modelInstance.calculateBoundingBox(boundingBox);
            btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
            constructionInfo.setCollisionShape(collisionShape);
            rigidBody = new btRigidBody(constructionInfo);
            rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | btRigidBody.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
            rigidBody.setContactCallbackFlag(Flags.BULLET_FLAG);
            rigidBody.setContactCallbackFilter(Flags.OBJECT_FLAG | Flags.ENTITY_FLAG | Flags.ENEMY_FLAG);
            rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        } else {
            rigidBody = bullet.getRigidBody();
        }
        rigidBody.setAngularFactor(angularFactor);
        rigidBody.setLinearFactor(linearFactor);

        if (bullet.getRigidBody() == null) {
            dynamicsWorld.addRigidBody(rigidBody, (short)Flags.BULLET_FLAG, (short)Masks.BULLET_MASK);
        }

        bullet.init(transform, modelInstance, rigidBody, direction, velocity);

        bullets.add(bullet);

        return bullet;
    }

    public static BulletCasing spawnBulletCasing(Matrix4 transform, ModelInstance modelInstance, Vector3 expulsionImpulse) {
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
        rigidBody.setContactCallbackFlag(Flags.BULLET_CASING_FLAG);
        rigidBody.setContactCallbackFilter(Flags.OBJECT_FLAG);

        dynamicsWorld.addRigidBody(rigidBody, (short)Flags.BULLET_CASING_FLAG, (short)Masks.BULLET_CASING_MASK);

        bulletCasing.init(transform, modelInstance, rigidBody, expulsionImpulse);

        bulletCasings.add(bulletCasing);

        return bulletCasing;
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
