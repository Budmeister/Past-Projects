package thePackage;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

final public class Settings {
	
	private static HashMap<String, Setting> settings;
	private static String dir;
	
	static void readFileAsSettings(String dir) throws IOException {
		settings = new HashMap<>();
		URL url = Settings.class.getResource(dir);
		InputStream is = url.openStream();
		Scanner s = new Scanner(is);
		
		while(s.hasNextLine()) {
			String ln = s.nextLine();
			String type = ln.substring(0, ln.indexOf(' '));
			ln = ln.substring(ln.indexOf(' ') + 1);
			String name = ln.substring(0, ln.indexOf('=') - 1);
			ln = ln.substring(ln.indexOf('=') + 1);
			String value = ln.substring(1, ln.indexOf(';'));
			Setting setting = new Setting(type, name, value);
			settings.put(name, setting);
		}
		
		Settings.dir = dir;
		s.close();
	}
	
	static void saveFile() throws IOException {
		if(dir == null)
			return;
		if(!System.getProperty("os.name").toLowerCase().contains("win"))
			return;
		File file = new File("settings.txt");
		PrintStream is = new PrintStream(file);
		Runtime.getRuntime().exec("attrib +H settings.txt");
		for(Setting s : settings.values())
			is.println(s.getType() + " " + s.getName() + " = " + s.getValue() + ";");
		is.close();
	}
	
	public static Setting getSetting(String name) {
		return settings.get(name);
	}
	
	public static int getIntSetting(String name) throws ClassCastException{
		return (int) getSetting(name).getValue();
	}
	
	public static double getDoubleSetting(String name) throws ClassCastException{
		return (double) getSetting(name).getValue();
	}
	
	public static long getLongSetting(String name) throws ClassCastException{
		return (long) getSetting(name).getValue();
	}
	
	public static short getShortSetting(String name) throws ClassCastException{
		return (short) getSetting(name).getValue();
	}
	
	public static float getFloatSetting(String name) throws ClassCastException{
		return (float) getSetting(name).getValue();
	}
	
	public static byte getByteSetting(String name) throws ClassCastException{
		return (byte) getSetting(name).getValue();
	}
	
	public static char getCharSetting(String name) throws ClassCastException{
		return (char) getSetting(name).getValue();
	}
	
	public static boolean getBooleanSetting(String name) throws ClassCastException{
		return (boolean) getSetting(name).getValue();
	}
	
}

class InvalidTypeException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5753348692081228544L;
	private String type, value;
	InvalidTypeException(String type, String value){
		this.type = type; this.value = value;
	}
	public String toString() {
		return "InvalidTypeException: Type: " + type + ", Value: " + value;
	}
}

final class Setting{
	
	private final String type, name;
	private Object value;
	
	public Setting(String type, String name, String value) {
		this.type = type;
		this.name = name;
		
		switch(type) {
		case "int":
			this.value = Integer.parseInt(value);
			break;
		case "long":
			this.value = Long.parseLong(value);
			break;
		case "double":
			this.value = Double.parseDouble(value);
			break;
		case "String":
			this.value = value;
			break;
		case "char":
			this.value = value.charAt(1);
			break;
		case "short":
			this.value = Short.parseShort(value);
			break;
		case "byte":
			this.value = Byte.parseByte(value);
			break;
		case "float":
			this.value = Float.parseFloat(value);
			break;
		case "boolean":
			this.value = Boolean.parseBoolean(value);
			break;
		default:
			throw new InvalidTypeException(type, value);
		}
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
}