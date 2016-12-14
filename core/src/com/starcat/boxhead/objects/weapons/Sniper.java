package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/13/2016.
 */

public class Sniper extends Gun {

    public Sniper() {
        modelInstance = new ModelInstance(AssetLoader.sniper);
        bulletModel = AssetLoader.bulletSniper;
        bulletCasingModel = AssetLoader.casingSniper;
        sound = AssetLoader.pistolSound;
        shootAnimationController = new AnimationController(modelInstance);
        shootAnimationController.allowSameAnimation = true;
        fireAnimation = "fire";
        altFireAnimation = null;

        bulletTranslationAlt = null;
        bulletTranslation = new Vector3(WeaponConstants.SNIPER_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.SNIPER_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SNIPER_CASING_EXPULSION_IMPULSE);

        damage = 100;
        accuracy = 1;
        bulletSpeed = .2f;
        reloadTime = 5;
    }
}
