package com.starcat.boxhead.particles;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.starcat.boxhead.utils.AssetLoader;

import java.util.ArrayList;

/**
 * Created by Vincent on 12/18/2016.
 */

public class ParticleManager {

    private static final ParticleManager instance = new ParticleManager();

    private ArrayList<Decal> decals;
    private TextureRegion bloodSplatter;



    private ParticleManager() {
        decals = new ArrayList<Decal>();
        bloodSplatter = new TextureRegion(AssetLoader.textures.findRegion("blood_spatter_1"));
    }



    public static ParticleManager instance() {
        return instance;
    }

    public void renderDecals(DecalBatch decalBatch) {
        for (int i = 0; i < decals.size(); i++) {
            decalBatch.add(decals.get(i));
        }
        decalBatch.flush();
    }

    public void addBloodSpLatterDecal(float x, float z) {
        Decal decal;

        switch (MathUtils.random(0, 2)) {
            case 0: {
                decal = Decal.newDecal(AssetLoader.textures.findRegion("blood_spatter_1"));
                break;
            } case 1: {
                decal = Decal.newDecal(AssetLoader.textures.findRegion("blood_spatter_2"));
                break;
            } case 2: {
                decal = Decal.newDecal(AssetLoader.textures.findRegion("blood_spatter_3"));
                break;
            } default: {
                decal = Decal.newDecal(AssetLoader.textures.findRegion("blood_spatter_1"));
                break;
            }
        }
        decal.setRotation(MathUtils.random(1, 360), 90, 0);
        decal.setScale(.01f);
        decal.translate(x, 0f, z);
        decal.setBlending(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        decals.add(decal);
    }

    public ArrayList<Decal>  getDecals() {
        return decals;
    }

    public void clear() {
        decals.clear();
    }
}
