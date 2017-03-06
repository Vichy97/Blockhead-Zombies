package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;

/**
 * Created by Vincent on 12/11/2016.
 */

public final class PistolDual extends DualWieldGun {

    public PistolDual(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.pistolDual);
        bulletModel = AssetLoader.bulletPistol;
        bulletCasingModel = AssetLoader.casingPistol;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire_right";
        fireAnimationAlt = "fire_left";
        walkAnimation = "walk_dual_wield";
        poseAnimation = "pose_dual_wield";
        playerFireAnimation = "shoot_dual_wield_right";
        playerFireAnimationAlt = "shoot_dual_wield_left";

        silhouette = new Sprite(AssetLoader.textures.findRegion("pistol_dual"));
        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_pistol"));

        silhouette.setSize(Dimensions.scaleWidth(silhouette.getRegionWidth()), Dimensions.scaleHeight(silhouette.getRegionHeight()));
        silhouette.setX(Gdx.graphics.getWidth() / 2 - silhouette.getWidth() / 2);
        silhouette.setY(Gdx.graphics.getHeight() / 7);
        ammoSilhouette.setSize(Dimensions.scaleWidth(ammoSilhouette.getRegionWidth()), Dimensions.scaleHeight(ammoSilhouette.getRegionHeight()));

        bulletTranslation = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION);
        bulletTranslationAlt = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION_ALT);
        bulletCasingTranslation = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION);
        bulletCasingTranslationAlt = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION_ALT);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.PISTOL_CASING_EXPULSION_IMPULSE);

        damage = 34;
        accuracy = 5;
        bulletSpeed = .1f;
        rateOfFire = .2f;
        reloadTime = 2;
        clipSize = 14;
        ammoInClip = 14;
        extraClips = 3;
        autofire = true;
    }

    @Override
    public void fire() {
        super.fire();
    }
}
