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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.environment.Afternoon;
import com.starcat.boxhead.environment.Evening;
import com.starcat.boxhead.environment.Night;
import com.starcat.boxhead.objects.GameObject;
import com.starcat.boxhead.objects.Star;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Cloud;
import com.starcat.boxhead.objects.Map;
import com.starcat.boxhead.physics.MyContactListener;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Flags;
import com.starcat.boxhead.utils.GameUtils;

import java.util.ArrayList;

/**
 * Created by Vincent on 6/19/2015.
 */
public class GameScreen implements Screen, InputProcessor {

    private boolean paused = false;

    public boolean leftPressed, rightPressed, upPressed, downPressed = false;

    private MyGdxGame game;

    private Camera UICamera, gameCamera;
    private Viewport UIViewport, gameViewport;

    private InputMultiplexer inputMultiplexer;
    private Stage stage;
    private Table pauseTable, playTable;

    private Label debugLabel;
    private StringBuilder stringBuilder;

    private ImageButton pauseButton, playButton, musicButton, soundButton;
    private TextButton menuButton;
    private Touchpad touchpad;

    private SpriteBatch spriteBatch;

    private ModelCache modelCache;
    private ModelBatch modelBatch, shadowBatch;
    private ArrayList<GameObject> modelInstances, shadowInstances;
    private int visibleModelInstanceCount = 0;

    private Environment environment;
    private DirectionalShadowLight sunlight;

    private btCollisionShape mapBaseCollisionShape;
    private btCollisionObject mapObjectsCollisionObject;

    private Map currentMap;

    private Cloud[] cloudArray;
    private Star[] stars;

    private FPSLogger fpsLogger;

    private btDiscreteDynamicsWorld dynamicsWorld;
    private DebugDrawer debugDrawer;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver constraintSolver;
    private btCollisionDispatcher dispatcher;
    private btDefaultCollisionConfiguration collisionConfig;
    private btCollisionShape mapObjectsCollisionShape;
    private btCollisionObject mapBaseCollisionObject;
    private MyContactListener contactListener;

    private Player player;



    public GameScreen(MyGdxGame game, OrthographicCamera UICamera, Viewport UIViewport, OrthographicCamera gameCamera, Viewport gameViewport) {
        debug("constructor");

        this.game = game;
        this.UICamera = UICamera;
        this.UIViewport = UIViewport;
        this.gameCamera = gameCamera;
        this.gameViewport = gameViewport;

        initUI();
        initWorld();
        initLightingAndCameras();
        initPhysics();

        player = EntityManager.spawnPlayer(new Vector3(10, 1.54f, 10), .055f);

        fpsLogger = new FPSLogger();

        spriteBatch = new SpriteBatch();
        stringBuilder = new StringBuilder();
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

        if (currentMap.getTimeOfDay() instanceof Night) {
            renderStars();
        } else {
            renderClouds(delta);
        }

        if (!paused) {
            dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 65f);
        }

