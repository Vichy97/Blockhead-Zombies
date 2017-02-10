package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
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
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starcat.boxhead.environment.Afternoon;
import com.starcat.boxhead.environment.Evening;
import com.starcat.boxhead.environment.Night;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Map;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.particles.ParticleManager;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.physics.CollisionFlags;
import com.starcat.boxhead.utils.Dimensions;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 6/19/2015.
 *
 * This Screen is where all actual game play takes place
 */
public final class GameScreen extends BaseScreen {

    private boolean paused = false;
    private boolean touched = false;

    private InputMultiplexer inputMultiplexer;
    private Stage stage;
    private Table pauseTable, playTable, gameOverTable;

    private Label debugLabel, gameOverLabel, continueLabel;

    private ImageButton pauseButton, playButton, nextWeaponButton, previousWeaponButton;
    private Touchpad touchpad;

    private float gameOverTableOpacity = 0;
    private float gameOverTableTint = 0;

    private ModelCache modelCache, shadowCache;

    private Environment environment;
    private DirectionalShadowLight sunlight;

    private btCollisionShape mapBaseCollisionShape;
    private btCollisionObject mapObjectsCollisionObject;

    private EntityManager entityManager;
    private ParticleManager particleManager;

    private Map map;

    private btDiscreteDynamicsWorld dynamicsWorld;
    private DebugDrawer debugDrawer;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver constraintSolver;
    private btCollisionDispatcher dispatcher;
    private btDefaultCollisionConfiguration collisionConfig;
    private btCollisionShape mapObjectsCollisionShape;
    private btCollisionObject mapBaseCollisionObject;

    private long diff, start = System.currentTimeMillis();

    private float worldSize;
    private float targetWorldSize;



    public GameScreen(MyGdxGame game, Map map, Texture playerSkin) {
        super(game);

        map.setTranslation(0, 0, 0);
        this.map = map;

        targetWorldSize = 90;
        worldSize = map.getWorldSize();

        initUI();
        initWorld();
        initLighting();
        initPhysics();

        particleManager = ParticleManager.instance();
        entityManager = EntityManager.instance();
        entityManager.setDynamicsWorld(dynamicsWorld);

        entityManager.spawnPlayer(new Vector3(0, 20f, 0), .055f, playerSkin);
        //TODO: wave spawning system (probably handled by entity manager)
        for (int i = 0; i < 5; i++) {
            entityManager.spawnZombie(new Vector3(5, 0, 5));
        }

        initInput();
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.getGameViewport().apply();
        game.getGameCamera().update();

        map.renderSky(game.getSpriteBatch());

        game.getModelBatch().begin(game.getGameCamera());
        game.getModelBatch().render(modelCache, environment);
        entityManager.renderEntities(game.getModelBatch(), environment);
        entityManager.renderBullets(game.getModelBatch(), environment);
        game.getModelBatch().end();

        particleManager.renderDecals(game.getDecalBatch());

        sunlight.begin(Vector3.Zero, game.getGameCamera().direction);
        game.getShadowBatch().begin(sunlight.getCamera());
        game.getShadowBatch().render(shadowCache);
        entityManager.renderEntities(game.getShadowBatch(), environment);
        game.getShadowBatch().end();
        sunlight.end();

        map.clearSkyColor();

        if (MyGdxGame.DEBUG && MyGdxGame.WIREFRAME &&  !paused) {
              debugDrawer.begin(game.getGameCamera());
              dynamicsWorld.debugDrawWorld();
              debugDrawer.end();
        }

        renderUI();

        super.render(delta);
    }

    @Override
    public void update(float delta) {
        if (!paused) {
            entityManager.update(delta);

            if (entityManager.getPlayer().getHitpoints() <= 0) {
                gameOverTable.setVisible(true);
                playTable.setVisible(false);
            }

            if (entityManager.getPlayer().getCurrentWeapon().isAutofire() && entityManager.getPlayer().getCurrentWeapon().isAutofire() && (Gdx.input.isKeyPressed(Input.Keys.SPACE) || touched)) {
                entityManager.getPlayer().fire();
            }

            dynamicsWorld.stepSimulation(delta, 10, 1f/75f);
            GdxAI.getTimepiece().update(delta);
        }

        updateCamera();
    }

