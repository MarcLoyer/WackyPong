package com.obduratereptile.wackypong.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.obduratereptile.wackypong.ActionResolver;
import com.obduratereptile.wackypong.WackyPong;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		// Run the texture packer...
/*
		String assetsPath = "C:/Users/Marc/Documents/AssetDevelopment/WackyPong/Images"; // run directory should be set to android/assets in the run config
		String atlasPath = "atlas";
        TexturePacker.Settings packSettings = new TexturePacker.Settings();
        packSettings.pot = true;
		// I tried all the different filters, So far the best is MipMapLinearLinear or MipMap
		packSettings.filterMin = Texture.TextureFilter.MipMap;
		packSettings.filterMag = Texture.TextureFilter.MipMap;
        packSettings.flattenPaths = true;
        packSettings.combineSubdirectories = true;

        TexturePacker.process(packSettings, assetsPath, atlasPath, "textures.pack");
*/

        
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new WackyPong(new ActionResolverDesktop()), config);
	}
}
