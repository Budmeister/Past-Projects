package thePackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import thePackage.Cards.Stack;

import static thePackage.GlobalVariables.frameHandler;
import static thePackage.GlobalVariables.machiKoroGame;
import static thePackage.GlobalVariables.messageHandler;

public abstract class MachiKoroPlayer extends LoggingObject implements Paintable, ImageUpdatable, Pressable, MessageReceiver{
	
	protected TrainStation ts;
	protected ShoppingMall sm;
	protected AmusementPark ap;
	protected RadioTower rt;
	public int number;
	protected String pos;
	private boolean centerPriority;
	private String name;
	private Cards cards;
	private Coins coins;
	private Paintable logo;
	protected BufferedImage logoImage;
	
	protected MachiKoroPlayer(int num, boolean centerPriority) {
		s = new Status("Player " + num, "created");
		l = new Logger(s);
		number = num;
		this.centerPriority = centerPriority; pos = "south";
		ts = new TrainStation();
		sm = new ShoppingMall();
		ap = new AmusementPark();
		rt = new RadioTower();
		cards = new Cards(this);
		coins = new Coins(this);
		incCoins(3);
		name = "";
		logo = new Paintable() {
			public void paintIt(Graphics g) {
				Point p = LocationCoordinates.getCoordinate(pos, "Logo");
				if(logoImage != null) {
					int mx = p.x + 44, my = p.y + 44;
					Point mp = new Point(mx, my);
					FrameHandler.rotate(g, pos, mp);
					g.drawImage(logoImage, p.x, p.y, 88, 88, null);
					FrameHandler.rotateBack(g, pos, mp);
				}
			}
			@Override
			public boolean shouldRemove() {
				return false;
			}
		};
	}
	
	/**
	 * This method does all the actions required to take this person's turn. This method blocks until the turn
	 * is over. 
	 * @throws InterruptedException if this thread is interrupted before the player's turn is over. This 
	 * exception clears the interrupted status of the thread. 
	 */
	public abstract void takeTurn() throws InterruptedException;
	
	
	public String getPosition() {
		return pos;
	}
	
	/**
	 * Sets the position on the screen. Valid positions: {@code "north"}, {@code "south"}, {@code "east"}, or {@code "west"}.
	 * @param pos the position to set.
	 * @throws IllegalArgumentException if the inputed string is not valid.
	 */
	public void setPosition(String pos) throws IllegalArgumentException{
		if(!pos.equals("north") && !pos.equals("south") && !pos.equals("east") && !pos.equals("west"))
			throw new IllegalArgumentException("This method can only take \"north\", \"south\", \"east\" or \"west\"");
		this.pos = pos;
		cards.setPosition(pos);
	}
	
//	/**
//	 * Adds an establishment of the given type to this player's card stack without animating it.
//	 * @param type
//	 */
//	public void addEstablishment(String type) {
//		cards.add(type);
//	}
//	
//	/**
//	 * Removes an establishment of the given type from this player's card stack without animating it. If no such establishment exists, nothing happens.
//	 * @param type
//	 */
//	public void removeEstablishment(String type) {
//		cards.remove(type);
//	}
	
	public TrainStation getTrainStation() {
		return ts;
	}
	
	public ShoppingMall getShoppingMall() {
		return sm;
	}
	
	public AmusementPark getAmusementPark() {
		return ap;
	}
	
	public RadioTower getRadioTower() {
		return rt;
	}
	
//	public void animateAddMoneyFromBank(int inc) {
//		frame.animateMoneyTransfer(pos, "bank", inc, () -> money+=inc);
//	}
//	
//	public void animateAddMoneyFromLocation(String loc, int inc) {
//		frame.animateMoneyTransfer(pos, loc, inc, () -> money+=inc);
//	}
//	
//	public void animateAddEstablishmentFromBank(String type) {
//		frame.animateEstablishmentTransfer(pos, "bank", type, () -> addEstablishment(type));
//	}
//	
//	public void animateAddEstablishmentFromLocation(String loc, String type) {
//		frame.animateEstablishmentTransfer(pos, loc, type, () -> addEstablishment(type));
//	}
	
//	public void addMoney(int inc) {
//		money+=inc;
//	}
//	
//	public int stealMoney(int dec) {
//		if(money < dec) {
//			int temp = money;
//			money = 0;
//			return temp;
//		}else {
//			money-=dec;
//			return dec;
//		}
//	}
	
	/**
	 * This method transfers coins from the bank to this player's {@code
	 * Coins} object and animates it. This method does not block.
	 * @param amount the amount to transfer.
	 */
	public void addCoins(int amount) {
		l.log("Adding " + amount + " coins from bank");
		frameHandler.bankCoinsObject().slideAway(pos, amount);
	}
	
	/**
	 * This method increments the number of coins this player's {@code Coins}
	 * object has by {@code amount} and doesn't animate it. 
	 * @param amount the amount to increment. 
	 */
	public void incCoins(int amount) {
		coins.add(amount);
	}
	
	/**
	 * This method transfers coins from this player's {@code Coins} object
	 * to another player's and animates it. This method does not block.
	 * @param amount the amount to transfer.
	 * @param newPos the position to put the money to.
	 */
	public void stealCoins(int amount, String newPos) {
		coins.slideAway(newPos, amount);
	}
	
	/**
	 * This method transfers an establishment to this player's {@code Cards}
	 * object from the bank and animates it. This method does not block.
	 * @param est
	 */
	public void addEstablishment(String est) {
		frameHandler.bankCardsObject().slideAway(est, pos);
	}
	
	/**
	 * This method transfers an establishment from this player's {@code Cards}
	 * object to another player's and animates it. This method does not block.
	 * @param est
	 * @param newPos
	 */
	public void stealEstablishment(String est, String newPos) {
		cards.slideAway(est, newPos);
	}
	
	public void setGame(MachiKoroGame g) {
		machiKoroGame = g;
	}
	
	public void setFrame(FrameHandler f) {
		frameHandler = f;
	}
	
