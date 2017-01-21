import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class chatServer {

		private ObjectOutputStream output;
		private ObjectInputStream input;
		private ServerSocket server;
		private Socket connection;
		
		public void connect(){
			//the serverSocket need only be initiated and binded once
			//other clients should only use the one that was created when the 1st client connected
			if (isConnected()) {
				try {
					server = new ServerSocket(5555, 100);
					waitForConnection();
					setupStreams();
				} catch (IOException e) {
					System.out.print("Error: Could not initialize a server socket for chatting");
					e.printStackTrace();
				}	
			}
		}

		// wait for connection
		private void waitForConnection() throws IOException{
			connection = server.accept();
		}
		
		// get stream to send and receive data
		private void setupStreams() throws IOException{
			// set up the output stream
			output = new ObjectOutputStream(connection.getOutputStream());
			// set up the input stream
			input = new ObjectInputStream(connection.getInputStream());
		}
		
		// a method to receive a message
		public String receiveMessage() throws IOException{ 
			String message = "Nothing has been received";
			
			do{
				try{
					message = (String) input.readObject();
				}catch(ClassNotFoundException classNotFoundException){}
			}while(!message.equals("CLIENT - bye"));
			
			return message;
		}
		
		//a method to send a message
		public void sendMessage(String message){
			try{
				//message must be formated as below before sending
				//output.writeObject("SERVER - " + message);
				output.writeObject(message);
				output.flush();
			}catch(IOException ioException){}
		}
		
		//Close the streams and sockets
		public void disconnect() {
			try{
				output.close();
				input.close();
				connection.close();
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		
		//check if connected
		public boolean isConnected(){
			boolean status = false;
			
			if (server != null)
				status = true;
			return status;
		}
}
