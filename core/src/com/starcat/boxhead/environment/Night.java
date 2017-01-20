package com.starcat.boxhead.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 8/15/2016.
 */
public class Night extends TimeOfDay {

    public Sprite[] stars;

    public Night() {
        skyColor = new Color(30f / 255f, 50f / 255f, 95f / 255f, 1f);
        ambientColor = new Color(.1f, .1f, .2f, 1);
        sunlightColor = new Color(.3f, .3f, .4f, 1);
        sunlightDirection = new Vector3(.8f, -.5f, .4f);

        stars = new Sprite[18];

        stars[0] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[0].setCenter(105, 1000);
        stars[1] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[1].setCenter(540, 1040);
        stars[2] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[2].setCenter(1045, 980);
        stars[3] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[3].setCenter(380, 800);
        stars[4] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[4].setCenter(1000, 650);
        stars[5] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[5].setCenter(1450, 450);
        stars[6] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[6].setCenter(1820, 350);
        stars[7] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[7].setCenter(580, 200);
        stars[8] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[8].setCenter(1485, 100);
        stars[9] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[9].setCenter(170, 540);
        stars[10] = new Sprite(AssetLoader.textures.findRegion("star_small"));
        stars[10].setCenter(1730, 840);

        stars[11] = new Sprite(AssetLoader.textures.findRegion("star_big"));
        stars[11].setCenter(250, 900);
        stars[12] = new Sprite(AssetLoader.textures.findRegion("star_big"));
        stars[12].setCenter(800, 840);
        stars[13] = new Sprite(AssetLoader.textures.findRegion("star_big"));
        stars[13].setCenter(1300, 800);
        stars[14] = new Sprite(AssetLoader.textures.findRegion("star_big"));
        stars[14].setCenter(1580, 1000);
        stars[15] = new Sprite(AssetLoader.textures.findRegion("star_big"));
        stars[15].setCenter(600, 460);
        stars[16] = new Sprite(AssetLoader.textures.findRegion("star_big"));
        stars[16].setCenter(1200, 260);
        stars[17] = new Sprite(AssetLoader.textures.findRegion("star_big"));
        stars[17].setCenter(240, 280);
    }

    @Override
    public void renderSky(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        for (int i = 0; i < stars.length; i++) {
            stars[i].draw(spriteBatch);
        }
        spriteBatch.end();
    }
}
