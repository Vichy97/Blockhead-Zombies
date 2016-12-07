package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Screen;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 12/3/2016.
 */

public class AboutScreen implements Screen {

    private MyGdxGame game;



    public AboutScreen(MyGdxGame game) {
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
        GameUtils.debug(this, "hide");
    }

    @Override
    public void dispose() {
        GameUtils.debug(this, "dispose");
    }

}
