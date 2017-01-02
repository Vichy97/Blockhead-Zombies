package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 11/20/2016.
 *
 * this class is used to keep track of translation constants for the
 * weapon models
 */

public final class WeaponConstants {

    public static final Vector3 PISTOL_BULLET_TRANSLATION = new Vector3(-.3f, .487f, .75f);
    public static final Vector3 PISTOL_BULLET_TRANSLATION_ALT = new Vector3(.3f, .487f, .75f);
    public static final Vector3 PISTOL_CASING_TRANSLATION = new Vector3(-.3f, .487f, .567f);
    public static final Vector3 PISTOL_CASING_TRANSLATION_ALT = new Vector3(-.3f, .487f, .567f);
    public static final Vector3 PISTOL_CASING_EXPULSION_IMPULSE = new Vector3(2, 2, .2f);

    public static final Vector3 UZI_BULLET_TRANSLATION = new Vector3(-.3f, .52f, .9f);
    public static final Vector3 UZI_BULLET_TRANSLATION_ALT = new Vector3(.3f, .52f, .9f);
    public static final Vector3 UZI_CASING_TRANSLATION = new Vector3(-.3f, .52f, .5f);
    public static final Vector3 UZI_CASING_TRANSLATION_ALT = new Vector3(.3f, .52f, .5f);
    public static final Vector3 UZI_CASING_EXPULSION_IMPULSE = new Vector3(2, 2, .2f);

    public static final Vector3 SHOTGUN_BULLET_TRANSLATION = new Vector3(-.3f, .52f, 1f);
    public static final Vector3 SHOTGUN_BULLET_TRANSLATION_ALT = new Vector3(.3f, .52f, 1f);
    public static final Vector3 SHOTGUN_CASING_TRANSLATION = new Vector3(-.3f, .52f, .56f);
    public static final Vector3 SHOTGUN_CASING_TRANSLATION_ALT = new Vector3(.3f, .52f, .56f);
    public static final Vector3 SHOTGUN_CASING_EXPULSION_IMPULSE = new Vector3(2, 2, .2f);

    public static final Vector3 SNIPER_BULLET_TRANSLATION = new Vector3(-.29f, .485f, 1.55f);
    public static final Vector3 SNIPER_CASING_TRANSLATION = new Vector3(-.29f, .485f, .675f);
    public static final Vector3 SNIPER_CASING_EXPULSION_IMPULSE = new Vector3(2, 2, .2f);
}
