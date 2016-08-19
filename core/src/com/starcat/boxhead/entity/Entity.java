package com.starcat.boxhead.entity;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.starcat.boxhead.physics.MotionState;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Entity implements Disposable {
    protected ModelInstance modelInstance;
    protected MotionState motionState;
    protected btRigidBody rigidBody;

    protected int direction = 2;

    protected Quaternion rotation;
    protected float currentRotationAngle = 0;
    protected Vector3 rotationVector;



    public Entity() {
        rotation = new Quaternion();
        rotationVector = new Vector3(0, 1, 0);
    }

    public void init(Vector3 position, ModelInstance modelInstance,  btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        this.modelInstance = modelInstance;

        rigidBody = new btRigidBody(constructionInfo);

        rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() & btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        rigidBody.setLinearFactor(new Vector3(1, 0, 1));
        rigidBody.setAngularFactor(new Vector3(0, 1, 0));

        motionState = new MotionState();
        motionState.transform = modelInstance.transform.setTranslation(position);
        rigidBody.setMotionState(motionState);
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        this.modelInstance = modelInstance;

        rigidBody = new btRigidBody(constructionInfo);
        rigidBody.setWorldTransform(transform);

        rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() & btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        rigidBody.setLinearFactor(new Vector3(1, 0, 1));
        rigidBody.setAngularFactor(new Vector3(0, 1, 0));

        motionState = new MotionState();
        motionState.transform = modelInstance.transform;
        rigidBody.setMotionState(motionState);

    }

    public void update(float delta) {
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public btRigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    public void dispose() {
        rigidBody.getCollisionShape().dispose();
        rigidBody.dispose();
        motionState.dispose();
    }
}
