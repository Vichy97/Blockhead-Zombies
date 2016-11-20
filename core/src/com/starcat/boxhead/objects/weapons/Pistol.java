package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.starcat.boxhead.objects.entities.EntityManager;
import com.starcat.boxhead.utils.AssetLoader;

/**
 * Created by Vincent on 8/17/2016.
 */
public class Pistol extends Gun {

    public Pistol() {

        modelInstance = new ModelInstance(AssetLoader.pistol);
        bulletModel = AssetLoader.pistolBullet;
        bulletCasingModel = AssetLoader.pistolBulletCasing;
        sound = AssetLoader.pistolSound;

        translation = new Vector3(WeaponConstants.PISTOL_TRANSLATION);
        bulletTranslation = new Vector3(WeaponConstants.PISTOL_BULLET_TRANSLATION);
        bulletCasingTranslation = new Vector3(WeaponConstants.PISTOL_CASING_TRANSLATION);
        bulletCasingExpulsionImpulse = new Vector3(WeaponConstants.PISTOL_CASING_EXPULSION_IMPULSE);

        damage = 20;
        accuracy = 5;
        bulletSpeed = .1f;
        reloadTime = .1f;
    }

}
