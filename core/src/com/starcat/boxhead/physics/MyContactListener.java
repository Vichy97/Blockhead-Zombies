package com.starcat.boxhead.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.starcat.boxhead.utils.Flags;

/**
 * Created by Vincent on 8/19/2016.
 */
public class MyContactListener extends ContactListener {
    @Override
    public void onContactStarted(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
        colObj0.setUserValue(Flags.SHOULD_POOL_FLAG);
        Gdx.app.log("ContactListener", "contact");
    }
}