package com.starcat.boxhead.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.entities.Behavior.ZombieState;
import com.starcat.boxhead.objects.entities.Bullet;
import com.starcat.boxhead.objects.entities.Entity;
import com.starcat.boxhead.objects.entities.Zombie;

/**
 * Created by Vincent on 8/19/2016.
 *
 * This class is used to trigger collision based events
 */
public class MyContactListener extends ContactListener {

    @Override
    public void onContactStarted(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
        if (colObj0.userData instanceof Bullet) {
            ((Bullet) colObj0.userData).setShouldPool(true);

            if (colObj1.userData instanceof Zombie) {
                ((Zombie) colObj1.userData).damage(((Bullet) colObj0.userData).getDamage());

                if (((Zombie) colObj1.userData).getHitpoints() <= 0) {
                    if (((Zombie) colObj1.userData).stateMachine.getCurrentState() != ZombieState.DIE) {
                        ((Zombie) colObj1.userData).stateMachine.changeState(ZombieState.DIE);
                    }
                }

            } else if (colObj1.userData instanceof Entity) {
                ((Entity) colObj1.userData).damage(((Bullet) colObj0.userData).getDamage());

                if (((Entity) colObj1.userData).getHitpoints() <= 0) {
                    ((Entity) colObj1.userData).setShouldRemoveBody(true);
                    ((Entity) colObj1.userData).setShouldPool(true);
                }
            }

        } else if (colObj1.userData instanceof Bullet) {
            ((Bullet) colObj1.userData).setShouldPool(true);

            if (colObj0.userData instanceof Zombie) {
                ((Zombie) colObj0.userData).damage(((Bullet) colObj1.userData).getDamage());

                if (((Zombie) colObj0.userData).getHitpoints() <= 0) {
                    if (((Zombie) colObj0.userData).stateMachine.getCurrentState() != ZombieState.DIE) {
                        ((Zombie) colObj0.userData).stateMachine.changeState(ZombieState.DIE);
                    }
                }

            } else if (colObj0.userData instanceof Entity) {
                ((Entity) colObj0.userData).damage(((Bullet) colObj1.userData).getDamage());

                if (((Entity) colObj0.userData).getHitpoints() <= 0) {
                    ((Entity) colObj1.userData).setShouldRemoveBody(true);
                    ((Entity) colObj0.userData).setShouldPool(true);
                }
            }
        }
    }



    private static void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("ContactListener", message);
        }
    }
}