	public void setMessenger(MessageHandler m) {
		messageHandler = m;
	}

	@Override
	public void paintIt(Graphics g) {
		//GMK create paintIt for MachiKoroPlayer
		cards.paintIt(g);
		coins.paintIt(g);
		logo.paintIt(g);
		paintLandmarks(g);
	}
	
	private void paintLandmarks(Graphics g) {
		MachiKoroLandmark[] landmarks = {ts, sm, ap, rt};
		for(int a = 0; a < landmarks.length; a++) {
			MachiKoroLandmark l = landmarks[a];
			BufferedImage img;
			
			String pos;
			switch(this.pos) {
			case "north":
				pos = "south";
				break;
			case "south":
				pos = "north";
				break;
			case "east":
				pos = "west";
				break;
			case "west":
				pos = "east";
				break;
			default:
				pos = "";
			}
			
			if(l.isOpen())
				img = CardImages.getImage(l.name + " - open - " + pos + ".png");
			else
				img = CardImages.getImage(l.name + " - closed - " + pos + ".png");
			Point p = LocationCoordinates.getCoordinate(this.pos, l.name);
			int w, h;
			if(pos.equals("north") || pos.equals("south")) {
				w = 60;
				h = 90;
			}else {
				w = 90;
				h = 60;
			}
			g.drawImage(img, p.x, p.y, w, h, null);
		}
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}

