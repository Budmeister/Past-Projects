package thePackage;

import java.awt.Graphics;
import java.util.ArrayList;

import thePackage.MotionEngine.Type;
//import static thePackage.GlobalVariables.frameHandler;
//import static thePackage.GlobalVariables.machiKoroGame;
//import static thePackage.GlobalVariables.messageHandler;

public class Slide implements Paintable{
	
	public double x, y, width, height;
	private ArrayList<SlideElement> elements;
	private SlideElement[] finishedElements;
	private int currentElement;
	private Paintable paintable;

	/**
	 * Constructs a new {@code Slide} object. 
	 * @param p the thing for this slide object to paint. 
	 * @param x the initial {@code x} value.
	 * @param y the initial {@code y} value.
	 * @param width the initial width.
	 * @param height the initial height.
	 */
	public Slide(Paintable p, int x, int y, int width, int height) {
		elements = new ArrayList<>();
		finishedElements = null;
		currentElement = -1;
		paintable = p;
		this.x = x; this.y = y; this.width = width; this.height = height;
	}
	
	/**
	 * This method animates the current element. If the current element's
	 * {@code isFinished()} method returns true, it switches to the next 
	 * element. 
	 */
	public final void paintIt(Graphics g) {
		if(shouldRemove())
			return;
		if(finishedElements != null) {
			finishedElements[currentElement].animate();
			if(finishedElements[currentElement].isFinished()) {
				finishedElements[currentElement++].run();
				if(currentElement < finishedElements.length)
					finishedElements[currentElement].setOriginalVars(this);
			}
		}
		if(paintable != null)
			paintable.paintIt(g);
	}
		
	public final boolean shouldRemove() {
		return currentElement >= finishedElements.length;
	}
	
	public final void addElement(SlideElement e) {
		elements.add(e);
	}
	
	public final SlideElement removeElement(int i) {
		return elements.remove(i);
	}
	
	public final SlideElement getElement(int i) {
		return elements.get(i);
	}
	
	public final void finish() {
		if(finishedElements == null && elements.size() != 0) {
			finishedElements = new SlideElement[elements.size()];
			elements.toArray(finishedElements);
			currentElement = 0;
			finishedElements[0].setOriginalVars(this);
		}
	}
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public int getWidth() {
		return (int) width;
	}
	
	public int getHeight() {
		return (int) height;
	}

}

abstract class SlideElement implements Runnable{
	
	protected Slide slide;
	protected Runnable onArrival;
	
	protected SlideElement(Slide s) {
		this.slide = s;
		onArrival = ()->{};
	}

	public void setOnArrival(Runnable oa) {
		this.onArrival = oa;
	}
	
	public final void run() {
		onArrival.run();
	}
	
	public void setOriginalVars() {
		setOriginalVars(slide);
	}
	
	public void setOriginalVars(Slide s) {}
	
	public abstract void animate();
	
	public abstract boolean isFinished();
	
	public int totalFramesToRun() {
		return -1;
	}
	
}

final class MotionEngine{
	
	enum Type{
		LINEAR,
		SINUSOIDAL,
		PARABOLIC,
		REVERSE_PARABOLIC,
	}
	
	public static double function(Type t, int current, int end) throws IllegalArgumentException{
		if(current > end)
			throw new IllegalArgumentException("The current value cannot be greater than the ending value.");
		if(current < 0)
			throw new IllegalArgumentException("The current value must be positive or 0.");
		switch(t) {
		case LINEAR:
			return ((double) current)/end;
		case PARABOLIC:
			return square(((double) current)/end);
		case REVERSE_PARABOLIC:
			return -square(((double) current)/end - 1) + 1;
		case SINUSOIDAL:
			return -regularCos(((double) current)/end) / 2 + .5;
		default:
			return 0;
		}
	}
	
	public static double getChangeInFunction(Type t, int current, int end, double OX, double NX) throws IllegalArgumentException{
		if(current >= end)
			throw new IllegalArgumentException("The current value must be less than the ending value.");
		if(current < 0)
			throw new IllegalArgumentException("The current value must be positive or 0.");
		if(t == Type.PARABOLIC)
			bruh();
		double dx = NX - OX;
		double dy = dx * (function(t, current + 1, end) - function(t, current, end));
		return dy;
	}static void bruh() {}
	
