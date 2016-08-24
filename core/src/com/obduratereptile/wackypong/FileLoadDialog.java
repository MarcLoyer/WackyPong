package com.obduratereptile.wackypong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.obduratereptile.wackypong.LevelSelector.IndexedButton;
import com.obduratereptile.wackypong.LevelSelector.LevelSelectorListener;

public class FileLoadDialog extends Dialog implements LevelSelectorListener {
	private WackyPong game;
	private LevelSelector selectFile;
	private TextButton buttonCancel;
	public int selectedFile;
	
	public FileLoadDialog(Skin skin, WackyPong g) {
		super("Load Playing Field", skin, "default");
		this.game = g;
		// add the content...
		SpriteDrawable file = game.getSpriteDrawable("file");
		SpriteDrawable emptyfile = game.getSpriteDrawable("emptyfile");
		
		selectFile = new LevelSelector("Select a slot...", skin, "default", 2, 5, file, emptyfile, game);
		getContentTable().add(selectFile).size(500, 260).expand().fill();
		selectFile.addListener(this);
		
		selectedFile = -1;
		configLevelSelector();
		
		// add the buttons...
		buttonCancel = new TextButton("Cancel", skin, "default");
		this.button(buttonCancel, null);
		getButtonTable().getCells().get(0).pad(10);
	}
	
	private void configLevelSelector() {
		for (int i=0; i<10; i++) {
			if (!Gdx.files.local("saves/field_"+i+".txt").exists()) {
				IndexedButton btn = (IndexedButton)selectFile.getCells().get(i+1).getActor();
				btn.setEmpty(true);
				btn.setDisabled(true);
			}
		}
	}

	@Override
	public Dialog show(Stage stage) {
		selectedFile = -1;
		configLevelSelector();
		
		return super.show(stage);
	}
	
	@Override
	public void selected(int index) {
		game.clink.play(game.volumeSounds);
		selectedFile = index;
		result(null);
		hide();
	}
}
