package com.vincent.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import com.vincent.util.GameUtils;
import com.vincent.World.CustomMapObject;
import com.vincent.util.map.MapUtils;

/**
 * Created by Vincent on 7/8/2015.
 *
 */
public class Entity extends CustomMapObject{

    public Vector3 tilePosition;

    protected Vector2 speed;
    protected Vector2 diagonalSpeed;
    protected Sprite sprite;
    protected Body body;
    private int direction = -1;
    private float maxSpeed;
    protected Texture[] textures;

    public Entity(TextureRegion textureRegion) {
        super(textureRegion);
        sprite = new Sprite();
        speed = new Vector2(0, 0);
        tilePosition = new Vector3(0, 0, 0);
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }

    public void update() {

        float delta = Gdx.graphics.getDeltaTime();

        switch (direction) {
            case 1: {
                speed.x = 0;
                speed.y = maxSpeed * delta;
                sprite.setTexture(textures[0]);
                break;
            }
            case 2: {
                speed.set(diagonalSpeed.x * delta * -1, diagonalSpeed.y * delta * -1);
                sprite.setTexture(textures[1]);
                break;
            }
            case 3: {
                speed.x = maxSpeed * delta;
                speed.y = 0;
                sprite.setTexture(textures[2]);
                break;
            }
            case 4: {
                speed.set(diagonalSpeed.x * delta * -1, diagonalSpeed.y * delta);
                sprite.setTexture(textures[3]);
                break;
            }
            case 5: {
                speed.x = 0;
                speed.y = maxSpeed * delta * -1;
                sprite.setTexture(textures[4]);
                break;
            }
            case 6: {
                speed.set(diagonalSpeed.x * delta, diagonalSpeed.y * delta);
                sprite.setTexture(textures[5]);
                break;
            }
            case 7: {
                speed.x = maxSpeed * delta * -1;
                speed.y = 0;
                sprite.setTexture(textures[6]);
                break;
            }
            case 8: {
                speed.set(diagonalSpeed.x * delta, diagonalSpeed.y * delta * -1);
                sprite.setTexture(textures[7]);
                break;
            }
            default: {
                speed.x = 0;
                speed.y = 0;
                break;
            }
        }

        body.setLinearVelocity(speed.x, speed.y);
        float x = body.getPosition().x * GameUtils.PIXELS_PER_METER;
        float y = body.getPosition().y * GameUtils.PIXELS_PER_METER;
        position.set(x - width / 2, y - height / 2, 0);
        sprite.setCenter((int)x, (int)y);
        tilePosition = MapUtils.worldToTile(x, y);
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

    public Vector3 getPosition() {
        position.set(sprite.getX(), sprite.getY(), 0);
        return position;
    }
}
