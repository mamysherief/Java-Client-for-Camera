
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
	videoClient axisVideoStream = new videoClient();
	chatServer chattingServer = new chatServer();
	chatClient chattingClient; 
	boolean isServer;
	int camPort, frmWidth, frmHeight;
	private String camParameter, camIP, camRes, camFps;
	private JTextField userText;
	private JTextArea chatWindow;
	private JScrollPane sp;
	
	public AxisCameraClient(String camIP, String camRes, String camFps, boolean asServerOrClient) {
		this.camIP = camIP;
		this.camPort = 4444;
		this.camRes = camRes;
		this.camFps = camFps;
		this.isServer = asServerOrClient;
		this.camParameter = "resolution=" + camRes + "&fps=" + camFps + '\0';
		this.frmWidth = 300 + Integer.parseInt(camRes.substring(0, camRes.indexOf("x")));
		this.frmHeight = 30 + Integer.parseInt(camRes.substring(camRes.indexOf("x") + 1));
	}

	@Override
	public void run() {
		
		try {
			//connect to camera and initialize
			axisVideoStream.connect(camIP, camPort, camParameter);
			System.out.println("connected to " + camIP + " on port " + camPort);
			System.out.println(camParameter);
			System.out.println("Window frame is " + frmWidth + " by " + frmHeight);
			
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
	 		
	 		// add action listener for userText
			userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						//chattingServer.sendMessage(event.getActionCommand());
						showAndSendMessage(event.getActionCommand());
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
			
		    // need to make it in a thread
		    // to continue executing the chatting part
//		    new Thread(() -> {
//		        try {
//					axisVideoStream.liveStream(label, frame);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		    }).start();
		    new Thread(new Runnable() {
		        public void run() {
		        	try {
						axisVideoStream.liveStream(label, frame);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		   }).start();
			
			System.out.println("initializing chatting. Please wait ...");
		    //chattingServer.connect();
			System.out.println("-------------------------");
			System.out.println("connected to a pc to chat");
			System.out.println("-------------------------");
			
			//start chatting
			if (isServer) {
				if (chattingServer.isConnected())
					startChatting();
			} else //this is if it is a client connection
				startChatting();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	/************** Chatting part *****************/
	
	private void startChatting() throws IOException { 
		//check if connection is established or not
		String message = "You are now connected!";
		showAndSendMessage(message);
		//allow the user to type in the text box
		ableToType(true);
		do{
			if (isServer)
				message = chattingServer.receiveMessage();
			else {
				chattingClient = new chatClient("192.168.20.240");
				message = chattingClient.receiveMessage();
			}
			showAndSendMessage("\n" + message);
		}while(!message.equals("END"));
	}
	
	//updates chatWindow
	private void showAndSendMessage(final String text){
		// we update part of the GUI that changes and leave others that don't
		// creates a thread that only appends the chat window inside the run()
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
					//send the same message
					if (isServer)
						chattingServer.sendMessage(text);
					else
						chattingClient.sendMessage(text);
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
	
}
