package com.obduratereptile.wackypong.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Cannon extends Image {
	final float SPEED = 100;

	public World world;

	public float direction;
	public boolean isHidden;
	public float maxAngle;
	public float minAngle;
	public int player;

	private Vector2 positionHide;
	private Vector2 positionShow;

	public Cannon(World world, float x, float y) { // (x,y) is the origin point of the cannon
		super(world.game.atlas.createSprite("cannon"));
		this.world = world;

		float width = 40;
		float height = 70;
		positionHide = new Vector2(x - width/2, y + width/2);
		positionShow = new Vector2(x - width/2, y - height + width/2);
		setBounds(positionHide.x, positionHide.y, width, height);
		setOrigin(width/2, height - width/2);

		isHidden = false;
		setAngles(85, 5);
		direction = 1;
		this.player = 0;
		addAction(Actions.forever(
				Actions.sequence(
						Actions.rotateTo(maxAngle, 0.5f),
						Actions.rotateTo(minAngle, 0.5f)
				)
		));


		this.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				// ignore touch if AI is supposed to launch
				World w = ((Cannon)e.getListenerActor()).world;
				if (w.numPlayers == 0) return;
				if (w.numPlayers == 1) {
					if (player == 1) return;
				}

				fire();
			}
		});
	}

	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
	}
	
	public void hide() {
		if (isHidden) return;
		isHidden = true;
		setTouchable(Touchable.disabled);
		clearActions();
		addAction(Actions.sequence(
				Actions.rotateTo(0, 0.5f),
				Actions.parallel(
						Actions.run(new Runnable() {
							public void run () {
								world.game.playRetractCannon();
							}
						}),
						Actions.moveTo(positionHide.x, positionHide.y, 1.5f)
				)

			));
	}

	public void show(int player) {
		if (!isHidden) return;
		direction = 1;
		this.player = player;

		addAction(Actions.sequence(
				Actions.after(Actions.delay(0.3f)),
				Actions.parallel(
						Actions.run(new Runnable() {
							public void run () {
								world.game.playDeployCannon();
							}
						}),
						Actions.moveTo(positionShow.x, positionShow.y, 1.5f)
				),
				Actions.run(new Runnable() {
					public void run () {
						setTouchable(Touchable.enabled);
						isHidden = false;
					}
				}),
				Actions.forever(
						Actions.sequence(
								Actions.rotateTo(maxAngle, 0.5f),
								Actions.rotateTo(minAngle, 0.5f)
						)
				)
			));
	}

	public void setAngles(float max, float min) {
		// max, min, whatevs... the one closest to 0 is min, the other is max
		if (max<0) {
			if (max > min) {
				minAngle = max;
				maxAngle = min;
			} else {
				minAngle = min;
				maxAngle = max;
			}
		} else {
			if (max > min) {
				minAngle = min;
				maxAngle = max;
			} else {
				minAngle = max;
				maxAngle = min;
			}
		}
	}

	public void fire() {
		// Create a new ball just past the end of the cannon, with a velocity moving away from the cannon
		float cannonLength = 60;
		double rad = Math.toRadians(getRotation());
		Vector2 p = new Vector2(getX(), getY());
		p.add(getOriginX(), getOriginY());
		p.add(cannonLength * (float) Math.sin(rad), -cannonLength * (float) Math.cos(rad));
		Ball b = new Ball(world, p.x, p.y);
		b.setVelocity(200 * (float) Math.sin(rad), -200 * (float) Math.cos(rad));
		world.addBall(b);
		world.game.playFireBall();
		hide();
	}

	public boolean contains(float x, float y) {
		Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight());
		return r.contains(x,y);
	}

}
