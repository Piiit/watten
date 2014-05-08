package com.mpp.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import com.mpp.watten.logic.Watten;

public class Server {
	
	private static final int MAXCLIENTS = 20;
	private static final int PORT = 9999;
	private static Vector<ServerThread> clients = new Vector<ServerThread>();
	private static Vector<Watten> games = new Vector<Watten>();

	public static void main(String args[]) {
		start();
	}
	
	public static void start() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Starting WATTEN server...");
			while (true) {  
				try {  
					Socket clientSocket = serverSocket.accept();
					System.out.println("Client likes to connected @ local port " + clientSocket.getLocalPort());
					
					ServerThread client = new ServerThread(clientSocket, clients, games, MAXCLIENTS);
					client.start();
					
	            } catch(IOException e) {  
	            	e.getStackTrace();
				}
	         }
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
