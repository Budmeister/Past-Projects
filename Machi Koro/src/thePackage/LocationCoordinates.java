package thePackage;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class LocationCoordinates {
	
	public static void main(String[] args) throws IOException{
		readFileAsCoordinates("/TextDocuments/coordinates.txt");
		System.out.println(hierarchy);
	}
	
	private static HashMap<String, HashMap<String, Point>> hierarchy;
	
	public static void readFileAsCoordinates(String dir) throws IOException{
		if(hierarchy == null)
			hierarchy = new HashMap<>();
		
		URL url = LocationCoordinates.class.getResource(dir);
		InputStream is = url.openStream();
		Scanner s = new Scanner(is);
		
		String ln;
		while(s.hasNextLine()) {
			ln = s.nextLine();
			
			if(ln.length() != 0 && ln.charAt(0) == '[' && ln.charAt(ln.length() - 1) == ']') {
				String positionName = ln.substring(1, ln.length() - 1);
				HashMap<String, Point> position = new HashMap<>();
				
				while(!(ln = s.nextLine()).equals("[done]")) {
					String locationName = ln.substring(0, ln.indexOf(':'));
					String locationX = ln.substring(ln.indexOf(':') + 1, ln.indexOf(','));
					String locationY = ln.substring(ln.indexOf(',') + 1);
					
					int x = Integer.parseInt(locationX);
					int y = Integer.parseInt(locationY);
					position.put(locationName, new Point(x, y));
				}
				
				hierarchy.put(positionName, position);
			}
		}
		
		s.close();
	}
	
	public static Point getCoordinate(String position, String section) {
		return hierarchy.get(position).get(section);
	}
	
}

