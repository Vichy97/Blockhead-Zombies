package com.starcat.boxhead.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g3d.Model;
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
 */
public final class AssetLoader implements Disposable {

    private static AssetManager manager;

    public static I18NBundle i18NBundle;

    private static BitmapFont largeFont, smallFont, verySmallFont, menuFont, menuLabelFont;
    public static BitmapFont tinyFont;
    private static TextureAtlas ui;
    public static TextureAtlas textures;
    public static Skin uiSkin;

    public static Model player, zombie;

    public static Map map1;
    public static Model mapBase, mapObjects, mapDoodads;
    public static Map map2;
    public static Model map2Base, map2Objects;

    public static Map[] maps;

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

    public static Texture skinAdventurer;
    public static Texture skinMan;
    public static Texture skinManAlt;
    public static Texture skinWoman;
    public static Texture skinWomanAlt;
    public static Texture skinScientist;
    public static Texture skinPolice;
    public static Texture skinSoldier;
    public static Texture skinCowboy;
    public static Texture skinZombie;
    public static Texture skinDummy;

    public static Texture[] playerSkins;

    public static Texture libgdxLogo;
    public static Texture starcatLogo;

    public static Sound button_click;
    public static Sound pistolSound;



    public AssetLoader() {
        GameUtils.debug(this, "constructor");
        manager = new AssetManager();
    }



    public void loadSplashScreen() {
        manager.load("libgdx_logo.png",Texture.class);
        manager.load("starcat_logo.png",Texture.class);
        manager.finishLoading();
        libgdxLogo = manager.get("libgdx_logo.png", Texture.class);
        starcatLogo = manager.get("starcat_logo.png", Texture.class);
    }

    //only queues assets to be loaded. calling update() actually does the loading
    public void load() {
        GameUtils.debug(this, "load");

        TextureLoader.TextureParameter textureParam = new TextureLoader.TextureParameter();
        textureParam.minFilter = Texture.TextureFilter.Linear;
        textureParam.magFilter = Texture.TextureFilter.Linear;
        textureParam.format = Pixmap.Format.RGBA8888;

        FreetypeFontLoader.FreeTypeFontLoaderParameter tinyFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        tinyFont.fontFileName = "fonts/droid_sans.ttf";
        tinyFont.fontParameters.color = Color.BLACK;
        tinyFont.fontParameters.size = Dimensions.getScreenHeight() / 30;
        FreetypeFontLoader.FreeTypeFontLoaderParameter verySmallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        verySmallFont.fontFileName = "fonts/droid_sans.ttf";
        verySmallFont.fontParameters.color = Color.WHITE;
        verySmallFont.fontParameters.size = Dimensions.getScreenHeight() / 20;
        verySmallFont.fontParameters.shadowOffsetX = -3;
        verySmallFont.fontParameters.shadowOffsetY = 3;
        verySmallFont.fontParameters.borderWidth = 2;
        verySmallFont.fontParameters.borderStraight = false;
        verySmallFont.fontParameters.borderWidth = 2;
        FreetypeFontLoader.FreeTypeFontLoaderParameter smallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        smallFont.fontFileName = "fonts/droid_sans.ttf";
        smallFont.fontParameters.color = Color.WHITE;
        smallFont.fontParameters.size = Dimensions.getScreenHeight() / 15;
        smallFont.fontParameters.shadowOffsetX = -3;
        smallFont.fontParameters.shadowOffsetY = 3;
        smallFont.fontParameters.borderWidth = 2;
        smallFont.fontParameters.borderStraight = false;
        smallFont.fontParameters.borderWidth = 2;
        FreetypeFontLoader.FreeTypeFontLoaderParameter largeFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        largeFont.fontFileName = "fonts/droid_sans.ttf";
        largeFont.fontParameters.color = Color.WHITE;
        largeFont.fontParameters.size = Dimensions.getScreenHeight() / 10;
        largeFont.fontParameters.shadowOffsetX = -5;
        largeFont.fontParameters.shadowOffsetY = 5;
        largeFont.fontParameters.borderStraight = false;
        largeFont.fontParameters.borderWidth = 2;
        FreetypeFontLoader.FreeTypeFontLoaderParameter menuFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        menuFont.fontFileName = "fonts/zombie_noize.ttf";
        menuFont.fontParameters.color = Color.WHITE;
        menuFont.fontParameters.size = Dimensions.getScreenHeight() / 6;
        menuFont.fontParameters.shadowOffsetX  = -5;
        menuFont.fontParameters.shadowOffsetY  = 5;
        menuFont.fontParameters.borderWidth = 2;
        FreetypeFontLoader.FreeTypeFontLoaderParameter menuLabelFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        menuLabelFont.fontFileName = "fonts/banana_brick.ttf";
        menuLabelFont.fontParameters.color = new Color(130f / 255f, 0, 0, 1);
        menuLabelFont.fontParameters.size = Dimensions.getScreenHeight() / 7;
        menuLabelFont.fontParameters.shadowOffsetX = -5;
        menuLabelFont.fontParameters.shadowOffsetY = 5;
        menuLabelFont.fontParameters.borderStraight = false;
        menuLabelFont.fontParameters.borderWidth = 5.5f;

        manager.load("locales/Boxhead", I18NBundle.class);

        manager.load("ui/ui.atlas", TextureAtlas.class);
        manager.load("textures.atlas", TextureAtlas.class);

        manager.load("maps/map_1/map_1_base.g3dj", Model.class);
        manager.load("maps/map_1/map_1_objects.g3dj", Model.class);
        manager.load("maps/map_1/map_1_doodads.g3dj", Model.class);


        manager.load("maps/map_2/map_2_base.g3dj", Model.class);
        manager.load("maps/map_2/map_2_objects.g3dj", Model.class);

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

        manager.load("objects/entities/adventurer.png", Texture.class);
        manager.load("objects/entities/man.png", Texture.class);
        manager.load("objects/entities/man_alt.png", Texture.class);
        manager.load("objects/entities/woman.png", Texture.class);
        manager.load("objects/entities/woman_alt.png", Texture.class);
        manager.load("objects/entities/cowboy.png", Texture.class);
        manager.load("objects/entities/soldier.png", Texture.class);
        manager.load("objects/entities/dummy.png", Texture.class);
        manager.load("objects/entities/scientist.png", Texture.class);
        manager.load("objects/entities/police.png", Texture.class);
        manager.load("objects/entities/zombie.png", Texture.class);

        manager.load("audio/ui/button_click.ogg", Sound.class);
        manager.load("audio/guns/pistol.ogg", Sound.class);

        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(new InternalFileHandleResolver()));
        manager.setLoader(BitmapFont.class, new FreetypeFontLoader(new InternalFileHandleResolver()));

