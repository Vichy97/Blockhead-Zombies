package com.vincent.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vincent.game.MyGdxGame;
import com.vincent.util.AssetLoader;

/**
 * Created by Vincent on 8/10/2015.
 *
 * Select which map you want to play
 */
public class MapSelectScreen implements Screen {

    private MyGdxGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private I18NBundle bundle;

    private Drawable buttonDrawable;
    private TextButton.TextButtonStyle textButtonStyle;
    private BitmapFont textFont;

    private TextButton map1;

    private Table table;
    private Stage stage;

    public MapSelectScreen(final MyGdxGame game, OrthographicCamera camera, Viewport viewport, I18NBundle bundle) {
        debug("constructor");
        this.game = game;
        this.camera = camera;
        this.viewport = viewport;
        this.bundle = bundle;

        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        stage = new Stage(viewport);
        table = new Table();

        //set the button style
        buttonDrawable = new NinePatchDrawable(AssetLoader.button);
        textButtonStyle = new TextButton.TextButtonStyle();
        textFont = AssetLoader.createFont("DroidSans.ttf", 100);
        textFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textButtonStyle.font = textFont;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.down = buttonDrawable;
        textButtonStyle.up = buttonDrawable;

        //add the buttons and set their click listeners
        map1 = new TextButton("MAP 1", textButtonStyle);
        map1.pad(20);

        map1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setCurrentMap(AssetLoader.map1TileMap, AssetLoader.map1ObjectMap);
                game.setScreen("game");
            }
        });


        //add ui elements to the table then add that to the stage
        table.setBounds(0, 0, MyGdxGame.GAME_WIDTH, MyGdxGame.GAME_HEIGHT);
        table.add(map1).width(300).center();
        table.row();
        stage.addActor(table);
    }

    @Override
    public void show() {
        debug("show");

        Gdx.input.setInputProcessor(stage);

        //since there is no animation we dont need to render at 60fps we can only render once
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

        //render ui elements and receive input from them
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

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        debug("dispose");
        stage.dispose();
        textFont.dispose();
        System.gc();
    }

    //method for writing to the log
    private void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("Map Select Screen", message);
        }
    }
}
