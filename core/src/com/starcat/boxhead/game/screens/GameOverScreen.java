package com.starcat.boxhead.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 11/29/2016.
 */

public class GameOverScreen extends BaseScreen {

    private Stage stage;
    private Table table;

    private TextButton menuButton;
    private Label gameOverLabel, scoreLabel;



    public GameOverScreen(MyGdxGame game) {
        super(game);

        menuButton = new TextButton(AssetLoader.i18NBundle.get("menu"), AssetLoader.uiSkin, "small");
        menuButton.addListener(menuListener);
        gameOverLabel = new Label(AssetLoader.i18NBundle.get("gameOver"), AssetLoader.uiSkin);
        scoreLabel = new Label(AssetLoader.i18NBundle.get("score"), AssetLoader.uiSkin);

        table = new Table();
        table.setFillParent(true);

        table.add(gameOverLabel).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getHeight() / 6);
        table.row();
        table.add(menuButton).width(Gdx.graphics.getWidth() / 6).height(Gdx.graphics.getHeight() / 6);
        table.row();

        stage = new Stage();
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        game.getUIViewport().apply();
        game.getUICamera().position.set(game.getGameCamera().viewportWidth / 2, game.getGameCamera().viewportHeight / 2, 0);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);

        game.getUICamera().update();

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();

        stage.dispose();
        System.gc();
    }



    private ClickListener menuListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AssetLoader.button_click.play();
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    };
}
