package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Hazard extends Actor {
	World world;
	Circle bounds;
	
	public Hazard(World w, float x, float y, float radius) {
		super();
		this.world = w;
		setBounds(x-radius, y-radius, radius*2, radius*2);
		bounds = new Circle(x, y, radius);
	}

	public Hazard(World w, float x, float y) {
		this(w, x, y, 10);
	}

	public void restart() {
		return;
	}

	public abstract Hazard copy();
	
	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		update(deltaTime);
	}
	
	public abstract void draw(Batch batch, float parentAlpha);
	
	public abstract void update(float deltaTime);

	public abstract boolean collision(Ball ball);
	
	public float getRadius() {
		return bounds.radius;
	}
	
	public void setRadius(float radius) {
		float x = getX();
		float y = getY();
		float r = getRadius();
		
		setBounds(x+r-radius, y+r-radius, radius*2, radius*2);
		bounds.radius = radius;
	}
	
	public void moveTo(float x, float y) {
		bounds.x = x;
		bounds.y = y;
		float r = getRadius();
		this.setPosition(x-r, y-r);
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		float r = getRadius();
		bounds.setPosition(getX()+r, getY()+r);
	}
	
	/**
	 * Computes the reflection velocity of an incoming ball and updates the ball velocity
	 * Reference: http://vobarian.com/collisions/2dcollisions2.pdf
	 * @param ball the ball
	 */
	protected void bounce(Ball ball) {
		// compute unit normal and tangential vectors based on the collision
		Vector3 unitNormal = new Vector3(ball.bounds.x - bounds.x, ball.bounds.y - bounds.y, 0);
		unitNormal.setLength(1);
		Vector3 normal = new Vector3(unitNormal); // need this below...
		Vector3 unitTangent = new Vector3(-unitNormal.y, unitNormal.x, 0);

		// decompose the pre-collision velocity into the unit vectors
		float vN = unitNormal.dot(ball.velocity);
		float vT = unitTangent.dot(ball.velocity);

		// the tangential velocity doesn't change
		// the normal velocity is reversed
		ball.velocity.set(unitNormal.scl(-vN).add(unitTangent.scl(vT)));

		// move the ball off the hazard to prevent collision captures
		normal.scl(ball.bounds.radius + getRadius()).add(bounds.x, bounds.y, 0);
		ball.bounds.x = normal.x;
		ball.bounds.y = normal.y;
	}
	
	public String getString() {
		String clazz = getClass().getSimpleName();
		
		return (clazz + "(" + bounds.x + ", " + bounds.y + ", " + bounds.radius + ");");
	}
}
