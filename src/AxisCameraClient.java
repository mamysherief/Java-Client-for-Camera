
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
	
	public AxisCameraClient(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		
		try {
			//client = new Socket("192.168.20.246", 4444);
			System.out.println("connected ...");
			
			InputStream in = client.getInputStream();
			DataInputStream data = new DataInputStream(in);
			DataOutputStream dout = new DataOutputStream(client.getOutputStream());
			
			//pass parameters like resolution and fps
            //for example "capture-cameraIP=192.168.20.248&capture-userpass=root:passs&resolution=176x144&fps=1"
			//&resolution=864x486&fps=20 
            dout.writeUTF("capture-cameraIP=192.168.20.246&capture-userpass=root:passs&resolution=400x320&fps=20");
            
            //initialize GUI
            JFrame frame = new JFrame();
	 		frame.setVisible(true);
	 		frame.setSize(1300, 1000);
	 		frame.setTitle("Received Image");
	 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 		// welcome logo and place holder
	 		BufferedImage img = ImageIO.read(AxisCameraClient.class.getResource("/axisLogo.jpg"));
	 		JLabel label = new JLabel(new ImageIcon(img));
		    frame.add(label);
		    
            
			while(true){//Infinite loop for reading images
            //int temp = 0;			
           
            // while(temp<10){	
				
				int size = data.readInt(); //Reading image size first
				System.out.println("Frame size: " + size);

				byte[] bytes = new byte[size]; //Creating a byte array for image data
				for(int i = 0; i < size; i++) { //Now byte array includes the image data
					in.read(bytes, i, 1);
				}
				
                System.out.println("Image received!!!!"); 
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