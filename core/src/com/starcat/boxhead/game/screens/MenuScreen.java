package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 2/10/2015.
 *
 * This screens is the main menu of the game
 */
public class MenuScreen implements Screen {

    private MyGdxGame game;

    private Stage stage;
    private Table table;

    private TextButton play;
    private TextButton exit;



    public MenuScreen(final MyGdxGame game) {
        GameUtils.debug(this, "constructor");

        this.game = game;

        stage = new Stage(game.getUIViewport());
        table = new Table();
        table.setFillParent(true);

        play = new TextButton(AssetLoader.i18NBundle.get("play"), AssetLoader.uiSkin, "large");
        play.addListener(playListener);
        exit = new TextButton(AssetLoader.i18NBundle.get("exit"), AssetLoader.uiSkin, "large");
        exit.addListener(exitListener);

        table.add(play).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getHeight() / 6);
        table.row();
        table.add(exit).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getHeight() / 6);
        stage.addActor(table);

        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getUICamera().viewportWidth / 2, game.getUICamera().viewportHeight / 2, 0);
    }



     @Override
    public void show() {
        GameUtils.debug(this, "show");

        Gdx.input.setInputProcessor(stage);

        //TODO: might need to change this if button animations are added
        Gdx.graphics.setContinuousRendering(false);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.graphics.requestRendering();
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

        stage.dispose();
        System.gc();
    }



    private ClickListener playListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            game.setScreen(new GameScreen(game));
            dispose();
        }
    };

    private ClickListener exitListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            Gdx.app.exit();
        }
    };
}