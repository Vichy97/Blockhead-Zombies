package com.starcat.boxhead.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.Map;

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

    public static I18NBundle i18NBundle;

    private static BitmapFont largeFont, smallFont;
    private static TextureAtlas ui;
    public static Skin uiSkin;

    public static Map map;
    public static Model mapBase, mapObjects, mapDoodads;
    public static Model boxhead;
    public static Model pistol;
    public static Model pistolBullet;
    public static Model pistolBulletCasing;

    public static Texture cloudTexture1;
    public static Texture cloudTexture2;
    public static Texture cloudTexture3;
    public static Texture starTexture1;
    public static Texture starTexture2;

    public static Sound button_click;
    public static Sound pistolSound;



    public static void load() {
        debug("load");

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        manager = new AssetManager();

        manager.load("locales/Boxhead", I18NBundle.class);

        manager.load("cloud_1.png", Texture.class, param);
        manager.load("cloud_2.png", Texture.class, param);
        manager.load("cloud_3.png", Texture.class, param);
        manager.load("starBig.png", Texture.class, param);
        manager.load("starSmall.png", Texture.class, param);

        manager.load("ui/ui.atlas", TextureAtlas.class);
        manager.load("maps/test_scene/test_scene_base.g3dj", Model.class);
        manager.load("maps/test_scene/test_scene_objects.g3dj", Model.class);
        manager.load("maps/test_scene/test_scene_doodads.g3dj", Model.class);

        manager.load("objects/entities/boxhead.g3dj", Model.class);

        manager.load("objects/weapons/pistol.g3db", Model.class);
        manager.load("objects/projectiles/pistol_bullet.g3db", Model.class);
        manager.load("objects/projectiles/pistol_bullet_casing.g3db", Model.class);

        manager.load("audio/ui/button_click.ogg", Sound.class);
        manager.load("audio/guns/pistol.ogg", Sound.class);
    }

    public static boolean update() {
        debug("update");

        return manager.update();
    }

    public static void initAssets() {
        debug("initAssets");

        i18NBundle = manager.get("locales/Boxhead");

        largeFont = createFont("DroidSans.ttf", Gdx.graphics.getHeight() / 10, Color.WHITE);
        smallFont = createFont("DroidSans.ttf", Gdx.graphics.getHeight() / 15, Color.WHITE);
        ui = manager.get("ui/ui.atlas", TextureAtlas.class);
        uiSkin = new Skin(Gdx.files.internal("ui/ui.json"));
        uiSkin.addRegions(ui);
        uiSkin.get("large", TextButton.TextButtonStyle.class).font = largeFont;
        uiSkin.get("small", TextButton.TextButtonStyle.class).font = smallFont;
        uiSkin.get("default", CheckBox.CheckBoxStyle.class).font = smallFont;
        uiSkin.get("default", Label.LabelStyle.class).font = smallFont;

        cloudTexture1 = manager.get("cloud_1.png", Texture.class);
        cloudTexture2 = manager.get("cloud_2.png", Texture.class);
        cloudTexture3 = manager.get("cloud_3.png", Texture.class);
        starTexture1 = manager.get("starBig.png", Texture.class);
        starTexture2 = manager.get("starSmall.png", Texture.class);

        mapBase = manager.get("maps/test_scene/test_scene_base.g3dj", Model.class);
        mapObjects = manager.get("maps/test_scene/test_scene_objects.g3dj", Model.class);
        mapDoodads = manager.get("maps/test_scene/test_scene_doodads.g3dj", Model.class);
        map = new Map(mapBase, mapObjects, mapDoodads);

        boxhead = manager.get("objects/entities/boxhead.g3dj", Model.class);

        pistol = manager.get("objects/weapons/pistol.g3db", Model.class);
        pistolBullet = manager.get("objects/projectiles/pistol_bullet.g3db", Model.class);
        pistolBulletCasing = manager.get("objects/projectiles/pistol_bullet_casing.g3db", Model.class);

        button_click = manager.get("audio/ui/button_click.ogg", Sound.class);
        pistolSound = manager.get("audio/guns/pistol.ogg", Sound.class);

        for (int i = 0; i < mapBase.nodes.size; i++) {
            String id = mapBase.nodes.get(i).id;
            ModelInstance instance = new ModelInstance(mapBase, id);
            Node node = instance.getNode(id);

            instance.transform.set(node.globalTransform);
            node.translation.set(0, 0, 0);
            node.scale.set(1, 1, 1);
            node.rotation.idt();
            instance.calculateTransforms();

        }

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
