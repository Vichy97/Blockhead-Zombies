package com.starcat.boxhead.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.entity.EntityManager;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Cloud;
import com.starcat.boxhead.objects.Map;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 6/19/2015.
 */
public class GameScreen implements Screen, InputProcessor {

    private  CameraInputController controller;
    private boolean paused = false;
    private boolean music = true;
    private boolean sound = true;

    public boolean leftPressed, rightPressed, upPressed, downPressed = false;

    private MyGdxGame game;
    private I18NBundle bundle;

    private Camera UICamera, gameCamera;
    private Viewport UIViewport, gameViewport;
    //pixel dimensions of the physical screen
    private int screenWidth;
    private int screenHeight;
    //height of the game used for the viewports
    private int gameWidth;
    private int gameHeight;

    private float aspectRatio;

    private InputMultiplexer inputMultiplexer;
    private Stage stage;
    private Table pauseTable, playTable;

    private ImageButton pauseButton, playButton, musicButton, soundButton;
    private TextButton menuButton;
    private Touchpad touchpad;

    private SpriteBatch spriteBatch;
    private ModelBatch modelBatch, shadowBatch;
    private ModelCache modelCache, shadowCache;

    private Environment environment;
    private DirectionalShadowLight sunlight;

    private btCollisionShape mapBaseCollisionShape;
    private btCollisionObject mapObjectsCollisionObject;

    private Map currentMap;

    private Cloud[] cloudArray;

    private FPSLogger fpsLogger;

    private btDiscreteDynamicsWorld dynamicsWorld;
    private DebugDrawer debugDrawer;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver constraintSolver;
    private btCollisionDispatcher dispatcher;
    private btDefaultCollisionConfiguration collisionConfig;
    private EntityManager entityManager;
    private btCollisionShape mapObjectsCollisionShape;
    private btCollisionObject mapBaseCollisionObject;


    public GameScreen(MyGdxGame game, OrthographicCamera UICamera, Viewport UIViewport, PerspectiveCamera gameCamera, Viewport gameViewport, I18NBundle bundle) {
        debug("constructor");

        this.game = game;
        this.UICamera = UICamera;
        this.UIViewport = UIViewport;
        this.gameCamera = gameCamera;
        this.gameViewport = gameViewport;
        this.bundle = bundle;

        gameWidth = MyGdxGame.GAME_WIDTH;
        gameHeight = MyGdxGame.GAME_HEIGHT;
        aspectRatio = MyGdxGame.ASPECT_RATIO;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        initUI();
        initWorld();
        initLightingAndCameras();
        initPhysics();

        entityManager.spawnPlayer(new Vector3(10, 10, 10));
        entityManager.spawnPlayer(new Vector3(10, 30, 10));
        entityManager.spawnPlayer(new Vector3(10, 50, 10));
        entityManager.spawnPlayer(new Vector3(10, 70, 10));
        entityManager.spawnPlayer(new Vector3(10, 90, 10));
        entityManager.spawnPlayer(new Vector3(10, 110, 10));

        fpsLogger = new FPSLogger();

        spriteBatch = new SpriteBatch();
    }



