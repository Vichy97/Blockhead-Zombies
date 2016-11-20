package com.starcat.boxhead.objects.weapons;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 11/20/2016.
 *
 * this class is used to keep track of translation constants for the
 * weapon models (player position can't be used because the gun will be
 * at his feet instead of in his hand)
 */

public class WeaponConstants {

    public static final Vector3 PISTOL_TRANSLATION = new Vector3(-.3f, .35f, .6f);
    public static final Vector3 PISTOL_BULLET_TRANSLATION = new Vector3(-.3f, .487f, .75f);
    public static final Vector3 PISTOL_CASING_TRANSLATION = new Vector3(-.3f, .487f, .567f);
    public static final Vector3 PISTOL_CASING_EXPULSION_IMPULSE = new Vector3(2, 2, .2f);

}
