package com.tricon.ruleengine.service.scrapfull.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.PatientScrapSearchDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.scrapping.DentaBenefitScrapDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.PatientDetailTemp;
import com.tricon.ruleengine.model.db.PatientHistoryTemp;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagment;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.service.impl.BaseScrappingServiceImpl;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DateUtils;
import com.tricon.ruleengine.utils.FreqencyUtils;

public class DeltaDentalServiceImpl extends BaseScrappingServiceImpl implements Callable<Boolean> {

	// document :
	// https://docs.google.com/spreadsheets/d/1-3PVTrzgSl0n3OEY7Qt0JZ_WDK7twIQWU3Hk7UjSdBM/edit#gid=1557872290
	static {

		// java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		// java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		// java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(Level.OFF);
		// java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF);
		// System.setProperty("org.apache.commons.logging.Log",
		// "org.apache.commons.logging.impl.NoOpLog");

	}

	private static String benefitContract = "Contract Benefit Level";
	private static String benefitLimitation = "Limitation";
	private static String benefitAgeLimit = "Age Limit";
	private static String benefitAlveoloplasty = "Alveoloplasty in conjunction";
	// private static String counterLink = "";
	// private static HashMap<String, String> counterElementMap = null;

	// private static String benefitMaximumLifeTime = "Maximum Life Time ";
	// private static String benefitMaximumLifeTimeRem = "Maximum Life Time Rem";

	@Autowired
	ScrapingFullDataDoa dataDoa;

	@Autowired
	PatientDao patDao;

	private ScrappingSiteDetailsFull scrappingSiteDetails;
	// this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
	// this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
	// this.mapData=mapData;
	// this.updateSheet=updateSheet;
	private ScrappingFullDataDetailDto dto;
	private Office office;
	private User user;
	private String driverLocation;
	private int processId;
	private String taxId;

	private static String siteName = "";

	public DeltaDentalServiceImpl(PatientDao patDao, ScrapingFullDataDoa dataDoa,
			ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto, User user, Office office,
			int processId,String taxId,String driverLocation) {

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
		this.processId=processId;
		this.taxId=taxId;
		// store parameter for later user
	}

