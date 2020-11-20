package thePackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.swing.*;

import arrays.*;
import decks.*;

public class PalmTreeGame implements ActionListener{
	
	public static final int PALM_TREE_PORT = 13;
	public static String connectTo;
	
	Array cards = new Array();
	Deck serverHand, clientHand, serverBottomCards, clientBottomCards, serverTopCards, clientTopCards, drawPile, discardPile, gonePile;
	int[] serverChosenCards = {-1, -1, -1}, clientChosenCards = {-1, -1, -1};
	
	String programStatus;
	String gameStatus;
	String waitingOn;
	
	Socket sock;
	ServerSocket servSock;
	DataInputStream is;
	DataOutputStream os;
	
	Timer t;
	
	ScreenHandler window;
	InGameMessageHandler messageHandler;
	Logger logger;

	public static void main(String[] args) {
		PalmTreeGame g;
		if(args.length != 0)
			g = new PalmTreeGame(args[0]);
		else
			g = new PalmTreeGame("localhost");
		g.run();
	}
	
	public PalmTreeGame(String ct) {
		connectTo = ct;
	}
	
	void run() {
		logger = new Logger(this);
		window = new ScreenHandler(this);
		programStatus = "menu";
		t = new Timer(10, this);
		t.start();
	}
	
	void startSearch() {
		programStatus = "searching";
		search();
		
	}
	
	void search() {
		logger.log("searching...");
		try {
			sock = new Socket(connectTo, PALM_TREE_PORT);
			is = new DataInputStream(
					new BufferedInputStream(sock.getInputStream()));
			os = new DataOutputStream(
					new BufferedOutputStream(sock.getOutputStream()));
			programStatus = "found";
			startAsClient();
			ArrayList<String> msg = new ArrayList<>();
			msg.add("Game found");
			msg.add("Waiting on host...");
			show(msg, BU_NOTHING, () -> dealAsClient());
		}catch (UnknownHostException e) {
			ArrayList<String> msg = new ArrayList<>();
			msg.add("Error");
			msg.add("Unknown host");
			msg.add("Try again?");
			show(msg, BU_YES_OR_NO, () -> {
				if(window.answer)
					new Thread(() -> startSearch()).start();
				else
					stopSearch();
			});
			programStatus = "unknown host";
		}catch (NoRouteToHostException e) {
			ArrayList<String> msg = new ArrayList<>();
			msg.add("Error");
			msg.add("Unreachable");
			msg.add("Try again?");
			show(msg, BU_YES_OR_NO, () -> {
				if(window.answer)
					new Thread(() -> startSearch()).start();
				else
					stopSearch();
			});
			programStatus = "unreachable";
		}catch (ConnectException e) {
			ArrayList<String> msg = new ArrayList<>();
			msg.add("Error");
			msg.add("Connection refused");
			msg.add("Try again?");
			show(msg, BU_YES_OR_NO, () -> {
				if(window.answer)
					new Thread(() -> startSearch()).start();
				else
					stopSearch();
			});
			programStatus = "connection refused";
		}catch (IOException e) {
			ArrayList<String> msg = new ArrayList<>();
			msg.add("Error");
			msg.add("IOException on start");
			msg.add("Try again?");
			show(msg, BU_YES_OR_NO, () -> {
				if(window.answer)
					new Thread(() -> startSearch()).start();
				else
					stopSearch();
			});
			programStatus = "ioexception";
		}
	}
	
