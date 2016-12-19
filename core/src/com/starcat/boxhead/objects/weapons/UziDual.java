package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/12/2016.
 */

public class UziDual extends Gun {

    public UziDual(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.uziDual);
        bulletModel = AssetLoader.bulletUzi;
        bulletCasingModel = AssetLoader.casingUzi;
        sound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire_right";
        fireAnimationAlt = "fire_left";
        walkAnimation = "walk_dual_wield";
        poseAnimation = "pose_dual_wield";
        playerFireAnimation = "shoot_dual_wield_right";
        playerFireAnimationAlt = "shoot_dual_wield_left";

        bulletTranslation = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
        bulletTranslationAlt = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION_ALT);
        bulletCasingTranslation = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
        bulletCasingTranslationAlt = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION_ALT);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.UZI_CASING_EXPULSION_IMPULSE);

        damage = 20;
        accuracy = 5;
        bulletSpeed = .2f;
        reloadTime = .05f;
        autofire = true;
    }

    @Override
    public void fire() {
        super.fire();
    }
}
