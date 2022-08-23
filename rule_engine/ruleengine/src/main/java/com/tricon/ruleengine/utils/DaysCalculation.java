package com.tricon.ruleengine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
public class DaysCalculation{
	
    public static int daysBetween=-1;
    
    /**
     This Method calculates days between two dates for given format.
     if format is incorrect then return -1 or any exception occures during getDays method calculation
    * */
    
	public static int getDays(String days){		
		String firstDate="";
		String secondDate="";
		Date date1;
		Date date2;
		String day[]=days.split(" AND "); 
                try {
                	firstDate=day[0].replace("\'", "");
            		secondDate=day[1].replace("\'", "");   
                	SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");              	
    				date1 = format.parse(firstDate);
    				date2 = format.parse(secondDate);
    				daysBetween = (int)ChronoUnit.DAYS.between(date1.toInstant(),date2.toInstant()); 
    				return Math.abs(daysBetween);
    			} 
                 catch (ParseException e){
    				 e.printStackTrace();
    				 return daysBetween;
    			  }  
               
	}     
}
