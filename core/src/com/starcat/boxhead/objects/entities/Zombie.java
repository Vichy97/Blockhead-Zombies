package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.starcat.boxhead.objects.entities.Behavior.ZombieState;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 9/27/2016.
 */

public class Zombie extends Entity {

    private boolean fade = false;
    private AnimationController animationController;
    public StateMachine<Zombie, ZombieState> stateMachine;
    private BlendingAttribute blendingAttribute;

    public Zombie() {
        super();
        stateMachine = new DefaultStateMachine<Zombie, ZombieState>(this, ZombieState.CHASE);
    }

    public void init(Vector3 position, btRigidBody rigidBody) {
        super.init(position, new ModelInstance(AssetLoader.zombie), rigidBody);
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;

        blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 1;
        modelInstance.materials.get(0).set(blendingAttribute);

        stateMachine.changeState(ZombieState.CHASE);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateMachine.update();

        if (hitpoints <= 0 && stateMachine.getCurrentState() != ZombieState.DIE) {
            stateMachine.changeState(ZombieState.DIE);
        }
    }

    public AnimationController getAnimationController() {
        return animationController;
    }

    public StateMachine<Zombie, ZombieState> getStateMachine() {
        return stateMachine;
    }

    public boolean shouldFade() {
        return fade;
    }

    public void setFade(boolean fade) {
        this.fade = fade;
    }

    public void setOpacity(float opacity) {
        blendingAttribute.opacity = opacity;
    }

    public float getOpacity() {
        return blendingAttribute.opacity;
    }
 }
