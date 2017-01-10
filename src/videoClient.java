import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class videoClient {
	Socket client;
	DataInputStream data;
	DataOutputStream dout;
	InputStream in;
	
	public void connect(String camIP, int camPort, String camParameter) throws IOException{
		client = new Socket(camIP, camPort);
		in = client.getInputStream();
		data = new DataInputStream(in);
		dout = new DataOutputStream(client.getOutputStream());
		
        dout.writeBytes(camParameter);
        dout.flush();

	}
	
	public void liveStream(JLabel label, Frame frame) throws IOException{
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
}
