package com.starcat.boxhead.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 8/15/2016.
 */
public class Night extends TimeOfDay {

    public Night() {
        skyColor = new Color(30f / 255f, 50f / 255f, 95f / 255f, 1f);
        ambientColor = new Color(.1f, .1f, .2f, 1);
        sunlightColor = new Color(.3f, .3f, .4f, 1);
        sunlightDirection = new Vector3(.8f, -.5f, .4f);
    }
}
