package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.obduratereptile.wackypong.WackyPong;

public class Shrink extends Hazard {
	private final float SPEED = -360;
	private final float ANGLE = 30;

	public Sprite img;
	public float angle;

	public Shrink(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprite("shrink");
		angle = 0;
		img.setBounds(x - radius, y - radius, radius * 2, radius * 2);
		img.setOrigin(radius,  radius);
	}
	
	public Shrink(World w, float x, float y) {
		this(w, x, y, 10);
	}
	
	@Override
	public Shrink copy() {
		return new Shrink(world, bounds.x, bounds.y, bounds.radius);
	}

	@Override
	public void setRadius(float radius) {
		float x = getX();
		float y = getY();
		float r = getRadius();

		img.setBounds(x+r-radius, y+r-radius, radius*2, radius*2);
		img.setOrigin(radius,  radius);
		super.setRadius(r);
	}

	@Override
	public boolean collision(Ball ball) {
		World world = (World)getParent();

		if (super.collision(ball)) {
			if (ball.lastHitBy == -1) return true;
			if (world.paddle[ball.lastHitBy].isShrunk) {
				world.paddle[ball.lastHitBy].unshrink();
			} else {
				world.paddle[(ball.lastHitBy+1)%2].shrink();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void update(float deltaTime) {
		angle += deltaTime * SPEED;
		img.setRotation(angle);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		img.setPosition(getX(), getY());
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

	@Override
	protected void positionChanged() {
		if (bounds==null) return;
		bounds.x = getX() + bounds.radius;
		bounds.y = getY() + bounds.radius;
		if (img==null) return;
		img.setPosition(getX(), getY());
	}
}
