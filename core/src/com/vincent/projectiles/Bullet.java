package com.vincent.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.vincent.World.BodyManager;
import com.vincent.World.SortableObject;
import com.vincent.util.AssetLoader;
import com.vincent.util.GameUtils;

/**
 * Created by Vincent on 8/17/2015.
 */
public class Bullet implements SortableObject, Pool.Poolable{

    private Body body;
    private Sprite sprite;
    private float speed;
    private Vector3 position;
    private Vector2 diagonalSpeed;
    private boolean visible = false;
    private int direction = 1;
    private Pool<Bullet> pool;


    public Bullet(World world, BodyDef bodyDef, FixtureDef fixtureDef, Pool<Bullet> pool) {
        this.pool = pool;

        visible = true;
        sprite = new Sprite(AssetLoader.bullet);
        position = new Vector3();
        diagonalSpeed = new Vector2();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x / GameUtils.PIXELS_PER_METER, position.y / GameUtils.PIXELS_PER_METER);
        bodyDef.linearDamping = 0;

        body = world.createBody(bodyDef);
        body.setBullet(true);
        body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(sprite.getWidth() / 2 / GameUtils.PIXELS_PER_METER, sprite.getHeight() / 2 / GameUtils.PIXELS_PER_METER);

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef);
        polygonShape.dispose();
    }

    public void update() {
        sprite.setCenter(body.getPosition().x * GameUtils.PIXELS_PER_METER, body.getPosition().y * GameUtils.PIXELS_PER_METER);
        position.x = sprite.getX();
        position.y = sprite.getY();
    }


    @Override
    public void render(Batch batch) {
        if (visible) {
            sprite.draw(batch);
        }
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

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public void reset() {
        speed = 0;
        direction = 1;
        diagonalSpeed.set(0, 0);
        visible = false;
        body.setLinearVelocity(0, 0);
    }

    public void free() {
        pool.free(this);
    }

    public void spawn(Vector3 position, float speed, int direction) {
        this.position.set(position);
        this.speed = speed;

        sprite.setCenter(position.x, position.y);
        body.setTransform(position.x / GameUtils.PIXELS_PER_METER, position.y / GameUtils.PIXELS_PER_METER, 0);
        diagonalSpeed.set((float) (Math.cos(22.5) * speed), (float) (Math.sin(22.5) * speed));

        this.direction = direction;
        this.visible = true;

        if(!body.isActive()) {
            BodyManager.addBodyToActive(body);
        }

        fire();
    }

    public void fire() {
        switch (direction) {
            case 1: {
                body.setLinearVelocity(0, speed);
                break;
            } case 2: {
                body.setTransform(body.getPosition(), MathUtils.degreesToRadians * -22.5f);
                sprite.setRotation(-22.5f);
                body.setLinearVelocity(-diagonalSpeed.x, -diagonalSpeed.y);
                break;
            } case 3: {
                body.setLinearVelocity(speed, 0);
                body.setTransform(body.getPosition(),MathUtils.degreesToRadians *  -90f);
                sprite.setRotation(-90f);
                break;
            } case 4: {
                body.setLinearVelocity(-diagonalSpeed.x, diagonalSpeed.y);
                body.setTransform(body.getPosition(), MathUtils.degreesToRadians * -112.5f);
                sprite.setRotation(-112.5f);
                break;
            } case 5: {
                body.setLinearVelocity(0, -speed);
                body.setTransform(body.getPosition(), MathUtils.degreesToRadians * 180f);
                sprite.setRotation(180f);
                break;
            } case 6: {
                body.setLinearVelocity(diagonalSpeed.x, diagonalSpeed.y);
                body.setTransform(body.getPosition(), MathUtils.degreesToRadians * 112.5f);
                sprite.setRotation(112.5f);
                break;
            } case 7: {
                body.setLinearVelocity(-speed, 0);
                body.setTransform(body.getPosition(),MathUtils.degreesToRadians *  90f);
                sprite.setRotation(90f);
                break;
            } case 8: {
                body.setLinearVelocity(diagonalSpeed.x, -diagonalSpeed.y);
                body.setTransform(body.getPosition(), MathUtils.degreesToRadians * 22.5f);
                sprite.setRotation(22.5f);
                break;
            } default: {
                break;
            }
        }
    }

    public Body getBody() {
        return body;
    }

}
