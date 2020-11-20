package testers;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Scanner;

import javax.swing.*;

public class SourceFileWriteTester {
	// CONCLUSION:
	// You cannot write to files that are locally placed

	public static void main(String[] args) throws IOException {
		writeFile("/TextDocuments/settings.txt");
		startJFrame();
	}
	
	static void writeFile(String dir) throws FileNotFoundException {
		String whatToWrite = "boolean show_money_bags = false;";
		URL url = SourceFileWriteTester.class.getResource(dir);
		dir = url.getFile();
		dir = dir.replace("%20", " ");
		File file = new File(dir);
		PrintStream ps = new PrintStream(file);
		ps.println(whatToWrite);
		ps.close();
	}
	
	static String readFile(String dir) throws IOException {
		URL url = SourceFileWriteTester.class.getResource(dir);
		InputStream is = url.openStream();
		Scanner s = new Scanner(is);
		String ln = s.nextLine();
		s.close();
		return ln;
	}
	
	static JLabel label;
	static JButton button;
	static void startJFrame() throws IOException {
		JFrame frame = new JFrame();
		label = new JLabel(readFile("/TextDocuments/settings.txt"));
		button = new JButton("Bruh");
		button.addActionListener(ae -> {
			try {
				writeFile("/TextDocuments/settings.txt");
				String s = readFile("/TextDocuments/settings.txt");
				label.setText(s);
			} catch (FileNotFoundException e) {
				label.setText("FileNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				label.setText("IOException");
				e.printStackTrace();
			}
		});
		
		frame.setLayout(new BorderLayout());
		frame.add(label, BorderLayout.NORTH);
		frame.add(button, BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
