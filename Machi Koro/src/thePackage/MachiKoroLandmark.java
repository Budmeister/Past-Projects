package thePackage;

import decks.*;


abstract public class MachiKoroLandmark extends Card {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6080257486515998303L;
	protected String name;
	protected String message;
	protected int price;
	protected int owner;
	protected boolean open = false;
	
	public boolean isOpen() {
		return open;
	}
	
	public void open() {
		open = true;
	}
}

class TrainStation extends MachiKoroLandmark {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7205488713462457549L;

	TrainStation() {
		name = "Train Station";
		message = "You may roll 1 or 2 dice.";
		price = 4;
	}
}

class ShoppingMall extends MachiKoroLandmark {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1185326242561219477L;

	ShoppingMall() {
		name = "Shopping Mall";
		message = "Each of your COFFEE and SHOP establishments earn +1 coin.";
		price = 10;
	}
}

class AmusementPark extends MachiKoroLandmark {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2285834228523308523L;

	AmusementPark() {
		name = "Amusement Park";
		message = "If you roll doubles, take another turn.";
		price = 16;
	}
}

class RadioTower extends MachiKoroLandmark {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5309477304414555085L;

	RadioTower() {
		name = "Radio Tower";
		message = "Ones every turn, you can choose to re-roll your dice.";
		price = 22;
	}
}