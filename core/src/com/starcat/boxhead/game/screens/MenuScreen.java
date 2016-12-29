package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Cloud;
import com.starcat.boxhead.objects.Map;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 2/10/2015.
 *
 * This screens is the main menu of the game
 */
public class MenuScreen extends BaseScreen {

    private InputMultiplexer inputMultiplexer;

    private Stage stage;
    private Table menuTable;
    private Table optionsTable;
    private Table aboutTable;

    private TextButton playButton;
    private TextButton exitButton;
    private TextButton optionsButton;
    private Label titleLabel;
    private Label musicLabel;
    private Label soundLabel;
    private Label optionsLabel;
    private Label aboutLabel;
    private Label aboutTextLabel;
    private ImageButton musicButton;
    private ImageButton soundButton;
    private ImageButton aboutButton;
    private ImageButton shareButton;
    private ImageButton achievementsButton;

    private ModelCache modelCache, shadowCache;

    private Environment environment;
    private DirectionalShadowLight sunlight;

    private Map currentMap;
    private Cloud[] cloudArray;



    public MenuScreen(final MyGdxGame game) {
        super(game);

        game.getGameCamera().position.set(-40, 35, -40);
        game.getGameViewport().setWorldSize(game.GAME_WIDTH / 30, game.GAME_HEIGHT / 30);

        initUI();
        initWorld();
        initLighting();
    }



     @Override
    public void show() {
         GameUtils.debug(this, "show");

         inputMultiplexer = new InputMultiplexer();
         inputMultiplexer.addProcessor(stage);
         inputMultiplexer.addProcessor(this);

         Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.getGameViewport().apply();
        game.getGameCamera().update();

        renderClouds(delta);
        game.getModelBatch().begin(game.getGameCamera());
        game.getModelBatch().render(modelCache, environment);
        game.getModelBatch().end();

        sunlight.begin(Vector3.Zero, game.getGameCamera().direction);
        game.getShadowBatch().begin(sunlight.getCamera());
        game.getShadowBatch().render(shadowCache);
        game.getShadowBatch().end();
        sunlight.end();

        Gdx.gl.glClearColor(currentMap.getTimeOfDay().skyColor.r, currentMap.getTimeOfDay().skyColor.g, currentMap.getTimeOfDay().skyColor.b, 1);

        game.getUICamera().update();

        stage.act();
        stage.draw();
    }



