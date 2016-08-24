package com.starcat.boxhead.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 8/14/2016.
 */
public class Afternoon extends TimeOfDay {

    public Afternoon() {
        skyColor = new Color(85f / 255f, 180f / 255f, 230f / 255f, 1f);
        ambientColor = new Color(.35f, .35f, .35f, 1);
        sunlightColor = new Color(.5f, .5f, .5f, 1);
        sunlightDirection = new Vector3(.3f, -.35f, .1f);
    }

}
