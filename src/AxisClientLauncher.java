import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AxisClientLauncher {
	
	private JFrame frame;
	private JLabel label;
	private JButton button;
	
	public AxisClientLauncher() throws IOException {
		frame = new JFrame();
		frame.setVisible(true);
		//frame.setSize(300, 400);
		frame.setTitle("Axis Camera Client Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// welcome logo and place holder
		BufferedImage img = ImageIO.read(AxisCameraClient.class.getResource("/axisLogo.jpg"));
		label = new JLabel(new ImageIcon(img));
	    frame.add(label);
	    
	    // add a button to launch the axis camera client / live preview
	    button = new JButton("Live Preview");
		frame.add(button, BorderLayout.SOUTH);
		frame.repaint();
		 
		// add action listener
		button.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					try {
						startLivePreview();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
		);
	}    

	public static void main(String[] args) throws UnknownHostException, IOException {
		AxisClientLauncher axisClientLauncher = new AxisClientLauncher();
		axisClientLauncher.frame.setSize(350,200);
		axisClientLauncher.frame.revalidate();
	}
	
	public void startLivePreview() throws UnknownHostException, IOException {
		Socket client = new Socket("192.168.20.246", 4444);
		Thread t1 = new Thread(new AxisCameraClient(client));
		t1.start();
		
		Socket client2 = new Socket("192.168.20.246", 4444);
		Thread t2 = new Thread(new AxisCameraClient(client2));
		t2.start();
	}
	
}
