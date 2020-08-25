package com.tricon.ruleengine.service.scrapfull.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.microsoft.schemas.office.x2006.encryption.STSaltSize;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.PatientScrapSearchDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
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
import com.tricon.ruleengine.utils.MessageUtil;

public class BCBSDnoaconnectImpl extends BaseScrappingServiceImpl implements Callable<Boolean> {

	// document :
	// https://docs.google.com/spreadsheets/d/1-3PVTrzgSl0n3OEY7Qt0JZ_WDK7twIQWU3Hk7UjSdBM/edit#gid=1557872290
	static {

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

	}

	private static String inNetworkCoinsurance = "In Network Coinsurance";
	private static String inNetworkDeductible = "In Network Deductible";
	private static String lastrowunder2ndcolumn = "lastrowunder2ndcolumn";
	private static String lastrowunder2ndcolumnNext = "lastrowunder2ndcolumnNext";

	private static String uptoAge = "Up to Age";
	private static String otherLimitations = "Other Limitations";

	private static String benefitInNetwork = "In Network";
	private static String benefitInDeductible = "In Network ded";
	private static String benefitWaitingPeriod = "Waiting Period";
	private static String benefitMaximumLifeTime = "Maximum Life Time ";
	private static String benefitMaximumLifeTimeRem = "Maximum Life Time  Rem";
	// private static String referenceId;
	// private static String procedureData;

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

