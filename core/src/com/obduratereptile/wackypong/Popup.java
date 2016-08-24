package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Popup extends Table {
	InputListener stageListener;
	ChangeListener menuItemListener;
	InputListener defaultInputListener;
	
	Actor target = null;
	boolean cancelTouchUp;
	
	public Popup() {
		setTouchable(Touchable.enabled);
		
		stageListener = new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (contains(x, y) == false) remove();
				return true;
			}
		};

		menuItemListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (event.isStopped() == false) remove();
			}
		};
	}
	
	public boolean contains(float x, float y) {
		if ((x < getX()) || (x > getRight())) return false;
		if ((y < getY()) || (y > getTop())) return false;
		return true;
	}
	
	public TextButton addMenuItem(String label, Skin skin, String style) {
		TextButton btn = new TextButton(label, skin, style);
		add(btn).width(90).fillX().expandX().row();
		
		btn.addListener(menuItemListener);
		return btn;
	}
	
	/**
	 * Returns input listener that can be added to scene2d actor. When mouse button is pressed on that actor,
	 * menu will be displayed
	 * @param mouseButton from {@link Buttons}
	 */
	public InputListener getDefaultInputListener () {
		if (defaultInputListener == null) {
			defaultInputListener = new InputListener() {				
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					cancelTouchUp = false;
					return true;
				}

				@Override
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (cancelTouchUp) return;
					showMenu(event.getStage(), event.getTarget(), event.getStageX(), event.getStageY());
				}
			};
		}
		return defaultInputListener;
	}
	
	/**
	 * Displays the popup at the location of the given Actor
	 * @param stage
	 * @param actor
	 */
	public void showMenu(Stage stage, Actor actor) {
		Vector2 pos = actor.localToStageCoordinates(new Vector2(0,0));
		showMenu(stage, actor, pos.x, pos.y);
	}
	
	public void showMenu(Stage stage, Actor actor, float x, float y) {
		target = actor;
		
		if (y<getHeight())
			setPosition(x, y);
		else
			setPosition(x, y-getHeight());
		//setPosition(x, y - getHeight());
		//if (stage.getHeight() - getY() > stage.getHeight()) setY(getY() + getHeight());
		stage.addActor(this);
	}
	
	@Override
	public boolean remove() {
		if (getStage() != null) getStage().removeListener(stageListener);
		return super.remove();
	}
	
	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
		if (stage != null) stage.addListener(stageListener);
	}

}
