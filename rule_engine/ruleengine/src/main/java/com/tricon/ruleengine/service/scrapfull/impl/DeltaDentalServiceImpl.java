package com.tricon.ruleengine.service.scrapfull.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.PatientScrapSearchDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.PatientDetailTemp;
import com.tricon.ruleengine.model.db.PatientHistoryTemp;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.service.impl.BaseScrappingServiceImpl;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DateUtils;
import com.tricon.ruleengine.utils.FreqencyUtils;
import com.tricon.ruleengine.utils.MessageUtil;

public class DeltaDentalServiceImpl extends BaseScrappingServiceImpl implements  Callable<Boolean> {

	//document : https://docs.google.com/spreadsheets/d/1-3PVTrzgSl0n3OEY7Qt0JZ_WDK7twIQWU3Hk7UjSdBM/edit#gid=1557872290
	static {
		
		  java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(
		  Level.OFF);
		  java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(
		  Level.OFF);
		  java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(
		  Level.OFF);
		  java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF
		  ); System.setProperty("org.apache.commons.logging.Log",
		  "org.apache.commons.logging.impl.NoOpLog");
		 
	}

	private static String inNetworkCoinsurance="In Network Coinsurance";
	private static String inNetworkDeductible="In Network Deductible";
	private static String lastrowunder2ndcolumn="lastrowunder2ndcolumn";
	private static String lastrowunder2ndcolumnNext="lastrowunder2ndcolumnNext";
	
	private static String uptoAge="Up to Age";
	private static String otherLimitations="Other Limitations";
	
	
	private static String benefitInNetwork="In Network";
	private static String benefitInDeductible="In Network ded";
	private static String benefitWaitingPeriod="Waiting Period";
	private static String benefitMaximumLifeTime="Maximum Life Time ";
	private static String benefitMaximumLifeTimeRem="Maximum Life Time  Rem";
	
	
	@Autowired
	ScrapingFullDataDoa dataDoa;
	
	@Autowired
	PatientDao patDao;
	
	private ScrappingSiteDetailsFull scrappingSiteDetails;
	//this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
	//this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
	//this.mapData=mapData;
	//this.updateSheet=updateSheet;
	private ScrappingFullDataDetailDto dto;
	private Office office;
	private User user;
	
	
	
	private static String siteName="";
    
	public DeltaDentalServiceImpl(PatientDao patDao,ScrapingFullDataDoa dataDoa,ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto,
			User user,Office office) {
		
		this.patDao=patDao;
		this.dataDoa=dataDoa;
		this.scrappingSiteDetails=scrappingSiteDetails;
		//this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		//this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		//this.mapData=mapData;
		//this.updateSheet=updateSheet;
		this.dto = dto;
		this.office = office;
		siteName=dto.getSiteName();
		this.user=user;
	       // store parameter for later user
	   }
	
	public String scrapSite(ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto,
			User user,Office office) {
		WebDriver driver = null;
		setProps(scrappingSiteDetails.getProxyPort());
		try {
			driver = new HtmlUnitDriver(true);
			boolean navigate = loginToSiteDelta(dto, driver);
			int one=0;
            try {
			for (PatientScrapSearchDto data : dto.getDto()) {
				boolean issueNo = true;
				if (one==0) issueNo =navigatetoMainSite(driver, navigate);
				one=1;
				System.out.println("888888888888- STARTED...");
				PatientTemp d = parsePage(driver, data, siteName, issueNo,office);
				System.out.println("888888888888 -END "+d);
				if (d != null) {
					// Update the Data in Database
					updateDatainDB(d,office,user);
				}
                Thread.sleep(2000);
			}
            }catch (Exception e) {
				// TODO: handle exception
            	e.printStackTrace();
			}
			// scrappingSiteDetails.get
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				scrappingSiteDetails.setRunning(false);
				dataDoa.updateScrappingDetailsById(scrappingSiteDetails);
				driver.close();
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}

		}
		return "done";
	}

	@Override
	public Boolean call() throws Exception {
		scrapSite(scrappingSiteDetails,dto,user,office);
		return true;
	}
	
	private PatientTemp parsePage(WebDriver driver, PatientScrapSearchDto sh, String webSiteName, boolean issueNo,Office office)
			throws InterruptedException {
		PatientTemp temp = new PatientTemp();
		temp.setDob(sh.getDob());
		temp.setPatientId(DateUtils.createPatientIdByDate(sh.getPatientId()));
		//temp.setFirstName(sh.getFirstName());
		temp.setWebsiteName(webSiteName);
		//temp.setLastName(sh.getLastName());
		temp.setOffice(office);
		temp.setStatus("Parsing Start");
		if (!issueNo) {

			temp.setStatus("Check Password.");
		}
		PatientDetailTemp t = new PatientDetailTemp();
		t.setPatient(temp);
		t.setOffice(office);
		t.setMemberId(sh.getMemberId().trim());
		t.setMemberSSN(sh.getSsnNumber().trim());
		
		Set<PatientDetailTemp> s = new HashSet<>();
		s.add(t);
		temp.setPatientDetails(s);
		if (!issueNo)
			return temp;
		issueNo = searchPatient(driver, temp, sh.getMemberId(), sh.getSsnNumber(), sh.getDob(),sh.getFirstName(),sh.getLastName());
		if (!issueNo) {
			temp.setStatus("Patient Not found.." +sh.getDob()+" "+ sh.getMemberId());
			temp.setFirstName(sh.getFirstName());
			temp.setLastName(sh.getLastName());
			
			return temp;
		}
		temp.setStatus("Patient found..");
		//populateMandatoryData(temp);
		createPatientDetailSetup(driver, temp, sh);
		//Click on MyPatient Link so that Next Patient can Start to be Searched 
		driver.findElement(By.xpath("/html/body/div[1]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[1]/div/div[2]/table/tbody/tr/td[4]/span/a")).click();
	    Thread.sleep(2000);
		// Logic to fetch data from Site...

		return temp;
	}

	private void updateDatainDB(PatientTemp data,Office office , User user) {
        try {
		patDao.savePatientTempDataWithDetailsAndHistory(data, office, user);
        }catch(Exception c) {
        	System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIII");
        	StringWriter sw = new StringWriter();
            c.printStackTrace(new java.io.PrintWriter(sw));
            //String exceptionAsString = sw.toString();
        	data.setStatus(data.getStatus()+ "-- "+ sw.toString());
        	try {
        		patDao.updatePatientTempDataOnly(data);
        	}catch (Exception e) {
				// TODO: handle exception
			}
        }
	}

	private void createPatientDetailSetup(WebDriver driver, PatientTemp temp,PatientScrapSearchDto sh)
			throws InterruptedException {
		
		try {
			   fetchPatDetails(driver, temp);
				System.out.println("d");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				temp.setStatus("Patient Fetch data issue");
			}
		

	}

	private boolean searchPatient(WebDriver driver, PatientTemp temp, String memberid, String ssn, String dob,String fName,String lName)
			throws InterruptedException {

		String id = memberid.trim().equals("") ? ssn.trim() : memberid.trim();
		
		if (id.equals("") || dob.equals("") || fName.trim().equals("") || lName.trim().equals(""))
			return false;
		
		WebElement ini= driver.findElement(By.xpath("/html/body/div[2]/form/div/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[10]/div/div[3]/table/tbody/tr/td[1]/span/input"));
		ini.clear();
		Thread.sleep(5000);

		WebElement validate = driver.findElement(
				By.xpath("/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[10]/div/div[3]/table/tbody/tr/td[3]/button"));
		ini.sendKeys(fName.trim()+" "+ lName.trim());
		try {
			validate.click();
		} catch (Exception e) {
			return false;
		}
		Thread.sleep(5000);
		List<WebElement> rows = driver
				.findElements(By.className("data-table-row"));
		//boolean found=false;
		if (rows != null && rows.size()>0) {
			try {
			for(WebElement row: rows) {
				
		    List<WebElement> tds = row.findElements(By.tagName("td"));
			//List<WebElement>d=tds.get(7).findElements(By.tagName("span")).get(1).getText();
			String name= tds.get(7).findElements(By.tagName("span")).get(1).getText();//findElement(By.tagName("span")).getText();
			String[] dobA	=tds.get(7).findElements(By.tagName("span")).get(0).findElements(By.tagName("span")).get(3).getText().split("/");
			String dobS= ((dobA[0].length()==1)?("0"+dobA[0]):dobA[0])+"/"+((dobA[1].length()==1)?("0"+dobA[1]):dobA[1])+"/"+dobA[2];// + "/" +(dobA[0].length()==1)?"0"+dobA[1].length():"";
			//String mem =tds.get(12).findElements(By.tagName("span")).get(1).getText();
			//Ask for ID as not given any where 
			if (name.equalsIgnoreCase((fName.trim()+" "+ lName.trim())) &&
				//id .contains(mem) && 
				dobS.equals(dob) ) {
				//found=true;
				 row.findElement(By.className("table-col8")).findElements(By.tagName("a")).get(0).click();
				Thread.sleep(3000);
				return true;
				
				
			}
			}
			}catch (Exception e) {
				return false;
				// TODO: handle exception
			}
		}else {
			return false;
		}
		return false;

	}

	private void populateMandatoryData(PatientTemp temp){
		PatientDetailTemp dtemp =temp.getPatientDetails().iterator().next();
		
		dtemp.setPlanAnnualMax(Constants.SCRAPPING_MANDATORY_WARNING); //1
		dtemp.setPlanAnnualMaxRemaining(Constants.SCRAPPING_MANDATORY_WARNING);//2
		dtemp.setPlanIndividualDeductible(Constants.SCRAPPING_MANDATORY_WARNING);//3
		dtemp.setPlanIndividualDeductibleRemaining(Constants.SCRAPPING_MANDATORY_WARNING);//4
		dtemp.setBasicPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//5
		dtemp.setBasicSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);//6
		dtemp.setMajorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//7
		dtemp.setMajorSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);//8
		dtemp.setEndodonticsPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//9
		dtemp.setEndoSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);//10
		
		dtemp.setPerioSurgeryPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//11
		dtemp.setPerioSurgerySubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);//12
		
		dtemp.setPreventivePercentage(Constants.SCRAPPING_MANDATORY_WARNING);//13
		dtemp.setDiagnosticPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//14
		dtemp.setpAXRaysPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//15
		dtemp.setNightGuardsD9944Fr(Constants.SCRAPPING_MANDATORY_WARNING);//19 //Cross Check
		
		dtemp.setBasicWaitingPeriod(Constants.SCRAPPING_MANDATORY_WARNING);//20 in DOC
		dtemp.setMajorWaitingPeriod(Constants.SCRAPPING_MANDATORY_WARNING);//21 in DOC
		
		//dtemp.setsSCD2930FL();//22  not mandatory
		//dtemp.setsSCD2931FL();//23  not mandatory
		//dtemp.setExamsD0120FL();//24  not mandatory
		//dtemp.setExamsD0140FL();//25  not mandatory
		//dtemp.seteExamsD0145FL("");//26  not mandatory
		//dtemp.setExamsD0150FL("");//27  not mandatory
		//dtemp.setxRaysBWSFL("");//28 not mandatory
		//dtemp.setxRaysPAD0220FL("");//29 not mandatory
		//dtemp.setxRaysPAD0230FL("");//30 not mandatory
		//dtemp.setxRaysFMXFL("");//31 not mandatory
		//32 missing
		//dtemp.setFlourideD1208FL("");//33 not mandatory
		//dtemp.setFlourideAgeLimit("");//34 not mandatory
		//dtemp.setVarnishD1206FL("");//35 not mandatory
		//dtemp.setVarnishD1206AgeLimit("");//36 not mandatory
		dtemp.setSealantsD1351Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//37
		//dtemp.setSealantsD1351FL("");//38
		//dtemp.setSealantsD1351AgeLimit("")//39
		dtemp.setSealantsD1351PrimaryMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);//40
		dtemp.setSealantsD1351PrimaryMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);//41
		dtemp.setSealantsD1351PermanentMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);//42
		//dtemp.setProphyD1110FL("");//43
		//dtemp.setProphyD1120FL("");//44
		//45 missing
		dtemp.setsRPD4341Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//46
		//dtemp.setsRPD4341FL("");//47
		//48
		//49
	    dtemp.setPerioMaintenanceD4910Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//50
		//dtemp.setPerioMaintenanceD4910FL("");//51
	    dtemp.setPerioMaintenanceD4910AltWProphyD0110(Constants.SCRAPPING_MANDATORY_WARNING);//52
	    dtemp.setFMDD4355Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//53
	    //dtemp.setfMDD4355FL("");//54
	    dtemp.setGingivitisD4346Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//55
		//dtemp.setGingivitisD4346FL("");//56
	    dtemp.setNitrousD9230Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//57
	    dtemp.setiVSedationD9243Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//58
	    dtemp.setiVSedationD9248Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//59
	    dtemp.setExtractionsMinorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//60
	    dtemp.setExtractionsMajorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//61
	    dtemp.setCrownLengthD4249Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//62
	    //dtemp.setCrownLengthD4249FL("");//63
	    //64
	    //dtemp.setAlveoD7311FL("");//65
	    //66
	    //dtemp.setAlveoD7310FL("");//67
	    //dtemp.setCompleteDenturesD5110D5120FL("");//68
	    //dtemp.setImmediateDenturesD5130D5140FL("");//69
	    //dtemp.setPartialDenturesD5213D5214FL("");//70
	    //dtemp.setInterimPartialDenturesD5214FL("");//71
	    //72
	    //dtemp.setBoneGraftsD7953FL("");//73
	    dtemp.setImplantCoverageD6010Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//74
	    dtemp.setImplantCoverageD6057Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//75
	    dtemp.setImplantCoverageD6190Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//76
	    dtemp.setImplantSupportedPorcCeramicD6065Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//77
	    dtemp.setPostCompositesD2391Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//78
	    //dtemp.setPostCompositesD2391FL("");//79
	    //80
	    dtemp.setCrownsD2750D2740Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//81
	    //dtemp.setCrownsD2750D2740FL("");//82
	    //83
	    //84
	    dtemp.setD9310Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//85
	    //dtemp.setD9310FL("");//86
				
	    dtemp.setBuildUpsD2950Covered(Constants.SCRAPPING_MANDATORY_WARNING);//87
	    //dtemp.setBuildUpsD2950FL("");//88
        //89
	    dtemp.setOrthoPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//90
	    //dtemp.setOrthoMax();//91
	    //dtemp.setOrthoAgeLimit("");//92 benefit
	    // 93 //94 //95 //96 //97 //99 //100 //101 
	    dtemp.setBridges1(Constants.SCRAPPING_MANDATORY_WARNING);//102
	    //dtemp.setBridges2("");//103
	    //104
	    dtemp.setDen5225Per(Constants.SCRAPPING_MANDATORY_WARNING);//105
	    //dtemp.setDenf5225FR("");//107
	    
	    dtemp.setDen5226Per(Constants.SCRAPPING_MANDATORY_WARNING);//106
	    //dtemp.setDenf5226Fr("");//108
	    dtemp.setDiagnosticSubDed(Constants.SCRAPPING_MANDATORY_WARNING);//109
	    
	    //dtemp.setImplantsFrD6010("");//110
	    //dtemp.setImplantsFrD6057("");//111
	    //dtemp.setImplantsFrD6065("");//112
	    //dtemp.setImplantsFrD6190("");//113
	    dtemp.setNightGuardsD9945Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//114
	    //dtemp.setOrthoRemaining("");//115
		//dtemp.setOrthoWaitingPeriod("");//116
	    dtemp.setpAXRaysSubDed(Constants.SCRAPPING_MANDATORY_WARNING);//117
	    dtemp.setPreventiveSubDed(Constants.SCRAPPING_MANDATORY_WARNING);//118
	    //119
	    dtemp.setFmxPer(Constants.SCRAPPING_MANDATORY_WARNING);//120
	    //dtemp.setNightGuardsD9944Fr("");//121
	    //dtemp.setNightGuardsD9945Fr("");//122
	    
	    //123 //124 //125 //126
	    
	    
	    
		//Debug
	}

	private void fetchPatDetails(WebDriver driver, PatientTemp temp) throws InterruptedException {
        Thread.sleep(8000);
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();
		
		
		//WebElement divOffBenfitm= driver.findElement(By.xpath("/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/div[1]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/div[1]/div/div/div/table/tbody/tr/td[2]/div"));
		List<WebElement>  divOfMaximum= driver.findElements(By.tagName("table"));
		String dd="";
		for(WebElement divOffMax:divOfMaximum) {
			
			try {
				if (divOffMax.getAttribute("summary").equals("Maximums")) {
					for(WebElement tr:divOffMax.findElements(By.tagName("tr"))) {
					
					if (tr.getText().startsWith("Contract Individual Maximum")){
						
						List<WebElement> tds=tr.findElements(By.className("x264"));
						dtemp.setPlanAnnualMax(tds.get(0).getText().replace("$", "").replace(",", ""));//1
						dtemp.setPlanAnnualMaxRemaining(tds.get(1).getText().replace("$", "").replace(",", ""));//2
					}
					}
					//
				}
				if (divOffMax.getAttribute("summary").equals("Deductibles")) {
					for(WebElement tr:divOffMax.findElements(By.tagName("tr"))) {
					
					if (tr.getText().startsWith("Contract Individual Deductible")){
						
						List<WebElement> tds=tr.findElements(By.className("x264"));
						dtemp.setPlanIndividualDeductible(tds.get(0).getText().replace("$", "").replace(",", ""));//3
						dtemp.setPlanIndividualDeductibleRemaining(tds.get(1).getText().replace("$", "").replace(",", ""));//4
					}
					}
					//
				} 
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		System.out.println("Benefit Start ....");
		openSideBarFirst(driver, "Benefit search");
		
		System.out.println("dd"+dd);
		Set<PatientHistoryTemp> hisSet = temp.getPatientHistory();
		openSideBarFirst(driver,"Procedure History");
		fetchHistoryformation(driver,hisSet);
		
		WebElement eleBody = driver.findElement(By.xpath("/html/body/ui-view/ui-view/div/div[3]/div[2]/div/div[2]/plan-info/div/div[2]/table/tbody"));
		List<WebElement> eleTR = eleBody.findElements(By.tagName("tr"));
		Thread.sleep(500);
		for (WebElement el : eleTR) {
			List<WebElement> tds = el.findElements(By.tagName("td"));
			for (WebElement td : tds) {

				if (td.getText().equals("Maximums - Benefit Period")) {
					try {
						dtemp.setPlanAnnualMax(
								el.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",", "").replace("N/A", ""));
					} catch (Exception e) {
						//e.printStackTrace();
						dtemp.setPlanAnnualMax(Constants.SCRAPPING_ISSUE_FETCHING);
						// TODO: handle exception
					}
				}else if (td.getText().equals("Maximums - Benefit Period Remaining")) {
					try {
						dtemp.setPlanAnnualMaxRemaining(
								el.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",", "").replace("N/A", ""));
					} catch (Exception e) {
						//e.printStackTrace();
						dtemp.setPlanAnnualMaxRemaining(Constants.SCRAPPING_ISSUE_FETCHING);
					}
				}else if (td.getText().equals("Deductible - Benefit Period")) {
					try {
						dtemp.setPlanIndividualDeductible(
								el.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",", "").replace("N/A", ""));
					} catch (Exception e) {
						//e.printStackTrace();
						dtemp.setPlanIndividualDeductible(Constants.SCRAPPING_ISSUE_FETCHING);
					}
				}else if (td.getText().equals("Deductible - Benefit Period Remaining")) {
					try {
						dtemp.setPlanIndividualDeductibleRemaining(
								el.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",", "").replace("N/A", ""));
					} catch (Exception e) {
						//e.printStackTrace();
						dtemp.setPlanIndividualDeductibleRemaining(Constants.SCRAPPING_ISSUE_FETCHING);
					}
				} 

			}

		}
		String basicper6=fetchValueByCode("D4346",driver,inNetworkCoinsurance,true,false,true);
		if (basicper6.equals(""))basicper6="0";
		String basicper1=fetchValueByCode("D4341",driver,inNetworkCoinsurance,true,false,true);
		if (basicper1.equals(""))basicper1="0";
		try {
			String v=new Float(basicper6).floatValue() >= new Float(basicper1).floatValue()?"D4341":"D4346";
			dtemp.setBasicSubjectDeductible(MessageUtil.getTEXTNAORYES(fetchValueByCode(v,driver,inNetworkDeductible,true,false,true)));
			dtemp.setBasicPercentage(new Float(basicper6).floatValue() >= new Float(basicper1).floatValue()?basicper1:basicper6);
			
		}catch(Exception p){
			dtemp.setBasicPercentage(basicper6+""+basicper1);
			dtemp.setBasicSubjectDeductible(Constants.SCRAPPING_ISSUE_FETCHING);
		}

        dtemp.setMajorPercentage(fetchValueByCode("D2740",driver,inNetworkCoinsurance,true,false,true));//7
		
        dtemp.setMajorSubjectDeductible(MessageUtil.getTEXTNAORYES(fetchValueByCode("D2750",driver,inNetworkDeductible,true,false,true)));//8
		dtemp.setCrownsD2750D2740Percentage(fetchValueByCode("D2750",driver,inNetworkCoinsurance ,true,true,false));//81
	    dtemp.setCrownsD2750D2740FL(fetchValueByCode("D2750",driver,lastrowunder2ndcolumn,false,true,true));//82
	    
	    
		dtemp.setPreventivePercentage(fetchValueByCode("D1110",driver,inNetworkCoinsurance,true,false,true));//13
		dtemp.setProphyD1110FL(fetchValueByCode("D1110",driver,lastrowunder2ndcolumn,false,true,true));//43
		
		String v =fetchValueByCode("D0150",driver,inNetworkCoinsurance,true,false,false);
		dtemp.setDiagnosticPercentage(v);//14
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setExamsD0150FL(fetchValueByCode("D0150",driver,lastrowunder2ndcolumn,false,true,true));//27
		else fetchValueByCode("",driver,"",false,true,true);
	
		
		v =fetchValueByCode("D0220",driver,inNetworkCoinsurance,true,false,false);
		dtemp.setpAXRaysPercentage(v);//15
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setxRaysPAD0220FL(fetchValueByCode("D0220",driver,lastrowunder2ndcolumn,false,true,true));//29
		else fetchValueByCode("",driver,"",false,true,true);
	
		
		
		dtemp.setNightGuardsD9944Fr(fetchValueByCode("D9944",driver,inNetworkCoinsurance,true,false,false));//19 //Cross Check
		dtemp.setNightGuardsD9944Fr(fetchValueByCode("D9944",driver,lastrowunder2ndcolumn ,false,true,true));//121
		
		v =fetchValueByCode("D2930",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setsSCD2930FL(fetchValueByCode("D2930",driver,lastrowunder2ndcolumn,false,true,true));//22
		else fetchValueByCode("",driver,"",false,true,true);
		
		v =fetchValueByCode("D2931",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setsSCD2931FL(fetchValueByCode("D2931",driver,lastrowunder2ndcolumn,false,true,true));//23
		else fetchValueByCode("",driver,"",false,true,true);
		
		v =fetchValueByCode("D0120",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setExamsD0120FL(fetchValueByCode("D0120",driver,lastrowunder2ndcolumn,false,true,true));//24
		else fetchValueByCode("",driver,"",false,true,true);
		
		v =fetchValueByCode("D0140",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setExamsD0140FL(fetchValueByCode("D0140",driver,lastrowunder2ndcolumn,false,true,true));//25
		else fetchValueByCode("",driver,"",false,true,true);
		
		v =fetchValueByCode("D0145",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.seteExamsD0145FL(fetchValueByCode("D0145",driver,lastrowunder2ndcolumn,false,true,true));//26
		else fetchValueByCode("",driver,"",false,true,true);
		
			
		v =fetchValueByCode("D0272",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setxRaysBWSFL(fetchValueByCode("D0272",driver,lastrowunder2ndcolumn,false,true,true));//28
		else fetchValueByCode("",driver,"",false,true,true);
		
			
		v =fetchValueByCode("D0230",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setxRaysPAD0230FL(fetchValueByCode("D0230",driver,lastrowunder2ndcolumn,false,true,true));//30
		else fetchValueByCode("",driver,"",false,true,true);
		
		v =fetchValueByCode("D0210",driver,inNetworkCoinsurance,true,false,false);
		dtemp.setFmxPer(v);//120
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setxRaysFMXFL(fetchValueByCode("D0210",driver,lastrowunder2ndcolumn,false,true,true));//31
		else fetchValueByCode("",driver,"",false,true,true);
		//32 missing
		
		
		v =fetchValueByCode("D01208",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setFlourideD1208FL(fetchValueByCode("D01208",driver,lastrowunder2ndcolumn,false,true,false));//33
		
		v =fetchValueByCode("D1208",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setFlourideAgeLimit(fetchValueByCode("D1208",driver,uptoAge,false,true,true));//34
		else fetchValueByCode("",driver,"",false,true,true);
		
		v =fetchValueByCode("D1206",driver,inNetworkCoinsurance,false,false,false);
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setVarnishD1206FL(fetchValueByCode("D1206",driver,lastrowunder2ndcolumn,false,true,false));//35
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setVarnishD1206AgeLimit(fetchValueByCode("D1206",driver,uptoAge,false,true,true));//36
		else fetchValueByCode("",driver,"",false,true,true);
		
		v=fetchValueByCode("D1351",driver,inNetworkCoinsurance,true,false,false);
		dtemp.setSealantsD1351Percentage(v);//37
		if (!v.equals("0"))dtemp.setSealantsD1351FL(fetchValueByCode("D1351",driver,lastrowunder2ndcolumn,false,true,false));//38

		if (!v.equals("0"))dtemp.setSealantsD1351AgeLimit(fetchValueByCode("D1351",driver,uptoAge,false,true,false));//39
		
		
		String th =fetchValueByCode("D1351",driver,otherLimitations,false,true,false);
		
		if (!th.contains(Constants.SCRAPPING_ISSUE_FETCHING)) {
			dtemp.setSealantsD1351PrimaryMolarsCovered(FreqencyUtils.checkForteehIntext(siteName, th, "A,B,I,J,K,L,S,T"));//40
			dtemp.setSealantsD1351PrimaryMolarsCovered(FreqencyUtils.checkForteehIntext(siteName, th, "4,5,12,13,20,21,28,29"));//41
			dtemp.setSealantsD1351PermanentMolarsCovered(FreqencyUtils.checkForteehIntext(siteName, th, "2,3,14,15,18,19,30,31"));//42
				
			
		}
		
		dtemp.setpAXRaysSubDed(fetchValueByCode("D1351",driver,inNetworkDeductible,true,true,true));//117

		//4 May
		
		dtemp.setProphyD1120FL(fetchValueByCode("D1120",driver,lastrowunder2ndcolumn,false,false,true));//44
		//45 missing
		dtemp.setsRPD4341Percentage(fetchValueByCode("D4341",driver,inNetworkCoinsurance,true,false,false));//46
		dtemp.setsRPD4341FL(fetchValueByCode("D4341",driver,lastrowunder2ndcolumn,false,true,true));//47
		//48
		//49
		dtemp.setPerioMaintenanceD4910Percentage(fetchValueByCode("D4910",driver,inNetworkCoinsurance,true,false,false));//50
		dtemp.setPerioMaintenanceD4910FL(fetchValueByCode("D4910",driver,lastrowunder2ndcolumn,false,true,false));//51
		v = fetchValueByCode("D4910",driver,lastrowunder2ndcolumnNext,true,true,true);
		dtemp.setPerioMaintenanceD4910AltWProphyD0110(FreqencyUtils.checkForteehIntext(siteName, v, "D1110,D1120"));//52
		
		v =fetchValueByCode("D4355",driver,inNetworkCoinsurance,true,false,false);
		dtemp.setFMDD4355Percentage(v);//53
		//It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))dtemp.setfMDD4355FL(fetchValueByCode("D4355",driver,lastrowunder2ndcolumn,false,true,false));//54
			
		dtemp.setGingivitisD4346Percentage(fetchValueByCode("D4355",driver,inNetworkCoinsurance,true,true,false));//55
		dtemp.setGingivitisD4346FL(fetchValueByCode("D4355",driver,lastrowunder2ndcolumn,false,true,true));//56
		
		dtemp.setNitrousD9230Percentage(fetchValueByCode("D9230",driver,inNetworkCoinsurance,true,false,true));//57
	    dtemp.setiVSedationD9243Percentage(fetchValueByCode("D9243",driver,inNetworkCoinsurance,true,false,true));//58
	    dtemp.setiVSedationD9248Percentage(fetchValueByCode("D9248",driver,inNetworkCoinsurance,true,false,true));//59
	    dtemp.setExtractionsMinorPercentage(fetchValueByCode("D7140",driver,inNetworkCoinsurance,true,false,true));//60
	    dtemp.setExtractionsMajorPercentage(fetchValueByCode("D7210",driver,inNetworkCoinsurance,true,false,true));//61
	    dtemp.setCrownLengthD4249Percentage(fetchValueByCode("D4249",driver,inNetworkCoinsurance,true,false,false));//62
	    dtemp.setCrownLengthD4249FL(fetchValueByCode("D4249",driver,lastrowunder2ndcolumn,false,true,true));//63
	    //64
	    dtemp.setAlveoD7311FL(fetchValueByCode("D7311",driver,lastrowunder2ndcolumn,false,false,true));//65
	    //66
	    dtemp.setAlveoD7311FL(fetchValueByCode("D7310",driver,lastrowunder2ndcolumn,false,false,true));//67
	    
	    dtemp.setCompleteDenturesD5110D5120FL(fetchValueByCode("D5110",driver,lastrowunder2ndcolumn,false,false,true));//68
	    dtemp.setImmediateDenturesD5130D5140FL(fetchValueByCode("D5130",driver,lastrowunder2ndcolumn,false,false,true));//69
	    dtemp.setPartialDenturesD5213D5214FL(fetchValueByCode("D5213",driver,lastrowunder2ndcolumn,false,false,true));//70
	    dtemp.setInterimPartialDenturesD5214FL(fetchValueByCode("D5214",driver,lastrowunder2ndcolumn,false,false,true));//71
	    //72
	    dtemp.setBoneGraftsD7953FL(fetchValueByCode("D7953",driver,lastrowunder2ndcolumn,false,false,true));//73 
		
	    dtemp.setImplantCoverageD6010Percentage(fetchValueByCode("D6010",driver,inNetworkCoinsurance ,true,false,true));//74
	    dtemp.setImplantsFrD6010(fetchValueByCode("D6010",driver,lastrowunder2ndcolumn ,false,true,true));//110
	    
	    dtemp.setImplantCoverageD6057Percentage(fetchValueByCode("D6057",driver,inNetworkCoinsurance ,true,false,true));//75
	    dtemp.setImplantsFrD6057(fetchValueByCode("D6057",driver,lastrowunder2ndcolumn ,false,true,true));//111
	    
	    dtemp.setImplantCoverageD6190Percentage(fetchValueByCode("D6190",driver,inNetworkCoinsurance ,true,false,true));//76
	    dtemp.setImplantsFrD6190(fetchValueByCode("D6190",driver,lastrowunder2ndcolumn ,false,true,true));//113
		   
	    dtemp.setImplantSupportedPorcCeramicD6065Percentage(fetchValueByCode("D6065",driver,inNetworkCoinsurance ,true,false,true));//77
	    dtemp.setImplantsFrD6065(fetchValueByCode("D6065",driver,lastrowunder2ndcolumn ,false,true,true));//112
	   
	    dtemp.setPostCompositesD2391Percentage(fetchValueByCode("D2391",driver,inNetworkCoinsurance ,true,false,false));//78
	    dtemp.setPostCompositesD2391FL(fetchValueByCode("D2391",driver,lastrowunder2ndcolumn,false,true,true));//79
	    //80
	    //83
	    //84
	    dtemp.setD9310Percentage(fetchValueByCode("D9310",driver,inNetworkCoinsurance ,true,false,false));//85
	    dtemp.setD9310FL(fetchValueByCode("D9310",driver,lastrowunder2ndcolumn,false,true,true));//86
				
	    dtemp.setBuildUpsD2950Covered(fetchValueByCode("D2950",driver,inNetworkCoinsurance ,true,false,false));//87
	    dtemp.setBuildUpsD2950FL(fetchValueByCode("D2950",driver,lastrowunder2ndcolumn,false,true,true));//88
        //89
	    dtemp.setOrthoPercentage(fetchValueByCode("D8090",driver,inNetworkCoinsurance ,true,false,false));//90
	    dtemp.setOrthoAgeLimit(fetchValueByCode("D8090",driver,uptoAge ,false,true,true));//92
        // 93 //94 //95 //96 //97 //99 //100 //101
	    
	    dtemp.setBridges1(fetchValueByCode("D6245",driver,inNetworkCoinsurance ,true,false,false));//102
	    dtemp.setBridges2(fetchValueByCode("D6245",driver,lastrowunder2ndcolumn,false,true,true));//103
        //104
	    dtemp.setDen5225Per(fetchValueByCode("D5225",driver,inNetworkCoinsurance ,true,false,false));//105
	    dtemp.setDenf5225FR(fetchValueByCode("D5225",driver,lastrowunder2ndcolumn ,false,true,true));//107
	    
	    dtemp.setDen5226Per(fetchValueByCode("D5226",driver,inNetworkCoinsurance ,true,false,false));//106
	    dtemp.setDenf5226Fr(fetchValueByCode("D5226",driver,lastrowunder2ndcolumn ,false,true,true));//108
	    
	    dtemp.setNightGuardsD9945Percentage(fetchValueByCode("D9945",driver,inNetworkCoinsurance ,true,false,true));//114
	    dtemp.setNightGuardsD9945Fr(fetchValueByCode("D9945",driver,lastrowunder2ndcolumn ,false,true,true));//122
	    
		openSideBarFirst(driver,"Benefit Information");
		dtemp.setEndodonticsPercentage(fetchBenefitInformation("Endodontics",driver,benefitInNetwork,true,true));//9
		dtemp.setEndoSubjectDeductible(MessageUtil.getTEXTNAORYES(fetchBenefitInformation("Endodontics",driver,benefitInDeductible,true,true)));//10
		dtemp.setPerioSurgeryPercentage(fetchBenefitInformation("Periodontal Surgery",driver,benefitInNetwork,true,true));//11
		dtemp.setPerioSurgerySubjectDeductible(MessageUtil.getTEXTNAORYES(fetchBenefitInformation("Periodontal Surgery",driver,benefitInDeductible,true,true)));//12
		
		dtemp.setBasicWaitingPeriod(MessageUtil.getTEXTSatisfied(fetchBenefitInformation("Composite Fillings",driver,benefitWaitingPeriod,true,true)));//20 in DOC
		dtemp.setMajorWaitingPeriod(MessageUtil.getTEXTSatisfied(fetchBenefitInformation("Crowns",driver,benefitWaitingPeriod,true,true)));//21 in DOC
		 
		dtemp.setOrthoMax(fetchBenefitInformation("Orthodontics",driver,benefitMaximumLifeTime,false,true));//91
		dtemp.setOrthoRemaining(fetchBenefitInformation("Orthodontics",driver,benefitMaximumLifeTimeRem,false,false));//115
		dtemp.setOrthoWaitingPeriod(MessageUtil.getTEXTSatisfied(fetchBenefitInformation("Orthodontics",driver,benefitWaitingPeriod,true,false)));//116
	    
		
		dtemp.setDiagnosticSubDed(MessageUtil.getTEXTNAORYES(fetchBenefitInformation("Oral Exams",driver,benefitInDeductible,true,true)));//109
		
		dtemp.setPreventiveSubDed(MessageUtil.getTEXTNAORYES(fetchBenefitInformation("Amalgam Fillings",driver,benefitInDeductible,true,true)));//118
		//119
		//123 //124 //125 //126
	    
		
		
		
		dtemp.setAptDate("");

	}

	private void openSideBarFirst(WebDriver driver,String sideBarName) throws InterruptedException{
		Thread.sleep(5000);
		List<WebElement> lis = driver.findElements(By.tagName("a"));
		 try {
		   for(WebElement li:lis) {
			   if (li.getText().equals(sideBarName)) {
				   Thread.sleep(2000);
				   li.click();
			   }

		   }
		 }catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void fetchHistoryformation(WebDriver driver,Set<PatientHistoryTemp> setHis) throws InterruptedException{
	 Thread.sleep(2000);	
	 WebElement ph=driver.findElement(By.id("procedure-history"));
	 Thread.sleep(5000);	
	 System.out.println("111");
	 WebElement canvas =ph.findElement(By.className("ui-grid-canvas"));
	 Thread.sleep(5000);
	 System.out.println("222");
	 List<WebElement> rows=	canvas.findElements(By.className("ui-grid-row"));
	 Thread.sleep(2000);
	 System.out.println("33");
	 System.out.println(rows);
	 System.out.println(rows.size());
	 
	 for(WebElement row:rows) {
		 try {
		 PatientHistoryTemp t= new PatientHistoryTemp(); 
		 List<WebElement> vals=row.findElements(By.className("ui-grid-cell-contents"));
		 System.out.println("99999");
				 if (vals.size()>=1) t.setHistoryCode(vals.get(0).getText());
		 //if (vals.size()>=2) t.setHistoryDOS(historyDOS);HistoryCode(vals.get(1).getText());
		 if (vals.size()>=3) {
			 String[] s=vals.get(2).getText().trim().split("/");
			 
			 //t.setHistoryDOS((s[0].length()==2?s[0]:"0"+s[0])+"/"+(s[1].length()==2?s[1]:"0"+s[1])+"/"+(s[2]));
			 try {
				 t.setHistoryDOS(s[2]+"-"+(s[0].length()==2?s[0]:"0"+s[0])+"-"+(s[1].length()==2?s[1]:"0"+s[1]));
					 
			 }catch (Exception e) {
				// TODO: handle exception
				 t.setHistoryDOS(vals.get(2).getText());
			}
		 }
		 t.setHistoryTooth("");
		 t.setHistorySurface("");
		 
		 if (vals.size()>=1) setHis.add(t);
	
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
    System.out.println("retttt");
    
	}	
	private String fetchBenefitInformation(String name, WebDriver driver,String type,boolean mandatory,boolean subsectionOPen) throws InterruptedException{
		System.out.println("fetchBenefitInformation"+ name);
		
		String value=Constants.SCRAPPING_NOT_FOUND;
		WebElement togggle=null;
		if (mandatory) value=value+ ". "+ Constants.SCRAPPING_MANDATORY_WARNING;
		else value="";
		Thread.sleep(1000);
			 try {
			    		List<WebElement> divForNames =driver.findElements(By.className("benefit-category-data"));
						for(WebElement divForName:divForNames) {
						WebElement span =divForName.findElements(By.tagName("span")).get(0);
						String spanText=span.findElements(By.tagName("div")).get(0).getText().trim();
						if (spanText.equals(name)){
						if (type.equals(benefitInNetwork))	{
						value =span.findElements(By.className("text-center")).get(6).getText().replace("%","");
						break;
						}
						else if (type.equals(benefitInDeductible )) { 
							value =span.findElements(By.className("text-center")).get(7).getText().replace("%","");
						    break;
						} else if (type.equals(benefitWaitingPeriod)) {
							
							togggle=span.findElements(By.tagName("div")).get(0);
							if (true) {
								togggle.click();
								
							     Thread.sleep(3000);
							}
							
							value = divForName.findElement(By.tagName("dd")).getText();
							break;
						} else if (type.equals(benefitMaximumLifeTime)) {
							
							togggle=span.findElements(By.tagName("div")).get(0);
							if (true) {
								togggle.click();
							     Thread.sleep(3000);
							}
							WebElement table = divForName.findElement(By.tagName("table"));
							List<WebElement> trs = table.findElements(By.tagName("tr"));
							for(WebElement tr :trs) {
								//System.out.println(tr.getText());
								if (tr.getText().contains("Maximums - Lifetime")) {//
									value=	tr.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",", "");
									break;
								}
							//	
							}
							
							break;
						} else if (type.equals(benefitMaximumLifeTimeRem)) {
							
							togggle=span.findElements(By.tagName("div")).get(0);
							if (true) {
								togggle.click();
							     Thread.sleep(3000);
							}
							WebElement table = divForName.findElement(By.tagName("table"));
							List<WebElement> trs = table.findElements(By.tagName("tr"));
							for(WebElement tr :trs) {
								//System.out.println(tr.getText());
								if (tr.getText().contains("Maximums - Lifetime Remaining")) {
									value=	tr.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",", "");
									break;
								}
							//	
							}
						}
					 break;	
					}
				 }
			   
			 }catch (Exception e) {
				 e.printStackTrace();
				 return value+" "+Constants.SCRAPPING_ISSUE_FETCHING;
			}
			 if (togggle!=null) togggle.click();
		return value;
	}
	
	/**
	 * 
	 * @param code
	 * @param driver
	 * @param type
	 * @param mandatory
	 * @param directoryValues mean Procedure code is already Clicked
	 * @return
	 * @throws InterruptedException
	 */
	private String fetchValueByCode(String code, WebDriver driver,String type,boolean mandatory,boolean directValues,boolean close) throws InterruptedException {
		System.out.println("fetchValueByCode"+ code);
		String value=Constants.SCRAPPING_NOT_FOUND;
		if (mandatory) value=value+ ". "+ Constants.SCRAPPING_MANDATORY_WARNING;
		if (!directValues) {
			Thread.sleep(8000);
			try {
			WebElement pcode= driver.findElement(By.id("procedureCode"));
			pcode.sendKeys("");
			pcode.sendKeys(code);
			WebElement but=driver.findElement(By.xpath("//*[@id=\"procedure-lookup\"]/div/div[2]/form/div[3]/div/div/button"));
			but.click();
			}catch (Exception e) {
				return value+" "+Constants.SCRAPPING_ISSUE_FETCHING;
				// TODO: handle exception
			}
			//check for code existence
			try {
				if (driver.findElement(By.xpath("//*[@id=\"procedure-lookup\"]/div/div[2]/form/div[2]")).getText().equals("Please enter a valid procedure code"))
					return Constants.SCRAPPING_ISSUE_FETCHING_CODE;
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println("HHHHHHHHHHHHHHH"+ code);
			Thread.sleep(5000);
		}
		value=Constants.SCRAPPING_MAIN_CONDTION_MET;
		try {
		if (type.equals(inNetworkCoinsurance)){
		WebElement div = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div/div[3]/div[1]/div[2]/dl/div"));
		value =div.findElements(By.tagName("dd")).get(0).getText().replace("%","");
		
		}else if (type.equals(inNetworkDeductible)) {
			WebElement div =driver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div/div[3]/div[1]/div[2]/dl/div"));
			value =div.findElements(By.tagName("dd")).get(1).getText();
			
		}else if (type.equals(lastrowunder2ndcolumn)) {
			WebElement div =driver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[2]"));
			try {
			value =FreqencyUtils.convertFrequecyString(siteName,div.findElements(By.tagName("dt")).get(0).getText());
			}catch (Exception e) {
				value="";
			}
			
		}else if (type.equals(lastrowunder2ndcolumnNext)) {
			WebElement div =driver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[2]"));
			try {
			value =div.findElements(By.tagName("dd")).get(0).getText();
			}catch (Exception e) {
				value="";
			}
			
		}else if (type.equals(uptoAge)) {
			WebElement div =driver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[1]/dl"));
			value =MessageUtil.removeUptoAge(siteName,div.findElements(By.tagName("dt")).get(0).getText());
			
		}else if (type.equals(otherLimitations)) {
			WebElement dd =driver.findElement(By.xpath("/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[1]/dl/span[3]/dd"));
			if (dd.getText().startsWith("Limited to teeth"))
			value =MessageUtil.removeLimitedToteeth(siteName,dd.getText());
			else value =Constants.SCRAPPING_ISSUE_FETCHING;
			
		}
	   }catch(Exception x) {
		   return value+" "+Constants.SCRAPPING_ISSUE_FETCHING;
	   }
		//Close Button
		if (close) {
			try {
				driver.findElement(By.xpath("/html/body/div[1]/div/div/div[4]/div/div/div/button")).click();
				
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return value; 
		
	}
	
	
	private void navigatetoUrl(WebDriver driver, String url, int sec)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		driver.navigate().to(url);
		Thread.sleep(sec);

	}

	private boolean navigatetoMainSite(WebDriver driver, boolean navigate)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		if (!navigate)
			return navigate;
		List<WebElement> eles=driver.findElements(By.className("myToolsTable"));
		eles.get(0).findElement(By.tagName("a")).click();
		Thread.sleep(5000);
		return true;
	}

	public boolean loginToSiteDelta(ScrappingFullDataDetailDto dto, WebDriver driver)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		boolean navigate = true;
		driver.get(dto.getSiteUrl());
		Thread.sleep(4000);// Need to keep this number high for Linux issue.
		WebElement userNameElement = driver.findElement(By.id("username"));
		userNameElement.sendKeys(dto.getUserName());
		WebElement passwordElement = driver.findElement(By.id("password"));

		passwordElement.sendKeys(dto.getPassword());
		WebElement loginButonElement = driver.findElement(
				By.id("loginButton"));

		loginButonElement.click();
		Thread.sleep(5000);// Need to keep this number high for Linux issue.
		try {
			WebElement p = driver
					.findElement(By.id("errorMsgSpan"));
			if (p.getText().startsWith("Username or password entered is not valid.")) {
				navigate = false;
			}
		} catch (Exception p) {

		}

		// System.out.println(p.getText());
		return navigate;
	}

	/**
	 * Link:  https://www.deltadentalins.com/
       Username:  Crosbyfamilydental
       Password:  Crosby000
	 * 
	 * @param main
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws FailingHttpStatusCodeException
	 */

	public static void main(String[] main)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		System.out.println("ssda");

		String g = "[{Deepak$,.[{";
	try {
		System.out.println(g.replaceFirst("\\[\\{", "{"));
	}catch (Exception e) {
		e.printStackTrace();
	}
		//System.out.println(g.replace(",", ""));
		//System.out.println(g.replace(".", ""));

		
		// ScrappingSiteDetails d= new ScrappingSiteDetails();
		ScrappingSiteDetailsFull f = new ScrappingSiteDetailsFull();
		ScrappingSiteFull fu = new ScrappingSiteFull();
		fu.setSiteName("Detla Dental");
		f.setScrappingSite(fu);
		f.setProxyPort("9500");
		// d.setGoogleSheetId("");
		ScrappingFullDataDetailDto dto = new ScrappingFullDataDetailDto();
		dto.setPassword("Crosby000");
		dto.setUserName("Crosbyfamilydental");

		PatientScrapSearchDto psc = new PatientScrapSearchDto();
		List<PatientScrapSearchDto> l = new ArrayList<>();
		psc.setDob("03/20/1992");
		psc.setFirstName("Heather");
		psc.setLastName("Griffith");
		psc.setMemberId("1125727908");//632307605
		psc.setSsnNumber("");

		l.add(psc);
		// dto.setPassword("Smile123");
		dto.setDto(l);
		dto.setSiteUrl("https://www.deltadentalins.com/");
		DeltaDentalServiceImpl i = new DeltaDentalServiceImpl(null,null,f,dto,null,null);
		i.setProps("9500");
		//i.scrappingSiteDetails = f;
		// dto.setUserName("crosbyfd07");
		i.scrapSite(f, dto,null,null);

		/*
		 * d.setGoogleSheetName(googleSheetName); d.setOffice(office);
		 * 
		 * i.scrapSite(d, mapData);
		 */
	}

}