	private WebDriver getBrowserDriver() {
		// System.setProperty("webdriver.gecko.driver",
		// "D:/Project/Tricon/linkedinapp/linkedin/lib/geckodriver.exe");
		// for chrome
		// webClient = new WebClient();
		ChromeOptions options = new ChromeOptions();
		try {
			// https://chromedriver.chromium.org/downloads
			System.out.println("getBrowserDriver" + driverLocation);
			System.setProperty("webdriver.chrome.driver", driverLocation);
			// ChromeOptions options = new ChromeOptions();
			// System.out.println("555");
			options.addArguments("-disable-infobars");
			options.addArguments("--headless");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.setExperimentalOption("useAutomationExtension", false);
			// System.out.println("8888");
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
			// System.out.println("1118888");
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		// System.out.println("getBrowserDriver:" + 88888);
		ChromeDriverService chromeDriverService = ChromeDriverService.createDefaultService();
		int port = chromeDriverService.getUrl().getPort();
		System.out.println("PORT---" + port);
		try {
			int chromeDriverProcessID = GetChromeDriverProcessID(port);
			System.out.println("detected chromedriver process id " + chromeDriverProcessID);
			System.out.println("detected chrome process id " + GetChromeProcesID(chromeDriverProcessID));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ChromeDriver(chromeDriverService, options);

	}

	public String scrapSite(ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto, User user,
			Office office) {
		setProps(scrappingSiteDetails.getProxyPort());
		try {

			System.out.println("MEM 3-" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
			// int one = 0;
			try {
				for (PatientScrapSearchDto data : dto.getDto()) {
					Thread thread = new Thread() {
						public void run() {
							System.out.println("Thread Running");

							WebDriver driver = getBrowserDriver();// new HtmlUnitDriver(true);// getBrowserDriver();
							try {
								boolean navigate = loginToSiteDelta(dto, driver);
								boolean issueNo = navigatetoMainSite(driver, navigate);
								System.out.println(" DeltaDentalServiceImpl ..888888888888- STARTED...");
								System.out.println("MEM 4-"
										+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
								PatientTemp d = parsePage(driver, data, siteName, issueNo, office);
								System.out.println("888888888888 -END " + d);
								if (d != null) {
									// Update the Data in Database
									updateDatainDB(d, office, user);
								}
								Thread.sleep(2000);
							} catch (Exception dri) {

							} finally {
								driver.close();
								driver.quit();
								scrappingSiteDetails.setRunning(false);
								ScrappingFullDataManagment manage = dataDoa.getScrappingFullDataManagmentData();
								ScrappingFullDataManagmentProcess manageP = dataDoa
										.getScrappingFullDataManagmentDataProcess(processId);
								manageP.setCount(manageP.getCount() - 1);
								manageP.setUpdatedBy(user);
								manageP.setUpdatedDate(new Date());
								dataDoa.updateScrappingFullDataManagmentProcess(manageP);
								if (manage.getProcessCount() > 0) {
									manage.setProcessCount(manage.getProcessCount() - 1);
									dataDoa.increasecrapCount(manage);
								}

								dataDoa.updateScrappingDetailsById(scrappingSiteDetails);
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
		}
		return "done";
	}

	@Override
	public Boolean call() throws Exception {
		// System.out.println("CCCCCCCCCCCCCCCCCCC");
		scrapSite(scrappingSiteDetails, dto, user, office);
		return true;
	}

	private PatientTemp parsePage(WebDriver driver, PatientScrapSearchDto sh, String webSiteName, boolean issueNo,
			Office office) throws InterruptedException {
		PatientTemp temp = new PatientTemp();
		try {
			temp.setDob(sh.getDob());
			temp.setPatientId(DateUtils.createPatientIdByDate(sh.getPatientId()));
			// temp.setFirstName(sh.getFirstName());
			temp.setWebsiteName(webSiteName);
			// temp.setLastName(sh.getLastName());
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
			issueNo = searchPatient(driver, temp, sh.getMemberId(), sh.getSsnNumber(), sh.getDob(), sh.getFirstName(),
					sh.getLastName());
			if (!issueNo) {
				temp.setStatus("Patient Not found.." + sh.getDob() + " " + sh.getMemberId());
				temp.setFirstName(sh.getFirstName());
				temp.setLastName(sh.getLastName());

				return temp;
			}
			temp.setStatus("Patient found..");
			temp.setFirstName(sh.getFirstName());
			temp.setLastName(sh.getLastName());
			populateMandatoryData(temp);
			createPatientDetailSetup(driver, temp, sh);
			// Click on MyPatient Link so that Next Patient can Start to be Searched
			List<WebElement> myPats = driver.findElements(By.tagName("a"));
			for (WebElement myPat : myPats) {
				if (myPat.getText() != null && myPat.getText().trim().equals("My patients")) {
					Thread.sleep(2000);
					myPat.click();

				}
			}
			Thread.sleep(2000);

			// Logic to fetch data from Site...
		} catch (Exception e) {
			// TODO: handle exception
		}
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

		try {
			fetchPatDetails(driver, temp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			temp.setStatus("Patient Fetch data issue");
		}

	}

	private boolean searchPatient(WebDriver driver, PatientTemp temp, String memberid, String ssn, String dob,
			String fName, String lName) throws InterruptedException {

		// String id = memberid.trim().equals("") ? ssn.trim() : memberid.trim();
		// id.equals("") ||
		if (dob.equals("") || fName.trim().equals("") || lName.trim().equals(""))
			return false;

		WebElement ini = driver.findElement(By.xpath(
				"/html/body/div[2]/form/div/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[10]/div/div[3]/table/tbody/tr/td[1]/span/input"));
		ini.clear();
		Thread.sleep(5000);

		WebElement validate = driver.findElement(By.xpath(
				"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[10]/div/div[3]/table/tbody/tr/td[3]/button"));
		ini.sendKeys(fName.trim() + " " + lName.trim());
		try {
			validate.click();
		} catch (Exception e) {
			return false;
		}
		Thread.sleep(5000);
		List<WebElement> rows = driver.findElements(By.className("data-table-row"));
		// boolean found=false;
		if (rows != null && rows.size() > 0) {
			try {
				for (WebElement row : rows) {

					List<WebElement> tds = row.findElements(By.tagName("td"));
					// List<WebElement>d=tds.get(7).findElements(By.tagName("span")).get(1).getText();
					String name = tds.get(7).findElements(By.tagName("span")).get(1).getText();// findElement(By.tagName("span")).getText();
					String[] dobA = tds.get(7).findElements(By.tagName("span")).get(0).findElements(By.tagName("span"))
							.get(3).getText().split("/");
					String dobS = ((dobA[0].length() == 1) ? ("0" + dobA[0]) : dobA[0]) + "/"
							+ ((dobA[1].length() == 1) ? ("0" + dobA[1]) : dobA[1]) + "/" + dobA[2];// + "/"
																									// +(dobA[0].length()==1)?"0"+dobA[1].length():"";
					// String mem =tds.get(12).findElements(By.tagName("span")).get(1).getText();
					// Ask for ID as not given any where
					if (name.equalsIgnoreCase((fName.trim() + " " + lName.trim())) &&
					// id .contains(mem) &&
							dobS.equals(dob)) {
						// found=true;
						row.findElement(By.className("table-col8")).findElements(By.tagName("a")).get(0).click();
						Thread.sleep(3000);
						return true;

					}
				}
			} catch (Exception e) {
				return false;
				// TODO: handle exception
			}
		} else {
			return false;
		}
		return false;

	}

	private void populateMandatoryData(PatientTemp temp) {
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();

		dtemp.setPlanAnnualMax(Constants.SCRAPPING_MANDATORY_WARNING); // 1
		dtemp.setPlanAnnualMaxRemaining(Constants.SCRAPPING_MANDATORY_WARNING);// 2
		// dtemp.setPlanIndividualDeductible();// 3
		// dtemp.setPlanIndividualDeductibleRemaining(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 4
		dtemp.setBasicPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 5
		// dtemp.setBasicSubjectDeductible();// 6
		dtemp.setMajorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 7
		// dtemp.setMajorSubjectDeductible();// 8
		dtemp.setEndodonticsPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 9
		// dtemp.setEndoSubjectDeductible();// 10

		dtemp.setPerioSurgeryPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 11
		// dtemp.setPerioSurgerySubjectDeductible();// 12

		dtemp.setPreventivePercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 13
		dtemp.setDiagnosticPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 14
		// dtemp.setpAXRaysPercentage();// 15
		dtemp.setMissingToothClause(Constants.SCRAPPING_MANDATORY_WARNING);// 16
		// 17 18
		// dtemp.setNightGuardsD9944Fr();// 19 //Cross Check

		// dtemp.setBasicWaitingPeriod();// 20 in DOC
		// dtemp.setMajorWaitingPeriod();// 21 in DOC

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
		// 32 missing
		// dtemp.setFlourideD1208FL("");//33 not mandatory
		// dtemp.setFlourideAgeLimit("");//34 not mandatory
		// dtemp.setVarnishD1206FL("");//35 not mandatory
		// dtemp.setVarnishD1206AgeLimit("");//36 not mandatory
		// dtemp.setSealantsD1351Percentage();// 37
		// dtemp.setSealantsD1351FL("");//38
		// dtemp.setSealantsD1351AgeLimit("")//39
		// dtemp.setSealantsD1351PrimaryMolarsCovered();// 40
		// dtemp.setSealantsD1351PreMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 41
		// dtemp.setSealantsD1351PermanentMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 42
		// dtemp.setProphyD1110FL("");//43
		// dtemp.setProphyD1120FL("");//44
		// 45 missing
		// dtemp.setsRPD4341Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 46
		// dtemp.setsRPD4341FL("");//47
		// 48
		// 49
		// dtemp.setPerioMaintenanceD4910Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 50
		// dtemp.setPerioMaintenanceD4910FL("");//51
		// dtemp.setPerioMaintenanceD4910AltWProphyD0110(Constants.SCRAPPING_MANDATORY_WARNING);// 52
		// dtemp.setFMDD4355Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 53
		// dtemp.setfMDD4355FL("");//54
		// dtemp.setGingivitisD4346Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 55
		// dtemp.setGingivitisD4346FL("");//56
		// dtemp.setNitrousD9230Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 57
		// dtemp.setiVSedationD9243Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 58
		// dtemp.setiVSedationD9248Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 59
		// dtemp.setExtractionsMinorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 60
		// dtemp.setExtractionsMajorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 61
		// dtemp.setCrownLengthD4249Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 62
		// dtemp.setCrownLengthD4249FL("");//63
		//dtemp.setAlveoD7310CoveredWithEXT("");//64
		// dtemp.setAlveoD7311FL("");//65
		//dtemp.setAlveoD7311CoveredWithEXT("");//66
		// dtemp.setAlveoD7310FL("");//67
		// dtemp.setCompleteDenturesD5110D5120FL("");//68
		// dtemp.setImmediateDenturesD5130D5140FL("");//69
		// dtemp.setPartialDenturesD5213D5214FL("");//70
		// dtemp.setInterimPartialDenturesD5214FL("");//71
		// 72
		// dtemp.setBoneGraftsD7953FL("");//73
		// dtemp.setImplantCoverageD6010Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 74
		// dtemp.setImplantCoverageD6057Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 75
		// dtemp.setImplantCoverageD6190Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 76
		// dtemp.setImplantSupportedPorcCeramicD6065Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 77
		// dtemp.setPostCompositesD2391Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 78
		// dtemp.setPostCompositesD2391FL("");//79
		// 80
		// dtemp.setCrownsD2750D2740Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 81
		// dtemp.setCrownsD2750D2740FL("");//82
		// 83
		//dtemp.setNightGuardsD9940Percentage("");// 84
		// dtemp.setD9310Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 85
		// dtemp.setD9310FL("");//86

		// dtemp.setBuildUpsD2950Covered(Constants.SCRAPPING_MANDATORY_WARNING);// 87
		// dtemp.setBuildUpsD2950FL("");//88
		// 89
		// dtemp.setOrthoPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 90
		dtemp.setOrthoMax(Constants.SCRAPPING_MANDATORY_WARNING);// 91
		dtemp.setOrthoAgeLimit(Constants.SCRAPPING_MANDATORY_WARNING);// 92 benefit
		//dtemp.setOrthoSubjectDeductible(""); //93
		//dtemp.setFillingsBundling("");//94
		//95 //96 //97 //99 //100 //101
		// dtemp.setBridges1(Constants.SCRAPPING_MANDATORY_WARNING);// 102
		// dtemp.setBridges2("");//103
		// 104
		// dtemp.setDen5225Per(Constants.SCRAPPING_MANDATORY_WARNING);// 105
		// dtemp.setDenf5225FR("");//107

		// dtemp.setDen5226Per();// 106
		// dtemp.setDenf5226Fr("");//108
		// dtemp.setDiagnosticSubDed();// 109

		// dtemp.setImplantsFrD6010("");//110
		// dtemp.setImplantsFrD6057("");//111
		// dtemp.setImplantsFrD6065("");//112
		// dtemp.setImplantsFrD6190("");//113
		// dtemp.setNightGuardsD9945Percentage();// 114
		dtemp.setOrthoRemaining(Constants.SCRAPPING_MANDATORY_WARNING);// 115 --
		// dtemp.setOrthoWaitingPeriod("");//116
		// dtemp.setpAXRaysSubDed();// 117
		// dtemp.setPreventiveSubDed();// 118
		// 119
		// dtemp.setFmxPer();// 120
		// dtemp.setNightGuardsD9944Fr("");//121
		// dtemp.setNightGuardsD9945Fr("");//122

		// 123 //124 //125 //126
		// dtemp.setPlanDependentsCoveredtoAge();// 127
		// 128 129 130
		// dtemp.setPlanAssignmentofBenefits();// 131
		// 132 133 134 135
		dtemp.setEmployerName(Constants.SCRAPPING_MANDATORY_WARNING);//138
		dtemp.setGroup(Constants.SCRAPPING_MANDATORY_WARNING);//139
		dtemp.setInsAddress(Constants.SCRAPPING_MANDATORY_WARNING);//140
		dtemp.setPayerId(Constants.SCRAPPING_MANDATORY_WARNING);//141
		dtemp.setPolicyHolderDOB(Constants.SCRAPPING_MANDATORY_WARNING);//142
		dtemp.setPlanTermedDate(Constants.SCRAPPING_MANDATORY_WARNING);//143
		dtemp.setPlanEffectiveDate(Constants.SCRAPPING_MANDATORY_WARNING);//144
		dtemp.setGeneralDateIVwasDone(Constants.SCRAPPING_MANDATORY_WARNING);//145
		dtemp.setPolicyHolderDOB(Constants.SCRAPPING_MANDATORY_WARNING);//146
		
		
		// Debug
	}

	private void fetchPatDetails(WebDriver driver, PatientTemp temp) throws InterruptedException {
		Thread.sleep(8000);
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();

		// WebElement divOffBenfitm=
		// driver.findElement(By.xpath("/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/div[1]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/div[1]/div/div/div/table/tbody/tr/td[2]/div"));
		List<WebElement> divOfMaximum = driver.findElements(By.tagName("table"));
		String dd = "";
		int age = 0;
		try {
			age = DateUtils.calculateAgeYMD(temp.getDob(), false)[0];
		} catch (Exception e) {
			// TODO: handle exception
		}

		String ageLimtId = "";
		dtemp.setInsName("Denta Dental of Tx");
		dtemp.setInsContact("8005212227");
		dtemp.setcSRName("Scraping Tool");//137
		dtemp.setTaxId(taxId);//136
		dtemp.setGeneralDateIVwasDone(Constants.SIMPLE_DATE_FORMAT_IVF.format(new Date()));
		for (WebElement divOffMax : divOfMaximum) {

			try {

				if (divOffMax.getAttribute("summary").equals("Benefits and Covered Services")) {
					dtemp.setBasicWaitingPeriod("No");// 20
					dtemp.setMajorWaitingPeriod("No");// 21
					dtemp.setOrthoWaitingPeriod("No");// 116

					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Restorative")) {
							try {
								List<WebElement> tds = tr.findElements(By.className("x26c"));
								String dt = tds.get(1).getText();
								String de = tds.get(2).getText();// need Logic
								dtemp.setBasicWaitingPeriod(DateUtils.getDiffBetweenMonths(dt, de));// 20
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Prosthodontics; Removable")) {
							try {
								List<WebElement> tds = tr.findElements(By.className("x26c"));
								String dt = tds.get(1).getText();
								String de = tds.get(2).getText();
								dtemp.setMajorWaitingPeriod(DateUtils.getDiffBetweenMonths(dt, de));// 21
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Orthodontics")) {
							try {
								List<WebElement> tds = tr.findElements(By.className("x26c"));
								String dt = tds.get(1).getText();
								String de = tds.get(2).getText();
								dtemp.setOrthoWaitingPeriod(DateUtils.getDiffBetweenMonths(dt, de));// 116

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					//
				}

				if (divOffMax.getAttribute("summary").equals("Elligibility and Benefits Summary")) {
					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {
						try {
							if (tr.getText().startsWith("Program type:")) {

								List<WebElement> tds = tr.findElements(By.className("x26b"));
								dtemp.setPlanType(tds.get(0).getText());

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Plan name:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));//138
								dtemp.setEmployerName(tds.get(0).getText());

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Plan number:")) {

								List<WebElement> tds = tr.findElements(By.className("x26b"));//139
								dtemp.setGroup(tds.get(0).getText());

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						try {
							if (tr.getText().startsWith("Enrollee name:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));//142
								dtemp.setPolicyHolder(tds.get(0).getText());

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Date of birth:")) {

								List<WebElement> tds = tr.findElements(By.className("x26b"));
								dtemp.setPolicyHolderDOB(tds.get(0).getText());//146
								String[] s = tds.get(0).getText().split("/");
								try {
									dtemp.setPolicyHolderDOB(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]));//146

								} catch (Exception e) {
									// TODO: handle exception
									//dtemp.setPlanEffectiveDate("");
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Effective date:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));
								dtemp.setPlanEffectiveDate(tds.get(0).getText());//144
								String[] s = tds.get(0).getText().split("/");
								try {
									dtemp.setPlanEffectiveDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]));//144

								} catch (Exception e) {
									// TODO: handle exception
									//dtemp.setPlanEffectiveDate("");
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("End date:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));
								dtemp.setPlanTermedDate(tds.get(0).getText());//143
								String[] s = tds.get(0).getText().split("/");
								try {
									dtemp.setPlanTermedDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]));//143

								} catch (Exception e) {
									// TODO: handle exception
									//dtemp.setPlanTermedDate("");
								}
								

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					//
				}
				if (divOffMax.getAttribute("summary").equals("Maximums")) {
					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Contract Individual Maximum")) {
                            try { 
							List<WebElement> tds = tr.findElements(By.className("x264"));
							dtemp.setPlanAnnualMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 1
							dtemp.setPlanAnnualMaxRemaining(tds.get(1).getText().replace("$", "").replace(",", ""));// 2
                            }catch (Exception e) {
                            	e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Lifetime Individual Maximum")
								&& tr.getText().contains("Orthodontics")) {
                            try {
							List<WebElement> tds = tr.findElements(By.className("x264"));
							dtemp.setOrthoRemaining(tds.get(1).getText().replace("$", "").replace(",", ""));// 115
                            } catch (Exception e) {
                            	e.printStackTrace();
							}

						}
						if (tr.getText().startsWith("Lifetime Individual Maximum")
								&& tr.getText().contains("Orthodontics")) {
                            try {
							List<WebElement> tds = tr.findElements(By.className("x264"));
							dtemp.setOrthoMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 91
                            } catch (Exception e) {
                            	e.printStackTrace();
							}

						}
					}
					//
				}
				if (divOffMax.getAttribute("summary").equals("Deductibles")) {
					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Contract Individual Deductible")) {
                            try {
							List<WebElement> tds = tr.findElements(By.className("x264"));
							dtemp.setPlanIndividualDeductible(tds.get(0).getText().replace("$", "").replace(",", ""));// 3
							dtemp.setPlanIndividualDeductibleRemaining(
									tds.get(1).getText().replace("$", "").replace(",", ""));// 4
							if (tr.getText().contains("Preventive")) {
								dtemp.setPreventiveSubDed("Yes");// 118
							} else {
								dtemp.setPreventiveSubDed("No");// 118
							}
							if (tr.getText().contains("Diagnostic")) {
								dtemp.setDiagnosticSubDed("Yes");// 109
							} else {
								dtemp.setDiagnosticSubDed("No");// 109
							}
							if (tr.getText().contains("Restorative")) {
								dtemp.setBasicSubjectDeductible("Yes");// 6
							} else {
								dtemp.setBasicSubjectDeductible("No");// 6
							}
							if (tr.getText().contains("Prosthodontics")) {
								dtemp.setMajorSubjectDeductible("Yes");// 8
							} else {
								dtemp.setMajorSubjectDeductible("No");// 8
							}
							if (tr.getText().contains("Endodontics")) {
								dtemp.setEndoSubjectDeductible("Yes");// 10
							} else {
								dtemp.setEndoSubjectDeductible("No");// 10
							}
							if (tr.getText().contains("Endodontics")) {
								dtemp.setPerioSurgerySubjectDeductible("Yes");// 12
							} else {
								dtemp.setPerioSurgerySubjectDeductible("No");// 12
							}
                            } catch (Exception e) {
                            	e.printStackTrace();
							}

						}

					}
					//
				}
				if (divOffMax.getAttribute("summary").equals("Other Provisions")) {
					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Missing Tooth Coverage")) {
                            try {
							List<WebElement> tds = tr.findElements(By.className("x264"));
							dtemp.setMissingToothClause(tds.get(0).getText().replace("$", "").replace(",", ""));// 16
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Orthodontic Age Limit")) {
							try {
							// keep id
							ageLimtId = tr.getAttribute("id");// 92
							// 17
							// 18
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Child Covered to Age")) {
							try {
							
							List<WebElement> tds = tr.findElements(By.className("x264"));
							dtemp.setPlanDependentsCoveredtoAge(tds.get(0).getText());// 127
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Assignment of Benefits")) {
						    try {
							List<WebElement> tds = tr.findElements(By.className("x264"));
							if (tds.get(0)!=null && tds.get(0).getText()!=null && tds.get(0).getText().toLowerCase().contains("group accepts assignment of benefits"))
								dtemp.setPlanAssignmentofBenefits("Yes");// 131
							else
								dtemp.setPlanAssignmentofBenefits("No");// 131
						    } catch (Exception e) {
						    	e.printStackTrace();
							}
						}
					}
					//
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		List<WebElement> spans= driver.findElements(By.className("x24k"));
		String claim="";
		for(WebElement span:spans) {
			claim=claim+" "+span.getText().trim();
		}
		if (!claim.equals("")) {
			dtemp.setInsAddress(claim);//140
			try {
				dtemp.setPayerId(claim.split("Claim payer ID :")[1]);//141
				dtemp.setInsAddress(claim.split("Claim payer ID :")[0]);//141
			}catch(Exception p) {
				
			}
		}
		if (ageLimtId.equals("")) {
			try {
				List<WebElement> aas = driver.findElement(By.id("ageLimtId")).findElements(By.tagName("a"));
				for (WebElement aa : aas) {
					if (aa.getText() != null && aa.getText().equals("Click here")) {
						aa.click();
						Thread.sleep(3000);
						List<WebElement> tabs = driver.findElements(By.tagName("table"));
						try {
							for (WebElement tab : tabs) {
								if (tab.getAttribute("summary") != null
										&& tab.getAttribute("summary").equals("Ortho Age informat")) {
									dtemp.setOrthoAgeLimit(tab.findElements(By.tagName("td")).get(1).getText());// 92
									break;
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						List<WebElement> buts = driver.findElements(By.tagName("button"));
						for (WebElement but : buts) {
							if (but.getText() != null && but.getText().equals("Close")) {
								but.click();
								Thread.sleep(3000);
								break;
							}
						}
						break;

					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		String planType = dtemp.getPlanType();
		openSideBarFirst(driver, "Benefit details", true,
				new String[] { "Endodontics", "Periodontics", "Preventive", "Diagnostic","Prosthodontics; Removable" }, true, temp);
		
		dtemp.setEndodonticsPercentage(fetchBenefitDetails("", temp, driver, "", "Prosthodontics; Removable",
				"", planType, false, true));// 7
		
		
		dtemp.setEndodonticsPercentage(fetchBenefitDetails("", temp, driver, "", "Endodontics",
				"", planType, false, true));// 9
		// 10
		dtemp.setPerioSurgeryPercentage(fetchBenefitDetails("", temp, driver, "", "Periodontics",
				"Surgical Services", planType, false, true));// 11
		// 12
		dtemp.setPreventivePercentage(fetchBenefitDetails("", temp, driver, "", "Preventive", "",
				planType, false, true));// 13
		dtemp.setDiagnosticPercentage(fetchBenefitDetails("", temp, driver, "", "Diagnostic",
				"", planType, false, true));// 14
		dtemp.setpAXRaysPercentage(fetchBenefitDetails("D0220", temp, driver, "", "Diagnostic",
				"Radiographs/Diagnostic Imaging", planType, true, true));// 15

		System.out.println("Benefit search ....");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,-100)");
		openSideBarFirst(driver, "Benefit search", false, null, false, temp);
		HashMap<String, DentaBenefitScrapDto> benefitInfoMap = new HashMap<>();
		DentaBenefitScrapDto dt = null;
		HashMap<String, String> values = new HashMap<>();

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 51, 52
		dt.setMandatory(new boolean[] { false, false });
		benefitInfoMap.put("D4910", dt);// a

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 55 56
		dt.setMandatory(new boolean[] { false, false });
		benefitInfoMap.put("D4346", dt);// b

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });
		dt.setMandatory(new boolean[] { true, true });// 5 78 79
		benefitInfoMap.put("D2391", dt);// c
		//////////////////////////////////////

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 7 81 82
		dt.setMandatory(new boolean[] { true, true });
		benefitInfoMap.put("D2740", dt);// d

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 19 121
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D9944", dt);// e

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 22
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D2930", dt);// f

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 23
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D2931", dt);// g

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 24
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D0120", dt);// h

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 25
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D0140", dt);// i

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 26
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);

		benefitInfoMap.put("D0145", dt);// j

		String[] info = fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, false, null);// call for
																											// first 10
		benefitInfoMap = new HashMap<>();

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 27
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D0150", dt);// a

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 28
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D0272", dt);// b

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 29
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D0220", dt);// c

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 30
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D0230", dt);// d

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 31 120
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D0210", dt);// e

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitAgeLimit, benefitLimitation });// 33 34
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D1208", dt);// f

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitAgeLimit, benefitLimitation });// 35 36
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D1206", dt);// g

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitAgeLimit, benefitLimitation });// 37 38 39 40 41 42
		dt.setMandatory(new boolean[] { false, false, false });
		dt.setAge(age);
		benefitInfoMap.put("D1351", dt);// h

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 43
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D1110", dt);// i

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 44
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D1120", dt);// j

		fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info);// call for second 10
		benefitInfoMap = new HashMap<>();
		// 45
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 47 48 49
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4341", dt);// a

		// 53
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 54 55
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4355", dt);// b

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 57
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D9230", dt);// c

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 58
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D9243", dt);// d

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 59
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D9248", dt);// e

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 60
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D7210", dt);// f

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 61
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D7240", dt);// g

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 62 63
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4249", dt);// h

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitAlveoloplasty, benefitLimitation });// 64 65
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D7311", dt);// i

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitAlveoloplasty, benefitLimitation });// 66 67
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D7310", dt);// j

		fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info);// call for third 10
		benefitInfoMap = new HashMap<>();

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 68
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D5110", dt);// a

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 69
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D5130", dt);// b

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 70
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D5213", dt);// c

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 71
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D5214", dt);// d

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 73
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D7953", dt);// e

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 74 110
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D6010", dt);// f

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 75 111
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D6057", dt);// g

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 76 113
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D6190", dt);// h

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 77 112
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D6065", dt);// i

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 105
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D9945", dt);// j

		fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info);// call for fourth 10
		benefitInfoMap = new HashMap<>();

		// 80
		// 83
		// 84

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 85 86
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D9310", dt);// a

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 87 88
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D2950", dt);// b

		// 89
		// 93
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 90
		dt.setMandatory(new boolean[] { true });
		dt.setAge(age);
		benefitInfoMap.put("D8090", dt);// c

		// 94 95
		// 96 97 98 99 100 101

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 102 103
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D6245", dt);// d

		// 104

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 105 107
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D5225", dt);// e

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 106 108
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D5226", dt);// f

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 114 122
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D9945", dt);// g

		// 117
		// 119
		// 123 124 125 126
		// 128 129 130
		// 132 133 134 135

		fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info);// call for Fifth 10
		benefitInfoMap = new HashMap<>();

