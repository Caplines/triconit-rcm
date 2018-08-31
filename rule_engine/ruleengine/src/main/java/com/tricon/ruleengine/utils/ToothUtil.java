package com.tricon.ruleengine.utils;

import java.util.ArrayList;
import java.util.List;

public class ToothUtil {

	public static void main(String[] args) {
		
		String s[] =getToothsFromTooth("1,A-7,10-15,32,34-34");
		System.out.println(s[0]);
		String s1[] =getToothsFromTooth(null);
		//System.out.println(s1[0]);
		//System.out.println("d");
		findCommonTooth(new String [] {"1"},new String [] {"a","1","ddd"});
		
			// TODO Auto-generated method stub

	}
	
	
	public static List<String> findCommonTooth(String[] a, String[] b) {

		if (a==null   || b==null) return null; 
        List<String> commonElements = new ArrayList<String>();

        for(int i = 0; i < a.length ;i++) {
            for(int j = 0; j< b.length ; j++) {
            	//System.out.println(a[i]+"-"+b[j]);
                    if(a[i].trim().equalsIgnoreCase(b[j].trim())) {  
                    //Check if the list already contains the common element
                        if(!commonElements.contains(a[i])) {
                            //add the common element into the list
                            commonElements.add(a[i]);
                        }
                    }
            }
        }
        //System.out.println(commonElements.size());
       for(String aa:commonElements) {
    	   //System.out.println(aa);
       }
        return commonElements;
    }
	
	public static String[] getToothsFromTooth(String toothNumbers) {
		
		List<String> finalTooths=new ArrayList<>();
		if (toothNumbers==null ) return new String[] {""};
		String commaLevel[]=toothNumbers.split(",");
		for(String cl:commaLevel) {
			String [] dashLevel=cl.split("-");
			if (dashLevel.length==2) {
				
				try {
					int x=Integer.parseInt(dashLevel[0]);
					int y=Integer.parseInt(dashLevel[1]);
					for(int k=x;k<=y;k++) {
						
						finalTooths.add(k+"");
					}
				}catch(Exception e) {
					finalTooths.add(dashLevel[0]);
					finalTooths.add(dashLevel[1]);
				}
				
			}else {
				finalTooths.add(dashLevel[0]);
			}
		}
		
		return finalTooths.toArray(new String[finalTooths.size()]);
		
	}

}
