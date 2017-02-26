package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.starcat.boxhead.physics.MotionState;

/**
 * Created by Vincent on 11/15/2016.
 *
 * Any moving game object such as Entities or bullets.
 * For a non-moving object use StaticGameObject
 */

public class DynamicGameObject implements Pool.Poolable, Disposable {
    //which axis the object can rotate around
    protected static Vector3 rotationVector = Vector3.Y;

    protected ModelInstance modelInstance;
    protected btRigidBody rigidBody;
    protected MotionState motionState;

    protected Quaternion rotation;
    protected Vector3 position;
    protected Vector3 temp;

    private boolean shouldPool = false;
    private boolean shouldRemoveBody = false;

    private float radius;
    private static BoundingBox boundingBox;
    private Vector3 center = new Vector3();
    private Vector3 dimensions = new Vector3();



    public DynamicGameObject() {
        rotation = new Quaternion();
        motionState = new MotionState();
        position = new Vector3();
        temp = new Vector3();
    }

    /*
     * because most classes that extend this can be pooled (entities and bullets both
     * need to be pooled), we use an init method instead of just the constructor. It
     * is called whenever an object is retrieved from the pool
     */
    public void init(Vector3 position, ModelInstance modelInstance, btRigidBody rigidBody) {
        this.modelInstance = modelInstance;
        this.rigidBody = rigidBody;

        motionState.transform = modelInstance.transform.setTranslation(position);
        rigidBody.setMotionState(motionState);

        rigidBody.setWorldTransform(rigidBody.getWorldTransform());
        rigidBody.setAngularVelocity(Vector3.Zero);
        rigidBody.setLinearVelocity(Vector3.Zero);

        rigidBody.userData = this;
        shouldPool = false;
        shouldRemoveBody = false;

        boundingBox = new BoundingBox();
        modelInstance.calculateBoundingBox(boundingBox);
        boundingBox.getCenter(center);
        boundingBox.getDimensions(dimensions);
        radius = dimensions.len2() / 2f;
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody) {
        this.modelInstance = modelInstance;
        this.rigidBody = rigidBody;

        motionState.transform = modelInstance.transform;
        rigidBody.setMotionState(motionState);

        rigidBody.setWorldTransform(transform);
        rigidBody.setAngularVelocity(Vector3.Zero);
        rigidBody.setLinearVelocity(Vector3.Zero);

        rigidBody.userData = this;
        shouldPool = false;
        shouldRemoveBody = false;

        boundingBox = new BoundingBox();
        modelInstance.calculateBoundingBox(boundingBox);
        boundingBox.getCenter(center);
        boundingBox.getDimensions(dimensions);
        radius = dimensions.len2() / 2f;
    }



    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(modelInstance);
    }

    public void update(float delta) {
        modelInstance.transform.getTranslation(position);
    }

    public boolean isVisible(Camera camera) {
        return camera.frustum.sphereInFrustum(position, radius);
    }



    public btRigidBody getRigidBody() {
        return rigidBody;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public boolean getShouldPool() {
        return shouldPool;
    }

    public void setShouldPool(boolean shouldPool) {
        this.shouldPool = shouldPool;
    }

    public boolean getShouldRemoveBody() {
        return shouldRemoveBody;
    }

    public void setShouldRemoveBody(boolean shouldRemoveBody) {
        this.shouldRemoveBody = shouldRemoveBody;
    }



    @Override
    public void reset() {
        //rigid body can still collide so it gets moved away from playing area
        rigidBody.setWorldTransform((rigidBody.getWorldTransform().translate(0, 15, 0)));
        rigidBody.setLinearVelocity(Vector3.Zero);
        rigidBody.setUserValue(0);
        rigidBody.userData = null;
        shouldPool = false;
    }

    @Override
    public void dispose() {
        rigidBody.getCollisionShape().dispose();
        rigidBody.dispose();
        motionState.dispose();
    }

}
