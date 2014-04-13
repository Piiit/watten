package com.mpp.network;

import java.io.*; 
import java.net.*; 

import com.mpp.tools.MessageAction;

public class ServerThread extends Thread 


{  
	private Server server; 
	private Socket socket;
	private String name = "";
 
	public ServerThread( Server server, Socket socket ) { 
 
		this.server = server; 
		this.socket = socket; 
 
		start(); 
	} 

public void run() { 
	
	Message message = new Message(MessageAction.CHAT);
	
	try { 

		ObjectOutputStream dout = new ObjectOutputStream( socket.getOutputStream() );
		ObjectInputStream din = new ObjectInputStream( socket.getInputStream() ); 


		//
		//din.reset();
		
		while (name.isEmpty() && !server.outputStreams.containsKey(name))
		{
			System.out.println("Please enter your name");
			message.setAction(MessageAction.LOGIN);
			message.setMessage("Please enter your name");
			dout.writeUnshared(message);
			//dout.reset();
			
			din = new ObjectInputStream( socket.getInputStream() );
			System.out.println("server hier"); 
			message = (Message) din.readObject();
			System.out.println("bis hier");
			System.out.println("server msg: "+message);
			if(message.getAction() == MessageAction.LOGIN)
				name = message.getName();
			else {
				message.setMessage("Please LOGIN first");
				dout.writeUnshared(message);
				dout.reset();
			}
		}
		
		dout = new ObjectOutputStream( socket.getOutputStream() );
		din = new ObjectInputStream( socket.getInputStream() ); 

		
		message.setAction(MessageAction.CHAT);
		message.setMessage("Welcome "+name+" to our chat room!");
		dout.writeUnshared(message);
		//dout.reset();
		/*
		message.setMessage("To leave the chat please type /quit!");
		dout.writeUnshared(message);
		//dout.reset();
		
		message.setAction(MessageAction.CHAT);
		message.setMessage("New client <"+name+"> enterd the room!");
		
		server.sendToAll( message ); 
		
		server.outputStreams.put( socket, dout ); 
		*/
		
		boolean forever = true;
		while (forever) 
		{
			message = (Message) din.readObject();

			if((message.getAction() == MessageAction.CHAT && message.getMessage().equals("/quit")) || message.getAction() == MessageAction.LOGOUT)
			{
				message.setAction(MessageAction.CHAT);
				message.setMessage("*** Bye "+name+" ***");
				server.outputStreams.remove(socket);
				
		//		server.sendToAll( "Client "+"<"+name+"> left the room!");
				forever = false;
			}
			else
			{
				System.out.println( "Sending "+message ); 
				message.setName(name);
				server.sendToAll( message );
			}
		} 
	} catch( EOFException ie ) { 

		System.out.println("Server: End of file Exception!");
	 
	} catch( IOException ie ) { 

		System.out.println("Server: Input/Output Exception!");
		
	} catch (ClassNotFoundException e) {
		
		System.out.println("Server: Message Cast exception!");
		
	} finally { 
 
		server.removeConnection( socket ); 
		message.setAction(MessageAction.CHAT);
		message.setMessage("Client <"+name+"> left the room!");
		server.sendToAll( message );

		} 
	} 
} 