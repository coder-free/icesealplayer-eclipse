package com.zbf.iceseal.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTools {
	public static final String DEFAULTFORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String getNow(){
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULTFORMAT);
		return sdf.format(Calendar.getInstance().getTime());
	}
	public static String getDate(Calendar cal){
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULTFORMAT);
		return sdf.format(cal.getTime());
	}
}
