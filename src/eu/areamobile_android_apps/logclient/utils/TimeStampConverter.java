package eu.areamobile_android_apps.logclient.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampConverter {

	private static final String FORMAT_DATE = "dd/MM/yyyy";
	private static final String FORMAT_HOUR = "HH:mm:ss";
	
	public static String toDate (String timestamp){
		
		return convert(timestamp, FORMAT_DATE);
	}
	
	public static String toHour (String timestamp){
		
		return convert(timestamp, FORMAT_HOUR);
	}
	
	private static String convert (String timestamp, String format){
		
		Long timeInMillis = Long.parseLong(timestamp);
		Date d = new Date(timeInMillis);
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(d);

	}
}