		dtemp.setBasicPercentage(getBenefitProcedureValueFromMap("D2391", benefitContract, values, true));// 5
		
		dtemp.setMajorPercentage(getBenefitProcedureValueFromMap("D2740", benefitContract, values, true));// 7

		// 8
		dtemp.setNightGuardsD9944Fr(getBenefitProcedureValueFromMap("D9944", benefitLimitation, values, true));// 19

		dtemp.setsSCD2930FL(getBenefitProcedureValueFromMap("D2930", benefitLimitation, values, true));// 22

		dtemp.setsSCD2931FL(getBenefitProcedureValueFromMap("D2931", benefitLimitation, values, true));// 23

		dtemp.setExamsD0120FL(getBenefitProcedureValueFromMap("D0120", benefitLimitation, values, true));// 24

		dtemp.setExamsD0140FL(getBenefitProcedureValueFromMap("D0140", benefitLimitation, values, true));// 25

		dtemp.seteExamsD0145FL(getBenefitProcedureValueFromMap("D0145", benefitLimitation, values, true));// 26

		dtemp.setExamsD0150FL(getBenefitProcedureValueFromMap("D0150", benefitLimitation, values, true));// 28

		dtemp.setxRaysPAD0220FL(getBenefitProcedureValueFromMap("D0220", benefitLimitation, values, true));// 29

