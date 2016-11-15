package com.starcat.boxhead.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 2/10/2015.
 *
 * The menu of the game
 */
public class MenuScreen implements Screen {

    private MyGdxGame game;

    private Camera camera;
    private Viewport viewport;

    private Stage stage;
    private Table table;

    private TextButton play;
    private TextButton exit;



    public MenuScreen(final MyGdxGame game, OrthographicCamera camera, Viewport viewport) {
        debug("constructor");

        this.game = game;
        this.camera = camera;
        this.viewport = viewport;

        stage = new Stage(viewport);
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

        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }



     @Override
    public void show() {
        debug("show");

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

        camera.update();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        debug("resize");

        viewport.update(width, height);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        Gdx.graphics.requestRendering();
    }

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
        stage.dispose();
        System.gc();
    }



    private void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("Menu Screen", message);
        }
    }



    private ClickListener playListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            game.setScreen("game");
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
