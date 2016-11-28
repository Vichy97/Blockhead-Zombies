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

    private AnimationController animationController;
    public StateMachine<Zombie, ZombieState> stateMachine;

    public Zombie() {
        super();
        stateMachine = new DefaultStateMachine<Zombie, ZombieState>(this, ZombieState.CHASE);
    }

    public void init(Vector3 position, btRigidBody rigidBody) {
        super.init(position, new ModelInstance(AssetLoader.zombie), rigidBody);
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        animationController.setAnimation("walk", -1).speed = .2f;

        stateMachine.changeState(ZombieState.CHASE);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateMachine.update();
    }

    public AnimationController getAnimationController() {
        return animationController;
    }

    public StateMachine<Zombie, ZombieState> getStateMachine() {
        return stateMachine;
    }
}
