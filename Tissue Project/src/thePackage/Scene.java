package thePackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class Scene implements Paintable, Clickable{
	
	private ArrayList<Paintable> images;
	private Scene prevScene;
	private BackButton backButton;
	private String name;
	
	public Scene(Scene prevScene) {
		images = new ArrayList<>();
		this.prevScene = prevScene;
		backButton = new BackButton(prevScene);
	}
	
	public void addImage(Paintable i) {
		images.add(i);
	}

	@Override
	public void click(int x, int y) {
		System.out.println("Clicked! " + prevScene);
		if(prevScene != null)
			backButton.click(x, y);
		for(Paintable i : images)
			if(i instanceof Clickable)
				((Clickable) i).click(x, y);
	}
	
	public void moveCursor(int x, int y) {
		TissueProject.tp.setcurrentCaption(null);
		if(prevScene != null)
			backButton.moveCursor(x, y);
		for(Paintable i: images)
			if(i instanceof Clickable)
				((Clickable) i).moveCursor(x, y);
	}

	@Override
	public void paint(Graphics g) {
		if(prevScene != null)
			backButton.paint(g);
		for(Paintable i : images)
			i.paint(g);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setPrevScene(Scene p) {
		prevScene = p;
		backButton.prevScene = p;
	}
	
	public Scene getPrevScene() {
		return prevScene;
	}
	
	public String toString() {
		return "Scene: " + name;
	}
	
}

class BackButton implements Paintable, Clickable{
	
	Scene prevScene;
	private boolean mouseOver;
	private static int x = 50, y = 10, w = 100, h = 50;
	private static Font font = new Font("Times New Roman", Font.PLAIN, 20);
	
	BackButton(Scene prevScene) {
		this.prevScene = prevScene;
	}

	@Override
	public void click(int x, int y) {
		if(isMouseOver()) 
			TissueProject.tp.setCurrentScene(prevScene);
	}
	
	public void moveCursor(int x, int y) {
		setMouseOver(x > BackButton.x && y > BackButton.y && x < BackButton.x + w && y < BackButton.y + h);
	}
	
	public void paint(Graphics g) {
		if(isMouseOver()) {
			g.setColor(new Color(200, 200, 200, 150));
			g.fillOval(x, y, w, h);
		}
		g.setColor(Color.black);
		g.setFont(font);
		g.drawString("Back", x + w / 2 - g.getFontMetrics().stringWidth("Back") / 2, y + h / 2 + g.getFontMetrics().getAscent() / 2);
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}
	
}