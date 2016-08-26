package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.obduratereptile.wackypong.WackyPong;

public class World extends Group {
	public WackyPong game;
	
	public Sprite centerlineImage;
	
	public Cannon cannon;
	public Array<Ball> ball = new Array<Ball>(false, 10);
	public Paddle[] paddle = new Paddle[2];
	public Hazard[] hazard;
	public int numHazards;
	
	public World(WackyPong game) {
		this.game = game;
		
		// initialize the game objects...
		game.player1Score = 0;
		game.player2Score = 0;
		cannon = new Cannon(this, WackyPong.SCREENSIZEX/2, WackyPong.SCREENSIZEY, 40.0f);
		addActor(cannon);
		launchBall(0);
		
		paddle[0] = new Paddle(this, false);
		paddle[1] = new Paddle(this, true);
		addActor(paddle[0]);
		addActor(paddle[1]);
		
		numHazards = 0;
		hazard = new Hazard[30];
		
		// load our images...
		centerlineImage = game.atlas.createSprite("dottedline");
	}
	
	public Hazard addHazard(Hazard hazard) {
		if (numHazards>30) return null;
		this.hazard[numHazards++] = hazard;
		
		addActor(hazard);
		return hazard;
	}
	
	public Hazard removeHazard(Hazard hazard) {
		boolean found = false;
		for (int i=0; i<numHazards; i++) {
			if (this.hazard[i]==hazard) found = true;
			if (found) {
				if (i<(numHazards-1)) {
					this.hazard[i] = this.hazard[i+1]; // shift everything down one spot
				} else {
					this.hazard[i] = null; // just delete the last entry
				}
			}
		}
		if (!found) return null;
		--numHazards;
		
		removeActor(hazard);
		return hazard;
	}
	
	public String toString() {
		String s = "" + numHazards + "\n";
		for (int i=0; i<numHazards; i++) {
			Hazard h = hazard[i];
			s += h.getClass().getSimpleName() + "(" + h.bounds.x + ", " + h.bounds.y + ", " + h.bounds.radius + ")";
			s += " // (" + h.getX() + ", " + h.getY() + ", " + h.getWidth() + ", " + h.getHeight() + ")";
			s += "\n";
		}
		return s;
	}
	
	public void write(String filename) {
		FileHandle handle = Gdx.files.local(filename);
		String text = getHazardsString();
		handle.writeString(text, false);
	}
	
	private String getHazardsString() {
		String text = "";
		for (int i=0; i<numHazards; i++) {
			text += hazard[i].getString() + "\n";
		}
		return text;
	}

	public void read(String filename) {
		FileHandle handle = Gdx.files.local(filename);
		String text = handle.readString();
		clearHazards();
		parseString(text);
	}
	
	private void clearHazards() {
		//might as well clear out the balls too...
		for (int i=0; i<ball.size; i++) {
			removeActor(ball.get(i));
		}
		ball.clear();
		
		for (int i=numHazards-1; i>=0; i--) {
			removeActor(hazard[i]);
			hazard[i] = null;
		}
		numHazards = 0;
	}

	private void parseString(String text) {
		String[] lines = text.split("\n");
		
		for(String line: lines) {
			String[] tokens = line.split("[\\(\\) ,;]+");
			if (tokens.length != 4) continue;
			
			String h = tokens[0];
			float x = Float.parseFloat(tokens[1]);
			float y = Float.parseFloat(tokens[2]);
			float r = Float.parseFloat(tokens[3]);
			if (h.equals("Bumper")) { addHazard(new Bumper(this, x, y, r)); continue; }
			if (h.equals("PinballBumper")) { addHazard(new PinballBumper(this, x, y, r)); continue; }
			if (h.equals("Capture")) { addHazard(new Capture(this, x, y, r)); continue; }
			if (h.equals("Shrink")) { addHazard(new Shrink(this, x, y, r)); continue; }
			if (h.equals("Spinner")) { addHazard(new Spinner(this, x, y, r)); continue; }
			if (h.equals("Warp")) { addHazard(new Warp(this, x, y, r)); continue; }
		}
	}

