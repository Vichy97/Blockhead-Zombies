package com.starcat.boxhead.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.starcat.boxhead.objects.entities.Bullet;
import com.starcat.boxhead.objects.entities.Entity;

/**
 * Created by Vincent on 8/19/2016.
 */
public class MyContactListener extends ContactListener {
    @Override
    public void onContactStarted(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
        if (colObj0.userData instanceof Bullet) {
            ((Bullet) colObj0.userData).setShouldPool(true);

            if (colObj1.userData instanceof Entity) {
            }
        } else if (colObj1.userData instanceof Bullet) {
            ((Bullet) colObj1.userData).setShouldPool(true);
        }
    }
}