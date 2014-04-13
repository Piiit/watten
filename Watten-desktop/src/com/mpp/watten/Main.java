package com.mpp.watten;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mpp.network.*;
import com.mpp.tools.MessageAction;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Watten";
		cfg.width = 800;
		cfg.height = 600;
		
		new LwjglApplication(new Game(), cfg);
		
		//Client client = new Client("localhost",5555);
		
		
		//System.out.println("hier");
		//message.setAction(MessageAction.CHAT);
		//message.setMessage("hey all");
		//client.processMessage(message);
	}
}
