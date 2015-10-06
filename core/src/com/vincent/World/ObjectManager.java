package com.vincent.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.vincent.game.MyGdxGame;
import com.vincent.util.map.CustomTileMapRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Vincent on 8/4/2015.
 *
 * Handle sorting and rendering objects
 * updating is done in EntityManager because not all objects need updating
 * however all objects (even entities) need rendering
 */
public class ObjectManager {

    //list of objects to be sorted and rendered *THIS INCLUDES ALL ENTITIES*
    private static ArrayList<SortableObject> objects = new ArrayList<>();
    //list of static objects that are rendered but these have no depth and are therefore not sorted
    private static ArrayList<SortableObject> staticObjects = new ArrayList<>();
    private CustomTileMapRenderer renderer;

    public ObjectManager(CustomTileMapRenderer tileMapRenderer) {
        renderer = tileMapRenderer;
    }

    public ObjectManager(CustomTileMapRenderer tileMapRenderer, MapObjects mapObjects, MapObjects staticMapObjects) {
        renderer = tileMapRenderer;
        addObjects(mapObjects);
        addStaticObjects(staticMapObjects);
    }

    public void renderObjects() {
        renderer.getBatch().begin();
        renderer.renderObjectArrayList(objects);
        renderer.getBatch().end();
    }

    public void renderStaticObjects() {
        renderer.getBatch().begin();
        renderer.renderObjectArrayList(staticObjects);
        renderer.getBatch().end();
    }

    private static Comparator comparator = new Comparator<SortableObject>() {
        @Override
        public int compare(SortableObject object1, SortableObject object2) {
            return object1.compareTo(object2);
        }
    };

    public static void addObject(SortableObject object) {
        if (object instanceof CustomMapObject) {
            ((CustomMapObject)object).initPosition();
        }
        objects.add(object);
    }

    public void addObjects(MapObjects mapObjects) {
        for (int i = 0; i < mapObjects.getCount(); i++) {
            MapObject object = mapObjects.get(i);
            if (object instanceof CustomMapObject) {
                ((CustomMapObject) object).initPosition();
                objects.add((CustomMapObject)object);
            }
        }
    }

    public static void addStaticObject(SortableObject object) {
        if (object instanceof CustomMapObject) {
            ((CustomMapObject)object).initPosition();
        }
        staticObjects.add(object);
    }

    public void addStaticObjects(MapObjects mapObjects) {
        for (int i = 0; i < mapObjects.getCount(); i++) {
            MapObject object = mapObjects.get(i);
            if (object instanceof CustomMapObject) {
                ((CustomMapObject) object).initPosition();
                staticObjects.add((CustomMapObject)object);
            }
        }
    }

    public void sortObjects() {
        Collections.sort(objects, comparator);
    }

    //method for writing to the log
    private void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("Object Manager", message);
        }
    }

    public void dispose() {
        objects.clear();
        staticObjects.clear();
    }
}
