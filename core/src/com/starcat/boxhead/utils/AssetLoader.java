package com.starcat.boxhead.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.starcat.boxhead.objects.Map;

/**
 * Created by Vincent on 2/10/2015.
 *
 * This class handles all asset loading it makes use of AssetManager
 * to load assets asynchronously so you can display other things such
 * as a splash screen or loading bar. (there wont be a huge lag spike when loading)
 * most textures are also stored in texture atlas's for more efficient loading/rendering
 *
 * If ram is short then I could make more methods such as loadMenuAssets
 * then unload before the next screen (probably not necessary)
 */
public class AssetLoader implements Disposable {

    private static AssetManager manager;

    public static I18NBundle i18NBundle;

    private static BitmapFont largeFont, smallFont;
    private static TextureAtlas ui;
    public static Skin uiSkin;

    public static Map map;
    public static Model mapBase, mapObjects, mapDoodads;
    public static Model player, zombie;

    public static Model pistol;
    public static Model pistolDual;
    public static Model uzi;
    public static Model uziDual;
    public static Model shotgun;
    public static Model shotgunDual;
    public static Model shotgunShort;
    public static Model shotgunShortDual;
    public static Model sniper;
    public static Model sniperCamo;

    public static Model bulletPistol;
    public static Model bulletSniper;
    public static Model bulletUzi;
    public static Model bulletAssaultRifle;
    public static Model casingPistol;
    public static Model casingSniper;
    public static Model casingUzi;
    public static Model casingShotgun;
    public static Model casingAssaultRifle;

    public static Texture cloudTexture1;
    public static Texture cloudTexture2;
    public static Texture cloudTexture3;
    public static Texture starTexture1;
    public static Texture starTexture2;
    public static Texture skinAdventurer;
    public static Texture bloodSpatter;

    public static Sound button_click;
    public static Sound pistolSound;



    public AssetLoader() {
        GameUtils.debug(this, "constructor");
        manager = new AssetManager();
    }



    //only queues assets to be loaded. calling update() actually does the loading
    public void load() {
        GameUtils.debug(this, "load");

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        param.format = Pixmap.Format.RGBA8888;

        manager.load("locales/Boxhead", I18NBundle.class);

        manager.load("cloud_1.png", Texture.class, param);
        manager.load("cloud_2.png", Texture.class, param);
        manager.load("cloud_3.png", Texture.class, param);
        manager.load("starBig.png", Texture.class, param);
        manager.load("starSmall.png", Texture.class, param);
        manager.load("blood_spatter.png", Texture.class, param);

        manager.load("ui/ui.atlas", TextureAtlas.class);
        manager.load("maps/test_scene/test_scene_base.g3dj", Model.class);
        manager.load("maps/test_scene/test_scene_objects.g3dj", Model.class);
        manager.load("maps/test_scene/test_scene_doodads.g3dj", Model.class);

        manager.load("objects/entities/player.g3dj", Model.class);
        manager.load("objects/entities/zombie.g3dj", Model.class);

        manager.load("objects/weapons/pistol.g3dj", Model.class);
        manager.load("objects/weapons/pistol_dual.g3dj", Model.class);
        manager.load("objects/weapons/uzi.g3dj", Model.class);
        manager.load("objects/weapons/uzi_dual.g3dj", Model.class);
        manager.load("objects/weapons/shotgun.g3dj", Model.class);
        manager.load("objects/weapons/shotgun_dual.g3dj", Model.class);
        manager.load("objects/weapons/shotgun_short.g3dj", Model.class);
        manager.load("objects/weapons/shotgun_short_dual.g3dj", Model.class);
        manager.load("objects/weapons/sniper.g3dj", Model.class);
        manager.load("objects/weapons/sniper_camo.g3dj", Model.class);

        manager.load("objects/projectiles/bullet_pistol.g3dj", Model.class);
        manager.load("objects/projectiles/bullet_sniper.g3dj", Model.class);
        manager.load("objects/projectiles/bullet_uzi.g3dj", Model.class);
        manager.load("objects/projectiles/bullet_assault_rifle.g3dj", Model.class);

        manager.load("objects/projectiles/casing_pistol.g3dj", Model.class);
        manager.load("objects/projectiles/casing_sniper.g3dj", Model.class);
        manager.load("objects/projectiles/casing_shotgun.g3dj", Model.class);
        manager.load("objects/projectiles/casing_uzi.g3dj", Model.class);
        manager.load("objects/projectiles/casing_assault_rifle.g3dj", Model.class);

        manager.load("objects/entities/skin_exclusiveZombie.png", Texture.class);

        manager.load("audio/ui/button_click.ogg", Sound.class);
        manager.load("audio/guns/pistol.ogg", Sound.class);
    }

    public boolean update() {
        GameUtils.debug(this, "update");

        return manager.update();
    }

    public void initAssets() {
        GameUtils.debug(this, "initAssets");

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
        bloodSpatter = manager.get("blood_spatter.png", Texture.class);

        mapBase = manager.get("maps/test_scene/test_scene_base.g3dj", Model.class);
        mapObjects = manager.get("maps/test_scene/test_scene_objects.g3dj", Model.class);
        mapDoodads = manager.get("maps/test_scene/test_scene_doodads.g3dj", Model.class);
        map = new Map(mapBase, mapObjects, mapDoodads);

        player = manager.get("objects/entities/player.g3dj", Model.class);
        zombie = manager.get("objects/entities/zombie.g3dj", Model.class);

        skinAdventurer = manager.get("objects/entities/skin_exclusiveZombie.png", Texture.class);

        pistol = manager.get("objects/weapons/pistol.g3dj", Model.class);
        pistolDual = manager.get("objects/weapons/pistol_dual.g3dj", Model.class);
        uzi = manager.get("objects/weapons/uzi.g3dj", Model.class);
        uziDual = manager.get("objects/weapons/uzi_dual.g3dj", Model.class);
        shotgun = manager.get("objects/weapons/shotgun.g3dj", Model.class);
        shotgunDual = manager.get("objects/weapons/shotgun_dual.g3dj", Model.class);
        shotgunShort = manager.get("objects/weapons/shotgun_short.g3dj", Model.class);
        shotgunShortDual = manager.get("objects/weapons/shotgun_short_dual.g3dj", Model.class);
        sniper = manager.get("objects/weapons/sniper.g3dj", Model.class);
        sniperCamo = manager.get("objects/weapons/sniper_camo.g3dj", Model.class);

        bulletPistol = manager.get("objects/projectiles/bullet_pistol.g3dj", Model.class);
        bulletSniper = manager.get("objects/projectiles/bullet_sniper.g3dj", Model.class);
        bulletUzi = manager.get("objects/projectiles/bullet_uzi.g3dj", Model.class);
        bulletAssaultRifle = manager.get("objects/projectiles/bullet_assault_rifle.g3dj", Model.class);

        casingPistol = manager.get("objects/projectiles/casing_pistol.g3dj", Model.class);
        casingSniper = manager.get("objects/projectiles/casing_sniper.g3dj", Model.class);
        casingShotgun = manager.get("objects/projectiles/casing_shotgun.g3dj", Model.class);
        casingUzi = manager.get("objects/projectiles/casing_uzi.g3dj", Model.class);
        casingAssaultRifle = manager.get("objects/projectiles/casing_assault_rifle.g3dj", Model.class);

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
    private BitmapFont createFont(String font, int size, Color color) {
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

    public AssetManager getManager() {
        return manager;
    }

    @Override
    public void dispose() {
        manager.dispose();
    }

}