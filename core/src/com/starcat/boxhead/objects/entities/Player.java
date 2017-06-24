package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.starcat.boxhead.objects.entities.Behavior.PlayerState;
import com.starcat.boxhead.objects.weapons.Gun;
import com.starcat.boxhead.objects.weapons.Pistol;
import com.starcat.boxhead.objects.weapons.Shotgun;
import com.starcat.boxhead.objects.weapons.Sniper;
import com.starcat.boxhead.objects.weapons.Uzi;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;
import com.starcat.boxhead.utils.GameUtils;

import java.util.ArrayList;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Player extends Entity implements InputProcessor {

    private int currentWeapon;
    private Vector3 speed;
    private float maxSpeed;
    private float currentRotationAngle = -45;
    private AnimationController walkAnimationController;
    private AnimationController shootAnimationController;

    public StateMachine<Player, PlayerState> stateMachine;
    private boolean upPressed, downPressed, leftPressed, rightPressed = false;
    private Touchpad controller;

    private boolean grounded;
    private ArrayList<Gun> guns = new ArrayList<Gun>();



    public void init(Vector3 position, float maxSpeed, btRigidBody rigidBody, Texture skin) {
        super.init(position, new ModelInstance(AssetLoader.player), rigidBody);
        this.maxSpeed = maxSpeed;

        modelInstance.materials.get(0).set(new TextureAttribute(TextureAttribute.Diffuse, skin));

        speed = new Vector3(maxSpeed, 0, maxSpeed);
        speed.rotate(rotationVector, -45);

        guns.add(new Pistol(this));
        guns.add(new Shotgun(this));
        guns.add(new Uzi(this));

        currentWeapon = 0;

        walkAnimationController = new AnimationController(modelInstance);
        shootAnimationController = new AnimationController(modelInstance);
        shootAnimationController.allowSameAnimation = true;
        walkAnimationController.setAnimation("pose_dual_wield");

        stateMachine = new DefaultStateMachine<Player, PlayerState>(this, PlayerState.ALIVE);

        grounded = false;
    }



    @Override
    public void update(float delta) {
        super.update(delta);

        if (controller != null && controller.isTouched()) {
            moving = true;
            speed.set(maxSpeed * delta, 0, maxSpeed * delta);
            float angle = GameUtils.getTouchpadAngle(controller);

            speed.rotate(rotationVector, angle);
            rotation.set(rotationVector, angle - currentRotationAngle);
            rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
            currentRotationAngle = angle;
        } else {
            moving = false;
        }

        stateMachine.update();

        if (hitpoints <= 0) {
            stateMachine.changeState(PlayerState.DEAD);
        }
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        super.render(modelBatch, environment);
        if (hitpoints > 0) {
            guns.get(currentWeapon).render(modelBatch, environment);
        }
    }

    public void renderUI(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        if (hitpoints > 0 && grounded) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(Dimensions.getHalfScreenWidth() - Dimensions.scaleWidth(50), Dimensions.getHalfScreenHeight() + Dimensions.scaleHeight(100), Dimensions.scaleWidth(100), Dimensions.scaleHeight(10));
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1 - hitpoints / 100, hitpoints / 100, 0, 1);
            shapeRenderer.rect(Dimensions.getHalfScreenWidth() - Dimensions.scaleWidth(50), Dimensions.getHalfScreenHeight() + Dimensions.scaleHeight(100), Dimensions.scaleWidth((int)hitpoints), Dimensions.scaleHeight(10));
            shapeRenderer.end();

            guns.get(currentWeapon).renderUI(spriteBatch, shapeRenderer);
        }
    }



    public void fire() {
        guns.get(currentWeapon).fire();
    }

    public void switchGunLeft() {
        if (currentWeapon > 0) {
            currentWeapon--;
        }
    }

    public void switchGunRight() {
        if (currentWeapon < (guns.size() - 1)) {
            currentWeapon++;
        }
    }



    public float getCurrentRotationAngle() {
        return currentRotationAngle;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public Vector3 getCurrentSpeed() {
        return speed;
    }

    public boolean isMoving() {
        return moving;
    }

    public Gun getCurrentWeapon() {
        return guns.get(currentWeapon);
    }

    public AnimationController getWalkAnimationController() {
        return  walkAnimationController;
    }

    public AnimationController getShootAnimationController() {
        return shootAnimationController;
    }

    public void addController(Touchpad controller) {
        this.controller = controller;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            upPressed = true;
        } else if (keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        } else if (keycode == Input.Keys.DOWN) {
            downPressed = true;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = true;
        } else if (keycode == Input.Keys.SPACE) {
            fire();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.UP) {
            upPressed = false;
        } else if (keycode == Input.Keys.RIGHT) {
            rightPressed = false;
        } else if (keycode == Input.Keys.DOWN) {
            downPressed = false;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        fire();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
