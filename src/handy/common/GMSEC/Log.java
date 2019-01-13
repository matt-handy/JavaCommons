package handy.common.GMSEC;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;

import gov.nasa.gsfc.gmsec.api.GMSEC_Exception;
import gov.nasa.gsfc.gmsec.api.field.Field;
import gov.nasa.gsfc.gmsec.api.field.I32Field;
import gov.nasa.gsfc.gmsec.api.mist.ConnectionManager;

public class Log {
	
	public static final String SEVERITY_FIELD_NAME = "SEVERITY"; 
	
	private ConnectionManager connMan;
	private PrintWriter writer;
	private static Log instance;
	
	private Log(){
		
	}
	
	public synchronized static Log getInstance(){
		if(instance == null){
			instance = new Log();
		}
		return instance;
	}
	
	public enum LogLevel {NOMINAL(1), WARNING(3), CRITICAL(4);
		public final int levelMap;
		private LogLevel(int levelMap){
			this.levelMap = levelMap;
		}
		
		public String getName(){
			return getName(levelMap);
		}
		
		public static String getName(int levelMap){
			switch(levelMap){
			case 1:
				return "Nominal";
			case 3:
				return "Warning";
			case 4: 
				return "Critical";
			default:
				throw new IllegalArgumentException();	
			}
				
		}
	}
	
	public synchronized void log(String entry, LogLevel level){
		if(connMan != null){
			try{
				connMan.publishLog(entry, new I32Field(SEVERITY_FIELD_NAME, level.levelMap));
			}catch(GMSEC_Exception e){
				System.out.println("I can't log!: " + entry);
			}
		}
		if(writer != null){
			writer.println(Instant.now().toString() + " - " + level.getName() + ": " + entry);
			writer.flush();
		}
		System.out.println(level + ": " + entry);
		
	}
	
	public static void setConnectionManager(ConnectionManager connMan, String subject) throws GMSEC_Exception{
		getInstance().connMan = connMan;
		connMan.setLoggingDefaults(subject, new ArrayList<Field>());
	}
	
	public synchronized static void setLogFile(String filename) throws IOException{
		getInstance().writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
	}
}
