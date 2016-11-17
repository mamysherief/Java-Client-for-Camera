import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AxisClientLauncher {
	
	private JFrame frame;
	private JLabel label;
	
	public AxisClientLauncher() throws IOException{
		frame = new JFrame();
		frame.setVisible(true);
		frame.setSize(300, 400);
		frame.setTitle("Axis Camera Client Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// welcome logo and place holder
		BufferedImage img = ImageIO.read(AxisCameraClient.class.getResource("/axisLogo.jpg"));
		label = new JLabel(new ImageIcon(img));
	    frame.add(label);
	    frame.repaint();
	}    

	public static void main(String[] args) throws UnknownHostException, IOException {
		AxisClientLauncher axisClientLauncher = new AxisClientLauncher();
		
		Socket client = new Socket("192.168.20.246", 4444);
		Thread t1 = new Thread(new AxisCameraClient(client));
		t1.start();
	}
}
