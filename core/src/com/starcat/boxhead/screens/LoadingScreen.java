package com.starcat.boxhead.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 6/18/2015.
 *
 * this is the loading screen before the menu
 */
public class LoadingScreen implements Screen {

    private MyGdxGame game;



    public LoadingScreen(MyGdxGame game) {
        debug("constructor");
        this.game = game;

        //queue assets to be loaded to the asset manager
        AssetLoader.load();
    }



    @Override
    public void show() {
        debug("show");
    }

    @Override
    public void render(float delta) {
        debug("" + AssetLoader.getQueuedAssets());

        //update asset manager if it isn't done loading
        if (AssetLoader.update()) {
            //if it is done, initialize assets before moving on
            AssetLoader.initAssets();
            //switch to the menu
            game.setScreen("menu");
        }

        float progress = AssetLoader.getProgress();
    }

    @Override
    public void resize(int width, int height) {
        debug("resize");
    }

    //you cant pause the loading screen
    @Override
    public void pause() {
        debug("pause");
    }

    @Override
    public void resume() {
        debug("resume");
    }

    @Override
    public void hide() {
        debug("hide");
        dispose();
    }

    @Override
    public void dispose() {
        debug("dispose");
        System.gc();
    }



    //method for writing to the log
    private void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("Loading Screen", message);
        }
    }
}
