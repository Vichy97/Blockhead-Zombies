package com.vincent.util.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.vincent.World.SortableObject;

import java.util.ArrayList;

/**
 * Created by Vincent on 7/2/2015.
 *
 * IsometricTiledMapRenderer with added functionality such as object rendering
 */
public class CustomTileMapRenderer extends IsometricTiledMapRenderer {
    private TiledMap map;
    private Vector3 vector3;

    public CustomTileMapRenderer(TiledMap map) {
        super(map);
        this.map = map;
        vector3 = new Vector3();
    }

    public void renderObjectLayer(String layerName) {
        MapObjects mapObjects = map.getLayers().get(layerName).getObjects();

        beginRender();
        for (int i = 0; i < mapObjects.getCount(); i++) {
            renderObject(mapObjects.get(i));
        }
        endRender();
    }

    public void renderObjectLayer(MapLayer layer) {
        MapObjects mapObjects = layer.getObjects();

        for (int i = 0; i < mapObjects.getCount(); i++) {
            renderObject(mapObjects.get(i));
        }
    }

    public void renderObjectArrayList(ArrayList<SortableObject> mapObjects) {
        for (int i = 0; i < mapObjects.size(); i++) {
            mapObjects.get(i).render(getBatch());
        }
    }

}

