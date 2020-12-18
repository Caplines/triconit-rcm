package com.tricon.ruleengine.service.scrapfull.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.By;
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
	static {

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

	}

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
							System.out.println("Thread Running");
							WebDriver driver = getBrowserDriver();// new HtmlUnitDriver(true);// getBrowserDriver();
							try {
								System.out.println("MEM 9-"
										+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

								boolean navigate = loginToSiteUnitedCon(dto, driver);

								boolean issueNo = navigatetoMainSite(driver, navigate);
								System.out.println("888888888888- STARTED...");
								PatientTemp d = parsePage(driver, data, siteName, issueNo, office);
								System.out.println("888888888888 -END " + d.getPatientId());
								if (d != null) {
									// Update the Data in Database
									updateDatainDB(d, office, user);
								}

								Thread.sleep(2000);
							} catch (Exception dri) {
								dri.printStackTrace();
							} finally {
								driver.close();
								driver.quit();
								finalSetUpCall();
							}
						}
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

	private void updateDatainDB(PatientTemp data, Office office, User user) {
		try {
			patDao.savePatientTempDataWithDetailsAndHistory(data, office, user);
		} catch (Exception c) {
			System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIII");
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
	}

	private void createPatientDetailSetup(WebDriver driver, PatientTemp temp, PatientScrapSearchDto sh)
			throws InterruptedException {
		Thread.sleep(3000);
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
		Thread.sleep(5000);
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();

		dtemp.setInsName("United Concordia"); //136
		dtemp.setInsContact("800-332-0366"); // 138
		dtemp.setcSRName("UCCI Scraping Tool");// 139
		dtemp.setTaxId(taxId); //137

		List<PatientHistoryTemp> hisSet = temp.getPatientHistory();
		// openSideBarFirst(driver,"Procedure History");
		fetchHistoryformation(driver, hisSet);
		dtemp.setPlanAnnualMaxRemaining("0");//Set default as Zero //2 mand
		dtemp.setPlanIndividualDeductibleRemaining("0");//Set default as Zero //3 mand
		dtemp.setOrthoRemaining("0");//Set default as Zero
		try {
	   WebElement accums=driver.findElement(By.className("accums"));
	   List<WebElement> acc= accums.findElements(By.className("panel-default"));
	   for(WebElement ac:acc) {
		   if (ac.getText().contains("PROGRAM DOLLAR MAX")  ) {
			  if (ac.getText().contains("No maximum applied to the current")) {
				  dtemp.setPlanAnnualMaxRemaining("0");
				  
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
		   if (ac.getText().contains("PROGRAM DOLLAR DED")  ) {
				  if (ac.getText().contains("No maximum applied to the current")) {
					   dtemp.setPlanIndividualDeductibleRemaining("0");
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
	      }
			   
	   
        }catch(Exception x) {
        	
        }
        
        String []v= fetchDeductiblesAndMaximums("Deductibles and Maximums", driver, false, false);
        dtemp.setPlanAnnualMax(v[0]);//1 mand
        
        dtemp.setPlanIndividualDeductible(v[1]);//3 mand
        
        if (dtemp.getPlanAnnualMaxRemaining().equals("0")){
		dtemp.setPlanAnnualMaxRemaining(dtemp.getPlanAnnualMax());
        }
        
	     if (dtemp.getPlanIndividualDeductibleRemaining().equals("0")){
		  dtemp.setPlanIndividualDeductibleRemaining(dtemp.getPlanIndividualDeductible());
	     }
	     
	     
	     dtemp.setOrthoMax(v[2]);//91 mand
	     
	     if (dtemp.getOrthoRemaining().equals("0")){
	  		  dtemp.setOrthoRemaining(dtemp.getOrthoMax());
	  	 }
	        
		dtemp.setMissingToothClause(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Missing Tooth Clause", true, false, false));// 16 mand

		dtemp.setReplacementClause(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Prosthetic Prior Placement", true, true, false));// 17 mand

		dtemp.setxRaysBundling(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Accumulate Xrays", true, false, false));// 32
		
		dtemp.setPlanAssignmentofBenefits(fetchCordinationBenefit("Coordination and Other Benefits", driver,
				"Assignment Of Benefits", true, true, false));// 131 mand

		dtemp.setBasicPercentage(fetchBenefitByProcedure("Restorations", new String[] { "D2391", "D2392", "D2393" },
				driver, benefitProcCopy, false, false, false, temp.getGradePay()));// 5
		
		dtemp.setBasicSubjectDeductible(fetchBenefitByProcedure("Restorations", null, driver, benefitProAppliedtoded,
				false, true, false, temp.getGradePay()));// 6
		
		dtemp.setPostCompositesD2391Percentage(fetchBenefitByProcedure("Restorations", new String[] {"D2391"}, driver, benefitProcCopy,
				false, true, true, temp.getGradePay()));// 78
		
		dtemp.setPostCompositesD2391FL(fetchBenefitByProcedure("Restorations", new String[] {"D2391"}, driver, benefitProcLimitation,
				false, true, true, temp.getGradePay()));// 79
		
		dtemp.setPosteriorCompositesD2391Downgrade(fetchBenefitByProcedure("Restorations", new String[] {"D2391"}, driver, benefitProcLimitationSentenceAlternateBenefitProvision,
				false, true, true, temp.getGradePay()));// 80

		dtemp.setMajorPercentage(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				null, driver, benefitProcCopy, false, false, false, temp.getGradePay()));// 7

		dtemp.setMajorSubjectDeductible(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				null, driver, benefitProAppliedtoded, true, true, false, temp.getGradePay()));// 8 mand

		dtemp.setCompleteDenturesD5110D5120FL(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5110"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 68

		dtemp.setImmediateDenturesD5130D5140FL(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5130"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 69

		dtemp.setPartialDenturesD5213D5214FL(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5213"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 70

		dtemp.setInterimPartialDenturesD5214FL(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5820"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 71

		dtemp.setDen5225Per(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5225"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 105

		dtemp.setDen5226Per(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5226"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 106

		dtemp.setDenf5225FR(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5225"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 107

		dtemp.setDenf5226Fr(fetchBenefitByProcedure("Dentures, Denture Adjustments, Denture Repairs, Relining",
				new String[] {"D5226"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 108

		dtemp.setEndodonticsPercentage(fetchBenefitByProcedure("Endodontic Procedures",
				null, driver, benefitProcCopy, false, false, false, temp.getGradePay()));// 9

		dtemp.setEndoSubjectDeductible(fetchBenefitByProcedure("Endodontic Procedures",
				null, driver, benefitProAppliedtoded, true, true, false, temp.getGradePay()));// 10 mand

		dtemp.setPerioSurgeryPercentage(fetchBenefitByProcedure("Endodontic Procedures",
				null, driver, benefitProcCopy, false, false, false, temp.getGradePay()));// 11

		dtemp.setPerioSurgerySubjectDeductible(fetchBenefitByProcedure("Endodontic Procedures",
				null, driver, benefitProAppliedtoded, true, true, false, temp.getGradePay()));// 12 mand

		dtemp.setPreventivePercentage(fetchBenefitByProcedure("Cleanings & Fluoride",
				null, driver, benefitProcCopy, false, false, false, temp.getGradePay()));// 13

		dtemp.setDiagnosticPercentage(fetchBenefitByProcedure("Preventive Exams",
				null, driver, benefitProcCopy, false, false, false, temp.getGradePay()));// 14
		
		dtemp.setDiagnosticSubDed(fetchBenefitByProcedure("Preventive Exams",
				null, driver, benefitProAppliedtoded, false, true, false, temp.getGradePay()));// 109
		
		dtemp.setNightGuardsD9940Percentage(fetchBenefitByProcedure("Miscellaneous Services",
				new String[] {"D9944"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 19
		
		dtemp.setNightGuardsD9944Fr(fetchBenefitByProcedure("Miscellaneous Services",
				new String[] {"D9944"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 121

		dtemp.setNightGuardsD9945Fr(fetchBenefitByProcedure("Miscellaneous Services",
				new String[] {"D9945"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 122

		dtemp.setNightGuardsD9945Percentage(fetchBenefitByProcedure("Miscellaneous Services",
				new String[] {"D9945"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 114 cross check

		dtemp.setsSCD2930FL(fetchBenefitByProcedure("Other Restorations",
				new String[] {"D2930"}, driver, benefitProcLimitation, false, false, true, temp.getGradePay()));// 22
		
		dtemp.setsSCD2931FL(fetchBenefitByProcedure("Other Restorations",
				new String[] {"D2931"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 23
		
		dtemp.setBuildUpsD2950Covered(fetchBenefitByProcedure("Other Restorations",
				new String[] {"D2950"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 87

		dtemp.setBuildUpsD2950FL(fetchBenefitByProcedure("Other Restorations",
				new String[] {"D2950"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 88

		dtemp.setExamsD0120FL(fetchBenefitByProcedure("Preventive Exams",
				new String[] {"D0120"}, driver, benefitProcLimitation, false, false, true, temp.getGradePay()));// 24
		
		dtemp.setExamsD0140FL(fetchBenefitByProcedure("Preventive Exams",
				new String[] {"D0140"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 25
		
		dtemp.seteExamsD0145FL(fetchBenefitByProcedure("Preventive Exams",
				new String[] {"D0145"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 26
		
		dtemp.setExamsD0150FL(fetchBenefitByProcedure("Preventive Exams",
				new String[] {"D0150"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 27
		
		dtemp.setpAXRaysPercentage(fetchBenefitByProcedure("X-rays",
				new String[] { "D0220"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 15

		dtemp.setxRaysBWSFL(fetchBenefitByProcedure("X-rays",
				new String[] {"D0272","D0274"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 28
		
		dtemp.setxRaysPAD0220FL(fetchBenefitByProcedure("X-rays",
				new String[] {"D0220"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 29

		dtemp.setxRaysPAD0230FL(fetchBenefitByProcedure("X-rays",
				new String[] {"D0230"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 30
		
		dtemp.setxRaysFMXFL(fetchBenefitByProcedure("X-rays",
				new String[] {"D0210"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 31
		
		dtemp.setpAXRaysSubDed(fetchBenefitByProcedure("X-rays",
				new String[] {"D0220"}, driver, benefitProAppliedtoded, false, true, true, temp.getGradePay()));// 117

		dtemp.setFmxPer(fetchBenefitByProcedure("X-rays",
				new String[] {"D0210"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 120
		
		
		dtemp.setFlourideD1208FL(fetchBenefitByProcedure("Cleanings & Fluoride",
				new String[] {"D1208"}, driver, benefitProcLimitation, false, false, true, temp.getGradePay()));// 33
		
		dtemp.setFlourideAgeLimit(fetchBenefitByProcedure("Cleanings & Fluoride",
				new String[] {"D1208"}, driver, benefitProcLimitationSentence, false, true, true, temp.getGradePay()));// 34
		
		dtemp.setVarnishD1206FL(fetchBenefitByProcedure("Cleanings & Fluoride",
				new String[] {"D1206"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 35
		
		dtemp.setVarnishD1206AgeLimit(fetchBenefitByProcedure("Cleanings & Fluoride",
				new String[] {"D1206"}, driver, benefitProcLimitationSentence, false, true, true, temp.getGradePay()));// 36
		
		dtemp.setProphyD1110FL(fetchBenefitByProcedure("Cleanings & Fluoride",
				new String[] {"D1110"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 43

		dtemp.setProphyD1120FL(fetchBenefitByProcedure("Cleanings & Fluoride",
				new String[] {"D1120"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 44

		dtemp.setProphyD1120FL(fetchBenefitByProcedure("Cleanings & Fluoride",
				null, driver, benefitProAppliedtoded, false, true, false, temp.getGradePay()));// 118
		
		dtemp.setSealantsD1351Percentage(fetchBenefitByProcedure("Sealants",
				new String[] {"D1351"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 37

		dtemp.setSealantsD1351FL(fetchBenefitByProcedure("Sealants",
				new String[] {"D1351"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 38

		dtemp.setSealantsD1351AgeLimit(fetchBenefitByProcedure("Sealants",
				new String[] {"D1351"}, driver, benefitProcLimitationSentence, false, true, true, temp.getGradePay()));// 39
		
		dtemp.setSealantsD1351AgeLimit(fetchBenefitByProcedure("Sealants",
				new String[] {"D1351"}, driver, benefitProcLimitationSentencePrimayMolar, false, true, true, temp.getGradePay()));// 40

		//41
		dtemp.setSealantsD1351AgeLimit(fetchBenefitByProcedure("Sealants",
				new String[] {"D1351"}, driver, benefitProcLimitationSentencePermanentMolar, false, true, true, temp.getGradePay()));// 42

		//45
		
		dtemp.setsRPD4341Percentage(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4341"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 46

		dtemp.setsRPD4341FL(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4341"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 47

		//48 49
		dtemp.setPerioMaintenanceD4910Percentage(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4910"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 50

		dtemp.setPerioMaintenanceD4910FL(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4910"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 51
		
		dtemp.setPerioMaintenanceD4910AltWProphyD0110(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4910"}, driver, benefitProcLimitationSentenceCombinationRoutine, false, true, true, temp.getGradePay()));// 52
		
		dtemp.setFMDD4355Percentage(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4345"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 53
		
		dtemp.setfMDD4355FL(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4345"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 54

		dtemp.setGingivitisD4346Percentage(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4346"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 55

		dtemp.setGingivitisD4346FL(fetchBenefitByProcedure("Non-Surgical Periodontal Services",
				new String[] {"D4346"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 56

		dtemp.setNitrousD9230Percentage(fetchBenefitByProcedure("Adjunctive Services",
				new String[] {"D9230"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 57

		dtemp.setiVSedationD9243Percentage(fetchBenefitByProcedure("Adjunctive Services",
				new String[] {"D9243"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 58


		dtemp.setiVSedationD9248Percentage(fetchBenefitByProcedure("Adjunctive Services",
				new String[] {"D9248"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 59
		
		dtemp.setD9310Percentage(fetchBenefitByProcedure("Adjunctive Services",
				new String[] {"D9310"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 85
		
		dtemp.setD9310FL(fetchBenefitByProcedure("Adjunctive Services",
				new String[] {"D9310"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 86

		
		dtemp.setExtractionsMinorPercentage(fetchBenefitByProcedure("Oral Surgery",
				new String[] {"D7210"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 60
		
		dtemp.setExtractionsMajorPercentage(fetchBenefitByProcedure("Oral Surgery",
				new String[] {"D7140"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 61
		
		dtemp.setAlveoD7311FL(fetchBenefitByProcedure("Oral Surgery",
				new String[] {"D7311"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 65

		dtemp.setCrownLengthD4249Percentage(fetchBenefitByProcedure("Surgical Periodontal Services",
				new String[] {"D4249"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 62
		
		dtemp.setCrownLengthD4249FL(fetchBenefitByProcedure("Surgical Periodontal Services",
				new String[] {"D4249"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 63
		
		//64
        //72
		dtemp.setBoneGraftsD7953FL(fetchBenefitByProcedure("Other Repair Procedures",
				new String[] {"D7953"}, driver, benefitProcLimitation, false, false, true, temp.getGradePay()));// 73
		
		dtemp.setImplantCoverageD6010Percentage(fetchBenefitByProcedure("Surgical Implant Procedures",
				new String[] {"D6010"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 74
		
		dtemp.setImplantsFrD6010(fetchBenefitByProcedure("Surgical Implant Procedures",
				new String[] {"D6010"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 110
		
		
		dtemp.setImplantCoverageD6057Percentage(fetchBenefitByProcedure("Implant Supported Prosthetics",
				new String[] {"D6057"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 75
		
		dtemp.setImplantCoverageD6190Percentage(fetchBenefitByProcedure("Implant Supported Prosthetics",
				new String[] {"D6190"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 76
		
		dtemp.setImplantSupportedPorcCeramicD6065Percentage(fetchBenefitByProcedure("Implant Supported Prosthetics",
				new String[] {"D6065"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 77
		
		dtemp.setImplantsFrD6057(fetchBenefitByProcedure("Implant Supported Prosthetics",
				new String[] {"D6057"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 111
		
		dtemp.setImplantsFrD6065(fetchBenefitByProcedure("Implant Supported Prosthetics",
				new String[] {"D6065"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 112
		
		dtemp.setImplantsFrD6190(fetchBenefitByProcedure("Implant Supported Prosthetics",
				new String[] {"D6190"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 113
		
		
		dtemp.setCrownsD2750D2740Percentage(fetchBenefitByProcedure("Crowns, Inlays & Onlays",
				new String[] {"D2740"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 81
		
		dtemp.setCrownsD2750D2740FL(fetchBenefitByProcedure("Crowns, Inlays & Onlays",
				new String[] {"D2750"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 82
		//83 84
		
		dtemp.setOrthoPercentage(fetchBenefitByProcedure("Orthodontics",
				new String[] {"D2740"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 91
		
		dtemp.setOrthoAgeLimit(fetchBenefitByProcedure("Orthodontics",
				new String[] {"D8080"}, driver, benefitProcLimitation, false, true, true, temp.getGradePay()));// 92
		
		dtemp.setOrthoSubjectDeductible(fetchBenefitByProcedure("Orthodontics",
				null, driver, benefitProAppliedtoded, false, true, false, temp.getGradePay()));// 93	

		dtemp.setBridges1(fetchBenefitByProcedure("Fixed Prosthetics & Fixed Partial Denture Retainers",
				new String[] {"D6245","D6740"}, driver, benefitProcCopy, false, false, true, temp.getGradePay()));// 102
		
		dtemp.setBridges2(fetchBenefitByProcedure("Fixed Prosthetics & Fixed Partial Denture Retainers",
				new String[] {"D6245","D6740"}, driver, benefitProcCopy, false, true, true, temp.getGradePay()));// 103  ask puneet
		
		
		
		
		
		
		
		
		
		
		dtemp.setGeneralDateIVwasDone(Constants.SIMPLE_DATE_FORMAT_IVF.format(new Date()));//147 and 97

		WebElement el = driver.findElement(By.id("memberBackground"));
		List<WebElement> divs = el.findElements(By.tagName("div"));
		for (WebElement div : divs) {
			if (div.getText() != null && div.getText().startsWith("Coverage Effective")) {
				try {
					WebElement ce = div.findElements(By.tagName("div")).get(2);
					String s[] = ce.getText().split(" - ")[0].split("/");
					dtemp.setPlanEffectiveDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
							+ (s[1].length() == 2 ? s[1] : "0" + s[1]));// 146

				} catch (Exception e) {
					dtemp.setPlanEffectiveDate("");//146
				}
				break;
			}
		}
		dtemp.setPlanTermedDate("");//145 keep it blank
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

		dtemp.setPayerId("CX007");//143
		dtemp.setInsAddress("P.O. Box 69451. Harrisburg, PA 17106");//142
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
		try {
			if (!subsectionOPen) {
				WebElement formElement = driver.findElement(By.id("j_id_hu"));
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
				if (y != null && y.startsWith("Individual Maximum") && y.contains("Ortho")) {
					String t=y.replace("Individual Maximum","").trim().split(" ")[0].replace("$","").replace(",","");
					r[2]=t;
					// Assignment Of Benefits
				}
				if (y != null && y.startsWith("Individual Maximum") && !y.contains("Ortho")) {
					String t=y.replace("Individual Maximum","").trim().split(" ")[0].replace("$","").replace(",","");
					r[0]=t;
					// Assignment Of Benefits
				}
				if (y != null && y.startsWith("Individual Deductible")) {
					if (tr.getText().replaceAll("Individual Deductible", "").trim().contains("None")) {
						r[1]="0";	
					}else {
						r[1]=y.replaceAll("Individual Deductible", "").trim();
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		
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
				WebElement formElement = driver.findElement(By.id("j_id_hu"));
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

	private String fetchBenefitByProcedure(String name, String[] codes, WebDriver driver, String type,
			boolean mandatory, boolean subsectionOPen, boolean findInCodesOnly, String gradePay) throws InterruptedException {
		System.out.println("fetchBenefitByProcedure-" + name);

		String value = Constants.SCRAPPING_NOT_FOUND;
		Map<String, List<UnitedConLimitationDto>> dataMap = new HashMap<>();
		// WebElement togggle = null;
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		else
			value = "";
		Thread.sleep(1000);
		try {
			if (!subsectionOPen) {
				//System.out.println("5");
				WebElement formElement = driver.findElement(By.id("j_id_jm"));
				//System.out.println("6");
				List<WebElement> maintables = formElement.findElements(By.tagName("table"));
				//System.out.println("7");
				for (WebElement maintable : maintables) {
					if (maintable.getText() != null && maintable.getText().startsWith(name)) {
						//System.out.println("8");
						maintable.findElements(By.tagName("span")).get(0).click();
						//System.out.println("9");
						Thread.sleep(5000);
						break;
					}
				}
			}
			//System.out.println("10");
			WebElement formElement = driver.findElement(By.id("j_id_jm"));
			//System.out.println("11");
			List<WebElement> maintables = formElement.findElements(By.tagName("table"));
			//System.out.println("12");
			for (WebElement maintable : maintables) {
				if (maintable.getAttribute("id") != null
						&& maintable.getAttribute("id").equals("benefitDetailAllServiceProceduresList")
						&& !maintable.getAttribute("class").contains(" hidden")) {

					Thread.sleep(1000);
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
					
					//value =FreqencyUtils.convertFrequecyUCCIString(limit);
					value = limit + "";
				}
			}
			if (benefitProcLimitationSentence.equals(type)) {
				if (limit.equals("-1"))
					value = "99";
				else {
					if (limit.contains("Under 14 Years Of Age")) value="14";
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
				if (value.equals("") || mandatory)
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
		Thread.sleep(4000);

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
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		try {
			driver.findElement(By.id("onetrust-accept-btn-handler")).click();
			Thread.sleep(5000);
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
			Thread.sleep(2000);// Need to keep this number high for Linux issue.
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
					Thread.sleep(1000);
					break;

				}
			}

			WebElement userNameElement = driver.findElement(By.id("signinFormModal:username"));
			userNameElement.sendKeys(dto.getUserName());
			WebElement passwordElement = driver.findElement(By.id("signinFormModal:password"));

			passwordElement.sendKeys(dto.getPassword());
			WebElement loginButonElement = driver.findElement(By.id("signinFormModal:submit"));

			loginButonElement.click();
			Thread.sleep(2000);// Need to keep this number high for Linux issue.
			try {
				driver.findElement(By.id("signinFormError"));
				navigate = false;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		// System.out.println(p.getText());
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
		fu.setSiteName("BCBS");
		f.setScrappingSite(fu);
		f.setProxyPort("9500");
		// d.setGoogleSheetId("");
		ScrappingFullDataDetailDto dto = new ScrappingFullDataDetailDto();
		dto.setPassword("Capline@12345");
		dto.setUserName("crosby_ucci");
		dto.setSiteName("United Concordia");

		PatientScrapSearchDto psc = new PatientScrapSearchDto();
		List<PatientScrapSearchDto> l = new ArrayList<>();
		psc.setDob("05/17/2010");
		psc.setFirstName("STARLYN");
		psc.setLastName("D. BIAS");
		psc.setMemberId("01144286800");
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
