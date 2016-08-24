package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Vincent on 8/21/2016.
 */
public class Star {
    private Sprite sprite;

    public Star(int x, int y, Texture texture) {
        sprite = new Sprite(texture);
        sprite.setX(x);
        sprite.setY(y);
    }


    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        sprite.draw(spriteBatch);
        spriteBatch.end();
    }
}
