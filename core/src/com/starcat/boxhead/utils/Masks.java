package com.starcat.boxhead.utils;

/**
 * Created by Vincent on 10/21/2016.
 *
 * collision masks
 */

public class Masks {
    public static final int PLAYER_MASK = Flags.ENTITY_FLAG | Flags.BULLET_FLAG | Flags.OBJECT_FLAG;
    public static final int ENEMY_MASK = Flags.ENTITY_FLAG | Flags.BULLET_FLAG | Flags.OBJECT_FLAG;
    public static final int BULLET_MASK = Flags.ENTITY_FLAG | Flags.OBJECT_FLAG;
    public static final int BULLET_CASING_MASK = Flags.OBJECT_FLAG | Flags.GROUND_FLAG | Flags.BULLET_CASING_FLAG;
}
