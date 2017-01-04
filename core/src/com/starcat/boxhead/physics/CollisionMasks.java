package com.starcat.boxhead.physics;

/**
 * Created by Vincent on 10/21/2016.
 *
 * collision masks
 */

public final class CollisionMasks {
    public static final int PLAYER_MASK = CollisionFlags.ENTITY_FLAG | CollisionFlags.BULLET_FLAG | CollisionFlags.OBJECT_FLAG;
    public static final int ENEMY_MASK = CollisionFlags.ENTITY_FLAG | CollisionFlags.BULLET_FLAG | CollisionFlags.OBJECT_FLAG;
    public static final int BULLET_MASK = CollisionFlags.ENTITY_FLAG | CollisionFlags.OBJECT_FLAG;
    public static final int BULLET_CASING_MASK = CollisionFlags.OBJECT_FLAG | CollisionFlags.GROUND_FLAG | CollisionFlags.BULLET_CASING_FLAG | ENEMY_MASK;
}
