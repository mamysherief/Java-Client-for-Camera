
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

public class RecieveNew {
	public static void main(String[] args) {


		try {
			Socket client = new Socket("192.168.20.246", 4444);
			InputStream in = client.getInputStream();
			DataInputStream data = new DataInputStream(in);
			
			System.out.println("connected ..."); // to receive welcome first then size ...
			DataOutputStream dout = new DataOutputStream(client.getOutputStream());
            //dout.writeUTF("capture-cameraIP=192.168.20.248&capture-userpass=root:passs&resolution=176x144&fps=1");
            dout.writeUTF("capture-cameraIP=192.168.20.246&capture-userpass=root:passs&resolution=176x144&fps=1");

			//while(true){//Infinite loop for reading images
            int temp = 0;			
            while(temp<10){		
				
				int size = data.readInt(); //Reading image size first
				System.out.println("Frame size: " + size);

				byte[] bytes = new byte[size]; //Creating a byte array for image data
				for(int i = 0; i < size; i++) { //Now byte array includes the image data
					in.read(bytes, i, 1);
				}
				
				//save(byte); //Just save the byte array into a file
				

				//convert byte[] to image
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
                System.out.println("Image received!!!!"); 
                previewBImg(img);
                
                temp++;	// just to check 10 times
				
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	
	
	 private static void previewBImg(BufferedImage img) {
	    	JFrame frame = new JFrame();
	 		frame.setVisible(true);
	 		frame.setSize(600, 600);
	 		frame.setTitle("Sent Image");
	 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	 		
			JLabel label = new JLabel(new ImageIcon(img));
		    frame.add(label);
		    frame.repaint();
		}
}