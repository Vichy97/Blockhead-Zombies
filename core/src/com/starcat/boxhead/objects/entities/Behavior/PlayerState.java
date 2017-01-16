package com.starcat.boxhead.objects.entities.Behavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;

/**
 * Created by Vincent on 1/10/2017.
 */

public enum PlayerState implements State<Player> {

    ALIVE() {
        @Override
        public void update(Player player) {
            player.getRigidBody().setAngularVelocity(Vector3.Zero);
            player.getRigidBody().setLinearVelocity(Vector3.Zero);

            if (player.isMoving()) {
                player.getRigidBody().translate(player.getCurrentSpeed());
            }

            player.getCurrentWeapon().update(Gdx.graphics.getDeltaTime());

            player.getShootAnimationController().update(Gdx.graphics.getDeltaTime());
            player.getWalkAnimationController().update(Gdx.graphics.getDeltaTime());

            player.heal(.025f);
        }

        @Override
        public void enter(Player player) {

        }

        @Override
        public void exit(Player player) {

        }
    },

    DEAD() {
        @Override
        public void update(Player player) {
            player.getWalkAnimationController().update(Gdx.graphics.getDeltaTime());
            player.setShouldRemoveBody(true);
        }

        @Override
        public void enter(Player player) {
            player.getWalkAnimationController().setAnimation("die").speed = .4f;
        }

        @Override
        public void exit(Player player) {

        }
    };



    @Override
    public void enter(Player player) {

    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void exit(Player player) {

    }

    @Override
    public boolean onMessage(Player player, Telegram telegram) {
        return false;
    }
}
