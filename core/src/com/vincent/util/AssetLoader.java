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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.vincent.util.map.CustomAtlasTmxMapLoader;
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
    private static com.vincent.util.map.MapBodyManager mapBodyManager;
    private static InternalFileHandleResolver fileHandleResolver;

    //loader for tiled maps
    private static CustomAtlasTmxMapLoader mapLoader;
    private static SoundLoader soundLoader;

    public static World world;

    //atlas for ui elements
    private static TextureAtlas ui;

    public static NinePatch button;
    public static TextureRegion touchpadNob;
    public static TextureRegion touchpadBase;
    public static TextureRegion close;
    public static TextureRegion pause;
    public static TextureRegion play;
    public static TextureRegion soundOn;
    public static TextureRegion soundOff;
    public static TextureRegion musicOn;
    public static TextureRegion musicOff;
    public static TextureRegion star;
    public static TextureRegion options;
    public static Texture boxhead_1;
    public static Texture boxhead_2;
    public static Texture boxhead_3;
    public static Texture boxhead_4;
    public static Texture boxhead_5;
    public static Texture boxhead_6;
    public static Texture boxhead_7;
    public static Texture boxhead_8;

    public static TiledMap map1;

    //load all assets here, but note that calling manager.load only queues assets to load
    public static void load() {
        world = new World(new Vector2(0, 0), true);

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;

        CustomAtlasTmxMapLoader.CustomAtlasTiledMapLoaderParameters tileParameters = new CustomAtlasTmxMapLoader.CustomAtlasTiledMapLoaderParameters();
        tileParameters.textureMinFilter = Texture.TextureFilter.Linear;


        manager = new AssetManager();
        mapBodyManager = new com.vincent.util.map.MapBodyManager(world, GameUtils.PIXELS_PER_METER, null, 0);
        fileHandleResolver = new InternalFileHandleResolver();
        mapLoader = new CustomAtlasTmxMapLoader(fileHandleResolver);
        soundLoader = new SoundLoader(fileHandleResolver);


        manager.load("entities/player/up/boxhead_1.png", Texture.class, param);
        manager.load("entities/player/up_right/boxhead_2.png", Texture.class, param);
        manager.load("entities/player/right/boxhead_3.png", Texture.class, param);
        manager.load("entities/player/down_right/boxhead_4.png", Texture.class, param);
        manager.load("entities/player/down/boxhead_5.png", Texture.class, param);
        manager.load("entities/player/down_left/boxhead_6.png", Texture.class, param);
        manager.load("entities/player/left/boxhead_7.png", Texture.class, param);
        manager.load("entities/player/up_left/boxhead_8.png", Texture.class, param);

        manager.load("ui/ui.atlas", TextureAtlas.class);
        manager.setLoader(TiledMap.class, mapLoader);
        manager.load("maps/map1.tmx", TiledMap.class, tileParameters);

    }

    //initialize the various resources after they are loaded
    //take care to call this before you reference any assets to avoid a null pointer
    public static void initAssets() {
        ui = manager.get("ui/ui.atlas", TextureAtlas.class);
        button = ui.createPatch("button");
        touchpadBase = ui.findRegion("touchpad_base");
        touchpadNob = ui.findRegion("touchpad_nob");
        close = ui.findRegion("close");
        pause = ui.findRegion("pause");
        play = ui.findRegion("play");
        soundOn = ui.findRegion("sound_on");
        soundOff = ui.findRegion("sound_off");
        musicOn = ui.findRegion("music_on");
        musicOff = ui.findRegion("music_Off");
        star = ui.findRegion("star");
        options = ui.findRegion("options");

        boxhead_1 = manager.get("entities/player/up/boxhead_1.png", Texture.class);
        boxhead_2 = manager.get("entities/player/up_right/boxhead_2.png", Texture.class);
        boxhead_3 = manager.get("entities/player/right/boxhead_3.png", Texture.class);
        boxhead_4 = manager.get("entities/player/down_right/boxhead_4.png", Texture.class);
        boxhead_5 =  manager.get("entities/player/down/boxhead_5.png", Texture.class);
        boxhead_6 = manager.get("entities/player/down_left/boxhead_6.png", Texture.class);
        boxhead_7 = manager.get("entities/player/left/boxhead_7.png", Texture.class);
        boxhead_8 = manager.get("entities/player/up_left/boxhead_8.png", Texture.class);


        map1 = manager.get("maps/map1.tmx");
        mapBodyManager.createPhysics(map1);
        MapUtils.correctMapObjects(map1, "objects");
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
       return manager.update();
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
        mapBodyManager.destroyPhysics();
        world.dispose();
    }

    //disposes all assets but does not dispose of the manager itself
    public static void clearManager() {
        manager.clear();
    }
}