    public void initUI()  {
        stage = new Stage(game.getUIViewport());

        menuTable = new Table();
        menuTable.setFillParent(true);

        titleLabel = new Label(AssetLoader.i18NBundle.get("menuTitle"), AssetLoader.uiSkin, "title");
        playButton = new TextButton(AssetLoader.i18NBundle.get("play"), AssetLoader.uiSkin, "menu");
        playButton.addListener(playListener);
        optionsButton = new TextButton(AssetLoader.i18NBundle.get("options"), AssetLoader.uiSkin, "menu");
        optionsButton.addListener(optionsListener);
        exitButton = new TextButton(AssetLoader.i18NBundle.get("exit"), AssetLoader.uiSkin, "menu");
        exitButton.addListener(exitListener);
        aboutButton = new ImageButton(AssetLoader.uiSkin.getDrawable("about"));
        aboutButton.addListener(aboutListener);
        shareButton = new ImageButton(AssetLoader.uiSkin.getDrawable("share"));
        shareButton.addListener(shareListener);
        achievementsButton = new ImageButton(AssetLoader.uiSkin.getDrawable("achievement"));
        achievementsButton.addListener(achievementsListener);

        menuTable.top();
        menuTable.add(titleLabel).pad(Gdx.graphics.getHeight() / 10, 0, Gdx.graphics.getHeight() / 15, 0).expandX().colspan(3);;
        menuTable.row();
        menuTable.add(playButton).colspan(3);;
        menuTable.row();
        menuTable.add(optionsButton).colspan(3);;
        menuTable.row();
        menuTable.add(exitButton).colspan(3);
        menuTable.row();
        menuTable.add(aboutButton).top().left().expandY().expandX().padLeft(aboutButton.getHeight() / 3);
        menuTable.add(shareButton).top().right().padRight(shareButton.getHeight() / 4);
        menuTable.add(achievementsButton).top().right().padRight(achievementsButton.getHeight() / 3);



        optionsTable = new Table();
        optionsTable.setVisible(false);

        optionsLabel = new Label(AssetLoader.i18NBundle.get("options"), AssetLoader.uiSkin, "large");
        soundLabel = new Label(AssetLoader.i18NBundle.get("sound"), AssetLoader.uiSkin, "small");
        musicLabel = new Label(AssetLoader.i18NBundle.get("music"), AssetLoader.uiSkin, "small");

        musicButton = new ImageButton(AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_off"));
        musicButton.addListener(musicListener);
        soundButton = new ImageButton(AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_off"));
        soundButton.addListener(soundListener);

        optionsTable.add(optionsLabel).colspan(2).pad(0, optionsLabel.getWidth() / 3, optionsLabel.getHeight() / 2, optionsLabel.getWidth() / 3);
        optionsTable.row();
        optionsTable.add(soundLabel);
        optionsTable.add(musicLabel);
        optionsTable.row();
        optionsTable.add(soundButton);
        optionsTable.add(musicButton);

        optionsTable.setWidth(game.GAME_WIDTH / 2 - 200);
        optionsTable.setHeight(game.GAME_HEIGHT / 2 - 50);
        optionsTable.setPosition(game.GAME_WIDTH / 2 - optionsTable.getWidth() / 2, game.GAME_HEIGHT / 2 - optionsTable.getHeight() / 2);
        optionsTable.background(AssetLoader.uiSkin.getDrawable("button"));



        aboutTable = new Table();
        aboutTable.setWidth(game.GAME_WIDTH / 2 + game.GAME_WIDTH / 12);
        aboutTable.setHeight(game.GAME_HEIGHT / 2 + game.GAME_HEIGHT / 4);
        aboutTable.setPosition(game.GAME_WIDTH / 2 - aboutTable.getWidth() / 2, game.GAME_HEIGHT / 2 - aboutTable.getHeight() / 2);
        aboutTable.setBackground(AssetLoader.uiSkin.getDrawable("button"));
        aboutTable.setVisible(false);

        aboutLabel = new Label(AssetLoader.i18NBundle.get("about"), AssetLoader.uiSkin, "large");
        aboutTextLabel = new Label("\n" + AssetLoader.i18NBundle.get("programming") +":          Vincent Williams \n" +
                                    AssetLoader.i18NBundle.get("design") + ":                          Vincent Williams \n" +
                                    "\n                       " + AssetLoader.i18NBundle.get("madeUsing") + ": \n" +
                                    "     LibGDX, Autodesk Maya Student, \n" +
                                    "     Paint.net, Audacity \n \n" +
                                    "                            " + AssetLoader.i18NBundle.get("resources") + ": \n" +
                                    "     Kenney Game Assets - kenney.nl", AssetLoader.uiSkin, "very_small");

        aboutTable.add(aboutLabel);
        aboutTable.row();
        aboutTable.add(aboutTextLabel);



        stage.addActor(menuTable);
        stage.addActor(optionsTable);
        stage.addActor(aboutTable);

        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getUICamera().viewportWidth / 2, game.getUICamera().viewportHeight / 2, 0);
    }

    public void initLighting() {
        environment = new Environment();
        sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 70f, 70f, 1, 75);
        sunlight.set(currentMap.getTimeOfDay().sunlightColor, currentMap.getTimeOfDay().sunlightDirection);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentMap.getTimeOfDay().ambientColor));
        environment.add(sunlight);
        environment.shadowMap = sunlight;
    }

    public void initWorld() {
        currentMap = AssetLoader.map;

        modelCache = new ModelCache();
        shadowCache = new ModelCache();

        modelCache.begin();
        modelCache.add(currentMap.base);
        modelCache.add(currentMap.objects);
        modelCache.add(currentMap.doodads);
        modelCache.end();

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
        for (Cloud cloud : cloudArray) {
            cloud.update(delta);
            cloud.render(game.getSpriteBatch());
        }
    }



    @Override
    public void dispose() {
        super.dispose();

        stage.dispose();
        modelCache.dispose();
        sunlight.dispose();

        System.gc();
    }



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (optionsTable.isVisible()) {
            if (screenX > optionsTable.getX() + optionsTable.getWidth() || screenX < optionsTable.getX() || screenY < optionsTable.getY() || screenY > optionsTable.getY() + optionsTable.getHeight()) {
                AssetLoader.button_click.play();
                optionsTable.setVisible(false);
                menuTable.setVisible(true);
            }
        } else if (aboutTable.isVisible()) {
            if (screenX > aboutTable.getX() + aboutTable.getWidth() || screenX < aboutTable.getX() || screenY < aboutTable.getY() || screenY > aboutTable.getY() + aboutTable.getHeight()) {
                AssetLoader.button_click.play();
                aboutTable.setVisible(false);
                menuTable.setVisible(true);
            }
        }
        return true;
    }

    private ClickListener playListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            game.setScreen(new GameScreen(game));
            dispose();
        }
    };

    private ClickListener optionsListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            optionsTable.setVisible(true);
            menuTable.setVisible(false);
        }
    };

    private ClickListener exitListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            Gdx.app.exit();
        }
    };

    private ClickListener soundListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener musicListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener aboutListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            menuTable.setVisible(false);
            aboutTable.setVisible(true);
        }
    };

    private ClickListener achievementsListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener shareListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };
}