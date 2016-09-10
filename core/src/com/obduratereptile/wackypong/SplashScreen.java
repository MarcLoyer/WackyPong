package com.obduratereptile.wackypong;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SplashScreen extends Stage implements Screen {
	public WackyPong game;
	public OrthographicCamera camera;
	
	public Texture texLogo;
	public Sprite logo;
	public Music logoSound;
	public long startTime;
	
	public SplashScreen(WackyPong g) {
		super(new FitViewport(WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY));
		Gdx.input.setInputProcessor(this);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		
		game = g;
		startTime = TimeUtils.millis();
	}
	
	@Override
	public void show() {
		game.volume = game.getVolume();
		game.volumeSounds = game.getVolumeSounds();
		game.numPlayers = game.getNumPlayers();
		game.difficultyLevel = game.getDifficultyLevel();
		
		game.manager = new AssetManager();

		// load our font
		game.manager.load("my.fnt", BitmapFont.class);
		
		// load our textures
		game.manager.load("atlas/textures.pack.atlas", TextureAtlas.class);
		game.manager.load("mainbackground.png", Texture.class);

		// load our music and sound effects
		game.manager.load("sounds/blip1.wav", Sound.class);
		game.manager.load("sounds/blip2.wav", Sound.class);
		game.manager.load("sounds/blip3.wav", Sound.class);
		game.manager.load("sounds/boop1.wav", Sound.class);
		game.manager.load("sounds/boop2.wav", Sound.class);
		game.manager.load("sounds/boop3.wav", Sound.class);
		game.manager.load("sounds/boop4.wav", Sound.class);
		game.manager.load("sounds/clink.wav", Sound.class);
		game.manager.load("sounds/paddle.wav", Sound.class);
		game.manager.load("sounds/score1.wav", Sound.class);
		game.manager.load("sounds/score2.wav", Sound.class);
		game.manager.load("sounds/deploy1.wav", Sound.class);
		game.manager.load("sounds/deploy2.wav", Sound.class);
		game.manager.load("sounds/launch1.wav", Sound.class);
		game.manager.load("sounds/launch2.wav", Sound.class);
		game.manager.load("sounds/launch3.wav", Sound.class);
		game.manager.load("sounds/launch4.wav", Sound.class);

		game.manager.load("music/351774__cybermad__pixel-song-2.ogg", Music.class);
		
		game.batch = new SpriteBatch();
		game.renderer = new ShapeRenderer();
		
		// We load this texture locally because it must be displayed while the
		// assetManager is loading everything else :)
		texLogo = new Texture(Gdx.files.internal("obduratereptile.png"));
		logo = new Sprite(texLogo);
		float x = WackyPong.SCREENSIZEX - logo.getWidth();
		float y = WackyPong.SCREENSIZEY - logo.getHeight();
		logo.setPosition(x/2, y/2);
		
		logoSound = Gdx.audio.newMusic(Gdx.files.internal("rattle.ogg"));
		logoSound.setVolume(game.volume);
		logoSound.play();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		logo.draw(game.batch);
		game.batch.end();
		
		if (game.manager.update()) {
			// done loading assets, let's move on
			game.font = game.manager.get("my.fnt", BitmapFont.class);
			game.atlas = game.manager.get("atlas/textures.pack.atlas", TextureAtlas.class);

			game.blip1 = game.manager.get("sounds/blip1.wav", Sound.class);
			game.blip2 = game.manager.get("sounds/blip2.wav", Sound.class);
			game.blip3 = game.manager.get("sounds/blip3.wav", Sound.class);
			game.boop1 = game.manager.get("sounds/boop1.wav", Sound.class);
			game.boop2 = game.manager.get("sounds/boop2.wav", Sound.class);
			game.boop3 = game.manager.get("sounds/boop3.wav", Sound.class);
			game.boop4 = game.manager.get("sounds/boop4.wav", Sound.class);
			game.clink = game.manager.get("sounds/clink.wav", Sound.class);
			game.paddle = game.manager.get("sounds/paddle.wav", Sound.class);
			game.score1 = game.manager.get("sounds/score1.wav", Sound.class);
			game.score2 = game.manager.get("sounds/score2.wav", Sound.class);
			game.deploy1 = game.manager.get("sounds/deploy1.wav", Sound.class);
			game.deploy2 = game.manager.get("sounds/deploy2.wav", Sound.class);
			game.launch1 = game.manager.get("sounds/launch1.wav", Sound.class);
			game.launch2 = game.manager.get("sounds/launch2.wav", Sound.class);
			game.launch3 = game.manager.get("sounds/launch3.wav", Sound.class);
			game.launch4 = game.manager.get("sounds/launch4.wav", Sound.class);

			game.buttonBackground = new NinePatchDrawable(game.atlas.createPatch("buttonbackground"));
			game.buttonBackground2 = new NinePatchDrawable(game.buttonBackground).tint(new Color(0, .8f, 0, 1));
			game.mainBackground = new Sprite(game.manager.get("mainbackground.png", Texture.class));
			game.mainBackground.setBounds(0, 0, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);

			// show the splash for at least 5 seconds
			if ((TimeUtils.millis()-startTime) > 5000) {
				game.setScreen(new MainMenuScreen(game));
				dispose();
			}
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {
		logoSound.pause();
	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		texLogo.dispose();
		logoSound.dispose();
	}

}
