package thePackage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public final class Establishments {
	
	private static HashMap<String, Establishment> establishments;
	private static HashMap<String, EstablishmentActionTemplate> actions;
	static Logger l;
	static Status s;
	
	public static Establishment getEstablishment(String key) {
		return establishments.get(key);
	}
	
	public static void createOriginalActions() {
		if(s == null)
			s = new Status("Establishments", "stable");
		if(l == null)
			l = new Logger(s);
		if(actions == null)
			actions = new HashMap<>();
		
		l.log("Creating actions for original game");
		
		EstablishmentActionTemplate get = new EstablishmentActionTemplate((args, modifiers, owner, roller) -> {
			int amount = Integer.parseInt(args[0]);
			boolean all = false, incramentable = false;
			for(char c : modifiers) {
				if(c == 'a')
					all = true;
				if(c == 'i')
					incramentable = true;
			}
			if(incramentable && owner.getShoppingMall().isOpen())
				amount++;
			GlobalVariables.frameHandler.bankCoinsObject().slideAway(owner.pos, amount);
		});
		
		EstablishmentActionTemplate steal = new EstablishmentActionTemplate((args, modifiers, owner, roller) -> {
			int amount = Integer.parseInt(args[0]);
			boolean incramentable = false, choosable = false, all = false;
			for(char c : modifiers) {
				if(c == 'i')
					incramentable = true;
				if(c == 'c')
					choosable = true;
				if(c == 'a')
					all = true;
			}
			if(owner == roller && !choosable)
				return;
			MachiKoroPlayer stealFrom;
			if(incramentable && owner.getShoppingMall().isOpen())
				amount++;
			if(all) {
				for(MachiKoroPlayer p : GlobalVariables.machiKoroGame.getPlayers())
					if(p != owner)
						p.stealCoins(amount, owner.pos);
				return;
			}else if(choosable)
				stealFrom = GlobalVariables.frameHandler.choosePlayer(owner, "Choose who you want to steal money from.");
			else
				stealFrom = roller;
//			owner.animateAddMoneyFromLocation(stealFrom.pos, stealFrom.stealMoney(amount));
			stealFrom.stealCoins(amount, owner.pos);
		});
		
		EstablishmentActionTemplate trade = new EstablishmentActionTemplate((args, modifiers, owner, roller) -> {
			MachiKoroPlayer tradeWith = GlobalVariables.frameHandler.choosePlayer(owner, "Choose a player to trade an establishment with.");
			String urEst = GlobalVariables.frameHandler.chooseEstablishment(owner, tradeWith, "Choose the establishment you want");
			String myEst = GlobalVariables.frameHandler.chooseEstablishment(owner, owner, "Choose the establishment you woule like to trade");
			owner.stealEstablishment(myEst, tradeWith.pos);
			tradeWith.stealEstablishment(urEst, owner.pos);
//			owner.animateAddEstablishmentFromLocation(tradeWith.pos, urEst);
//			tradeWith.removeEstablishment(urEst);
//			tradeWith.animateAddEstablishmentFromLocation(owner.pos, myEst);
//			owner.removeEstablishment(myEst);
		});
		
		EstablishmentActionTemplate getAll = new EstablishmentActionTemplate((args, modifiers, owner, roller) -> {
			int amount = Integer.parseInt(args[1]);
//			owner.animateAddMoneyFromBank(
//				amount * owner.getNumCardsType(args[0]));
			owner.addCoins(amount * owner.getNumCardsType(args[0]));
		});
		
		actions.put("get", get);
		actions.put("steal", steal);
		actions.put("trade", trade);
		actions.put("getAll", getAll);
	}
	
	public static void readFileAsEstablishments(String dir) throws IOException {
		l.log("Reading: " + dir);
		if(establishments == null)
			establishments = new HashMap<>();
		URL url = Establishments.class.getResource(dir);
		InputStream is1 = url.openStream();
		Scanner s1 = new Scanner(is1);
		int numCards = 0;
		while(s1.hasNextLine())
			if(s1.nextLine().equals("[card]"))
				numCards++;
		s1.close();
		
		InputStream is2 = url.openStream();
		Scanner s2 = new Scanner(is2);
		int currentCard = -1;
		String ln;
		while(s2.hasNextLine()) {
			ln = s2.nextLine();
			
			
			if(ln.equals("[card]")) {
				currentCard++;
				TemporaryEstablishment te = new TemporaryEstablishment();
				while(!(ln = s2.nextLine()).equals("[done]")) {
					String command = ln.substring(0, ln.indexOf(':'));
					String arguments = ln.substring(ln.indexOf(':') + 1);
					
					int numArgs;
					if(arguments.length() != 0) {
						numArgs = 0;
						boolean sameWord = false;
						for(int a = 0; a < arguments.length(); a++) {
							char c = arguments.charAt(a);
							if(c == ',')
								sameWord = false;
							else if(!sameWord) {
								numArgs++;
								sameWord = true;
							}
						}
					}else {
						numArgs = 0;
					}
//					
//					if(ln.contains(","))
//						for(int a = ln.indexOf(','); a != -1; a = ln.indexOf(',', a))
//							numArgs++;
//					else
//						numArgs = ln.equals(command + ':') ? 0 : 1;
					String[] args = new String[numArgs];
					int argNum = 0;
					String arg = "";
					if(numArgs > 1) {
						for(int a = ln.indexOf(':') + 1; a < ln.length(); a++) {
							char c = ln.charAt(a);
							if(c == ',') {
								args[argNum++] = arg;
								arg = "";
							}else {
								arg+=c;
							}
						}
						args[args.length - 1] = arg;
					}else if (numArgs == 1) {
						args[0] = ln.substring(ln.indexOf(':') + 1);
					}
					
//					String argv="[";
//					for(String s : args)
//						argv+=s + ',';
//					argv = argv.substring(0, argv.length() - 1) + ']';
//					l.log(argv);
					
					if(command.equals("name")) {
						te.setName(args[0]);
					}else if(command.equals("message")) {
						te.setMessage(ln.substring(ln.indexOf(':') + 1));
					}else if(command.equals("numbers")) {
						int[] nums = new int[args.length];
						for(int a = 0; a < nums.length; a++)
							nums[a] = Integer.parseInt(args[a]);
						te.setActivationNums(nums);
					}else if(command.equals("price")) {
						te.setPrice(Integer.parseInt(args[0]));
					}else if(command.equals("color")) {
						te.setColor(args[0]);
					}else if(command.equals("type")) {
						te.setType(args[0]);
					}else if(command.equals("id")) {
						te.setId(Integer.parseInt(args[0]));
					}else if(command.equals("do")) {
						ArrayList<EstablishmentAction> actions = new ArrayList<>();
						while(!(ln = s2.nextLine()).equals("}")) {
							char[] modifiers = new char[0];
							String actionAndModifiers = ln.substring(0, ln.indexOf(':'));
							for(int a = 0; a < actionAndModifiers.length() - 1; a++) {
								if(actionAndModifiers.charAt(a + 1) == ' ') {
									char[] nm = new char[modifiers.length + 1];
									for(int b = 0; b < modifiers.length; b++)
										nm[b] = modifiers[b];
									nm[modifiers.length] = actionAndModifiers.charAt(a);
									modifiers = nm;
								}else if(actionAndModifiers.charAt(a) != ' ') {
									a = actionAndModifiers.length();
								}
							}
							String action;
							if(modifiers.length != 0)
								action = actionAndModifiers.substring(actionAndModifiers.lastIndexOf(' ') + 1);
							else
								action = actionAndModifiers;
							
							if(arguments.length() != 0) {
								numArgs = 0;
								boolean sameWord = false;
								for(int a = 0; a < arguments.length(); a++) {
									char c = arguments.charAt(a);
									if(c == ',')
										sameWord = false;
									else if(!sameWord) {
										numArgs++;
										sameWord = true;
									}
								}
							}else {
								numArgs = 0;
							}

							args = new String[numArgs];
							argNum = 0;
							arg = "";
							if(numArgs > 1) {
								for(int a = ln.indexOf(':') + 1; a < ln.length(); a++) {
									char c = ln.charAt(a);
									if(c == ',') {
										args[argNum++] = arg;
										arg = "";
									}else {
										arg+=c;
									}
								}
								args[args.length - 1] = arg;
							}else if (numArgs == 1) {
								args[0] = ln.substring(ln.indexOf(':') + 1);
							}
							actions.add(Establishments.actions.get(action).createEstablishmentAction(args, modifiers));
						}
						te.setActions(actions.toArray(new EstablishmentAction[0]));
					}
				}
				establishments.put(te.getName(), te.finish());
			}
			GlobalVariables.frameHandler.loadingBar.setFullness(((double) currentCard) / numCards);
			
		}
		if(s2 != null)
			s2.close();
		l.log("Finished reading: " + dir);
		GlobalVariables.frameHandler.loadingBar.setShouldRemove(true);
	}
	
}

