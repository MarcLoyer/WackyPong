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
import com.obduratereptile.wackypong.world.Ball;
import com.obduratereptile.wackypong.world.World;

public class MainMenuScreen extends Stage implements Screen {
	public WackyPong game;

	public OrthographicCamera camera;
	public Vector3 touchPos;

	public TitleLetter[] letters = new TitleLetter[9];
	public Ball ball;

	public MainMenuScreen(WackyPong g) {
		super(new FitViewport(WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY));
		Gdx.input.setInputProcessor(this);

		this.game = g;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		touchPos = new Vector3(0, 0, 0);

		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

		TextButton btn = new TextButton("Level Editor", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX / 2 - 90, WackyPong.SCREENSIZEY - 300, 180, 50);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new EditorScreen(game));
				dispose();
			}
		});
		btn.getStyle().up = game.buttonBackground;
		addActor(btn);

/*
		btn = new TextButton("Ad", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX / 2 + 150, WackyPong.SCREENSIZEY - 300, 180, 50);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.showAd();
			}
		});
		btn.getStyle().up = game.buttonBackground;
		addActor(btn);
*/

		btn = new TextButton("Play", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX / 2 - 90, WackyPong.SCREENSIZEY - 370, 180, 50);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new OptionsScreen(game));
				dispose();
			}
		});
		btn.getStyle().up = game.buttonBackground;
		addActor(btn);

		btn = new TextButton("How To", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX / 2 - 90, WackyPong.SCREENSIZEY - 440, 180, 50);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new HelpScreen(game));
				dispose();
			}
		});
		btn.getStyle().up = game.buttonBackground;
		addActor(btn);

		World world = new World(game, 0);

		letters[0] = new TitleLetter(game, "W"); addActor(letters[0]);
		letters[1] = new TitleLetter(game, "A"); addActor(letters[1]);
		letters[2] = new TitleLetter(game, "C"); addActor(letters[2]);
		letters[3] = new TitleLetter(game, "K"); addActor(letters[3]);
		letters[4] = new TitleLetter(game, "Y"); addActor(letters[4]);
		letters[5] = new TitleLetter(game, "P"); addActor(letters[5]);
		letters[6] = new TitleLetter(game, "O"); addActor(letters[6]);
		letters[7] = new TitleLetter(game, "N"); addActor(letters[7]);
		letters[8] = new TitleLetter(game, "G"); addActor(letters[8]);
		ball = new Ball(world, WackyPong.SCREENSIZEX/2, WackyPong.SCREENSIZEY/2);
		ball.setVelocity(200, 200);
		ball.lateralSpin = 0;
		addActor(ball);
	}

	@Override
	public void show() {
		game.playMusic();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.25f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.renderer.setProjectionMatrix(camera.combined);

		act(delta);
		if (ball.bounds.x < 10) { ball.bounds.x = 10; ball.velocity.x *= -1; }
		if (ball.bounds.x > WackyPong.SCREENSIZEX-10) { ball.bounds.x = WackyPong.SCREENSIZEX-10; ball.velocity.x *= -1; }
		if (ball.bounds.y < 10) { ball.setY(0); ball.velocity.y *= -1; }
		if (ball.bounds.y > WackyPong.SCREENSIZEY-10) { ball.bounds.y = WackyPong.SCREENSIZEY-10; ball.velocity.y *= -1; }
		for (int i=0; i<9; i++) {
			letters[i].collision(ball);
		}

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
