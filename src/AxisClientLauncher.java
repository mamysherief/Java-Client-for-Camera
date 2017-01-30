import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AxisClientLauncher {
	
	private JFrame frame;
	private JLabel label;
	private JButton button;

	private JComboBox<String> cmbResLst;
	private JComboBox<String> cmbFpsLst;
	private JLabel lbl4all;
	
	private JTextField txtfield;
	public String camIP, camUserPass, camRes, camFps;
	
	chatServer chattingServer = new chatServer();
	boolean asServer = true;
	
	public AxisClientLauncher() throws IOException {
		// initiate parameter
		camIP = "192.168.20.246";
		camUserPass = "root:passs";
		camRes = "400x320";
		camFps = "20";
		
		//Axis Client Launcher GUI
		frame = new JFrame();
		frame.setVisible(true);
		frame.setSize(600, 300);
		frame.setTitle("Axis Camera Client Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// one panal for the logo and another for btn, cmb, txt
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		
		// welcome logo and place holder
		BufferedImage img = ImageIO.read(AxisCameraClient.class.getResource("/mahAxisLogo.jpg"));
		label = new JLabel(new ImageIcon(img));
	    frame.add(label);
	    
	    // add a button to launch the axis camera client / live preview
	    button = new JButton("Live Preview");
		frame.add(button, BorderLayout.SOUTH);
		frame.repaint();
		 
		//add lbl4all 
	    lbl4all = new JLabel("Select your parameters:");
	    panel2.add(lbl4all);
	    
	    //add txtfield for entering IP of the camera
	    txtfield = new JTextField("192.168.20.246");
	    panel2.add(txtfield);
	    
		// add resolution combo box
		String[] strResLst = {"1280x960","1280x720","1024x768","1024x640","800x600","800x500","800x450","640x480","640x400","640x360","480x360","480x300","480x270","320x240","320x200","320x180","240x180","176x144","160x120","160x100","160x90"};
		cmbResLst = new JComboBox<String>(strResLst);
		panel2.add(cmbResLst);
		
		// add fps combo box
		String[] strFpsLst = {"12","15","20","25","30","48"};
		cmbFpsLst = new JComboBox<String>(strFpsLst);
		panel2.add(cmbFpsLst);
		
		// add a button to launch the axis camera client / live preview
	    button = new JButton("Live Preview");
		panel2.add(button);
		
		frame.add(panel1, BorderLayout.NORTH);
		frame.add(panel2, BorderLayout.SOUTH);
		frame.repaint();	
		frame.revalidate();
		
		// add action listener for button
		button.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					camIP = txtfield.getText();
					camRes = (String)cmbResLst.getSelectedItem();
					camFps = (String)cmbFpsLst.getSelectedItem();
					try {
						//TODO: need to have a logic/protocol to check 
						// 		if the person to be called is online.
						//		maybe in a separate function.
						startLivePreview(!asServer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
		);
		
		// start listening for incoming calls/connections
		System.out.println("Listening for connections...");
		chattingServer.connect();
		// if connected pass the connection to the chat app
		doSomeThing();
	}    

	public static void main(String[] args) throws UnknownHostException, IOException {
//		AxisClientLauncher axisClientLauncher = new AxisClientLauncher();
		new AxisClientLauncher();
		//axisClientLauncher.frame.setSize(600,300);
		//axisClientLauncher.frame.revalidate();
	}
	
	public void startLivePreview(boolean asServerOrClient) throws UnknownHostException, IOException {
		Thread t1 = new Thread(new AxisCameraClient(camIP, camRes, camFps, asServerOrClient));
		t1.start();
	}
	
	private void doSomeThing() throws UnknownHostException, IOException {
		// TODO pass the socket if possible
		// call startLivePreview() or ....
		// clean up before leaving
		startLivePreview(asServer);
		//chattingServer.setSocket(chattingServer);
	}
}
