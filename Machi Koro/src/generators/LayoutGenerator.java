package generators;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

import thePackage.LocationCoordinates;

@SuppressWarnings({ "serial", "unused" })
public class LayoutGenerator extends JComponent {
	
	JFrame settingsFrame;
	JFrame mainFrame;
	JFileChooser fileChooser;
	DefaultListModel<Item> things;
	int selected = 0;
	String selectedType = "";
	
	public static void main(String[] args) {
		LayoutGenerator lg = new LayoutGenerator();
		lg.start();
	}
	
	int w = 800;
	int width, height;
	void start() {
		settingsFrame = new JFrame("Layout creator");
		settingsFrame.setLayout(new BorderLayout());
		Label wl = new Label("Width:");
		TextField wv = new TextField(5);
		Label hl = new Label("Height:");
		TextField hv = new TextField(5);
		Button sb = new Button("Submit");
		sb.addActionListener(ae -> {
			width = Integer.parseInt(wv.getText());
			height = Integer.parseInt(hv.getText());
			begin();
		});
		Panel p = new Panel();
		p.add(wl);
		p.add(wv);
		p.add(hl);
		p.add(hv);
		p.add(sb);
		settingsFrame.add(p);
		
		settingsFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		settingsFrame.pack();
		settingsFrame.setLocationRelativeTo(null);
		settingsFrame.setVisible(true);
	}
	
	ArrayList<Line> lines;
	ArrayList<Rectangle> rectangles;
	ArrayList<Circle> circles;
	private String dir;
	Button gib, dib;
	
