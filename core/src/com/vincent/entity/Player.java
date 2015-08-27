package com.vincent.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.vincent.util.GameUtils;


/**
 * Created by Vincent on 6/25/2015.
 *
 * class for handling player updating and rendering
 */
public class Player extends Entity {

    private Touchpad touchpad;

    public Player(Texture[] texture, float maxSpeed, Vector3 position, World world, Touchpad touchpad, BodyDef bodyDef, FixtureDef fixtureDef) {
        super(new TextureRegion(texture[0]));
        setMaxSpeed(maxSpeed);
        this.touchpad = touchpad;
        textures = texture;
        sprite = new Sprite(texture[0]);
        sprite.setCenter(position.x, position.y);

        speed = new Vector2(0, 0);
        diagonalSpeed = new Vector2((float) (Math.cos(22.5) * maxSpeed), (float) (Math.sin(22.5) * maxSpeed));

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x / GameUtils.PIXELS_PER_METER, position.y / GameUtils.PIXELS_PER_METER);
        bodyDef.linearDamping = 0;

        body = world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(sprite.getWidth() / 2 / GameUtils.PIXELS_PER_METER, sprite.getWidth() / 2 / GameUtils.PIXELS_PER_METER);

        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef);
        polygonShape.dispose();
    }

    @Override
    public void update() {
        float touchpadPercentX = touchpad.getKnobPercentX();
        float touchpadPercentY = touchpad.getKnobPercentY();
        if (touchpadPercentX != 0 || touchpadPercentY != 0) {
            moving = true;
        } else {
            moving = false;
        }

        if (GameUtils.getTouchpadEightDirection(touchpadPercentX, touchpadPercentY) != -1) {
            setDirection(GameUtils.getTouchpadEightDirection(touchpadPercentX, touchpadPercentY));
        }

        super.update();
    }
}
