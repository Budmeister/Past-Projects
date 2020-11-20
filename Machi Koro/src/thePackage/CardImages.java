package thePackage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

abstract public class CardImages {
	static Logger l;
	static Status s;
	
	private static HashMap<String, BufferedImage> images;
	
	public static void loadImages() throws IOException {
		s = new Status("CardImages", "stable");
		l = new Logger(s);
		if(images != null)
			return;
		GlobalVariables.frameHandler.setScene(GlobalVariables.frameHandler.gameLoading());
		images = new HashMap<>();
		File imageFolder = new File(getPathNameFromURL(CardImages.class.getResource("/images")));
		File[] folders = imageFolder.listFiles();
		int n = 0;
		for(File folder : folders) {
			n+=folder.listFiles().length;
		}
		int b = 0;
		for(File folder : folders) {
			for(File img : folder.listFiles()) {
				b++;
				images.put(img.getName(), ImageIO.read(img));
				GlobalVariables.frameHandler.loadingBar.setFullness((double) b / n);
			}
		}
		l.log("Created images");
		
		// GMK fix loadImages() to include ImageInfo objects
	}
	
	public static void main(String[] args) {
		try {
			loadImages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(images);
	}
	
	private static String getPathNameFromURL(URL url) {
		String s = url.getFile();
		if(!s.contains("%20"))
			return s;
		for(int a = 0; a < s.length() - 3; a++) {
			String sub = s.substring(a, a + 3);
			if(sub.equals("%20"))
				s = s.substring(0, a) + " " + s.substring(a + 3);
		}
		return s;
	}
	
	public static HashMap<String, BufferedImage> getImages() {
		return images;
	}
	
	public static BufferedImage getImage(String key) {
		if(images == null)
			try {
				loadImages();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		BufferedImage i = images.get(key);
		if(i == null)
			throw new ImageNotFoundException(key, Thread.currentThread());
		return i;
	}
}

class ImageInfo{
	private String name;
	private int width, height;
	private BufferedImage image;
	
	public ImageInfo(String name, int width, int height, BufferedImage img) {
		setName(name); setWidth(width); setHeight(height); setImage(img);
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

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
}

class ImageNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 112348480974021485L;
	private String imageName;
	private Thread thread;
	
	public ImageNotFoundException(String imageName, Thread thread) {
		setImageName(imageName); setThread(thread);
	}
	
	@Override
	public String toString() {
		return "ImageNotFoundException:" + getImageName() + " at " + getThread();
	}

	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * @param thread the thread to set
	 */
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	
}
