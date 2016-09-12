package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
	public RadioButton difficultyLevel;
	public int difficultyLevelInt;

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
		levelSelector.setPosition(20, WackyPong.SCREENSIZEY-280);
		levelSelector.setSize(490, 250); // hmm. this is not the right way...
		addActor(levelSelector);
		levelSelector.addListener(this);

		TextButton btn = new TextButton("...or play a generated field", skin, "default");
		btn.setBounds(20, WackyPong.SCREENSIZEY-330, 490, 50);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new GameScreen(game, numPlayersInt, difficultyLevelInt, -1));
	            dispose();
			}
		});
		btn.setBackground(game.buttonBackground);
		addActor(btn);
		
		SpriteDrawable mute = game.getSpriteDrawable("Mute_Icon");
		SpriteDrawable speaker = game.getSpriteDrawable("Speaker_Icon");
		
		soundControl = new SlideControl(skin, "sound f/x", speaker, mute, mute);
		soundControl.setBounds(20, 70, 410, 50);
		soundControl.setValue(game.volumeSounds);
		soundControl.setBackground(game.buttonBackground);
		addActor(soundControl);
		soundControl.slider.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				float volume = soundControl.slider.getValue();
				game.setVolumeSounds(volume);
			}
		});
		
		musicControl = new SlideControl(skin, "music", speaker, mute, mute);
		musicControl.setBounds(20, 20, 410, 50);
		musicControl.setValue(game.volume);
		musicControl.setBackground(game.buttonBackground);
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
		numPlayers.setBounds(520, 360, 270, 80);
		numPlayers.setChecked(game.numPlayers);
		numPlayersInt = Integer.parseInt(numPlayers.buttonGroup.getChecked().getText().toString());
		numPlayers.setBackground(game.buttonBackground);
		addActor(numPlayers);
		numPlayers.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setNumPlayers(numPlayers.getChecked());
				TextButton btn = ((RadioButton)event.getListenerActor()).buttonGroup.getChecked();
				numPlayersInt = Integer.parseInt(btn.getText().toString());
			}
		});

		difficultyLevel = new RadioButton(
			"AI Difficulty Level", skin, "default", true,
				"You'll lose",
				"You might win",
				"You'll prbly win",
				"Enjoy your win :)"
		);
		difficultyLevel.setBounds(520, 160, 270, 200);
		difficultyLevel.setChecked(game.difficultyLevel);
		String t = difficultyLevel.buttonGroup.getChecked().getText().toString();
		if (t.equals("You'll lose")) difficultyLevelInt = 0;
		if (t.equals("You might win")) difficultyLevelInt = 1;
		if (t.equals("You'll prbly win")) difficultyLevelInt = 2;
		if (t.equals("Enjoy your win :)")) difficultyLevelInt = 3;

		difficultyLevel.setBackground(game.buttonBackground);
		addActor(difficultyLevel);
		difficultyLevel.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setDifficultyLevel(difficultyLevel.getChecked());
				TextButton btn = ((RadioButton)event.getListenerActor()).buttonGroup.getChecked();
				String t = btn.getText().toString();
				if (t.equals("You'll lose")) difficultyLevelInt = 0;
				if (t.equals("You might win")) difficultyLevelInt = 1;
				if (t.equals("You'll prbly win")) difficultyLevelInt = 2;
				if (t.equals("Enjoy your win :)")) difficultyLevelInt = 3;
			}
		});


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
		game.setScreen(new GameScreen(game, numPlayersInt, difficultyLevelInt, index));
		dispose();
	}
}
