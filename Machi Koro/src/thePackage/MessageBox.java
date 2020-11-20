package thePackage;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MessageBox implements Paintable, Pressable, ImageUpdatable, ActionListener {
	
	private boolean shouldRemove;
	private String message;
	private ArrayList<String> messageLines;
	private RoundButton button1, button2;
	private MachiKoroScene otherPainters;
	private int width, height;
	protected static final int radius = 50, border = 5;
	protected static final int x = FrameHandler.width / 2, y = FrameHandler.height / 2;
	protected static final int maxWidth = 500, maxHeight = 400;
	private ArrayList<ActionListener> actionListeners;
	private int cwspeed;
	protected static final int dwspeed = 100;
	protected static final int fontSize = 30;
	
	public MessageBox(String message) {
		this.message = message;
		width = 0; height = 0;
		button1 = new RoundButton("red", "wide", x - 100, y + maxHeight / 2 - 100, 200, 50);
		button1.setText("Close");
		button1.addActionListener(this);
		actionListeners = new ArrayList<>();
	}
	
	public MessageBox(String message, String exitButtonMessage) {
		this.message = message;
		width = 0; height = 0;
		button1 = new RoundButton("red", "wide", x - 100, y + maxHeight / 2 - 100, 200, 50);
		button1.setText(exitButtonMessage);
		button1.addActionListener(this);
		actionListeners = new ArrayList<>();
	}
	
	public MessageBox(String message, String button1Message, String button2Message) {
		this.message = message;
		width = 0; height = 0;
		button1 = new RoundButton("red", "wide", x - 225, y + maxHeight / 2 - 100, 200, 50);
		button1.setText(button1Message);
		button1.addActionListener(this);
		button2 = new RoundButton("green", "wide", x + 25, y + maxHeight / 2 - 100, 200, 50);
		button2.setText(button2Message);
		button2.addActionListener(this);
		actionListeners = new ArrayList<>();
	}
	
	private void createLines(Graphics g) {
		messageLines = new ArrayList<>();
		FontMetrics met = g.getFontMetrics();
		String curr = "";
		while(met.stringWidth(message) > width - border) {
			boolean nl = false;
			while(met.stringWidth(curr) < width - border && !message.equals("")) {
				if(message.charAt(0) == '\n') {
					message = message.substring(1);
					nl = true;
					break;
				}
				curr+=message.charAt(0);
				message = message.substring(1);
			}
			if(!message.equals("") && !nl) {
				message = curr.charAt(curr.length() - 1) + message;
				curr = curr.substring(0, curr.length() - 1);
			}
			messageLines.add(curr);
			curr = "";
		}
		messageLines.add(message);
	}
	
	public void addActionListener(ActionListener ae) {
		actionListeners.add(ae);
	}
	
	public void addPainter(Paintable p) {
		if(otherPainters == null)
			otherPainters = new MachiKoroScene();
		otherPainters.addPainter(p);
	}
	
	protected boolean shouldPaint() {
		return width >= maxWidth;
	}
	
	public void paintIt(Graphics g) {
		int arcWidth = (width < radius ? width : radius * 2);
		g.setColor(FrameHandler.accent1);
		g.fillRoundRect(x - width / 2, y - height / 2, width, height, arcWidth, arcWidth);
		for(int y = getTopY(arcWidth / 2); y < getBottomY(border); y++) {
			double alph = getAlpha(y, arcWidth / 2, border);
			g.setColor(getColor(alph));
			g.drawLine(getLeftX(y, arcWidth / 2, border), y, getRightX(y, arcWidth / 2, border), y);
		}
		if(shouldPaint()) {
			g.setFont(FrameHandler.getDefaultFont(fontSize));
			
			if(messageLines == null)
				createLines(g);
			int totalHeight = fontSize * messageLines.size();
			g.setColor(Color.white);
			for(int a = 0; a < messageLines.size(); a++) {
				String ln = messageLines.get(a);
				int sw = g.getFontMetrics().stringWidth(ln);
				g.drawString(ln, x - sw / 2, y - totalHeight / 2 + fontSize * a);
			}
			
			if(button1 != null)
				button1.paintIt(g);
			if(button2 != null)
				button2.paintIt(g);
			if(otherPainters != null)
				otherPainters.paintIt(g);
		}
		width+=cwspeed;
		height+=((double) maxHeight) / maxWidth * cwspeed;
		if(shouldPaint() || width <= 0)
			freeze();
	}
	
	private int getLeftX(int y, int r, int b) {
		y-=MessageBox.y + height / 2;
		y*=-1;
		y-=r;
		return (int) -Math.sqrt(Math.pow(r - b, 2) - Math.pow(y, 2)) + x - width / 2 + r;
//		return (int) -Math.sqrt(Math.pow((getBottomY(b) - getTopY(r)),2) - Math.pow(y-getBottomY(b),2)) + getBottomY(b);
	}
	
	private int getRightX(int y, int r, int b) {
		return 2 * x - getLeftX(y, r, b);
	}
	
	private int getTopY(int r) {
		return y + height / 2 - r;
	}
	
	private int getBottomY(int b) {
		return y + height / 2 - b;
	}
	
	private double getAlpha(int y, int r, int b) {
		return ((double) (y - getTopY(r))) / (getBottomY(b) - getTopY(r)) * 255;
	}
	
	private static ArrayList<Color> alphas;
	private Color getColor(double a) {
		int i = (int) a;
		if(alphas == null)
			alphas = new ArrayList<>();
		if(i >= alphas.size())
			for(int alph = alphas.size(); alph <= i; alph++)
				alphas.add(new Color(255, 255, 255, alph));
		return alphas.get(i);
	}
	
	public boolean shouldRemove() {
		return shouldRemove && width == 0;
	}
	
	public void enter() {
		cwspeed = dwspeed;
	}
	
	public void exit() {
		cwspeed = -dwspeed;
		shouldRemove = true;
	}
	
	public void freeze() {
		cwspeed = 0;
	}

	@Override
	public void press(int mouseX, int mouseY) {
		if(shouldPaint()) {
			if(button1 != null)
				button1.press(mouseX, mouseY);
			if(button2 != null)
				button2.press(mouseX, mouseY);
			if(otherPainters != null)
				otherPainters.press(mouseX, mouseY);
		}
	}

	@Override
	public void release(int mouseX, int mouseY) {
		if(shouldPaint()) {
			if(button1 != null)
				button1.release(mouseX, mouseY);
			if(button2 != null)
				button2.release(mouseX, mouseY);
			if(otherPainters != null)
				otherPainters.release(mouseX, mouseY);
		}
	}

	@Override
	public void updateImage(int mouseX, int mouseY) {
		if(shouldPaint()) {
			if(button1 != null)
				button1.updateImage(mouseX, mouseY);
			if(button2 != null)
				button2.updateImage(mouseX, mouseY);
			if(otherPainters != null)
				otherPainters.updateImage(mouseX, mouseY);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String wb = (e.getSource() == button1 ? "button1" : "button2");
		for(ActionListener al : actionListeners)
			al.actionPerformed(new ActionEvent(this, 0, wb));
	}

}
