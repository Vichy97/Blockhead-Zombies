package com.starcat.boxhead.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Player extends Entity {

    public Player(Vector3 position, btRigidBodyConstructionInfo constructionInfo) {
        super(position, new ModelInstance(AssetLoader.boxhead), constructionInfo);
    }
}
