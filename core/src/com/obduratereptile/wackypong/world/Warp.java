package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class Warp extends Hazard {
	public Array<Sprite> img;
	public Animation ani;
	private float elapsedTime = 0;
	public Warp sendTo;
	
	public Warp(World w, float x, float y, float radius) {
		super(w, x, y, radius);
		img = world.game.atlas.createSprites("warp");
		ani = new Animation(0.1f, img, Animation.PlayMode.LOOP_PINGPONG);
		sendTo = this; // in case someone only places one warp hazard on the field
	}
	
	public Warp(World w, float x, float y) {
		this(w, x, y, 10);
	}
	
	@Override
	public Warp copy() {
		return new Warp(world, bounds.x, bounds.y, bounds.radius);
	}
	
	@Override
	public void setParent(Group parent) {
		super.setParent(parent);
		if (parent==null) return;
		if (!(parent instanceof World)) return;
		
		// go through the list of hazards, and add this Warp hazard to the sendTo field of the
		// last Warp hazard, and set our sendTo field to the first Warp hazard. If this is the
		// only Warp hazard, leave sendTo null.
		World world = (World)parent;
		boolean isFirst = true;
		for (int i=0; i<world.numHazards; i++) {
			if (world.hazard[i] instanceof Warp) {
				if (isFirst) { //this is the first one in the list
					sendTo = (Warp)world.hazard[i];
				}
				if (((Warp)world.hazard[i]).sendTo == sendTo) { //this one points to the first one
					((Warp)world.hazard[i]).sendTo = this;
					break;
				}
				isFirst = false;
			}
		}
	}
	
	@Override
	public boolean collision(Ball ball) {
		if (!bounds.overlaps(ball.bounds)) {
			if (ball.traversing == this) ball.traversing = null;
			return false;
		}
		if (ball.traversing == this) return false;
		
		world.game.playBlip();
		for (int i=0; i<listener.size; i++) {
			listener.get(i).collided(this);
		}
		// move the ball
		ball.setBallPosition(sendTo.bounds.x, sendTo.bounds.y);
		ball.traversing = sendTo;
		return true;
	}
	
	@Override
	public void update(float deltaTime) {
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		elapsedTime += Gdx.graphics.getDeltaTime();
		batch.draw(ani.getKeyFrame(elapsedTime), 
				getX(), getY(),
				bounds.radius * 2, bounds.radius * 2);
		//batch.draw(img.get(0), bounds.x - bounds.radius, bounds.y - bounds.radius, bounds.radius * 2, bounds.radius * 2);
	}

	@Override
	protected void positionChanged() {
		if (bounds==null) return;
		bounds.x = getX() + bounds.radius;
		bounds.y = getY() + bounds.radius;
	}
}
