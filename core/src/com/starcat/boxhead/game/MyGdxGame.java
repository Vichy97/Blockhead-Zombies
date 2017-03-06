package com.starcat.boxhead.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.game.screens.LoadingScreen;
import com.starcat.boxhead.particles.ZStrategyComparator;
import com.starcat.boxhead.physics.MyContactListener;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;
import com.starcat.boxhead.utils.GameUtils;

public class MyGdxGame extends Game {

    public static final boolean DEBUG = false;
    public static final boolean WIREFRAME = false;

    private OrthographicCamera UICamera;
    private OrthographicCamera gameCamera;
    private Viewport UIViewport, gameViewport;

    private AssetLoader assetLoader;

    private SpriteBatch spriteBatch;
    private DecalBatch decalBatch;
    private ModelBatch modelBatch, shadowBatch;
    private ShapeRenderer shapeRenderer;
    private StringBuilder stringBuilder;

    private FPSLogger fpsLogger;

    private Preferences settings;




    @Override
    public void create() {
        GameUtils.debug(this, "created");

        Dimensions.init();

        UICamera = new OrthographicCamera(Dimensions.getGameHeight() / Dimensions.getAspectRatio(), Dimensions.getGameHeight());
        gameCamera = new OrthographicCamera(Dimensions.getGameHeight() / Dimensions.getAspectRatio(), Dimensions.getGameHeight());
        gameCamera.rotate(-30, 1, 0, 0);
        gameCamera.rotate(225, 0, 1, 0);
        gameCamera.position.set(-40, 35, -40);
        gameCamera.near = 0;
        gameCamera.far = 200;
        gameCamera.update();

        UIViewport = new ScreenViewport(UICamera);
        gameViewport = new FillViewport(Dimensions.getGameWidth() / 30, Dimensions.getGameHeight() / 30, gameCamera);

        assetLoader = new AssetLoader();
        spriteBatch = new SpriteBatch();
        DefaultShader.Config config = new DefaultShader.Config();
        config.numDirectionalLights = 1;
        config.numPointLights = 0;
        config.numBones = 0;
        config.numSpotLights = 0;
        config.defaultCullFace = GL20.GL_BACK;
        modelBatch = new ModelBatch(new DefaultShaderProvider(config));
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        decalBatch = new DecalBatch(new CameraGroupStrategy(gameCamera, new ZStrategyComparator(gameCamera)));
        shapeRenderer = new ShapeRenderer();
        stringBuilder = new StringBuilder();
        fpsLogger = new FPSLogger();

        Bullet.init();
        MyContactListener contactListener = new MyContactListener();

        settings = Gdx.app.getPreferences("settings");

        setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {
        GameUtils.debug(this, "dispose");

        super.dispose();
        assetLoader.dispose();
        modelBatch.dispose();
        shadowBatch.dispose();
        spriteBatch.dispose();
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

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    public ModelBatch getShadowBatch() {
        return shadowBatch;
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

    public Preferences getSettings() {
        return settings;
    }

}