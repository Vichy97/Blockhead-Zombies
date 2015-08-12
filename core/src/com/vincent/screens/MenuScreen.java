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

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vincent.game.MyGdxGame;
import com.vincent.util.AssetLoader;


/**
 * Created by Vincent on 2/10/2015.
 *
 * The menu of the game
 */
public class MenuScreen implements Screen {

    private MyGdxGame game;

    private I18NBundle bundle;

    //you want to add all the ui elements to the table not directly to the stage
    //this allows you to set their position and dimensions more easily
    //for more info on how stages an tables work visit the libgdx documentation
    private Stage stage;
    private Table table;

    private TextButton playButton, exitButton;
    private TextButton.TextButtonStyle textButtonStyle;
    private NinePatchDrawable buttonDrawable;

    private OrthographicCamera camera;
    private Viewport viewport;

    private BitmapFont textFont;


    public MenuScreen(final MyGdxGame game, OrthographicCamera camera, Viewport viewport, I18NBundle bundle) {
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
        playButton = new TextButton(bundle.get("play"), textButtonStyle);
        playButton.pad(20);
        exitButton = new TextButton(bundle.get("exit"), textButtonStyle);
        exitButton.pad(20);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen("map select");
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //add ui elements to the table then add that to the stage
        table.setBounds(0, 0, MyGdxGame.GAME_WIDTH, MyGdxGame.GAME_HEIGHT);
        table.add(playButton).width(300).center();
        table.row();
        table.add(exitButton).width(300).center();
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

    //you cant pause the menu
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
        textFont.dispose();
        System.gc();
    }

    //method for writing to the log
    private void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("MenuScreen", message);
        }
    }
}
