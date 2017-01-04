package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;

/**
 * Created by Vincent on 12/12/2016.
 */

public final class UziDual extends DualWieldGun {

    public UziDual(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.uziDual);
        bulletModel = AssetLoader.bulletUzi;
        bulletCasingModel = AssetLoader.casingUzi;
        sound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire_right";
        fireAnimationAlt = "fire_left";
        walkAnimation = "walk_dual_wield";
        poseAnimation = "pose_dual_wield";
        playerFireAnimation = "shoot_dual_wield_right";
        playerFireAnimationAlt = "shoot_dual_wield_left";

        silhouette = new Sprite(AssetLoader.textures.findRegion("uzi_dual"));
        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_uzi"));

        silhouette.setSize(Dimensions.getGameScreenWidthRatio() * silhouette.getRegionWidth(), Dimensions.getGameScreenHeightRatio() * silhouette.getRegionHeight());
        silhouette.setX(Gdx.graphics.getWidth() / 2 - silhouette.getWidth() / 2);
        silhouette.setY(Gdx.graphics.getHeight() / 7);

        ammoSilhouette.setSize(Gdx.graphics.getWidth() / 1920 * ammoSilhouette.getRegionWidth(), Gdx.graphics.getHeight() / 1080 * ammoSilhouette.getRegionHeight());

        bulletTranslation = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
        bulletTranslationAlt = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION_ALT);
        bulletCasingTranslation = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
        bulletCasingTranslationAlt = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION_ALT);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.UZI_CASING_EXPULSION_IMPULSE);

        damage = 20;
        accuracy = 5;
        bulletSpeed = .2f;
        rateOfFire = .05f;
        reloadTime = 2;
        clipSize = 30;
        ammoInClip = 30;
        extraClips = 3;
        autofire = true;
    }
}
