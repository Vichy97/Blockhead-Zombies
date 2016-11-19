package com.starcat.boxhead.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by Vincent on 9/1/2016.
 *
 * This class is used for game objects that aren't dynamic
 * (trees or rocks for example) for dynamic objects use
 * DynamicGameObject. This class is mainly used for
 * frustum culling (otherwise you just create a collsion mesh
 * from the map and dont bother making a StaticGameObject)
 */
public class StaticGameObject extends ModelInstance {
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public final float radius;

    private final static BoundingBox bounds = new BoundingBox();

    public StaticGameObject(Model model, String rootNode, boolean mergeTransform) {
        super(model, rootNode, mergeTransform);
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2f;
    }

    public StaticGameObject(Model model, String rootNode) {
        super(model, rootNode);
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2;
    }
}
