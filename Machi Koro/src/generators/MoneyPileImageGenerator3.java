package generators;

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

public class MoneyPileImageGenerator3 extends JComponent implements MouseListener{

	public static void main(String[] args) {
		MoneyPileImageGenerator3 mpig = new MoneyPileImageGenerator3();
		mpig.generate();
	}
	
	int width = 150, height = 150;
	BufferedImage one, five, ten;
	Image o, f, t;
	final int w1 = 25, w5 = 50, w10 = 75;
	JFileChooser fileChooser;
	BufferedImage img;
	String dir;
	int imageNum;
	
	void generate(){
		fileChooser = new JFileChooser();
		int choice = fileChooser.showOpenDialog(new JFrame("Where?"));
		
		if(choice != JFileChooser.APPROVE_OPTION)
			System.exit(0);
		dir = fileChooser.getSelectedFile().getAbsolutePath();
		dir = dir.substring(0, dir.lastIndexOf("\\"));
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
		
		JFrame frame = new JFrame();
		setPreferredSize(new Dimension(width, height));
		frame.add(this);
		frame.addMouseListener(this);
		img = makeImage(currentValue);
		
		new Timer(50, ae -> repaint()).start();
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	int maxValue;
	int currentValue = 1;
	BufferedImage makeImage(int value) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		
		int sum = 0;
		do {
			Image i;
			do{
				i = getRandomCoin();
			}while(type(i) + sum > value);
			int x = (int) ((width - i.width) * Math.random()), y = (int) ((height - i.width) * Math.random());
			g.drawImage(i.image, x, y, i.width, i.width, null);
			sum+=type(i);
		}while(sum < value);
		System.out.println(value + "," + sum);
		return img;
	}
	
	int type(Image i) {
		switch(i.width) {
		case w1:
			return 1;
		case w5:
			return 5;
		case w10:
			return 10;
		default:
			return -1;
		}
	}
	
	void writeImage(BufferedImage img) {
		File f = new File(dir + "\\moneypile" + imageNum++ + ".png");
		try {
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("IOException on image write.");
			e.printStackTrace();
			System.exit(1);
		}
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
	
	int left = -1, right = -1;
	int fadeSpeed = 30;
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.drawImage(img, 0, 0, null);
		if(left != -1) {
			if(left <= 255) {
				g.setColor(new Color(255, left, left, left));
				g.fillRect(0, 0, width / 2, height);
				left+=fadeSpeed;
			}else
				left = -1;
		}
		if(right != -1) {
			if(right <= 255) {
				g.setColor(new Color(right, 255, right, right));
				g.fillRect(width / 2, 0, width / 2, height);
				right+=fadeSpeed;
			}else
				right = -1;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX() - 8;
		if(x < width / 2) {
			left = 0;
			img = makeImage(currentValue);
		}else {
			right = 0;
			new Thread(() -> {writeImage(img); img = makeImage(++currentValue);}).start();
		}
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