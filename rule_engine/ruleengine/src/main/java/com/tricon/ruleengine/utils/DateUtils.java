package com.tricon.ruleengine.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.DateUtil;

import com.itextpdf.text.log.SysoCounter;
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

	private static long calculateAgeInMonths(Date birthDate) throws ParseException {
		Date today = new Date();
		Instant instant = Instant.ofEpochMilli(today.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		LocalDate localDate = localDateTime.toLocalDate();

		Instant instantB = Instant.ofEpochMilli(birthDate.getTime());
		LocalDateTime birthDateDT = LocalDateTime.ofInstant(instantB, ZoneId.systemDefault());
		LocalDate birthDateL = birthDateDT.toLocalDate();
		long monthsBetween = ChronoUnit.MONTHS.between(
			     YearMonth.from(birthDateL), 
			     YearMonth.from(localDate));
		return monthsBetween;
		
	}

	public static long calculateAgeInMonths(int[] age) throws ParseException {
		long months=0;
        if (age==null) return 0;
        if (age[0]>0) months= age[0]*12;
        if (age[1]>0) months= months + age[1];
        
		return months;
		
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

		cyYearFrom = "01/01/" + (CurrentYear-times+1);//old code "01/01/" + (CurrentYear)
		cyYearTo = "12/31/" + (CurrentYear);//old "12/31/" + (CurrentYear + times-1)
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
			GoogleSheets ivsheet,String CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER, Office off,String dateToCompare,boolean rmLogic,int transactionType,
			int isSheet,Map<String, List<Object>> ivfMapPat) {
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
			if (isSheet!=Constants.VALIDATE_FROM_SHEET) {
			ivfMap = ConnectAndReadSheets.readSheet(ivsheet.getSheetId(),
					off.getName() + " " + ivsheet.getAppSheetName(), ivs, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
					off.getName(),true,false);
			}else {
				ivfMap=ivfMapPat;
			}
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
					if (transactionType ==Constants.userType_TR){
						if (!validateClaimorTransactionDate(dateToCompareD, dai)) {
						rm1.add(k);
					    }
					}
					if (transactionType ==Constants.userType_CL){
					    //may we need to interchange parameters..
						if (!validateClaimorTransactionDate(dateToCompareD, dai)) {
						rm1.add(k);
					    }
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
	  //Changes Required in LC3 Engine (Phase 2 & Phase 3) - 7/29/2019 5:10 PM EMAIL
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
	
	public static String createPatientIdByDate(String patientId) {
	
	   if (patientId!=null && !patientId.trim().equals("")) return patientId.trim();
	   return "9999"+new Date().getTime();
		
	}
	
	/***
	 * calculate days divide by 30 take int value..
	 * @param s
	 * @param e
	 * @return
	 */
	public static float getDiffBetweenMonthsFullDateInParsing(String s,String e) {	
		try {
		Date startDate=Constants.SIMPLE_DATE_FORMAT.parse(s);
		Date endDate=Constants.SIMPLE_DATE_FORMAT.parse(e);
		
			long difference = endDate.getTime() - startDate.getTime();
	       float daysBetween = (difference / (1000*60*60*24));
	       return (int)daysBetween/30;
	       
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			return 0;
		}
		//return "0";

	}
	
	public static String getDiffBetweenMonths(String s,String e) {
		
		try {
		Date startDate=Constants.SIMPLE_DATE_FORMAT.parse(s);
		Date endDate=Constants.SIMPLE_DATE_FORMAT.parse(e);
		
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(startDate);
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);

		int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		return (diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH))+"";
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			return "issue";
		}
		//return "0";
		
	}
	public static String getDiffBetweenMonthsIV(String s,String e) {
		
		try {
		Date startDate=Constants.SIMPLE_DATE_FORMAT_IVF.parse(s);
		Date endDate=Constants.SIMPLE_DATE_FORMAT_IVF.parse(e);
		
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(startDate);
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);

		int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		return (diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH))+"";
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			return "No";
		}
		//return "0";
		
	}

	public static boolean waitingPeroidCheck(Date one, Date two) {

		return (one.compareTo(two) > 0);

	}
	
	public static String correctDateformat(String value) {
		// 4/12/2019 MM/dd/yyyy

		// 2018-10-17 yyyy-MM-dd
		Date date = null;
		if (value == null)
			return "";
		if (value.trim().equals(""))
			return "";
		String myDate = "";
		try {
			SimpleDateFormat sdf = Constants.SIMPLE_DATE_FORMAT_IVF;

			date = sdf.parse(value);

			if (!value.equals(sdf.format(date))) {
				date = null;
			} else {
				myDate = value;
			}
		} catch (ParseException ex) {
			// ex.printStackTrace();
		}

		if (date == null) {

			try {
				SimpleDateFormat sdf = Constants.SIMPLE_DATE_FORMAT;

				date = sdf.parse(value);
				myDate = Constants.SIMPLE_DATE_FORMAT_IVF.format(date);

			} catch (ParseException ex) {
				// ex.printStackTrace();
			}

		}
		return myDate;
	}

	public static void main(String[] a) {
		  SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a z");
		  System.out.println(getDiffBetweenMonths( "09/15/2016","09/20/2017"));
		  String dd="10/10/2019";
		  try {
			  System.out.println("1570665600000");
			System.out.println(sdf.parse("10/10/2019 00:00:00 AM GMT").getTime());
			System.out.println(getDiffBetweenMonthsFullDateInParsing( "09/15/2016","09/20/2017"));
			System.out.println(getDiffBetweenMonthsFullDateInParsing( "12/01/2019","05/31/2020"));
			System.out.println(getDiffBetweenMonthsFullDateInParsing( "12/01/2019","11/30/2020"));
			
			Date cd=Constants.SIMPLE_DATE_FORMAT.parse(Constants.SIMPLE_DATE_FORMAT.format(new Date()));
			Date eDate=Constants.SIMPLE_DATE_FORMAT.parse("12/08/2020");
			System.out.println("**********************");
			System.out.println(calculateAgeInMonths(DateUtils.calculateAgeYMD("2020-11-29", true)));
			if(waitingPeroidCheck( cd,eDate)) {
				System.out.println("No");
			}else {
				System.out.println("yes");
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
}
