package com.vincent.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.vincent.World.ObjectManager;

import java.util.ArrayList;

/**
 * Created by Vincent on 7/8/2015.
 *
 * This class handles spawning and updating entities and their bounding boxes,
 * but does not handle the rendering of entities (that is done in the ObjectManager class)
 */
public class EntityManager {

    private World world;
    private FixtureDef fixtureDef;
    private BodyDef bodyDef;

    private ArrayList<Entity> entities;

    public EntityManager(World world) {
        this.world = world;
        fixtureDef = new FixtureDef();
        bodyDef = new BodyDef();

        entities = new ArrayList<>();
    }

    public void updateEntities() {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update();
        }
    }

    public Player spawnPlayer(Texture[] texture, float maxSpeed, Vector3 position, Touchpad touchpad) {
        Player player = new Player(texture, maxSpeed, position, world, touchpad, bodyDef, fixtureDef);
        entities.add(player);
        ObjectManager.addObject(player);
        return player;
    }

    public void spawnEntity() {

    }

    public void dispose() {
        entities.clear();
    }
}