	private static double square(double arg0) {
		return Math.pow(arg0, 2);
	}
	
	private static double regularCos(double arg0) {
		return Math.cos(arg0 * Math.PI);
	}
	
	private MotionEngine() {}
	
}

class SimultaneousElement extends SlideElement{
	
	private SlideElement element1, element2;

	public SimultaneousElement(Slide s, SlideElement e1, SlideElement e2) throws IllegalArgumentException{
		super(s);
		if(e1.getClass() == e2.getClass())
			throw new IllegalArgumentException("The two given slide elements must be of different classes.");
		element1 = e1;
		element2 = e2;
	}
	
	@Override
	public void setOriginalVars(Slide s) {
		element1.setOriginalVars(s);
		element2.setOriginalVars(s);
	}

	@Override
	public void animate() {
		if(!element1.isFinished())
			element1.animate();
		if(!element2.isFinished())
			element2.animate();
	}

	@Override
	public boolean isFinished() {
		return element1.isFinished() && element2.isFinished();
	}
	
	@Override
	public int totalFramesToRun() {
		return Math.max(element1.totalFramesToRun(), element2.totalFramesToRun());
	}
	
}

class GrowElement extends SlideElement{
	
	private double NX, NY, NW, NH, OX, OY, OW, OH;
	private final int timer;
	private int time;
	private MotionEngine.Type motionType;
	
	
	public GrowElement(Slide s, int newWidth, int newHeight, int timer, Type motionType) {
		super(s);
		NW = newWidth;
		NH = newHeight;
		time = 0;
		this.timer = timer;
		this.motionType = motionType;
	}
	
	@Override
	public void setOriginalVars(Slide s) {
		OW = s.width;
		OH = s.height;
		OX = s.x;
		OY = s.y;
		NX = OX + OW / 2 - NW / 2;
		NY = OY + OH / 2 - NH / 2;
	}

	@Override
	public void animate() {
		slide.x+=getDX();
		slide.y+=getDY();
		slide.width+=getDW();
		slide.height+=getDH();
		time++;
	}
	
	private double getDX() {
		return MotionEngine.getChangeInFunction(motionType, time, timer, OX, NX);
	}
	
	private double getDY() {
		return MotionEngine.getChangeInFunction(motionType, time, timer, OY, NY);
	}
	
	private double getDW() {
		double dw = MotionEngine.getChangeInFunction(motionType, time, timer, OW, NW);
//		anonymousLogger.log(slide + "'s dw:" + dw);
		return dw;
	}
	
	private double getDH() {
		return MotionEngine.getChangeInFunction(motionType, time, timer, OH, NH);
	}
	
	@Override
	public boolean isFinished() {
		return time == timer;
	}
	
	@Override
	public int totalFramesToRun() {
		return timer;
	}
	
}

class MoveElement extends SlideElement{
	
	private double NX, NY, OX, OY;
	private final int timer;
	private int time;
	private MotionEngine.Type motionType;

	public MoveElement(Slide s, int x2, int y2, int timer, MotionEngine.Type motionType) {
		super(s);
		NX = x2;
		NY = y2;
		time = 0;
		this.timer = timer;
		this.motionType = motionType;
	}
	
	@Override
	public void setOriginalVars(Slide s) {
		OX = s.x;
		OY = s.y;
	}

	@Override
	public void animate() {
		slide.x+=getDX();
		slide.y+=getDY();
		time++;
	}
	
	private double getDX() {
		double dx = MotionEngine.getChangeInFunction(motionType, time, timer, OX, NX);
//		anonymousLogger.log(slide + "'s dx:" + dx);
		return dx;
	}
	
	private double getDY() {
		return MotionEngine.getChangeInFunction(motionType, time, timer, OY, NY);
	}

	@Override
	public boolean isFinished() {
		return time == timer;
	}
	
	@Override
	public int totalFramesToRun() {
		return timer;
	}
	
}

class DelayElement extends SlideElement{
	private final int timer;
	private int time;

	protected DelayElement(Slide s, int numSlides) {
		super(s);
		time = 0;
		timer = numSlides;
	}
	
	public void setOriginalVars(Slide s) {
	}

	@Override
	public void animate() {
		time++;
	}

	@Override
	public boolean isFinished() {
		return time == timer;
	}
	
}
