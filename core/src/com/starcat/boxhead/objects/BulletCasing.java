package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.objects.entities.Entity;

/**
 * Created by Vincent on 8/22/2016.
 */
public class BulletCasing extends Entity {

    public BulletCasing() {
        super();
    }

    public void init(Matrix4 transform, ModelInstance modelInstance, btRigidBody rigidBody, Vector3 expulsionImpulse) {
        super.init(transform, modelInstance, rigidBody);
        rigidBody.applyCentralImpulse(expulsionImpulse);
    }
}
