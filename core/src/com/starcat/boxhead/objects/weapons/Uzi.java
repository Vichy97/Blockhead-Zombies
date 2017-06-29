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

        flags |= WeaponConstants.FLAG_DUAL;

        if ((flags & WeaponConstants.FLAG_DUAL) == WeaponConstants.FLAG_DUAL) {
            if ((flags & WeaponConstants.FLAG_SILENCED) == WeaponConstants.FLAG_SILENCED) {
                if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziShortSilencedGoldDual);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uziShortSilencedDual);
                    }
                } else {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziSilencedGoldDual);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uziSilencedDual);
                    }
                }
            } else {
                if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziShortGoldDual);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uziShortDual);
                    }
                } else {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziGoldDual);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uziDual);
                    }
                }
            }

            fireAnimation = "fire_right";
            fireAnimationAlt = "fire_left";
            walkAnimation = "walk_dual_wield";
            poseAnimation = "pose_dual_wield";
            playerFireAnimation = "shoot_dual_wield_right_small";
            playerFireAnimationAlt = "shoot_dual_wield_left_small";

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
                if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziShortSilencedGold);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uziShortSilenced);
                    }
                } else {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziSilencedGold);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uziSilenced);
                    }
                }
            } else {
                if ((flags & WeaponConstants.FLAG_SHORT) == WeaponConstants.FLAG_SHORT) {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziShortGold);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uziShort);
                    }
                } else {
                    if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
                        modelInstance = new ModelInstance(AssetLoader.uziGold);
                    } else {
                        modelInstance = new ModelInstance(AssetLoader.uzi);
                    }
                }
            }

            fireAnimation = "fire";
            fireAnimationAlt = "fire";
            walkAnimation = "walk_single_wield";
            poseAnimation = "pose_single_wield";
            playerFireAnimation = "shoot_single_wield_small";
            playerFireAnimationAlt = "shoot_single_wield_small";

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
