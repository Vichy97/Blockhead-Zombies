package com.vincent.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.vincent.World.SortableObject;
import com.vincent.util.GameUtils;
import com.vincent.util.map.MapUtils;

/**
 * Created by Vincent on 7/8/2015.
 *
 * Base class for entities such as the player or zombies
 */
public class Entity implements SortableObject {

    public Vector3 tilePosition;

    //basically whether the joystick is being held
    protected boolean moving = false;
    protected Vector2 speed;
    protected Vector2 diagonalSpeed;
    protected Body body;
    //we only update speed and collision boxes if the direction changed so we track the last direction
    protected int direction = 1, lastDirection = 0;
    protected float maxSpeed;
    protected Vector3 position, interpolatedPosition;
    //angle of entity
    protected float angle;
    //different collision boxes for different entitiy directions
    protected Fixture fixture1, fixture2, fixture3, fixture4;
    //vertices of fixtures
    protected Vector2[] vertices;
    //offset in pixel for the sprite to be drawn from the collision box
    protected Vector2 offset;

    private int hitpoints;

    public Entity() {
        speed = new Vector2(0, 0);
        tilePosition = new Vector3(0, 0, 0);
        position = new Vector3();
        interpolatedPosition = new Vector3(0, 0, 0);
        offset = new Vector2(0, 0);

        offset.set(-68, 7);
    }

    @Override
    public void render(Batch batch) {
    }

    @Override
    public int compareTo(SortableObject object) {
        if (position.y > object.getPosition().y) {
            return -1;
        } else if (position.y < object.getPosition().y) {
            return 1;
        } else if (position.y == object.getPosition().y){
            if (position.x > object.getPosition().x) {
                return 1;
            } else if (position.x < object.getPosition().x) {
                return -1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    public void update(float delta, float alpha) {

        if (moving) {
            if (lastDirection != direction) {
                lastDirection = direction;

                switch(direction) {
                    case 1: {
                        speed.x = 0;
                        speed.y = maxSpeed;
                        angle = 360;

                        offset.set(-68, 7);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture1);
                        break;
                    } case 2: {
                        speed.set(diagonalSpeed.x, diagonalSpeed.y);
                        angle = 63.4f;

                        offset.set(-63, 0);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture2);
                        break;
                    } case 3: {
                        speed.x = maxSpeed;
                        speed.y = 0;
                        angle = 90;

                        offset.set(-58, -2);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture3);
                        break;
                    } case 4: {
                        speed.set(diagonalSpeed.x, -diagonalSpeed.y);
                        angle = 116.6f;

                        offset.set(-64, -2);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture4);
                        break;
                    } case 5: {
                        speed.x = 0;
                        speed.y = maxSpeed * -1;
                        angle = 180;

                        offset.set(-68, 8);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture1);
                        break;
                    } case 6: {
                        speed.set(-diagonalSpeed.x, -diagonalSpeed.y);
                        angle = 243.4f;

                        offset.set(-60, 0);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture2);
                        break;
                    } case 7: {
                        speed.x = maxSpeed * -1;
                        speed.y = 0;
                        angle = 270;

                        offset.set(-52, -2);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture3);
                        break;
                    } case 8: {
                        speed.set(-diagonalSpeed.x, diagonalSpeed.y);
                        angle = 337.5f;

                        offset.set(-59, -1);

                        body.getFixtureList().removeIndex(0);
                        body.getFixtureList().add(fixture4);
                        break;
                    } default: {
                        speed.x = 0;
                        speed.y = 0;
                        break;
                    }
                }
            }
        } else {
            speed.set(0, 0);
            lastDirection = 0;
        }

        body.setLinearVelocity(speed.x, speed.y);

        position.x = (body.getPosition().x * GameUtils.PIXELS_PER_METER) + offset.x;
        position.y = (body.getPosition().y * GameUtils.PIXELS_PER_METER) + offset.y;

        interpolatedPosition.x = position.x;// * alpha + interpolatedPosition.x * (1.0f - alpha);
        interpolatedPosition.y = position.y;// * alpha + interpolatedPosition.y * (1.0f - alpha);

        tilePosition = MapUtils.worldToTile(position.x, position.y);
    }

    public void damage(int hitpoints) {
        this.hitpoints -= hitpoints;
    }



    public Vector3 getTilePosition() {
        return tilePosition;
    }

    public float getEntityX() {
        return position.x;
    }

    public float getEntityY() {
        return position.y;
    }

    public int getDirection() {
        return direction;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setSpeed(Vector2 speed) {
        this.speed = speed;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        diagonalSpeed = new Vector2((float)(Math.cos(63.435f) * maxSpeed), (float)(Math.sin(63.435f) * maxSpeed));
    }

    public Vector2 getDiagonalSpeed() {
        return diagonalSpeed;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public float getAngle() {
        return angle;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean getMoving() {
        return moving;
    }


    @Override
    public Vector3 getPosition() {
        return position;
    }
}
