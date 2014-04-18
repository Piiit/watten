import java.io.*; 
import java.net.*; 
import java.util.*; 


public class Server {


	private ServerSocket serversocket; 

	public Hashtable<Socket, ObjectOutputStream> outputStreams = new Hashtable<Socket, ObjectOutputStream>(); 

	final private int MAX_CONNECTIONS  = 4;
	
	private int count = 0;

	public Server( int port ) throws IOException { 

		listen( port ); 
		
	} 


	private void listen( int port ) throws IOException { 

		serversocket = new ServerSocket( port ); 

		System.out.println( "Listening on "+serversocket ); 


		while (true) { 

			Socket socket = serversocket.accept(); 

			System.out.println( "Connection from "+socket ); 

			ObjectOutputStream dout = new ObjectOutputStream( socket.getOutputStream() ); 
			dout.writeUTF("HHHH");

			if(count < MAX_CONNECTIONS)
			{
				count++;
				new ServerThread( this, socket );
			}
			else
			{
				dout.writeUTF("Maximum of "+MAX_CONNECTIONS+" reached! You can't connect to server !");
			}

		} 
	} 

	Enumeration<ObjectOutputStream> getOutputStreams() { 


		return outputStreams.elements(); 
	} 

	void sendToAll( String message ) { 

		synchronized( outputStreams ) { 
			
			for (Enumeration<ObjectOutputStream> e = getOutputStreams(); e.hasMoreElements(); ) { 
				
				ObjectOutputStream dout = e.nextElement(); 
			
				try { 
					dout.writeUTF(message ); 
				} catch( IOException ie ) { System.out.println( ie ); } 
			} 
		} 
	} 

	void removeConnection( Socket s ) { 

		synchronized( outputStreams ) { 

			System.out.println( "Removing connection to "+s ); 

			 count--;

			try { 
				s.close(); 

			} catch( IOException ie ) { 
				System.out.println( "Error closing "+s ); 
				ie.printStackTrace(); 


			} 
		} 
	} 

	static public void main( String args[] ) throws Exception { 

		int port = 5555;

		new Server( port ); 


	} 
} 
