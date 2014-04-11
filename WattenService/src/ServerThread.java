import java.io.*; 
import java.net.*; 


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

	try { 

		DataOutputStream dout = new DataOutputStream( socket.getOutputStream() );
		DataInputStream din = new DataInputStream( socket.getInputStream() ); 

		
		while (name.isEmpty() && !server.outputStreams.containsKey(name))
		{
			dout.writeUTF("Please enter your name ... ");
			name = din.readUTF();
		}
			
		dout.writeUTF("Welcome "+name+" to our chat room!");
		dout.writeUTF("To leave the chat please type /quit! ");
			
		server.sendToAll( "New client <"+name+"> enterd the room!" ); 
		
		server.outputStreams.put( socket, dout ); 

		boolean forever = true;
		while (forever) 
		{
			String message = din.readUTF(); 

			if(message.equals("/quit"))
			{
				dout.writeUTF("*** Bye "+name+" ***");
				server.outputStreams.remove(socket);
		//		server.sendToAll( "Client "+"<"+name+"> left the room!");
				forever = false;
			}
			else
			{
				System.out.println( "Sending "+message ); 
				server.sendToAll( "<"+name+"> "+message );
			}
		} 
	} catch( EOFException ie ) { 

		System.out.println("Server: End of file Exception!");
	 
	} catch( IOException ie ) { 

		System.out.println("Server: Input/Output Exception!");
		
	} finally { 
 
		server.removeConnection( socket ); 
		server.sendToAll( "Client <"+name+"> left the room!" );

		} 
	} 
} 