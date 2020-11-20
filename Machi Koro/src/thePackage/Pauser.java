package thePackage;

import java.util.ArrayList;

public class Pauser extends LoggingObject{
	private ArrayList<Boolean> paused;
	private static int numPausers;
	
	public Pauser() {
		l = new Logger("Pauser " + numPausers++, "bruh");
		paused = new ArrayList<>();
	}
	
	/**
	 * This method pauses the current thread until this object's {@code unpause()} method is called.
	 * @throws InterruptedException if the thread is interrupted while waiting. This exception clears the 
	 * interrupted status of the thread.
	 */
	public void pause() throws InterruptedException {
		l.log("pausing");
		synchronized(paused) {
			int curr = paused.size();
			paused.add(true);
			while(paused.get(curr))
				if(Thread.interrupted())
					throw new InterruptedException();
		}
	}
	
	/**
	 * This method unpauses all threads currently waiting because of calling the {@code pause} method on this 
	 * object. 
	 */
	public void unpause() {
		l.log("unpausing");
		synchronized(paused) {
			for(int a = 0; a < paused.size(); a++)
				paused.set(a, false);
			paused = new ArrayList<>();
		}
	}
	
	/**
	 * This method tells if there are currently any threads paused on this object. 
	 * @return true if there are threads paused on this object. 
	 */
	public boolean isPaused() {
		synchronized(paused) {
			return !paused.isEmpty();
		}
	}

}
