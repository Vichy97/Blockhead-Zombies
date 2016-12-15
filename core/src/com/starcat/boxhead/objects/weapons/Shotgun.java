package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/13/2016.
 */

public class Shotgun extends Gun {

    public Shotgun(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.shotgun);
        bulletModel = null;
        bulletCasingModel = AssetLoader.casingShotgun;
        sound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire";
        fireAnimationAlt = null;

        bulletTranslationAlt = null;
        bulletTranslation = new Vector3(WeaponConstants.SHOTGUN_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.SHOTGUN_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SHOTGUN_CASING_EXPULSION_IMPULSE);

        damage = 30;
        accuracy = 5;
        bulletSpeed = .1f;
        reloadTime = 2;
    }
}
