package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Vincent on 8/7/2015.
 *
 * background clouds
 */
public class Cloud {

    private Sprite sprite;
    private final int SPEED_X = 20;

    public Cloud(int x, TextureRegion texture) {
        sprite = new Sprite(texture);
        sprite.setX(x);
        sprite.setY(MathUtils.random(0, 1080 - sprite.getHeight()));
    }

    public void update(float delta) {
        sprite.setX(sprite.getX() - SPEED_X * delta);

        if (sprite.getX() < 0 - sprite.getWidth()) {
            sprite.setX(1920);
            sprite.setY(Math.round(MathUtils.random(0, 1080 - sprite.getHeight())));
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        sprite.draw(spriteBatch);
        spriteBatch.end();
    }
}
