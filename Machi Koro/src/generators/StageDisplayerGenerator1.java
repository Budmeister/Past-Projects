package generators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import thePackage.FrameHandler;
import thePackage.StageDisplay;

public class StageDisplayerGenerator1 {

	public static void main(String[] args) {
		StageDisplayerGenerator1 sdg = new StageDisplayerGenerator1();
		sdg.start();
	}
	
	void start() {
		JFileChooser fileChooser = new JFileChooser();
		
		int choice = fileChooser.showOpenDialog(new JFrame());
		if(choice != JFileChooser.APPROVE_OPTION)
			return;
		
		File f = fileChooser.getSelectedFile();
		String parentDir = f.getParent();
		drawStage1(parentDir);
		drawStage2(parentDir);
		drawStage3(parentDir);
		System.exit(0);
	}
	
	void drawStage1(String parentDir) {
		BufferedImage img = new BufferedImage(200, 100, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		g.setColor(FrameHandler.accent4);
		g.fillRect(0, 0, 199, 99);
		g.setColor(Color.black);
		g.drawRect(0, 0, 199, 99);
		BufferedImage dice = null;
		try {
			dice = ImageIO.read(StageDisplayerGenerator1.class.getResource("/images/dice_blue/dice_5_slanted_blue.png"));
		} catch (IOException e) {}
		g.drawImage(dice, 50, 0, null);
		
		try {
			File f = new File(parentDir + "/stage1.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {}
	}
	
	void drawStage2(String parentDir) {
		BufferedImage img = new BufferedImage(200, 100, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		g.setColor(FrameHandler.accent1);
		g.fillRect(0, 0, 199, 99);
		g.setColor(Color.black);
		g.drawRect(0, 0, 199, 99);
		
		for(int a = 0; a < StageDisplay.colors.length; a++) {
			g.setColor(StageDisplay.colors[a]);
			g.fillRect((int) (10 + 47.5 * a), 31, 37, 37);
			g.setColor(Color.black);
			g.drawRect((int) (10 + 47.5 * a), 31, 37, 37);
		}

		try {
			File f = new File(parentDir + "/stage2.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {}
	}
	
	void drawStage3(String parentDir) {
		BufferedImage img = new BufferedImage(200, 100, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		g.setColor(FrameHandler.accent2);
		g.fillRect(0, 0, 199, 99);
		g.setColor(Color.black);
		g.drawRect(0, 0, 199, 99);
		BufferedImage money = null, establishments = null;
		try {
			money = ImageIO.read(StageDisplayerGenerator1.class.getResource("/images/moneypiles150x150_v2/random_money_pile.png"));
			establishments = ImageIO.read(StageDisplayerGenerator1.class.getResource("/images/moneypiles150x150_v2/random_establishments.png"));
		} catch (IOException e) {}
		g.drawImage(money, 0, 0, 100, 100, null);
		g.drawImage(establishments, 100, 20, 100, 59, null);

		try {
			File f = new File(parentDir + "/stage3.png");
			ImageIO.write(img, "png", f);
		} catch (IOException e) {}
	}

}
