package testers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import thePackage.Paintable;

public class RandomThingTester {
	
	public static void main(String[] args) {
		printStackTrace();
	}
	
	static void print(Object o) {
		System.out.println(o);
	}

	static void printStackTrace() {
		StackTraceElement[] elems = Thread.currentThread().getStackTrace();
		for(StackTraceElement e : elems)
			System.out.println("(" + e.getFileName() + ":" + e.getLineNumber() + ")");
	}
}