package com.starcat.boxhead.entity;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.physics.MotionState;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Entity extends btRigidBody {
    private Vector3 position;
    private ModelInstance modelInstance;
    private MotionState motionState;

    public Entity(Vector3 position, ModelInstance modelInstance,  btRigidBodyConstructionInfo constructionInfo) {
        super(constructionInfo);
        this.position = position;
        this.modelInstance = modelInstance;

        motionState = new MotionState();
        motionState.transform = this.modelInstance.transform.setToTranslation(position);
        setMotionState(motionState);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    @Override
    public void dispose() {
        super.dispose();
        motionState.dispose();
    }
}
