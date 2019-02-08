package com.tricon.ruleengine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gdata.data.dublincore.Date;
import com.tricon.ruleengine.dto.FreqencyDto;
import com.tricon.ruleengine.dto.ServiceCodeIvfTimesFreqFieldDto;

public class FreqencyUtils {

	public static FreqencyDto parseFrequecy(String frequecny) {

		FreqencyDto dto = new FreqencyDto();
		String duration = "";
		try {
			String temp = "";
			frequecny = frequecny.trim().toLowerCase();
			String timeAndDuration[] = frequecny.split("x");
			if (timeAndDuration.length > 0) {
				dto.setTimes(Integer.parseInt(timeAndDuration[0]));
			}
			if (timeAndDuration.length > 1) {
				duration = timeAndDuration[1];
				String secondPart[] = duration.split("_");
				if (secondPart.length > 0) {
					temp = secondPart[0].trim();
					//
					if (temp.indexOf("cal.mo") > -1) {
						dto.setCalendarMonth(Integer.parseInt(temp.replace("cal.mo", "")));

					} else if (temp.indexOf("mo") > -1) {
						dto.setMonths(Integer.parseInt(temp.replace("mo", "")));
					} else if (temp.indexOf("cy") > -1) {
						dto.setCy(Integer.parseInt(temp.replace("cy", "")));

					} else if (temp.indexOf("fy") > -1) {
						dto.setFy(Integer.parseInt(temp.replace("fy", "")));

					} else if (temp.indexOf("py") > -1) {
						dto.setFy(Integer.parseInt(temp.replace("py", "")));

					} else if (temp.indexOf("lt") > -1) {
						dto.setLt(Integer.parseInt(temp.replace("lt", "")));
					} else if (temp.indexOf("days") > -1) {
						dto.setOnlyDays(Integer.parseInt(temp.replace("days", "")));
					}
				}
				if (secondPart.length > 1) {
					dto.setDays(Integer.parseInt(secondPart[1].replaceAll("d", "")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;

	}

	/*
	 * public static Map<String,List<ServiceCodeIvfTimesFreqFieldDto>>
	 * getCountTimeServiceCode11(List<ServiceCodeIvfTimesFreqFieldDto> li,String
	 * tooth){
	 * 
	 * Map<String,List<ServiceCodeIvfTimesFreqFieldDto>> fm=new HashMap<>();
	 * for(ServiceCodeIvfTimesFreqFieldDto s:li) {
	 * List<ServiceCodeIvfTimesFreqFieldDto> l2=fm.get(s.getServiceCode()); if
	 * (l2==null) l2= new ArrayList<>(); l2.add(s);
	 * 
	 * } for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry :
	 * fm.entrySet()) { List<ServiceCodeIvfTimesFreqFieldDto> s= entry.getValue();
	 * getCountTimeServiceCode(s,entry.getKey()); } return fm; }
	 * 
	 */
	public static Object[] getCountTimeServiceCode(List<ServiceCodeIvfTimesFreqFieldDto> li, String serviceCode) {
		int count = 0;
		int times = 0;
		int ctr = 0;
		String tooth = "";
		String code = "";
		String surface = "";

		List<String> dos = new ArrayList<>();
		String fr = "";
		String fl = "";
		if (li != null) {
			for (ServiceCodeIvfTimesFreqFieldDto s : li) {
				if (s.getServiceCode().equals(serviceCode)) {
					count = count + s.getCount();
					dos.add(s.getDos());
					fl = s.getFieldName();
					code = s.getServiceCode();
					tooth = s.getTooth();
					fr = s.getFreqency();
					surface=s.getSurface();
					if (ctr == 0)
						times = s.getTimes();
					else if (times > s.getTimes())
						times = s.getTimes();/// Set to minimum value
				}
				ctr++;
			}
			
		}
		if (!surface.equals("")) surface="("+surface+")";
		return new Object[] { count, tooth, String.join(",", dos), fl, times,code,fr,surface };
	}

	/*
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, String s1, String tooth) {
		int ct = 0;
		int ti = 0;
		int actualmax=-1;
		Object[] mess = null;
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1);
		 code=(String)c1[5];
		ct = ct + (Integer) c1[0];
		ti = ti + (Integer) c1[4];
		if(ti>0) actualmax = ti;
		if ( (actualmax>0 ) && (ct > 0 && ct > actualmax)) {
			String dos = (String) c1[2];
			String fl = (String) c1[3];
			mess = new Object[] { code,tooth,dos, actualmax };

		}
		return mess;
	}
    */
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			String s1, String s2, String tooth) {
		int ct = 0;
		int ti = 0;
		int actualmax=-1;
		Object[] mess = null;
		String code="";
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1);
		if ( !((String)c1[5]).equals("")  ){code=code+(String)c1[5];}
		ct = ct + (Integer) c1[0];
		ti = ti + (Integer) c1[4];
		if (ti>0) actualmax = ti;
		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2);
		if ( !((String)c2[5]).equals("")  ){code=code+(String)c2[5];}
		ct = ct + (Integer) c2[0];
		ti = ti + (Integer) c2[4];
		if ((ti>0 && actualmax==-1) || (ti>0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		if ( (actualmax>0 ) && (ct > 0 && ct > actualmax)) {
			String dos = "";
			String fl = "";
			String fr = "";
			String sur = "";
				if (l1 != null) {
				dos = dos + " " + (String) c1[2];
				fl = fl + " " + (String) c1[3];
				fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];
			}
			if (l2 != null) {
				dos = dos + " " + (String) c2[2];
				fl = fl + " " + (String) c2[3];
				fr = fr + " " + (String) c2[6];
				sur = sur + " " + (String) c2[7];
			}
			if (!sur.equals("")) sur="("+sur+")";
			mess = new Object[] { code,tooth,dos, actualmax,fr,sur };

		}
		return mess;
	}

	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, String s1, String s2, String s3, String tooth) {
		int ct = 0;
		int ti = 0;
		int actualmax=-1;
		Object[] mess = null;
		String code="";
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1);
		if ( !((String)c1[5]).equals("")  ){code=code+(String)c1[5];}
		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if(ti>0) actualmax = ti;
		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2);
		if ( !((String)c2[5]).equals("")  ){code=code+(String)c2[5];}
		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti>0 && actualmax==-1) || (ti>0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3);
		if ( !((String)c3[5]).equals("")  ){code=code+(String)c3[5];}
		ti = (Integer) c3[4];
		if ((ti>0 && actualmax==-1) || (ti>0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		if ( (actualmax>0 ) && (ct > 0 && ct > actualmax)) {
			String dos = "";
			String fl = "";
			String fr = "";
			String sur = "";
			
			if (l1 != null) {
				dos = dos + " " + (String) c1[2];
				fl = fl + " " + (String) c1[3];
				fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];
				
			}
			if (l2 != null) {
				dos = dos + " " + (String) c2[2];
				fl = fl + " " + (String) c2[3];
				fr = fr + " " + (String) c2[6];
				sur = sur + " " + (String) c2[7];
				
			}
			if (l3 != null) {
				dos = dos + " " + (String) c3[2];
				fl = fl + " " + (String) c3[3];
				fr = fr + " " + (String) c3[6];
				sur = sur + " " + (String) c3[7];
				
			}
			
			//3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals("")) sur="("+sur+")";
			mess = new Object[] { code,tooth,dos, actualmax, fr,sur};

		}
		return mess;
	}

	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3,List<ServiceCodeIvfTimesFreqFieldDto> l4, String s1, String s2, String s3,String s4, String tooth) {
		int ct = 0;
		int ti = 0;
		int actualmax=-1;
		Object[] mess = null;
		String code="";
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1);
		if ( !((String)c1[5]).equals("")  ){code=code+(String)c1[5];}

		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if(ti>0) actualmax = ti;

		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2);
		if ( !((String)c2[5]).equals("")  ){code=code+(String)c2[5];}

		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti>0 && actualmax==-1) || (ti>0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3);
		if ( !((String)c3[5]).equals("")  ){code=code+(String)c3[5];}
		ct = ct + (Integer) c3[0];
		ti = (Integer) c3[4];
		if ((ti>0 && actualmax==-1) || (ti>0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c4 = FreqencyUtils.getCountTimeServiceCode(l4, s4);
		if ( !((String)c4[5]).equals("")  ){code=code+(String)c4[5];}

		ct = ct + (Integer) c4[0];
		ti = (Integer) c4[4];
		if ((ti>0 && actualmax==-1) || (ti>0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		if ( (actualmax>0 ) && (ct > 0 && ct > actualmax)) {
			String dos = "";
			String fl = "";
			String fr = "";
			String sur = "";
			
			if (l1 != null) {
				dos = dos + " " + (String) c1[2];
				fl = fl + " " + (String) c1[3];
				fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];
				
			}
			if (l2 != null) {
				dos = dos + " " + (String) c2[2];
				fl = fl + " " + (String) c2[3];
				fr = fr + " " + (String) c2[6];
				sur = sur + " " + (String) c2[7];
				
			}
			if (l3 != null) {
				dos = dos + " " + (String) c3[2];
				fl = fl + " " + (String) c3[3];
				fr = fr + " " + (String) c3[6];
				sur = sur + " " + (String) c3[7];
				
			}
			if (l4 != null) {
				dos = dos + " " + (String) c4[2];
				fl = fl + " " + (String) c4[3];
				fr = fr + " " + (String) c4[6];
				sur = sur + " " + (String) c4[7];
				
			}
			
			//3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals("")) sur="("+sur+")";
			mess = new Object[] { code,tooth,dos, actualmax,fr,sur};

		}
		return mess;
	}

	public static boolean checkforAlikeCodes(String tpCodes, String historyCode) {
		boolean alikecodepresent = false;
		// 2 C D1206 and D1208
		if (tpCodes.equals("D1206") && historyCode.equals("D1208")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D1208") && historyCode.equals("D1206")) {
			alikecodepresent = true;
		}
		// 2C D4341 and D4342
		if (tpCodes.equals("D4341") && historyCode.equals("D4342")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D4342") && historyCode.equals("D4341")) {
			alikecodepresent = true;
		}
		// 2C D1110 and D1120
		if (tpCodes.equals("D1110") && historyCode.equals("D1120")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D1120") && historyCode.equals("D1110")) {
			alikecodepresent = true;
		}
		// 4C D0270 D0272 D0273 D0274
		if (tpCodes.equals("D0270") && historyCode.equals("D0272")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0270") && historyCode.equals("D0273")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0270") && historyCode.equals("D0274")) {
			alikecodepresent = true;
		}
		
		
		if (tpCodes.equals("D0272") && historyCode.equals("D0270")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0272") && historyCode.equals("D0273")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0272") && historyCode.equals("D0274")) {
			alikecodepresent = true;
		}
		
		
		if (tpCodes.equals("D0273") && historyCode.equals("D0270")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0273") && historyCode.equals("D0272")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0273") && historyCode.equals("D0274")) {
			alikecodepresent = true;
		}
		
		if (tpCodes.equals("D0274") && historyCode.equals("D0270")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0274") && historyCode.equals("D0272")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0274") && historyCode.equals("D0273")) {
			alikecodepresent = true;
		}

		// 3C D0150, D0120, and D0140
		if (tpCodes.equals("D0150") && historyCode.equals("D0120")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0120") && historyCode.equals("D0150")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0120") && historyCode.equals("D0140")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0140") && historyCode.equals("D0120")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0150") && historyCode.equals("D0140")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0140") && historyCode.equals("D0150")) {
			alikecodepresent = true;
		}

       
		
		// 3C D2391, M2391, P2391
       if (compairThreeVlaues(tpCodes, historyCode, "D2391", "M2391", "P2391")!=null) {
			alikecodepresent = true;
		}
		// 3C D2392, M2392, P2392
		if (compairThreeVlaues(tpCodes, historyCode, "D2392", "M2392", "P2392")!=null) {
			alikecodepresent = true;
		}
        //
		// 3C D2393, M2393, P2393
		if (compairThreeVlaues(tpCodes, historyCode, "D2393", "M2393", "P2393")!=null) {
			alikecodepresent = true;
		}
        //
		// 3C D2394, M2394, P2394
		if (compairThreeVlaues(tpCodes, historyCode, "D2394", "M2394", "P2394")!=null) {
			alikecodepresent = true;
		}
		

		return alikecodepresent;
	}
	
	
	
	private static Boolean  compairThreeVlaues(String tpCodes,String historyCode,String one ,String two, String three) {
		Boolean b=null;
		//boolean alikecodepresent=false;
		
		if (tpCodes.equals(one) && historyCode.equals(two)) {
			b = true;
		}
		if (tpCodes.equals(two) && historyCode.equals(one)) {
			b = true;
		}
		if (tpCodes.equals(two) && historyCode.equals(three)) {
			b = true;
		}
		if (tpCodes.equals(three) && historyCode.equals(two)) {
			b = true;
		}
		if (tpCodes.equals(one) && historyCode.equals(three)) {
			b = true;
		}
		if (tpCodes.equals(three) && historyCode.equals(one)) {
			b = true;
		}
		return b;
	}
	
	public static void addToFailedSet(Set<String> failedCodeSet,Object[] m) {
		
		if (failedCodeSet!=null && m!=null && m.length>0) {
			failedCodeSet.add((String)m[0]);
		}
		
		
		
	}

	
}