	public BCBSDnoaconnectImpl(PatientDao patDao, ScrapingFullDataDoa dataDoa,
			ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto, User user, Office office,
			int processId,String taxId, String driverLocation) {

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
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ChromeDriver(chromeDriverService, options);

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
								System.out.println("MEM 2-"
										+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

								boolean navigate = loginToSiteBCBS(dto, driver);

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
		temp.setStatus("Parsing Start");
		if (!issueNo) {

			temp.setStatus("Check Password.");
		}
		PatientDetailTemp t = new PatientDetailTemp();
		t.setPatient(temp);
		t.setOffice(office);
		t.setMemberId(sh.getMemberId().trim());
		t.setMemberSSN(sh.getSsnNumber().trim());
		/* un-comment this to populate SSN in case client wants
		try {
		String id = sh.getMemberId().trim().equals("") ? sh.getSsnNumber().trim() : sh.getMemberId().trim();
		if (id.matches("(.*?)[a-zA-Z](.*?)") && t.getMemberSSN().equals("")){
			t.setMemberSSN(id);
		};
		}catch(Exception y) {
			
		}
		*/
		Set<PatientDetailTemp> s = new HashSet<>();
		s.add(t);
		temp.setPatientDetails(s);
		if (!issueNo)
			return temp;
		issueNo = searchPatient(driver, temp, sh.getMemberId(), sh.getSsnNumber(), sh.getDob());
		if (!issueNo) {
			temp.setStatus("Patient Not found.." + sh.getDob() + " " + sh.getMemberId() + " " + sh.getSsnNumber());
			temp.setFirstName(sh.getFirstName().trim());
			temp.setLastName(sh.getLastName().trim());

			return temp;
		}
		temp.setStatus("Patient found..");
		populateMandatoryData(temp);
		boolean sd= createPatientDetailsurl(driver, temp, sh);
		if (sd== false) {
			if (temp.getFirstName().equals(""))temp.setFirstName(sh.getFirstName());
			if (temp.getLastName().equals(""))temp.setLastName(sh.getLastName());
			temp.setPatientId(sh.getPatientId());
			temp.setStatus("Web Site not responsed while Scrapping Also Check Password/User Name . Please try after Some Time..");
		}
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

	private boolean createPatientDetailsurl(WebDriver driver, PatientTemp temp, PatientScrapSearchDto sh)
			throws InterruptedException {

		//boolean dentalFound = false;
		try {
			String id = sh.getMemberId().trim().equals("") ? sh.getSsnNumber().trim() : sh.getMemberId().trim();

			// Check for select box
			Select select = null;
			try {
				select = new Select(driver.findElement(By.tagName("select")));

				List<WebElement> ops = select.getOptions();
				for (WebElement op : ops) {

					if (op.getText().contains(" - Dental")) {
						select.selectByValue(op.getAttribute("value"));
						//dentalFound = true;
						Thread.sleep(1000);
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			List<WebElement> eles = driver.findElements(By.className("form-control-static"));
			try {
				String[] name = eles.get(1).getText().split(" ");
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

			String url = "";
			String[] dobA = null;
			
			
			id=id.replaceAll("[a-zA-Z]", "");
			try {
				dobA = sh.getDob().split("/");// mm/dd/yyyy
				url = "https://www.dnoaconnect.com/members?dateOfBirth=" + dobA[2] + "-" + dobA[0] + "-" + dobA[1]
						+ "&subscriberId=" + id;
			} catch (Exception e) {
				temp.setStatus("DOB ISSUE -" + sh.getDob());
				carryOn = false;
			}
			if (carryOn) {
				navigatetoUrl(driver, url, 3000);
				String z = driver.getPageSource();
				System.out.println(z);
				System.out.println("444444444");
				
				boolean arrayC = false;
				z = z.replaceFirst("\\[\\{", "{");
				if (z.contains("}]}]}]")) {///   }]}]}
					z = z.replace("}]}]}]", "}]}]}");// }]}]
					arrayC = true;
				}else if (z.contains("}]}]")) {
					z = z.replace("}]}]", "}]}");// }]}]
					arrayC = true;
				}else
					z = z.replace("}]", "  }");// }]}]
				 System.out.println(z);
				z = z.replace("</pre></body></html>", "");
				z = "{\"subscriber\":" + z.split("\"subscriberId\":")[1];
				//System.out.println(z);

				// String referenceId = "";
				try {
					JSONObject jsonObj = new JSONObject(z);
					if (arrayC) {
						JSONArray ja = jsonObj.getJSONArray("policies");
						jsonObj = new JSONObject(ja.getJSONObject(0).toString());
						String x = jsonObj.get("policyType").toString();
						temp.setReferenceId(jsonObj.get("referenceId").toString());
						if (!x.equals("Medical")) {
							url = "https://www.dnoaconnect.com/#!/benefits/" + temp.getReferenceId() + "?subscriberId="
									+ id + "&dateOfBirth=" + dobA[2] + "-" + dobA[0] + "-" + dobA[1];
						} else {
							jsonObj = new JSONObject(ja.getJSONObject(1).toString());
							temp.setReferenceId(jsonObj.get("referenceId").toString());
							url = "https://www.dnoaconnect.com/#!/benefits/" + temp.getReferenceId() + "?subscriberId="
									+ id + "&dateOfBirth=" + dobA[2] + "-" + dobA[0] + "-" + dobA[1];

						}
					} else {
						JSONObject jsonChildObject = (JSONObject) jsonObj.get("policies");
						// System.out.println(jsonChildObject.toString());
						jsonObj = new JSONObject(jsonChildObject.toString());
						temp.setReferenceId(jsonObj.get("referenceId").toString());
						url = "https://www.dnoaconnect.com/#!/benefits/" + temp.getReferenceId() + "?subscriberId=" + id
								+ "&dateOfBirth=" + dobA[2] + "-" + dobA[0] + "-" + dobA[1];

					}

					navigatetoUrl(driver, url, 5000);

					fetchPatDetails(driver, temp);

					System.out.println("d");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					temp.setStatus("Patient Url creation issue");
				}
			}
			// https://www.dnoaconnect.com/members?dateOfBirth=1985-12-02&subscriberId=830794722
			// String url=dobA[2]+"%2F"+dobA[0]+"%2F"+dobA[1];
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			// TODO: handle exception
		}
          return true;
	}

	private boolean searchPatient(WebDriver driver, PatientTemp temp, String memberid, String ssn, String dob)
			throws InterruptedException {
		String id = memberid.trim().equals("") ? ssn.trim() : memberid.trim();
		if (id.equals("") || dob.equals(""))
			return false;
		WebElement sub = driver.findElement(By.id("subscriberId"));
		WebElement db = driver.findElement(By.id("dateOfBirth"));
		List<WebElement> validates = driver.findElements(By.tagName("button"));
		WebElement validateB=null;
	    for(WebElement validate:validates) {
	    	if (validate.getText()!=null &&  
	    		validate.getText().trim().equals("VALIDATE")) {
	    		validateB=	validate;
	    		
	    	}
	    }
		//WebElement validate = driver.findElement(
		//		By.xpath("/html/body/ui-view/div/div/ui-view/div/form/div/div[1]/div/div/div[6]/div/button"));
		sub.sendKeys(id);
		db.sendKeys(dob);
		try {
			validateB.click();
		} catch (Exception e) {
			return false;
		}
		Thread.sleep(4000);
		try {
		WebElement pTag = driver
				.findElement(By.xpath("/html/body/ui-view/div/div/ui-view/div/form/div/div[1]/div/div/div[2]/p[3]"));
		if (pTag != null && pTag.getText()!=null) {
			if (pTag.getText().startsWith("We're sorry! We were unable to locate you")) {
				return false;
			}
		}
		}catch(Exception y) {
			
		}
		return true;

	}

	private void populateMandatoryData(PatientTemp temp) {
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();

		dtemp.setPlanAnnualMax(Constants.SCRAPPING_MANDATORY_WARNING); // 1
		dtemp.setPlanAnnualMaxRemaining(Constants.SCRAPPING_MANDATORY_WARNING);// 2
		dtemp.setPlanIndividualDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 3
		dtemp.setPlanIndividualDeductibleRemaining(Constants.SCRAPPING_MANDATORY_WARNING);// 4
		dtemp.setBasicPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 5
		dtemp.setBasicSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 6
		dtemp.setMajorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 7
		dtemp.setMajorSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 8
		dtemp.setEndodonticsPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 9
		dtemp.setEndoSubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 10

		dtemp.setPerioSurgeryPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 11
		dtemp.setPerioSurgerySubjectDeductible(Constants.SCRAPPING_MANDATORY_WARNING);// 12

		dtemp.setPreventivePercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 13
		dtemp.setDiagnosticPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 14
		dtemp.setpAXRaysPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 15
		dtemp.setNightGuardsD9940Percentage(Constants.SCRAPPING_MANDATORY_WARNING);
		dtemp.setNightGuardsD9944Fr(Constants.SCRAPPING_MANDATORY_WARNING);// 19 //Cross Check

		dtemp.setBasicWaitingPeriod(Constants.SCRAPPING_MANDATORY_WARNING);// 20 in DOC
		dtemp.setMajorWaitingPeriod(Constants.SCRAPPING_MANDATORY_WARNING);// 21 in DOC

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
		dtemp.setSealantsD1351Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 37
		// dtemp.setSealantsD1351FL("");//38
		// dtemp.setSealantsD1351AgeLimit("")//39
		dtemp.setSealantsD1351PrimaryMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);// 40
		dtemp.setSealantsD1351PrimaryMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);// 41
		dtemp.setSealantsD1351PermanentMolarsCovered(Constants.SCRAPPING_MANDATORY_WARNING);// 42
		// dtemp.setProphyD1110FL("");//43
		// dtemp.setProphyD1120FL("");//44
		// 45 missing
		dtemp.setsRPD4341Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 46
		// dtemp.setsRPD4341FL("");//47
		// 48
		// 49
		dtemp.setPerioMaintenanceD4910Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 50
		// dtemp.setPerioMaintenanceD4910FL("");//51
		dtemp.setPerioMaintenanceD4910AltWProphyD0110(Constants.SCRAPPING_MANDATORY_WARNING);// 52
		dtemp.setFMDD4355Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 53
		// dtemp.setfMDD4355FL("");//54
		dtemp.setGingivitisD4346Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 55
		// dtemp.setGingivitisD4346FL("");//56
		dtemp.setNitrousD9230Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 57
		dtemp.setiVSedationD9243Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 58
		dtemp.setiVSedationD9248Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 59
		dtemp.setExtractionsMinorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 60
		dtemp.setExtractionsMajorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 61
		dtemp.setCrownLengthD4249Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 62
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
		dtemp.setImplantCoverageD6010Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 74
		dtemp.setImplantCoverageD6057Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 75
		dtemp.setImplantCoverageD6190Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 76
		dtemp.setImplantSupportedPorcCeramicD6065Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 77
		dtemp.setPostCompositesD2391Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 78
		// dtemp.setPostCompositesD2391FL("");//79
		// 80
		dtemp.setCrownsD2750D2740Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 81
		// dtemp.setCrownsD2750D2740FL("");//82
		// 83
		// 84
		dtemp.setD9310Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 85
		// dtemp.setD9310FL("");//86

		dtemp.setBuildUpsD2950Covered(Constants.SCRAPPING_MANDATORY_WARNING);// 87
		// dtemp.setBuildUpsD2950FL("");//88
		// 89
		dtemp.setOrthoPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 90
		// dtemp.setOrthoMax();//91
		// dtemp.setOrthoAgeLimit("");//92 benefit
		// 93 //94 //95 //96 //97 //99 //100 //101
		dtemp.setBridges1(Constants.SCRAPPING_MANDATORY_WARNING);// 102
		// dtemp.setBridges2("");//103
		// 104
		dtemp.setDen5225Per(Constants.SCRAPPING_MANDATORY_WARNING);// 105
		// dtemp.setDenf5225FR("");//107

		dtemp.setDen5226Per(Constants.SCRAPPING_MANDATORY_WARNING);// 106
		// dtemp.setDenf5226Fr("");//108
		dtemp.setDiagnosticSubDed(Constants.SCRAPPING_MANDATORY_WARNING);// 109

		// dtemp.setImplantsFrD6010("");//110
		// dtemp.setImplantsFrD6057("");//111
		// dtemp.setImplantsFrD6065("");//112
		// dtemp.setImplantsFrD6190("");//113
		dtemp.setNightGuardsD9945Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 114
		// dtemp.setOrthoRemaining("");//115
		// dtemp.setOrthoWaitingPeriod("");//116
		dtemp.setpAXRaysSubDed(Constants.SCRAPPING_MANDATORY_WARNING);// 117
		dtemp.setPreventiveSubDed(Constants.SCRAPPING_MANDATORY_WARNING);// 118
		// 119
		dtemp.setFmxPer(Constants.SCRAPPING_MANDATORY_WARNING);// 120
		// dtemp.setNightGuardsD9944Fr("");//121
		// dtemp.setNightGuardsD9945Fr("");//122

		// 123 //124 //125 //126

		// Debug
	}

	private void fetchPatDetails(WebDriver driver, PatientTemp temp) throws InterruptedException {
		Thread.sleep(5000);
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();
		
		dtemp.setInsName("BCBS of Tx");
		dtemp.setInsContact("8005212227");
		dtemp.setcSRName("Scraping Tool");
		dtemp.setTaxId(taxId);
		
		List<WebElement> cards = driver.findElements(By.className("card-content"));
		int x1=0;
		int ct=0;
		String playTermedDate="";
		System.out.println(cards.size());
		for(WebElement card : cards) {
			List<WebElement> divs =card.findElements(By.tagName("div"));
		
		for (WebElement div : divs) {
			x1++;
			//System.out.println("xxxx--"+x1+"--CT--"+ct);
			if (div.getText() != null && div.getText().startsWith("Plan Name:")) {
				try {
				dtemp.setPlanType(div.getText().split("Plan Name:")[1]);// this is new
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Group Name:")) {
				try {
				dtemp.setEmployerName(div.getText().split("Group Name:")[1]);//140
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Group Number:")) {
				try {
				dtemp.setGroup(div.getText().split("Group Number:")[1]);//141
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Claims Address:")) {
				try {
				dtemp.setInsAddress(div.getText().split("Claims Address:")[1]);//142
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Payer ID:")) {
				try {
				dtemp.setPayerId(div.getText().split("Payer ID:")[1].replace("\n", ""));//143
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Subscriber Name:")) {
				try {
				dtemp.setPolicyHolder(div.getText().split("Subscriber Name:")[1]);//144
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Eligibility:")) {
				try {
					String[] s = div.findElement(By.tagName("div")).getText().trim().split("–")[0].trim().split("/");

					// t.setHistoryDOS((s[0].length()==2?s[0]:"0"+s[0])+"/"+(s[1].length()==2?s[1]:"0"+s[1])+"/"+(s[2]));
					try {
						dtemp.setPlanEffectiveDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
								+ (s[1].length() == 2 ? s[1] : "0" + s[1]));//146

					} catch (Exception e) {
						// TODO: handle exception
						dtemp.setPlanEffectiveDate("");
					}
				} catch (Exception e) {
					// TODO: handle exception
					dtemp.setPlanEffectiveDate("issue");
				}
				try {
					String cs = div.findElement(By.tagName("div")).getText().trim().split("–")[1];
					if (cs.trim().equalsIgnoreCase("current")) {
						dtemp.setPlanTermedDate("");//145
					} else {
						String[] s = cs.trim().split("/");
						try {
							dtemp.setPlanTermedDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
									+ (s[1].length() == 2 ? s[1] : "0" + s[1]));

						} catch (Exception e) {
							// TODO: handle exception
							dtemp.setPlanTermedDate("issue");
						}

					}
				} catch (Exception e) {

				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Benefit Period:")) {
				try {
					String cs = div.findElement(By.tagName("div")).getText().trim().split("–")[1];
					if (cs.trim().equalsIgnoreCase("current")) {
						playTermedDate="";//145
					} else {
						String[] s = cs.trim().split("/");
						try {//let's pick the date from section "Eligibility" but if there is mentioned "Current" or is blank then we have to consider the date in field "Benefit Period"
							playTermedDate=s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
									+ (s[1].length() == 2 ? s[1] : "0" + s[1]);

						} catch (Exception e) {
							// TODO: handle exception
							playTermedDate="";
						}

					}
				} catch (Exception e) {

				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Subscriber ID:")) {
				try {
				dtemp.setMemberId(div.getText().split("Subscriber ID:")[1]);//144
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}
			if (div.getText() != null && div.getText().startsWith("Date of Birth:")) {
				try {
				/*	
				//String cs = div.findElement(By.tagName("div")).getText().trim();
				
				String[] s = cs.trim().split("/");//not in website
				dtemp.setPolicyHolderDOB(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
						+ (s[1].length() == 2 ? s[1] : "0" + s[1]));
				
				*/
				}catch (Exception e) {
					// TODO: handle exception
				}
				ct++;
			}

			//System.out.println("ct---"+ct);
			if (ct==10 || x1>100) break;
			//System.out.println("432142");
		}
		}
		if (dtemp.getPlanTermedDate().equals("") || dtemp.getPlanTermedDate().equals("issue") && !playTermedDate.equals("issue")){
			dtemp.setPlanTermedDate(playTermedDate);
		}
		dtemp.setGeneralDateIVwasDone(Constants.SIMPLE_DATE_FORMAT_IVF.format(new Date()));
		WebElement eleBody = driver.findElement(
				By.xpath("/html/body/ui-view/ui-view/div/div[3]/div[2]/div/div[2]/plan-info/div/div[2]/table/tbody"));
		List<WebElement> eleTR = eleBody.findElements(By.tagName("tr"));
		Thread.sleep(500);

		for (WebElement el : eleTR) {
			List<WebElement> tds = el.findElements(By.tagName("td"));
			for (WebElement td : tds) {

				if (td.getText().equals("Maximums - Benefit Period")) {
					try {
						dtemp.setPlanAnnualMax(el.findElements(By.tagName("td")).get(1).getText().replace("$", "")
								.replace(",", "").replace("N/A", "")); // 1
					} catch (Exception e) {
						// e.printStackTrace();
						dtemp.setPlanAnnualMax(Constants.SCRAPPING_ISSUE_FETCHING);
						// TODO: handle exception
					}
				} else if (td.getText().equals("Maximums - Benefit Period Remaining")) {
					try {
						dtemp.setPlanAnnualMaxRemaining(el.findElements(By.tagName("td")).get(1).getText()
								.replace("$", "").replace(",", "").replace("N/A", ""));
					} catch (Exception e) {
						// e.printStackTrace();
						dtemp.setPlanAnnualMaxRemaining(Constants.SCRAPPING_ISSUE_FETCHING);
					}
				} else if (td.getText().equals("Deductible - Benefit Period")) {
					try {
						dtemp.setPlanIndividualDeductible(el.findElements(By.tagName("td")).get(1).getText()
								.replace("$", "").replace(",", "").replace("N/A", ""));// 3
					} catch (Exception e) {
						// e.printStackTrace();
						dtemp.setPlanIndividualDeductible(Constants.SCRAPPING_ISSUE_FETCHING);
					}
				} else if (td.getText().equals("Deductible - Benefit Period Remaining")) {
					try {
						dtemp.setPlanIndividualDeductibleRemaining(el.findElements(By.tagName("td")).get(1).getText()
								.replace("$", "").replace(",", "").replace("N/A", ""));
					} catch (Exception e) {
						// e.printStackTrace();
						dtemp.setPlanIndividualDeductibleRemaining(Constants.SCRAPPING_ISSUE_FETCHING);
					}
				}

			}

		}

		openSideBarFirst(driver, "Benefit Information");
		dtemp.setEndodonticsPercentage(fetchBenefitInformation("Endodontics", driver, benefitInNetwork, true, true));// 9
		dtemp.setEndoSubjectDeductible(MessageUtil
				.getTEXTNAORYES(fetchBenefitInformation("Endodontics", driver, benefitInDeductible, true, true)));// 10
		dtemp.setPerioSurgeryPercentage(
				fetchBenefitInformation("Periodontal Surgery", driver, benefitInNetwork, true, true));// 11
		dtemp.setPerioSurgerySubjectDeductible(MessageUtil.getTEXTNAORYES(
				fetchBenefitInformation("Periodontal Surgery", driver, benefitInDeductible, true, true)));// 12

		//dtemp.setPlanEffectiveDate
		
		dtemp.setBasicWaitingPeriod(MessageUtil.getTEXTWaitingPeriodBCBS(
				fetchBenefitInformation("Composite Fillings", driver, benefitWaitingPeriod, true, true),dtemp.getPlanEffectiveDate()));// 20 in DOC
		dtemp.setMajorWaitingPeriod(MessageUtil
				.getTEXTWaitingPeriodBCBS(fetchBenefitInformation("Crowns", driver, benefitWaitingPeriod, true, true),dtemp.getPlanEffectiveDate()));// 21 in
																												// DOC

		dtemp.setOrthoMax(fetchBenefitInformation("Orthodontics", driver, benefitMaximumLifeTime, false, true));// 91
		dtemp.setOrthoRemaining(
				fetchBenefitInformation("Orthodontics", driver, benefitMaximumLifeTimeRem, false, false));// 115
		dtemp.setOrthoWaitingPeriod(MessageUtil
				.getTEXTWaitingPeriodBCBS(fetchBenefitInformation("Orthodontics", driver, benefitWaitingPeriod, true, false),dtemp.getPlanEffectiveDate()));// 116

		dtemp.setDiagnosticSubDed(MessageUtil
				.getTEXTNAORYES(fetchBenefitInformation("Oral Exams", driver, benefitInDeductible, true, true)));// 109

		dtemp.setPreventiveSubDed(MessageUtil
				.getTEXTNAORYES(fetchBenefitInformation("Prophylaxis", driver, benefitInDeductible, true, true)));// 118
 		/*
		 * //open tab List<WebElement> e1 = driver.findElements(By.tagName("a"));
		 * for(WebElement e2:e1) { String s= e2.getAttribute("target"); if
		 * (e2.isDisplayed() && s!=null && s.equals("_blank")){ e2.click(); break; } }
		 * //e1.sendKeys(Keys.CONTROL +"t");0 ArrayList<String> tabs = new
		 * ArrayList<String> (driver.getWindowHandles());
		 * System.out.println("TAB SIZE--"+tabs.size());
		 * 
		 * driver.switchTo().window(tabs.get(1)); //switches to new tab
		 */
		List<PatientHistoryTemp> hisSet = temp.getPatientHistory();
		//openSideBarFirst(driver, "Procedure History");
		fetchHistoryformation(driver, hisSet,temp.getReferenceId());
		String basicper6 = fetchValueByCode("D4346", temp, driver, inNetworkCoinsurance, true, false, true);
		if (basicper6.equals(""))
			basicper6 = "0";
		String basicper1 = fetchValueByCode("D4341", temp, driver, inNetworkCoinsurance, true, false, true);
		if (basicper1.equals(""))
			basicper1 = "0";
		try {
			String v = new Float(basicper6).floatValue() >= new Float(basicper1).floatValue() ? "D4341" : "D4346";
			dtemp.setBasicSubjectDeductible(MessageUtil
					.getTEXTNAORYES(fetchValueByCode(v, temp, driver, inNetworkDeductible, true, false, true)));
			dtemp.setBasicPercentage(
					new Float(basicper6).floatValue() >= new Float(basicper1).floatValue() ? basicper1 : basicper6);

		} catch (Exception p) {
			dtemp.setBasicPercentage(basicper6 + "" + basicper1);
			dtemp.setBasicSubjectDeductible(Constants.SCRAPPING_ISSUE_FETCHING);
		}

		dtemp.setMajorPercentage(fetchValueByCode("D2740", temp, driver, inNetworkCoinsurance, true, false, true));// 7

		dtemp.setMajorSubjectDeductible(MessageUtil
				.getTEXTNAORYES(fetchValueByCode("D2750", temp, driver, inNetworkDeductible, true, false, true)));// 8
		dtemp.setCrownsD2750D2740Percentage(
				fetchValueByCode("D2750", temp, driver, inNetworkCoinsurance, true, true, false));// 81
		dtemp.setCrownsD2750D2740FL(fetchValueByCode("D2750", temp, driver, lastrowunder2ndcolumn, false, true, true));// 82

		dtemp.setPreventivePercentage(fetchValueByCode("D1110", temp, driver, inNetworkCoinsurance, true, false, true));// 13
		dtemp.setProphyD1110FL(fetchValueByCode("D1110", temp, driver, lastrowunder2ndcolumn, false, true, true));// 43

		String v = fetchValueByCode("D0150", temp, driver, inNetworkCoinsurance, true, false, false);
		dtemp.setDiagnosticPercentage(v);// 14
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setExamsD0150FL(fetchValueByCode("D0150", temp, driver, lastrowunder2ndcolumn, false, true, true));// 27
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D0220", temp, driver, inNetworkCoinsurance, true, false, false);
		dtemp.setpAXRaysPercentage(v);// 15
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setxRaysPAD0220FL(fetchValueByCode("D0220", temp, driver, lastrowunder2ndcolumn, false, true, true));// 29
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		// setNightGuardsD9944Fr ///
		dtemp.setNightGuardsD9940Percentage(
				fetchValueByCode("D9944", temp, driver, inNetworkCoinsurance, true, false, false));// 19 //Cross Check
		dtemp.setNightGuardsD9944Fr(fetchValueByCode("D9944", temp, driver, lastrowunder2ndcolumn, false, true, true));// 121

		v = fetchValueByCode("D2930", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setsSCD2930FL(fetchValueByCode("D2930", temp, driver, lastrowunder2ndcolumn, false, true, true));// 22
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D2931", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setsSCD2931FL(fetchValueByCode("D2931", temp, driver, lastrowunder2ndcolumn, false, true, true));// 23
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D0120", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setExamsD0120FL(fetchValueByCode("D0120", temp, driver, lastrowunder2ndcolumn, false, true, true));// 24
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D0140", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setExamsD0140FL(fetchValueByCode("D0140", temp, driver, lastrowunder2ndcolumn, false, true, true));// 25
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D0145", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.seteExamsD0145FL(fetchValueByCode("D0145", temp, driver, lastrowunder2ndcolumn, false, true, true));// 26
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D0272", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setxRaysBWSFL(fetchValueByCode("D0272", temp, driver, lastrowunder2ndcolumn, false, true, true));// 28
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D0230", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setxRaysPAD0230FL(fetchValueByCode("D0230", temp, driver, lastrowunder2ndcolumn, false, true, true));// 30
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D0210", temp, driver, inNetworkCoinsurance, true, false, false);
		dtemp.setFmxPer(v);// 120
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setxRaysFMXFL(fetchValueByCode("D0210", temp, driver, lastrowunder2ndcolumn, false, true, true));// 31
		else
			fetchValueByCode("", temp, driver, "", false, true, true);
		// 32 missing

		v = fetchValueByCode("D01208", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setFlourideD1208FL(
					fetchValueByCode("D01208", temp, driver, lastrowunder2ndcolumn, false, true, false));// 33

		v = fetchValueByCode("D1208", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setFlourideAgeLimit(fetchValueByCode("D1208", temp, driver, uptoAge, false, true, true));// 34
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D1206", temp, driver, inNetworkCoinsurance, false, false, false);
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setVarnishD1206FL(fetchValueByCode("D1206", temp, driver, lastrowunder2ndcolumn, false, true, false));// 35
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setVarnishD1206AgeLimit(fetchValueByCode("D1206", temp, driver, uptoAge, false, true, true));// 36
		else
			fetchValueByCode("", temp, driver, "", false, true, true);

		v = fetchValueByCode("D1351", temp, driver, inNetworkCoinsurance, true, false, false);
		dtemp.setSealantsD1351Percentage(v);// 37
		if (!v.equals("0"))
			dtemp.setSealantsD1351FL(
					fetchValueByCode("D1351", temp, driver, lastrowunder2ndcolumn, false, true, false));// 38

		if (!v.equals("0"))
			dtemp.setSealantsD1351AgeLimit(fetchValueByCode("D1351", temp, driver, uptoAge, false, true, false));// 39

		String th = fetchValueByCode("D1351", temp, driver, otherLimitations, false, true, false);

		if (!th.contains(Constants.SCRAPPING_ISSUE_FETCHING)) {
			dtemp.setSealantsD1351PrimaryMolarsCovered(
					FreqencyUtils.checkForteehIntext(siteName, th, "A,B,I,J,K,L,S,T"));// 40
			dtemp.setSealantsD1351PreMolarsCovered(
					FreqencyUtils.checkForteehIntext(siteName, th, "4,5,12,13,20,21,28,29"));// 41
			dtemp.setSealantsD1351PermanentMolarsCovered(
					FreqencyUtils.checkForteehIntext(siteName, th, "2,3,14,15,18,19,30,31"));// 42

		} else {
			dtemp.setSealantsD1351PrimaryMolarsCovered("No");// 40
			dtemp.setSealantsD1351PreMolarsCovered("No");// 41
			dtemp.setSealantsD1351PermanentMolarsCovered("No");// 42
		}

		dtemp.setpAXRaysSubDed(MessageUtil
				.getTEXTNAORYES(fetchValueByCode("D0220", temp, driver, inNetworkDeductible, true, false, true)));// 117

		// 4 May

		dtemp.setProphyD1120FL(fetchValueByCode("D1120", temp, driver, lastrowunder2ndcolumn, false, false, true));// 44
		// 45 missing
		dtemp.setsRPD4341Percentage(fetchValueByCode("D4341", temp, driver, inNetworkCoinsurance, true, false, false));// 46
		dtemp.setsRPD4341FL(fetchValueByCode("D4341", temp, driver, lastrowunder2ndcolumn, false, true, true));// 47
		// 48
		// 49
		dtemp.setPerioMaintenanceD4910Percentage(
				fetchValueByCode("D4910", temp, driver, inNetworkCoinsurance, true, false, false));// 50
		dtemp.setPerioMaintenanceD4910FL(
				fetchValueByCode("D4910", temp, driver, lastrowunder2ndcolumn, false, true, false));// 51
		v = fetchValueByCode("D4910", temp, driver, lastrowunder2ndcolumnNext, true, true, true);
		dtemp.setPerioMaintenanceD4910AltWProphyD0110(FreqencyUtils.checkForteehIntext(siteName, v, "D1110,D1120"));// 52

		v = fetchValueByCode("D4355", temp, driver, inNetworkCoinsurance, true, false, false);
		dtemp.setFMDD4355Percentage(v);// 53
		// It might also be missing if there is no frequency but procedure is covered"
		if (!v.equals("0"))
			dtemp.setfMDD4355FL(fetchValueByCode("D4355", temp, driver, lastrowunder2ndcolumn, false, true, false));// 54

		dtemp.setGingivitisD4346Percentage(
				fetchValueByCode("D4346", temp, driver, inNetworkCoinsurance, true, false, false));// 55
		dtemp.setGingivitisD4346FL(fetchValueByCode("D4346", temp, driver, lastrowunder2ndcolumn, false, true, true));// 56

		dtemp.setNitrousD9230Percentage(
				fetchValueByCode("D9230", temp, driver, inNetworkCoinsurance, true, false, true));// 57
		dtemp.setiVSedationD9243Percentage(
				fetchValueByCode("D9243", temp, driver, inNetworkCoinsurance, true, false, true));// 58
		dtemp.setiVSedationD9248Percentage(
				fetchValueByCode("D9248", temp, driver, inNetworkCoinsurance, true, false, true));// 59
		dtemp.setExtractionsMinorPercentage(
				fetchValueByCode("D7140", temp, driver, inNetworkCoinsurance, true, false, true));// 60
		dtemp.setExtractionsMajorPercentage(
				fetchValueByCode("D7210", temp, driver, inNetworkCoinsurance, true, false, true));// 61
		dtemp.setCrownLengthD4249Percentage(
				fetchValueByCode("D4249", temp, driver, inNetworkCoinsurance, true, false, false));// 62
		dtemp.setCrownLengthD4249FL(fetchValueByCode("D4249", temp, driver, lastrowunder2ndcolumn, false, true, true));// 63
		// 64
		dtemp.setAlveoD7311FL(fetchValueByCode("D7311", temp, driver, lastrowunder2ndcolumn, false, false, true));// 65
		// 66
		dtemp.setAlveoD7310FL(fetchValueByCode("D7310", temp, driver, lastrowunder2ndcolumn, false, false, true));// 67

		dtemp.setCompleteDenturesD5110D5120FL(
				fetchValueByCode("D5110", temp, driver, lastrowunder2ndcolumn, false, false, true));// 68
		dtemp.setImmediateDenturesD5130D5140FL(
				fetchValueByCode("D5130", temp, driver, lastrowunder2ndcolumn, false, false, true));// 69
		dtemp.setPartialDenturesD5213D5214FL(
				fetchValueByCode("D5213", temp, driver, lastrowunder2ndcolumn, false, false, true));// 70
		dtemp.setInterimPartialDenturesD5214FL(
				fetchValueByCode("D5820", temp, driver, lastrowunder2ndcolumn, false, false, true));// 71
		// 72
		dtemp.setBoneGraftsD7953FL(fetchValueByCode("D7953", temp, driver, lastrowunder2ndcolumn, false, false, true));// 73

		dtemp.setImplantCoverageD6010Percentage(
				fetchValueByCode("D6010", temp, driver, inNetworkCoinsurance, true, false, true));// 74
		dtemp.setImplantsFrD6010(fetchValueByCode("D6010", temp, driver, lastrowunder2ndcolumn, false, true, true));// 110

		dtemp.setImplantCoverageD6057Percentage(
				fetchValueByCode("D6057", temp, driver, inNetworkCoinsurance, true, false, true));// 75
		dtemp.setImplantsFrD6057(fetchValueByCode("D6057", temp, driver, lastrowunder2ndcolumn, false, true, true));// 111

		dtemp.setImplantCoverageD6190Percentage(
				fetchValueByCode("D6190", temp, driver, inNetworkCoinsurance, true, false, true));// 76
		dtemp.setImplantsFrD6190(fetchValueByCode("D6190", temp, driver, lastrowunder2ndcolumn, false, true, true));// 113

		dtemp.setImplantSupportedPorcCeramicD6065Percentage(
				fetchValueByCode("D6065", temp, driver, inNetworkCoinsurance, true, false, true));// 77
		dtemp.setImplantsFrD6065(fetchValueByCode("D6065", temp, driver, lastrowunder2ndcolumn, false, true, true));// 112

		dtemp.setPostCompositesD2391Percentage(
				fetchValueByCode("D2391", temp, driver, inNetworkCoinsurance, true, false, false));// 78
		dtemp.setPostCompositesD2391FL(
				fetchValueByCode("D2391", temp, driver, lastrowunder2ndcolumn, false, true, true));// 79
		// 80
		// 83
		// 84
		dtemp.setD9310Percentage(fetchValueByCode("D9310", temp, driver, inNetworkCoinsurance, true, false, false));// 85
		dtemp.setD9310FL(fetchValueByCode("D9310", temp, driver, lastrowunder2ndcolumn, false, true, true));// 86

		dtemp.setBuildUpsD2950Covered(
				fetchValueByCode("D2950", temp, driver, inNetworkCoinsurance, true, false, false));// 87
		dtemp.setBuildUpsD2950FL(fetchValueByCode("D2950", temp, driver, lastrowunder2ndcolumn, false, true, true));// 88
		// 89
		dtemp.setOrthoPercentage(fetchValueByCode("D8090", temp, driver, inNetworkCoinsurance, true, false, false));// 90
		dtemp.setOrthoAgeLimit(fetchValueByCode("D8090", temp, driver, uptoAge, false, true, true));// 92
		// 93 //94 //95 //96 //97 //99 //100 //101

		dtemp.setBridges1(fetchValueByCode("D6245", temp, driver, inNetworkCoinsurance, true, false, false));// 102
		dtemp.setBridges2(fetchValueByCode("D6245", temp, driver, lastrowunder2ndcolumn, false, true, true));// 103
		// 104
		dtemp.setDen5225Per(fetchValueByCode("D5225", temp, driver, inNetworkCoinsurance, true, false, false));// 105
		dtemp.setDenf5225FR(fetchValueByCode("D5225", temp, driver, lastrowunder2ndcolumn, false, true, true));// 107

		dtemp.setDen5226Per(fetchValueByCode("D5226", temp, driver, inNetworkCoinsurance, true, false, false));// 106
		dtemp.setDenf5226Fr(fetchValueByCode("D5226", temp, driver, lastrowunder2ndcolumn, false, true, true));// 108

		dtemp.setNightGuardsD9945Percentage(
				fetchValueByCode("D9945", temp, driver, inNetworkCoinsurance, true, false, true));// 114
		dtemp.setNightGuardsD9945Fr(fetchValueByCode("D9945", temp, driver, lastrowunder2ndcolumn, false, true, true));// 122
		// driver.close();
		// ArrayList<String> tabs1 = new ArrayList<String> (driver.getWindowHandles());
		// System.out.println("TAB SIZE--"+tabs1.size());
		// Thread.sleep(2000);
		// driver.switchTo().window(tabs.get(0)); // switch back to main screen
		// Thread.sleep(20000);
		// 119
		// 123 //124 //125 //126

		dtemp.setAptDate("");

	}

	private void openSideBarFirst(WebDriver driver, String sideBarName) throws InterruptedException {
		Thread.sleep(5000);
		List<WebElement> lis = driver.findElements(By.xpath("//*[@id=\"sidenav\"]/li"));
		try {
			for (WebElement li : lis) {
				String bi = li.findElement(By.tagName("p")).getText();
				if (bi.equals(sideBarName)) {
					li.findElement(By.tagName("a")).click();
					Thread.sleep(15000);
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void fetchHistoryformation(WebDriver driver, List<PatientHistoryTemp> setHis,String refId) throws InterruptedException {
		Thread.sleep(2000);
		String url="https://www.dnoaconnect.com/members/"+refId+"/procedureHistory";
		try {
		navigatetoUrl(driver, url, 3000);
		String data = driver.getPageSource();
		// System.out.println("data");
		// System.out.println(data);
		data = data.replace("</pre></body></html>", "");
		data = "[{\"code\":" + data.split("\\[\\{\"code\":")[1];
		//System.out.println(data);
		JSONArray jsonObj = new JSONArray(data);
		for(int i = 0; i < jsonObj.length(); i++)
		{
			PatientHistoryTemp t = new PatientHistoryTemp();
		      JSONObject objects = jsonObj.getJSONObject(i);
		      String y =objects.get("code").toString();
		      if (y.length()==3) y="0"+y;
		      else  if (y.length()==2) y="00"+y;
		      t.setHistoryCode("D"+y);
		      t.setHistoryDOS(objects.get("serviceDate").toString());
		      t.setHistoryTooth("");
			  t.setHistorySurface("");
		      setHis.add(t);
		      //Iterate through the elements of the array i.
		      //Get thier value.
		      //Get the value for the first element and the value for the last element.
		}
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		/*
		WebElement ph = driver.findElement(By.id("procedure-history"));
		Thread.sleep(5000);
		System.out.println("111");
		WebElement canvas = ph.findElement(By.className("ui-grid-canvas"));
		Thread.sleep(5000);
		System.out.println("222");
		List<WebElement> rows = canvas.findElements(By.className("ui-grid-row"));
		Thread.sleep(2000);
		System.out.println("33");
		System.out.println(rows);
		//
	    System.out.println(rows.size());

		for (WebElement row : rows) {
			try {
				PatientHistoryTemp t = new PatientHistoryTemp();
				List<WebElement> vals = row.findElements(By.className("ui-grid-cell-contents"));
				System.out.println("99999");
				if (vals.size() >= 1) {
					t.setHistoryCode(vals.get(0).getText());
				}
				// if (vals.size()>=2)
				// t.setHistoryDOS(historyDOS);HistoryCode(vals.get(1).getText());
				if (vals.size() >= 3) {
					String[] s = vals.get(2).getText().trim().split("/");

					// t.setHistoryDOS((s[0].length()==2?s[0]:"0"+s[0])+"/"+(s[1].length()==2?s[1]:"0"+s[1])+"/"+(s[2]));
					try {
						t.setHistoryDOS(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
								+ (s[1].length() == 2 ? s[1] : "0" + s[1]));

					} catch (Exception e) {
						// TODO: handle exception
						t.setHistoryDOS(vals.get(2).getText());
					}
				}
				t.setHistoryTooth("");
				t.setHistorySurface("");

				if (vals.size() >= 1)
					setHis.add(t);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        */
		System.out.println("retttt");

	}

	private String fetchBenefitInformation(String name, WebDriver driver, String type, boolean mandatory,
			boolean subsectionOPen) throws InterruptedException {
		System.out.println("fetchBenefitInformation" + name);

		String value = Constants.SCRAPPING_NOT_FOUND;
		WebElement togggle = null;
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		else
			value = "";
		Thread.sleep(1000);
		int cbenefitInNetwork=6;
		int cbenefitInDeductible=7;
		try {
			String tes =driver.findElement(By.className("benefit-category-heading")).getText();
			if (tes.contains("Coinsurance") && tes.contains("Deductible")) {
				cbenefitInNetwork=0;//TO HANDLE for 06/08/2007 --> 833008964 This is exception case
				cbenefitInDeductible=1;
			}
		}catch(Exception n) {
			
		}
		try {
			List<WebElement> divForNames = driver.findElements(By.className("benefit-category-data"));
			for (WebElement divForName : divForNames) {
				WebElement span = divForName.findElements(By.tagName("span")).get(0);
				String spanText = span.findElements(By.tagName("div")).get(0).getText().trim();
				
				if (spanText.equals(name)) {
					boolean covered =span.getText().contains("Not covered");
						if (type.equals(benefitInNetwork)) {
						if (covered) {
								 value="No";
								 break;
						}	
						value = span.findElements(By.className("text-center")).get(cbenefitInNetwork).getText().replace("%", "");
						break;
					} else if (type.equals(benefitInDeductible)) {
						 if (covered) {
							 value="No";
							 break;
						 }
						value = span.findElements(By.className("text-center")).get(cbenefitInDeductible).getText().replace("%", "");
						break;
					} else if (type.equals(benefitWaitingPeriod)) {

						togggle = span.findElements(By.tagName("div")).get(0);
						if (true) {
							togggle.click();

							Thread.sleep(3000);
						}
						value = divForName.findElement(By.tagName("dd")).getText();
						break;
					} else if (type.equals(benefitMaximumLifeTime)) {

						togggle = span.findElements(By.tagName("div")).get(0);
						if (true) {
							togggle.click();
							Thread.sleep(3000);
						}
						WebElement table = divForName.findElement(By.tagName("table"));
						List<WebElement> trs = table.findElements(By.tagName("tr"));
						for (WebElement tr : trs) {
							// System.out.println(tr.getText());
							if (tr.getText().contains("Maximums - Lifetime")) {//
								value = tr.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",",
										"");
								break;
							}
							//
						}

						break;
					} else if (type.equals(benefitMaximumLifeTimeRem)) {

						togggle = span.findElements(By.tagName("div")).get(0);
						if (true) {
							togggle.click();
							Thread.sleep(3000);
						}
						WebElement table = divForName.findElement(By.tagName("table"));
						List<WebElement> trs = table.findElements(By.tagName("tr"));
						for (WebElement tr : trs) {
							// System.out.println(tr.getText());
							if (tr.getText().contains("Maximums - Lifetime Remaining")) {
								value = tr.findElements(By.tagName("td")).get(1).getText().replace("$", "").replace(",",
										"");
								break;
							}
							//
						}
					}
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return value + " " + Constants.SCRAPPING_ISSUE_FETCHING;
		}
		if (togggle != null)
			togggle.click();
		System.out.println("cc-"+value);
		return value;
	}

	private String callBenefitCodeUrl(String code, WebDriver driver, PatientTemp temp) {
		//System.out.println("CALLED--URL...");
		String data = "";
		try {
			String url = "https://www.dnoaconnect.com/members/" + temp.getReferenceId() + "/procedureBenefits/"
					+ code.substring(1);
			navigatetoUrl(driver, url, 2000);
			data = driver.getPageSource();
			// System.out.println("data");
			// System.out.println(data);
			data = data.replace("</pre></body></html>", "");
			data = "{\"subscriber\":" + data.split("\"subscriberId\":")[1];
			JSONObject jsonObj = new JSONObject(data);
			if (jsonObj.get("procedureStatus").toString().equals("active")) {

			} else {
				data = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			data = "";
			// TODO: handle exception
		}
		return data;

	}

	/**
	 * 
	 * @param code
	 * @param driver
	 * @param type
	 * @param mandatory
	 * @param directoryValues
	 *            mean Procedure code is already Clicked
	 * @return
	 * @throws InterruptedException
	 */
	private String fetchValueByCode(String code, PatientTemp temp, WebDriver driver, String type, boolean mandatory,
			boolean directValues, boolean close) throws InterruptedException {
		System.out.println("fetchValueByCode" + code + " " + type);
		String value = Constants.SCRAPPING_NOT_FOUND;
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		if (!directValues) {
			temp.setProcedureData(callBenefitCodeUrl(code, driver, temp));
			// check for code existence
		}
		value = Constants.SCRAPPING_MAIN_CONDTION_MET;
		try {
			if (type.equals(inNetworkCoinsurance)) {
				JSONObject jsonObj = new JSONObject(temp.getProcedureData());
				JSONObject ja = (JSONObject) jsonObj.get("benefit");// getJSONArray
				// jsonObj = ja.get("coinsuranceInNetwork").toString();
				value = ja.get("coinsuranceInNetwork").toString().replace("%", "");

				/*
				 * WebElement div = driver.findElement(By.xpath(
				 * "/html/body/div[1]/div/div/div[3]/div/div[3]/div[1]/div[2]/dl/div")); value
				 * =div.findElements(By.tagName("dd")).get(0).getText().replace("%","");
				 */
			} else if (type.equals(inNetworkDeductible)) {
				JSONObject jsonObj = new JSONObject(temp.getProcedureData());
				JSONObject ja = (JSONObject) jsonObj.get("benefit");// getJSONArray
				// jsonObj = ja.get("coinsuranceInNetwork").toString();
				Object t = ja.get("deductibleMetInNetwork");
				if (t == null)
					value = "N/A";
				else
					value = t.toString().replace("null", "N/A");
				/*
				 * WebElement div =driver.findElement(By.xpath(
				 * "/html/body/div[1]/div/div/div[3]/div/div[3]/div[1]/div[2]/dl/div")); value
				 * =div.findElements(By.tagName("dd")).get(1).getText();
				 */

			} else if (type.equals(lastrowunder2ndcolumn)) {

				JSONObject jsonObj = new JSONObject(temp.getProcedureData());
				JSONObject ja = (JSONObject) jsonObj.get("benefit");// getJSONArray
				JSONObject limit = (JSONObject) ja.get("limitations");
				value = ja.get("coinsuranceInNetwork").toString().replace("%", "");
				if (!value.equals("0")) {
					value = limit.get("occurrences").toString() + "X" + limit.get("length").toString()
							+ limit.get("unit").toString();
					if (value.equals("nullXnullnull"))
						value = "";
				} else {
					value = "0";// Constants.NO_FREQUENCY;
				}
				value = FreqencyUtils.convertFrequecyString(siteName, value);
				/*
				 * WebElement div =driver.findElement(By.xpath(
				 * "/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[2]")); try { value
				 * =FreqencyUtils.convertFrequecyString(siteName,div.findElements(By.tagName(
				 * "dt")).get(0).getText()); }catch (Exception e) { value=""; }
				 */

			} else if (type.equals(lastrowunder2ndcolumnNext)) {
				JSONObject jsonObj = new JSONObject(temp.getProcedureData());
				JSONObject ja = (JSONObject) jsonObj.get("benefit");// getJSONArray
				JSONObject limit = (JSONObject) ja.get("limitations");
				JSONArray proc = (JSONArray) limit.get("procedures");
				if (proc != null && proc.length() > 0) {
					value = "";
					String comma = "";
					for (int i = 0; i < proc.length(); i++) {
						JSONObject childObject = proc.optJSONObject(i);
						JSONArray ar = (JSONArray) childObject.get("codes");
						if (ar != null && ar.length() > 0) {
							for (int j = 0; j < ar.length(); j++) {
								// System.out.println(ar.get(j));
								value = value + comma + "D" + ar.get(j);
								comma = ",";

							}
						}
					}
				}

				/*
				 * WebElement div =driver.findElement(By.xpath(
				 * "/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[2]")); try { value
				 * =div.findElements(By.tagName("dd")).get(0).getText(); }catch (Exception e) {
				 * value=""; }
				 */
			} else if (type.equals(uptoAge)) {
				JSONObject jsonObj = new JSONObject(temp.getProcedureData());
				JSONObject ja = (JSONObject) jsonObj.get("benefit");// getJSONArray
				JSONObject limit = (JSONObject) ja.get("limitations");
				int x=((Integer) limit.get("ageMaximum"));
				if (x>=999) x=99;
				value = x+"";
				//if (value<=999) value=99;
				/*
				 * WebElement div =driver.findElement(By.xpath(
				 * "/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[1]/dl")); value
				 * =MessageUtil.removeUptoAge(siteName,div.findElements(By.tagName("dt")).get(0)
				 * .getText());
				 */
			} else if (type.equals(otherLimitations)) {
				JSONObject jsonObj = new JSONObject(temp.getProcedureData());
				JSONObject ja = (JSONObject) jsonObj.get("benefit");// getJSONArray
				JSONObject limit = (JSONObject) ja.get("limitations");
				JSONArray proc = (JSONArray) limit.get("limitedToTeeth");
				if (proc != null && proc.length() > 0) {
					value = "";
					String comma = "";
					for (int i = 0; i < proc.length(); i++) {
						// System.out.println(proc.optString(i));
						value = value + comma + proc.optString(i);
						comma = ",";
					}
				}

				/*
				 * WebElement dd =driver.findElement(By.xpath(
				 * "/html/body/div[1]/div/div/div[3]/div/div[3]/div[2]/div[1]/dl/span[3]/dd"));
				 * if (dd.getText().startsWith("Limited to teeth")) value
				 * =MessageUtil.removeLimitedToteeth(siteName,dd.getText()); else value
				 * =Constants.SCRAPPING_ISSUE_FETCHING;
				 */

			}
		} catch (Exception x) {
			x.printStackTrace();
			return value + " " + Constants.SCRAPPING_ISSUE_FETCHING;
		}
		// Close Button
		/*
		 * if (close) { try { driver.findElement(By.xpath(
		 * "/html/body/div[1]/div/div/div[4]/div/div/div/button")).click();
		 * 
		 * }catch (Exception e) { // TODO: handle exception } }
		 */
		System.out.println("VALUE:" + value);
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
		driver.get("https://www.dnoaconnect.com/#!/lookup");
		Thread.sleep(4000);
		return true;
	}

	public boolean loginToSiteBCBS(ScrappingFullDataDetailDto dto, WebDriver driver)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		boolean navigate = true;
		driver.get(dto.getSiteUrl());
		Thread.sleep(4000);// Need to keep this number high for Linux issue.
		WebElement userNameElement = driver.findElement(By.id("username"));
		userNameElement.sendKeys(dto.getUserName().trim());
		WebElement passwordElement = driver.findElement(By.id("password"));

		passwordElement.sendKeys(dto.getPassword().trim());
		WebElement loginButonElement = driver.findElement(
				By.xpath("/html/body/ui-view/div/div/ui-view/div[2]/div/div[2]/div[1]/form/div[4]/button"));

		loginButonElement.click();
		Thread.sleep(1000);// Need to keep this number high for Linux issue.
		try {
			WebElement p = driver
					.findElement(By.xpath("/html/body/ui-view/div/div/ui-view/div[2]/div/div[2]/div[1]/p[3]"));
			if (p.getText().startsWith("You have entered an invalid User ID")) {
				navigate = false;
			}
		} catch (Exception p) {

		}

		// System.out.println(p.getText());
		return navigate;
	}

	/**
	 * Link: https://www.dnoaconnect.com/#!/ Username: crosbyfd07 Password: Smile123
	 * Member ID: 830794722 DOB: 12/02/1985 Name: Michael Perkins 115296917101
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
		dto.setPassword("Smile123");
		dto.setUserName("crosbyfd07");
		dto.setSiteName("BCBS");

		PatientScrapSearchDto psc = new PatientScrapSearchDto();
		List<PatientScrapSearchDto> l = new ArrayList<>();
		psc.setDob("03/21/1986");
		psc.setFirstName("KYNDRICK");//For policies issue KYNDRICK HILL 831918461 03/21/1986 (crosbyfd07-Smile123) 
		psc.setLastName("HILL");
		psc.setMemberId("831918461");
		psc.setSsnNumber("");

		l.add(psc);
		// dto.setPassword("Smile123");
		dto.setDto(l);
		psc = new PatientScrapSearchDto();
		psc.setDob("03/21/1986");
		psc.setFirstName("KYNDRICK"); 
		psc.setLastName("HILL");
		psc.setMemberId("831918461");
		psc.setSsnNumber("");
		// l.add(psc);

		dto.setDto(l);

		dto.setSiteUrl("https://www.dnoaconnect.com/#!/");
		BCBSDnoaconnectImpl i = new BCBSDnoaconnectImpl(null, null, null, dto, null, null, 1,"",
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
