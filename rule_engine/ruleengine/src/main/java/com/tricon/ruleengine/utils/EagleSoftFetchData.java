package com.tricon.ruleengine.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.Security;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import org.json.JSONObject;

import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;


public class EagleSoftFetchData {
	
	static Class<?> clazz = EagleSoftFetchData.class;
	
	public String getDataUsingSockets(EagleSoftDBDetails esDB,EagleSoftQueryObject q,
			 String p1,String p2,String p3,BufferedWriter bw) {
		
	    Socket socket = null;
	    String data="";
		try {
			/* not done from here use this from service Layer ..dbAccesService.setUpSSLCertificates();
			 Properties systemProps = System.getProperties();
			    systemProps.put("javax.net.ssl.trustStore", p1);
			    systemProps.put("javax.net.ssl.keyStore", p2);
			    systemProps.put("javax.net.ssl.keyStorePassword", p3);
			    System.setProperties(systemProps);
			    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			*/
			socket =getConnectionToES(esDB);
			OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(),"UTF-8");
				BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		    JSONObject jsonObject=new JSONObject();
				jsonObject.put("ids",q.getIds());
			    jsonObject.put("query",q.getQuery() );
			    RuleEngineLogger.generateLogs(clazz,
			    		q.getQuery(),Constants.rule_log_debug, null);
			    jsonObject.put("columncount",q.getColumnCount());
			    jsonObject.put("prepstcount",q.getPrepStCount());
		    writer.write(jsonObject.toString()+"\n");
		    writer.flush();
		    data= reader.readLine();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			RuleEngineLogger.generateLogs(clazz,
					e.getMessage(),Constants.rule_log_debug, bw);
		}
		
		return data;	

	}
	
	public Socket getConnectionToES(EagleSoftDBDetails esDB) {
		
		try {
			return ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(esDB.getIpAddress(), esDB.geteSport());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
			
	}

	public void closeConnectionToES(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}

}
