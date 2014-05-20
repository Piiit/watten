package com.mpp.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class WTools {
	
	private static 	Texture tableTex;
	private static Image tableImage;

	
	
	
	public static void initiate(){
		
		tableTex = new Texture(Gdx.files.internal("data/table.jpg"));
		tableTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		tableImage = new Image(tableTex);
	}
	
	public static Image getTableImage(){
		return tableImage;
	}

}
