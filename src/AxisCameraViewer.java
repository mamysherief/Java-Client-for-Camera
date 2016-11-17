import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class AxisCameraViewer {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket client = new Socket("192.168.20.246", 4444);
		Thread t1 = new Thread(new AxisCameraClient(client));
		t1.start();
	}
}
