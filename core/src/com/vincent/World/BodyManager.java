package com.vincent.World;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.vincent.projectiles.Bullet;

import java.util.ArrayList;

/**
 * Created by Vincent on 8/26/2015.
 *
 * Class for managing Box2d bodies and collisions
 * this handles pooling the objects and deactivating them when
 * they collide or die (entities and bullets mainly)
 */
public class BodyManager implements ContactListener {

    private static ArrayList<Body> bodiesToActivate = new ArrayList<>();
    private static ArrayList<Body> bodiesToDeactivate = new ArrayList<>();

    public BodyManager() {

    }

    @Override
    public void beginContact(Contact contact) {
        Object objectA = contact.getFixtureA().getBody().getUserData();
        Object objectB = contact.getFixtureB().getBody().getUserData();
        if (objectA instanceof Bullet) {
            ((Bullet) objectA).free();
            addBodyToDeactivate(((Bullet) objectA).getBody());
        }

        if (objectB instanceof Bullet) {
            ((Bullet) objectB).free();
            addBodyToDeactivate(((Bullet) objectB).getBody());
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public static void addBodyToActive(Body body) {
        bodiesToActivate.add(body);
    }

    public static void addBodyToDeactivate(Body body) {
        bodiesToDeactivate.add(body);
    }

    public void deactivateBodies() {
        for (int i = 0; i < bodiesToDeactivate.size(); i++) {
            if (bodiesToDeactivate.get(i).isActive()) {
                bodiesToDeactivate.get(i).setActive(false);
            }
        }

        bodiesToDeactivate.clear();
    }

    public void activateBodies() {
        for (int i = 0; i < bodiesToActivate.size(); i++) {
            if (!bodiesToActivate.get(i).isActive()) {
                bodiesToActivate.get(i).setActive(true);
            }
        }

        bodiesToActivate.clear();
    }
}
