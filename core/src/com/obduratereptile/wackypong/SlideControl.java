package com.obduratereptile.wackypong;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class SlideControl extends Table {
	public Label label;
	public ImageButton button;
	public Slider slider; // instantiator should add a listener to this guy
	public float unmutedValue;
	
	SlideControl(Skin skin, String lbl, Drawable imgUp, Drawable imgDown, Drawable imgChecked) {
		super(skin);
		label = new Label(lbl, skin, "default");
		// might be better to use ImageButtonStyle constructor?
		button = new ImageButton(imgUp, imgDown, imgChecked);
		slider = new Slider(0, 1, 0.1f, false, skin);
		slider.setValue(0.5f);
		unmutedValue = slider.getValue();
		
		add(label).size(100, 40).fill().right();
		add(button).size(40, 40).fill().center().space(10);
		add(slider).size(160, 40).fill().center();
		
		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (button.isChecked()) {
					slider.setValue(0);
				} else {
					slider.setValue(unmutedValue);
				}
			}
		});
		
		slider.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				float currentValue = slider.getValue();
				if (currentValue > 0) {
					unmutedValue = currentValue;
					if (button.isChecked()) {
						button.setChecked(false);
					}
				} else {
					if (!button.isChecked()) {
						button.setChecked(true);
					}
				}
			}
		});
		
		//TODO: add a background so that a border can be drawn around this widget
	}
	
	public void setValue(float value) {
		slider.setValue(value);
		if (value == 0.0f)
			button.setChecked(true);
		else
			button.setChecked(false);
	}
}
