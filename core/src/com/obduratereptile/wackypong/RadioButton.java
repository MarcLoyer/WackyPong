package com.obduratereptile.wackypong;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

public class RadioButton extends Table {
	Label label;
	ButtonGroup<CheckBox> buttonGroup;
	CheckBox[] buttons;

	public RadioButton(String label, Skin skin, String style, String ... buttonString) {
		super();

		int numButtons = buttonString.length;
		this.label = new Label(label, skin, style);
		this.label.setAlignment(Align.center);
		this.buttonGroup = new ButtonGroup<CheckBox>();
		this.buttons = new CheckBox[numButtons];

		add(this.label).fill().colspan(numButtons);
		row();

		for (int i=0; i<buttonString.length; i++) {
			buttons[i] = new CheckBox(buttonString[i], skin, style);
			buttons[i].align(Align.center);

			add(buttons[i]).width(50).fill().expandX();
			buttonGroup.add(buttons[i]);
		}
	}

	public RadioButton(String label, Skin skin, String style, boolean isVertical, String ... buttonString) {
		super();

		int numButtons = buttonString.length;
		this.label = new Label(label, skin, style);
		this.label.setAlignment(Align.center);
		this.buttonGroup = new ButtonGroup<CheckBox>();
		this.buttons = new CheckBox[numButtons];

		add(this.label).fill().colspan(numButtons);

		for (int i=0; i<buttonString.length; i++) {
			buttons[i] = new CheckBox(buttonString[i], skin, style);
			if (isVertical)
				buttons[i].align(Align.left);
			else
				buttons[i].align(Align.center);

			if ((isVertical)||(i==0)) row();
			add(buttons[i]).fill().expandX();
			buttonGroup.add(buttons[i]);
		}
	}

	public void setChecked(int index) {
		buttons[index].setChecked(true);
	}

	public int getChecked() {
		return buttonGroup.getCheckedIndex();
	}

	public String getSelection() {
		return "" + buttonGroup.getChecked().getText();
	}
}
