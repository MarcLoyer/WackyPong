package com.obduratereptile.wackypong.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.obduratereptile.wackypong.WackyPong;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		// Run the texture packer...
//		String assetsPath = "../android/assets/";
//        TexturePacker.Settings packSettings = new TexturePacker.Settings();  
//        packSettings.pot = true;
//        packSettings.flattenPaths = true;
//        packSettings.combineSubdirectories = true;
//        
//        TexturePacker.process(packSettings, assetsPath+"images", assetsPath+"atlas", "textures.pack");

        
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new WackyPong(), config);
	}
}
