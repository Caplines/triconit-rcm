package com.tricon.ruleengine.utils;

import java.io.BufferedWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Collections2;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;

public class ToothUtil {

	static Class<?> clazz = ToothUtil.class;
	
	static String splitter="---";
	
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

	
	public static boolean commonSurfaceLogic(String a, String b) {

		boolean result=false;
		/*
		for (int z=0;b.length()>z;z++) {
			for (int y=0;a.length()>y;y++) {
				if (String.valueOf(a.charAt(y)).equalsIgnoreCase(String.valueOf(b.charAt(z)))) {
					result =true;
					break;
				}
			}
			if (result) break;
		}
		*/
		if (a.equals("") && a.equals(b)) result =false;
		else if (a.length()<b.length()) {
			result =true;
		}
		return result;
	}
	
	public static boolean diffSurfaceLogic(String a, String b) {
		
		boolean result=false;
		for (int z=0;b.length()>z;z++) {
			for (int y=0;a.length()>y;y++) {
				if (String.valueOf(a.charAt(y)).equalsIgnoreCase(String.valueOf(b.charAt(z)))) {
					result =true;
					break;
				}
			}
			if (result) break;
		}
		result=!result;
		if (a.equals("") && a.equals(b)) result =false;
		return result;
		
	}
	//Lower order Filling found in History on SAME Surface
	/**
	 * Map<String,List<String>> historyMap here map contains data from last 12 months and only
	 * and has same surface and same Teeth
	 * Lower order Filling codes.
	 * @param codeTosearch
	 * @param historyMap
	 * @return
	 */
	//NOTE SURFACE DOES NOT MATTER NOW
    public static List<String> lowerHigherOrderFillingFound(TreatmentPlan tp,
    		Map<String,List<ToothHistoryDto>> historyMap,boolean low,Date tpDate,boolean sameSurface1, 
    		int withinMonth,int months,BufferedWriter bw) {
    	//NOTE SURFACE DOES NOT MATTER NOW	
    	List<String> allCodes=null;
		String r=null;
    	for (Map.Entry<String, List<ToothHistoryDto>> entry : historyMap.entrySet()) {
		     String codeH=entry.getKey();
		     List<ToothHistoryDto> eL=entry.getValue();
		     
		     int codeHINT=Integer.parseInt(codeH.substring(1));
		     int codeSINT=Integer.parseInt(tp.getServiceCode().substring(1));
		     //Lower order check..
		     
		     if (codeH.substring(0, 1).equals(tp.getServiceCode().substring(0,1)) &&  low && (codeSINT>codeHINT)) {
		    	 for(ToothHistoryDto d:eL) {
		    		 String[] tooths=	ToothUtil.getToothsFromTooth(tp.getTooth());
		    		 RuleEngineLogger.generateLogs(clazz, "Service Code - "+tp.getServiceCode()+"- "+"Surface TP-" + tp.getSurface()+" -Surface History- "+d.getSurfaceTooth()+" TOOTH -"+tp.getTooth(),
								Constants.rule_log_debug, bw);
		    		 Date dos = null;
		    		 if (d.getSurfaceTooth().equals("")) continue;
						try {
							dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(d.getHistoryDos());
							RuleEngineLogger.generateLogs(clazz,
									"History DOS-" + d.getHistoryDos(),
									Constants.rule_log_debug, bw);
							
							
						
						
					    //if (sameSruface && !DateUtils.checkfor12m(tpDate, dos) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && d.getSurfaceTooth().toLowerCase().equals(tp.getSurface().toLowerCase())) {
					   //   if (sameSruface && !DateUtils.checkforXm(tpDate, dos,12) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && commonSurfaceLogic(d.getSurfaceTooth(), tp.getSurface())) {
						    if (withinMonth==1 && !DateUtils.checkforXm(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth()) ) {
						    	r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						     if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
								
						  }
						    if (withinMonth==2 && DateUtils.checkforXmMore(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth()) ) {
						    	r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						     if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
								
						  }
					   // if (!sameSruface && !DateUtils.checkfor12m(tpDate, dos) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && !d.getSurfaceTooth().toLowerCase().equals(tp.getSurface().toLowerCase())) {
					     //OUT OF SCOPE
					      /*
					      if (!sameSruface && !DateUtils.checkforXm(tpDate, dos,12) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && diffSurfaceLogic(d.getSurfaceTooth(), tp.getSurface())) {
							    	r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						     if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
										
					       }
					      */
					     } catch (ParseException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
		    	 }
		    	 
		     }
		     //in case of Low = false => Surface ,boolean is not considered
		     if (codeH.substring(0, 1).equals(tp.getServiceCode().substring(0,1)) &&  !low && codeSINT<codeHINT)  {
		    	 
		     	 for(ToothHistoryDto d:eL) {
		    		 String[] tooths=	ToothUtil.getToothsFromTooth(tp.getTooth());
		    		 RuleEngineLogger.generateLogs(clazz, "Surface TP-" + tp.getSurface()+" -Surface History- "+d.getSurfaceTooth(),
								Constants.rule_log_debug, bw);
		    		 Date dos = null;
		    		 if (d.getSurfaceTooth().equals("")) continue;
						try {
							dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(d.getHistoryDos());
							RuleEngineLogger.generateLogs(clazz,
									"History DOS-" + d.getHistoryDos(),
									Constants.rule_log_debug, bw);
							
							
						
						
					    if (withinMonth==1 &&  !DateUtils.checkforXm(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth())) {
						    r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						    if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
					       }
					    if (withinMonth==2 &&  DateUtils.checkforXmMore(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth())) {
						    r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						    if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
					       }
					    
						} catch (ParseException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
		    	 }
		    	 
		     }
		}
		return allCodes;
		
	}
    
	//NOTE SURFACE DOES NOT MATTER NOW
    public static List<String> lowerHigherOrderFillingFound51_52(TreatmentPlan tp,
    		Map<String,List<ToothHistoryDto>> historyMap,boolean low,Date tpDate,boolean sameSurface1, 
    		int withinMonth,int months,BufferedWriter bw) {
    	//NOTE SURFACE DOES NOT MATTER NOW	
    	List<String> allCodes=null;
		String r=null;
    	for (Map.Entry<String, List<ToothHistoryDto>> entry : historyMap.entrySet()) {
		     String codeH=entry.getKey();
		     List<ToothHistoryDto> eL=entry.getValue();
		     
		     int codeHINT=Integer.parseInt(codeH.substring(1));
		     int codeSINT=Integer.parseInt(tp.getServiceCode().substring(1));
		     //Lower order check..
		     
		     if (codeH.substring(0, 1).equals(tp.getServiceCode().substring(0,1)) &&  low && (codeSINT>codeHINT)) {
		    	 for(ToothHistoryDto d:eL) {
		    		 String[] tooths=	ToothUtil.getToothsFromTooth(tp.getTooth());
		    		 RuleEngineLogger.generateLogs(clazz, "Service Code - "+tp.getServiceCode()+"- "+"Surface TP-" + tp.getSurface()+" -Surface History- "+d.getSurfaceTooth()+" TOOTH -"+tp.getTooth(),
								Constants.rule_log_debug, bw);
		    		 Date dos = null;
		    		 //if (d.getSurfaceTooth().equals("")) continue;
						try {
							dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(d.getHistoryDos());
							RuleEngineLogger.generateLogs(clazz,
									"History DOS-" + d.getHistoryDos(),
									Constants.rule_log_debug, bw);
							
							
						
						
					    //if (sameSruface && !DateUtils.checkfor12m(tpDate, dos) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && d.getSurfaceTooth().toLowerCase().equals(tp.getSurface().toLowerCase())) {
					   //   if (sameSruface && !DateUtils.checkforXm(tpDate, dos,12) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && commonSurfaceLogic(d.getSurfaceTooth(), tp.getSurface())) {
						    if (withinMonth==1 && !DateUtils.checkforXm(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth()) ) {
						    	r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						     if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
								
						  }
						    if (withinMonth==2 && DateUtils.checkforXmMore(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth()) ) {
						    	r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						     if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
								
						  }
					   // if (!sameSruface && !DateUtils.checkfor12m(tpDate, dos) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && !d.getSurfaceTooth().toLowerCase().equals(tp.getSurface().toLowerCase())) {
					     //OUT OF SCOPE
					      /*
					      if (!sameSruface && !DateUtils.checkforXm(tpDate, dos,12) && Arrays.asList(tooths).contains(d.getHistoryTooth()) && diffSurfaceLogic(d.getSurfaceTooth(), tp.getSurface())) {
							    	r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						     if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
										
					       }
					      */
					     } catch (ParseException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
		    	 }
		    	 
		     }
		     //in case of Low = false => Surface ,boolean is not considered
		     if (codeH.substring(0, 1).equals(tp.getServiceCode().substring(0,1)) &&  !low && codeSINT<codeHINT)  {
		    	 
		     	 for(ToothHistoryDto d:eL) {
		    		 String[] tooths=	ToothUtil.getToothsFromTooth(tp.getTooth());
		    		 RuleEngineLogger.generateLogs(clazz, "Surface TP-" + tp.getSurface()+" -Surface History- "+d.getSurfaceTooth(),
								Constants.rule_log_debug, bw);
		    		 Date dos = null;
		    		 //if (d.getSurfaceTooth().equals("")) continue;
						try {
							dos = Constants.SIMPLE_DATE_FORMAT_IVF.parse(d.getHistoryDos());
							RuleEngineLogger.generateLogs(clazz,
									"History DOS-" + d.getHistoryDos(),
									Constants.rule_log_debug, bw);
							
							
						
						
					    if (withinMonth==1 &&  !DateUtils.checkforXm(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth())) {
						    r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						    if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
					       }
					    if (withinMonth==2 &&  DateUtils.checkforXmMore(tpDate, dos,months) && Arrays.asList(tooths).contains(d.getHistoryTooth())) {
						    r=tp.getServiceCode()+splitter+d.getHistoryCode() +splitter+d.getHistoryTooth()+splitter+d.getSurfaceTooth();
						    if (allCodes==null) allCodes= new ArrayList<>();
						     allCodes.add(r);
					       }
					    
						} catch (ParseException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
		    	 }
		    	 
		     }
		}
		return allCodes;
		
	}

    public static List<String> generateErrorListForRule171(TreatmentPlan tp,List<EagleSoftFeeShedule> esfeess,List<String> res
    		,BufferedWriter bw){
    	List<String> dList=new ArrayList<>();
        DecimalFormat d= new DecimalFormat("#.##");
        d.setRoundingMode(RoundingMode.HALF_DOWN);
    	for(String data:res) {
			String[] datas=data.split(splitter);
		
				Collection<EagleSoftFeeShedule> ruleGen = Collections2.filter(esfeess,
						name -> name.getFeesServiceCode().equals(datas[1]));
				if (ruleGen != null) {
					for (EagleSoftFeeShedule fs : ruleGen) {
						RuleEngineLogger.generateLogs(clazz,
								" FS FEE -" + fs.getFeesFee() + " :: Treatment Plan Fee-" + tp.getFee(),
								Constants.rule_log_debug, bw);
                       double diff=Double.parseDouble(fs.getFeesFee())-Double.parseDouble(tp.getFee());
                       dList.add(datas[0]+splitter+datas[1]+splitter+datas[2]+splitter+datas[3]+splitter+d.format(diff));
						
					}
				} else {
					RuleEngineLogger.generateLogs(clazz,
							" Treatment Plan Service code is mssing in  Fee Schedule-" + tp.getServiceCode(),
							Constants.rule_log_debug, bw);

					// Service Code not found..
					
				}
			}
    	return dList;
		
    }
    
    public static List<String> generateErrorListForRule172(TreatmentPlan tp,List<EagleSoftFeeShedule> esfeess,List<String> res
    		,BufferedWriter bw){
    	List<String> dList=new ArrayList<>();
    	for(String data:res) {
			String[] datas=data.split(splitter);
		
				Collection<EagleSoftFeeShedule> ruleGen = Collections2.filter(esfeess,
						name -> name.getFeesServiceCode().equals(datas[1]));
				if (ruleGen != null) {
					for (EagleSoftFeeShedule fs : ruleGen) {
						RuleEngineLogger.generateLogs(clazz,
								" FS FEE -" + fs.getFeesFee() + " :: Treatment Plan Fee-" + tp.getFee(),
								Constants.rule_log_debug, bw);
                       dList.add(datas[0]+splitter+datas[1]+splitter+datas[2]+splitter+datas[3]);
						
					}
				} else {
					RuleEngineLogger.generateLogs(clazz,
							" Treatment Plan Service code is mssing in  Fee Schedule-" + tp.getServiceCode(),
							Constants.rule_log_debug, bw);

					// Service Code not found..
					
				}
			}
    	return dList;
		
    }

    //For rule 51 and 52
	public static List<String> generateErrorListForRule51_52(TreatmentPlan tp, List<EagleSoftFeeShedule> esfeess,
			List<String> res, BufferedWriter bw) {
		List<String> dList = new ArrayList<>();
		for (String data : res) {
			String[] datas = data.split(splitter);
           if (datas.length==4)
			dList.add(datas[0] + splitter + datas[1] + splitter + datas[2] + splitter + datas[3]);
           else dList.add(datas[0] + splitter + datas[1] + splitter + datas[2] + splitter + "");

		}
		return dList;

	}

    public static void main(String a []) {
    	String aa ="D3420";
    	System.out.println(commonSurfaceLogic("", "b"));
    	System.out.println(diffSurfaceLogic("","b"));
    	
    	
    }

}
