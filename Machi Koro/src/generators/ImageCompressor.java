package generators;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ImageCompressor {
    public static void compress(File f, double scaleFactor, File dir) {
    	try {
    		BufferedImage image = ImageIO.read(f);
    		int width = (int) (scaleFactor * image.getWidth()), height = (int) (scaleFactor * image.getHeight());
    		BufferedImage smallerImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    		Graphics2D g = (Graphics2D) smallerImage.getGraphics();
    		g.drawImage(image, 0, 0, width, height, null);
    		String name = f.getName();
    		File newFile = new File(dir.getAbsolutePath() + "/" + name);
    		ImageIO.write(smallerImage, "png", newFile);
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    }

    public static void main(String[] args) {
//    	JFileChooser fileChooser = new JFileChooser();
//    	int result = fileChooser.showOpenDialog(new JFrame("Choose a folder"));
//    	if(result != JFileChooser.APPROVE_OPTION)
//    		return;
//    	File folder = fileChooser.getSelectedFile();
//    	result = fileChooser.showOpenDialog(new JFrame("Choose a save destination"));
//    	if(result != JFileChooser.APPROVE_OPTION)
//    		return;
//    	File folder2 = fileChooser.getSelectedFile();
    	JFrame frame = new JFrame("Image Compressor");
    	frame.setLayout(new BorderLayout());
    	JPanel tp = new JPanel();
    	tp.add(new JLabel("From:"));
    	JTextField from = new JTextField(20);
    	tp.add(from);
    	JPanel bp = new JPanel();
    	bp.add(new JLabel("To:"));
    	JTextField to = new JTextField(20);
    	bp.add(to);
    	JButton button = new JButton("Compress");
    	button.addActionListener(ae -> {
    		File folder = new File(from.getText());
    		File folder2 = new File(to.getText());
	    	File[] images = folder.listFiles();
	    	for(File f : images) {
	    		compress(f, 1.0/5, folder2);
	    	}
	    	System.exit(0);
    	});
    	frame.add(tp, BorderLayout.NORTH);
    	frame.add(bp, BorderLayout.CENTER);
    	frame.add(button, BorderLayout.SOUTH);
    	
    	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	frame.pack();
    	frame.setLocationRelativeTo(null);
    	frame.setVisible(true);
    }
}