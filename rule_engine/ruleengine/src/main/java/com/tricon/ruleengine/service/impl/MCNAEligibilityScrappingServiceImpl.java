package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONObject;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;


import com.tricon.ruleengine.dto.scrapping.EligibilityDto;
import com.tricon.ruleengine.dto.scrapping.HistoryDto;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.ConstantsScrapping;
//import java.util.function.Function;

public class MCNAEligibilityScrappingServiceImpl extends BaseScrappingServiceImpl implements  Callable<List<?>>	 {
	
	private ScrappingSiteDetails scrappingSiteDetails=null;
	private String CLIENT_SECRET_DIR,CREDENTIALS_FOLDER;
	private Map<String, List<Object>> mapData=null;
	private boolean updateSheet=false;
	
	private final int  max=2;
	private int  ctALL=0;
	private int attempt =0;
	

	public ScrappingSiteDetails getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}


	public void setScrappingSiteDetails(ScrappingSiteDetails scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}


	MCNAEligibilityScrappingServiceImpl(ScrappingSiteDetails scrappingSiteDetails,
    		String	CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER,Map<String, List<Object>> mapData,boolean updateSheet) {
		this.scrappingSiteDetails=scrappingSiteDetails;
		this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		this.mapData=mapData;
		this.updateSheet=updateSheet;
	       // store parameter for later user
	   }
	
	
	@Override
    public List<EligibilityDto> call() {
		List<EligibilityDto> r=scrapSite(scrappingSiteDetails,mapData);
		  try {
			 if (updateSheet) ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
					  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<EligibilityDto>)r,scrappingSiteDetails.getRowCount(),"NO","M");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage().split("Attempting to write column: ")[1].split(",")[0]) ;
			if (e.getMessage().contains("Attempting to write column: "))
			appendSheet(r);
		}

			return r;
    }
	
	private void appendSheet(List<EligibilityDto> r) {
		try {
			ConnectAndReadSheets.appendCelltoSheet(scrappingSiteDetails.getGoogleSheetId(),
					  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,Constants.ATTEMPT_TO_ADD_NEW_COLUMNS);
			 if (updateSheet) ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
					  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<EligibilityDto>)r,scrappingSiteDetails.getRowCount(),"NO","D");
			}catch(Exception e) {
				attempt=attempt+1;
				e.printStackTrace();
				if (attempt<50 && e.getMessage().contains("Attempting to write column: "))
				appendSheet(r);
			}
	}



	public List<EligibilityDto> scrapSite(ScrappingSiteDetails scrappingSiteDetails,
			Map<String, List<Object>> mapData) {
		WebDriver driver = new HtmlUnitDriver(true);
		// TODO Auto-generated method stub
		setProps(scrappingSiteDetails.getProxyPort());
		List<EligibilityDto> eList = new ArrayList<>();
		try {
			loginToSiteMCNA(scrappingSiteDetails, driver);

			for (Map.Entry<String, List<Object>> entry : mapData.entrySet()) {
				List<Object> x = (List<Object>) entry.getValue();
				navigatetoEligiblity(driver);
				for (Object obj : x) {
					MCNADentaSheet sh = (MCNADentaSheet) obj;
					EligibilityDto d=	parsePage(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(), sh.getfName(), sh.getZip(),sh.getAppointmentDate(),
							scrappingSiteDetails.getLocationProvider(),"",
							sh.getfName(),sh.getlName(),sh.getInsuranceName(),false);
					if (d!=null && d.getEligible().equals(ConstantsScrapping.SUBSCRIBER_NOT_FOUND) && 
							(!sh.getSubscriberId().equalsIgnoreCase("NA") || !sh.getSubscriberId().trim().equals(""))){
						navigatetoEligiblity(driver);
						//(WebDriver driver,String dob,String subscriberId,
						//		String verifyLastName,String verifyFirstName,String zip,String serviceDate,String officeName,String locationProvider,
						//		String fname,String lname,String insuranceName,boolean byName)
						
						
						
						d=	parsePage(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(), sh.getfName(), sh.getZip(),sh.getAppointmentDate(),
								scrappingSiteDetails.getLocationProvider(),"",
								sh.getfName(),sh.getlName(),sh.getInsuranceName(),true);
					}
					if (d!=null) {
						d.setMcnaSheet(sh);
						eList.add(d);
						
						if (updateSheet) {
							if (eList.size()>=max) {
								try {
									List<EligibilityDto> rListC = new ArrayList<>(eList);
									//int q=1+(max*ctALL);
									//if (q>1) q=q+1+ctALLO;
									//ctALLO=ctALLO+1;
									
									ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
											  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<EligibilityDto>)rListC,scrappingSiteDetails.getRowCount(),"YES","M");

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//size=size+rList.size();
								ctALL=ctALL+1;
								eList.clear();
							}else {
							 //size=size+1;
							 //finalSize=size;
							}	
						}
						
						
						
					}
				}
			}
			// scrappingSiteDetails.get
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			driver.close();
			}catch (Exception e) {
				// TODO: handle exception
			}
		return eList;
	}

	private void navigatetoEligiblity(WebDriver driver) throws InterruptedException {
		driver.navigate().to("https://portal.mcna.net/provider/verify_eligibility");
		Thread.sleep(5000);
	}

	private EligibilityDto parsePage(WebDriver driver,String dob,String subscriberId,
			String verifyLastName,String verifyFirstName,String zip,String serviceDate,String officeName,String locationProvider,
			String fname,String lname,String insuranceName,boolean byName) throws Exception{
		String id="7";//6 old 
		
		if (dob.equals("") ) return null;
		if (subscriberId.equals("") &&  verifyLastName.equals("")) return null;
		EligibilityDto dto= new EligibilityDto();
		String[] dobA=dob.split("/");
		WebElement element4 =driver.findElement(By.xpath("/html/body/div["+id+"]/div[1]/div[2]"));
		//WebDriverWait wait= new WebDriverWait(driver, 5);
		//wait.until(ExpectedConditions.visibilityOf(element4));
		element4 =driver.findElement(By.id("verifyDob"));
		element4.sendKeys(dob);
		driver.findElement(By.id("verifySubscriberId"));
		if (subscriberId!=null && !subscriberId.equalsIgnoreCase("NA") && !subscriberId.trim().equals(""))element4.sendKeys(subscriberId);
		driver.findElement(By.id("verifyLastName"));
		element4.sendKeys(verifyLastName);
		driver.findElement(By.id("verifyFirstName"));
		element4.sendKeys(verifyFirstName);
		driver.findElement(By.id("verifyZip"));
		element4.sendKeys(zip);
		Thread.sleep(1000);
		driver.findElement(By.id("facilityId"));
		String facilityId=driver.findElement(By.id("facilityId")).getAttribute("value");
		if (!byName)createVerifyUrlAndNavigate(driver, dobA[0]+"%2F"+dobA[1]+"%2F"+dobA[2], subscriberId, verifyLastName, verifyFirstName, zip, facilityId);
		else createVerifyUrlAndNavigate(driver, dobA[0]+"%2F"+dobA[1]+"%2F"+dobA[2], "0", verifyLastName, "", "", facilityId);
		//Check for Valid data
		String z=driver.getPageSource();
		//use try catch here ...
       //System.out.println(z.split(",\"id\":\"")[1].split("\",\"sub_id\":\"")[0]);
       JSONObject jsonObj = new JSONObject(z);
       JSONObject jsonChildObject = (JSONObject)jsonObj.get("verify_eligibility");
       Iterator<String> iterator  = jsonChildObject.keys();
       String key = null;
       String insured="";
       String messageRes="";
       String subsId="";
       
       while(iterator.hasNext()){
           key = (String)iterator.next();
           if (key.equals("insured"))
        	   insured=(String)((JSONObject)jsonChildObject.get(key)).get("id");
           if (key.equals("response_message")) {
        	   messageRes=(String) jsonChildObject.get(key);
           }
           if (key.equals("insured")) {
        	   subsId=(String)((JSONObject)jsonChildObject.get(key)).get("sub_id");
           }
       }
           
       
       //                                                                insured /subsid   /dob      /hidden/0/1
	   if (messageRes.equalsIgnoreCase("ok")) {
	   dto.setMessage(ConstantsScrapping.SUBSCRIBER_FOUND);
	   if (byName) {
		   subscriberId=subsId;
	   }
	   driver.navigate().to("https://portal.mcna.net/provider/eligible/"+insured+"/"+subscriberId+"/"+dobA[2]+"-"+dobA[0]+"-"+dobA[1]+"/"+facilityId+"/0/1");
		Thread.sleep(5000);
		element4 =driver.findElement(By.xpath("/html/body/div["+id+"]/div[1]/div[2]"));		
		if (element4.getText().startsWith("Subscriber is Eligible")) {
			//System.out.println("Subscriber is Eligible");
			dto.setEligible(ConstantsScrapping.SUBSCRIBER_Eligible);
				
		}else {
			dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
		}
		List<WebElement> wList=driver.findElements(By.className("eligLine"));
		for(WebElement w:wList) {
			String st=w.getText().replaceAll("\r", "").replaceAll("\n", "");
			if (st.startsWith("Plan:")) {
				dto.setEmployerName(st.replace("Plan:", ""));
			}
			if (st.startsWith("Copay:")) {
				dto.setCopay(st.replace("Copay:", ""));
			}
			if (st.startsWith("Benefits Available:")) {
				dto.setBenefitRemaining(st.replace("Benefits Available:", ""));
			}
			
		}
		wList=driver.findElements(By.className("eligTextRight"));
		for(WebElement w:wList) {
			//System.out.print(w.getText());
			
			String txt=w.getText().replace("\r", "").replace("\n", "");
			boolean providerSame=true;
			//System.out.println("PPPPPP"+txt);
			if (txt.contains("YOU ARE NOT the")){
				providerSame=false;
				dto.setProviderChange("Change Provider");
			}else {
				dto.setProviderChange("");
			}
			dto.setProviderSame(providerSame+"");
			if (txt.contains("The Main Dental Home provider is")) {
				dto.setProviderName(txt.split("The Main Dental Home provider is ")[1]);
			}
			break;
		}
	wList= driver.findElements(By.xpath("/html/body/div["+id+"]/div[1]/div[3]/table[2]/tbody/tr"));
	String dos="";
	//dto.getHistoryList()
		for(WebElement w:wList) {
			HistoryDto hd= new HistoryDto();
			//System.out.print(w.getText());
		List<WebElement> wListChild=w.findElements(By.tagName("td"));
		if (wListChild.size()==0) continue;
		if (wListChild.size()>0) {
			//System.out.println(wListChild.get(0).getText());
			if (!wListChild.get(0).getText().trim().equals("")) dos=wListChild.get(0).getText();
		}
		hd.setDos(dos);
		if (wListChild.size()>1) {
			hd.setCode(wListChild.get(1).getText().split(":")[0]);
			//hd.setDescription(description);
		}
		if (wListChild.size()>2) {
		}
		if (wListChild.size()>3) {
			hd.setTooth(wListChild.get(3).getText());
		}
		if (wListChild.size()>4) {
			hd.setSurface(wListChild.get(4).getText());
		}
		//System.out.println(wListChild.get(5).getText());
		
		dto.getHistoryList().add(hd);
			
		}
	   }else {
			//System.out.println(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);	
			dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
			dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
		}
	   
	   
	   
	   dto.setDob(dob);
       dto.setInsuranceName(insuranceName);
       dto.setFirstName(fname);
       dto.setLastName(lname);
       dto.setSubscriberId(subscriberId);
       
	   return dto;
	}
	
	public static void createVerifyUrlAndNavigate(WebDriver driver,String dob,
			String subscriberId,String verifyLastName,String verifyFirstName,
			String zip, String provider) throws InterruptedException {
		String url="https://portal.mcna.net/provider/verify_eligibility.json?verifyDob="+dob
				+ "&verifySubscriberId="+subscriberId+"&verifyLastName="+verifyLastName+"&verifyFirstName="+verifyFirstName
				+ "&verifyZip="+zip+"&providerFacilityId="+provider;
		driver.navigate().to(url);
		
        Thread.sleep(4000);
		
	}

}
