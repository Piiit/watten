package com.mpp.network;

import java.io.*; 
import java.net.*; 

import com.mpp.tools.MessageAction;

public class Client implements Runnable 
{ 

	private Socket socket;
	private boolean close = false;

	private ObjectOutputStream dout; 
	private ObjectInputStream din; 

	public Client( String host, int port ) { 

		try {
			socket = new Socket( host, port ); 

			System.out.println( "connected to "+socket ); 

			din = new ObjectInputStream( socket.getInputStream() ); 
			dout = new ObjectOutputStream( socket.getOutputStream() ); 

			new Thread( this ).start(); 
			
		} catch( IOException ie ) { System.out.println( ie ); } 
	} 

	public void processMessage( Message message ) { 
		try { 
			
			System.out.println("hier 2");
			 
//			dout = new ObjectOutputStream( socket.getOutputStream() );
			
			System.out.println("hier 3");
			System.out.println(message);
			System.out.println("test 1");
			dout.writeUnshared( message );
			System.out.println("test 2");
			//din = new ObjectInputStream( socket.getInputStream() );
			//dout.reset();
		
			if((message.getAction() == MessageAction.CHAT && message.getMessage().equals("/quit")) || message.getAction() == MessageAction.LOGOUT)
				close = true;
		} catch( IOException ie ) { System.out.println( ie ); } 
	} 

	public void run() { 
		try { 
			
			
			boolean forever = true;
			while (forever) {
				

				din = new ObjectInputStream( socket.getInputStream() ); 
				dout = new ObjectOutputStream( socket.getOutputStream() ); 
				
				System.out.println("test");
				Message message = null;
				
				try {
					
					message = (Message) din.readObject();
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				System.out.println("read"+message);
				
				if(message.getAction() == MessageAction.LOGIN){
					message = new Message(MessageAction.LOGIN);
					message.setName("patrick"+System.currentTimeMillis());
					System.out.println("hier");
					processMessage(message);
				}
				
				if(message.getAction() == MessageAction.CHAT) {
			
					System.out.println(message.getMessage());
				}
				
				if(close == true)
				{
					forever = false;
					socket.close();
				}
			} 
		} catch( IOException ie ) { System.out.println( ie ); } 
	}
} 