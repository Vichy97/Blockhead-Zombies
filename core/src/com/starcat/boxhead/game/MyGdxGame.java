package com.starcat.boxhead.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.SimpleOrthoGroupStrategy;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.game.screens.LoadingScreen;
import com.starcat.boxhead.particles.ZStrategyComparator;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.GameUtils;

public class MyGdxGame extends Game {

    public static final boolean DEBUG = false;
    public static final boolean WIREFRAME = true;

    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;
    public static float ASPECT_RATIO;

    private OrthographicCamera UICamera;
    private OrthographicCamera gameCamera;
    private Viewport UIViewport, gameViewport;

    private AssetLoader assetLoader;
    private SpriteBatch spriteBatch;
    private DecalBatch decalBatch;
    private ShapeRenderer shapeRenderer;
    private StringBuilder stringBuilder;
    private FPSLogger fpsLogger;



    @Override
    public void create() {
        GameUtils.debug(this, "created");

        ASPECT_RATIO = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();

        UICamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
        gameCamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
        gameCamera.rotate(-30, 1, 0, 0);
        gameCamera.rotate(225, 0, 1, 0);
        gameCamera.position.set(0, 10, 0);
        gameCamera.near = 1;
        gameCamera.far = 200;
        gameCamera.update();

        UIViewport = new ScreenViewport(UICamera);
        gameViewport = new FillViewport(GAME_WIDTH / 100, GAME_HEIGHT / 100, gameCamera);

        assetLoader = new AssetLoader();
        spriteBatch = new SpriteBatch();
        decalBatch = new DecalBatch(new CameraGroupStrategy(gameCamera, new ZStrategyComparator(gameCamera)));
        shapeRenderer = new ShapeRenderer();
        stringBuilder = new StringBuilder();
        fpsLogger = new FPSLogger();

        setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {
        GameUtils.debug(this, "dispose");

        super.dispose();
        assetLoader.dispose();
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

    public AssetLoader getAssetLoader() {
        return assetLoader;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public DecalBatch getDecalBatch() {
        return decalBatch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public FPSLogger getFpsLogger() {
        return fpsLogger;
    }

}