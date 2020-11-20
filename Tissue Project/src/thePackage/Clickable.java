package thePackage;

public interface Clickable {
	
	public void click(int x, int y);
	
	public default void moveCursor(int x, int y) {}
	
}
