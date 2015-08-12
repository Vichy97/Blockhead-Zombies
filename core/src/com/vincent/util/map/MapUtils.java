package com.vincent.util.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Vincent on 7/12/2015.
 *
 */
public class MapUtils {
    public static final int TILE_WIDTH = 128;
    public static final int TILE_HEIGHT = 64;
    private static Matrix4 transformationMatrix = new Matrix4().idt().translate(0, .25f, 0).scale((float) (Math.sqrt(2.0)), (float) (Math.sqrt(2.0) / 2.0), 1.0f).rotate(0, 0, 1, -45);
    static Vector3 tempVector3 = new Vector3(0, 0, 0);

    //transforms game coordinates to isometric coordinates. **NOT THE SAME AS TILE COORDINATES**
    public static Vector3 gameToIso(Vector3 position) {
        tempVector3.set(position);
        tempVector3.mul(transformationMatrix.inv());
        return tempVector3;
    }
    public static Vector3 gameToIso(Vector2 position) {
        tempVector3.set(position.x, position.y, 0);
        return gameToIso(tempVector3);
    }
    public static Vector3 gameToIso(float x, float y) {
        tempVector3.set(x, y, 0);
        return gameToIso(tempVector3);
    }

    //transforms isometric coordinates to game coordinates
    public static Vector3 isoToGame(Vector3 position) {
        tempVector3.set(position);
        tempVector3.mul(transformationMatrix);
        return tempVector3;
    }

    public static Vector3 isoToGame(Vector2 position) {
        tempVector3.set(position.x, position.y, 0);
        return isoToGame(tempVector3);
    }

    public static Vector3 isoToGame(float x, float y) {
        tempVector3.set(x, y, 0);
        return isoToGame(tempVector3);
    }



    public static Vector3 screenToTile(float x, float y, OrthographicCamera camera) {
        tempVector3.set(x, y, 0);
        //converts screen coordinates to world coordinates
        camera.unproject(tempVector3);
        //then converts world to iso
        return worldToTile(tempVector3);
    }

    public static Vector3 screenToTile(Vector3 point, OrthographicCamera camera) {
        tempVector3.set(point);
        //converts screen coordinates to world coordinates
        camera.unproject(tempVector3);
        //then converts world to iso
        return worldToTile(tempVector3);
    }

    public static Vector3 screenToTile(Vector2 point, OrthographicCamera camera) {
        tempVector3.set(point.x, point.y, 0);
        //converts screen coordinates to world coordinates
        camera.unproject(tempVector3);
        //then converts world to iso
        return worldToTile(tempVector3);
    }

    public static Vector3 worldToTile(Vector3 point) {
        tempVector3.set(point);
        tempVector3.x /= TILE_WIDTH;
        tempVector3.y = (tempVector3.y - TILE_HEIGHT / 2) / TILE_HEIGHT + tempVector3.x;
        tempVector3.x -= tempVector3.y - tempVector3.x;
        //round down to nearest int
        tempVector3.x = (float)Math.floor(tempVector3.x);
        tempVector3.y = (float)Math.floor(tempVector3.y);
        return tempVector3;
    }

    public static Vector3 worldToTile(Vector2 point) {
        tempVector3.set(point.x, point.y, 0);
        return worldToTile(tempVector3);
    }

    public static Vector3 worldToTile(float x, float y) {
        tempVector3.set(x, y, 0);
        return worldToTile(tempVector3);
    }

    //corrects the maps object positions to game coordinates instead of isometric coordinates
    public static void correctMapObjects(TiledMap tiledMap, String layer) {
        MapObjects mapObjects = tiledMap.getLayers().get(layer).getObjects();
        for (int i = 0; i < mapObjects.getCount(); i++) {
            MapObject object = mapObjects.get(i);
           if (object instanceof TextureMapObject) {
                tempVector3.set(((TextureMapObject) object).getX(), ((TextureMapObject) object).getY(), 0);
                isoToGame(tempVector3);
                ((TextureMapObject) object).setX(tempVector3.x - ((TextureMapObject) object).getTextureRegion().getRegionWidth() / 2);
                ((TextureMapObject) object).setY(tempVector3.y + TILE_HEIGHT / 2);
            }
        }
    }

    public static void correctMapObjects(TiledMap tiledMap, int layer) {
        MapObjects mapObjects = tiledMap.getLayers().get(layer).getObjects();
        for (int i = 0; i < mapObjects.getCount(); i++) {
            MapObject object = mapObjects.get(i);
            if (object instanceof RectangleMapObject) {
                tempVector3.set(((RectangleMapObject) object).getRectangle().getX(), ((RectangleMapObject) object).getRectangle().getY(), 0);
                isoToGame(tempVector3);
                ((RectangleMapObject) object).getRectangle().setX(tempVector3.x);
                ((RectangleMapObject) object).getRectangle().setY(tempVector3.y);
            } else if (object instanceof TextureMapObject) {
                tempVector3.set(((TextureMapObject) object).getX(), ((TextureMapObject) object).getY(), 0);
                isoToGame(tempVector3);
                ((TextureMapObject) object).setX(tempVector3.x);
                ((TextureMapObject) object).setY(tempVector3.y);
            }
        }
    }

    public static ArrayList<CustomMapObject> mapObjectsToList(MapObjects mapObjects) {
        ArrayList<CustomMapObject> objects = new ArrayList<>();
        for (int i = 0; i < mapObjects.getCount(); i++) {
            if (mapObjects.get(i) instanceof CustomMapObject) {
                objects.add((CustomMapObject) mapObjects.get(i));
            }
        }
        return objects;
    }
}