    @Override
    public void show() {
        debug("show");

        Gdx.graphics.setContinuousRendering(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        gameViewport.apply();
        gameCamera.update();

        renderClouds(delta);

        if (!paused) {
            dynamicsWorld.stepSimulation(delta);
        }

        modelBatch.begin(gameCamera);
        modelBatch.render(modelCache, environment);
        entityManager.render(modelBatch, environment);
        modelBatch.end();

        sunlight.begin(Vector3.Zero, gameCamera.direction);
        shadowBatch.begin(sunlight.getCamera());
        shadowBatch.render(shadowCache);
        entityManager.render(shadowBatch, environment);
        shadowBatch.end();
        sunlight.end();

        Gdx.gl.glClearColor(40f / 255f, 175f / 255f, 230f / 255f, 1f);

        if (MyGdxGame.DEBUG && !paused) {
           // debugDrawer.begin(gameCamera);
            //dynamicsWorld.debugDrawWorld();
            //debugDrawer.end();

            fpsLogger.log();
        }

        UIViewport.apply();
        UICamera.update();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        debug("resize");

        gameViewport.update(width, height);
        gameViewport.apply();
        UIViewport.update(width, height);
        UIViewport.apply();
        UICamera.position.set(UICamera.viewportWidth / 2, UICamera.viewportHeight / 2, 0);
        Gdx.graphics.requestRendering();
    }

    @Override
    public void pause() {
        debug("pause");

        pauseButton.setVisible(false);
        playButton.setVisible(true);
        menuButton.setVisible(true);
        musicButton.setVisible(true);
        soundButton.setVisible(true);
        touchpad.setVisible(false);

        Gdx.graphics.setContinuousRendering(false);
        paused = true;
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

        mapBaseCollisionShape.dispose();
        mapObjectsCollisionShape.dispose();

        dynamicsWorld.dispose();
        collisionConfig.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        constraintSolver.dispose();

        modelBatch.dispose();
        shadowBatch.dispose();

        spriteBatch.dispose();
        stage.dispose();

        System.gc();
    }



    private void initUI() {
        debug("initUI");

        controller = new CameraInputController(gameCamera);

        pauseButton = new ImageButton(AssetLoader.uiSkin.getDrawable("pause"));
        pauseButton.addListener(pauseButtonListener);

        playButton = new ImageButton(AssetLoader.uiSkin.getDrawable("play"));
        playButton.addListener(playButtonListener);
        playButton.setVisible(false);

        musicButton = new ImageButton(AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_off"));
        musicButton.addListener(musicButtonListener);
        musicButton.setVisible(false);

        soundButton = new ImageButton(AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_off"));
        soundButton.addListener(soundButtonListener);
        soundButton.setVisible(false);

        menuButton = new TextButton(bundle.get("menu"), AssetLoader.uiSkin, "small");
        menuButton.addListener(menuButtonListener);
        menuButton.setVisible(false);

        touchpad = new Touchpad(0, AssetLoader.uiSkin);


        UIViewport.apply();
        UICamera.position.set(UICamera.viewportWidth / 2, UICamera.viewportHeight / 2, 0);

        playTable = new Table();
        playTable.setFillParent(true);
        pauseTable = new Table();
        pauseTable.setFillParent(true);

        playTable.add(touchpad).pad(50).bottom().left().expand();
        playTable.add(pauseButton).top().right();

        pauseTable.add(playButton).colspan(2);
        pauseTable.row();
        pauseTable.add(soundButton);
        pauseTable.add(musicButton);
        pauseTable.row();
        pauseTable.add(menuButton).height(128).colspan(2).fillX();

        stage = new Stage(UIViewport);
        stage.addActor(playTable);
        stage.addActor(pauseTable);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(controller);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initLightingAndCameras() {
        debug("initLightingAndCameras");

        gameViewport.apply();

        gameCamera.rotate(-30, 1, 0, 0);
        gameCamera.rotate(225, 0, 1, 0);
        gameCamera.position.set(0, 10, 0);
        gameCamera.near = 1;
        gameCamera.far = 200;
        gameCamera.update();

        environment = new Environment();
        sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 50);
        sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
        environment.add(sunlight);
        environment.shadowMap = sunlight;

        Gdx.gl20.glCullFace(GL20.GL_BACK);
    }

    private void initPhysics() {
        debug("initPhysics");

        Bullet.init();

        BoundingBox boundingBox = new BoundingBox();
        currentMap.base.calculateBoundingBox(boundingBox);

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

        mapBaseCollisionShape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(.5f));
        mapBaseCollisionObject = new btCollisionObject();
        mapBaseCollisionObject.setCollisionShape(mapBaseCollisionShape);
        mapBaseCollisionObject.setWorldTransform(currentMap.base.transform);

        mapObjectsCollisionShape = Bullet.obtainStaticNodeShape(currentMap.objects.nodes);
        mapObjectsCollisionObject = new btCollisionObject();
        mapObjectsCollisionObject.setCollisionShape(mapObjectsCollisionShape);
        mapObjectsCollisionObject.setWorldTransform(currentMap.objects.transform);

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        dynamicsWorld.setDebugDrawer(debugDrawer);

        dynamicsWorld.addCollisionObject(mapBaseCollisionObject);
        dynamicsWorld.addCollisionObject(mapObjectsCollisionObject);

        entityManager = new EntityManager(dynamicsWorld);
    }

    private void initWorld() {
        currentMap = AssetLoader.map;

        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        modelCache = new ModelCache();
        modelCache.begin();
        modelCache.add(currentMap.base);
        modelCache.add(currentMap.objects);
        modelCache.add(currentMap.doodads);
        modelCache.end();

        shadowCache = new ModelCache();
        shadowCache.begin();
        shadowCache.add(currentMap.objects);
        shadowCache.add(currentMap.doodads);
        shadowCache.end();

        spawnClouds();
    }

    private void spawnClouds() {
        cloudArray = new Cloud[6];
        for (int i = 0; i < cloudArray.length; i++) {
            if (i % 3 == 0) {
                cloudArray[i] = new Cloud(1920 - i * 350, AssetLoader.cloudTexture1);
            } else if (i % 2 == 0) {
                cloudArray[i] = new Cloud(1920 - i * 350, AssetLoader.cloudTexture2);
            } else {
                cloudArray[i] = new Cloud(1920 - i * 350, AssetLoader.cloudTexture3);
            }
        }
    }

    private void renderClouds(float delta) {
        for (Cloud aCloudArray : cloudArray) {
            if (!paused) {
                aCloudArray.update(delta);
            }
            aCloudArray.render(spriteBatch);
        }
    }



    private static void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("Game Screen", message);
        }
    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            upPressed = true;
        } else if (keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        } else if (keycode == Input.Keys.DOWN) {
            downPressed = true;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = true;
        } else if (keycode == Input.Keys.SPACE) {
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.UP) {
            upPressed = false;
        } else if (keycode == Input.Keys.RIGHT) {
            rightPressed = false;
        } else if (keycode == Input.Keys.DOWN) {
            downPressed = false;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }



    private ClickListener playButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            playButton.setVisible(false);
            pauseButton.setVisible(true);
            musicButton.setVisible(false);
            soundButton.setVisible(false);
            menuButton.setVisible(false);
            touchpad.setVisible(true);

            inputMultiplexer.addProcessor(stage);
            Gdx.graphics.setContinuousRendering(true);
            paused = false;
        }
    };

    private ClickListener pauseButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            pause();

        }
    };

    private ClickListener musicButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            music = false;
        }
    };

    private ClickListener soundButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            sound = false;
        }
    };

    private ClickListener menuButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            game.setScreen("menu");
        }
    };

}