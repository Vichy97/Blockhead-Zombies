package com.vincent.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vincent.util.AssetLoader;

import java.util.Locale;

public class MyGdxGame extends Game {

    public static final boolean DEBUG = true;

    //width and height of the game. note these are not the size of the actual screen
    //these can be in any measurements you want but it is easy to make
    //it a resolution size and design all your assets for the specific size.
    //then you can just draw them without worrying about scaling and the viewport
    //will scale them for different screen sizes and resolutions automatically
    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;
    //aspect ratio of the actual screen size
    public static float ASPECT_RATIO;

    //various screens for the games different states
    public com.vincent.screens.LoadingScreen loadingScreen;
    public com.vincent.screens.MenuScreen menuScreen;
    public com.vincent.screens.GameScreen gameScreen;

    //reuse the viewports and camera through all the states
    private OrthographicCamera UICamera, gameCamera;
    //viewport for the game. you might want to experiment with
    //different viewports based on your game type or style
    private Viewport UIViewport, gameViewport;

    @Override
    public void create() {
        Gdx.app.log("Game", "created");

        ASPECT_RATIO = (float)Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();

        UICamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
        gameCamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);

        UIViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, UICamera);
        gameViewport = new FillViewport(GAME_WIDTH, GAME_HEIGHT, gameCamera);

        //support for different languages
        FileHandle fileHandle = Gdx.files.internal("locales/Boxhead");
        I18NBundle bundle = I18NBundle.createBundle(fileHandle, Locale.getDefault());


        //since these are initialized here you must be sure not to access any resources in
        //their constructors because the assets have not been loaded (they load in the show() method of LoadingScreen)
        //and you will get a null pointer exception. access resources in show() instead
        loadingScreen = new com.vincent.screens.LoadingScreen(this);
        menuScreen = new com.vincent.screens.MenuScreen(this, UICamera, UIViewport, bundle);
        gameScreen = new com.vincent.screens.GameScreen(this, UICamera, UIViewport, gameCamera, gameViewport, bundle);

        //the first screen is the loading screen
        setScreen(loadingScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
        Gdx.app.log("Game ", "dispose");
    }
}
