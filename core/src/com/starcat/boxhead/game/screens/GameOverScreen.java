package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 11/29/2016.
 */

public class GameOverScreen implements Screen {

    private MyGdxGame game;

    private Stage stage;
    private Table table;

    private TextButton menuButton;
    private Label gameOverLabel, scoreLabel;



    public GameOverScreen(MyGdxGame game) {
        GameUtils.debug(this, "constructor");

        this.game = game;

        menuButton = new TextButton(AssetLoader.i18NBundle.get("menu"), AssetLoader.uiSkin, "small");
        menuButton.addListener(menuListener);
        gameOverLabel = new Label(AssetLoader.i18NBundle.get("gameOver"), AssetLoader.uiSkin);
        scoreLabel = new Label(AssetLoader.i18NBundle.get("score"), AssetLoader.uiSkin);

        table = new Table();
        table.setFillParent(true);

        table.add(gameOverLabel).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getHeight() / 6);
        table.row();
        table.add(menuButton).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getHeight() / 6);
        table.row();

        stage = new Stage();
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getGameCamera().viewportWidth / 2, game.getGameCamera().viewportHeight / 2, 0);
    }



    @Override
    public void show() {
        GameUtils.debug(this, "show");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);

        game.getUICamera().update();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        GameUtils.debug(this, "resize");

        game.getUIViewport().update(width, height);
        game.getGameViewport().apply();
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

        stage.dispose();
        System.gc();
    }



    private ClickListener menuListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    };
}
