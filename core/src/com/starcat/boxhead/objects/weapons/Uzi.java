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

        modelInstance = new ModelInstance(AssetLoader.uzi);
        bulletModel = AssetLoader.bulletUzi;
        bulletCasingModel = AssetLoader.casingUzi;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire";
        walkAnimation = "walk_single_wield";
        poseAnimation = "pose_single_wield";
        playerFireAnimation = "shoot_single_wield";

        silhouette = new Sprite(AssetLoader.textures.findRegion("uzi"));
        ammoSilhouette = new Sprite(AssetLoader.textures.findRegion("ammo_uzi"));

        silhouette.setSize(Dimensions.scaleWidth(silhouette.getRegionWidth()), Dimensions.scaleHeight(silhouette.getRegionHeight()));
        silhouette.setX(Dimensions.getHalfScreenWidth() - silhouette.getWidth() / 2);
        silhouette.setY(Dimensions.getScreenHeight() / 7);

        ammoSilhouette.setSize(Dimensions.scaleWidth(ammoSilhouette.getRegionWidth()), Dimensions.scaleHeight(ammoSilhouette.getRegionHeight()));

        bulletTranslation = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.UZI_CASING_EXPULSION_IMPULSE);

        damage = 20;
        accuracy = 10;
        bulletSpeed = .15f;
        rateOfFire = .1f;
        autofire = true;
        reloadTime = 2;
        clipSize = 20;
        ammoInClip = 20;
        extraClips = 3;
        autofire = true;
    }
}
