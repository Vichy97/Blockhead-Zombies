package com.vincent.Weapons;

/**
 * Created by Vincent on 8/30/2015.
 *
 * An abstract class to define what a gun should do
 */
public abstract class Gun {
    protected int damage;
    protected float fireRate;
    protected float bulletSpeed;

    public abstract void fire();

    public float getDamage() {
        return damage;
    }

    public float getFireRate() {
        return fireRate;
    }
}
