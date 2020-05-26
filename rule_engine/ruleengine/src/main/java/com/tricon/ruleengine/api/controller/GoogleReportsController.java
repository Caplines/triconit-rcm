package com.tricon.ruleengine.api.controller;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.beans.BeanUtils;
//import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.FlexBean;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.GoogleReportDTO;
import com.tricon.ruleengine.dto.sheetresponse.*;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.service.GoogleReportService;
import com.tricon.ruleengine.service.impl.EagleSoftDBAccessServiceImpl;
import com.tricon.ruleengine.utils.Constants;

@CrossOrigin
@RestController
public class GoogleReportsController {

	public static boolean alreadyRunning=false;
	
	static Class<?> clazz = GoogleReportsController.class;
	
	@Autowired
	GoogleReportService gs;

	@Autowired
	EagleSoftDBAccessService es;

	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/googleReport")
	public ResponseEntity<Object> fethESresponse(@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "ids", required = false) String ids,
			@RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "columnCount", required = true) int columnCount,
			@RequestParam(value = "office", required = true) String office, HttpServletRequest request,
			HttpServletResponse response) {
		//
		es.setUpSSLCertificates();
		//System.out.println(query);
		// query = " select " + + query;
		//System.out.println(office);
		//System.out.println(ids);
		Map<String, List<String>> dataMap = gs.getESDataFromServer(query, ids, columnCount, office,password);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", dataMap));

	}

	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/googleReport2")
	@ResponseBody
	public ResponseEntity<Object> fethESresponse2(
			@RequestParam(value = "selectcolumns", required = true) String selectcolumns,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "ids", required = false) String ids,
			@RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "columnCount", required = true) int columnCount,
			@RequestParam(value = "office", required = true) String office, HttpServletRequest request,
			HttpServletResponse response) throws JSONException {
		//
		es.setUpSSLCertificates();
		List<FlexBean> beanList = new ArrayList<>();
		FlexBean dataBean = null;
		//System.out.println(query);
		query = " select " + selectcolumns + " " + query;
		//System.out.println(office);
		//System.out.println(ids);
		//System.out.println(query);
		Map<String, List<String>> dataMap = gs.getESDataFromServer(query, ids, columnCount, office,password);
		String finalData = "";
		if (dataMap != null) {
			String a[] = selectcolumns.split(",");
			List<String> li = Arrays.asList(a);
			int y = 0;
			String comma = "";
			for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
				if (entry.getValue() != null) {
					List<String> des = (List<String>) (entry.getValue());
					int x = 0;
					if (y > 0)
						comma = ",";
					finalData = finalData + comma + "{";
					String comma2 = "";
					dataBean = new FlexBean();// dataBean
					for (String da : li) {
						String v = des.get(x);
						if (v == null)
							v = "";
						dataBean.set(da.replaceAll(" ", "_"), v);
						finalData = finalData + comma2 + "\"" + da.replaceAll(" ", "_") + "\":\"" + v + "\"";
						x++;
						if (x > 0)
							comma2 = ",";

						// {"phonetype":"N95","cat":"WP"},{}
					}
					beanList.add(dataBean);
					finalData = finalData + "}";
					y++;

				}

			}
		}
		finalData = "{\"data\":[" + finalData + "]}";
		//JSONObject jsonObj = new JSONObject(finalData);
		//System.out.println("FFFFFFFFF=" + finalData);

		//JSONObject jsonObj2 = new JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");

		// https://medium.com/@paulgambill/how-to-import-json-data-into-google-spreadsheets-in-less-than-5-minutes-a3fede1a014a
		// return finalData;
		return ResponseEntity.ok(beanList);

	}

	private List<Object> fetchData(String selectcolumns,String query,String ids,int columnCount,String password,
			              String office, HttpServletRequest request,
			HttpServletResponse response) {
		List<Object> l = new ArrayList<>();	
		try {
			alreadyRunning=true;
			es.setUpSSLCertificates();
			
			/*
		    System.out.println("DPPPPPPPPPP--"+request.getRequestURI());
			System.out.println("DPPPPPPPPPP--"+request.getHeaderNames());
			for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements();) {
			    String nextHeaderName = (String) e.nextElement();
			    String headerValue = request.getHeader(nextHeaderName);
			    System.out.println("DPPPPPPPPPP--"+nextHeaderName);
			    System.out.println("DPPPPPPPPPP--"+headerValue);
			    
				}
			
			System.out.println("----------------------");
			System.out.println("---------URL-------------"+request.getRequestURL());
			`.out.println("RRRRRRRRRRRRR---------"+request.getRemoteHost() );
			
			System.out.println(new URL(request.getRequestURL().toString()).getHost());
			*/
			List<GoogleReportDTO> beanList = new ArrayList<>();
			GoogleReportDTO dataBean = null;
			String a[] = selectcolumns.split(",");
			query = " select " + selectcolumns + " " + query;
			boolean unicode16=false;        //patperio 
			if (query.toLowerCase().contains("patperio ")) unicode16=true;
			LinkedHashMap<String, List<String>> dataMap = gs.getESDataFromServer(query, ids, columnCount, office,password);
			//String finalData = "";
			if (dataMap != null) {
				//List<String> li = Arrays.asList(a);
				//String comma = "";
				for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
					if (entry.getValue() != null) {
						List<String> des = (List<String>) (entry.getValue());
						int x = 0;
						//finalData = finalData + comma + "{";
						dataBean = new GoogleReportDTO();// dataBean
						
						for (int y=0;y<a.length;y++) {
							String v = des.get(x);
							//int ss=0;
							if(unicode16) {//handle uincode characters
								//avoid loop here to 32 
								if(a[y].contains("tooth_1")|| a[y].contains("tooth_2") || a[y].contains("tooth_3")
								  || a[y].contains("tooth_4")  || a[y].contains("tooth_5") || a[y].contains("tooth_6")
								  || a[y].contains("tooth_7")  || a[y].contains("tooth_8") || a[y].contains("tooth_9")
								  || a[y].contains("tooth_10")  || a[y].contains("tooth_11") || a[y].contains("tooth_12")
								  || a[y].contains("tooth_13")  || a[y].contains("tooth_14") || a[y].contains("tooth_15")
								  || a[y].contains("tooth_16")  || a[y].contains("tooth_17") || a[y].contains("tooth_18")
								  || a[y].contains("tooth_19")  || a[y].contains("tooth_20") || a[y].contains("tooth_21")
								  || a[y].contains("tooth_22")  || a[y].contains("tooth_23") || a[y].contains("tooth_24")
								  || a[y].contains("tooth_25")  || a[y].contains("tooth_26") || a[y].contains("tooth_27")
								  || a[y].contains("tooth_28")  || a[y].contains("tooth_29") || a[y].contains("tooth_30")
								  || a[y].contains("tooth_31")  || a[y].contains("tooth_32")
								  
								  ) {
									
										v = des.get(x);
										v=v.replaceAll("\n", "");
										//System.out.println(v);
											try {
										byte[] b=v.getBytes("UTF-8");
										v="";	
										for(int n=0;n<b.length;n++) {
											if ((b[n]+"").equals("32")) continue;
											v=v+b[n]+",";
										}
										v=v.replaceAll(",$", "");
										 }catch(Exception u) {
											 
										 }
									
									}	
							}
							
							
							
							setUPResponseData(dataBean, x, v);
							x++;
						}
						beanList.add(dataBean);

					}

				}
			}
	          /*
	          * http://localhost:8080/googleESReport?query=
	            FROM dbo.syscolumns b,sysobjects a where a.id=b.id and a.name LIKE 'treatment_plans'
	            &selectcolumns=b.name&columnCount=1&office=Jasper&password=134568 
	          */
			    //Add name and age
			
			
			//Class<?> c2 = Class.forName("com.tricon.ruleengine.dto.sheetresponse.Response"+columnCount);//--Done
			
			if (beanList!=null) {
			
			for (GoogleReportDTO d:beanList) {
			    l.add(setUPResponseDataRequired(d,columnCount,null));
			}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
			alreadyRunning=false;
		}
		return l;
	}
	
	private boolean checktimer(int max) throws InterruptedException {
		
		RuleEngineLogger.generateLogs(clazz, "checktimer "+alreadyRunning+"--"+max, Constants.rule_log_debug, null);
		if (alreadyRunning) {
			RuleEngineLogger.generateLogs(clazz, "checktimer "+alreadyRunning, Constants.rule_log_debug, null);
			max++;
			if (max>7) return false;
			Thread.sleep(2000);
			checktimer(max);
		}
		RuleEngineLogger.generateLogs(clazz, "checktimer "+alreadyRunning+"--"+max, Constants.rule_log_debug, null);
		
		return false;
	}
	
	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/googleESReport")
	@ResponseBody
	public ResponseEntity<Object> fethESGoogleresponse(
			@RequestParam(value = "selectcolumns", required = true) String selectcolumns,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "ids", required = false) String ids,
			@RequestParam(value = "columnCount", required = true) int columnCount,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "office", required = true) String office, HttpServletRequest request,
			HttpServletResponse response) throws JSONException, MalformedURLException, ClassNotFoundException, InterruptedException {
		//
		
		List<Object> l = null;	
		if (alreadyRunning) {
			
			checktimer(1);
		}
		
		l= fetchData(selectcolumns, query, ids, columnCount, password, office, request, response);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", l));

	}

	private GoogleReportDTO setUPResponseData(GoogleReportDTO dataBean, int x, String data) {
		
		if (data==null)
            data="-NO-DATA-";
		data=data.replaceAll("\\\\u000", "-");
		
		if (dataBean != null) {
			if (x == 0)
				dataBean.setC1(data);
			else if (x == 1)
				dataBean.setC2(data);
			else if (x == 2)
				dataBean.setC3(data);
			else if (x == 3)
				dataBean.setC4(data);
			else if (x == 4)
				dataBean.setC5(data);
			else if (x == 5)
				dataBean.setC6(data);
			else if (x == 6)
				dataBean.setC7(data);
			else if (x == 7)
				dataBean.setC8(data);
			else if (x == 8)
				dataBean.setC9(data);
			else if (x == 9)
				dataBean.setC10(data);
			else if (x == 10)
				dataBean.setC11(data);
			else if (x == 11)
				dataBean.setC12(data);
			else if (x == 12)
				dataBean.setC13(data);
			else if (x == 13)
				dataBean.setC14(data);
			else if (x == 14)
				dataBean.setC15(data);
			else if (x == 15)
				dataBean.setC16(data);
			else if (x == 16)
				dataBean.setC17(data);
			else if (x == 17)
				dataBean.setC18(data);
			else if (x == 18)
				dataBean.setC19(data);
			else if (x == 19)
				dataBean.setC20(data);
			else if (x == 20)
				dataBean.setC21(data);
			else if (x == 21)
				dataBean.setC22(data);
			else if (x == 22)
				dataBean.setC23(data);
			else if (x == 23)
				dataBean.setC24(data);
			else if (x == 24)
				dataBean.setC25(data);
			else if (x == 25)
				dataBean.setC26(data);
			else if (x == 26)
				dataBean.setC27(data);
			else if (x == 27)
				dataBean.setC28(data);
			else if (x == 28)
				dataBean.setC29(data);
			else if (x == 29)
				dataBean.setC30(data);
			else if (x == 30)
				dataBean.setC31(data);
			else if (x == 31)
				dataBean.setC32(data);
			else if (x == 32)
				dataBean.setC33(data);
			else if (x == 33)
				dataBean.setC34(data);
			else if (x == 34)
				dataBean.setC35(data);
			else if (x == 35)
				dataBean.setC36(data);
			else if (x == 36)
				dataBean.setC37(data);
			else if (x == 37)
				dataBean.setC38(data);
			else if (x == 38)
				dataBean.setC39(data);
			else if (x == 39)
				dataBean.setC40(data);
			else if (x == 40)
				dataBean.setC41(data);
			else if (x == 41)
				dataBean.setC42(data);
			else if (x == 42)
				dataBean.setC43(data);
			else if (x == 43)
				dataBean.setC44(data);
			else if (x == 44)
				dataBean.setC45(data);
			else if (x == 45)
				dataBean.setC46(data);
			else if (x == 46)
				dataBean.setC47(data);
			else if (x == 47)
				dataBean.setC48(data);
			else if (x == 48)
				dataBean.setC49(data);
			else if (x == 49)
				dataBean.setC50(data);
			else if (x == 50)
				dataBean.setC51(data);
			else if (x == 51)
				dataBean.setC52(data);
			else if (x == 52)
				dataBean.setC53(data);
			else if (x == 53)
				dataBean.setC54(data);
			else if (x == 54)
				dataBean.setC55(data);
			else if (x == 55)
				dataBean.setC56(data);
			else if (x == 56)
				dataBean.setC57(data);
			else if (x == 57)
				dataBean.setC58(data);
			else if (x == 58)
				dataBean.setC59(data);
			else if (x == 59)
				dataBean.setC60(data);
			else if (x == 60)
				dataBean.setC61(data);
			else if (x == 61)
				dataBean.setC62(data);
			else if (x == 62)
				dataBean.setC63(data);
			else if (x == 63)
				dataBean.setC64(data);
			else if (x == 64)
				dataBean.setC65(data);
			else if (x == 65)
				dataBean.setC66(data);
			else if (x == 66)
				dataBean.setC67(data);
			else if (x == 67)
				dataBean.setC68(data);
			else if (x == 68)
				dataBean.setC69(data);
			else if (x == 69)
				dataBean.setC70(data);
			else if (x == 70)
				dataBean.setC71(data);
			else if (x == 71)
				dataBean.setC72(data);
			else if (x == 72)
				dataBean.setC73(data);
			else if (x == 73)
				dataBean.setC74(data);
			else if (x == 74)
				dataBean.setC75(data);
			else if (x == 75)
				dataBean.setC76(data);
			else if (x == 76)
				dataBean.setC77(data);
			else if (x == 77)
				dataBean.setC78(data);
			else if (x == 78)
				dataBean.setC79(data);
			else if (x == 79)
				dataBean.setC80(data);
			else if (x == 80)
				dataBean.setC81(data);
			else if (x == 81)
				dataBean.setC82(data);
			else if (x == 82)
				dataBean.setC83(data);
			else if (x == 83)
				dataBean.setC84(data);
			else if (x == 84)
				dataBean.setC85(data);
			else if (x == 85)
				dataBean.setC86(data);
			else if (x == 86)
				dataBean.setC87(data);
			else if (x == 87)
				dataBean.setC88(data);
			else if (x == 88)
				dataBean.setC89(data);
			else if (x == 89)
				dataBean.setC90(data);
			else if (x == 90)
				dataBean.setC91(data);
			else if (x == 91)
				dataBean.setC92(data);
			else if (x == 92)
				dataBean.setC93(data);
			else if (x == 93)
				dataBean.setC94(data);
			else if (x == 94)
				dataBean.setC95(data);
			else if (x ==95)
				dataBean.setC96(data);
			else if (x == 96)
				dataBean.setC97(data);
			else if (x == 97)
				dataBean.setC98(data);
			else if (x == 98)
				dataBean.setC99(data);
			else if (x == 99)
				dataBean.setC100(data);

		}
		return dataBean;
	}

	
	private Object setUPResponseDataRequired(GoogleReportDTO dataBean, int x, Object  obj) {
		
		Object newObject = null;
		if (x==1)newObject = new Response1(); 
		else if (x==2)newObject = new Response2(); 
		else if (x==3)newObject = new Response3(); 
		else if (x==4)newObject = new Response4(); 
		else if (x==5)newObject = new Response5(); 
		else if (x==6)newObject = new Response6(); 
		else if (x==7)newObject = new Response7(); 
		else if (x==8)newObject = new Response8(); 
		else if (x==9)newObject = new Response9(); 
		else if (x==10)newObject = new Response10(); 
		else if (x==11)newObject = new Response11(); 
		else if (x==12)newObject = new Response12(); 
		else if (x==13)newObject = new Response13(); 
		else if (x==14)newObject = new Response14(); 
		else if (x==15)newObject = new Response15(); 
		else if (x==16)newObject = new Response16(); 
		else if (x==17)newObject = new Response17(); 
		else if (x==18)newObject = new Response18(); 
		else if (x==19)newObject = new Response19(); 
		else if (x==20)newObject = new Response20(); 
		else if (x==21)newObject = new Response21(); 
		else if (x==22)newObject = new Response22(); 
		else if (x==23)newObject = new Response23(); 
		else if (x==24)newObject = new Response24(); 
		else if (x==25)newObject = new Response25(); 
		else if (x==26)newObject = new Response26(); 
		else if (x==27)newObject = new Response27(); 
		else if (x==28)newObject = new Response28(); 
		else if (x==29)newObject = new Response29();
		else if (x==30)newObject = new Response30();
		else if (x==31)newObject = new Response31();
		else if (x==32)newObject = new Response32();
		else if (x==33)newObject = new Response33();
		else if (x==34)newObject = new Response34();
		else if (x==35)newObject = new Response35();
		else if (x==36)newObject = new Response36();
		else if (x==37)newObject = new Response37();
		else if (x==38)newObject = new Response38();
		else if (x==39)newObject = new Response39();
		else if (x==40)newObject = new Response40();
		else if (x==41)newObject = new Response41();
		else if (x==42)newObject = new Response42();
		else if (x==43)newObject = new Response43();
		else if (x==44)newObject = new Response44();
		else if (x==45)newObject = new Response45();
		else if (x==46)newObject = new Response46();
		else if (x==47)newObject = new Response47();
		else if (x==48)newObject = new Response48();
		else if (x==49)newObject = new Response49();
		else if (x==50)newObject = new Response50();
		else if (x==51)newObject = new Response51();
		else if (x==52)newObject = new Response52();
		else if (x==53)newObject = new Response53();
		else if (x==54)newObject = new Response54();
		else if (x==55)newObject = new Response55();
		else if (x==56)newObject = new Response56();
		else if (x==57)newObject = new Response57();
		else if (x==58)newObject = new Response58();
		else if (x==59)newObject = new Response59();
		else if (x==60)newObject = new Response60();
		else if (x==61)newObject = new Response61();
		else if (x==62)newObject = new Response62();
		else if (x==63)newObject = new Response63();
		else if (x==64)newObject = new Response64();
		else if (x==65)newObject = new Response65();
		else if (x==66)newObject = new Response66();
		else if (x==67)newObject = new Response67();
		else if (x==68)newObject = new Response68();
		else if (x==69)newObject = new Response69();
		else if (x==70)newObject = new Response70();
		else if (x==71)newObject = new Response71();
		else if (x==72)newObject = new Response72();
		else if (x==73)newObject = new Response73();
		else if (x==74)newObject = new Response74();
		else if (x==75)newObject = new Response75();
		else if (x==76)newObject = new Response76();
		else if (x==77)newObject = new Response77();
		else if (x==78)newObject = new Response78();
		else if (x==79)newObject = new Response79();
		else if (x==80)newObject = new Response80();
		else if (x==81)newObject = new Response81();
		else if (x==82)newObject = new Response82();
		else if (x==83)newObject = new Response83();
		else if (x==84)newObject = new Response84();
		else if (x==85)newObject = new Response85();
		else if (x==86)newObject = new Response86();
		else if (x==87)newObject = new Response87();
		else if (x==88)newObject = new Response88();
		else if (x==89)newObject = new Response89();
		else if (x==90)newObject = new Response90();
		else if (x==91)newObject = new Response91();
		else if (x==92)newObject = new Response92();
		else if (x==93)newObject = new Response93();
		else if (x==94)newObject = new Response94();
		else if (x==95)newObject = new Response95();
		else if (x==96)newObject = new Response96();
		else if (x==97)newObject = new Response97();
		else if (x==98)newObject = new Response98();
		else if (x==99)newObject = new Response99();
		else if (x==100)newObject = new Response100();
		BeanUtils.copyProperties(dataBean,newObject);
		return newObject;
	}
}
