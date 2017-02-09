package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Map;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 2/10/2015.
 *
 * This screens is the main menu of the game
 */
public final class MenuScreen extends BaseScreen {

    private InputMultiplexer inputMultiplexer;

    private Stage stage;
    private Table menuTable;
    private Table optionsTable;
    private Table aboutTable;
    private Table mapSelectTable;

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
    private ImageButton nextMapButton;
    private ImageButton previousMapButton;
    private ImageButton playerNextButton;
    private ImageButton previousPlayerButton;
    private ImageButton playerButton;
    private TextButton shopButton;
    private TextButton startButton;

    private ModelCache modelCache, shadowCache;

    private ModelInstance playerModel;
    private AnimationController playerAnimationController;

    private Environment environment;
    private DirectionalShadowLight sunlight;

    private int currentMap = 0;
    private float worldSize = 30;

    private Map[] maps;

    private Vector3 cameraTargetVector;
    /*
     *TODO: this could be used to have a smaller section of shadow where the player is walking
     *this would require less resources for higher quality shadows (no shadows off camera)
    */
    private Vector3 sunlightVector;
    private Viewport playerViewport;
    private OrthographicCamera playerCamera;



    public MenuScreen(final MyGdxGame game) {
        super(game);

        game.getGameCamera().position.set(-42, 35, -42);
        game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / 30, Dimensions.getGameHeight() / 30);

        playerCamera = new OrthographicCamera(Dimensions.getGameHeight() / Dimensions.getAspectRatio(), Dimensions.getGameHeight());
        playerCamera.rotate(-30, 1, 0, 0);
        playerCamera.rotate(225, 0, 1, 0);
        playerCamera.position.set(-42, 35, -42);
        playerCamera.near = 0;
        playerCamera.far = 200;
        playerCamera.update();

        playerViewport = new FillViewport(Dimensions.getGameWidth() / 30, Dimensions.getGameHeight() / 30, playerCamera);
        playerViewport.setWorldSize(Dimensions.getGameWidth() / 30, Dimensions.getGameHeight() / 30);

        cameraTargetVector = new Vector3(game.getGameCamera().position);
        sunlightVector = new Vector3(0, 0, 0);

        currentMap = 0;
        maps = new Map[2];
        maps[0] = AssetLoader.map1;
        maps[1] = AssetLoader.map2;

        maps[1].setTranslation(-60, 0, 60);

        initUI();
        initWorld();
        initLighting();

        playerModel = new ModelInstance(AssetLoader.player);
        playerModel.transform.rotate(0, 1, 0, 180);
        playerModel.transform.scl(5);
        playerModel.transform.setTranslation(playerCamera.position.x + 22, playerCamera.position.y - 15, playerCamera.position.z - 13);

        playerAnimationController = new AnimationController(playerModel);
        playerAnimationController.setAnimation("pose_single_wield");
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
        super.render(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);



        game.getGameViewport().apply();
        game.getGameCamera().update();

        maps[0].renderSky(game.getSpriteBatch());
        game.getModelBatch().begin(game.getGameCamera());
        game.getModelBatch().render(modelCache, environment);
        game.getModelBatch().end();

        sunlight.begin(sunlightVector, game.getGameCamera().direction);
        game.getShadowBatch().begin(sunlight.getCamera());
        game.getShadowBatch().render(shadowCache);
        game.getShadowBatch().end();
        sunlight.end();

        maps[0].clearSkyColor();



        if (mapSelectTable.isVisible()) {
            playerViewport.apply();
            playerCamera.update();

            game.getModelBatch().begin(playerCamera);
            game.getModelBatch().render(playerModel, environment);
            game.getModelBatch().end();
        }



        game.getUIViewport().apply();
        game.getUICamera().update();

