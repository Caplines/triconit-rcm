package com.tricon.ruleengine.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.Security;
import java.util.Properties;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONObject;

import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;


public class EagleSoftFetchData {
	
	static Class<?> clazz = EagleSoftFetchData.class;
	
	private static  SocketFactory socketFactory=  SSLSocketFactory.getDefault();
	
	
	public String getDataUsingSockets(EagleSoftDBDetails esDB,EagleSoftQueryObject q,
			 String p1,String p2,String p3,BufferedWriter bw) {
		
	    Socket socket = null;
	    String data="";
	    OutputStreamWriter writer =null;
	    BufferedReader reader =null;
	    InputStreamReader in=null;
		try {
			/* not done from here use this from service Layer ..dbAccesService.setUpSSLCertificates();
			 Properties systemProps = System.getProperties();
			    systemProps.put("javax.net.ssl.trustStore", p1);
			    systemProps.put("javax.net.ssl.keyStore", p2);
			    systemProps.put("javax.net.ssl.keyStorePassword", p3);
			    System.setProperties(systemProps);
			    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			*/
			socket =getConnectionToES(esDB,bw);
			
			if (socket!=null) {
			 writer = new OutputStreamWriter(socket.getOutputStream(),"UTF-8");
			in= new InputStreamReader(socket.getInputStream(), "UTF-8");
		    reader=new BufferedReader(in);
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
		    
			}else {
				RuleEngineLogger.generateLogs(clazz,
						"No route to host .."+esDB.getIpAddress(),Constants.rule_log_debug, bw);
				data=null;	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			RuleEngineLogger.generateLogs(clazz,
					e.getMessage(),Constants.rule_log_debug, bw);
		}finally {
			
				try{
					if (in!=null) in.close();
					if (reader!=null) reader.close();
					if (writer!=null) writer.close();
				    
				}catch (Exception e2) {
					e2.printStackTrace();
					// TODO: handle exception
				}
			closeConnectionToES(socket);
		}
		
		return data;	

	}
	
	public Socket getConnectionToES(EagleSoftDBDetails esDB,BufferedWriter bw) {
		Socket socket=null;
		try {
			
			socket =socketFactory.createSocket();
			socket.connect(new InetSocketAddress(esDB.getIpAddress(), esDB.geteSport()), 5000);
			
			//return ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(esDB.getIpAddress(), esDB.geteSport());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (bw!=null)RuleEngineLogger.generateLogs(clazz,
					"No route to host .."+esDB.getIpAddress(),Constants.rule_log_debug, bw);
			
			return null;
		}
		return socket;
			
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
