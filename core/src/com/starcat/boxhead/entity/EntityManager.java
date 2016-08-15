package com.starcat.boxhead.entity;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.starcat.boxhead.utils.AssetLoader;

import java.util.ArrayList;

/**
 * Created by Vincent on 8/12/2016.
 */
public class EntityManager implements Disposable{
    ArrayList<Entity> entities;
    btDynamicsWorld dynamicsWorld;
    private btCollisionShape collisionShape;
    private btRigidBody.btRigidBodyConstructionInfo constructionInfo;
    private Vector3 localInertia, tempVec3;
    private BoundingBox boundingBox;

    public EntityManager(btDynamicsWorld dynamicsWorld) {
        entities = new ArrayList<Entity>();
        this.dynamicsWorld = dynamicsWorld;
        localInertia = new Vector3();
        tempVec3 = new Vector3();
        boundingBox = new BoundingBox();
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).render(modelBatch, environment);
        }
    }

    public void spawnEntitiy(Vector3 position, ModelInstance modelInstance, float mass) {
        collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        if (mass > 0) {
            collisionShape.calculateLocalInertia(mass, localInertia);
        } else {
            localInertia.setZero();
        }
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, collisionShape, localInertia);
        Entity entity = new Entity(position, modelInstance, constructionInfo);

        dynamicsWorld.addRigidBody(entity);
        entities.add(entity);

        constructionInfo.dispose();
    }

    public void spawnPlayer(Vector3 position) {
        float mass = 1;

        AssetLoader.boxhead.calculateBoundingBox(boundingBox);
        collisionShape = new btBoxShape(boundingBox.getDimensions(tempVec3).scl(.5f));
        collisionShape.calculateLocalInertia(mass, localInertia);
        constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, collisionShape, localInertia);
        Entity entity = new Player(position, constructionInfo);

        dynamicsWorld.addRigidBody(entity);
        entities.add(entity);

        constructionInfo.dispose();
    }

    @Override
    public void dispose() {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).dispose();
        }
        collisionShape.dispose();
    }
}
