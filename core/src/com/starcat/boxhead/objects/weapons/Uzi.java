package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;

/**
 * Created by Vincent on 12/12/2016.
 */

public final class Uzi extends Gun {

    public Uzi(Player player) {
        super(player);

        damage = 20;
        accuracy = 10;
        bulletSpeed = .15f;
        rateOfFire = .1f;
        autofire = true;
        reloadTime = 2;
        clipSize = 20;
        ammoInClip = clipSize;
        extraClips = 3;
        autofire = true;

        if ((flags & WeaponConstants.FLAG_DUAL) == WeaponConstants.FLAG_DUAL) {
            if ((flags & WeaponConstants.FLAG_SILENCED) == WeaponConstants.FLAG_SILENCED) {
                //TODO: more models
                if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {

                    } else {

                    }
                } else {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {

                    } else {

                    }
                }
            } else {
                modelInstance = new ModelInstance(AssetLoader.uziDual);
            }

            fireAnimation = "fire_right";
            fireAnimationAlt = "fire_left";
            walkAnimation = "walk_dual_wield";
            poseAnimation = "pose_dual_wield";
            playerFireAnimation = "shoot_dual_wield_right";
            playerFireAnimationAlt = "shoot_dual_wield_left";

            silhouette = new Sprite(AssetLoader.textures.findRegion("uzi_dual"));

            rateOfFire *= .5f;
            reloadTime *= 1.5;
            clipSize *= 2;
            ammoInClip = clipSize;

            bulletTranslation = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
            bulletTranslationAlt = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION_ALT);
            bulletCasingTranslation = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
            bulletCasingTranslationAlt = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION_ALT);
            bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.UZI_CASING_EXPULSION_IMPULSE);
        } else {
            if ((flags & WeaponConstants.FLAG_SILENCED) == WeaponConstants.FLAG_SILENCED) {
                //TODO: more models
                if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {

                    } else {

                    }
                } else {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {

                    } else {

                    }
                }
            } else {
                modelInstance = new ModelInstance(AssetLoader.uzi);
            }

            fireAnimation = "fire";
            fireAnimationAlt = "fire";
            walkAnimation = "walk_single_wield";
            poseAnimation = "pose_single_wield";
            playerFireAnimation = "shoot_single_wield";
            playerFireAnimationAlt = "shoot_single_wield";

            silhouette = new Sprite(AssetLoader.textures.findRegion("uzi"));

            bulletTranslation = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
            bulletTranslationAlt = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
            bulletCasingTranslation = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
            bulletCasingTranslationAlt = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
            bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.UZI_CASING_EXPULSION_IMPULSE);
        }

        bulletModel = AssetLoader.bulletUzi;
        bulletCasingModel = AssetLoader.casingUzi;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;

        silhouette.setSize(Dimensions.scaleWidth(silhouette.getRegionWidth()), Dimensions.scaleHeight(silhouette.getRegionHeight()));
        silhouette.setX(Dimensions.getHalfScreenWidth() - silhouette.getWidth() / 2);
        silhouette.setY(Dimensions.getScreenHeight() / 7);

        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_uzi"));
        ammoSilhouette.setSize(Dimensions.scaleWidth(ammoSilhouette.getRegionWidth()), Dimensions.scaleHeight(ammoSilhouette.getRegionHeight()));
    }
}
