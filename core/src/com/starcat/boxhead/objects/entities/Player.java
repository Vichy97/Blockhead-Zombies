package com.starcat.boxhead.objects.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.starcat.boxhead.objects.entities.Behavior.PlayerState;
import com.starcat.boxhead.objects.weapons.Gun;
import com.starcat.boxhead.objects.weapons.UziDual;
import com.starcat.boxhead.utils.AssetLoader;
import com.starcat.boxhead.utils.Dimensions;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 8/12/2016.
 */
public class Player extends Entity implements InputProcessor {

    private Gun currentWeapon;
    private Vector3 speed;
    private float maxSpeed;
    private float currentRotationAngle = 0;
    private int direction = 2;
    private AnimationController walkAnimationController;
    private AnimationController shootAnimationController;

    public StateMachine<Player, PlayerState> stateMachine;
    private boolean upPressed, downPressed, leftPressed, rightPressed = false;
    private Touchpad controller;



    public void init(Vector3 position, float maxSpeed, btRigidBody rigidBody) {
        super.init(position, new ModelInstance(AssetLoader.player), rigidBody);
        this.maxSpeed = maxSpeed;

        speed = new Vector3(maxSpeed, 0, maxSpeed);
        speed.rotate(rotationVector, -45);

        currentWeapon = new UziDual(this);

        walkAnimationController = new AnimationController(modelInstance);
        shootAnimationController = new AnimationController(modelInstance);
        shootAnimationController.allowSameAnimation = true;

        stateMachine = new DefaultStateMachine<Player, PlayerState>(this, PlayerState.ALIVE);
    }



    @Override
    public void update(float delta) {
        super.update(delta);

        if (upPressed) {
            if (leftPressed) {
                setDirection(8);
            } else if (rightPressed) {
                setDirection(2);
            } else {
                setDirection(1);
            }
        } else if (downPressed) {
            if (leftPressed) {
                setDirection(6);
            } else if (rightPressed) {
                setDirection(4);
            } else {
                setDirection(5);
            }
        } else if (leftPressed) {
            setDirection(7);
        } else if (rightPressed) {
            setDirection(3);
        } else if (controller != null && controller.isTouched()) {
            setDirection(GameUtils.getTouchpadEightDirection(controller.getKnobPercentX(), controller.getKnobPercentY()));
        } else {
            setDirection(-1);
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
            currentWeapon.render(modelBatch, environment);
        }
    }

    public void renderUI(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        if (hitpoints > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(Dimensions.getHalfScreenWidth() - 50 * Dimensions.getGameScreenWidthRatio(), Dimensions.getHalfScreenHeight() + 40 * Dimensions.getGameScreenHeightRatio(), 100 * Dimensions.getGameScreenWidthRatio(), 10 * Dimensions.getGameScreenHeightRatio());
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1 - hitpoints / 100, hitpoints / 100, 0, 1);
            shapeRenderer.rect(Dimensions.getHalfScreenWidth() - 50 * Dimensions.getGameScreenWidthRatio(), Dimensions.getHalfScreenHeight() + 40 * Dimensions.getGameScreenHeightRatio(), hitpoints * Dimensions.getGameScreenWidthRatio(), 10 * Dimensions.getGameScreenHeightRatio());
            shapeRenderer.end();
            currentWeapon.renderUI(spriteBatch, shapeRenderer);
        }
    }



    public void fire() {
        currentWeapon.fire();
    }



    public void setDirection(int direction) {

        //this only needs to be calculated if the direction has changed
        if (this.direction != direction || direction == -1 || (!moving)) {
            moving = true;
            switch (direction) {
                case 1: {
                    speed.rotate(rotationVector, 45 - currentRotationAngle);
                    rotation.set(rotationVector, 45 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 45;
                    break;
                }
                case 2: {
                    speed.rotate(rotationVector, 360 - currentRotationAngle);
                    rotation.set(rotationVector, 360 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 360;
                    break;
                }
                case 3: {
                    speed.rotate(rotationVector, 315 - currentRotationAngle);
                    rotation.set(rotationVector, 315 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 315;
                    break;
                }
                case 4: {
                    speed.rotate(rotationVector, 270 - currentRotationAngle);
                    rotation.set(rotationVector, 270 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 270;
                    break;
                }
                case 5: {
                    speed.rotate(rotationVector, 225 - currentRotationAngle);
                    rotation.set(rotationVector, 225 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 225;
                    break;
                }
                case 6: {
                    speed.rotate(rotationVector, 180 - currentRotationAngle);
                    rotation.set(rotationVector, 180 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 180;
                    break;
                }
                case 7: {
                    speed.rotate(rotationVector, 135 - currentRotationAngle);
                    rotation.set(rotationVector, 135 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 135;
                    break;
                }
                case 8: {
                    speed.rotate(rotationVector, 90 - currentRotationAngle);
                    rotation.set(rotationVector, 90 - currentRotationAngle);
                    rigidBody.setWorldTransform(rigidBody.getWorldTransform().rotate(rotation));
                    currentRotationAngle = 90;
                    break;
                }
                default: {
                    moving = false;
                }
            }
        }

        if (direction != -1) {
            this.direction = direction;
        }
    }

    public int getDirection() {
        return direction;
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
        return currentWeapon;
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
