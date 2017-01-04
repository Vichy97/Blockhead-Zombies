package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starcat.boxhead.environment.Afternoon;
import com.starcat.boxhead.environment.Evening;
import com.starcat.boxhead.environment.Night;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Cloud;
import com.starcat.boxhead.objects.Map;
import com.starcat.boxhead.objects.Star;
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
    private boolean leftPressed, rightPressed, upPressed, downPressed = false;
    private boolean touched = false;

    private InputMultiplexer inputMultiplexer;
    private Stage stage;
    private Table pauseTable, playTable;

    private Label debugLabel;

    private ImageButton pauseButton, playButton, nextWeaponButton, previousWeaponButton;
    private TextButton menuButton;
    private Touchpad touchpad;

    private ModelCache modelCache, shadowCache;

    private Environment environment;
    private DirectionalShadowLight sunlight;

    private btCollisionShape mapBaseCollisionShape;
    private btCollisionObject mapObjectsCollisionObject;

    private EntityManager entityManager;
    private ParticleManager particleManager;

    private Map currentMap;

    private Cloud[] cloudArray;
    private Star[] stars;

    private btDiscreteDynamicsWorld dynamicsWorld;
    private DebugDrawer debugDrawer;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver constraintSolver;
    private btCollisionDispatcher dispatcher;
    private btDefaultCollisionConfiguration collisionConfig;
    private btCollisionShape mapObjectsCollisionShape;
    private btCollisionObject mapBaseCollisionObject;

    private long diff, start = System.currentTimeMillis();



    public GameScreen(MyGdxGame game) {
        super(game);

        //FIXME: 90 should maybe not be 90 lol
        game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / 90,Dimensions.getGameHeight() / 90);
        game.getGameCamera().position.set(0, 10, 0);

        initUI();
        initWorld();
        initLighting();
        initPhysics();

        particleManager = ParticleManager.instance();
        entityManager = EntityManager.instance();
        entityManager.setDynamicsWorld(dynamicsWorld);

        //TODO: wave spawning system (probably handled by entity manager)
        entityManager.spawnPlayer(new Vector3(0, .8f, 0), .055f);
        for (int i = 0; i < 5; i++) {
            entityManager.spawnZombie(new Vector3(5,.8f, 5));
        }

    }



    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.getGameViewport().apply();
        game.getGameCamera().update();

        if (currentMap.getTimeOfDay() instanceof Night) {
            renderStars();
        } else {
            renderClouds(delta);
        }

        if (!paused) {
            dynamicsWorld.stepSimulation(delta, 10, 1f/75f);
            GdxAI.getTimepiece().update(delta);
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

        if (!entityManager.getPlayer().isMoving()) {
            entityManager.getPlayer().setDirection(direction);
        } else if (entityManager.getPlayer().getDirection() != direction) {
            entityManager.getPlayer().setDirection(direction);
        }

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

        if (!paused) {
            entityManager.update(Gdx.graphics.getDeltaTime());

            if (entityManager.getPlayer().getHitpoints() <= 0) {
                game.setScreen(new GameOverScreen(game));
                dispose();
            }

            if (entityManager.getPlayer().getCurrentWeapon().isAutofire() && entityManager.getPlayer().getCurrentWeapon().isAutofire() && (Gdx.input.isKeyPressed(Input.Keys.SPACE) || touched)) {
                entityManager.getPlayer().fire();
            }
        }

        Vector3 position = entityManager.getPlayer().getPosition();
        game.getGameCamera().position.set(position.x - 10, 10, position.z - 10);

        Gdx.gl.glClearColor(currentMap.getTimeOfDay().skyColor.r, currentMap.getTimeOfDay().skyColor.g, currentMap.getTimeOfDay().skyColor.b, 1);

        if (MyGdxGame.DEBUG && MyGdxGame.WIREFRAME &&  !paused) {
              debugDrawer.begin(game.getGameCamera());
              dynamicsWorld.debugDrawWorld();
              debugDrawer.end();
        }

        game.getStringBuilder().setLength(0);
        game.getStringBuilder().append("FPS: ").append(Gdx.graphics.getFramesPerSecond());

        debugLabel.setText(game.getStringBuilder());

        game.getUIViewport().apply();
        game.getGameCamera().update();

        if (!paused) {
            entityManager.getPlayer().renderUI(game.getSpriteBatch(), game.getShapeRenderer());
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

        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getUICamera().viewportWidth / 2, game.getUICamera().viewportHeight / 2, 0);

        playTable = new Table();
        playTable.setFillParent(true);
        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setVisible(false);
        if (MyGdxGame.DEBUG) {
            playTable.debug();
            pauseTable.debug();
        }

        playTable.add(pauseButton).colspan(3).top().right().expandX();
        playTable.row();
        playTable.add(touchpad).bottom().left().pad(Dimensions.getGameScreenHeightRatio() * 50).expandY();
        playTable.add(previousWeaponButton).expandX().bottom().right().padBottom(Dimensions.getGameScreenHeightRatio() * 150).padRight(Dimensions.getGameScreenWidthRatio() * 300);
        playTable.add(nextWeaponButton).bottom().left().padBottom(Dimensions.getGameScreenHeightRatio() * 150).padRight(Dimensions.getGameScreenWidthRatio() * 700);

        pauseTable.add(playButton);

        stage = new Stage(game.getUIViewport());
        stage.addActor(playTable);
        stage.addActor(pauseTable);
        stage.addActor(debugLabel);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initLighting() {
        GameUtils.debug(this, "initLighting");

        environment = new Environment();
        sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 70f, 70f, 1, 75);
        sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
        environment.add(sunlight);
        environment.shadowMap = sunlight;

        Gdx.gl20.glCullFace(GL20.GL_BACK);
    }

    private void initPhysics() {
        GameUtils.debug(this, "initPhysics");

        BoundingBox boundingBox = new BoundingBox();
        currentMap.base.calculateBoundingBox(boundingBox);

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

        mapBaseCollisionShape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(.5f));
        mapBaseCollisionObject = new btCollisionObject();
        mapBaseCollisionObject.setCollisionShape(mapBaseCollisionShape);
        mapBaseCollisionObject.setRestitution(.5f);

        mapObjectsCollisionShape = Bullet.obtainStaticNodeShape(currentMap.objects.nodes);
        mapObjectsCollisionObject = new btCollisionObject();
        mapObjectsCollisionObject.setCollisionShape(mapObjectsCollisionShape);
        mapObjectsCollisionObject.setWorldTransform(currentMap.objects.transform);
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
        currentMap = AssetLoader.map;

        modelCache = new ModelCache();
        shadowCache = new ModelCache();

        modelCache.begin();
        modelCache.add(new ModelInstance(currentMap.base));
        modelCache.add(new ModelInstance(currentMap.objects));
        modelCache.add(new ModelInstance(currentMap.doodads));
        modelCache.end();

        shadowCache.begin();
        shadowCache.add(new ModelInstance(currentMap.objects));
        shadowCache.add(new ModelInstance(currentMap.doodads));
        shadowCache.end();

        spawnClouds();
        spawnStars();
    }

    private void spawnClouds() {
        cloudArray = new Cloud[6];
        for (int i = 0; i < cloudArray.length; i++) {
            if (i % 3 == 0) {
                cloudArray[i] = new Cloud(1920 - i * 350, AssetLoader.textures.findRegion("cloud_1"));
            } else if (i % 2 == 0) {
                cloudArray[i] = new Cloud(1920 - i * 350, AssetLoader.textures.findRegion("cloud_2"));
            } else {
                cloudArray[i] = new Cloud(1920 - i * 350, AssetLoader.textures.findRegion("cloud_3"));
            }
        }
    }

    private void spawnStars() {
        stars = new Star[18];
        stars[0] = new Star(105, 1000, AssetLoader.textures.findRegion("star_small"));
        stars[1] = new Star(540, 1040, AssetLoader.textures.findRegion("star_small"));
        stars[2] = new Star(1045, 980, AssetLoader.textures.findRegion("star_small"));
        stars[3] = new Star(380, 800, AssetLoader.textures.findRegion("star_small"));
        stars[4] = new Star(1000, 650, AssetLoader.textures.findRegion("star_small"));
        stars[5] = new Star(1450, 450, AssetLoader.textures.findRegion("star_small"));
        stars[6] = new Star(1820, 350, AssetLoader.textures.findRegion("star_small"));
        stars[7] = new Star(580, 200, AssetLoader.textures.findRegion("star_small"));
        stars[8] = new Star(1485, 100, AssetLoader.textures.findRegion("star_small"));

        stars[9] = new Star(250, 900, AssetLoader.textures.findRegion("star_big"));
        stars[10] = new Star(800, 840, AssetLoader.textures.findRegion("star_big"));
        stars[11] = new Star(1300, 800, AssetLoader.textures.findRegion("star_big"));
        stars[12] = new Star(1580, 1000, AssetLoader.textures.findRegion("star_big"));
        stars[13] = new Star(600, 460, AssetLoader.textures.findRegion("star_big"));
        stars[14] = new Star(1200, 260, AssetLoader.textures.findRegion("star_big"));
        stars[15] = new Star(240, 280, AssetLoader.textures.findRegion("star_big"));
        stars[16] = new Star(170, 540, AssetLoader.textures.findRegion("star_small"));
        stars[17] = new Star(1730, 840, AssetLoader.textures.findRegion("star_small"));
    }

    private void renderStars() {
        for (Star star : stars) {
            star.render(game.getSpriteBatch());
        }
    }

    private void renderClouds(float delta) {
        for (Cloud aCloudArray : cloudArray) {
            if (!paused) {
                aCloudArray.update(delta);
            }
            aCloudArray.render(game.getSpriteBatch());
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
            entityManager.getPlayer().fire();
        } else if (keycode == Input.Keys.BACKSPACE) {
            GameUtils.takeScreenshot();
        } else if (keycode == Input.Keys.SHIFT_RIGHT){
            entityManager.spawnZombie(new Vector3(0, .8f, 0));
        } else if (keycode == Input.Keys.NUM_1) {
            currentMap.setTimeOfDay(new Afternoon());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 100);
            sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
        } else if (keycode == Input.Keys.NUM_2) {
            currentMap.setTimeOfDay(new Evening());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 100);
            sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
            environment.add(sunlight);
            environment.shadowMap = sunlight;
        } else if (keycode == Input.Keys.NUM_3) {
            currentMap.setTimeOfDay(new Night());
            environment = new Environment();
            sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 50f, 50f, 1, 100);
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        entityManager.getPlayer().fire();
        touched = true;
        return true;
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