		dtemp.setxRaysPAD0230FL(getBenefitProcedureValueFromMap("D0230", benefitLimitation, values, true));// 30

		dtemp.setxRaysFMXFL(getBenefitProcedureValueFromMap("D0230", benefitLimitation, values, true));// 31
		// 32

		dtemp.setFlourideD1208FL(getBenefitProcedureValueFromMap("D1208", benefitLimitation, values, true));// 33

		dtemp.setFlourideAgeLimit(getBenefitProcedureValueFromMap("D1208", benefitAgeLimit, values, true));// 34

		dtemp.setVarnishD1206FL(getBenefitProcedureValueFromMap("D1206", benefitLimitation, values, true));// 35

		dtemp.setVarnishD1206AgeLimit(getBenefitProcedureValueFromMap("D1206", benefitAgeLimit, values, true));// 36

		dtemp.setSealantsD1351Percentage(getBenefitProcedureValueFromMap("D1351", benefitContract, values, true));// 37

		dtemp.setSealantsD1351FL(getBenefitProcedureValueFromMap("D1351", benefitContract, values, true));// 38

		dtemp.setSealantsD1351AgeLimit(getBenefitProcedureValueFromMap("D1351", benefitAgeLimit, values, true));// 39

		String lim = getBenefitProcedureValueFromMap("D1351", benefitLimitation + "_TOOTH", values, true);
		System.out.println("LIMITS--" + lim);
		if (lim != null)
			lim = lim.replace(" ", "");
		dtemp.setSealantsD1351PrimaryMolarsCovered(FreqencyUtils.checkForteehIntext(siteName, lim, "A,B,I,J,K,L,S,T"));// 40
		dtemp.setSealantsD1351PrimaryMolarsCovered(
				FreqencyUtils.checkForteehIntext(siteName, lim, "4,5,12,13,20,21,28,29"));// 41
		dtemp.setSealantsD1351PermanentMolarsCovered(
				FreqencyUtils.checkForteehIntext(siteName, lim, "2,3,14,15,18,19,30,31"));// 42

		dtemp.setProphyD1110FL(getBenefitProcedureValueFromMap("D1110", benefitLimitation, values, true));// 43
		// 45
		dtemp.setsRPD4341Percentage(getBenefitProcedureValueFromMap("D1120", benefitContract, values, true));// 46

		dtemp.setsRPD4341FL(getBenefitProcedureValueFromMap("D4341", benefitLimitation, values, true));// 47

		// dtemp.setsRPD4341QuadsPerDay(getBenefitProcedureValueFromMap("D4341",
		// benefitLimitation, values, true));// 48
		// 49
		dtemp.setPerioMaintenanceD4910Percentage(
				getBenefitProcedureValueFromMap("D4910", benefitContract, values, true));// 50

		dtemp.setPerioMaintenanceD4910Percentage(
				getBenefitProcedureValueFromMap("D4910", benefitLimitation, values, true));// 51
		// 52
		dtemp.setFMDD4355Percentage(getBenefitProcedureValueFromMap("D4355", benefitContract, values, true));// 53

		dtemp.setfMDD4355FL(getBenefitProcedureValueFromMap("D4355", benefitLimitation, values, true));// 54

		dtemp.setGingivitisD4346Percentage(getBenefitProcedureValueFromMap("D4346", benefitContract, values, true));// 55

		dtemp.setGingivitisD4346FL(getBenefitProcedureValueFromMap("D4346", benefitLimitation, values, true));// 56

		dtemp.setNitrousD9230Percentage(getBenefitProcedureValueFromMap("D9230", benefitContract, values, true));// 57

		dtemp.setiVSedationD9243Percentage(getBenefitProcedureValueFromMap("D9243", benefitContract, values, true));// 58

		dtemp.setiVSedationD9248Percentage(getBenefitProcedureValueFromMap("D9248", benefitContract, values, true));// 59

		dtemp.setExtractionsMinorPercentage(getBenefitProcedureValueFromMap("D7210", benefitContract, values, true));// 60

		dtemp.setExtractionsMajorPercentage(getBenefitProcedureValueFromMap("D7240", benefitContract, values, true));// 61

		dtemp.setCrownLengthD4249Percentage(getBenefitProcedureValueFromMap("D4249", benefitContract, values, true));// 62

