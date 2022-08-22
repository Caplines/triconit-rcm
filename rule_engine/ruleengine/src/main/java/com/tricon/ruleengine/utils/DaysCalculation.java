package com.tricon.ruleengine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
public class DaysCalculation
{
    public static long daysBetween=0L;
	public static int getDays(String days) {
		
		String day[]=days.split(" AND ");
		String firstDate=day[0].replace("\'", "");
		String secondDate=day[1].replace("\'", "");
		SimpleDateFormat format= new SimpleDateFormat("MM/dd/yyyy");		
			try {
				Date date1;
				Date date2;
				date1 = format.parse(firstDate);
				date2 = format.parse(secondDate);
				daysBetween = ChronoUnit.DAYS.between(date1.toInstant(),date2.toInstant());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		   
				
		    return (int)daysBetween;
	}     
}
