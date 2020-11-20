package thePackage;

public class GlobalVariables {
	
	public static FrameHandler frameHandler;
	public static MachiKoroGame machiKoroGame;
	public static MessageHandler messageHandler;
	public static String gameType;
	private static Status s = new Status("Anonymous Logger", "stable");
	public static Logger anonymousLogger = new Logger(s);
	public static boolean DEBUG;
	public static boolean VERBOSE;

}
