package com.vincent.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

import java.nio.ByteBuffer;

/**
 * Created by Vincent on 6/30/2015.
 *
 * random static utility methods and fields for things like screenshots
 */
public class GameUtils {

    public static final float PIXELS_PER_METER = 128f;

    private static int screenshotCounter = 1;

    public static int getTouchpadFourDirection(float touchpadPercentX, float touchpadPercentY) {
        float angle = (float)(180 + Math.atan2(touchpadPercentY, touchpadPercentX) * 180.0d / Math.PI);

        if (touchpadPercentX == 0 && touchpadPercentY == 0) {
            return -1;
        } else if (angle <= 315 && angle > 225) {
            return 1;
        } else if (angle <= 225 && angle > 135) {
            return 2;
        } else if (angle <= 135 && angle > 45) {
            return 3;
        } else if ((angle <= 45 && angle >= 0) || (angle <= 360 && angle > 315)) {
            return 4;
        } else {
            return -1;
        }
    }

    public static int getTouchpadEightDirection(float touchpadPercentX, float touchpadPercentY) {
        float angle = (float)(180 + Math.atan2(touchpadPercentY, touchpadPercentX) * 180.0d / Math.PI);

        if (touchpadPercentX == 0 && touchpadPercentY == 0) {
            return -1;
        } else if (angle > 247.5 && angle <= 292.5) {
            return 1;
        } else if (angle > 202.5 && angle <= 247.5) {
            return 2;
        } else if (angle > 157.5 && angle <= 202.5) {
            return 3;
        } else if (angle > 112.5 && angle <= 157.5) {
            return 4;
        } else if (angle > 67.5 && angle <= 112.5) {
            return 5;
        } else if (angle > 22.5 && angle <= 67.5) {
            return 6;
        } else if (angle <= 22.5 || angle > 337.5) {
            return 7;
        } else if (angle > 292.5 && angle <= 337.5) {
            return 8;
        } else {
            return -1;
        }
    }

    public static void saveScreenshot(){
        Gdx.app.log(" ", "saveScreenshot");
        try {
            FileHandle fh;
            do {
                fh = new FileHandle(Gdx.files.getLocalStoragePath() + "screenshot" + screenshotCounter++ + ".png");
                Gdx.app.log(" ", Gdx.files.getLocalStoragePath());
            } while (fh.exists());
            Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown){
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

        if (yDown) {
            // Flip the pixmap upside down
            ByteBuffer pixels = pixmap.getPixels();
            int numBytes = w * h * 4;
            byte[] lines = new byte[numBytes];
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
        }

        return pixmap;
    }

}
