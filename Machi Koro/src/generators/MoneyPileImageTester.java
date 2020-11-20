package generators;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import thePackage.FrameHandler;

public class MoneyPileImageTester extends JComponent implements MouseListener{

	public static void main(String[] args) {
		MoneyPileImageTester mpit = new MoneyPileImageTester();
		mpit.start();
	}
	
	File smallFolder, largeFolder;
	BufferedImage[] smallImages, largeImages;
	JLabel smallLabel, largeLabel;
	
	void start() {
//		JFileChooser fileChooser1 = new JFileChooser();
//		int choice = fileChooser1.showOpenDialog(new JFrame("Small"));
//		if(choice != JFileChooser.APPROVE_OPTION)
//			System.exit(0);
//		
//		JFileChooser fileChooser2 = new JFileChooser();
//		choice = fileChooser2.showOpenDialog(new JFrame("Large"));
//		if(choice != JFileChooser.APPROVE_OPTION)
//			System.exit(0);
//		
//		smallFolder = fileChooser1.getSelectedFile();
//		largeFolder = fileChooser2.getSelectedFile();
		
		back = FrameHandler.background1;
		back2 = new Color(back.getRed(), back.getGreen(), back.getBlue(), 100);
		
		smallFolder = new File("C:\\Users\\josia\\Documents\\images\\gameplay\\moneypiles150x150");
		largeFolder = new File("C:\\Users\\josia\\Documents\\images\\gameplay\\moneypiles100x100");
		
		generateImages();
		
		smallLabel = new JLabel();
		largeLabel = new JLabel();
		
		small = getSmallImage();
		large = getLargeImage();
		
		setPreferredSize(new Dimension(width, height));
		new Timer(50, ae -> repaint()).start();
		
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.NORTH);
		frame.addMouseListener(this);
		
		frame.add(smallLabel, BorderLayout.CENTER);
		frame.add(largeLabel, BorderLayout.SOUTH);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	int width = 150, height = 150;
	BufferedImage small, large;
	int flash = -1;
	int fadeSpeed = 30;
	Color back;
	Color back2;
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(back);
		g.fillRect(0, 0, width, height);
		g.drawImage(large, (width - large.getWidth()) / 2, (height - large.getHeight()) / 2, null);
		g.setColor(back2);
//		g.fillRect((width - 100) / 2, (height - 100) / 2, 100, 100);
		g.drawImage(small, (width - small.getWidth()) / 2, (height - small.getHeight()) / 2, null);
		
		if(flash != -1) {
			if(flash <= 255) {
				g.setColor(new Color(flash, 255, flash, flash));
				g.fillRect(0, 0, width, height);
				flash+=fadeSpeed;
			}else {
				flash = -1;
				large = getLargeImage();
				small = getSmallImage();
			}
		}
	}
	
	void generateImages() {
		try {
			File[] smallFiles = smallFolder.listFiles();
			smallImages = new BufferedImage[smallFiles.length];
			for(int a = 0; a < smallFiles.length; a++)
				smallImages[a] = ImageIO.read(smallFiles[a]);
			
			File[] largeFiles = largeFolder.listFiles();
			largeImages = new BufferedImage[largeFiles.length];
			for(int a = 0; a < largeFiles.length; a++)
				largeImages[a] = ImageIO.read(largeFiles[a]);
		} catch (IOException e) {
			System.out.println("IOException on generating images");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	BufferedImage getSmallImage() {
		int num = (int) (smallImages.length * Math.random());
		smallLabel.setText("Small: " + num);
		return smallImages[num];
	}
	
	BufferedImage getLargeImage() {
		int num = (int) (largeImages.length * Math.random());
		largeLabel.setText("Large: " + num);
		return largeImages[num];
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		flash = 0;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}
	
	

}
