package generators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class GreySquare {
	
	int darkness = 25, opaqueness = 100;

	public static void main(String[] args) throws IOException {
		GreySquare gs = new GreySquare();
		gs.generate();
		
		System.exit(0);
	}
	
	void generate() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		
		int choice = fileChooser.showOpenDialog(new JFrame());
		if(choice != JFileChooser.APPROVE_OPTION)
			return;
		
		File f = fileChooser.getSelectedFile();
		
		BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		g.setColor(new Color(darkness, darkness, darkness, opaqueness));
		g.fillRect(0, 0, 100, 100);
		ImageIO.write(img, "png", f);
	}

}
