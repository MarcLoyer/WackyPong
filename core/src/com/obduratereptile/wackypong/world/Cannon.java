package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Cannon extends Image {
	final float SPEED = 100;

	public World world;
	public Texture img;

	public float angle;
	public float direction;
	public boolean isHidden;
	public float maxAngle;
	public float minAngle;
	public int player;

	public Cannon(World world, float x, float y, float w) {
		super(world.game.getSpriteDrawable("cannon", Color.GOLD));
		this.world = world;
		
		setBounds(x - w/2, y, w, w);
		maxAngle = 85;
		minAngle = 5;

		angle = minAngle;
		direction = 1;
		isHidden = false;
		player = -1;
		
		setOrigin(getWidth()/2, getHeight()/2);
		setRotation(angle);
		
		
		this.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				// TODO: ignore touch if AI is supposed to launch
				fire();
			}
		});
	}
	
	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		update(deltaTime);
	}
	
	public void update(float deltaTime) {
		//TODO: recode this using RotateToAction ?
		angle += direction * deltaTime * SPEED;
		if (angle > maxAngle) {
			angle = maxAngle;
			direction *= -1;
		}
		if (angle < minAngle) {
			angle = minAngle;
			direction *= -1;
		}
		setRotation(angle);
	}
	
	public void hide() {
		if (isHidden) return;
		//TODO: play a sound effect
		isHidden = true;
		setTouchable(Touchable.disabled);
		addAction(Actions.sequence(
				Actions.moveTo(getX(), getY()+getHeight(), 1.5f),
				Actions.run(new Runnable() {
					public void run () {
						angle = minAngle;
						direction = 0;
						setRotation(angle);
					}
				})
			));
	}

	public void show(int player) {
		if (!isHidden) return;
		//TODO: play a sound effect
		isHidden = false;
		direction = 1;
		this.player = player;
		
		addAction(Actions.sequence(
				Actions.moveTo(getX(), getY()-getHeight(), 1.5f),
				Actions.run(new Runnable() {
					public void run () {
						setTouchable(Touchable.enabled);
					}
				})
			));
	}

	public void setAngles(float max, float min) {
		maxAngle = max;
		minAngle = min;
		if (angle > maxAngle)
			angle = maxAngle;
		if (angle < minAngle)
			angle = minAngle;
		setRotation(angle);
	}

	public void fire() {
		// launch the ball at the same angle as the cannon
		Ball b = new Ball(world, getX()+getWidth()/2, getY()+getHeight()/2);
		b.lastHitBy = player;
		double rad = Math.toRadians(angle);
		b.setVelocity(200 * (float) Math.sin(rad), -100 * (float) Math.cos(rad));
		world.addBall(b);
		//TODO: play a sound effect
		
		hide();
	}

	public boolean contains(float x, float y) {
		Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight());
		return r.contains(x,y);
	}

}