        int direction;
        if (upPressed) {
            if (leftPressed) {
                direction = 8;
            } else if (rightPressed) {
                direction = 2;
            } else {
                direction = 1;
            }
        } else if (downPressed) {
            if (leftPressed) {
                direction = 6;
            } else if (rightPressed) {
                direction = 4;
            } else {
                direction = 5;
            }
        } else if (leftPressed) {
            direction = 7;
        } else if (rightPressed) {
            direction = 3;
        } else {
            direction = GameUtils.getTouchpadEightDirection(touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
        }

        if (!player.isMoving()) {
            player.setDirection(direction);
        } else if (player.getDirection() != direction) {
            player.setDirection(direction);
        }

        if (!paused) {
            EntityManager.update(Gdx.graphics.getDeltaTime());
        }

        visibleModelInstanceCount = 0;
        modelBatch.begin(gameCamera);
        for(GameObject object : modelInstances) {
            if (isVisible(object)) {
                modelBatch.render(object, environment);
                visibleModelInstanceCount++;
            }
        }
        EntityManager.renderEntities(modelBatch, environment);
        EntityManager.renderBullets(modelBatch, environment);
        modelBatch.end();

        sunlight.begin(Vector3.Zero, gameCamera.direction);
        shadowBatch.begin(sunlight.getCamera());
        for(GameObject object : shadowInstances) {
            if (isVisible(object)) {
                shadowBatch.render(object, environment);
            }
        }
        EntityManager.renderEntities(shadowBatch, environment);
        shadowBatch.end();
        sunlight.end();

        Vector3 position = player.getPosition();
        gameCamera.position.set(position.x - 10, 10, position.z - 10);

        Gdx.gl.glClearColor(currentMap.getTimeOfDay().skyColor.r, currentMap.getTimeOfDay().skyColor.g, currentMap.getTimeOfDay().skyColor.b, 1);

        if (MyGdxGame.DEBUG && !paused) {
            debugDrawer.begin(gameCamera);
            dynamicsWorld.debugDrawWorld();
            debugDrawer.end();
        }

        //fpsLogger.log();

        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleModelInstanceCount);
        debugLabel.setText(stringBuilder);

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

        EntityManager.dispose();
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

    private boolean isVisible(GameObject instance) {
        return gameCamera.frustum.sphereInFrustum(instance.center, instance.radius);
    }



    private void initUI() {
        debug("initUI");

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

        menuButton = new TextButton(AssetLoader.i18NBundle.get("menu"), AssetLoader.uiSkin, "small");
        menuButton.addListener(menuButtonListener);
        menuButton.setVisible(false);

        touchpad = new Touchpad(0, AssetLoader.uiSkin);

        debugLabel = new Label("debugLabel", AssetLoader.uiSkin);
        debugLabel.setY(MyGdxGame.GAME_HEIGHT - debugLabel.getHeight());

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
        stage.addActor(debugLabel);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
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
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);

