package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starcat.boxhead.ui.ShadowLabel;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Cloud;
import com.starcat.boxhead.objects.Map;
import com.starcat.boxhead.ui.ShadowTextButton;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 2/10/2015.
 *
 * This screens is the main menu of the game
 */
public class MenuScreen extends BaseScreen {

    private Stage stage;
    private Table table;

    private ShadowTextButton play;
    private ShadowTextButton exit;
    private ShadowTextButton options;
    private ShadowLabel title;
    private ImageButton about;

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

        Gdx.input.setInputProcessor(stage);

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
        table = new Table();
        table.setFillParent(true);

        title = new ShadowLabel(AssetLoader.i18NBundle.get("menuTitle"), AssetLoader.uiSkin, "title");
        play = new ShadowTextButton(AssetLoader.i18NBundle.get("play"), AssetLoader.uiSkin, "menu");
        play.addListener(playListener);
        options = new ShadowTextButton(AssetLoader.i18NBundle.get("options"), AssetLoader.uiSkin, "menu");
        options.addListener(optionsListener);
        exit = new ShadowTextButton(AssetLoader.i18NBundle.get("exit"), AssetLoader.uiSkin, "menu");
        exit.addListener(exitListener);

        table.top();
        table.add(title).pad(Gdx.graphics.getHeight() / 10, 0, Gdx.graphics.getHeight() / 15, 0);
        table.row();
        table.add(play);
        table.row();
        table.add(options);
        table.row();
        table.add(exit);
        stage.addActor(table);

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
            //dispose();
        }
    };

    private ClickListener exitListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            Gdx.app.exit();
        }
    };
}