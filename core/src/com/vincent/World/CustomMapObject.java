package com.vincent.World;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 7/19/2015.
 *
 * A custom object for easy sorting and rendering. It can be used for entities and static objects
 */
public class CustomMapObject extends TextureMapObject implements SortableObject{

    public int height, width;
    public Vector3 position;

    public CustomMapObject(TextureRegion textureRegion, float x, float y, float scaleX, float scaleY) {
        super(textureRegion);
        setX(x);
        setY(y);
        setScaleX(scaleX);
        setScaleY(scaleY);
        position = new Vector3();
    }

    public CustomMapObject(TextureRegion textureRegion) {
        super(textureRegion);
        position = new Vector3();
    }

    //for some reason getX() and getY() dont work in the constructor
    public void initPosition() {
        position.set(getX(), getY(), 0);
    }


    @Override
    public int compareTo(SortableObject object) {
        if (position.y > object.getPosition().y) {
            return -1;
        } else if (position.y < object.getPosition().y) {
            return 1;
        } else if (position.y == object.getPosition().y){
            if (position.x > object.getPosition().x) {
                return 1;
            } else if (position.x < object.getPosition().x) {
                return -1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public void render(Batch batch) {
        batch.draw(getTextureRegion(), position.x, position.y);
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }
}
