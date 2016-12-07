package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Screen;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 6/18/2015.
 *
 * this is the loading screen before the menu
 */
public class LoadingScreen implements Screen {

    private MyGdxGame game;



    public LoadingScreen(MyGdxGame game) {
        GameUtils.debug(this, "constructor");
        this.game = game;

        AssetLoader.load();
    }



    @Override
    public void show() {
        GameUtils.debug(this, "show");
    }

    @Override
    public void render(float delta) {
        GameUtils.debug(this, "" + AssetLoader.getQueuedAssets());

        //returns true if it is done loading
        if (AssetLoader.update()) {
            AssetLoader.initAssets();
            game.setScreen(new MenuScreen(game));
            dispose();
        }

        float progress = AssetLoader.getProgress();
    }

    @Override
    public void resize(int width, int height) {
        GameUtils.debug(this, "resize");
    }

    @Override
    public void pause() {
        GameUtils.debug(this, "pause");
    }

    @Override
    public void resume() {
        GameUtils.debug(this, "resume");
    }

    @Override
    public void hide() {
        GameUtils.debug(this, "hide");
    }

    @Override
    public void dispose() {
        GameUtils.debug(this, "dispose");

        System.gc();
    }
}
