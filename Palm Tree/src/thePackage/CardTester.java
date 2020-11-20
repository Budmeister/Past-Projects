package thePackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.*;

import arrays.Array;
import decks.*;

@SuppressWarnings("serial")
public class CardTester extends JComponent implements MouseListener, ActionListener, MouseMotionListener{
	static ArrayList<Array> decks = new ArrayList<>();
	static JFrame frame;
	static Timer t;
	int width = 1000, height = 700;
	static int selectedDeck;
	int mouseX, mouseY, pmouseX, pmouseY;
	boolean[] mouseButtons = {false, false, false, false};

	public static void main(String[] args) {
//		File directory = new File("./");
//		   System.out.println(directory.getAbsolutePath());
		Deck deck1 = new Deck();
		deck1.stackBicycle();
		deck1.shuffle2();
		System.out.println(deck1);
		selectedDeck = 0;
		addDeck(deck1, 50, 100);
		
		Deck deck2 = new Deck();
		deck2.stackBicycle();
		deck2.shuffle3();
		System.out.println(deck2);
		addDeck(deck2, 350, 100);
		
		frame = new JFrame("BRUH");
		CardTester ct = new CardTester();
		frame.add(ct);
		frame.addMouseListener(ct);
		frame.addMouseMotionListener(ct);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setFocusTraversalKeysEnabled(false);
		
		t = new Timer(10, ct);
		t.start();
	}
	
	static void addDeck(Deck deck, int x, int y) {
		Array d = new Array();
		d.push(deck);
		d.push(x);
		d.push(y);
		decks.add(d);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(new Color(0, 117, 84));
		g.fillRect(0, 0, width, height);
		
		for(int d = 0; d < decks.size(); d++) {
			Array info = decks.get(d);
			Deck deck = (Deck) info.i(0).v;
			int x = info.i(1).I(), y = info.i(2).I();
			for(int a = deck.getHeight(); a > 0; a--) {
				BicycleCard c = (BicycleCard) deck.getCard(deck.getHeight() - a);
				Image i;
				if(c.isFaceUp())
					i = c.getImage();
				else
					i = c.getBackImage();
				g.drawImage(i, x + a, y + a, 100, 140, null);
			}
		}
		
		Array sd = decks.get(selectedDeck);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(sd.i(1).I() - 10, sd.i(2).I() - 10, 270, 370);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseButtons[e.getButton()] = true;
		int s = decks.size();
		for(int d = 0; d < s; d++) {
			Array info = decks.get(d);
			int mx = e.getX() - 8, my = e.getY() - 31;
			int dx = info.i(1).I(), dy = info.i(2).I();
			if(mx > dx - 10 && mx < dx + 270 && my > dy - 10 && my < dy + 370) {
				selectedDeck = d;
				if(mouseButtons[1] && mouseButtons[3]) {
					Deck newDeck = new Deck();
					Deck deck = (Deck) info.i(0).v;
					if(deck.getHeight() != 1) {
						newDeck.stackCard(deck.popTopCard());
						addDeck(newDeck, info.i(1).I(), info.i(2).I());
						selectedDeck = decks.size() - 1;
					}
				}
//				if(e.getButton() == 3) {
//					Deck deck = (Deck) info.i(0).v;
//					BicycleCard card = (BicycleCard) deck.getTopCard();
//					card.setFaceUp(!card.isFaceUp());
//				}
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButtons[e.getButton()] = false;
		if(e.getButton() == 3) {
			Array info = decks.get(selectedDeck);
			int mx = e.getX() - 8, my = e.getY() - 31;
			int dx = info.i(1).I(), dy = info.i(2).I();
			if(mx > dx - 10 && mx < dx + 270 && my > dy - 10 && my < dy + 370) {
				Deck deck = (Deck) info.i(0).v;
				BicycleCard card = (BicycleCard) deck.getTopCard();
				card.setFaceUp(!card.isFaceUp());
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		decks.get(selectedDeck).i(1).v = decks.get(selectedDeck).i(1).I() + mouseX - pmouseX;
		decks.get(selectedDeck).i(2).v = decks.get(selectedDeck).i(2).I() + mouseY - pmouseY;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		pmouseX = mouseX;
		pmouseY = mouseY;
		mouseX = e.getX() - 8;
		mouseY = e.getY() - 31;
	}

}
