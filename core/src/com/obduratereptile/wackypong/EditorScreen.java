package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.wackypong.world.Bumper;
import com.obduratereptile.wackypong.world.Capture;
import com.obduratereptile.wackypong.world.Hazard;
import com.obduratereptile.wackypong.world.PinballBumper;
import com.obduratereptile.wackypong.world.Shrink;
import com.obduratereptile.wackypong.world.Spinner;
import com.obduratereptile.wackypong.world.Warp;
import com.obduratereptile.wackypong.world.World;

public class EditorScreen extends Stage implements Screen {
	public WackyPong game;
	
	public OrthographicCamera camera;
	
	public Hazard haz[];
	public World world;
	
	public Skin skin;
	public Popup popup;
	public SetRadiusDialog dialog;
	public HazardDragListener hazardDragListener;
	
	public EditorScreen(WackyPong g) {
		super(new FitViewport(WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY));
		Gdx.input.setInputProcessor(this);
		
		this.game = g;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		TextButton btn;

		popup = new Popup();
		btn = popup.addMenuItem("delete", skin, "default");
		btn.getStyle().up = game.buttonBackground2;
		btn.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				game.clink.play(game.volumeSounds);
				world.removeHazard((Hazard)popup.target);
				return true;
			}
		});
		btn = popup.addMenuItem("set radius", skin, "default");
		btn.getStyle().up = game.buttonBackground2;
		btn.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				game.clink.play(game.volumeSounds);
				dialog.setTarget((Hazard)popup.target);
				dialog.show(event.getStage());
				
				return true;
			}
		});
		
		dialog = new SetRadiusDialog("Set radius", skin, "default");
		
		btn = new TextButton("Back", skin, "default");
		btn.setBounds(1 * WackyPong.SCREENSIZEX/4, WackyPong.SCREENSIZEY * 0.05f, 60, 30);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				game.setScreen(new MainMenuScreen(game));
	            dispose();
			}
		});
		btn.getStyle().up = game.buttonBackground;
		addActor(btn);
		
		btn = new TextButton("Load", skin, "default");
		btn.setBounds(2 * WackyPong.SCREENSIZEX/4, WackyPong.SCREENSIZEY * 0.05f, 60, 30);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				loadField();
			}
		});
		btn.getStyle().up = game.buttonBackground;
		addActor(btn);
		
		btn = new TextButton("Save", skin, "default");
		btn.setBounds(3 * WackyPong.SCREENSIZEX/4, WackyPong.SCREENSIZEY * 0.05f, 60, 30);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.clink.play(game.volumeSounds);
				saveField();
			}
		});
		btn.getStyle().up = game.buttonBackground;
		addActor(btn);
		
		world = new World(game, 0);
		addActor(world);
		world.setBounds(WackyPong.SCREENSIZEX * 0.2f, WackyPong.SCREENSIZEY * 0.2f, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		world.setScale(0.8f);
		
		buildHazardPalette();
	}
	
	private void buildHazardPalette() {
		hazardDragListener = new HazardDragListener();
		
		for (int i=0; i<6; i++) {
			float x = 60.0f;
			float y = WackyPong.SCREENSIZEY - 60.0f - i*75.0f;
			switch (i) {
			case(0): addToPalette(x, y, new Bumper(world, 0, 0, 20)); break;
			case(1): addToPalette(x, y, new PinballBumper(world, 0, 0, 20)); break;
			case(2): addToPalette(x, y, new Capture(world, 0, 0, 20)); break;
			case(3): addToPalette(x, y, new Shrink(world, 0, 0, 20)); break;
			case(4): addToPalette(x, y, new Spinner(world, 0, 0, 20)); break;
			case(5): addToPalette(x, y, new Warp(world, 0, 0, 20)); break;
			}
		}
	}
	
	private void addToPalette(float x, float y, Hazard h) {
		Label lbl = null;
		
		if (h instanceof Bumper) lbl = new Label("Bumper", skin, "default");
		if (h instanceof PinballBumper) lbl = new Label("Pinball Bumper", skin, "default");
		if (h instanceof Capture) lbl = new Label("Capture", skin, "default");
		if (h instanceof Shrink) lbl = new Label("Shrink", skin, "default");
		if (h instanceof Spinner) lbl = new Label("Spinner", skin, "default");
		if (h instanceof Warp) lbl = new Label("Warp", skin, "default");
		lbl.setAlignment(Align.center);
		lbl.setSize(100, 20);
		lbl.setPosition(x-50, y+20);
		addActor(lbl);
		
		h.moveTo(x, y);
		addActor(h);
		h.addListener(hazardDragListener);
	}
	
	protected void saveField() {
		FileSaveDialog dialog = new FileSaveDialog(skin, game) {
			protected void result(Object obj) {
				if (selectedFile != -1) {
					String filename = "saves/field_" + selectedFile + ".txt";
					world.write(filename);
				}
				super.result(obj);
			}
		};
		dialog.show(this);
	}

	protected void loadField() {
		FileLoadDialog dialog = new FileLoadDialog(skin, game) {
			protected void result(Object obj) {
				if (selectedFile != -1) {
					String filename = "saves/field_" + selectedFile + ".txt";
					world.read(filename);
					// add InputListeners to all the hazards so they can be modified
					for (int i = 0; i < world.numHazards; i++) {
						world.hazard[i].addListener(hazardDragListener);
					}
				}
				super.result(obj);
			}
		};
		dialog.show(this);
	}

	@Override
	public void show() {
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
	
	class HazardDragListener extends DragListener {
		Stage stage = null;
		Hazard hazard = null;
		Vector3 pos;
		
		@Override
		public void dragStart(InputEvent event, float x, float y, int pointer) {
			super.dragStart(event, x, y, pointer);
			Vector2 pos;
			
			stage = event.getStage();
			if (event.getTarget().getParent()==world) { // moving an existing hazard
				hazard = (Hazard)event.getTarget();
			} else { // placing a new hazard
				hazard = ((Hazard)event.getTarget()).copy();
			}
			pos = hazard.localToStageCoordinates(new Vector2(x, y));
			stage.addActor(hazard);
			hazard.moveTo(pos.x, pos.y);
		}
		
		@Override
		public void drag(InputEvent event, float x, float y, int pointer) {
			super.drag(event, x, y, pointer);
			//move the Hazard to the touch
			pos = new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0);
			camera.unproject(pos);
			hazard.moveTo(pos.x, pos.y);
			if (!popup.cancelTouchUp) {
				popup.cancelTouchUp = true;
			}
		}
		
		@Override
		public void dragStop(InputEvent event, float x, float y, int pointer) {
			super.dragStop(event, x, y, pointer);
			//place the hazard onto the world
			hazard.remove();
			world.removeHazard(hazard);
			Actor actor = stage.hit(pos.x, pos.y, false);
			if (actor == world) {
				Vector2 p = stageToWorldCoordinates(new Vector2(pos.x, pos.y));
				hazard.moveTo(p.x, p.y);
				world.addHazard(hazard);
				hazard.addListener(hazardDragListener);
				hazard.addListener(popup.getDefaultInputListener());
			} else {
				hazard = null; // user tried to place it on another game object, or outside the field
			}
		}
	}
	
	public Vector2 stageToWorldCoordinates(Vector2 v) {
		// hard-coded hack. Yuk.
		float x = WackyPong.SCREENSIZEX * 0.2f;
		float y = WackyPong.SCREENSIZEY * 0.2f;
		//float w = WackyPong.SCREENSIZEX;
		//float h = WackyPong.SCREENSIZEY;
		float scale = 1.0f/0.8f;
		
		Vector2 newV = new Vector2(v.x, v.y);
		newV.add(-x, -y);
		newV.scl(scale);
		
		return newV;
	}
}
