package com.tricon.ruleengine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
public class DaysCalculation{
	   
    /**
     This Method calculates days between two dates for given format.
     if format is incorrect then return -1 or any exception occures during getDays method calculation
    * */
 
    public static int getDays(String days){	
    	int daysBetween=-1;
		String day[]=days.split(" AND ");
		String firstDate="";
		String secondDate=""; 
                try {
                	firstDate=day[0].replace("\'", "");
            		secondDate=day[1].replace("\'", "");   
                	SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");              	
    				Date date1 = format.parse(firstDate);
    				Date date2 = format.parse(secondDate);
    				daysBetween = (int)ChronoUnit.DAYS.between(date1.toInstant(),date2.toInstant()); 
    				return Math.abs(daysBetween);
    			} 
                 catch (ParseException e){
    				 e.printStackTrace();
    				 return daysBetween;
    			  }                
	}     
}
