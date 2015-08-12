package com.vincent.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.awt.Dimension;
import java.awt.Toolkit;

public class DesktopLauncher {
    public static void main (String[] arg) {

        //this packs textures to a .pack file before runtime. This allows you to just drop new textures
        //into the assets file and have them auto packed. this is good for editing assets
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.pot = false;
        TexturePacker.process(settings, "C:/Users/Vincent/Desktop/Android Workspace/Assets/ui", "D:/Vincent/Documents/Github/boxhead/android/assets/ui", "ui");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            config.width = (int)screenSize.getWidth();
            config.height = (int)screenSize.getHeight();
            config.fullscreen = true;
            config.title = "Boxhead";

        new LwjglApplication(new com.vincent.game.MyGdxGame(), config);
    }


}
