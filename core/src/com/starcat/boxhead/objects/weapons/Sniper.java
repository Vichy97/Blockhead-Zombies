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

public final class Sniper extends Gun {

    public Sniper(Player player) {
        super(player);

        damage = 100;
        accuracy = 1;
        bulletSpeed = .2f;
        rateOfFire = 1f;
        autofire = false;
        reloadTime = 2;
        clipSize = 3;
        ammoInClip = clipSize;
        extraClips = 3;

        if ((flags & WeaponConstants.FLAG_ALT_SKIN) == WeaponConstants.FLAG_ALT_SKIN) {
            modelInstance = new ModelInstance(AssetLoader.sniperCamo);
        } else {
            modelInstance = new ModelInstance(AssetLoader.sniper);
        }

        bulletModel = AssetLoader.bulletSniper;
        bulletCasingModel = AssetLoader.casingSniper;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire";
        fireAnimationAlt = "fire";
        walkAnimation = "walk_large_weapon";
        playerFireAnimation = "shoot_large_weapon";
        playerFireAnimationAlt = "shoot_large_weapon";
        poseAnimation = "pose_large_weapon";

        silhouette = new Sprite(AssetLoader.textures.findRegion("sniper"));
        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_sniper"));
        ammoSilhouette.setSize(Dimensions.scaleWidth(ammoSilhouette.getRegionWidth()), Dimensions.scaleHeight(ammoSilhouette.getRegionHeight()));

        silhouette.setSize(Dimensions.scaleWidth(silhouette.getRegionWidth()), Dimensions.scaleHeight(silhouette.getRegionHeight()));
        silhouette.setX(Dimensions.getHalfScreenWidth() - silhouette.getWidth() / 2);
        silhouette.setY(Dimensions.getScreenHeight() / 7);

        bulletTranslation = new Vector3(WeaponConstants.SNIPER_BULLET_TRANSLATION);
        bulletTranslationAlt = new Vector3(WeaponConstants.SNIPER_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.SNIPER_CASING_TRANSLATION);
        bulletCasingTranslationAlt = new Vector3(WeaponConstants.SNIPER_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SNIPER_CASING_EXPULSION_IMPULSE);
    }
}
