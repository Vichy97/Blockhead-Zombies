package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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

    private boolean sound;
    private boolean music;
    private boolean shadows;
    private boolean blood;
    private boolean particles;

    private Stage stage;
    private Table menuTable;
    private Table optionsTable;
    private Table aboutTable;
    private Table mapSelectTable;
    private Table mapSelectTable2;

    private TextButton playButton;
    private TextButton exitButton;
    private TextButton optionsButton;
    private Label titleLabel;
    private Label musicLabel;
    private Label soundLabel;
    private Label bloodLabel;
    private Label shadowsLabel;
    private Label particlesLabel;
    private Label optionsLabel;
    private Label aboutLabel;
    private Label aboutTextLabel;
    private ImageButton bloodButton;
    private ImageButton shadowsButton;
    private ImageButton particlesButton;
    private ImageButton musicButton;
    private ImageButton soundButton;
    private ImageButton aboutButton;
    private ImageButton shareButton;
    private ImageButton achievementsButton;
    private ImageButton nextMapButton;
    private ImageButton previousMapButton;
    private ImageButton nextPlayerButton;
    private ImageButton previousPlayerButton;
    private TextButton shopButton;
    private TextButton startButton;

    private ModelCache modelCache, shadowCache;

    private ModelInstance playerModel;
    private AnimationController playerAnimationController;

    private Environment environment;
    private DirectionalShadowLight sunlight;

    private int currentMap = 0;
    private float worldSize = 30;

     //TODO: this could be used to have a smaller section of shadow where the player is walking
     //this would require less resources for higher quality shadows (no shadows off camera)
    private Vector3 sunlightVector;
    private Vector3 cameraTargetVector;

    private Viewport playerViewport;
    private OrthographicCamera playerCamera;
    private int currentSkin;



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
        currentSkin = 0;

        initSettings();
        initUI();
        initWorld();
        initLighting();

        playerModel = new ModelInstance(AssetLoader.player);
        playerModel.transform.rotate(0, 1, 0, 180);
        playerModel.transform.scl(4.5f);
        playerModel.transform.setTranslation(-25, 20, -60);

        playerModel.materials.get(1).set(new TextureAttribute(TextureAttribute.Diffuse, AssetLoader.playerSkins[currentSkin]));

        playerAnimationController = new AnimationController(playerModel);
        playerAnimationController.setAnimation("pose_empty");
        playerAnimationController.update(Gdx.graphics.getDeltaTime());
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

        AssetLoader.map1.renderSky(game.getSpriteBatch());
        game.getModelBatch().begin(game.getGameCamera());
        game.getModelBatch().render(modelCache, environment);
        game.getModelBatch().end();

        sunlight.begin(sunlightVector, game.getGameCamera().direction);
        if (shadows) {
            game.getShadowBatch().begin(sunlight.getCamera());
            game.getShadowBatch().render(shadowCache);
            game.getShadowBatch().end();
        }
        sunlight.end();

        AssetLoader.map1.clearSkyColor();



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

        if (worldSize < AssetLoader.maps[currentMap].getWorldSize()) {
            game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / ++worldSize, Dimensions.getGameHeight() / ++worldSize);
        } else if (worldSize > AssetLoader.maps[currentMap].getWorldSize()) {
            game.getGameViewport().setWorldSize(Dimensions.getGameWidth() / --worldSize, Dimensions.getGameHeight() / --worldSize);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        playerViewport.update(width, height);
    }


    private void initSettings() {
        sound = game.getSettings().getBoolean("sound", true);
        music = game.getSettings().getBoolean("music", true);
        shadows = game.getSettings().getBoolean("shadows", true);
        blood = game.getSettings().getBoolean("blood", true);
        particles = game.getSettings().getBoolean("particles", true);
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
        aboutButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        aboutButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        aboutButton.addListener(aboutListener);

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
        soundLabel = new Label(AssetLoader.i18NBundle.get("sound"), AssetLoader.uiSkin, "very_small");
        musicLabel = new Label(AssetLoader.i18NBundle.get("music"), AssetLoader.uiSkin, "very_small");
        bloodLabel = new Label(AssetLoader.i18NBundle.get("blood"), AssetLoader.uiSkin, "very_small");
        shadowsLabel = new Label(AssetLoader.i18NBundle.get("shadows"), AssetLoader.uiSkin, "very_small");
        particlesLabel = new Label(AssetLoader.i18NBundle.get("particles"), AssetLoader.uiSkin, "very_small");

        musicButton = new ImageButton(AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_on"), AssetLoader.uiSkin.getDrawable("music_off"));
        musicButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        musicButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        musicButton.setChecked(!music);
        musicButton.addListener(musicListener);

        soundButton = new ImageButton(AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_on"), AssetLoader.uiSkin.getDrawable("sound_off"));
        soundButton.getImageCell().width(Dimensions.scaleWidth(120)).height(Dimensions.scaleWidth(120));
        soundButton.setSize(Dimensions.scaleWidth(120), Dimensions.scaleWidth(120));
        soundButton.setChecked(!sound);
        soundButton.addListener(soundListener);

        bloodButton = new ImageButton(AssetLoader.uiSkin.getDrawable("blood"), AssetLoader.uiSkin.getDrawable("blood"), AssetLoader.uiSkin.getDrawable("blood_off"));
        bloodButton.getImageCell().width(Dimensions.scaleWidth(150)).height(Dimensions.scaleWidth(150));
        bloodButton.setSize(Dimensions.scaleWidth(150), Dimensions.scaleWidth(150));
        bloodButton.setChecked(!blood);
        bloodButton.addListener(bloodListener);

        shadowsButton = new ImageButton(AssetLoader.uiSkin.getDrawable("shadows"), AssetLoader.uiSkin.getDrawable("shadows"), AssetLoader.uiSkin.getDrawable("shadows_off"));
        shadowsButton.getImageCell().width(Dimensions.scaleWidth(150)).height(Dimensions.scaleWidth(150));
        shadowsButton.setSize(Dimensions.scaleWidth(150), Dimensions.scaleWidth(150));
        shadowsButton.setChecked(!shadows);
        shadowsButton.addListener(shadowsListener);

        particlesButton = new ImageButton(AssetLoader.uiSkin.getDrawable("particles"), AssetLoader.uiSkin.getDrawable("particles"), AssetLoader.uiSkin.getDrawable("particles_off"));
        particlesButton.getImageCell().width(Dimensions.scaleWidth(150)).height(Dimensions.scaleWidth(150));
        particlesButton.setSize(Dimensions.scaleWidth(150), Dimensions.scaleWidth(150));
        particlesButton.setChecked(!particles);
        particlesButton.addListener(particlesListener);

        optionsTable.add(optionsLabel).colspan(6).pad(0, optionsLabel.getWidth() / 3, optionsLabel.getHeight() / 4, optionsLabel.getWidth() / 3);
        optionsTable.row();
        optionsTable.add(soundLabel).colspan(3);
        optionsTable.add(musicLabel).colspan(3);
        optionsTable.row();
        optionsTable.add(soundButton).colspan(3);
        optionsTable.add(musicButton).colspan(3);
        optionsTable.row();
        optionsTable.add(bloodLabel).colspan(2);
        optionsTable.add(shadowsLabel).colspan(2);
        optionsTable.add(particlesLabel).colspan(2);
        optionsTable.row();
        optionsTable.add(bloodButton).colspan(2);
        optionsTable.add(shadowsButton).colspan(2);
        optionsTable.add(particlesButton).colspan(2);

        optionsTable.setWidth(Dimensions.scaleWidth(800));
        optionsTable.setHeight(Dimensions.scaleHeight(550));
        optionsTable.setPosition(Dimensions.getHalfScreenWidth() - optionsTable.getWidth() / 2, Dimensions.getHalfScreenHeight() - optionsTable.getHeight() / 2);
        optionsTable.background(AssetLoader.uiSkin.getDrawable("background"));



        aboutTable = new Table();
        aboutTable.setWidth(Dimensions.scaleWidth(1200));
        aboutTable.setHeight(Dimensions.scaleHeight(900));
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
        mapSelectTable2 = new Table();
        mapSelectTable2.setFillParent(true);
        mapSelectTable2.setVisible(false);

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

        nextPlayerButton = new ImageButton(AssetLoader.uiSkin.getDrawable("right"));
        nextPlayerButton.getImageCell().width(Dimensions.scaleWidth(100)).height(Dimensions.scaleWidth(100));
        nextPlayerButton.setSize(Dimensions.scaleWidth(100), Dimensions.scaleWidth(100));
        nextPlayerButton.addListener(nextPlayerListener);

        previousPlayerButton = new ImageButton(AssetLoader.uiSkin.getDrawable("left"));
        previousPlayerButton.getImageCell().width(Dimensions.scaleWidth(100)).height(Dimensions.scaleWidth(100));
        previousPlayerButton.setSize(Dimensions.scaleWidth(100), Dimensions.scaleWidth(100));
        previousPlayerButton.addListener(previousPlayerListener);

        shopButton = new TextButton(AssetLoader.i18NBundle.get("shop"), AssetLoader.uiSkin, "large");
        shopButton.addListener(shopListener);

        mapSelectTable.center();
        mapSelectTable.add(previousMapButton).expandX().left().padLeft(Dimensions.scaleWidth(25));
        mapSelectTable.add(startButton).expandX();
        mapSelectTable.add(nextMapButton).expandX().right().padRight(Dimensions.scaleWidth(25));

        mapSelectTable2.bottom();
        mapSelectTable2.add(previousPlayerButton).padLeft(Dimensions.scaleWidth(50));
        mapSelectTable2.add(nextPlayerButton).expandX().left().padLeft(Dimensions.scaleWidth(140));
        mapSelectTable2.add(shopButton).expandX().right().padRight(Dimensions.scaleWidth(50));
        mapSelectTable2.padBottom(Dimensions.scaleHeight(100));



        if (MyGdxGame.DEBUG) {
            aboutTable.debug();
            optionsTable.debug();
            menuTable.debug();
            mapSelectTable.debug();
            mapSelectTable2.debug();
        }

        stage.addActor(menuTable);
        stage.addActor(optionsTable);
        stage.addActor(aboutTable);
        stage.addActor(mapSelectTable);
        stage.addActor(mapSelectTable2);

        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getUICamera().viewportWidth / 2, game.getUICamera().viewportHeight / 2, 0);
    }

    private void initLighting() {
        environment = new Environment();
        sunlight = new DirectionalShadowLight(1920 * 3, 1080 * 3, 70f, 70f, 1, 75);
        sunlight.set(AssetLoader.maps[0].getTimeOfDay().sunlightColor, AssetLoader.maps[0].getTimeOfDay().sunlightDirection);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, AssetLoader.maps[0].getTimeOfDay().ambientColor));
        environment.add(sunlight);
        environment.shadowMap = sunlight;
    }

    private void initWorld() {

        modelCache = new ModelCache();
        shadowCache = new ModelCache();

        modelCache.begin();
        modelCache.add(AssetLoader.maps[currentMap].base);
        modelCache.add(AssetLoader.maps[currentMap].objects);
        if (AssetLoader.maps[currentMap].doodads != null) {
            modelCache.add(AssetLoader.maps[currentMap].doodads);
        }

        if (currentMap > 0) {
            modelCache.add(AssetLoader.maps[currentMap - 1].base);
            modelCache.add(AssetLoader.maps[currentMap - 1].objects);
            if (AssetLoader.maps[currentMap - 1].doodads != null) {
                modelCache.add(AssetLoader.maps[currentMap - 1].doodads);
            }
        }
        if (currentMap < AssetLoader.maps.length - 1) {
            modelCache.add(AssetLoader.maps[currentMap + 1].base);
            modelCache.add(AssetLoader.maps[currentMap + 1].objects);
            if (AssetLoader.maps[currentMap + 1].doodads != null) {
                modelCache.add(AssetLoader.maps[currentMap + 1].doodads);
            }
        }
        modelCache.end();

        shadowCache.begin();
        shadowCache.add(AssetLoader.maps[currentMap].objects);
        if (AssetLoader.maps[currentMap].doodads != null) {
            shadowCache.add(AssetLoader.maps[currentMap].doodads);
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
                game.getSettings().flush();
                if (sound) {
                    AssetLoader.button_click.play();
                }
                optionsTable.setVisible(false);
                menuTable.setVisible(true);
            }
        } else if (aboutTable.isVisible()) {
            if (screenX > aboutTable.getX() + aboutTable.getWidth() || screenX < aboutTable.getX() || screenY < aboutTable.getY() || screenY > aboutTable.getY() + aboutTable.getHeight()) {
                if (sound) {
                    AssetLoader.button_click.play();
                }
                aboutTable.setVisible(false);
                menuTable.setVisible(true);
            }
        }
        return true;
    }

    private ClickListener playListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            menuTable.setVisible(false);
            mapSelectTable.setVisible(true);
            mapSelectTable2.setVisible(true);
        }
    };

    private ClickListener optionsListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            optionsTable.setVisible(true);
            menuTable.setVisible(false);
        }
    };

    private ClickListener exitListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            Gdx.app.exit();
        }
    };

    private ClickListener soundListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (!sound) {
                AssetLoader.button_click.play();
            }
            sound = !sound;
            game.getSettings().putBoolean("sound", sound);
        }
    };

    private ClickListener musicListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            music = !music;
            game.getSettings().putBoolean("music", music);
        }
    };

    private ClickListener bloodListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            blood = !blood;
            game.getSettings().putBoolean("blood", blood);
        }
    };

    private ClickListener shadowsListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            shadows = !shadows;
            game.getSettings().putBoolean("shadows", shadows);
            game.getSettings().flush();
        }
    };

    private ClickListener particlesListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            particles = !particles;
            game.getSettings().putBoolean("particles", particles);
        }
    };

    private ClickListener aboutListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            menuTable.setVisible(false);
            aboutTable.setVisible(true);
        }
    };

    private ClickListener achievementsListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }
        }
    };

    private ClickListener shareListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }
        }
    };

    private ClickListener nextMapListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            if (currentMap < AssetLoader.maps.length - 1) {
                currentMap++;
                initWorld();
            }

            if (currentMap == AssetLoader.maps.length - 1) {
                nextMapButton.setVisible(false);
            }
            previousMapButton.setVisible(true);

            cameraTargetVector.add(-60, 0, 60);
            sunlightVector.add(-60, 0, 60);
        }
    };

    private ClickListener previousMapListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            if (currentMap > 0){
                currentMap--;
                initWorld();
            }

            if (currentMap == 0) {
                previousMapButton.setVisible(false);
            }
            nextMapButton.setVisible(true);

            cameraTargetVector.add(60, 0, -60);
            sunlightVector.add(60, 0, -60);
        }
    };

    private ClickListener previousPlayerListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            if (currentSkin > 0) {
                currentSkin --;

                playerModel.materials.get(1).set(new TextureAttribute(TextureAttribute.Diffuse, AssetLoader.playerSkins[currentSkin]));
            }

            if (currentSkin == 0) {
                previousPlayerButton.setVisible(false);
            }
            nextPlayerButton.setVisible(true);
        }
    };

    private ClickListener nextPlayerListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            if (currentSkin < AssetLoader.playerSkins.length) {
                currentSkin++;

                playerModel.materials.get(1).set(new TextureAttribute(TextureAttribute.Diffuse, AssetLoader.playerSkins[currentSkin]));
            }

            if (currentSkin == AssetLoader.playerSkins.length - 1) {
                nextPlayerButton.setVisible(false);
            }
            previousPlayerButton.setVisible(true);
        }
    };

    private ClickListener shopListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }
        }
    };

    private ClickListener startListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (sound) {
                AssetLoader.button_click.play();
            }

            game.setScreen(new GameScreen(game, AssetLoader.maps[currentMap], AssetLoader.playerSkins[currentSkin]));
            dispose();
        }
    };
}