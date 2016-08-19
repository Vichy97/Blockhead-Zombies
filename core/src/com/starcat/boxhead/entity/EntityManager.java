package com.starcat.boxhead.entity;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pool;
import com.starcat.boxhead.objects.Bullet;
import com.starcat.boxhead.utils.AssetLoader;

import java.util.ArrayList;

/**
 * Created by Vincent on 8/12/2016.
 */
public class EntityManager {

    private static btDynamicsWorld dynamicsWorld;
    private static ArrayList<Entity> entities = new ArrayList<Entity>();
    private static btRigidBody.btRigidBodyConstructionInfo constructionInfo;
    private static Vector3 localInertia = new Vector3();
    private static Vector3 tempVec3 = new Vector3();
    private static BoundingBox boundingBox = new BoundingBox();
    private static Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet();
        }
    };

    public static void setDynamicsWorld(btDynamicsWorld world) {
        dynamicsWorld = world;
    }

    public static void update(float delta) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update(delta);
        }
    }

    public static void render(ModelBatch modelBatch, Environment environment) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).render(modelBatch, environment);
        }
    }

    public static Entity spawnEntitiy(Vector3 position, ModelInstance modelInstance, float mass) {
        modelInstance.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        if (mass > 0) {
            collisionShape.calculateLocalInertia(mass, localInertia);
        } else {
            localInertia.setZero();
        }
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, collisionShape, localInertia);
        Entity entity = new Entity();
        entity.init(position, modelInstance, constructionInfo);

        dynamicsWorld.addRigidBody(entity.getRigidBody());
        entities.add(entity);

        constructionInfo.dispose();

        return entity;
    }

    public static Player spawnPlayer(Vector3 position, float maxSpeed) {
        float mass = 1;

        AssetLoader.boxhead.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        collisionShape.calculateLocalInertia(mass, localInertia.setZero());
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, collisionShape, localInertia.setZero());
        Player player = new Player();
        player.init(position, maxSpeed, constructionInfo);

        dynamicsWorld.addRigidBody(player.getRigidBody());
        entities.add(player);

        constructionInfo.dispose();

        return player;
    }

    public static Bullet spawnBullet(Matrix4 transform, ModelInstance modelInstance, int direction, float velocity) {
        Bullet bullet = bulletPool.obtain();

        float mass = 1;

        modelInstance.calculateBoundingBox(boundingBox);
        btCollisionShape collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));

        collisionShape.calculateLocalInertia(mass, localInertia.setZero());
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, collisionShape, localInertia.setZero());

        bullet.init(transform, modelInstance, constructionInfo, direction, velocity);
        dynamicsWorld.addRigidBody(bullet.getRigidBody());
        entities.add(bullet);

        constructionInfo.dispose();

        return bullet;
    }


    public static void dispose() {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).dispose();
        }
    }
}
