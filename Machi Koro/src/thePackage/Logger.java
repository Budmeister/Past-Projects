package thePackage;

import java.io.PrintStream;
import java.util.Date;

public class Logger {
	
	private static PrintStream defaultStream = System.out;
	
	private PrintStream localStream;
	private Status localStatus;
	public Logger(String title, String status) {
		localStatus = new Status(title, status);
		if(defaultStream != null)
			localStream = defaultStream;
		log("--CREATED LOGGER--");
	}
	
	public Logger(Status status) {
		localStatus = status;
		if(defaultStream != null)
			localStream = defaultStream;
		log("--CREATED LOGGER--");
	}
	
	public Status getStatusObject() {
		return localStatus;
	}
	
	public void setStatusObject(Status status) {
		localStatus = status;
	}
	
	public void log(String message) {
		localStream.println(localStatus + ": " +  message);
	}
	
	public static void setDefaultOutputStream(PrintStream ps) {
		defaultStream = ps;
	}
	
	public static PrintStream getDefaultOuptutStream() {
		return defaultStream;
	}
	
	public void setLocalOutputStream(PrintStream ps) {
		localStream = ps;
	}
	
	public PrintStream getLocalOutputStream() {
		return localStream;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Logger))
			return false;
		return ((Logger) o).getStatusObject().equals(getStatusObject());
	}
	
}

class Status{
	private String title;
	private String status;
	private static int length = 40;
	
	
	public Status(String title, String status) {
		this.title = title;
		this.status = status;
	}
	
	public Status(String title){
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String t) {
		title = t;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String s) {
		status = s;
	}
	
	public String toString() {
		return spacingAndHyphenBuffer(title + " - " + status, length) + new Date(System.currentTimeMillis()).toString();
	}
	
	private static String spacingAndHyphenBuffer(String s, int l) {
		if(s.length() < l - 2) {
			s+=" ";
			while(s.length() < l - 1)
				s+="-";
			s+=" ";
		}else {
			s+=" - ";
		}
		return s;
	}

	/**
	 * @return the length
	 */
	public static int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public static void setLength(int length) {
		Status.length = length;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Status))
			return false;
		Status s = (Status) o;
		return getTitle().equals(s.getTitle()) && getStatus().equals(s.getStatus());
	}
}