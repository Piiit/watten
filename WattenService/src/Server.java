import java.io.*; 
import java.net.*; 
import java.util.*; 


public class Server {


	private ServerSocket serversocket; 

	public Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>(); 

	final private int MAX_CONNECTIONS  = 3;
	
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

			DataOutputStream dout = new DataOutputStream( socket.getOutputStream() ); 


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

	Enumeration<DataOutputStream> getOutputStreams() { 


		return outputStreams.elements(); 
	} 

	void sendToAll( String message ) { 

		synchronized( outputStreams ) { 
			
			for (Enumeration<DataOutputStream> e = getOutputStreams(); e.hasMoreElements(); ) { 
				
				DataOutputStream dout = e.nextElement(); 
			
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
