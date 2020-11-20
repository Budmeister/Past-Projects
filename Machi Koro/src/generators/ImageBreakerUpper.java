package generators;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageBreakerUpper extends JComponent{
	
	JFileChooser fileChooser;
	JFrame mainFrame;
	BufferedImage img;
	BufferedImage subimage;

	public static void main(String[] args) {
		ImageBreakerUpper ibu = new ImageBreakerUpper();
		ibu.start(args);
	}
	
	void start(String[] args) {
		File file;
		if(args.length == 0) {
			fileChooser = new JFileChooser();
			int result = fileChooser.showOpenDialog(new JFrame("Image"));
			if(result != JFileChooser.APPROVE_OPTION)
				return;
			file = fileChooser.getSelectedFile();
		}else {
			file = new File(args[0]);
		}
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		JPanel top = new JPanel();
		JLabel xl = new JLabel("x:");
		JTextField x = new JTextField(5);
		JLabel yl = new JLabel("y:");
		JTextField y = new JTextField(5);
		JLabel wl = new JLabel("w:");
		JTextField w = new JTextField(5);
		JLabel hl = new JLabel("h:");
		JTextField h = new JTextField(5);
		top.add(xl); top.add(x); top.add(yl); top.add(y); top.add(wl); top.add(w); top.add(hl); top.add(h);
		
		JPanel bottom = new JPanel();
		JLabel nl = new JLabel("Name:");
		JTextField n = new JTextField(10);
		JButton sb = new JButton("Submit");
		sb.addActionListener(ae -> begin(file, x.getText(), y.getText(), w.getText(), h.getText(), n.getText()));
		bottom.add(nl); bottom.add(n); bottom.add(sb);
		
		frame.add(top, BorderLayout.NORTH); frame.add(bottom, BorderLayout.SOUTH);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	void begin(File f, String xs, String ys, String ws, String hs, String name) {
		int x = Integer.parseInt(xs), y = Integer.parseInt(ys), w = Integer.parseInt(ws), h = Integer.parseInt(hs);
		try {
			img = ImageIO.read(f);
			subimage = img.getSubimage(x, y, w, h);
			String path = f.getAbsolutePath();
			File file2 = new File(path.substring(0, path.lastIndexOf('\\')) + "\\" + name);
			ImageIO.write(subimage, "png", file2);
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(subimage, 0, 0, subimage.getWidth(), subimage.getHeight(), null);
	}

}