	void stopSearch() {
		logger.log("stopping search");
		programStatus = "stopping";
		try {
			if(sock != null) {
				sock.close();
				sock = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		programStatus = "menu";
	}
	
	void startHost() {
		logger.log("starting host");
		programStatus = "hosting";
		window.frame.setTitle("Palm Tree - " + programStatus);
		ArrayList<String> h = new ArrayList<>();
		h.add("Hosting game");
		h.add("Click search on another");
		h.add("computer to join.");
		show(h, BU_CANCEL, () -> stopHost());
		
		new Thread(() -> {
			sock = null;
			servSock = null;
			try {
				servSock = new ServerSocket(PALM_TREE_PORT);
				if((sock = servSock.accept()) != null) {
					is = new DataInputStream(new 
							BufferedInputStream(sock.getInputStream()));
					os = new DataOutputStream(new 
							BufferedOutputStream(sock.getOutputStream()));
					programStatus = "connected";
					window.frame.setTitle("Palm Tree - " + programStatus);
					ArrayList<String> s = new ArrayList<>();
					s.add("Horray!");
					s.add("Someone joined!");
					s.add("Press \"Ok\" to start");
					show(s, BU_OK, () -> startAsHost());
				}
			}catch(IOException e) {
				ArrayList<String> msg = new ArrayList<>();
				msg.add("Error");
				msg.add("IOException on start.");
				msg.add("Try again.");
				show(msg, BU_OK, () -> stopHost());
				programStatus = "ioexception";
			}
		}).start();
	}
	
	void stopHost() {
		logger.log("stopping host");
		programStatus = "stopping";
		try {
			if(sock != null) {
				sock.close();
				sock = null;
			}
			if(servSock != null) {
				servSock.close();
				sock = null;
			}
		}catch(IOException e) {}
		programStatus = "menu";
	}
	
	void startAsHost() {
		logger.log("starting game as host");
		programStatus = "Host: in game";
		window.frame.setTitle("Palm Tree - host");
		gameStatus = "waiting";
		waitingOn = "start confirmation";
		messageHandler = new InGameMessageHandler(this);
		messageHandler.sendMessage(InGameMessageHandler.START, new int[0]);
	}
	
	void startAsClient() {
		logger.log("starting game as client");
		programStatus = "Client: in game";
		window.frame.setTitle("Palm Tree - client");
		gameStatus = "waiting";
		messageHandler = new InGameMessageHandler(this);
	}
	
	final int BU_OK = 0;
	final int BU_YES_OR_NO = 1;
	final int BU_CANCEL = 2;
	final int BU_NOTHING = 3;
	
	void show(ArrayList<String> msg, int type) {
		String m = msg.get(0);
		for(int l = 1; l < msg.size(); l++)
			m+=" / " + msg.get(l);
		logger.log("Showing: " + m);
		window.message = msg;
		window.showMessage = true;
		window.messageType = type;
		window.onClose = () -> {};
	}
	
	void show(ArrayList<String> msg, int type, Runnable r) {
		show(msg, type);
		window.onClose = r;
	}
	
	void stopMessage(boolean run, boolean answer) {
		if(!window.showMessage)
			return;
		logger.log("stopping message - run=" + run + ", answer=" + answer);
		window.showMessage = false;
		window.answer = answer;
		if(run)
			window.onClose.run();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		window.actionPerformed(e);
	}
	
	void dealAsHost() {
		logger.log("dealing as host");
		drawPile = new Deck();
		gonePile = new Deck();
		discardPile = new Deck();
		serverHand = new Deck();
		clientHand = new Deck();
		serverBottomCards = new Deck();
		clientBottomCards = new Deck();
		serverTopCards = new Deck();
		clientTopCards = new Deck();
		drawPile.stackBicycle();
		drawPile.popTopCard();
		drawPile.popTopCard();
		drawPile.shuffle4();
		int[] deck = new int[104];
		for(int a = 0; a < 52; a++) {
			deck[2 * a] = ((BicycleCard) drawPile.getCard(a)).getValue();
			deck[2 * a + 1] = ((BicycleCard) drawPile.getCard(a)).getSuit();
		}
		waitingOn = "deck confirmation";
		messageHandler.sendMessage(InGameMessageHandler.DECK, deck);
//		for(int a = 0; a < 52; a++) {
//			try {
//				os.writeInt(((BicycleCard) drawPile.getCard(a)).getValue());
//				os.writeInt(((BicycleCard) drawPile.getCard(a)).getSuit());
//				os.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	void dealAsClient() {
		logger.log("dealing as client");
//		drawPile = new Deck();
		gonePile = new Deck();
		discardPile = new Deck();
		serverHand = new Deck();
		clientHand = new Deck();
		serverBottomCards = new Deck();
		clientBottomCards = new Deck();
		serverTopCards = new Deck();
		clientTopCards = new Deck();
		new Thread(() -> animateDeal(CLIENT)).start();
	}
	
	static final int HOST = 0;
	static final int CLIENT = 1;
	
	void animateDeal(int me) {
		logger.log("animating deal");
		try {
			int meY, youY;
			Deck meHand, youHand, meBottomCards, youBottomCards;
			if(me == HOST) {
				meY = 450;
				meHand = serverHand;
				meBottomCards = serverBottomCards;
				youY = 100;
				youHand = clientHand;
				youBottomCards = clientBottomCards;
			}else {
				meY = 450;
				meHand = clientHand;
				meBottomCards = clientBottomCards;
				youY = 100;
				youHand = serverHand;
				youBottomCards = serverBottomCards;
			}
			
			long interval = 250;
			
			if(me == CLIENT) {
				for(int a = 0; a < 3; a++) {
					BicycleCard meCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(meCard, 100, 100, 650 + 110 * (meBottomCards.getHeight() + 1), meY, () -> meBottomCards.stackCard(meCard));
					
					Thread.sleep(interval);
					
					BicycleCard youCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(youCard, 100, 100, 650 + 110 * (youBottomCards.getHeight() + 1), youY, () -> youBottomCards.stackCard(youCard));
					
					Thread.sleep(interval);
				}
				
				for(int a = 0; a < 7; a++) {
					BicycleCard meCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(meCard, 100, 100, 450, meY, () -> meHand.stackCard(meCard));
					
					Thread.sleep(interval);
					
					BicycleCard youCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(youCard, 100, 100, 450, youY, () -> youHand.stackCard(youCard));
					
					Thread.sleep(interval);
				}
			}else {
				for(int a = 0; a < 3; a++) {
					BicycleCard youCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(youCard, 100, 100, 650 + 110 * (youBottomCards.getHeight() + 1), youY, () -> youBottomCards.stackCard(youCard));
					
					Thread.sleep(interval);
					
					BicycleCard meCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(meCard, 100, 100, 650 + 110 * (meBottomCards.getHeight() + 1), meY, () -> meBottomCards.stackCard(meCard));
					
					Thread.sleep(interval);
				}
				
				for(int a = 0; a < 7; a++) {
					BicycleCard youCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(youCard, 100, 100, 450, youY, () -> youHand.stackCard(youCard));
					
					Thread.sleep(interval);
					
					BicycleCard meCard = (BicycleCard) drawPile.popTopCard();
					window.startSlide(meCard, 100, 100, 450, meY, () -> meHand.stackCard(meCard));
					
					Thread.sleep(interval);
				}
			}
			
			gameStatus = "chosing cards";
		}catch(InterruptedException e) {
			ArrayList<String> msg = new ArrayList<>();
			msg.add("Error");
			msg.add("Interupted while dealing");
			msg.add("Returning to the home page");
			if(me == HOST)
				show(msg, BU_OK, () -> stopHost());
			else
				show(msg, BU_OK, () -> stopSearch());
		}
		
	}
	
}

class InGameMessageHandler extends Thread{
	
	public static final int START = 23;
	public static final int MYTURN = 24;
	public static final int CHOSEN = 25;
	public static final int DRAW = 26;
	public static final int DECK = 27;
	
	public static final int ROGER_THAT = 70;
	
	PalmTreeGame game;
	Socket sock;
	ServerSocket servSock;
	DataInputStream is;
	DataOutputStream os;
	Logger logger;
	
	void logMessage(int type) {
		switch(type) {
		case START:
			logger.log("Recieved \"START\"");
			break;
		case MYTURN:
			logger.log("Recieved \"MYTURN\"");
			break;
		case CHOSEN:
			logger.log("Recieved \"CHOSEN\"");
			break;
		case DRAW:
			logger.log("Recieved \"DRAW\"");
			break;
		case DECK:
			logger.log("Recieved \"Deck\"");
			break;
		case ROGER_THAT:
			logger.log("Recieved \"ROGER_THAT\"");
		}
	}
	
	public void run() {
		while(true) {
			try {
				int type = is.readInt();
				logMessage(type);
				if(type == START) {
					sendMessage(ROGER_THAT, new int[0]);
				}else if(type == MYTURN) {
					int[] chosen = {is.readInt(), is.readInt(), is.readInt(), is.readInt()};
					game.window.sortDescending(chosen);
					if(game.programStatus.equals("Host: in game"))
						game.clientChosenCards = chosen;
					else
						game.serverChosenCards = chosen;
					
					game.window.animatePlay(ScreenHandler.YOU);
				}else if(type == CHOSEN) {
					int[] list = {is.readInt(), is.readInt(), is.readInt()};
					if((game.programStatus.equals("Host: in game")))
						game.clientChosenCards = list;
					else
						game.serverChosenCards = list;
					game.window.animateChoose(ScreenHandler.YOU);
				}else if(type == DRAW) {
					game.window.animateDraw(ScreenHandler.YOU);
					game.gameStatus = "my turn";
				}else if(type == DECK) {
					game.drawPile = new Deck();
					for(int a = 0; a < 52; a++) {
						try {
							game.drawPile.stackCard(new BicycleCard(is.readInt(), is.readInt()));
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					sendMessage(ROGER_THAT, new int[0]);
					game.stopMessage(true, false);
				}else 
					
				if(type == ROGER_THAT) {
					if(game.programStatus.equals("Host: in game") && game.gameStatus.equals("waiting")) {
						if(game.waitingOn.equals("start confirmation")) {
							game.waitingOn = "deck confirmation";
							game.dealAsHost();
						}else if(game.waitingOn.equals("deck confirmation"))
							new Thread(() -> game.animateDeal(PalmTreeGame.HOST)).start();
					}
				}
			} catch (IOException e) {
				ArrayList<String> msg = new ArrayList<>();
				msg.add("Error");
				msg.add("IOException");
				msg.add("Press \"Ok\" to quit.");
				game.show(msg, game.BU_OK, () -> System.exit(1));
			}
		}
	}
	
	public synchronized void sendMessage(int type, int[] otherInfo) {
		logSend(type);
		try {
			os.writeInt(type);
			for(int i : otherInfo)
				os.writeInt(i);
			os.flush();
		} catch (IOException e) {
			ArrayList<String> msg = new ArrayList<>();
			msg.add("Error");
			msg.add("IOException");
			msg.add("Press \"Ok\" to quit.");
			game.show(msg, game.BU_OK, () -> game.stopHost());
		}
	}
	
	void logSend(int type) {
		switch(type) {
		case START:
			logger.log("Sending \"START\"");
			break;
		case MYTURN:
			logger.log("Sending \"MYTURN\"");
			break;
		case CHOSEN:
			logger.log("Sending \"CHOSEN\"");
			break;
		case DRAW:
			logger.log("Sending \"DRAW\"");
			break;
		case DECK:
			logger.log("Sending \"Deck\"");
			break;
		case ROGER_THAT:
			logger.log("Sending \"ROGER_THAT\"");
		}
		
	}

	public InGameMessageHandler(PalmTreeGame g) {
		game = g;
		sock = g.sock;
		servSock = g.servSock;
		is = g.is;
		os = g.os;
		logger = g.logger;
		start();
	}
	
}

class Logger{
	
	void log(String msg) {
		System.out.println("Palm Tree Game - " + game.programStatus + " - " + game.gameStatus + " - " + new Timestamp(System.currentTimeMillis()) + ": " + msg);
	}
	
	PalmTreeGame game;
	
	Logger(PalmTreeGame g){
		System.out.println("Palm Tree Game Logger: starting...");
		game = g;
	}
}
