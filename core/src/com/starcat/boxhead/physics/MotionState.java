package com.starcat.boxhead.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * Created by Vincent on 8/13/2016.
 *
 * this class syncs collision body movement with a model instance
 * (so the model stays with its collision object)
 */
public final class MotionState extends btMotionState {
    public Matrix4 transform;

    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
