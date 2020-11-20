package generators;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageBreakerUpper2 extends JComponent{
	
	JFileChooser fileChooser;
	JFrame mainFrame;
	BufferedImage img;
	BufferedImage subimage;
	Column[] columns = {new Column(0, 0, 50)};
	Row[] rows = {new Row(11, 18, 566)};
	int numPer = 2;
	int w = 1000, h = 50;

	public static void main(String[] args) {
		ImageBreakerUpper2 ibu = new ImageBreakerUpper2();
		ibu.start(args);
		System.exit(0);
	}
	
	void start(String[] args) {
		File file;
		if(args.length == 0) {
			fileChooser = new JFileChooser();
			int result = fileChooser.showOpenDialog(new JFrame("Image"));
			if(result != JFileChooser.APPROVE_OPTION)
				return;
			file = fileChooser.getSelectedFile();
		}else {
			file = new File(args[0]);
		}
		for(int c = 0; c < columns.length; c++) {
			Column column = columns[c];
			for(int r = 0; r < numPer; r++) {
				begin(file, column.getX(), column.getY(r), w, h, c + "," + r + ".png");
			}
		}
//		for(int r = 0; r < rows.length; r++) {
//			Row row = rows[r];
//			for(int c = 0; c < 4; c++) {
//				begin(file, row.getX(c), row.getY(), w, h, c + "," + r + ".png");
//			}
//		}
	}
	
	void begin(File f, int x, int y, int w, int h, String name) {
		try {
			img = ImageIO.read(f);
			subimage = img.getSubimage(x, y, w, h);
			String path = f.getAbsolutePath();
			File file2 = new File(path.substring(0, path.lastIndexOf('\\')) + "\\" + name);
			ImageIO.write(subimage, "png", file2);
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(subimage, 0, 0, subimage.getWidth(), subimage.getHeight(), null);
	}

}

class Column{
	private int x, initialY, dy;
	
	Column(int x, int initialY, int dy){
		setX(x); setInitialY(initialY); setDy(dy);
	}
	
	public int getY(int num) {
		return initialY + num * dy;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int initialX) {
		this.x = initialX;
	}

	/**
	 * @return the initialY
	 */
	public int getInitialY() {
		return initialY;
	}

	/**
	 * @param initialY the initialY to set
	 */
	public void setInitialY(int initialY) {
		this.initialY = initialY;
	}

	/**
	 * @return the dy
	 */
	public int getDy() {
		return dy;
	}

	/**
	 * @param dy the dy to set
	 */
	public void setDy(int dy) {
		this.dy = dy;
	}
}

class Row {
	private int y, initialX, dx;
	
	Row(int initialX, int y, int dy){
		setY(y); setInitialX(initialX); setDx(dy);
	}
	
	public int getX(int num) {
		return initialX + num * dx;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int initialY) {
		this.y = initialY;
	}

	/**
	 * @return the initialX
	 */
	public int getInitialX() {
		return initialX;
	}

	/**
	 * @param initialX the initialX to set
	 */
	public void setInitialX(int initialY) {
		this.initialX = initialY;
	}

	/**
	 * @return the dx
	 */
	public int getDx() {
		return dx;
	}

	/**
	 * @param dx the dx to set
	 */
	public void setDx(int dx) {
		this.dx = dx;
	}
	
}
