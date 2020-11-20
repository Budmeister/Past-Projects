package thePackage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageHandler extends LoggingObject{
	
	MachiKoroGame game;
	FrameHandler frame;
	
	enum State{
		HOST,
		CLIENT,
		DYING,
		DEAD
		
	}
	
	private State state;

	public Connection clientConnection;
	public LocalPlayer clientPlayer;

	public ArrayList<Connection> serverConnections;
	private ServerSocket servSock;
	
	MessageHandler(){
		s = new Status("SocketHandler", "DEAD");
		l = new Logger(s);
		setState(State.DEAD);
		l.log("MessageSender created with no port number");
	}
	
	public void startAsHost(int port) throws IOException {
		setState(State.HOST);
		servSock = new ServerSocket(port);
		serverConnections = new ArrayList<>();
		startSearch();
		startCheckup();
	}
	
	private boolean searching = false;
	
	/**
	 * This method creates a new thread that searches for connections on the designated port until 
	 * stopSearching() gets called. Once 3 connections are made, this method calls stopSearching(). 
	 * When a connection is made, a message is sent to the MachiKoroGame object that a connection is 
	 * made with a NonSerializableMessageInfo object holding that player's Connection as the MessageInfo. 
	 * The MachiKoroGame object is expected to call tiePlayerToConnection(MachiKoroPlayer, Connection) 
	 * to tie a MachiKoroPlayer object to the new connection. 
	 * @throws IllegalStateException if this object is not in the HOST state
	 */
	private void startSearch() throws IllegalStateException{
		if(state != State.HOST)
			throw new IllegalStateException("This method can only be called after startAsHost(int) has been called.");
		searching = true;
		new Thread(() -> {
			while(!Thread.currentThread().isInterrupted() && searching) {
				try {
					Connection c = new Connection(servSock.accept());
					serverConnections.add(c);
					c.startReceiving();
					Message m = new Message(null, Message.Type.NEW_CONNECTION, new NonSerializableMessageInfo(serverConnections.size() - 1, "", c), l);
					m.send();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(getNumConnections() > 3)
					stopSearching();
			}
		}).start();
	}
	
	public void tiePlayerToConnection(RemotePlayer player, Connection connection) {
		connection.setPlayer(player);
	}
	
	public void stopSearching() {
		searching = false;
	}
	
	/**
	 * This method closes the {@link Connection} object corresponding to {@code index} and starts 
	 * the search for connections again if it is not already searching.
	 * @param index the number of the connection to remove.
	 * @throws IllegalStateException if the {@code MessageHandler} object is not in the {@code HOST} state.
	 */
	public void kick(int index) throws IllegalStateException{
		if(state != State.HOST)
			throw new IllegalStateException("Cannot kick a connection unless the MessageHandler is in the HOST state");
		Connection con = serverConnections.get(index);
		con.close();
		serverConnections.remove(index);
		if(!isSearching() && frame.s.getStatus().equals("Waiting Scene"))
			startSearch();
	}
	
	/**
	 * This method closes the given {@link Connection} object, removes it from the list of 
	 * {@code Connection}s and starts the search again if it is not already searching. 
	 * @param con the connection to remove.
	 * @throws IllegalStateException if the {@code MessageHandler} object is not in the {@code HOST} state.
	 */
	public void kick(Connection con) throws IllegalStateException{
		if(state != State.HOST)
			throw new IllegalStateException("Cannot kick a connection unless the MessageHandler is in the HOST state");
		con.close();
		serverConnections.remove(con);
		if(!isSearching())
			startSearch();
	}
	
	public boolean isSearching() {
		return searching;
	}
	
	/**
	 * Returns the number of connections this {@link MessageHandler} object has if it is in the {@code HOST} state.
	 * @return the number of connections.
	 * @throws IllegalStateException if the {@code MessageHandler} is not in the {@code HOST} state.
	 */
	public int getNumConnections() throws IllegalStateException{
		if(serverConnections == null)
			throw new IllegalStateException("This method can only be invoked in the HOST state.");
		return serverConnections.size();
	}
	
	public void startAsClient(String connectTo, int portNum) throws UnknownHostException, IOException {
		Socket s = new Socket(connectTo, portNum);
		clientConnection = new Connection(s);
		clientConnection.startReceiving();
		setState(State.CLIENT);
	}
	
	public void kill() {
		if(state == State.DEAD)
			return;
		if(serverConnections != null)
			for(int a = 0; a < serverConnections.size(); a++)
				kick(a);
		setState(State.DYING);
		searching = false;
		if(clientConnection != null)
			clientConnection.close();
		serverConnections = null;
		setState(State.DEAD);
	}
	
	/**
	 * 
	 * @param m the message to be delivered
	 * @throws IOException if there is an IOException on delivery
	 * @throws IllegalStateException if the MessageHandler object has a state of DEAD or DYING
	 */
	public void deliver(Message m) throws IOException, IllegalStateException{
		if(state == State.DEAD || state == State.DYING)
			throw new IllegalStateException("Can not deliver a message if the MessageHandler object is DEAD or DYING.");
		if(GlobalVariables.VERBOSE) {
			StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
			l.log("(" + ste.getFileName() + ":" + ste.getLineNumber() + ")" + " - Delivering message: " + m);
		}
		SendablePlayerWrapper ad = m.getAddressee();
		if(state == State.HOST) {
			if(ad == null) {
				GlobalVariables.machiKoroGame.receive(m);
			}else if(ad.equals(SendablePlayerWrapper.EVERYONE)) {
				for(MachiKoroPlayer p : game.getPlayers())
					if(p instanceof LocalPlayer || p instanceof MachiKoroBot)
						p.receive(m);
				for(Connection c : serverConnections)
					if(!c.getPlayer().l.getStatusObject().getTitle().equals(m.getOrigin()))
						c.send(m);
				GlobalVariables.machiKoroGame.receive(m);
			}else if(ad.getPlayer(game) instanceof RemotePlayer){
				((RemotePlayer) ad.getPlayer(game)).getConnection().send(m);
			}else {
				ad.getPlayer(game).receive(m);
			}
		}else if(state == State.CLIENT){
			// If we are the client, there is no need to send the message
			// to the local MachiKoroGame object. The client's MachiKoroGame
			// object isn't nearly as involved as the host's.
			if(game.getPlayers().size() == 0) {
				if(m.getType() == Message.Type.ROGER)
					clientConnection.send(m);
				else
					game.receive(m);
			}else if(ad != null && ad.getPlayer(game) instanceof LocalPlayer) {
				ad.getPlayer(game).receive(m);
			}else
				clientConnection.send(m);
		}
		frame.receive(m);
	}

	public void setGame(GameType g) {
		game = (MachiKoroGame) g;
	}

	public void setFrame(GameFrame f) {
		frame = (FrameHandler) f;
	}
	
	private void setState(State state) {
		this.state = state;
		s.setStatus(state.toString());
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * This method is for the {@link MahiKoroGame} object to notify the {@link MessageHandler} object that
	 * a {@code ROGER} message was received from a connection. It removes that {@code Connection}
	 * object from the list of connections that are being waited on if it is there. If it isn't, it puts it into 
	 * a list of extra {@code ROGER}'d connections to be found later.
	 * @param m the message that was received.
	 * @throws IllegalStateException if the {@code MessageHandler} object is not in the {@code HOST} state.
	 * @throws IllegalArgumentException if a message was passed that was not a {@code ROGER} message.
	 * @throws ConnectionNotFoundException if the message's origin did not correspond to a connection object in {@code serverConnections}
	 */
	public void receivedRoger(Message m) throws IllegalStateException, IllegalArgumentException, ConnectionNotFoundException{
		if(state != State.HOST)
			throw new IllegalStateException("This method should not be called unless the MessageHandler object is in the HOST state.");
		if(m.getType() != Message.Type.ROGER)
			throw new IllegalArgumentException();
		String name = m.getOrigin();
		Connection origin = null;
		for(Connection c : serverConnections)
			if(c.getPlayer().l.getStatusObject().getTitle().equals(name))
				origin = c;
		if(origin == null)
			throw new ConnectionNotFoundException(name);
		if(checkupWaiters.contains(origin)) {
			checkupWaiters.remove(origin);
		}else {
			extraRogers.add(origin);
		}
	}
	
	private ArrayList<Connection> checkupWaiters;
	private ArrayList<Connection> extraRogers;
	private Thread checkupThread;
	private boolean checkingUp = false;
	public static final int CHECK_UP_INTERVAL = 2000;
	
	private Message createCheckupMessage(Connection c) {
		return new Message(c.getPlayer(), Message.Type.CHECK_UP, l);
	}
	
	private void startCheckup() throws IllegalStateException{
		if(state != State.HOST)
			throw new IllegalStateException("Cannot start checkup if not in host mode.");
		checkingUp = true;
		checkupWaiters = new ArrayList<>();
		extraRogers = new ArrayList<>();
		checkupThread = new Thread(() -> {
			try {
				while(!Thread.currentThread().isInterrupted() && checkingUp) {
					if(checkupWaiters.size() != 0) {
						for(int a = 0; a < checkupWaiters.size(); a++) {
							Connection c = checkupWaiters.get(a);
							boolean shouldHaveError = true;
							for(int b = 0; b < extraRogers.size(); b++) {
								Connection c2 = extraRogers.get(b);
								if(c == c2) {
									shouldHaveError = false;
									checkupWaiters.remove(c);
									extraRogers.remove(c2);
									break;
								}
							}
							if(shouldHaveError) {
								CheckupNotRespondedToException exception = new CheckupNotRespondedToException(c);
								ErrorInfo info = new ErrorInfo(c.getPlayer().number, c.getPlayer().l.getStatusObject().getTitle(), exception);
								Message message = new Message(null, Message.Type.ERROR, info, l);
								message.send();
								Errors.log(exception);
							}
						}
					}
					for(Connection c : serverConnections) {
						createCheckupMessage(c).send();
						checkupWaiters.add(c);
					}
					try {
						Thread.sleep(CHECK_UP_INTERVAL);
					}catch(InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			} catch (NullPointerException e) {
				// Exit the thread
			}
		});
		checkupThread.start();
	}
	
	private void stopCheckup() {
		checkingUp = false;
	}
	
}

/**
 * This exception indicates that a connection was sought for that did not exist.
 * @author josia
 *
 */
class ConnectionNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7801065393087442794L;
	
	private String connection;
	
	ConnectionNotFoundException(String connection){
		this.connection = connection;
	}
	
	public String attemptedConnection() {
		return connection;
	}
	
}

/**
 * This exception indicates that an error has occurred regarding the checkup system. 
 * @author josia
 * @see CheckupNotRespondedToException
 */
abstract class CheckupException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1271969154306554059L;
	
	protected Connection connection;
	
	public CheckupException(Connection c) {
		connection = c;
	}

	/**
	 * @return the connection that did not respond to the checkup.
	 */
	public Connection getConnection() {
		return connection;
	}
	
}

/**
 * This exception indicates that a connection did not respond to a {@code CHECK_UP} message with a 
 * {@code ROGER} message. This exception will never be thrown, but will be delivered to the 
 * {@link MachiKoroGame} object--via a message with an {@code ErrorInfo} object as its 
 * {@link MessageInfo}--when the {@link MessageHandler}'s {@code checkupThread} thread 
 * prepares to send out the {@code CHECK_UP} messages by making sure that all the {@code Connection}
 * objects have responded and one or more haven't. 
 * @author josia
 *
 */
class CheckupNotRespondedToException extends CheckupException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2597179729833574692L;

	public CheckupNotRespondedToException(Connection c) {
		super(c);
	}
	
	@Override
	public String toString() {
		return "CheckupNotRespondedToException: Connection " + connection + " did not respond to a checkup message.";
	}
	
}

class Connection implements Runnable{
	public final ObjectOutputStream out;
	public final ObjectInputStream in;
	public final Socket socket;
	private MachiKoroPlayer player;
	
	public Connection(Socket s) throws IOException{
		socket = s;
		out = new ObjectOutputStream(socket.getOutputStream()); out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * This method writes a message to this object's {@code ObjectOutputStream} then flushes it.
	 * @param m the message to send.
	 * @throws IOException if an IOException occurs.
	 */
	public void send(Message m) throws IOException {
		out.writeObject(m);
		out.flush();
	}
	
	/**
	 * This method waits until this connection is sent an object, then casts that object to Message and returns it.
	 * This method's only intended use is in this object's run() method from the Runnable interface to be put into a thread, but it can be called by any object.
	 * @return The Message object that was received.
	 * @throws IOException if there is an IOException while waiting.
	 * @throws ClassNotFoundException if the object that was received was not one of the known classes.
	 * @throws ClassCastException if the object that was received was not of type Message.
	 */
	public Message receive() throws IOException, ClassNotFoundException, ClassCastException {
		return (Message) in.readObject();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted() && searching) {
			try {
				Message m = receive();
				m.send();
			}catch(IOException e) {
				Message m = new Message(null, Message.Type.ERROR, new ErrorInfo(0, "", e), new Logger(this.toString(), "temporary"));
				GlobalVariables.machiKoroGame.receive(m);
				Errors.log(e);
				stopReceiving();
			}catch(ClassNotFoundException e) {
				Errors.log(e);
			}
		}
	}
	
	/**
	 * The thread that receives messages
	 */
	public Thread searchingThread;
	private boolean searching = false;
	
	/**
	 * First, this method gets rid of the old searchingThread if there is one. Then, it creates
	 * a new thread with this Connection object as the Runnable, so the run method of this
	 * object, which waits for a message to be sent, will be called in a new thread. 
	 */
	public void startReceiving() {
		if(searchingThread != null && !searchingThread.isInterrupted())
			searchingThread.interrupt();
		searching = true;
		searchingThread = new Thread(this);
		searchingThread.start();
	}
	
	/**
	 * This method ends the search for messages on this connection.
	 */
	public void stopReceiving() {
		searching = false;
	}
	
	/**
	 * This method ends this connection by closing its object streams and calling stopReceiving().
	 * After this method is called, this Connection object should not be used anymore.
	 */
	public void close() {
		stopReceiving();
		try {
			out.close();
		}catch(IOException e) {
		}catch(NullPointerException e) {}
		try {
			in.close();
		}catch(IOException e) {
		}catch(NullPointerException e) {}
		try {
			socket.close();
		}catch(IOException e) {
		}catch(NullPointerException e) {}
	}

	/**
	 * @return the player
	 */
	public MachiKoroPlayer getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(MachiKoroPlayer player) {
		this.player = player;
	}
	
	@Override
	public String toString() {
		if(player != null)
			return "[" + socket.getInetAddress() + ", " + player.getName() + "]";
		else
			return "[" + socket.getInetAddress() + ", null]";
	}
}

class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3406493521171633792L;
	
	/**
	 * The {@link SendablePlayerWrapper} object that holds the number of the player to whom the message is addressed. 
	 * If it is {@link null}, {@link send()} will deliver it to the local {@link MachiKoroGame} object if the {@link MessageHandler} object 
	 * is in the {@link SERVER} state or to the remote {@link MachiKoroGame} object if the {@link MessageHandler} object is in the 
	 * {@link CLIENT} state. All messages also get sent to the local and remote {@link FrameHandler} object.
	 * @see {@link MessageHandler.deliver()}
	 */
	private SendablePlayerWrapper addressee;
	private Type type;
	private String origin;
	private MessageInfo info;
	private State state;
	
	public Message(MachiKoroPlayer addressee, Type type, MessageInfo info, Logger origin) {
		this.type = type; setAddressee(addressee); this.info = info; this.origin = origin.getStatusObject().getTitle();
		state = State.NEW;
	}
	
	public Message(MachiKoroPlayer addressee, Type type, Logger origin) {
		this.type = type; setAddressee(addressee); this.origin = origin.getStatusObject().getTitle();
		state = State.NEW;
	}
	
	public Message(Type type, MessageInfo info, Logger origin) {
		this.type = type; setAddressee(null); this.info = info; this.origin = origin.getStatusObject().getTitle();
		state = State.NEW;
	}
	
	public Message(Type type, Logger origin) {
		this.type = type; this.origin = origin.getStatusObject().getTitle();
		state = State.NEW;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Returns the {@link SendablePlayerWrapper} object that holds the number of the player to whom the message is addressed. 
	 * If it is {@link null}, {@link send()} will deliver it to the local {@link MachiKoroGame} object if the {@link MessageHandler} object 
	 * is in the {@link SERVER} state or to the remote {@link MachiKoroGame} object if the {@link MessageHandler} object is in the 
	 * {@link CLIENT} state. All messages also get sent to the local and remote {@link FrameHandler} object.
	 * @see {@link MessageHandler.deliver()}
	 */
	public SendablePlayerWrapper getAddressee() {
		return addressee;
	}
	
	/**
	 * @param addressee the addressee to set
	 */
	private void setAddressee(MachiKoroPlayer addressee) {
		if(addressee == null)
			this.addressee = null;
		else
			this.addressee = new SendablePlayerWrapper(addressee);
	}
	
	/**
	 * @return the origin of the message
	 */
	public String getOrigin() {
		return origin;
	}
	
	/**
	 * @return the info
	 */
	public MessageInfo getInfo() {
		return info;
	}
	
	/**
	 * Sets this message's info variable to the given info variable. 
	 * @param info
	 * @throws IllegalStateException if this message has already been sent.
	 */
	public void setInfo(MessageInfo info) throws IllegalStateException{
		if(state == State.SENT)
			throw new IllegalStateException("The message has already been sent.");
		this.info = info;
	}
	
	/**
	 * Sends this message to the player to whom it was addressed. 
	 * If the player is a local player, it will sent it locally. 
	 * If the player is a remote player, it will send it via the {@link MessageHandler} object.
	 * If the addressee is {@code null}, it will deliver it to the {@code MachiKoroGame} object.
	 * If an {@link IOException} is thrown, this method logs it to the {@link Errors} logger and returns {@code 1}.
	 * Otherwise, it returns {@code 0}.
	 * @return {@code 1} if an {@code IOException} is thrown. {@code 0} otherwise.
	 * @throws IllegalStateException if this message has already been sent.
	 * @throws IllegalStateException if the MessageHandler object has a state of DEAD or DYING.
	 */
	public int send() throws IllegalStateException{
		if(state == State.SENT)
			throw new IllegalStateException("The message has already been sent.");
		int r = 0;
		try {
			GlobalVariables.messageHandler.deliver(this);
		}catch(IOException e) {
			r = 1;
		}
		state = State.SENT;
		return r;
	}

	enum State {
		NEW,
		SENT
	}
	
	enum Type {
		// General
		CHECK_UP,
		ROGER,
		PLAYER_WON,
		
		
		// Pre-game
		BEGIN,
		WELCOME,
		NEW_CONNECTION,
		PLAYER_KICKED,
		BYE,
		
		// Turn messages
		ITS_YOUR_TURN,
		ROLL,
		BUY,
		TRADE,
		
		// Local
		ERROR,
		
	}
	
	public String toString() {
		return "Message[Type=" + type + ",Addressee=" + addressee + ",MessageInfo=" + info + "]";
	}
	
}

class SendablePlayerWrapper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5930550963173615312L;
	public int playerNumber;
	public SendablePlayerWrapper(MachiKoroPlayer player) {
		playerNumber = GlobalVariables.machiKoroGame.getPlayers().indexOf(player);
	}
	
	public static final SendablePlayerWrapper EVERYONE = new SendablePlayerWrapper(-1);
	
	public SendablePlayerWrapper(int playerNumber) {
		this.playerNumber = playerNumber;
	}
	
	public MachiKoroPlayer getPlayer(MachiKoroGame game) {
		if(playerNumber == -1)
			return null;
		return game.getPlayer(playerNumber);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SendablePlayerWrapper))
			return false;
		return playerNumber == ((SendablePlayerWrapper) o).playerNumber;
	}
	
	public String toString() {
		if(GlobalVariables.machiKoroGame.getPlayers().isEmpty())
			return playerNumber == -1 ? "Everyone" : "Player " + playerNumber;
		MachiKoroPlayer player = getPlayer(GlobalVariables.machiKoroGame);
		return playerNumber == -1 ? "Everyone" : (player instanceof LocalPlayer ? "Local" : (player instanceof MachiKoroBot ? "Bot" : "Remote")) + "Player " + playerNumber;
	}
}

class MessageInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1545704534980500976L;
	
	public final int id;
	public final String stringPart;
	
	public MessageInfo(int id, String stringPart) {
		this.id = id;
		this.stringPart = stringPart;
	}
	
}

/**
 * This class is a {@link MessageInfo} class that is only for sending local messages.
 * It holds an object that does not necessarily implement the {@link Serializable} interface.
 * An object of this type can be passed to a {@link Message} object, but if the {@link object}
 * parameter is not Serializable, that message cannot be sent to a remote computer.
 * @author josia
 * @see {@link MessageInfo}
 */
class NonSerializableMessageInfo extends MessageInfo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1856413298513548798L;
	
	public Object object;
	
	public NonSerializableMessageInfo(int id, String stringPart, Object object) {
		super(id, stringPart);
		this.object = object;
	}
	
	public boolean isSerializable() {
		return object instanceof Serializable;
	}
	
}

class ErrorInfo extends NonSerializableMessageInfo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1265984398429119845L;

	public ErrorInfo(int id, String stringPart, Exception e) {
		super(id, stringPart, e);
	}
	
}

interface MessageReceiver{
	void receive(Message m);
}