        stage.act();
        stage.draw();
    }

    @Override
    public void update(float delta) {
        playerModel.transform.rotate(0, 1, 0, 1);

        Vector3 currentCameraPosition = game.getGameCamera().position;

        if (currentCameraPosition != cameraTargetVector) {

            if (currentCameraPosition.x < cameraTargetVector.x) {
                game.getGameCamera().translate(2, 0, 0);
            }

            if (currentCameraPosition.y < cameraTargetVector.y) {
                game.getGameCamera().translate(0, 2, 0);
            }

            if (currentCameraPosition.z < cameraTargetVector.z) {
                game.getGameCamera().translate(0, 0, 2);
            }

            if (currentCameraPosition. x > cameraTargetVector.x) {
                game.getGameCamera().translate(-2, 0, 0);
            }

            if (currentCameraPosition. y > cameraTargetVector.y) {
                game.getGameCamera().translate(0, -2, 0);
            }

            if (currentCameraPosition.z > cameraTargetVector.z) {
                game.getGameCamera().translate(0, 0, -2);
            }
        }

        if (worldSize < maps[currentMap].getWorldSize()) {
            game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / ++worldSize, Dimensions.getGameHeight() / ++worldSize);
        } else if (worldSize > maps[currentMap].getWorldSize()) {
            game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / --worldSize, Dimensions.getGameHeight() / --worldSize);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        playerViewport.update(width, height);
    }



    private void initUI()  {
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
        aboutButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        aboutButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        shareButton = new ImageButton(AssetLoader.uiSkin.getDrawable("share"));
        shareButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        shareButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        shareButton.background(AssetLoader.uiSkin.getDrawable("background"));
        shareButton.addListener(shareListener);
        achievementsButton = new ImageButton(AssetLoader.uiSkin.getDrawable("achievement"));
        achievementsButton.addListener(achievementsListener);
        achievementsButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        achievementsButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        achievementsButton.background(AssetLoader.uiSkin.getDrawable("background"));

        menuTable.top();
        menuTable.add(titleLabel).pad(Dimensions.scaleHeight(100), 0, Dimensions.scaleHeight(70), 0).expandX().colspan(3);;
        menuTable.row();
        menuTable.add(playButton).colspan(3);;
        menuTable.row();
        menuTable.add(optionsButton).colspan(3);;
        menuTable.row();
        menuTable.add(exitButton).colspan(3);
        menuTable.row();
        menuTable.add(aboutButton).top().left().expandY().expandX().padLeft(aboutButton.getHeight() / 4);
        menuTable.add(shareButton).top().right().padRight(shareButton.getHeight() / 4);
        menuTable.add(achievementsButton).top().right().padRight(achievementsButton.getHeight() / 4);



        optionsTable = new Table();
        optionsTable.setVisible(false);

        optionsLabel = new Label(AssetLoader.i18NBundle.get("options"), AssetLoader.uiSkin, "large");
        soundLabel = new Label(AssetLoader.i18NBundle.get("sound"), AssetLoader.uiSkin, "small");
        musicLabel = new Label(AssetLoader.i18NBundle.get("music"), AssetLoader.uiSkin, "small");

        musicButton = new ImageButton(AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_off"));
        musicButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        musicButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        musicButton.addListener(musicListener);
        soundButton = new ImageButton(AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_off"));
        soundButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        soundButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        soundButton.addListener(soundListener);

        optionsTable.add(optionsLabel).colspan(2).pad(0, optionsLabel.getWidth() / 3, optionsLabel.getHeight() / 2, optionsLabel.getWidth() / 3);
        optionsTable.row();
        optionsTable.add(soundLabel);
        optionsTable.add(musicLabel);
        optionsTable.row();
        optionsTable.add(soundButton);
        optionsTable.add(musicButton);

        optionsTable.setWidth(Dimensions.scaleWidth(775));
        optionsTable.setHeight(Dimensions.scaleHeight(500));
        optionsTable.setPosition(Dimensions.getHalfScreenWidth() - optionsTable.getWidth() / 2, Dimensions.getHalfScreenHeight() - optionsTable.getHeight() / 2);
        optionsTable.background(AssetLoader.uiSkin.getDrawable("background"));



        aboutTable = new Table();
        aboutTable.setWidth(Dimensions.scaleWidth(1100));
        aboutTable.setHeight(Dimensions.scaleHeight(800));
        aboutTable.setPosition(Dimensions.getHalfScreenWidth() - aboutTable.getWidth() / 2, Dimensions.getHalfScreenHeight() - aboutTable.getHeight() / 2);
        aboutTable.setBackground(AssetLoader.uiSkin.getDrawable("background"));
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



        mapSelectTable = new Table();
        mapSelectTable.setFillParent(true);
        mapSelectTable.setVisible(false);

        nextMapButton = new ImageButton(AssetLoader.uiSkin.getDrawable("right"));
        nextMapButton.getImageCell().width(Dimensions.scaleWidth(225)).height(Dimensions.scaleWidth(225));
        nextMapButton.setSize(Dimensions.scaleWidth(225), Dimensions.scaleWidth(225));
        nextMapButton.addListener(nextMapListener);

        previousMapButton = new ImageButton(AssetLoader.uiSkin.getDrawable("left"));
        previousMapButton.getImageCell().width(Dimensions.scaleWidth(225)).height(Dimensions.scaleWidth(225));
        previousMapButton.setSize(Dimensions.scaleWidth(225), Dimensions.scaleWidth(225));
        previousMapButton.addListener(previousMapListener);
        previousMapButton.setVisible(false);

        startButton = new TextButton(AssetLoader.i18NBundle.get("start"), AssetLoader.uiSkin, "menu");
        startButton.addListener(startListener);

        playerNextButton = new ImageButton(AssetLoader.uiSkin.getDrawable("right"));
        playerNextButton.getImageCell().width(Dimensions.scaleWidth(100)).height(Dimensions.scaleWidth(100));
        playerNextButton.setSize(Dimensions.scaleWidth(100), Dimensions.scaleWidth(100));
        playerNextButton.addListener(nextPlayerListener);

        previousPlayerButton = new ImageButton(AssetLoader.uiSkin.getDrawable("left"));
        previousPlayerButton.getImageCell().width(Dimensions.scaleWidth(100)).height(Dimensions.scaleWidth(100));
        previousPlayerButton.setSize(Dimensions.scaleWidth(100), Dimensions.scaleWidth(100));
        previousPlayerButton.addListener(previousPlayerListener);

        shopButton = new TextButton(AssetLoader.i18NBundle.get("shop"), AssetLoader.uiSkin, "menu");
        shopButton.addListener(shopListener);

        mapSelectTable.top();
        mapSelectTable.add(previousMapButton).padTop(Dimensions.scaleHeight((int)(Dimensions.getGameHeight() / 2 - previousMapButton.getHeight() / 2)));
        mapSelectTable.add(startButton).center().expandX().padTop(Dimensions.scaleHeight((int)(Dimensions.getGameHeight() / 2 - previousMapButton.getHeight() / 2)));
        mapSelectTable.add(nextMapButton).padTop(Dimensions.scaleHeight((int)(Dimensions.getGameHeight() / 2 - previousMapButton.getHeight() / 2)));
        mapSelectTable.row();
        mapSelectTable.add(previousPlayerButton).padTop(Dimensions.scaleHeight(200));
        mapSelectTable.add(playerNextButton).padTop(Dimensions.scaleHeight(200));
        mapSelectTable.add(shopButton).padTop(Dimensions.scaleHeight(200)).padRight((Dimensions.scaleWidth(50)));



        if (MyGdxGame.DEBUG) {
            aboutTable.debug();
            optionsTable.debug();
            menuTable.debug();
            mapSelectTable.debug();
        }

        stage.addActor(menuTable);
        stage.addActor(optionsTable);
        stage.addActor(aboutTable);
        stage.addActor(mapSelectTable);

        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getUICamera().viewportWidth / 2, game.getUICamera().viewportHeight / 2, 0);
    }

    private void initLighting() {
        environment = new Environment();
        sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 70f, 70f, 1, 75);
        sunlight.set(maps[0].getTimeOfDay().sunlightColor, maps[0].getTimeOfDay().sunlightDirection);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, maps[0].getTimeOfDay().ambientColor));
        environment.add(sunlight);
        environment.shadowMap = sunlight;
    }

    private void initWorld() {

        modelCache = new ModelCache();
        shadowCache = new ModelCache();

        modelCache.begin();
        modelCache.add(maps[currentMap].base);
        modelCache.add(maps[currentMap].objects);
        if (maps[currentMap].doodads != null) {
            modelCache.add(maps[currentMap].doodads);
        }

        if (currentMap > 0) {
            modelCache.add(maps[currentMap - 1].base);
            modelCache.add(maps[currentMap - 1].objects);
            if (maps[currentMap - 1].doodads != null) {
                modelCache.add(maps[currentMap - 1].doodads);
            }
        }
        if (currentMap < maps.length - 1) {
            modelCache.add(maps[currentMap + 1].base);
            modelCache.add(maps[currentMap + 1].objects);
            if (maps[currentMap + 1].doodads != null) {
                modelCache.add(maps[currentMap + 1].doodads);
            }
        }
        modelCache.end();

        shadowCache.begin();
        shadowCache.add(maps[currentMap].objects);
        if (maps[currentMap].doodads != null) {
            shadowCache.add(maps[currentMap].doodads);
        }
        shadowCache.end();
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

            menuTable.setVisible(false);
            mapSelectTable.setVisible(true);
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

    private ClickListener nextMapListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            if (currentMap < maps.length - 1) {
                currentMap++;
                initWorld();
            }

            if (currentMap == maps.length - 1) {
                previousMapButton.setVisible(true);
                nextMapButton.setVisible(false);
            }
            cameraTargetVector.add(-60, 0, 60);
            sunlightVector.add(-60, 0, 60);
        }
    };

    private ClickListener previousMapListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();

            if (currentMap > 0){
                currentMap--;
                initWorld();
            }

            if (currentMap == 0) {
                previousMapButton.setVisible(false);
                nextMapButton.setVisible(true);
            }

            cameraTargetVector.add(60, 0, -60);
            sunlightVector.add(60, 0, -60);
        }
    };

    private ClickListener previousPlayerListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();

        }
    };

    private ClickListener nextPlayerListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener shopListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
        }
    };

    private ClickListener startListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();

            game.setScreen(new GameScreen(game));
            dispose();
        }
    };
}