package thePackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class StageDisplay implements Paintable {
	
	private static final int WIDTH = 200, HEIGHT = 100;
	public static final int ROLL = 0, EARN = 1, CONSTRUCTION = 2;
	public static final int RED = 0, GREEN = 1, BLUE = 2, PURPLE = 3;
	public static final Color[] colors = {FrameHandler.accent2,
											FrameHandler.accent4,
											FrameHandler.background2,
											FrameHandler.accent3};
	private int stage;
	private int color;
	private double yRelative;
	private int yRelativeTo, yRelativeFrom;
	private int timer;
	public static final int TIME_STOP = 20;
	private int x, y;
	
	public StageDisplay(int x, int y) {
		setStage(0);
		setColor(-1);
		setX(x);
		setY(y);
		yRelative = 0;
		yRelativeTo = 0;
		yRelativeFrom = 0;
		timer = -1;
	}
	
	/**
	 * This method pushes the display to the next stage or to the next color. If it is already the last stage,
	 * it loops around to the first stage and returns {@code true}. Otherwise it returns {@code false}.
	 * @return {@code true} if the turn completed. {@code false} otherwise.
	 */
	public boolean nextStage() {
		boolean b = true;
		boolean isNextTurn = false;
		if(stage == 0) {
			setStage(1);
			setColor(0);
		} else if(stage == 1) {
			setColor(color+1);
			if(color == 4) {
				setColor(-1);
				setStage(2);
			}else
				b = false;
		} else if(stage == 2) {
			setStage(0);
			isNextTurn = true;
		} else
			b = false;
		if(b)
			slide();
		return isNextTurn;
	}
	
	/**
	 * This method simply blocks until the slide is complete. If the thread is interrupted during the block,
	 * an {@code InterruptedException} is thrown. The interrupted status of the thread is cleared by the 
	 * exception.
	 * @throws InterruptedException if the current thread is interrupted.
	 */
	public void waitForSlide() throws InterruptedException {
		while(timer != -1)
			if(Thread.interrupted())
				throw new InterruptedException();
	}
	
	private void slide() {
		yRelativeTo = (int) (yRelative - HEIGHT);
		yRelativeFrom = (int) yRelative;
		timer = 0;
	}
	
	private static double dy(int timer) {
		double x1 = (double) timer / TIME_STOP;
		double x2 = (double) (timer + 1) / TIME_STOP;
		double y1 = (-Math.cos(Math.PI * x1) + 1) / 2;
		double y2 = (-Math.cos(Math.PI * x2) + 1) / 2;
		double dy = y2 - y1;
		return dy;
	}

	@Override
	public void paintIt(Graphics g) {
		g.setClip(x, y, WIDTH, HEIGHT);
		
		if(timer != -1) {
			double dy = dy(timer++) * (yRelativeTo - yRelativeFrom);
			yRelative+=dy;
			if(timer == TIME_STOP)
				timer = -1;
			int prevStage = stage - 1;
			if(prevStage == -1)
				prevStage = 2;
			
			BufferedImage i1 = CardImages.getImage("stage" + (prevStage + 1) + ".png");
			BufferedImage i2 = CardImages.getImage("stage" + (stage + 1) + ".png");
			
			g.drawImage(i1, x, y + (int) yRelative, WIDTH, HEIGHT, null);
			g.drawImage(i2, x, y + (int) yRelative + HEIGHT, WIDTH, HEIGHT, null);
		} else {
			yRelative = 0;
			BufferedImage i = CardImages.getImage("stage" + (stage + 1) + ".png");
			
			g.drawImage(i, x, y, WIDTH, HEIGHT, null);
			
			if(stage == 1) {
				g.setColor(Color.WHITE);
				g.fillRect((int) (18 + 47.5 * color) + x, 40 + y, 19, 19);
			}
		}
		g.setColor(Color.BLACK);
		g.drawRect(x, y, WIDTH, HEIGHT);
		
		g.setClip(null);
	}

	@Override
	public boolean shouldRemove() {
		return false;
	}

	/**
	 * @return the stage
	 */
	public int getStage() {
		return stage;
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(int stage) throws IllegalArgumentException {
		if(stage != 0 && stage != 1 && stage != 2)
			throw new IllegalArgumentException("The argument must be 1, 2, or 3: " + stage);
		this.stage = stage;
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

}
