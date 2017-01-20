package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 12/8/2016.
 */

public class BaseScreen implements Screen, InputProcessor {

    protected MyGdxGame game;



    public BaseScreen(MyGdxGame game) {
        this.game = game;
    }



    @Override
    public void show() {
        GameUtils.debug(this, "show");
    }

    @Override
    public void render(float delta) {
        update(delta);
    }

    public void update(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        GameUtils.debug(this, "resize");

        game.getGameViewport().update(width, height);
        game.getGameViewport().apply();
        game.getUIViewport().update(width, height);
        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getUICamera().viewportWidth / 2, game.getUICamera().viewportHeight / 2, 0);

        Gdx.graphics.requestRendering();
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



    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
