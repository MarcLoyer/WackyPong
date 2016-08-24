package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.obduratereptile.wackypong.WackyPong;

public class Shrink extends Hazard {
	public Sprite img;
	
	public Shrink(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprite("bumper");
		img.setBounds(bounds.x - bounds.radius, bounds.y - bounds.radius, bounds.radius * 2, bounds.radius * 2);
		img.setOrigin(bounds.radius,  bounds.radius);
	}
	
	public Shrink(World w, float x, float y) {
		this(w, x, y, 10);
	}
	
	@Override
	public Shrink copy() {
		return new Shrink(world, bounds.x, bounds.y, bounds.radius);
	}
	
	@Override
	public boolean collision(Ball ball) {
		World world = (World)getParent();
		
		if (!bounds.overlaps(ball.bounds)) return false;
		
		world.game.playBoop();
		bounce(ball);
		if (ball.lastHitBy == -1) return true;
		if (world.paddle[ball.lastHitBy].isShrunk) {
			world.paddle[ball.lastHitBy].unshrink();
		} else {
			world.paddle[(ball.lastHitBy+1)%2].shrink();
		}
		return true;
	}
	
	@Override
	public void update(float deltaTime) {
		return;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		scale();
		img.draw(batch);
	}
	
	/**
	 * poor man's animation - scale the hazard down and up based on the deltaTime
	 */
	float scaleFactor = 1.0f;
	float scaleSpeed = 2.5f;
	boolean scalingDown = true;
	
	private void scale() {
		float delta = Gdx.graphics.getDeltaTime();
		if (scalingDown) {
			scaleFactor -= delta * scaleSpeed;
			if (scaleFactor < 0.3f) {
				scaleFactor = 0.3f +(0.3f-scaleFactor);
				scalingDown = false;
			}
		} else {
			scaleFactor += delta * scaleSpeed;
			if (scaleFactor > 1.0f) {
				scaleFactor = 1.0f - (scaleFactor-1.0f);
				scalingDown = true;
			}
		}
		img.setScale(scaleFactor);
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
