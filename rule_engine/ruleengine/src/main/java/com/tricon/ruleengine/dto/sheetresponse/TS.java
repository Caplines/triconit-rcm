package com.tricon.ruleengine.dto.sheetresponse;

import java.io.FileOutputStream;

public class TS {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//int x=5;
		for(int x=6;x<=100;x++) {
			
		
		String fileData = tes(x);
		FileOutputStream fos = new FileOutputStream("D:/Project/Tricon/RuleEngine(Capline Dental Management)/capline/"
				+ "rule_engine/ruleengine/src/main/java/com/tricon/ruleengine/dto/sheetresponse/Response"+x+".java");
		fos.write(fileData.getBytes());
		fos.flush();
		fos.close();
		}
	}
	
	public static String tes(int x) {
		
		String dd="package com.tricon.ruleengine.dto.sheetresponse;\r\n" + 
				"\r\n" + 
				"public class Response"+x+" extends Response"+(x-1)+"{\r\n" + 
				"	\r\n" + 
				"	String c"+x+";\r\n" + 
				"\r\n" + 
				"	public String getC"+x+"() {\r\n" + 
				"		return c"+x+";\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	public void setC"+x+"(String c"+x+") {\r\n" + 
				"		this.c"+x+" = c"+x+";\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	\r\n" + 
				"\r\n" + 
				"}\r\n" + 
				"";
		
		return  dd;
		
	}

}
