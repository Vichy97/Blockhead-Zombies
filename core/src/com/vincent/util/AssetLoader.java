package com.vincent.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.vincent.game.MyGdxGame;
import com.vincent.util.map.CustomAtlasTmxMapLoader;
import com.vincent.util.map.CustomTmxMapLoader;
import com.vincent.util.map.MapUtils;

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

    //loader for tiled maps
    private static AtlasTmxMapLoader tileMapLoader;
    private static CustomTmxMapLoader objectMapLoader;
    private static SoundLoader soundLoader;

    //atlas for ui elements
    private static TextureAtlas ui;

    public static NinePatch button;
    public static TextureRegionDrawable touchpadNob;
    public static TextureRegionDrawable touchpadBase;
    public static TextureRegionDrawable pause;
    public static TextureRegionDrawable play;
    public static TextureRegionDrawable soundOn;
    public static TextureRegionDrawable soundOff;
    public static TextureRegionDrawable musicOn;
    public static TextureRegionDrawable musicOff;
    public static Texture boxhead_1;
    public static Texture boxhead_2;
    public static Texture boxhead_3;
    public static Texture boxhead_4;
    public static Texture boxhead_5;
    public static Texture boxhead_6;
    public static Texture boxhead_7;
    public static Texture boxhead_8;
    public static Texture cloudTexture1;
    public static Texture cloudTexture2;
    public static Texture cloudTexture3;
    public static Texture bullet;

    public static TiledMap map1TileMap;
    public static TiledMap map1ObjectMap;

    //load all assets here, but note that calling manager.load only queues assets to load
    public static void load() {
        Gdx.app.log("AssetLoader", "load");

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        AtlasTmxMapLoader.AtlasTiledMapLoaderParameters tileParameters = new AtlasTmxMapLoader.AtlasTiledMapLoaderParameters();
        tileParameters.textureMinFilter = Texture.TextureFilter.Linear;
        tileParameters.textureMagFilter = Texture.TextureFilter.Linear;

        CustomTmxMapLoader.CustomParameters objectParameters = new CustomTmxMapLoader.CustomParameters();
        objectParameters.textureMinFilter = Texture.TextureFilter.Linear;
        objectParameters.textureMagFilter = Texture.TextureFilter.Linear;

        manager = new AssetManager();
        fileHandleResolver = new InternalFileHandleResolver();
        tileMapLoader = new AtlasTmxMapLoader(fileHandleResolver);
        objectMapLoader = new CustomTmxMapLoader(fileHandleResolver);
        soundLoader = new SoundLoader(fileHandleResolver);


        manager.load("entities/player/up/boxhead_1.png", Texture.class, param);
        manager.load("entities/player/up_right/boxhead_2.png", Texture.class, param);
        manager.load("entities/player/right/boxhead_3.png", Texture.class, param);
        manager.load("entities/player/down_right/boxhead_4.png", Texture.class, param);
        manager.load("entities/player/down/boxhead_5.png", Texture.class, param);
        manager.load("entities/player/down_left/boxhead_6.png", Texture.class, param);
        manager.load("entities/player/left/boxhead_7.png", Texture.class, param);
        manager.load("entities/player/up_left/boxhead_8.png", Texture.class, param);
        manager.load("cloud_1.png", Texture.class, param);
        manager.load("cloud_2.png", Texture.class, param);
        manager.load("cloud_3.png", Texture.class, param);
        manager.load("bullet.png", Texture.class, param);

        manager.load("ui/ui.atlas", TextureAtlas.class);
        manager.setLoader(TiledMap.class, objectMapLoader);
        manager.load("maps/map1Object.tmx", TiledMap.class, objectParameters);
        manager.finishLoading(); //not sure if this should be here but it has issues with assigning two different loaders for a single assets type (this fixes that somehow)
        manager.setLoader(TiledMap.class, tileMapLoader);
        manager.load("maps/map1Tile.tmx", TiledMap.class, tileParameters);
    }

    //initialize the various resources after they are loaded
    //take care to call this before you reference any assets to avoid a null pointer
    public static void initAssets() {
        Gdx.app.log("AssetLoader", "init");
        ui = manager.get("ui/ui.atlas", TextureAtlas.class);
        button = ui.createPatch("button");
        touchpadBase = new TextureRegionDrawable(ui.findRegion("touchpad_base"));
        touchpadNob = new TextureRegionDrawable(ui.findRegion("touchpad_nob"));
        pause = new TextureRegionDrawable(ui.findRegion("pause"));
        play = new TextureRegionDrawable(ui.findRegion("play"));
        soundOn = new TextureRegionDrawable(ui.findRegion("sound_on"));
        soundOff = new TextureRegionDrawable(ui.findRegion("sound_off"));
        musicOn = new TextureRegionDrawable(ui.findRegion("music_on"));
        musicOff = new TextureRegionDrawable(ui.findRegion("music_off"));

        boxhead_1 = manager.get("entities/player/up/boxhead_1.png", Texture.class);
        boxhead_2 = manager.get("entities/player/up_right/boxhead_2.png", Texture.class);
        boxhead_3 = manager.get("entities/player/right/boxhead_3.png", Texture.class);
        boxhead_4 = manager.get("entities/player/down_right/boxhead_4.png", Texture.class);
        boxhead_5 =  manager.get("entities/player/down/boxhead_5.png", Texture.class);
        boxhead_6 = manager.get("entities/player/down_left/boxhead_6.png", Texture.class);
        boxhead_7 = manager.get("entities/player/left/boxhead_7.png", Texture.class);
        boxhead_8 = manager.get("entities/player/up_left/boxhead_8.png", Texture.class);
        cloudTexture1 = manager.get("cloud_1.png", Texture.class);
        cloudTexture2 = manager.get("cloud_2.png", Texture.class);
        cloudTexture3 = manager.get("cloud_3.png", Texture.class);
        bullet = manager.get("bullet.png", Texture.class);

        map1TileMap = manager.get("maps/map1Tile.tmx");
        map1ObjectMap = manager.get("maps/map1Object.tmx");
        MapUtils.correctMapObjects(map1ObjectMap.getLayers().get("objects"));
        MapUtils.correctMapObjects(map1ObjectMap.getLayers().get("static objects"));
    }

    //creates a bitmap font of the given size from the given .ttf file
    //scaling a font doesnt work well in libgdx so this is
    //necessary to make clean looking text
    public static BitmapFont createFont(String font, int size) {
        BitmapFont textFont;
        FileHandle fontFile = Gdx.files.internal("fonts/" + font);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        textFont = generator.generateFont(parameter);
        generator.dispose();
        return textFont;
    }

    //this needs to be called continuously to load assets
    //manager.load only queues assets to be loaded. this actually loads
    //them asynchronously. calling manager.finishloading() will load all
    //assets at once but it will load them synchronously
    public static boolean update() {
        debug("update");
        return manager.update();
    }

    //method for writing to the log
    private static void debug(String message) {
        if (MyGdxGame.DEBUG) {
            Gdx.app.log("Asset Loader", message);
        }
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
}
