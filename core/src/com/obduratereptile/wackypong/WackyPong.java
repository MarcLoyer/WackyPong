package com.obduratereptile.wackypong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class WackyPong extends Game {
	public static final float SCREENSIZEX = 800;
	public static final float SCREENSIZEY = 480;

	private ActionResolver actionResolver;

	enum State {PAUSED, RUNNING, GAMEOVER}
	State gameState = State.PAUSED;

	public Preferences prefs;

	public int gameCount = 0;
	public int player1Score = 0;
	public int player2Score = 0;

	public AssetManager manager;
	public TextureAtlas atlas;
	public SpriteBatch batch;
	public ShapeRenderer renderer;
    public BitmapFont font;
    
    public float volume;
    public Music music = null;
    
    public float volumeSounds;
    public Sound blip1;
	public Sound blip2;
	public Sound blip3;
	public Sound boop1;
	public Sound boop2;
	public Sound boop3;
	public Sound boop4;
	public Sound clink;
	public Sound paddle;
	public Sound deploy1;
	public Sound deploy2;
	public Sound launch1;
	public Sound launch2;
	public Sound launch3;
	public Sound launch4;
	public Sound score1;
	public Sound score2;

	public int numPlayers;
	public int difficultyLevel;

	public NinePatchDrawable buttonBackground;
	public NinePatchDrawable buttonBackground2;

	public WackyPong(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	public void showAd() {
		actionResolver.showOrLoadInterstitialAd();
	}

	@Override
	public void create () {
		prefs = Gdx.app.getPreferences("com.obduratereptile.wackypong.prefs");
		setScreen(new SplashScreen(this));
	}
	
	public void setVolume(float v) {
		volume = v;
		prefs.putFloat("volume", volume);
		prefs.flush();
		if (music!=null) {
			music.setVolume(v);
		}
	}
	
	public float getVolume() {
		return prefs.getFloat("volume", 1.0f);
	}

	public void setVolumeSounds(float v) {
		volumeSounds = v;
		prefs.putFloat("volumeSounds", volumeSounds);
		prefs.flush();
	}

	public float getVolumeSounds() {
		return prefs.getFloat("volumeSounds", 1.0f);
	}

	public void setNumPlayers(int np) {
		numPlayers = np;
		prefs.putInteger("numPlayers", numPlayers);
		prefs.flush();
	}

	public int getNumPlayers() { return prefs.getInteger("numPlayers", 1); }

	public void setDifficultyLevel(int dl) {
		difficultyLevel = dl;
		prefs.putInteger("difficultyLevel", difficultyLevel);
		prefs.flush();
	}

	public int getDifficultyLevel() { return prefs.getInteger("difficultyLevel", 2); }

	public void playMusic() {
		String file = "music/351774__cybermad__pixel-song-2.ogg";
		if (music==null) {
			music = manager.get(file);
		}
		if (music.isPlaying()) return;
		music.setVolume(volume);
		music.setLooping(true);
		music.play();
	}
	
	public void pauseMusic() {
		if (music==null) return;
		music.pause();
	}
	
	public void stopMusic() {
		if (music==null) return;
		music.stop();
	}
	
	public void playBlip() {
		int i = (int)(Math.random() * 3);
		switch (i) {
		case 0: blip1.play(volumeSounds); break;
		case 1: blip2.play(volumeSounds); break;
		case 2: blip3.play(volumeSounds); break;
		}
	}
	
	public void playBoop() {
		int i = (int)(Math.random() * 4);
		switch (i) {
		case 0: boop1.play(volumeSounds); break;
		case 1: boop2.play(volumeSounds); break;
		case 2: boop3.play(volumeSounds); break;
		case 3: boop4.play(volumeSounds); break;
		}
	}

	public void playScore() {
		int i = (int)(Math.random() * 2);
		switch (i) {
			case 0: score1.play(volumeSounds); break;
			default: score2.play(volumeSounds); break;
		}
	}

	public void playDeployCannon() {
		deploy2.play(volumeSounds);
	}

	public void playRetractCannon() {
		deploy1.play(volumeSounds);
	}

	public void playFireBall() {
		int i = (int)(Math.random() * 4);
		switch (i) {
			case 0: launch1.play(volumeSounds); break;
			case 1: launch2.play(volumeSounds); break;
			case 2: launch3.play(volumeSounds); break;
			case 3: launch4.play(volumeSounds); break;
		}
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		renderer.dispose();
		manager.dispose();
		atlas.dispose();
		font.dispose();
		if (music!=null) music.dispose();
	}
	
	// convenience methods...
	public SpriteDrawable getSpriteDrawable(String filename) {
		return getSpriteDrawable(filename, Color.GRAY);
	}
	
	public SpriteDrawable getSpriteDrawable(String name, Color tint) {
		Sprite texReg = atlas.createSprite(name);
		SpriteDrawable texDraw = new SpriteDrawable(texReg);
		return texDraw.tint(tint);
	}
	
}
