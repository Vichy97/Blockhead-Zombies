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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.vincent.World.ObjectManager;
import com.vincent.entity.EntityManager;
import com.vincent.game.MyGdxGame;
import com.vincent.entity.Player;
import com.vincent.util.AssetLoader;
import com.vincent.util.map.CustomTileMapRenderer;
import com.vincent.util.GameUtils;
/**
 * Created by Vincent on 6/19/2015.
 *
 * Main Screen for the game. most code should go here
 * this class handles updating and rendering all of the actual game
 */
public class GameScreen implements Screen, InputProcessor {

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

    //separate stage and table fot the pause menu because it has different
    //visibility and we dont want input from both at once
    private Stage mainStage;
    private Stage touchpadStage;
    private Stage pauseStage;

    private Table mainTable;
    private Table pauseTable;

    private ImageButton pauseButton, playButton;
    private ImageButton.ImageButtonStyle imageButtonStylePause, imageButtonStylePlay;
    private Drawable pauseDrawable,playDrawable;

    //touchpads are onscreen joysticks. your game might not need these
    private Touchpad touchpad1, touchpad2;
    private Touchpad.TouchpadStyle touchpadStyle;
    private TextureRegionDrawable touchpadBase;
    private TextureRegionDrawable touchpadNob;

    //map fields
    private CustomTileMapRenderer tiledMapRenderer;
    private TiledMap map1;
    private int mapWidth, mapHeight;

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

    private BitmapFont font;
    private SpriteBatch spriteBatch;

    //Box2d fields
    private World world;
    private Box2DDebugRenderer debugRenderer;
    //how often the world physics should update
    public final float step = 1f/60f;
    private double accumulator = 0;
    private double currentTime = 0;
    //box2d is measured in meters so we use this and the debug matrix to convert
    private Matrix4 debugMatrix;
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;

    EntityManager entityManager;
    ObjectManager objectManager;

    public GameScreen(MyGdxGame game, OrthographicCamera UICamera, Viewport UIViewport, OrthographicCamera gameCamera, Viewport gameViewport, I18NBundle bundle) {
        debug("constructor");
        this.game = game;
        this.UICamera = UICamera;
        this.UIViewport = UIViewport;
        this.gameCamera = gameCamera;
        this.gameViewport = gameViewport;
        this.bundle = bundle;

        gameWidth = MyGdxGame.GAME_WIDTH;
        gameHeight = MyGdxGame.GAME_HEIGHT;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        UIViewport.apply();
        UICamera.position.set(UICamera.viewportWidth / 2, UICamera.viewportHeight / 2, 0);

        gameViewport.apply();
        gameCamera.update();

        touchpadStage = new Stage(UIViewport);
        mainTable = new Table();

        mainStage = new Stage();

        pauseStage = new Stage(UIViewport);
        pauseTable = new Table();
        pauseTable.setVisible(false);

        inputMultiplexer = new InputMultiplexer();
    }



    @Override
    public void show() {
        debug("show");

        initUI();

        //setup input
        inputMultiplexer.addProcessor(touchpadStage);
        inputMultiplexer.addProcessor(pauseStage);
        inputMultiplexer.addProcessor(mainStage);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);

        font = AssetLoader.createFont("DroidSans.ttf", 25);
        font.setColor(Color.BLACK);
        spriteBatch = new SpriteBatch();

        map1 = AssetLoader.map1;
        tiledMapRenderer = new CustomTileMapRenderer(map1);

        boxhead_1 = AssetLoader.boxhead_1;
        boxhead_2 = AssetLoader.boxhead_2;
        boxhead_3 = AssetLoader.boxhead_3;
        boxhead_4 = AssetLoader.boxhead_4;
        boxhead_5 = AssetLoader.boxhead_5;
        boxhead_6 = AssetLoader.boxhead_6;
        boxhead_7 = AssetLoader.boxhead_7;
        boxhead_8 = AssetLoader.boxhead_8;

        Texture[] textures = {boxhead_1, boxhead_2, boxhead_3, boxhead_4, boxhead_5, boxhead_6, boxhead_7, boxhead_8};
        objectManager = new ObjectManager(tiledMapRenderer, map1.getLayers().get("objects").getObjects());

        world = AssetLoader.world;
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        entityManager = new EntityManager(world, fixtureDef, bodyDef);
        entityManager.spawnPlayer(textures, playerMaxSpeed, new Vector3(1984, 32, 0), touchpad1);

