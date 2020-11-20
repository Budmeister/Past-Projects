package thePackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class MachiKoroButton implements Paintable, Pressable{
	
	private int x, y, width, height;
	private BufferedImage image;
	protected ArrayList<ActionListener> actionListeners;
	
	protected MachiKoroButton(int x, int y, int width, int height){
		setX(x); setY(y); setWidth(width); setHeight(height);
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
	public void setX(int x) {
		this.x = x;
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
	public void setY(int y) {
		this.y = y;
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

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public boolean mouseIsIn(int x, int y) {
//		if(GlobalVariables.DEBUG)
//			return x > getX() * FrameHandler.debugScaler &&
//					x < (getX() + getWidth()) * FrameHandler.debugScaler && 
//					y > getY() * FrameHandler.debugScaler &&
//					y < (getY() + getHeight()) * FrameHandler.debugScaler;
//		else
			return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + getHeight();
	}
	
	public abstract void press(int mouseX, int mouseY);
	
	public void release(int mouseX, int mouseY) {
		for(ActionListener al : actionListeners)
			al.actionPerformed(new ActionEvent(this, 0, "Button Clicked"));
	}
	
	public void addActionListener(ActionListener ae) {
		actionListeners.add(ae);
	}
	
	public void removeActionListener(int index) {
		actionListeners.remove(index);
	}
	
	public boolean hasActionListener() {
		return !actionListeners.isEmpty();
	}
	
}

interface ImageUpdatable{
	void updateImage(int mouseX, int mouseY);
}

class RoundButton extends MachiKoroButton implements ImageUpdatable{
	private BufferedImage unclicked = null;
	private BufferedImage clicked = null;
	private String type;
	private String color;
	private String text;
	private Font font;
	private int fontSize;
	private Color textColor;
	private boolean visible;
	
	private int sw, sh, LW, LH;
	private int sx, sy, LX, LY;

	RoundButton(String color, String type, int x, int y, int width, int height) {
		super(x, y, width, height);
		sw = width; sh = height; LW = (int) (sw * 1.1); LH = (int) (sh * 1.1);
		sx = x; sy = y; LX = (int) (sx - .05 * sw); LY = (int) (sy - .05 * sh);
		actionListeners = new ArrayList<>();
		setColor(color); setFontSize(20); setFont(FrameHandler.getDefaultFont(getFontSize())); setTextColor(Color.black); setType(type); setVisible(true);
		makeImages();
	}
	
	private void makeImages() {
		unclicked = CardImages.getImage("button_" + getType() + "_" + getColor() + "_unclicked.png");
		clicked   = CardImages.getImage("button_" + getType() + "_" + getColor()  +  "_clicked.png");
		setImage(unclicked);
	}

	@Override
	public void paintIt(Graphics g) {
		if(!visible)
			return;
		g.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), null);
		if(text != null) {
			int sw = g.getFontMetrics(getFont()).stringWidth(getText());
			int sh = g.getFontMetrics(getFont()).getHeight();
			int asc = g.getFontMetrics(getFont()).getAscent();
			g.setFont(getFont());
			g.setColor(getTextColor());
			g.drawString(getText(), getX() + getWidth() / 2 - sw / 2, getY() + getHeight() / 2 - sh / 2 + asc);
		}
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}
	
	@Override
	public void updateImage(int mouseX, int mouseY) {
		if(!visible)
			return;
		if(mouseIsIn(mouseX, mouseY)) {
			setX(LX); setY(LY); setWidth(LW); setHeight(LH);
		}else {
			setX(sx); setY(sy); setWidth(sw); setHeight(sh);
		}
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		setFont(FrameHandler.getDefaultFont(fontSize));
	}

	/**
	 * @return the textColor
	 */
	public Color getTextColor() {
		return textColor;
	}

	/**
	 * @param textColor the textColor to set
	 */
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void press(int mouseX, int mouseY) {
		if(!visible)
			return;
		if(mouseIsIn(mouseX, mouseY))
			setImage(clicked);
	}
	
	public void release(int mouseX, int mouseY) {
		if(!visible)
			return;
		if(mouseIsIn(mouseX, mouseY))
			for(ActionListener ae : actionListeners)
				ae.actionPerformed(new ActionEvent(this, 0, "Button pressed"));
		setImage(unclicked);
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public String toString() {
		return "MachiKoroButton: " + getText() + " - " + getColor() + " - " + getType();
	}
	
}