		dtemp.setCrownLengthD4249FL(getBenefitProcedureValueFromMap("D4249", benefitLimitation, values, true));// 63

		lim = getBenefitProcedureValueFromMap("D7311", benefitAlveoloplasty, values, true);
		if (lim.toLowerCase().contains("alveoloplasty in conjunction with extractions"))
			lim = "Yes";
		else
			lim = "No";

		dtemp.setAlveoD7311CoveredWithEXT(lim);// 64

		dtemp.setAlveoD7311FL(getBenefitProcedureValueFromMap("D7311", benefitLimitation, values, true));// 65

		lim = getBenefitProcedureValueFromMap("D7310", benefitAlveoloplasty, values, true);
		if (lim.toLowerCase().contains("alveoloplasty in conjunction with extractions"))
			lim = "Yes";
		else
			lim = "No";

		dtemp.setAlveoD7310CoveredWithEXT(lim);// 66

		dtemp.setAlveoD7310FL(getBenefitProcedureValueFromMap("D7311", benefitLimitation, values, true));// 67

		dtemp.setCompleteDenturesD5110D5120FL(
				getBenefitProcedureValueFromMap("D5110", benefitLimitation, values, true));// 68

		dtemp.setImmediateDenturesD5130D5140FL(
				getBenefitProcedureValueFromMap("D5130", benefitLimitation, values, true));// 69

		dtemp.setPartialDenturesD5213D5214FL(getBenefitProcedureValueFromMap("D5213", benefitLimitation, values, true));// 70

		dtemp.setInterimPartialDenturesD5214FL(
				getBenefitProcedureValueFromMap("D5214", benefitLimitation, values, true));// 71
		// 72
		dtemp.setBoneGraftsD7953FL(getBenefitProcedureValueFromMap("D7953", benefitLimitation, values, true));// 73

		dtemp.setImplantCoverageD6010Percentage(
				getBenefitProcedureValueFromMap("D6010", benefitContract, values, true));// 74

		dtemp.setImplantCoverageD6190Percentage(
				getBenefitProcedureValueFromMap("D6057", benefitContract, values, true));// 75

		dtemp.setImplantCoverageD6190Percentage(
				getBenefitProcedureValueFromMap("D6190", benefitContract, values, true));// 76

		dtemp.setImplantSupportedPorcCeramicD6065Percentage(
				getBenefitProcedureValueFromMap("D6065", benefitContract, values, true));// 77

		dtemp.setPostCompositesD2391Percentage(getBenefitProcedureValueFromMap("D2391", benefitContract, values, true));// 78

		dtemp.setPostCompositesD2391FL(getBenefitProcedureValueFromMap("D2391", benefitLimitation, values, true));// 79
		// 80
		dtemp.setCrownsD2750D2740Percentage(getBenefitProcedureValueFromMap("D2740", benefitContract, values, true));// 81

		dtemp.setCrownsD2750D2740FL(getBenefitProcedureValueFromMap("D2740", benefitLimitation, values, true));// 82
		// 83 84

		dtemp.setD9310Percentage(getBenefitProcedureValueFromMap("D9310", benefitContract, values, true));// 85

		dtemp.setD9310FL(getBenefitProcedureValueFromMap("D9310", benefitLimitation, values, true));// 86

		dtemp.setBuildUpsD2950Covered(getBenefitProcedureValueFromMap("D2950", benefitContract, values, true));// 87

		dtemp.setBuildUpsD2950FL(getBenefitProcedureValueFromMap("D2950", benefitLimitation, values, true));// 88
		// 89
		dtemp.setOrthoPercentage(getBenefitProcedureValueFromMap("D8090", benefitContract, values, true));// 90

		// 91 qweawe

		dtemp.setOrthoAgeLimit(getBenefitProcedureValueFromMap("D8090", benefitAgeLimit, values, true));// 92
		// 93
		dtemp.setBridges1(getBenefitProcedureValueFromMap("D6245", benefitContract, values, true));// 94

		dtemp.setBridges2(getBenefitProcedureValueFromMap("D6245", benefitLimitation, values, true));// 95
		// 96 97 98 99 100 101 102 103 104 105
		dtemp.setNightGuardsD9945Percentage(getBenefitProcedureValueFromMap("D9945", benefitContract, values, true));// 105

		// 108 110

		dtemp.setFmxPer(getBenefitProcedureValueFromMap("D0210", benefitContract, values, true));// 111

		// CHECK FOR FREQENCY CORRECT LOGIC....

		// By default we have Diagnostic open..
		// //template1:r1:1:r1:1:r1:0:t1:3:commandLink1

		System.out.println("dd" + dd);
		Set<PatientHistoryTemp> hisSet = temp.getPatientHistory();
		openSideBarFirst(driver, "Treatment history", false, null, false, temp);
		fetchHistoryformation(driver, hisSet);
		System.out.println("999");
		// int ccc = 0;
		// if (ccc == 0)
		// return;

		// 119
		// 123 //124 //125 //126

