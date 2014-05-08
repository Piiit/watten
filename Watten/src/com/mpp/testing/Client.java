package com.mpp.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import xml.Loadable;
import xml.SimpleXML;

public class Client {
	
	private static PrintWriter output = null;
	private static BufferedReader input = null;
	private static Socket socket = null;
	private static final int PORT = 9999;
	private static final String ADDRESS = "localhost";
	
	public static void main(String args[]) {
		Client c = new Client();
		c.startManualInput();
	}

	public void startManualInput() {
		try {
			socket = new Socket(ADDRESS, PORT);
			try {
				output = new PrintWriter(socket.getOutputStream(), true);
				input = new BufferedReader(new InputStreamReader(System.in));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ClientOutput clientOut = new ClientOutput(socket);
			clientOut.start();
			
			String line = "";
			boolean done = false;
			while(!done && !socket.isClosed()) {
				try {
					line = input.readLine();

					String regex = "\\s";
					String parts[] = line.split(regex);
					
					String cmd = parts[0].toUpperCase();

					switch(cmd) {
						case "Q": 
							sendRequest("quit");
						break;
						case "N":
							sendRequest("create_game", "name", parts[1]);
						break;
						default:
							output.println(line);
					}
					
					done = line.equals("/quit");
					
					System.out.println(line);
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("Closing connection.");
				output.close();
				input.close();
				if(!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
	
	private void sendRequest(String command) {
		output.println(SimpleXML.createTag("request", SimpleXML.createTag("command", command)));
	}
	
	private void sendRequest(String command, String message, Loadable data) {
		output.println(SimpleXML.createTag("request", 
				SimpleXML.createTag("command", command)) + 
				SimpleXML.createTag("message", message) +
				SimpleXML.createTag("data", data.serialize())
				);
	}
	
	private void sendRequest(String command, String ... details) {
		String out = "";
		int i = 0;
		String tagName = ""; 
		for(String s : details) {
			if(i % 2 == 0) {
				tagName = s;
			} else {
				out += SimpleXML.createTag(tagName, s);
			}
			i++;	
		}
		output.println(SimpleXML.createTag("request", SimpleXML.createTag("command", command) + out));
	}

}
