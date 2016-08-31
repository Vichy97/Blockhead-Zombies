package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.starcat.boxhead.physics.MotionState;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Entity implements Disposable {
    protected static Vector3 rotationVector = Vector3.Y;

    protected ModelInstance modelInstance;
    protected MotionState motionState;
    protected btRigidBody rigidBody;
    protected Quaternion rotation;
    protected Vector3 position;


    public Entity() {
        rotation = new Quaternion();
        motionState = new MotionState();
        position = new Vector3();
    }

    public void init(Vector3 position, ModelInstance modelInstance,  btRigidBody rigidBody) {
        this.modelInstance = modelInstance;
        this.rigidBody = rigidBody;

        motionState.transform = modelInstance.transform.setTranslation(position);
        rigidBody.setMotionState(motionState);

        rigidBody.setWorldTransform(rigidBody.getWorldTransform());
        rigidBody.setAngularVelocity(Vector3.Zero);
        rigidBody.setLinearVelocity(Vector3.Zero);
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody) {
        this.modelInstance = modelInstance;
        this.rigidBody = rigidBody;

        motionState.transform = modelInstance.transform;
        rigidBody.setMotionState(motionState);

        rigidBody.setWorldTransform(transform);
        rigidBody.setAngularVelocity(Vector3.Zero);
        rigidBody.setLinearVelocity(Vector3.Zero);
    }

    public void update(float delta) {
        modelInstance.transform.getTranslation(position);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(modelInstance);
    }

    public btRigidBody getRigidBody() {
        return rigidBody;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public Vector3 getPosition() {
        return position;
    }

    @Override
    public void dispose() {
        rigidBody.getCollisionShape().dispose();
        rigidBody.dispose();
        motionState.dispose();
    }
}
