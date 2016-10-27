package com.starcat.boxhead.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.screens.GameScreen;
import com.starcat.boxhead.screens.LoadingScreen;
import com.starcat.boxhead.screens.MenuScreen;
import com.starcat.boxhead.utils.AssetLoader;

public class MyGdxGame extends Game {

	public static final boolean DEBUG = false;

	public static final int GAME_WIDTH = 1920;
	public static final int GAME_HEIGHT = 1080;
	public static float ASPECT_RATIO;

	private OrthographicCamera UICamera;
	private OrthographicCamera gameCamera;
	private Viewport UIViewport, gameViewport;



	@Override
	public void create() {
		debug("created");

		ASPECT_RATIO = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

		UICamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
		gameCamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);

		UIViewport = new ScreenViewport(UICamera);
		gameViewport = new FillViewport(GAME_WIDTH / 120, GAME_HEIGHT / 120, gameCamera);

		setScreen("loading");
	}

	@Override
	public void dispose() {
		debug("dispose");

		super.dispose();
		AssetLoader.dispose();
	}



	public void setScreen(String screen) {
		if (screen.equals("loading")) {
			setScreen(new LoadingScreen(this));
		} else if(screen.equals("menu")) {
			setScreen(new MenuScreen(this, UICamera, UIViewport));
		} else if(screen.equals("game")) {
			setScreen(new GameScreen(this, UICamera, UIViewport, gameCamera, gameViewport));
		} else {
			debug("Incorrect Screen Name");
			Gdx.app.exit();
		}
		System.gc();
	}

	private static void debug(String message) {
		if (MyGdxGame.DEBUG) {
			Gdx.app.log("My GDX Game", message);
		}
	}


}
