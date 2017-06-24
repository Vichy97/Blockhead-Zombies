package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;

/**
 * Created by Vincent on 8/17/2016.
 */
public final class Pistol extends Gun {

    public Pistol(Player player) {
        super(player);

        damage = 34;
        accuracy = 5;
        bulletSpeed = .1f;
        rateOfFire = .4f;
        reloadTime = 2;
        clipSize = 7;
        ammoInClip = clipSize;
        extraClips = 3;
        autofire = true;


        if ((flags & WeaponConstants.FLAG_DUAL) == WeaponConstants.FLAG_DUAL) {
            if ((flags & WeaponConstants.FLAG_SILENCED) == WeaponConstants.FLAG_SILENCED) {
                //TODO:
            } else {
                modelInstance = new ModelInstance(AssetLoader.pistolDual);
            }

            fireAnimation = "fire_right";
            fireAnimationAlt = "fire_left";
            walkAnimation = "walk_single_wield";
            poseAnimation = "walk_single_wield";
            playerFireAnimation = "shoot_dual_wield_right";
            playerFireAnimationAlt = "shoot_dual_wield_right";

            silhouette = new Sprite(AssetLoader.textures.findRegion("pistol_dual"));

            rateOfFire *= .5f;
            reloadTime *= 1.5;
            clipSize *= 2;
            ammoInClip = clipSize;

            bulletTranslation = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION);
            bulletTranslationAlt = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION_ALT);
            bulletCasingTranslation = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION);
            bulletCasingTranslationAlt = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION_ALT);
            bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.PISTOL_CASING_EXPULSION_IMPULSE);
        } else {
            if ((flags & WeaponConstants.FLAG_SILENCED) == WeaponConstants.FLAG_SILENCED) {
                //TODO:
            } else {
                modelInstance = new ModelInstance(AssetLoader.pistol);
            }

            fireAnimation = "fire";
            fireAnimationAlt = "fire";
            walkAnimation = "walk_single_wield";
            poseAnimation = "pose_single_wield";
            playerFireAnimation = "shoot_single_wield";
            playerFireAnimationAlt = "shoot_single_wield";

            silhouette = new Sprite(AssetLoader.textures.findRegion("pistol"));

            bulletTranslation = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION);
            bulletTranslationAlt = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION);
            bulletCasingTranslation = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION);
            bulletCasingTranslationAlt = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION);
            bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.PISTOL_CASING_EXPULSION_IMPULSE);
        }

        bulletModel = AssetLoader.bulletPistol;
        bulletCasingModel = AssetLoader.casingPistol;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;

        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_pistol"));
        ammoSilhouette.setSize(Dimensions.scaleWidth(ammoSilhouette.getRegionWidth()), Dimensions.scaleHeight(ammoSilhouette.getRegionHeight()));

        silhouette.setSize(Dimensions.scaleWidth(silhouette.getRegionWidth()), Dimensions.scaleHeight(silhouette.getRegionHeight()));
        silhouette.setX(Dimensions.getHalfScreenWidth() - silhouette.getWidth() / 2);
        silhouette.setY(Dimensions.getScreenHeight() / 7);
    }
}
