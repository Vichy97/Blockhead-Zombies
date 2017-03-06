package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/19/2016.
 */

public final class ShotgunDual extends DualWieldGun {
    public ShotgunDual(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.shotgunDual);
        bulletCasingModel = AssetLoader.casingShotgun;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire_right";
        fireAnimationAlt = "fire_left";
        walkAnimation = "walk_dual_wield";
        poseAnimation = "pose_dual_wield";
        playerFireAnimation = "shoot_dual_wield_right";
        playerFireAnimationAlt = "shoot_dual_wield_left";

        silhouette = new Sprite(AssetLoader.textures.findRegion("shotgun_dual"));
        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_shotgun"));

        silhouette.setSize(Gdx.graphics.getWidth() / 1920 * silhouette.getRegionWidth(), Gdx.graphics.getHeight() / 1080 * silhouette.getRegionHeight());
        silhouette.setX(Gdx.graphics.getWidth() / 2 - silhouette.getWidth() / 2);
        silhouette.setY(Gdx.graphics.getHeight() / 10);

        ammoSilhouette.setSize(Gdx.graphics.getWidth() / 1920 * ammoSilhouette.getRegionWidth(), Gdx.graphics.getHeight() / 1080 * ammoSilhouette.getRegionHeight());

        bulletTranslation = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION);
        bulletTranslationAlt = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION_ALT);
        bulletCasingTranslation = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION);
        bulletCasingTranslationAlt = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION_ALT);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SHOTGUN_CASING_EXPULSION_IMPULSE);

        damage = 30;
        accuracy = 5;
        bulletSpeed = .1f;
        rateOfFire = .5f;
        reloadTime = 3f;
        clipSize = 16;
        ammoInClip = 16;
        extraClips = 100;
        autofire = true;
    }
}
