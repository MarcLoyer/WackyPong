package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.wackypong.world.Bumper;
import com.obduratereptile.wackypong.world.Capture;
import com.obduratereptile.wackypong.world.Hazard;
import com.obduratereptile.wackypong.world.PinballBumper;
import com.obduratereptile.wackypong.world.Shrink;
import com.obduratereptile.wackypong.world.Spinner;
import com.obduratereptile.wackypong.world.Warp;
import com.obduratereptile.wackypong.world.World;

import java.io.IOException;

/**
 * Created by Marc on 9/9/2016.
 */
public class HelpScreen extends Stage implements Screen {
    public WackyPong game;

    public OrthographicCamera camera;
    public Skin skin;

    public ObjectMap<String, String> helptext;

    public HelpScreen(WackyPong g) {
        super(new FitViewport(WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY));
        Gdx.input.setInputProcessor(this);

        game = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        TextButton btn;

        initializeText();

        Table table = new Table(skin);
        table.center();
        World world = new World(game, 0);
        Label lbl;

        lbl = new Label(helptext.get("howToPlay"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().colspan(2)
                .grow()
                .row();

        lbl = new Label(helptext.get("howToEdit"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().colspan(2)
                .grow()
                .row();

        table.add(new Bumper(world, 0, 0, 20)).pad(5);
        lbl = new Label(helptext.get("bumperDescription"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().expandX().fillX()
                .row();

        table.add(new Capture(world, 0, 0, 20)).pad(5);
        lbl = new Label(helptext.get("captureDescription"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().expandX().fillX()
                .row();

        table.add(new PinballBumper(world, 0, 0, 20)).pad(5);
        lbl = new Label(helptext.get("pinballbumperDescription"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().expandX().fillX()
                .row();

        table.add(new Shrink(world, 0, 0, 20)).pad(5);
        lbl = new Label(helptext.get("shrinkDescription"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().expandX().fillX()
                .row();

        table.add(new Spinner(world, 0, 0, 20)).pad(5);
        lbl = new Label(helptext.get("spinnerDescription"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().expandX().fillX()
                .row();

        table.add(new Warp(world, 0, 0, 20)).pad(5);
        lbl = new Label(helptext.get("warpDescription"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().expandX().fillX()
                .row();

        lbl = new Label(helptext.get("credits"), skin, "default");
        lbl.setWrap(true);
        table.add(lbl)
                .pad(5).left().colspan(2)
                .grow()
                .row();

        table.setBackground(game.buttonBackground);

        ScrollPane scroll = new ScrollPane(table, skin, "default");
        scroll.setBounds(0, 80, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY-80);
        addActor(scroll);

        btn = new TextButton("Main Menu", skin, "default");
        btn.setBounds(WackyPong.SCREENSIZEX-190, 20, 160, 50);
        btn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.clink.play(game.volumeSounds);
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        btn.getStyle().up = game.buttonBackground;
        addActor(btn);
    }

    public void initializeText() {
        XmlReader xmlreader = new XmlReader();
        try {
            XmlReader.Element element = xmlreader.parse(Gdx.files.internal("helptext.xml"));
            helptext = new ObjectMap<String, String>();
            for (int i=0; i<element.getChildCount(); i++) {
                XmlReader.Element e = element.getChild(i);
                helptext.put(e.getName(), e.getText());
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.renderer.setProjectionMatrix(camera.combined);

        act(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        game.pauseMusic();
    }

    @Override
    public void resume() {
        game.playMusic();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
