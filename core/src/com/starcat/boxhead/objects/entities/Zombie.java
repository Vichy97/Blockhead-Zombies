package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 9/27/2016.
 */

public class Zombie extends Entity {

    private AnimationController walkAnimationController;

    public void init(Vector3 position, btRigidBody rigidBody) {
        super.init(position, new ModelInstance(AssetLoader.zombie), rigidBody);

        walkAnimationController = new AnimationController(modelInstance);
    }

    @Override
    protected void updateAnimations(float delta) {
        if(!moving) {
            walkAnimationController.setAnimation(null);
        } else {
            walkAnimationController.setAnimation("walk", -1).speed = .2f;
        }
        walkAnimationController.update(delta);
    }
}
