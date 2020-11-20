package thePackage;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class Dice implements Paintable {

	private boolean rolling;
	private int roll = -1;
	private double velocity;
	private double x, y;
	private double theta;
	
	private static final int initialVelocity = 30;
	private static final int acceleration = 1;
	
	private final BufferedImage[] images;
	private BufferedImage currentImage;
	
	public Dice(String color) throws IllegalArgumentException {
		if(!color.equals("blue") && !color.equals("green"))
			throw new IllegalArgumentException("Must enter \"green\" or \"blue\" as the argument to this constructor.");
		images = new BufferedImage[12];
		int b = 0;
		for(int a = 0; a < 6; a++) {
			BufferedImage img = CardImages.getImage("dice_" + (a + 1) + "_" + color + ".png");
			images[b++] = img;
			BufferedImage img2= CardImages.getImage("dice_" + (a + 1) + "_slanted_" + color + ".png");
			images[b++] = img2;
		}
		
		rolling = false;
	}
	
	private double num;
	
	@Override
	public void paintIt(Graphics g) {
		if(rolling) {
			x+=velocity * Math.cos(theta);
			y+=velocity * Math.sin(theta);
			velocity-=acceleration;
			if(velocity <= 0) {
				rolling = false;
				currentImage = images[2 * roll];
			}else {
				if(num++ % 2 == 0)
					currentImage = images[(int) (2 * (int) (6 * Math.random()) + (num%4)/2)];
				if(num > 10000)
					num = 0;
			}
		}
		if(currentImage != null)
			g.drawImage(currentImage, (int) (x - currentImage.getWidth()), (int) (y - currentImage.getHeight()), null);
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}

	/**
	 * @return {@code true} if the dice is in the rolling state. {@code false} otherwise.
	 */
	public boolean isRolling() {
		return rolling;
	}

	/**
	 * This method rolls the dice object and returns the value of the roll as an integer between {@code 1} and {@code 6}.
	 * This method blocks until the roll is complete.
	 * @param x the x to roll from.
	 * @param y the y to roll from.
	 * @param theta the angle in radians to roll.
	 * @return the roll.
	 * @throws InterruptedException if any thread interrupted the current thread before or while the current thread was waiting for a notification. The interrupted status of the current thread is not cleared when this exception is thrown.
	 */
	public int roll(int x, int y, double theta) throws InterruptedException {
		this.x = x; this.y = y; this.theta = theta;
		num = 0;
		roll = (int) (6 * Math.random());
		rolling = true;
		velocity = initialVelocity;
		currentImage = images[2 * (int) (6 * Math.random())];
		while(rolling)
			if(Thread.currentThread().isInterrupted())
				throw new InterruptedException();
		return roll + 1;
	}

	/**
	 * This method rolls the dice object and returns the value of the roll as an integer between {@code 1} and {@code 6}.
	 * This method does not block.
	 * @param x the x to roll from.
	 * @param y the y to roll from.
	 * @param theta the angle in radians to roll.
	 * @return the roll.
	 */
	public int rollWithoutBlocking(int x, int y, double theta) {
		this.x = x; this.y = y; this.theta = theta;
		num = 0;
		roll = (int) (6 * Math.random());
		rolling = true;
		velocity = initialVelocity;
		currentImage = images[2 * (int) (6 * Math.random())];
		return roll + 1;
	}

}
