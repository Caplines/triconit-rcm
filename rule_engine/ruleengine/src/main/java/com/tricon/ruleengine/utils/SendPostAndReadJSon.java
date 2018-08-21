package com.tricon.ruleengine.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SendPostAndReadJSon {
	
	
	public static  Object sendPostRequest(String urlParameters,String urlReq,Class<?> clazz) throws IOException {
		//System.out.println(urlParameters);
		byte[] postData =urlParameters.getBytes( StandardCharsets.UTF_8 );
		int postDataLength = postData.length;
		Object fromGraph=null;
		InputStream stream = null;
		String request = urlReq;
		URL url = new URL( request );
		HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty("charset", "utf-8");
		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
		conn.setUseCaches(false);
		try {
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
            String result = reader.readLine();
            fromGraph  =new ObjectMapper().readValue(result, clazz);
            //Player p = g.fromJson(jsonString, Player.class)


            //Read more: http://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html#ixzz5LoZdhIvN

			}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return fromGraph;
	}
	
	public static  String sendGetDataGraphDownloadUrl(String token,String urlReq) {
		try {
            HttpClient httpclient = HttpClientBuilder.create().build();  // the http-client, that will send the request
            HttpGet httpGet = new HttpGet(urlReq);   // the http GET request
            httpGet.addHeader("Authorization", "bearer "+token); // add the authorization header to the request
            HttpResponse response = httpclient.execute(httpGet); // the client executes the request and gets a response
            int responseCode = response.getStatusLine().getStatusCode();  // check the response code
            switch (responseCode) {
                case 200: { 
                    // everything is fine, handle the response
                    String stringResponse = EntityUtils.toString(response.getEntity());  // now you have the response as String, which you can convert to a JSONObject or do other stuff
            	System.out.println(stringResponse.split("\",\"createdDateTime\"")[0].split(".graph.downloadUrl\":\"")[1]);
            	
                    return 	stringResponse.split("\",\"createdDateTime\"")[0].split(".graph.downloadUrl\":\"")[1];
                    //String  =new ObjectMapper().readValue(stringResponse, clazz);
                //    return stringResponse;
                    //break;
                }
                case 500: {
                    // server problems ?
                    break;
                }
                case 403: {
                    // you have no authorization to access that resource
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

	public static  Object sendGetDataGraphDownloadJson(String token,String urlReq,Class<?> clazz) {
		Object fromGraph=null;
		HttpResponse response=null;
		try {
            //System.out.println(urlReq);
            
            
            int timeout = 50000;
            RequestConfig config = RequestConfig.custom()
              .setConnectTimeout(timeout * 1000)
              .setConnectionRequestTimeout(timeout * 1000)
              .setSocketTimeout(timeout * 1000).build();
            CloseableHttpClient client = 
            		  HttpClientBuilder.create().setDefaultRequestConfig(config).build();
            //HttpClient httpclient = HttpClientBuilder.create().build();  // the http-client, that will send the request
            HttpGet httpGet = new HttpGet(urlReq);   // the http GET request
            httpGet.addHeader("Authorization", "bearer "+token); // add the authorization header to the request
             response = client.execute(httpGet); // the client executes the request and gets a response
            int responseCode = response.getStatusLine().getStatusCode();  // check the response code
            System.out.println("-----responseCode--"+responseCode);
            switch (responseCode) {
                case 200: { 
                    String stringResponse = EntityUtils.toString(response.getEntity());  // now you have the response as String, which you can convert to a JSONObject or do other stuff
            	//System.out.println("--stringResponse----");
            	fromGraph  =new ObjectMapper().readValue(stringResponse, clazz);
                }
                case 500: {
                    break;
                }
                case 403: {
                    // you have no authorization to access that resource
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	if( response.getEntity() != null ) {
       
               // response.getEntity().consumeContent();
             }
		}
		return fromGraph;
	}

	
	public static  String sendPostRequestSession(JSONObject msg,String urlReq,String token,boolean pasreRes) throws Exception {

		/*
		HttpResponse response=null;
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		

		try {
		    HttpPost request = new HttpPost(urlReq);
		    StringEntity params = new StringEntity(msg.toString());
		    request.addHeader("content-type", "application/json");
		    request.setHeader("Authorization", "bearer "+"eyJ0eXAiOiJKV1QiLCJub25jZSI6IkFRQUJBQUFBQUFEWHpaM2lmci1HUmJEVDQ1ek5TRUZFNWFFNnFWQ2F5OGhFTW5nVjktNE02QmJEMEsyTlBRUFlFQ28yVDlCQ1J6em1sek84Y3pCMm1HLURTTUdWbDdWdVU4WWk0eVFaTFZ1eFNjSGhtdjd3V3lBQSIsImFsZyI6IlJTMjU2IiwieDV0IjoiN19adWYxdHZrd0x4WWFIUzNxNmxValVZSUd3Iiwia2lkIjoiN19adWYxdHZrd0x4WWFIUzNxNmxValVZSUd3In0.eyJhdWQiOiJodHRwczovL2dyYXBoLm1pY3Jvc29mdC5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC80YTQxZDNiMS1jN2FiLTQ4N2UtOTQ5My0zMDkzYmMzYmI0OTYvIiwiaWF0IjoxNTMyNjk4Njc2LCJuYmYiOjE1MzI2OTg2NzYsImV4cCI6MTUzMjcwMjU3NiwiYWNjdCI6MCwiYWNyIjoiMSIsImFpbyI6IjQyQmdZREMwV0hKeSs1dmRyV1VyejJ1cjE3SitVbkNOVEdHczJiOVR3emRtZzltdE5Ra0EiLCJhbXIiOlsicHdkIl0sImFwcF9kaXNwbGF5bmFtZSI6IlJ1bGUgRW5naW5lIExvY2FsIiwiYXBwaWQiOiI4NGIxZTY5Zi1lYTljLTRhNGYtYmZmMi0xYzhkZjRhZDViYzciLCJhcHBpZGFjciI6IjEiLCJpcGFkZHIiOiIxMTIuNzkuMTUyLjE3MCIsIm5hbWUiOiJKYXNwZXIiLCJvaWQiOiI5MDRmOTQzNS1hNTE3LTQzNDgtODRiNC04Njg4NDMxMDZiYzEiLCJwbGF0ZiI6IjMiLCJwdWlkIjoiMTAwMzAwMDBBOEMwN0I3OSIsInNjcCI6IkZpbGVzLlJlYWQgRmlsZXMuUmVhZC5BbGwgRmlsZXMuUmVhZFdyaXRlIEZpbGVzLlJlYWRXcml0ZS5BbGwgVXNlci5SZWFkIiwic3ViIjoiQjV5UF9pSmVhZXJGNkpjaTNBaGI1aVlaNU90cmgyNGl4OHUtMUlzZEFOUSIsInRpZCI6IjRhNDFkM2IxLWM3YWItNDg3ZS05NDkzLTMwOTNiYzNiYjQ5NiIsInVuaXF1ZV9uYW1lIjoiamFzcGVyQGNhcGxpbmVzZXJ2aWNlcy5jb20iLCJ1cG4iOiJqYXNwZXJAY2FwbGluZXNlcnZpY2VzLmNvbSIsInV0aSI6IkpvaWh6eWpLZms2eEtoaEROUDltQUEiLCJ2ZXIiOiIxLjAifQ.dvZoaiUrRp849H4--Uh7M30zFtLf3sM_GeanOOQ4TBsnDKoYopnF4eKpNg1QvYRk2KzxjdUtHyb4XtRmde7mYYs4qDXYHLGICr01UjaFFY02f1fXmYzaDd4nVGGW35XWzVyEGkHysuM47XMOgkbLZQflvi8KqJG6Zp7v7lG-MWFRvhnZdnU_QRL3TsicPbl-xZsV90v7VGXf5nEZYZiAIec1l0yYEcxt7dcVKU0klNBZNFSHocNR8_nk5F5ZnOfE_7_l3hzrBW0GT5QTT50WTMHrBg5trwW8lMDjYmeSdPTwSd4tCbbq0FcK1vLJDUe7n3hFKAaHz0Uq0Y1FTvnVpw"); 
		    request.setEntity(params);
		    response= httpClient.execute(request);
		    int responseCode = response.getStatusLine().getStatusCode();  // check the response code
            switch (responseCode) {
                case 200: { 
                    String stringResponse = EntityUtils.toString(response.getEntity());  // now you have the response as String, which you can convert to a JSONObject or do other stuff
                    
                   System.out.println("--stringResponse----");
                }
                case 500: {
                    break;
                }
                case 403: {
                    // you have no authorization to access that resource
                    break;
                }
            }
		// handle response here...
		} catch (Exception ex) {
		    // handle exception here
		} finally {
		    httpClient.close();
		}
		*/
		//byte[] postData =urlParameters.getBytes( StandardCharsets.UTF_8 );
		//int postDataLength = postData.length;
		//Object fromGraph=null;
		//msg.set
		//StringEntity params = new StringEntity(msg.toString());
		//System.out.println("calllllllllllllllll");
		InputStream stream = null;
		String request = urlReq;
		URL url = new URL( request );
		HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "bearer "+token); 
		//conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
		conn.setUseCaches(false);
		try {
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(msg.toString());
			stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
            String result = reader.readLine();
            System.out.println("ssssssss-0"+result);
            //System.out.println(result.split("&usid=")[0].split("\",\"id\":\"")[1]);
            //Player p = g.fromJson(jsonString, Player.class)
            if (pasreRes) return result.split("&usid=")[0].split("\",\"id\":\"")[1];
            //Read more: http://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html#ixzz5LoZdhIvN

			}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

}
