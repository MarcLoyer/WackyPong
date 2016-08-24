package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class PinballBumper extends Hazard {
	public Sprite img;
	
	public PinballBumper(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprite("pinballbumper");
	}
	
	public PinballBumper(World w, float x, float y) {
		this(w, x, y, 10);
	}
	
	@Override
	public PinballBumper copy() {
		return new PinballBumper(world, bounds.x, bounds.y, bounds.radius);
	}
	
	@Override
	public boolean collision(Ball ball) {
		if (!bounds.overlaps(ball.bounds)) return false;
		
		world.game.playBlip();
		bounce(ball);
		ball.speedup(2);
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
