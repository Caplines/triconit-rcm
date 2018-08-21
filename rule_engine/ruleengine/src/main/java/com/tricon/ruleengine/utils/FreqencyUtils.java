package com.tricon.ruleengine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.data.dublincore.Date;
import com.tricon.ruleengine.dto.FreqencyDto;
import com.tricon.ruleengine.dto.ServiceCodeIvfTimesFreqFieldDto;

public class FreqencyUtils {

	public static FreqencyDto parseFrequecy(String frequecny) {
		 
		FreqencyDto dto= new FreqencyDto();
        String duration="";
        try {
        String temp="";
        frequecny=frequecny.trim().toLowerCase();
		String timeAndDuration[]=frequecny.split("x");
		if (timeAndDuration.length>0) {
			dto.setTimes(Integer.parseInt(timeAndDuration[0]));
		}
		if (timeAndDuration.length>1) {
			duration =timeAndDuration[1];
			String secondPart[]=duration.split("_");
			if (secondPart.length>0) {
				temp =secondPart[0].trim();
				//
				if (temp.indexOf("cal.mo")>-1) {
					dto.setCalendarMonth(Integer.parseInt(temp.replace("cal.mo", "")));
					
				}
				else if (temp.indexOf("mo")>-1) {
				dto.setMonths(Integer.parseInt(temp.replace("mo", "")));
				}
				else if (temp.indexOf("cy")>-1) {
					dto.setCy(Integer.parseInt(temp.replace("cy", "")));
					
				}
				else if (temp.indexOf("cy")>-1) {
					dto.setFy(Integer.parseInt(temp.replace("fy", "")));
					
				}
				else if (temp.indexOf("cy")>-1) {
                	dto.setFy(Integer.parseInt(temp.replace("py", "")));
					
				}
				else if (temp.indexOf("lt")>-1) {
                	dto.setLt(Integer.parseInt(temp.replace("lt", "")));
				}
				else if (temp.indexOf("days")>-1) {
                	dto.setOnlyDays(Integer.parseInt(temp.replace("days", "")));
				}
			}if (secondPart.length>1) {
				dto.setDays(Integer.parseInt(secondPart[1].replaceAll("d", "")));
			}
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return dto;

	}
	
	public static Map<String,List<ServiceCodeIvfTimesFreqFieldDto>> getCountTimeServiceCode1(List<ServiceCodeIvfTimesFreqFieldDto> li,String tooth){

		Map<String,List<ServiceCodeIvfTimesFreqFieldDto>> fm=new HashMap<>();
		for(ServiceCodeIvfTimesFreqFieldDto s:li) {
			List<ServiceCodeIvfTimesFreqFieldDto> l2=fm.get(s.getServiceCode());
			if (l2==null) l2= new ArrayList<>();
			l2.add(s);
			
		}
		for (Map.Entry<String, List<ServiceCodeIvfTimesFreqFieldDto>> entry : fm.entrySet()) { 
			List<ServiceCodeIvfTimesFreqFieldDto> s=   entry.getValue();
			getCountTimeServiceCode(s,entry.getKey());
		}
		return fm;
	}
	
	
	public static Object[] getCountTimeServiceCode(List<ServiceCodeIvfTimesFreqFieldDto> li,String serviceCode){
		int count=0;
		int times=0;
		int  ctr=0;
		String tooth="";
		String code="";
		
		List<String> dos=new ArrayList<>();
		String fr="";
		String fl="";
		
		for(ServiceCodeIvfTimesFreqFieldDto s:li) {
			if (s.getServiceCode().equals(serviceCode)) {
				count=count+s.getCount();
				dos.add(s.getDos());
				fl=s.getFieldName();
				code=s.getServiceCode();
				tooth=s.getTooth();
				fr=s.getFreqency();
				if (ctr==0) times=s.getTimes();
				else if (times>s.getTimes()) times=s.getTimes();///Set to minimum value
			}
			ctr++;
		}
		return new Object[] {code,tooth,String.join(",", dos),fl};
	}
	
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1,String s1,String tooth){
		int ct=0;
		int ti=0;
		Object[] mess=null;
		Object c1[]=FreqencyUtils.getCountTimeServiceCode(l1, s1);
		ct=ct+(Integer)c1[0];
		ti=ti+(Integer)c1[1];
		int actualmax=ti;
		if (ct>0 && ct>actualmax) {
			String dos=(String)c1[3];
			String fl=(String)c1[2];
			mess= new Object[]{tooth,dos,fl};
			
		}
		return mess;
	}
	
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1,
			List<ServiceCodeIvfTimesFreqFieldDto> l2,String s1,String s2,String tooth){
		int ct=0;
		int ti=0;
		Object[] mess=null;
		Object c1[]=FreqencyUtils.getCountTimeServiceCode(l1, s1);
		ct=ct+(Integer)c1[0];
		ti=ti+(Integer)c1[1];
		int actualmax=ti;
		Object[] c2=FreqencyUtils.getCountTimeServiceCode(l2, s2);
		ct=ct+(Integer)c2[0];
		ti=ti+(Integer)c2[1];
		if (actualmax>ti) actualmax=ti;///Set to minimum value
		if (ct>0 && ct>actualmax) {
			String dos=(String)c1[3]+"-"+(String)c2[3];
			String fl=(String)c1[2]+"-"+(String)c2[2];
			mess= new Object[]{tooth,dos,fl};
			
		}
		return mess;
	}
	
	public static Object[] getError(List<ServiceCodeIvfTimesFreqFieldDto> l1,
			List<ServiceCodeIvfTimesFreqFieldDto> l2,List<ServiceCodeIvfTimesFreqFieldDto> l3 ,String s1,String s2,String s3,String tooth){
		int ct=0;
		int ti=0;
		Object[] mess=null;
		Object c1[]=FreqencyUtils.getCountTimeServiceCode(l1, s1);
		ct=ct+(Integer)c1[0];
		ti=(Integer)c1[1];
		int actualmax=ti;
		Object[] c2=FreqencyUtils.getCountTimeServiceCode(l2, s2);
		ct=ct+(Integer)c2[0];
		ti=(Integer)c2[1];
		if (actualmax>ti) actualmax=ti;///Set to minimum value

		Object[] c3=FreqencyUtils.getCountTimeServiceCode(l3, s3);
		ct=ct+(Integer)c3[0];
		ti=(Integer)c3[1];
		if (actualmax>ti) actualmax=ti;///Set to minimum value

		if (ct>0 && ct>actualmax) {
			String dos=(String)c1[3]+"-"+(String)c2[3]+"-"+(String)c3[3];
			String fl=(String)c1[2]+"-"+(String)c2[2]+"-"+(String)c3[2];
			mess= new Object[]{tooth,dos,fl};
			
		}
		return mess;
	}
	public static void main(String a[]) {
		
		FreqencyDto d=parseFrequecy("1x5CY");
		System.out.println(d.getCy());
		Date treatmentDate =new Date();
		
		
	}
}
