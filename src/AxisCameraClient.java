
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
	
	public AxisCameraClient(String camIP, String camRes, String camFps) {
		this.camIP = camIP;
		this.camPort = 4444;
		this.camRes = camRes;
		this.camFps = camFps;
		this.camUserPass = "root:passs";
		//this.camParameter = "capture-cameraIP=" + (String)camIP + "&capture-userpass=" + camUserPass + "&resolution=" + camRes + "&fps=" + camFps;
		this.camParameter = "resolution=" + camRes + "&fps=" + camFps + '\0';
		this.frmWidth = 20 + Integer.parseInt(camRes.substring(0, camRes.indexOf("x")));
		this.frmHeight = 40 + Integer.parseInt(camRes.substring(camRes.indexOf("x") + 1));
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
	 		// welcome logo and place holder
	 		BufferedImage img = ImageIO.read(AxisCameraClient.class.getResource("/mahAxisLogo.jpg"));
	 		JLabel label = new JLabel(new ImageIcon(img));
		    frame.add(label);
		    
            
			while(true){//Infinite loop for reading images
            //int temp = 0;			
           
            // while(temp<10){	
				
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
                
              //  temp++;	// just to check 10 times
				
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}