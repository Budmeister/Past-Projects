package thePackage;

import java.awt.event.KeyEvent;

public interface Typable {
	
	static String getKeyNameFromKeyCode(int key) {
		return KeyEvent.getKeyText(key);
	}
	
	void type(int key);
	
	void releaseKey(int key);
	
}
