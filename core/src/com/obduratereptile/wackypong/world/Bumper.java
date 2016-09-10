package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Bumper extends Hazard {
	public Sprite img;
	
	public Bumper(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprite("bumper");
	}
	
	public Bumper(World w, float x, float y) {
		this(w, x, y, 10);
	}
	
	@Override
	public Bumper copy() {
		return new Bumper(world, bounds.x, bounds.y, bounds.radius);
	}
	
	@Override
	public boolean collision(Ball ball) {
		if (!bounds.overlaps(ball.bounds)) return false;
		
		world.game.playBlip();
		bounce(ball);
		return true;
	}
	
	@Override
	public void update(float deltaTime) {
		return;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(img, getX(), getY(), bounds.radius * 2, bounds.radius * 2);
	}

	@Override
	protected void positionChanged() {
		if (bounds==null) return;
		bounds.x = getX() + bounds.radius;
		bounds.y = getY() + bounds.radius;
	}
}