        manager.load("large_font", BitmapFont.class, largeFont);
        manager.load("menu_font", BitmapFont.class, menuFont);
        manager.load("small_font", BitmapFont.class, smallFont);
        manager.load("menu_label_font", BitmapFont.class, menuLabelFont);
        manager.load("very_small_font", BitmapFont.class, verySmallFont);
        manager.load("tiny_font", BitmapFont.class, tinyFont);
    }

    public boolean update() {
        GameUtils.debug(this, "update");

        return manager.update();
    }

    public void initAssets() {
        GameUtils.debug(this, "initAssets");

        i18NBundle = manager.get("locales/Boxhead");

        largeFont = manager.get("large_font", BitmapFont.class);
        smallFont = manager.get("small_font", BitmapFont.class);
        menuFont = manager.get("menu_font", BitmapFont.class);
        menuLabelFont = manager.get("menu_label_font", BitmapFont.class);
        verySmallFont = manager.get("very_small_font", BitmapFont.class);
        tinyFont = manager.get("tiny_font", BitmapFont.class);

        ui = manager.get("ui/ui.atlas", TextureAtlas.class);
        uiSkin = new Skin(Gdx.files.internal("ui/ui.json"));
        uiSkin.addRegions(ui);
        uiSkin.get("default", CheckBox.CheckBoxStyle.class).font = smallFont;
        uiSkin.get("large", TextButton.TextButtonStyle.class).font = largeFont;
        uiSkin.get("small", TextButton.TextButtonStyle.class).font = smallFont;
        uiSkin.get("menu", TextButton.TextButtonStyle.class).font = menuFont;
        uiSkin.get("default", Label.LabelStyle.class).font = smallFont;
        uiSkin.get("title", Label.LabelStyle.class).font = menuLabelFont;
        uiSkin.get("large", Label.LabelStyle.class).font = largeFont;
        uiSkin.get("small", Label.LabelStyle.class).font = smallFont;
        uiSkin.get("menu", Label.LabelStyle.class).font = menuFont;
        uiSkin.get("very_small", Label.LabelStyle.class).font = verySmallFont;

        textures = manager.get("textures.atlas", TextureAtlas.class);

        mapBase = manager.get("maps/map_1/map_1_base.g3dj", Model.class);
        mapObjects = manager.get("maps/map_1/map_1_objects.g3dj", Model.class);
        mapDoodads = manager.get("maps/map_1/map_1_doodads.g3dj", Model.class);

        map2Base = manager.get("maps/map_2/map_2_base.g3dj", Model.class);
        map2Objects = manager.get("maps/map_2/map_2_objects.g3dj", Model.class);

        maps = new Map[2];
        maps[0] = map1 = new Map(mapBase, mapObjects, mapDoodads, 30);
        maps[1] = map2 = new Map(map2Base, map2Objects, null, 70);
        maps[1].setTranslation(-60, 0, 60);

        player = manager.get("objects/entities/player.g3dj", Model.class);
        zombie = manager.get("objects/entities/zombie.g3dj", Model.class);

        playerSkins = new Texture[9];
        playerSkins[0] = skinMan = manager.get("objects/entities/man.png", Texture.class);
        playerSkins[1] = skinWoman = manager.get("objects/entities/woman.png", Texture.class);
        playerSkins[2] = skinManAlt = manager.get("objects/entities/man_alt.png", Texture.class);
        playerSkins[3] = skinWomanAlt = manager.get("objects/entities/woman_alt.png", Texture.class);
        playerSkins[4] = skinAdventurer = manager.get("objects/entities/adventurer.png", Texture.class);
        playerSkins[5] = skinCowboy = manager.get("objects/entities/cowboy.png", Texture.class);
        playerSkins[6] = skinPolice = manager.get("objects/entities/police.png", Texture.class);
        playerSkins[7] = skinSoldier = manager.get("objects/entities/soldier.png", Texture.class);
        playerSkins[8] = skinScientist = manager.get("objects/entities/scientist.png", Texture.class);



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

        uiSkin.getDrawable("touchpad_knob").setMinHeight(Dimensions.getScreenHeight() / 6);
        uiSkin.getDrawable("touchpad_knob").setMinWidth(Dimensions.getScreenHeight() / 6);
        uiSkin.getDrawable("touchpad_base").setMinHeight(Dimensions.getScreenWidth() / 6.5f);
        uiSkin.getDrawable("touchpad_base").setMinWidth(Dimensions.getScreenWidth() / 6.5f);
    }

    public AssetManager getManager() {
        return manager;
    }



    @Override
    public void dispose() {
        manager.dispose();
    }

}