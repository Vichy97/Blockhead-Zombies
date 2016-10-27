package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.objects.entities.Behavior.ZombieState;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 9/27/2016.
 */

public class Zombie extends Entity {

    private AnimationController walkAnimationController, attackAnimationController;
    public StateMachine<Zombie, ZombieState> stateMachine;

    public void init(Vector3 position, btRigidBody rigidBody) {
        super.init(position, new ModelInstance(AssetLoader.zombie), rigidBody);


        stateMachine = new DefaultStateMachine<Zombie, ZombieState>(this, ZombieState.CHASE);
        walkAnimationController = new AnimationController(modelInstance);
        attackAnimationController = new AnimationController(modelInstance);
        walkAnimationController.setAnimation("walk", -1).speed = .2f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateMachine.update();
    }

    public AnimationController getWalkAnimationController() {
        return walkAnimationController;
    }

    public StateMachine<Zombie, ZombieState> getStateMachine() {
        return stateMachine;
    }
}
