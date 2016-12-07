package com.starcat.boxhead.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.game.screens.AboutScreen;
import com.starcat.boxhead.game.screens.GameOverScreen;
import com.starcat.boxhead.game.screens.GameScreen;
import com.starcat.boxhead.game.screens.LoadingScreen;
import com.starcat.boxhead.game.screens.MenuScreen;
import com.starcat.boxhead.game.screens.SettingsScreen;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.GameUtils;

public class MyGdxGame extends Game {

    //TODO: make all batches static fields of this class
    public static final boolean DEBUG = false;
    public static final boolean WIREFRAME = true;

    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;
    public static float ASPECT_RATIO;

    //TODO: one camera (viewport.apply())
    private OrthographicCamera UICamera;
    private OrthographicCamera gameCamera;
    private Viewport UIViewport, gameViewport;



    @Override
    public void create() {
        GameUtils.debug(this, "created");

        ASPECT_RATIO = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();

        UICamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
        gameCamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);

        UIViewport = new ScreenViewport(UICamera);
        gameViewport = new FillViewport(GAME_WIDTH / 150, GAME_HEIGHT / 150, gameCamera);

        setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {
        GameUtils.debug(this, "dispose");

        super.dispose();
        AssetLoader.dispose();
    }



    public OrthographicCamera getUICamera() {
        return UICamera;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public Viewport getUIViewport() {
        return UIViewport;
    }

    public Viewport getGameViewport() {
        return gameViewport;
    }

}
