package com.mpp.network;

import java.io.*; 
import java.net.*; 
import com.mpp.tools.MessageAction;

public class Client implements Runnable { 

	private Socket socket;
	private boolean close = false;

	private ObjectOutputStream dout; 
	private ObjectInputStream din; 

	public Client( String host, int port ) throws Exception { 

		try {
			socket = new Socket( host, port ); 

			System.out.println( "connected to "+socket ); 

			din = new ObjectInputStream( socket.getInputStream() ); 
			dout = new ObjectOutputStream( socket.getOutputStream() ); 

			new Thread( this ).start(); 
			
		} catch( Exception e ) { 
			System.out.println( e );
			throw e;
		} 
	} 

	public void processMessage( Message message ) throws Exception { 
		System.out.println(message);
		
		dout = new ObjectOutputStream( socket.getOutputStream() ); 
		dout.writeObject(message);
		dout.close();
	
		if((message.getAction() == MessageAction.CHAT && message.getMessage().equals("/quit")) || message.getAction() == MessageAction.LOGOUT)
			close = true;
	} 

	public void run() { 
		try { 
			din = new ObjectInputStream( socket.getInputStream() ); 
			dout = new ObjectOutputStream( socket.getOutputStream() ); 
			
			boolean forever = true;
			while (forever && !socket.isClosed()) {
				Message message = null;
				Object object = null;
				
//				Thread.sleep(10000);
				try {
					object = din.readObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println("read: "+object);
				
				if(object == null) {
					continue;
				}
				
				if(object instanceof Message) {
					message = (Message)object;
				}
				
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
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
} 