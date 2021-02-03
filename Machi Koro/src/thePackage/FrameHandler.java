package thePackage;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class FrameHandler extends GameFrame implements Paintable, MouseListener, MouseMotionListener, KeyListener, MessageReceiver{
	
	MachiKoroGame game;
	MessageHandler messenger;
	
	/** A background color that is a light blue. */
	public static final Color background1 = new Color(98, 202, 225);
	/** A background color that is a darker blue but not as dark as {@link FrameHandler}{@code .accent1}. */
	public static final Color background2 = new Color(0, 173, 220);
	/** An accent color that is a dark blue. */
	public static final Color accent1 = new Color(29, 119, 178);
	/** A red accent color. */
	public static final Color accent2 = new Color(158, 40, 60);
	/** A purple accent color. */
	public static final Color accent3 = new Color(125, 58, 139);
	/** A green accent color. */
	public static final Color accent4 = new Color(80, 127, 66);
	private static HashMap<Integer, Font> fonts;
	/** 
	 * This method returns the default font for Machi Koro (Broadway) with the given size. 
	 * @param size the size of the desired font. 
	 * @return the default font. 
	 */
	static final Font getDefaultFont(int size) {
		if(fonts == null)
			fonts = new HashMap<>();
		Font f = fonts.get(size);
		if(f == null) {
			f = new Font("Broadway", Font.PLAIN, size);
			fonts.put(size, f);
		}
		return f;
	}
	
	private Color backgroundColor;
	
	public MessageDisplayer messageDisplayer;

	public MachiKoroScene scene;
	public static final int width = 1366, height = 768;
	public static double debugScaler = 0.75;
	public static int debugWidth = (int) (width * debugScaler), debugHeight = (int) (height * debugScaler);
	public ConsoleHandler console;
	
	/**
	 * Creates a {@link FrameHandler} object in the NORMAL mode. Do not use this constructor for debugging.
	 */
	public FrameHandler(){
		super(width, height, 32);
		s = new Status("FrameHandler", "constructing");
		l = new Logger(s);
		messageDisplayer = new MessageDisplayer();
		getComponent().setPreferredSize(new Dimension(width, height));
		setPainter(this);
		
		applyDefaultFullScreenSettings();
		s.setStatus("stable");
	}
	
	/**
	 * Creates a {@link FrameHandler} object in the DEBUG mode. Only use this constructor for the Debug mode.
	 * @param num the random number that represents this game of the {@link JFrame}.
	 */
	public FrameHandler(int num) {
		super("Window " + num, true);
		console = new ConsoleHandler();
		s = new Status("FrameHandler - DEBUG", "constructing");
		l = new Logger(s);
		messageDisplayer = new MessageDisplayer();
		setLayout(new BorderLayout());
		Picture comp = new Picture();
		comp.setPreferredSize(new Dimension(debugWidth, debugHeight));
		setComponent(comp);
		add(comp, BorderLayout.NORTH);
		add(console, BorderLayout.SOUTH);
		setPainter(this);
		
		applyDefaultSettings();
		s.setStatus("stable");
	}
	
	// GMK Animate the game in thePackage.FrameHandler.paintIt(Graphics) method
	public void paintIt(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		if(GlobalVariables.DEBUG) {
			((Graphics2D) g).scale(debugScaler, debugScaler);
		}
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		
		if(scene != null)
			scene.paintIt(g);
	}
	
	public static void showOKMessageBox(String title, String message) {
		JFrame f = new JFrame(title);
		f.setLayout(new BorderLayout());
		f.add(new Label(message), BorderLayout.NORTH);
		Button b = new Button("Ok");
		b.addActionListener(ae -> f.dispose());
		f.add(b, BorderLayout.SOUTH);
		
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	public void launchIntoMenu() {
		setScene(mainMenu(true));
	}
	
	public MachiKoroScene mainMenu(boolean motion) {
		return mainMenu(new MachiKoroScene(), motion);
	}
	
	Runnable waitTime = () -> {
		try {
			Thread.sleep(200);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	};
	
	public MachiKoroScene mainMenu(MachiKoroScene s, boolean motion) {
		this.s.setStatus("Main Menu");
		l.log("Creating Main Menu");
		s.setWidth(width); s.setHeight(height); s.setBackground(background2);
		Grouping buttons = new Grouping() {
			RoundButton solo, multiplayer, howto, about, quit;

			@Override
			public void paintIt(Graphics g) {
				if(solo != null)
					solo.paintIt(g);
				if(multiplayer != null)
					multiplayer.paintIt(g);
				if(howto != null)
					howto.paintIt(g);
				if(about != null)
					about.paintIt(g);
				if(quit != null)
					quit.paintIt(g);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
			public void show() {
				l.log("Showing buttons");
				solo = new RoundButton("green", "thin", 100, height / 2 - 100, 200, 100); solo.setText("Solo");
				solo.setFontSize(40); solo.addActionListener(ae -> game.startGameSolo());
				if(motion)
					waitTime.run();
				multiplayer = new RoundButton("blue", "wide", 400, height / 2 - 100, 200, 50); multiplayer.setText("Multiplayer");
				multiplayer.addActionListener(ae -> game.startGameMultiplayer());
				if(motion)
					waitTime.run();
				howto = new RoundButton("purple", "wide", 325, height / 2, 200, 50); howto.setText("How to");
				if(motion)
					waitTime.run();
				about = new RoundButton("light_blue", "wide", 250, height / 2 + 100, 200, 50); about.setText("About");
				if(motion)
					waitTime.run();
				quit = new RoundButton("red", "wide", 175, height / 2 + 200, 200, 50); quit.setText("Quit");
				quit.addActionListener(ae -> System.exit(0));
			}
			
			public void press(int mouseX, int mouseY) {
				if(solo != null)
					solo.press(mouseX, mouseY);
				if(multiplayer != null)
					multiplayer.press(mouseX, mouseY);
				if(howto != null)
					howto.press(mouseX, mouseY);
				if(about != null)
					about.press(mouseX, mouseY);
				if(quit != null)
					quit.press(mouseX, mouseY);
			}
			
			public void release(int mouseX, int mouseY) {
				if(solo != null)
					solo.release(mouseX, mouseY);
				if(multiplayer != null)
					multiplayer.release(mouseX, mouseY);
				if(howto != null)
					howto.release(mouseX, mouseY);
				if(about != null)
					about.release(mouseX, mouseY);
				if(quit != null)
					quit.release(mouseX, mouseY);
			}
			
			public void updateImage(int mouseX, int mouseY) {
				if(solo != null)
					solo.updateImage(mouseX, mouseY);
				if(multiplayer != null)
					multiplayer.updateImage(mouseX, mouseY);
				if(howto != null)
					howto.updateImage(mouseX, mouseY);
				if(about != null)
					about.updateImage(mouseX, mouseY);
				if(quit != null)
					quit.updateImage(mouseX, mouseY);
			}
			
			public Grouping go() {
				if(!motion)
					show();
				return this;
			}
			
		}.go();
		Paintable logo = new Paintable(){
			int y = -109, x = 50, lo = 25;
			double v = 0, gr = 1.75;
			BufferedImage img = CardImages.getImage("logo2.png");
			int ticker = 0;

			@Override
			public void paintIt(Graphics g) {
				g.drawImage(img, x, y, null);
				v+=gr;
				y+=v;
				if(y >= lo) {
					v*=-.9;
					ticker++;
					if(ticker == 4) {
						v = 0;
						gr = 0;
						ticker++;
						new Thread(() -> buttons.show()).start();
					}
				}
			}
			
			@Override
			public boolean shouldRemove() {
				return false;
			}
			
			public Paintable construct(boolean motion) {
				if(!motion) {
					v = 0;
					gr = 0;
					y = lo;
				}
				return this;
			}
			
		}.construct(motion);
		
		Paintable back = new Paintable(){
			BufferedImage img = CardImages.getImage("background_image3.png");

			@Override
			public void paintIt(Graphics g) {
				g.drawImage(img, (width - img.getWidth()) / 2, height - img.getHeight(), null);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};
		
		Paintable man = new Paintable(){
			BufferedImage img = CardImages.getImage("man.png");
			int x = motion ? width + 500 : width - img.getWidth(), y = height - img.getHeight();
			int vel = 15;

			@Override
			public void paintIt(Graphics g) {
				g.drawImage(img, x, y, null);
				x-=vel;
				if(x < width - img.getWidth())
					vel = 0;
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};
		s.addPainter(back);
		s.addPainter(logo);
		s.addPainter(man);
		s.addPainter(buttons);
		return s;
	}
	
	class Grouping implements Paintable, Pressable, ImageUpdatable{

		@Override
		public void paintIt(Graphics g) {
			
		}

		@Override
		public boolean shouldRemove() {
			return false;
		}
		
		public void show() {
			
		}

		@Override
		public void press(int mouseX, int mouseY) {
			
		}

		@Override
		public void release(int mouseX, int mouseY) {
			
		}

		@Override
		public void updateImage(int mouseX, int mouseY) {
			
		}
		
	}
	
	LoadingBar loadingBar;
	class LoadingBar implements Paintable{
		private int x = 100, y = height - 100, w = width - 200, h = 20;
		private double fullness = 0;
		private boolean remove = false;
		
		@Override
		public void paintIt(Graphics g) {
			g.setColor(Color.GRAY);
			g.fillRect(x, y, w, h);
			g.setColor(Color.GREEN);
			g.fillRect(x, y, (int) (fullness * w), h);
			g.setColor(Color.DARK_GRAY);
			g.drawRect(x, y, w, h);
		}

		@Override
		public boolean shouldRemove() {
			return remove;
		}

		/**
		 * @return the fullness
		 */
		public double getFullness() {
			return fullness;
		}

		/**
		 * @param fullness the fullness to set
		 */
		public void setFullness(double fullness) {
			this.fullness = fullness;
		}

		public void setShouldRemove(boolean remove) {
			this.remove = remove;
		}
		
	}
	public MachiKoroScene gameLoading() {
		s.setStatus("Loading Game");
		MachiKoroScene s = new MachiKoroScene();
		l.log("Creating Loading Screen");
		s.setWidth(width); s.setHeight(height); s.setBackground(background2);
		GlobalVariables.gameType = "original";
		BufferedImage b = null;
		try {
			b = ImageIO.read(FrameHandler.class.getResource("/images/cover/background_image3.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
		final BufferedImage background = b;
		
		Paintable back = new Paintable() {

			@Override
			public void paintIt(Graphics g) {
				g.drawImage(background, (width - background.getWidth()) / 2, height - background.getHeight(), null);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};
		
		loadingBar = new LoadingBar();
		s.addPainter(back); s.addPainter(loadingBar);
		
		return s;
	}
	
	/**
	 * Creates the MachiKoroScene object to start a solo game
	 * @return the scene
	 */
	public void addSoloScene(MachiKoroScene currentScene) {
		s.setStatus("Solo Scene");
		MachiKoroScene soloScene = new MachiKoroScene();
		soloScene.setWidth(currentScene.getWidth());
		soloScene.setHeight(currentScene.getHeight());
		soloScene.setBackground(background1);
		
		Paintable title = new Paintable() {

			@Override
			public void paintIt(Graphics g) {
				g.setFont(getDefaultFont(100));
				g.setColor(Color.BLACK);
				int sw = g.getFontMetrics().stringWidth("Solo");
				g.drawString("Solo", (width - sw) / 2, 140);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};
		
		List<String> stock = new List<>(width / 2 - 317, 150, 300, 400);
		stock.addList(MachiKoroBot.getBotNames()); stock.setSelected(0);
		
		List<String> bots = new List<>(width / 2 + 17, 150, 300, 400);
		bots.setSelected(0);
		
		RoundButton add = new RoundButton("green", "thin", width / 2 - 317, 600, 200, 100); add.setText("Add"); add.setFontSize(40);
		add.addActionListener(ae -> {
			if(stock.getSelected() > stock.size() || stock.getSelected() < 0 || stock.isEmpty())
				return;
			bots.add(
					stock.remove(
								stock.getSelected()));
			if(stock.getSelected() >= stock.size())
				stock.setSelected(stock.size() - 1);
		});
		RoundButton remove = new RoundButton("red", "thin", width / 2 + 17, 600, 200, 100); remove.setText("Remove"); remove.setFontSize(40);
		remove.addActionListener(ae -> {
			if(bots.getSelected() > bots.size() || bots.getSelected() < 0 || bots.isEmpty())
				return;
			stock.add(
					bots.remove(
							bots.getSelected()));
			if(bots.getSelected() >= bots.size())
				bots.setSelected(bots.size() - 1);
		});
		
		RoundButton start = new RoundButton("green", "wide", width / 2 + 350, 600, 200, 50); start.setText("Start");
		start.addActionListener(ae -> {
			if(bots.isEmpty())
				return;
			game.addPlayer(new LocalPlayer(0));
			for(int a = 0; a < bots.size(); a++) {
				MachiKoroBot bot = MachiKoroBot.createRandomBot(a + 1);
				bot.setName(bots.get(a));
				game.addPlayer(bot);
			}
			game.startGameplay();
		});
		
		RoundButton back = new RoundButton("blue", "wide", width / 2 - 550, 600, 200, 50); back.setText("Back");
		back.addActionListener(ae -> {
			MachiKoroScene mainMenu = mainMenu(false);
			mainMenu.slideInFromTop(60);
			scene.addPainter(mainMenu);
		});
		
		soloScene.addPainter(title); soloScene.addPainter(stock); soloScene.addPainter(bots); soloScene.addPainter(add); soloScene.addPainter(remove); soloScene.addPainter(start); soloScene.addPainter(back);
		
		messageDisplayer.setText("Choose your bots.");
		soloScene.addPainter(messageDisplayer);
		
		currentScene.addPainter(soloScene);
		soloScene.slideInFromBottom(60);
	}
	
	public void addMultiplayerScene(MachiKoroScene currentScene) {
		s.setStatus("Multiplayer Scene");
		game.resetGameplayVariables();
		MachiKoroScene multiplayerScene = new MachiKoroScene();
		multiplayerScene.setWidth(width); multiplayerScene.setHeight(height);
		multiplayerScene.setBackground(accent2);
		
		Paintable title = new Paintable() {

			@Override
			public void paintIt(Graphics g) {
				g.setFont(getDefaultFont(100));
				g.setColor(Color.BLACK);
				int sw = g.getFontMetrics().stringWidth("Multiplayer");
				g.drawString("Multiplayer", (width - sw) / 2, 140);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};
		
		TabList mode = new TabList(0, 0); mode.stayCentered(new Point(width / 2, 200));
		
		Paintable portLabel = new Paintable() {

			@Override
			public void paintIt(Graphics g) {
				g.setFont(getDefaultFont(20));
				g.setColor(Color.BLACK);
				g.drawString("Port:", 440, 325);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};
		TextBox port = new TextBox(500, 300, 100);
		RoundButton generatePort = new RoundButton("green", "thin", 620, 300, 80, 40); generatePort.setText("Create");
		generatePort.addActionListener(ae -> port.setText("" + ((int) (Math.random() * 65536))));
		
		Paintable hostLabel = new Paintable() {

			@Override
			public void paintIt(Graphics g) {
				g.setFont(getDefaultFont(20));
				g.setColor(Color.BLACK);
				g.drawString("Host:", 440, 425);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};
		TextBox host = new TextBox(500, 400, 300); try { host.setText(InetAddress.getLocalHost().getHostAddress().toString());} catch (UnknownHostException e) {host.setText("Unable to get local IP");} host.setEditable(false);
		mode.addActionListener(ae -> {
			if(mode.getSelected() == 0)	{
				try { host.setText(InetAddress.getLocalHost().getHostAddress().toString());} catch (UnknownHostException e) {host.setText("Unable to get local IP");}
				host.setEditable(false);
			}else if(mode.getSelected() == 1) {
				host.setEditable(true);
			}
		});
		
		MachiKoroScene hostlocal = new MachiKoroScene();
		RoundButton starthost = new RoundButton("green", "wide", width / 2 + 350, 600, 200, 50); starthost.setText("Host");
		starthost.addActionListener(ae -> {
			try {
				messenger.startAsHost(Integer.parseInt(port.getText()));
			} catch (NumberFormatException | IOException e) {
				Errors.log(e);
				e.printStackTrace();
			}
			addWaitingScene(0, currentScene);
		});
		{
			hostlocal.addPainter(portLabel);
			hostlocal.addPainter(port);
			hostlocal.addPainter(generatePort);
			hostlocal.addPainter(hostLabel);
			hostlocal.addPainter(host);
			hostlocal.addPainter(starthost);
		}
		
		MachiKoroScene joinlocal = new MachiKoroScene();
		RoundButton startjoin = new RoundButton("light_blue", "wide", width / 2 + 350, 600, 200, 50); startjoin.setText("Join");
		startjoin.addActionListener(ae -> {
			try {
				messenger.startAsClient(host.getText(), Integer.parseInt(port.getText()));
			} catch (NumberFormatException | IOException e) {
				Errors.log(e);
				e.printStackTrace();
			}
		});
		{
			joinlocal.addPainter(portLabel);
			joinlocal.addPainter(port);
			joinlocal.addPainter(hostLabel);
			joinlocal.addPainter(host);
			joinlocal.addPainter(startjoin);
		}
		
		MachiKoroScene passnplay = new MachiKoroScene();
		RoundButton startpassnplay = new RoundButton("purple", "wide", width / 2 - 300, height / 2 - 75, 600, 150); startpassnplay.setText("Start Pass N Play"); startpassnplay.setFontSize(60);
		startpassnplay.addActionListener(ae -> addWaitingScene(2, currentScene));
		passnplay.addPainter(startpassnplay);
		
		mode.addTab("Host Local", hostlocal); mode.addTab("Join Local", joinlocal); mode.addTab("Pass N Play", passnplay);
		
		RoundButton back = new RoundButton("blue", "wide", width / 2 - 550, 600, 200, 50); back.setText("Back");
		back.addActionListener(ae -> {
			MachiKoroScene mainMenu = mainMenu(false);
			mainMenu.slideInFromTop(60);
			scene.addPainter(mainMenu);
		});
		
		multiplayerScene.addPainter(back);
		multiplayerScene.addPainter(title); multiplayerScene.addPainter(mode);
		
		messageDisplayer.setText("Create or join a local game or start Pass N Play.");
		multiplayerScene.addPainter(messageDisplayer);
		
		currentScene.addPainter(multiplayerScene);
		multiplayerScene.slideInFromBottom(60);
	}
	
	
	class Slot implements Paintable, Pressable, ImageUpdatable, Typable{
		
		MachiKoroPlayer player;
		int x, y;
		BufferedImage image = CardImages.getImage("player_slot_250x250.png");
		BufferedImage logo;
		Font font = getDefaultFont(30);
		boolean canDelete = false;
		RoundButton add, change, delete, button_ptr;
		int num;
		TextBox name;
		ActionListener al;
		
		Slot(int x, int y, int num, MachiKoroPlayer player){
			this.x = x; this.y = y;
			this.player = player; this.num = num;
			name = new TextBox(x, y + 300, 200); name.setText("Name");
			name.addActionListener(ae -> al.actionPerformed(ae));
			if(player instanceof LocalPlayer)
				logo = CardImages.getImage("keyboard_logo.png");
			else if(player instanceof RemotePlayer)
				logo = CardImages.getImage("remote_logo.png");
			else if(player instanceof MachiKoroBot)
				logo = CardImages.getImage("bot_logo.png");
			
			add = new RoundButton("light_blue", "wide", x + 10, y + 230, 180, 45); add.setText("Add");
			add.addActionListener(ae -> {
				this.player = new LocalPlayer(this.num);
				logo = CardImages.getImage("keyboard_logo.png");
				button_ptr = change;
				
				if(messenger.getState() == MessageHandler.State.HOST)
					for(Connection c : messenger.serverConnections) {
						Message ncm = new Message(c.getPlayer(), Message.Type.NEW_CONNECTION, new MessageInfo(num, ""), l);
						ncm.send();
					}
			});
			
			change = new RoundButton("purple", "wide", x + 10, y + 230, 180, 45); change.setText("Bot");
			change.addActionListener(ae -> {
				this.player = MachiKoroBot.createRandomBot(this.num);
				logo = CardImages.getImage("bot_logo.png");
				button_ptr = delete;
			});
			
			delete = new RoundButton("red", "wide", x + 10, y + 230, 180, 45); delete.setText("Delete");
			delete.addActionListener(ae -> {
				if(this.player != null && this.player instanceof RemotePlayer)
					((RemotePlayer) this.player).kick();
				if(messenger.getState() == MessageHandler.State.HOST)
					for(Connection c : messenger.serverConnections) {
						Message pkm = new Message(c.getPlayer(), Message.Type.PLAYER_KICKED, new MessageInfo(num, ""), l);
						pkm.send();
					}
				this.player = null;
				logo = null;
				delete.setText("Delete");
				button_ptr = add;
			});
			if(player == null)
				button_ptr = add;
			else if(player instanceof RemotePlayer)
				button_ptr = delete;
			else
				button_ptr = change;
		}
		
		@Override
		public void paintIt(Graphics g) {
			if(messenger.getState() == MessageHandler.State.CLIENT) {
				canDelete = false;
				button_ptr = delete;
			}
			g.setColor(Color.BLACK);
			g.drawRect(x, y, 200, 200);
			if(player != null) {
				g.drawImage(image, x - 25, y - 25, 250, 250, null);
				if(logo != null)
					g.drawImage(logo, x + 100 - logo.getWidth() / 2, y + 120 - logo.getHeight() / 2, null);
				
				g.setFont(font);
				String n;
				if(player instanceof MachiKoroBot)
					n = "Bot " + (num + 1);
				else
					n = "Player " + (num + 1);
				
				int w = g.getFontMetrics(font).stringWidth(n);
				g.drawString(n, x + 100 - w / 2, y + 20);
				
				if(player instanceof LocalPlayer)
					name.paintIt(g);
			}
			if(canDelete || button_ptr != delete)
				button_ptr.paintIt(g);
		}

		@Override
		public boolean shouldRemove() {
			return false;
		}

		@Override
		public void updateImage(int mouseX, int mouseY) {
			if(canDelete || button_ptr != delete)
				button_ptr.updateImage(mouseX, mouseY);
		}

		@Override
		public void press(int mouseX, int mouseY) {
			if(canDelete || button_ptr != delete)
				button_ptr.press(mouseX, mouseY);
			if(player != null) {
				if(player instanceof LocalPlayer)
					name.press(mouseX, mouseY);
			}
		}

		@Override
		public void release(int mouseX, int mouseY) {
			if(canDelete || button_ptr != delete)
				button_ptr.release(mouseX, mouseY);
			if(player != null) {
				if(player instanceof LocalPlayer)
					name.release(mouseX, mouseY);
			}
		}

		@Override
		public void type(int key) {
			if(player != null && player instanceof LocalPlayer)
				name.type(key);
		}

		@Override
		public void releaseKey(int key) {
			if(player != null && player instanceof LocalPlayer)
				name.releaseKey(key);
		}
		
		@Override
		public String toString() {
			return "Slot:x:" + x + ",y:" + ",player:" + player;
		}
		
		/**
		 * This method is used to set the player to a given {@link RemotePlayer} and set the buttons to
		 * the needed settings. 
		 * @param player the player to set.
		 */
		public void setRemotePlayer(RemotePlayer player) {
			this.player = player;
			logo = CardImages.getImage("remote_logo.png");
			button_ptr = delete;
			delete.setText("Kick");
		}
		
		public void setLocalPlayer(LocalPlayer player) {
			this.player = player;
			logo = CardImages.getImage("keyboard_logo.png");
			button_ptr = delete;
		}
	}
	
	Slot[] slots_for_waiting_scene = null;
	
	/**
	 * If the waiting scene is active because of calling the method {@link addWaitingScene()}, and 
	 * there is an open slot that is not holding a {@link MachiKoroPlayer}, this method adds the 
	 * given {@code RemotePlayer} to the waiting scene. This method is for adding a {@link RemotePlayer}. Adding other 
	 * players can be done with the {@link addLocalPlayerToWaitingScene()} method. If the waiting scene is not active, this 
	 * method throws an {@link IllegalStateException}.
	 * @param player the {@code RemotePlayer} to add.
	 * @throws IllegalStateException if the waiting scene is not active.
	 * @return the number representing the {@code Slot} the player got added to or {@code -1} if it was not added.
	 */
	public int addRemotePlayerToWaitingScene(RemotePlayer player) throws IllegalStateException{
		if(!s.getStatus().equals("Waiting Scene"))
			throw new IllegalStateException("Cannot add a player to the waiting scene unless the waiting scene is active.");
		int num = -1;
		for(int a = 0; a < slots_for_waiting_scene.length; a++)
			if(slots_for_waiting_scene[a].player == null) {
				slots_for_waiting_scene[a].setRemotePlayer(player);
				num = a;
				break;
			}
		return num;
	}
	

	/**
	 * If the waiting scene is active because of calling the method {@link addWaitingScene()}, and 
	 * there is an open slot that is not holding a {@link MachiKoroPlayer}, this method adds the 
	 * given {@code RemotePlayer} to the waiting scene. If the {@code preferred} {@link Slot} is not holding a player, it 
	 * adds the player to that {@code Slot}. This method is for adding a {@link RemotePlayer}. Adding 
	 * other players can be done with the {@link addLocalPlayerToWaitingScene()} method. If the waiting scene is not active, 
	 * this method throws an {@link IllegalStateException}.
	 * @param player the {@code RemotePlayer} to add.
	 * @throws IllegalStateException if the waiting scene is not active.
	 * @return the number representing the {@code Slot} the player got added to or {@code -1} if it was not added.
	 */
	public int addRemotePlayerToWaitingScene(RemotePlayer player, int preferred) throws IllegalStateException{
		if(slots_for_waiting_scene[preferred].player == null) {
			slots_for_waiting_scene[preferred].setRemotePlayer(player);
			return preferred;
		}else {
			int num = -1;
			for(int a = 0; a < slots_for_waiting_scene.length; a++)
				if(slots_for_waiting_scene[a].player == null) {
					slots_for_waiting_scene[a].setRemotePlayer(player);
					num = a;
					break;
				}
			return num;
		}
	}
	
	/**
	 * Adds the given {@code LocalPlayer} to the waiting scene if it is active. If the waiting
	 * scene is not active, this method throws an {@link IllegalStateException}. If there is no
	 * open slot to put the player in, nothing happens. 
	 * @param player the player to add.
	 * @return the number of the {@code Slot} the player was added to or {@code -1} if there were no open slots.
	 * @throws IllegalStateException if the waiting scene is not active. 
	 */
	public int addLocalPlayerToWaitingScene(LocalPlayer player) throws IllegalStateException{
		if(!s.getStatus().equals("Waiting Scene"))
			throw new IllegalStateException("Cannot add a player to the waiting scene unless the waiting scene is active.");
		int num = -1;
		for(int a = 0; a < slots_for_waiting_scene.length; a++)
			if(slots_for_waiting_scene[a].player == null) {
				slots_for_waiting_scene[a].setLocalPlayer(player);
				num = a;
				break;
			}
		return num;
	}

	/**
	 * Adds the given {@code LocalPlayer} to the waiting scene if it is active. If the waiting
	 * scene is not active, this method throws an {@link IllegalStateException}. If there is no
	 * open slot to put the player in, nothing happens. If the {@code preferred} {@link Slot} is
	 * available, it is put in that {@code Slot}. Otherwise, it is put in the {@code Slot} farthest
	 * to the left.
	 * @param player the player to add.
	 * @return the number of the {@code Slot} the player was added to or {@code -1} if there were no open slots.
	 * @throws IllegalStateException if the waiting scene is not active. 
	 */
	public int addLocalPlayerToWaitingScene(LocalPlayer player, int preferred) throws IllegalStateException{
		if(slots_for_waiting_scene[preferred].player == null) {
			slots_for_waiting_scene[preferred].setLocalPlayer(player);
			return preferred;
		}else {
			int num = -1;
			for(int a = 0; a < slots_for_waiting_scene.length; a++)
				if(slots_for_waiting_scene[a].player == null) {
					slots_for_waiting_scene[a].setLocalPlayer(player);
					num = a;
					break;
				}
			return num;
		}
	}
	
	/**
	 * If the waiting scene is active because of calling the method {@link addWaitingScene()}, and 
	 * the given player is in one or more of the slots in {@link slots_for_waiting_scene}, this method 
	 * sets that slot's {@code player} field to {@code null} and sets the {@code button_ptr} field to its 
	 * {@code add} field. This method can remove {@link RemotePlayer}s or any {@link MachiKoroPlayer}s. 
	 * If the waiting scene is not active, this method throws an {@link IllegalStateException}.
	 * @param player the {@code MachiKoroPlayer} to remove.
	 * @throws IllegalStateException if the waiting scene is not active.
	 */
	public void removePlayerFromWaitingScene(MachiKoroPlayer player) throws IllegalStateException{
		if(!s.getStatus().equals("Waiting Scene"))
			throw new IllegalStateException("Cannot remove a player from the waiting scene unless the waiting scene is active.");
		for(int a = 0; a < slots_for_waiting_scene.length; a++)
			if(slots_for_waiting_scene[a].player == player) {
				slots_for_waiting_scene[a].player = null;
				slots_for_waiting_scene[a].button_ptr = slots_for_waiting_scene[a].add;
			}
	}
	
	/**
	 * If the waiting scene is active because of calling the method {@link addWaitingScene()}, this method
	 * sets slot number {@code player}'s {@code player} field to {@code null} and sets the {@code button_ptr} 
	 * field to its {@code add} field. This method can remove {@link RemotePlayer}s or any {@link 
	 * MachiKoroPlayer}s. If the waiting scene is not active, this method throws and {@link 
	 * IllegalStateException}.
	 * @param player the number of the {@link Slot} whose player to set to null.
	 * @throws IllegalStateException if the waiting scene is not active.
	 */
	public void removePlayerFromWaitingScene(int player) throws IllegalStateException{
		if(!s.getStatus().equals("Waiting Scene"))
			throw new IllegalStateException("Cannot remove a player from the waiting scene unless the waiting scene is active.");
		slots_for_waiting_scene[player].player = null;
		slots_for_waiting_scene[player].button_ptr = slots_for_waiting_scene[player].add;
	}

	public void addWaitingScene(int mode, MachiKoroScene currentScene) {
		s.setStatus("Waiting Scene");
		MachiKoroScene waitingScene = new MachiKoroScene();
		waitingScene.setWidth(width); waitingScene.setHeight(height); waitingScene.setBackground(accent4);
		
		slots_for_waiting_scene = new Slot[4];
		for(int a = 0; a < 4; a++) {
			int x = 193 + 260 * a, y = 300;
			Slot s = new Slot(x, y, a, null);
			s.canDelete = true;
			s.al = ae -> {
				
			};
			slots_for_waiting_scene[a] = s;
			waitingScene.addPainter(s);
		}
		if(mode != 1) {
			LocalPlayer localPlayer = new LocalPlayer(0);
			game.getPlayers().add(localPlayer);
			Slot s = slots_for_waiting_scene[0];
			s.player = localPlayer;
			s.logo = CardImages.getImage("keyboard_logo.png");
			s.canDelete = false;
			s.button_ptr = s.delete;
		}
		

		
		Paintable title = new Paintable() {

			@Override
			public void paintIt(Graphics g) {
				g.setFont(getDefaultFont(100));
				g.setColor(Color.BLACK);
				int sw = g.getFontMetrics().stringWidth("Players");
				g.drawString("Players", (width - sw) / 2, 140);
			}

			@Override
			public boolean shouldRemove() {
				return false;
			}
			
		};

		RoundButton back = new RoundButton("blue", "wide", width / 2 - 550, 675, 200, 50); back.setText("Back");
		back.addActionListener(ae -> {
			messenger.kill();
			addMultiplayerScene(currentScene);
		});
		
		RoundButton start = new RoundButton("green", "wide", width / 2 + 350, 675, 200, 50); start.setText("Start");
		start.addActionListener(ae -> game.startGameplay());
		
		waitingScene.addPainter(back); waitingScene.addPainter(title);
		
		if(mode == 0 || mode == 2) {
			messageDisplayer.setText("Choose which players you want in your game.");
			waitingScene.addPainter(start);
		}else {
			messageDisplayer.setText("Wait until the host starts the game.");
		}
		waitingScene.addPainter(messageDisplayer);
		
		currentScene.addPainter(waitingScene);
		waitingScene.slideInFromBottom(60);
	}
	
	private String[] positions;
	
	public void setPlayerPositions() {
		int b = 0;
		int a = game.getTurn();
		if(!game.getPlayer(a).hasCenterPriority())
			return;
		
		do {
			game.getPlayer(a).pos = positions[b++];
			if(++a >= game.getPlayers().size())
				a = 0;
		}while(a != game.getTurn());
	}
	
	public MachiKoroScene coinSlides;
	public MachiKoroScene establishmentSlides;
	public Dice greenDie, blueDie;
	public EstablishmentMenu establishmentMenu;
	public StageDisplay stageDisplayer;

	public void addGameplayScene() {
		s.setStatus("Gameplay Scene");
		MachiKoroScene currentScene = this.scene;
		MachiKoroScene gameplayScene = new MachiKoroScene();
		gameplayScene.setWidth(width); gameplayScene.setHeight(height); gameplayScene.setBackground(background2);
		
		// GMK create gameplay scene
		bankCoins = new Coins(null);
		gameplayScene.addPainter(bankCoins);
		bankCards = new Cards(null);
		gameplayScene.addPainter(bankCards);
		
		String[] p4 = {"south", "west", "north", "east"};
		String[] p3 = {"south", "west", "north"};
		String[] p2 = {"south", "north"};
		switch(game.getPlayers().size()) {
		case 2: positions = p2; break;
		case 3: positions = p3; break;
		case 4: positions = p4; break;
		}
		for(MachiKoroPlayer p : game.getPlayers())
			gameplayScene.addPainter(p);
		setPlayerPositions();
		
		String p = "";
		for(MachiKoroPlayer pl : game.getPlayers())
			p+=pl.pos + ",";
		l.log("Positions: " + p);
		
		coinSlides = new MachiKoroScene();
		coinSlides.addPainter(new Paintable() {
			public void paintIt(Graphics g) {}
			public boolean shouldRemove() {return false;}
			public String toString() {return "coinSlides Placeholder";}
		});
		gameplayScene.addPainter(coinSlides);
		
		establishmentSlides = new MachiKoroScene();
		establishmentSlides.addPainter(new Paintable() {
			public void paintIt(Graphics g) {}
			public boolean shouldRemove() {return false;}
			public String toString() {return "establishmentSlides Placeholder";}
		});
		
		greenDie = new Dice("green");
		blueDie = new Dice("blue");
		gameplayScene.addPainter(greenDie);
		gameplayScene.addPainter(blueDie);
		
		establishmentMenu = new EstablishmentMenu();
		gameplayScene.addPainter(establishmentMenu);
		
		gameplayScene.addPainter(establishmentSlides);
		
//		game.getPlayer(0).getCards().slideAway("Bakery", "north");
		
//		new Thread(() -> {
// 			Thread.currentThread().setName("Tester Thread 1");
// 			try {
// 				MessageBox mb = new MessageBox("Ln 1:Blah blah blah blah blah blah\nLn 2: Blah blah blah blah blah", "No", "Yes");
// 				mb.addActionListener(ae -> l.log(ae.getActionCommand()));
// 				gameplayScene.addPainter(mb);
// 				Thread.sleep(2000);
// 				mb.enter();
// 				throw new Exception() {};
// 			} catch (InterruptedException e) {
// 				Thread.currentThread().interrupt();
// 			} catch (Exception e) {
// 				Thread.currentThread();
// 			}
// 		}).start();
		
		stageDisplayer = new StageDisplay(100, 100);
		gameplayScene.addPainter(stageDisplayer);
		
		RoundButton pause = new RoundButton("red", "thin", width - 125, height - 75, 100, 50); pause.setText("Pause");
		pause.addActionListener(ae -> game.pause());
		gameplayScene.addPainter(pause);
		
		messageDisplayer.setText("");
		gameplayScene.addPainter(messageDisplayer);
		
		currentScene.addPainter(gameplayScene);
		gameplayScene.slideInFromRight(60);
	}
	
	/**
	 * This method rolls one or both die and returns the roll value. This method blocks until the animation
	 * is complete. If {@code both} is {@code false}, only the green die will be rolled. Otherwise, both the green
	 * and the blue die will be rolled and their values will be added. 
	 * @param both whether or not to roll both die. 
	 * @param from the position from which to roll the die.
	 * @return the combined value of both die if {@code both} is true. Otherwise the value of the one die that
	 * was rolled. 
	 * @throws InterruptedException if this thread is interrupted while the die are being rolled. This 
	 * exception does not clear the interrupted status of the thread. 
	 */
	public int rollDie(boolean both, String from) throws InterruptedException {
		double lowerAngle = -Math.PI / 6, higherAngle = -Math.PI / 3;
		double dt = 0;
		switch(from) {
		case "north": dt = -Math.PI; break;
		case "south": break;
		case "east": dt = -Math.PI / 2; break;
		case "west": dt = Math.PI / 2;
		}
		lowerAngle+=dt;
		higherAngle+=dt;
		Point p = LocationCoordinates.getCoordinate(from, "Roll From");
		Integer roll = 0;
		if(both) {
			double angle2 = Math.random() * (higherAngle - lowerAngle) + lowerAngle;
			roll+=blueDie.rollWithoutBlocking(p.x, p.y, angle2);
		}
		double angle1 = Math.random() * (higherAngle - lowerAngle) + lowerAngle;
		roll+=greenDie.roll(p.x, p.y, angle1);
		
		l.log("Rolled a " + roll);
		return roll;
	}
	
	/**
	 * This method processes the all the red cards and waits for the animation to finish. Then it animates
	 * all the blues, but doesn't wait for them to finish. 
	 * @param roll the number of the roll to animate. 
	 * @param roller the {@code MachiKoroPlayer} object of the player whose roll to animate.
	 * @param onCompletion the {@code Runnable} to run on the completion of both reds and blues.
	 * @throws InterruptedException if the thread is interrupted while waiting on the reds. 
	 */
	public void processRoll(int roll, MachiKoroPlayer roller, Runnable onCompletion) throws InterruptedException{
		class IntegerHolder{
			int value;
			public IntegerHolder(int initValue) { value = initValue; }
		}
		IntegerHolder num = new IntegerHolder(0);
		Runnable r = () -> num.value++;
		for(MachiKoroPlayer p : game.getPlayers())
			p.getCards().processColor("red", roll, roller, r);
		
		while(num.value < game.getPlayers().size())
			if(Thread.interrupted())
				throw new InterruptedException();
		
		for(MachiKoroPlayer p : game.getPlayers())
			p.getCards().processColor("blue", roll, roller, r);
		new Thread(() -> {
			try {
				while(num.value < 2 * game.getPlayers().size())
					if(Thread.interrupted())
						throw new InterruptedException();
				onCompletion.run();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}).start();
	}
	
	
	

	/**
	 * @return the background
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackgroundColor(Color background) {
		this.backgroundColor = background;
	}

	/**
	 * This method pauses the game so a player can select another player. This method does not return until a player is chosen.
	 * @param chooser
	 * @param msg
	 * @return the MachiKoroPlayer object of the chosen player
	 */
	public MachiKoroPlayer choosePlayer(MachiKoroPlayer chooser, String msg) {
		// GMK Create MachiKoroPlayer choosePlayer(MachiKoroPlayer, String)
		return null;
	}
	
	/**
	 * This method pauses the game so a player can select an establishment from a player's establishments. This method does not return until an establishment is chosen.
	 * @param chooser
	 * @param chosen
	 * @param msg
	 * @return the String object that is the type of the chosen establishment
	 */
	public String chooseEstablishment(MachiKoroPlayer chooser, MachiKoroPlayer chosen, String msg) {
		// GMK MachiKoroEstablishment chooseEstablishment(MachiKoroPlayer, MachiKoroPlayer, String)
		return null;
	}

	@Override
	public void setGame(GameType g) {
		game = (MachiKoroGame) g;
	}

	@Override
	public void setMessenger(MessageSender m) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(scene != null)
			scene.press(getX(e), getY(e));
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(scene != null)
			scene.release(getX(e), getY(e));
	}
	
	public int getX(MouseEvent e) {
		if(GlobalVariables.DEBUG)
			return (int) (e.getX() / debugScaler) - 10;
		else
			return e.getX();
	}
	
	public int getY(MouseEvent e) {
		if(GlobalVariables.DEBUG)
			return (int) (e.getY() / debugScaler) - 41;
		else
			return e.getY();
	}
	
//	class Money implements Paintable{
//		private String to;
//		private String from;
//		private final int amount;
//		private int timer;
//		private int length;
//		private Runnable onArrival;
//		
//		Money(String to, String from, int amount) {
//			this.to = to;
//			this.from = from;
//			this.amount = amount;
//			timer = 0;
//			length = 10;
//		}
//		
//		void setLength(int l) {
//			length = l;
//		}
//		
//		public void paintIt(Graphics g) {
//			Point t = LocationCoordinates.getCoordinate(to, "money");
//			Point f = LocationCoordinates.getCoordinate(from, "money");
//			int x = (int) ((timer / length) * (t.getX() - f.getX()) + f.getX());
//			int y = (int) ((timer / length) * (t.getY() - f.getY()) + f.getY());
//			int w = 25, h = 25;
//			g.setColor(new Color(189, 114, 39));
//			g.fillOval(x - w / 2, y - w / 2, w, h);
//			g.setColor(new Color(127, 76, 41));
//			w = 20; h = 20;
//			g.fillOval(x - w / 2, y - w / 2, w, h);
//			g.setColor(Color.WHITE);
//			g.setFont(new Font("Broadway", 20, Font.PLAIN));
//			g.drawString(""+amount, x - 5, y - 10);
//			timer++;
//		}
//		
//		public boolean shouldRemove() {
//			if(timer >= length)
//				onArrival.run();
//			return (timer >= length);
//		}
//
//		/**
//		 * @return the onArrival
//		 */
//		public Runnable getOnArrival() {
//			return onArrival;
//		}
//
//		/**
//		 * @param onArrival the onArrival to set
//		 */
//		public void setOnArrival(Runnable onArrival) {
//			this.onArrival = onArrival;
//		}
//	}
//	
//	class EstSlide implements Paintable{
//		private String to;
//		private String from;
//		private String type;
//		private int timer;
//		private int length;
//		private Runnable onArrival;
//		BufferedImage img;
//		EstSlide(String to, String from, String type) {
//			this.to = to;
//			this.from = from;
//			this.type = type;
//			timer = 0;
//			length = 10;
//			img = Establishments.getEstablishment(type).getImage();
//		}
//		
//		void setLength(int l) {
//			length = l;
//		}
//		
//		public void paintIt(Graphics g) {
//			Point t = LocationCoordinates.getCoordinate(to, type);
//			Point f = LocationCoordinates.getCoordinate(from, type);
//			int x = (int) ((timer / length) * (t.getX() - f.getX()) + f.getX());
//			int y = (int) ((timer / length) * (t.getY() - f.getY()) + f.getY());
//			g.drawImage(img, x, y, null);
//			timer++;
//		}
//		
//		public boolean shouldRemove() {
//			if(timer >= length)
//				onArrival.run();
//			return (timer >= length);
//		}
//
//		/**
//		 * @return the onArrival
//		 */
//		public Runnable getOnArrival() {
//			return onArrival;
//		}
//
//		/**
//		 * @param onArrival the onArrival to set
//		 */
//		public void setOnArrival(Runnable onArrival) {
//			this.onArrival = onArrival;
//		}
//		
//	}

//	public void animateMoneyTransfer(String to, String from, int amount) {
//		scene.addPainter(new Money(to, from, amount));
//	}
//	
//	public void animateMoneyTransfer(String to, String from, int amount, Runnable onArrival) {
//		Money m = new Money(to, from, amount);
//		m.setOnArrival(onArrival);
//		scene.addPainter(m);
//	}
	
	private Coins bankCoins;
	private Cards bankCards;
	
	public Coins bankCoinsObject() {
		return bankCoins;
	}
	
	public Cards bankCardsObject() {
		return bankCards;
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}
	
	public static void rotate(Graphics go, String pos, Point p) {
		Graphics2D g = (Graphics2D) go;
		double theta = 0;
		switch(pos) {
		case "north":
			theta = Math.PI;
			break;
		case "south":
			return;
		case "east":
			theta = 3 * Math.PI / 2;
			break;
		case "west":
			theta = Math.PI / 2;
		}
		g.rotate(theta, p.x, p.y);
	}
	
	public static void rotateBack(Graphics go, String pos, Point p) {
		Graphics2D g = (Graphics2D) go;
		double theta = 0;
		switch(pos) {
		case "north":
			theta = -Math.PI;
			break;
		case "south":
			return;
		case "east":
			theta = -3 * Math.PI / 2;
			break;
		case "west":
			theta = -Math.PI / 2;
		}
		g.rotate(theta, p.x, p.y);
	}

	/**
	 * @return the scene1
	 */
	public MachiKoroScene getScene() {
		return scene;
	}

	/**
	 * @param scene1 the scene1 to set
	 */
	public void setScene(MachiKoroScene scene) {
		this.scene = scene;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(scene != null)
			scene.updateImage(getX(e), getY(e));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(scene != null)
			scene.type(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(scene != null)
			scene.releaseKey(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void receive(Message m) {
		Message.Type type = m.getType();
		switch(type) {
		case BEGIN: // Client
			addGameplayScene();
			break;
		case BUY:
			break;
		case CHECK_UP:
			break;
		case ITS_YOUR_TURN:
			break;
		case NEW_CONNECTION:
			break;
		case PLAYER_WON:
			break;
		case ROGER:
			break;
		case ROLL:
			break;
		case TRADE:
			break;
		default:
			break;
		}
	}

}

class MessageDisplayer implements Paintable{
	
	private String text;
	private Color color;
	
	private int screenWidth = 1366, screenHeight = 768;
	private int y = screenHeight - 10;
	private int size = 20;
	
	public MessageDisplayer() {
		text = "";
		color = Color.BLACK;
	}
	
	@Override
	public void paintIt(Graphics g) {
		g.setColor(color);
		g.setFont(FrameHandler.getDefaultFont(size));
		FontMetrics f = g.getFontMetrics();
		int w = f.stringWidth(text), h = f.getAscent();
		
		g.drawString(text, (screenWidth - w) / 2, y - h / 2);
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public boolean shouldRemove() {
		return false;
	}
	
}