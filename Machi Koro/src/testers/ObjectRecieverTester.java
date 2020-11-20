package testers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ObjectRecieverTester {

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		Socket sock = new Socket("localhost", 5);
		ObjectInputStream o = new ObjectInputStream(sock.getInputStream());
		
//		Object o1 = o.readObject();
//		System.out.println(o1);
		Object o2 = o.readObject();
		System.out.println(o2);
		
		o.close();
		sock.close();
	}

}
