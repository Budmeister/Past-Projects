package generators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class BankMoneyPileImageGenerator2 implements MouseListener{

	public static void main(String[] args) throws IOException {
		BankMoneyPileImageGenerator2 bmpig = new BankMoneyPileImageGenerator2();
		bmpig.generate();
//		testRandom();
	}
	
	BufferedImage img;
	BufferedImage one, fiv, ten;
	Graphics2D g;
	File f;
	int w;
	
	static int width = 550, height = 250;
	int numCoins = 200;
	void generate() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		int choice = fileChooser.showOpenDialog(new JFrame());
		
		if(choice != JFileChooser.APPROVE_OPTION)
			return;
		f = fileChooser.getSelectedFile();
		
		Class<BankMoneyPileImageGenerator> clazz = BankMoneyPileImageGenerator.class;
		one = ImageIO.read(clazz.getResource("/images/cover/money-1.png"));
		fiv = ImageIO.read(clazz.getResource("/images/cover/money-5.png"));
		ten = ImageIO.read(clazz.getResource("/images/cover/money-10.png"));
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		g = (Graphics2D) img.getGraphics();
		
		
		JFrame frame = new JFrame();
		@SuppressWarnings("serial")
		JComponent comp = new JComponent() {
			public void paintComponent(Graphics g) {
				g.drawImage(img, 0, 0, null);
			}
		};
		comp.setPreferredSize(new Dimension(width, height));
		frame.add(comp);
		Timer t = new Timer(50, ae -> comp.repaint());
		t.start();
		frame.addMouseListener(this);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	@Deprecated
	void rotateRandom(Graphics2D g, int x, int y) {
		double t = 2 * Math.PI * Math.random();
		double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		g.translate(x, y);
		g.rotate(t);
		g.translate(r * Math.cos(Math.PI - t), r * Math.sin(Math.PI - t));
	}
	
	void paintRotatedRandom(BufferedImage img, Graphics2D g, int x, int y, int w) {
		double t = 2 * Math.PI * Math.random();
		g.translate(x, y);
		g.rotate(t);
		g.drawImage(img, -w / 2, -w / 2, w, w, null);
		g.rotate(-t);
		g.translate(-x, -y);
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
	
	BufferedImage randomCoin() {
		double r = weightedRandom(2);
		if(r > 2.0 / 3) {
			w = 75;
			return ten;
		}else if(r > 1.0 / 3) {
			w = 50;
			return fiv;
		}else {
			w = 25;
			return one;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 3)
			try {
				ImageIO.write(img, "png", f);
				System.exit(0);
			}catch (IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		int x = e.getX() - 8, y = e.getY() - 31;
		paintRotatedRandom(randomCoin(), g, x, y, w);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

}
