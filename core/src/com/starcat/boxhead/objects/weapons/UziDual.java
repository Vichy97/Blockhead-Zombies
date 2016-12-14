package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/12/2016.
 */

public class UziDual extends Gun {

    public UziDual() {

        modelInstance = new ModelInstance(AssetLoader.uziDual);
        bulletModel = AssetLoader.bulletUzi;
        bulletCasingModel = AssetLoader.casingUzi;
        sound = AssetLoader.pistolSound;
        shootAnimationController = new AnimationController(modelInstance);
        shootAnimationController.allowSameAnimation = true;
        fireAnimation = "fire_right";
        altFireAnimation = "fire_left";

        bulletTranslationAlt = null;
        bulletTranslation = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
        bulletTranslationAlt = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION_ALT);
        bulletCasingTranslation = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.UZI_CASING_EXPULSION_IMPULSE);

        damage = 20;
        accuracy = 10;
        bulletSpeed = .15f;
        reloadTime = .05f;
        autofire = true;
    }

    @Override
    public void fire() {
        super.fire();
    }
}
