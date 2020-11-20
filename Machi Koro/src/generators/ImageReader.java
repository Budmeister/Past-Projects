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

public class ImageReader extends JComponent{
	
	JFileChooser fileChooser;
	JFrame mainFrame;
	BufferedImage img;

	public static void main(String[] args) {
		ImageReader ir = new ImageReader();
		ir.start();
		System.exit(0);
	}
	
	static int[][] coords;
	static BufferedImage[] cards = new BufferedImage[15];
	
	void start() {
		fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(new JFrame("Image"));
		if(result != JFileChooser.APPROVE_OPTION)
			return;
		File file = fileChooser.getSelectedFile();
		byte[] pixels = null;
		int pixel = 0, row, col;
		try {
			img = ImageIO.read(file);
			Graphics g = img.getGraphics();
			AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
			Graphics2D g2d = (Graphics2D) img.getGraphics();
			g2d.setComposite(composite);
			pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			final int width = img.getWidth();
			final int height = img.getHeight();
			final boolean hasAlphaChannel = img.getAlphaRaster() != null;
			int pixelLength;
			if(hasAlphaChannel)
				pixelLength = 4;
			else
				pixelLength = 3;
			JFrame frame = new JFrame("Loading");
			JProgressBar progressBar = new JProgressBar(0, pixels.length);
			frame.add(progressBar); frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); frame.pack(); frame.setLocationRelativeTo(null); frame.setVisible(true);
			g.setColor(new Color(225, 225, 0, 255));
			for(pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel+=pixelLength) {
				progressBar.setValue(pixel);
				int argb = 0;
				int alpha, blue, green, red;
				if(pixelLength == 4) {
					argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
					argb += ((int) pixels[pixel + 1] & 0xff); // blue
					argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
					argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
					alpha = ((int) pixels[pixel] & 0xff);
					blue = ((int) pixels[pixel + 1] & 0xff);
					green = ((int) pixels[pixel + 2] & 0xff);
					red = ((int) pixels[pixel + 3] & 0xff);
				}else {
//					argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
					argb += ((int) pixels[pixel] & 0xff); // blue
					argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
					argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
//					int alpha = ((int) pixels[pixel] & 0xff);
					blue = ((int) pixels[pixel] & 0xff);
					green = ((int) pixels[pixel + 1] & 0xff);
					red = ((int) pixels[pixel + 2] & 0xff);
				}
				if(red == 99 && green == 202 && blue == 225) {
					g2d.drawRect(col, row, 1, 1);
//					img.setRGB(col, row, argb);
				}
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
			String path = file.getAbsolutePath();
			File file2 = new File(path.substring(0, path.lastIndexOf('\\')) + "\\" + file.getName());
			ImageIO.write(img, "png", file2);
			frame.dispose();
			setPreferredSize(new Dimension(width, height));
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println(pixels.length);
			System.out.println(pixel);
		} catch (IOException e) {
			e.printStackTrace();
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
