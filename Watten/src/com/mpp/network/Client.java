package com.mpp.network;

import java.io.*; 
import java.net.*; 

public class Client implements Runnable 
{ 

	private Socket socket;
	private boolean close = false;

	private DataOutputStream dout; 
	private DataInputStream din; 

	public Client( String host, int port ) { 

		try {
			socket = new Socket( host, port ); 

			System.out.println( "connected to "+socket ); 

			din = new DataInputStream( socket.getInputStream() ); 
			dout = new DataOutputStream( socket.getOutputStream() ); 

			new Thread( this ).start(); 
		} catch( IOException ie ) { System.out.println( ie ); } 
	} 

	public void processMessage( String message ) { 
		try { 

			dout.writeUTF( message ); 
			if(message.equals("/quit"))
				close = true;
		} catch( IOException ie ) { System.out.println( ie ); } 
	} 

	public void run() { 
		try { 

			boolean forever = true;
			while (forever) { 

				String message = din.readUTF();
				System.out.println(message);
				if(close == true)
				{
					forever = false;
					socket.close();
				}
			} 
		} catch( IOException ie ) { System.out.println( ie ); } 
	}
} 