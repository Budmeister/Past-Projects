package generators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class BankMoneyPileImageGenerator {

	public static void main(String[] args) throws IOException {
//		BankMoneyPileImageGenerator bmpig = new BankMoneyPileImageGenerator();
//		bmpig.generate();
		testRandom();
	}
	
	static int width = 550, height = 250;
	int numCoins = 200;
	void generate() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		int choice = fileChooser.showOpenDialog(new JFrame());
		
		if(choice != JFileChooser.APPROVE_OPTION)
			return;
		
		Class<BankMoneyPileImageGenerator> clazz = BankMoneyPileImageGenerator.class;
		BufferedImage one = ImageIO.read(clazz.getResource("/images/cover/money-1.png"));
		BufferedImage fiv = ImageIO.read(clazz.getResource("/images/cover/money-5.png"));
		BufferedImage ten = ImageIO.read(clazz.getResource("/images/cover/money-10.png"));
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) img.getGraphics();
		
		BufferedImage ci;
		int w;
		for(int a = 0; a < numCoins; a++) {
			int x = (int) (Math.random() * width);
			double c = getCat(x);
			int y = (int) ((height - height / 2 * signedWeightedRandom(c)) / 2);
//			rotateRandom(g, x, y);
			double r = Math.random();
			if(r > 2.0 / 3) {
				ci = one;
				w = 25;
			}else if(r > 1.0 / 3) {
				ci = fiv;
				w = 50;
			}else {
				ci = ten;
				w = 75;
			}
			System.out.println("x:" + x + ",y:" + y + ",w:" + w + ",c:" + c);
			x-=w/2;
			y-=w/2;
			g.drawImage(ci, x, y, w, w, null);
		}
		
		File f = fileChooser.getSelectedFile();
		ImageIO.write(img, "png", f);
		
		JFrame frame = new JFrame();
		@SuppressWarnings("serial")
		JComponent comp = new JComponent() {
			public void paintComponent(Graphics g) {
				g.drawImage(img, 0, 0, null);
			}
		};
		comp.setPreferredSize(new Dimension(width, height));
		frame.add(comp);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	void rotateRandom(Graphics2D g, int x, int y) {
		g.rotate(2 * Math.PI * Math.random(), x, y);
	}
	
	static double weightedRandom(double w) {
		return Math.pow(Math.random(), w);
	}
	
	static double signedWeightedRandom(double w) {
		if(Math.random() > .5)
			return weightedRandom(w);
		else
			return -weightedRandom(w);
	}
	
	static double getCat(int x) {
		return Math.abs(x - width / 2) / (width / 10);
	}
	
	static void testRandom() {
		JFrame frame = new JFrame();
		@SuppressWarnings("serial")
		JComponent comp = new JComponent() {
			public void paintComponent(Graphics g) {
				g.setColor(Color.red);
				g.drawRect(0, 0, 300, 300);
				g.drawLine(0, 150, 300, 150);
				int c = -1;
				for(int a = 0; a < 30; a++) {
					int y = (int) (signedWeightedRandom(2) / getCat(a * width / 300) * 150 + 150);
					g.drawRect(10 * a - 1, y - 1, 3, 3);
					if(c != -1)
						g.drawLine(10 * (a - 1), c, 10 * a, y);
					c = y;
				}
			}
		};
		comp.setPreferredSize(new Dimension(300, 300));
		frame.add(comp);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
