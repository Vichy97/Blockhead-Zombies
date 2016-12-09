package com.starcat.boxhead.game.screens;

import com.starcat.boxhead.game.MyGdxGame;
import com.starcat.boxhead.utils.GameUtils;

/**
 * Created by Vincent on 6/18/2015.
 *
 * this is the loading screen before the menu
 */
public class LoadingScreen extends BaseScreen {


    public LoadingScreen(MyGdxGame game) {
        super(game);

        game.getAssetLoader().load();
    }



    @Override
    public void render(float delta) {
        GameUtils.debug(this, "" + game.getAssetLoader().getManager().getQueuedAssets());

        //returns true if it is done loading
        if (game.getAssetLoader().update()) {
            game.getAssetLoader().initAssets();
            game.setScreen(new MenuScreen(game));
            dispose();
        }

        float progress = game.getAssetLoader().getManager().getProgress();
    }

}
