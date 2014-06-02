package com.mpp.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.mpp.tools.xml.SimpleXML;
import com.mpp.watten.cards.MultipleCards;

public class ClientThread extends Thread {
	
	private BufferedReader input = null;
	private Socket socket = null;
	
	public ClientThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		
	    String line = "";
	    try {
	    	while (!socket.isClosed()) {
	    		line = input.readLine();
	    		if(line != null) {
	    			
	    			System.err.println("OUTPUT:" + line);
	    			
	    			SimpleXML xml = new SimpleXML(line);
	    			xml.parse();
	    			
	    			String command = xml.root.getNode("command").getData().toLowerCase();
	    			
		    		switch(command) {
		    			case "quit":
		    				if("ACK".equalsIgnoreCase(xml.root.getNode("type").getData())) {
		    					socket.close();
		    				} else {
		    					System.out.println("ERROR: " + unescape(xml.root.getNode("message").getData()));
		    				}
		    			break;
		    			case "create_game":
		    			case "join_game":
		    			case "start_game":
		    			case "select_rank":
		    			case "select_suit":
		    				if("NAK".equalsIgnoreCase(xml.root.getNode("type").getData())) {
		    					System.out.println("ERROR: " + unescape(xml.root.getNode("message").getData()));
		    				}
		    			break;
		    			case "start_round":
		    				MultipleCards hand = new MultipleCards();
		    				hand.load(xml.root.getNode("hand"));
		    			break;
		    			case "help":
		    			case "list_games":
		    			case "info":
		    				if("ACK".equalsIgnoreCase(xml.root.getNode("type").getData()))
		    					System.out.println(unescape(xml.root.getNode("message").getData()));
		    			break;
		    			case "chat":
		    				System.out.println(unescape(xml.root.getNode("message").getData()));
		                break;
		                default:
		                	System.err.println(line);
		    		}
	    		}
	    	}
	    } catch (Exception e) {
	    	System.err.println("Closing client output.");
	    	e.printStackTrace(System.err);
	    } finally {
			try {
				input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
	    }

		System.err.println("Bye");
	}
	
	private String unescape(String text) {
		return text.replace("\\n", "\n");
	}
	
}
