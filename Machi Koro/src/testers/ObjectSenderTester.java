package testers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class ObjectSenderTester {

	public static void main(String[] args) throws IOException {
		ServerSocket servSock = new ServerSocket(5);
		Socket sock = servSock.accept();
		ObjectOutputStream o = new ObjectOutputStream(sock.getOutputStream());
		
		Object o2 = null;
		Breh b = new Breh(new Serializable() {});
		
		o.writeObject(b);
		o.flush();
		
		o.close();
		servSock.close();
	}

}

class Breh implements Serializable{
	Object o;
	Breh(Object o){
		this.o = o;
	}
}

enum Stuff{
	thing1(1),
	thing2(2),
	thing3(3);
	int a;
	Stuff(int a){
		this.a = a;
	}
	@Override
	public String toString() {
		return "" + a;
	}
}

class Bruh1{
	
	public Bruh1(int i, String string) {
		a = i; b = string;
	}
	
	int a;
	String b;
	
	public String toString() {
		return "a:" + a + ",b:" + b;
	}
}

class Bruh2 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6441681848161717579L;

	public Bruh2(int i, String string, Bruh1 bruh1) {
		a = i; b = string; c = bruh1;
	}
	
	int a;
	String b;
	Bruh1 c;
	
	public String toString() {
		return "a:" + a + ",b:" + b + ",c:" + c;
	}
	
}