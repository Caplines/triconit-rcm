package com.tricon.ruleengine.api.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
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
import com.tricon.ruleengine.service.EagleSoftDBAccessService;
import com.tricon.ruleengine.service.GoogleReportService;

@CrossOrigin
@RestController
public class GoogleReportsController {

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
		System.out.println(query);
		// query = " select " + + query;
		System.out.println(office);
		System.out.println(ids);
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
		System.out.println(query);
		query = " select " + selectcolumns + " " + query;
		System.out.println(office);
		System.out.println(ids);
		System.out.println(query);
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
		JSONObject jsonObj = new JSONObject(finalData);
		System.out.println("FFFFFFFFF=" + finalData);

		JSONObject jsonObj2 = new JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");

		// https://medium.com/@paulgambill/how-to-import-json-data-into-google-spreadsheets-in-less-than-5-minutes-a3fede1a014a
		// return finalData;
		return ResponseEntity.ok(beanList);

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
			HttpServletResponse response) throws JSONException, MalformedURLException {
		//
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
		System.out.println("RRRRRRRRRRRRR---------"+request.getRemoteHost() );
		
		System.out.println(new URL(request.getRequestURL().toString()).getHost());
		*/
		List<GoogleReportDTO> beanList = new ArrayList<>();
		GoogleReportDTO dataBean = null;
		//String a[] = selectcolumns.split(",");
		query = " select " + selectcolumns + " " + query;
		Map<String, List<String>> dataMap = gs.getESDataFromServer(query, ids, columnCount, office,password);
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
					for (int y=0;y<columnCount;y++) {
						String v = des.get(x);
						setUPResponseData(dataBean, x, v);
						x++;
					}
					beanList.add(dataBean);

				}

			}
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", beanList));

	}

	private GoogleReportDTO setUPResponseData(GoogleReportDTO dataBean, int x, String data) {

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

		}
		return dataBean;
	}

}
