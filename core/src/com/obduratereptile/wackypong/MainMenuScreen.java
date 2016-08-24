package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen extends Stage implements Screen {
	public WackyPong game;

	public OrthographicCamera camera;
	public Vector3 touchPos;
	
	public MainMenuScreen(WackyPong g) {
		super(new FitViewport(WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY));
		Gdx.input.setInputProcessor(this);

		this.game = g;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		touchPos = new Vector3(0, 0, 0);

		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

		Label lbl = new Label("Wacky Pong", skin, "default");
		lbl.setBounds(WackyPong.SCREENSIZEX / 2 - 200, WackyPong.SCREENSIZEY - 200, 400, 100);
		lbl.setAlignment(Align.center);
		lbl.setFontScale(3.0f);
		addActor(lbl);

		TextButton btn = new TextButton("Level Editor", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX / 2 - 50, WackyPong.SCREENSIZEY - 300, 100, 30);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new EditorScreen(game));
				dispose();
			}
		});
		addActor(btn);

		btn = new TextButton("Play", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX / 2 - 50, WackyPong.SCREENSIZEY - 350, 100, 30);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new OptionsScreen(game));
				dispose();
			}
		});
		addActor(btn);
	}

	@Override
	public void show() {
		game.playMusic();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.renderer.setProjectionMatrix(camera.combined);

		act(delta);
		draw();
	}

	@Override
	public void resize(int width, int height) {
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
	}

}
