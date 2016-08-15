package com.starcat.boxhead.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.boxhead.screens.GameScreen;
import com.starcat.boxhead.screens.LoadingScreen;
import com.starcat.boxhead.screens.MenuScreen;
import com.starcat.boxhead.utils.AssetLoader;

import java.util.Locale;

public class MyGdxGame extends Game {

	public static final boolean DEBUG = true;

	public static final int GAME_WIDTH = 1920;
	public static final int GAME_HEIGHT = 1080;
	public static float ASPECT_RATIO;

	private OrthographicCamera UICamera;
	private PerspectiveCamera gameCamera;
	private Viewport UIViewport, gameViewport;

	//I18NBundles handle localization for different countries
	private I18NBundle bundle;



	@Override
	public void create() {
		debug("created");

		ASPECT_RATIO = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

		UICamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
		gameCamera = new PerspectiveCamera(45, GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);

		UIViewport = new ScreenViewport(UICamera);
		gameViewport = new FillViewport(GAME_WIDTH / 75, GAME_HEIGHT / 75, gameCamera);

		FileHandle fileHandle = Gdx.files.internal("locales/Boxhead");
		bundle = I18NBundle.createBundle(fileHandle, Locale.getDefault());

		Gdx.graphics.setTitle(bundle.get("gameTitle"));

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
			setScreen(new MenuScreen(this, UICamera, UIViewport, bundle));
		} else if(screen.equals("game")) {
			setScreen(new GameScreen(this, UICamera, UIViewport, gameCamera, gameViewport, bundle));
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
