package thePackage;

public class Errors extends LoggingObject{
	
	static Status s = new Status("Error");
	static Logger l = new Logger(s);
	
	static void log(String message) {
		l.log(message);
	}
	
	static void log(Exception e) {
		log(e.toString());
	}
	
}
