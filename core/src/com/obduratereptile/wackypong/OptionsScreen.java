package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.wackypong.LevelSelector.IndexedButton;
import com.obduratereptile.wackypong.LevelSelector.LevelSelectorListener;

public class OptionsScreen extends Stage implements Screen, LevelSelectorListener {
	public WackyPong game;
	
	public OrthographicCamera camera;
	public Vector3 touchPos;
	
	public Skin skin;
	public LevelSelector levelSelector;
	public SlideControl soundControl;
	public SlideControl musicControl;
	public RadioButton numPlayers;
	public int numPlayersInt;
	
	public OptionsScreen(WackyPong g) {
		super(new FitViewport(WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY));
		Gdx.input.setInputProcessor(this);
		
		this.game = g;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		
		touchPos = new Vector3(0, 0, 0);
		
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		SpriteDrawable file = game.getSpriteDrawable("file");
		SpriteDrawable emptyfile = game.getSpriteDrawable("emptyfile");
		
		levelSelector = new LevelSelector("Select a saved field...", skin, "default", 2, 5, file, emptyfile, game);
		levelSelector.setBounds(WackyPong.SCREENSIZEX/2-250, WackyPong.SCREENSIZEY-290, 500, 200);
		addActor(levelSelector);
		levelSelector.addListener(this);

		TextButton btn = new TextButton("...or play a generated field", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX/2-150, WackyPong.SCREENSIZEY-350, 300, 30);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new GameScreen(game, Integer.parseInt(numPlayers.getSelection()), -1));
	            dispose();
			}
		});
		addActor(btn);
		
		SpriteDrawable mute = game.getSpriteDrawable("Mute_Icon");
		SpriteDrawable speaker = game.getSpriteDrawable("Speaker_Icon");
		
		soundControl = new SlideControl(skin, "sound f/x", speaker, mute, mute);
		soundControl.setBounds(30, 80, 300, 30);
		soundControl.setValue(game.volumeSounds);
		addActor(soundControl);
		soundControl.slider.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				float volume = soundControl.slider.getValue();
				game.setVolumeSounds(volume);
			}
		});
		
		musicControl = new SlideControl(skin, "music", speaker, mute, mute);
		musicControl.setBounds(30, 30, 300, 30);
		musicControl.setValue(game.volume);
		addActor(musicControl);
		musicControl.slider.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				float volume = musicControl.slider.getValue();
				game.setVolume(volume);
			}
		});
		
		numPlayers = new RadioButton(
				"Number of Players", skin, "default", 
				// TODO: add "2 (network)" option
				"2", "1", "0"
				);
		numPlayers.setBounds(360, 30, 150, 30);
		addActor(numPlayers);
		numPlayers.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				TextButton btn = ((RadioButton)event.getListenerActor()).buttonGroup.getChecked();
				numPlayersInt = Integer.parseInt(btn.getText().toString());
			}
		});

		//TODO: add AI difficulty levels

		btn = new TextButton("Main Menu", skin, "default");
		btn.setBounds(WackyPong.SCREENSIZEX-130, 30, 100, 30);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new MainMenuScreen(game));
	            dispose();
			}
		});
		addActor(btn);
	}
	
	@Override
	public void show() {
		if (!Gdx.files.local("saves/").exists()) {
			Gdx.files.local("saves/").mkdirs();
		}
		for (int i=0; i<10; i++) {
			if (!Gdx.files.local("saves/field_"+i+".txt").exists()) {
				IndexedButton btn = (IndexedButton)levelSelector.getCells().get(i+1).getActor();
				btn.setEmpty(true);
				btn.setDisabled(true);
			}
		}
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
	
	public void selected(int index) {
		game.clink.play(game.volumeSounds);
		game.setScreen(new GameScreen(game, Integer.parseInt(numPlayers.getSelection()), index));
		dispose();
	}
}
