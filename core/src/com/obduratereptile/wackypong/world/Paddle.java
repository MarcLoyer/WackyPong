package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.obduratereptile.wackypong.WackyPong;

public class Paddle extends Actor {
	private static float WIDTH = 20f;
	private static float HEIGHT = 100f;
	private static float HEIGHTSMALL = 60f;
	private static float SPEED = 800f;
	
	World world;
	public Sprite img, imgSmall;
	public boolean isShrunk;
	public float target;
	
	public Paddle(World world, boolean mirrored) {
		super();
		this.world = world;

		img = world.game.atlas.createSprite("paddle");
		imgSmall = world.game.atlas.createSprite("paddlesmall");

		img.setSize(WIDTH, HEIGHT);
		imgSmall.setSize(WIDTH, HEIGHTSMALL);
		setSize(WIDTH, HEIGHT);

		if (mirrored) {
			img.setPosition(WackyPong.SCREENSIZEX - WIDTH, (WackyPong.SCREENSIZEY - HEIGHT) / 2);
			imgSmall.setPosition(WackyPong.SCREENSIZEX - WIDTH, (WackyPong.SCREENSIZEY - HEIGHTSMALL) / 2);
			setPosition(WackyPong.SCREENSIZEX - WIDTH, (WackyPong.SCREENSIZEY - HEIGHT) / 2);
		} else {
			img.setPosition(0, (WackyPong.SCREENSIZEY - HEIGHT) / 2);
			imgSmall.setPosition(0, (WackyPong.SCREENSIZEY - HEIGHTSMALL) / 2);
			setPosition(0, (WackyPong.SCREENSIZEY - HEIGHT) / 2);
		}

		this.isShrunk = false;
		this.target = WackyPong.SCREENSIZEY/2;
	}
	
	public void shrink() {
		if (isShrunk) return;
		isShrunk = true;

		setY(getY() + (HEIGHT - HEIGHTSMALL)/2);
		setHeight(HEIGHTSMALL);
	}
	
	public void unshrink() {
		if (!isShrunk) return;
		isShrunk = false;

		float y = getY() - (HEIGHT - HEIGHTSMALL)/2;
		if (y<0) y = 0;
		if (y>(WackyPong.SCREENSIZEY - HEIGHT)) y = WackyPong.SCREENSIZEY - HEIGHT;
		setY(y);
		setHeight(HEIGHT);
	}
	
	public void moveTo(Vector3 pos) {
		target = pos.y;
	}
	
	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		update(deltaTime);
	}
	
	public void update(float deltaTime) {
		// move towards the target...
		float y = getY() + getHeight()/2;
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

		if (y<getHeight()/2) y = getHeight()/2;
		if (y>(WackyPong.SCREENSIZEY - getHeight()/2)) y = WackyPong.SCREENSIZEY - getHeight()/2;

		setY(y - getHeight()/2);
		img.setY(y - HEIGHT/2);
		imgSmall.setY(y - HEIGHTSMALL/2);
		return;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (isShrunk)
			imgSmall.draw(batch);
		else
			img.draw(batch);
	}

	public boolean collision(Ball ball) {
		if ((ball.bounds.x + ball.bounds.radius) < getX()) return false;
		if ((ball.bounds.x - ball.bounds.radius) > getRight()) return false;
		if ((ball.bounds.y + ball.bounds.radius) < getY()) return false;
		if ((ball.bounds.y - ball.bounds.radius) > getTop()) return false;

		float minY = getY() + getWidth()/2;
		float maxY = getTop() - getWidth()/2;

		if ((ball.bounds.y > minY) && (ball.bounds.y < maxY)) {
			// the ball hit in the middle of the paddle
			ball.velocity.x *= -1;
		} else {
			// get the center of the paddle curve
			float x = getX() + getWidth()/2;
			float y = (ball.bounds.y > minY)? maxY:	minY;

			// compute unit normal and tangential vectors based on the collision
			Vector3 unitNormal = new Vector3(ball.bounds.x-x, ball.bounds.y-y, 0);
			unitNormal.setLength(1);
			Vector3 unitTangent = new Vector3(-unitNormal.y, unitNormal.x, 0);

			// decompose the pre-collision velocity into the unit vectors
			float vN = unitNormal.dot(ball.velocity);
			float vT = unitTangent.dot(ball.velocity);

			// the tangential velocity doesn't change
			// the normal velocity is reversed
			ball.velocity.set(unitNormal.scl(-vN).add(unitTangent.scl(vT)));
		}

		// move the ball off the paddle to prevent collision captures
		if (ball.velocity.x > 0)
			ball.bounds.x = getRight() + ball.bounds.radius;
		else
			ball.bounds.x = getX() - ball.bounds.radius;

		return true;
	}
}
