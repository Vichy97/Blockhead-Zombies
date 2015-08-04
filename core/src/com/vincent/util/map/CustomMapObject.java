package com.vincent.util.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 7/19/2015.
 */
public class CustomMapObject extends TextureMapObject implements Comparable<CustomMapObject>{

    public int height, width;
    public Vector3 position;

    public CustomMapObject(TextureRegion textureRegion, float x, float y, float scaleX, float scaleY) {
        super(textureRegion);
        setX(x);
        setY(y);
        setScaleX(scaleX);
        setScaleY(scaleY);
        width = getTextureRegion().getRegionWidth();
        height = getTextureRegion().getRegionHeight();
        position = new Vector3();
    }

    public CustomMapObject(TextureRegion textureRegion) {
        super(textureRegion);
        width = getTextureRegion().getRegionWidth();
        height = getTextureRegion().getRegionHeight();
        position = new Vector3();
    }

    public void render(Batch batch) {
        batch.draw(getTextureRegion(), position.x, position.y);
    }

    //for some reason getX() and getY() dont work in the constructor
    public void initPosition() {
        position.set(getX(), getY(), 0);
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }

    @Override
    public int compareTo(CustomMapObject object) {
        if (position.y > object.position.y) {
            return -1;
        } else if (position.y < object.position.y) {
            return 1;
        } else if (position.y == object.position.y){
            if (position.x > object.position.x) {
                return 1;
            } else if (position.x < object.position .x) {
                return -1;
            } else {
                return 0;
            }
        }
        return 0;
    }
}
