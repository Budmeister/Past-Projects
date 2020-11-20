package generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import thePackage.TissueProject;

public class ImageNameGenerator {

	public static void main(String[] args) throws FileNotFoundException {
		ImageNameGenerator ing = new ImageNameGenerator();
		ing.generate();
		
		System.exit(0);
	}
	
	void generate() throws FileNotFoundException {
		File imagesFolder = new File(getPathNameFromURL(TissueProject.class.getResource("/images")));
		
		JFileChooser fileChooser = new JFileChooser();
		
		int choice = fileChooser.showOpenDialog(new JFrame());
		
		if(choice != JFileChooser.APPROVE_OPTION)
			return;
		
		File f = fileChooser.getSelectedFile();
		PrintStream ps = new PrintStream(f);
		File[] images = imagesFolder.listFiles();
		for(File image : images)
			ps.println(image.getName());
		
		ps.close();
	}
	
	private static String getPathNameFromURL(URL url) {
		String s = url.getFile();
		if(!s.contains("%20"))
			return s;
		for(int a = 0; a < s.length() - 3; a++) {
			String sub = s.substring(a, a + 3);
			if(sub.equals("%20"))
				s = s.substring(0, a) + " " + s.substring(a + 3);
		}
		return s;
	}

}
