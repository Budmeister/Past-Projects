package thePackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class TextBox implements Paintable, Pressable, Typable{
	
	private Color backColor, borderColor, selectedColor, textColor;
	private String text;
	private int x, y, width, height;
	private int fontSize = 20;
	private Font font;
	private boolean selected;
	private boolean editable;
	private ArrayList<ActionListener> actionListeners;
	
	public TextBox(int x, int y, int width) {
		actionListeners = new ArrayList<>();
		setX(x); setY(y); setWidth(width); setHeight(fontSize + 20); setText(""); setSelected(false); setEditable(true);
		setBorderColor(FrameHandler.accent4); setTextColor(FrameHandler.accent1); setFont(FrameHandler.getDefaultFont(fontSize)); setSelectedColor(FrameHandler.background1); setBackColor(FrameHandler.background2);
	}
	
	/**
	 * @return the backColor
	 */
	public Color getBackColor() {
		return backColor;
	}
	/**
	 * @param backColor the backColor to set
	 */
	public void setBackColor(Color backColor) {
		this.backColor = backColor;
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
	}
	
	public boolean mouseIsIn(int mouseX, int mouseY) {
		return mouseX > getX() && mouseX < getX() + getWidth() && mouseY > getY() && mouseY < getY() + getHeight();
	}

	@Override
	public void type(int key) {
		if(isSelected() && isEditable()) {
			if(key == KeyEvent.VK_BACK_SPACE) {
				if(getText().length() != 0)
					setText(getText().substring(0, getText().length() - 1));
			}else if(key == KeyEvent.VK_ENTER) {
				for(ActionListener ae : actionListeners)
					ae.actionPerformed(new ActionEvent(this, key, text));
			}else {
				String s = Typable.getKeyNameFromKeyCode(key);
				char c;
				if(s.contains("NumPad"))
					c = s.charAt(7);
				else if(s.equals("Back Quote"))
					c = '`';
				else if(s.equals("Minus"))
					c = '-';
				else if(s.equals("Equals"))
					c = '=';
				else if(s.equals("Period"))
					c = '.';
				else if(s.equals("Comma"))
					c = ',';
				else if(s.equals("Slash"))
					c = '/';
				else if(s.equals("Back Slash"))
					c = '\\';
				else if(s.equals("Quote"))
					c = '\'';
				else
					c = s.charAt(0);
				setText(getText() + c);
			}
		}
	}

	@Override
	public void releaseKey(int key) {
		
	}

	@Override
	public void press(int mouseX, int mouseY) {
		if(isEditable())
			setSelected(mouseIsIn(mouseX, mouseY));
	}

	@Override
	public void release(int mouseX, int mouseY) {
		
	}

	@Override
	public void paintIt(Graphics g) {
		if(isSelected())
			g.setColor(getSelectedColor());
		else
			g.setColor(getBackColor());
		g.fillRect(getX(), getY(), getWidth(), getHeight());
		g.setColor(getTextColor());
		g.setFont(getFont());
		g.drawString(getText(), getX() + 10, getY() + getHeight() - 10);
		g.setColor(getBorderColor());
		g.drawRect(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public boolean shouldRemove() {
		return false;
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
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
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