		dtemp.setAptDate("");

	}

	private String getBenefitProcedureValueFromMap(String key, String type, HashMap<String, String> values,
			boolean mandatory) {

		String val = values.get(key + "_" + type);
		System.out.println("KEY--" + key);
		System.out.println("VAL--" + val);
		System.out.println("TYPE--" + type);
		String ret = "";
		if (mandatory)
			ret = Constants.SCRAPPING_MANDATORY_WARNING;

		if (val != null) {
			if (type.equals(benefitAgeLimit))
				ret = val;
			else if (type.equals(benefitAlveoloplasty))
				ret = val;
			else if (type.equals(benefitContract))
				ret = val.split(" ")[0];
			else if (type.equals(benefitLimitation)) {
				ret = val.split("----")[0];
				if (ret.equals("") && val.split("----").length > 1)
					ret = val.split("----")[1];
			} else if (type.equals(benefitLimitation + "_TOOTH")) {
				ret = val;
			}
		}
		System.out.println("VAL RET--" + val);

		return ret;
	}

	private void openSideBarFirst(WebDriver driver, String sideBarName, boolean child, String[] chidNames,
			boolean bluffCode, PatientTemp temp) throws InterruptedException {
		// Thread.sleep(5000);
		// if (bluffCode) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String bluff = "var script = document.createElement('script');script.innerHTML = 'function highlightRow(e){console.log(999)};function unhighlightRow(e){console.log(999)}';document.head.appendChild(script)";
		js.executeScript(bluff);
		// }
		// System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
		Thread.sleep(3000);
		// WebElement dd =
		// driver.findElement(By.id("template1:r1:1:r1:1:r1:0:r4:0:cellFormat3"));
		// Actions actions = new Actions(driver);
		// actions.moveToElement(dd).perform();
		// Thread.sleep(3000);
		List<WebElement> lis = driver.findElements(By.tagName("a"));
		// int x=0;
		try {
			for (WebElement li : lis) {

				// System.out.println(li.getText());
				if (li.getText().equals(sideBarName)) {
					// System.out.println(driver.getCurrentUrl());
					// driver.manage().timeouts().setScriptTimeout(200, TimeUnit.SECONDS);
					// Actions actions = new Actions(driver);

					// actions.moveToElement(li).click();
					String counterLink = li.getAttribute("id");
					// System.out.println(li.getAttribute("id"));
					// asdadad anscetor logic
					//
					li.click();
					Thread.sleep(5000);
					if (child && chidNames != null) {
						// Thread.sleep(2000);
						HashMap<String, String> counterElementMap = new HashMap<>();

						List<WebElement> lisaa = driver.findElements(By.tagName("a"));
						for (WebElement lisa : lisaa) {

							// li = driver.findElement(By.id(counterLink));
							// WebElement parent = li.findElement(By.xpath(".."));
							// System.out.println(parent);
							// Thread.sleep(8000);
							for (String n : chidNames) {

								// System.out.println(parent.getAttribute("id"));
								// List<WebElement> aa = lisa.findElements(By.tagName("a"));
								// for (WebElement a : aa) {
								// System.out.println(a.getText());
								if (lisa.getText() != null && lisa.getText().equals(n)) {
									// System.out.println(lisa.getAttribute("id"));
									// System.out.println(n);
									counterElementMap.put(n, lisa.getAttribute("id"));
									temp.setCounterElementMap(counterElementMap);
									break;
								}
								// }
							}
							if (counterElementMap.size() == chidNames.length)
								break;
						}
					}

					// Object dd1
					// =js.executeScript("document.getElementById('"+li.getAttribute("id")+"').click()");
					// li.click();
					// System.out.println(driver.getCurrentUrl());
					// Thread.sleep(5000);
					// driver.navigate().refresh();
					// System.out.println(li.getAttribute("href"));
					// driver.navigate().to(li.getAttribute("href"));
					break;
				}
				// x++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	private void fetchHistoryformation(WebDriver driver, Set<PatientHistoryTemp> setHis) throws InterruptedException {
		Thread.sleep(3000);
		System.out.println(driver.getCurrentUrl());

		List<WebElement> tables = driver.findElements(By.tagName("table"));

		String sum = "";
		for (WebElement table : tables) {
			try {
				sum = table.getAttribute("summary");
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (sum.equals("Maximums")) {
				// System.out.println("11");

				List<WebElement> trs = table.findElements(By.tagName("tr"));
				trs.remove(0);
				PatientHistoryTemp t = null;
				for (WebElement tr : trs) {
					List<WebElement> tds = tr.findElements(By.tagName("td"));
					// t.setHistoryCode(tds.get(0).getText().trim());// Code
					String code = tds.get(0).getText().trim();
					// System.out.println("CCCCCCCCCC" + code);
					try {
						// tds.get(3).getText().trim();//multiple Codes..
						String[] dts = tds.get(4).getText().trim().split(",");// dates
						String tooth = tds.get(5).getText().trim();// multiple tooth
						String surface = tds.get(7).getText().trim();// multiple surface
						for (String dt : dts) {
							t = new PatientHistoryTemp();
							t.setHistoryCode(code);
							t.setHistoryDOS(dt);
							t.setHistoryTooth(tooth);
							t.setHistorySurface(surface);
							setHis.add(t);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				// break;
			}

		}
		// Thread.sleep(1000);

	}

	/*
	private String fetchBenefitDetails(String name, PatientTemp temp, WebDriver driver, String type, String firstA,
			String secondA, String planType, boolean mainOpen, boolean mandatory) throws InterruptedException {
		System.out.println("fetchBenefitDetails -" + name);
		System.out.println("IN-" + name + "-" + new Date());
		String value = Constants.SCRAPPING_NOT_FOUND;
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		else
			value = "";
		Thread.sleep(1000);
		try {
			if (!mainOpen) {
				// System.out.println(firstA);
				// System.out.println(counterElementMap.get(firstA));

				driver.findElement(By.id(temp.getCounterElementMap().get(firstA))).click();
				Thread.sleep(7000);
			}
			List<WebElement> as = driver.findElements(By.tagName("a"));
			for (WebElement a : as) {
				if (a.getText().equals(secondA)) {
					a.click();
					Thread.sleep(7000);
					break;
				}
			}

			List<WebElement> tdss = driver.findElements(By.tagName("td"));
			WebElement table = null;
			for (WebElement td : tdss) {
				if (td.getText().equals(name)) {
					table = td.findElement(By.xpath("..")).findElement(By.xpath(".."));
					break;
				}

			}
			if (table == null)
				return Constants.SCRAPPING_NOT_FOUND;
			// WebElement table =driver.findElement(By.id(thirdA));

			// boolean found = false;
			try {
				// String clas=table.getAttribute("class");
				// System.out.println("DD"+clas);
				// if (clas!=null && clas.equalsIgnoreCase("x22t")) {
				List<WebElement> ths = table.findElements(By.tagName("th"));
				// count the Th
				// if (type.equals(benefitContract) || type.equals(benefitAgeLimit)) {
				int counter = 0;
				for (WebElement th : ths) {
					try {
						if (th.getText() != null && th.getText().contains(" " + planType)) {
							// found = true;
							try {
								counter = counter + Integer.parseInt(th.getAttribute("colspan"));
							} catch (Exception x) {
								counter = counter + 1;
							}
							break;

						}
						try {
							counter = counter + Integer.parseInt(th.getAttribute("colspan"));
						} catch (Exception x) {
							counter = counter + 1;
						}
					} catch (Exception e) {
						// TODO: handle exception

					}
					// if (th.getText().contains(planType)) break;

				}
				// if (!found) continue;
				List<WebElement> trs = table.findElements(By.tagName("tr"));
				int x = 0;
				for (WebElement tr : trs) {
					if (x == 0 || x == 1) {
						x++;
						continue;
					}
					// System.out.println("&&&--" + x);
					List<WebElement> tds = tr.findElements(By.tagName("td"));
					// System.out.println("99999--" + counter);
					if (tds != null && tds.size() > 0) {
						// System.err.println(tds.get(0).getText());
						if (tds.get(0).getText().equals(name)) {
							// System.err.println(tds.get(0).getText());
							value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];

							break;
						}

					}
				}

				// }

				// }
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}

		} catch (Exception e) {
			e.printStackTrace();
			return value + " " + Constants.SCRAPPING_ISSUE_FETCHING;
		}
		// if (toggle != null)
		// togggle.click();
		// System.out.println("value--" + value);
		System.out.println("OUT-" + new Date());
		return value;
	}
   */
	private String fetchBenefitDetails(String name, PatientTemp temp, WebDriver driver, String type, String firstA,
			String secondA, String planType, boolean mainOpen, boolean mandatory) throws InterruptedException {
		System.out.println("fetchBenefitDetails TEST -" + name+" -ss"+secondA);
		//System.out.println("IN-" + name + "-" + new Date());
		String value = Constants.SCRAPPING_NOT_FOUND;
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		else
			value = "";
		Thread.sleep(1000);
		String showHide="Show all +";
		try {
			if (!mainOpen) {
				// System.out.println(firstA);
				// System.out.println(counterElementMap.get(firstA));

				driver.findElement(By.id(temp.getCounterElementMap().get(firstA))).click();
				Thread.sleep(9000);
			}else {
				showHide="Hide all -";
			}
			
			//SHOW ALL LINK..
			List<String> linkNamesinUi= new ArrayList<>();
			List<WebElement> aas=driver.findElements(By.tagName("a"));
			for (WebElement aa : aas) {
				if (aa.getText()!=null && aa.getText().equals(showHide)) {
					List<WebElement> trs =aa.findElement(By.xpath("..")).findElement(By.xpath("..")).
							findElement(By.xpath("..")).findElement(By.xpath("..")).findElement(By.xpath(".."))
					.findElement(By.xpath("..")).findElement(By.xpath("..")).findElements(By.tagName("tr"));
					for(WebElement tr:trs) {
						//System.out.println(tr.getText());
						if (tr.getText().contains(showHide)) {
							continue;
						}
						//System.out.println(tr.getText());
						if (tr.getText()!=null)linkNamesinUi.add(tr.getText());
					}
					if (!mainOpen) {
						aa.click();
						Thread.sleep(9000);
					}
					
					break;
				}
			}
			
		List<WebElement>divsMains=driver.findElements(By.xpath("/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/div[1]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/div[1]/div/div/div/table/tbody/tr/td[2]/div/div[15]/div/div"));
			
		//String linkNames="";
		
		
		int countertrack=-1;
		boolean br=false;
		boolean found=false;
		
		for(String linkName:linkNamesinUi) {
			boolean moveForward=false;
			for(WebElement divsMain :divsMains) {
				if (countertrack==2) {
                    //if secondA is blank then find any first percentage
					//if linkName.equals(secondA) is then Go to the specific link
					//also name can be blank in that look for first percentage
				if (linkName.equals(secondA) || secondA.equals("")) {
					moveForward=true;
				}
				if (moveForward){
				WebElement table=	divsMain.findElement(By.tagName("table"));
					List<WebElement> ths = table.findElements(By.tagName("th"));
                    	//WebElement acTable=null;
                    	int counter = 0;
        				for (WebElement th : ths) {
        					try {
        						if (th.getText() != null && th.getText().contains(" " + planType)) {
        							// found = true;
        							try {
        								counter = counter + Integer.parseInt(th.getAttribute("colspan"));
        								//acTable=th.findElement(By.xpath("..")).findElement(By.xpath(".."));
        							} catch (Exception x) {
        								counter = counter + 1;
        							}
        							break;

        						}
        						try {
        							counter = counter + Integer.parseInt(th.getAttribute("colspan"));
        						} catch (Exception x) {
        							counter = counter + 1;
        						}
        					} catch (Exception e) {
        						// TODO: handle exception

        					}
        					// if (th.getText().contains(planType)) break;

        				}
        				
        				List<WebElement> trs = table.findElements(By.tagName("tr"));
        				int x = 0;
        				for (WebElement tr : trs) {
        					//System.err.println(tr.getText());
        					if (x == 0 || x == 1) {
        						x++;
        						continue;
        					}
        					if (tr.getText().contains("Delta Dental"))continue;
        					if (tr.getText().contains("Contract Benefit Level Age limit"))continue;
        					// System.out.println("&&&--" + x);
        					List<WebElement> tds = tr.findElements(By.tagName("td"));
        					// System.out.println("99999--" + counter);
        					if (tds != null && tds.size() > 0) {
        						//System.err.println(tds.get(0).getText());
        						if (tds.get(0).getText().equals(name)) {
        							// System.err.println(tds.get(0).getText());
        							value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
                                    System.out.println("ccc-"+value);
                                    found=true;
        							break;
        						}else if (name.equals("")) {
        							value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
                                    System.out.println("ccc999-"+value);
                                    found=true;
                                    if (!value.equals("")) break;
        						}

        					}
        				}
        				
        			//}
						if (linkName.equals(secondA)) { 
							br=true;
							break;
						}
						if (secondA.equals("") && found) {
							br=true;
							break;
						}
                    
				}//Counter track if
				
			}
			
				if (divsMain.getText()!=null && divsMain.getText().equals(linkName))//Check for second header then re-initialize  countertrack and br ==false;
				{	
					br=false;
					countertrack=0;
						
			    }
				countertrack++;
			}//DIV MAIN
			if (br) break;
		}
		
		}catch (Exception e) {
			// TODO: handle exception
		}
		return value;
	}

	// return TABLE
	private String[] fetchBenefitSearchProcedure(HashMap<String, DentaBenefitScrapDto> dataMap,
			HashMap<String, String> values, WebDriver driver, String planType, boolean clear, String[] divInfo)
			throws InterruptedException {
		// System.out.println("fetchBenefitInformation" + name);

		Thread.sleep(2000);
		String[] info = new String[4];
		if (clear) {
			List<WebElement> el = driver.findElements(By.tagName("a"));
			for (WebElement e : el) {
				if (e.getText() != null && e.getText().equals("Clear All")) {
					e.click();
					Thread.sleep(3000);
					break;
				}
			}
		}
		try {
			String comma = "";
			String names = "";
			int xt = 1;
			for (Map.Entry<String, DentaBenefitScrapDto> entry : dataMap.entrySet()) {

				if (xt > 10)
					break;// only 10 values at at time..
				// System.out.println("fetchBenefitSearchProcedure" + entry.getKey());
				names = names + comma + entry.getKey();
				xt = xt + 1;
				comma = ",";
			}
			WebElement inputBox = null;
			// template1:r1:0:r1:2:r1:0:r1:1:it1::content
			if (divInfo == null) {
				List<WebElement> ins = driver.findElements(By.tagName("input"));
				for (WebElement in : ins) {
					if (in.getAttribute("type") != null && in.getAttribute("type").equals("text")) {
						inputBox = in;
						info[0] = inputBox.getAttribute("id");
						break;
					}
				}
			} else {
				info[0] = divInfo[0];
				inputBox = driver.findElement(By.id(divInfo[0]));
			}
			// WebElement inputBox =
			// driver.findElement(By.id("template1:r1:1:r1:1:r1:0:r1:1:it1::content"));
			// Thread.sleep(1000);
			inputBox.sendKeys(names);
			System.out.println("fetchBenefitSearchProcedure" + names);
			Thread.sleep(5000);
			if (divInfo == null) {
				List<WebElement> ins = driver.findElements(By.tagName("button"));
				for (WebElement in : ins) {
					if (in.getText() != null && in.getText().equals("Search")) {// Search Button
						info[1] = in.getAttribute("id");
						in.click();

						break;
					}
				}
			} else {
				info[1] = divInfo[1];
				driver.findElement(By.id(divInfo[1])).click();
			}
			// driver.findElement(By.id("template1:r1:1:r1:1:r1:0:r1:1:cb11")).click();
			Thread.sleep(5000);

			//
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,3500)");
			// js.executeScript("window.scrollBy(0,500)");
			// js.executeScript("window.scrollBy(0,500)");
			Thread.sleep(3000);
			//
			// System.out.println("LL:" + tableBd.size());
			int counterTofind = 3;
			WebElement table = null;
			if (divInfo == null) {
				List<WebElement> divMain = driver.findElements(By.tagName("div"));
				for (WebElement divMain1 : divMain) {
					if (divMain1.getAttribute("id").endsWith("tablePanelGrp")) {
						info[2] = divMain1.getAttribute("id");
						// table= driver.findElement(By.xpath("//*[@id=\""+info[2]+"\"]/div[7]/table"));
						System.out.println("----------------------------------------");
						System.out.println("info[2]");
						System.out.println(info[2]);
						// break;
					}
				}
			} else {
				// table=driver.findElement(By.xpath("//*[@id=\""+divInfo[2]+"\"]/div[7]/table"));
				info[2] = divInfo[2];
			}

			for (Map.Entry<String, DentaBenefitScrapDto> entry : dataMap.entrySet()) {
				int y = 0;// make counter to fetch same string [] position of boolean [] and String []
				DentaBenefitScrapDto data = entry.getValue();
				for (String type : data.getTypes()) {

					String value = Constants.SCRAPPING_NOT_FOUND;
					if (data.getMandatory()[y])
						value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
					else
						value = "";
					// values.put(entry.getKey()+"_"+type, value);//populate with default value;
					// WebElement benefitDiv =
					// driver.findElement(By.id("template1:r1:1:r1:1:r1:0:r1:1:tablePanelGrp"));
					// List<WebElement> tableBd = benefitDiv.findElements(By.tagName("table"));

					// String id="";

					// WebElement
					// table=driver.findElement(By.xpath("//*[@id=\"template1:r1:1:r1:1:r1:0:r1:1:tablePanelGrp\"]/div[7]/table"));
					table = driver.findElement(By.xpath("//*[@id=\"" + info[2] + "\"]/div[7]/table"));
					System.err.println("FOR ---" + entry.getKey());
					// for (WebElement table : tableBd)
					{
						System.err.println("FOR -INNN--" + entry.getKey() + "-" + type);
						boolean found = false;
						try {
							if (values.get(entry.getKey() + "_" + type) != null)
								break;
							String clas = table.getAttribute("class");
							System.out.println("DD" + clas);

							if (clas != null && clas.equalsIgnoreCase("x22t")) {
								List<WebElement> ths = table.findElements(By.tagName("th"));
								if (type.equals(benefitContract) || type.equals(benefitAgeLimit)
										|| type.equals(benefitAlveoloplasty)) {
									int counter = 0;
									for (WebElement th : ths) {
										try {
											if (th.getText() != null && th.getText().contains(" " + planType)) {
												found = true;
												try {
													counter = counter + Integer.parseInt(th.getAttribute("colspan"));
												} catch (Exception x) {
													counter = counter + 1;
												}
												break;
											}
											try {
												counter = counter + Integer.parseInt(th.getAttribute("colspan"));
											} catch (Exception x) {
												counter = counter + 1;
											}
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
									if (!found)
										continue;
									System.out.println("counterTofind--" + counterTofind);
									try {

										for (int x = 3; x < dataMap.size() + 3; x++) {
											WebElement tr = driver.findElement(By.xpath(
													"//*[@id=\"" + info[2] + "\"]/div[7]/table/tbody/tr[" + x + "]"));
											List<WebElement> tds = tr.findElements(By.tagName("td"));
											System.out.println("99999--" + counter);
											System.err.println(tds.get(0).getText());
											if (!tds.get(0).getText().equals(entry.getKey()))
												continue;
											if (values.get(entry.getKey() + "_" + type) != null)
												break;
											if (type.equals(benefitContract))
												value = tds.get(counter - 1 - 1).getText().replace("%", "");

											if (type.equals(benefitAgeLimit)) {
												value = tds.get(counter - 1 - 1).getText().trim();
												if (value.contains(" ")) {// 100% 1 -- 100%
													value = tds.get(counter - 1 + 4).getText();

												} else if (value.equals("")) {
													value = tds.get(counter - 1).getText();
												} else {
													value = tds.get(counter - 1 + 3).getText();
												}
												// value = tds.get(counter - 1).getText();
											}
											if (type.equals(benefitAlveoloplasty))
												value = tds.get(1).getText();
											values.put((entry.getKey() + "_" + type), value);
											break;
										}
									} catch (Exception p) {
										p.printStackTrace();
										values.put((entry.getKey() + "_" + type), "N/A");
									}
									// break;

								}
								if (type.equals(benefitLimitation)) {
									boolean fd = false;
									// List<WebElement> trone =
									// driver.findElement(By.xpath("//*[@id=\"template1:r1:1:r1:1:r1:0:r1:1:tablePanelGrp\"]/div[7]/table")).findElements(By.tagName("tr"));
									try {
										for (int x = 3; x < dataMap.size() + 3; x++) {// 12
											WebElement tr = driver.findElement(By.xpath(
													"//*[@id=\"" + info[2] + "\"]/div[7]/table/tbody/tr[" + x + "]"));
											List<WebElement> tds = tr.findElements(By.tagName("td"));
											System.err.println(tds.get(0).getText());
											if (!tds.get(0).getText().equals(entry.getKey()))
												continue;
											if (values.get(entry.getKey() + "_" + type) != null)
												break;

											// for (WebElement td0 : td0s) {
											// System.out.println("ooo");
											// System.out.println("ooo");
											// System.out.println("ooo");

											// if (td0!=null && (!td0.getText().equals(entry.getKey())))
											// continue;
											WebElement td = tds.get(2);
											// WebElement tr =
											// driver.findElement(By.xpath("//*[@id=\"template1:r1:1:r1:1:r1:0:r1:1:tablePanelGrp\"]/div[7]/table/tbody/tr["+"3"+"]"))
											// ;;//table.findElements(By.tagName("tr")).get(counterTofind);
											// WebElement td = tr.findElements(By.tagName("td")).get(2);

											value = td.getText();
											System.out.println("5555:" + tds.get(0).getText());

											System.out.println("44444:" + value);
											fd = true;
											if (value.contains("[Limitations Apply]")) {
												// Click on Link
												td.findElements(By.tagName("a")).get(0).click();
												Thread.sleep(3000);
												List<String> ar = new ArrayList<>();
												try {
													if (divInfo == null && info[3] == null) {
														List<WebElement> containers = driver
																.findElements(By.tagName("td"));
														String containerId = null;
														for (WebElement container1 : containers) {
															if (container1.getAttribute("id") != null
																	&& container1.getAttribute("id")
																			.contains("::contentContainer")) {
																containerId = container1.getAttribute("id");
																info[3] = containerId;
																break;
															}
														}
													} else if (divInfo != null) {
														info[3] = divInfo[3];
													}
													WebElement container = driver.findElement(By.id(info[3]));
													List<WebElement> trs = container.findElements(By.tagName("table"))
															.get(1).findElements(By.tagName("tr"));
													trs.remove(0);

													for (WebElement tr1 : trs) {
														List<WebElement> tds1 = tr1.findElements(By.tagName("td"));
														ar.add(tds1.get(0).getText() + "----" + tds1.get(1).getText()
																+ "----" + tds1.get(2).getText());
													}
												} catch (Exception nn) {
													nn.printStackTrace();
												}
												String tempVal = "";
												String tempDef = "";
												String tempValT = "";
												String tempDefT = "";

												for (String ap : ar) {
													String[] dd = ap.split("----");
													try {

														// values.put((entry.getKey()+"_"+type),dd[0]);
														if (dd[1].equals("No")) {
															tempVal = dd[0];
															try {
																tempValT = dd[2];// Not sure if tooth exits or not
															} catch (Exception e) {

															}

															break;
														}
														System.out.println(data.getAge());
														System.out.println("--"
																+ dd[1].replace("years and older", "").trim() + "---");
														String te = dd[1].replace("years and older", "").trim();
														System.out.println("te--" + te + "---");
														if (entry.getKey().equals("D2931")) {
															System.out.println("9");
														}
														if (data.getAge() >= Integer.parseInt(te)) {
															tempVal = dd[0];
															try {
																tempValT = dd[2];// Not sure if tooth exits or not
															} catch (Exception e) {

															}
															break;
														}
													} catch (Exception ew) {
														ew.printStackTrace();
														tempDef = dd[0];// Where we do not have text "years and older"
														try {
															tempDefT = dd[2];// Not sure if tooth exits or not
														} catch (Exception e) {

														}

														// values.put((entry.getKey()+"_"+type),"");
														// break;
													}
													// data.getAge();
												}
												if (tempVal.equals("")) {
													try {
														values.put((entry.getKey() + "_" + type),
																FreqencyUtils.convertFrequecyDentaString("", tempDef));
														values.put((entry.getKey() + "_" + type + "_TOOTH"), tempDefT);
														// values.put((entry.getKey()+"_"+type),ar.get(ar.size()-1).split("----")[0]);
													} catch (Exception ew) {

													}
												} else {
													values.put((entry.getKey() + "_" + type),
															FreqencyUtils.convertFrequecyDentaString("", tempVal));
													values.put((entry.getKey() + "_" + type + "_TOOTH"), tempValT);
												}
												List<WebElement> buts = driver.findElements(By.tagName("button"));
												for (WebElement but : buts) {
													if (but.getText().equals("Close")) {
														but.click();
														System.out.println("CLOSED TRUE");
														Thread.sleep(3000);
														break;

													}
												}
												break;
											} // LIMITATIONS
											values.put((entry.getKey() + "_" + type),
													FreqencyUtils.convertFrequecyDentaString("", value));
											// break;
											// }//FOR td0s
											if (fd)
												break;
										}
										// if (fd) break;
									} catch (Exception e) {
										values.put(entry.getKey() + "_" + type, "N/A");// populate with default value;
									}
								}
							} // Class Check
						} catch (Exception e) {
							e.printStackTrace();
							values.put(entry.getKey() + "_" + type, "ERROR");// populate with default value;
							// TODO: handle exception
						}
					} // FOR LOOP table

					y = y + 1;

				} // String [] loop
				counterTofind = counterTofind + 1;// 7
				// counterTofind=counterTofind+1;
			} // hash map

		} catch (Exception e) {
			e.printStackTrace();
			// return value + " " + Constants.SCRAPPING_ISSUE_FETCHING;
		}
		// if (togggle != null)
		// togggle.click();
		return info;
	}

	/*
	 * private void navigatetoUrl(WebDriver driver, String url, int sec) throws
	 * InterruptedException, FailingHttpStatusCodeException, MalformedURLException,
	 * IOException { driver.navigate().to(url); Thread.sleep(sec);
	 * 
	 * }
	 */

	private boolean navigatetoMainSite(WebDriver driver, boolean navigate)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		if (!navigate)
			return navigate;
		List<WebElement> eles = driver.findElements(By.className("myToolsTable"));
		eles.get(0).findElement(By.tagName("a")).click();
		Thread.sleep(20000);
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
		WebElement loginButonElement = driver.findElement(By.id("loginButton"));

		loginButonElement.click();
		Thread.sleep(5000);// Need to keep this number high for Linux issue.
		try {
			WebElement p = driver.findElement(By.id("errorMsgSpan"));
			if (p.getText().startsWith("Username or password entered is not valid.")) {
				navigate = false;
			}
		} catch (Exception p) {

		}

		// System.out.println(p.getText());
		return navigate;
	}

	private static int GetChromeDriverProcessID(int aPort) throws IOException, InterruptedException {
		String[] commandArray = new String[3];

		if (SystemUtils.IS_OS_LINUX) {
			commandArray[0] = "/bin/sh";
			commandArray[1] = "-c";
			commandArray[2] = "netstat -anp | grep LISTEN | grep " + aPort;
		} else if (SystemUtils.IS_OS_WINDOWS) {
			commandArray[0] = "cmd";
			commandArray[1] = "/c";
			commandArray[2] = "netstat -aon | findstr LISTENING | findstr " + aPort;
		} else {
			System.out.println("platform not supported");
			System.exit(-1);
		}

		System.out.println("running command " + commandArray[2]);

		Process p = Runtime.getRuntime().exec(commandArray);
		p.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}

		String result = sb.toString().trim();

		System.out.println("parse command response line:");
		System.out.println(result);

		return SystemUtils.IS_OS_LINUX ? ParseChromeDriverLinux(result) : ParseChromeDriverWindows(result);
	}

	private static int GetChromeProcesID(int chromeDriverProcessID) throws IOException, InterruptedException {
		String[] commandArray = new String[3];

		if (SystemUtils.IS_OS_LINUX) {
			commandArray[0] = "/bin/sh";
			commandArray[1] = "-c";
			commandArray[2] = "ps -efj | grep google-chrome | grep " + chromeDriverProcessID;
		} else if (SystemUtils.IS_OS_WINDOWS) {
			commandArray[0] = "cmd";
			commandArray[1] = "/c";
			commandArray[2] = "wmic process get processid,parentprocessid,executablepath | find \"chrome.exe\" |find \""
					+ chromeDriverProcessID + "\"";
		} else {
			System.out.println("platform not supported");
			System.exit(-1);
		}

		System.out.println("running command " + commandArray[2]);

		Process p = Runtime.getRuntime().exec(commandArray);
		p.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (SystemUtils.IS_OS_LINUX && line.contains("/bin/sh")) {
				continue;
			}

			sb.append(line + "\n");
		}

		String result = sb.toString().trim();

		System.out.println("parse command response line:");
		System.out.println(result);

		return SystemUtils.IS_OS_LINUX ? ParseChromeLinux(result) : ParseChromeWindows(result);
	}

	private static int ParseChromeLinux(String result) {
		String[] pieces = result.split("\\s+");
		// root 20780 20772 20759 15980 9 11:04 pts/1 00:00:00
		// /opt/google/chrome/google-chrome.........
		// the second one is the chrome process id
		return Integer.parseInt(pieces[1]);
	}

	private static int ParseChromeWindows(String result) {
		String[] pieces = result.split("\\s+");
		// C:\Program Files (x86)\Google\Chrome\Application\chrome.exe 14304 19960
		return Integer.parseInt(pieces[pieces.length - 1]);
	}

	private static int ParseChromeDriverLinux(String netstatResult) {
		String[] pieces = netstatResult.split("\\s+");
		String last = pieces[pieces.length - 1];
		// tcp 0 0 127.0.0.1:2391 0.0.0.0:* LISTEN 3333/chromedriver
		return Integer.parseInt(last.substring(0, last.indexOf('/')));
	}

	private static int ParseChromeDriverWindows(String netstatResult) {
		String[] pieces = netstatResult.split("\\s+");
		// TCP 127.0.0.1:26599 0.0.0.0:0 LISTENING 22828
		return Integer.parseInt(pieces[pieces.length - 1]);
	}

	/**
	 * Link: https://www.deltadentalins.com/ Username: Crosbyfamilydental Password:
	 * Crosby000
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
			System.out.println(g.replaceFirst("\\[\\{", "{"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(g.replace(",", ""));
		// System.out.println(g.replace(".", ""));

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
		psc.setMemberId("1125727908");// 632307605
		psc.setSsnNumber("");

		l.add(psc);
		// dto.setPassword("Smile123");

		psc = new PatientScrapSearchDto();
		psc.setDob("03/20/1992");
		psc.setFirstName("Heather");
		psc.setLastName("Griffith");
		psc.setMemberId("1125727908");// 632307605
		psc.setSsnNumber("");

		//l.add(psc);

		dto.setDto(l);
		dto.setSiteUrl("https://www.deltadentalins.com/");
		DeltaDentalServiceImpl i = new DeltaDentalServiceImpl(null, null, f, dto, null, null,1,"",
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
