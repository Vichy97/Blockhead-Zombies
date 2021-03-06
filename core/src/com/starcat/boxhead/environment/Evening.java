package com.starcat.boxhead.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 8/14/2016.
 */
public class Evening extends Day {

    public Evening() {
        skyColor = new Color(110f / 255f, 145f / 255f, 185f / 255f, 1f);
        ambientColor = new Color(.2f, .2f, .2f, 1);
        sunlightColor = new Color(.5f, .4f, .4f, 1);
        sunlightDirection = new Vector3(1f, -.5f, .4f);
        cloudSpeed = 10;
    }
}
