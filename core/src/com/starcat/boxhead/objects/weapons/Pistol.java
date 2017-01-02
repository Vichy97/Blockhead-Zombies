package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 8/17/2016.
 */
public final class Pistol extends Gun {

    public Pistol(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.pistol);
        bulletModel = AssetLoader.bulletPistol;
        bulletCasingModel = AssetLoader.casingPistol;
        sound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire";
        walkAnimation = "walk_single_wield";
        poseAnimation = "pose_single_wield";
        playerFireAnimation = "shoot_single_wield";

        bulletTranslation = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.PISTOL_CASING_EXPULSION_IMPULSE);

        damage = 34;
        accuracy = 5;
        bulletSpeed = .1f;
        rateOfFire = .4f;
        autofire = true;
    }

}