	void begin() {
		JTextField inc = new JTextField(5);
		dib = new Button("Delete");
		dib.setEnabled(false);
		
		mainFrame = new JFrame("Layout " + width + "x" + height);
		settingsFrame.dispose();
		mainFrame.setLayout(new BorderLayout());
		
		lines = new ArrayList<>();
		lines.add(new Line(0, 50, width, 50));
		lines.add(new Line(50, 0, 50, height));
		lines.add(new Line(width - 50, 0, width - 50, height));
		lines.add(new Line(0, height - 50, width, height - 50));
		
		lines.add(new Line(width / 2, 0, width / 2, height));
		lines.add(new Line(0, height / 2, width, height / 2));
		rectangles = new ArrayList<>();
		circles = new ArrayList<>();
		things = new DefaultListModel<>();
		
		Dimension d = new Dimension(w, w * height / width);
		setPreferredSize(d);
		mainFrame.add(this);
		
		JPanel controlPanel = new JPanel();
		
		JPanel lp = new JPanel();
		lp.setLayout(new BorderLayout());
		JPanel ltp = new JPanel();
		JLabel l1 = new JLabel("a1:");
		JTextField a1 = new JTextField(5);
		JLabel l2 = new JLabel(", a2:");
		JTextField a2 = new JTextField(5);
		JLabel l3 = new JLabel(", a3:");
		JTextField a3 = new JTextField(5);
		JLabel l4 = new JLabel(", a4:");
		JTextField a4 = new JTextField(5);
		ltp.add(l1); ltp.add(a1); ltp.add(l2); ltp.add(a2); ltp.add(l3); ltp.add(a3); ltp.add(l4); ltp.add(a4);
		
		JPanel lmp = new JPanel();
		Button lb = new Button("Add line(x1,y1,x2,y2)");
		lb.addActionListener(ae -> {
			Line l = 
				new Line(Integer.parseInt(a1.getText()),
						 Integer.parseInt(a2.getText()),
						 Integer.parseInt(a3.getText()),
						 Integer.parseInt(a4.getText()));
			things.addElement(new Item(l.toString(), l));
			gib.setEnabled(true);
			dib.setEnabled(true);
			lines.add(l);
			if(inc.getText().equals(""))
				reset(a1, a2, a3, a4);
		});
		Button rb = new Button("Add rectangle(x,y,w,h)");
		rb.addActionListener(ae -> {
			Rectangle r =
				new Rectangle(Integer.parseInt(a1.getText()),
							  Integer.parseInt(a2.getText()),
							  Integer.parseInt(a3.getText()),
							  Integer.parseInt(a4.getText()));
			things.addElement(new Item(r.toString(), r));
			gib.setEnabled(true);
			dib.setEnabled(true);
			rectangles.add(r);
			if(inc.getText().equals(""))
				reset(a1, a2);
		});
		Button cb = new Button("Add circle(x,y,w,h)");
		cb.addActionListener(ae -> {
			Circle c = 
					new Circle(Integer.parseInt(a1.getText()),
							   Integer.parseInt(a2.getText()),
							   Integer.parseInt(a3.getText()),
							   Integer.parseInt(a4.getText()));
			things.addElement(new Item(c.toString(), c));
			gib.setEnabled(true);
			dib.setEnabled(true);
			circles.add(c);
			if(inc.getText().equals(""))
				reset(a1, a2);
		});
		lmp.add(lb); lmp.add(rb); lmp.add(cb);
		
		JPanel lbp = new JPanel();
		Button ob = new Button("Open");
		ob.addActionListener(ae -> open());
		Button eb = new Button("Export");
		eb.addActionListener(ae -> export());
		Button sb = new Button("Save");
		sb.addActionListener(ae -> save());
		lbp.add(ob); lbp.add(eb); lbp.add(sb);
		
		lp.add(ltp, BorderLayout.NORTH); lp.add(lmp, BorderLayout.CENTER); lp.add(lbp, BorderLayout.SOUTH);
		
		JPanel mp = new JPanel();
		mp.setLayout(new BorderLayout());
		JPanel mtp = new JPanel();
		Button UP = new Button();
		UP.addActionListener(ae -> a2.setText("" + (Integer.parseInt(a2.getText()) - Integer.parseInt(inc.getText()))));
		mtp.add(UP);
		JPanel mmp = new JPanel();
		Button LEFT = new Button();
		LEFT.addActionListener(ae -> a1.setText("" + (Integer.parseInt(a1.getText()) - Integer.parseInt(inc.getText()))));
		Button MIDDLE = new Button();
		Button RIGHT = new Button();
		RIGHT.addActionListener(ae -> a1.setText("" + (Integer.parseInt(a1.getText()) + Integer.parseInt(inc.getText()))));
		mmp.add(LEFT); mmp.add(MIDDLE); mmp.add(RIGHT);
		JPanel mbp = new JPanel();
		Button DOWN = new Button();
		DOWN.addActionListener(ae -> a2.setText("" + (Integer.parseInt(a2.getText()) + Integer.parseInt(inc.getText()))));
		mbp.add(DOWN);
		
		mp.add(mtp, BorderLayout.NORTH); mp.add(mmp, BorderLayout.CENTER); mp.add(mbp, BorderLayout.SOUTH);
		
		JPanel rp = new JPanel();
		rp.setLayout(new BorderLayout());
		
		gib = new Button("Get info");
		gib.setEnabled(false);
		JList<Item> data = new JList<>(things);
		data.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane listScroller = new JScrollPane(data);
		listScroller.setPreferredSize(new Dimension(170, 100));
		dib.addActionListener(ae -> {
			int i = data.getSelectedIndex();
			things.get(i).remove();
			if(things.size() <= i)
				i--;
			data.setSelectedIndex(i);
			if(things.size() == 0) {
				gib.setEnabled(false);
				dib.setEnabled(false);
			}
		});
		gib.addActionListener(ae -> {
			Item i = things.get(data.getSelectedIndex());
			Shape s = i.getShape();
			int[] args = s.getInfo();
			a1.setText("" + args[0]);
			a2.setText("" + args[1]);
			a3.setText("" + args[2]);
			a4.setText("" + args[3]);
		});
		data.addListSelectionListener(l -> {
			int index = data.getSelectedIndex();
			if(index == -1)
				return;
			Item i = things.get(index);
			Shape s = i.getShape();
			for(int a = 0; a < lines.size(); a++)
				if(lines.get(a) == s) {
					selected = a;
					selectedType = "Line";
				}
			for(int a = 0; a < rectangles.size(); a++)
				if(rectangles.get(a) == s) {
					selected = a;
					selectedType = "Rectangle";
				}
			for(int a = 0; a < circles.size(); a++)
				if(circles.get(a) == s) {
					selected = a;
					selectedType = "Circle";
				}
			gib.setEnabled(true);
			dib.setEnabled(true);
		});
		JPanel rlp = new JPanel(), rrp = new JPanel();
		rlp.setLayout(new BorderLayout());
		rlp.add(inc, BorderLayout.NORTH); rlp.add(gib, BorderLayout.CENTER); rlp.add(dib, BorderLayout.SOUTH); rrp.add(listScroller, BorderLayout.EAST);
		rp.add(rlp, BorderLayout.WEST); rp.add(rrp, BorderLayout.EAST);
		
		controlPanel.add(lp, BorderLayout.WEST); controlPanel.add(mp, BorderLayout.CENTER); controlPanel.add(rp, BorderLayout.EAST);
		
		controlPanel.setBorder(BorderFactory.createTitledBorder("Control Panel"));
		mainFrame.add(controlPanel, BorderLayout.SOUTH);
		
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		Timer t = new Timer(100, ae -> repaint());
		t.start();
	}
	
	
	void open() {
		fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(new JFrame("Open"));
		if(result == JFileChooser.APPROVE_OPTION) {
			lines = new ArrayList<>();
			rectangles = new ArrayList<>();
			circles = new ArrayList<>();
			File file = fileChooser.getSelectedFile();
			dir = file.getAbsolutePath();
			Scanner s = null;
			try {
				s = new Scanner(file);
				String mode = "";
				while(s.hasNextLine()) {
					String ln = s.nextLine();
					if(ln.equals("LINE"))
						mode = "line";
					else if(ln.equals("RECTANGLE"))
						mode = "rectangle";
					else if(ln.equals("CIRCLE"))
						mode = "circle";
					else {
						int[] args = new int[4];
						int[] firsts = new int[5];
						firsts[0] = 0;
						firsts[4] = ln.length() + 1;
						int b = 0;
						for(int a = 0; a < ln.length(); a++)
							if(ln.charAt(a) == ',')
								firsts[++b] = a + 1;
						for(int a = 0; a < firsts.length - 1; a++)
							args[a] = Integer.parseInt(ln.substring(firsts[a], firsts[a + 1] - 1));
						
						if(mode.equals("line")) {
							Line l = new Line(args[0], args[1], args[2], args[3]);
							things.addElement(new Item(l.toString(), l));
							lines.add(l);
						}
						else if(mode.equals("rectangle")) {
							Rectangle r = new Rectangle(args[0], args[1], args[2], args[3]);
							things.addElement(new Item(r.toString(), r));
							rectangles.add(r);
						}
						else if(mode.equals("circle")) {
							Circle c = new Circle(args[0], args[1], args[2], args[3]);
							things.addElement(new Item(c.toString(), c));
							circles.add(c);
						}
					}
				}
			} catch (FileNotFoundException e) {} finally {
				if(s!=null)
					s.close();
			}
		}
	}
	
