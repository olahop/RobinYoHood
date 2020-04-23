package com.robinhood.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.robinhood.game.controller.Controller;
import com.robinhood.game.model.Model;
import com.robinhood.game.assetManagers.GameAssetManager;

/**
 * Superclass in Template method pattern - base of all application UIs.
 *
 * @author group 11
 * @version 1.0
 * @since 2020-04-25
 */
// TODO: check if table actions in subclasses can be generalized and moved here
// TODO: consider adding model to super constructor, like controller
public abstract class View {

    protected Controller controller;
    protected Model model;
    protected final Stage stage;
    protected final Table table;

    protected final Skin buttonSkin;
    protected final Skin textSkin;
    protected final Skin headerSkin;


    View(Controller controller, Model model) {
        this(controller);
        this.model = model;
    }

    View(Controller controller) {
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        GameAssetManager assetManager = GameAssetManager.getInstance();
        assetManager.loadSkins();
        assetManager.loadBackgrounds();
        assetManager.finishLoading();
        buttonSkin = assetManager.get(assetManager.buttonSkin);
        textSkin = assetManager.get(assetManager.buttonSkin);
        headerSkin = assetManager.get(assetManager.buttonSkin);

        // Create new table that fills the screen -> Table added to stage
        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        Texture menuback = assetManager.get(assetManager.menuBackgroundString);
        TextureRegion menuback1 = new TextureRegion(menuback);
        TextureRegionDrawable menuBack2 = new TextureRegionDrawable(menuback1);
        table.setBackground(menuBack2);
        stage.addActor(table);
    }

    public void render() {
        float[] values = hextoRGB("#5f8db0");
        Gdx.gl.glClearColor(values[0], values[1], values[2], 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    public void dispose () {
        stage.dispose();
    }

    protected ClickListener generateNavigateListener(final String destination) {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float clickX, float clickY) {
                controller.navigateTo(destination);
            }
        };
    }

    // FIXME: refac later
    //Hjelpemetoder - hex til glcolors
    /* re-map RGB colors so they can be used in OpenGL */
    private float[] map(float[]rgb) {
        float[] result = new float[3];
        result[0] = rgb[0] / 255;
        result[1] = rgb[1] / 255;
        result[2] = rgb[2] / 255;
        return result;
    }
    //Can use hexadecimals for colors instead of the gl colors
    public float[] hextoRGB(String hex) {
        float[] rgbcolor = new float[3];
        rgbcolor[0] = Integer.valueOf(hex.substring(1,3 ),16);
        rgbcolor[1] = Integer.valueOf(hex.substring(3,5 ),16);
        rgbcolor[2] = Integer.valueOf(hex.substring(5,7 ),16);
        return map(rgbcolor);
    }

}