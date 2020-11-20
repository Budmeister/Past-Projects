package generators;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageReader2 extends JComponent{
	
	JFileChooser fileChooser;
	JFrame mainFrame;
	BufferedImage img;

	public static void main(String[] args) {
		ImageReader2 ir = new ImageReader2();
		ir.start();
	}
	
	static int initialX = 11, initialY = 7;
	static BufferedImage[] cards = new BufferedImage[15];
	int w = 364, h = 364;
	int dx = 3142, dy = 2314;
	int offset[] = {0, 2, 2};
	
	void start() {
		fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(new JFrame("Image"));
		if(result != JFileChooser.APPROVE_OPTION)
			return;
		File file = fileChooser.getSelectedFile();
		try {
			img = ImageIO.read(file);
			for(int x = 0; x < 2; x++)
				for(int y = 0; y < 5; y++)
					greyOut(img, dx * x + initialX, dy * y + initialY);
		}catch(IOException e) {}
	}
	
	void greyOut(BufferedImage i, int x, int y) {
		byte[] pixels = null;
		int pixel = 0, row, col;
		BufferedImage subimage = i.getSubimage(x, y, w, h);
		try {
			Graphics g = subimage.getGraphics();
			AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
			Graphics2D g2d = (Graphics2D) subimage.getGraphics();
			g2d.setComposite(composite);
			pixels = ((DataBufferByte) subimage.getRaster().getDataBuffer()).getData();
			final int width = subimage.getWidth();
			final int height = subimage.getHeight();
			final boolean hasAlphaChannel = subimage.getAlphaRaster() != null;
			int pixelLength;
			if(hasAlphaChannel)
				pixelLength = 4;
			else
				pixelLength = 3;
			g.setColor(new Color(225, 225, 0, 255));
			for(pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel+=pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff); // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				int alpha = ((int) pixels[pixel] & 0xff);
				int blue = ((int) pixels[pixel + 1] & 0xff);
				int green = ((int) pixels[pixel + 2] & 0xff);
				int red = ((int) pixels[pixel + 3] & 0xff);
				if(red == 99 && green == 202 && green == 225) {
					g2d.drawRect(col, row, 1, 1);
//					img.setRGB(col, row, argb);
				}
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
//			String path = file.getAbsolutePath();
//			File file2 = new File(path.substring(0, path.lastIndexOf('\\')) + "\\" + file.getName());
//			ImageIO.write(i, "png", file2);
			setPreferredSize(new Dimension(width, height));
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println(pixels.length);
			System.out.println(pixel);
		}
		mainFrame = new JFrame("Image");
		mainFrame.add(this);
		
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
	}
	
	int[][] convertTo2D(BufferedImage image) {
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;
		
		int[][] result = new int[height][width];
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff); // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += -16777216; // 255 alpha
				argb += ((int) pixels[pixel] & 0xff); // blue
				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}
		return result;
	}

}
