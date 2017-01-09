
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Just a simple client application
 * for a camera server
 * @author  Mohammed Sherief
 * @version 1.02, 05/10/2016
 */

public class AxisCameraClient implements Runnable{
	Socket client;
	int camPort, frmWidth, frmHeight;
	private String camParameter, camIP, camUserPass, camRes, camFps;
	private JTextField userText;
	private JTextArea chatWindow;
	private JScrollPane sp;
	
	
	// for chatting
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public AxisCameraClient(String camIP, String camRes, String camFps) {
		this.camIP = camIP;
		this.camPort = 4444;
		this.camRes = camRes;
		this.camFps = camFps;
		this.camUserPass = "root:passs";
		//this.camParameter = "capture-cameraIP=" + (String)camIP + "&capture-userpass=" + camUserPass + "&resolution=" + camRes + "&fps=" + camFps;
		this.camParameter = "resolution=" + camRes + "&fps=" + camFps + '\0';
		this.frmWidth = 300 + Integer.parseInt(camRes.substring(0, camRes.indexOf("x")));
		this.frmHeight = 30 + Integer.parseInt(camRes.substring(camRes.indexOf("x") + 1));
	}

	@Override
	public void run() {
		
		try {
			client = new Socket(camIP, camPort);
			System.out.println("connected to " + camIP + " on port " + camPort);
			System.out.println(camParameter);
			System.out.println("Window frame is " + frmWidth + " by " + frmHeight);
			InputStream in = client.getInputStream();
			DataInputStream data = new DataInputStream(in);
			DataOutputStream dout = new DataOutputStream(client.getOutputStream());
			
            dout.writeBytes(camParameter);
            dout.flush();
            
            //initialize GUI
            JFrame frame = new JFrame();
	 		frame.setVisible(true);
	 		frame.setSize(frmWidth, frmHeight);
	 		frame.setTitle("Live Video Stream with a resolution of " + camRes + " and fps of " + camFps);
	 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 		
	 		// one panal for the logo and another for btn, cmb, txt
			JPanel panel1 = new JPanel();
			JPanel panel2 = new JPanel();
	 		JPanel panel3 = new JPanel();
	 		
	 		// welcome logo and place holder for video stream
	 		BufferedImage img = ImageIO.read(AxisCameraClient.class.getResource("/mahAxisLogo.jpg"));
	 		JLabel label = new JLabel(new ImageIcon(img));
	 	
	 		// initialize text area and chat window
	 		userText = new JTextField(50);
	 		chatWindow = new JTextArea(15,25);
	 		
	 		// add action listener
			userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
			);
	 		
			panel1.add(label);
			panel2.add(chatWindow);
			panel2.add(new JScrollPane(sp), BorderLayout.CENTER);
			panel3.add(userText);
		    frame.add(panel1, BorderLayout.WEST);
			frame.add(panel2, BorderLayout.EAST);
		    frame.add(panel3, BorderLayout.SOUTH);
			
		    // put this in the label/panal listener
            liveStream(in, data, label, frame);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
		
	public void liveStream(InputStream in, DataInputStream data, JLabel label, Frame frame) throws IOException{
		while(true){//Infinite loop for reading images
			
			int size = data.readInt(); //Reading image size first
			//System.out.println("Frame size: " + size);
	
			byte[] bytes = new byte[size]; //Creating a byte array for image data
			for(int i = 0; i < size; i++) { //Now byte array includes the image data
				in.read(bytes, i, 1);
			}
			
	        //System.out.println("Image received!!!!"); 
	        //preview image stream
		    
		    ImageIcon image = new ImageIcon(bytes);
	        label.setIcon(image);
	        label.validate();
	        frame.repaint();
		}
	}
	
	
	
	
	
	// chatting part
	//set up and run the server - background work
	public void startChatting(){
		// in case if we mess up something and error occurs we use try catch
		try {
			// set up the server
			server = new ServerSocket(5555, 100);
			//the code that you want to run over and over again
			while(true){
				//try to do something and if we do it wrong then we want to catch it
				try{
					// we are going to run 3 methods which are ...
					// the method that handles the  ...
					waitForConnection();
					//setup Input and output streams
					setupStreams();
					//
					whileChatting();
				}catch(EOFException eofException){
					showMessage("\n Server ended the connection!");
				}finally{
					//house keeping stuff like end the stream close the connection and ...
					cleanUp();
				}
				
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	// wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		// to notify the user that the ... successfull and waiting for others to connect 
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException{
		// set up the output stream - path way to send data
		output = new ObjectOutputStream(connection.getOutputStream());
		// house keeping to clean the buffer from bytes w/c are leftover in the stream
		output.flush();
		// set up the input stream - path
		input = new ObjectInputStream(connection.getInputStream());
		// you cant flush here. the sender only flush
		showMessage("\n Steams are now setup! you can start to chat \n");
	}
	
	// during the chat conversation
	private void whileChatting() throws IOException{ 
		String message = "You are now connected!";
		sendMessage(message);
		//allow the user to type in the text box
		ableToType(true);
		do{
			//have a conversation
			try{
				//input is the stream where the message (data/bytes) comes through
				//cast it to string to make sure it is a string and nothing else
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				// in case if they send something other than string or mess up
				showMessage("\n I don't know what the user sent!");
			}
			//When some one need to break conversation
		}while(!message.equals("CLIENT - END"));
	}
	
	//send a message to client - different from showMessage
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			//then flush it out once you send something in case if bytes are left in the stream
			//so you don't have leftovers - house keeping stuff
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n ERROR: DUDE I CAN'T SEND THAT MESSAGE");
		}
	}
	
	//updates chatWindow
	private void showMessage(final String text){
		// we update part of the GUI that changes and leave others that don't
		SwingUtilities.invokeLater(						// creates a thread that only appends the chat window inside the run()
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);
	}
	
	// let the user type a msg in to the text...
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
	
	//Close the streams and sockets
	private void cleanUp() {
		showMessage("\n Closing Down ...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
}
	
