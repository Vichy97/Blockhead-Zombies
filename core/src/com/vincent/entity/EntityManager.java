package com.vincent.entity;

import java.util.ArrayList;

/**
 * Created by Vincent on 7/8/2015.
 */
public class EntityManager {

    private static ArrayList<Entity> entities = new ArrayList<>();

    public static void updateEntities() {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update();
        }
    }

    public static void spawnPlayer() {

    }

    public static void spawnEntity() {

    }
}
