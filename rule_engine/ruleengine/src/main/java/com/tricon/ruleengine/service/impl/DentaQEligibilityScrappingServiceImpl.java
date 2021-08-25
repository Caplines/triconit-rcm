package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.scrapping.EligibilityDto;
import com.tricon.ruleengine.dto.scrapping.HistoryDto;
import com.tricon.ruleengine.model.db.IVFDefaultValue;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.db.SealantEligibilityRule;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.ConstantsScrapping;

public class DentaQEligibilityScrappingServiceImpl extends BaseScrappingServiceImpl implements Callable<List<?>> {

	private ScrappingSiteDetails scrappingSiteDetails = null;
	private String CLIENT_SECRET_DIR, CREDENTIALS_FOLDER;
	private Map<String, List<Object>> mapData = null;
	private boolean updateSheet = false;
	private final int max = 2;
	private int ctALL = 0;
	private String siteType = "";
	private String officeName;
	private int attempt = 0;
	
	private List<IVFDefaultValue> iVFDefaultValues;
	private List<SealantEligibilityRule> sealantEligibilityRules;
	
	private CaplineIVFGoogleFormService caplineIVFGoogleFormService;

	private IVFormType iVFormType;
	
	private Office office;
	
	public ScrappingSiteDetails getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}

	public void setScrappingSiteDetails(ScrappingSiteDetails scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}

	DentaQEligibilityScrappingServiceImpl(ScrappingSiteDetails scrappingSiteDetails, String CLIENT_SECRET_DIR,
			String CREDENTIALS_FOLDER, Map<String, List<Object>> mapData, boolean updateSheet, String siteType,
			String officeName, String driverLocation,List<IVFDefaultValue>iVFDefaultValues,List<SealantEligibilityRule> sealantEligibilityRules,
    		CaplineIVFGoogleFormService caplineIVFGoogleFormService,IVFormType iVFormType,Office office) {
		this.scrappingSiteDetails = scrappingSiteDetails;
		this.CLIENT_SECRET_DIR = CLIENT_SECRET_DIR;
		this.CREDENTIALS_FOLDER = CREDENTIALS_FOLDER;
		this.mapData = mapData;
		this.updateSheet = updateSheet;
		this.siteType = siteType;
		this.officeName = officeName;
		this.driverLocation = driverLocation;
		this.sealantEligibilityRules=sealantEligibilityRules;
		this.iVFDefaultValues=iVFDefaultValues;
		this.caplineIVFGoogleFormService=caplineIVFGoogleFormService;
		this.iVFormType=iVFormType;
		this.office=office;
		// store parameter for later user
	}

	@Override
	public List<EligibilityDto> call() {
		List<EligibilityDto> r = scrapSite(scrappingSiteDetails, mapData);
		try {
			if (updateSheet)
				ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
						scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
						(List<EligibilityDto>) r, scrappingSiteDetails.getRowCount(), "NO", "D");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// System.out.println(e.getMessage().split("Attempting to write column:
			// ")[1].split(",")[0]) ;
			if (e.getMessage().contains("Attempting to write column: "))
				appendSheet(r);
		}
		
		//Create Default Data...
		 //CODE DONE IN main method          ***********************************************************8
		 //CaplineIVFFormDto caplineIVFFormDto= new CaplineIVFFormDto();
		try {
			//this.caplineIVFGoogleFormService.saveIVFFormData(caplineIVFFormDto, office, false, iVFormType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}

	private void appendSheet(List<EligibilityDto> r) {
		try {
			ConnectAndReadSheets.appendCelltoSheet(scrappingSiteDetails.getGoogleSheetId(),
					scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
					Constants.ATTEMPT_TO_ADD_NEW_COLUMNS);
			if (updateSheet)
				ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
						scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,
						(List<EligibilityDto>) r, scrappingSiteDetails.getRowCount(), "NO", "D");
		} catch (Exception e) {
			attempt = attempt + 1;
			e.printStackTrace();
			if (attempt < 50 && e.getMessage().contains("Attempting to write column: "))
				appendSheet(r);
		}
	}

	public static void main(String[] stg) throws Exception {

		ScrappingSiteDetails det = null;
		Map<String, List<Object>> mapData = null;
		List<Object> cc = new ArrayList<>();
		// cc.add(new MCNADentaSheet("","","","Frank", "Duque", "739090558",
		// "03/31/2020", "Dentaquest",0+""));
		// cc.add(new MCNADentaSheet("","","","", "MEDELLIN", "530120999", "10/28/2009",
		// "Dentaquest",0+""));//
		// cc.add(new MCNADentaSheet("","","","MATTHEW", "MEDELLIN", "530120999",
		// "10/28/2009", "Dentaquest",0+""));//
		// cc.add(new MCNADentaSheet("","","","", "", "617765281", "06/20/2013",
		// "Dentaquest",0+""));//
		// cc.add(new MCNADentaSheet("","","","", "", "739438815", "04/11/2020",
		// "Dentaquest",0+""));//
		//cc.add(new MCNADentaSheet("", "", "", "", "", "731539564", "2/17/2019", "Dentaquest", 0 + ""));//
		//cc.add(new MCNADentaSheet("", "", "", "", "", "604429287", "12/18/2002", "Dentaquest", 0 + ""));//
		  cc.add(new MCNADentaSheet("", "", "", "", "", "523946504", "01/09/2004", "Dentaquest", 0 + ""));//
		// Ryleigh Britt 2013/06/20
		mapData = new HashMap<>();
		mapData.put("1", cc);
		det = new ScrappingSiteDetails();
		det.setProxyPort("111");
		det.setLocation("Geetika Rastogi");

		// https://connectsso.dentaquest.com/authsso/providersso/SSOProviderLogin.aspx?TYPE=33554433&REALMOID=06-6a4c193d-7520-4f3d-b194-83367a3ef454&GUID=&SMAUTHREASON=0&METHOD=POST&SMAGENTNAME=-SM-imZolSjcs1FQR%2fH0k3NSK1Uvx4zWgziEWSOuwqcKGG1C%2bW%2fQdG3dRa7BVqGyOpNh&TARGET=-SM-https%3a%2f%2fconnectsso%2edentaquest%2ecom%2fprovideraccessv2%2findex%2ehtml
		// Dental@6743 offshorebfd for 739438815 04/11/2020 Beaumont
		det.setPassword("Winter2020$");// // Devine%1245976
		det.setUserName("Beaumont321"); // Devin13458
		det.setLocationProvider("");
		Office f = new Office();
		f.setName("Jasper");
		det.setOffice(f);
		// det.setOffice("Devine");
		// det.set
		DentaQEligibilityScrappingServiceImpl x = new DentaQEligibilityScrappingServiceImpl(det,
				"E:/Project/Tricon/files/client_secret.json", "E:/Project/Tricon/files", mapData, true, "new",
				f.getName(), "D:/Project/Tricon/linkedinapp/linkedinbit/linkedinapp/lib/chromedriver.exe",null,null,null,null,null);
		x.scrapSite(det, mapData);

	}

	public List<EligibilityDto> scrapSite(ScrappingSiteDetails scrappingSiteDetails,
			Map<String, List<Object>> mapData) {
		WebDriver driver = null;
		if (this.siteType == null)
			this.siteType = "old";
		if (this.siteType.equals(""))
			this.siteType = "old";
		if (this.siteType.equals("old")) {
			driver = new HtmlUnitDriver(true);
			setProps(scrappingSiteDetails.getProxyPort());
		} else
			driver = getBrowserDriver();
		// TODO Auto-generated method stub

		List<EligibilityDto> eList = new ArrayList<>();
		try {

			if (this.siteType.equals("new"))
				loginToSiteDentaNew(scrappingSiteDetails, driver);
			else
				loginToSiteDentaOld(scrappingSiteDetails, driver);

			for (Map.Entry<String, List<Object>> entry : mapData.entrySet()) {
				List<Object> x = (List<Object>) entry.getValue();
				for (Object obj : x) {
					MCNADentaSheet sh = (MCNADentaSheet) obj;
					EligibilityDto d = null;
					if (this.siteType.equals("old"))
						d = parsePageOld(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(), sh.getfName(),
								sh.getZip(), scrappingSiteDetails.getLocationProvider(), sh.getfName(), sh.getlName(),
								sh.getInsuranceName(), true);
					else
						d = parsePageNew(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(), sh.getfName(),
								sh.getZip(), sh.getAppointmentDate(), this.officeName,
								scrappingSiteDetails.getLocationProvider(), sh.getfName(), sh.getlName(),
								sh.getInsuranceName(), scrappingSiteDetails.getLocation(), true);

					if (d != null && d.getMessage().equals(ConstantsScrapping.SUBSCRIBER_NOT_FOUND)) {

						if (this.siteType.equals("old")) {
							if (!sh.getlName().equals("") || !sh.getfName().equals(""))
								d = parsePageOld(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(),
										sh.getfName(), sh.getZip(), scrappingSiteDetails.getLocationProvider(),
										sh.getfName(), sh.getlName(), sh.getInsuranceName(), false);
						} else {
							d = parsePageNew(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(), sh.getfName(),
									sh.getZip(), sh.getAppointmentDate(), this.officeName,
									scrappingSiteDetails.getLocationProvider(), sh.getfName(), sh.getlName(),
									sh.getInsuranceName(), scrappingSiteDetails.getLocation(), false);
						}
					}

					if (d != null) {
						d.setMcnaSheet(sh);
						eList.add(d);

						if (updateSheet) {
							if (eList.size() >= max) {
								try {
									List<EligibilityDto> rListC = new ArrayList<>(eList);
									ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
											scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR,
											CREDENTIALS_FOLDER, (List<EligibilityDto>) rListC,
											scrappingSiteDetails.getRowCount(), "YES", "D");

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								ctALL = ctALL + 1;
								eList.clear();
							} else {
							}
						}

					}
				}
			}
			// scrappingSiteDetails.get
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		try {
			driver.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return eList;

	}

	private EligibilityDto parsePageNew(WebDriver driver, String dob, String subscriberId, String verifyLastName,
			String verifyFirstName, String zip, String serviceDate, String officeName, String locationProvider,
			String fname, String lname, String insuranceName, String location, boolean checkSub) throws Exception {
		if (dob.equals(""))
			return null;
		navigatetoEligiblityNew(driver);
		EligibilityDto dto = new EligibilityDto();
		String[] dobA = dob.split("/");
		try {
			List<WebElement> lis = driver.findElements(By.tagName("li"));
			for (WebElement li : lis) {
				String na = li.getAttribute("data-tabname");
				if (na != null && na.equals("Eligibility")) {
					li.findElement(By.tagName("a")).click();
					break;
				}
			}
			// Select Location only for Beaumont or Jasper
			//Here we pass Provider as Location From UI..
			String ori = "/html/body/div[3]/div/div[2]/div/div/div[2]/div/div/div/div/div[2]/div[2]/div/div[2]/div/div/div/div/div[1]/div[1]/div[2]/div/div/select/option";
			if (officeName.equalsIgnoreCase("Beaumont") || officeName.equalsIgnoreCase("Jasper")) {
				Thread.sleep(3000);
				
				try {
					List<WebElement> wList =	driver.findElement(By.className("testScript_blp_location")).findElements(By.tagName("option"));	
					for (WebElement w : wList) {

						if (location.equals("")) {
							w.click();
						} else {
							String nm = w.getText();
							if (!locationProvider.equals("") && nm.replaceAll(" ", "").toLowerCase().contains(locationProvider.replaceAll(" ", "").toLowerCase())) {
								w.click();
								break;
							}
							w.click();
						}

					}
					Thread.sleep(3000);
				}catch(Exception lp ) {
					
				}
				//ori = "/html/body/div[3]/div/div[2]/div/div/div[2]/div/div/div/div/div[2]/div[2]/div/div[2]/div/div/div/div/div[1]/div[1]/div[2]/div/div/select/option";
				// need Credential for This to write code
				try {
					
					
					List<WebElement> wList = driver.findElement(By.className("testScript_blp_provider")).findElements(By.tagName("option"));
					

					for (WebElement w : wList) {

						if (location.equals("")) {
							w.click();
						} else {
							String nm = w.getText();
							if (nm.replaceAll(" ", "").equalsIgnoreCase(location.replaceAll(" ", ""))) {
								w.click();
								break;
							}
							w.click();
						}

					}
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}

			// Select select box with any option here we have selected first one.
			// Select se = new
			// Select(driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div[2]/div/div/div/div/div[2]/div[2]/div/div[2]/div/div/div/div/div[1]/div[1]/div[2]/div/div/select")));
			// System.out.println(se.);
			// se.selectByIndex(1);

			if (!(officeName.equalsIgnoreCase("Beaumont") || officeName.equalsIgnoreCase("Jasper"))) {

			List<WebElement> wList = driver

					.findElements(By.xpath(ori));
			for (WebElement w : wList) {

				if (location.equals("")) {
					w.click();
				} else {
					String nm = w.getText();
					if (nm.replaceAll(" ", "").equalsIgnoreCase(location.replaceAll(" ", ""))) {
						w.click();
						break;
					}
					w.click();
				}
			}
			}
			// 470:111;a

			WebElement sdate = driver.findElement(By.className("0memberSearchServiceDate"));
			if (serviceDate != null && !serviceDate.equals("")) {
				sdate.clear();
				sdate.sendKeys(serviceDate);
			}

			WebElement element3 = driver.findElement(By.className("0memberSearchBirthdate"));
			element3.clear();
			element3.sendKeys(dobA[0] + "/" + dobA[1] + "/" + dobA[2]);

			element3 = driver.findElement(By.className("0memberSearchMemberNumber"));
			element3.clear();

			if (checkSub && (!subscriberId.equalsIgnoreCase("NA")) && !subscriberId.trim().equals("")) {
				element3.sendKeys(subscriberId);
			}

			element3 = driver.findElement(By.className("0memberSearchLastName"));
			element3.clear();
			if (!subscriberId.equalsIgnoreCase("NA") && !checkSub) {
				element3.sendKeys(verifyLastName);
			}
			element3 = driver.findElement(By.className("0memberSearchFirstName"));
			element3.clear();

			if (!subscriberId.equalsIgnoreCase("NA") && !checkSub) {
				element3.sendKeys(verifyFirstName);
			}

			//
			try {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("return document.getElementById('walkme-player').remove();");
			} catch (Exception p) {

			}
			driver.findElement(By.id("searchClick")).click();
			Thread.sleep(8000);
			boolean dataFound = false;
			dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
			dto.setEligible(ConstantsScrapping.SUBSCRIBER_Eligible);
			WebElement dataDiv = null;
			List<WebElement> trs = null;
			try {
				dataDiv = driver.findElement(By.id("Eligible"));
				trs = dataDiv.findElements(By.tagName("tr"));
				if (trs.size() > 0) {
					dataFound = true;
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_FOUND);
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_Eligible);
				}

			} catch (Exception p) {

			}

			if (!dataFound) {
				try {
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
					dataDiv = driver.findElement(By.id("outOfNetwork"));
					trs = dataDiv.findElements(By.tagName("tr"));
					if (trs.size() > 0) {
						dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
					}
				} catch (Exception p) {

				}
				try {
					dataDiv = driver.findElement(By.id("notFound"));
					trs = dataDiv.findElements(By.tagName("tr"));
					if (trs.size() > 0) {
						dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
					}
				} catch (Exception p) {

				}
				try {
					dataDiv = driver.findElement(By.id("notEligible"));
					trs = dataDiv.findElements(By.tagName("tr"));
					if (trs.size() > 0) {
						dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
					}
				} catch (Exception p) {

				}
			}
			// outOfNetwork
			// notFound
			// notEligible
			// Eligible
			// if tr then data is there
			/*
			 * Devin13458 Devine%1245976
			 * https://connectsso.dentaquest.com/authsso/providersso/SSOProviderLogin.aspx?
			 * TYPE=33554433&REALMOID=06-6a4c193d-7520-4f3d-b194-83367a3ef454&GUID=&
			 * SMAUTHREASON=0&METHOD=POST&SMAGENTNAME=-SM-imZolSjcs1FQR%
			 * 2fH0k3NSK1Uvx4zWgziEWSOuwqcKGG1C%2bW%2fQdG3dRa7BVqGyOpNh&TARGET=-SM-https%3a%
			 * 2f%2fconnectsso%2edentaquest%2ecom%2fprovideraccessv2%2findex%2ehtml
			 * 
			 * 
			 * 1036 Frank Duque 739090558 03/31/2020 53012999 10/28/2009
			 */
			// boolean closeNewTab=false;
			if (dataFound) {
				String planName = "";
				String pName = "";
				List<WebElement> tds = trs.get(1).findElements(By.tagName("td"));
				planName = tds.get(5).getText();
				pName = tds.get(4).getText();
				tds.get(4).findElement(By.tagName("a")).click();
				Thread.sleep(8000);
				// Shift tab
				ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
				// closeNewTab=true;
				driver.switchTo().window(tabs2.get(1));
				dto.setCopay("");
				dto.setEmployerName(planName); /// Plan Internally this becomes the Employers name for us at Capline
				if (planName.toLowerCase().contains("chip")) {
					try {
						String[] cp = driver.findElement(By.className("slds-panel__section"))
								.findElements(By.tagName("li")).get(0).getText().replace("Plan", "").split("\\$");
						dto.setCopay("$" + cp[1].replace("Co-pay", "").trim());
						dto.setEmployerName(planName.split("\\$")[0].replace(" - ", "")); /// Plan Internally this
																							/// becomes the Employers
																							/// name for us at Capline
					} catch (Exception e) {

					}
				}
				try {
					dto.setFirstName(fname);
					dto.setLastName(lname);
					String names[] = pName.split(" ");
					if (verifyFirstName.equals("")) {
						dto.setFirstName(names[0]);
					}
					if (verifyLastName.equals("")) {
						dto.setLastName(names[1]);
					}
				} catch (Exception na) {

				}
				if (!checkSub) {
					try {
						dto.setSubscriberId(driver.findElement(By.className("slds-panel__section"))
								.findElements(By.tagName("li")).get(1).findElement(By.tagName("span")).getText());

					} catch (Exception e) {

					}
				} else {
					dto.setSubscriberId(subscriberId);
				}
				WebElement moc = null;
				try {
					moc = driver.findElement(By.className("memberOverviewContainer"));
					List<WebElement> dds = moc.findElements(By.tagName("dd"));
					for (WebElement dd : dds) {
						String t = dd.getAttribute("title");
						if (t != null && t.equals("Provider Name"))
							dto.setProviderName(dd.getText());
						else if (t != null && t.equals("Provider Office"))
							dto.setProviderName(dto.getProviderName() + " " + dd.getText());

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					String covereage = driver.findElement(By.xpath("//*[@id=\"other\"]/tbody/tr/td[1]")).getText();
					
					dto.setComment(covereage);// OTHER COVERAGE
																						// //comment==Other Covreage
				} catch (Exception e) {
					e.printStackTrace();
					try {
						String covereage = driver.findElement(By.tagName("h5")).getText();
						
						dto.setComment(covereage);// OTHER COVERAGE
																							// //comment==Other Covreage
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				try {
					// Where is benefit remaining
					dto.setBenefitRemaining("");
					List<WebElement> trds = driver.findElement(By.id("benefits")).findElement(By.tagName("tbody"))
							.findElements(By.tagName("td"));
					for (WebElement trd : trds) {
						String t = trd.getAttribute("data-label");
						if (t != null && t.equals("Remaining")) {
							dto.setBenefitRemaining(trd.getText().trim());
							break;
						}
					}
				} catch (Exception e) {

				}

				// history
				try {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("return document.getElementById('walkme-player').remove();");
				} catch (Exception p) {

				}
				try {
					WebElement tabs = driver.findElement(By.className("slds-tabs_default"));
					lis = tabs.findElements(By.tagName("li"));
					for (WebElement li : lis) {
						String t = li.getAttribute("title");
						if (t != null && t.equals("Service History")) {
							li.click();
							Thread.sleep(3000);
							break;
						}

						//
					}
					WebElement htable = driver.findElement(By.id("historyTable"));
					List<WebElement> ele = htable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
					for (WebElement e : ele) {
						HistoryDto hd = new HistoryDto();
						List<WebElement> ts = e.findElements(By.tagName("td"));

						hd.setDos(ts.get(0).getText());// date
						hd.setCode(ts.get(1).getText());// code
						hd.setTooth("");
						hd.setSurface("");
						//List<String> t = null;

						try {
							/*
							String tooth[] = ts.get(3).getText().split("/");
							if (!tooth[3].equals("-")) {
								hd.setSurface(tooth[3].replace("-", "").replace(" ", ""));
							}
							if (!tooth[0].equals("-")) {
								if (t == null)
									t = new ArrayList<>();
								t.add(tooth[0]);
							}
							if (!tooth[1].equals("-")) {
								if (t == null)
									t = new ArrayList<>();
								t.add(tooth[1].replace("-", "").replace(" ", ""));
							}
							if (!tooth[2].equals("-")) {
								if (t == null)
									t = new ArrayList<>();
								t.add(tooth[2].replace("-", "").replace(" ", ""));
							}
							

							if (t != null)
								hd.setTooth(String.join(",", t));
						  */
							hd.setTooth(fetchToothFromHistoryDataNew(ts.get(3).getText()));
						} catch (Exception n) {
							n.printStackTrace();
						}
						dto.getHistoryList().add(hd);
					}

					while (true) {
						if (!fetchPagginationHistoryNew(driver, dto))
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				driver.close();
				driver.switchTo().window(tabs2.get(0));

			} else {

			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		dto.setDob(dob);
		dto.setInsuranceName(insuranceName);
		// dto.setFirstName(fname);
		// dto.setLastName(lname);
		dto.setSubscriberId(subscriberId);
		System.out.println(dto.getFirstName());
		return dto;

	}

	
	private String fetchToothFromHistoryDataNew(String tooths) {
		String tooth="";
		if (tooths!=null) {
		 String[] th=tooths.split("/");
		 for(String t:th) {
			 if (!t.trim().equals("-")) {
				 tooth=t;
				 break;
			 }
		 }
		}
		return tooth;
	}
	private boolean fetchPagginationHistoryNew(WebDriver driver, EligibilityDto dto) {

		WebElement historyTab = driver.findElement(By.id("history-tab"));
		List<WebElement> aas = historyTab.findElements(By.tagName("a"));
		boolean callNext = false;
		// System.out.println("CALL>...");
		try {
			for (WebElement a : aas) {
				if (a.getText() != null && a.getText().equals("Next")) {
					// System.out.println("CALL>... FOUND");
					callNext = true;
					a.click();
					Thread.sleep(3000);
					WebElement htable = driver.findElement(By.id("historyTable"));
					List<WebElement> ele = htable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
					for (WebElement e : ele) {
						HistoryDto hd = new HistoryDto();
						List<WebElement> ts = e.findElements(By.tagName("td"));

						hd.setDos(ts.get(0).getText());// date
						hd.setCode(ts.get(1).getText());// code
						hd.setTooth("");
						hd.setSurface("");
						//List<String> t = null;

						try {
							/*String tooth[] = ts.get(3).getText().split("/");
							if (!tooth[3].equals("-")) {
								hd.setSurface(tooth[3].replace("-", "").replace(" ", ""));
							}
							if (!tooth[0].equals("-")) {
								if (t == null)
									t = new ArrayList<>();
								t.add(tooth[0]);
							}
							if (!tooth[1].equals("-")) {
								if (t == null)
									t = new ArrayList<>();
								t.add(tooth[1].replace("-", "").replace(" ", ""));
							}
							if (!tooth[2].equals("-")) {
								if (t == null)
									t = new ArrayList<>();
								t.add(tooth[2].replace("-", "").replace(" ", ""));
							}

							if (t != null)
								hd.setTooth(String.join(",", t));
						  */
							hd.setTooth(fetchToothFromHistoryDataNew(ts.get(3).getText()));
						} catch (Exception n) {
							n.printStackTrace();
						}
						dto.getHistoryList().add(hd);
					}
					break;
				}
			}
		} catch (Exception e) {
			callNext = false;
			// TODO: handle exception
		}

		return callNext;

	}

	private void navigatetoEligiblityOld(WebDriver driver) throws InterruptedException {
		driver.navigate().to(
				"https://govservices.dentaquest.com/Router.jsp?source=SearchMember&component=Members&code=MEMBER_ELIG_SEARCH&targetLink=true");
		Thread.sleep(8000);
	}

	private void navigatetoEligiblityNew(WebDriver driver) throws InterruptedException {
		driver.navigate().to("https://provideraccess.dentaquest.com/s/");
		Thread.sleep(5000);

	}

	private EligibilityDto parsePageOld(WebDriver driver, String dob, String subscriberId, String verifyLastName,
			String verifyFirstName, String zip, String locationProvider, String fname, String lname,
			String insuranceName, boolean checkSub) throws Exception {
		if (dob.equals(""))
			return null;
		navigatetoEligiblityOld(driver);
		EligibilityDto dto = new EligibilityDto();
		String[] dobA = dob.split("/");
		// Select select box with any option here we have selected last one.
		List<WebElement> wList = driver

				.findElements(By.xpath("/html/body/table[3]/tbody/tr/td[3]/div[2]/form/div[3]/b/select/option"));
		// **/html/body/table[3]/tbody/tr/td[3]/form/div[2]/select/option */
		/*
		 * locationProvider="Geetika Rastogi";
		 * 
		 * for (WebElement w : wList) {
		 * 
		 * if (locationProvider.equals("")) { //this will never work now but keep this
		 * ((HtmlUnitWebElement) w).click(); } else {
		 * 
		 * String nm = w.getText(); if (nm.replaceAll(" ",
		 * "").replaceAll(" ","").toLowerCase().trim().contains(locationProvider.
		 * replaceAll(" ","").trim().toLowerCase())) { ((HtmlUnitWebElement) w).click();
		 * break; } ((HtmlUnitWebElement) w).click(); } }
		 */
		for (WebElement w : wList) {

			if (locationProvider.equals("")) {
				((HtmlUnitWebElement) w).click();
			} else {
				String nm = w.getText();
				if (nm.replaceAll(" ", "").equalsIgnoreCase(locationProvider.replaceAll(" ", ""))) {
					((HtmlUnitWebElement) w).click();
					break;
				}
				((HtmlUnitWebElement) w).click();
			}
		}

		WebElement element3 = driver.findElement(By.id("Q061MEMBER0dob"));
		element3.clear();
		element3.sendKeys(dobA[0] + "/" + dobA[1] + "/" + dobA[2]);

		element3 = driver.findElement(By.id("Q061MEMBER0memberNo"));
		element3.clear();

		if (checkSub && (!subscriberId.equalsIgnoreCase("NA")) && !subscriberId.trim().equals("")) {
			element3.sendKeys(subscriberId);
		}

		element3 = driver.findElement(By.id("Q061MEMBER0lastName"));
		element3.clear();
		if (!subscriberId.equalsIgnoreCase("NA") && !checkSub) {
			element3.sendKeys(verifyLastName);
		}

		element3 = driver.findElement(By.id("Q061MEMBER0firstName"));
		element3.clear();

		if (!subscriberId.equalsIgnoreCase("NA") && !checkSub) {
			element3.sendKeys(verifyFirstName);
		}

		driver.findElement(By.name("Search")).click();
		Thread.sleep(10000);
		// Fetch Data
		wList = driver.findElements(By.className("results"));
		boolean eligble = false;
		if (wList.size() > 0) {
			// Active
			List<WebElement> wListChild = wList.get(0).findElements(By.tagName("tr"));
			int x = 0;
			// boolean intervention=false;
			// System.out.println(driver.getPageSource());
			int interventionCount = 0;
			for (WebElement child : wListChild) {
				if (x == 0) {
					x++;

					if (child.getText().contains(" Intervention ")) {
						// intervention=true;
						interventionCount = interventionCount + 1;
					}
					continue;
				}
				// System.out.println("child.getText()"+child.getText());
				if (child.getText().contains("Multiple potential members found")) {
					break;
				}
				if (child.getText().equals("No Results Found")) {
					break;
				} else {
					eligble = true;
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_FOUND);
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_Eligible);
					List<WebElement> wListChildTD = child.findElements(By.tagName("td"));
					// if (wListChildTD.size()>0)System.out.println(wListChildTD.get(0).getText());
					if (wListChildTD.size() > 1) {
						// dto.setS
						// System.out.println("Service Date-"+wListChildTD.get(1).getText());
					}
					if (wListChildTD.size() > 2) {
						// System.out.println("Subscriber-"+wListChildTD.get(2).getText());
						// dto.setS
					}
					if (wListChildTD.size() > 3) {
						// System.out.println("DOB-"+wListChildTD.get(3).getText());
					}
					if (wListChildTD.size() > (4 + interventionCount)) {
						// System.out.println("Name-"+wListChildTD.get(4+interventionCount).getText());
					}
					if (wListChildTD.size() > (6 + interventionCount)) {
						dto.setEmployerName(wListChildTD.get(6 + interventionCount).getText());// Plan
						// System.out.println("Plan -"+wListChildTD.get(6+interventionCount).getText());
					}
					if (wListChildTD.size() > (12 + interventionCount)) {
						// System.out.println("Provider
						// Name.-"+wListChildTD.get(13+interventionCount).getText());
						dto.setProviderName(wListChildTD.get(12 + interventionCount).getText());// Dentist/Office Name
					}

					if (wListChildTD.size() > 3) {
						// click on Name:
						wListChildTD.get(3).findElement(By.tagName("a")).click();
						Thread.sleep(5000);
						// History click
						driver.findElement(By.xpath(
								"/html/body/table[3]/tbody/tr/td[3]/div[2]/form/div[4]/table[2]/tbody/tr/td/a[3]"))
								.click();
						;
						// html/body/table[3]/tbody/tr/td[3]/form/div[3]/table[3]/tbody/tr/td/a[3]
						Thread.sleep(5000);
						// parse History
						List<WebElement> wListChildTDHis = driver.findElements(
								By.xpath("/html/body/table[3]/tbody/tr/td[3]/div[2]/form/div[3]/table[3]/tbody/tr"));
						// html/body/table[3]/tbody/tr/td[3]/form/div[2]/table[4]/tbody/tr
						// if (wListChildTDHis!=null && wListChildTDHis.size()>0 ) {
						for (WebElement ele : wListChildTDHis) {
							HistoryDto hd = new HistoryDto();
							List<WebElement> eL = ele.findElements(By.tagName("td"));
							if (eL.size() > 0) {
								hd.setCode(eL.get(0).getText());

							}
							if (eL.size() > 1) {
								hd.setDescription(eL.get(1).getText());
							}
							if (eL.size() > 2) {
								hd.setTooth(eL.get(2).getText());
							}
							if (eL.size() > 4) {
								hd.setDos(eL.get(4).getText());
							}
							if (eL.size() == 0) {
								hd.setCode("No Treatment History was found for this subscriber.");
								hd.setDescription("");
								hd.setTooth("");
								hd.setDos("");

							}
							dto.getHistoryList().add(hd);
						}

						driver.navigate().to(
								"https://govservices.dentaquest.com/Router.jsp?source=MemberDetail&component=MemberDetails&breadcrumb=true");
						Thread.sleep(5000);
						// Click on view Benefit max..
						driver.findElement(By.xpath(
								"/html/body/table[3]/tbody/tr/td[3]/div[2]/form/div[4]/table[2]/tbody/tr/td/a[1]"))
								.click();
						// html/body/table[3]/tbody/tr/td[3]/form/div[3]/table[3]/tbody/tr/td/a[1]

						Thread.sleep(5000);
						// copay and Remaining Benefit.
						dto.setComment("");
						if (driver.findElement(By.xpath(
								"/html/body/table[3]/tbody/tr/td[3]/div[2]/form/table[6]/tbody/tr/td[1]")) != null) {
							// html/body/table[3]/tbody/tr/td[3]/form/table[8]/tbody/tr
							String cp[] = driver
									.findElement(By.xpath(
											"/html/body/table[3]/tbody/tr/td[3]/div[2]/form/table[6]/tbody/tr/td[1]"))
									.getText().replace("\n", "").replace("\r", "").split("Benefit: ");
							if (cp.length > 1 && cp[1].contains("Co-pay")) {
								cp = cp[1].split(" Co-pay");
								cp = cp[0].split("\\$");
								if (cp.length > 1) {
									dto.setCopay("$" + cp[1]);

								}
							} else if (cp.length > 1 && cp[1].contains(" Copay")) {
								cp = cp[1].split(" Copay");
								cp = cp[0].split("\\$");
								if (cp.length > 1) {
									dto.setCopay("$" + cp[1]);

								}
							}
						}

						wListChildTD = driver.findElements(
								By.xpath("/html/body/table[3]/tbody/tr/td[3]/div[2]/form/table[7]/tbody/tr"));
						// html/body/table[3]/tbody/tr/td[3]/form/table[9]/tbody/tr
						for (WebElement ele : wListChildTD) {
							if (ele.getText().equalsIgnoreCase("No Results Found")) {
								// System.out.println("Remaining benefits not found....");
							} else {
								List<WebElement> wListChildTDD = ele.findElements(By.tagName("td"));
								if (wListChildTDD.size() > 8) {
									// System.out.println("Remaining ----"+ wListChildTDD.get(8).getText());
									dto.setBenefitRemaining(wListChildTDD.get(8).getText());
								}
							}
						}

					}
					// Parse sub data
				}
			}
		}
		if (wList.size() > 1 && !eligble) {
			// Ineligible
			List<WebElement> wListChild = wList.get(1).findElements(By.tagName("tr"));
			int x = 0;
			for (WebElement child : wListChild) {
				if (x == 0) {
					x++;
					continue;
				}
				if (child.getText().equals("No Results Found")) {
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
					// System.out.println(child.getText()+" ( Ineligible)");

					break;
				} else {
					// System.out.println("PAt is Ineligible");
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
					eligble = true;
				}
			}
		}
		if (wList.size() > 2 && !eligble) {
			// not found
			List<WebElement> wListChild = wList.get(2).findElements(By.tagName("tr"));
			int x = 0;
			for (WebElement child : wListChild) {
				if (x == 0) {
					x++;
					continue;
				}
				if (child.getText().equals("No Results Found Ineligible")) {
					// System.out.println(child.getText());

					break;
				} else {
					// System.out.println("Pat not found ....");
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
				}
			}
		}
		dto.setDob(dob);
		dto.setInsuranceName(insuranceName);
		dto.setFirstName(fname);
		dto.setLastName(lname);
		dto.setSubscriberId(subscriberId);
		return dto;
	}

}
