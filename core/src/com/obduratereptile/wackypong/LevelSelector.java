package com.obduratereptile.wackypong;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Implements a UI Actor allowing the user to select a level to play. Supports
 * disabling levels.
 * @author Marc
 *
 */
public class LevelSelector extends Table {
	private WackyPong game;
	private int numRows;
	private int numColumns;
	private Skin skin;
	private boolean labelExists;
	
	private Array<LevelSelectorListener> listeners;
	
	public LevelSelector(String label, Skin skin, String style, int numRows, int numColumns, Drawable file, Drawable empty, WackyPong g) {
		super();
		this.game = g;
		this.numRows = numRows;
		this.numColumns = numColumns;
		this.listeners = new Array<LevelSelectorListener>();
		
		if (!label.equals("")) {
			labelExists = true;
			Label lbl = new Label(label, skin, style);
			lbl.setAlignment(Align.center);
			add(lbl).fill().colspan(numColumns);
			row();
		} else {
			labelExists = false;
		}
		
		IndexedButton button;
		for (int i=0; i<numRows; i++) {
			for (int j=0; j<numColumns; j++) {
				button = new IndexedButton(i*numColumns + j, false, skin, file, empty);
				add(button).size(80, 80).fill().center().space(20);
				
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						callListeners(((IndexedButton)event.getListenerActor()).index);
					}
				});
				
			}
			row();
		}
		
		//TODO: add a background so that a border can be drawn around this widget
	}
	
	public void disableAllCells(boolean disable) {
		Iterator<Cell> cell = getCells().iterator();
		while (cell.hasNext()) {
			Actor a = cell.next().getActor();
			if (a instanceof IndexedButton) {
				((IndexedButton)a).setDisabled(disable);
			}
		}
	}
	
	public void disableCell(int i, int j, boolean disable) {
		disableCell(i*numColumns + j, disable);
	}
	
	public void disableCell(int cellNum, boolean disable) {
		IndexedButton btn = (IndexedButton)getCells().get(cellNum+(labelExists?1:0)).getActor();
		btn.setDisabled(disable);
	}
	
	public boolean isDisabled(int i, int j) {
		return isDisabled(i*numColumns + j);
	}
	
	public boolean isDisabled(int cellNum) {
		IndexedButton btn = (IndexedButton)getCells().get(cellNum+(labelExists?1:0)).getActor();
		return btn.isDisabled();
	}
	
	class IndexedButton extends Button {
		public int index;
		public boolean empty;
		public Label lbl;
		public Image imgFile;
		public Image imgEmpty;
		public Image ban;
		public Stack stack;
		
		public IndexedButton(int index, boolean empty, Skin skin, Drawable imgFile, Drawable imgEmpty) {
			super(skin, "default");
			
			this.index = index;
			
			this.lbl = new Label((empty? "empty": "" + index), skin, "default");
			this.lbl.setAlignment(Align.center);
			this.lbl.setTouchable(Touchable.disabled);
			
			this.imgFile = new Image(imgFile);
			this.imgFile.setTouchable(Touchable.disabled);
			this.imgEmpty = new Image(imgEmpty);
			this.imgEmpty.setTouchable(Touchable.disabled);
			setEmpty(empty);
			
			ban = new Image(game.getSpriteDrawable("ban2", Color.RED));
			ban.setTouchable(Touchable.disabled);
			ban.setVisible(false);
			
			stack = new Stack(
					this.imgFile,
					this.imgEmpty,
					this.ban,
					this.lbl
					);
			add(stack);
		}
		
		public void setEmpty(boolean e) {
			empty = e;
			if (empty) {
				imgFile.setVisible(false);
				imgEmpty.setVisible(true);
				lbl.setText("empty");
			} else {
				imgFile.setVisible(true);
				imgEmpty.setVisible(false);
				lbl.setText(""+index);
			}
		}
		
		public void setDisabled(boolean disable) {
			if (disable) {
				setTouchable(Touchable.disabled);
				ban.setVisible(true);
			} else {
				setTouchable(Touchable.enabled);
				ban.setVisible(false);
			}
			super.setDisabled(disable);
		}
	}
	
	public interface LevelSelectorListener {
		public void selected(int index);
	}
	
	public void addListener(LevelSelectorListener listener) {
		listeners.add(listener);
	}
	
	private void callListeners(int index) {
		Iterator<LevelSelectorListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().selected(index);
		}
	}
}
