package com.vincent.World;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Vincent on 8/18/2015.
 *
 * Everything that needs to be depth sorted
 * Using this makes me not need to subclass mapObject for everything
 * now everything (bullets, entities, objects etc..) are all SortableObjects
 *
 * Ideally this would be an abstract class but i dont feel like rewriting
 * libgdx map api #gay
 */
public interface SortableObject{

    void render(Batch batch);

    int compareTo(SortableObject object);

    Vector3 getPosition();
}
