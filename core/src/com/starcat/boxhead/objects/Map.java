package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.environment.Afternoon;
import com.starcat.boxhead.environment.TimeOfDay;

/**
 * Created by Vincent on 8/15/2016.
 */
public class Map {

    public ModelInstance base, objects, doodads;
    private TimeOfDay timeOfDay;
    private float worldSize;

    public Map(Model base, Model objects, Model doodads, float worldSize) {
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

        this.worldSize = worldSize;
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

    public void setTranslation(float x, float y, float z) {
        base.transform.setTranslation(x, y, z);
        objects.transform.setTranslation(x, y, z);
        if (doodads != null) {
            doodads.transform.setTranslation(x, y, z);
        }
    }

    public float getWorldSize() {
        return worldSize;
    }
}
