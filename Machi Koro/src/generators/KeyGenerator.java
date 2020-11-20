package generators;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class KeyGenerator implements KeyListener{

	public static void main(String[] args) {
		KeyGenerator kg = new KeyGenerator();
		kg.start();
	}
	
	void start() {
		JFrame frame = new JFrame("BOI");
		frame.addKeyListener(this);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Key:" + e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