class Establishment{

	private String name;
	private String message;
	private int[] activationNums;
	private int price;
	private String color;
	private String type;
	private int id;
	private EstablishmentAction[] actions;
	private HashMap<String, BufferedImage> images;
	
	protected Establishment(String name, String message, int[] activationNums, int price, String color, String type, int id, EstablishmentAction[] actions) {
		setName(name); setMessage(message); setActivationNums(activationNums); setPrice(price); setColor(color); setType(type); setId(id); setActions(actions);
		images = new HashMap<>();
		images.put("north", CardImages.getImage(name + " - south.png"));
		images.put("south", CardImages.getImage(name + " - north.png"));
		images.put("east",  CardImages.getImage(name + " - west.png" ));
		images.put("west",  CardImages.getImage(name + " - east.png" ));
	}
	
	protected Establishment() {
		
	}
	
	public BufferedImage getImage(String pos) {
		return images.get(pos);
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
	protected void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	protected void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the activationNums
	 */
	public int[] getActivationNums() {
		return activationNums;
	}
	/**
	 * @param activationNums the activationNums to set
	 */
	protected void setActivationNums(int[] activationNums) {
		this.activationNums = activationNums;
	}
	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	protected void setPrice(int price) {
		this.price = price;
	}
	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	protected void setColor(String color) {
		this.color = color;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	protected void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the actions
	 */
	public EstablishmentAction[] getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	protected void setActions(EstablishmentAction[] actions) {
		this.actions = actions;
	}
	
	public void act(MachiKoroPlayer owner, MachiKoroPlayer roller) {
		for(EstablishmentAction a : actions)
			a.act(owner, roller);
	}
	
	@Override
	public String toString() {
		return "Establishment:" + getName();
	}
	
}

class TemporaryEstablishment extends Establishment{

	public TemporaryEstablishment(String name, String message, int[] activationNums, int price, String color, String type, int id, EstablishmentAction[] actions) {
		super(name, message, activationNums, price, color, type, id, actions);
	}
	
	public TemporaryEstablishment() {
		super();
	}
	
	/**
	 * @param name the name to set
	 */
	protected void setName(String name) {
		super.setName(name);
	}
	/**
	 * @param message the message to set
	 */
	protected void setMessage(String message) {
		super.setMessage(message);
	}
	/**
	 * @param activationNums the activationNums to set
	 */
	protected void setActivationNums(int[] activationNums) {
		super.setActivationNums(activationNums);
	}
	/**
	 * @param price the price to set
	 */
	protected void setPrice(int price) {
		super.setPrice(price);
	}
	/**
	 * @param color the color to set
	 */
	protected void setColor(String color) {
		super.setColor(color);
	}
	/**
	 * @param type the type to set
	 */
	protected void setType(String type) {
		super.setType(type);
	}
	/**
	 * @param id the id to set
	 */
	protected void setId(int id) {
		super.setId(id);
	}
	/**
	 * @param actions the actions to set
	 */
	protected void setActions(EstablishmentAction[] actions) {
		super.setActions(actions);
	}
	
	public Establishment finish() {
		return new Establishment(getName(), getMessage(), getActivationNums(), getPrice(), getColor(), getType(), getId(), getActions());
	}
	
	@Override
	public String toString() {
		return "TemporaryEstablishment:" + getName();
	}
}

interface EstablishmentCommand{
	void act(String[] args, char[] modifiers, MachiKoroPlayer owner, MachiKoroPlayer roller);
}

class EstablishmentActionTemplate{
	private EstablishmentCommand command;
	public EstablishmentActionTemplate(EstablishmentCommand command){
		this.command = command;
	}
	/**
	 * @return the command
	 */
	public EstablishmentCommand getCommand() {
		return command;
	}
	public EstablishmentAction createEstablishmentAction(String[] args, char[] modifiers) {
		return new EstablishmentAction(args, modifiers, command);
	}
}

class EstablishmentAction{
	private String[] args;
	private char[] modifiers;
	private EstablishmentCommand command;
	public EstablishmentAction(String[] args, char[] modifiers, EstablishmentCommand command) {
		this.args = args;
		this.modifiers = modifiers;
		this.command = command;
	}
	/**
	 * @return the args
	 */
	public String[] getArgs() {
		return args;
	}
	/**
	 * @return the modifiers
	 */
	public char[] getModifiers() {
		return modifiers;
	}
	/**
	 * @return the command
	 */
	public EstablishmentCommand getCommand() {
		return command;
	}
	public void act(MachiKoroPlayer owner, MachiKoroPlayer roller) {
		command.act(args, modifiers, owner, roller);
	}
}



class EstablishmentNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4641065752659295823L;
	
	String att;
	
	public EstablishmentNotFoundException(String att) {
		this.att = att;
	}
	
	public String toString() {
		return "EstablishmentNotFoundException:" + att;
	}
	
}