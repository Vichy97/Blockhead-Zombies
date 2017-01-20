package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.starcat.boxhead.environment.Afternoon;
import com.starcat.boxhead.environment.TimeOfDay;

/**
 * Created by Vincent on 8/15/2016.
 */
public class Map {

    public ModelInstance base, objects, doodads;
    private TimeOfDay timeOfDay;

    public Map(Model base, Model objects, Model doodads) {
        if (base != null) {
            this.base = new ModelInstance(base);
        }
        if (objects != null) {
            this.objects = new ModelInstance(objects);
        }
        if (doodads != null) {
            this.doodads = new ModelInstance(doodads);
        }

        timeOfDay = new Afternoon();
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public void renderSky(SpriteBatch spriteBatch) {
        timeOfDay.renderSky(spriteBatch);
    }

    public void clearSkyColor() {
        timeOfDay.clearSkyColor();
    }
}
