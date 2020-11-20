package generators;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MoneyPileImageGenerator {

	public static void main(String[] args) {
		MoneyPileImageGenerator mpig = new MoneyPileImageGenerator();
		mpig.generate();
	}
	
	int width = 100, height = 100;
	int numCoins = 7;
	BufferedImage one, five, ten;
	Image o, f, t;
	int w1 = 25, w5 = 50, w10 = 75;
	
	void generate(){
		JFileChooser fileChooser = new JFileChooser();
		int choice = fileChooser.showOpenDialog(new JFrame("Where?"));
		
		if(choice != JFileChooser.APPROVE_OPTION)
			return;
		try {
			one = ImageIO.read(MoneyPileImageGenerator.class.getResource("/images/cover/money-1.png"));
			five = ImageIO.read(MoneyPileImageGenerator.class.getResource("/images/cover/money-5.png"));
			ten = ImageIO.read(MoneyPileImageGenerator.class.getResource("/images/cover/money-10.png"));
			o = new Image(one, w1);
			f = new Image(five, w5);
			t = new Image(ten, w10);
		} catch (IOException e) {
			System.out.println("IOException on image creation.");
			System.exit(1);
		}
		
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		
		for(int num = 0; num < numCoins; num++) {
			Image i = getRandomCoin();
			int x = (int) ((width - i.width) * Math.random()), y = (int) ((height - i.width) * Math.random());
			g.drawImage(i.image, x, y, i.width, i.width, null);
		}
		
		File f = fileChooser.getSelectedFile();
		try {
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("IOException on image write.");
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
	
	Image getRandomCoin() {
		double r = Math.random() * 3;
		if(((int) r) == 0)
			return t;
		if(((int) r) == 1)
			return f;
		if(((int) r) == 2)
			return o;
		return null;
	}

}

class Image {
	final BufferedImage image;
	final int width;
	
	Image(BufferedImage i, int w){
		image = i;
		width = w;
	}
}