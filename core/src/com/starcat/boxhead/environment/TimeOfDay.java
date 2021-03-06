package com.starcat.boxhead.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 8/14/2016.
 *
 * The lighting is adjusted depending on the time of day
 */
public abstract class TimeOfDay {

    public Color skyColor;
    public Color ambientColor;
    public Color sunlightColor;
    public Vector3 sunlightDirection;

    public abstract void renderSky(SpriteBatch spriteBatch);

    public void clearSkyColor() {
        Gdx.gl.glClearColor(skyColor.r, skyColor.g, skyColor.b, 1);
    }
}
