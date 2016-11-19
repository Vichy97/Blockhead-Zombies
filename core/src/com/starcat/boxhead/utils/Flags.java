package com.starcat.boxhead.utils;

/**
 * Created by Vincent on 8/19/2016.
 *
 * flags used for collision filters
 */
public class Flags {
    public static final int GROUND_FLAG = 1 << 7;
    public static final int OBJECT_FLAG = 1 << 2;
    public static final int ENTITY_FLAG = 1 << 3;
    public static final int BULLET_FLAG = 1 << 4;
    public static final int BULLET_CASING_FLAG = 1 << 5;
    public static final int SHOULD_POOL_FLAG = 1 << 6;
    public static final int ENEMY_FLAG = 1 << 7;
}