	/**
	 * @return the centerPriority
	 */
	public boolean hasCenterPriority() {
		return centerPriority;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public int getNumCardsType(String type) {
		return cards.getNumCardsType(type);
	}
	

	@Override
	public void receive(Message m) {
		// GMK create MachiKoroPlayer.recieve(Message m)
		Message.Type type = m.getType();
		switch(type) {
		case BEGIN:
			break;
		case BUY:
			break;
		case BYE:
			break;
		case CHECK_UP:
			break;
		case ERROR:
			break;
		case ITS_YOUR_TURN:
			break;
		case NEW_CONNECTION:
			break;
		case PLAYER_KICKED:
			break;
		case PLAYER_WON:
			break;
		case ROGER:
			break;
		case ROLL:
			break;
		case TRADE:
			break;
		case WELCOME:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void press(int mouseX, int mouseY) {
		if(cards != null)
			cards.press(mouseX, mouseY);
	}
	
	@Override
	public void release(int mouseX, int mouseY) {
		if(cards != null)
			cards.release(mouseX, mouseY);
	}
	
	public void updateImage(int mouseX, int mouseY) {
		if(cards != null)
			cards.updateImage(mouseX, mouseY);
	}

	/**
	 * @return the coins object of this player.
	 */
	public Coins getCoins() {
		return coins;
	}
	
	public Cards getCards() {
		return cards;
	}
	
	protected void processRoll(int roll, Runnable r) {
		cards.processColor("green", roll, this, r);
		cards.processColor("blue", roll, this, r);
	}
}

abstract class MachiKoroBot extends MachiKoroPlayer{
	
	private String botType, name;
	public MachiKoroBot(int num, String botType) {
		super(num, false); this.botType = botType; this.logoImage = CardImages.getImage("bot_logo.png");
	}
	
	//GMK create MachiKoroBot.createRandomBot(int)
	public static MachiKoroBot createRandomBot(int num) {
		MachiKoroBot bot = null;
		int r = (int) (getNumBotTypes() * Math.random());
		switch(r) {
		case 0:
			bot = new CopyingBot(num);
			break;
		case 1:
			bot = new OneItemBot(num);
			break;
		case 2:
			bot = new HoleInPocketBot(num);
			break;
		case 3:
			bot = new DontBuyBot(num);
		}
		return bot;
	}
	
	//GMK create MachiKoroBot.getBots()
	public static String[] getBotNames() {
		String[] names = {"Steve",
				"Kevin",
				"Alice",
				"Annie"};
		return names;
	}
	
	public static int getNumBotTypes() {
		return 4;
	}

	/**
	 * @return the botType
	 */
	public String getBotType() {
		return botType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}

class CopyingBot extends MachiKoroBot{

	public CopyingBot(int num) {
		super(num, "CopyingBot");
	}

	@Override
	public void takeTurn() {
		
	}
	
}

class OneItemBot extends MachiKoroBot{

	public OneItemBot(int num) {
		super(num, "OneItemBot");
	}

	@Override
	public void takeTurn() {
		
	}
	
}

class HoleInPocketBot extends MachiKoroBot{

	public HoleInPocketBot(int num) {
		super(num, "HoleInPocketBot");
	}

	@Override
	public void takeTurn() {
		
	}
	
}

class DontBuyBot extends MachiKoroBot{

	public DontBuyBot(int num) {
		super(num, "DontBuyBot");
	}

	@Override
	public void takeTurn() {
		
	}
	
}

//GMK Create RemotePlayer class
class RemotePlayer extends MachiKoroPlayer{
	
	private Connection connection;

	public RemotePlayer(int num, Connection c) {
		super(num, false);
		connection = c;
		logoImage = CardImages.getImage("remote_logo.png");
	}

	@Override
	public void takeTurn() {
		
	}
	
	public void kick() {
		GlobalVariables.messageHandler.kick(connection);
		GlobalVariables.machiKoroGame.getPlayers().remove(this);
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {Thread.currentThread();
		return connection;
	}

	static class ReceptionNotSupportedException extends RuntimeException{
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 123308693923259529L;
		
	}
	
	@Override
	public void receive(Message m) {
		throw new ReceptionNotSupportedException();
	}
	
}

//GMK Create LocalPlayer class
class LocalPlayer extends MachiKoroPlayer{
	
	private RoundButton roll1Button, roll2Button;
	private int roll = -1;

	public LocalPlayer(int num) {
		super(num, true);
		logoImage = CardImages.getImage("keyboard_logo.png");
		roll1Button = new RoundButton("green", "thin", FrameHandler.width / 2 - 200, FrameHandler.height / 2 + 150, 100, 50); roll1Button.setText("Roll 1");
		roll1Button.addActionListener(ae -> new Thread(() -> {
			try {
				roll1Button.setVisible(false);
				roll2Button.setVisible(false);
				roll = frameHandler.rollDie(false, pos);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start());
		roll1Button.setVisible(false);
		
		roll2Button = new RoundButton("blue", "thin", FrameHandler.width / 2 + 200, FrameHandler.height / 2 + 150, 100, 50); roll2Button.setText("Roll 2");
		roll2Button.addActionListener(ae -> new Thread(() -> {
			try {
				roll1Button.setVisible(false);
				roll2Button.setVisible(false);
				roll = frameHandler.rollDie(true, pos);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start());
		roll2Button.setVisible(false);
	}
	
	String formatArray(Object[] arr) {
		String string = "[";
		for(int a = 0; a < arr.length; a++)
			if(arr[a] instanceof Object[])
				string+=formatArray((Object[]) arr[a]) + (a == arr.length - 1 ? "]\n" : ",");
			else
				string+=arr[a] + (a == arr.length - 1 ? "]\n" : ",");
		return string;
	}
	
	void printArray(Object[] arr) {
		l.log(formatArray(arr));
	}
	
	void processTest(int roll) throws InterruptedException {
		int numPlayers = machiKoroGame.getPlayers().size();
		Cards[] cards = new Cards[numPlayers];
		for(int a = 0; a < numPlayers; a++)
			cards[a] = machiKoroGame.getPlayers().get(a).getCards();
		
		String[] colors = {"red", "green", "blue", "purple"};
		Stack[][][] stacks = new Stack[colors.length][numPlayers][];
		/* The stacks matrix looks like this:
		 * {{{stack, stack, stack},      // player 1
		 * {stack, stack},				 // player 2	// color 0
		 * {stack, stack, stack, stack}},// player 3
		 * 
		 * {{stack, stack, stack},       // player 1
		 * {stack, stack},				 // player 2	// color 1
		 * {stack, stack, stack, stack}},// player 3
		 * 
		 * {{stack, stack, stack},       // player 1
		 * {stack, stack},				 // player 2	// color 2
		 * {stack, stack, stack, stack}},// player 3
		 * 
		 * {{stack, stack, stack},       // player 1
		 * {stack, stack},				 // player 2	// color 3
		 * {stack, stack, stack, stack}}}// player 3
		 * 
		 * or
		 * 
		 * stack = stacks[colorNumber][playerNumber][establishmentNumber]
		 * 						^			^				^
		 * 						|			|				|
		 * 		   using the colors array   |				|
		 * 									|				|
		 * 							the player's number		|
		 * 													|
		 * 		the number representing the position of the establishment in the player's list of establishments
		 */
		for(int colorNumber = 0; colorNumber < stacks.length; colorNumber++) {
			Stack[][] color = stacks[colorNumber];
			for(int playerNumber = 0; playerNumber < numPlayers; playerNumber++) {
				int numIn = 0;
				for(Stack s : cards[playerNumber].getStacks())
					if(s.getEstablishment().getColor().equals(colors[colorNumber]))
						numIn++;
				color[playerNumber] = new Stack[numIn];
				int establishmentOfColorNumber = 0;
				for(int establishmentNumber = 0; establishmentNumber < cards[playerNumber].getStacks().size(); establishmentNumber++)
					if(cards[playerNumber].getStacks().get(establishmentNumber).getEstablishment().getColor().equals(colors[colorNumber]))
						color[playerNumber][establishmentOfColorNumber++] = cards[playerNumber].getStacks().get(establishmentNumber);
			}
		}
		
		long delay = 1500;
		for(int colorNumber = 0; colorNumber < stacks.length; colorNumber++) {
			int playerNumber = machiKoroGame.getTurn();
			do {
				for(int establishmentNumber = 0; establishmentNumber < stacks[colorNumber][playerNumber].length; establishmentNumber++) {
					Stack stack = stacks[colorNumber][playerNumber][establishmentNumber];
					Establishment e = stack.getEstablishment();
					boolean isTheRightNumber = false;
					for(int num : e.getActivationNums())
						if(num == roll)
							isTheRightNumber = true;
					if(isTheRightNumber && (colors[colorNumber].equals("blue") || (colors[colorNumber].equals("green") || colors[colorNumber].equals("purple") && playerNumber == this.number) || (colors[colorNumber].equals("red") && playerNumber != this.number))) {
						stack.act(this);
						delay+=500 * stack.getNumCards();
					}
				}
				playerNumber++;
				if(playerNumber == machiKoroGame.getPlayers().size())
					playerNumber = 0;
			} while (playerNumber != machiKoroGame.getTurn());
//			if(delay != 1500)
				Thread.sleep(delay);
			delay = 1500;
			if(colorNumber < 4)
				frameHandler.stageDisplayer.nextStage();
		}
	}
	
	/*
	 * Notes:
	 * 1. Red is not working - the condition that you only give money if it is your turn is reversed, so now
	 * you only get money if it is your turn, which never actually activates the red cards.
	 * 2. The convenience store seems to be always incremented as if the shopping mall is open even when it's
	 * not. 
	 * 3. There are other misc. discrepancies concerning the number of coins given. 
	 * 4. Right now, the above method(processTest) is being used. All of the notes are concerning it.
	 * (non-Javadoc)
	 * @see thePackage.MachiKoroPlayer#takeTurn()
	 */
	
	@Override
	public void takeTurn() throws InterruptedException {
		if(ts.isOpen())
			roll2Button.setVisible(true);
		roll1Button.setVisible(true);
		while(roll == -1)
			if(Thread.interrupted())
				throw new InterruptedException();
		frameHandler.stageDisplayer.nextStage();
		frameHandler.stageDisplayer.waitForSlide();
		
		class IntegerHolder{
			int value;
			public IntegerHolder(int initValue) { value = initValue; }
		}
		
		IntegerHolder numActionsCompleted = new IntegerHolder(0);
		Runnable inc = () -> {
			numActionsCompleted.value++;
		};
//		frameHandler.processRoll(roll, this, inc);
//		processRoll(roll, inc);
		processTest(roll);
//		while(numActionsCompleted.value < 2)
//			if(Thread.interrupted())
//				throw new InterruptedException();
		l.log("Processed a " + roll);
		roll = -1;
//		frameHandler.stageDisplayer.nextStage();
		frameHandler.stageDisplayer.waitForSlide();
		
//		while(frameHandler.coinSlides.size() > 1)
//			if(Thread.currentThread().isInterrupted())
//				throw new InterruptedException();
		
		frameHandler.establishmentMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int price = 0;
				if(ae.getID() != -1) {
					price = Establishments.getEstablishment(ae.getActionCommand()).getPrice();
					if(price > getCoins().getValue())
						return;
				}
				frameHandler.establishmentMenu.removeActionListener(this);
				if(ae.getID() == -1)
					return;
				frameHandler.bankCardsObject().slideAway(ae.getActionCommand(), pos);
				stealCoins(price, "bank");
				frameHandler.establishmentMenu.close();
			}
		});
		frameHandler.establishmentMenu.choose();
		while(frameHandler.establishmentMenu.isOpen())
			if(Thread.interrupted())
				throw new InterruptedException();
		
		
		
		
		frameHandler.stageDisplayer.nextStage();
	}
	
	@Override
	public void paintIt(Graphics g) {
		super.paintIt(g);
		roll1Button.paintIt(g);
		roll2Button.paintIt(g);
	}
	
	@Override
	public void press(int mouseX, int mouseY) {
		super.press(mouseX, mouseY);
		roll1Button.press(mouseX, mouseY);
		roll2Button.press(mouseX, mouseY);
	}
	
	@Override
	public void release(int mouseX, int mouseY) {
		super.release(mouseX, mouseY);
		roll1Button.release(mouseX, mouseY);
		roll2Button.release(mouseX, mouseY);
	}
	
	@Override
	public void updateImage(int mouseX, int mouseY) {
		super.updateImage(mouseX, mouseY);
		roll1Button.updateImage(mouseX, mouseY);
		roll2Button.updateImage(mouseX, mouseY);
	}
	
	@Override
	public void receive(Message m) {
		Message.Type type = m.getType();
		MessageInfo info = m.getInfo();
		switch(type) {
		case BEGIN:
			if(messageHandler.getState() == MessageHandler.State.CLIENT)
				machiKoroGame.receive(m);
			break;
		case BUY:
			break;
		case CHECK_UP:
			if(messageHandler.getState() == MessageHandler.State.CLIENT)
				machiKoroGame.receive(m);
			break;
		case ERROR:
			break;
		case ITS_YOUR_TURN:
			break;
		case NEW_CONNECTION:
			if(messageHandler.getState() == MessageHandler.State.CLIENT)
				machiKoroGame.receive(m);
			break;
		case PLAYER_WON:
			break;
		case ROGER:
			break;
		case ROLL:
			break;
		case TRADE:
			break;
		case WELCOME:
			break;
		default:
			break;
		
		}
	}
	
}

class Coins implements Paintable{
	
//	class CoinSlide implements Paintable, Runnable{
//		
//		class Coin implements Paintable{
//			private Point p;
//			private Point slidingTo;
//			private int timer;
//			private int time = 20;
//			private int type;
//			private Runnable onArrival;
//			public Coin(Point p, int type) {
//				this.p = p;
//				this.type = type;
//			}
//			
//			public void slideTo(Point p) {
//				this.slidingTo = p;
//				timer = 0;
//			}
//			
//			public void slideTo(Point p, Runnable oa) {
//				slideTo(p);
//				onArrival = oa;
//			}
//			
//			public void paintIt(Graphics g) {
//				if(timer == time) {
//					onArrival.run();
//					slidingTo = null;
//					timer = 0;
//				}
//				if(slidingTo != null) {
//					timer++;
//					g.drawImage(images[type], p.x + (timer / time) * (slidingTo.x - p.x), p.y + (timer / time) * (slidingTo.y - p.y), null);
// 				}else {
//					g.drawImage(images[type], p.x, p.y, widths[type], widths[type], null);
//				}
//			}
//			
//			public boolean shouldRemove() {
//				return p.equals(slidingTo);
//			}
//		}
//		
//		private ArrayList<Coin> coins;
//		private String pos2;
//		
//		public CoinSlide(int[] coins, String to) {
//			pos2 = to;
//			this.coins = new ArrayList<>();
//			for(int a = 0; a < coins.length; a++)
//				for(int b = 0; b < coins[a]; b++)
//					this.coins.add(new Coin(getRandomPoint(getPosition()), coinTypes[a]));
//		}
//		
//		private final int delayTime = 200;
//		
//		@Override
//		public void run() {
//			for(Coin c : coins) {
//				c.slideTo(getRandomPoint2(getPosition()));
//				try {
//					Thread.sleep(delayTime);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//				}
//			}
//			for(Coin c : coins) {
//				c.slideTo(getRandomPoint2(pos2));
//				try {
//					Thread.sleep(delayTime);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//				}
//			}
//			for(Coin c : coins) {
//				c.slideTo(getRandomPoint(pos2), () -> GlobalVariables.machiKoroGame.getPlayer(pos2).getCoins().add(c.type));
//				try {
//					Thread.sleep(delayTime);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//				}
//			}
//		}
//		
//		private Point getRandomPoint2(String pos) {
//			Point p = getRandomPoint(pos);
//			int dx = 0, dy = 0;
//			switch(getPosition()) {
//			case "north":
//				dy = 100;
//				break;
//			case "south":
//				dy = -100;
//				break;
//			case "east":
//				dx = -100;
//				break;
//			case "west":
//				dx = 100;
//			}
//			p.x+=dx;
//			p.y+=dy;
//			return p;
//		}
//		
//		@Override
//		public void paintIt(Graphics g) {
//			for(Coin c : coins)
//				c.paintIt(g);
//		}
//
//		@Override
//		public boolean shouldRemove() {
//			return false;
//		}
//		
//	}
	
	class Coin implements Paintable {
		
		private Slide s;
		private int amount;
		
		public Coin(Slide s, int amount) {
			this.s = s;
			this.amount = amount;
		}
		
		public Coin(int amount) {
			this.amount = amount;
		}
		
		public void setSlideObject(Slide s) {
			this.s = s;
		}
		
		double border = 1.1;
		
		@Override
		public void paintIt(Graphics g) {
			if(s != null) {
				g.setColor(Color.white);
				g.fillOval((int) (s.x - (border - 1) * s.width / 2), (int) (s.y - (border - 1) * s.height / 2), (int) (s.width * border), (int) (s.height * border));
				g.drawImage(getImage(), s.getX(), s.getY(), s.getWidth(), s.getHeight(), null);
			}
		}

		@Override
		public boolean shouldRemove() {
			return false;
		}
		
		private BufferedImage getImage() {
			return images[amount];
		}
		
	}
	
	private Slide createCoinSlide(int amount) throws IllegalArgumentException{
		if(amount != 1 && amount != 5 && amount != 10)
			throw new IllegalArgumentException("The amount must be 1, 5, or 10; amount: " + amount);
		Point p = getMidwayPoint(getPosition());
		Coin c = new Coin(amount);
		Slide s = new Slide(c, p.x, p.y, 0, 0);
		c.setSlideObject(s);
		return s;
	}
	
	private MachiKoroScene createCoinsSlide(String pos, final int amount) {
		MachiKoroScene m = new MachiKoroScene();
		Point p = getMidwayPoint(pos);
		int amountLeft = amount;
		int b = 0;
		while(amountLeft > 0) {
			final int cc = getLargestCoin(amountLeft);
			amountLeft-=cc;
			Slide s = createCoinSlide(cc);
			DelayElement de = new DelayElement(s, b+=10);
			if(player != null)
				de.setOnArrival(() -> value-=cc);
			s.addElement(de);
			s.addElement(new GrowElement(s, 2 * widths[cc], 2 * widths[cc], 10, MotionEngine.Type.REVERSE_PARABOLIC));
			s.addElement(new MoveElement(s, p.x - widths[cc], p.y - widths[cc], 10, MotionEngine.Type.REVERSE_PARABOLIC));
			GrowElement ge = new GrowElement(s, 0, 0, 10, MotionEngine.Type.PARABOLIC);
			ge.setOnArrival(() -> {
				if(!pos.equals("bank"))
					machiKoroGame.getPlayer(pos).incCoins(cc);
			});
			s.addElement(ge);
			s.finish();
			m.addPainter(s);
		}
		return m;
	}
	
	private int getLargestCoin(int amount) {
		if(amount >= 10)
			return 10;
		if(amount >= 5)
			return 5;
		if(amount >= 1)
			return 1;
		return 0;
	}
	
	private static BufferedImage[] images = new BufferedImage[11];
	private static int[] widths = new int[11];
	
	private MachiKoroPlayer player;
	private int value;
	
	public Coins(MachiKoroPlayer player) {
		this.player = player;
		createImages();
	}
	
	public String getPosition() {
		if(player == null)
			return "bank";
		else
			return player.pos;
	}
	
	private static void createImages() {
		if(images[1] == null) {
			images[1] = CardImages.getImage("money-1.png");
			images[5] = CardImages.getImage("money-5.png");
			images[10] = CardImages.getImage("money-10.png");
//			widths[1] = 40;
//			widths[5] = 60;
//			widths[10] = 70;
			widths[1] = 25;
			widths[5] = 50;
			widths[10] = 75;
		}
	}
	
	private BufferedImage pileImage;
	private void setImage() {
		if(player == null)
			pileImage = CardImages.getImage("bank_moneypile3.png");
		else
			if(value == 0)
				pileImage = null;
			else
				pileImage = CardImages.getImage("moneypile" + (value < 40 ? value - 1 : 39) + ".png");
	}
	
	public void paintIt(Graphics g) {
		setImage();
		if(player == null) {
			g.drawImage(pileImage, (FrameHandler.width - pileImage.getWidth()) / 2, (FrameHandler.height - pileImage.getHeight()) / 2, null);
			return;
		}
		Point p = getTLPoint(getPosition());
		String msg = value + " Coin" + (value == 1 ? "" : "s");
		rotate(g, getPosition());
		if(Settings.getBooleanSetting("show_money_pile")) {
			g.drawImage(pileImage, p.x, p.y, 150, 150, null);
			g.setColor(Color.black);
			g.drawRect(p.x, p.y, 150, 150);
			g.setFont(FrameHandler.getDefaultFont(20));
			g.drawString(msg, p.x + 5, p.y + 145);
		}else {
			g.setColor(FrameHandler.accent3);
			g.fillRect(p.x, p.y, 150, 150);
			g.setColor(Color.black);
			g.drawRect(p.x, p.y, 150, 150);
			g.setFont(FrameHandler.getDefaultFont(30));
			int sh = g.getFontMetrics().getAscent();
			int sw = g.getFontMetrics().stringWidth(msg);
			g.drawString(msg, p.x + (150 - sw) / 2, p.y + (150 - sh) / 2);
		}
		rotateBack(g, getPosition());
	}
	
	private void rotate(Graphics g, String pos) {
		FrameHandler.rotate(g, pos, getMidwayPoint(pos));
	}
	
	private void rotateBack(Graphics g, String pos) {
		FrameHandler.rotateBack(g, pos, getMidwayPoint(pos));
	}
	
	public boolean shouldRemove() {
		return false;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	
	public void add(int amount) {
		value+=amount;
	}
	
	/**
	 * 
	 * @param pos
	 * @param amount
	 * @return the total value of the coins that were sent.
	 */
	public int slideAway(String pos, int amount) {
		if(amount > getValue() && player != null)
			amount = getValue();
		if(amount == 0)
			return 0;
		MachiKoroScene cs = createCoinsSlide(pos, amount);
		GlobalVariables.frameHandler.coinSlides.addPainter(cs);
		return amount;
	}
	
	private static final int[] coinTypes = {10, 5, 1};
	
	private int[] getChange(int amount) {
		int[] change = new int[3];
		int remaining = amount;
		for(int a = 0; a < coinTypes.length; a++) {
			change[a] = remaining / coinTypes[a];
			remaining = remaining % coinTypes[a];
		}
		return change;
	}
	
	/**
	 * This method makes change from one type of coin to another. It animates a 
	 * slide to the bank and slides the designated amount to another recipient.
	 * If the amount designated for another recipient is {@code 0}, all the money
	 * will be returned to the owner of this {@code Coins} object.
	 * @param type1 the type of coin to be exchanged.
	 * @param type2 the desired type of coin.
	 * @param secondRecipient the second recipient of the money to be exchanged.
	 * @param secondRecipientAmount the amount designated for another recipient.
	 * @throws IllegalStateException if this player does not have enough of the 
	 * right type of coin for the exchange.
	 * @throws IllegalArgumentException if the first or second argument is not
	 * {@code 1}, {@code 5}, or {@code 10}.
	 * @throws IllegalArgumentException if {@code secondRecipientAmount} is greater
	 * than the amount of money being exchanged or an improper amount. 
	 * (ex. {@code change(5, 1, "north", 6)}, {@code change(1, 10, "north", 7)})
	 */
	public void change(int type1, int type2, String secondRecipient, int secondRecipientAmount) {
		
	}
	
	private static int range = 150;
	
	private static Point getRandomPoint(String pos) {
		Point p = LocationCoordinates.getCoordinate(pos, "Money");
		p = new Point(p.x + (int) (range * Math.random() - range / 2), p.y + (int) (range * Math.random()) - range / 2);
		return p;
	}
	
	private static Point getMidwayPoint(String pos) {
		Point p = LocationCoordinates.getCoordinate(pos, "Money");
		if(!pos.equals("bank"))
			p = new Point(p.x + range / 2, p.y + range / 2);
		return p;
	}
	
	private static Point getTLPoint(String pos) {
		return LocationCoordinates.getCoordinate(pos, "Money");
	}
	
}

class Cards extends MachiKoroScene{
	
	class Stack extends MachiKoroButton{
		private String type;
		private int numCards;
		private int id;
		private Establishment establishment;
		private String position;
		
		private int gap = 20;
		private int gapDirectionX, gapDirectionY;
		
		private int stackNumber;
		private Cards cards;
		
		Stack(String type, String pos, int stackNumber, Cards cards) throws EstablishmentNotFoundException{
			super(0, 0, 0, 0);
			setPosition(pos); setStackNumber(stackNumber); this.cards = cards;
			this.type = type;
			numCards = 0;
			establishment = Establishments.getEstablishment(type);
			if(establishment == null)
				throw new EstablishmentNotFoundException(type);
		}
		
		/**
		 * @return the numCards
		 */
		public int getNumCards() {
			return numCards;
		}
		
		/**
		 * @param numCards the numCards to set
		 */
		public void setNumCards(int numCards) {
			this.numCards = numCards;
		}
		
		/**
		 * This method acts out the action of all the cards in this stack with 500ms intervals. It does not block.
		 * @param roller the {@code MachiKoroPlayer} object for the player that rolled the action that activated
		 * this action.
		 */
		public void act(MachiKoroPlayer roller) {
			new Thread(() -> {
				try {
					for(int a = 0; a < numCards; a++) {
						establishment.act(owner, roller);
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}).start();
		}
		
		public void addCard() {
			numCards++;
		}
		
		public void removeCard() {
			numCards--;
		}
		
		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
		
		@Override
		public void paintIt(Graphics g) {
			for(int a = 0; a < numCards; a++)
				g.drawImage(establishment.getImage(position), getX() + a * gap * gapDirectionX, getY() + a * gap * gapDirectionY, getWidth(), getHeight(), null);
			g.setColor(Color.YELLOW);
			if(choosingCard && mouseIsIn)
				for(int a = 0; a < 5; a++)
					g.drawRoundRect(getLeftBound() - a, getTopBound() - a, getRightBound() - getLeftBound() + 2 * a, getBottomBound() + 2 * a, 4, 4);
		}
		boolean mouseIsIn;
		
		private int getLeftBound() {
			if(position.equals("west"))
				return getX() - gap * numCards;
			else
				return getX();
		}
		
		private int getRightBound() {
			if(position.equals("east"))
				return getX() + getWidth() + gap * numCards;
			else
				return getX() + getWidth();
		}
		
		private int getTopBound() {
			if(position.equals("north"))
				return getY() - gap * numCards;
			else
				return getY();
		}
		
		private int getBottomBound() {
			if(position.equals("south"))
				return getY() + getHeight() + gap * numCards;
			else
				return getY() + getHeight();
		}
		
		@Override
		public boolean shouldRemove() {
			return numCards == 0;
		}
		
		public void updateImage(int mouseX, int mouseY) {
			mouseIsIn = mouseIsIn(mouseX, mouseY);
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @return the establishment
		 */
		public Establishment getEstablishment() {
			return establishment;
		}

		@Override
		public void press(int mouseX, int mouseY) {
			
		}

		@Override
		public void release(int mouseX, int mouseY) {
			if(choosingCard && mouseIsIn(mouseX, mouseY)) {
				selectedCard = type;
				cards.notifyAll();
				choosingCard = false;
			}
		}
		
		private boolean choosingCard = false;
		
		public void chooseACard() {
			choosingCard = true;
		}

		/**
		 * @return the position
		 */
		public String getPosition() {
			return position;
		}
		
		public void setPosition() {
			setPosition(getOwner().pos);
		}

		/**
		 * @param position the position to set
		 */
		public void setPosition(String position) {
			this.position = position;
			updateDimensions();
		}
		
		private void updateDimensions() {
			Point p = LocationCoordinates.getCoordinate(position, "First Establishment");
			int x = p.x, y = p.y;
			if(position.equals("east")) {
				y-=70 * stackNumber;
				setWidth(90); setHeight(60);
				gapDirectionX = 1; gapDirectionY = 0;
			}else if(position.equals("west")) {
				y+=70 * stackNumber;
				setWidth(90); setHeight(60);
				gapDirectionX = -1; gapDirectionY = 0;
			}else if(position.equals("north")) {
				x-=70 * stackNumber;
				setWidth(60); setHeight(90);
				gapDirectionX = 0; gapDirectionY = -1;
			}else if(position.equals("south")) {
				int ox = x;
				x+=70 * stackNumber;
				setWidth(60); setHeight(90);
				gapDirectionX = 0; gapDirectionY = 1;
				if(Thread.currentThread().getStackTrace()[4].getMethodName().equals("paintIt"))
					owner.l.log("stackNumber:" + stackNumber + ",ox:" + ox + ",x:" + x);
			}
			setX(x); setY(y);
		}

		/**
		 * @return the stackNumber
		 */
		public int getStackNumber() {
			return stackNumber;
		}

		/**
		 * @param stackNumber the stackNumber to set
		 */
		public void setStackNumber(int stackNumber) {
			this.stackNumber = stackNumber;
		}
		
		public String toString() {
			return type + "s(" + position + " #" + stackNumber + "):" + numCards;
		}

	}
	
	class Card implements Paintable{
		
		private Slide slide;
		private String type;
		
		public Card(Slide s, String type) {
			slide = s;
			this.type = type;
		}
		
		public Card(String type) {
			this.type = type;
		}

		@Override
		public void paintIt(Graphics g) {
			g.drawImage(getImage(), slide.getX(), slide.getY(), slide.getWidth(), slide.getHeight(), null);
		}

		@Override
		public boolean shouldRemove() {
			return false;
		}
		
		private BufferedImage getImage() {
			String pos = getPosition();
			if(pos.equals("bank"))
				pos = "south";
			return Establishments.getEstablishment(type).getImage(pos);
		}

		public void setSlideObject(Slide s) {
			slide = s;
		}
		
	}
	
	private ArrayList<Stack> stacks;
	private MachiKoroPlayer owner;
	
	public Cards(MachiKoroPlayer owner) {
		stacks = new ArrayList<>();
		this.owner = owner;
		if(GlobalVariables.gameType.equals("original")) {
			add("Wheat Field");
			add("Bakery");
		}
	}
	
	public void processColor(String color, int roll, MachiKoroPlayer roller, Runnable r) {
		boolean wasRun = false;
		for(Stack s : stacks) {
			Establishment e = s.getEstablishment();
			boolean isIn = false;
			for(int a : e.getActivationNums())
				if(roll == a)
					isIn = true;
			if(isIn && e.getColor().equals(color)) {
				new Thread(() -> {
					try {
						for(int b = 0; b < s.getNumCards(); b++) {
							e.act(owner, roller);
							Thread.sleep(500);
						}
						Thread.sleep(1500);
						if(r != null)
							r.run();
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}
				}).start();
				wasRun = true;
			}
		}
		if(r != null && !wasRun)
			r.run();
	}
	
	public void processGreensAndPurples(int roll, Runnable r) {
		boolean isIn = false;
		for(Stack s : stacks) {
			Establishment e = s.getEstablishment();
			if(e.getColor().equals("green") || e.getColor().equals("purple")) {
				new Thread(() -> {
					boolean wasRun = false;
					for(int a : e.getActivationNums())
						if(a == roll) {
							try {
								for(int b = 0; b < s.getNumCards(); b++) {
									e.act(owner, owner);
									Thread.sleep(500);
								}
								Thread.sleep(1500);
								if(r != null) {
									r.run();
									wasRun = true;
								}
							} catch (InterruptedException e1) {
								Thread.currentThread().interrupt();
							}
						}
					if(!wasRun && r != null)
						r.run();
				}).start();
				for(int a : e.getActivationNums())
					if(a == roll)
						isIn = true;
			}
		}
		if(!isIn && r != null)
			r.run();
	}
	
	public void processReds(int roll, MachiKoroPlayer roller, Runnable r) {
		boolean isIn = false;
		for(Stack s : stacks) {
			Establishment e = s.getEstablishment();
			if(e.getColor().equals("red")) {
				new Thread(() -> {
					boolean wasRun = false;
					for(int a : e.getActivationNums())
						if(a == roll) {
							try {
								for(int b = 0; b < s.getNumCards(); b++) {
									e.act(owner, roller);
									Thread.sleep(500);
								}
								Thread.sleep(1500);
								if(r != null) {
									r.run();
									wasRun = true;
								}
							} catch (InterruptedException e1) {
								Thread.currentThread().interrupt();
							}
							if(!wasRun && r != null)
								r.run();
						}
				}).start();
				for(int a : e.getActivationNums())
					if(a == roll)
						isIn = true;
			}
		}
		if(!isIn && r != null)
			r.run();
	}
	
	public void processBlues(int roll, MachiKoroPlayer roller, Runnable r) {
		boolean isIn = false;
		for(Stack s : stacks) {
			Establishment e = s.getEstablishment();
			if(e.getColor().equals("blue")) {
				new Thread(() -> {
					boolean wasRun = false;
					for(int a : e.getActivationNums())
						if(a == roll) {
							try {
								for(int b = 0; b < s.getNumCards(); b++) {
									e.act(owner, roller);
									Thread.sleep(500);
								}
								Thread.sleep(1500);
								if(r != null) {
									r.run();
									wasRun = true;
								}
							} catch (InterruptedException e1) {
								Thread.currentThread().interrupt();
							}
							if(!wasRun && r != null)
								r.run();
						}
				}).start();
				for(int a : e.getActivationNums())
					if(a == roll)
						isIn = true;
			}
		}
		if(!isIn && r != null)
			r.run();
	}
	
	public void add(String type) throws EstablishmentNotFoundException {
		if(owner == null)
			return;
		boolean alreadyExists = false;
		for(Stack s : stacks) {
			if(s.getType().equals(type)) {
				s.addCard();
				alreadyExists = true;
			}
		}
		if(!alreadyExists) {
			Stack s = new Stack(type, owner.pos, stacks.size(), this);
			s.addCard();
			stacks.add(s);
			super.addPainter(s);
		}
	}
	
	public int getNumCardsType(String type) {
		int num = 0;
		for(Stack s : stacks)
			if(Establishments.getEstablishment(s.getType()).getType().equals(type))
				num+=s.getNumCards();
		return num;
	}
	
	public boolean remove(String type) {
		if(owner == null)
			return false;
		Stack stack = null;
		for(Stack s : stacks) {
			if(s.getType().equals(type)) {
				s.removeCard();
				stack = s;
			}
		}
		if(stack != null && stack.shouldRemove()) {
			super.painters.remove(stack);
			stacks.remove(stack);
			for(int a = 0; a < stacks.size(); a++)
				stacks.get(a).setStackNumber(a);
		}
		if(stack != null)
			return true;
		return false;
	}
	
	private Slide createEstablishmentSlide(String type, String to) {
		Point pFrom = getMidPoint(getPosition(), type);
		Point pTo = getMidPoint(to, type);
		Card c = new Card(type);
		Slide s = new Slide(c, pFrom.x, pFrom.y, 0, 0);
		c.setSlideObject(s);
		int nw = 240, nh = 360;
		final long time = System.currentTimeMillis();
		GrowElement e1 = new GrowElement(s, nw, nh, 10, MotionEngine.Type.REVERSE_PARABOLIC);
		e1.setOnArrival(() -> GlobalVariables.anonymousLogger.log("e1 finished - " + (System.currentTimeMillis() - time)));
		s.addElement(e1);
		s.addElement(new MoveElement(s, pTo.x - nw / 2, pTo.y - nh / 2, 10, MotionEngine.Type.REVERSE_PARABOLIC));
		GrowElement ge = new GrowElement(s, 0, 0, 10, MotionEngine.Type.PARABOLIC);
		ge.setOnArrival(() -> machiKoroGame.getPlayer(to).getCards().add(type));
		s.addElement(ge);
		s.finish();
		return s;
	}
	
	private Slide createEstablishmentSlide(String type) {
		Point pFrom = new Point(FrameHandler.width / 2, FrameHandler.height / 2);
		Point pTo = getMidPoint(getPosition(), type);
		Card c = new Card(type);
		Slide s = new Slide(c, pFrom.x, pFrom.y, 0, 0);
		c.setSlideObject(s);
		int nw = 240, nh = 360;
		s.addElement(new GrowElement(s, nw, nh, 10, MotionEngine.Type.REVERSE_PARABOLIC));
		s.addElement(new MoveElement(s, pTo.x - nw / 2, pTo.y - nh / 2, 10, MotionEngine.Type.REVERSE_PARABOLIC));
		GrowElement ge = new GrowElement(s, 0, 0, 10, MotionEngine.Type.PARABOLIC);
		ge.setOnArrival(() -> add(type));
		s.addElement(ge);
		s.finish();
		return s;
	}
	
	public void slideAway(String type, String to) {
		if(owner == null || remove(type)) {
			frameHandler.establishmentSlides.addPainter(createEstablishmentSlide(type, to));
		}
	}
	
	public void slideTo(String type) {
		frameHandler.establishmentSlides.addPainter(createEstablishmentSlide(type));
	}
	
	public Point getTLPoint(String pos, String type) {
		Stack stack = null;
		for(Stack s : machiKoroGame.getPlayer(pos).getCards().getStacks())
			if(s.getType().equals(type))
				stack = s;
		if(stack != null)
			return new Point(stack.getX(), stack.getY());
		else {
			ArrayList<Stack> ss = machiKoroGame.getPlayer(pos).getCards().getStacks();
			Stack s = ss.get(ss.size() - 1);
			int x = s.getX(), y = s.getY();
			switch(pos) {
			case "north": x-=70; break;
			case "south": x+=70; break;
			case "east": y-=70; break;
			case "west": y+=70;
			}
			return new Point(x, y);
		}
	}
	
	public Point getMidPoint(String pos, String type) {
		if(pos.equals("bank"))
			return new Point(FrameHandler.width / 2, FrameHandler.height / 2);
		Point p = getTLPoint(pos, type);
		int x = p.x, y = p.y;
		int dx = 0, dy = 0;
		switch(pos) {
		case "north":
			dx = 30;
			dy = 45;
			break;
		case "south":
			dx = 30;
			dy = 45;
			break;
		case "east":
			dx = 45;
			dy = 30;
			break;
		case "west":
			dx = 45;
			dy = 30;
		}
		x+=dx;
		y+=dy;
		return new Point(x, y);
	}
	
	public ArrayList<Stack> getStacks() {
		return stacks;
	}
	
	@Override
	public void addPainter(Paintable p) {}
	
	private String selectedCard;
	
	public String selectCard() {
		for(Stack s : stacks)
			s.chooseACard();
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		for(Stack s : stacks)
			s.choosingCard = false;
		return selectedCard;
	}
	
	public void paintIt(Graphics g) {
		setPosition();
		super.paintIt(g);
	}

	/**
	 * @return the owner
	 */
	public MachiKoroPlayer getOwner() {
		return owner;
	}
	
	public void setPosition() {
		for(Stack s : stacks)
			s.setPosition();
	}
	
	public void setPosition(String position) {
		for(Stack s : stacks)
			s.setPosition(position);
	}
	
	public String getPosition() {
		if(owner == null)
			return "bank";
		else
			return owner.pos;
	}
	
}
