package com.vincent.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.vincent.World.Cloud;
import com.vincent.World.ObjectManager;
import com.vincent.entity.EntityManager;
import com.vincent.game.MyGdxGame;
import com.vincent.entity.Player;
import com.vincent.util.AssetLoader;
import com.vincent.util.map.CustomTileMapRenderer;
import com.vincent.util.GameUtils;
import com.vincent.util.map.MapBodyManager;

/**
 * Created by Vincent on 6/19/2015.
 *
 * Main Screen for the game. most code should go here
 * this class handles updating and rendering all of the actual game
 */
public class GameScreen implements Screen, InputProcessor {

    private boolean paused = false;
    private boolean music = true;
    private boolean sound = true;

    public boolean leftPressed, rightPressed, upPressed, downPressed;

    private MyGdxGame game;
    private I18NBundle bundle;
    private InputMultiplexer inputMultiplexer;

    //view related fields
    private OrthographicCamera UICamera, gameCamera;
    private Viewport UIViewport, gameViewport;
    //pixel dimensions of the physical screen
    private int screenWidth;
    private int screenHeight;
    //height of the game used for the viewports
    private int gameWidth;
    private int gameHeight;

    private float aspectRatio;

    //separate stage and table fot the pause menu because it has different
    //visibility and we dont want input from both at once
    private Stage mainStage;
    private Stage touchpadStage;

    private ImageButton pauseButton, playButton, musicOnButton, musicOffButton, soundOnButton, soundOffButton;
    private TextButton menuButton;
    private TextButton.TextButtonStyle textButtonStyle;
    private NinePatchDrawable buttonDrawable;
    private BitmapFont textButtonFont;

    //touchpads are onscreen joysticks
    private Touchpad touchpad1, touchpad2;
    private Touchpad.TouchpadStyle touchpadStyle;
    private TextureRegionDrawable touchpadBase;
    private TextureRegionDrawable touchpadNob;

    //map fields
    private CustomTileMapRenderer tiledMapRenderer;
    private TiledMap currentMap;
    private int mapWidth, mapHeight;
    private MapLayer tiles;

    //TODO: make this much less messy and remove texture variable declerations
    //TODO: (refer to the static fields in AssetLoader directly) once i get more artwork
    //player fields
    private float playerMaxSpeed = 300;
    private Player player;
    private Texture boxhead_1;
    private Texture boxhead_2;
    private Texture boxhead_3;
    private Texture boxhead_4;
    private Texture boxhead_5;
    private Texture boxhead_6;
    private Texture boxhead_7;
    private Texture boxhead_8;
    private Texture cloudTexture1;
    private Texture cloudTexture2;
    private Texture cloudTexture3;

    private BitmapFont font;
    private SpriteBatch spriteBatch;

    //Box2d fields
    private World world;
    private Box2DDebugRenderer debugRenderer;
    //how often the world physics should update
    public final float step = 1f/60f;
    private double accumulator = 0;
    //box2d is measured in meters so we use this and the debug matrix to convert
    private Matrix4 debugMatrix;

    private EntityManager entityManager;
    private ObjectManager objectManager;
    private MapBodyManager mapBodyManager;

    private Cloud[] cloudArray;

    public GameScreen(MyGdxGame game, OrthographicCamera UICamera, Viewport UIViewport, OrthographicCamera gameCamera, Viewport gameViewport, I18NBundle bundle, TiledMap map) {
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

        UIViewport.apply();
        UICamera.position.set(UICamera.viewportWidth / 2, UICamera.viewportHeight / 2, 0);

        gameViewport.apply();
        gameCamera.update();

        touchpadStage = new Stage(UIViewport);
        mainStage = new Stage(UIViewport);

        inputMultiplexer = new InputMultiplexer();

        initUI();

        //setup input
        inputMultiplexer.addProcessor(touchpadStage);
        inputMultiplexer.addProcessor(mainStage);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);

        font = AssetLoader.createFont("DroidSans.ttf", 25);
        font.setColor(Color.BLACK);
        spriteBatch = new SpriteBatch();

        boxhead_1 = AssetLoader.boxhead_1;
        boxhead_2 = AssetLoader.boxhead_2;
        boxhead_3 = AssetLoader.boxhead_3;
        boxhead_4 = AssetLoader.boxhead_4;
        boxhead_5 = AssetLoader.boxhead_5;
        boxhead_6 = AssetLoader.boxhead_6;
        boxhead_7 = AssetLoader.boxhead_7;
        boxhead_8 = AssetLoader.boxhead_8;
        cloudTexture1 = AssetLoader.cloudTexture1;
        cloudTexture2 = AssetLoader.cloudTexture2;
        cloudTexture3 = AssetLoader.cloudTexture3;

        currentMap = map;
        tiles = currentMap.getLayers().get("tiles");

