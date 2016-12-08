package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Screen;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 12/3/2016.
 */

public class SettingsScreen implements Screen {

    private MyGdxGame game;



    public SettingsScreen(MyGdxGame game) {
        GameUtils.debug(this, "constructor");

        this.game = game;
    }



    @Override
    public void show() {
        GameUtils.debug(this, "show");
    }

    @Override
    public void render(float delta) {

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
        GameUtils.debug(this, "dispose");
    }

    @Override
    public void dispose() {
        GameUtils.debug(this, "dispose");

        System.gc();
    }

}