package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.Player;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 12/19/2016.
 */

public final class SniperCamo extends Gun {

    public SniperCamo(Player player) {
        super(player);

        modelInstance = new ModelInstance(AssetLoader.sniperCamo);
        bulletModel = AssetLoader.bulletSniper;
        bulletCasingModel = AssetLoader.casingSniper;
        fireSound = AssetLoader.pistolSound;
        animationController = new AnimationController(modelInstance);
        animationController.allowSameAnimation = true;
        fireAnimation = "fire";
        walkAnimation = "walk_large_weapon";
        playerFireAnimation = "shoot_large_weapon";

        bulletTranslation = new Vector3(WeaponConstants.SNIPER_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.SNIPER_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.SNIPER_CASING_EXPULSION_IMPULSE);

        damage = 100;
        accuracy = 1;
        bulletSpeed = .2f;
        rateOfFire = 2;
    }
}
