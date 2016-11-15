package com.starcat.boxhead.objects.entities.Behavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Zombie;
import com.starcat.boxhead.utils.Flags;

/**
 * Created by Vincent on 10/25/2016.
 */

public enum ZombieState implements State<Zombie> {

    CHASE() {
        @Override
        public void update(Zombie zombie) {
            zombie.getWalkAnimationController().update(Gdx.graphics.getDeltaTime());

            if(zombie.getBehavior() != null) {
                zombie.getBehavior().calculateSteering(zombie.getSteeringOutput());
                zombie.applySteering(Gdx.graphics.getDeltaTime());
            }

            if (zombie.getPosition().dst(zombie.getBehavior().getTarget().getPosition()) <= 1.5f) {
                zombie.stateMachine.changeState(CHASE);
            }
        }

        @Override
        public void enter(Zombie zombie) {
            zombie.getWalkAnimationController().setAnimation("walk", -1).speed = .2f;
        }

        @Override
        public void exit(Zombie zombie) {
            zombie.getRigidBody().setLinearVelocity(Vector3.Zero);
            zombie.getWalkAnimationController().setAnimation(null);
        }
    },
    ATTACK(){
        @Override
        public void update(Zombie zombie) {
        }

        @Override
        public void enter(Zombie zombie) {
        }

        @Override
        public void exit(Zombie zombie) {
        }
    },
    DIE() {
        @Override
        public void update(Zombie zombie) {
        }

        @Override
        public void enter(Zombie zombie) {
        }

        @Override
        public void exit(Zombie zombie) {
            zombie.getRigidBody().setUserValue(Flags.SHOULD_POOL_FLAG);
        }
    };



    @Override
    public void enter(Zombie entity) {

    }

    @Override
    public void update(Zombie entity) {

    }

    @Override
    public void exit(Zombie entity) {

    }

    @Override
    public boolean onMessage(Zombie entity, Telegram telegram) {
        return false;
    }
}
