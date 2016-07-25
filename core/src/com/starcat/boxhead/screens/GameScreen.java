package com.starcat.boxhead.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Cloud;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 6/19/2015.
 */
public class GameScreen implements Screen, InputProcessor {

    private boolean paused = false;
    private boolean music = true;
    private boolean sound = true;

    public boolean leftPressed, rightPressed, upPressed, downPressed = false;

    private MyGdxGame game;
    private I18NBundle bundle;
    private InputMultiplexer inputMultiplexer;

    private OrthographicCamera UICamera, gameCamera;
    private Viewport UIViewport, gameViewport;
    //pixel dimensions of the physical screen
    private int screenWidth;
    private int screenHeight;
    //height of the game used for the viewports
    private int gameWidth;
    private int gameHeight;

    private float aspectRatio;

    private Stage stage;
    private Table pauseTable, playTable;

    private ImageButton pauseButton, playButton, musicButton, soundButton;
    private TextButton menuButton;
    private Touchpad touchpad;

    private SpriteBatch spriteBatch;
    private ModelBatch modelBatch, shadowBatch;

    private Environment environment;
    private DirectionalShadowLight shadowLight;

    private Cloud[] cloudArray;



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
        aspectRatio = MyGdxGame.ASPECT_RATIO;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        initUI();

        gameViewport.apply();
        
        gameCamera.rotate(-30, 1, 0, 0);
        gameCamera.rotate(225, 0, 1, 0);
        gameCamera.position.set(0, 10, 0);
        gameCamera.near = 1f;
        gameCamera.far = 100;
        gameCamera.update();

        spriteBatch = new SpriteBatch();
        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        environment = new Environment();
        shadowLight = new DirectionalShadowLight(1024, 1024, 30f, 30f, 1f, 100f);
        shadowLight.set(0.5f, 0.5f, 0.5f, 1f, -.9f, .5f);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .2f, .2f, .2f, 1f));
        environment.add(shadowLight);
        environment.shadowMap = shadowLight;

        spawnClouds();
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

        modelBatch.begin(gameCamera);
        modelBatch.render(AssetLoader.mapInstance, environment);
        modelBatch.end();

        shadowLight.begin(Vector3.Zero, gameCamera.direction);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(AssetLoader.mapInstance);
        shadowBatch.end();
        shadowLight.end();

        Gdx.gl.glClearColor(40f / 255f, 175f / 255f, 230f / 255f, 1f);

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

        modelBatch.dispose();
        shadowBatch.dispose();
        spriteBatch.dispose();
        stage.dispose();
        System.gc();
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
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
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