package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public class Spinner extends Hazard {
	private final float SPEED = 720;
	private final float ANGLE = 30;
	
	public Sprite img;
	public float angle;
	
	public Spinner(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprite("spinner"); //TODO: add spin to the ball to make it curve away
		angle = 0;
		img.setPosition(x-radius, y-radius);
		img.setSize(radius*2,  radius*2);
		img.setOrigin(radius,  radius);
		img.setRotation(angle);
	}
	
	public Spinner(World w, float x, float y) {
		this(w, x, y, 10);
	}
	
	@Override
	public Spinner copy() {
		return new Spinner(world, bounds.x, bounds.y, bounds.radius);
	}
	
	@Override
	public boolean collision(Ball ball) {
		if (!bounds.overlaps(ball.bounds)) {
			if (ball.traversing == this) ball.traversing = null;
			return false;
		}
		if (ball.traversing == this) return false;
		
		world.game.playBlip();
		bounce(ball);

		// add spin to the ball to make it curve
		ball.addToSpin((float)Math.random() * 60 - 30);

		//rotate the velocity vector by a random amount
		//TODO - this may cause collision captures
		float degrees = ((float)Math.random() * 2*ANGLE) - ANGLE;
		ball.velocity.rotate(degrees);
		ball.traversing = this;
		return true;
	}
	
	@Override
	public void update(float deltaTime) {
		angle += deltaTime * SPEED;
		img.setRotation(angle);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		img.draw(batch);
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		img.setPosition(getX(), getY());
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		img.setPosition(getX(), getY());
	}
}
