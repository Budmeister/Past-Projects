package thePackage;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class MachiKoroScene implements Paintable, Pressable, Typable, ImageUpdatable{
	protected ArrayList<Paintable> painters;
	private Color background = null;
	private int width, height;
	private int translateX, translateY;
	
	public MachiKoroScene() {
		painters = new ArrayList<>();
		setTranslateX(0); setTranslateY(0);
	}
	
	public void addPainter(Paintable p) {
		painters.add(p);
	}
	
	public boolean hasPainter() {
		return !painters.isEmpty();
	}
	
	public int size() {
		return painters.size();
	}
	
	public boolean reset() {
		if(painters.isEmpty())
			return false;
		else
			painters = new ArrayList<>();
		return true;
	}

	private int velX, velY;
	private boolean justArrived = false;
	@Override
	public void paintIt(Graphics g) {
		setJustArrived(false);
		Graphics gc;
		if(getTranslateX() != 0 || getTranslateY() != 0)
			gc = g.create();
		else
			gc = g;
		
		if(velX != 0 || velY != 0) {
			if(theseAreClose(0, getTranslateX(), Math.abs(velX)) && theseAreClose(0, getTranslateY(), Math.abs(velY))) {
				setTranslateX(0);
				setTranslateY(0);
				velX = 0;
				velY = 0;
				setJustArrived(true);
			} else {
				setTranslateX(getTranslateX() + velX); setTranslateY(getTranslateY() + velY);
				gc.translate(translateX, translateY);
			}
		}
		if(background != null) {
			gc.setColor(background);
			gc.fillRect(0, 0, width, height);
		}
		int ja = -1;
		for(int a = 0; a < painters.size(); ) {
			Paintable p = painters.get(a);
			p.paintIt(gc);
			if(p.shouldRemove())
				painters.remove(p);
			else if(p instanceof MachiKoroScene && ((MachiKoroScene) p).isJustArrived())
					ja = a++;
			else
				a++;
		}
		if(ja != -1) {
			MachiKoroScene s = (MachiKoroScene) painters.get(ja);
			set(s);
		}
		
		gc = null;
	}
	
	public void add(MachiKoroScene s) {
		for(Paintable p : s.painters)
			addPainter(p);
	}
	
	public void set(MachiKoroScene s) {
		reset();
		add(s);
		setBackground(s.getBackground());
		setWidth(s.getWidth());
		setHeight(s.getHeight());
		setTranslateX(s.getTranslateX());
		setTranslateY(s.getTranslateY());
	}
	
	private boolean theseAreClose(int num1, int num2, int range) {
		return num1 >= num2 - range && num1 <= num2 + range;
	}

	@Override
	public boolean shouldRemove() {
		for(Paintable p : painters)
			if(!p.shouldRemove())
				return false;
		return true;
	}

	/**
	 * @return the background
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(Color background) {
		this.background = background;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void updateImage(int mouseX, int mouseY) {
		for(Paintable p : painters)
			if(p instanceof ImageUpdatable)
				((ImageUpdatable) p).updateImage(mouseX + getTranslateX(), mouseY + getTranslateY());
	}
	
	public void press(int mouseX, int mouseY) {
		for(int a = 0; a < painters.size(); a++) {
			Paintable p = painters.get(a);
			if(p instanceof Pressable)
				((Pressable) p).press(mouseX + getTranslateX(), mouseY + getTranslateY());
		}
	}
	
	public void release(int mouseX, int mouseY) {
		for(int a = 0; a < painters.size(); a++) {
			Paintable p = painters.get(a);
			if(p instanceof Pressable)
				((Pressable) p).release(mouseX + getTranslateX(), mouseY + getTranslateY());
		}
	}

	@Override
	public void type(int key) {
		for(int a = 0; a < painters.size(); a++) {
			Paintable p = painters.get(a);
			if(p instanceof Typable)
				((Typable) p).type(key);
		}
	}

	@Override
	public void releaseKey(int key) {
		for(int a = 0; a < painters.size(); a++) {
			Paintable p = painters.get(a);
			if(p instanceof Typable)
				((Typable) p).releaseKey(key);
		}
	}
	
	public String toString() {
		return painters.toString();
	}

	/**
	 * @return the translateX
	 */
	public int getTranslateX() {
		return translateX;
	}

	/**
	 * @param translateX the translateX to set
	 */
	public void setTranslateX(int translateX) {
		this.translateX = translateX;
	}

	/**
	 * @return the translateY
	 */
	public int getTranslateY() {
		return translateY;
	}

	/**
	 * @param translateY the translateY to set
	 */
	public void setTranslateY(int translateY) {
		this.translateY = translateY;
	}
	
	public void slideInFromLeft(int speed) {
		setTranslateX(-width);
		setTranslateY(0);
		velX = speed;
		velY = 0;
	}
	
	public void slideInFromRight(int speed) {
		setTranslateX(width);
		setTranslateY(0);
		velX = -speed;
		velY = 0;
	}
	
	public void slideInFromTop(int speed) {
		setTranslateX(0);
		setTranslateY(-height);
		velX = 0;
		velY = speed;
	}
	
	public void slideInFromBottom(int speed) {
		setTranslateX(0);
		setTranslateY(height);
		velX = 0;
		velY = -speed;
	}
	
	private double r2o2 = 1.0 / Math.sqrt(2);
	
	@Deprecated
	public void slideInFromTopLeft(int speed) {
		setTranslateX(-width);
		setTranslateY(-height);
		velX = (int) (speed * r2o2);
		velY = (int) (speed * r2o2);
	}
	
	@Deprecated
	public void slideInFromTopRight(int speed) {
		setTranslateX(width);
		setTranslateY(-height);
		velX = (int) (-speed * r2o2);
		velY = (int) (speed * r2o2);
	}
	
	@Deprecated
	public void slideInFromBottomLeft(int speed) {
		setTranslateX(-width);
		setTranslateY(height);
		velX = (int) (speed * r2o2);
		velY = (int) (-speed * r2o2);
	}
	
	@Deprecated
	public void slideInFromBottomRight(int speed) {
		setTranslateX(width);
		setTranslateY(height);
		velX = (int) (-speed * r2o2);
		velY = (int) (-speed * r2o2);
	}

	/**
	 * @return the justArrived
	 */
	public boolean isJustArrived() {
		return justArrived;
	}

	/**
	 * @param justArrived the justArrived to set
	 */
	public void setJustArrived(boolean justArrived) {
		this.justArrived = justArrived;
	}

}
