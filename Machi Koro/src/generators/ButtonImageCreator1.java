package generators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import thePackage.FrameHandler;

public class ButtonImageCreator1 {
	JFileChooser fileChooser;
	JColorChooser colorChooser;
	static int width = 200, height = 100;

	public static void main(String[] args) {
		ButtonImageCreator1 bic = new ButtonImageCreator1();
		width = 1000; height = 100;
		bic.start();
//		width = 200; height = 100;
//		bic.start();
		System.exit(0);
	}
	
	int radius = 100;
	
	void start() {
		fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(new JFrame("Save"));
		if(result != JFileChooser.APPROVE_OPTION)
			return;
		colorChooser = new JColorChooser();
		colorChooser.setColor(FrameHandler.background1);
		Color color1 = JColorChooser.showDialog(new JFrame("Color1"), "Color1", Color.BLACK);
		File file = fileChooser.getSelectedFile();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setColor(color1);
		g.fillRoundRect(0, 0, width, height, radius, radius);
		for(int y = getTopY(); y < getBottomY(); y++) {
			for(int x = getLeftX(y); x < getRightX(y); x++) {
				double alph = getAlpha(y);
				g.setColor(new Color(255, 255, 255, (int) alph));
				g.fillRect(x, y, 1, 1);
			}
		}
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			// XXX Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(file);
		System.out.println(color1);
	}
	
	int getLeftX(int y) {
		return (int) -Math.sqrt(Math.pow((getBottomY() - getTopY()),2) - Math.pow(y-getBottomY(),2)) + getBottomY();
	}
	
	int getRightX(int y) {
		return width - getLeftX(y);
	}
	
	int getTopY() {
		return 5;
	}
	
	int getBottomY() {
		return 50;
	}
	
	double getAlpha(int y) {
		return 255- ((double) (y - getTopY())) / (getBottomY() - getTopY()) * 255;
	}

}
