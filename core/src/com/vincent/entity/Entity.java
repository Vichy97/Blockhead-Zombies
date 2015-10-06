package com.vincent.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import com.vincent.World.SortableObject;
import com.vincent.util.GameUtils;
import com.vincent.util.map.MapUtils;

/**
 * Created by Vincent on 7/8/2015.
 *
 */
public class Entity implements SortableObject {

    public Vector3 tilePosition;

    protected boolean moving = false;
    protected Vector2 speed;
    protected Vector2 diagonalSpeed;
    protected Sprite sprite;
    protected Body body;
    protected int direction = 1;
    protected float maxSpeed;
    protected Texture[] textures;
    protected Vector3 position;
    protected float angle;

    private int hitpoints;

    public Entity() {
        sprite = new Sprite();
        speed = new Vector2(0, 0);
        tilePosition = new Vector3(0, 0, 0);
        position = new Vector3();
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
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

    public void update(float delta) {

        if (moving) {
            switch(direction) {
                case 1: {
                    speed.x = 0;
                    speed.y = maxSpeed;
                    //angle = 0;
                    break;
                }
                case 2: {
                    speed.set(diagonalSpeed.x * -1, diagonalSpeed.y * -1);
                   // angle = 22.5f;
                    break;
                }
                case 3: {
                    speed.x = maxSpeed;
                    speed.y = 0;
                   // angle = 90;
                    break;
                }
                case 4: {
                    speed.set(diagonalSpeed.x * -1, diagonalSpeed.y);
                    break;
                }
                case 5: {
                    speed.x = 0;
                    speed.y = maxSpeed * -1;
                    break;
                }
                case 6: {
                    speed.set(diagonalSpeed.x, diagonalSpeed.y);
                    break;
                }
                case 7: {
                    speed.x = maxSpeed * -1;
                    speed.y = 0;
                    break;
                }
                case 8: {
                    speed.set(diagonalSpeed.x, diagonalSpeed.y * -1);
                    break;
                }
                default: {
                    speed.x = 0;
                    speed.y = 0;
                    break;
                }
            }
        } else {
            speed.set(0, 0);
        }

        sprite.setTexture(textures[direction - 1]);

        body.setLinearVelocity(speed.x, speed.y);
        float x = body.getPosition().x * GameUtils.PIXELS_PER_METER;
        float y = body.getPosition().y * GameUtils.PIXELS_PER_METER;
        sprite.setCenter((int)x, (int)y);
        tilePosition = MapUtils.worldToTile(x, y);
    }

    public void damage(int hitpoints) {
        this.hitpoints -= hitpoints;
    }



    public Vector3 getTilePosition() {
        return tilePosition;
    }

    public float getEntityX() {
        return sprite.getX();
    }

    public float getEntityY() {
        return sprite.getY();
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
        diagonalSpeed = new Vector2((float)(Math.cos(22.5) * maxSpeed), (float)(Math.sin(22.5) * maxSpeed));
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

    public Sprite getSprite() {
        return sprite;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    @Override
    public Vector3 getPosition() {
        position.set(sprite.getX(), sprite.getY(), 0);
        return position;
    }
}
