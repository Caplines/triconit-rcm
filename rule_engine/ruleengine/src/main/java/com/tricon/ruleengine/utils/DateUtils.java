package com.tricon.ruleengine.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.DateUtil;

import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

public class DateUtils {

	// Compares Two Date
	public static boolean compareDates(Date one, Date two) {

		return (one.compareTo(two) >= 0);
	}

	// Compares Two Date
	public static void CheckForStringInDate(String date) throws RuleEngineDateException {

		if (date.matches(".*[a-zA-Z]+.*")) {
			throw new RuleEngineDateException("Date Contains String");
		}
	}

	public static String covertToExcelDate(String date) {
		Double javaDate = null;
		try {
			javaDate = DateUtil.getExcelDate(Constants.SIMPLE_DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (javaDate == null)
			javaDate = 0d;
		return javaDate.intValue() + "";

	}

	
	public static int[] calculateAgeYMD(String bd,boolean isIVF) throws ParseException {
		
		Date birthDate=null;
		
				if (isIVF)birthDate=Constants.SIMPLE_DATE_FORMAT_IVF.parse(bd);
				else birthDate=Constants.SIMPLE_DATE_FORMAT.parse(bd);
		ZoneId defaultZoneId = ZoneId.systemDefault();
		LocalDate today = LocalDate.now();
		Instant instant = birthDate.toInstant();

        //2. Instant + system default time zone + toLocalDate() = LocalDate
        LocalDate localBDate = instant.atZone(defaultZoneId).toLocalDate();
		
		
		//LocalDate birthday = LocalDate.of(1960, Month.JANUARY, 1);
		Period p = Period.between(localBDate, today);
		 
		//Now access the values as below
		//System.out.println(p.getDays());ma
		//System.out.println(p.getMonths());
		//System.out.println(p.getYears());
		return new int[] {p.getYears(),p.getMonths(),p.getDays()};
	}
	private static int calculateAge(Date birthDate) throws ParseException {
		Date today = new Date();
		Instant instant = Instant.ofEpochMilli(today.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		LocalDate localDate = localDateTime.toLocalDate();

		Instant instantB = Instant.ofEpochMilli(birthDate.getTime());
		LocalDateTime birthDateDT = LocalDateTime.ofInstant(instantB, ZoneId.systemDefault());

		LocalDate birthDateL = birthDateDT.toLocalDate();

		if ((birthDateL != null) && (localDate != null)) {
			return Period.between(birthDateL, localDate).getYears();
		} else {
			return 0;
		}
	}

	public static int calculateAgeIVF(String birthDateS) throws ParseException {
		Date birthDate = Constants.SIMPLE_DATE_FORMAT_IVF.parse(birthDateS);
		return calculateAge(birthDate);

	}

	public static int calculateAgeO(String birthDateS) throws ParseException {
		Date birthDate = Constants.SIMPLE_DATE_FORMAT.parse(birthDateS);
		return calculateAge(birthDate);
	}

	public static Long daysBetweenDates(Date d1, Date d2) {
		TimeUnit tm = TimeUnit.DAYS;
		long diffInMillies = d2.getTime() - d1.getTime();
		System.out.println(diffInMillies);
		return tm.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	

	public static Date[] getFiscalYear(int times) {
		int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
		int CurrentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
		String financiyalYearFrom = "";
		String financiyalYearTo = "";
		if (CurrentMonth < 6) {
			financiyalYearFrom = "06/01/" + (CurrentYear - 1 - times);
			financiyalYearTo = "05/31/" + (CurrentYear);
		} else {
			financiyalYearFrom = "06/01/" + (CurrentYear - times);
			financiyalYearTo = "05/31/" + (CurrentYear + 1);
		}
		// System.out.println(financiyalYearFrom);
		// System.out.println(financiyalYearTo);

		try {
			return new Date[] { Constants.SIMPLE_DATE_FORMAT.parse(financiyalYearFrom),
					Constants.SIMPLE_DATE_FORMAT.parse(financiyalYearTo) };
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date[] getCalendarYear(int times) {
		int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
		String cyYearFrom = "";
		String cyYearTo = "";

		cyYearFrom = "01/01/" + (CurrentYear);
		cyYearTo = "12/31/" + (CurrentYear + times-1);
		System.out.println(cyYearFrom);
		System.out.println(cyYearTo);

		try {
			return new Date[] { Constants.SIMPLE_DATE_FORMAT.parse(cyYearFrom),
					Constants.SIMPLE_DATE_FORMAT.parse(cyYearTo) };
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date getNextYear(Date date) {

		// Date date = null;
		// try {
		// date = Constants.SIMPLE_DATE_FORMAT.parse("3/31/2017");
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR) + 1, calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1);
		//System.out.println(calendar.get(Calendar.YEAR));
		// calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)-1,
		// calendar.get(Calendar.DATE));

		return calendar.getTime();
	}

	public static boolean isDatesBetweenDates(Date a, Date b, Date d) {

		return a.compareTo(d) * d.compareTo(b) >= 0;

	}
	
	public static boolean checkForAgeLimit(int[] age,int limit) {
		
		boolean properage=true;
		if (age.length==3) {
			
			//Compare Age Years
			//years 36 limit 80
			if (age[0]==limit && (age[1]>0  || age[2]>0)) {
				properage =false;
			}else if (age[0]>limit) {
				properage =false;
				
			}
		}
		
		return properage;
	}

	
	
	public static boolean checkforXm(Date tpDate,Date dos,int months) {
		
		//Calendar calendarC = new GregorianCalendar();
		//calendarC.setTime(new Date());
		//
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dos);
		calendar.set(calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH)+months, calendar.get(Calendar.DATE));
		Date date12m=calendar.getTime();
		System.out.println(date12m);
		//isDatesBetweenDates(a, b, d)
		if (tpDate.compareTo(date12m)>0 ){
			return true;
		}else{
			return false;
		}
		
	}

	public static boolean checkforXmMore(Date tpDate,Date dos,int months) {
		
		//Calendar calendarC = new GregorianCalendar();
		//calendarC.setTime(new Date());
		//
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dos);
		calendar.set(calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH)+months, calendar.get(Calendar.DATE));
		Date date12m=calendar.getTime();
		System.out.println(date12m);
		System.out.println(tpDate.compareTo(date12m));
		//isDatesBetweenDates(a, b, d)
		if (tpDate.compareTo(date12m)<=0 ){
			return false;
		}else{
			return true;
		}
		
	}
	
	
	/*
	 * This method is used to Extract latest IVF ID from Map of IVF's
	 */
	public static Object[] selectOneKeyFromMapWithLatestDate(String pid,
			GoogleSheets ivsheet,String CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER, Office off,String dateToCompare,boolean rmLogic) {
		String []ivs=pid.split(",");
		Map<String, List<Object>> ivfMap=null;
		Map<String, List<Object>> orifMap=null;
		
		Date dateToCompareD=null;
		try {
			 dateToCompareD= Constants.SIMPLE_DATE_FORMAT.parse(dateToCompare);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
					off.getName() + " " + ivsheet.getAppSheetName(), ivs, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
					off.getName(),true,false);
			if (ivfMap!=null) {
				orifMap= new HashMap<>();
			    orifMap.putAll(ivfMap);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String,String> tempMap=new HashMap<>();
		
		if (ivfMap!=null) {
			
			//REMOVE IRRELEVANT Dates
			if (rmLogic){
			List<String> rm1=new ArrayList<>();
			for (Map.Entry<String,List<Object>> entry : ivfMap.entrySet()) {  
				String k= entry.getKey();
				for (Object obj : entry.getValue()) {
					IVFTableSheet i = (IVFTableSheet) obj;
					try {
						Date dai=Constants.SIMPLE_DATE_FORMAT_IVF.parse(i.getGeneralDateIVwasDone());
					if (!validateClaimorTransactionDate(dateToCompareD, dai)) {
						rm1.add(k);
					}
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			for(String r:rm1) {
				ivfMap.remove(r);
			}
		    }
			
			pid="";
			
			Map<String,List<Object>> tempIvMap=null;
			Map<String,List<Object>> tempIvMap2=null;
			
			List<String> rm=new ArrayList<>();
			for (Map.Entry<String,List<Object>> entry : ivfMap.entrySet()) {  
				String k= entry.getKey();
				for (Object obj : entry.getValue()) {
	            	
	            	IVFTableSheet i = (IVFTableSheet) obj;
	                		
                    if (tempIvMap==null) {
                    	tempIvMap= new HashMap<>();
                    	List<Object> l= new ArrayList<>();
                    	l.add(i);
                    	tempMap.put(k, i.getPatientId());
                    	tempIvMap.put(k, l);
                    	tempIvMap2= new HashMap<>();
                    	tempIvMap2.put(k, l);
                    	
                    }else {
                    	for (Map.Entry<String,List<Object>> entry1 : tempIvMap.entrySet()) { 
                    		List<Object> lo=entry1.getValue();
                    		for(Object oo:lo) {
                    			IVFTableSheet iv=	(IVFTableSheet)oo;
                    			/*if (iv.getUniqueID().equals(i.getUniqueID())) {
                    				continue;
                    			}*/
                        		String d=iv.getGeneralDateIVwasDone();
                        		try {
                        			
                        		Date da=Constants.SIMPLE_DATE_FORMAT_IVF.parse(d);
                        		Date dai=Constants.SIMPLE_DATE_FORMAT_IVF.parse(i.getGeneralDateIVwasDone());
                        		//System.out.println(!da.after(dai));
                        	   if (!iv.getUniqueID().equals(i.getUniqueID())) {
                        	   if (!da.after(dai)) { 
                        			rm.add(entry1.getKey());
                        			List<Object> l= new ArrayList<>();
                        			l.add(i);
                        			tempMap.put(entry.getKey(), i.getPatientId());
                        			tempIvMap2.put(entry.getKey(), l);
                        		}else {
                        			List<Object> l= new ArrayList<>();
                        			l.add(i);
                        			tempMap.put(entry.getKey(), i.getPatientId());
                        			tempIvMap2.put(entry.getKey(), l);
                        			
                        		}
                        		}else {
                        			List<Object> l= new ArrayList<>();
                        			l.add(i);
                        			tempMap.put(entry.getKey(), i.getPatientId());
                        			tempIvMap2.put(entry.getKey(), l);
                        		}
                        		}catch (Exception e) {
									// TODO: handle exception
								}
                    		}
                    		
                    		
                    	}
                    	tempIvMap.clear();
                    	tempIvMap.putAll(tempIvMap2);
                    	
                    }

				}

			}
			//tempIvMap=tempIvMap2;
			for(String r:rm) {
				tempIvMap.remove(r);
				tempMap.remove(r);
			}
			ivfMap=tempIvMap;
			 
			 
		}
		return new Object[] {orifMap,ivfMap};
	}
	
	//1. If Tx. Plan Validation Date (Tx. Plan Validation) or Date of Service (Claim Validation) <= 5th of the month -> Consider IV done since 26th of last month.
	//2. If Tx. Plan Validation Date (Tx. Plan Validation) or Date of Service (Claim Validation) > 5th of the month -> Consider IV done in that month only.
	private static boolean validateClaimorTransactionDate(Date ctDate,Date ivDate) {
		
		  Calendar calendarCT = new GregorianCalendar();
		  calendarCT.setTime(ctDate);
		  boolean pass=false;
		  int monthCT = calendarCT.get(Calendar.MONTH);
		  int datCT   = calendarCT.get(Calendar.DATE);
		  
		  Calendar calendarIV = new GregorianCalendar();
		  calendarIV.setTime(ivDate);
		  int monthIV = calendarIV.get(Calendar.MONTH);
		  //int datIV   = calendarIV.get(Calendar.DATE);
		  
		  if (datCT<=5) {
			  calendarCT.set(calendarCT.get(Calendar.YEAR) , calendarCT.get(Calendar.MONTH)-1, 26);
			  //System.out.println(calendarCT.getTime().toString());
			  //System.out.println(ivDate.toString());
			  
			 if ( ivDate.after(calendarCT.getTime()) || ivDate.equals(calendarCT.getTime())) {
				 pass=true;
			 }
			  
		  }else {
			 //calendarIV.set(calendarIV.get(Calendar.YEAR) , calendarIV.get(Calendar.MONTH), 1);
			if (monthIV== monthCT) {
				pass=true;
			}
			  
		  }
		  
		  return pass;
	}
	
	/*
	public static void main(String[] a) {
		  SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		  String cdate="05/03/2019";
		  String ivdate="26/02/2019";
		  Calendar calendar = new GregorianCalendar();
		  Calendar calendar2 = new GregorianCalendar();
		  
		  ////ivfPlanTermDate.compareTo(currentDate)
		 try {
		   calendar.setTime(sdf.parse(cdate));
		   calendar.set(calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

		   calendar2.setTime(sdf.parse(ivdate));
		   calendar2.set(calendar2.get(Calendar.YEAR) , calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DATE));

		   System.out.println(validateClaimorTransactionDate(calendar.getTime(), calendar2.getTime()));
			//System.out.println(sdf.parse(term).compareTo(sdf.parse(dos))>=0);
			//System.out.println(sdf.parse(term).compareTo(dInterval)>=0);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		}
	*/	
}
