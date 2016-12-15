package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/11/2016.
 */

public class PistolDual extends Gun {

    public PistolDual(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.pistolDual);
        bulletModel = AssetLoader.bulletPistol;
        bulletCasingModel = AssetLoader.casingPistol;
        sound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire_right";
        fireAnimationAlt = "fire_left";

        bulletTranslation = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION);
        bulletTranslationAlt = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION_ALT);
        bulletCasingTranslation = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.PISTOL_CASING_EXPULSION_IMPULSE);

        damage = 34;
        accuracy = 5;
        bulletSpeed = .1f;
        reloadTime = .05f;
    }

    @Override
    public void fire() {
        super.fire();
    }
}
