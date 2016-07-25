package com.starcat.boxhead.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.starcat.boxhead.game.MyGdxGame;

/**
 * Created by Vincent on 2/10/2015.
 *
 * load all assets here and call load from one of your screens
 * this also makes use of assetmanager to load assets asynchronously so you can display
 * other things such as a splash screen or loading bar. most textures are also stored
 * in texture atlas's for more efficient loading/rendering
 *
 * if ram is short then I could make more methods such as loadMenuAssets
 * then unload before the next screen (probably not necessary)
 */
public class AssetLoader {

    private static AssetManager manager;
    private static InternalFileHandleResolver fileHandleResolver;

    private static SoundLoader soundLoader;

    private static BitmapFont largeFont, smallFont;
    private static TextureAtlas ui;
    public static Skin uiSkin;

    private static Model map;
    public static ModelInstance mapInstance;

    public static Texture cloudTexture1;
    public static Texture cloudTexture2;
    public static Texture cloudTexture3;



    public static void load() {
        Gdx.app.log("AssetLoader", "load");

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        manager = new AssetManager();
        fileHandleResolver = new InternalFileHandleResolver();
        soundLoader = new SoundLoader(fileHandleResolver);

        manager.load("cloud_1.png", Texture.class, param);
        manager.load("cloud_2.png", Texture.class, param);
        manager.load("cloud_3.png", Texture.class, param);

        manager.load("ui/ui.atlas", TextureAtlas.class);
        manager.load("maps/test_scene.g3db", Model.class);

    }

    public static boolean update() {
        debug("update");
        return manager.update();
    }

    public static void initAssets() {
        Gdx.app.log("AssetLoader", "init");

        largeFont = createFont("DroidSans.ttf", Gdx.graphics.getHeight() / 10, Color.WHITE);
        smallFont = createFont("DroidSans.ttf", Gdx.graphics.getHeight() / 15, Color.WHITE);
        ui = manager.get("ui/ui.atlas", TextureAtlas.class);
        uiSkin = new Skin(Gdx.files.internal("ui/ui.json"));
        uiSkin.addRegions(ui);
        uiSkin.get("large", TextButton.TextButtonStyle.class).font = largeFont;
        uiSkin.get("small", TextButton.TextButtonStyle.class).font = smallFont;
        uiSkin.get("default", CheckBox.CheckBoxStyle.class).font = smallFont;

        map = manager.get("maps/test_scene.g3db", Model.class);
        mapInstance = new ModelInstance(map);

        cloudTexture1 = manager.get("cloud_1.png", Texture.class);
        cloudTexture2 = manager.get("cloud_2.png", Texture.class);
        cloudTexture3 = manager.get("cloud_3.png", Texture.class);
    }

    //creates a freetype bitmap font
    private static BitmapFont createFont(String font, int size, Color color) {
        BitmapFont textFont;
        FileHandle fontFile = Gdx.files.internal("fonts/" + font);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        textFont = generator.generateFont(parameter);
        generator.dispose();
        return textFont;
    }

    //returns a float between 0 and 1. this doesnt work well for atlas's
    public static float getProgress() {
        return manager.getProgress();
    }

    //returns the number of assets left to load. this fluctuates?
    public static int getQueuedAssets() {
        return manager.getQueuedAssets();
    }

    //dispose all assets and manager
    public static void dispose() {
        manager.dispose();
    }

    //disposes all assets but does not dispose of the manager itself
    public static void clearManager() {
        manager.clear();
    }



    //method for writing to the log
    private static void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("Asset Loader", message);
        }
    }
}
