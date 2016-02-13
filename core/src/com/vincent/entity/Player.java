package com.vincent.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.vincent.Weapons.Gun;
import com.vincent.Weapons.Pistol;
import com.vincent.projectiles.ProjectileManager;
import com.vincent.util.AssetLoader;
import com.vincent.util.GameUtils;


/**
 * Created by Vincent on 6/25/2015.
 *
 * class for handling player updating and rendering
 */
public class Player extends Entity {

    private Touchpad moveTouchpad;
    private ProjectileManager projectileManager;
    private float reloadTime = 0;
    private Gun currentWeapon;
    //whether the touchpad is being used or the keyboard
    private boolean touchpad = true;
    private float animationCounter;
    private Animation animation;

    public Player(float maxSpeed, Vector3 position, World world, Touchpad moveTouchpad, ProjectileManager projectileManager, BodyDef bodyDef, FixtureDef fixtureDef) {
        super();

        setMaxSpeed(maxSpeed);
        this.moveTouchpad = moveTouchpad;
        this.projectileManager = projectileManager;
        speed = new Vector2(0, 0);



        //initialize collision boxes (fixtures)
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x / GameUtils.PIXELS_PER_METER, position.y / GameUtils.PIXELS_PER_METER);
        bodyDef.linearDamping = 0;

        body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        vertices = new Vector2[4];

        vertices[0] = new Vector2(-14 / GameUtils.PIXELS_PER_METER, 7 / GameUtils.PIXELS_PER_METER);
        vertices[1] = new Vector2(28 / GameUtils.PIXELS_PER_METER, 7 / GameUtils.PIXELS_PER_METER);
        vertices[2] = new Vector2(-14 / GameUtils.PIXELS_PER_METER, 14 / GameUtils.PIXELS_PER_METER);
        vertices[3] = new Vector2(28 / GameUtils.PIXELS_PER_METER, 14 / GameUtils.PIXELS_PER_METER);
        polygonShape.set(vertices);
        fixtureDef.shape = polygonShape;
        fixture1 = body.createFixture(fixtureDef);

        vertices[0].set(0, 21 / GameUtils.PIXELS_PER_METER);
        vertices[1].set(-14 / GameUtils.PIXELS_PER_METER, 14 / GameUtils.PIXELS_PER_METER);
        vertices[2].set(14 / GameUtils.PIXELS_PER_METER, 0);
        vertices[3].set(28 / GameUtils.PIXELS_PER_METER, 7 / GameUtils.PIXELS_PER_METER);
        polygonShape.set(vertices);
        fixtureDef.shape = polygonShape;
        fixture2 = body.createFixture(fixtureDef);

        vertices[0].set(-2 / GameUtils.PIXELS_PER_METER, -2 / GameUtils.PIXELS_PER_METER);
        vertices[1].set(-2 / GameUtils.PIXELS_PER_METER, 22 / GameUtils.PIXELS_PER_METER);
        vertices[2].set(18 / GameUtils.PIXELS_PER_METER, -2 / GameUtils.PIXELS_PER_METER);
        vertices[3].set(18 / GameUtils.PIXELS_PER_METER, 22 / GameUtils.PIXELS_PER_METER);
        polygonShape.set(vertices);
        fixtureDef.shape = polygonShape;
        fixture3 = body.createFixture(fixtureDef);

        vertices[0].set(0, 0);
        vertices[1].set(28 / GameUtils.PIXELS_PER_METER, 14 / GameUtils.PIXELS_PER_METER);
        vertices[2].set(-14 / GameUtils.PIXELS_PER_METER, 7 / GameUtils.PIXELS_PER_METER);
        vertices[3].set(14 / GameUtils.PIXELS_PER_METER, 21 / GameUtils.PIXELS_PER_METER);
        polygonShape.set(vertices);
        fixtureDef.shape = polygonShape;
        fixture4 = body.createFixture(fixtureDef);

        body.getFixtureList().clear();
        body.getFixtureList().add(fixture1);

        polygonShape.dispose();

        animation = AssetLoader.playerDownLeft;

        currentWeapon = new Pistol(this.projectileManager, this);
    }

    @Override
    public void render(Batch batch) {

        if (moving) {
            animationCounter += Gdx.graphics.getDeltaTime();
        } else {
            animationCounter = 0;
        }

        switch (direction) {
            case 1: {
                batch.draw(AssetLoader.playerUp.getKeyFrame(animationCounter, true), interpolatedPosition.x, interpolatedPosition.y);
                break;
            } case 3: {
                batch.draw(AssetLoader.playerRight.getKeyFrame(animationCounter, true), interpolatedPosition.x, interpolatedPosition.y);
                break;
            } case 5: {
                batch.draw(AssetLoader.playerDown.getKeyFrame(animationCounter, true), interpolatedPosition.x, interpolatedPosition.y);
                break;
            } case 6: {
                batch.draw(AssetLoader.playerDownLeft.getKeyFrame(animationCounter, true), interpolatedPosition.x, interpolatedPosition.y);
                break;
            } case 7: {
                batch.draw(AssetLoader.playerLeft.getKeyFrame(animationCounter, true), interpolatedPosition.x, interpolatedPosition.y);
            }
        }

    }

    @Override
    public void update(float delta, float alpha) {

        int direction = GameUtils.getTouchpadEightDirection(moveTouchpad.getKnobPercentX(), moveTouchpad.getKnobPercentY());

        if (direction != -1 && touchpad) {
            moving = true;
            this.direction = direction;
        } else if (touchpad) {
            moving = false;
        }

        reloadTime += delta;

        super.update(delta, alpha);
    }

    public void fire() {
        if (reloadTime >= currentWeapon.getFireRate()) {
            currentWeapon.fire();
            reloadTime = 0;
        }
    }

    public void setTouchpad(boolean touchpad) {
        this.touchpad = touchpad;
    }

    public boolean getTouchpad() {
        return touchpad;
    }
}
