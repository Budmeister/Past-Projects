package thePackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

public class TissueProject extends JComponent implements MouseMotionListener, MouseListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 335529374460665149L;
	private static HashMap<String, BufferedImage> images;
	private Scene currentScene;
	private JFrame mainFrame;
	private String currentCaption;
	
	public static final int width = 900, height = 600;
	public static TissueProject tp;

	public static void main(String[] args) {
		tp = new TissueProject();
		tp.start();
	}
	
	public void start() {
		try {
			loadImages();
		} catch (IOException e) {
			JFrame errorFrame = new JFrame("Error");
			errorFrame.setLayout(new BorderLayout());
			errorFrame.add(new JLabel("IOException loading images. Press ok to exit."), BorderLayout.NORTH);
			JButton ok = new JButton("Ok");
			ok.addActionListener(ae -> System.exit(0));
			errorFrame.add(ok, BorderLayout.SOUTH);
			
			errorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			errorFrame.pack();
			errorFrame.setLocationRelativeTo(null);
			errorFrame.setVisible(true);
			
			while(true);
		}
		
		try {
			currentScene = loadScenes();
		} catch (IOException e) {
			JFrame errorFrame = new JFrame("Error");
			errorFrame.setLayout(new BorderLayout());
			errorFrame.add(new JLabel("IOException loading scenes. Press ok to exit."), BorderLayout.NORTH);
			JButton ok = new JButton("Ok");
			ok.addActionListener(ae -> System.exit(0));
			errorFrame.add(ok, BorderLayout.SOUTH);
			
			errorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			errorFrame.pack();
			errorFrame.setLocationRelativeTo(null);
			errorFrame.setVisible(true);
			
			while(true);
		}
//		currentScene = new Scene(null);
//		currentScene.addImage(new ClickablePicture(300, 100, 200, 200, "epithelialtissue.jpg"));
		setPreferredSize(new Dimension(width, height));
		
		mainFrame = new JFrame("Tissues!");
		mainFrame.add(this);
		mainFrame.addMouseListener(this);
		mainFrame.addMouseMotionListener(this);
		
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		Timer t = new Timer(100, ae -> repaint());
		t.start();
		
	}
	
	public static BufferedImage getImage(String name) {
		return images.get(name);
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(new Color(100, 100, 255));
		g.fillRect(0, 0, width, height);
		g.setColor(Color.black);
		g.fillRect(50, 500, 800, 50);
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		paintCenteredString(getCurrentCaption(), width / 2, height - 67, g);
		if(currentScene != null)
			currentScene.paint(g);
	}
	
	public void setcurrentCaption(String currentCaption) {
		this.currentCaption = currentCaption;
	}
	
	public String getCurrentCaption() {
		return currentCaption;
	}
	
	private void paintCenteredString(String str, int x, int y, Graphics g) {
		if(str == null)
			return;
		int w = g.getFontMetrics().stringWidth(str);
		g.drawString(str, x - w / 2, y);
	}
	
	private void loadImages() throws IOException {
		InputStream names = TissueProject.class.getResourceAsStream("/imagenames.txt");
		Scanner s = new Scanner(names);
		images = new HashMap<>();
		String ln;
		while(s.hasNext())
			images.put((ln = s.next()), ImageIO.read(TissueProject.class.getResource("/images/" + ln)));
		s.close();
	}
	
	private Scene loadScenes() throws IOException {
		Scanner s = new Scanner(TissueProject.class.getResourceAsStream("/scenes.txt"));
		HashMap<String, Scene> scenes = new HashMap<>();
		HashMap<String, ClickablePicture> pictures = new HashMap<>();
		
		while(s.hasNext()) {
			String ln = s.nextLine();
			switch(ln) {
			case "[scene]":
				Scene scene = new Scene(null);
				ln = s.nextLine();
				while(!ln.equals("[end]")) {
					String paramType = ln.substring(0, ln.indexOf(":"));
					String param = ln.substring(ln.indexOf(':') + 1);
					switch(paramType) {
					case "name":
						scene.setName(param);
						break;
					case "previous":
						scene.setPrevScene(scenes.get(param));
					}
					ln = s.nextLine();
				}
				scenes.put(scene.getName(), scene);
				System.out.println(scene);
				break;
			case "[picture]":
				ClickablePicture cp = new ClickablePicture();
				ln = s.nextLine();
				while(!ln.equals("[end]")) {
					String paramType = ln.substring(0, ln.indexOf(':'));
					String param = ln.substring(ln.indexOf(':') + 1);
					switch(paramType) {
					case "name":
						cp.setName(param);
						break;
					case "super":
						scenes.get(param).addImage(cp);
						break;
					case "image":
						cp.setImage(getImage(param));
						break;
					case "sub":
						cp.setNextScene(scenes.get(param));
						break;
					case "caption":
						cp.setCaption(param);
						break;
					case "noborder":
						cp.setNoBorder(Boolean.parseBoolean(param));
						break;
					case "x":
						cp.setX(Integer.parseInt(param));
						break;
					case "y":
						cp.setY(Integer.parseInt(param));
						break;
					case "w":
						cp.setW(Integer.parseInt(param));
						break;
					case "h":
						cp.setH(Integer.parseInt(param));
					}
					ln = s.nextLine();
				}
				pictures.put(cp.getName(), cp);
				System.out.println(cp);
			}
		}
//		System.out.println(scenes);
//		System.out.println(pictures);
		s.close();
		return scenes.get("primaryScene");
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

	@Override
	public void mouseReleased(MouseEvent e) {
		if(currentScene != null)
			currentScene.click(getX(e), getY(e));
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(currentScene != null)
			currentScene.moveCursor(getX(e), getY(e));
	}
	
	private int getX(MouseEvent e) {
		return e.getX() - 8;
	}
	
	private int getY(MouseEvent e) {
		return e.getY() - 31;
	}

	public Scene getCurrentScene() {
		return currentScene;
	}

	public void setCurrentScene(Scene currentScene) {
		this.currentScene = currentScene;
		setcurrentCaption(null);
	}

}