        spawnClouds();

        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();

        mapBodyManager = new MapBodyManager(world, GameUtils.PIXELS_PER_METER, null, 0);
        mapBodyManager.createPhysics(currentMap);
    }



    @Override
    public void show() {
        debug("show");

        Texture[] textures = {boxhead_1, boxhead_2, boxhead_3, boxhead_4, boxhead_5, boxhead_6, boxhead_7, boxhead_8};

        tiledMapRenderer = new CustomTileMapRenderer(currentMap);

        objectManager = new ObjectManager(tiledMapRenderer, currentMap.getLayers().get("objects").getObjects(), currentMap.getLayers().get("static objects").getObjects());
        entityManager = new EntityManager(world);
        player = entityManager.spawnPlayer(textures, playerMaxSpeed, new Vector3(1984, 32, 0), touchpad1);

        Gdx.graphics.setContinuousRendering(true);
    }

    //game loop update and render all stuff here
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(40f / 255f, 175f / 255f, 230f / 255f, 1f);

        tiledMapRenderer.setView(gameCamera);
        gameViewport.apply();

        renderClouds(delta);

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer((TiledMapTileLayer) tiles);
        tiledMapRenderer.getBatch().end();
        objectManager.renderStaticObjects();
        if (!paused) {
            objectManager.sortObjects();
            objectManager.renderObjects();
            entityManager.updateEntities();
        } else {
            objectManager.renderObjects();
        }

        gameCamera.position.set((int)player.getEntityX(), (int)player.getEntityY(), 0);

        debugMatrix = gameCamera.combined.scale(GameUtils.PIXELS_PER_METER, GameUtils.PIXELS_PER_METER, 0);
        if (MyGdxGame.DEBUG) {
            debugRenderer.render(world, debugMatrix);
        }

        if (!paused) {
            stepWorld(delta);
        }

        gameCamera.update();

        if (MyGdxGame.DEBUG) {
            spriteBatch.begin();
            font.draw(spriteBatch, "PlayerIso: " + player.position, 50, screenHeight - 25);
            font.draw(spriteBatch, "TouchpadPercent: X = " + touchpad1.getKnobPercentX() + " Y = " + touchpad1.getKnobPercentY(), 50, screenHeight - 50);
            font.draw(spriteBatch, "TouchpadAngle: " + (180 + Math.atan2(touchpad1.getKnobPercentY(), touchpad1.getKnobPercentX()) * 180.0d / Math.PI), 50, screenHeight - 75);
            font.draw(spriteBatch, "PlayerDirection: " + player.getDirection(), 50, screenHeight - 100);
            font.draw(spriteBatch, "FPS: " + (Gdx.graphics.getFramesPerSecond()), 50, screenHeight - 125);
            spriteBatch.end();
        }


        UIViewport.apply();
        UICamera.update();
        mainStage.act();
        mainStage.draw();
        if (!paused) {
            touchpadStage.act();
            touchpadStage.draw();
        }

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

        //hide the pause button and show the pause menu
        pauseButton.setVisible(false);
        playButton.setVisible(true);
        menuButton.setVisible(true);

        if (music) {
            musicOnButton.setVisible(true);
        } else {
            musicOffButton.setVisible(true);
        }
        if (sound) {
            soundOnButton.setVisible(true);
        } else {
            soundOffButton.setVisible(true);
        }
        //get input from pause menu and ignore all other input
        inputMultiplexer.removeProcessor(touchpadStage);
        //stop rendering. now only touch events will be handled
        Gdx.graphics.setContinuousRendering(false);
        paused = true;
    }

    //nothing happens here because if the game is minimized
    //we want to stay paused when we open it back up
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

        mapBodyManager.destroyPhysics();
        world.dispose();
        font.dispose();
        spriteBatch.dispose();
        mainStage.dispose();
        touchpadStage.dispose();
        tiledMapRenderer.dispose();
        currentMap.dispose();
        entityManager.dispose();
        objectManager.dispose();
        System.gc();
    }



    //method for setting up the user interface. This just keeps the show() method cleaner
    private void initUI() {

        //set button styles and images
        buttonDrawable = new NinePatchDrawable(AssetLoader.button);
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonFont = AssetLoader.createFont("DroidSans.ttf", 50);
        textButtonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textButtonStyle.font = textButtonFont;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.down = buttonDrawable;
        textButtonStyle.up = buttonDrawable;

        //set the buttons click listeners
        pauseButton = new ImageButton(AssetLoader.pause);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause();
            }
        });
        pauseButton.setX(gameHeight/aspectRatio - pauseButton.getWidth());
        pauseButton.setY(gameHeight - pauseButton.getHeight());

        playButton = new ImageButton(AssetLoader.play);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //resume
                playButton.setVisible(false);
                pauseButton.setVisible(true);
                musicOnButton.setVisible(false);
                musicOffButton.setVisible(false);
                soundOnButton.setVisible(false);
                soundOffButton.setVisible(false);
                menuButton.setVisible(false);

                inputMultiplexer.addProcessor(touchpadStage);
                //begin rendering again
                Gdx.graphics.setContinuousRendering(true);
                paused = false;
            }
        });
        playButton.setX(gameWidth / 2 - playButton.getWidth() / 2);
        playButton.setY(gameHeight / 2 - playButton.getHeight() / 2 + playButton.getHeight() * 1.5f);
        playButton.setVisible(false);

        musicOnButton = new ImageButton(AssetLoader.musicOn);
        musicOnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music = false;
                musicOnButton.setVisible(false);
                musicOffButton.setVisible(true);
            }
        });
        musicOnButton.setX(gameWidth / 2 - musicOnButton.getWidth());
        musicOnButton.setY(gameHeight / 2);
        musicOnButton.setVisible(false);

        musicOffButton = new ImageButton(AssetLoader.musicOff);
        musicOffButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music = true;
                musicOffButton.setVisible(false);
                musicOnButton.setVisible(true);
            }
        });
        musicOffButton.setX(gameWidth / 2 - musicOffButton.getWidth());
        musicOffButton.setY(gameHeight / 2);
        musicOffButton.setVisible(false);

        soundOnButton = new ImageButton(AssetLoader.soundOn);
        soundOnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sound = false;
                soundOnButton.setVisible(false);
                soundOffButton.setVisible(true);
            }
        });
        soundOnButton.setX(gameWidth / 2);
        soundOnButton.setY(gameHeight / 2);
        soundOnButton.setVisible(false);

        soundOffButton = new ImageButton(AssetLoader.soundOff);
        soundOffButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sound = true;
                soundOffButton.setVisible(false);
                soundOnButton.setVisible(true);
            }
        });
        soundOffButton.setX(gameWidth / 2);
        soundOffButton.setY(gameHeight / 2);
        soundOffButton.setVisible(false);

        menuButton = new TextButton(bundle.get("menu"), textButtonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen("menu");
            }
        });
        menuButton.setWidth(256);
        menuButton.setHeight(128);
        menuButton.setX(gameWidth / 2 - menuButton.getWidth() / 2);
        menuButton.setY(gameHeight / 2 - menuButton.getHeight());
        menuButton.setVisible(false);

        //set the touchpad style and position
        touchpadBase = new TextureRegionDrawable(AssetLoader.touchpadBase);
        touchpadNob = new TextureRegionDrawable(AssetLoader.touchpadNob);
        touchpadStyle = new Touchpad.TouchpadStyle(touchpadBase, touchpadNob);
        touchpad1 = new Touchpad(0, touchpadStyle);
        touchpad2 = new Touchpad(0, touchpadStyle);
        touchpad1.setX(gameHeight / 20);
        touchpad1.setY(gameHeight / 20);
        touchpad2.setX(gameWidth - (gameHeight / 20) - touchpad1.getWidth());
        touchpad2.setY(gameHeight / 20);

        //add touchpads to stage
        touchpadStage.addActor(touchpad1);
        touchpadStage.addActor(touchpad2);

        //add ui elements to the main stage
        mainStage.addActor(pauseButton);
        mainStage.addActor(playButton);
        mainStage.addActor(musicOnButton);
        mainStage.addActor(musicOffButton);
        mainStage.addActor(soundOnButton);
        mainStage.addActor(soundOffButton);
        mainStage.addActor(menuButton);
    }

    private void stepWorld(float delta) {
        double frameTime = Math.min(delta, 0.25);

        accumulator += frameTime;

        while (accumulator >= step) {
            world.step(step, 6, 2);
            accumulator -= step;
        }
    }

    //method for writing to the log
    private void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("GameScreen", message);
        }
    }

    private void spawnClouds() {
        cloudArray = new Cloud[6];
        for (int i = 0; i < cloudArray.length; i++) {
            if (i % 3 == 0) {
                cloudArray[i] = new Cloud(1920 - i * 350, cloudTexture1);
            } else if (i % 2 == 0) {
                cloudArray[i] = new Cloud(1920 - i * 350, cloudTexture2);
            } else {
                cloudArray[i] = new Cloud(1920 - i * 350, cloudTexture3);
            }
        }
    }

    private void renderClouds(float delta) {
        for (int i = 0; i < cloudArray.length; i++) {
            if (!paused) {
                cloudArray[i].update(delta);
            }
            cloudArray[i].render(spriteBatch);
        }
    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            upPressed = true;
        } else if (keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        } else if (keycode == Input.Keys.DOWN) {
            leftPressed = true;
        } else if (keycode == Input.Keys.LEFT) {
            downPressed = true;
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
}
