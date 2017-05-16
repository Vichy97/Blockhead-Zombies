package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 6/18/2015.
 *
 * this is the loading screen before the menu
 */
public final class LoadingScreen extends BaseScreen {

    private Sprite splashLogo;
    private Sprite splashLogo2;
    private float splashTimer = 3f;
    private float splashTimer2 = 6f;
    private boolean splashOne = true;
    private boolean init = false;

    public LoadingScreen(MyGdxGame game) {
        super(game);

        game.getAssetLoader().loadSplashScreen();
        splashLogo = new Sprite(AssetLoader.libgdxLogo);
        splashLogo.setSize(Dimensions.scaleWidth(splashLogo.getWidth()), Dimensions.scaleHeight(splashLogo.getHeight()));
        splashLogo.setX(Dimensions.getHalfScreenWidth() - splashLogo.getWidth() / 2);
        splashLogo.setY(Dimensions.getHalfScreenHeight() - splashLogo.getHeight() / 2);
        splashLogo2 = new Sprite(AssetLoader.starcatLogo);
        splashLogo2.setSize(Dimensions.scaleWidth(splashLogo2.getWidth()),Dimensions.scaleHeight(splashLogo2.getHeight()));
        splashLogo2.setX(Dimensions.getHalfScreenWidth() - splashLogo2.getWidth() / 2);
        splashLogo2.setY(Dimensions.getHalfScreenHeight() - splashLogo2.getHeight() / 2);

        game.getAssetLoader().load();
    }



    @Override
    public void render(float delta) {
        GameUtils.debug(this, "" + game.getAssetLoader().getManager().getQueuedAssets());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);

        splashTimer -= delta;
        splashTimer2 -= delta;

        if (splashTimer <= 0 && splashLogo.getColor().a > .05f && splashOne) {
            splashLogo.setColor(1, 1, 1, splashLogo.getColor().a - .1f);
        }

        if (splashLogo.getColor().a <= .1f) {
            splashOne = false;
        }

        if (splashTimer2 <= 0 && splashLogo2.getColor().a > .05f) {
            splashLogo2.setColor(1, 1, 1, splashLogo2.getColor().a - .1f);
        }

        game.getSpriteBatch().begin();
        if (splashOne) {
            splashLogo.draw(game.getSpriteBatch());
        } else {
            splashLogo2.draw(game.getSpriteBatch());
        }
        game.getSpriteBatch().end();

        //returns true if it is done loading
        if (game.getAssetLoader().update()) {
            if (!init) {
                game.getAssetLoader().initAssets();
                init = true;
            }

            if (splashLogo2.getColor().a <= .1f) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        }
    }

}
