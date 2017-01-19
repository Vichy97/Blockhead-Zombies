package com.starcat.boxhead.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 1/16/2017.
 */

public abstract class Day extends TimeOfDay {

    private Sprite[] clouds;
    protected float cloudSpeed;


    public Day() {
        clouds = new Sprite[6];

        for (int i = 0; i < clouds.length; i++) {
            if (i % 3 == 0) {
                clouds[i] = new Sprite(AssetLoader.textures.findRegion("cloud_1"));
            } else if (i % 2 == 0) {
                clouds[i] = new Sprite(AssetLoader.textures.findRegion("cloud_2"));
            } else {
                clouds[i] = new Sprite(AssetLoader.textures.findRegion("cloud_3"));
            }

            clouds[i].setX(1920 - i * 350);
            clouds[i].setY(MathUtils.random(0, 1080 - clouds[i].getHeight()));
        }
    }

    @Override
    public void renderSky(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        for (int i = 0; i < clouds.length; i++) {
            clouds[i].draw(spriteBatch);

            clouds[i].setX(clouds[i].getX() - cloudSpeed * Gdx.graphics.getDeltaTime());
            if (clouds[i].getX() < -clouds[i].getWidth()) {
                clouds[i].setPosition(1920, MathUtils.random(0, 1080 - clouds[i].getHeight()));
            }

        }
        spriteBatch.end();
    }

    public void setCloudSpeed(float cloudSpeed) {
        this.cloudSpeed = cloudSpeed;
    }
}
