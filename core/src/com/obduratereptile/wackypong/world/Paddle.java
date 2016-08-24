package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.obduratereptile.wackypong.WackyPong;

public class Paddle extends Actor {
	private static float WIDTH = 20f;
	private static float HEIGHT = 100f;
	private static float SPEED = 800f;
	
	World world;
	public NinePatch img;
	public boolean isShrunk;
	public float target;
	
	public Paddle(World world, boolean mirrored) {
		super();
		setSize(WIDTH, HEIGHT);
		this.world = world;
		img = world.game.atlas.createPatch("paddle");
		this.isShrunk = false;
		this.target = WackyPong.SCREENSIZEY/2;
	}
	
	public void shrink() {
		isShrunk = true;
		setHeight(HEIGHT*2.0f/3.0f);
	}
	
	public void unshrink() {
		isShrunk = false;
		setHeight(HEIGHT);
	}
	
	@Override
	public void setHeight(float h) {
		float middle = h - img.getBottomHeight() - img.getTopHeight();
		if (middle<0) return;
		img.setMiddleHeight(middle);
		setY(getY()+(getHeight() - h)/2);
		super.setHeight(h);
	}
	
	public void moveTo(Vector3 pos) {
		// the paddle only moves in the y direction. I pass the
		// full vector in case I want to change that later. This
		// code offsets the touch by the height of the paddle and
		// checks the end conditions.
		float y = pos.y - getHeight()/2;
		if (y<0) y = 0f;
		if (y>getParent().getHeight()-getHeight()) y = getParent().getHeight()-getHeight();
		
		// This code just moves the paddle directly to the touch:
		//setY(y);
		
		// The paddle will move towards this target:
		target = y;
	}
	
	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		update(deltaTime);
	}
	
	public void update(float deltaTime) {
		// move towards the target...
		float y = getY();
		float dy = SPEED * deltaTime;
		
		if (y>target) {
			if (dy > (y-target))
				y = target;
			else
				y -= dy;
		} else {
			if (dy > (target-y))
				y = target;
			else
				y += dy;
		}
		
		setY(y);
		return;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		img.draw(batch, getX(), getY(), getWidth(), getHeight());
	}
}
