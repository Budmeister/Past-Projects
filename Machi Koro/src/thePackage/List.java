package thePackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class List<T> extends ArrayList<T> implements Paintable, Pressable{

	private int x, y, width, height;
	private int itemWidth;
	private Color itemColor, borderColor, backgroundColor;
	private Font font;
	private int selected;
	private String title;
	
	public List(int x, int y, int width, int height) {
		setX(x); setY(y); setWidth(width); setHeight(height);
		setItemWidth(25); setFont(FrameHandler.getDefaultFont(getItemWidth() - 10)); setTitle("");
		setItemColor(FrameHandler.accent1); setBorderColor(FrameHandler.accent2); setBackgroundColor(FrameHandler.background1);
		setSelected(-1);
	}
	
	@Override
	public void paintIt(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
		g.setFont(getFont());
		FontMetrics metrics = g.getFontMetrics();
		for(int a = 0; a < size(); a++) {
			T t = get(a);
			int y = getY() + getItemWidth() * a;
			if(a == getSelected())
				g.setColor(Color.WHITE);
			else
				g.setColor(itemColor);
			g.fillRect(getX(), y, getWidth(), getItemWidth());
			g.setColor(Color.BLACK);
			g.drawString(t.toString(), getX() + 10, y + (getItemWidth() - metrics.getHeight()) + metrics.getAscent());
			
			
			g.drawRect(getX(), y, getWidth(), getItemWidth());
			
		}
		g.setColor(getBorderColor());
		g.drawRect(getX(), getY(), getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawString(getTitle(), getX(), getY() - metrics.getHeight());
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}

	@Override
	public void press(int mouseX, int mouseY) {
		if(getIndex(mouseX, mouseY) != -1)
			setSelected(getIndex(mouseX, mouseY));
	}

	@Override
	public void release(int mouseX, int mouseY) {
		
	}

	public boolean mouseIsIn(int x, int y) {
		return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + getHeight();
	}
	
	public int getIndex(int mouseX, int mouseY) {
		if(mouseIsIn(mouseX, mouseY) && mouseY < getItemWidth() * size() + getY())
			return (int) ((mouseY - getY()) / getItemWidth());
		else
			return -1;
	}
	
	public void setList(T[] list) {
		while(!isEmpty())
			remove(0);
		addList(list);
	}
	
	public void addList(T[] list) {
		for(T t : list)
			add(t);
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
	 * @return the itemWidth
	 */
	public int getItemWidth() {
		return itemWidth;
	}

	/**
	 * @param itemWidth the itemWidth to set
	 */
	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	/**
	 * @return the itemColor
	 */
	public Color getItemColor() {
		return itemColor;
	}

	/**
	 * @param itemColor the itemColor to set
	 */
	public void setItemColor(Color itemColor) {
		this.itemColor = itemColor;
	}

	/**
	 * @return the borderColor
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
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
	 * @return the selected
	 */
	public int getSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(int selected) {
		this.selected = selected;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}