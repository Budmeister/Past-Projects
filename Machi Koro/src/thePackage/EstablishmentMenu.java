package thePackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class EstablishmentMenu extends LoggingObject implements Paintable, Pressable, ImageUpdatable{
	
	private boolean isOpen;
	private int width, height, minHeight, maxHeight;
	private final BufferedImage borderTop, borderBottom;
	private int openSpeed;
	private Color color;
	private Point mid;
	private BufferedImage[] images;
	private ArrayList<ActionListener> actionListeners;
	private boolean isChoosing;
	private int hovering;
	public int[][] numCardsRemaining;
	public RoundButton cancel;
	
	public EstablishmentMenu() {
		s = new Status("EstablishmentMenu", "closed");
		l = new Logger(s);
		width = 1000; minHeight = 100; height = minHeight; maxHeight = 650;
		openSpeed = 25; color = FrameHandler.background1;
		mid = new Point(FrameHandler.width / 2, FrameHandler.height / 2);
		actionListeners = new ArrayList<>();
		cancel = new RoundButton("red", "wide", FrameHandler.width / 2 + 250, FrameHandler.height / 2 + 300, 200, 50); cancel.setText("Cancel");
		cancel.addActionListener(ae -> {
			for(int a = 0; a < actionListeners.size(); a++)
				actionListeners.get(a).actionPerformed(new ActionEvent(this, -1, null));
			close();
		});
		isOpen = false; isChoosing = false;
		hovering = -1;
		xInfo = getLeftBoundAndSpacingForRowWith5(cardWidth, xPadding);
		yInfo = getTopBoundAndSpacingForColumnWith3(cardHeight, yPadding);
		
		int[][] ncl = {
				{6,6,6,6,6},
				{6,4,4,4,6},
				{6,6,6,6,6}
		};
		numCardsRemaining = ncl;
		
		borderTop = CardImages.getImage("establishment_menu_border_opaque_top.png");
		borderBottom = CardImages.getImage("establishment_menu_border_opaque_bottom.png");
		createOriginalImages();
	}
	
	public int getNumRemaining(String est) {
		int e = -1;
		for(int a = 0; a < en.length; a++)
			if(en[a].equals(est))
				e = a;
		if(e == -1)
			throw new IllegalArgumentException("The input must be the name of an establishment");
		int y = e / 5;
		int x = e % 5;
		return numCardsRemaining[y][x];
	}
	
	private String[] en;
	
	private void createOriginalImages() {
		String[] en = {
				"Wheat Field",
				"Ranch",
				"Bakery",
				"Cafe",
				"Convenience Store",
				"Forest",
				"Stadium",
				"TV Station",
				"Business Center",
				"Cheese Factory",
				"Furniture Factory",
				"Mine",
				"Family Restaurant",
				"Apple Orchard",
				"Fruit and Vegetable Market"};
		this.en = en;
		images = new BufferedImage[en.length];
		for(int a = 0; a < en.length; a++)
			images[a] = CardImages.getImage(en[a] + " - north.png");
	}

	@Override
	public void paintIt(Graphics g) {
		if(isOpen) {
			if(height < maxHeight)
				height+=openSpeed;
		}else {
			if(height > minHeight)
				height-=openSpeed;
		}
		if(height > minHeight) {
			g.setColor(color);
			g.fillRect(mid.x - width / 2, mid.y - height / 2 + 50, width, height - 100);
			g.drawImage(borderTop, mid.x - width / 2, mid.y - height / 2, width, 50, null);
			g.drawImage(borderBottom, mid.x - width / 2, mid.y + height / 2 - 50, width, 50, null);
			if(height > 195) {
				paintMiddleRow(g);
			}
			if(height == maxHeight && isChoosing) {
				cancel.paintIt(g);
				paintTopRow(g);
				paintBottomRow(g);
			}
			if(height == maxHeight && isChoosing && hovering != -1) {
				int yi = hovering / 5;
				int xi = hovering % 5;
				int dx = cardWidth + xInfo[1];
				int dy = cardHeight + yInfo[1];
				int x = xInfo[0] + xi * dx - stackedCardDistance * numCardsRemaining[yi][xi];
				int y = yInfo[0] + yi * dy - stackedCardDistance * numCardsRemaining[yi][xi];
				g.setColor(Color.YELLOW);
				for(int a = 0; a < 5; a++)
					g.drawRect(x - a, y - a, cardWidth + 2 * a, cardHeight + 2 * a);
			}
		}
	}
	
	private int[] getLeftBoundAndSpacingForRowWith5(int imageWidth, int padding) {
		int total = width;
		total-=5 * imageWidth;
		total-=2 * padding;
		int spacing = total / 4;
		int leftBound = (int) (FrameHandler.width / 2 - 2.5 * imageWidth - 2 * spacing);
		int[] r = {
				leftBound,
				spacing
		};
		return r;
	}
	
	private int[] getTopBoundAndSpacingForColumnWith3(int imageHeight, int padding) {
		int total = maxHeight;
		total-=3 * imageHeight;
		total-=2 * padding;
		int spacing = total / 2;
		int topBound = (int) (FrameHandler.height / 2 - 1.5 * imageHeight - spacing);
		int[] r = {
				topBound,
				spacing
		};
		return r;
	}
	
	private int cardWidth = 120,
			cardHeight = 180,
			xPadding = 50,
			yPadding = 40;
	private int[] xInfo, yInfo;
	
	private int stackedCardDistance = 2;
	
	private void paintTopRow(Graphics g) {
		int first = 0, last = 4;
		int w = cardWidth, h = cardHeight;
		int x = xInfo[0], spacing = xInfo[1], dx = w + spacing, y = yInfo[0];
		
		paintRow(first, last, x, y, dx, w, h, g);
	}
	
	private void paintMiddleRow(Graphics g) {
		int first = 5, last = 9;
		int w = cardWidth, h = cardHeight;
		int x = xInfo[0], spacing = xInfo[1], dx = w + spacing, y = yInfo[0] + yInfo[1] + h;
		
		paintRow(first, last, x, y, dx, w, h, g);
	}
	
	private void paintBottomRow(Graphics g) {
		int first = 10, last = 14;
		int w = cardWidth, h = cardHeight;
		int x = xInfo[0], spacing = xInfo[1], dx = w + spacing, y = yInfo[0] + 2 * (yInfo[1] + h);
		
		paintRow(first, last, x, y, dx, w, h, g);
	}
	
	private void paintRow(int first, int last, int x, int y, int dx, int w, int h, Graphics g) {
		for(int i = first; i <= last; i++) {
			BufferedImage img = images[i];
			for(int a = 0; a < numCardsRemaining[i / 5][i % 5]; a++)
				g.drawImage(img, x - a * stackedCardDistance, y - a * stackedCardDistance, w, h, null);
			x+=dx;
		}
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public void open() {
		isOpen = true;
		s.setStatus("open");
	}
	
	public void close() {
		isOpen = false;
		s.setStatus("closed");
		GlobalVariables.frameHandler.messageDisplayer.setText("");
	}
	
	public void choose() {
		open();
		isChoosing = true;
	}
	
	public void addActionListener(ActionListener al) {
		actionListeners.add(al);
	}
	
	public void removeActionListener(ActionListener al) {
		actionListeners.remove(al);
	}
	
	public void removeActionListener(int index) {
		actionListeners.remove(index);
	}
	
	public void resetActionListeners() {
		actionListeners = new ArrayList<>();
	}
	
	public int mouseOver(int x, int y) {
		int xInit = xInfo[0], dx = cardWidth + xInfo[1];
		int yInit = yInfo[0], dy = cardHeight + yInfo[1];
		int[] c = {-1, -1};
		for(int a = 0; a < 5; a++)
			for(int b = 0; b < 3; b++)
				if(x > xInit + a * dx - stackedCardDistance * numCardsRemaining[b][a] && 
						x < xInit + a * dx + cardWidth - stackedCardDistance * numCardsRemaining[b][a] &&
						y > yInit + b * dy - stackedCardDistance * numCardsRemaining[b][a] &&
						y < yInit + b * dy + cardHeight - stackedCardDistance * numCardsRemaining[b][a]) {
					c[0] = a; c[1] = b;
				}
		if(c[0] != -1)
			return c[0] + 5 * c[1];
		else
			return -1;
	}
	
	@Override
	public void press(int mouseX, int mouseY) {
		if(height == maxHeight && isChoosing)
			cancel.press(mouseX, mouseY);
	}
	
	@Override
	public void release(int mouseX, int mouseY) {
		if(!isChoosing)
			return;
		int card = mouseOver(mouseX, mouseY);
		if(card != -1)
			for(int a = 0; a < actionListeners.size(); a++)
				actionListeners.get(a).actionPerformed(new ActionEvent(this, card, en[card]));
		if(height == maxHeight)
			cancel.release(mouseX, mouseY);
	}
	
	@Override
	public void updateImage(int mouseX, int mouseY) {
		if(!isOpen)
			return;
		int mo = mouseOver(mouseX, mouseY);
		if(isChoosing)
			hovering = mo;
		else
			hovering = -1;
		if(mo != -1) {
			Establishment e = Establishments.getEstablishment(en[mo]);
			GlobalVariables.frameHandler.messageDisplayer.setText(e.getName() + ": " + e.getMessage());
		}else
			GlobalVariables.frameHandler.messageDisplayer.setText("");
		if(height == maxHeight && isChoosing)
			cancel.updateImage(mouseX, mouseY);
	}
	
}
