package thePackage;

import java.awt.Font;
import java.io.PrintStream;

import javax.swing.*;

public class ConsoleHandler extends JTextArea{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9036491305161197113L;
	
	private LogListener logger;
	
	public ConsoleHandler() {
		super(7, 120);
		logger = new LogListener(this);
		Logger.setDefaultOutputStream(logger);
		GlobalVariables.anonymousLogger.setLocalOutputStream(logger);
		Errors.l.setLocalOutputStream(logger);
		setEditable(false); setFocusable(false);
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		setText("");
	}
	
}

class LogListener extends PrintStream{
	
	private ConsoleHandler console;

	public LogListener(ConsoleHandler c) {
		super(System.out);
		this.console = c;
	}
	
	@Override
	public void println(String ln) {
		if(GlobalVariables.VERBOSE)
			System.out.println(ln);
		
		String text = console.getText();
		int numLines = 0;
		for(int a = 0; a < text.length(); a++)
			if(text.charAt(a) == '\n')
				numLines++;
		if(numLines != 0)
			numLines++;
		else if(!text.equals(""))
			numLines = 1;
		
		if(numLines == 0) {
			console.setText(ln);
		}else if(numLines < 7){
			console.setText(text + '\n' + ln);
		}else {
			String text2 = "";
			for(int a = text.indexOf('\n')+1; a < text.length(); a++)
				text2+=text.charAt(a);
			console.setText(text2);
		}
		numLines++;
	}
	
}