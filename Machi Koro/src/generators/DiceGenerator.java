package generators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import thePackage.FrameHandler;

public class DiceGenerator {
	
	static Color[] colors = {FrameHandler.accent1, FrameHandler.accent4};
	static String[] names = {"blue", "green"};
	
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser();
		int choice = fileChooser.showOpenDialog(new JFrame("Where should we put the dice"));
		
		if(choice == JFileChooser.APPROVE_OPTION) {
			for(int a = 0; a < colors.length; a++) {
				Color color = colors[a];
				File folder = new File(fileChooser.getSelectedFile().getAbsolutePath() + '_' + names[a]);
				for(int b = 0; b < 6; b++) {
					File f = new File(folder.getAbsolutePath() + "/dice_" + (b + 1) + "_" + names[a] + ".png");
					f.mkdirs();
					
					BufferedImage i = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
					Graphics g = i.getGraphics();
					g.translate(50, 50);
					
					drawDice(g, b, color);
					
					try {
						ImageIO.write(i, "png", f);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					File f2 = new File(folder.getAbsolutePath() + "/dice_" + (b + 1) + "_slanted_" + names[a] + ".png");
					f2.mkdirs();
					
					BufferedImage i2 = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
					Graphics2D g2 = (Graphics2D) i2.getGraphics();
					g2.rotate(Math.PI / 4);
					g2.translate(50 * Math.sqrt(2), 0);
					drawDice(g2, b, color);
					
					try {
						ImageIO.write(i2, "png", f2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.exit(0);
	}
	
	public static int[][] positionsX = {{0},
								{-1,1},
								{-1,0,1},
								{-1,1,-1,1},
								{-1,1,0,-1,1},
								{-1,1,-1,1,-1,1}};
	public static int[][] positionsY = {{0},
								{-1,1},
								{-1,0,1},
								{-1,-1,1,1},
								{-1,-1,0,1,1},
								{-1,-1,0,0,1,1}};
	
	static void drawDice(Graphics g, int a, Color color) {
		int size = 66;
		int dist = 15;
		int thickness = 7;
		int curve = 20;
		g.setColor(Color.black);
		g.fillRoundRect(-size / 2, -size / 2, size, size, curve, curve);
		g.setColor(color);
		g.fillRoundRect(-size / 2 + thickness, -size / 2 + thickness, size - 2 * thickness, size - 2 * thickness, curve - thickness, curve - thickness);
		
		g.setColor(Color.black);
		int[] xs = positionsX[a];
		int[] ys = positionsY[a];
		for(int c = 0; c < xs.length; c++) {
			int x = dist * xs[c];
			int y = dist * ys[c];
			g.fillOval(x - thickness / 2, y - thickness / 2, thickness, thickness);
		}
	}

}
