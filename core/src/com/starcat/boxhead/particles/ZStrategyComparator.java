package com.starcat.boxhead.particles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import java.util.Comparator;

/**
 * Created by Vincent on 12/18/2016.
 */

public class ZStrategyComparator implements Comparator<Decal> {

    Camera camera;

    public ZStrategyComparator(Camera camera) {
        this.camera = camera;
    }


    @Override
    public int compare(Decal o1, Decal o2) {
        float dist1 = camera.position.dst(0, 0, o1.getPosition().z);
        float dist2 = camera.position.dst(0, 0, o2.getPosition().z);
        return (int)Math.signum(dist1 - dist2);
    }
}
