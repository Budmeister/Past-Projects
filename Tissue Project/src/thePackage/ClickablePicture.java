package thePackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ClickablePicture implements Paintable, Clickable{
	
	private BufferedImage image;
	private int x, y, w, h;
	private boolean mouseOver;
	private Scene nextScene;
	private String name;
	private String caption;
	private boolean noBorder = false;
	
	public ClickablePicture() {
		if(getImage() == null)
			setImage(TissueProject.getImage("greysquare.png"));
	}
	
	public ClickablePicture(int x, int y, int w, int h, String image) {
		setX(x); setY(y); setW(w); setH(h);
		setImage(TissueProject.getImage(image));
		setMouseOver(false);
		if(getImage() == null)
			setImage(TissueProject.getImage("greysquare.png"));
	}

	@Override
	public void click(int x, int y) {
		if(getNextScene() != null && isMouseOver())
			TissueProject.tp.setCurrentScene(getNextScene());
	}
	
	public void moveCursor(int x, int y) {
		setMouseOver(x > getX() && x < getX() + getW() && y > getY() && y < getY() + getH());
		if(isMouseOver())
			TissueProject.tp.setcurrentCaption(getCaption());
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(getImage(), getX(), getY(), getW(), getH(), null);
		if(isMouseOver() && !isNoBorder()) {
			g.setColor(Color.blue);
			for(int a = 0; a < 5; a++)
				g.drawRect(getX() - a, getY() - a, getW() + 2 * a, getH() + 2 * a);
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Scene getNextScene() {
		return nextScene;
	}

	public void setNextScene(Scene nextScene) {
		this.nextScene = nextScene;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public String toString() {
		return "Picture: " + name;
	}

	public boolean isNoBorder() {
		return noBorder;
	}

	public void setNoBorder(boolean noBorder) {
		this.noBorder = noBorder;
	}
	
	
}