    private void updateCamera() {
        Vector3 position = entityManager.getPlayer().getPosition();
        game.getGameCamera().position.set(position.x - 42, 35, position.z - 42);

        if (worldSize > targetWorldSize) {
            game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / (worldSize -= .5f), Dimensions.getGameHeight() / (worldSize -= .5f));
        } else if (worldSize < targetWorldSize) {
            game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / (worldSize += .5f), Dimensions.getGameHeight() / (worldSize += .5f));
        }
    }

    private void renderUI() {
        game.getStringBuilder().setLength(0);
        game.getStringBuilder().append("FPS: ").append(Gdx.graphics.getFramesPerSecond());

        debugLabel.setText(game.getStringBuilder());

        game.getUIViewport().apply();
        game.getGameCamera().update();

        if (!paused) {
            entityManager.getPlayer().renderUI(game.getSpriteBatch(), game.getShapeRenderer());
        }

        if (gameOverTable.isVisible()) {
            if (gameOverTableOpacity < 1) {
                gameOverTableOpacity += .01f;
            }

            if (gameOverTableTint < .6f) {
                gameOverTableTint += .01f;
            }

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
            game.getShapeRenderer().setColor(1, 0, 0, gameOverTableTint);
            game.getShapeRenderer().rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            game.getShapeRenderer().end();

            gameOverTable.setColor(1, 0, 0, gameOverTableOpacity);
        }

        stage.act();
        stage.draw();
    }



    @Override
    public void pause() {
        super.pause();

        playTable.setVisible(false);
        pauseTable.setVisible(true);

        paused = true;
    }

    @Override
    public void dispose() {
        super.dispose();

        entityManager.clear();
        particleManager.clear();

        game.getStringBuilder().setLength(0);

        mapBaseCollisionShape.dispose();
        mapObjectsCollisionShape.dispose();

        modelCache.dispose();
        shadowCache.dispose();

        dynamicsWorld.dispose();
        collisionConfig.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        constraintSolver.dispose();

        stage.dispose();

        System.gc();
    }



    //method to limit fps (will soon switch game over to 30fps)
    public void sleep(int fps) {
        if(fps > 0){
            diff = System.currentTimeMillis() - start;
            long targetDelay = 1000 / fps;
            if (diff < targetDelay) {
                try {
                    Thread.sleep(targetDelay - diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            start = System.currentTimeMillis();
        }
    }



    private void initUI() {
        GameUtils.debug(this,"initUI");

        pauseButton = new ImageButton(AssetLoader.uiSkin.getDrawable("pause"));
        pauseButton.getImageCell().size(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        pauseButton.setSize(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        pauseButton.addListener(pauseButtonListener);

        playButton = new ImageButton(AssetLoader.uiSkin.getDrawable("play"));
        playButton.getImageCell().size(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        playButton.setSize(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        playButton.addListener(playButtonListener);

        nextWeaponButton = new ImageButton(AssetLoader.uiSkin.getDrawable("right"));
        nextWeaponButton.getImageCell().size(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        nextWeaponButton.setSize(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        nextWeaponButton.addListener(nextWeaponButtonListener);

        previousWeaponButton = new ImageButton(AssetLoader.uiSkin.getDrawable("left"));
        previousWeaponButton.getImageCell().size(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        previousWeaponButton.setSize(120 * Dimensions.getGameScreenWidthRatio(), 120 * Dimensions.getGameScreenWidthRatio());
        previousWeaponButton.addListener(previousWeaponButtonListener);

        touchpad = new Touchpad(0, AssetLoader.uiSkin);

        debugLabel = new Label("debugLabel", AssetLoader.uiSkin);
        debugLabel.setY(Dimensions.getScreenHeight() - debugLabel.getHeight());

        gameOverLabel = new Label("GAME OVER", AssetLoader.uiSkin, "menu");
        continueLabel = new Label("touch to continue...", AssetLoader.uiSkin, "small");

        playTable = new Table();
        playTable.setFillParent(true);
        playTable.add(pauseButton).colspan(3).top().right().expandX();
        playTable.row();
        playTable.add(touchpad).bottom().left().pad(Dimensions.getGameScreenHeightRatio() * 50).expandY();
        playTable.add(previousWeaponButton).expandX().bottom().right().padBottom(Dimensions.getGameScreenHeightRatio() * 150).padRight(Dimensions.getGameScreenWidthRatio() * 300);
        playTable.add(nextWeaponButton).bottom().left().padBottom(Dimensions.getGameScreenHeightRatio() * 150).padRight(Dimensions.getGameScreenWidthRatio() * 700);

        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setVisible(false);
        pauseTable.add(playButton);

        gameOverTable = new Table();
        gameOverTable.setFillParent(true);
        gameOverTable.setVisible(false);
        gameOverTable.add(gameOverLabel);
        gameOverTable.row();
        gameOverTable.add(continueLabel);

        if (MyGdxGame.DEBUG) {
            playTable.debug();
            pauseTable.debug();
        }

        stage = new Stage(game.getUIViewport());
        stage.addActor(playTable);
        stage.addActor(pauseTable);
        stage.addActor(gameOverTable);
        stage.addActor(debugLabel);
    }

    private void initLighting() {
        GameUtils.debug(this, "initLighting");

        environment = new Environment();
        sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 70f, 70f, 1, 75);
        sunlight.set(map.getTimeOfDay().sunlightColor, map.getTimeOfDay().sunlightDirection);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, map.getTimeOfDay().ambientColor));
        environment.add(sunlight);
        environment.shadowMap = sunlight;

        Gdx.gl20.glCullFace(GL20.GL_BACK);
    }

    private void initPhysics() {
        GameUtils.debug(this, "initPhysics");

        BoundingBox boundingBox = new BoundingBox();
        map.base.calculateBoundingBox(boundingBox);

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

        mapBaseCollisionShape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(.5f));
        mapBaseCollisionObject = new btCollisionObject();
        mapBaseCollisionObject.setCollisionShape(mapBaseCollisionShape);
        mapBaseCollisionObject.setRestitution(.5f);
        mapBaseCollisionObject.setWorldTransform(mapBaseCollisionObject.getWorldTransform().translate(0, -.58f, 0));

        mapObjectsCollisionShape = Bullet.obtainStaticNodeShape(map.objects.nodes);
        mapObjectsCollisionObject = new btCollisionObject();
        mapObjectsCollisionObject.setCollisionShape(mapObjectsCollisionShape);
        mapObjectsCollisionObject.setWorldTransform(map.objects.transform);
        mapObjectsCollisionObject.setContactCallbackFlag(CollisionFlags.OBJECT_FLAG);
        mapObjectsCollisionObject.setContactCallbackFilter(0);

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
        dynamicsWorld.setDebugDrawer(debugDrawer);

        dynamicsWorld.addCollisionObject(mapBaseCollisionObject, (short) CollisionFlags.GROUND_FLAG, (short)(CollisionFlags.OBJECT_FLAG | CollisionFlags.ENTITY_FLAG | CollisionFlags.BULLET_CASING_FLAG));
        dynamicsWorld.addCollisionObject(mapObjectsCollisionObject, (short) CollisionFlags.OBJECT_FLAG, (short) (CollisionFlags.BULLET_CASING_FLAG | CollisionFlags.ENTITY_FLAG | CollisionFlags.BULLET_FLAG));
    }

    private void initWorld() {
        modelCache = new ModelCache();
        shadowCache = new ModelCache();

        modelCache.begin();
        modelCache.add(new ModelInstance(map.base));
        modelCache.add(new ModelInstance(map.objects));
        if (map.doodads != null) {
            modelCache.add(new ModelInstance(map.doodads));
        }
        modelCache.end();

        shadowCache.begin();
        if (map.objects != null) {
            shadowCache.add(new ModelInstance(map.objects));
        }
        if (map.doodads != null) {
            shadowCache.add(new ModelInstance(map.doodads));
        }
        shadowCache.end();

    }

    private void initInput() {
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(entityManager.getPlayer());
        entityManager.getPlayer().addController(touchpad);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACKSPACE) {
            GameUtils.takeScreenshot();
        } else if (keycode == Input.Keys.SHIFT_RIGHT){
            entityManager.spawnZombie(new Vector3(0, 0, 0));
        } else if (keycode == Input.Keys.NUM_1) {
            map.setTimeOfDay(new Afternoon());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 100);
            sunlight.set(map.getTimeOfDay().sunlightColor, map.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, map.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
        } else if (keycode == Input.Keys.NUM_2) {
            map.setTimeOfDay(new Evening());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 100);
            sunlight.set(map.getTimeOfDay().sunlightColor, map.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, map.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
        } else if (keycode == Input.Keys.NUM_3) {
            map.setTimeOfDay(new Night());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 100);
            sunlight.set(map.getTimeOfDay().sunlightColor, map.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, map.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touched = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touched = false;
        return true;
    }



    private ClickListener playButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            pauseTable.setVisible(false);
            playTable.setVisible(true);

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

    private ClickListener nextWeaponButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener previousWeaponButtonListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

}