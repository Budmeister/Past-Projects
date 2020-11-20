package thePackage;

import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Game {
	
	GameType game;
	GameFrame frame;
	MessageSender messenger;
	
	boolean DEBUG;
	
	public Game(String[] args) throws FileNotFoundException {
		if(args.length != 0) {
			if(args[0].equals("-debug"))
				DEBUG = true;
			else
				Logger.setDefaultOutputStream(new PrintStream(new File(args[0])));
		}else {
			Logger.setDefaultOutputStream(System.out);
			DEBUG = false;
		}
		
	}
	
	public void setGame(GameType g) {
		game = g;
		updateLocals();
	}
	
	public void setFrame(GameFrame f) {
		frame = f;
		updateLocals();
	}
	
	public void setMessenger(MessageSender m) {
		messenger = m;
		updateLocals();
	}
	
	private void updateLocals() {
		try {
			game.setFrame(frame);
			game.setMessenger(messenger);
		}catch(NullPointerException e) {} try {
			frame.setGame(game);
			frame.setMessenger(messenger);
		}catch(NullPointerException e) {} try {
			messenger.setGame(game);
			messenger.setFrame(frame);
		}catch(NullPointerException e) {}
	}
	
	public void start() {
		game.launch();
	}
	
}

abstract class GameType extends LoggingObject{
	
	protected GameFrame frame;
	protected MessageSender messenger;

	public abstract void setFrame(GameFrame f);
	
	public abstract void setMessenger(MessageSender m);
	
	public abstract void launch();
	
}

@SuppressWarnings("serial")
abstract
class GameFrame extends JFrame {
	
	protected GameType game;
	protected MessageSender messenger;
	protected Logger l;
	protected Status s;
	
	private Picture comp;
	private Paintable p;
	
	protected Timer t;
	
	class Picture extends JComponent {
		protected void paintComponent(Graphics g) {
			if(p != null)
				p.paintIt(g);
		}
	}
	
	/**
	 * Creates a full screen window
	 */
	public GameFrame(int width, int height, int pixelDepth) {
		super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		setUndecorated(true);
		setIgnoreRepaint(true);
		device.setFullScreenWindow(this);
		if(device.isDisplayChangeSupported()) {
			DisplayMode dm = new DisplayMode(width, height, pixelDepth, 0);
			DisplayMode[] dms = device.getDisplayModes();
			for(DisplayMode x : dms)
				if(x.getWidth() == dm.getWidth() && x.getHeight() == dm.getHeight() && x.getBitDepth() == dm.getBitDepth())
					device.setDisplayMode(dm);
		}
		constructor();
	}
	
	public GameFrame() {
		super();
		constructor();
	}
	
	public GameFrame(String title, boolean debug){
		super(title);
		if(!debug)
			constructor();
	}
	
	private void constructor() {
		comp = new Picture();
		add(comp);
	}
	
	public void applyDefaultSettings() {
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void applyDefaultFullScreenSettings() {
//		pack();
		setVisible(true);
	}
	
	public JComponent getComponent() {
		return comp;
	}
	
	protected void setComponent(Picture p) {
		comp = p;
	}
	
	public void setPainter(Paintable p) {
		this.p = p;
	}
	
	public void startWindowRefresh(int interval) {
		t = new Timer(interval, ae -> comp.repaint());
		t.start();
	}
	
	public void stopTimer() {
		t.stop();
	}
	
	public Timer getTimer() {
		return t;
	}
	
	
	public abstract void setGame(GameType g);
	
	public abstract void setMessenger(MessageSender m);
	
}

abstract class MessageSender extends LoggingObject{
	
	protected GameType game;
	protected GameFrame frame;
	
	private String connection;
	private int portNum;
	
	private Socket sock = null;
	
	private ServerSocket servSock = null;
	private Socket[] clientSocks = null;
	
	private int numConnections = 0;
	
	private int mode = 0;
	/** The final constant that represents a state of "host" */
	public static final int HOST = 1;
	/** The final constant that represents a state of "client" */
	public static final int CLIENT = 2;
	
	public MessageSender(int port) {
		portNum = port;
	}
	
	public MessageSender() {
		portNum = -1;
	}
	
	
	
	public void startAsHost() throws IOException {
		startAsHost(1);
	}
	
	public void startAsHost(int maxConnections) throws IOException {
		l.log("Attempting host start with a maximum of " + maxConnections + " connections");
		servSock = new ServerSocket(portNum);
		clientSocks = new Socket[maxConnections];
		mode = HOST;
		s.setStatus("created");
		s.setTitle("SocketHandler  - host");
		l.log("Host successful");
	}
	
	public Socket searchAsHost() throws IOException {
		if(mode != HOST)
			return null;
		if(numConnections >= clientSocks.length)
			return null;
		s.setStatus("searching");
		Socket cs = servSock.accept();
		clientSocks[numConnections] = cs;
		numConnections++;
		s.setStatus("stable");
		l.log("Successfully connected to " + cs.getInetAddress());
		return cs;
	}
	
	public void startAsClient(String connection) throws UnknownHostException, IOException {
		this.connection = connection;
		s.setStatus("searching");
		l.log("Attempting client start");
		sock = new Socket(connection, portNum);
		mode = CLIENT;
		s.setStatus("found");
		s.setTitle("SocketHandler - client");
		l.log("Connected to " + sock.getInetAddress());
	}
	
	public void kill() {
		if(sock != null)
			try {
				sock.close();
			}catch (IOException e) {}
		if(servSock != null)
			try {
				servSock.close();
			}catch (IOException e) {}
		for(Socket s : clientSocks)
			if(s != null) {
				try {
					s.close();
				}catch(IOException e) {}
			}else
				break;
		clientSocks = null;
		numConnections = 0;
		mode = 0;
	}
	

	public abstract void setGame(GameType g);
	
	public abstract void setFrame(GameFrame f);

	/**
	 * @return the connection
	 */
	public String getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}

	/**
	 * @return the portNum
	 */
	public int getPortNum() {
		return portNum;
	}

	/**
	 * @param portNum the portNum to set
	 */
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}



	/**
	 * @return the numConnections
	 */
	public int getNumConnections() {
		return numConnections;
	}



	/**
	 * @return the clientSock of the given index
	 */
	public Socket getClientSock(int i) {
		return clientSocks[i];
	}
	
	/**
	 * @return the inputStream of the clientSock at the given index
	 * @throws IOException 
	 */
	public InputStream getInputStream(int i) throws IOException {
		return clientSocks[i].getInputStream();
	}
	
	/**
	 * @return the outputStream of the clientSock at the given index
	 * @throws IOException 
	 */
	public OutputStream getOutputStream(int i) throws IOException {
		return clientSocks[i].getOutputStream();
	}

	/**
	 * @return the servSock
	 */
	public ServerSocket getServSock() {
		return servSock;
	}



	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}



	/**
	 * @return the sock
	 */
	public Socket getSock() {
		return sock;
	}



	/**
	 * @param sock the sock to set
	 */
	public void setSock(Socket sock) {
		this.sock = sock;
	}


}

class LoggingObject {
	
	protected Logger l;
	protected Status s;
	
}