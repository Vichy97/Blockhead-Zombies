package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;

/**
 * Created by Vincent on 12/13/2016.
 */

public final class Shotgun extends Gun {

    public Shotgun(Player player) {
        super(player);

        damage = 20;
        accuracy = 10;
        bulletSpeed = .15f;
        rateOfFire = 1;
        reloadTime = 2;
        clipSize = 6;
        ammoInClip = 6;
        extraClips = 3;
        autofire = false;

        if ((flags & WeaponConstants.FLAG_DUAL) == WeaponConstants.FLAG_DUAL) {
            if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                modelInstance = new ModelInstance(AssetLoader.shotgunShortDual);
                accuracy *= .5f;
            } else {
                modelInstance = new ModelInstance(AssetLoader.shotgunDual);
            }

            fireAnimation = "fire_right";
            fireAnimationAlt = "fire_left";
            walkAnimation = "walk_dual_wield";
            poseAnimation = "pose_dual_wield";
            playerFireAnimation = "shoot_dual_wield_right";
            playerFireAnimationAlt = "shoot_dual_wield_left";

            silhouette = new Sprite(AssetLoader.textures.findRegion("shotgun_dual"));

            bulletTranslation = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION);
            bulletTranslationAlt = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION_ALT);
            bulletCasingTranslation = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION);
            bulletCasingTranslationAlt = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION_ALT);
            bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SHOTGUN_CASING_EXPULSION_IMPULSE);

            rateOfFire *= .5f;
            reloadTime *= 1.5;
            clipSize *= 2;
            ammoInClip = clipSize;
        } else {
            if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                modelInstance = new ModelInstance(AssetLoader.shotgunShort);
                accuracy *= .5f;
            } else {
                modelInstance = new ModelInstance(AssetLoader.shotgun);
            }

            fireAnimation = "fire";
            fireAnimationAlt = "fire";
            walkAnimation = "walk_single_wield";
            poseAnimation = "pose_single_wield";
            playerFireAnimation = "shoot_single_wield";
            playerFireAnimationAlt = "shoot_single_wield";

            silhouette = new Sprite(AssetLoader.textures.findRegion("shotgun"));

            bulletTranslation = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION);
            bulletTranslationAlt = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION);
            bulletCasingTranslation = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION);
            bulletCasingTranslationAlt = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION);
            bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SHOTGUN_CASING_EXPULSION_IMPULSE);
        }

        bulletModel = AssetLoader.bulletShotgun;
        bulletCasingModel = AssetLoader.casingShotgun;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;

        silhouette.setSize(Dimensions.scaleWidth(silhouette.getRegionWidth()), Dimensions.scaleHeight(silhouette.getRegionHeight()));
        silhouette.setX(Dimensions.getHalfScreenWidth() - silhouette.getWidth() / 2);
        silhouette.setY(Dimensions.getScreenHeight() / 7);

        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_shotgun"));
        ammoSilhouette.setSize(Dimensions.scaleWidth(ammoSilhouette.getRegionWidth()), Dimensions.scaleHeight(ammoSilhouette.getRegionHeight()));
    }

    @Override
    public void fire() {
        fire(5);
    }
}
