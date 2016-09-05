package com.obduratereptile.wackypong;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.obduratereptile.wackypong.world.Ball;
import com.obduratereptile.wackypong.world.Capture;
import com.obduratereptile.wackypong.world.Spinner;
import com.obduratereptile.wackypong.world.Warp;
import com.obduratereptile.wackypong.world.World;

public class GameScreen extends Stage implements Screen {
	public WackyPong game;
	public OrthographicCamera camera;
	public Vector3 touchPos;
	
	static public final int RUNNING = 0;
	static public final int PAUSED = 1;
	public int gameState;
	
	public Skin skin;
	public ImageButton btnPause;
	public Dialog dialog;
	
	public World world;
	public int savedFieldIndex;
	public int numPlayers;
	
	public GameScreen(WackyPong g, int numPlayers) {
		super(new FitViewport(800,480));
		Gdx.input.setInputProcessor(this);
		
		this.game = g;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		touchPos = new Vector3();
		
		world = new World(game);
		addActor(world);
		world.setBounds(0.0f, 0.0f, WackyPong.SCREENSIZEX, WackyPong.SCREENSIZEY);
		savedFieldIndex = -1;
		
		// add the UI controls
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		btnPause = new ImageButton(game.getSpriteDrawable("pause"));
		btnPause.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				showPauseDialog("Paused");
			}
		});
		addActor(btnPause);
		btnPause.setBounds(WackyPong.SCREENSIZEX/2-16, 0, 32, 32);

		this.gameState = RUNNING;
		this.numPlayers = numPlayers;
	}
	
	public GameScreen(WackyPong g, int numPlayers, int savedFieldIndex) {
		this(g, numPlayers);
		this.savedFieldIndex = savedFieldIndex;
		
		if (savedFieldIndex == -1) {
			// randomly generate a field
			world.generateHazards();

		} else {
			world.read("saves/field_"+savedFieldIndex+".txt");
		}
	}
	
	/**
	 * Launches a save file dialog that allows the user to select one of ten possible files.
	 * This methods returns immediately if the field was not randomly generated (because it
	 * must have been loaded from a file and so does not need to be saved).
	 */
	protected void saveField() {
		FileSaveDialog dialog = new FileSaveDialog(skin, game) {
			protected void result(Object obj) {
				if (selectedFile != -1) {
					String filename = "saves/field_" + selectedFile + ".txt";
					world.write(filename);
				}
				gameState = RUNNING;
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
		
		switch (gameState) {
		case(PAUSED):
			break;
		case(RUNNING):
			act(delta);
			break;
		}
		
		game.batch.begin();
		draw();
		game.batch.end();
		
		// get user input
		getPlayerInput();
		
		// check if the game is over
		if (game.player1Score >= 7) showPauseDialog("Player 1 wins!");
		if (game.player2Score >= 7) showPauseDialog("Player 2 wins!");
	}
	
	void showPauseDialog(String title) {
		if ((dialog != null) && (dialog.getStage() != null)) return;
		
		gameState = PAUSED;

		TextButton btn;

		dialog = new Dialog(title, skin, "default") {
			protected void result(Object obj) {
				String s = (String) obj;
				if (s.equals("resume")) {
					gameState = RUNNING;
				}
				if (s.equals("new game")) {
					if (++game.gameCount%5 == 0) game.showAd();
					restart();
					gameState = RUNNING;
				}
				if (s.equals("quit")) {
					if (++game.gameCount%5 == 0) game.showAd();
					game.setScreen(new MainMenuScreen(game));
					dispose();
				}
				if (s.equals("save"))
					saveField();
			}
		};
		if (title.equals("Paused")) {
			btn = new TextButton("resume", skin, "default");
			dialog.button(btn, "resume");
		} else {
			btn = new TextButton("new game", skin, "default");
			dialog.button(btn, "new game");
		}
		btn = new TextButton("quit", skin, "default");
		dialog.button(btn, "quit");
		if (savedFieldIndex == -1) {
			btn = new TextButton("save", skin, "default");
			dialog.button(btn, "save");
		}
		dialog.show(this);
	}
	
	public void restart() {
		game.player1Score = 0;
		game.player2Score = 0;
		world.restart();
	}
	
	/**
	 * Checks for touches/clicks and keyboard input.
	 */
	private void getPlayerInput() {
		
		// check multi-touch input for human players...
		for (int i=0; i<2; i++) {
			if(Gdx.input.isTouched(i)) {
				touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				camera.unproject(touchPos);
				
				// Check for gaming controls
				if (touchPos.x<WackyPong.SCREENSIZEX/2-60) { // left paddle
					if (numPlayers>0) world.paddle[0].moveTo(touchPos);
				}
				
				if (touchPos.x>WackyPong.SCREENSIZEX/2+60) { // right paddle
					if (numPlayers>1) world.paddle[1].moveTo(touchPos);
				}
				
				//TODO: this is debugging code - it moves the ball to the touch location
				//ball.get(0).x = touchPos.x; ball.get(0).y = touchPos.y;
			}
		}
		
		// check keyboard for human players
		if (numPlayers>0) {
			if (Gdx.input.isKeyPressed(Keys.Q)) world.paddle[0].moveTo(new Vector3(world.paddle[0].getX(), world.paddle[0].getY()+100, 0));
			if (Gdx.input.isKeyPressed(Keys.A)) world.paddle[0].moveTo(new Vector3(world.paddle[0].getX(), world.paddle[0].getY()-100, 0));
		}
		if (numPlayers>1) {
			if (Gdx.input.isKeyPressed(Keys.P)) world.paddle[1].moveTo(new Vector3(world.paddle[1].getX(), world.paddle[1].getY()+100, 0));
			if (Gdx.input.isKeyPressed(Keys.L)) world.paddle[1].moveTo(new Vector3(world.paddle[1].getX(), world.paddle[1].getY()-100, 0));
		}
		if (numPlayers>0) {
			if (!world.cannon.isHidden) {
				// ignore keypress if AI is supposed to launch
				if (Gdx.input.isKeyPressed(Keys.SPACE)) world.cannon.fire();
			}
		}
		
		// AI players...
		if (numPlayers == 2) return;
		// get the left-most and right-most ball
		Ball leftmost=null, rightmost=null;
		Iterator<Ball> b = world.ball.iterator();
		while (b.hasNext()) {
			Ball bb = b.next();
			if (leftmost==null) {
				if (bb.velocity.x != 0) leftmost = bb;
			} else {
				if ((bb.bounds.x < leftmost.bounds.x) && (bb.velocity.x < 0)) leftmost = bb;
			}
			if (rightmost==null) {
				if (bb.velocity.x != 0) rightmost = bb;
			} else {
				if ((bb.bounds.x > rightmost.bounds.x) && (bb.velocity.x > 0)) rightmost = bb;
			}
		}

		// TODO: make the AIs imperfect
		// - slow down the paddles?
		// - inaccurate ball prediction?
		if ((leftmost!=null) && (numPlayers == 0)) {
			world.paddle[0].moveTo(new Vector3(0, leftmost.bounds.y, 0));
		}
		if (rightmost!=null) {
			world.paddle[1].moveTo(new Vector3(0, rightmost.bounds.y, 0));
		}
		
		if (!world.cannon.isHidden) {
			if ((world.cannon.player==1) || (numPlayers==0)) {
				fireCannon();
			}
			
		}
	}
	
	private boolean firing = false;
	
	private void fireCannon() {
		if (firing) return;
		firing = true;
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				long time = (long)(1000 * Math.random()); //random time between 0 and 1 second
				try {
					Thread.sleep(1000+time);
				} catch (InterruptedException e) {
				}
				world.cannon.fire();
				firing = false;
			}
		});
		thread.start();
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
		skin.dispose();
	}

}
