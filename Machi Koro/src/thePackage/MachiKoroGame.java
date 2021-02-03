package thePackage;

import java.awt.Graphics;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import thePackage.FrameHandler.Slot;

@SuppressWarnings("unused")
public class MachiKoroGame extends GameType implements MessageReceiver{
	
	/* GMK Things to do--
	 * Y: Establish/test libraries
	 * Y: Make subclasses
	 * N: Make menu
	 * N: Animate
	 * --Y: Background:rgb(0, 173, 220)
	 * --N: Font: "Broadway"??
	 * --Y: Get images
	 * --N: Create Animation for players
	 * N: Create Bots
	 */
	
	MessageHandler messenger;
	FrameHandler frame;
	private boolean paused = false;
	
	private ArrayList<MachiKoroPlayer> players;
	
	MachiKoroGame(){
		s = new Status("Main Object", "stable");
		l = new Logger(s);
	}
	
	static Game g;
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		boolean DEBUG = false;
		boolean VERBOSE = false;
		for(String s : args) {
			if(s.equals("-debug"))
				DEBUG = true;
			else if(s.equals("-verbose"))
				VERBOSE = true;
			else {
				try {
					PrintStream ps = new PrintStream(new File(s));
					Logger.setDefaultOutputStream(ps);
				} catch (FileNotFoundException e) {}
			}
		}
		GlobalVariables.DEBUG = DEBUG;
		GlobalVariables.VERBOSE = VERBOSE;
		if(DEBUG) {
			int number = (int) (Math.random() * 1000);
			FrameHandler frame = new FrameHandler(number); frame.setFocusTraversalKeysEnabled(false);
			MachiKoroGame game = new MachiKoroGame();
			MessageHandler messenger = new MessageHandler();
			frame.setGame(game);
			frame.messenger = messenger;
			game.setFrame(frame);
			game.setMessenger(messenger);
			messenger.setFrame(frame);
			messenger.setGame(game);
			game.launch();
		}else { // try {
			FrameHandler frame = new FrameHandler();
			MachiKoroGame game = new MachiKoroGame();
			MessageHandler messenger = new MessageHandler();
			frame.setGame(game);
			frame.messenger = messenger;
			game.setFrame(frame);
			game.setMessenger(messenger);
			messenger.setFrame(frame);
			messenger.setGame(game);
			game.launch();
//			g = new Game(args);
//			g.setGame(new MachiKoroGame());
//			g.setFrame(new FrameHandler());
//			g.setMessenger(new MessageHandler());
//			g.start();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
		}
	}
	
	// GMK Make launch method to start the menu
	/**
	 * Launches the game and loads the menu
	 */
	@Override
	public void launch() {
		try {
			GlobalVariables.frameHandler = frame;
			GlobalVariables.machiKoroGame = this;
			GlobalVariables.messageHandler = messenger;
			frame.addMouseListener(frame);
			frame.addMouseMotionListener(frame);
			frame.addKeyListener(frame);
			frame.setPainter(frame);
			frame.startWindowRefresh(50);
			CardImages.loadImages();
			Establishments.createOriginalActions();
			Establishments.readFileAsEstablishments(	"/TextDocuments/cards_original.txt");
			LocationCoordinates.readFileAsCoordinates(	"/TextDocuments/coordinates.txt");
			Settings.readFileAsSettings(				"/TextDocuments/settings.txt");
			
			frame.launchIntoMenu();
		} catch (IOException e) {
			e.printStackTrace();
			Errors.log(e);
			if(frame != null)
				frame.dispose();
			FrameHandler.showOKMessageBox("Error", "IOException on start");
			return;
		}
	}
	
	private MessageBox pausedMenu;
	
	public void pause() {
		if(paused)
			return;
		s.setStatus("paused");
		l.log("Paused");
		pausedMenu = new MessageBox("Paused", "Resume", "Quit");
		pausedMenu.addActionListener(ae -> {
			if(ae.getActionCommand().equals("button1")) {
				resume();
			}else {
				s.setStatus("stable");
				l.log("Quitting");
				paused = false;
				if(messenger.getState() != MessageHandler.State.DEAD) {
					messenger.kill();
				}else {
					MachiKoroScene mms = frame.mainMenu(false);
					frame.scene.addPainter(mms);
					mms.slideInFromLeft(60);
				}
			}
		});
		pausedMenu.enter();
		frame.scene.addPainter(pausedMenu);
		paused = true;
	}
	
	public void resume() {
		if(!paused)
			return;
		s.setStatus("stable");
		l.log("Resumed");
		pausedMenu.exit();
		paused = false;
	}
	
	public boolean paused() {
		return paused;
	}

	@Override
	public void setFrame(GameFrame f) {
		frame = (FrameHandler) f;
	}

	public void setMessenger(MessageHandler m) {
		messenger = (MessageHandler) m;
	}
	
	public void startGameSolo() {
		l.log("Starting solo game");
		frame.addSoloScene(frame.getScene());
	}
	
	public void startGameMultiplayer() {
		l.log("Starting multiplayer game");
		frame.addMultiplayerScene(frame.getScene());
	}
	
	public ArrayList<MachiKoroPlayer> getPlayers(){
		return players;
	}
	
	public MachiKoroPlayer getPlayer(int index) {
		return players.get(index);
	}
	
	public MachiKoroPlayer getPlayer(String pos) {
		for(MachiKoroPlayer p : players)
			if(p.pos.equals(pos))
				return p;
		return null;
	}
	
	public void addPlayer(MachiKoroPlayer player) {
		players.add(player);
	}
	
	private int turn;
	
	/**
	 * 
	 * @return the number representing whose turn it is.
	 */
	public int getTurn() {
		return turn;
	}
	
	/**
	 * This method changes the turn variable so it is pointing to the next player.
	 * If the number gets higher than the number of players, it starts back at {@code 0}.
	 */
	public void nextTurn() {
		turn++;
		if(turn >= players.size())
			turn = 0;
	}
	
	/**
	 * This method starts the gameplay, sends messages to all the connections that the game has begun
	 * if the {@link MessageHandler} is in the {@code HOST} state, and sets it to {@code Player 0}'s 
	 * turn.
	 * @throws IllegalStateException if the game is not ready to be started.
	 */
	public void startGameplay() throws IllegalStateException{
		// GMK create MachiKoroGame.startGamePlay()
		int numPlayers = 0;
		for(Slot s : frame.slots_for_waiting_scene)
			if(s.player != null)
				numPlayers++;
		if(numPlayers < 2)
			throw new IllegalStateException("There must be more than 1 player.");
		// If there are any other criteria for the game being ready, put the
		// IllegalStateExceptions here.
		
		turn = 0;
		Slot[] slots = frame.slots_for_waiting_scene;
		players = new ArrayList<>();
		if(messenger.getState() == MessageHandler.State.HOST) {
			for(Slot s : slots)
				if(s.player != null) {
					players.add(s.player);
					if(s.player instanceof RemotePlayer)
						new Message(s.player, Message.Type.BEGIN, l).send();
				}
		}else if(messenger.getState() == MessageHandler.State.CLIENT) {
			for(Slot s : slots)
				if(s.player != null)
					players.add(s.player);
		}else {
			for(Slot s : slots)
				if(s.player != null)
					players.add(s.player);
		}
		for(MachiKoroPlayer player : players)
			player.s.setStatus("playing");
		frame.addGameplayScene();
		startGameplayThread();
	}
	
	public Thread gameplayThread;
	public boolean gameplay = false;
	
	public void startGameplayThread() {
		gameplay = true;
		gameplayThread = new Thread(() -> {
			while(gameplay && !Thread.currentThread().isInterrupted()) {
				try {
					players.get(getTurn()).takeTurn(); // This method shouldn't return until the player has taken their turn.
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				nextTurn();
			}
		});
		gameplayThread.setName("Gameplay Thread");
		gameplayThread.start();
	}
	
	public void resetGameplayVariables() {
		players = new ArrayList<>();
	}

	@Override
	public void receive(Message m) {
		// GMK create MachiKoroGame.recieve(Message m)
		Message.Type type = m.getType();
		MessageInfo info = m.getInfo();
		switch(type) {
		case WELCOME: // Client
			int id = info.id;
			String string = info.stringPart;
			frame.addWaitingScene(1, frame.scene);
			for(int a = 0; a < string.length(); a++)
				if(string.charAt(a) == '1') {
					RemotePlayer player = new RemotePlayer(a, messenger.clientConnection);
					frame.addRemotePlayerToWaitingScene(player, a);
				}
			messenger.clientPlayer = new LocalPlayer(id);
			frame.addLocalPlayerToWaitingScene(messenger.clientPlayer, id);
			break;
		case BEGIN: // Client
			for(Slot s : frame.slots_for_waiting_scene)
				if(s.player != null)
					players.add(s.player);
			break;
		case BUY:
			break;
		case CHECK_UP: // Client
			new Message(null, Message.Type.ROGER, messenger.clientPlayer.l).send();
			break;
		case ITS_YOUR_TURN:
			break;
		case NEW_CONNECTION:
			if(messenger.getState() == MessageHandler.State.HOST) { // Host
				Connection c = (Connection) ((NonSerializableMessageInfo) m.getInfo()).object;
				if(messenger.getNumConnections() > 3 || getPlayers().size() > 4) {
					messenger.kick(c);
					break;
				}
				RemotePlayer player = new RemotePlayer(players.size(), c);
				players.add(player);
				messenger.tiePlayerToConnection(player, c);
				int num = frame.addRemotePlayerToWaitingScene(player);
				if(num == -1) {
					messenger.kick(c);
					frame.removePlayerFromWaitingScene(player);
					break;
				}
				String slots = "";
				for(Slot s : frame.slots_for_waiting_scene)
					if(s.player != null && s.player != player)
						slots+="1";
					else
						slots+="0";
				MessageInfo welcomeInfo = new MessageInfo(num, slots);
				Message welcome = new Message(player, Message.Type.WELCOME, welcomeInfo, l);
				
				welcome.send();
				for(Connection other : messenger.serverConnections)
					if(other != c) {
						Message ncm = new Message(other.getPlayer(), Message.Type.NEW_CONNECTION, new MessageInfo(num, ""), l);
						ncm.send();
					}
			}else if(messenger.getState() == MessageHandler.State.CLIENT) { // Client
				if(frame.s.getStatus().equals("Waiting Scene")) {
					id = info.id;
					RemotePlayer player = new RemotePlayer(id, messenger.clientConnection);
					frame.addRemotePlayerToWaitingScene(player, id);
				}
			}
			break;
		case PLAYER_KICKED: // Client
			id = info.id;
			frame.removePlayerFromWaitingScene(id);
			break;
		case PLAYER_WON:
			break;
		case ROGER: // Host
			messenger.receivedRoger(m);
			break;
		case ROLL:
			break;
		case TRADE:
			break;
		case ERROR:
			Exception e = (Exception) ((ErrorInfo) info).object;
			if(e instanceof IOException) {
				if(messenger.getState() == MessageHandler.State.HOST) {
					String origin = m.getOrigin();
					for(int a = 0; a < messenger.serverConnections.size(); a++) {
						Connection c = messenger.serverConnections.get(a);
						if(origin.equals(c.toString())) {
							Message kpm = new Message(c.getPlayer(), Message.Type.PLAYER_KICKED, new MessageInfo(c.getPlayer().number, ""), l);
							try{ frame.removePlayerFromWaitingScene(c.getPlayer()); } catch (IllegalStateException e1) {}
							messenger.kick(c);
							kpm.send();
						}
					}
				}else if(messenger.getState() == MessageHandler.State.CLIENT) {
					messenger.kill();
					frame.addMultiplayerScene(frame.scene);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void setMessenger(MessageSender m) {
		
	}
	
//	/**
//	 * This method loops through the players starting with the player whose turn it is and performs the action
//	 *  in the {@code PlayerAction} object. This method throws and {@link InterruptedException} if the thread
//	 *  is interrupted while looping.
//	 * @param pa the {@code PlayerAction} object to loop with.
//	 * @throws InterruptedException if an {@code InterruptedException} is thrown.
//	 * @see {@link shortLoopPlayers()}, {@link getPlayers()}
//	 */
//	public void longLoopPlayers(PlayerAction pa) throws InterruptedException {
//		int a = getTurn();
//		do {
//			pa.actionPerformed(getPlayer(a));
//			a++;
//		} while (a != getTurn());
//	}
//	
//	/**
//	 * This method loops through the players starting with the player whose turn it is and performs the action
//	 * in the {@code PlayerAction} object. The action should not throw an {@link InterruptedException}.
//	 * @param pa the {@code PlayerAction} object to loop with.
//	 * @throws IllegalArgumentException if an {@code InterruptedException} is thrown.
//	 * @see {@link longLoopPlayers()}, {@link getPlayers()}
//	 */
//	public void shortLoopPlayers(PlayerAction pa) throws IllegalArgumentException {
//		int a = getTurn();
//		do {
//			try {
//				pa.actionPerformed(getPlayer(a));
//			} catch (InterruptedException e) {
//				throw new IllegalArgumentException("The given method should not throw an InterruptedException.");
//			}
//			a++;
//		} while (a != getTurn());
//	}

}
//
//interface PlayerAction{
//	
//	void actionPerformed(MachiKoroPlayer player) throws InterruptedException;
//	
//}