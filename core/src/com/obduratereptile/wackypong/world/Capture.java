package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class Capture extends Hazard {
	public Array<Sprite> img;
	private boolean caught;
	private Ball ball;
	
	public Capture(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprites("capture");
		caught = false;
		ball = null;
	}
	
	public Capture(World w, float x, float y) {
		this(w, x, y, 20);
	}
	
	@Override
	public Capture copy() {
		return new Capture(world, bounds.x, bounds.y, bounds.radius);
	}

	@Override
	public void restart() {
		super.restart();
		caught = false;
	}

	@Override
	public boolean collision(Ball ball) {
		World world = (World)getParent();
		
		if (!bounds.overlaps(ball.bounds)) {
			if (ball.traversing == this) ball.traversing = null;
			return false;
		}
		if (ball.traversing == this) return false;
		
		world.game.playBoop();
		if (caught) {
			this.ball.velocity.x = ball.velocity.x;
			this.ball.velocity.y = ball.velocity.y;
			this.ball.traversing = this;
			this.ball = null;
			bounce(ball);
			caught = false;
		} else {
			ball.setBallPosition(bounds.x, bounds.y);
			ball.setVelocity(0,0);
			ball.traversing = this;
			this.ball = ball;
			caught = true;
			world.launchBall(ball.lastHitBy);
		}
		return true;
	}
	
	@Override
	public void update(float deltaTime) {
		return;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (caught) {
			batch.draw(img.get(1), getX(), getY(), bounds.radius * 2, bounds.radius * 2);
		} else {
			batch.draw(img.get(0), getX(), getY(), bounds.radius * 2, bounds.radius * 2);
		}
	}

	@Override
	protected void positionChanged() {
		if (bounds==null) return;
		bounds.x = getX() + bounds.radius;
		bounds.y = getY() + bounds.radius;
	}
}