	void export() {
		fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(new JFrame("Export"));
		if(result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			dir = file.getAbsolutePath();
			PrintWriter p = null;
			try {
				p = new PrintWriter(file);
				p.println("LINE");
				for(Line l : lines)
					p.println(l.getX1() + "," + l.getY1() + "," + l.getX2() + "," + l.getY2());
				p.println("RECTANGLE");
				for(Rectangle r : rectangles)
					p.println(r.getX() + "," + r.getY() + "," + r.getWidth() + "," + r.getHeight());
				p.println("CIRCLE");
				for(Circle c : circles)
					p.println(c.getX() + "," + c.getY() + "," + c.getWidth() + "," + c.getHeight());
			} catch (FileNotFoundException e) {} finally {
				if(p!=null)
					p.close();
			}
		}
	}
	
	void save() {
		if(dir!=null) {
			File file = new File(dir);
			PrintWriter p = null;
			try {
				p = new PrintWriter(file);
				p.println("LINE");
				for(Line l : lines)
					p.println(l.getX1() + "," + l.getY1() + "," + l.getX2() + "," + l.getY2());
				p.println("RECTANGLE");
				for(Rectangle r : rectangles)
					p.println(r.getX() + "," + r.getY() + "," + r.getWidth() + "," + r.getHeight());
				p.println("CIRCLE");
				for(Circle c : circles)
					p.println(c.getX() + "," + c.getY() + "," + c.getWidth() + "," + c.getHeight());
			} catch (FileNotFoundException e) {} finally {
				if(p!=null)
					p.close();
			}
		}
	}
	
	private void reset(JTextField a, JTextField b, JTextField c, JTextField d) {
		a.setText(""); b.setText(""); c.setText(""); d.setText("");
	}
	
