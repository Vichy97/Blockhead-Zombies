package com.vincent.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vincent.screens.GameScreen;
import com.vincent.screens.LoadingScreen;
import com.vincent.screens.MapSelectScreen;
import com.vincent.screens.MenuScreen;
import com.vincent.util.AssetLoader;

import java.util.Locale;

public class MyGdxGame extends Game {

    //whether debug logs will be written and other debug info such as collision boxes will be drawn
    public static final boolean DEBUG = false;

    //width and height of the game. note these are not the size of the actual screen
    //these can be in any measurements you want but it is easy to make
    //it a resolution size and design all your assets for the specific size.
    //then you can just draw them without worrying about scaling and the viewport
    //will scale them for different screen sizes and resolutions automatically
    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;
    //aspect ratio of the actual screen size
    public static float ASPECT_RATIO;

    //reuse the viewports and camera through all the states
    private OrthographicCamera UICamera, gameCamera;

    /*
    * viewport for the game. you might want to experiment with
    * different viewports based on your game type or style
    */
    private Viewport UIViewport, gameViewport;

    //I18NBundles handle localization for different countries
    private I18NBundle bundle;

    //maps are drawn in parts. all of the tiles first then all of the objects
    private TiledMap currentTileMap, currentObjectMap;

    @Override
    public void create() {
        Gdx.app.log("Game", "created");

        ASPECT_RATIO = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

        UICamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
        gameCamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);

        UIViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, UICamera);
        gameViewport = new FillViewport(GAME_WIDTH, GAME_HEIGHT, gameCamera);

        //support for different languages
        FileHandle fileHandle = Gdx.files.internal("locales/Boxhead");
        bundle = I18NBundle.createBundle(fileHandle, Locale.getDefault());

        //Gdx.graphics.setVSync(true); //may cause stuttering? (only on pc not android)
        Gdx.graphics.setTitle(bundle.get("gameTitle"));

        //the first screen is the loading screen
        setScreen("loading");
    }

    /*
    * Screens are similar to fragments or activities. each one is a different "page" of the game
    * such as menu or score screen
    */
    public void setScreen(String screen) {
        switch (screen) {
            case "loading": {
                setScreen(new LoadingScreen(this));
                break;
            } case "menu": {
                setScreen(new MenuScreen(this, UICamera, UIViewport, bundle));
                break;
            } case "map select": {
                setScreen(new MapSelectScreen(this, UICamera, UIViewport, bundle));
                break;
            } case "game": {
                setScreen(new GameScreen(this, UICamera, UIViewport, gameCamera, gameViewport, bundle, currentTileMap, currentObjectMap));
                break;
            } default: {
                debug("Incorrect Screen Name");
                Gdx.app.exit();
            }
        }
        //garbage collection when switching screens to avoid garbage collection during gameplay that could cause lag spikes
        System.gc();
    }

    public void setCurrentMap(TiledMap tileMap, TiledMap objectMap) {
        currentTileMap = tileMap;
        currentObjectMap = objectMap;
    }

    //method for writing to the log
    private static void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("My GDX Game", message);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
        Gdx.app.log("Game ", "dispose");
    }
}
