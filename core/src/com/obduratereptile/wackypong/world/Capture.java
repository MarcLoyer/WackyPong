package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Capture extends Hazard {
	public Sprite img;
	private boolean caught;
	private Ball ball;
	
	public Capture(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprite("capture");
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
	public boolean collision(Ball ball) {
		World world = (World)getParent();
		
		if (!bounds.overlaps(ball.bounds)) {
			if (ball.traversing == this) ball.traversing = null;
			return false;
		}
		if (ball.traversing == this) return false;
		
		world.game.playBoop();
		if (caught) {
			this.ball.velocity.x = ball.velocity.x; //TODO: what angle should the ball come out at?
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
			world.launchBall(-1); //TODO: the player who hit the ball into the hazard gets to launch the new one
		}
		return true;
	}
	
	@Override
	public void update(float deltaTime) {
		return;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(img, bounds.x - bounds.radius, bounds.y - bounds.radius, bounds.radius * 2, bounds.radius * 2);
	}
}