	private void reset(JTextField a, JTextField b) {
		a.setText(""); b.setText("");
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(w, w * height / width);
	}
	
	protected void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, w, w * height / width);
		g.setColor(Color.red);
		g.drawRect(0, 0, w-1, w * height / width -1);
		
		g.setColor(Color.black);
		g.drawLine(0, w * height / width, w / width, w * height / width);
		for(int a = 0; a < lines.size(); a++) {
			Line l = lines.get(a);
			if(selectedType.equals("Line") && selected == a)
				g.setColor(Color.red);
			else
				g.setColor(Color.black);
			g.drawLine(l.getX1() * w / width, l.getY1() * w / width, l.getX2() * w / width, l.getY2() * w / width);
		}
		for(int a = 0; a < rectangles.size(); a++) {
			Rectangle r = rectangles.get(a);
			if(selectedType.equals("Rectangle") && selected == a)
				g.setColor(Color.red);
			else
				g.setColor(Color.black);
			g.drawRect(r.getX() * w / width, r.getY() * w / width, r.getWidth() * w / width, r.getHeight() * w / width);
		}
		for(int a = 0; a < circles.size(); a++) {
			Circle c = circles.get(a);
			if(selectedType.equals("Circle") && selected == a)
				g.setColor(Color.red);
			else
				g.setColor(Color.black);
			g.drawOval(c.getX() * w / width, c.getY() * w / width, c.getWidth() * w / width, c.getHeight() * w / width);
		}
	}
	
	class Item{
		private String name;
		private Shape shape;
		Item(String n, Shape s){
			setName(n); setShape(s);
		}
		
		public void remove() {
			things.remove(getIndex());
			lines.remove(shape);
			rectangles.remove(shape);
			circles.remove(shape);
		}
		
		public String toString() {
			return name;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the index
		 */
		public int getIndex() {
			for(int a = 0; a < things.size(); a++)
				if(things.elementAt(a) == this)
					return a;
			return -1;
		}
		
		public Shape getShape() {
			return shape;
		}
		
		public void setShape(Shape s) {
			shape = s;
		}
	}
}

abstract class Shape {
	abstract public int[] getInfo();
}

class Line extends Shape{
	private int x1, y1, x2, y2;
	Line(int x1, int y1, int x2, int y2){
		setX1(x1); setY1(y1); setX2(x2); setY2(y2);
	}
	
	public String toString() {
		return "Line:" + x1 + "," + y1 + "," + x2 + "," + y2;
	}
	
	public int[] getInfo() {
		int[] i = {x1, y1, x2, y2};
		return i;
	}
	
	/**
	 * @return the x1
	 */
	public int getX1() {
		return x1;
	}
	/**
	 * @param x1 the x1 to set
	 */
	public void setX1(int x1) {
		this.x1 = x1;
	}
	/**
	 * @return the y1
	 */
	public int getY1() {
		return y1;
	}
	/**
	 * @param y1 the y1 to set
	 */
	public void setY1(int y1) {
		this.y1 = y1;
	}
	/**
	 * @return the x2
	 */
	public int getX2() {
		return x2;
	}
	/**
	 * @param x2 the x2 to set
	 */
	public void setX2(int x2) {
		this.x2 = x2;
	}
	/**
	 * @return the y2
	 */
	public int getY2() {
		return y2;
	}
	/**
	 * @param y2 the y2 to set
	 */
	public void setY2(int y2) {
		this.y2 = y2;
	}
}

class Rectangle extends Shape{
	private int x, y, width, height;
	Rectangle(int x, int y, int width, int height){
		setX(x); setY(y); setWidth(width); setHeight(height);
	}
	
	public String toString() {
		return "Rectangle:" + x + "," + y + "," + width + "," + height;
	}
	
	public int[] getInfo() {
		int[] i = {x, y, width, height};
		return i;
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
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
}

class Circle extends Shape{
	private int x, y, width, height;
	Circle(int x, int y, int width, int height){
		setX(x); setY(y); setWidth(width); setHeight(height);
	}
	
	public String toString() {
		return "Circle:" + x + "," + y + "," + width + "," + height;
	}
	
	public int[] getInfo() {
		int[] i = {x, y, width, height};
		return i;
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
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
}