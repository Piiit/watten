package com.mpp.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.mpp.tools.UserInterface;
import com.mpp.tools.xml.SimpleXML;

public class ClientForConsole {
	
	private static PrintWriter output = null;
	private static BufferedReader input = null;
	private static Socket socket = null;
	private static final int PORT = 9999;
	private static final String ADDRESS = "localhost";
	
	public static void main(String args[]) {
		ClientForConsole c = new ClientForConsole();
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
			

			ClientThread clientOut = new ClientThread(socket);
			clientOut.start();

			String loginName = UserInterface.inputField("Please enter your name");
			sendRequest("login", "name", loginName);

			String line = "";
			while(!socket.isClosed()) {
				try {
					line = input.readLine();

					String regex = "\\s";
					String parts[] = line.split(regex);
					
					String cmd = parts[0].toUpperCase();
					String gameName = "";

					switch(cmd) {
						case "Q": 
							sendRequest("quit");
						break;
						case "N":
							gameName = "";
							if(parts.length > 1) {
								gameName = parts[1];
							} 
							sendRequest("create_game", "name", gameName);
						break;
						case "J": 
							gameName = "";
							if(parts.length > 1) {
								gameName = parts[1];
							} 
							sendRequest("join_game", "name", gameName);
						break;
						case "S":
							sendRequest("start_game");
						break;
						case "H":
							sendRequest("help");
						break;
						case "L":
							sendRequest("list_games");
						break;
						case "C":
							String msg = "";
							if(parts.length > 1) {
								for(int i = 1; i < parts.length; i++) 
									msg += parts[i];
							} 
							sendRequest("chat","message",msg);
						break;
						case "I":
							gameName = "";
							if(parts.length > 1) {
								gameName = parts[1];
							} 
							sendRequest("info", "name", gameName);
						break;
                        case "SR":
                                sendRequest("select_rank","card_index",parts[1]);
                        break;
                        case "SS":
                                sendRequest("select_suit","card_index",parts[1]);
                        break;
                        case "P":
                        		sendRequest("play_card","card_index",parts[1]);
                        break;

					}
					
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
	
	private void sendRequest(String command) {
		output.println(SimpleXML.createTag("request", SimpleXML.createTag("command", command)));
	}
	
//	private void sendRequest(String command, String message, Loadable data) {
//		output.println(SimpleXML.createTag("request", 
//				SimpleXML.createTag("command", command)) + 
//				SimpleXML.createTag("message", message) +
//				SimpleXML.createTag("data", data.serialize())
//				);
//	}
	
	private synchronized void sendRequest(String command, String ... details) {
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