        debugRenderer = new Box2DDebugRenderer();

        Gdx.graphics.setContinuousRendering(true);
    }

    //game loop update and render all stuff here
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        tiledMapRenderer.setView(gameCamera);
        gameViewport.apply();

        tiledMapRenderer.render(new int[]{0});
        entityManager.updateEntities();
        objectManager.renderObjects();

        debugMatrix = gameCamera.combined.cpy().scale(GameUtils.PIXELS_PER_METER, GameUtils.PIXELS_PER_METER, 0);

        debugRenderer.render(world, debugMatrix);
        //TODO: interpolate world stepping to stop object stuttering
        stepWorld();

        gameCamera.update();

        spriteBatch.begin();
      //  font.draw(spriteBatch, "PlayerIso: " + player.position, 50, 700);
        font.draw(spriteBatch, "TouchpadPercent: X = " + touchpad1.getKnobPercentX() + " Y = " + touchpad1.getKnobPercentY(), 50, 675);
        font.draw(spriteBatch, "TouchpadAngle: " + (180 + Math.atan2(touchpad1.getKnobPercentY(), touchpad1.getKnobPercentX()) * 180.0d / Math.PI), 50, 650);
//        font.draw(spriteBatch, "PlayerDirection: " + player.getDirection(), 50, 625);
        font.draw(spriteBatch, "FPS: " + (int)(1/delta), 50, 600);
        spriteBatch.end();

        UIViewport.apply();
        UICamera.update();
        pauseStage.act();
        pauseStage.draw();
        touchpadStage.act();
        touchpadStage.draw();
        mainStage.act();
        mainStage.draw();
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
        pauseTable.setVisible(true);
        pauseButton.setVisible(false);
        //get input from pause menu and ignore all other input
        inputMultiplexer.removeProcessor(touchpadStage);
        //stop rendering. now only touch events will be handled
        Gdx.graphics.setContinuousRendering(false);
        Gdx.graphics.requestRendering();
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
        font.dispose();
        mainStage.dispose();
        pauseStage.dispose();
        touchpadStage.dispose();
        spriteBatch.dispose();
        tiledMapRenderer.dispose();
        map1.dispose();
    }



    //method for setting up the user interface. This just keeps the show method cleaner
    private void initUI() {
        //set button styles and images
        pauseDrawable = new TextureRegionDrawable(AssetLoader.pause);
        playDrawable = new TextureRegionDrawable(AssetLoader.play);

        imageButtonStylePlay = new ImageButton.ImageButtonStyle();
        imageButtonStylePlay.up = playDrawable;
        imageButtonStylePause = new ImageButton.ImageButtonStyle();
        imageButtonStylePause.up = pauseDrawable;

        //set the buttons click listeners
        pauseButton = new ImageButton(imageButtonStylePause);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause();
            }
        });
        playButton = new ImageButton(imageButtonStylePlay);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //resume
                pauseTable.setVisible(false);
                pauseButton.setVisible(true);
                inputMultiplexer.addProcessor(touchpadStage);
                //begin rendering again
                Gdx.graphics.setContinuousRendering(true);
            }
        });

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

        //add ui elements to the pause stage
        pauseTable.setBounds(0, 0, gameWidth, gameHeight);
        pauseTable.center();
        pauseTable.add(playButton).width(screenWidth / 15).height(screenWidth / 15);
        pauseStage.addActor(pauseTable);

        //add ui elements to the main stage
        mainTable.setBounds(0, 0, screenWidth, screenHeight);
        mainTable.top().right();
        mainTable.add(pauseButton).width(screenWidth / 15).height(screenWidth / 15);
        mainStage.addActor(mainTable);
    }

    private void stepWorld() {
        double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);

        accumulator += frameTime;

        currentTime = newTime;

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



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            player.setDirection(1);
        } else if (keycode == Input.Keys.RIGHT) {
            player.setDirection(2);
        } else if (keycode == Input.Keys.DOWN) {
            player.setDirection(3);
        } else if (keycode == Input.Keys.LEFT) {
            player.setDirection(4);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.UP && player.getDirection() == 1) {
            player.setDirection(-1);
        } else if (keycode == Input.Keys.RIGHT && player.getDirection() == 2) {
            player.setDirection(-1);
        } else if (keycode == Input.Keys.DOWN && player.getDirection() == 3) {
            player.setDirection(-1);
        } else if (keycode == Input.Keys.LEFT && player.getDirection() == 4) {
            player.setDirection(-1);
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
