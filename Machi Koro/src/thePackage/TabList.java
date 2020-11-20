package thePackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TabList implements Paintable, Pressable, Typable, ImageUpdatable{
	
	private ArrayList<Tab> tabs;
	private Color borderColor, fontColor, selectedColor;
	private Font font;
	private int x, y;
	private int selected;
	private Point centered = null;
	int size = 20;
	private ArrayList<ActionListener> actionListeners;
	
	public TabList(int x, int y) {
		tabs = new ArrayList<>(); actionListeners = new ArrayList<>();
		setX(x); setY(y);
		setBorderColor(FrameHandler.accent4); setFontColor(FrameHandler.accent1); setFont(FrameHandler.getDefaultFont(size)); setSelectedColor(FrameHandler.background1);
		setSelected(0);
	}

	@Override
	public void paintIt(Graphics g) {
		if(!tabs.isEmpty()) {
			int a;
			for(a = 0; a < tabs.size(); a++) {
				Tab t = tabs.get(a);
				if(a == getSelected())
					t.paintIt(g);
				
				g.setFont(getFont());
				int w = t.width(getFont());
				g.setColor(getFontColor());
				if(a == getSelected())
					g.setColor(getSelectedColor());
				g.drawString(t.getTitle(), t.getX() + 10, t.getY() + getHeight() - 10);
				
				if(a < tabs.size() - 1) {
					g.setColor(getBorderColor());
					g.drawLine(t.getX() + w, getY(), t.getX() + w, getY() + size + 20);
				}
			}
		}
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}
	
	public void addTab(String title, MachiKoroScene scene) {
		tabs.add(new Tab(title, scene));
		generateTabCoords();
	}
	
	private void generateTabCoords() {
		int w = 0, h = 0;
		if(centered != null) {
			w = findWidth(); h = getHeight();
			int x = centered.x, y = centered.y;
			setX(x - w / 2); setY(y - h / 2);
		}
		int width = 0;
		for(Tab t : tabs) {
			t.setX(getX() + width); t.setY(getY());
			width+=t.width(getFont());
		}
	}
	
	public int findWidth() {
		int width = 0;
		for(Tab t : tabs)
			width+=t.width(getFont());
		return width;
	}
	
	public int getHeight() {
		return size + 20;
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
	 * @return the fontColor
	 */
	public Color getFontColor() {
		return fontColor;
	}

	/**
	 * @param fontColor the fontColor to set
	 */
	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
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

	public boolean selectedIsIn() {
		return getSelected() >= 0 && getSelected() < tabs.size();
	}

	@Override
	public void press(int mouseX, int mouseY) {
		for(int a = 0; a < tabs.size(); a++)
			if( mouseX > tabs.get(a).getX() && 
				mouseX < tabs.get(a).getX() + tabs.get(a).width(getFont()) &&
				mouseY > tabs.get(a).getY() && 
				mouseY < tabs.get(a).getY() + getHeight())
				setSelected(a);
		if(mouseX > getX() && mouseX < getX() + findWidth() && mouseY > getY() && mouseY < getY() + getHeight())
			for(ActionListener ae : actionListeners)
				ae.actionPerformed(new ActionEvent(this, 0, "", getSelected()));
		if(selectedIsIn())
			tabs.get(getSelected()).press(mouseX, mouseY);
	}
	
	@Override
	public void release(int mouseX, int mouseY) {
		if(selectedIsIn())
			tabs.get(getSelected()).release(mouseX, mouseY);
	}

	@Override
	public void updateImage(int mouseX, int mouseY) {
		if(selectedIsIn())
			tabs.get(getSelected()).updateImage(mouseX, mouseY);
	}

	@Override
	public void type(int key) {
		if(selectedIsIn())
			tabs.get(getSelected()).type(key);
	}

	@Override
	public void releaseKey(int key) {
		if(selectedIsIn())
			tabs.get(getSelected()).releaseKey(key);
	}

	/**
	 * @return the selectedColor
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}

	/**
	 * @param selectedColor the selectedColor to set
	 */
	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	/**
	 * @param centered the centered to set
	 */
	public void stayCentered(Point centered) {
		this.centered = centered;
	}

	/**
	 * @return the actionListeners
	 */
	public ArrayList<ActionListener> getActionListeners() {
		return actionListeners;
	}
	
	public void addActionListener(ActionListener ae) {
		actionListeners.add(ae);
	}
	
	public void removeActionListener(int index) {
		actionListeners.remove(index);
	}

}

class Tab implements Paintable, Pressable, Typable, ImageUpdatable{
	
	private String title;
	private MachiKoroScene scene;
	private int x, y;
	
	public Tab(String title, MachiKoroScene scene) {
		setTitle(title); setScene(scene);
	}
	
	@Override
	public void paintIt(Graphics g) {
		scene.paintIt(g);
	}
	
	@Override
	public boolean shouldRemove() {
		return scene.shouldRemove();
	}
	
	public int width(Font f) {
		return GlobalVariables.frameHandler.getGraphics().getFontMetrics(f).stringWidth(title) + 20;
	}

	/**
	 * @return the scene
	 */
	public MachiKoroScene getScene() {
		return scene;
	}

	/**
	 * @param scene the scene to set
	 */
	public void setScene(MachiKoroScene scene) {
		this.scene = scene;
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

	@Override
	public void press(int mouseX, int mouseY) {
		scene.press(mouseX, mouseY);
	}

	@Override
	public void release(int mouseX, int mouseY) {
		scene.release(mouseX, mouseY);
	}

	@Override
	public void updateImage(int mouseX, int mouseY) {
		scene.updateImage(mouseX, mouseY);
	}

	@Override
	public void type(int key) {
		scene.type(key);
	}

	@Override
	public void releaseKey(int key) {
		scene.releaseKey(key);
	}
	
	@Override
	public String toString() {
		return "Tab:" + title;
	}
	
}