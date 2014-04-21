package com.mpp.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientOutput extends Thread {
	
	private BufferedReader input = null;
	private Socket socket = null;
	
	public ClientOutput(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	    String line = "";
	    try {
	    	while (!socket.isClosed()) {
	    		line = input.readLine();
	    		if(line != null) {
	    			System.out.println(line);
		    		if(line.startsWith("*** Bye")) {
		    			socket.close();
		    		}
	    		}
	    	}
	    } catch (Exception e) {
	    	System.out.println("Closing client output.");
	    	e.printStackTrace();
	    }
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Bye");
	}
	
}
