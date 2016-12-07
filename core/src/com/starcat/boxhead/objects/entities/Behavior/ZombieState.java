package com.starcat.boxhead.objects.entities.Behavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.objects.entities.Zombie;

/**
 * Created by Vincent on 10/25/2016.
 *
 * Entity behavior uses a finite state machine for decision making
 * The AI in this game is pretty simple so it mainly decides when to
 * attack vs chase
 */

public enum ZombieState implements State<Zombie> {

    CHASE() {
        @Override
        public void update(Zombie zombie) {
            zombie.getAnimationController().update(Gdx.graphics.getDeltaTime());

            if(zombie.getBehavior() != null) {
                zombie.getBehavior().calculateSteering(zombie.getSteeringOutput());
                zombie.applySteering(Gdx.graphics.getDeltaTime());
            }

            if (zombie.getPosition().dst(zombie.getBehavior().getTarget().getPosition()) <= 1f) {
                zombie.stateMachine.changeState(ATTACK);
            }
        }

        @Override
        public void enter(Zombie zombie) {
            zombie.getAnimationController().setAnimation("walk", -1).speed = .2f;
        }

        @Override
        public void exit(Zombie zombie) {
            zombie.getRigidBody().setLinearVelocity(Vector3.Zero);
            zombie.getAnimationController().setAnimation(null);
        }
    },
    ATTACK(){
        @Override
        public void update(Zombie zombie) {
            zombie.getAnimationController().update(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void enter(final Zombie zombie) {
            zombie.getAnimationController().setAnimation("attack").speed = .5f;

            AnimationController.AnimationListener animationListener = new AnimationController.AnimationListener() {
                @Override
                public void onEnd(AnimationController.AnimationDesc animation) {
                    if (zombie.getPosition().dst(zombie.getBehavior().getTarget().getPosition()) <= 1f) {
                        EntityManager.getPlayer().damage(10);
                    }

                    Timer.instance().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {

                            zombie.stateMachine.changeState(CHASE);
                        }
                    }, 1);
                }

                @Override
                public void onLoop(AnimationController.AnimationDesc animation) {

                }
            };

            zombie.getAnimationController().current.listener = animationListener;
        }

        @Override
        public void exit(Zombie zombie) {
            zombie.getAnimationController().setAnimation(null);
        }
    },
    DIE() {
        @Override
        public void update(Zombie zombie) {
                zombie.getAnimationController().update(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void enter(final Zombie zombie) {
            zombie.setShouldRemoveBody(true);
            zombie.getAnimationController().setAnimation("die").speed = .6f;

            AnimationController.AnimationListener animationListener = new AnimationController.AnimationListener() {
                @Override
                public void onEnd(AnimationController.AnimationDesc animation) {
                    Timer.instance().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            zombie.setShouldPool(true);
                        }
                    }, 2);
                }

                @Override
                public void onLoop(AnimationController.AnimationDesc animation) {

                }
            };

            zombie.getAnimationController().current.listener = animationListener;
        }

        @Override
        public void exit(Zombie zombie) {
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
