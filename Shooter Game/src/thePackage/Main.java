package thePackage;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

import arrays.*;
import camara.*;

public class Main implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, CustomGraphics, Action{
	static SCamara window;
	Array bullets = new Array(), targets = new Array(), powerups = new Array();
	int score = 0, power = 100, regenSpeed = 1;
	double life = 100;
	boolean showLife = false;
	String status = "menu";
	Array messageLines;
	String quickMessage;
	boolean dispQuickMessage = false;
	int quickMessageTimer = 0;
	boolean shaking = false;
	Array shakeInit;
	
	Random r = new Random();
	
	double[][] enemyPositions = {{60.0,0.0},
								 {0.0,-30},
								 {-60.0,0.0},
								 {-120.0,0.0},
								 {-180.0,-30.0},
								 {-240.0,0.0},
								 {-270.0,60.0},
								 {0.0,30.0},
								 {-90.0,60.0},
								 {-180.0,30.0}};

	
	static Cursor[] cursors = new Cursor[2];
	int[] enemies = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
	int[] enemyLife = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	int level = 0;
	int[] numEnemiesToSpawn = {0, 1, 2, 3, 5, 5, 0};
	Array obstacles = new Array();
	double mouseSensitivity = 0.25;
	
	int counter = 0;
	public static boolean DEBUG = false;
	public static boolean CHEATER = false;
	public static int WIDTH = 1200, HEIGHT = 700;

	public static void main(String[] args) {
		for(int a = 0; a < args.length; a++) {
			if(args[a].equals("-debug"))
				DEBUG = true;
			if(args[a].equals("-cheater"))
				CHEATER = true;
			if(args[a].equals("-width")) {
				try {
					a++;
					WIDTH = Integer.parseInt(args[a]);
				}catch(NumberFormatException e) {
					// yay
				}
			}
			if(args[a].equals("-height")) {
				try {
					a++;
					HEIGHT = Integer.parseInt(args[a]);
				}catch(NumberFormatException e) {
					// yay
				}
			}
		}
		
		new Main().start();
	}
	
	Main() {
		window = new SCamara("Shooter Game", WIDTH, HEIGHT, 1000);
		
		// Make the cursor invisible
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		Cursor regularCursor = window.window.getContentPane().getCursor();
		
		cursors[0] = blankCursor;
		cursors[1] = regularCursor;
		
	}
	
	void start() {
		System.out.println("Loading menu...");
		window.setBackground(new Color(0, 255, 0));
		
		window.addMouseListener(this);
		window.addAction(this);
		window.addCustomGraphics(this);
		
	}
	
