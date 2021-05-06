package com.tricon.ruleengine.service.scrapfull.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.PatientScrapSearchDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.scrapping.UnitedConLimitationDto;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.PatientDetailTemp;
import com.tricon.ruleengine.model.db.PatientHistoryTemp;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagment;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DateUtils;
import com.tricon.ruleengine.utils.FreqencyUtils;
import com.tricon.ruleengine.utils.MessageUtil;

public class UnitedConcordiaImpl extends BasefullScrapImpl implements Callable<Boolean> {

	// document :
	// https://docs.google.com/spreadsheets/d/1-3PVTrzgSl0n3OEY7Qt0JZ_WDK7twIQWU3Hk7UjSdBM/edit#gid=1557872290
	/*static {

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

	}*/

	private static String benefitProcCopy = "Coverage % or Copay $";
	private static String benefitProAppliedtoded = "Applied to Deductbile";
	private static String benefitProcLimitation = "Limitation";
	private static String benefitProcLimitationSentence="Sentence Under 14 Years Of Age";
	private static String benefitProcLimitationSentencePrimayMolar="Primary & Molars";
	private static String benefitProcLimitationSentencePermanentMolar="Permanent & Molars";
	private static String benefitProcLimitationSentenceCombinationRoutine="In Combination with Routine Cleanings";
	private static String benefitProcLimitationSentenceAlternateBenefitProvision="Alternate Benefit Provision";
	
	// private static String referenceId;
	// private static String procedureData;

	public UnitedConcordiaImpl(PatientDao patDao, ScrapingFullDataDoa dataDoa,
			ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto, User user, Office office,
			int processId, String taxId,IVFormType ivFormType, String driverLocation) {

		this.patDao = patDao;
		this.dataDoa = dataDoa;
		this.scrappingSiteDetails = scrappingSiteDetails;
		// this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		// this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		// this.mapData=mapData;
		// this.updateSheet=updateSheet;
		this.dto = dto;
		this.office = office;
		siteName = dto.getSiteName();
		this.user = user;
		this.driverLocation = driverLocation;
		this.processId = processId;
		this.taxId = taxId;
        this.ivFormType=ivFormType;
		// store parameter for later user
	}

