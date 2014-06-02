package com.mpp.watten;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mpp.ui.WattenGame;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Watten";
		cfg.width = 800;
		cfg.height = 600;
		
		new LwjglApplication(new WattenGame(), cfg);
		
	}
}
