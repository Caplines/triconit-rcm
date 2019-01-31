package com.tricon.ruleengine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToothUtil {

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

	public static String[] getQuadToothsFromTooth(String toothNumbers) {
		
		List<String> finalTooths=new ArrayList<>();
		if (toothNumbers==null ) return new String[] {""};
		String commaLevel[]=toothNumbers.split(",");
		for(String cl:commaLevel) {
			String [] dashLevel=cl.split("-");
			if (dashLevel.length==2) {
					
			}else {
				if (dashLevel[0].equals("UL") || dashLevel[0].equals("LL") ||
						dashLevel[0].equals("LR") || dashLevel[0].equals("UR")	) {
				finalTooths.add(dashLevel[0]);
				}
			}
		}
		
		return finalTooths.toArray(new String[finalTooths.size()]);
		
	}

	
	//Lower order Filling found in History on SAME Surface
	/**
	 * Map<String,List<String>> historyMap here map contains data from last 12 monthns and only
	 * and has same surface and same Teeth
	 * Lower order Filling codes.
	 * @param codeTosearch
	 * @param historyMap
	 * @return
	 */
    public static String lowerHigerOrderFillingFound(String codeTosearch,String surface,String[] tooth,
    		Map<String,List<String>> historyMap,boolean low
    		) {
		
		String r=null;
    	for (Map.Entry<String, List<String>> entry : historyMap.entrySet()) {
		     String codeH=entry.getKey();
		     String[] tooths=(String[])entry.getValue().toArray();
		     
		     int codeHINT=Integer.parseInt(codeH.substring(1));
		     int codeSINT=Integer.parseInt(codeTosearch.substring(1));
		     //Lower order check..
		     if (low && (codeSINT>codeHINT)) {
		    	findCommonTooth(tooth, tooths);
		    	 r=codeHINT+"###"+String.join(",", findCommonTooth(tooth, tooths))+"###"+surface;
		    	 break;
		     }else if(low && (codeSINT<codeHINT)) {
		    	 
		     }
		}
		return r;
		
	}

}
