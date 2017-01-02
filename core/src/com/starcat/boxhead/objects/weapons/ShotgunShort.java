package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/19/2016.
 */

public final class ShotgunShort extends Gun {

    public ShotgunShort(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.shotgunShort);
        bulletCasingModel = AssetLoader.casingShotgun;
        sound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire";
        walkAnimation = "walk_single_wield";
        poseAnimation = "pose_single_wield";
        playerFireAnimation = "shoot_single_wield";

        bulletTranslation = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SHOTGUN_CASING_EXPULSION_IMPULSE);

        damage = 30;
        accuracy = 5;
        bulletSpeed = .1f;
        autofire = true;
        rateOfFire = 1;
    }
}
