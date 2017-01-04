package com.starcat.boxhead.utils;

import com.badlogic.gdx.Gdx;

/**
 * Created by Vincent on 1/3/2017.
 *
 * this class is used to keep track of commonly used dimensions
 * similar to how android uses xml files
 */

public final class Dimensions {

    private static final int gameWidth = 1920;
    private static final int gameHeight = 1080;
    private static int screenWidth;
    private static int screenHeight;
    private static float aspectRatio;
    private static float gameScreenWidthRatio;
    private static float gameScreenHeightRatio;

    private static int halfScreenWidth;
    private static int halfScreenHeight;

    //this has to be called because Gdx.graphics does not exist on compilation
    public static void init() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        aspectRatio = (float)screenHeight / (float)screenWidth;
        gameScreenWidthRatio = (float)screenWidth / (float)gameWidth;
        gameScreenHeightRatio = (float)screenHeight / (float)gameHeight;

        halfScreenWidth = screenWidth / 2;
        halfScreenHeight = screenHeight / 2;
    }

    public static int getGameWidth() {
        return gameWidth;
    }

    public static int getGameHeight() {
        return gameHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static float getAspectRatio() {
        return aspectRatio;
    }

    public static float getGameScreenWidthRatio() {
        return gameScreenWidthRatio;
    }

    public static float getGameScreenHeightRatio() {
        return gameScreenHeightRatio;
    }

    public static int getHalfScreenWidth() {
        return halfScreenWidth;
    }

    public static int getHalfScreenHeight() {
        return halfScreenHeight;
    }

    public static float scaleWidth(int width) {
        return (float)width * gameScreenWidthRatio;
    }

    public static float scaleHeight(int height) {
        return (float)height * gameScreenHeightRatio;
    }
}