	public String scrapSite(ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto, User user,
			Office office) {
		// WebDriver driver = null;
		setProps(scrappingSiteDetails.getProxyPort());
		System.out.println("MEM 1-" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

		try {
			// driver = new HtmlUnitDriver(true);
			// driver = getBrowserDriver();// new HtmlUnitDriver(true);//
			// getBrowserDriver();

			// System.out.println("MEM 2-"+(Runtime.getRuntime().totalMemory() -
			// Runtime.getRuntime().freeMemory()));

			// boolean navigate = loginToSiteBCBS(dto, driver);
			System.out.println("MEM 3-" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

			try {
				for (PatientScrapSearchDto data : dto.getDto()) {
					Thread thread = new Thread() {
						public void run() {
							String s = "0";
							System.out.println("Thread Running");
							WebDriver driver = getBrowserDriver();// new HtmlUnitDriver(true);// getBrowserDriver();
							try {
								boolean navigate = loginToSiteUnitedCon(dto, driver);
								boolean issueNo = navigatetoMainSite(driver, navigate);
								System.out.println("issueNo"+issueNo);
								 System.out.println("MEM 4-"
								 + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
								PatientTemp d = parsePage(driver, data, siteName, issueNo, office);
								 System.out.println("888888888888 -END " + d);
								if (d != null) {
									// Update the Data in Database
									s = updateDatainDB(d, office, user) + "";
								}
								Thread.sleep(2000);
							} catch (Exception dri) {

							} finally {
								driver.close();
								driver.quit();
								scrappingSiteDetails.setRunning(false);
								System.out.println("EE DATE =+"+new Date());
								ScrappingFullDataManagment manage = dataDoa.getScrappingFullDataManagmentData();
								ScrappingFullDataManagmentProcess manageP = dataDoa
										.getScrappingFullDataManagmentDataProcess(processId);
								String os = manageP.getStatus();
								if (os.equals(""))
									manageP.setStatus(s);
								else {
									manageP.setStatus(os + "," + s);
								}
								manageP.setCount(manageP.getCount() - 1);
								manageP.setUpdatedBy(user);
								manageP.setUpdatedDate(new Date());
								dataDoa.updateScrappingFullDataManagmentProcess(manageP);
								if (manage.getProcessCount() > 0) {
									manage.setProcessCount(manage.getProcessCount() - 1);
									dataDoa.increasecrapCount(manage);
								}

								dataDoa.updateScrappingDetailsById(scrappingSiteDetails);
							}						}
					};

					thread.start();
					Thread.sleep(10000);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// scrappingSiteDetails.get
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}

		}
		return "done";
	}

	@Override
	public Boolean call() throws Exception {
		scrapSite(scrappingSiteDetails, dto, user, office);
		return true;
	}

	private PatientTemp parsePage(WebDriver driver, PatientScrapSearchDto sh, String webSiteName, boolean issueNo,
			Office office) throws InterruptedException {
		PatientTemp temp = new PatientTemp();
		temp.setDob(sh.getDob());
		temp.setPatientId(DateUtils.createPatientIdByDate(sh.getPatientId()));
		// temp.setFirstName(sh.getFirstName());
		temp.setWebsiteName(webSiteName);
		// temp.setLastName(sh.getLastName());
		temp.setOffice(office);
		temp.setGradePay(sh.getGradePay());
		temp.setStatus("Parsing Start");
		if (!issueNo) {

			temp.setStatus("Check Password.");
		}
		PatientDetailTemp t = new PatientDetailTemp();
		t.setPatient(temp);
		t.setOffice(office);
		t.setMemberId(sh.getMemberId().trim());
		t.setMemberSSN(sh.getSsnNumber().trim());
        t.setiVFormType(ivFormType);
		Set<PatientDetailTemp> s = new HashSet<>();
		s.add(t);
		temp.setPatientDetails(s);
		if (!issueNo)
			return temp;
		issueNo = searchPatient(driver, temp, sh.getMemberId(), sh.getSsnNumber(), sh.getDob());
		if (!issueNo) {
			temp.setStatus("Patient Not found.." + sh.getDob() + " " + sh.getMemberId() + " " + sh.getSsnNumber());
			temp.setFirstName(sh.getFirstName());
			temp.setLastName(sh.getLastName());

			return temp;
		}
		temp.setStatus("Patient found..");
		populateMandatoryData(temp);
		createPatientDetailSetup(driver, temp, sh);
		// Logic to fetch data from Site...

		return temp;
	}

	private Integer updateDatainDB(PatientTemp data, Office office, User user) {
		Integer i = 0;
		try {
			i = patDao.savePatientTempDataWithDetailsAndHistory(data, office, user);
		} catch (Exception c) {
			// System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIII");
			StringWriter sw = new StringWriter();
			c.printStackTrace(new java.io.PrintWriter(sw));
			// String exceptionAsString = sw.toString();
			data.setStatus(data.getStatus() + "-- " + sw.toString());
			try {
				patDao.updatePatientTempDataOnly(data);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return i;
	}

	private void createPatientDetailSetup(WebDriver driver, PatientTemp temp, PatientScrapSearchDto sh)
			throws InterruptedException {
		Thread.sleep(13000);
		try {
			String id = sh.getMemberId().trim().equals("") ? sh.getSsnNumber().trim() : sh.getMemberId().trim();
			// Check For name...
			WebElement eles = driver.findElement(By.id("memberName"));
			try {
				String[] name = eles.getText().split(" ");
				String lname = "";
				for (int x = 0; x < name.length; x++) {
					if (x == 0)
						temp.setFirstName(name[x]);
					else
						lname = lname + " " + name[x];

				}
				temp.setLastName(lname);
			} catch (Exception p) {
				temp.setFirstName("");
				temp.setLastName("");
			}

			boolean carryOn = true;
			if (!sh.getFirstName().trim().equals("")
					&& !sh.getFirstName().trim().toLowerCase().equals(temp.getFirstName().trim().toLowerCase())) {
				carryOn = false;
				temp.setFirstName(sh.getFirstName());
				temp.setStatus(temp.getStatus() + " First name mismatch issue " + sh.getFirstName() + "-- "
						+ temp.getFirstName() + "or (ssn/member id) " + id + "issue");
			}
			if (!sh.getLastName().trim().equals("")
					&& !sh.getLastName().trim().toLowerCase().equals(temp.getLastName().trim().toLowerCase())) {
				temp.setLastName(sh.getLastName());
				carryOn = false;
				temp.setStatus(temp.getStatus() + " Last name mismatch issue " + sh.getLastName() + " -- "
						+ temp.getLastName() + "or (ssn/member id) " + id + "issue");
			}

			fetchPatDetails(driver, temp);
			// https://www.dnoaconnect.com/members?dateOfBirth=1985-12-02&subscriberId=830794722
			// String url=dobA[2]+"%2F"+dobA[0]+"%2F"+dobA[1];
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	private boolean searchPatient(WebDriver driver, PatientTemp temp, String memberid, String ssn, String dob)
			throws InterruptedException {

		String id = memberid.trim().equals("") ? ssn.trim() : memberid.trim();
		if (id.equals("") || dob.equals(""))
			return false;
		WebElement sub = driver.findElement(By.id("search:search1"));
		WebElement db = driver.findElement(By.id("search:search2"));
		WebElement validate = driver.findElement(
				By.xpath("/html/body/div/div[2]/div/div/div/div/nav/div/form/div/div[3]/table/tbody/tr/td/input[1]"));
		sub.sendKeys(id);
		db.sendKeys(dob);
		try {
			validate.click();
		} catch (Exception e) {
			return false;
		}
		Thread.sleep(4000);
		try {
			WebElement pTag = driver.findElement(By.id("searchNoResults"));
			if (pTag != null) {
				if (pTag.getText().contains("We're sorry but no results are found")) {
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			WebElement pTag = driver.findElement(By.id("searchOutdatedId"));
			if (pTag != null) {
				if (pTag.getText().contains("The Member ID you searched is outdated.")) {
					driver.findElement(By.id("frmOtherId:otherId")).click();
					Thread.sleep(3000);
					return true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;

	}

	private void populateMandatoryData(PatientTemp temp) {
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();

		dtemp.setPlanAnnualMax(Constants.SCRAPPING_MANDATORY_WARNING); // 1
		dtemp.setPlanAnnualMaxRemaining(Constants.SCRAPPING_MANDATORY_WARNING);// 2
		dtemp.setPlanIndividualDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 3
		dtemp.setPlanIndividualDeductibleRemaining(Constants.SCRAPPING_MANDATORY_WARNING);// 4
		//dtemp.setBasicPercentage();// 5
		dtemp.setBasicSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 6
		//dtemp.setMajorPercentage();// 7
		dtemp.setMajorSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 8
		//dtemp.setEndodonticsPercentage();// 9
		dtemp.setEndoSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 10

		//dtemp.setPerioSurgeryPercentage();// 11
		dtemp.setPerioSurgerySubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 12

		//dtemp.setPreventivePercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 13
		//dtemp.setDiagnosticPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 14
		//dtemp.setpAXRaysPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 15
		dtemp.setMissingToothClause(Constants.SCRAPPING_MANDATORY_WARNING);//16
		dtemp.setReplacementClause(Constants.SCRAPPING_MANDATORY_WARNING);//17
		
		//dtemp.setNightGuardsD9944Fr();// 19 //Cross Check

		//dtemp.setBasicWaitingPeriod();// 20 in DOC
		//dtemp.setMajorWaitingPeriod();// 21 in DOC

		// dtemp.setsSCD2930FL();//22 not mandatory
		// dtemp.setsSCD2931FL();//23 not mandatory
		// dtemp.setExamsD0120FL();//24 not mandatory
		// dtemp.setExamsD0140FL();//25 not mandatory
		// dtemp.seteExamsD0145FL("");//26 not mandatory
		// dtemp.setExamsD0150FL("");//27 not mandatory
		// dtemp.setxRaysBWSFL("");//28 not mandatory
		// dtemp.setxRaysPAD0220FL("");//29 not mandatory
		// dtemp.setxRaysPAD0230FL("");//30 not mandatory
		// dtemp.setxRaysFMXFL("");//31 not mandatory
		dtemp.setxRaysBundling(Constants.SCRAPPING_MANDATORY_WARNING);//32
		
		// 32 missing
		// dtemp.setFlourideD1208FL("");//33 not mandatory
		// dtemp.setFlourideAgeLimit("");//34 not mandatory
		// dtemp.setVarnishD1206FL("");//35 not mandatory
		// dtemp.setVarnishD1206AgeLimit("");//36 not mandatory
		//dtemp.setSealantsD1351Percentage();// 37
		// dtemp.setSealantsD1351FL("");//38
		// dtemp.setSealantsD1351AgeLimit("")//39
		//dtemp.setSealantsD1351PrimaryMolarsCovered(Constants.);// 40
		//dtemp.setSealantsD1351PrimaryMolarsCovered(Constants.);// 41
		//dtemp.setSealantsD1351PermanentMolarsCovered(Constants.);// 42
		// dtemp.setProphyD1110FL("");//43
		// dtemp.setProphyD1120FL("");//44
		// 45 missing
		//dtemp.setsRPD4341Percentage(Constants.);// 46
		// dtemp.setsRPD4341FL("");//47
		// 48
		// 49
		//dtemp.setPerioMaintenanceD4910Percentage(Constants.);// 50
		// dtemp.setPerioMaintenanceD4910FL("");//51
		//dtemp.setPerioMaintenanceD4910AltWProphyD0110(Constants.);// 52
		//dtemp.setFMDD4355Percentage(Constants.=);// 53
		// dtemp.setfMDD4355FL("");//54
		//dtemp.setGingivitisD4346Percentage(Constants.);// 55
		// dtemp.setGingivitisD4346FL("");//56
		//dtemp.setNitrousD9230Percentage(Constants.);// 57
		//dtemp.setiVSedationD9243Percentage(Constants.);// 58
		//dtemp.setiVSedationD9248Percentage(Constants.);// 59
		//dtemp.setExtractionsMinorPercentage(Constants.);// 60
		//dtemp.setExtractionsMajorPercentage(Constants.);// 61
		//dtemp.setCrownLengthD4249Percentage(Constants.);// 62
		// dtemp.setCrownLengthD4249FL("");//63
		// 64
		// dtemp.setAlveoD7311FL("");//65
		// 66
		// dtemp.setAlveoD7310FL("");//67
		// dtemp.setCompleteDenturesD5110D5120FL("");//68
		// dtemp.setImmediateDenturesD5130D5140FL("");//69
		// dtemp.setPartialDenturesD5213D5214FL("");//70
		// dtemp.setInterimPartialDenturesD5214FL("");//71
		// 72
		// dtemp.setBoneGraftsD7953FL("");//73
		//dtemp.setImplantCoverageD6010Percentage(Constants.);// 74
		//dtemp.setImplantCoverageD6057Percentage(Constants.);// 75
		//dtemp.setImplantCoverageD6190Percentage(Constants.);// 76
		//dtemp.setImplantSupportedPorcCeramicD6065Percentage(Constants);// 77
		//dtemp.setPostCompositesD2391Percentage(Constants.);// 78
		// dtemp.setPostCompositesD2391FL("");//79
		// 80
		//dtemp.setCrownsD2750D2740Percentage(Constants.);// 81
		// dtemp.setCrownsD2750D2740FL("");//82
		// 83
		// 84
		//dtemp.setD9310Percentage(Constants);// 85
		// dtemp.setD9310FL("");//86

		//dtemp.setBuildUpsD2950Covered(Constants);// 87
		// dtemp.setBuildUpsD2950FL("");//88
		// 89
		//dtemp.setOrthoPercentage();// 90
		dtemp.setOrthoMax(Constants.SCRAPPING_MANDATORY_WARNING);//91
		// dtemp.setOrthoAgeLimit("");//92 benefit
		// 93 //94 //95 //96 //97 //99 //100 //101
		//dtemp.setBridges1(Constants.SCRAPPING_MANDATORY_WARNING);// 102
		// dtemp.setBridges2("");//103
		// 104
		//dtemp.setDen5225Per(Constants.SCRAPPING_MANDATORY_WARNING);// 105
		// dtemp.setDenf5225FR("");//107

		//dtemp.setDen5226Per(Constants.SCRAPPING_MANDATORY_WARNING);// 106
		// dtemp.setDenf5226Fr("");//108
		//dtemp.setDiagnosticSubDed(Constants.SCRAPPING_MANDATORY_WARNING);// 109

		// dtemp.setImplantsFrD6010("");//110
		// dtemp.setImplantsFrD6057("");//111
		// dtemp.setImplantsFrD6065("");//112
		// dtemp.setImplantsFrD6190("");//113
		//dtemp.setNightGuardsD9945Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 114
		// dtemp.setOrthoRemaining("");//115
		// dtemp.setOrthoWaitingPeriod("");//116
		//dtemp.setpAXRaysSubDed(Constants.SCRAPPING_MANDATORY_WARNING);// 117
		//dtemp.setPreventiveSubDed(Constants.SCRAPPING_MANDATORY_WARNING);// 118
		// 119
		//dtemp.setFmxPer(Constants.SCRAPPING_MANDATORY_WARNING);// 120
		// dtemp.setNightGuardsD9944Fr("");//121
		// dtemp.setNightGuardsD9945Fr("");//122
        dtemp.setPlanAssignmentofBenefits(Constants.SCRAPPING_MANDATORY_WARNING);//131
		// 123 //124 //125 //126

		// Debug
	}

	private void fetchPatDetails(WebDriver driver, PatientTemp temp) throws InterruptedException {
		driver.manage().window().maximize();
		Thread.sleep(9000);
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();

		dtemp.setInsName("United Concordia"); //136
		dtemp.setInsContact("800-332-0366"); // 138
		dtemp.setcSRName("UCCI Scraping Tool");// 139
		dtemp.setTaxId(taxId); //137
		dtemp.setInsAddress("P.O. Box 69451. Harrisburg, PA 17106"); //142
		dtemp.setPayerId("CX007");//143
		dtemp.setPlanType("PPO");
		dtemp.setsRPD4341QuadsPerDay("2");//As per chat 6 March 2021 Anjali
		dtemp.setsRPD4341DaysBwTreatment("1");//As per chat 6 March 2021 Anjali
		
		try {
		WebElement ac= driver.findElement(By.className("memberStatusLabel"));
		if (ac.getText().contains("INACTIVE")) {
			temp.setStatus(Constants.PATIENT_NOT_ACTIVE);
			return;
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
		 
		Date cd= new Date();
		dtemp.setGeneralDateIVwasDone(Constants.SIMPLE_DATE_FORMAT_IVF.format(cd));//147 and 97

		
		List<PatientHistoryTemp> hisSet = temp.getPatientHistory();
		// openSideBarFirst(driver,"Procedure History");
		fetchHistoryformation(driver, hisSet);
		dtemp.setPlanAnnualMaxRemaining("0");//Set default as 0 //2 mand
		dtemp.setPlanIndividualDeductibleRemaining("-1");//Set -1 /Means we need to put Plan_IndividualDeductible //3
		dtemp.setOrthoRemaining("0");//Set default as Zero
		try {
	   WebElement accums=driver.findElement(By.className("accums"));
	   List<WebElement> acc= accums.findElements(By.className("panel-default"));
	   for(WebElement ac:acc) {
		   if (ac.getText().contains("PROGRAM DOLLAR MAX") || ac.getText().startsWith("Deductibles") ) {
			  if (ac.getText().contains("No deductible applied to the current")) {
				  dtemp.setPlanAnnualMaxRemaining("999999");
				  
			  }else {
				    List<WebElement> pbs =ac.findElements(By.className("panel-body"));
					  for(WebElement pb:pbs) {
						    if (pb.getText().trim().startsWith("INDIVIDUAL")) {
						    	dtemp.setPlanAnnualMaxRemaining(pb.findElement(By.tagName("strong")).getText().replace("Remaining", "")
						    	.replace("$", "").replace(",", "").trim());//2 mand
						    	break;
						    }
					  }
				  
			  }
		   }
		   if (ac.getText().contains("PROGRAM DOLLAR DED") || ac.getText().startsWith("Maximums")  ) {
				  if (ac.getText().contains("No maximum applied to the current")) {
					   dtemp.setPlanIndividualDeductibleRemaining("-1");//Means we need to put Plan_IndividualDeductible
				  }else {
					  List<WebElement> pbs =ac.findElements(By.className("panel-body"));
					  for(WebElement pb:pbs) {
						    if (pb.getText().trim().startsWith("INDIVIDUAL")) {
						    	dtemp.setPlanIndividualDeductibleRemaining(pb.findElement(By.tagName("strong")).getText().replace("Remaining", "")
						    	.replace("$", "").replace(",", "").trim());//4
						    	break;
						    }
					  }
				  }
		   }
		   if (ac.getText().contains("LIFETIME SVC DOLLAR MAX")  ) {
				  if (ac.getText().contains("No maximum applied to the current")) {
					   dtemp.setOrthoRemaining("-1");//Means we need to put Plan_IndividualDeductible
				  }else {
					  List<WebElement> pbs =ac.findElements(By.className("panel-body"));
					  for(WebElement pb:pbs) {
						    if (pb.getText().trim().startsWith("INDIVIDUAL")) {
						    	dtemp.setOrthoRemaining(pb.findElement(By.tagName("strong")).getText().replace("Remaining", "")
						    	.replace("$", "").replace(",", "").trim());//4
						    	break;
						    }
					  }
				  }
		   }
	      }
			   
	   
        }catch(Exception x) {
        	
        }
        
        String []v= fetchDeductiblesAndMaximums("Deductibles and Maximums", driver, false, false);
        dtemp.setPlanAnnualMax(v[0]);//1 mand
        
        dtemp.setPlanIndividualDeductible(v[1]);//3 mand
        
        /*
        if (dtemp.getPlanAnnualMaxRemaining().equals("0")){
		dtemp.setPlanAnnualMaxRemaining(dtemp.getPlanAnnualMax());
        }
        */
	     if (dtemp.getPlanIndividualDeductibleRemaining().equals("-1")){
		  dtemp.setPlanIndividualDeductibleRemaining(dtemp.getPlanIndividualDeductible());
	     }
	     
	     
	     dtemp.setOrthoMax(v[2]);//91 mand
	     
	     if (dtemp.getOrthoRemaining().equals("-1")){
	  		  dtemp.setOrthoRemaining(dtemp.getOrthoMax());//115 issue 
	  	 }
	        
		dtemp.setMissingToothClause(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Missing Tooth Clause", true, false, false));// 16 mand

		dtemp.setReplacementClause(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Prosthetic Prior Placement", true, true, false));// 17 mand

		dtemp.setxRaysBundling(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Accumulate Xrays", true, false, false));// 32
		
		dtemp.setPlanAssignmentofBenefits(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Assignment Of Benefits", true, true, false));// 131 mand

		
		String headingName="Restorations";
		
		 Map<String, List<UnitedConLimitationDto>> map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
				                   benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		 
		dtemp.setBasicPercentage(fetchBenefitByProcedure(headingName, new String[] { "D2391", "D2392", "D2393" },
				driver, benefitProcCopy,  false, false, temp.getGradePay(),map));// 5
		
		dtemp.setBasicSubjectDeductible(fetchBenefitByProcedure(headingName, null, driver, benefitProAppliedtoded,
				 true, false, temp.getGradePay(),map));// 6
		
		dtemp.setPostCompositesD2391Percentage(fetchBenefitByProcedure(headingName, new String[] {"D2391"}, driver, benefitProcCopy,
				 true, true, temp.getGradePay(),map));// 78
		
		dtemp.setPostCompositesD2391FL(fetchBenefitByProcedure(headingName, new String[] {"D2391"}, driver, benefitProcLimitation,
				 true, true, temp.getGradePay(),map));// 79
		
		dtemp.setPosteriorCompositesD2391Downgrade(fetchBenefitByProcedure(headingName, new String[] {"D2391"}, driver, benefitProcLimitationSentenceAlternateBenefitProvision,
				 true, true, temp.getGradePay(),map));// 80

		headingName="Dentures, Denture Adjustments, Denture Repairs, Relining";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                 benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
 		
		dtemp.setMajorPercentage(fetchBenefitByProcedure(headingName,
				null, driver, benefitProcCopy, false, false, temp.getGradePay(),map));// 7

		dtemp.setMajorSubjectDeductible(fetchBenefitByProcedure(headingName,
				null, driver, benefitProAppliedtoded, true,  false, temp.getGradePay(),map));// 8 mand

		
		dtemp.setCompleteDenturesD5110D5120FL(fetchBenefitByProcedure(headingName,
				new String[] {"D5110"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 68

		dtemp.setImmediateDenturesD5130D5140FL(fetchBenefitByProcedure(headingName,
				new String[] {"D5130"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 69

		dtemp.setPartialDenturesD5213D5214FL(fetchBenefitByProcedure(headingName,
				new String[] {"D5213"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 70

		dtemp.setInterimPartialDenturesD5214FL(fetchBenefitByProcedure(headingName,
				new String[] {"D5820"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 71

		dtemp.setDen5225Per(fetchBenefitByProcedure(headingName,
				new String[] {"D5225"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 105

		dtemp.setDen5226Per(fetchBenefitByProcedure(headingName,
				new String[] {"D5226"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 106

		dtemp.setDenf5225FR(fetchBenefitByProcedure(headingName,
				new String[] {"D5225"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 107

		dtemp.setDenf5226Fr(fetchBenefitByProcedure(headingName,
				new String[] {"D5226"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 108

		headingName="Endodontic Procedures";
		
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                 benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		dtemp.setEndodonticsPercentage(fetchBenefitByProcedure(headingName,
				null, driver, benefitProcCopy, false, false, temp.getGradePay(),map));// 9

		dtemp.setEndoSubjectDeductible(fetchBenefitByProcedure(headingName,
				null, driver, benefitProAppliedtoded, true,  false, temp.getGradePay(),map));// 10 mand

		dtemp.setD3330(fetchBenefitByProcedure(headingName,
				new String[] {"D3330"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 156

		dtemp.setD3330Freq(fetchBenefitByProcedure(headingName,
				new String[] {"D3330"}, driver, benefitProcLimitation, false,  true, temp.getGradePay(),map));// 157
		
		//
		headingName="Surgical Periodontal Services";
		
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		dtemp.setPerioSurgeryPercentage(fetchBenefitByProcedure(headingName,
				null, driver, benefitProcCopy, false,  false, temp.getGradePay(),map));// 11

		
		dtemp.setPerioSurgerySubjectDeductible(fetchBenefitByProcedure(headingName,
				null, driver, benefitProAppliedtoded, true, false, temp.getGradePay(),map));// 12 mand
		
		dtemp.setCrownLengthD4249Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D4249"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 62
		
		dtemp.setCrownLengthD4249FL(fetchBenefitByProcedure(headingName,
				new String[] {"D4249"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 63
		
		
		headingName="Cleanings & Fluoride";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setPreventivePercentage(fetchBenefitByProcedure(headingName,
				null, driver, benefitProcCopy, false, false, temp.getGradePay(),map));// 13
		
		dtemp.setFlourideD1208FL(fetchBenefitByProcedure(headingName,
				new String[] {"D1208"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 33
		
		dtemp.setFlourideAgeLimit(fetchBenefitByProcedure(headingName,
				new String[] {"D1208"}, driver, benefitProcLimitationSentence, false, true, temp.getGradePay(),map));// 34
		
		dtemp.setVarnishD1206FL(fetchBenefitByProcedure(headingName,
				new String[] {"D1206"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 35
		
		dtemp.setVarnishD1206AgeLimit(fetchBenefitByProcedure(headingName,
				new String[] {"D1206"}, driver, benefitProcLimitationSentence, false, true, temp.getGradePay(),map));// 36
		
		dtemp.setProphyD1110FL(fetchBenefitByProcedure(headingName,
				new String[] {"D1110"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 43

		dtemp.setProphyD1120FL(fetchBenefitByProcedure(headingName,
				new String[] {"D1120"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 44

		dtemp.setPreventiveSubDed(fetchBenefitByProcedure(headingName,
				null, driver, benefitProAppliedtoded, false, false, temp.getGradePay(),map));// 118
		
		headingName="Preventive Exams";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		dtemp.setDiagnosticPercentage(fetchBenefitByProcedure(headingName,
				null, driver, benefitProcCopy, false, false, temp.getGradePay(),map));// 14
		
		dtemp.setDiagnosticSubDed(fetchBenefitByProcedure(headingName,
				null, driver, benefitProAppliedtoded, false, false, temp.getGradePay(),map));// 109

		dtemp.setExamsD0120FL(fetchBenefitByProcedure(headingName,
				new String[] {"D0120"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 24
		
		dtemp.setExamsD0140FL(fetchBenefitByProcedure(headingName,
				new String[] {"D0140"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 25
		
		System.out.println("D0140"+dtemp.getExamsD0140FL());
		
		dtemp.seteExamsD0145FL(fetchBenefitByProcedure(headingName,
				new String[] {"D0145"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 26
		
		dtemp.setExamsD0150FL(fetchBenefitByProcedure(headingName,
				new String[] {"D0150"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 27

		dtemp.setPedo1(fetchBenefitByProcedure(headingName,
				new String[] {"D0160"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 148
		
		dtemp.setD0160Freq(fetchBenefitByProcedure(headingName,
				new String[] {"D0160"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 149

		
		headingName="X-rays";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setpAXRaysPercentage(fetchBenefitByProcedure(headingName,
				new String[] { "D0220"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 15

		dtemp.setxRaysBWSFL(fetchBenefitByProcedure(headingName,
				new String[] {"D0272","D0274"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 28
		
		dtemp.setxRaysPAD0220FL(fetchBenefitByProcedure(headingName,
				new String[] {"D0220"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 29

		dtemp.setxRaysPAD0230FL(fetchBenefitByProcedure(headingName,
				new String[] {"D0230"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 30
		
		dtemp.setxRaysFMXFL(fetchBenefitByProcedure(headingName,
				new String[] {"D0210"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 31
		
		dtemp.setpAXRaysSubDed(fetchBenefitByProcedure(headingName,
				new String[] {"D0220"}, driver, benefitProAppliedtoded, false, true, temp.getGradePay(),map));// 117

		dtemp.setFmxPer(fetchBenefitByProcedure(headingName,
				new String[] {"D0210"}, driver, benefitProcCopy, false, false, temp.getGradePay(),map));// 120  Look For other codes also
		
		dtemp.setPano1(fetchBenefitByProcedure(headingName,
				new String[] {"D0330"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 152

		dtemp.setD0330Freq(fetchBenefitByProcedure(headingName,
				new String[] {"D0330"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 153

		dtemp.setCrownsD2750D2740PaysPrepSeatDate("Seat Date");// 18
		
		headingName="Miscellaneous Services";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		dtemp.setNightGuardsD9940Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D9944"}, driver, benefitProcCopy, false,  true, temp.getGradePay(),map));// 19
		
		dtemp.setNightGuardsD9945Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D9945"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 114 

		dtemp.setNightGuardsD9944Fr(fetchBenefitByProcedure(headingName,
				new String[] {"D9944"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 121

		dtemp.setNightGuardsD9945Fr(fetchBenefitByProcedure(headingName,
				new String[] {"D9945"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 122

		
		headingName="Other Restorations";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setsSCD2930FL(fetchBenefitByProcedure(headingName,
				new String[] {"D2930"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 22
		
		dtemp.setsSCD2931FL(fetchBenefitByProcedure(headingName,
				new String[] {"D2931"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 23
		
		dtemp.setBuildUpsD2950Covered(fetchBenefitByProcedure(headingName,
				new String[] {"D2950"}, driver, benefitProcCopy, false, false, temp.getGradePay(),map));// 87

		dtemp.setBuildUpsD2950FL(fetchBenefitByProcedure(headingName,
				new String[] {"D2950"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 88
		
		
		dtemp.setPedo2(fetchBenefitByProcedure(headingName,
				new String[] {"D2934"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 150

		dtemp.setsSCD2930FL(fetchBenefitByProcedure(headingName,
				new String[] {"D2934"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 151
		

		
		headingName="Sealants";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		dtemp.setSealantsD1351Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D1351"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 37

		dtemp.setSealantsD1351FL(fetchBenefitByProcedure(headingName,
				new String[] {"D1351"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 38

		dtemp.setSealantsD1351AgeLimit(fetchBenefitByProcedure(headingName,
				new String[] {"D1351"}, driver, benefitProcLimitationSentence, false, true, temp.getGradePay(),map));// 39
		
		dtemp.setSealantsD1351AgeLimit(fetchBenefitByProcedure(headingName,
				new String[] {"D1351"}, driver, benefitProcLimitationSentencePrimayMolar, false, true, temp.getGradePay(),map));// 40

		//41
		dtemp.setSealantsD1351AgeLimit(fetchBenefitByProcedure(headingName,
				new String[] {"D1351"}, driver, benefitProcLimitationSentencePermanentMolar, false, true, temp.getGradePay(),map));// 42

		
		headingName="Non-Surgical Periodontal Services";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		//45

		dtemp.setsRPD4341Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D4341"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 46

		dtemp.setsRPD4341FL(fetchBenefitByProcedure(headingName,
				new String[] {"D4341"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 47

		//48 49
		dtemp.setPerioMaintenanceD4910Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D4910"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 50

		dtemp.setPerioMaintenanceD4910FL(fetchBenefitByProcedure(headingName,
				new String[] {"D4910"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 51
		
		dtemp.setPerioMaintenanceD4910AltWProphyD0110(fetchBenefitByProcedure(headingName,
				new String[] {"D4910"}, driver, benefitProcLimitationSentenceCombinationRoutine, false, true, temp.getGradePay(),map));// 52
		
		dtemp.setFMDD4355Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D4355"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 53
		
		dtemp.setfMDD4355FL(fetchBenefitByProcedure(headingName,
				new String[] {"D4355"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 54

		dtemp.setGingivitisD4346Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D4346"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 55

		dtemp.setGingivitisD4346FL(fetchBenefitByProcedure(headingName,
				new String[] {"D4346"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 56

		dtemp.setD4381(fetchBenefitByProcedure(headingName,
				new String[] {"D4381"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 154

		dtemp.setD4381Freq(fetchBenefitByProcedure(headingName,
				new String[] {"D4381"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 155

		
		headingName="Adjunctive Services";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setNitrousD9230Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D9230"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 57

		dtemp.setiVSedationD9243Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D9243"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 58


		dtemp.setiVSedationD9248Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D9248"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 59
		
		dtemp.setD9310Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D9310"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 85
		
		dtemp.setD9310FL(fetchBenefitByProcedure(headingName,
				new String[] {"D9310"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 86
       System.out.println("0000000000000000000");
       System.out.println(dtemp.getD9310FL());
		
		headingName="Oral Surgery";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setExtractionsMinorPercentage(fetchBenefitByProcedure(headingName,
				new String[] {"D7210"}, driver, benefitProcCopy, false, false, temp.getGradePay(),map));// 60
		
		dtemp.setExtractionsMajorPercentage(fetchBenefitByProcedure(headingName,
				new String[] {"D7140"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 61
		
		//64
		dtemp.setAlveoD7311FL(fetchBenefitByProcedure(headingName,
				new String[] {"D7311"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 65

		//66
		dtemp.setAlveoD7310FL(fetchBenefitByProcedure(headingName,
				new String[] {"D7311"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 67
		
		headingName="Other Repair Procedures";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
	    //72
		dtemp.setBoneGraftsD7953FL(fetchBenefitByProcedure(headingName,
					new String[] {"D7953"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 73

		headingName="Surgical Implant Procedures";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setImplantCoverageD6010Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D6010"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 74
		
		dtemp.setImplantsFrD6010(fetchBenefitByProcedure(headingName,
				new String[] {"D6010"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 110


		headingName="Implant Supported Prosthetics";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		dtemp.setImplantCoverageD6057Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D6057"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 75
		
		dtemp.setImplantCoverageD6190Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D6190"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 76
		
		dtemp.setImplantSupportedPorcCeramicD6065Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D6065"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 77
		
		dtemp.setImplantsFrD6057(fetchBenefitByProcedure(headingName,
				new String[] {"D6057"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 111
		
		dtemp.setImplantsFrD6065(fetchBenefitByProcedure(headingName,
				new String[] {"D6065"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 112
		
		dtemp.setImplantsFrD6190(fetchBenefitByProcedure(headingName,
				new String[] {"D6190"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 113
		
		
		headingName="Crowns, Inlays & Onlays";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setCrownsD2750D2740Percentage(fetchBenefitByProcedure(headingName,
				new String[] {"D2740"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 81
		
		dtemp.setCrownsD2750D2740FL(fetchBenefitByProcedure(headingName,
				new String[] {"D2750"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 82
		
		headingName="Orthodontics";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		dtemp.setOrthoPercentage(fetchBenefitByProcedure(headingName,
				new String[] {"D8090"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map));// 90
		
		dtemp.setOrthoAgeLimit(fetchBenefitByProcedure(headingName,
				new String[] {"D8080"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map));// 92
		
		dtemp.setOrthoSubjectDeductible(fetchBenefitByProcedure(headingName,
				null, driver, benefitProAppliedtoded, false, false, temp.getGradePay(),map));// 93	

		
		headingName="Fixed Prosthetics";
		
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});
		
		String b1=fetchBenefitByProcedure(headingName,
				new String[] {"D6245"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map);
		
		String b11=fetchBenefitByProcedure(headingName,
				new String[] {"D6245"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map);

		headingName="Fixed Partial Denture Retainers";
		map= fetchBenefitByProcedureMap(headingName, driver, new String[] {benefitProcCopy, benefitProAppliedtoded, benefitProcCopy, 
                benefitProcLimitation,benefitProcLimitationSentenceAlternateBenefitProvision});

		String b2=fetchBenefitByProcedure(headingName,
				new String[] {"D6740"}, driver, benefitProcCopy, false, true, temp.getGradePay(),map);//102 Check Logic once...
       
		String b22=fetchBenefitByProcedure(headingName,
				new String[] {"D6740"}, driver, benefitProcLimitation, false, true, temp.getGradePay(),map);//103
       
		String[] vals=pickLessOftwoNumber(b1, b2, "D6245", "D6740");
       dtemp.setBridges1(vals[0]);
       if (!vals[1].equals("")) {
    	   if (dtemp.getComments()==null) dtemp.setComments("");
    	   dtemp.setComments(dtemp.getComments()+" "+ vals[1]);
              
       }
    			
		
		vals =pickNotEqualsValues(b11, b22, "D6245", "D6740");
		
		dtemp.setBridges2(vals[0]); 
		System.out.println("setBridges2--"+vals[0]);
		if (!vals[1].equals("")) {
	    	   if (dtemp.getComments()==null) dtemp.setComments("");
	    	   dtemp.setComments(dtemp.getComments()+" "+ vals[1]);
	              
	     }
		
		
		

		System.out.println("RRRR");
		/* 



		

		

		
		//64
 		
		
		
		
		
		//83 84
		

		*/
		

		WebElement el = driver.findElement(By.id("memberBackground"));
		List<WebElement> divs = el.findElements(By.tagName("div"));
		for (WebElement div : divs) {
			if (div.getText() != null && div.getText().startsWith("Coverage Effective")) {
				try {
					WebElement ce = div.findElements(By.tagName("div")).get(2);
					String s[] = ce.getText().split(" - ")[0].split("/");
					
					dtemp.setPlanEffectiveDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
							+ (s[1].length() == 2 ? s[1] : "0" + s[1]));// 146

					try {
					String s1[]=(ce.getText().split(" - ")[1]).substring(0, 10)  .split("/");
					dtemp.setPlanTermedDate(s1[2] + "-" + (s1[0].length() == 2 ? s1[0] : "0" + s1[0]) + "-"
							+ (s1[1].length() == 2 ? s1[1] : "0" + s1[1]));// 145
					}catch(Exception c) {
						dtemp.setPlanTermedDate("");// 145 Just put blank
					}
					
				} catch (Exception e) {
					dtemp.setPlanEffectiveDate("");//146
				}
				break;
			}
		}
		el = driver.findElement(By.id("policyInfo"));
		divs = el.findElements(By.tagName("div"));
		for (WebElement div : divs) {
			if (div.getText() != null && div.getText().startsWith("Policyholder")) {
				try {
					String phmail = div.getText().replace("Policyholder", "").trim();
					String[] phmailpind = phmail.split("Mailing Address");
					dtemp.setPolicyHolder(phmailpind[0].trim());// 144
					// System.out.println("Mailing Address"+phmailpind[1].trim()+"-------");
				} catch (Exception e) {

				}
			}
			if (div.getText() != null && div.getText().startsWith("Your Network")) {
				try {
					String gr = div.getText().split("Group Network")[1].split("Dental Plan")[0].trim();
					dtemp.setEmployerName(gr);// 140
				} catch (Exception e) {

				}
			}
			if (div.getText() != null && div.getText().startsWith("Group / ID")) {
				try {
					String gr = div.getText().split("Covered Members")[0].replace("Group / ID", "").trim()
							.replaceAll("[a-zA-Z]", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", "")
							.trim();
					dtemp.setGroup(gr);// 141
				} catch (Exception e) {

				}
			}
		}

		dtemp.setAptDate("");
	}

	private void fetchHistoryformation(WebDriver driver, List<PatientHistoryTemp> setHis) throws InterruptedException {
		Thread.sleep(2000);
		try {
			WebElement tableB = driver.findElement(By.id("serviceHistoryPanelList:tbody_element"));
			List<WebElement> trs = tableB.findElements(By.tagName("tr"));
			PatientHistoryTemp t = null;
			for (WebElement tr : trs) {
				List<WebElement> tds = tr.findElements(By.tagName("td"));
				t = new PatientHistoryTemp();
				t.setHistoryDOS(tds.get(1).getText());
				t.setHistoryCode(tds.get(2).getText());
				t.setHistoryTooth(tds.get(3).getText());
				t.setHistorySurface(tds.get(4).getText());
				setHis.add(t);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("RRR");
	}

	private String[] fetchDeductiblesAndMaximums(String name, WebDriver driver,
			boolean subsectionOPen, boolean close) throws InterruptedException {
		System.out.println("fetchCordinationBenefit-" + name + " -" );

		String[] r= new String[3];
		r[0]=r[1]=r[2]="0";
		try {
			if (!subsectionOPen) {
				WebElement servicesGroup= driver.findElement(By.id("servicesGroup"));
				WebElement formElement = servicesGroup.findElements(By.tagName("form")).get(0);
				List<WebElement> maintables = formElement.findElements(By.tagName("table"));
				for (WebElement maintable : maintables) {
					if (maintable.getText() != null && maintable.getText().startsWith(name)) {
						maintable.findElements(By.tagName("span")).get(0).click();
						Thread.sleep(5000);
						break;
					}
				}
			}

			WebElement tab = driver.findElement(By.id("benefitPolicyInformationDeductiblesAndMaximums"));
			List<WebElement> trs = tab.findElements(By.tagName("tr"));
			for (WebElement tr : trs) {
				String y=tr.getText();
				if (y != null && y.startsWith("Individual Maximum") && y.toLowerCase().contains("ortho") && !y.toLowerCase().contains("none")) {
					String t=y.replace("Individual Maximum","").trim().split(" ")[0].replace("$","").replace(",","");
					r[2]=t;
					// Assignment Of Benefits
				}
				if (y != null && y.startsWith("Individual Maximum") && !y.toLowerCase().contains("ortho") && !y.toLowerCase().contains("none")) {
					String t=y.replace("Individual Maximum","").trim().split(" ")[0].replace("$","").replace(",","");
					r[0]=t;
					// Assignment Of Benefits
				}
				if (y != null && y.startsWith("Individual Deductible") && !y.toLowerCase().contains("none")) {
					if (tr.getText().replaceAll("Individual Deductible", "").trim().toLowerCase().contains("none")) {
						r[1]="0";	
					}else {
						r[1]=y.replaceAll("Individual Deductible", "").trim().split(" ")[0].replace("$","").replace(",","");
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		if (r[0].toLowerCase().contains("none")) r[0]="0";
		if (r[1].toLowerCase().contains("none")) r[1]="0";
		
		return r;
	}

	private String fetchCordinationBenefit(String name, WebDriver driver, String type, boolean mandatory,
			boolean subsectionOPen, boolean close) throws InterruptedException {
		System.out.println("fetchCordinationBenefit-" + name + " -" + type);

		String value = Constants.SCRAPPING_NOT_FOUND;
		//Map<String, List<UnitedConLimitationDto>> dataMap = new HashMap<>();
		// WebElement togggle = null;
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		else
			value = "";
		Thread.sleep(1000);
		try {
			if (!subsectionOPen) {
				WebElement servicesGroup= driver.findElement(By.id("servicesGroup"));
				WebElement formElement = servicesGroup.findElements(By.tagName("form")).get(0);
				List<WebElement> maintables = formElement.findElements(By.tagName("table"));
				for (WebElement maintable : maintables) {
					if (maintable.getText() != null && maintable.getText().startsWith(name)) {
						maintable.findElements(By.tagName("span")).get(0).click();
						Thread.sleep(5000);
						break;
					}
				}
			}

			WebElement tab = driver.findElement(By.id("benefitPolicyCoordinationAndOtherBenefits"));
			List<WebElement> trs = tab.findElements(By.tagName("tr"));
			value = "No";
			for (WebElement tr : trs) {
				if (tr.getText() != null && tr.getText().startsWith(type) && tr.getText().contains("Will Apply")) {
					System.out.println(tr.getText());
					value = "Yes";
					// Assignment Of Benefits
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		System.out.println("value--" + value);
		return value;
	}

	private Map<String, List<UnitedConLimitationDto>> fetchBenefitByProcedureMap(String name, WebDriver driver,String[] types
			 ) throws InterruptedException {
		System.out.println("fetchBenefitByProcedure-" + name);

		Map<String, List<UnitedConLimitationDto>> dataMap = new LinkedHashMap<>();
		// WebElement togggle = null;
		Thread.sleep(5000);
		try {
				WebElement servicesGroup= driver.findElement(By.id("servicesGroup"));
				WebElement formElement = servicesGroup.findElements(By.tagName("form")).get(1);
				//System.out.println("6");
				List<WebElement> maintables = formElement.findElements(By.tagName("table"));
				//System.out.println("7");
				for (WebElement maintable : maintables) {
					if (maintable.getText() != null && maintable.getText().startsWith(name)) {
						//System.out.println("8");
						maintable.findElements(By.tagName("span")).get(0).click();
						//System.out.println("9");
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("window.scrollBy(0,3500)");

						Thread.sleep(8000);
						break;
					}
				}
			
			//System.out.println("10");
			servicesGroup= driver.findElement(By.id("servicesGroup"));
			formElement = servicesGroup.findElements(By.tagName("form")).get(1);
			//System.out.println("11");
			maintables = formElement.findElements(By.tagName("table"));
			//System.out.println("12");
			Thread.sleep(5000);
			for (WebElement maintable : maintables) {
				if (maintable.getAttribute("id") != null
						&& maintable.getAttribute("id").equals("benefitDetailAllServiceProceduresList")
						&& !maintable.getAttribute("class").contains(" hidden")) {

						Thread.sleep(5000);
					//System.out.println("13");
					List<WebElement> trs = maintable.findElements(By.tagName("tr"));
					trs.remove(0);// remove th row
					Thread.sleep(1000);
					for (WebElement tr : trs) {
						//System.out.println("14");
						String code = tr.findElements(By.tagName("td")).get(0).getText().trim();// code
						//System.out.println("CODE map--"+code);
						
							
						//}
						try {
							//System.out.println("15");
							UnitedConLimitationDto dto = new UnitedConLimitationDto();
							// String vm="";
							if (tr.findElements(By.tagName("td")).get(2).getText().equals("Not Covered")) {
								dto.setCopay(0);//may change when testing happens
								dto.setLimitation("Not Covered");
								dto.setAppliedtoDed("");
								if (dataMap.get(code) == null) {
									List<UnitedConLimitationDto> li = new ArrayList<>();
									li.add(dto);
									dataMap.put(code, li);
								} else {
									List<UnitedConLimitationDto> li = dataMap.get(code);
									li.add(dto);
									//dataMap.put(code, li);

								}

								continue;
							}
							for(String type:types) {
							if (benefitProcCopy.equals(type)) {
								//System.out.println("16");
								try {
									dto.setCopay(Integer.parseInt(tr.findElements(By.tagName("td")).get(4).getText()
											.trim().replace("%", "")));// Co Pay
									// System.out.println("COPAY-"+dto.getCopay());
								} catch (Exception p) {
									continue;
								}
							}
							if (benefitProAppliedtoded.equals(type)) {
								//System.out.println("17");
								try {
									dto.setAppliedtoDed(tr.findElements(By.tagName("td")).get(6).getText().trim());// Applied
																													// to
																													// Deductible
									// System.out.println("setAppliedtoDed-"+dto.getAppliedtoDed());
								} catch (Exception p) {
									continue;
								}
							}
							}// for Types Loop 
							String limitation = tr.findElements(By.tagName("td")).get(5).getText().trim()
									.replace(" | More...", "").trim();// limitation
							// System.out.println("limitation-"+limitation);
							dto.setLimitation(limitation);
							if (dataMap.get(code) == null) {
								List<UnitedConLimitationDto> li = new ArrayList<>();
								li.add(dto);
								dataMap.put(code, li);
							} else {
								List<UnitedConLimitationDto> li = dataMap.get(code);
								li.add(dto);
								//dataMap.put(code, li);

							 }
							//}//for Types 
							

						} catch (Exception n) {
							n.printStackTrace();
							UnitedConLimitationDto dto = new UnitedConLimitationDto();
							dto.setCopay(-1);
							List<UnitedConLimitationDto> li = new ArrayList<>();
							li.add(dto);
							dataMap.put(code, li);
						}
					}

					break;
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return dataMap;
	   }
	
	
	private String[] pickLessOftwoNumber(String b1,String b2,String c1,String c2) {
		String[] data=new String[2];
		data[0]=data[1]="";
		if (b1==null) b1="";
		if (b2==null) b2="";
		
	       if (!b1.equals(b2)){
	   		   float f1=-1;
			   float f2=-1;
	   	       try {
	    		   f1=Float.parseFloat(b1);
	    		}catch (Exception e) {
				// TODO: handle exception
			    }
	    	   try {
	    		   f2=Float.parseFloat(b2);
	    	   }catch (Exception e) {
				// TODO: handle exception
			   }
	    	   //take less value and not -1
	    	   if (f2<f1) {
	    	            if (f2==-1) {
	    	            	data[0]=f1+"";
	    	            }else if (f1==-1) {
	    	            	data[0]=f2+"";
	    	            }else {
	    	            	data[0] =f2+"";
	    	            	data[1] =c1+"  "+f1+"\n";
	    	            	data[1] =data[1]+ c2+"  "+f2+"\n";
	    	            }
	    	   }else {
	    		     if (f1==-1) {
	    		    	 data[0]=f2+"";
	 	            }else if (f2==-1) {
	 	            	data[0]=f1+"";
	 	            }else {
	 	            	data[0]=f1+"";
	 	            	data[1] =c2+" "+f2+"\n";
	 	            	data[1] =data[1]+ c1+"  "+f1+"\n";
	 	            }
	    	   }
	    	   
	    	  // dtemp.setBridges1(bridges1);
	       }else {
	    	   data[0]=b1;
	       }
        return data;
	}

	private String[] pickNotEqualsValues(String b1,String b2,String c1,String c2) {
		String[] data=new String[2];
		data[0]=data[1]="";
		if (b1==null) b1="";
		if (b2==null) b2="";
	       if (!b1.equals(b2)){
	   		   
	    	 	data[0]=b1+"";
	         	data[1]=c2+b2+"\n";
	         	data[1]=data[1]+c2+b2+"\n";
			    	   
	    	   
	    	  // dtemp.setBridges1(bridges1);
	       }else {
	    	   data[0]=b1;
	       }
        return data;
	}

	/**
	 * 
	 * @param name
	 * @param codes
	 * @param driver
	 * @param type
	 * @param mandatory
	 * @param findInCodesOnly
	 * @param gradePay
	 * @param dataMap
	 * @return
	 */
	private String fetchBenefitByProcedure(String name, String[] codes, WebDriver driver, String type,
				boolean mandatory, boolean findInCodesOnly, String gradePay,Map<String, List<UnitedConLimitationDto>> dataMap) {
			int coPay = -1;
			String limit = "-1";
            //int coPayFirst = -1;
			//String limitFirst = "-1";
			boolean found = false;
			String value = Constants.SCRAPPING_NOT_FOUND;
			if (mandatory)
				value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
			else
				value = "";
			System.out.println("SIZE----"+dataMap.size()+", Name-->"+name);
			if (codes!=null) {
			for(String c:codes) {
				System.out.println("Code----"+c);
			}
			}
			gradePay=gradePay.trim();
			if (!gradePay.equals("")) {
				if (gradePay.toLowerCase().contains("e1") ||
				   gradePay.toLowerCase().contains("e2") ||
				   gradePay.toLowerCase().contains("e3") ||
					gradePay.toLowerCase().contains("e4")) {
					gradePay="Pay Grades E1 thru E4";
				}else {
					gradePay="All Other Pay Grades";
				}
			}
			
			//System.out.println("gradePay--"+gradePay);
			try {
			for (Map.Entry<String, List<UnitedConLimitationDto>> entry : dataMap.entrySet()) {
				// List<UnitedConLimitationDto> li=entry.getValue();
               
                
				if (benefitProAppliedtoded.equals(type)) {
					boolean breakCodes=false;
					value="";
					for (UnitedConLimitationDto dto : entry.getValue()) {
						if (findInCodesOnly) {
							//Only check in Given Code
							for (String cd : codes) {
								if (entry.getKey().equals(cd)) {
									
									if (dto.getAppliedtoDed().equalsIgnoreCase("yes")) {
										value = "Yes";
										found = true;
										breakCodes=true;
										break;	
									}
									
								}else {
									continue;
								}
							}
							found = true;
							break;
						}
						else if (dto.getAppliedtoDed().equalsIgnoreCase("yes")) {
							value = "Yes";
							found = true;
							breakCodes=true;
							break;
						}

					}
					if (breakCodes) break;
				}
				
				
				if (benefitProAppliedtoded.equals(type) && found) {
					break;
				}
				if ((benefitProcCopy.equals(type) || benefitProcLimitation.equals(type) || benefitProcLimitationSentence.equals(type)
						|| benefitProcLimitationSentencePermanentMolar.equals(type) || benefitProcLimitationSentencePrimayMolar.equals(type)
						|| benefitProcLimitationSentenceCombinationRoutine.equals(type) || benefitProcLimitationSentenceAlternateBenefitProvision.equals(type))
						&& !found) {
					boolean breakCodes=false;
					if (codes != null) {
						for (String cd : codes) {
							if (breakCodes) break;
							//System.out.println("KEY--"+entry.getKey()+"-");
							//System.out.println("cd--"+cd+"-");

							if (entry.getKey().equals(cd)) {
								int size = entry.getValue().size();
								boolean checkForGrade = false;// multiple records are not
								if (size > 1)
									checkForGrade = true;
								for (UnitedConLimitationDto dto : entry.getValue()) {

									if (checkForGrade && !gradePay.equals("")
											&& dto.getLimitation().contains(gradePay)) {
										found = true;
										coPay = dto.getCopay();
										limit=dto.getLimitation();
										breakCodes=true;
										System.out.println("CODE 1-" + entry.getKey());
										break;
									} else if (checkForGrade && gradePay.equals("")) {
										found = true;
										coPay = dto.getCopay();
										limit=dto.getLimitation();
										breakCodes=true;
										System.out.println("CODE-2 -" + entry.getKey());
										break;
									} else if (!checkForGrade) {//For non multiple codes 
										found = true;
										coPay = dto.getCopay();
										limit=dto.getLimitation();
										breakCodes=true;
										System.out.println("CODE- 3-" + entry.getKey()+" -- limit-"+limit+"- copay-"+coPay);
										break;
									}

								}
								
							}
						}
					} // codes!= null
					//if (codes==null) {
					if (!found && !findInCodesOnly) {
						int size = entry.getValue().size();
						boolean checkForGrade = false;// multiple records are not
						if (size > 1)
							checkForGrade = true;
						for (UnitedConLimitationDto dto : entry.getValue()) {
							// System.out.println("cccc cp-"+dto.getCopay());
							//System.out.println("cccc key-"+entry.getKey());
							//System.out.println("cccc size-"+size);
							//System.out.println("LLLL :"+dto.getLimitation()+"-:-");
							//limit=dto.getLimitation();
							if (checkForGrade && !gradePay.equals("")
									&& dto.getLimitation().contains(gradePay)) {
							if (coPay < dto.getCopay()) {
									coPay = dto.getCopay();
									System.out.println("CODE 4 " + entry.getKey()+"-->"+coPay);
								}
							} else if (checkForGrade && gradePay.equals("")) {
								if (coPay < dto.getCopay()) {
									coPay = dto.getCopay();
									System.out.println("CODE 5 " + entry.getKey()+"-->"+coPay);
								}
							} else if (!checkForGrade) {//For non multiple codes 
								if (coPay < dto.getCopay()) {
									coPay = dto.getCopay();
									System.out.println("CODE 6 -" + entry.getKey()+"-->"+coPay);
								}
							}

						}
					}
					//}
					if (benefitProcCopy.equals(type) && found) {
						break;
					}
					if (benefitProcLimitation.equals(type) && found) {
						break;
					}
					if (benefitProcLimitationSentence.equals(type) && found) {
						break;
					}

				} // co pay
			}//Map Loop
			if (benefitProcCopy.equals(type)) {
				if (coPay == -1)
					value = "";
				else
					value = coPay + "";
			}
			if (benefitProcLimitation.equals(type)) {
				if (limit.equals("-1"))
					value = "";
				else {
					
					value =FreqencyUtils.convertFrequecyUCCIString(limit);
					//value = limit + "";
				}
			}
			if (benefitProcLimitationSentence.equals(type)) {
				if (limit.equals("-1")) {
					value = "";//Decide latter
				}else {
					if (limit.contains("Under") && limit.contains("Years Of Age")) {
						value =limit.split("Under")[1].split("Years Of Age")[0].trim();
					    /*try {
					    	Integer.parseInt(value);
					    }catch(Exception p) {
					    	value="99";
					    }*/
						//value="14";//Under 14 Years Of Age
					}
					
					else value = "99";
				}
			}
			if (benefitProcLimitationSentenceCombinationRoutine.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("In Combination with Routine Cleanings")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProcLimitationSentenceAlternateBenefitProvision.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("Alternate Benefit Provision")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProcLimitationSentencePermanentMolar.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("Permanent") && limit.contains("Molars")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProcLimitationSentencePrimayMolar.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("Primary") && limit.contains("Molars")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProAppliedtoded.equals(type)) {
				if (value.equals(""))
					value = "No";

			}
			
			
			if (findInCodesOnly) {
				/*
				 * formElement = driver.findElement(By.id("j_id_jm")); maintables =
				 * formElement.findElements(By.tagName("table")); for (WebElement maintable :
				 * maintables) { if (maintable.getText() != null &&
				 * maintable.getText().startsWith(name)) {
				 * maintable.findElements(By.tagName("span")).get(0).click();
				 * Thread.sleep(1000); break; } }
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
			return value + " " + Constants.SCRAPPING_ISSUE_FETCHING;
		}
		System.out.println("value--" + value);
		return value;
	}

	private String fetchBenefitByProcedureOld(String name, String[] codes, WebDriver driver, String type,
			boolean mandatory, boolean subsectionOPen, boolean findInCodesOnly, String gradePay) throws InterruptedException {
		System.out.println("fetchBenefitByProcedure-" + name);

		String value = Constants.SCRAPPING_NOT_FOUND;
		Map<String, List<UnitedConLimitationDto>> dataMap = new HashMap<>();
		// WebElement togggle = null;
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		else
			value = "";
		//Thread.sleep(3000);
		try {
			if (!subsectionOPen) {
				WebElement servicesGroup= driver.findElement(By.id("servicesGroup"));
				WebElement formElement = servicesGroup.findElements(By.tagName("form")).get(1);
				//System.out.println("6");
				List<WebElement> maintables = formElement.findElements(By.tagName("table"));
				//System.out.println("7");
				for (WebElement maintable : maintables) {
					if (maintable.getText() != null && maintable.getText().startsWith(name)) {
						//System.out.println("8");
						maintable.findElements(By.tagName("span")).get(0).click();
						//System.out.println("9");
						Thread.sleep(3000);
						break;
					}
				}
			}
			//System.out.println("10");
			WebElement servicesGroup= driver.findElement(By.id("servicesGroup"));
			WebElement formElement = servicesGroup.findElements(By.tagName("form")).get(1);
			//System.out.println("11");
			List<WebElement> maintables = formElement.findElements(By.tagName("table"));
			//System.out.println("12");
			//Thread.sleep(1000);
			for (WebElement maintable : maintables) {
				if (maintable.getAttribute("id") != null
						&& maintable.getAttribute("id").equals("benefitDetailAllServiceProceduresList")
						&& !maintable.getAttribute("class").contains(" hidden")) {

					//if (!subsectionOPen) {//IF any issue Remove this condition and directly put Thread Sleep.
						Thread.sleep(3000);
					//}
					//System.out.println("13");
					List<WebElement> trs = maintable.findElements(By.tagName("tr"));
					trs.remove(0);// remove th row
					for (WebElement tr : trs) {
						//System.out.println("14");
						String code = tr.findElements(By.tagName("td")).get(0).getText().trim();// code
						//System.out.println("CODE-"+code);
						try {
							//System.out.println("15");
							UnitedConLimitationDto dto = new UnitedConLimitationDto();
							// String vm="";
							if (tr.findElements(By.tagName("td")).get(2).getText().equals("Not Covered")) {
								dto.setCopay(0);//may change when testing happens
								dto.setLimitation("Not Covered");
								dto.setAppliedtoDed("");
								if (dataMap.get(code) == null) {
									List<UnitedConLimitationDto> li = new ArrayList<>();
									li.add(dto);
									dataMap.put(code, li);
								} else {
									List<UnitedConLimitationDto> li = dataMap.get(code);
									li.add(dto);
									dataMap.put(code, li);

								}

								continue;
							}
							if (benefitProcCopy.equals(type)) {
								//System.out.println("16");
								try {
									dto.setCopay(Integer.parseInt(tr.findElements(By.tagName("td")).get(4).getText()
											.trim().replace("%", "")));// Co Pay
									// System.out.println("COPAY-"+dto.getCopay());
								} catch (Exception p) {
									continue;
								}
							}
							if (benefitProAppliedtoded.equals(type)) {
								//System.out.println("17");
								try {
									dto.setAppliedtoDed(tr.findElements(By.tagName("td")).get(6).getText().trim());// Applied
																													// to
																													// Deductible
									// System.out.println("setAppliedtoDed-"+dto.getAppliedtoDed());
								} catch (Exception p) {
									continue;
								}
							}

							String limitation = tr.findElements(By.tagName("td")).get(5).getText().trim()
									.replace(" | More...", "").trim();// limitation
							// System.out.println("limitation-"+limitation);
							dto.setLimitation(limitation);
							if (dataMap.get(code) == null) {
								List<UnitedConLimitationDto> li = new ArrayList<>();
								li.add(dto);
								dataMap.put(code, li);
							} else {
								List<UnitedConLimitationDto> li = dataMap.get(code);
								li.add(dto);
								dataMap.put(code, li);

							}

						} catch (Exception n) {
							n.printStackTrace();
							UnitedConLimitationDto dto = new UnitedConLimitationDto();
							dto.setCopay(-1);
							List<UnitedConLimitationDto> li = new ArrayList<>();
							li.add(dto);
							dataMap.put(code, li);
						}
					}

					break;
				}
			}

			int coPay = -1;
			String limit = "-1";
			boolean found = false;
			System.out.println("SIZE----"+dataMap.size());
			for (Map.Entry<String, List<UnitedConLimitationDto>> entry : dataMap.entrySet()) {
				// List<UnitedConLimitationDto> li=entry.getValue();
               
                
				if (benefitProAppliedtoded.equals(type)) {
					for (UnitedConLimitationDto dto : entry.getValue()) {
						if (dto.getAppliedtoDed().equalsIgnoreCase("yes")) {
							value = "Yes";
							found = true;
							break;
						}

					}
				}
				
				
				if (benefitProAppliedtoded.equals(type) && found) {
					break;
				}
				if (benefitProcCopy.equals(type) || benefitProcLimitation.equals(type) || benefitProcLimitationSentence.equals(type)
						|| benefitProcLimitationSentencePermanentMolar.equals(type) || benefitProcLimitationSentencePrimayMolar.equals(type)
						|| benefitProcLimitationSentenceCombinationRoutine.equals(type) || benefitProcLimitationSentenceAlternateBenefitProvision.equals(type)) {
					if (codes != null) {
						for (String cd : codes) {
							// System.out.println("KEY--"+entry.getKey()+"-");
							// System.out.println("cd--"+cd+"-");

							if (entry.getKey().equals(cd)) {
								int size = entry.getValue().size();
								boolean checkForGrade = false;// multiple records are not
								if (size > 1)
									checkForGrade = true;
								for (UnitedConLimitationDto dto : entry.getValue()) {

									if (checkForGrade && !gradePay.equals("")
											&& gradePay.contains(dto.getLimitation())) {
										found = true;
										coPay = dto.getCopay();
										limit=dto.getLimitation();
										System.out.println("CODE 1-" + entry.getKey());
										break;
									} else if (gradePay.equals("")) {
										found = true;
										coPay = dto.getCopay();
										limit=dto.getLimitation();
										System.out.println("CODE-2 -" + entry.getKey());
										break;
									} else if (!checkForGrade) {
										found = true;
										coPay = dto.getCopay();
										limit=dto.getLimitation();
										System.out.println("CODE- 3-" + entry.getKey());
										break;
									}

								}
							}
						}
					} // codes!= null
					//if (codes==null) {
					if (!found && !findInCodesOnly) {
						for (UnitedConLimitationDto dto : entry.getValue()) {
							// System.out.println("cccc-"+dto.getCopay());
							// System.out.println("cccc-"+entry.getKey());
							//limit=dto.getLimitation();
							if (!gradePay.equals("") && gradePay.contains(dto.getLimitation())) {
								if (coPay < dto.getCopay()) {
									coPay = dto.getCopay();
									System.out.println("CODE 4 -" + entry.getKey());
								}
							} else if (gradePay.equals("")) {
								if (coPay < dto.getCopay()) {
									coPay = dto.getCopay();
									System.out.println("CODE 5 -" + entry.getKey());
								}
							}

						}
					}
					//}
					if (benefitProcCopy.equals(type) && found) {
						break;
					}
					if (benefitProcLimitation.equals(type) && found) {
						break;
					}
					if (benefitProcLimitationSentence.equals(type) && found) {
						break;
					}

				} // co pay
			}
			if (benefitProcCopy.equals(type)) {
				if (coPay == -1)
					value = "";
				else
					value = coPay + "";
			}
			if (benefitProcLimitation.equals(type)) {
				if (limit.equals("-1"))
					value = "";
				else {
					
					value =FreqencyUtils.convertFrequecyUCCIString(limit);
					value = limit + "";
				}
			}
			if (benefitProcLimitationSentence.equals(type)) {
				if (limit.equals("-1"))
					value = "99";
				else {
					if (limit.contains("Under") && limit.contains("Years Of Age")) {
						value =limit.split("Under")[1].split("Years Of Age")[0].trim();
					    /*try {
					    	Integer.parseInt(value);
					    }catch(Exception p) {
					    	value="99";
					    }*/
						//value="14";//Under 14 Years Of Age
					}
					
					else value = "99";
				}
			}
			if (benefitProcLimitationSentenceCombinationRoutine.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("In Combination with Routine Cleanings")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProcLimitationSentenceAlternateBenefitProvision.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("Alternate Benefit Provision")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProcLimitationSentencePermanentMolar.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("Permanent") && limit.contains("Molars")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProcLimitationSentencePrimayMolar.equals(type)) {
				if (limit.equals("-1"))
					value = "No";
				else {
					if (limit.contains("Primary") && limit.contains("Molars")) value="Yes";
					else value = "No";
				}
			}
			if (benefitProAppliedtoded.equals(type)) {
				if (value.equals(""))
					value = "No";

			}
			
			
			if (findInCodesOnly) {
				/*
				 * formElement = driver.findElement(By.id("j_id_jm")); maintables =
				 * formElement.findElements(By.tagName("table")); for (WebElement maintable :
				 * maintables) { if (maintable.getText() != null &&
				 * maintable.getText().startsWith(name)) {
				 * maintable.findElements(By.tagName("span")).get(0).click();
				 * Thread.sleep(1000); break; } }
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
			return value + " " + Constants.SCRAPPING_ISSUE_FETCHING;
		}
		System.out.println("value--" + value);
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
		driver.get("https://www.unitedconcordia.com/tuctpi/subscriber.xhtml");
		Thread.sleep(10000);

		return true;
	}

	public boolean loginToSiteUnitedCon(ScrappingFullDataDetailDto dto, WebDriver driver)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		boolean navigate = true;
		// driver.Window.Maximize();
		driver.get(dto.getSiteUrl());
		Thread.sleep(1000);// Need to keep this number high for Linux issue.
		// System.out.println(driver.getPageSource());
		try {
			driver.findElement(By.id("covid19Modal")).findElement(By.tagName("button")).click();
			Thread.sleep(4000);
		} catch (Exception e) {
		}

		try {
			driver.findElement(By.id("onetrust-accept-btn-handler")).click();
			Thread.sleep(7000);
		} catch (Exception e) {
		}
		try {

			// Layout issue...
			WebElement userNameElement = driver.findElement(By.id("signinForm:username"));
			userNameElement.sendKeys(dto.getUserName());
			WebElement passwordElement = driver.findElement(By.id("signinForm:password"));

			passwordElement.sendKeys(dto.getPassword());
			WebElement loginButonElement = driver
					.findElement(By.xpath("/html/body/div[2]/div[1]/div/div[2]/div[1]/form/div[3]/a"));

			loginButonElement.click();
			Thread.sleep(5000);// Need to keep this number high for Linux issue.
			try {
				WebElement p = driver.findElement(By.id("signinFormError:username"));
				if (p != null) {
					navigate = false;
				}
			} catch (Exception p) {
				/// navigate = false;
			}
		} catch (Exception newLogic) {

			List<WebElement> li = driver.findElements(By.tagName("button"));
			for (WebElement l : li) {
				if (l.getText().trim().contains("Sign In / Create Account")) {
					l.click();
					Thread.sleep(4000);
					break;

				}
			}

			WebElement userNameElement = driver.findElement(By.id("signinFormModal:username"));
			userNameElement.sendKeys(dto.getUserName());
			WebElement passwordElement = driver.findElement(By.id("signinFormModal:password"));

			passwordElement.sendKeys(dto.getPassword());
			WebElement loginButonElement = driver.findElement(By.id("signinFormModal:submit"));

			loginButonElement.click();
			Thread.sleep(5000);// Need to keep this number high for Linux issue.
			try {
				driver.findElement(By.id("signinFormError"));
				navigate = false;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		 System.out.println("navigate-"+navigate);
		return navigate;
	}

	/**
	 * Link: https://www.unitedconcordia.com/dental-insurance/dentist/ Member ID:
	 * 01144286800 DOB: 05/17/2010 Name: STARLYN D. BIAS
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
		System.out.println("taskkill /f /im chromedriver.exe");
		System.out.println("ST DATE :"+new Date());
		String g = "[{Deepak$,.[{";
		
		
		try {
			String y="Individual Maximum	$1,750 Per Lifetime ~ Ortho related";
			System.out.println(y.replace("Individual Maximum","").trim().split(" ")[0].replace("$","").replace(",",""));
			System.out.println(g.replaceFirst("\\[\\{", "{"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(g.replace(",", ""));
		// System.out.println(g.replace(".", ""));

		// ScrappingSiteDetails d= new ScrappingSiteDetails();
		ScrappingSiteDetailsFull f = new ScrappingSiteDetailsFull();
		ScrappingSiteFull fu = new ScrappingSiteFull();
		fu.setSiteName("United");
		f.setScrappingSite(fu);
		f.setProxyPort("9500");
		// d.setGoogleSheetId("");
		ScrappingFullDataDetailDto dto = new ScrappingFullDataDetailDto();
		dto.setPassword("Sintonsmile#2415");
		dto.setUserName("Sintonfd01");
		dto.setSiteName("United Concordia");

		PatientScrapSearchDto psc = new PatientScrapSearchDto();
		List<PatientScrapSearchDto> l = new ArrayList<>();
		psc.setDob("04/29/2002");//03/15/1973
		/** For Ddental456/Insurance@745 we have  126229918001  DOB-05/14/1958*/
		
		psc.setFirstName("");
		psc.setLastName("");
		psc.setMemberId("128372920001");//129521395001
		psc.setSsnNumber("");
		psc.setGradePay("");

		l.add(psc);
		// dto.setPassword("Smile123");
		dto.setDto(l);
		/*
		 * psc = new PatientScrapSearchDto(); psc.setDob("11/20/1973");
		 * psc.setFirstName("Vivian"); psc.setLastName("Courtney");
		 * psc.setMemberId("825711808"); psc.setSsnNumber(""); l.add(psc);
		 * 
		 * dto.setDto(l);
		 */

		dto.setSiteUrl("https://www.unitedconcordia.com/dental-insurance/dentist/");
		UnitedConcordiaImpl i = new UnitedConcordiaImpl(null, null, null, dto, null, null, 1, "",null,
				"D:/Project/Tricon/linkedinapp/linkedinbit/linkedinapp/lib/chromedriver.exe");
		i.setProps("9500");
		// i.scrappingSiteDetails = f;
		// dto.setUserName("crosbyfd07");
		i.scrapSite(f, dto, null, null);

		/*
		 * d.setGoogleSheetName(googleSheetName); d.setOffice(office);
		 * 
		 * i.scrapSite(d, mapData);
		 */
	}

}