	@Override
	public void sizeChanged() {
		updateGameObjects();
	}
	
	@Override
	public void positionChanged() {
		updateGameObjects();
	}
	
	public void updateGameObjects() {
		float w = getWidth();
		float h = getHeight();
		
		// update the position of the paddles and the cannon
		paddle[0].setX(0.0f);
		paddle[0].setY((h-paddle[0].getHeight())/2);
		paddle[1].setX(w - paddle[1].getWidth());
		paddle[1].setY((h-paddle[0].getHeight())/2);
		cannon.setX(w/2 - cannon.getWidth()/2);
		cannon.setY(h - cannon.getHeight());
	}
	
	public void collision(float deltaTime) {
		for (int i=0; i<ball.size; i++) {
			Ball bb = ball.get(i);
			checkFieldCollisions(deltaTime, bb);
			checkPaddleCollisions(deltaTime, bb);
			// ball to ball collisions
			for (int j=i+1; j<ball.size; j++) {
				bb.collision(deltaTime, ball.get(j));
			}
			checkGoal(bb);
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		drawField(batch);
		super.draw(batch, parentAlpha);
	}
	
	private void drawField(Batch batch) {
		batch.end();
		game.renderer.begin(ShapeType.Filled);
		game.renderer.setColor(Color.GREEN);
		game.renderer.rect(getX(), getY(), getWidth()*getScaleX(), getHeight()*getScaleY());
		game.renderer.end();
		game.renderer.begin(ShapeType.Line);
		game.renderer.setColor(Color.WHITE);
		game.renderer.rect(getX(), getY(), getWidth()*getScaleX(), getHeight()*getScaleY());
		game.renderer.end();
		
		batch.begin();
		//TODO: convert these to image and labels and just addActors().
		batch.draw(centerlineImage, getX() + getWidth()*getScaleX()/2-1, getY(), 3, getHeight()*getScaleY());
        game.font.draw(batch, ""+game.player1Score, getX() + (getWidth()/2-100)*getScaleX(), getY() + (getHeight()-40)*getScaleY());
        game.font.draw(batch, ""+game.player2Score, getX() + (getWidth()/2+100)*getScaleX(), getY() + (getHeight()-40)*getScaleY());
	}
	
	private void checkGoal(Ball ball) {
		// TODO: add sound effect for scoring a goal
		if (ball.bounds.x<(0+ball.bounds.radius)) { // player2 scored!
			game.player2Score += 1;
			removeBall(ball);
			if (noMovingBalls()) launchBall(0);
					}
		if (ball.bounds.x>(WackyPong.SCREENSIZEX-ball.bounds.radius)) { // player1 scored!
			game.player1Score += 1;
			removeBall(ball);
			if (noMovingBalls()) launchBall(1);
		}
	}
	
	public void removeBall(Ball b) {
		ball.removeValue(b, true);
		removeActor(b);
	}
	
	public void removeAllBalls() {
		for (int i=0; i<ball.size; i++) {
			removeActor(ball.get(i));
		}
		ball.clear();
	}
	
	public void addBall(Ball b) {
		ball.add(b);
		addActor(b);
	}
	
	/**
	 * Checks all existing balls to see if there are any that are uncaptured
	 * @return true if no balls are moving
	 */
	public boolean noMovingBalls() {
		ArrayIterator<Ball> b = new ArrayIterator<Ball>(ball);
		while (b.hasNext()) {
			if (!b.next().velocity.isZero()) return false;
		}
		return true;
	}

	public void launchBall(int player) {
		if (player==0) {
			cannon.setAngles(85, 5);
		} else if (player==1) {
			cannon.setAngles(-5, -85);
		} else {
			cannon.setAngles(85, -85);
		}
		cannon.show(player);
	}
	private void checkPaddleCollisions(float deltaTime, Ball ball) {
		if (paddle[0].collision(ball)) {
			game.paddle.play(game.volumeSounds);
			ball.hit(0);
		}

		if (paddle[1].collision(ball)) {
			game.paddle.play(game.volumeSounds);
			ball.hit(1);
		}

	}
	/**
	 * We check if the ball hit the top or bottom of the screen, or any of the hazards
	 * on the field. If it hit, we change the velocity of the ball and also move it
	 * away (a little bit) from the object it hit. This prevents double detections.
	 * @param deltaTime - the time since the last update
	 * @param ball - the ball
	 */
	private void checkFieldCollisions(float deltaTime, Ball ball) {
		// top and bottom edges
		if (ball.bounds.y<(0+ball.bounds.radius)) {
			ball.velocity.y *= -1;
			// TODO: move the ball off the wall
			ball.act(deltaTime); // this doesn't work - need to actually reposition the ball
			return;
		}
		if (ball.bounds.y>(WackyPong.SCREENSIZEY-ball.bounds.radius)) {
			ball.velocity.y *= -1;
			// TODO: move the ball off the wall
			ball.act(deltaTime); // this doesn't work - need to actually reposition the ball
			return;
		}
		
		for (int i=0; i<numHazards; i++) {
			if (hazard[i].collision(ball)) {
				// TODO: move the ball off the hazard - this needs to move into the hazard.collision method
				ball.act(deltaTime); // this doesn't work - need to actually reposition the ball
				return;
			}
		}
	}
	
	public void generateHazards() {
		int numHaz = 2 + (int)((Math.random()+Math.random()+Math.random())*10.0/3.0);
		int warpCnt = 0;
		Vector2 pos;
		for (int i=0; i<numHaz; i++) {
			pos = genHazardPos();
			if (pos.x == -1) continue;
			int typeHaz = (int)(Math.random()*6.0);
			createHazard(typeHaz, pos);
			if (typeHaz==5) warpCnt++;
		}
		// Warp hazards only work if there are more than 1, so...
		if (warpCnt==1) {
			pos = genHazardPos();
			if (pos.x == -1) {
				// damn! we have to drop the existing warp hazard
				for (int i=0; i<numHazards; i++) {
					if (hazard[i] instanceof Warp) {
						removeHazard(hazard[i]);
						break;
					}
				}
			}
			createHazard(5, pos);
		}
	}
	
	private void createHazard(int typeHaz, Vector2 pos) {
		switch (typeHaz) {
		case 0: addHazard(new Bumper(this, pos.x, pos.y, 20)); break;
		case 1: addHazard(new Capture(this, pos.x, pos.y, 20)); break;
		case 2: addHazard(new PinballBumper(this, pos.x, pos.y, 20)); break;
		case 3: addHazard(new Shrink(this, pos.x, pos.y, 20)); break;
		case 4: addHazard(new Spinner(this, pos.x, pos.y, 20)); break;
		case 5: addHazard(new Warp(this, pos.x, pos.y, 20)); break;
		}
	}
	
	public Vector2 genHazardPos() {
		// place the hazard in a mostly central part of the field, not too close
		// to any other hazard
		float minX = WackyPong.SCREENSIZEX *0.2f;
		float maxX = WackyPong.SCREENSIZEX *0.8f;
		float minY = WackyPong.SCREENSIZEY *0.1f;
		float maxY = WackyPong.SCREENSIZEY *0.9f;
		Vector2 pos = new Vector2(rand(minX, maxX), rand(minY,maxY));
		int loopCount = 0;
		pos.set(rand(minX, maxX), rand(minY,maxY));
		while (tooClose(pos) && (loopCount<10)) {
			loopCount++;
			pos.set(rand(minX, maxX), rand(minY,maxY));
		}
		if (loopCount >= 10) return new Vector2(-1,-1); //give up
		return pos;
	}
	
	private float rand(float min, float max) {
		return (float)(Math.random() * (max-min) + min);
	}
	
	private boolean tooClose(Vector2 pos) {
		Vector2 dist = new Vector2();
		for (int i=0; i<numHazards; i++) {
			dist.set(pos);
			dist.sub(hazard[i].bounds.x, hazard[i].bounds.y);
			if (dist.len2() < 100.0f*100.0f) return true;
		}
		return false;
	}
}
