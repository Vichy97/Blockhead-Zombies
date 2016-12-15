package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/12/2016.
 */

public class Uzi extends Gun {

    public Uzi(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.uzi);
        bulletModel = AssetLoader.bulletUzi;
        bulletCasingModel = AssetLoader.casingUzi;
        sound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire";
        fireAnimationAlt = "fire";

        bulletTranslationAlt = null;
        bulletTranslation = new Vector3(WeaponConstants.UZI_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.UZI_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.UZI_CASING_EXPULSION_IMPULSE);

        damage = 20;
        accuracy = 10;
        bulletSpeed = .15f;
        reloadTime = .1f;
        autofire = true;
    }
}
