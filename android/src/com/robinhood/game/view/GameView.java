package com.robinhood.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.math.Vector2;
import com.robinhood.game.controller.Controller;
import com.robinhood.game.model.Components;
import com.robinhood.game.model.Model;
import com.robinhood.game.view.interfaceObjects.DragIndicator;
import com.robinhood.game.view.interfaceObjects.Button;

import java.util.List;


public class GameView extends View {

    //private final Controller controller;
    private final DragIndicator dragIndicator;

    private SpriteBatch batch = new SpriteBatch();
    private BitmapFont font = new BitmapFont();

    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
    private final OrthographicCamera cam = new OrthographicCamera(32, 24);

    private World world;

    private boolean buyLv2 = true;
    private boolean buyLv3 = true;
    private boolean buyLv4 = true;


    public GameView(Controller cont, Model model) {
        super(cont);
        table.remove();

        //this.controller = cont;
        this.world = model.world;

        // Set the stage of the View superclass
        //stage = new Stage(new ScreenViewport());
        //Gdx.input.setInputProcessor(stage);

        // Initiate clickable objects within the interface
        Button menuButton = new Button("menu");
        Button leftButton = new Button("left");
        Button rightButton = new Button("right");

        // Add ClickListeners to call appropriate actions at clickable objects
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float clickX, float clickY) {
                getController().navigateTo("MENU");
            }
        });
        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float clickX, float clickY) {
                getController().move(true);
            }
        });
        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float clickX, float clickY) {
                getController().move(false);
            }
        });

        // Add all the clickable objects to this interface
        stage.addActor(menuButton);
        stage.addActor(leftButton);
        stage.addActor(rightButton);

        addBuys();

        // Add listener to detect drag on screen and trigger controller.drawBow()-method
        dragIndicator = new DragIndicator();
        stage.addActor(dragIndicator);
        stage.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float clickX, float clickY, int pointer) {
                float power = (float)Math.sqrt(Math.pow(clickX -
                        getDragStartX(), 2) + Math.pow(clickY - getDragStartY(), 2));
                dragIndicator.sprite.setSize(power, 40);

                double angleRad = Math.atan(Math.abs(clickY-getDragStartY())
                        / Math.abs(clickX-getDragStartX()));
                float angleDeg = (float)Math.toDegrees(angleRad);
                if(clickX < getDragStartX()) {
                    if(clickY < getDragStartY()) {
                        dragIndicator.sprite.setRotation(angleDeg);
                    } else {
                        dragIndicator.sprite.setRotation(360-angleDeg);
                    }
                } else {
                    if(clickY < getDragStartY()) {
                        dragIndicator.sprite.setRotation(180-angleDeg);
                    } else {
                        dragIndicator.sprite.setRotation(180+angleDeg);
                    }
                }
            }
            @Override
            public void dragStop(InputEvent event,
                                 float clickX,
                                 float clickY,
                                 int pointer) {
                getController().drawBow(new Vector2(
                        clickX-getDragStartX(),
                        clickY-getDragStartY())
                );
                dragIndicator.sprite.setSize(0,0);
            }
        });
    }

    @Override
    public void render() {
        float[] values = hextoRGB("#5f8db0");
        Gdx.gl.glClearColor(values[0], values[1], values[2], 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        handlePlayerInfo();
        debugRenderer.render(world, cam.combined);
    }

    private void handlePlayerInfo(){

        //TODO: change to int
        List<Integer> hitPoints = getController().getHP();
        List<Integer> energyPoints = getController().getEnergy();

        String hpText = "";
        String energyText = "EnergyPoints" + ": " + energyPoints + "\n";
        for (int i = 0; i < hitPoints.size(); i++) {
            //FIXME: change check, now game over with only one player at hp=0
            if(hitPoints.get(i) <= 0) {
                getController().handleGameOver();
            }
            hpText += "HitPoints P" + i + ": " + hitPoints.get(i) + "\n";

        }
        //TODO: change to only "energypoints" When changed to int
        checkBuys(energyPoints.get(0));

        batch.begin();
        font.draw(batch, (hpText + energyText), 750, 830);
        batch.end();
    }

    public void checkBuys(int energyPoints){
        if (energyPoints >= 20) {
            if(!this.buyLv2){
                addLv2();
            }
            this.buyLv2 = true;
        } else {
            if(this.buyLv2){
                removeButton("buyLevel2");
            }
            this.buyLv2 = false;
        }

        if(energyPoints >= 50){
            if(!this.buyLv3){
                addLv3();
            }
            this.buyLv3 = true;
        } else {
            if(this.buyLv3){
                removeButton("buyLevel3");
            }
            this.buyLv3 = false;
        }

        if(energyPoints >= 70){
            if(!this.buyLv4){
                addLv4();
            }
            this.buyLv4 = true;
        } else {
            if(this.buyLv4){
                removeButton("buyLevel4");
            }
            this.buyLv4 = false;
        }
    }

    public void addBuys(){
        Button buyLevel2 = new Button("buyLevel2");
        Button buyLevel3 = new Button("buyLevel3");
        Button buyLevel4 = new Button("buyLevel4");

        buyLevel2.addListener(generateBuyArrowListener("buyLevel2"));
        buyLevel3.addListener(generateBuyArrowListener("buyLevel3"));
        buyLevel4.addListener(generateBuyArrowListener("buyLevel4"));

        buyLevel2.setName("buyLevel2");
        buyLevel3.setName("buyLevel3");
        buyLevel4.setName("buyLevel4");

        stage.addActor(buyLevel2);
        stage.addActor(buyLevel3);
        stage.addActor(buyLevel4);
    }

    public void addLv2(){
        Button buyLevel2 = new Button("buyLevel2");

        buyLevel2.addListener(generateBuyArrowListener("buyLevel2"));

        stage.addActor(buyLevel2);
        buyLevel2.setName("buyLevel2");
    }

    public void addLv3(){
        Button buyLevel3 = new Button("buyLevel3");

        buyLevel3.addListener(generateBuyArrowListener("buyLevel3"));

        stage.addActor(buyLevel3);
        buyLevel3.setName("buyLevel3");
    }

    public void addLv4(){
        Button buyLevel4 = new Button("buyLevel4");

        buyLevel4.addListener(generateBuyArrowListener("buyLevel4"));

        stage.addActor(buyLevel4);
        buyLevel4.setName("buyLevel4");
    }

    protected ClickListener generateBuyArrowListener(final String type) {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float clickX, float clickY) {
                System.out.println("You want to buy a level " + type + " weapon");
                getController().buyArrow(type);
            }
        };
    }

    public void removeButton(String name){
        stage.getRoot().findActor(name).remove();
    }
}
