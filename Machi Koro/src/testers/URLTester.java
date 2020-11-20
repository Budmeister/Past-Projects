package testers;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class URLTester {
	static final String dir = "http://www.bruh123.com/";

	public static void main(String[] args) {
		new Thread(() -> recieve()).start();
		new Thread(() -> send()).start();
	}
	
	static void send() {
		try {
			URL url = new URL(dir);
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			PrintStream ps = new PrintStream(con.getOutputStream());
			ps.println("BOIIIIIII");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void recieve() {
		Scanner s = null;
		try {
			URL url = new URL(dir);
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			s = new Scanner(con.getInputStream());
			while(s.hasNext())
				System.out.println(s.nextLine());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(s != null)
				s.close();
		}
	}

}
