package com.tricon.ruleengine.utils;

import java.io.BufferedWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.MessageSource;


import com.tricon.ruleengine.dto.FreqencyDto;
import com.tricon.ruleengine.dto.ServiceCodeIvfTimesFreqFieldDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.CommonDataCheck;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

public class FreqencyUtils {

	public static FreqencyDto parseFrequecy(String frequecny) {

		FreqencyDto dto = new FreqencyDto();
		String duration = "";
		try {
			String temp = "";
			frequecny = frequecny.trim().toLowerCase().replaceAll(" ", "");
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
						try {
						dto.setLt(Integer.parseInt(temp.replace("lt", "")));
						}catch(Exception n) {
							dto.setLt(1);
						}
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
	public static Object[] getCountTimeServiceCode(List<ServiceCodeIvfTimesFreqFieldDto> li, String serviceCode,int inTpcount) {
		int count = inTpcount;
		int times = 0;
		int ctr = 0;
		String tooth = "";
		String historyTooth = "";
		String code = "";
		String surface = "";

		Set<String> dos = new HashSet<>();
		Set<String> dosWithTooth = new HashSet<>();
		Set<String> codehistory = new HashSet<>();
		String fr = "";
		String fl = "";
		if (li != null) {
			for (ServiceCodeIvfTimesFreqFieldDto s : li) {
				if (s.getServiceCode().equals(serviceCode)) {
					count = count + s.getCount();
					dos.add(s.getDos());
					dosWithTooth.add(s.getDos()+"(#"+s.getHistoryTooth()+")");
					if (s.getServiceCodeHis()!=null)codehistory.add(s.getServiceCodeHis());
					else codehistory.add("");
					fl = s.getFieldName();
					code = s.getServiceCode();
					tooth = s.getTooth();
					historyTooth= s.getHistoryTooth();
					fr = s.getFreqency();
					surface = s.getSurface();
					if (ctr == 0)
						times = s.getTimes();
					else if (times > s.getTimes())
						times = s.getTimes();/// Set to minimum value
				}
				ctr++;
			}

		}
		if (!surface.trim().equals(""))
			surface = "(" + surface + ")";
		return new Object[] { count, tooth, String.join(",", dos), fl, times, code, fr, surface,String.join(",", codehistory),historyTooth ,String.join(",", dosWithTooth)};
	}

	/*
	 * public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1,
	 * String s1, String tooth) { int ct = 0; int ti = 0; int actualmax=-1; Object[]
	 * mess = null; Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1);
	 * code=(String)c1[5]; ct = ct + (Integer) c1[0]; ti = ti + (Integer) c1[4];
	 * if(ti>0) actualmax = ti; if ( (actualmax>0 ) && (ct > 0 && ct > actualmax)) {
	 * String dos = (String) c1[2]; String fl = (String) c1[3]; mess = new Object[]
	 * { code,tooth,dos, actualmax };
	 * 
	 * } return mess; }
	 */
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			String s1, String s2, String tooth,int currentCount,boolean humana) {
		int ct = currentCount;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		Set<String> code = new HashSet<>();
		Set<String> codeH = new HashSet<>();
        Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code.add((String) c1[5]);
			codeH.add((String) c1[8]);
			
		}
		ct = ct + (Integer) c1[0];
		ti = ti + (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;
		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code.add((String) c2[5]);
			codeH.add((String) c2[8]);
		}
		ct = ct + (Integer) c2[0];
		ti = ti + (Integer) c2[4];// Times: 2 Actual Max=2
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
			Set<String> dos = new HashSet<>();
			String fl = "";
			String fr = "";
			String sur = "";
			if (l1 != null) {
				dos.add((String) c1[2]);
				fl = fl + " " + (String) c1[3];
				fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];
			}
			if (l2 != null) {
				dos.add((String) c2[2]);
				fl = fl + " " + (String) c2[3];
				fr = fr + " " + (String) c2[6];
				sur = sur + " " + (String) c2[7];
			}
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			//mess = new Object[] { code, tooth, dos, actualmax, fr, sur };
			if (humana)mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,String.join(",", codeH) };
			else mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,"" };
		}
		return mess;
	}

	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, String s1, String s2, String s3, String tooth,int currentCount) {
		int ct = currentCount;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		Set<String> code = new HashSet<>();
		Set<String> codeH = new HashSet<>();
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code.add((String)c1[5]);
		}
		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;
		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code.add((String)c2[5]);
		}
		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3,0);
		if (!((String) c3[5]).equals("")) {
			code.add((String)c3[5]);
		}
		ti = (Integer) c3[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
			Set<String> dos = new HashSet<>();
			Set<String> fr = new HashSet<>();
			String fl = "";
			String sur = "";

			if (l1 != null) {
				dos.add( (String) c1[2]);
				//dos = dos + " " + (String) c1[2];
				fl = fl + " " + (String) c1[3];
				fr.add( (String) c1[6]);
				//fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];

			}
			if (l2 != null) {
				dos.add( (String) c2[2]);
				fl = fl + " " + (String) c2[3];
				fr.add( (String) c2[6]);
				sur = sur + " " + (String) c2[7];

			}
			if (l3 != null) {
				dos.add( (String) c3[2]);
				fl = fl + " " + (String) c3[3];
				fr.add( (String) c3[6]);
				sur = sur + " " + (String) c3[7];

			}

			// 3124 code ,TOOTH,DOS, TIMES
			sur=sur.trim();
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, String.join(",", fr), sur,"" };
			//mess = new Object[] { code, tooth, dos, actualmax, fr, sur };

		}
		return mess;
	}

	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4, String s1, String s2,
			String s3, String s4, String tooth) {
		int ct = 0;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		String code = "";
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code = code + (String) c1[5];
		}

		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;

		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code = code + (String) c2[5];
		}

		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3,0);
		if (!((String) c3[5]).equals("")) {
			code = code + (String) c3[5];
		}
		ct = ct + (Integer) c3[0];
		ti = (Integer) c3[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c4 = FreqencyUtils.getCountTimeServiceCode(l4, s4,0);
		if (!((String) c4[5]).equals("")) {
			code = code + (String) c4[5];
		}

		ct = ct + (Integer) c4[0];
		ti = (Integer) c4[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
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

			// 3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			mess = new Object[] { code, tooth, dos, actualmax, fr, sur };

		}
		return mess;
	}

	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4,
			List<ServiceCodeIvfTimesFreqFieldDto> l5, String s1, String s2, String s3, String s4, String s5,
			String tooth) {
		int ct = 0;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		String code = "";
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code = code + (String) c1[5];
		}

		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;

		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code = code + (String) c2[5];
		}

		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3,0);
		if (!((String) c3[5]).equals("")) {
			code = code + (String) c3[5];
		}
		ct = ct + (Integer) c3[0];
		ti = (Integer) c3[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c4 = FreqencyUtils.getCountTimeServiceCode(l4, s4,0);
		if (!((String) c4[5]).equals("")) {
			code = code + (String) c4[5];
		}

		ct = ct + (Integer) c4[0];
		ti = (Integer) c4[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c5 = FreqencyUtils.getCountTimeServiceCode(l5, s5,0);
		if (!((String) c5[5]).equals("")) {
			code = code + (String) c5[5];
		}

		ct = ct + (Integer) c5[0];
		ti = (Integer) c5[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		System.out.println("actualmax=" + actualmax + " :ct=" + ct);
		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
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

			// 3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			mess = new Object[] { code, tooth, dos, actualmax, fr, sur };

		}
		return mess;
	}

	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4,
			List<ServiceCodeIvfTimesFreqFieldDto> l5, List<ServiceCodeIvfTimesFreqFieldDto> l6, String s1, String s2,
			String s3, String s4, String s5, String s6, String tooth) {
		int ct = 0;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		String code = "";
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code = code + (String) c1[5];
		}

		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;

		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code = code + (String) c2[5];
		}

		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3,0);
		if (!((String) c3[5]).equals("")) {
			code = code + (String) c3[5];
		}
		ct = ct + (Integer) c3[0];
		ti = (Integer) c3[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c4 = FreqencyUtils.getCountTimeServiceCode(l4, s4,0);
		if (!((String) c4[5]).equals("")) {
			code = code + (String) c4[5];
		}

		ct = ct + (Integer) c4[0];
		ti = (Integer) c4[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c5 = FreqencyUtils.getCountTimeServiceCode(l5, s5,0);
		if (!((String) c5[5]).equals("")) {
			code = code + (String) c5[5];
		}

		ct = ct + (Integer) c5[0];
		ti = (Integer) c5[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c6 = FreqencyUtils.getCountTimeServiceCode(l6, s6,0);
		if (!((String) c6[5]).equals("")) {
			code = code + (String) c6[5];
		}

		ct = ct + (Integer) c6[0];
		ti = (Integer) c6[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
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

			// 3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			mess = new Object[] { code, tooth, dos, actualmax, fr, sur };

		}
		return mess;
	}


	/**
	 * 
	 * @param l1
	 * @param l2
	 * @param l3
	 * @param l4
	 * @param l5
	 * @param l6
	 * @param l7
	 * @param l8
	 * @param s1
	 * @param s2
	 * @param s3
	 * @param s4
	 * @param s5
	 * @param s6
	 * @param s7
	 * @param s8
	 * @param tooth
	 * @param currentCount is count in treatment plan
	 * @return
	 */
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4,
			List<ServiceCodeIvfTimesFreqFieldDto> l5,String s1, String s2,
			String s3, String s4, String s5, String tooth,int currentCount,boolean humana) {
		int ct = currentCount;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		Set<String> code = new HashSet<>();
		Set<String> codeH = new HashSet<>();
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code.add((String) c1[5]);
			codeH.add((String) c1[8]);
			
		}

		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;

		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code.add((String) c2[5]);
			codeH.add((String) c2[8]);
		}

		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3,0);
		if (!((String) c3[5]).equals("")) {
			code.add((String) c3[5]);
			codeH.add((String) c3[8]);
		}
		ct = ct + (Integer) c3[0];
		ti = (Integer) c3[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c4 = FreqencyUtils.getCountTimeServiceCode(l4, s4,0);
		if (!((String) c4[5]).equals("")) {
			code.add((String) c4[5]);
			codeH.add((String) c4[8]);
		}

		ct = ct + (Integer) c4[0];
		ti = (Integer) c4[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c5 = FreqencyUtils.getCountTimeServiceCode(l5, s5,0);
		if (!((String) c5[5]).equals("")) {
			code.add((String) c5[5]);
			codeH.add((String) c5[8]);
		}

		ct = ct + (Integer) c5[0];
		ti = (Integer) c5[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		
		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
			Set<String> dos = new HashSet<>();
			
			String fl = "";
			String fr = "";
			String sur = "";

			if (l1 != null) {
				dos.add((String) c1[2]);
				fl = fl + " " + (String) c1[3];
				fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];

			}
			if (l2 != null) {
				dos.add((String) c2[2]);
				fl = fl + " " + (String) c2[3];
				fr = fr + " " + (String) c2[6];
				sur = sur + " " + (String) c2[7];

			}
			if (l3 != null) {
				dos.add((String) c3[2]);
				fl = fl + " " + (String) c3[3];
				fr = fr + " " + (String) c3[6];
				sur = sur + " " + (String) c3[7];

			}
			if (l4 != null) {
				dos.add((String) c4[2]);
				fl = fl + " " + (String) c4[3];
				fr = fr + " " + (String) c4[6];
				sur = sur + " " + (String) c4[7];

			}
			if (l5 != null) {
				dos.add((String) c5[2]);
				fl = fl + " " + (String) c5[3];
				fr = fr + " " + (String) c5[6];
				sur = sur + " " + (String) c5[7];

			}
			// 3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			if (humana)mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,String.join(",", codeH) };
			else mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,"" };

		}
		return mess;
	}
	
	/**
	 * 
	 * @param l1
	 * @param l2
	 * @param l3
	 * @param l4
	 * @param l5
	 * @param l6
	 * @param l7
	 * @param l8
	 * @param s1
	 * @param s2
	 * @param s3
	 * @param s4
	 * @param s5
	 * @param s6
	 * @param s7
	 * @param s8
	 * @param tooth
	 * @param currentCount is count in treatment plan
	 * @return
	 */
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4,
			List<ServiceCodeIvfTimesFreqFieldDto> l5, List<ServiceCodeIvfTimesFreqFieldDto> l6,List<ServiceCodeIvfTimesFreqFieldDto> l7,
			List<ServiceCodeIvfTimesFreqFieldDto> l8,List<ServiceCodeIvfTimesFreqFieldDto> l9,List<ServiceCodeIvfTimesFreqFieldDto> l10,
			List<ServiceCodeIvfTimesFreqFieldDto> l11,List<ServiceCodeIvfTimesFreqFieldDto> l12,
			String s1, String s2,
			String s3, String s4, String s5, String s6, String s7, String s8,String s9,String s10,String s11,String s12, String tooth,int currentCount,boolean humana) {
		int ct = currentCount;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		Set<String> code = new HashSet<>();
		Set<String> codeH = new HashSet<>();
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code.add((String) c1[5]);
			codeH.add((String) c1[8]);
			
		}

		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;

		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code.add((String) c2[5]);
			codeH.add((String) c2[8]);
		}

		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3,0);
		if (!((String) c3[5]).equals("")) {
			code.add((String) c3[5]);
			codeH.add((String) c3[8]);
		}
		ct = ct + (Integer) c3[0];
		ti = (Integer) c3[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c4 = FreqencyUtils.getCountTimeServiceCode(l4, s4,0);
		if (!((String) c4[5]).equals("")) {
			code.add((String) c4[5]);
			codeH.add((String) c4[8]);
		}

		ct = ct + (Integer) c4[0];
		ti = (Integer) c4[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c5 = FreqencyUtils.getCountTimeServiceCode(l5, s5,0);
		if (!((String) c5[5]).equals("")) {
			code.add((String) c5[5]);
			codeH.add((String) c5[8]);
		}

		ct = ct + (Integer) c5[0];
		ti = (Integer) c5[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c6 = FreqencyUtils.getCountTimeServiceCode(l6, s6,0);
		if (!((String) c6[5]).equals("")) {
			code.add((String) c6[5]);
			codeH.add((String) c6[8]);
		}

		ct = ct + (Integer) c6[0];
		ti = (Integer) c6[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		
		Object[] c7 = FreqencyUtils.getCountTimeServiceCode(l7, s7,0);
		if (!((String) c7[5]).equals("")) {
			code.add((String) c7[5]);
			codeH.add((String) c7[8]);
		}

		ct = ct + (Integer) c7[0];
		ti = (Integer) c7[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		
		Object[] c8 = FreqencyUtils.getCountTimeServiceCode(l8, s8,0);
		if (!((String) c8[5]).equals("")) {
			code.add((String) c8[5]);
			codeH.add((String) c8[8]);
			
		}

		ct = ct + (Integer) c8[0];
		ti = (Integer) c8[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		
		Object[] c9 = FreqencyUtils.getCountTimeServiceCode(l9, s9,0);
		if (!((String) c9[5]).equals("")) {
			code.add((String) c9[5]);
			codeH.add((String) c9[8]);
			
		}

		ct = ct + (Integer) c9[0];
		ti = (Integer) c9[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c10 = FreqencyUtils.getCountTimeServiceCode(l10, s10,0);
		if (!((String) c10[5]).equals("")) {
			code.add((String) c10[5]);
			codeH.add((String) c10[8]);
			
		}

		ct = ct + (Integer) c10[0];
		ti = (Integer) c10[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		
		Object[] c11 = FreqencyUtils.getCountTimeServiceCode(l11, s11,0);
		if (!((String) c11[5]).equals("")) {
			code.add((String) c11[5]);
			codeH.add((String) c11[8]);
			
		}

		ct = ct + (Integer) c11[0];
		ti = (Integer) c11[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c12 = FreqencyUtils.getCountTimeServiceCode(l12, s12,0);
		if (!((String) c12[5]).equals("")) {
			code.add((String) c12[5]);
			codeH.add((String) c12[8]);
			
		}

		ct = ct + (Integer) c12[0];
		ti = (Integer) c12[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		
		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
			Set<String> dos = new HashSet<>();
			
			String fl = "";
			String fr = "";
			String sur = "";

			if (l1 != null) {
				dos.add((String) c1[2]);
				fl = fl + " " + (String) c1[3];
				fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];

			}
			if (l2 != null) {
				dos.add((String) c2[2]);
				fl = fl + " " + (String) c2[3];
				fr = fr + " " + (String) c2[6];
				sur = sur + " " + (String) c2[7];

			}
			if (l3 != null) {
				dos.add((String) c3[2]);
				fl = fl + " " + (String) c3[3];
				fr = fr + " " + (String) c3[6];
				sur = sur + " " + (String) c3[7];

			}
			if (l4 != null) {
				dos.add((String) c4[2]);
				fl = fl + " " + (String) c4[3];
				fr = fr + " " + (String) c4[6];
				sur = sur + " " + (String) c4[7];

			}
			if (l5 != null) {
				dos.add((String) c5[2]);
				fl = fl + " " + (String) c5[3];
				fr = fr + " " + (String) c5[6];
				sur = sur + " " + (String) c5[7];

			}
			if (l6 != null) {
				dos.add((String) c6[2]);
				fl = fl + " " + (String) c6[3];
				fr = fr + " " + (String) c6[6];
				sur = sur + " " + (String) c6[7];

			}
			if (l7 != null) {
				dos.add((String) c7[2]);
				fl = fl + " " + (String) c7[3];
				fr = fr + " " + (String) c7[6];
				sur = sur + " " + (String) c7[7];

			}
			if (l8 != null) {
				dos.add((String) c8[2]);
				fl = fl + " " + (String) c8[3];
				fr = fr + " " + (String) c8[6];
				sur = sur + " " + (String) c8[7];

			}
			if (l9 != null) {
				dos.add((String) c9[2]);
				fl = fl + " " + (String) c9[3];
				fr = fr + " " + (String) c9[6];
				sur = sur + " " + (String) c9[7];

			}
			if (l10 != null) {
				dos.add((String) c10[2]);
				fl = fl + " " + (String) c10[3];
				fr = fr + " " + (String) c10[6];
				sur = sur + " " + (String) c10[7];

			}
			if (l11 != null) {
				dos.add((String) c11[2]);
				fl = fl + " " + (String) c11[3];
				fr = fr + " " + (String) c11[6];
				sur = sur + " " + (String) c11[7];

			}
			if (l12 != null) {
				dos.add((String) c12[2]);
				fl = fl + " " + (String) c12[3];
				fr = fr + " " + (String) c12[6];
				sur = sur + " " + (String) c12[7];

			}

			// 3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			if (humana)mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,String.join(",", codeH) };
			else mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,"" };

		}
		return mess;
	}
	
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4,
			List<ServiceCodeIvfTimesFreqFieldDto> l5, List<ServiceCodeIvfTimesFreqFieldDto> l6,List<ServiceCodeIvfTimesFreqFieldDto> l7,
			String s1, String s2,
			String s3, String s4, String s5, String s6, String s7, String tooth,int currentCount,String toothinTP,List<Object> tpList,boolean humana) {
		int ct = currentCount;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		Set<String> code = new HashSet<>();
		Set<String> codeH = new HashSet<>();
		Object c1[] = FreqencyUtils.getCountTimeServiceCode(l1, s1,0);
		if (!((String) c1[5]).equals("")) {
			code.add((String) c1[5]);
			codeH.add((String) c1[8]);
			
		}

		ct = ct + (Integer) c1[0];
		ti = (Integer) c1[4];
		if (ti > 0)
			actualmax = ti;

		Object[] c2 = FreqencyUtils.getCountTimeServiceCode(l2, s2,0);
		if (!((String) c2[5]).equals("")) {
			code.add((String) c2[5]);
			codeH.add((String) c2[8]);
		}

		ct = ct + (Integer) c2[0];
		ti = (Integer) c2[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c3 = FreqencyUtils.getCountTimeServiceCode(l3, s3,0);
		if (!((String) c3[5]).equals("")) {
			code.add((String) c3[5]);
			codeH.add((String) c3[8]);
		}
		ct = ct + (Integer) c3[0];
		ti = (Integer) c3[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c4 = FreqencyUtils.getCountTimeServiceCode(l4, s4,0);
		if (!((String) c4[5]).equals("")) {
			code.add((String) c4[5]);
			codeH.add((String) c4[8]);
		}

		ct = ct + (Integer) c4[0];
		ti = (Integer) c4[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c5 = FreqencyUtils.getCountTimeServiceCode(l5, s5,0);
		if (!((String) c5[5]).equals("")) {
			code.add((String) c5[5]);
			codeH.add((String) c5[8]);
		}

		ct = ct + (Integer) c5[0];
		ti = (Integer) c5[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value

		Object[] c6 = FreqencyUtils.getCountTimeServiceCode(l6, s6,0);
		if (!((String) c6[5]).equals("")) {
			code.add((String) c6[5]);
			codeH.add((String) c6[8]);
		}

		ct = ct + (Integer) c6[0];
		ti = (Integer) c6[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		
		Object[] c7 = FreqencyUtils.getCountTimeServiceCode(l7, s7,0);
		if (!((String) c7[5]).equals("")) {
			code.add((String) c7[5]);
			codeH.add((String) c7[8]);
		}

		ct = ct + (Integer) c7[0];
		ti = (Integer) c7[4];
		if ((ti > 0 && actualmax == -1) || (ti > 0 && actualmax > ti))
			actualmax = ti;/// Set to minimum value
		
		
		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
			Set<String> dos = new HashSet<>();
			Set<String> dosWithTooth = new HashSet<>();
			String fl = "";
			String fr = "";
			String sur = "";

			if (l1 != null) {
				dos.add((String) c1[2]);
				dosWithTooth.add((String) c1[10]);
				fl = fl + " " + (String) c1[3];
				fr = fr + " " + (String) c1[6];
				sur = sur + " " + (String) c1[7];

			}
			if (l2 != null) {
				dos.add((String) c2[2] );
				dosWithTooth.add((String) c2[10]);
				fl = fl + " " + (String) c2[3];
				fr = fr + " " + (String) c2[6];
				sur = sur + " " + (String) c2[7];

			}
			if (l3 != null) {
				dos.add((String) c3[2]);
				dosWithTooth.add((String) c3[10]);
				fl = fl + " " + (String) c3[3];
				fr = fr + " " + (String) c3[6];
				sur = sur + " " + (String) c3[7];

			}
			if (l4 != null) {
				dos.add((String) c4[2]);
				dosWithTooth.add((String) c4[10]);
				fl = fl + " " + (String) c4[3];
				fr = fr + " " + (String) c4[6];
				sur = sur + " " + (String) c4[7];

			}
			if (l5 != null) {
				dos.add((String) c5[2] );
				dosWithTooth.add((String) c4[10]);
				fl = fl + " " + (String) c5[3];
				fr = fr + " " + (String) c5[6];
				sur = sur + " " + (String) c5[7];

			}
			if (l6 != null) {
				dos.add((String) c6[2] );
				dosWithTooth.add((String) c6[10]);
				fl = fl + " " + (String) c6[3];
				fr = fr + " " + (String) c6[6];
				sur = sur + " " + (String) c6[7];

			}
			if (l7 != null) {
				dos.add((String) c7[2] );
				dosWithTooth.add((String) c7[10]);
				fl = fl + " " + (String) c7[3];
				fr = fr + " " + (String) c7[6];
				sur = sur + " " + (String) c7[7];

			}
			

			// 3124 code ,TOOTH,DOS, TIMES
			if (tooth.equals("NA")) {
				tooth=toothinTP;
			}
			Set<String> ths= new HashSet<>();
			ths.add(tooth);
			for (Object t : tpList) {
				CommonDataCheck tp = (CommonDataCheck) t;
				if (tp.getServiceCode().equalsIgnoreCase(s1) || tp.getServiceCode().equalsIgnoreCase(s2)
					|| tp.getServiceCode().equalsIgnoreCase(s3) || tp.getServiceCode().equalsIgnoreCase(s4)
					|| tp.getServiceCode().equalsIgnoreCase(s5) || tp.getServiceCode().equalsIgnoreCase(s6)
					|| tp.getServiceCode().equalsIgnoreCase(s7)) {
					ths.add(tp.getTooth());
				}
			}
			if (!sur.trim().equals(""))
				sur = "(" + sur + ")";
			if (humana)mess = new Object[] { String.join(",", code), String.join(",", ths), String.join(",", dosWithTooth), actualmax, fr, sur,String.join(",", codeH) };
			else mess = new Object[] { String.join(",", code), String.join(",", ths), String.join(",", dosWithTooth), actualmax, fr, sur,"" };

		}
		return mess;
	}


	private static Object [] CustumErrorData(List<ServiceCodeIvfTimesFreqFieldDto> l,String s,int actualmax,Set<String> code,Set<String> codeH,int ct,int ti) {
		Object c[] = FreqencyUtils.getCountTimeServiceCode(l, s,0);
		if (!((String) c[5]).equals("")) {
			code.add((String) c[5]);
			codeH.add((String) c[8]);
			
		}

		ct = ct + (Integer) c[0];
		ti = (Integer) c[4];
		if (ti > 0)
			actualmax = ti;
 
		 return c;
	}
	
	private static void CustumErrorDataString(Set<String> dos,String fl,String fr,String sur,List<ServiceCodeIvfTimesFreqFieldDto> l,Object[] c) {

		if (l != null) {
			dos.add((String) c[2]);
			fl = fl + " " + (String) c[3];
			fr = fr + " " + (String) c[6];
			sur = sur + " " + (String) c[7];
	 }
	}
		
	//currentCount ==0 for Frequency rules
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4,
			List<ServiceCodeIvfTimesFreqFieldDto> l5, List<ServiceCodeIvfTimesFreqFieldDto> l6,List<ServiceCodeIvfTimesFreqFieldDto> l7,
			List<ServiceCodeIvfTimesFreqFieldDto> l8,List<ServiceCodeIvfTimesFreqFieldDto> l9,List<ServiceCodeIvfTimesFreqFieldDto> l10,
			String s1, String s2,
			String s3, String s4, String s5, String s6, String s7, String s8,String s9,String s10, String tooth,int currentCount,boolean humana) {
		int ct = currentCount;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		Set<String> code = new HashSet<>();
		Set<String> codeH = new HashSet<>();
		Object c1[] =CustumErrorData(l1, s1, actualmax, code, codeH, actualmax, ti);
		Object c2[] =CustumErrorData(l2, s2 ,actualmax, code, codeH, actualmax, ti);
		Object c3[] =CustumErrorData(l3, s3, actualmax, code, codeH, actualmax, ti);
		Object c4[] =CustumErrorData(l4, s4, actualmax, code, codeH, actualmax, ti);
		Object c5[] =CustumErrorData(l5, s5, actualmax, code, codeH, actualmax, ti);
		Object c6[] =CustumErrorData(l6, s6, actualmax, code, codeH, actualmax, ti);
		Object c7[] =CustumErrorData(l7, s7, actualmax, code, codeH, actualmax, ti);
		Object c8[] =CustumErrorData(l8, s8, actualmax, code, codeH, actualmax, ti);
		Object c9[] =CustumErrorData(l9, s9, actualmax, code, codeH, actualmax, ti);
		Object c10[] =CustumErrorData(l10, s10, actualmax, code, codeH, actualmax, ti);
		
		
		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
			Set<String> dos = new HashSet<>();
			
			String fl = "";
			String fr = "";
			String sur = "";

			if (l1 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l1, c1);
			}
			if (l2 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l2, c2);

			}
			if (l3 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l3, c3);

			}
			if (l4 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l4, c4);

			}
			if (l5 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l5, c5);

			}
			if (l6 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l6, c6);

			}
			if (l7 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l7, c7);

			}
			if (l8 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l8, c8);

			}
			if (l9 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l9, c9);

			}
			if (l10 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l10, c10);

			}
	
			// 3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			if (humana)mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,String.join(",", codeH) };
			else mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,"" };

		}
		return mess;
	}

	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1, List<ServiceCodeIvfTimesFreqFieldDto> l2,
			List<ServiceCodeIvfTimesFreqFieldDto> l3, List<ServiceCodeIvfTimesFreqFieldDto> l4,
			List<ServiceCodeIvfTimesFreqFieldDto> l5, List<ServiceCodeIvfTimesFreqFieldDto> l6,List<ServiceCodeIvfTimesFreqFieldDto> l7,
			List<ServiceCodeIvfTimesFreqFieldDto> l8,List<ServiceCodeIvfTimesFreqFieldDto> l9,List<ServiceCodeIvfTimesFreqFieldDto> l10,
			List<ServiceCodeIvfTimesFreqFieldDto> l11,List<ServiceCodeIvfTimesFreqFieldDto> l12,List<ServiceCodeIvfTimesFreqFieldDto> l13,
			List<ServiceCodeIvfTimesFreqFieldDto> l14,List<ServiceCodeIvfTimesFreqFieldDto> l15,List<ServiceCodeIvfTimesFreqFieldDto> l16,
			List<ServiceCodeIvfTimesFreqFieldDto> l17,
			String s1, String s2,
			String s3, String s4, String s5, String s6, String s7, String s8,String s9,String s10,String s11,String s12,
			String s13,String s14,String s15,String s16,String s17,
			String tooth,int currentCount,boolean humana) {
		int ct = currentCount;
		int ti = 0;
		int actualmax = -1;
		Object[] mess = null;
		Set<String> code = new HashSet<>();
		Set<String> codeH = new HashSet<>();
		Object c1[] =CustumErrorData(l1, s1, actualmax, code, codeH, actualmax, ti);
		Object c2[] =CustumErrorData(l2, s2 ,actualmax, code, codeH, actualmax, ti);
		Object c3[] =CustumErrorData(l3, s3, actualmax, code, codeH, actualmax, ti);
		Object c4[] =CustumErrorData(l4, s4, actualmax, code, codeH, actualmax, ti);
		Object c5[] =CustumErrorData(l5, s5, actualmax, code, codeH, actualmax, ti);
		Object c6[] =CustumErrorData(l6, s6, actualmax, code, codeH, actualmax, ti);
		Object c7[] =CustumErrorData(l7, s7, actualmax, code, codeH, actualmax, ti);
		Object c8[] =CustumErrorData(l8, s8, actualmax, code, codeH, actualmax, ti);
		Object c9[] =CustumErrorData(l9, s9, actualmax, code, codeH, actualmax, ti);
		Object c10[] =CustumErrorData(l10, s10, actualmax, code, codeH, actualmax, ti);
		Object c11[] =CustumErrorData(l11, s11, actualmax, code, codeH, actualmax, ti);
		Object c12[] =CustumErrorData(l12, s12, actualmax, code, codeH, actualmax, ti);
		Object c13[] =CustumErrorData(l13, s13, actualmax, code, codeH, actualmax, ti);
		Object c14[] =CustumErrorData(l14, s14, actualmax, code, codeH, actualmax, ti);
		Object c15[] =CustumErrorData(l15, s15, actualmax, code, codeH, actualmax, ti);
		Object c16[] =CustumErrorData(l16, s16, actualmax, code, codeH, actualmax, ti);
		Object c17[] =CustumErrorData(l17, s17, actualmax, code, codeH, actualmax, ti);
		
		
		if ((actualmax > 0) && (ct > 0 && ct > actualmax)) {
			Set<String> dos = new HashSet<>();
			
			String fl = "";
			String fr = "";
			String sur = "";

			if (l1 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l1, c1);
			}
			if (l2 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l2, c2);

			}
			if (l3 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l3, c3);

			}
			if (l4 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l4, c4);

			}
			if (l5 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l5, c5);

			}
			if (l6 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l6, c6);

			}
			if (l7 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l7, c7);

			}
			if (l8 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l8, c8);

			}
			if (l9 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l9, c9);

			}
			if (l10 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l10, c10);

			}
			if (l11 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l11, c11);
			}
			if (l12 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l12, c12);
			}
			if (l13 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l13, c13);
			}
			if (l14 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l14, c14);
			}
			if (l15 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l15, c15);
			}
			if (l16 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l16, c16);
			}
			if (l17 != null) {
				CustumErrorDataString(dos, fl, fr, sur, l17, c17);
			}
	
			// 3124 code ,TOOTH,DOS, TIMES
			if (!sur.equals(""))
				sur = "(" + sur + ")";
			if (humana)mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,String.join(",", codeH) };
			else mess = new Object[] { String.join(",", code), tooth, String.join(",", dos), actualmax, fr, sur,"" };

		}
		return mess;
	}
	
	public static boolean checkforAlikeCodesNew1(String tpCodes, String historyCode,String shareFr) {
		boolean alikecodepresent = false;
		/*
		 * if ( (tpCodes.equals("D0150") || tpCodes.equals("D0120") ||
		 * tpCodes.equals("D0145") || tpCodes.equals("D0160")) &&
		 * (historyCode.equals("D0150") || historyCode.equals("D0120") ||
		 * historyCode.equals("D0145") || historyCode.equals("D0160")) ) {
		 * alikecodepresent = true; }
		 */
		/*if ((tpCodes.equals("D0150") || tpCodes.equals("D0120") || tpCodes.equals("D0145") || tpCodes.equals("D0140")
				|| tpCodes.equals("D0160"))
				&& (historyCode.equals("D0150") || historyCode.equals("D0120") || historyCode.equals("D0145")
						|| historyCode.equals("D0140") || historyCode.equals("D0160"))) {
			alikecodepresent = true;
		}*/
		if (shareFr.equalsIgnoreCase("yes") &&  ((tpCodes.equals("D0120") || tpCodes.equals("D0150"))
				&& (historyCode.equals("D0150") || historyCode.equals("D0120")) )) {
			alikecodepresent = true;
		}

		if ((tpCodes.equals("D0210") || tpCodes.equals("D0330"))
				&& (historyCode.equals("D0210") || historyCode.equals("D0330"))) {
			alikecodepresent = true;
		}

		return alikecodepresent;
	}

	public static boolean checkforAlikeCodesHumana(String tpCodes, String historyCode) {
		boolean alikecodepresent = false;
		/*
		 * if ( (tpCodes.equals("D0150") || tpCodes.equals("D0120") ||
		 * tpCodes.equals("D0145") || tpCodes.equals("D0160")) &&
		 * (historyCode.equals("D0150") || historyCode.equals("D0120") ||
		 * historyCode.equals("D0145") || historyCode.equals("D0160")) ) {
		 * alikecodepresent = true; }
		 */
		if ((tpCodes.equals("D2391") || tpCodes.equals("D2392") || tpCodes.equals("D2393") || tpCodes.equals("D2394")
				|| tpCodes.equals("D2330") || tpCodes.equals("D2331") || tpCodes.equals("D2332")|| tpCodes.equals("D2335") 
				|| tpCodes.equals("D2140") || tpCodes.equals("D2150") || tpCodes.equals("D2160")|| tpCodes.equals("D2161"))
				&& (historyCode.equals("D2391") || historyCode.equals("D2392") || historyCode.equals("D2393") || historyCode.equals("D2394") 
					|| historyCode.equals("D2330") || historyCode.equals("D2331") || historyCode.equals("D2332") || historyCode.equals("D2335")
					|| historyCode.equals("D2140") || historyCode.equals("D2150") || historyCode.equals("D2160")|| historyCode.equals("D2161"))) {
			alikecodepresent = true;
		}
		if ((tpCodes.equals("D7210") || tpCodes.equals("D7140"))
				&& (historyCode.equals("D7210") || historyCode.equals("D7140"))) {
			alikecodepresent = true;
		}
		if ((tpCodes.equals("D2740") || tpCodes.equals("D2750") || tpCodes.equals("D6740") || tpCodes.equals("D6245") || tpCodes.equals("D2790"))
				&& (historyCode.equals("D2740") || historyCode.equals("D2750") || historyCode.equals("D6740") || historyCode.equals("D6245") || historyCode.equals("D2790"))) {
			alikecodepresent = true;
		}

		return alikecodepresent;
	}

	public static boolean checkforAlikeCodes(String tpCodes, String historyCode, String shareFr) {
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
		if (shareFr.equalsIgnoreCase("yes") && (tpCodes.equals("D0150") && historyCode.equals("D0120"))) {
			alikecodepresent = true;
		}
		if (shareFr.equalsIgnoreCase("yes") && (tpCodes.equals("D0120") && historyCode.equals("D0150"))) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0120") && historyCode.equals("D0140")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0140") && historyCode.equals("D0120")) {
			alikecodepresent = true;
		}
		/*if (tpCodes.equals("D0150") && historyCode.equals("D0140")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D0140") && historyCode.equals("D0150")) {
			alikecodepresent = true;
		}*/
		//
		if (tpCodes.equals("D5110") && historyCode.equals("D5130")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D5130") && historyCode.equals("D5110")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D5120") && historyCode.equals("D5140")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D5140") && historyCode.equals("D5120")) {
			alikecodepresent = true;
		}
		if (tpCodes.equals("D4910") && historyCode.equals("D1110")) {
			alikecodepresent = true;
		}
		// 3C D5225, D5211, D5213
		if (compairThreeVlaues(tpCodes, historyCode, "D5225", "D5211", "D5213") != null) {
			alikecodepresent = true;
		}
		// 3C D5226, D5212, D5214
		if (compairThreeVlaues(tpCodes, historyCode, "D5226", "D5212", "D5214") != null) {
			alikecodepresent = true;
		}

		// 3C D2391, M2391, P2391
		if (compairThreeVlaues(tpCodes, historyCode, "D2391", "M2391", "P2391") != null) {
			alikecodepresent = true;
		}
		// 3C D2392, M2392, P2392
		if (compairThreeVlaues(tpCodes, historyCode, "D2392", "M2392", "P2392") != null) {
			alikecodepresent = true;
		}
		//
		// 3C D2393, M2393, P2393
		if (compairThreeVlaues(tpCodes, historyCode, "D2393", "M2393", "P2393") != null) {
			alikecodepresent = true;
		}
		//
		// 3C D2394, M2394, P2394
		if (compairThreeVlaues(tpCodes, historyCode, "D2394", "M2394", "P2394") != null) {
			alikecodepresent = true;
		}
		if (compairSevenValues(tpCodes, historyCode, "D7111", "D7140", "D7210","D7220", "D7230","D7240", "D7250") != null) {
			alikecodepresent = true;
		}
		
		if (compairFourVlaues(tpCodes, historyCode, "D6245", "D6740", "D2740","D2750") != null) {
			alikecodepresent = true;
		}

		return alikecodepresent;
	}
	
	public static boolean checkforAlikeCodesrule122(String tpCodes, String historyCode) {
		boolean alikecodepresent = false;
		
		if (compairThreeVlaues(tpCodes, historyCode, "D0140", "D0150", "D0120") != null) {
			alikecodepresent = true;
		}
		

		return alikecodepresent;
	}

	private static Boolean compairThreeVlaues(String tpCodes, String historyCode, String one, String two,
			String three) {
		Boolean b = null;
		// boolean alikecodepresent=false;

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
	
	private static Boolean compairFourVlaues(String tpCodes, String historyCode, String one, String two,
			String three, String four) {
		Boolean b = null;
		// boolean alikecodepresent=false;
 
		if (tpCodes.equals(one) && (historyCode.equals(two) || historyCode.equals(three) ||
				historyCode.equals(four) )) {
			b = true;
		}
		if (tpCodes.equals(two) && (historyCode.equals(one) || historyCode.equals(three) ||
				historyCode.equals(four) )) {
			b = true;
		}
		if (tpCodes.equals(three) && (historyCode.equals(one) || historyCode.equals(two) ||
				historyCode.equals(four))) {
			b = true;
		}
		if (tpCodes.equals(four) && (historyCode.equals(one) || historyCode.equals(two) ||
				historyCode.equals(three) )) {
			b = true;
		}
		
	
		return b;
	}

	private static Boolean compairSevenValues(String tpCodes, String historyCode, String one, String two,
			String three,String four,String five,String six,String seven) {
		Boolean b = null;
		// boolean alikecodepresent=false;
 
		if (tpCodes.equals(one) && (historyCode.equals(two) || historyCode.equals(three) ||
				historyCode.equals(four) ||historyCode.equals(five) || historyCode.equals(six) || historyCode.equals(seven))) {
			b = true;
		}
		if (tpCodes.equals(two) && (historyCode.equals(one) || historyCode.equals(three) ||
				historyCode.equals(four) ||historyCode.equals(five) || historyCode.equals(six) || historyCode.equals(seven))) {
			b = true;
		}
		if (tpCodes.equals(three) && (historyCode.equals(one) || historyCode.equals(two) ||
				historyCode.equals(four) ||historyCode.equals(five) || historyCode.equals(six) || historyCode.equals(seven))) {
			b = true;
		}
		if (tpCodes.equals(four) && (historyCode.equals(one) || historyCode.equals(two) ||
				historyCode.equals(three) ||historyCode.equals(five) || historyCode.equals(six) || historyCode.equals(seven))) {
			b = true;
		}
		if (tpCodes.equals(five) && (historyCode.equals(one) || historyCode.equals(two) ||
				historyCode.equals(three) ||historyCode.equals(four) || historyCode.equals(six) || historyCode.equals(seven))) {
			b = true;
		}

		if (tpCodes.equals(six) && (historyCode.equals(one) || historyCode.equals(two) ||
				historyCode.equals(three) ||historyCode.equals(four) || historyCode.equals(five) || historyCode.equals(seven))) {
			b = true;
		}
		if (tpCodes.equals(seven) && (historyCode.equals(one) || historyCode.equals(two) ||
				historyCode.equals(three) ||historyCode.equals(four) || historyCode.equals(five) || historyCode.equals(six))) {
			b = true;
		}
	
		return b;
	}

	public static void addToFailedSet(Set<String> failedCodeSet, Object[] m) {

		if (failedCodeSet != null && m != null && m.length > 0) {
			failedCodeSet.add((String) m[0]);
		}

	}

	public static List<TPValidationResponseDto> ivfFrequencyLogic(List<ServiceCodeIvfTimesFreqFieldDto> dataIVF,
			String tpCode, String tooth, ToothHistoryDto historyD, Class<?> clazz, BufferedWriter bw, int CurrentYear,
			Date planDate, MessageSource messageSource, Rules rule, IVFTableSheet ivf, Date TP_Date, Locale locale,
			Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> mapFlIVFFinal, Set<String> fcodes, Set<String> surfaces,
			Set<String> teethC, String ruleMessageName) {
		//ruleMessageName="rule21";
		boolean present = false;
		String benefitPeriodYear=ivf.getPlanCalendarFiscalYear();
		if (benefitPeriodYear==null) benefitPeriodYear="";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		RuleEngineLogger.generateLogs(clazz, "planDate :" + planDate, Constants.rule_log_debug, bw);
		for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF) {
			String freq = scivfTFD.getFreqency();

			ServiceCodeIvfTimesFreqFieldDto scivfTFDFinal = new ServiceCodeIvfTimesFreqFieldDto(tpCode,
					scivfTFD.getFieldName(), scivfTFD.getFreqency(), 0, 0, "");
			if (historyD!=null) scivfTFDFinal.setServiceCodeHis(historyD.getHistoryCode());
			scivfTFDFinal.setTooth(tooth);
			scivfTFDFinal.setSurface(historyD.getSurfaceTooth());
			scivfTFDFinal.setDos(historyD.getHistoryDos());
			RuleEngineLogger.generateLogs(clazz, "HISTORY CODE- " + historyD.getHistoryCode(), Constants.rule_log_debug,
					bw);
			RuleEngineLogger.generateLogs(clazz, "HISTORY DOS- " + historyD.getHistoryDos(), Constants.rule_log_debug,
					bw);

			RuleEngineLogger.generateLogs(clazz, "Frequency- " + freq, Constants.rule_log_debug, bw);
			if (freq == null ) {
				continue;
			}
			if (historyD.getHistoryDos().equals("") || freq.equalsIgnoreCase("") || freq.equalsIgnoreCase("NF")
					|| freq.equalsIgnoreCase("no frequency"))
				continue;
			// System.out.println("DDDDDDDDDDDDDD-"+freq);
			FreqencyDto FDTO = FreqencyUtils.parseFrequecy(freq);
			Date[] datesFIS = DateUtils.getFiscalYear(FDTO.getFy());
			int ti = FDTO.getTimes();
			scivfTFDFinal.setTimes(ti);
			RuleEngineLogger.generateLogs(clazz, "Tooth Number-" + tooth, Constants.rule_log_debug, bw);

			Date dos = null;
			try {
				dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(historyD.getHistoryDos());
				RuleEngineLogger.generateLogs(clazz, "History DOS-" + historyD.getHistoryDos(),
						Constants.rule_log_debug, bw);
			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			RuleEngineLogger.generateLogs(clazz, "TIMES:" + ti, Constants.rule_log_debug, bw);
			if (FDTO.getFy() > 0) {// Fiscal Year
				// isFiscalpresent=true;
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + FDTO.getFy(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + datesFIS[0] + "-" + datesFIS[1],
						Constants.rule_log_debug, bw);
				boolean fiscal = DateUtils.isDatesBetweenDates(datesFIS[0], datesFIS[1], dos);
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + fiscal, Constants.rule_log_debug, bw);
				if (fiscal) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
					// expCount = expCount + 1;
				}
			} else if (FDTO.getCy() > 0) {// Calendar Year
				
				/*if (benefitPeriodYear.equalsIgnoreCase("PY") || benefitPeriodYear.equalsIgnoreCase("FY")) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}else {*/
					
				
					RuleEngineLogger.generateLogs(clazz, "Calendar Year:" + FDTO.getCy(), Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "CurrentYear:" + CurrentYear, Constants.rule_log_debug, bw);
					Date[] calcy = DateUtils.getCalendarYear(FDTO.getCy());
					RuleEngineLogger.generateLogs(clazz, "DATE RANGE: FROM -" + calcy[0], Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "DATE RANGE: TO -" + calcy[1], Constants.rule_log_debug, bw);
	
					// isCalPresent=true;
					// CurrentYear
					if (DateUtils.isDatesBetweenDates(calcy[0], calcy[1], dos)) {
						present = true;
						scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
					}
			   //}
				/*
				 * present=true;//Remove after testing..
				 * scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);//Remove after testing..
				 */

			} else if (FDTO.getLt() > 0) {// Life Time
				RuleEngineLogger.generateLogs(clazz, "Life Time:" + FDTO.getLt(), Constants.rule_log_debug, bw);
				// isLifeTimePresent=true;
				present = true;
				scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);

			} else if (FDTO.getPy() > 0) {// Plan Year
				RuleEngineLogger.generateLogs(clazz, "Plan Year:" + FDTO.getPy(), Constants.rule_log_debug, bw);
				// isPlanYearPresent=true;
				Calendar calendar = new GregorianCalendar();
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule21.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				Date nextDate = DateUtils.getNextYear(planDate);
				// calendar.set(calendar.get(Calendar.YEAR)+1,calendar.get(Calendar.MONTH),
				// calendar.get(Calendar.DATE)+1);
				calendar.setTime(dos);
				boolean fiscal = DateUtils.isDatesBetweenDates(planDate, nextDate, dos);
				if (fiscal) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			} else if (FDTO.getCalendarMonth() > 0) {// Calendar Months Done (cal.mo)
				if (benefitPeriodYear.equalsIgnoreCase("PY") || benefitPeriodYear.equalsIgnoreCase("FY")) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}else {
				RuleEngineLogger.generateLogs(clazz, "Calendar Months:" + FDTO.getCalendarMonth(),
						Constants.rule_log_debug, bw);
				// (1X6cal.mo) Plan Date 1 Jan - 31 JAN --> jan-june and july-Dec. two
				// phase..
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule21.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				for (int x = 0; x <= 11;) {

					x = x + FDTO.getCalendarMonth();
					int initMonth = x - FDTO.getCalendarMonth();
					int endMonth = (x - 1);

					Calendar calendar = new GregorianCalendar();
					calendar.setTime(planDate);
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + initMonth,
							calendar.get(Calendar.DATE));
					initMonth = calendar.get(Calendar.MONTH);

					calendar = new GregorianCalendar();
					calendar.setTime(planDate);
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + endMonth,
							calendar.get(Calendar.DATE));
					endMonth = calendar.get(Calendar.MONTH);

					RuleEngineLogger.generateLogs(clazz, "Initial Calendar Month for Plan Date is:" + (initMonth + 1),
							Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "End Month for Plan Date is:" + (endMonth + 1),
							Constants.rule_log_debug, bw);

					calendar = new GregorianCalendar();
					calendar.setTime(dos);
					int dosmonth = calendar.get(Calendar.MONTH);

					calendar = new GregorianCalendar();
					calendar.setTime(TP_Date);
					int tpmonth = calendar.get(Calendar.MONTH);

					RuleEngineLogger.generateLogs(clazz, "Month for DOS is:" + (dosmonth + 1), Constants.rule_log_debug,
							bw);
					RuleEngineLogger.generateLogs(clazz, "Month for TP is:" + (tpmonth + 1), Constants.rule_log_debug,
							bw);
					RuleEngineLogger.generateLogs(clazz,
							" IS Month for DOS-(" + (dosmonth + 1) + ") is between Initial Calendar Month:("
									+ (initMonth + 1) + ") and End Month for Plan(" + (endMonth + 1) + ")==>"
									+ (initMonth <= dosmonth && dosmonth >= endMonth),
							Constants.rule_log_debug, bw);

					RuleEngineLogger.generateLogs(clazz,
							" IS Month for TP-(" + (tpmonth + 1) + ") is between Initial Calendar Month:("
									+ (initMonth + 1) + ") and End Month for Plan(" + (endMonth + 1) + ")==>"
									+ (initMonth <= tpmonth && tpmonth >= endMonth),
							Constants.rule_log_debug, bw);

					if ((initMonth <= dosmonth && dosmonth >= endMonth)
							&& (initMonth <= tpmonth && tpmonth >= endMonth)) {
						present = true;
						scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
					}
				 }
				}

			} else if (FDTO.getDays() > 0) {// Months & Days (1x6Mo_1D)
				/*if (benefitPeriodYear.equalsIgnoreCase("PY") || benefitPeriodYear.equalsIgnoreCase("FY")) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}else {*/
				RuleEngineLogger.generateLogs(clazz, " Days:" + FDTO.getDays(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Months :" + FDTO.getMonths(), Constants.rule_log_debug, bw);
				//
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule21.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
						nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(), nextAvailbleDate.get(Calendar.DATE));

				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR), nextAvailbleDate.get(Calendar.MONTH),
						nextAvailbleDate.get(Calendar.DATE) + FDTO.getDays());
				//
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 June Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			  //}
			} else if (FDTO.getOnlyDays() > 0) {// Days
				/*if (benefitPeriodYear.equalsIgnoreCase("PY") || benefitPeriodYear.equalsIgnoreCase("FY")) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}else {*/
				RuleEngineLogger.generateLogs(clazz, "ONLY DAYS :" + FDTO.getOnlyDays(), Constants.rule_log_debug, bw);
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule21.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR), nextAvailbleDate.get(Calendar.MONTH),
						nextAvailbleDate.get(Calendar.DATE) + FDTO.getOnlyDays());
				//
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 June Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			 //}

			} else if (FDTO.getMonths() > 0) {// Months
				if (benefitPeriodYear.equalsIgnoreCase("PY") || benefitPeriodYear.equalsIgnoreCase("FY")) {
					planDate= new Date();
					RuleEngineLogger.generateLogs(clazz, " benefitPeriodYear is "+benefitPeriodYear,
							Constants.rule_log_debug, bw);
				}
				RuleEngineLogger.generateLogs(clazz, "MONTHS :" + FDTO.getMonths(), Constants.rule_log_debug, bw);
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule21.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					//RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
					//		Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
						nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(), nextAvailbleDate.get(Calendar.DATE));
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 JUne Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			  }
			

			////
			if (present) {
				List<ServiceCodeIvfTimesFreqFieldDto> ln = mapFlIVFFinal.get(tooth);
				if (ln == null) {
					ln = new ArrayList<>();
					scivfTFDFinal.setHistoryTooth(historyD.getHistoryTooth());
					ln.add(scivfTFDFinal);
					mapFlIVFFinal.put(tooth, ln);
				} else {
					scivfTFDFinal.setHistoryTooth(historyD.getHistoryTooth());
					ln.add(scivfTFDFinal);
				}
			}
		}
		return dList;
	}

	public static List<TPValidationResponseDto> ivfFrequencyLogicForExams(List<ServiceCodeIvfTimesFreqFieldDto> dataIVF,
			String tpCode, String tooth, ToothHistoryDto historyD, Class<?> clazz, BufferedWriter bw, int CurrentYear,
			Date planDate, MessageSource messageSource, Rules rule, IVFTableSheet ivf, Date TP_Date, Locale locale,
			Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> mapFlIVFFinal, Set<String> fcodes, Set<String> surfaces,
			Set<String> teethC, String ruleMessageName) {
		ruleMessageName="rule57";
		boolean present = false;
		String benefitPeriodYear=ivf.getPlanCalendarFiscalYear();
		if (benefitPeriodYear==null) benefitPeriodYear="";
		List<TPValidationResponseDto> dList = new ArrayList<>();
		for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF) {
			String freq = scivfTFD.getFreqency();

			ServiceCodeIvfTimesFreqFieldDto scivfTFDFinal = new ServiceCodeIvfTimesFreqFieldDto(tpCode,
					scivfTFD.getFieldName(), scivfTFD.getFreqency(), 0, 0, "");
			scivfTFDFinal.setTooth(tooth);
			scivfTFDFinal.setSurface(historyD.getSurfaceTooth());
			scivfTFDFinal.setDos(historyD.getHistoryDos());
			RuleEngineLogger.generateLogs(clazz, "HISTORY CODE- " + historyD.getHistoryCode(), Constants.rule_log_debug,
					bw);
			RuleEngineLogger.generateLogs(clazz, "HISTORY DOS- " + historyD.getHistoryDos(), Constants.rule_log_debug,
					bw);

			RuleEngineLogger.generateLogs(clazz, "Frequency- " + freq, Constants.rule_log_debug, bw);
			if (historyD.getHistoryDos().equals("") || freq.equalsIgnoreCase("") || freq.equalsIgnoreCase("NF")
					|| freq.equalsIgnoreCase("no frequency"))
				continue;
			// System.out.println("DDDDDDDDDDDDDD-"+freq);
			FreqencyDto FDTO = FreqencyUtils.parseFrequecy(freq);
			Date[] datesFIS = DateUtils.getFiscalYear(FDTO.getFy());
			int ti = FDTO.getTimes();
			scivfTFDFinal.setTimes(ti);
			RuleEngineLogger.generateLogs(clazz, "Tooth Number-" + tooth, Constants.rule_log_debug, bw);

			Date dos = null;
			try {
				dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(historyD.getHistoryDos());
				RuleEngineLogger.generateLogs(clazz, "History DOS-" + historyD.getHistoryDos(),
						Constants.rule_log_debug, bw);
			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			RuleEngineLogger.generateLogs(clazz, "TIMES:" + ti, Constants.rule_log_debug, bw);
			if (FDTO.getFy() > 0) {// Fiscal Year
				// isFiscalpresent=true;
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + FDTO.getFy(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + datesFIS[0] + "-" + datesFIS[1],
						Constants.rule_log_debug, bw);
				boolean fiscal = DateUtils.isDatesBetweenDates(datesFIS[0], datesFIS[1], dos);
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + fiscal, Constants.rule_log_debug, bw);
				if (fiscal) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
					// expCount = expCount + 1;
				}
			} else if (FDTO.getCy() > 0) {// Calendar Year
				RuleEngineLogger.generateLogs(clazz, "Calendar Year:" + FDTO.getCy(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "CurrentYear:" + CurrentYear, Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "CurrentYear:" + CurrentYear, Constants.rule_log_debug, bw);
				Date[] calcy = DateUtils.getCalendarYear(FDTO.getCy());
				RuleEngineLogger.generateLogs(clazz, "DATE RANGE: FROM -" + calcy[0], Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "DATE RANGE: TO -" + calcy[1], Constants.rule_log_debug, bw);

				// isCalPresent=true;
				// CurrentYear
				if (DateUtils.isDatesBetweenDates(calcy[0], calcy[1], dos)) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}

			} else if (FDTO.getLt() > 0) {// Life Time
				RuleEngineLogger.generateLogs(clazz, "Life Time:" + FDTO.getLt(), Constants.rule_log_debug, bw);
				// isLifeTimePresent=true;
				present = true;
				scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);

			} else if (FDTO.getPy() > 0) {// Plan Year
				RuleEngineLogger.generateLogs(clazz, "Plan Year:" + FDTO.getPy(), Constants.rule_log_debug, bw);
				// isPlanYearPresent=true;
				Calendar calendar = new GregorianCalendar();
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule57.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				Date nextDate = DateUtils.getNextYear(planDate);
				// calendar.set(calendar.get(Calendar.YEAR)+1,calendar.get(Calendar.MONTH),
				// calendar.get(Calendar.DATE)+1);
				calendar.setTime(dos);
				boolean fiscal = DateUtils.isDatesBetweenDates(planDate, nextDate, dos);
				if (fiscal) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			} else if (FDTO.getCalendarMonth() > 0) {// Calendar Months Done (cal.mo)
				RuleEngineLogger.generateLogs(clazz, "Calendar Months:" + FDTO.getCalendarMonth(),
						Constants.rule_log_debug, bw);
				// (1X6cal.mo) Plan Date 1 Jan - 31 JAN --> jan-june and july-Dec. two
				// phase..
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule57.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				for (int x = 0; x <= 11;) {

					x = x + FDTO.getCalendarMonth();
					int initMonth = x - FDTO.getCalendarMonth();
					int endMonth = (x - 1);

					Calendar calendar = new GregorianCalendar();
					calendar.setTime(planDate);
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + initMonth,
							calendar.get(Calendar.DATE));
					initMonth = calendar.get(Calendar.MONTH);

					calendar = new GregorianCalendar();
					calendar.setTime(planDate);
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + endMonth,
							calendar.get(Calendar.DATE));
					endMonth = calendar.get(Calendar.MONTH);

					RuleEngineLogger.generateLogs(clazz, "Initial Calendar Month for Plan Date is:" + (initMonth + 1),
							Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "End Month for Plan Date is:" + (endMonth + 1),
							Constants.rule_log_debug, bw);

					calendar = new GregorianCalendar();
					calendar.setTime(dos);
					int dosmonth = calendar.get(Calendar.MONTH);

					calendar = new GregorianCalendar();
					calendar.setTime(TP_Date);
					int tpmonth = calendar.get(Calendar.MONTH);

					RuleEngineLogger.generateLogs(clazz, "Month for DOS is:" + (dosmonth + 1), Constants.rule_log_debug,
							bw);
					RuleEngineLogger.generateLogs(clazz, "Month for TP is:" + (tpmonth + 1), Constants.rule_log_debug,
							bw);
					RuleEngineLogger.generateLogs(clazz,
							" IS Month for DOS-(" + (dosmonth + 1) + ") is between Initial Calendar Month:("
									+ (initMonth + 1) + ") and End Month for Plan(" + (endMonth + 1) + ")==>"
									+ (initMonth <= dosmonth && dosmonth >= endMonth),
							Constants.rule_log_debug, bw);

					RuleEngineLogger.generateLogs(clazz,
							" IS Month for TP-(" + (tpmonth + 1) + ") is between Initial Calendar Month:("
									+ (initMonth + 1) + ") and End Month for Plan(" + (endMonth + 1) + ")==>"
									+ (initMonth <= tpmonth && tpmonth >= endMonth),
							Constants.rule_log_debug, bw);

					if ((initMonth <= dosmonth && dosmonth >= endMonth)
							&& (initMonth <= tpmonth && tpmonth >= endMonth)) {
						present = true;
						scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
					}
				}

			} else if (FDTO.getDays() > 0) {// Months & Days (1x6Mo_1D)

				RuleEngineLogger.generateLogs(clazz, " Days:" + FDTO.getDays(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Months :" + FDTO.getMonths(), Constants.rule_log_debug, bw);
				//
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule57.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
						nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(), nextAvailbleDate.get(Calendar.DATE));

				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR), nextAvailbleDate.get(Calendar.MONTH),
						nextAvailbleDate.get(Calendar.DATE) + FDTO.getDays());
				//
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 June Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}

			} else if (FDTO.getOnlyDays() > 0) {// Days
				RuleEngineLogger.generateLogs(clazz, "ONLY DAYS :" + FDTO.getOnlyDays(), Constants.rule_log_debug, bw);
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule57.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR), nextAvailbleDate.get(Calendar.MONTH),
						nextAvailbleDate.get(Calendar.DATE) + FDTO.getOnlyDays());
				//
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 June Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}

			} else if (FDTO.getMonths() > 0) {// Months
				RuleEngineLogger.generateLogs(clazz, "MONTHS :" + FDTO.getMonths(), Constants.rule_log_debug, bw);
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule57.error.messagepl",
									new Object[] { ivf.getPlanEffectiveDate() }, locale),
							Constants.FAIL, String.join(",", surfaces), String.join(",", teethC),
							String.join(",", fcodes)));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
						nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(), nextAvailbleDate.get(Calendar.DATE));
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 JUne Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			}

			////
			if (present) {
				List<ServiceCodeIvfTimesFreqFieldDto> ln = mapFlIVFFinal.get(tooth);
				if (ln == null) {
					ln = new ArrayList<>();
					ln.add(scivfTFDFinal);
					mapFlIVFFinal.put(tooth, ln);
				} else {
					ln.add(scivfTFDFinal);
				}
			}
		}
		return dList;
	}

	public static String checkForteehIntext(String siteName, String text, String teeth) {

		text = text.trim().toLowerCase().replaceAll("01", "1").replaceAll("02", "2").replaceAll("03", "3")
				.replaceAll("04", "4").replaceAll("05", "5").replaceAll("06", "6").replaceAll("07", "7")
				.replaceAll("08", "8").replaceAll("09", "9").replaceAll(" ", ",");

		String[] textA = text.split(",");
		String[] teethA = teeth.toLowerCase().split(",");
		List<String> textAList = new ArrayList<String>(Arrays.asList(textA));
		List<String> teethAList = new ArrayList<String>(Arrays.asList(teethA));
		textAList.retainAll(teethAList);

		return (textAList.size() == 0) ? "No" : "Yes";

	}

	// Use for BCBS ONLY
	public static String convertFrequecyString(String siteName, String text) {

		text = text.trim().toLowerCase();
		System.out.println("CCCCC----" + text + "--");
		String convert = text.replace(" consisting of codes:", "");
		convert = convert.replace("per quadrant", "");
		convert = convert.replace("once", "1");
		convert = convert.replace("twice", "2");
		convert = convert.replace("thrice", "3");
		convert = convert.replace("times", "");

		convert = convert.replace("months", "Mo");
		// convert=convert.replace(" years ", "CY");

		convert = convert.replace(" per tooth per ", "x");
		convert = convert.replace(" per benefit period ", "x");
		convert = convert.replace("per benefit period", "x1CY");
		convert = convert.replace("lifetime", "LT");

		convert = convert.replace(" per ", "x");
		convert = convert.replace(" ", "");

		if (text.equals(""))
			convert = Constants.NO_FREQUENCY;

		if (text.equalsIgnoreCase("not covered"))
			convert = Constants.NO_FREQUENCY;
		convert = convert.replace("benefitperiod", "CY");
		convert = convert.replace("1LT", "LT");
		if (convert.equals("nofrequency"))
			convert = Constants.NO_FREQUENCY;
		System.out.println("CCCCC----" + convert);
		return convert;
		// 1 : Once per tooth per 60 months consisting of codes:
		// 2 :Twice per benefit period consisting of codes:
		// 3 :6 times per benefit period consisting of codes
		/*
		 * System.out.println(convertFrequecyString(
		 * "","Once per tooth per 60 months consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per 36 months consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Twice per benefit period consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per tooth per lifetime consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","4 times per benefit period consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per 12 months consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per benefit period consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per tooth per lifetime consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per 60 months consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per tooth per 12 months consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per 36 months consisting of codes:"));
		 * System.out.println(convertFrequecyString(
		 * "","Once per quadrant per 24 months consisting of codes:"));
		 * 
		 * 
		 */
	}

	public static String convertFrequecyDentaString(String siteName, String text, String frequenCYFYPY) {

		text = text.trim().toLowerCase();
		String convert = "";
		String convert1 = "";
		String convert2 = "";
		// Benefit is limited to either
		if (text.startsWith("this procedure is a benefit once")) {
			convert1 = "1" + "x";
		} else if (text.startsWith("benefit is limited to one")) {
			convert1 = "1" + "x";
		} else if (text.contains("benefit is limited to once")) {
			convert1 = "1" + "x";
		} else if (text.contains("benefit is limited to either")) {
			convert1 = "1" + "x";
		} else if (text.contains("benefit is limited to two")) {
			convert1 = "2" + "x";
		} else if (text.contains("benefit is limited to three")) {
			convert1 = "3" + "x";
		} else if (text.contains("benefit is limited to four")) {
			convert1 = "4" + "x";
		} else if (text.contains("benefit is limited to five")) {
			convert1 = "5" + "x";
		} else if (text.contains("benefit is limited to six")) {
			convert1 = "6" + "x";
		} else if (text.contains("benefit is limited to seven")) {
			convert1 = "7" + "x";
		} else if (text.contains("benefit is limited to eight")) {
			convert1 = "8" + "x";
		} else if (text.contains("benefit is limited to nine")) {
			convert1 = "9" + "x";
		} else if (text.contains("benefit is limited to ten")) {
			convert1 = "10" + "x";
		} else if (text.contains("benefit is limited to any once")) {
			convert1 = "1" + "x";
		} else if (text.contains("benefit is limited to any either")) {
			convert1 = "1" + "x";
		} else if (text.contains("benefit is limited to any two")) {
			convert1 = "2" + "x";
		} else if (text.contains("benefit is limited to any three")) {
			convert1 = "3" + "x";
		} else if (text.contains("benefit is limited to any four")) {
			convert1 = "4" + "x";
		} else if (text.contains("benefit is limited to any five")) {
			convert1 = "5" + "x";
		} else if (text.contains("benefit is limited to any six")) {
			convert1 = "6" + "x";
		} else if (text.contains("benefit is limited to any seven")) {
			convert1 = "7" + "x";
		} else if (text.contains("benefit is limited to any eight")) {
			convert1 = "8" + "x";
		} else if (text.contains("benefit is limited to any nine")) {
			convert1 = "9" + "x";
		} else if (text.contains("benefit is limited to any ten")) {
			convert1 = "10" + "x";
		} else if (text.contains("this procedure is not a benefit")) {
			convert1 = "0";
		} else if (text.contains("benefit is limited by other services performed on same date")) {// email : Re:
																									// Scrapping -
																									// Frequency
																									// 8/25/2020
			convert1 = "No Frequency";
		} else if (text.contains("benefit is limited to ")) {

			convert1 = text.split("benefit is limited to ")[1].replaceAll("[a-zA-Z]", "").replace(".", "").trim() + "x";
		} else if (text.contains("benefit is limited to any ")) {

			convert1 = text.split("benefit is limited to ")[1].replaceAll("[a-zA-Z]", "").replace(".", "").trim() + "x";
		}

		if (text.contains("benefit is based on professional determination")) {
			convert = "Pre-D";
		} else if (text.contains("calendar year")) {
			String regex = "(.*?)within(.*?)calendar year(.*?)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches() && matcher.groupCount() >= 3) {
				// System.out.println(matcher.group(2));
				String gp = matcher.group(2);
				gp = gp.trim().replaceAll("[a-z]", "").trim();
				if (gp.equals(""))
					convert = "1" + frequenCYFYPY;
				else
					convert = gp + "" + frequenCYFYPY;
			} else {
				convert = matcher.group(0).trim();
			}

		} else if (text.contains("contract period")) {
			String regex = "(.*?)within(.*?)contract period(.*?)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches() && matcher.groupCount() >= 3) {
				// System.out.println(matcher.group(2));
				String gp = matcher.group(2);
				gp = gp.trim().replaceAll("[a-z]", "").trim();
				if (gp.equals(""))
					convert = "1" + frequenCYFYPY;
				else
					convert = gp + "" + frequenCYFYPY;
			} else {
				convert = matcher.group(0).trim();
			}

		} else if (text.contains("year period")) {
			String regex = "(.*?)within(.*?)year period(.*?)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches() && matcher.groupCount() >= 3) {
				// System.out.println(matcher.group(2));
				String gp = matcher.group(2);
				gp = gp.trim().replaceAll("[a-z]", "").trim();
				if (gp.equals(""))
					convert = "1" + frequenCYFYPY;
				else
					convert = gp + "" + frequenCYFYPY;
			} else {
				convert = matcher.group(0).trim();
			}

		} else if (text.contains("benefit period")) {
			String regex = "(.*?)within(.*?)benefit period(.*?)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches() && matcher.groupCount() >= 3) {
				// System.out.println(matcher.group(2));
				String gp = matcher.group(2);
				gp = gp.trim().replaceAll("[a-z]", "").trim();
				if (gp.equals(""))
					convert = "1" + frequenCYFYPY;
				else
					convert = gp + "" + frequenCYFYPY;
			} else {
				convert = matcher.group(0).trim();
			}
		} else if (text.contains("month period")) {
			String regex = "(.*?)within(.*?)month period(.*?)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches() && matcher.groupCount() >= 3) {
				// System.out.println(matcher.group(2));
				String gp = matcher.group(2);
				// System.out.println("888888--"+gp);
				gp = gp.trim().replaceAll("[a-z]", "");
				convert = gp.trim() + "Mo";
			} else {
				convert = matcher.group(0).trim();
			}

		} else if (text.contains("per lifetime")) {
			convert = "LT";
		} else if (text.contains("no frequency limitation")) {
			convert = "No Frequency";
		} else if (text.contains("this procedure is not a benefit of ")) {
			// convert="No";
		} else if (text.contains("date of service")) {
			if (text.contains("per date of service"))
				convert = "1DOS";
			// if (text.contains("twice date of service"))convert="1DOS";
			// if (text.contains("twice"))convert="1DOS";
			// if (text.contains("per"))convert="1DOS";
			// if (text.contains("per"))convert="1DOS";
		} else if (text.contains("day period")) {
			String regex = "(.*?)within(.*?)day period(.*?)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches() && matcher.groupCount() >= 3) {
				// System.out.println(matcher.group(2));
				String gp = matcher.group(2);
				// System.out.println("888888--"+gp);
				gp = gp.trim().replaceAll("[a-z]", "");
				convert = gp.trim() + "Days";
			} else {
				convert = matcher.group(0).trim();
			}
		}

		if (text.contains("limited to once per provider")) {
			convert2 = ",once per provider ";

		}
		if (text.contains("lifetime per provider")) {
			convert2 = ",once per provider ";

		}
		if (convert.equals("LT")) {
			// convert="1xLT";

		}
		if (text.equals("")) {
			convert = "No Frequency";
			text = "BLANK";
		}

		// System.out.println("CCCCC----"+convert1+convert+convert2);

		String fi = convert1 + convert + convert2 + "----" + text;
		// System.out.println("FI-->"+convert1+"<--->"+convert+"<--->"+convert2+"<--->"+text);
		if (fi.length() > 23)
			fi = fi.substring(0, 23);
		return fi;
		// 1 : Once per tooth per 60 months consisting of codes:
		// 2 :Twice per benefit period consisting of codes:
		// 3 :6 times per benefit period consisting of codes
		/*
		 * System.out.println(convertFrequecyDentaString(
		 * "","Benefit is limited to two of any oral evaluation procedure within a calendar year"
		 * )); System.out.println(convertFrequecyDentaString(
		 * "","Benefit is based on professional determination"));
		 * System.out.println(convertFrequecyDentaString(
		 * "","Benefit is limited to two of any oral evaluation procedure within a calendar year. Comprehensive evaluations are limited to once per provider."
		 * )); System.out.println(convertFrequecyDentaString(
		 * "","Benefit is limited to one crown procedure per tooth within a 24 month period"
		 * )); System.out.println(convertFrequecyDentaString(
		 * "","Benefit is limited to two of any bitewing x-ray procedure within a calendar year"
		 * )); System.out.println(convertFrequecyDentaString(
		 * "","For this program, this procedure has no frequency limitation"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to either one (D0210) intraoral complete series, or (D0330) panoramic radiographic image within a 3 year period"
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to two fluoride procedures within a calendar year"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per tooth within 3 calendar years for teeth without caries"
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to two of any prophylaxis procedures within a calendar year for codes"
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to two of any prophylaxis procedures within a calendar year for codes D1110, D1120, and D4346."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per quadrant within a 24 month period. Radiographic images and a copy of the treatment record are required if more than two quadrants of scaling and root planing are performed on the same date of service."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per quadrant within a 24 month period."));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per lifetime. Following active periodontal therapy, allow completion of a 30 day post-operative period before performing this procedure."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per lifetime. "));
		 * System.out.println(convertFrequecyDentaString("",
		 * "This procedure is not a benefit of most Delta Dental plans. The fee is the patient's responsibility."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per tooth per lifetime"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per tooth within a 3 year period"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per quadrant per lifetime"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per arch within a 5 year period"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "An interim partial denture is covered only to replace extracted anterior permanent teeth during the healing period. If provided for other circumstances, the patient is responsible for the cost. Delta Dental considers the fee for an interim partial denture to include the fee for all teeth and clasps. Benefit is limited to once per arch within a 5 year period."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once within a 5 year period"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per tooth within a 5 year period"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to one implant service per tooth within a 5 year period")
		 * ); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to one crown procedure per tooth within a 5 year period."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per tooth within a 5 year period. An allowance may be made for core buildup when extensive loss of tooth structure is supported by radiographic images or narrative report, or when following root canal treatment."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per surface, per tooth within a 24 month period")
		 * ); System.out.println(convertFrequecyDentaString("",
		 * "This procedure code is not recognized."));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to two problem focused evaluations within a group contract period"
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to either one (D0210) intraoral complete series, or (D0330) panoramic radiographic image within a 5 year period"
		 * )); System.out.println(convertFrequecyDentaString("", ""));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to four of either D4910 or D4346 within a group contract period. Prophylaxis procedures are a benefit following active periodontal therapy once a 30 day post-operative period has completed."
		 * )); System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per quadrant per lifetime"));
		 * System.out.println(convertFrequecyDentaString("",
		 * "Benefit is limited to once per surface, per tooth within a 12 month period")
		 * ); System.out.println(convertFrequecyDentaString("", ""));
		 * System.out.println(convertFrequecyDentaString("",
		 * "1234567891011121314as ddadas sssdsd").split("----")[1]);
		 * 
		 * 
		 */
	}

	public static String convertFrequecyUCCIString(String text) {
		// 3 Per 12 Months | 2 Per 12 Months ~ Per Office
		// For grade pay -- Endodontic Procedures
		try {
			text = text.trim().toLowerCase();
			if (text.contains("in network")) return "No Frequency";
			text=text.replace("per same group", "");
            //("1 Per Tooth ~ Per 5 Years ~ Age 18 And Older | Age 18 And Older | More...");//18 and older 18 and
			if (text.contains("age") && text.contains(" and older")) {
			String regexAge = "(.*?)age(.*?)and older(.*?)";
			Pattern patternAge = Pattern.compile(regexAge);
			Matcher matcherAge = patternAge.matcher(text);
			if (matcherAge.matches() && matcherAge.groupCount() >= 2) {
				//System.out.println("DD="+"age "+matcherAge.group(2).trim()+" and older"); 
				
				text = text.replaceAll("age "+matcherAge.group(2).trim()+" and older", "");
			}
			}
			String convert = "";
			System.out.println("TTT:" + text);
			text = text.replaceAll("~", "");

			if (text.contains("no coverage due to age limitation"))
				text = "0";
			if (text.contains("not covered"))
				text = "0";
			text = text.replace("in combination with routine cleanings", "");
			text = text.replace("bitewings four Films-", "");
			text = text.replace("per office", "");
			text = text.replace("| more...", "");
			text = text.replace("CONUS |", "");
			// 1 Per 36 Months | Age 5 And Older |
			text = text.replace("ortho related", "");
			text = text.replace("per area of the mouth", "");
			text = text.replace("pay grades e1 thru e4 | ", "");
			text = text.replace("all other pay grades | ", "");
			text = text.replace("in combination with cleanings", "");
			text = text.replace("no alternate benefit", "");
			text = text.replace("per inpatient or short procedure unit", "xLT");
			text = text.replace("bitewings four films", "");
			text = text.replace("-", "");
			
			if (text.contains("|")) {
				try {
				String[] convert1=text.split("\\|");
				System.out.println(convert1.length);
				if (convert1.length>1 && convert1[0].trim().equalsIgnoreCase(convert1[1].trim())) {
					text=convert1[0];
				}
				}catch(Exception v) {
					
				}
			}
			
			if (text.contains("per day")) {
				text = text.replace("per day", "");

			}
			if (text.contains("per tooth")) {// 1 Per Tooth ~ Per 3 Years
				text = text.replace(" per tooth ", "Per-Tooth");
			}
			if (text.contains("per") && text.contains("months")) {
				String pDent = "";
				if (text.contains("per dentist")) {
					pDent = "xProvider";
				}
				String temp[] = text.split(" ");
				int count = 0;
				for (int i = 0; i < temp.length; i++) {
					if ("months".equals(temp[i]))
						count++;
				}
				if (count > 1) {
					text = text.replaceAll("\\|", "");
					String regex = "(.*?)per(.*?)months(.*?)per(.*?)months(.*?)";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(text);
					int p1 = 0;
					int m1 = 0;
					int p2 = 0;
					int m2 = 0;

					if (matcher.matches() && matcher.groupCount() >= 5) {
						try {
						p1 = Integer.parseInt(matcher.group(1).trim());
						}catch(Exception num ) {
							
						}
						try {
						m1 = Integer.parseInt(matcher.group(2).trim());
						}catch(Exception num ) {
													
						}
						
						try {
						p2 = Integer.parseInt(matcher.group(3).trim());
                        }catch(Exception num ) {
							
						}
                        try {
						m2 = Integer.parseInt(matcher.group(4).trim());
                        }catch(Exception num ) {
							
						}
						if (p1 > p2) {
							convert = p2 + "x" + m2 + "Mo";
						} else {
							convert = p1 + "x" + m1 + "Mo";
						}
					} else {
						convert = matcher.group(0).trim();
					}
				} else {
					String regex = "(.*?)per(.*?)months(.*?)";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(text);
					if (matcher.matches() && matcher.groupCount() >= 3) {
						convert = matcher.group(1).trim() + "x" + matcher.group(2).trim() + "Mo";
					}

					if (!pDent.equals("")) {
						convert = convert.replace("x", pDent + "x");
					}
				}
			} else if (text.contains("calendar year")) {
				convert = text.replaceAll("[a-zA-Z]", "").trim() + "xCY";

			}else if (text.contains("per consultant")) {
				convert = text.split("per consultant")[0].trim()+"xprovider";
				if (text.contains("xLT")) convert=convert+"xLT"; 

			} else if (text.contains("per") && text.contains("years")) {

				String regex = "(.*?)per(.*?)years(.*?)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(text);
				System.err.println("BR-" + matcher.groupCount());
				if (matcher.matches() && matcher.groupCount() >= 3) {
					String tp = " per" + matcher.group(2) + "years";
					convert = text.replace(tp, "x" + matcher.group(2).trim() + "CY");
					if (convert.contains("Per-Tooth")) {
						convert = convert.replace("Per-Tooth", "xPer-Tooth");
					}

				}
			} else if (text.contains("calendar") && text.contains("year")) {
				convert = text.replaceAll("[a-zA-Z]", "").trim() + "xCY";
				if (text.contains("Per-Tooth")) {
					convert = convert.replace("-", "x").replace("xLT", "per-ToothxLT");
				}
			} else if (text.contains("per lifetime")) {
				convert = text.replaceAll("[a-zA-Z]", "").trim() + "xLT";
				if (text.contains("Per-Tooth")) {
					convert = convert.replace("-", "x").replace("xLT", "per-ToothxLT");
				}
			} else if (text.contains("in network/blank")) {
				convert = "No Frequency";
			} else if (text.contains("in network")) {
				convert = "No Frequency";
			} else if (text.contains("blank")) {
				convert = "No Frequency";
			} else if (text.equals("0")) {
				convert = "0";
			} else if (text.contains("")) {
				convert = "No Frequency";
			}
			
			if (convert.contains("under"))convert=convert.split("under")[0].trim();
			if (convert.contains("lifetime"))convert=convert.replace("lifetime", "LT");
			if (convert.contains("permanent teeth"))convert=convert.replace("permanent teeth", "");
			if (convert.contains("permanent molars"))convert=convert.replace("permanent molars", "");
			if (convert.contains("|")) {
				try {
				String[] convert1=convert.split("\\|");
				if (convert1.length>1 && convert1[0].trim().equalsIgnoreCase(convert1[1].trim())) {
					convert=convert1[0];
				}
				}catch(Exception v) {
					
				}
			}
			if (convert.length() > 40)
				convert = convert.substring(0, 39);	
			convert=convert.replaceAll("\\|", "");
			convert=convert.replaceAll("per-Tooth", "tooth");
			convert=convert.replaceAll("Per-Tooth", "tooth");
			convert=convert.replaceAll("1xtoothxLT","1xLT");
			
			convert=convert.replaceAll("1xtoothx3CY", "1x3CY");
			convert=convert.replaceAll("1toothx", "1xtoothx");
			convert=convert.replaceAll("1xProviderx12Mo","1XProviderX12Mo");
			System.out.println("Con-" + convert);
			return convert;
		} catch (Exception e) {
			e.printStackTrace();
			return "ISSUE";
		}
	}

	public static void main(String[] a) {

		////
		/*
		System.out.println(convertFrequecyDentaString("",
				"Benefit is limited to two fluoride procedures within a calendar year", "CY"));
		System.out.println(convertFrequecyDentaString("",
				"Benefit is limited to any 100 oral evaluation procedures within the contract period. Comprehensive evaluations are limited to once per provider.",
				"CY"));
		System.out.println(convertFrequecyDentaString("",
				"Benefit is limited to one crown procedure per tooth within a 5 year period.", "CY"));
		System.out.println(
				convertFrequecyDentaString("", "Benefit is limited to 13 periapical films per date of service", "CY"));

		System.out.println(convertFrequecyDentaString("", "benefit is based on professional determination", "CY"));
		System.out.println("77777777777777777");
		System.out.println(convertFrequecyDentaString("",
				"Benefit is limited to two of any oral evaluation procedure within a Benefit Period.", "PY"));
		System.out.println(convertFrequecyDentaString("",
				"An interim partial denture is covered only to replace extracted anterior permanent teeth during the healing period. If provided for other circumstances, the patient is responsible for the cost. Delta Dental considers the fee for an interim partial denture to include the fee for all teeth and clasps. Benefit is limited to once per arch within a 5 year period.",
				"PY"));
		*/
		convertFrequecyUCCIString("1 Per Tooth ~ Per 5 Years");
		/*
		convertFrequecyUCCIString("1 Per 12 Months ~ Per Dentist | More...");
		convertFrequecyUCCIString("1 per 5 years ~ per same group");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 5 Years ~ Age 18 And Older | Age 18 And Older | More...");//18 and older 18 and
		convertFrequecyUCCIString("1 Per Tooth ~ Per 5 Years ~ Age 18 And Older | Age 18 And Older | More...");
		convertFrequecyUCCIString("1 per 12 months ~ per dentist | 1 per 12 months ~ per dentist");
		convertFrequecyUCCIString("1 per tooth ~ per lifetime ~ under 15 years of age");
		convertFrequecyUCCIString("1 per tooth ~ per lifetime ~ under 15 years of age");
		convertFrequecyUCCIString("1 per tooth ~ per 5 years | permanent teeth");
		convertFrequecyUCCIString("1xPer-Toothx3CY  under 19 years of age  permanent molars");
		convertFrequecyUCCIString("1xPer-Toothx5CY | permanent teeth");
		convertFrequecyUCCIString("1 per tooth ~ per 3 years ~ under 19 years of age ~ permanent molars");
		convertFrequecyUCCIString("1 per 5 years | 1 per 5 years");
		convertFrequecyUCCIString("1 per tooth ~ per 5 years");
		convertFrequecyUCCIString("1 per tooth ~ per lifetime ~ under 15 years of age");
		convertFrequecyUCCIString("1 per tooth ~ per lifetime ~ under 15 years of age");
		convertFrequecyUCCIString("1xPer-Toothx5CY | permanent teeth");
		convertFrequecyUCCIString("1xPer-Toothx3CY  under 19 years of age  permanent molars");
		convertFrequecyUCCIString("1 per 12 months ~ per dentist | 1 per 12 months ~ per dentist");
		System.out.println("****************************");
		convertFrequecyUCCIString("1 Per Consultant ~ Per Inpatient Or Short Procedure Unit");
		
		convertFrequecyUCCIString("1 Per 12 M" + "onths | 2 Per 122 Months ~ Per Office ");
		convertFrequecyUCCIString("2 Per 122 Months ~ Per Office ");
		convertFrequecyUCCIString("2 Per Calendar Year");
		convertFrequecyUCCIString("In Network/Blank");
		convertFrequecyUCCIString("4 Per Calendar Year ~ In Combination With Routine Cleanings");
		convertFrequecyUCCIString("1 Per 36 Months | Age 5 And Older |");
		convertFrequecyUCCIString("Pay Grades E1 thru E4 | 1 Per Tooth ~ Per Lifetime | More...");
		// Pay Grades E1 thru E4 | 1 Per Tooth ~ Per Lifetime | More...
		// Pay Grades E1 thru E4 | 1 Per Tooth ~ Per Lifetime ~ Anterior Primary Teeth ~
		// Under 6 Years Of Age ~ Posterior Primary Teeth ~ Under 11 Years Of Age |
		// More...
		convertFrequecyUCCIString("1 Per 12 Months ~ Per Dentist");
		convertFrequecyUCCIString("1 Per Lifetime");
		convertFrequecyUCCIString("1 Per Tooth ~ Per Lifetime");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 3 Years");
		convertFrequecyUCCIString("1 Per 5 Years");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 3 Years");
		convertFrequecyUCCIString("1 Per 5 Years");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 5 Years");
		convertFrequecyUCCIString("2 Per Calendar Year");
		convertFrequecyUCCIString("4 Per Calendar Year ~ In Combination With Routine Cleanings");
		System.out.println("-----------------------------");
		convertFrequecyUCCIString("2 Per Calendar Year");
		convertFrequecyUCCIString("1 Per 12 Months");
		convertFrequecyUCCIString("In Network/Blank");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 3 Years");
		convertFrequecyUCCIString("1 Per 24 Months ~ Per Area Of The Mouth");
		convertFrequecyUCCIString("4 Per Calendar Year ~ In Combination With Routine Cleanings");
		convertFrequecyUCCIString("1 Per Lifetime");
		convertFrequecyUCCIString("1 Per Tooth ~ Per Lifetime");
		convertFrequecyUCCIString("1 Per 5 Years");
		convertFrequecyUCCIString("1 Per 12 Months ~ Per Dentist");
		convertFrequecyUCCIString("Not Covered");
		convertFrequecyUCCIString("1 Per 6 Months");
		convertFrequecyUCCIString("1 Per 12 Months ~ Per Dentist");
		convertFrequecyUCCIString("Bitewings Four Films-1 Per 6 Months | 4 Per Day");
		convertFrequecyUCCIString("");
		convertFrequecyUCCIString("1 Per 36 Months");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 3 Years");
		convertFrequecyUCCIString("1 Per 24 Months ~ Per Area Of The Mouth");
		convertFrequecyUCCIString("1 Per 36 Months ~ Age 16 And Older ~ In Combination with Cleanings");
		convertFrequecyUCCIString("4 Per 12 Months ~ In Combination With Routine Cleanings");
		convertFrequecyUCCIString("Not Covered");
		convertFrequecyUCCIString("1 Per Tooth ~ Per Lifetime");
		convertFrequecyUCCIString("1 Per 5 Years");
		convertFrequecyUCCIString("1 Per 12 Months | No Alternate Benefit");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 5 Years");
		convertFrequecyUCCIString("1 Per Consultant ~ Per Inpatient Or Short Procedure Unit");
		convertFrequecyUCCIString("1 Per Tooth ~ Per Lifetime");
		convertFrequecyUCCIString("1 Per Tooth ~ Per 5 Years");
		convertFrequecyUCCIString("1 Per 12 Months ~ Per Dentist");
		convertFrequecyUCCIString("3 Per 12 Months | 2 Per 12 Months ~ Per Office");
       */
	}

	public static List<TPValidationResponseDto> panoFMXFrequencyLogic(List<ServiceCodeIvfTimesFreqFieldDto> dataIVF,
			String tpCode, ToothHistoryDto historyD, Class<?> clazz, BufferedWriter bw, int CurrentYear, Date planDate,
			MessageSource messageSource, Rules rule, IVFTableSheet ivf, Date TP_Date, Locale locale,
			Map<String, List<ServiceCodeIvfTimesFreqFieldDto>> mapFlIVFFinal, String thKEY) {
		boolean present = false;
		List<TPValidationResponseDto> dList = new ArrayList<>();
		String benefitPeriodYear=ivf.getPlanCalendarFiscalYear();
		if (benefitPeriodYear==null) benefitPeriodYear="";
		for (ServiceCodeIvfTimesFreqFieldDto scivfTFD : dataIVF) {
			String freq = scivfTFD.getFreqency();

			ServiceCodeIvfTimesFreqFieldDto scivfTFDFinal = new ServiceCodeIvfTimesFreqFieldDto(tpCode,
					scivfTFD.getFieldName(), scivfTFD.getFreqency(), 0, 0, "");
			scivfTFDFinal.setDos(historyD.getHistoryDos());
			scivfTFDFinal.setTooth(historyD.getHistoryTooth());
			scivfTFDFinal.setServiceCodeHis(historyD.getHistoryCode());
			RuleEngineLogger.generateLogs(clazz, "HISTORY CODE- " + historyD.getHistoryCode(), Constants.rule_log_debug,
					bw);
			RuleEngineLogger.generateLogs(clazz, "HISTORY DOS- " + historyD.getHistoryDos(), Constants.rule_log_debug,
					bw);

			RuleEngineLogger.generateLogs(clazz, "Frequency- " + freq, Constants.rule_log_debug, bw);
			if (historyD.getHistoryDos().equals("") || freq.equalsIgnoreCase("") || freq.equalsIgnoreCase("NF")
					|| freq.equalsIgnoreCase("no frequency"))
				continue;
			// System.out.println("DDDDDDDDDDDDDD-"+freq);
			FreqencyDto FDTO = FreqencyUtils.parseFrequecy(freq);
			Date[] datesFIS = DateUtils.getFiscalYear(FDTO.getFy());
			int ti = FDTO.getTimes();
			scivfTFDFinal.setTimes(ti);

			Date dos = null;
			try {
				dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(historyD.getHistoryDos());
				RuleEngineLogger.generateLogs(clazz, "History DOS-" + historyD.getHistoryDos(),
						Constants.rule_log_debug, bw);
			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			RuleEngineLogger.generateLogs(clazz, "TIMES:" + ti, Constants.rule_log_debug, bw);
			if (FDTO.getFy() > 0) {// Fiscal Year
				// isFiscalpresent=true;
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + FDTO.getFy(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + datesFIS[0] + "-" + datesFIS[1],
						Constants.rule_log_debug, bw);
				boolean fiscal = DateUtils.isDatesBetweenDates(datesFIS[0], datesFIS[1], dos);
				RuleEngineLogger.generateLogs(clazz, "Fiscal Year:" + fiscal, Constants.rule_log_debug, bw);
				if (fiscal) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
					// expCount = expCount + 1;
				}
			} else if (FDTO.getCy() > 0) {// Calendar Year
				RuleEngineLogger.generateLogs(clazz, "Calendar Year:" + FDTO.getCy(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "CurrentYear:" + CurrentYear, Constants.rule_log_debug, bw);
				Date[] calcy = DateUtils.getCalendarYear(FDTO.getCy());
				RuleEngineLogger.generateLogs(clazz, "DATE RANGE: FROM -" + calcy[0], Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "DATE RANGE: TO -" + calcy[1], Constants.rule_log_debug, bw);

				// isCalPresent=true;
				// CurrentYear
				if (DateUtils.isDatesBetweenDates(calcy[0], calcy[1], dos)) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}

			} else if (FDTO.getLt() > 0) {// Life Time
				RuleEngineLogger.generateLogs(clazz, "Life Time:" + FDTO.getLt(), Constants.rule_log_debug, bw);
				// isLifeTimePresent=true;
				present = true;
				scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);

			} else if (FDTO.getPy() > 0) {// Plan Year
				RuleEngineLogger.generateLogs(clazz, "Plan Year:" + FDTO.getPy(), Constants.rule_log_debug, bw);
				// isPlanYearPresent=true;
				Calendar calendar = new GregorianCalendar();
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule54.error.message2", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
					return dList;
				}
				Date nextDate = DateUtils.getNextYear(planDate);
				// calendar.set(calendar.get(Calendar.YEAR)+1,calendar.get(Calendar.MONTH),
				// calendar.get(Calendar.DATE)+1);
				calendar.setTime(dos);
				boolean fiscal = DateUtils.isDatesBetweenDates(planDate, nextDate, dos);
				if (fiscal) {
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			} else if (FDTO.getCalendarMonth() > 0) {// Calendar Months Done (cal.mo)
				RuleEngineLogger.generateLogs(clazz, "Calendar Months:" + FDTO.getCalendarMonth(),
						Constants.rule_log_debug, bw);
				// (1X6cal.mo) Plan Date 1 Jan - 31 JAN --> jan-june and july-Dec. two
				// phase..
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule54.error.message2", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
					return dList;
				}
				for (int x = 0; x <= 11;) {

					x = x + FDTO.getCalendarMonth();
					int initMonth = x - FDTO.getCalendarMonth();
					int endMonth = (x - 1);

					Calendar calendar = new GregorianCalendar();
					calendar.setTime(planDate);
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + initMonth,
							calendar.get(Calendar.DATE));
					initMonth = calendar.get(Calendar.MONTH);

					calendar = new GregorianCalendar();
					calendar.setTime(planDate);
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + endMonth,
							calendar.get(Calendar.DATE));
					endMonth = calendar.get(Calendar.MONTH);

					RuleEngineLogger.generateLogs(clazz, "Initial Calendar Month for Plan Date is:" + (initMonth + 1),
							Constants.rule_log_debug, bw);
					RuleEngineLogger.generateLogs(clazz, "End Month for Plan Date is:" + (endMonth + 1),
							Constants.rule_log_debug, bw);

					calendar = new GregorianCalendar();
					calendar.setTime(dos);
					int dosmonth = calendar.get(Calendar.MONTH);

					calendar = new GregorianCalendar();
					calendar.setTime(TP_Date);
					int tpmonth = calendar.get(Calendar.MONTH);

					RuleEngineLogger.generateLogs(clazz, "Month for DOS is:" + (dosmonth + 1), Constants.rule_log_debug,
							bw);
					RuleEngineLogger.generateLogs(clazz, "Month for TP is:" + (tpmonth + 1), Constants.rule_log_debug,
							bw);
					RuleEngineLogger.generateLogs(clazz,
							" IS Month for DOS-(" + (dosmonth + 1) + ") is between Initial Calendar Month:("
									+ (initMonth + 1) + ") and End Month for Plan(" + (endMonth + 1) + ")==>"
									+ (initMonth <= dosmonth && dosmonth >= endMonth),
							Constants.rule_log_debug, bw);

					RuleEngineLogger.generateLogs(clazz,
							" IS Month for TP-(" + (tpmonth + 1) + ") is between Initial Calendar Month:("
									+ (initMonth + 1) + ") and End Month for Plan(" + (endMonth + 1) + ")==>"
									+ (initMonth <= tpmonth && tpmonth >= endMonth),
							Constants.rule_log_debug, bw);

					if ((initMonth <= dosmonth && dosmonth >= endMonth)
							&& (initMonth <= tpmonth && tpmonth >= endMonth)) {
						present = true;
						scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
					}
				}

			} else if (FDTO.getDays() > 0) {// Months & Days (1x6Mo_1D)

				RuleEngineLogger.generateLogs(clazz, " Days:" + FDTO.getDays(), Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Months :" + FDTO.getMonths(), Constants.rule_log_debug, bw);
				//
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule54.error.message2", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
						nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(), nextAvailbleDate.get(Calendar.DATE));

				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR), nextAvailbleDate.get(Calendar.MONTH),
						nextAvailbleDate.get(Calendar.DATE) + FDTO.getDays());
				//
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 June Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}

			} else if (FDTO.getOnlyDays() > 0) {// Days
				RuleEngineLogger.generateLogs(clazz, "ONLY DAYS :" + FDTO.getOnlyDays(), Constants.rule_log_debug, bw);
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule54.error.message2", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR), nextAvailbleDate.get(Calendar.MONTH),
						nextAvailbleDate.get(Calendar.DATE) + FDTO.getOnlyDays());
				//
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 June Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}

			} else if (FDTO.getMonths() > 0) {// Months
				RuleEngineLogger.generateLogs(clazz, "MONTHS :" + FDTO.getMonths(), Constants.rule_log_debug, bw);
				if (planDate == null) {
					dList.add(new TPValidationResponseDto(rule.getId(), rule.getName(),
							messageSource.getMessage("rule54.error.message2", new Object[] {}, locale), Constants.FAIL,
							"", "", ""));
					return dList;
				}
				if (dos.compareTo(planDate) < 0) {
					RuleEngineLogger.generateLogs(clazz, " HISTORY DATE PRIOR TO PLANDATE  IGNORE IT :",
							Constants.rule_log_debug, bw);

					// continue;
				}
				Calendar nextAvailbleDate = new GregorianCalendar();
				nextAvailbleDate.setTime(dos);
				nextAvailbleDate.set(nextAvailbleDate.get(Calendar.YEAR),
						nextAvailbleDate.get(Calendar.MONTH) + FDTO.getMonths(), nextAvailbleDate.get(Calendar.DATE));
				RuleEngineLogger.generateLogs(clazz, "NEXT DATE OF SERVICE AVIALBLE :" + nextAvailbleDate.getTime(),
						Constants.rule_log_debug, bw);
				RuleEngineLogger.generateLogs(clazz, "Current Date :" + TP_Date, Constants.rule_log_debug, bw);

				// 1 JUne Dec 12 -->6 Months
				// 1 Dec Dec 12
				if (TP_Date.compareTo(nextAvailbleDate.getTime()) <= 0) {
					RuleEngineLogger.generateLogs(clazz, " INCREASE COUNT BY 1-", Constants.rule_log_debug, bw);
					present = true;
					scivfTFDFinal.setCount(scivfTFDFinal.getCount() + 1);
				}
			}

			////
			if (present) {
				List<ServiceCodeIvfTimesFreqFieldDto> ln = mapFlIVFFinal.get(thKEY);
				if (ln == null) {
					ln = new ArrayList<>();
					ln.add(scivfTFDFinal);
					mapFlIVFFinal.put(thKEY, ln);
				} else {
					ln.add(scivfTFDFinal);
				}
			}
		}
		return dList;
	}

}
