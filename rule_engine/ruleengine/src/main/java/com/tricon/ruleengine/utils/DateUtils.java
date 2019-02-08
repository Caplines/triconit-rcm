package com.tricon.ruleengine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.DateUtil;

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

	public static void main(String[] a) {
	  SimpleDateFormat sdf= new SimpleDateFormat("dd/mm/yyyy");
	  String tpd="01/01/2019";
	  String dos="01/03/2017";
	 try {
		System.out.println(checkforXm(sdf.parse(tpd),sdf.parse(dos),12));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	}
}
