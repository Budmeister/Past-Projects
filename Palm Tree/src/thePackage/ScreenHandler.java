package thePackage;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;

import javax.swing.*;

import decks.*;

public class ScreenHandler extends JComponent implements MouseListener, ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8306285024062002498L;

	public JFrame frame;
	
	int width = 1000, height = 700;
	int offsetX = 8;
	int offsetY = 31;

	boolean showMessage = false;
	ArrayList<String> message;
	int messageType;
	Runnable onClose;
	boolean answer;
	
	ArrayList<CardSlide> slides = new ArrayList<>();
	ArrayList<Confetti> confetti = new ArrayList<>();
	
	PalmTreeGame game;
	Logger logger;
	
	Random r = new Random();
	
	public ScreenHandler(PalmTreeGame g) {
		game = g;
		logger = g.logger;
		if(System.getProperty("os.name").substring(0, 3).equalsIgnoreCase("mac")) {
			offsetX = 1;
			offsetY = 25;
		}
		
		frame = new JFrame("Palm Tree");
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addMouseListener(this);
		
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public void paintComponent(Graphics g) {
		int w = width, h = height;
		String programStatus = game.programStatus;
		String gameStatus = game.gameStatus;
		
		g.drawString(programStatus, w / 2, h / 2);
		if(gameStatus != null)
			g.drawString(gameStatus, w / 2, h / 2 + 100);
		
		if(programStatus.equals("menu") || 
		   programStatus.equals("searching") || 
		   programStatus.equals("hosting") || 
		   programStatus.equals("unknown host") || 
		   programStatus.equals("unreachable") || 
		   programStatus.equals("connection refused") || 
		   programStatus.equals("ioexception") || 
		   programStatus.equals("connected") || 
		   programStatus.equals("found")) {
			drawMenu(g);
		}else if(programStatus.length() > 7 && programStatus.substring(programStatus.length() - 7).equals("in game")) {
			try {
				drawGame(g);
			}catch(NullPointerException e) {
				
			}
		}
		
		
		
		if(showMessage) {
			displayMessage(g);
		}
		
		for(int c = 0; c < confetti.size(); c++) {
			Confetti conf = confetti.get(c);
			Color color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
			int size = 5;
			int[] xs = {(int) (conf.x - size), (int) conf.x, (int) (conf.x + size), (int) conf.x};
			int[] ys = {(int) conf.y, (int) (conf.y - size), (int) conf.y, (int) (conf.y + size)};
			g.setColor(color);
			g.fillPolygon(xs, ys, xs.length);
			
			int gravity = 1;
			conf.x+=conf.xVel;
			conf.y+=conf.yVel;
			conf.yVel+=gravity;
			conf.timer++;
			if(conf.timer == 100)
				confetti.remove(confetti.indexOf(conf));
		}
		
	}

	void drawMenu(Graphics g) {
		int w = width, h = height;
		
		g.setColor(new Color(252, 255, 132));
		g.fillRect(0, 0, w, h);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(10, 10, w - 20, h - 20);
		g.setColor(new Color(13, 183, 193));
		g.setFont(new Font("Bradley Hand ITC", Font.BOLD, 80));
		g.drawString("Palm", w / 2 - 150, h / 2 - 100);
		g.drawString("Tree", w / 2 - 50, h / 2);
		
		g.setColor(new Color(42, 247, 168));
		g.fillRoundRect(3 * w / 4 - 100, 3 * h / 4 - 50, 200, 100, 100, 100);
		g.setColor(new Color(0, 0, 0));
		g.drawRoundRect(3 * w / 4 - 100, 3 * h / 4 - 50, 200, 100, 100, 100);
		g.setFont(new Font("Bradley Hand ITC", Font.BOLD, 50));
		g.drawString("Host", 3 * w / 4 - 50, 3 * h / 4 + 12);
		
		g.drawRoundRect(w / 4 - 100, 3 * h / 4 - 50, 200, 100, 100, 100);
		g.drawString("Join", w / 4 - 50, 3 * h / 4 + 12);
	}
	
	void drawGame(Graphics g) {
		g.setColor(new Color(0, 117, 84));
		g.fillRect(0, 0, width, height);
		g.setColor(new Color(66, 134, 244));
		if(game.gameStatus.equals("my turn"))
			g.fillOval(350, 420, 30, 30);
		else if(game.gameStatus.equals("your turn"))
			g.fillOval(350, 240, 30, 30);
		
		if(game.drawPile != null) {
			g.setColor(new Color(0, 0, 0));
			g.drawRect(500, 280, 100, 140);
			drawDeck(100, 100, game.drawPile, g);
			drawDeck(100, 460, game.gonePile, g);
			drawDeck(500, 280, game.discardPile, g);
			
			Deck myBottomCards, myTopCards, myHand, yourBottomCards, yourTopCards, yourHand;
			if(game.programStatus.equals("Host: in game")) {
				myBottomCards = game.serverBottomCards;
				myTopCards = game.serverTopCards;
				myHand = game.serverHand;
				yourBottomCards = game.clientBottomCards;
				yourTopCards = game.clientTopCards;
				yourHand = game.clientHand;
			}else {
				myBottomCards = game.clientBottomCards;
				myTopCards = game.clientTopCards;
				myHand = game.clientHand;
				yourBottomCards = game.serverBottomCards;
				yourTopCards = game.serverTopCards;
				yourHand = game.serverHand;
			}
			drawLowerCards(650, 450, myBottomCards, false, g);
			drawLowerCards(650 - 2, 450 - 2, myTopCards, true, g);
			drawHand(500, 450, myHand, true, g);
			
			drawLowerCards(650, 100, yourBottomCards, false, g);
			drawLowerCards(650 - 2, 100 - 2, yourTopCards, true, g);
			drawHand(500, 100, yourHand, false, g);
			
//			if(game.programStatus.equals("Host: in game")) {
//				for(int c = 0; c < game.serverBottomCards.getHeight(); c++)
//					g.drawImage(game.serverBottomCards.getCard(c).getBackImage(), 650 + 110 * c, 450, 100, 140, null);
//				for(int c = 0; c < game.serverTopCards.getHeight(); c++)
//					g.drawImage(game.serverTopCards.getCard(c).getImage(), 650 + 110 * c - 2, 450 - 2, 100, 140, null);
//				drawHand(500, 450, game.serverHand, true, g);
//				
//				for(int c = 0; c < game.clientBottomCards.getHeight(); c++)
//					g.drawImage(game.clientBottomCards.getCard(c).getBackImage(), 650 + 110 * c, 100, 100, 140, null);
//				for(int c = 0; c < game.clientTopCards.getHeight(); c++)
//					g.drawImage(game.clientTopCards.getCard(c).getImage(), 650 + 110 * c - 2, 100 - 2, 100, 140, null);
//				drawHand(500, 100, game.clientHand, true, g);
//			}else if(game.programStatus.equals("Client: in game")) {
//				for(int c = 0; c < game.clientBottomCards.getHeight(); c++)
//					g.drawImage(game.clientBottomCards.getCard(c).getBackImage(), 650 + 110 * c, 450, 100, 140, null);
//				for(int c = 0; c < game.clientTopCards.getHeight(); c++)
//					g.drawImage(game.clientTopCards.getCard(c).getImage(), 650 + 110 * c - 2, 450 - 2, 100, 140, null);
//				drawHand(500, 450, game.clientHand, true, g);
//				
//				for(int c = 0; c < game.serverBottomCards.getHeight(); c++)
//					g.drawImage(game.serverBottomCards.getCard(c).getBackImage(), 650 + 110 * c, 100, 100, 140, null);
//				for(int c = 0; c < game.serverTopCards.getHeight(); c++)
//					g.drawImage(game.serverTopCards.getCard(c).getImage(), 650 + 110 * c - 2, 100 - 2, 100, 140, null);
//				drawHand(500, 100, game.serverHand, true, g);
//			}
		}
		
		for(int s = 0; s < slides.size(); s++) {
			CardSlide slide = slides.get(s);
			if(!showMessage)
				slide.timer++;
			if(slide.timer == 10) {
				slide.onArrival.run();
				checkWinner();
				slides.remove(s);
			}else {
				Image img;
				if(slide.card.isFaceUp())
					img = slide.card.getImage();
				else
					img = slide.card.getBackImage();
				g.drawImage(img, (int) (slide.fromX + .1 * (slide.toX - slide.fromX) * slide.timer), (int) (slide.fromY + .1 * (slide.toY - slide.fromY) * slide.timer), 100, 140, null);
			}
		}
	}
	
	void drawHand(int x, int y, Deck hand, boolean faceUp, Graphics g) {
//		if(hand == null)
//			return;
		int dw = 20 * (hand.getHeight() - 1) + 100;
		for(int c = 0; c < hand.getHeight(); c++) {
			int offset = 0;
			if(faceUp) {
				int[] chosenCards;
				if(game.programStatus.equals("Host: in game"))
					chosenCards = game.serverChosenCards;
				else
					chosenCards = game.clientChosenCards;
				for(int a : chosenCards)
					if(a == c)
						offset = -20;
			}
			BicycleCard card = (BicycleCard) hand.getCard(c);
			Image img;
			if(faceUp)
				img = card.getImage();
			else
				img = card.getBackImage();
			g.drawImage(img, x - dw / 2 + 20 * c, y + offset, 100, 140, null);
		}
	}
	
	void drawDeck(int x, int y, Deck deck, Graphics g) {
//		if(deck == null)
//			return;
		for(int c = 0; c < deck.getHeight(); c++) {
			BicycleCard card = (BicycleCard) deck.getCard(c);
			Image img;
			if(card.isFaceUp())
				img = card.getImage();
			else
				img = card.getBackImage();
			g.drawImage(img, x - c, y - c, 100, 140, null);
		}
	}
	
	void drawLowerCards(int x, int y, Deck cards, boolean faceUp, Graphics g) {
//		if(cards == null)
//			return;
		for(int a = 0; a < 3; a++) {
			int offset = 0;
			if(faceUp) {
				int[] chosenCards;
				boolean off = true;
				if(game.programStatus.equals("Host: in game")) {
					chosenCards = game.serverChosenCards;
					if(game.serverHand.getHeight() != 0)
						off = false;
				}else {
					chosenCards = game.clientChosenCards;
					if(game.clientHand.getHeight() != 0)
						off = false;
				}
				for(int b : chosenCards)
					if(b == a && off && game.gameStatus.equals("my turn"))
						offset = -20;
			}
			
			if(cards.getHeight() > a) {
				Card card = cards.getCard(a);
				if(card instanceof BicycleCard) {
					Image img;
					if(faceUp)
						img = card.getImage();
					else
						img = card.getBackImage();
					g.drawImage(img, x + 110 * a, y + offset, 100, 140, null);
				}
			}
		}
	}
	
	
	void displayMessage(Graphics g) {
		int w = width, h = height;
		
		int[] xs = {100, 110, w - 110, w - 100, w - 100, w - 110, 110, 100};
		int[] ys = {110, 100, 100, 110, h - 110, h - 100, h - 100, h - 110};

		g.setColor(new Color(252, 255, 132));
		g.fillPolygon(xs, ys, xs.length);
		g.setColor(new Color(0, 0, 0));
		g.drawPolygon(xs, ys, xs.length);

		if(messageType == game.BU_OK) {
			g.setColor(new Color(42, 247, 168));
			g.fillRoundRect(w / 2 - 100, h - 140, 200, 80, 100, 100);
			g.setColor(new Color(0, 0, 0));
			g.drawRoundRect(w / 2 - 100, h - 140, 200, 80, 100, 100);
			
			stringCentered("Ok", w / 2, h - 88, new Font("Bradley Hand ITC", Font.BOLD, 50), g);
		}else if(messageType == game.BU_CANCEL) {
			g.setColor(new Color(42, 247, 168));
			g.fillRoundRect(w / 2 - 100, h - 140, 200, 80, 100, 100);
			g.setColor(new Color(0, 0, 0));
			g.drawRoundRect(w / 2 - 100, h - 140, 200, 80, 100, 100);
			
			stringCentered("Cancel", w / 2, h - 88, new Font("Bradley Hand ITC", Font.BOLD, 50), g);
		}else if(messageType == game.BU_YES_OR_NO) {
			g.setColor(new Color(204, 28, 28));
			g.fillRoundRect(w / 4 - 100, h - 140, 200, 80, 100, 100);
			g.setColor(new Color(0, 0, 0));
			g.drawRoundRect(w / 4 - 100, h - 140, 200, 80, 100, 100);
			stringCentered("No", w / 4, h - 88, new Font("Bradley Hand ITC", Font.BOLD, 50), g);

			g.setColor(new Color(42, 247, 168));
			g.fillRoundRect(3 * w / 4 - 100, h - 140, 200, 80, 100, 100);
			g.setColor(new Color(0, 0, 0));
			g.drawRoundRect(3 * w / 4 - 100, h - 140, 200, 80, 100, 100);
			stringCentered("Yes", 3 * w / 4, h - 88, new Font("Bradley Hand ITC", Font.BOLD, 50), g);
			
		}

		g.setColor(new Color(0, 0, 0));
		stringCentered(message.get(0), w / 2, 200, new Font("Bradley Hand ITC", Font.BOLD, 100), g);
		
		int y = 300;
		for(int l = 1; l < message.size(); l++) {
			stringCentered(message.get(l), w / 2, y, new Font("Bradley Hand ITC", Font.BOLD, 30), g);
			y+= 50;
		}
	}

	void stringCentered(String msg, int x, int y, Font f, Graphics g) {
		int sw = g.getFontMetrics(f).stringWidth(msg);
		g.setFont(f);
		g.drawString(msg, x - sw / 2, y);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 3)
			animateWin(ME);
		
		int x = e.getX() - offsetX;
		int y = e.getY() - offsetY;
		int w = width;
		int h = height;
		
		if(showMessage) {
			if(messageType == game.BU_CANCEL || messageType == game.BU_OK) {
				if(x > w / 2 - 100 && x < w / 2 + 100 && y > h - 140 && y < h - 60) {
					showMessage = false;
					onClose.run();
				}
			}else if(messageType == game.BU_YES_OR_NO) {
				if(x > w / 4 - 100 && x < w / 4 + 100 && y > h - 140 && y < h - 60) {
					showMessage = false;
					answer = false;
					onClose.run();
				}else if(x > 3 * w / 4 - 100 && x < 3 * w / 4 + 100 && y > h - 140 && y < h - 60) {
					showMessage = false;
					answer = true;
					onClose.run();
				}
			}
		}else if(game.programStatus.equals("menu")) {
			if(x > 3 * w / 4 - 100 && x < 3 * w / 4 + 100 && y > 3 * h / 4 - 50 && y < 3 * h / 4 + 50) {
				game.startHost();
			}
			if(x > w / 4 - 100 && x < w / 4 + 100 && y > 3 * h / 4 - 50 && y < 3 * h / 4 + 50) {
				game.startSearch();
			}
			
		}else if(game.programStatus.length() > 7 && game.programStatus.substring(game.programStatus.length() - 7).equals("in game")) {
			if(game.gameStatus.equals("chosing cards")) {
				if(x > 390 && x < 610 && y > 450 && y < 590) {
					int c = (x - 390) / 20;
					if(c >= 7)
						c = 6;
					int[] chosenCards;
					if(game.programStatus.equals("Host: in game"))
						chosenCards = game.serverChosenCards;
					else
						chosenCards = game.clientChosenCards;
					boolean chosen = false;
					int numChosen = 0;
					for(int a = 0; a < chosenCards.length; a++) {
						if(chosenCards[a] != -1)
							numChosen++;
						if(chosenCards[a] == c)
							chosen = true;
					}
					if(!chosen) {
						if(numChosen != 3)
							chosenCards[2 - numChosen] = c;
						else
							chosenCards[0] = c;
					}
					sortAscending(chosenCards);
				}else if(x > 650 && x < 980 && y > 450 && y < 590) {
					int[] chosenCards;
					if(game.programStatus.equals("Host: in game"))
						chosenCards = game.serverChosenCards;
					else
						chosenCards = game.clientChosenCards;
					if(chosenCards[0] != -1) {
						animateChoose(ME);
						if(game.programStatus.equals("Host: in game"))
							game.gameStatus = "your turn";
						else
							game.gameStatus = "my turn";
					}
				}else {
					int[] list = {-1, -1, -1};
					if(game.programStatus.equals("Host: in game"))
						game.serverChosenCards = list;
					else
						game.clientChosenCards = list;
				}
			}else if(game.gameStatus.equals("my turn")) {
				Deck myHand;
				Deck myTopCards;
				Deck myBottomCards;
				int[] myChosen;
				if(game.programStatus.equals("Host: in game")) {
					myHand = game.serverHand;
					myTopCards = game.serverTopCards;
					myBottomCards = game.serverBottomCards;
					myChosen = game.serverChosenCards;
				}else {
					myHand = game.clientHand;
					myTopCards = game.clientTopCards;
					myBottomCards = game.clientBottomCards;
					myChosen = game.clientChosenCards;
				}
				int numTopLeft = 0;
				for(int a = 0; a < myTopCards.getHeight(); a++)
					if(myTopCards.getCard(a) instanceof BicycleCard)
						numTopLeft++;
				int numBottomLeft = 0;
				for(int a = 0; a < myBottomCards.getHeight(); a++)
					if(myBottomCards.getCard(a) instanceof BicycleCard)
						numBottomLeft++;
				if(myHand.getHeight() != 0) {
					int hw = 20 * (myHand.getHeight() - 1) + 100;
					int c = (x - (500 - hw / 2)) / 20;
					if(c >= myHand.getHeight())
						c = myHand.getHeight() - 1;
					if(x > 500 - (20 * (myHand.getHeight() - 1) + 100) / 2 && x < 500 + (20 * (myHand.getHeight() - 1) + 100) / 2 && y > 450 && y < 590) {
						boolean chosen = false;
						int numChosen = 0;
						for(int a = 0; a < myChosen.length; a++) {
							if(myChosen[a] == c)
								chosen = true;
							if(myChosen[a] != -1)
								numChosen++;
						}
						if(!chosen) {
							if(numChosen == 0) {
								myChosen[numChosen] = c;
								System.out.println("");
								for(int a : myChosen)
									System.out.println(a);
							}else {
								BicycleCard card1 = (BicycleCard) myHand.getCard(myChosen[0]);
								int value1 = card1.getValue();
								BicycleCard card2 = (BicycleCard) myHand.getCard(c);
								int value2 = card2.getValue();
								if(value1 == value2) {
									myChosen[numChosen] = c;
									System.out.println("");
									for(int a : myChosen)
										System.out.println(a);
								}else {
									int[] list = {c, -1, -1, -1};
									myChosen = list;
									if(game.programStatus.equals("Host: in game"))
										game.serverChosenCards = myChosen;
									else
										game.clientChosenCards = myChosen;
								}
							}
						}
					}else if(x > 500 - game.discardPile.getHeight() && x < 600 && y > 280 - game.discardPile.getHeight() && y < 420) {
						int myValue = 0;
						if(myChosen[0] != -1)
							myValue = ((BicycleCard) myHand.getCard(myChosen[0])).getValue();
						int deckValue = 0;
						if(game.discardPile.getHeight() != 0) {
							deckValue = ((BicycleCard) game.discardPile.getTopCard()).getValue();
							if(deckValue == 1)
								deckValue = 14;
						}
						
						if(myChosen[0] == -1) {
							animateDraw(ME);
							if(game.gameStatus.equals("my turn"))
								game.gameStatus = "your turn";
							else
								game.gameStatus = "my turn";
						}else if(myValue >= deckValue || myValue == 1 || myValue == 2 || myValue == 10)
							animatePlay(ME);
						else {
							int[] list = {-1, -1, -1, -1};
							myChosen = list;
							if(game.programStatus.equals("Host: in game"))
								game.serverChosenCards = myChosen;
							else
								game.clientChosenCards = myChosen;
						}
					}else {
						int[] list = {-1, -1, -1, -1};
						if(game.programStatus.equals("Host: in game"))
							game.serverChosenCards = list;
						else
							game.clientChosenCards = list;
					}
				}else if(numTopLeft != 0) {
					if(x > 650 && x < 970 && y > 450 && y < 590) {
						int chosenCard = (x - 650) / 110;
						if(myTopCards.getCard(chosenCard) instanceof BicycleCard) {
							BicycleCard card1 = (BicycleCard) myTopCards.getCard(chosenCard);
							int value1 = card1.getValue();
							int value2 = 0;
							if(myChosen[0] != -1) {
								BicycleCard card2 = (BicycleCard) myTopCards.getCard(myChosen[0]);
								value2 = card2.getValue();
							}
							if(value1 == value2 || value2 == 0) {
								int numChosen = 0;
								for(int a : myChosen)
									if(a != -1)
										numChosen++;
								myChosen[numChosen] = chosenCard;
								sortDescending(myChosen);
							}else {
								int[] list = {chosenCard, -1, -1, -1};
								myChosen = list;
								if(game.programStatus.equals("Host: in game"))
									game.serverChosenCards = myChosen;
								else
									game.clientChosenCards = myChosen;
							}
						}
					}else if(x > 500 - game.discardPile.getHeight() && x < 600 && y > 280 - game.discardPile.getHeight() && y < 420) {
						int myValue = 0;
						if(myChosen[0] != -1)
							myValue = ((BicycleCard) myTopCards.getCard(myChosen[0])).getValue();
						int deckValue = 0;
						if(game.discardPile.getHeight() != 0) {
							deckValue = ((BicycleCard) game.discardPile.getTopCard()).getValue();
							if(deckValue == 1)
								deckValue = 14;
						}
						
						if(myChosen[0] == -1) {
							animateDraw(ME);
							if(game.gameStatus.equals("my turn"))
								game.gameStatus = "your turn";
							else
								game.gameStatus = "my turn";
						}else if(myValue >= deckValue || myValue == 1 || myValue == 2 || myValue == 10)
							animatePlay(ME);
						else {
							int[] list = {-1, -1, -1, -1};
							myChosen = list;
							if(game.programStatus.equals("Host: in game"))
								game.serverChosenCards = myChosen;
							else
								game.clientChosenCards = myChosen;
						}
					}else {
						int[] list = {-1, -1, -1, -1};
						myChosen = list;
						if(game.programStatus.equals("Host: in game"))
							game.serverChosenCards = myChosen;
						else
							game.clientChosenCards = myChosen;
					}
				}else if(numBottomLeft != 0) {
					if(x > 650 && x < 970 && y > 450 && y < 590) {
						int chosenCard = (x - 650) / 110;
						if(myBottomCards.getCard(chosenCard) instanceof BicycleCard) {
							myChosen[0] = chosenCard;
							animatePlay(ME);
						}
					}
				}
			}
		}
		
	}
	
	static final int ME = 0;
	static final int YOU = 1;
	
	void animateChoose(int chooser) {
		logger.log("Player " + chooser + " chose their cards");
		int[] chosenCards;
		Deck hand;
		Deck topCards;
		int y;
		if(chooser == ME) {
			if(game.programStatus.equals("Host: in game")) {
				chosenCards = game.serverChosenCards;
				hand = game.serverHand;
				topCards = game.serverTopCards;
			}else {
				chosenCards = game.clientChosenCards;
				hand = game.clientHand;
				topCards = game.clientTopCards;
			}
			y = 450;
			game.messageHandler.sendMessage(InGameMessageHandler.CHOSEN, chosenCards);
		}else {
			if(game.programStatus.equals("Host: in game")) {
				chosenCards = game.clientChosenCards;
				hand = game.clientHand;
				topCards = game.clientTopCards;
			}else {
				chosenCards = game.serverChosenCards;
				hand = game.serverHand;
				topCards = game.serverTopCards;
			}
			y = 100;
		}
		for(int a = 0; a < chosenCards.length; a++) {
			int b = chosenCards[a];
			BicycleCard card = (BicycleCard) hand.popCard(b - a);
			card.setFaceUp(true);
			startSlide(card, 390 + 20 * b, y, 650 + 110 * a, y, () -> topCards.stackCard(card));
		}
		
		int[] list = {-1, -1, -1, -1};
		if(game.programStatus.equals("Host: in game"))
			game.serverChosenCards = list;
		else
			game.clientChosenCards = list;
	}
	
	void sortAscending(int[] arr) {
		boolean j = true;
		while(j) {
			j = false;
			for(int a = 0; a < arr.length - 1; a++) {
				if(arr[a] > arr[a + 1]) {
					int b = arr[a];
					arr[a] = arr[a + 1];
					arr[a + 1] = b;
					j = true;
				}
			}
		}
	}
	
	void sortDescending(int[] arr) {
		boolean j = true;
		while(j) {
			j = false;
			for(int a = 0; a < arr.length - 1; a++) {
				if(arr[a] < arr[a + 1]) {
					int b = arr[a];
					arr[a] = arr[a + 1];
					arr[a + 1] = b;
					j = true;
				}
			}
		}
	}
	
	void sortHand(Deck hand) {
		boolean j = true;
		while(j) {
			j = false;
			for(int a = 0; a < hand.getHeight() - 1; a++) {
				BicycleCard card1 = (BicycleCard) hand.getCard(a);
				BicycleCard card2 = (BicycleCard) hand.getCard(a + 1);
				int value1 = card1.getValue(), suit1 = card1.getSuit();
				int value2 = card2.getValue(), suit2 = card2.getSuit();
				if(value1 == 10)
					value1 = 14;
				if(value1 == 2)
					value1 = 15;
				if(value1 == 1)
					value1 = 16;
				
				if(value2 == 10)
					value2 = 14;
				if(value2 == 2)
					value2 = 15;
				if(value2 == 1)
					value2 = 16;
				if(value1 > value2 || (value1 == value2 && suit1 > suit2)) {
					hand.insertCard(a, hand.popCard(a + 1));
				}
			}
		}
	}
	
	void animateClear() {
		logger.log("clearing deck");
		new Thread(() -> {
			while(game.discardPile.getHeight() != 0) {
				BicycleCard card = (BicycleCard) game.discardPile.popTopCard();
				startSlide(card, 500 - game.discardPile.getHeight(), 280 - game.discardPile.getHeight(), 100 - game.gonePile.getHeight(), 460 - game.gonePile.getHeight(), () -> game.gonePile.stackCard(card));
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	void animateDraw(int drawer) {
		logger.log("Player " + drawer + " drawing the deck");
		if(game.discardPile.getHeight() == 0)
			return;
		new Thread(() -> {
			Deck hand;
			int y;
			if(drawer == ME) {
				if(game.programStatus.equals("Host: in game")) {
					hand = game.serverHand;
				}else {
					hand = game.clientHand;
				}
				y = 450;
				game.messageHandler.sendMessage(InGameMessageHandler.DRAW, new int[0]);
			}else {
				if(game.programStatus.equals("Host: in game")) {
					hand = game.clientHand;
				}else {
					hand = game.serverHand;
				}
				y = 100;
			}
			while(game.discardPile.getHeight() != 0) {
				BicycleCard card = (BicycleCard) game.discardPile.popTopCard();
				startSlide(card, 500 - game.discardPile.getHeight(), 280 - game.discardPile.getHeight(), 500, y, () -> hand.stackCard(card));
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	void animatePlay(int player) {
		logger.log("Player " + player + " playing their cards");
		Deck hand;
		Deck topCards;
		Deck bottomCards;
		int[] chosenCards;
		int y;
		if(player == ME) {
			if(game.programStatus.equals("Host: in game")) {
				hand = game.serverHand;
				topCards = game.serverTopCards;
				bottomCards = game.serverBottomCards;
				chosenCards = game.serverChosenCards;
			}else {
				hand = game.clientHand;
				topCards = game.clientTopCards;
				bottomCards = game.clientBottomCards;
				chosenCards = game.clientChosenCards;
			}
			y = 450;
			game.messageHandler.sendMessage(InGameMessageHandler.MYTURN, chosenCards);
		}else {
			if(game.programStatus.equals("Host: in game")) {
				hand = game.clientHand;
				topCards = game.clientTopCards;
				bottomCards = game.clientBottomCards;
				chosenCards = game.clientChosenCards;
			}else {
				hand = game.serverHand;
				topCards = game.serverTopCards;
				bottomCards = game.serverBottomCards;
				chosenCards = game.serverChosenCards;
			}
			y = 100;
		}
		sortDescending(chosenCards);
		int numTopLeft = 0;
		for(int a = 0; a < topCards.getHeight(); a++)
			if(topCards.getCard(a) instanceof BicycleCard)
				numTopLeft++;
		int numBottomLeft = 0;
		for(int a = 0; a < bottomCards.getHeight(); a++)
			if(bottomCards.getCard(a) instanceof BicycleCard)
				numBottomLeft++;
		if(hand.getHeight() != 0) {
			int hw = 20 * (hand.getHeight() - 1) + 100;
			int a = 0;
			while(a < 4 && chosenCards[a] != -1) {
				BicycleCard card = (BicycleCard) hand.popCard(chosenCards[a]);
				card.setFaceUp(true);
				startSlide(card, 500 - hw / 2 + 20 * chosenCards[a], y, 500 - game.discardPile.getHeight(), 280 - game.discardPile.getHeight(), () -> {
					game.discardPile.stackCard(card);
						int value = card.getValue();
						boolean four = true;
						for(int d = game.discardPile.getHeight() - 1; d >= game.discardPile.getHeight() - 4; d--) {
							if(d >= 0) {
								if(((BicycleCard) game.discardPile.getCard(d)).getValue() != value)
									four = false;
							}else
								four = false;
						}
						if(value == 10 || four)
							animateClear();
				});
				a++;
			}
			reloadHand(player);
		}else if(numTopLeft != 0) {
			int a = 0;
			while(chosenCards[a] != -1 && a != 3 && topCards.getCard(chosenCards[a]) instanceof BicycleCard) {
				BicycleCard card = (BicycleCard) topCards.popCard(chosenCards[a]);
				topCards.insertCard(chosenCards[a], new Card());
				card.setFaceUp(true);
				startSlide(card, 650 + 110 * chosenCards[a], y, 500 - game.discardPile.getHeight(), 280 - game.discardPile.getHeight(), () -> {
					game.discardPile.stackCard(card);
					int value = card.getValue();
					
					
					boolean four = true;
					for(int d = game.discardPile.getHeight() - 1; d >= game.discardPile.getHeight() - 4; d--) {
						if(d >= 0) {
							if(((BicycleCard) game.discardPile.getCard(d)).getValue() != value)
								four = false;
						}else
							four = false;
					}
					if(value == 10 || four)
						animateClear();
				});
				a++;
			}
		}else if(numBottomLeft != 0){
			BicycleCard card = (BicycleCard) bottomCards.popCard(chosenCards[0]);
			bottomCards.insertCard(chosenCards[0], new Card());
			card.setFaceUp(true);
			startSlide(card, 650 + 110 * chosenCards[0], y, 500 - game.discardPile.getHeight(), 280 - game.discardPile.getHeight(), () -> {
				int topValue = 0;
				if(game.discardPile.getHeight() != 0)
					topValue = ((BicycleCard) game.discardPile.getTopCard()).getValue();
				game.discardPile.stackCard(card);
				int value = ((BicycleCard) game.discardPile.getTopCard()).getValue();
				int realValue = value;
				if(value == 1 || value == 2 || value == 10)
					value = 14;
				if(topValue == 1 || topValue == 10)
					topValue = 14;
				if(value < topValue)
					animateDraw(player);
				
				boolean four = true;
				for(int d = game.discardPile.getHeight() - 1; d >= game.discardPile.getHeight() - 4; d--) {
					if(d >= 0) {
						if(((BicycleCard) game.discardPile.getCard(d)).getValue() != realValue)
							four = false;
					}else
						four = false;
				}
				if(realValue == 10 || four)
					animateClear();
			});
		}
		int[] list = {-1, -1, -1, -1};
		if(game.gameStatus.equals("my turn")) {
			game.gameStatus = "your turn";
			if(game.programStatus.equals("Host: in game"))
				game.serverChosenCards = list;
			else
				game.clientChosenCards = list;
		}else {
			game.gameStatus = "my turn";
			if(game.programStatus.equals("Host: in game"))
				game.clientChosenCards = list;
			else
				game.serverChosenCards = list;
		}
	}
	
	void animateWin(int winner) {
		logger.log("Player " + winner + " winning");
		new Thread(() -> {
			startConfetti(500, 700, -90, 30);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Confetti conf = confetti.remove(0);
			double x = conf.x;
			double y = conf.y;
			for(int a = 0; a < 2; a++) {
				for(int theta = 0; theta > -2*360; theta-=10) {
					startConfetti(x, y, theta, r.nextInt(20));
					if(a == 0) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			try {
				Thread.sleep(1000);
			}catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			ArrayList<String> msg= new ArrayList<>();
			if(winner == ME) {
				msg.add("Congrats!");
				msg.add("You won Palm Tree!");
			}else {
				msg.add("Sorry...");
				msg.add("You lost.");
				msg.add("Try again next time");
			}
			game.show(msg, game.BU_NOTHING);
		}).start();
	}
	
	void reloadHand(int reloadee) {
		logger.log("Player " + reloadee + " reloading their hand");
		Deck hand;
		int y;
		if(reloadee == ME) {
			if(game.programStatus.equals("Host: in game"))
				hand = game.serverHand;
			else
				hand = game.clientHand;
			y = 450;
		}else {
			if(game.programStatus.equals("Host: in game"))
				hand = game.clientHand;
			else
				hand = game.serverHand;
			y = 100;
		}
		if(hand.getHeight() < 3 && game.drawPile.getHeight() != 0) {
			BicycleCard card = (BicycleCard) game.drawPile.popTopCard();
			startSlide(card, 100 - game.drawPile.getHeight(), 100 - game.drawPile.getHeight(), 450, y, () -> {
				hand.stackCard(card);
				if(hand.getHeight() < 3)
					reloadHand(reloadee);
			});
		}
	}
	
	void checkWinner() {
		if(slides.size() == 0 && game.serverHand != null && game.clientHand != null && !game.gameStatus.equals("dealing") && !game.gameStatus.equals("waiting") && confetti.size() == 0) {
			Deck bottomCards;
			Deck hand;
			int bottomCardsLeft;
			
			bottomCards = game.serverBottomCards;
			hand = game.serverHand;
			bottomCardsLeft = 0;
			for(int a = 0; a < bottomCards.getHeight(); a++)
				if(bottomCards.getCard(a) instanceof BicycleCard)
					bottomCardsLeft++;
			if(bottomCardsLeft == 0 && hand.getHeight() == 0) {
				if(game.programStatus.equals("Host: in game"))
					animateWin(ME);
				else
					animateWin(YOU);
			}
			
			bottomCards = game.clientBottomCards;
			hand = game.clientHand;
			bottomCardsLeft = 0;
			for(int a = 0; a < bottomCards.getHeight(); a++)
				if(bottomCards.getCard(a) instanceof BicycleCard)
					bottomCardsLeft++;
			if(bottomCardsLeft == 0 && hand.getHeight() == 0) {
				if(game.programStatus.equals("Host: in game"))
					animateWin(YOU);
				else
					animateWin(ME);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String title = "Palm Tree - " + game.programStatus + " - " + game.gameStatus;
		if(game.programStatus.equals("in game"))
			title+=game.gameStatus;
		frame.setTitle(title);
		if(game.clientHand != null)
			sortHand(game.clientHand);
		if(game.serverHand != null)
			sortHand(game.serverHand);
		
			
		repaint();
	}
	
	void startSlide(BicycleCard card, int fromX, int fromY, int toX, int toY, Runnable onArrival) {
		slides.add(new CardSlide(card, fromX, fromY, toX, toY, onArrival));
	}
	
	void startConfetti(double x, double y, int theta, int vel) {
		confetti.add(new Confetti(x, y, theta, vel));
	}


}

class CardSlide{
	int timer;
	BicycleCard card;
	int fromX;
	int fromY;
	int toX;
	int toY;
	Runnable onArrival;
	
	CardSlide(BicycleCard card, int fromX, int fromY, int toX, int toY, Runnable onArrival) {
		timer = 0;
		this.card = card;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		this.onArrival = onArrival;
	}
	
}

class Confetti{
	int timer;
	double x;
	double y;
	double yVel, xVel;
	int vel;
	int theta;
	
	Confetti(double x2, double y2, int theta, int vel) {
		timer = 0;
		this.x = x2;
		this.y = y2;
		this.theta = theta;
		this.vel = vel;
		xVel = vel * Math.cos(Math.toRadians(theta));
		yVel = vel * Math.sin(Math.toRadians(theta));
	}
	
}