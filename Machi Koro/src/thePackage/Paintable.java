package thePackage;

import java.awt.Graphics;

public interface Paintable {
	/**
	 * This method paints the component onto the screen
	 * @param g
	 */
	void paintIt(Graphics g);
	/**
	 * This one returns a boolean indicating whether or not to remove this object from the screen
	 * @return
	 */
	boolean shouldRemove();
}