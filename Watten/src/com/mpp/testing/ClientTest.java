package com.mpp.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.mpp.network.ClientThread;
import com.mpp.tools.xml.SimpleXML;

public class ClientTest {
	
	private static PrintWriter output = null;
	private static BufferedReader input = null;
	private static Socket socket = null;
	private static final int PORT = 9999;
	private static final String ADDRESS = "localhost";
	
	public static void main(String args[]) {
		ClientTest c = new ClientTest();
		c.startManualInput();
	}

	public void startManualInput() {
		try {
			socket = new Socket(ADDRESS, PORT);
			try {
				output = new PrintWriter(socket.getOutputStream(), true);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ClientThread clientOut = new ClientThread(socket);
			clientOut.start();
			
			String line = "";
			while(!socket.isClosed()) {
				try {
					sendRequest("create_game", "name", "Watten");
					
					line = input.readLine();
					//TODO wait for ACK...
					sendRequest("join_game", "name", "Watten");

					line = input.readLine();

					
					System.out.println("CLIENT: "+line);
					
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
