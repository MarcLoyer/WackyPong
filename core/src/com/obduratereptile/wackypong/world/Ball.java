package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Ball extends Actor {
	public Circle bounds;
	
	static final float MAXSPEED = 800;
	static final float SPEEDINCREMENT = 50;
	
	World world;
	public Sprite img;
	public Vector2 velocity;
	public float lateralSpin;
	public int hitCount;
	public int lastHitBy;
	public Hazard traversing;
	
	public Ball(World world, float x, float y) {
		super();
		setSize(20, 20);
		bounds = new Circle(x, y, 10);
		this.world = world;
		img = world.game.atlas.createSprite("ball");
		img.setSize(20, 20);
		setBallPosition(x, y);
		
		velocity = new Vector2();
		lateralSpin = 0;
		hitCount = 0;
		lastHitBy = -1;
		traversing = null;
	}
	
	public void setVelocity(float x, float y) {
		velocity.x = x;
		velocity.y = y;
	}

	public void addToSpin(float spin) {
		float maxSpin = 50;

		lateralSpin += spin;
		if (lateralSpin > maxSpin) lateralSpin = maxSpin;
		if (lateralSpin < -maxSpin) lateralSpin = -maxSpin;
	}

	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		setBallPosition(bounds.x + velocity.x * deltaTime, bounds.y + velocity.y * deltaTime);
		velocity.rotate(lateralSpin * deltaTime);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		img.draw(batch);
	}
	
	public void hit(int player) {
		lastHitBy = player;
		hitCount++;
		if ((hitCount%10) == 0) {
			speedup(1);
		}
	}
	
	public void speedup(int incr) {
		double atan = Math.atan2(velocity.x, velocity.y);
		velocity.x += incr * SPEEDINCREMENT * Math.sin(atan);
		velocity.y += incr * SPEEDINCREMENT * Math.cos(atan);
		if (velocity.len2() > MAXSPEED*MAXSPEED) {
			velocity.x = MAXSPEED * (float)Math.sin(atan);
			velocity.y = MAXSPEED * (float)Math.cos(atan);
		}
	}

	public boolean collision(Ball ball) {
		if (!bounds.overlaps(ball.bounds)) return false;
		bounce(ball);
		return true;
	}
	
	/**
	 * Computes the reflection velocity of two colliding balls and updates the velocities of both
	 * Reference: http://vobarian.com/collisions/2dcollisions2.pdf
	 * @param ball the ball
	 */
	public void bounce(Ball ball) {
		// compute unit normal and tangential vectors based on the collision
		Vector2 unitNormal = new Vector2(ball.bounds.x - bounds.x, ball.bounds.y - bounds.y);
		unitNormal.setLength(1);
		Vector2 normal = new Vector2(unitNormal); // need this below...
		Vector2 unitTangent = new Vector2(-unitNormal.y, unitNormal.x);

		// decompose the pre-collision velocities into the unit vectors
		float vN1 = unitNormal.dot(velocity);
		float vT1 = unitTangent.dot(velocity);
		
		float vN2 = unitNormal.dot(ball.velocity);
		float vT2 = unitTangent.dot(ball.velocity);

		// the tangential velocities don't change
		// the normal velocities are swapped between the two balls
		Vector2 tempNormal = new Vector2(unitNormal);
		Vector2 tempTangent = new Vector2(unitTangent);
		
		velocity.set(tempNormal.scl(vN2).add(tempTangent.scl(vT1)));
		ball.velocity.set(unitNormal.scl(vN1).add(unitTangent.scl(vT2)));

		// move the ball off the hazard to prevent collision captures
		normal.scl(ball.bounds.radius + bounds.radius).add(bounds.x, bounds.y);
		ball.bounds.x = normal.x;
		ball.bounds.y = normal.y;

		// reverse the spin of both balls
		lateralSpin *= -1;
		ball.lateralSpin *= -1;
	}

	public void setBallPosition(float x, float y) {
		bounds.setPosition(x, y);
		setPosition(bounds.x-10, bounds.y-10);
		img.setPosition(bounds.x-10, bounds.y-10);
	}


}
