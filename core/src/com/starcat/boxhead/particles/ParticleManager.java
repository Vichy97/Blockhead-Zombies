package com.starcat.boxhead.particles;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.starcat.boxhead.utils.AssetLoader;

import java.util.ArrayList;

/**
 * Created by Vincent on 12/18/2016.
 */

public class ParticleManager {
    private ArrayList<Decal> decals;
    private TextureRegion bloodSplatter;
    private static ParticleManager instance;



    private ParticleManager() {
        decals = new ArrayList<Decal>();
        bloodSplatter = new TextureRegion(AssetLoader.bloodSpatter);
    }



    public static ParticleManager instance() {
        if(instance == null) {
            instance = new ParticleManager();
        }

        return instance;
    }

    public void renderDecals(DecalBatch decalBatch) {
        for (int i = 0; i < decals.size(); i++) {
            decalBatch.add(decals.get(i));
        }
        decalBatch.flush();
    }

    public void addBloodSpLatterDecal(float x, float z) {
        Decal decal = Decal.newDecal(bloodSplatter);
        decal.setRotation(0, 90, 0);
        decal.setScale(.01f);
        decal.translate(x, .6f, z);
        decal.setBlending(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        decals.add(decal);
    }

    public ArrayList<Decal>  getDecals() {
        return decals;
    }
}