        mapBaseCollisionShape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(.5f));
        mapBaseCollisionObject = new btCollisionObject();
        mapBaseCollisionObject.setCollisionShape(mapBaseCollisionShape);
        mapBaseCollisionObject.setWorldTransform(currentMap.base.transform.translate(0, -.05f, 0));
        mapBaseCollisionObject.setRestitution(.5f);

        mapObjectsCollisionShape = Bullet.obtainStaticNodeShape(currentMap.objects.nodes);
        mapObjectsCollisionObject = new btCollisionObject();
        mapObjectsCollisionObject.setCollisionShape(mapObjectsCollisionShape);
        mapObjectsCollisionObject.setWorldTransform(currentMap.objects.transform);
        mapObjectsCollisionObject.setContactCallbackFlag(Flags.OBJECT_FLAG);
        mapObjectsCollisionObject.setContactCallbackFilter(0);

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
        dynamicsWorld.setDebugDrawer(debugDrawer);

        dynamicsWorld.addCollisionObject(mapBaseCollisionObject, (short)Flags.GROUND_FLAG, (short)(Flags.OBJECT_FLAG | Flags.ENTITY_FLAG |Flags.BULLET_CASING_FLAG));
        dynamicsWorld.addCollisionObject(mapObjectsCollisionObject, (short)Flags.OBJECT_FLAG, (short)(Flags.BULLET_CASING_FLAG | Flags.ENTITY_FLAG | Flags.BULLET_FLAG));

        EntityManager.setDynamicsWorld(dynamicsWorld);

        contactListener = new MyContactListener();
    }

    private void initWorld() {
        currentMap = AssetLoader.map;

        modelCache = new ModelCache();

        DefaultShader.Config config = new DefaultShader.Config();
        config.numDirectionalLights = 1;
        config.numPointLights = 0;
        config.numBones = 0;
        config.defaultCullFace = GL20.GL_BACK;
        modelBatch = new ModelBatch(new DefaultShaderProvider(config));
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        modelInstances = new ArrayList<GameObject>();
        shadowInstances = new ArrayList<GameObject>();
        for (int i = 0; i < AssetLoader.mapBase.nodes.size; i++) {
            String id = AssetLoader.mapBase.nodes.get(i).id;
            GameObject instance = new GameObject(AssetLoader.mapBase, id);
            Node node = instance.getNode(id);

            instance.transform.set(node.globalTransform);
            node.translation.set(0, 0, 0);
            node.scale.set(1, 1, 1);
            node.rotation.idt();
            instance.calculateTransforms();

            modelInstances.add(instance);
        }

        for (int i = 0; i < AssetLoader.mapObjects.nodes.size; i++) {
            String id = AssetLoader.mapObjects.nodes.get(i).id;
            GameObject instance = new GameObject(AssetLoader.mapObjects, id);
            Node node = instance.getNode(id);

            instance.transform.set(node.globalTransform);
            node.translation.set(0, 0, 0);
            node.scale.set(1, 1, 1);
            node.rotation.idt();
            instance.calculateTransforms();

            modelInstances.add(instance);
            shadowInstances.add(instance);
        }

        for (int i = 0; i < AssetLoader.mapDoodads.nodes.size; i++) {
            String id = AssetLoader.mapDoodads.nodes.get(i).id;
            GameObject instance = new GameObject(AssetLoader.mapDoodads, id);
            Node node = instance.getNode(id);

            instance.transform.set(node.globalTransform);
            node.translation.set(0, 0, 0);
            node.scale.set(1, 1, 1);
            node.rotation.idt();
            instance.calculateTransforms();

            modelInstances.add(instance);
            shadowInstances.add(instance);
        }

        spawnClouds();
        spawnStars();
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

    private void spawnStars() {
        stars = new Star[18];
        stars[0] = new Star(105, 1000, AssetLoader.starTexture2);
        stars[1] = new Star(540, 1040, AssetLoader.starTexture2);
        stars[2] = new Star(1045, 980, AssetLoader.starTexture2);
        stars[3] = new Star(380, 800, AssetLoader.starTexture2);
        stars[4] = new Star(1000, 650, AssetLoader.starTexture2);
        stars[5] = new Star(1450, 450, AssetLoader.starTexture2);
        stars[6] = new Star(1820, 350, AssetLoader.starTexture2);
        stars[7] = new Star(580, 200, AssetLoader.starTexture2);
        stars[8] = new Star(1485, 100, AssetLoader.starTexture2);

        stars[9] = new Star(250, 900, AssetLoader.starTexture1);
        stars[10] = new Star(800, 840, AssetLoader.starTexture1);
        stars[11] = new Star(1300, 800, AssetLoader.starTexture1);
        stars[12] = new Star(1580, 1000, AssetLoader.starTexture1);
        stars[13] = new Star(600, 460, AssetLoader.starTexture1);
        stars[14] = new Star(1200, 260, AssetLoader.starTexture1);
        stars[15] = new Star(240, 280, AssetLoader.starTexture1);
        stars[16] = new Star(170, 540, AssetLoader.starTexture2);
        stars[17] = new Star(1730, 840, AssetLoader.starTexture2);
    }

    private void renderStars() {
        for (Star star : stars) {
                star.render(spriteBatch);
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
            player.fire();
        } else if (keycode == Input.Keys.BACKSPACE) {
            GameUtils.takeScreenshot();
        } else if (keycode == Input.Keys.NUM_1) {
            currentMap.setTimeOfDay(new Afternoon());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 50);
            sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
        } else if (keycode == Input.Keys.NUM_2) {
            currentMap.setTimeOfDay(new Evening());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 50);
            sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
        } else if (keycode == Input.Keys.NUM_3) {
            currentMap.setTimeOfDay(new Night());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 50);
            sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
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
        player.fire();
        return true;
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
            AssetLoader.button_click.play();
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
            AssetLoader.button_click.play();
            pause();
        }
    };

    private ClickListener musicButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener soundButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener menuButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            game.setScreen("menu");
        }
    };

}