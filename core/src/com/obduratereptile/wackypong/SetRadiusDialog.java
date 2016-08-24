package com.obduratereptile.wackypong;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.obduratereptile.wackypong.world.Hazard;

public class SetRadiusDialog extends Dialog {
	Hazard target;
	TextField tf;
	
	public SetRadiusDialog(String title, Skin skin, String style) {
		super(title, skin, style);
		target = null;
		
		tf = new TextField("", skin, style);
		tf.setTextFieldFilter(new DigitsOnlyFilter());
		
		Table tbl = getContentTable();
		tbl.add(new Label("Radius:", skin, style));
		tbl.add(tf);
		
		button("Ok", this);
		button("Cancel", null);
	}
	
	public void setTarget(Hazard target) {
		this.target = target;
		int r = (int)target.getRadius();
		tf.setText(""+r);
	}
	
	public int getRadius() {
		return Integer.parseInt(tf.getText());
	}
	
	@Override
	protected void result(java.lang.Object object) {
		if (object == null) return; // this means 'Cancel' was clicked
		if (target == null) return;
		target.setRadius(getRadius());
	}
}
