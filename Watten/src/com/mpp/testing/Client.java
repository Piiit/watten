package com.mpp.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import xml.SimpleXML;

public class Client {
	
	private static PrintWriter output = null;
	private static BufferedReader input = null;
	private static Socket socket = null;
	private static final int PORT = 9999;
	private static final String ADDRESS = "localhost";
	
	public static void main(String args[]) {
		startManualInput();
	}

	public static void startManualInput() {
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
							sendQuit();
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

	private static void sendQuit() {
		String xmlMessage = SimpleXML.createTag("request", SimpleXML.createTag("command", "quit"));
		output.println(xmlMessage);
	}

}