	void play() {
		System.out.println("Starting game...");
		
		status = "run";

		int x = window.cam.width / 2;
		int y = window.cam.height / 2;
		try {
			Robot robot = new Robot();
			robot.mouseMove(x + window.cam.getLocationOnScreen().x - 8, y + window.cam.getLocationOnScreen().y - 31);
		} catch (AWTException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		window.window.getContentPane().setCursor(cursors[0]);
		
		if(DEBUG) {
			window.shapes.push(cube(100, -50, -50, -50));
		}
		if(CHEATER) {
			life = 10000;
			regenSpeed = 100;
		}
		
		addBackground2();
		addTarget();
		
//		new Thread(() -> {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			String message = "BRUH!";
//			showQuickMessage(message);
//		}).start();
		
		window.addMouseMotionListener(this);
		window.addKeyListener(this);
		window.addMouseWheelListener(this);
		
		window.setBackground(new Color(95, 142, 1));
		
	}
	
	void pause() {
		status = "paused";
		window.window.getContentPane().setCursor(cursors[1]);
		
	}
	
	void resume() {
		status = "run";
		window.window.getContentPane().setCursor(cursors[0]);

		int w = window.cam.width;
		int h = window.cam.height;
		try {
			Robot robot = new Robot();
			robot.mouseMove(w / 2 + window.cam.getLocationOnScreen().x - 8, h / 2 + window.cam.getLocationOnScreen().y - 31);
		} catch (AWTException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() < 0 && CHEATER)
			levelUp();
	}
	
	public void log(String name, String msg) {
		System.out.println(name + ": " + msg);
	}
	
	public void stop(String cause) {
		log("System", "Game stopped for reason: " + cause);
		System.exit(0);
	}
	
	
	void addBackground1() {
		log("System", "Adding background # 1");
		SShape background = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		Array nod = new Array();
		Array edg = new Array();
		Array fac = new Array();
		double w = 50000;
		double h = 10000;
		double d = 60000;
		int t = 4;
		
		{
			double[][] n = {{-w / 2, 0, d},
				  		    {-w / 2, h, d},
				  		    {w / 2, 0, d},
				  		    {w / 2, h, d},
				  		    {-w / 2, 0, 0},
				  		    {-w / 2, h, 0},
				  		    {w / 2, 0, 0},
				  		    {w / 2, h, 0}};
			int[][] e =    {{0, 1},
			  		  		{1, 3},
			  		  		{3, 2},
			  		  		{2, 1},
			  		  		{0, 4},
			  		  		{4, 5},
			  		  		{5, 1},
			  		  		{2, 6},
			  		  		{6, 7},
			  		  		{7, 3}};
			int[][] f =    {{0, 1, 3},
	  		  		  		{0, 2, 3},
	  		  		  		{0, 1, 5},
	  		  		  		{0, 4, 5},
	  		  		  		{2, 3, 7},
	  		  		  		{2, 6, 7}};
			for(int a = 0; a < t; a++) {
				int l = nod.length;
				for(int b = 0; b < n.length; b++) {
					nod.push(new Array());
					nod.i(b + l).A().push(n[b][0]);
					nod.i(b + l).A().push(n[b][1] + 10000 * a);
					nod.i(b + l).A().push(n[b][2]);
				}
				l = edg.length;
				for(int b = 0; b < e.length; b++) {
					edg.push(new Array());
					edg.i(b + l).A().push(e[b][0] + 8 * a);
					edg.i(b + l).A().push(e[b][1] + 8 * a);
				}
				l = fac.length;
				for(int b = 0; b < f.length; b++) {
					fac.push(new Array());
					for(int c = 0; c < 3; c++)
						fac.i(b + l).A().push(f[b][c] + 8 * a);
				}
			}
		}
		{
			int p = 4;
			double[][] n = {{-w / 2, t * h, 0},
							{w / 2, t * h, 0},
							{-w / 2, t * h, d / p},
							{w / 2, t * h, d / p}};
			int[][] e =    {{0, 1},
							{1, 3},
							{3, 2},
							{2, 1}};
			int[][] f =    {{0, 1, 3},
							{0, 2, 3}};
			int N = nod.length;
			for(int a = 0; a < f.length; a++)
				for(int b = 0; b < f[a].length; b++)
					f[a][b]+=N;
			for(int a = 0; a < p; a++) {
				int l = nod.length;
				for(int b = 0; b < n.length; b++) {
					nod.push(new Array());
					nod.i(b + l).A().push(n[b][0]);
					nod.i(b + l).A().push(n[b][1]);
					nod.i(b + l).A().push(n[b][2] + d / p * a);
				}
				l = edg.length;
				for(int b = 0; b < e.length; b++) {
					edg.push(new Array());
					edg.i(b + l).A().push(e[b][0]);
					edg.i(b + l).A().push(e[b][1]);
				}
				l = fac.length;
				for(int b = 0; b < f.length; b++) {
					fac.push(new Array());
					for(int c = 0; c < 3; c++)
						fac.i(fac.length - 1).A().push(f[b][c]);
				}
			}
		}
		Color[] col = new Color[edg.length];
		Color[] fco = new Color[fac.length];
		for(int a = 0; a < fco.length; a++) {
			fco[a] = new Color(0, 255, 255);
		}
		background.setNodes(nod);
		background.setColors(col);
		background.setEdges(edg);
		background.setFaces(fac);
		background.setFaceColors(fco);

		window.shapes.push(background);
	}
	
	void addBackground2() {
		log("System", "Adding background # 2");
		window.shapes.push(dodecahedron(100000, true));
		SShape platform = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		double[][] nod = {{5000, -10000, 5000},
						  {5000, -10000, -5000},
						  {-5000, -10000, 5000},
						  {-5000, -10000, -5000}};
		int[][] fac =  	 {{0, 1, 3, 2}};
		Color[] fco = {new Color(0, 0, 0)};
		platform.setNodes(nod);
		platform.setFaces(fac);
		platform.setFaceColors(fco);
		window.shapes.push(platform);
	}
	
	SShape dodecahedron(double r, boolean isCool) {
		return polyhedron12(r, isCool);
	}
	
	SShape dodecahedron(double r, double x, double y, double z, boolean isCool) {
		SShape dodecahedron = polyhedron12(r, isCool);
		moveShape(dodecahedron, x, y, z);
		return dodecahedron;
	}
	
	SShape polyhedron12(double r, boolean isCool) {
		SShape dodecahedron = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		final double PHI = (Math.sqrt(5) - 1) / 2, a = 1 / Math.sqrt(3), b = a / PHI, c = a * PHI;
		
		int[] list = {-1, 1};
		Array nod = new Array();
		for(int i : list) {
			for(int j : list) {
				{
					Array n = new Array();
					n.push(0.0);
					n.push(i * c * r);
					n.push(j * b * r);
					nod.push(n);
				}
				{
					Array n = new Array();
					n.push(i * c * r);
					n.push(j * b * r);
					n.push(0.0);
					nod.push(n);
				}
				{
					Array n = new Array();
					n.push(i * b * r);
					n.push(0.0);
					n.push(j * c * r);
					nod.push(n);
				}
				for(int k : list) {
					Array n = new Array();
					n.push(i * a * r);
					n.push(j * a * r);
					n.push(k * a * r);
					nod.push(n);
				}
			}
		}
		int[][] fac = {{0, 3, 1, 11, 13},
					   {0, 3, 2, 8, 10},
					   {0, 13, 12, 18, 10},
					   {10, 18, 16, 6, 8},
					   {18, 16, 19, 17, 12},
					   {11, 13, 12, 17, 14},
					   
					   {1, 3, 2, 7, 4},
					   {1, 11, 14, 5, 4},
					   {14, 17, 19, 15, 5},
					   {4, 5, 15, 9, 7},
					   {9, 15, 19, 16, 6},
					   {2, 7, 9, 6, 8}};
		Array f = new Array();
		if(isCool)
			f = faceBaseHitter2(fac);
		else
			for(int i = 0; i < fac.length; i++) {
				Array F = new Array();
				for(int j = 0; j < fac[i].length; j++)
					F.push(fac[i][j]);
				f.push(F);
			}
		Color[] fco = new Color[f.length];
		for(int i = 0; i < f.length; i++)
			fco[i] = new Color(i * 255 / f.length, i * 255 / f.length, i * 255 / f.length);
		Color[] col = new Color[nod.length];
		for(int i = 0; i < nod.length; i++)
			col[i] = new Color(i * 255 / nod.length, i * 255 / nod.length, i * 255 / nod.length);
		dodecahedron.setNodes(nod);
		dodecahedron.setColors(col);
		dodecahedron.setFaces(f);
		dodecahedron.setFaceColors(fco);
		return dodecahedron;
	}
	
	Array faceBaseHitter1(int[][] f) {
		Array fac = new Array();
		for(int a = 0; a < f.length; a++) {
			if(f[a].length == 3) {
				for(int b = 0; b < f[a].length; b++) {
					Array currentFace = new Array();
					currentFace.push(f[a][0]);
					currentFace.push(f[a][1]);
					currentFace.push(f[a][2]);
					fac.push(currentFace);
				}
			}else {
				for(int b = 0; b < f[a].length - 3 + 1; b++) {
					Array currentFace = new Array();
					currentFace.push(f[a][0]);
					currentFace.push(f[a][1 + b]);
					currentFace.push(f[a][2 + b]);
					fac.push(currentFace);
				}
			}
		}
		return fac;
	}
	
	Array faceBaseHitter2(int[][] f) {
		Array fac = new Array();
		for(int a = 0; a < f.length; a++) {
			if(f[a].length == 3) {
				for(int b = 0; b < f[a].length; b++) {
					Array currentFace = new Array();
					currentFace.push(f[a][0]);
					currentFace.push(f[a][1]);
					currentFace.push(f[a][2]);
					fac.push(currentFace);
				}
			}else {
//				for(int b = 0; b < f[a].length - 3 + 1; b++) {
//					Array currentFace = new Array();
//					currentFace.push(f[a][0]);
//					currentFace.push(f[a][1 + b]);
//					currentFace.push(f[a][2 + b]);
//					fac.push(currentFace);
//				}
				for(int b = 0; b < f[a].length - 2; b++) {
					Array currentFace = new Array();
					currentFace.push(f[a][b]);
					currentFace.push(f[a][b + 1]);
					currentFace.push(f[a][b + 2]);
					fac.push(currentFace);
				}
				{
					Array currentFace = new Array();
					currentFace.push(f[a][f[a].length - 2]);
					currentFace.push(f[a][f[a].length - 1]);
					currentFace.push(f[a][0]);
					fac.push(currentFace);
				}
				{
					Array currentFace = new Array();
					currentFace.push(f[a][f[a].length - 1]);
					currentFace.push(f[a][0]);
					currentFace.push(f[a][1]);
					fac.push(currentFace);
				}
			}
		}
		return fac;
	}
	
	SShape target(double x, double y, double z) {
		SShape target = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		int width = 4000;
		int thickness = 400;
		double[][] nod = {{x, y, z},
						  {x + width, y, z},
						  {x, y, z + thickness},
						  {x + width, y, z + thickness},
						  {x, y - width, z},
						  {x + width, y - width, z},
						  {x, y - width, z + thickness},
						  {x + width, y - width, z + thickness},
						  {x + thickness, y - thickness, z},
						  {x + width - thickness, y - thickness, z},
						  {x + thickness, y - thickness, z + thickness},
						  {x + width - thickness, y - thickness, z + thickness},
						  {x + thickness, y - width + thickness, z},
						  {x + width - thickness, y - width + thickness, z},
						  {x + thickness, y - width + thickness, z + thickness},
						  {x + width - thickness, y - width + thickness, z + thickness}};
		int[][] edg = 	 {{0, 1},
						  {1, 3},
						  {3, 2},
						  {2, 0},
						  {2, 6},
						  {6, 7},
						  {7, 5},
						  {5, 4},
						  {4, 6},
						  {4, 0},
						  {5, 1},
						  {7, 3},
						  {8, 9},
						  {9, 11},
						  {11, 10},
						  {10, 8},
						  {10, 14},
						  {14, 15},
						  {15, 13},
						  {13, 12},
						  {12, 14},
						  {12, 8},
						  {13, 9},
						  {15, 11}};
		int[][] fac = 	 {{0, 1, 3},
						  {0, 2, 3},
					      {0, 2, 6},
					      {0, 6, 4},
					      {4, 5, 7},
					      {4, 6, 7},
					      {5, 1, 3},
					      {5, 7, 3},
					      {8, 9, 11},
					      {8, 10, 11},
					      {8, 12, 14},
					      {8, 10, 14},
					      {12, 13, 15},
					      {12, 14, 15},
					      {13, 9, 11},
					      {13, 15, 11},
					      {0, 8, 9},
					      {0, 9, 1},
					      {0, 8, 4},
					      {4, 8, 12},
					      {4, 12, 13},
					      {4, 13, 5},
					      {5, 13, 9},
					      {5, 9, 1}};
		Color[] col = new Color[24];
		Color[] fco = new Color[fac.length];
		for(int a = 0; a < 16; a++) {
			fco[a] = new Color(100, 100, 100);
		}
		for(int a = 16; a < fac.length; a++) {
			fco[a] = new Color(0, 237, 114);
		}
		target.setNodes(nod);
		target.setColors(col);
		target.setEdges(edg);
		target.setFaces(fac);
		target.setFaceColors(fco);
		
		Array t = new Array();
		t.push((double) r.nextInt(50000) - 21000);
		t.push((double) r.nextInt(10000) - 1000);
		t.push(target);
		t.push(window.shapes.length);
		targets.push(t);
		
		return target;
	}
	
	SShape powerup(double x, double y, double z) {
		double p4 = Math.sqrt(2) / 2;
		int r = 1000;
		SShape powerup = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		double[][] nod = {{x, y, z - 500},
						  {x + r, y, z},
						  {x + r * p4, y + r * p4, z},
						  {x, y + r, z},
						  {x - r * p4, y + r * p4, z},
						  {x - r, y, z},
						  {x - r * p4, y - r * p4, z},
						  {x, y - r, z},
						  {x + r * p4, y - r * p4, z}};
		int[][] edg =    {{1, 2},
				  		  {2, 3},
				  		  {3, 4},
				  		  {4, 5},
				  		  {5, 6},
				  		  {6, 7},
				  		  {7, 8},
				  		  {8, 1}};
		int[][] fac =    {{0, 1, 2},
						  {0, 2, 3},
						  {0, 3, 4},
						  {0, 4, 5},
						  {0, 5, 6},
						  {0, 6, 7},
						  {0, 7, 8},
						  {0, 8, 1}};
		Color[] col = new Color[8];
		Color[] fco = new Color[8];
		for(int a = 0; a < 8; a++)
			fco[a] = new Color(255, 255, 0);
		powerup.setNodes(nod);
		powerup.setEdges(edg);
		powerup.setFaces(fac);
		powerup.setColors(col);
		powerup.setFaceColors(fco);
		Array p = new Array();
		p.push(powerup);
		p.push(x);
		p.push(y);
		p.push(z);
		p.push(window.shapes.length);
		powerups.push(p);
		return powerup;
	}
	
	SShape enemy(int pos) {
		
		double[] angs = enemyPositions[pos];
		double xTheta = -(-angs[0] + 90);
		double yTheta = angs[1];
		int d = 100000;
//		SShape enemy = cube(10000, d * cos(angs[1]) * cos(angs[0]) - 5000, d * sin(angs[1]) - 5000, d * cos(angs[1]) * sin(angs[0]) - 5000);
		SShape enemy = pyramid2(8, 5000, 10000);
		moveShape(enemy, 0, 0, d);
		enemy.setNodes(SWindow.rotateX(SWindow.rotateY(enemy.nodes, yTheta), xTheta));
		window.shapes.push(enemy);
		enemies[pos] = window.shapes.length - 1;
		enemyLife[pos] = 4;
//		Color[] fco = new Color[enemy.faces.length];
//		for(int a = 0; a < fco.length; a++)
//			fco[a] = new Color(209, 10, 10);
//		enemy.setFaceColors(fco);
		return enemy;
	}
	
	SShape obstacle(double xTheta, double yTheta) {
//		int w = 5000;
//		SShape obstacle = cube(w, x - w / 2, y - w / 2, z - w / 2);
//		Color[] fco = new Color[6];
//		for(int a = 0; a < fco.length; a++)
//			fco[a] = new Color(255, 0, 0);
//		obstacle.setFaceColors(fco);
		xTheta = -(-xTheta + 90);
		SShape obstacle = pyramid2(5, 1000, 1000);
		int d = 100000;
		moveShape(obstacle, 0, 0, d);
		obstacle.setNodes(SWindow.rotateX(SWindow.rotateY(obstacle.nodes, yTheta), xTheta));
		log("New obstacle", "xTheta:" + xTheta + ", yTheta:" + yTheta);
		return obstacle;
	}
	
	SShape pyramid1(int numSides, double r, double h) {
		SShape pyramid = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		Array nod = new Array();
		{
			Array fn = new Array();
			fn.push(0.0);
			fn.push(0.0);
			fn.push(0.0);
			nod.push(fn);
		}
		for(int t = 0; t < 360; t+=360/numSides) {
			Array n = new Array();
			n.push(r * cos(t));
			n.push(r * sin(t));
			n.push(h);
			nod.push(n);
		}
		Array fac = new Array();
		{
			int[][] ff = new int[1][numSides];
			for(int a = 1; a < numSides + 1; a++) {
				ff[0][a - 1] = a;
			}
			Array ffs = faceBaseHitter1(ff);
			fac.push(faceBaseHitter1(ff).i(0).v);
			for(int f = 0; f < ffs.length; f++) {
				fac.push(ffs.i(f).v);
			}
		}
		for(int f = 1; f < numSides; f++) {
			Array currentFace = new Array();
			currentFace.push(f);
			currentFace.push(f + 1);
			currentFace.push(0);
			fac.push(currentFace);
		}
		{
			Array lastFace = new Array();
			lastFace.push(numSides);
			lastFace.push(1);
			lastFace.push(0);
			fac.push(lastFace);
		}
		Color[] fco = new Color[fac.length];
		for(int c = 0; c < numSides - 1; c++)
			fco[c] = new Color(0, 0, 0);
		for(double c = numSides - 2; c < fco.length; c++) {
			double x = (c - numSides + 1) / (fco.length - numSides + 1);
			fco[(int) c] = new Color((int) (255 * (.5 * sin(360 * x) + .5)), 0, 0);
		}
		pyramid.setColors(new Color[nod.length]);
		pyramid.setNodes(nod);
		pyramid.setFaces(fac);
		pyramid.setFaceColors(fco);
		return pyramid;
	}
	
	SShape pyramid2(int numSides, double r, double h) {
		SShape pyramid = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		Array nod = new Array();
		{
			Array fn = new Array();
			fn.push(0.0);
			fn.push(0.0);
			fn.push(0.0);
			nod.push(fn);
		}
		for(int t = 0; t < 360; t+=360/numSides) {
			Array node = new Array();
			node.push(r * cos(t));
			node.push(r * sin(t));
			node.push(h);
			nod.push(node);
		}
		Array fac = new Array();
		for(int a = 1; a < numSides; a++) {
			Array face = new Array();
			face.push(0);
			face.push(a);
			face.push(a + 1);
			fac.push(face);
		}
		{
			Array lf = new Array();
			lf.push(0);
			lf.push(numSides);
			lf.push(1);
			fac.push(lf);
		}
		Color[] fco = new Color[fac.length];
		for(int a = 0; a < fco.length; a++)
			fco[a] = new Color((int) (255 * (.5 * sin(360 * a / fco.length) + .5)), 0, 0);
		pyramid.setNodes(nod);
		pyramid.setFaces(fac);
		pyramid.setFaceColors(fco);
		return pyramid;
	}
	
	SShape cube(double l, double x, double y, double z) {
		SShape cube = new SShape(new Array(), new Array(), new Array(), new Array(), new Array(), SCamara.FACES);
		double[][] nod = {{x, y, z}, {x, y, z + l}, {x, y + l, z}, {x, y + l, z + l}, {x + l, y, z}, {x + l, y, z + l}, {x + l, y + l, z}, {x + l, y + l, z + l}};
		int[][] edg = {{0, 1}, {1, 3}, {3, 2}, {2, 0}, {4, 5}, {5, 7}, {7, 6}, {6, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
		int[][] fac = {{0, 1, 3, 2}, {0, 1, 5, 4}, {0, 4, 6, 3}, {4, 5, 7, 6}, {2, 3, 7, 6}, {1, 3, 7, 5}};
		Color[] col = new Color[12];
		Color[] fco = new Color[6];
		for(int a = 0; a < fco.length; a++) {
			fco[a] = new Color(0, 0, 0);
		}
		cube.setNodes(nod);
		cube.setEdges(edg);
		cube.setColors(col);
		cube.setFaces(fac);
		cube.setFaceColors(fco);
		return cube;
	}
	
	double cos(double angle) {
		return Math.cos(Math.toRadians(angle));
	}
	
	double sin(double angle) {
		return Math.sin(Math.toRadians(angle));
	}
	
	void showMessage(Array mLines) {
		log("System", "Showing message: ");
		for(int a = 0; a < mLines.length; a++)
			log("System", " " + '"' + mLines.i(a).S() + '"');
		messageLines = mLines;
//		dispMessage = true;
		status = "dispMessage";
		window.window.getContentPane().setCursor(cursors[1]);
	}
	
	void showQuickMessage(String qmsg) {
		log("System", "Showing quick message: " + '"' + qmsg + '"');
		quickMessage = qmsg;
		dispQuickMessage = true;
		quickMessageTimer = 0;
	}
	
	void startShake(double direction, double mag) {
		log("System", "Starting shake in the direction " + direction  + " with magnitude of " + mag);
		if(shaking)
			return;
		shaking = true;
		shakeInit = new Array();
		shakeInit.push(System.currentTimeMillis());
		shakeInit.push(window.camaraCoords.xTheta);
		shakeInit.push(window.camaraCoords.yTheta);
		shakeInit.push(direction);
		shakeInit.push(mag);
	}
	
	double s(double x, double y) {
//		return -15 * Math.sin(x / 100) * (x / 200 - 2);
		return -10 * y / (x / 50 - Math.PI) * Math.sin(x / 50 - Math.PI);
	}
	
	
	
	

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		int w = window.cam.width;
		int h = window.cam.height;
		if(status.equals("dispMessage")) {
			int x = e.getX() - 8;
			int y = e.getY() - 31;
			if(x > w / 2 - 75 && x < w / 2 + 75 && y > h - 125 && y < h - 75) {
//				dispMessage = false;
				status = "run";
				window.window.getContentPane().setCursor(cursors[0]);
				if(life < 0)
					stop("Life reached zero (" + life + ")");
				if(level == 6)
					stop("Level reached 6");
				
				try {
					Robot robot = new Robot();
					robot.mouseMove(w / 2 + window.cam.getLocationOnScreen().x - 8, h / 2 + window.cam.getLocationOnScreen().y - 31);
				} catch (AWTException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}else if(status.equals("menu")){
			int x = e.getX() - 8;
			int y = e.getY() - 31;
			log("Mouse", "x=" + x + ", y=" + y);
			if(x > 3 * w / 4 && x < 3 * w / 4 + 200 && y > h / 2 && y < h / 2 + 100)
				play();
		}else if(status.equals("run")){
			if(e.getButton() == 1) {
				if(bullets.length == 20) {
					log("System", "Too many bullets");
					Array msg = new Array();
					msg.push("You can't shoot!");
					msg.push("You have exceeded");
					msg.push("the maximum");
					msg.push("number of bullets(20).");
					msg.push("");
					msg.push("Wait for the bullets");
					msg.push("that you have shot to");
					msg.push("disappear.");
					showMessage(msg);
				}else {
					log("System", "New Bullet");
					int xTheta = (int) -window.camaraCoords.xTheta + 90;
					int yTheta = (int) -window.camaraCoords.yTheta;
					int x = window.camaraCoords.x;
					int y = window.camaraCoords.y;
					int z = window.camaraCoords.z;
					SShape bullet = cube(100, x + 1000 * cos(yTheta) * cos(xTheta), y + 1000 * sin(yTheta), z + 1000 *  cos(yTheta) * sin(xTheta));
	//				SShape bullet = cube(100, window.camaraCoords.x + 1000 * cos((int) -window.camaraCoords.xTheta + 90), window.camaraCoords.y + 1000 * sin((int) -window.camaraCoords.yTheta), window.camaraCoords.z + 1000 * sin((int) -window.camaraCoords.xTheta + 90));
					Array b = new Array();
					b.push(5 * power * cos(xTheta) * cos(yTheta));
					b.push(5 * power * sin(yTheta));
					b.push(5 * power * sin(xTheta) * cos(yTheta));
					b.push(bullet);
					b.push(window.shapes.length);
					bullets.push(b);
					window.shapes.push(bullet);
					
					power = 0;
				}
			}else {
				double xTheta = -window.camaraCoords.xTheta + 90, yTheta = -window.camaraCoords.yTheta;
				log("Player", "xTheta:" + xTheta + ", yTheta:" + yTheta);
			}
		}else if(status.equals("paused")){
			int x = e.getX() - 8;
			int y = e.getY() - 31;
			if(y > h - 125 && y < h - 75) {
				if(x > w / 2 - 250 && x < w / 2 + 100) {
					resume();
				}else if(x > w / 2 + 100 && x < w / 2 + 250) {
					stop("Game quit");
				}
			}
			
		}
	}

	public void action(ActionEvent ae) {
		
		if(status.equals("run")) {
			Coordinates cc = window.camaraCoords;
			if(DEBUG || CHEATER) {
				int speed = 2000;
				boolean[] keys = window.cam.keys;
				if(keys[KeyEvent.VK_W]) {
					cc.z-=speed*Math.sin(Math.toRadians(-(cc.xTheta + 90)));
					cc.x-=speed*Math.cos(Math.toRadians(-(cc.xTheta + 90)));
		//			camaraCoords.y+=speed*sin(-camaraCoords.yTheta);
				}
				if(keys[KeyEvent.VK_S]) {
					cc.z+=speed*Math.sin(Math.toRadians(-(cc.xTheta + 90)));
					cc.x+=speed*Math.cos(Math.toRadians(-(cc.xTheta + 90)));
		//			camaraCoords.y-=speed*sin(-camaraCoords.yTheta);
				}
				if(keys[KeyEvent.VK_A]) {
					cc.z-=speed*Math.sin(Math.toRadians(-(cc.xTheta)));
					cc.x-=speed*Math.cos(Math.toRadians(-(cc.xTheta)));
				}
				if(keys[KeyEvent.VK_D]) {
					cc.z+=speed*Math.sin(Math.toRadians(-(cc.xTheta)));
					cc.x+=speed*Math.cos(Math.toRadians(-(cc.xTheta)));
				}
				if(keys[KeyEvent.VK_SPACE]) {
						cc.y+=speed;
				}
				if(keys[KeyEvent.VK_SHIFT]) {
						cc.y-=speed;
				}
			}
	
			for(int b = 0; b < bullets.length; b++) {
				Array info = bullets.i(b).A();
				SShape bullet = (SShape) info.i(3).v;
				for(int n = 0; n < bullet.nodes.length; n++) {
					Array node = bullet.nodes.i(n).A();
					node.i(0).v = node.i(0).D() + info.i(0).D();
					node.i(1).v = node.i(1).D() + info.i(1).D();
					node.i(2).v = node.i(2).D() + info.i(2).D();
				}
				info.i(1).v = info.i(1).D() - 1;
			}
			for(int b = 0; b < bullets.length; b++) {
				boolean deleted = false;
				Array info = bullets.i(b).A();
				SShape bullet = (SShape) info.i(3).v;
				for(int t = 0; t < targets.length && !deleted; t++) {
					SShape target = (SShape) targets.i(t).A().i(2).v;
					Array node = bullet.nodes.i(0).A();
					if(node.i(2).D() > target.nodes.i(0).A().i(2).D()) {
						double x = target.nodes.i(0).A().i(0).D() + 100;
						double y = target.nodes.i(0).A().i(1).D() + 100;
						if(node.i(0).D() > x && node.i(0).D() < x + 3900 && node.i(1).D() < y && node.i(1).D() > y - 3900 && node.i(2).D() < target.nodes.i(0).A().i(2).D() + 400) {
							deleteBullet(b);
							score++;
							deleted = true;
							t = targets.length;
							if(score%5 == 0 && score < 25)
								addPowerup();
							if(score == 25)
								addTarget();
							if(score%10 == 0 && score > 25) {
								addEnemy();
							}
						}
					}
				}
				for(int p = 0; p < powerups.length && !deleted; p++) {
					SShape powerup = (SShape) powerups.i(p).A().i(0).v;
					if(dist3D(bullet.nodes.i(0).A(), powerup.nodes.i(0).A()) < 1000) {
	//					deleteItem(window.shapes, powerups.i(p).A().i(4).I());
	//					deleteItem(bullets, p);
	//					for(int c = p; c < powerups.length; c++) {
	//						powerups.i(c).A().i(4).v = powerups.i(c).A().i(4).I() - 1;
	//					}
						// Delete the powerup
						deletePowerup(p);
						deleteBullet(b);
						// Activate the powerup
						if(regenSpeed != 5)
							regenSpeed++;
						deleted = true;
						p = powerups.length; // getatta Dodge!
					}
				}
				for(int e = 0; e < enemies.length && !deleted; e++) {
					if(enemies[e] != -1) {
						SShape enemy = (SShape) window.shapes.i(enemies[e]).v;
						Array point = enemy.nodes.i(0).A();
						if(dist3D(bullet.nodes.i(0).A(), point) < 10000) {
							hurtEnemy(e);
							deleteBullet(b);
							deleted = true;
							e = enemies.length;
							
							boolean ens = false;
							for(int a = 0; a < enemies.length; a++)
								if(enemies[a] != -1)
									ens = true;
							if(!ens) {
								levelUp();
								for(int a = 0; a < numEnemiesToSpawn[level]; a++)
									addEnemy();
							}
						}
					}
				}
				for(int o = 0; o < obstacles.length && !deleted; o++) {
					SShape obstacle = (SShape) obstacles.i(o).A().i(0).v;
					Array bNode = bullet.nodes.i(0).A();
					Array oNode = obstacle.nodes.i(0).A();
					if(dist3D(bNode, oNode) <= 1000) {
						hurtObstacle(o);
						deleteBullet(b);
						deleted = true;
						o = obstacles.length;
					}
				}
				{
					Array origin = new Array();
					origin.push(0.0);
					origin.push(0.0);
					origin.push(0.0);
					if(dist3D(bullet.nodes.i(0).A(), origin) > 110000) {
						deleteBullet(b);
						deleted = true;
					}
				}
			}
			
			if(power < 100)
				power+=regenSpeed;
			if(life < 100)
				life+=0.1;
			if(life < 0) {
				Array msg = new Array();
				msg.push("Game Over");
				msg.push("Your life reached zero");
				showMessage(msg);
			}
			
			for(int t = 0; t < targets.length; t++) {
				SShape target = (SShape) targets.i(t).A().i(2).v;
				double x = targets.i(t).A().i(0).D();
				double y = targets.i(t).A().i(1).D();
				double tx = target.nodes.i(0).A().i(0).D();
				double ty = target.nodes.i(0).A().i(1).D();
				double tz = target.nodes.i(0).A().i(2).D();
				moveShape(target, (x - tx) / 100 + tx, (y - ty) / 100 + ty, tz);
				if(Math.abs(x - tx) < 50 || Math.abs(y - ty) < 50) {
					targets.i(t).A().i(0).v = (double) r.nextInt(50000) - 25000;
					targets.i(t).A().i(1).v = (double) r.nextInt(10000) - 1000;
				}
			}
			
			for(int p = 0; p < powerups.length; p++) {
				SShape powerup = (SShape) powerups.i(p).A().i(0).v;
				double th = 3;
				Array center = new Array();
				center.push((int) powerups.i(p).A().i(1).D());
				center.push((int) powerups.i(p).A().i(2).D());
				center.push((int) powerups.i(p).A().i(3).D());
				powerup.setNodes(
						SWindow.rotateX(
						SWindow.jumpTo(powerup.nodes, center), th));
				for(int n = 0; n < powerup.nodes.length; n++) {
					Array node = powerup.nodes.i(n).A();
					node.i(0).v = node.i(0).D() + center.i(0).I();
					node.i(1).v = node.i(1).D() + center.i(1).I();
					node.i(2).v = node.i(2).D() + center.i(2).I();
				}
			}
			
			for(int e = 0; e < enemies.length; e++)
				if(enemies[e] != -1)
					if(r.nextInt(1000) <= 1)
						addObstacle(e);
			
			for(int o = 0; o < obstacles.length; o++) {
				Array info = obstacles.i(o).A();
				SShape obstacle = (SShape) info.i(0).v;
				Array fn = obstacle.nodes.i(0).A();
				int s = 100;
				double x = fn.i(0).D(), y = fn.i(1).D(), z = fn.i(2).D();
				double xTheta = info.i(1).D(), yTheta = info.i(2).D();
				moveShape(obstacle, x - s * cos(xTheta) * cos(yTheta), y - s * sin(yTheta), z - s * sin(xTheta) * cos(yTheta));
				
				Array origin = new Array();
				origin.push(0.0);
				origin.push(0.0);
				origin.push(0.0);
				if(dist3D(fn, origin) <= 10000) {
					life-=35;
					deleteObstacle(o);
					o--;
				}
			}
			
			if(shaking) {
				double timeElapsed = System.currentTimeMillis() - shakeInit.i(0).L();
				double dir = shakeInit.i(3).D();
				double mag = shakeInit.i(4).D();
				cc.xTheta = shakeInit.i(1).D() + cos(dir) * s(timeElapsed, mag);
				cc.yTheta = shakeInit.i(2).D() + sin(dir) * s(timeElapsed, mag);
				shakeInit.i(3).v = shakeInit.i(3).D() + 3;
				if(timeElapsed > 631) {
					cc.xTheta = shakeInit.i(1).D();
					cc.yTheta = shakeInit.i(2).D();
					shaking = false;
					shakeInit = null;
				}
			}
		}
	}
	
	void deleteItem(Array arr, int index) {
		for(int a = index; a < arr.length - 1; a++) {
			arr.i(a).v = arr.i(a + 1).v;
		}
		arr.setLength(arr.length - 1);
	}
	
	
	void deleteTarget(int index) {
		log("Target " + index, "Being deleted");
		if(targets.length <= index) {
			log("Target " + index, "Cannot be deleted");
			return;
		}
		decrementShapes(targets.i(index).A().i(3).I());
		deleteItem(window.shapes, targets.i(index).A().i(3).I());
		deleteItem(targets, index);
	}
	
	void deleteBullet(int index) {
		log("Bullet " + index, "Being deleted");
		decrementShapes(bullets.i(index).A().i(4).I());
		deleteItem(window.shapes, bullets.i(index).A().i(4).I());
		deleteItem(bullets, index);
	}
	
	void deletePowerup(int index) {
		log("Powerup " + index, "Being deleted");
		decrementShapes(powerups.i(index).A().i(4).I());
		deleteItem(window.shapes, powerups.i(index).A().i(4).I());
		deleteItem(powerups, index);
	}
	
	void deleteEnemy(int index) {
		log("Enemy " + index, "Being deleted");
		decrementShapes(enemies[index]);
		deleteItem(window.shapes, enemies[index]);
		enemies[index] = -1;
	}
	
	void deleteObstacle(int index) {
		log("Obstacle " + index, "Being deleted");
		decrementShapes(obstacles.i(index).A().i(4).I());
		deleteItem(window.shapes, obstacles.i(index).A().i(4).I());
		deleteItem(obstacles, index);
	}
	
	void hurtEnemy(int index) {
		log("Enemy " + index, "Being hurt");
		SShape enemy = (SShape) window.shapes.i(enemies[index]).v;
		enemyLife[index]--;
		if(enemyLife[index] <= 0) {
			deleteEnemy(index);
			enemyLife[index] = 0;
			return;
		}
		
		int life = enemyLife[index];
		int numSides = enemy.faces.length;
		Color[] fco = new Color[numSides];
		for(int a = 0; a < fco.length; a++) {
			int density = (int) (255 * (.5 * sin(360 * a / fco.length) + .5));
			Color color;
			switch(life) {
			case 4:
				color = new Color(density, 0, 0);
				break;
			case 3:
				color = new Color(0, density, 0);
				break;
			case 2:
				color = new Color(density, density, 0);
				break;
			case 1:
				color = new Color(0, 0, density);
				break;
			default:
				color = new Color(0, 0, 0);
			}
			fco[a] = color;
		}
		enemy.setFaceColors(fco);
	}
	
	void hurtObstacle(int index) {
		log("Obstacle " + index, "Being hurt");
		SShape obstacle = (SShape) obstacles.i(index).A().i(0).v;
		int life = obstacles.i(index).A().i(3).I();
		obstacles.i(index).A().i(3).v = --life;
		if(life == 0) {
			deleteObstacle(index);
			return;
		}
		int numSides = obstacle.faces.length;
		Color[] fco = new Color[obstacle.faces.length];
		for(int a = 0; a < fco.length; a++) {
			int density = (int) (255 * (.5 * sin(360 * a / fco.length) + .5));
			Color color;
			switch(life) {
			case 2:
				color = new Color(density, 0, 0);
				break;
			case 1:
				color = new Color(0, 0, density);
				break;
			default:
				color = new Color(0, 0, 0);
			}
			fco[a] = color;
		}
		obstacle.setFaceColors(fco);
		
	}
	
	void addPowerup() {
		log("Powerups", "New powerup");
		SShape powerup = powerup(r.nextInt(50000) - 25000, r.nextInt(10000) - 5000, 50000);
		window.shapes.push(powerup);
	}
	
	void addTarget() {
		log("Targets", "New target");
		SShape target = target(r.nextInt(50000) - 21000, r.nextInt(10000) - 1000, 50000);
		window.shapes.push(target);
	}
	
	 void addEnemy() {
		log("Enemies", "Attempting spawn");
		int[] pp = new int[enemies.length];
		int n = 0;
		for(int a = 0; a < enemies.length; a++)
			if(enemies[a] == -1)
				pp[n++] = a;
		if(n == 0) {
			log("Enemies", "Spawn failed: maximum number of enemies reached");
			return;
		}
		int pos = pp[r.nextInt(n)];
		SShape enemy = enemy(pos);
//		double xTheta = -enemyPositions[pos][0] + 90, yTheta = -enemyPositions[pos][1];
		
		
		{
			double cxt = -window.camaraCoords.xTheta + 90;
			double cyt = -window.camaraCoords.yTheta;
			double ext = enemyPositions[pos][0];
			double eyt = enemyPositions[pos][1];
			double x1 = cos(cxt), x2 = cos(ext);
			double y1 = sin(cyt), y2 = sin(eyt);
			double z1 = sin(cxt), z2 = sin(ext);
			double vx = x1 - x2;
			double vy = y1 - y2;
			double vz = z1 - z2;
			double adj = Math.sqrt(sq(vx) + sq(vz));
			double opp = vy;
			double ang = Math.toDegrees(Math.atan(opp/adj));
			if(leftRight(cxt, ext) == -1) {
				ang-=90;
				ang*=-1;
				ang+=90;
			}
			startShake(ang, 2 / magnitude(vx, vy, vz));
		}
		showLife = true;
		log("Enemy " + pos, "Spawned in");
//		startShake(angleFromPoint(xTheta%360 - window.camaraCoords.xTheta%360, yTheta%360 - window.camaraCoords.yTheta%360));
//		System.out.println(((SShape) window.shapes.i(window.shapes.length - 1).v).nodes.i(0).A());
	}
	 
	 void addObstacle(int e) {
		 log("Enemy " + e, "Making an obstacle");
		 double xTheta = enemyPositions[e][0];
		 double yTheta = enemyPositions[e][1];
		 SShape obstacle = obstacle(xTheta, yTheta);
		 Array o = new Array();
		 o.push(obstacle);
		 o.push(xTheta);
		 o.push(yTheta);
		 o.push(2);
		 o.push(window.shapes.length);
		 obstacles.push(o);
		 window.shapes.push(obstacle);
	 }
	 
	 void levelUp() {
		 level++;
		 if(level == 6) {
			 Array msg = new Array();
			 msg.push("Congrats!");
			 msg.push("You won! You");
			 msg.push("beat level 5.");
			 showMessage(msg);
		 }else {
			 showQuickMessage("Level Up");
			 deleteTarget(0);
			 deleteTarget(0);
		 }
	 }
	
	int leftRight(double t1, double t2) {
		double s = sin(t1 - t2);
		if(s > 0)
			return 1;
		else if (s < 0)
			return -1;
		else
			return 0;
	}
	
	double magnitude(double x, double y, double z) {
		return Math.sqrt(sq(x) + sq(y) + sq(z));
	}
	
	void decrementShapes(int index) {
		if(DEBUG)
			log("System", "Decrementing shape IDs: " + index);
		for(int t = 0; t < targets.length; t++)
			if(targets.i(t).A().i(3).I() > index)
				targets.i(t).A().i(3).v = targets.i(t).A().i(3).I() - 1;
		for(int b = 0; b < bullets.length; b++)
			if(bullets.i(b).A().i(4).I() > index)
				bullets.i(b).A().i(4).v = bullets.i(b).A().i(4).I() - 1;
		for(int p = 0; p < powerups.length; p++)
			if(powerups.i(p).A().i(4).I() > index)
				powerups.i(p).A().i(4).v = powerups.i(p).A().i(4).I() - 1;
		for(int e = 0; e < enemies.length; e++)
			if(enemies[e] > index)
				enemies[e]--;
		for(int o = 0; o < obstacles.length; o++)
			if(obstacles.i(o).A().i(4).I() > index)
				obstacles.i(o).A().i(4).v = obstacles.i(o).A().i(4).I() - 1;
	}
	
	void moveShape(SShape shape, double x, double y, double z) {
		Array firstNode = shape.nodes.i(0).A();
		for(int n = 1; n < shape.nodes.length; n++) {
			Array node = shape.nodes.i(n).A();
			double dx = node.i(0).D() - firstNode.i(0).D();
			double dy = node.i(1).D() - firstNode.i(1).D();
			double dz = node.i(2).D() - firstNode.i(2).D();
			
			node.i(0).v = x + dx;
			node.i(1).v = y + dy;
			node.i(2).v = z + dz;
		}
		firstNode.i(0).v = x;
		firstNode.i(1).v = y;
		firstNode.i(2).v = z;
	}
	
	double dist3D(Array n1, Array n2) {
		return Math.sqrt(sq(n2.i(0).D() - n1.i(0).D()) + sq(n2.i(1).D() - n1.i(1).D()) + sq(n2.i(2).D() - n1.i(2).D()));
	}
	
	double sq(double num) {
		return Math.pow(num, 2);
	}
	
	double angleFromPoint(double x, double y) {
		if(x == 0) {
			if(y > 0)
				return 90;
			else if(y < 0)
				return 270;
			else
				return Double.NaN;
		}else if(x > 0) {
			return Math.toDegrees(Math.atan(y/x));
		}else {
			return Math.toDegrees(Math.atan(y/x)) + 180;
		}
	}
	
	

	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e) {
		if(!status.equals("dispMessage") && !status.equals("paused")) {
			window.camaraCoords.xTheta+=mouseSensitivity * window.cam.dx;
			window.camaraCoords.yTheta+=mouseSensitivity * window.cam.dy;
			if(shaking) {
				shakeInit.i(1).v = shakeInit.i(1).D() + mouseSensitivity * window.cam.dx;
				shakeInit.i(2).v = shakeInit.i(2).D() + mouseSensitivity * window.cam.dy;
			}
			
			if(window.camaraCoords.yTheta > 90)
				window.camaraCoords.yTheta = 90;
			if(window.camaraCoords.yTheta < -90)
				window.camaraCoords.yTheta = -90;
			
			int x = window.cam.width / 2;
			int y = window.cam.height / 2;
			try {
				Robot robot = new Robot();
				robot.mouseMove(x + window.cam.getLocationOnScreen().x - 8, y + window.cam.getLocationOnScreen().y - 31);
			} catch (AWTException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && status.equals("run")) {
			pause();
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public void prePaintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void postPaintComponent(Graphics g) {
		int w = window.cam.width;
		int h = window.cam.height;
		
		if(status.equals("run") || status.equals("dispMessage")) {
			// Regeneration speed
			{
				quad(0, h - 300, 50, h - 230, 50, h - 30, 0, h - 30, new Color(185, 244, 66), new Color(0, 0, 0), g);
				int b = 0;
				for(int a = h - 70; a >= h - 190; a-=40) {
					if(regenSpeed > b) {
						g.setColor(new Color(247, 87, 51));
						g.fillRect(10, a, 30, 30);
						g.setColor(new Color(0, 0, 0));
						g.drawRect(10, a, 30, 30);
					}else {
						g.setColor(new Color(247, 87, 51));
						g.drawRect(10, a, 30, 30);
					}
					b++;
				}
				if(regenSpeed >= 5)
					quad(10, h - 275, 10, h - 200, 40, h - 200, 40, h - 225, new Color(247, 87, 51), new Color(0, 0, 0), g);
				else
					quad(10, h - 275, 10, h - 200, 40, h - 200, 40, h - 225, new Color(185, 244, 66), new Color(247, 87, 51), g);
			}
			
			// Display the score/level
			g.setColor(new Color(185, 244, 66));
			g.fillRoundRect(-20, h - 30, 220, 50, 10, 10);
			g.setColor(new Color(0, 0, 0));
			g.drawRoundRect(-20, h - 30, 220, 50, 10, 10);
			g.setColor(new Color(0, 0, 0));
			g.setFont(new Font("Times New Roman", Font.BOLD, 16));
			if(level == 0)
				g.drawString("SCORE: " + score, 53, h - 10);
			else
				g.drawString("LEVEL: " + level, 53, h - 10);
			
			// Cross hairs
			g.setColor(new Color(100, 100, 100));
			g.drawLine(w / 2 - 12, h / 2, w / 2 + 12, h / 2);
			g.drawLine(w / 2, h / 2 - 12, w / 2, h / 2 + 12);
			
			// Gun
			{
				int[] xs = {w - 100, w - 100, w - 360, w - 350, w - 330, w, w};
				int[] ys = {h, h - 50, h - 150, h - 200, h - 200, h - 50, h};
				g.setColor(new Color(100, 100, 100));
				g.fillPolygon(xs, ys, xs.length);
				g.setColor(new Color(0, 0, 0));
				g.drawPolygon(xs, ys, xs.length);
			}
			
			// Life
			if(showLife) {
				g.setColor(new Color(247, 87, 51));
				g.fillRect(51, (int) (h - 30 - life), 10, (int) life);
			}
			
			// Power
			{
				quad(w - 310, h, w - 275, h - 30, w, h - 30, w, h, new Color(185, 244, 66), new Color(0, 0, 0), g);
				quad(w - 296, h - 5, w - 274, h - 25, w - 260, h - 25, w - 260, h - 5, new Color(247, 87, 51), new Color(0, 0, 0), g);
				int b = 0;
				for(int a = 255; a > 30; a-=25) {
					if(power > b) {
						g.setColor(new Color(247, 87, 51));
						g.fillRect(w - a, h - 25, 20, 20);
						g.setColor(new Color(0, 0, 0));
						g.drawRect(w - a, h - 25, 20, 20);
					}else {
						g.setColor(new Color(247, 87, 51));
						g.drawRect(w - a, h - 25, 20, 20);
					}
					b+=10;
				}
				int[] xs = {w - 30, w - 10, w, w, w - 10, w - 30};
				int[] ys = {h - 25, h - 25, h - 30, h, h - 5, h - 5};
				if(power >= 100) {
					g.setColor(new Color(247, 87, 51));
					g.fillPolygon(xs, ys, xs.length);
					g.setColor(new Color(0, 0, 0));
					g.drawPolygon(xs, ys, xs.length);
				}else {
					g.setColor(new Color(247, 87, 51));
					g.drawPolygon(xs, ys, xs.length);
				}
			}
			
			// Message
			if(status.equals("dispMessage")) {
				int[] xs = {100, 200, w - 200, w - 100, w - 100, w - 200, 200, 100};
				int[] ys = {200, 100, 100, 200, h - 200, h - 100, h - 100, h - 200};
				g.setColor(new Color(95, 142, 1));
				g.fillPolygon(xs, ys, xs.length);
				g.setColor(new Color(0, 0, 0));
				g.drawPolygon(xs, ys, xs.length);
				int numLines = messageLines.length;
				int fontSize1 = 60, fontSize2 = 40, fontDistance = 40;
				int a = 0;
				stringCentered(messageLines.i(a).S(), w / 2, h / 2 - fontDistance * numLines / 2 + fontDistance * a, new Font("Engravers Mt", Font.BOLD, fontSize1), g);
				g.setColor(new Color(255, 255, 255));
				for(a = 1; a < numLines; a++)
					stringCentered(messageLines.i(a).S(), w / 2, h / 2 - fontDistance * numLines / 2 + fontDistance * a, new Font("Modern No. 20", Font.BOLD, fontSize2), g);
				g.setColor(new Color(95, 142, 1));
				g.fillRoundRect(w / 2 - 75, h - 125, 150, 50, 50, 50);
				g.setColor(new Color(0, 0, 0));
				g.drawRoundRect(w / 2 - 75, h - 125, 150, 50, 50, 50);
				stringCentered("Ok", w / 2, h - 90, new Font("Copperplate Gothic", Font.BOLD, 30), g);
			}
			
			// Quick message
			if(dispQuickMessage) {
				g.setColor(new Color(50, 50, 50, quickMessageTimer));
				g.fillRect(w / 2 - 400 - quickMessageTimer / 2, h / 2 - 50 - quickMessageTimer / 2, 800 + quickMessageTimer, 100 + quickMessageTimer);
				g.setColor(new Color(255, 0, 0, quickMessageTimer));
//				g.drawRect(w / 2 - 400 - quickMessageTimer / 2, h / 2 - 50 - quickMessageTimer / 2, 800 + quickMessageTimer, 100 + quickMessageTimer);
				g.setColor(new Color(255, 255, 255, quickMessageTimer));
				stringCentered(quickMessage, w / 2, h / 2 + quickMessageTimer / 3, new Font("CopperPlate Gothic", Font.BOLD, quickMessageTimer), g);
				quickMessageTimer+=2;
				if(quickMessageTimer == 100) {
					dispQuickMessage = false;
					quickMessageTimer = 0;
				}
			}
		}else if(status.equals("menu")) {
			// Gun
			{
				int[] xs = {w - 100, w - 100, w - 360, w - 350, w - 330, w, w};
				int[] ys = {h, h - 50, h - 150, h - 200, h - 200, h - 50, h};
				g.setColor(new Color(100, 100, 100));
				g.fillPolygon(xs, ys, xs.length);
				g.setColor(new Color(0, 0, 0));
				g.drawPolygon(xs, ys, xs.length);
			}
			
			g.setColor(new Color(95, 142, 1));
			g.fillRoundRect(3 * w / 4, h / 2, 200, 100, 100, 100);
			g.setColor(new Color(0, 0, 0));
			g.drawRoundRect(3 * w / 4, h / 2, 200, 100, 100, 100);
			stringCentered("PLAY!", 3 * w / 4 + 100, h / 2 + 65, new Font("CopperPlate Gothic", Font.BOLD, 30), g);
			g.setFont(new Font("Castellar", Font.BOLD, 100));
			g.drawString("Shooter", 100, 300);
			g.drawString("Game", 300, 400);
		}else if(status.equals("paused")) {
			// Pause menu
			{
				int[] xs = {100, 200, w - 200, w - 100, w - 100, w - 200, 200, 100};
				int[] ys = {200, 100, 100, 200, h - 200, h - 100, h - 100, h - 200};
				g.setColor(new Color(95, 142, 1));
				g.fillPolygon(xs, ys, xs.length);
				g.setColor(new Color(0, 0, 0));
				g.drawPolygon(xs, ys, xs.length);
			}
			
			stringCentered("PAUSED", w / 2, 200, new Font("CopperPlate Gothic", Font.BOLD, 100), g);
			
			g.setFont(new Font("CopperPlate Gothic", Font.BOLD, 50));
			g.drawString("How to Play", 150, 250);
			g.setFont(new Font("CopperPlate Gothic", Font.BOLD, 30));
			g.setColor(new Color(247, 87, 51));
			g.drawString("1. Shoot the target", 200, 300);
			g.drawString("2. Get Powerups", 200, h - 300);
			g.drawString("3. Shoot Enemies", w / 2 + 200, 300);
			g.drawString("4. and obstacles", w / 2 + 200, h - 250);
			
			// Target
			g.setColor(new Color(0, 237, 114));
			g.fillRect(500, 300, 100, 10);
			g.fillRect(500, 300, 10, 100);
			g.fillRect(500, 390, 100, 10);
			g.fillRect(590, 300, 10, 100);
			
			// Powerup
			{
				int[] xs = new int[6];
				int[] ys = new int[6];
				for(int a = 0; a < 6; a++) {
					xs[a] = (int) (50 * cos(60 * a));
					xs[a]+=350;
					ys[a] = (int) (50 * sin(60 * a));
					ys[a]+=h / 2 + 150;
				}
				g.setColor(new Color(255, 255, 0));
				g.fillPolygon(xs, ys, xs.length);
			}
			
			// Enemy
			{
				int[] xs = new int[8];
				int[] ys = new int[8];
				for(int a = 0; a < 8; a ++) {
					xs[a] = (int) (50 * cos(45 * a));
					xs[a]+=w / 2 + 350;
					ys[a] = (int) (50 * sin(45 * a));
					ys[a]+=360;
				}
				g.setColor(Color.RED);
				g.fillPolygon(xs, ys, xs.length);
			}
			
			// Obstacle
			{
				int[] xs = {w / 2 + 350, w / 2 + 350, w / 2 + 300};
				int[] ys = {h - 200, h - 150, h - 175};
				g.fillPolygon(xs, ys, xs.length);
			}
			
			g.setColor(new Color(95, 142, 1));
			g.fillRoundRect(w / 2 - 250, h - 125, 150, 50, 50, 50);
			g.fillRoundRect(w / 2 + 100, h - 125, 150, 50, 50, 50);
			g.setColor(new Color(0, 0, 0));
			g.drawRoundRect(w / 2 - 250, h - 125, 150, 50, 50, 50);
			g.drawRoundRect(w / 2 + 100, h - 125, 150, 50, 50, 50);
			stringCentered("Resume", w / 2 - 175, h - 90, new Font("Copperplate Gothic", Font.BOLD, 30), g);
			stringCentered("Quit", w / 2 + 175, h - 90, new Font("Copperplate Gothic", Font.BOLD, 30), g);
		}
	}
	
	void stringCentered(String msg, int x, int y, Font f, Graphics g) {
		int sw = g.getFontMetrics(f).stringWidth(msg);
		g.setFont(f);
		g.drawString(msg, x - sw / 2, y);
	}
	
	void quad(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, Color fillColor, Color edgeColor, Graphics g) {
		Color c = g.getColor();
		
		int[] xs = {x1, x2, x3, x4};
		int[] ys = {y1, y2, y3, y4};
		g.setColor(fillColor);
		g.fillPolygon(xs, ys, 4);
		g.setColor(edgeColor);
		g.drawPolygon(xs, ys, 4);
		
		g.setColor(c);
	}




}
