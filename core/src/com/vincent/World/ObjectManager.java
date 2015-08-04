package com.vincent.World;

import com.badlogic.gdx.maps.MapObjects;
import com.vincent.util.map.CustomMapObject;
import com.vincent.util.map.CustomTileMapRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Vincent on 8/4/2015.
 */
public class ObjectManager {

    private static ArrayList<CustomMapObject> objects = new ArrayList<>();
    private CustomTileMapRenderer renderer;

    public ObjectManager(CustomTileMapRenderer tileMapRenderer) {
        renderer = tileMapRenderer;
    }

    public ObjectManager(CustomTileMapRenderer tileMapRenderer, MapObjects mapObjects) {
        renderer = tileMapRenderer;
        addObjects(mapObjects);

        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).initPosition();
        }
    }

    public void renderObjects() {
        renderer.renderObjectArrayList(objects);
    }

    private static Comparator comparator = new Comparator<CustomMapObject>() {
        @Override
        public int compare(CustomMapObject object1, CustomMapObject object2) {
            return object1.compareTo(object2);
        }
    };

    public static void addObject(CustomMapObject object) {
        object.initPosition();
        objects.add(object);
    }

    public void addObjects(MapObjects mapObjects) {
        for (int i = 0; i < mapObjects.getCount(); i++) {
            if (mapObjects.get(i) instanceof CustomMapObject) {
                objects.add((CustomMapObject)mapObjects.get(i));
            }
        }
    }

    public void sortObjects() {
        Collections.sort(objects, comparator);
    }
}
