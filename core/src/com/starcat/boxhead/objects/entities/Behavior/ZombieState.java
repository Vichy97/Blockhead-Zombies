package com.starcat.boxhead.objects.entities.Behavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.objects.entities.Zombie;
import com.starcat.boxhead.particles.ParticleManager;

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
                if (zombie.stateMachine.getCurrentState() != DIE) {
                    zombie.stateMachine.changeState(ATTACK);
                }
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

            zombie.getAnimationController().current.listener = new AnimationController.AnimationListener() {
                @Override
                public void onEnd(AnimationController.AnimationDesc animation) {
                    if (zombie.getPosition().dst(zombie.getBehavior().getTarget().getPosition()) <= 1.5f) {
                        EntityManager.instance().getPlayer().damage(10);
                        ParticleManager.instance().addBloodSpLatterDecal(EntityManager.instance().getPlayer().getPosition().x, EntityManager.instance().getPlayer().getPosition().z);

                    }

                    Timer.instance().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {

                            if (zombie.stateMachine.getCurrentState() != DIE) {
                                zombie.stateMachine.changeState(CHASE);
                            }
                        }
                    }, 1);
                }

                @Override
                public void onLoop(AnimationController.AnimationDesc animation) {

                }
            };
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

            if (zombie.shouldFade()) {
                zombie.setOpacity(zombie.getOpacity() - .01f);
                if (zombie.getOpacity() <= .25f) {
                    zombie.setShouldPool(true);
                }
            }
        }

        @Override
        public void enter(final Zombie zombie) {
            zombie.setShouldRemoveBody(true);
            zombie.getAnimationController().setAnimation("die").speed = .6f;

            zombie.getAnimationController().current.listener = new AnimationController.AnimationListener() {
                @Override
                public void onEnd(AnimationController.AnimationDesc animation) {
                    Timer.instance().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {

                            zombie.setFade(true);
                        }
                    }, 2);
                }

                @Override
                public void onLoop(AnimationController.AnimationDesc animation) {

                }
            };
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
