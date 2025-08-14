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

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.PatientScrapSearchDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.scrapping.DentaBenefitScrapDto;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.PatientDetailTemp;
import com.tricon.ruleengine.model.db.PatientDetailTemp2;
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

public class DeltaDentalServiceImpl extends BasefullScrapImpl implements Callable<Boolean> {

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
	private static String MIN_VAL="_MINVAL_";
	private static String MAX_VAL="_MAXVAL_";
	private static String MIN_MAX_VAL="_MIN_MAX_VAL_";
	private static String MIN_MAX_VAL_SPLIT="--SPSPSP--";
	private static String CLEAR_ID="";
	
	
	// private static String counterLink = "";
	// private static HashMap<String, String> counterElementMap = null;

	// private static String benefitMaximumLifeTime = "Maximum Life Time ";
	// private static String benefitMaximumLifeTimeRem = "Maximum Life Time Rem";

	public DeltaDentalServiceImpl(PatientDao patDao, ScrapingFullDataDoa dataDoa,
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
		setProps(scrappingSiteDetails.getProxyPort());
		 ///System.out.println("888888888888 -Start " + new Date());
		try {

			System.out.println("MEM 3-" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
			// int one = 0;
			try {
				for (PatientScrapSearchDto data : dto.getDto()) {
					Thread thread = new Thread() {
						public void run() {
							String s = "0";
							// System.out.println("Thread Running");

							WebDriver driver = getBrowserDriver();// new HtmlUnitDriver(true);// getBrowserDriver();
							try {
								boolean navigate = loginToSiteDelta(dto, driver);
								boolean issueNo = navigatetoMainSite(driver, navigate);
								 //System.out.println(" DeltaDentalServiceImpl ..888888888888- STARTED...");
								 //System.out.println("MEM 4-"
								 //+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
								PatientTemp d = parsePage(driver, data, siteName, issueNo, office);
								 System.out.println(new Date());
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
							}
						}
					};

					thread.start();
					Thread.sleep(15000);
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
		scrapSite(scrappingSiteDetails, dto, user, office);
		return true;
	}

	private PatientTemp parsePage(WebDriver driver, PatientScrapSearchDto sh, String webSiteName, boolean issueNo,
			Office office) throws InterruptedException {
		PatientTemp temp = new PatientTemp();
		try {
			temp.setDob(sh.getDob());
			temp.setSubscribersDob(sh.getSubscribersDob());
			temp.setSubscribersFirstName(sh.getSubscribersFirstName());
			temp.setSubscribersLastName(sh.getSubscribersLastName());

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
			t.setiVFormType(ivFormType);
            
			Set<PatientDetailTemp> s = new HashSet<>();
			s.add(t);
			temp.setPatientDetails(s);
			if (!issueNo)
				return temp;
			issueNo = searchPatient(driver, temp, sh.getMemberId(), sh.getSsnNumber(), sh.getDob(), sh.getFirstName(),
					sh.getLastName(), sh.getSsnNumber(), sh.getSubscribersFirstName(), sh.getSubscribersLastName(),
					sh.getSubscribersDob());
			if (!issueNo) {
				temp.setStatus("Patient Not found.." + sh.getDob() + " " + sh.getMemberId());
				temp.setFirstName(sh.getFirstName());
				temp.setLastName(sh.getLastName());

				return temp;
			}
			temp.setStatus(Constants.PATIENT_FOUND);
			temp.setFirstName(sh.getFirstName());
			temp.setLastName(sh.getLastName());
			populateMandatoryData(temp);
			createPatientDetailSetup(driver, temp, sh);
			// Click on Eligibility & benefits Link so that Next Patient can Start to be
			// Searched
			/*
			 * List<WebElement> myPats = driver.findElements(By.tagName("a")); for
			 * (WebElement myPat : myPats) { if (myPat.getText() != null &&
			 * myPat.getText().trim().equals("Eligibility & benefits")) {//Eligibility &
			 * benefits //My patients Thread.sleep(2000); myPat.click(); break; } }
			 * Thread.sleep(2000);
			 */
			// Logic to fetch data from Site...
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		try {
			fetchPatDetails(driver, temp);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			temp.setStatus("Patient Fetch data issue");
		}

	}

	private boolean searchPatient(WebDriver driver, PatientTemp temp, String memberid, String ssn1, String dob,
			String fName, String lName, String ssn, String sfn, String sln, String sdob) throws InterruptedException {

		// String id = memberid.trim().equals("") ? ssn.trim() : memberid.trim();
		// id.equals("") ||
		if (dob.equals("") || fName.trim().equals("") || lName.trim().equals(""))
			return false;
		// "/html/body/div[2]/form/div[2]/div[2]/div[1]/table/tbody/tr/td/div/div/table/tbody/tr[2]/td[2]/table/tbody/tr[5]/td[1]/span/input"
		WebElement ini = driver.findElement(By.xpath(
				"/html/body/div[2]/form/div[2]/div[2]/div[1]/table/tbody/tr/td/div/div/table/tbody/tr[2]/td[2]/table/tbody/tr[5]/td[1]/span/input"));
		ini.clear();
		Thread.sleep(2000);
		// "/html/body/div[2]/form/div[2]/div[2]/div[1]/table/tbody/tr/td/div/div/table/tbody/tr[2]/td[2]/table/tbody/tr[5]/td[2]/button"
		WebElement validate = driver.findElement(By.xpath(
				"/html/body/div[2]/form/div[2]/div[2]/div[1]/table/tbody/tr/td/div/div/table/tbody/tr[2]/td[2]/table/tbody/tr[5]/td[2]/button"));
		ini.sendKeys(fName.trim() + " " + lName.trim());
		try {
			validate.click();
		} catch (Exception e) {
			return false;
		}
		Thread.sleep(7000);
		boolean found = true;
		// Do form here..dob.
		List<WebElement> tabs = driver.findElements(By.tagName("table"));
		for (WebElement tab : tabs) {
			if (tab.getText().contains("No patient record available.")) {
				found = false;
				break;
			}
		}

		if (found) {
			List<WebElement> spans = driver.findElements(By.tagName("span"));
			for (WebElement span : spans) {
				if (span.getText().contains("Please make a selection:")) {
					{
						found = false;
						break;
					}
				}
				if (!found)
					break;
			}
			if (!found) {
				Date newDate = new Date();
				Object[] others = null;
				// Object[] empty=null;
				Object[] ppo = null;
				// Object[] atleastOneEnd=null;
				List<Object[]> all = new ArrayList<>();
				List<Object[]> allNew = new ArrayList<>();

				List<WebElement> trs = driver.findElements(By.tagName("tr"));
				for (WebElement tr : trs) {
					if (tr.getAttribute("_afrrk") != null) {
						List<WebElement> tds = tr.findElements(By.tagName("td"));
						System.out.println("-" + tds.get(20).getText().trim() + "-");

						try {
							Date d1 = Constants.SIMPLE_DATE_FORMAT.parse(tds.get(20).getText().trim());
							
							if (tds.get(20).getText().trim().equals("")) {
								System.out.println(">>"+ tr.getText().toLowerCase());
								all.add(new Object[] { tr.getText().toLowerCase().contains("ppo"), tds.get(3), d1,
										newDate });

							} else {
								
								if (newDate.compareTo(d1) <= 0) {
									System.out.println(">>::>>"+ tr.getText().toLowerCase());
									System.out.println(">>::>>"+ tds.get(3).getText());
									all.add(new Object[] { tr.getText().toLowerCase().contains("ppo"), tds.get(3), d1,
											Constants.SIMPLE_DATE_FORMAT.parse(tds.get(20).getText().trim()) });
								}
							}

						} catch (Exception p) {

						}
					}
				}

				// Remove Last Date if date less than today
				for (Object[] o : all) {
					if ((newDate).compareTo((Date) o[3]) <= 0) {
						allNew.add(o);
					}
				}

				for (Object[] o : allNew) {
					if ((boolean) o[0]) {
						if (ppo == null) {
							ppo = new Object[] { o[1], o[2] };
						} else if (((Date) ppo[1]).compareTo((Date) o[2]) <= 0) {
							ppo[1] = o[2];
							ppo[0] = o[1];
						}

					} else {
						if (others == null) {
							others = new Object[] { o[1], o[2] };
						} else if (((Date) others[1]).compareTo((Date) o[2]) <= 0) {
							others[1] = o[2];
							others[0] = o[1];
						}
					}
				}
				if (ppo != null) {
					// System.out.println("ppo");
					// System.out.println(((WebElement)ppo[0]).getText());
					// System.out.println(((Date)ppo[1]));
					((WebElement) ppo[0]).findElement(By.tagName("a")).click();

				} else if (others != null) {
					// System.out.println("OO");
					// System.out.println(((WebElement)others[0]).getText());
					// System.out.println(((Date)others[1]));
					((WebElement) others[0]).findElement(By.tagName("a")).click();

				}
				/*
				 * for(WebElement tr:trs) { if (tr.getAttribute("_afrrk")!=null ) {
				 * List<WebElement> tds =tr.findElements(By.tagName("td"));
				 * System.out.println("-"+tds.get(20).getText().trim()+"-");
				 * 
				 * try {
				 * 
				 * if (tds.get(20).getText().trim().equals("")) { Date
				 * d1=Constants.SIMPLE_DATE_FORMAT.parse(tds.get(18).getText().trim()); if
				 * (empty==null){ empty = new Object[] {tds.get(3),d1,newDate}; }else { if
				 * (((Date)empty[1]).compareTo(d1)<=0){ empty[1]=d1; empty[0]=tds.get(3); } } }
				 * if (tds.get(20).getText().trim().equals("")) { Date
				 * d1=Constants.SIMPLE_DATE_FORMAT.parse(tds.get(18).getText().trim()); if
				 * (tr.getText().toLowerCase().contains("ppo")){ if (ppo==null){ ppo = new
				 * Object[] {tds.get(3),d1,newDate}; }else if (((Date)ppo[1]).compareTo(d1)<=0){
				 * ppo[1]=d1; ppo[0]=tds.get(3);
				 * 
				 * }
				 * 
				 * }else { if (others==null){ others = new Object[] {tds.get(3),d1,newDate};
				 * }else if (((Date)others[1]).compareTo(d1)<=0){ others[1]=d1;
				 * others[0]=tds.get(3);
				 * 
				 * } }
				 * 
				 * }else {
				 * 
				 * Date d1=Constants.SIMPLE_DATE_FORMAT.parse(tds.get(20).getText().trim());
				 * Date ds=Constants.SIMPLE_DATE_FORMAT.parse(tds.get(18).getText().trim()); if
				 * (atleastOneEnd==null){ atleastOneEnd = new Object[] {tds.get(3),ds,d1}; } if
				 * (newDate.compareTo(d1)>0) { atleastOneEnd=null;
				 * 
				 * }
				 * 
				 * 
				 * if (newDate.compareTo(d1)<=0) { found =true; tds.get(3).click();
				 * others=empty=ppo=null; break; } } //if (found) break; }catch(Exception p) {
				 * 
				 * }
				 * 
				 * } } if (ppo!=null) { System.out.println(((WebElement)ppo[0]).getText());
				 * ((WebElement)ppo[0]).click();
				 * 
				 * }else if (others!=null) {
				 * System.out.println(((WebElement)others[0]).getText());
				 * ((WebElement)others[0]).click();
				 * 
				 * }else if (empty!=null) { ((WebElement)empty[0]).click();
				 * 
				 * }
				 */
			}

			Thread.sleep(8000);
			
			
			// write code to add patient in Website after that do view Click on Eligibility
			
			 try {
			 List<WebElement> rows = driver.findElements(By.className("data-table-row"));
			  for(WebElement row : rows) {
			  
			  List<WebElement> tds = row.findElements(By.tagName("td"));
			  //List<WebElement>d=tds.get(7).findElements(By.tagName("span")).get(1).getText();
			  String name = tds.get(7).findElements(By.tagName("span")).get(1).getText();
			  // findElement(By.tagName("span")).getText();
			  String[] dobA =tds.get(7).findElements(By.tagName("span")).get(0).findElements(By.tagName("span")).get(3).getText().split("/");
			  String dobS = ((dobA[0].length() == 1) ? ("0" + dobA[0]) : dobA[0]) + "/" + ((dobA[1].length() == 1) ? ("0" +  dobA[1]) : dobA[1]) + "/" + dobA[2];
			  
			  String status = tds.get(14).getText();
			  System.out.println("statusstatus-"+status);
			  if  (status.trim().equalsIgnoreCase("no")) continue;//Check only for active users
			  if (name.equalsIgnoreCase((fName.trim() + " " + lName.trim())) //&&  id.contains(mem) 
					  && dobS.equals(dob)) { // found=true;
			  row.findElement(By.className("table-col8")).findElements(By.tagName("a")).get(0).click();
			  Thread.sleep(8000); 
			  return true;
			  
			    } 
			  }
			  } catch (Exception e) {
				  //return false; // TODO: handle exception
			  }
			return true;
		}
		if (!found) {
			List<WebElement> buts = driver.findElements(By.tagName("button"));
			for (WebElement but : buts) {
				if (but.getText().contains("Add new patient")) {
					but.click();

					break;
				}
			}
			Thread.sleep(15000);
			/* OLD CODE
			 * WebElement fn = driver.findElement(By.xpath(
					"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[6]/span/table[2]/tbody/tr/td[1]/div/div[3]/span/input"));
			WebElement ln = driver.findElement(By.xpath(
					"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[6]/span/table[2]/tbody/tr/td[3]/div/div[3]/span/input"));
			WebElement en = driver.findElement(By.xpath(
					"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[6]/span/table[3]/tbody/tr/td[1]/div/div[3]/span/input"));
			WebElement db = driver.findElement(By.xpath(
					"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[6]/span/table[3]/tbody/tr/td[3]/div/div[3]/span/input"));
			WebElement dfn = driver.findElement(By.xpath(
					"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[6]/span/table[5]/tbody/tr/td[1]/div/div[3]/span/input"));
			WebElement ddb = driver.findElement(By.xpath(
					"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[6]/span/table[5]/tbody/tr/td[3]/div/div[3]/table/tbody/tr/td[2]/input"));
			if ((fName.trim() + lName.trim()).equals(sfn.trim() + sln.trim())) {
				fn.sendKeys(fName.trim());
				ln.sendKeys(lName.trim());
				en.sendKeys(memberid.trim());
				db.sendKeys(dob);

			} else {
				fn.sendKeys(sfn.trim());
				ln.sendKeys(sln.trim());
				en.sendKeys(memberid.trim());
				db.sendKeys(sdob);
				dfn.sendKeys(fName.trim());
				ddb.sendKeys(dob);
				// dln.sendKeys(lName.trim());
				Thread.sleep(3000);
				WebElement oth = driver.findElement(By.xpath(
						"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[6]/span/div/div[3]/table/tbody/tr/td[2]/select"));
				Select dropdown = new Select(oth);
				Thread.sleep(3000);
				dropdown.selectByVisibleText("Other");

			}
            */
			 WebElement fn = driver.findElement(By.xpath(
						"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[7]/span/div[3]/div[1]/table/tbody/tr/td[1]/div/div[3]/span/input"));
				WebElement ln = driver.findElement(By.xpath(
						"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[7]/span/div[3]/div[1]/table/tbody/tr/td[3]/div/div[3]/span/input"));
				WebElement db = driver.findElement(By.xpath(
						"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[7]/span/div[3]/div[3]/div/div[3]/span/input"));
				fn.clear();
				ln.clear();
				db.clear();
				fn.sendKeys(fName.trim());
				ln.sendKeys(lName.trim());
				db.sendKeys(dob.trim());
				
			    buts = driver.findElements(By.tagName("button"));
			    for (WebElement but : buts) {
				if (but.getText().contains("Find patient")) {
					but.click();
					break;
				}
			}
			Thread.sleep(20000);
			found = true;
			boolean multi=false;
			List<WebElement> spans = driver.findElements(By.tagName("span"));
			for (WebElement span : spans) {
				if (span.getText().contains("No patient found with this search")) {
					found = false;
					break;
                    
				}
				if (span.getText().contains("We found multiple results matching your search. Please enter the patient's member ID so we can verify their identity.")) {
					multi = true;
					found = false;
					break;
                    
				}
				/*if (!found)
					break;*/
			}
			if (multi) {
				// 
				 WebElement mem = driver.findElement(By.xpath(
							"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[7]/span/div[3]/div[7]/div/div[3]/span/input"));
				 mem.sendKeys(memberid.trim());	
				 buts = driver.findElements(By.tagName("button"));
				    for (WebElement but : buts) {
					if (but.getText().contains("Find patient")) {
						but.click();
						break;
					}
				 
			        }
				    found = true;
				    List<WebElement>  spans1 = driver.findElements(By.tagName("span"));
					for (WebElement span : spans1) {
						if (span.getText().contains("No patient found with this search")) {
							found = false;
							break;
		                    
						}
						
					}
					
					if (found) {
						//Do  new Code ...
						found = true;
						/*
						List<WebElement>  hrefs = driver.findElements(By.tagName("a"));
						for (WebElement href : hrefs) {
							if (href.getText().contains("Add to 'My patients' list")) {
								found = true;
								
								href.click();
								List<WebElement>  buts1 = driver.findElements(By.tagName("button"));
								for (WebElement but : buts1) {
									if (href.getText().contains("Ok")) {
										found = true;
								}
			                    
							}
							
						}
					   }
						*/
					}
			}
			if (!found) {
				if (memberid.trim().equals(""))
					return false;
				Thread.sleep(3000);
				List<WebElement> ids = driver.findElements(By.tagName("a"));
				for (WebElement id : ids) {
					if (id.getText().contains("Search by Member ID and DOB")) {
						id.click();
						Thread.sleep(1000);
						break;
	                    /*
						buts = driver.findElements(By.tagName("button"));
						for (WebElement but : buts) {
							if (but.getText().contains("Close")) {
								but.click();
								found = false;
								break;
							}
						}
						*/
					}
					/*if (!found)
						break;*/
				}
				
				found = true;
			    db = driver.findElement(By.xpath(
							"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[7]/span/div[3]/div[5]/div/div[3]/span/input"));
			    WebElement mem = driver.findElement(By.xpath(
						"/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div/table/tbody/tr/td[2]/div/div/div[7]/span/div[3]/div[3]/span/input"));
				db.clear();
				db.sendKeys(dob);
			    mem.clear();
			    mem.sendKeys(memberid.trim());
				buts = driver.findElements(By.tagName("button"));
				for (WebElement but : buts) {
					if (but.getText().contains("Find patient")) {
						but.click();
						break;
					}
				}

				Thread.sleep(5000);
				spans = driver.findElements(By.tagName("span"));
				found = true;
				for (WebElement span : spans) {
					if (span.getText()
							.contains("No patient found with this search")) {
						found=false;
						break;
					}
				}

			}
		} // found if end

		
		if (!found)
			return false;
		else {
			String vLink = "";
			String addLink = "";
			found = false;
			List<WebElement> trs = driver.findElements(By.tagName("tr"));
			for (WebElement tr : trs) {
				if (tr.getAttribute("_afrrk") != null) {
					int c = 0;
					//System.out.println("oooooooooooooooo");
					//System.out.println(tr.findElements(By.tagName("table")).get(0).getText());
					if (!tr.findElements(By.tagName("table")).get(0).getText().equalsIgnoreCase(fName+" "+lName)){
						continue;
					}
					//System.out.println("HEHEHHHHE");
					
					List<WebElement> tds = tr.findElements(By.tagName("td"));
					for (WebElement td : tds) {
						if (td.getAttribute("class") != null && td.getAttribute("class").equals("xh7")) {
							c++;
							System.out.println("ccc-->"+td.getText()+"---"+c);
							if (c == 6) {
								try {
									Date d = new Date();
									Date dt = Constants.SIMPLE_DATE_FORMAT.parse(td.getText().split(" to ")[1]);
									if (d.compareTo(dt) <= 0) {
										found = true;
										// break;
									}
								} catch (Exception x) {
									found = true;
									// break;
								}

							}
							//Old one had C==7
							if (c == 8 && found) {
								List<WebElement> aa = td.findElements(By.tagName("a"));
								for (WebElement a : aa) {
									if (a.getText().equals("View eligibility & benefits")) {
										vLink = a.getAttribute("id");
									} else if (a.getText().equals("Add to 'My patients' list")) {
										addLink = a.getAttribute("id");

									}
								}
								break;
							}
						}
					}
				}
				if (found)
					break;
			}

			if (!addLink.equals("")) {
				driver.findElement(By.id(addLink)).click();
				Thread.sleep(3000);
				// if there is any popup then add Logic to close popup

			}
			if (!vLink.equals("")) {
				driver.findElement(By.id(vLink)).click();
				Thread.sleep(3000);
				return true;
			}

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
		// dtemp.setPerioMaintenanceD4910Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 50
		// dtemp.setPerioMaintenanceD4910FL("");//51
		// dtemp.setPerioMaintenanceD4910AltWProphyD0110(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 52
		// dtemp.setFMDD4355Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 53
		// dtemp.setfMDD4355FL("");//54
		// dtemp.setGingivitisD4346Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 55
		// dtemp.setGingivitisD4346FL("");//56
		// dtemp.setNitrousD9230Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 57
		// dtemp.setiVSedationD9243Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 58
		// dtemp.setiVSedationD9248Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 59
		// dtemp.setExtractionsMinorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 60
		// dtemp.setExtractionsMajorPercentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 61
		// dtemp.setCrownLengthD4249Percentage(Constants.SCRAPPING_MANDATORY_WARNING);//
		// 62
		// dtemp.setCrownLengthD4249FL("");//63
		// dtemp.setAlveoD7310CoveredWithEXT("");//64
		// dtemp.setAlveoD7311FL("");//65
		// dtemp.setAlveoD7311CoveredWithEXT("");//66
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
		// dtemp.setNightGuardsD9940Percentage("");// 84
		// dtemp.setD9310Percentage(Constants.SCRAPPING_MANDATORY_WARNING);// 85
		// dtemp.setD9310FL("");//86

		// dtemp.setBuildUpsD2950Covered(Constants.SCRAPPING_MANDATORY_WARNING);// 87
		// dtemp.setBuildUpsD2950FL("");//88
		// 89
		// dtemp.setOrthoPercentage(Constants.SCRAPPING_MANDATORY_WARNING);// 90
		dtemp.setOrthoMax(Constants.SCRAPPING_MANDATORY_WARNING);// 91
		dtemp.setOrthoAgeLimit(Constants.SCRAPPING_MANDATORY_WARNING);// 92 benefit
		// dtemp.setOrthoSubjectDeductible(""); //93
		// dtemp.setFillingsBundling("");//94
		// 95 //96 //97 //99 //100 //101
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
		dtemp.setEmployerName(Constants.SCRAPPING_MANDATORY_WARNING);// 138
		dtemp.setGroup(Constants.SCRAPPING_MANDATORY_WARNING);// 139
		dtemp.setInsAddress(Constants.SCRAPPING_MANDATORY_WARNING);// 140
		dtemp.setPayerId(Constants.SCRAPPING_MANDATORY_WARNING);// 141
		dtemp.setPolicyHolderDOB(Constants.SCRAPPING_MANDATORY_WARNING);// 142
		dtemp.setPlanTermedDate(Constants.SCRAPPING_MANDATORY_WARNING);// 143
		dtemp.setPlanEffectiveDate(Constants.SCRAPPING_MANDATORY_WARNING);// 144
		dtemp.setGeneralDateIVwasDone(Constants.SCRAPPING_MANDATORY_WARNING);// 145
		dtemp.setPolicyHolderDOB(Constants.SCRAPPING_MANDATORY_WARNING);// 146

		// Debug
	}

	private void fetchPatDetails(WebDriver driver, PatientTemp temp) throws InterruptedException {
		Thread.sleep(15000);
		PatientDetailTemp dtemp = temp.getPatientDetails().iterator().next();

		// WebElement divOffBenfitm=
		// driver.findElement(By.xpath("/html/body/div[2]/form/div[1]/div[2]/div/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td[2]/div/div[2]/div/div/div[1]/div[2]/table/tbody/tr/td[2]/div/div/div[1]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/div[1]/div/div/div/table/tbody/tr/td[2]/div"));
		try {
			driver.manage().window().maximize();
			closeFeedBack(driver);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String bluff = "document.getElementsByClassName(\"QSISlider\")[0].remove()";
			js.executeScript(bluff);
			Thread.sleep(15000);
		} catch (Exception sli) {
			sli.printStackTrace();
		}
		List<WebElement> divOfMaximum = driver.findElements(By.tagName("table"));
		// String dd = "";
		float age = 0;
		try {
			int[] agArray = DateUtils.calculateAgeYMD(temp.getDob(), false);
			age = agArray[0] + (agArray[0] / 10);
		} catch (Exception e) {
			// TODO: handle exception
		}

		String ageLimtId = "";
		dtemp.setInsName("Denta Dental of GA");
		dtemp.setInsContact("8005212651");//
		dtemp.setcSRName("Scraping Tool");// 137
		dtemp.setTaxId(taxId);// 136
		dtemp.setsRPD4341QuadsPerDay("2");
		dtemp.setsRPD4341DaysBwTreatment("1");
		dtemp.setcOBStatus("No Information");
		dtemp.setPlanNetwork("IN");
		dtemp.setPlanPreDMandatory("No");
		dtemp.setPlanNonDuplicateClause("No");
		dtemp.setPlanFullTimeStudentStatus("No");
		dtemp.setPlanAssignmentofBenefits("Yes");
		dtemp.setCrownsD2750D2740PaysPrepSeatDate("Seat");
		dtemp.setClaimFillingLimit("12 Months");
		boolean skip = false;

		// check for Plan Selection in some patients "This table contains column headers
		// corresponding to the data body table below"
		for (WebElement divOffMax : divOfMaximum) {
			if (divOffMax != null && divOffMax.getAttribute("summary")
					.equals("This table contains column headers corresponding to the data body table below")) {
				// now select Plan
				// int skip=0;
				List<WebElement> trs = divOffMax.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElements(By.tagName("table")).get(1).findElements(By.tagName("tr"));
				for (WebElement tr : trs) {

					try {
						if (tr.findElements(By.tagName("td")).get(20).getText().trim().equals("")) {
							tr.findElements(By.tagName("td")).get(3).findElement(By.tagName("a")).click();
							Thread.sleep(8000);
							divOfMaximum = driver.findElements(By.tagName("table"));
							skip = true;
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					// skip++;
				}
				if (skip)
					break;

			}

		}

		dtemp.setGeneralDateIVwasDone(Constants.SIMPLE_DATE_FORMAT_IVF.format(new Date()));// 145
		dtemp.setPlanAnnualMax("0");// 1
		dtemp.setPlanAnnualMaxRemaining("0");// 2
		dtemp.setOrthoRemaining("0");// 115
		dtemp.setOrthoMax("0");// 91
		dtemp.setBasicWaitingPeriod("No");// 20
		dtemp.setMajorWaitingPeriod("No");// 21
		dtemp.setOrthoWaitingPeriod("No");// 116
		dtemp.setWaitingPeriod4("No");

		dtemp.setPlanIndividualDeductible("0");// 3
		dtemp.setPlanIndividualDeductibleRemaining("0");// 4
		dtemp.setPreventiveSubDed("No");// 118
		dtemp.setDiagnosticSubDed("No");// 109
		dtemp.setBasicSubjectDeductible("No");// 6
		dtemp.setMajorSubjectDeductible("No");// 8
		dtemp.setEndoSubjectDeductible("No");// 10
		dtemp.setPerioSurgerySubjectDeductible("No");// 12
		dtemp.setOrthoSubjectDeductible("No");// 93
		dtemp.setOrthoRemaining("0");// 115
		dtemp.setOrthoMax("0");// 91
		dtemp.setpAXRaysSubDed("No");
		dtemp.setNightGuardsD9940Percentage("0");
		boolean lifetimeindpresent = false;
		String lifetimeindorthodics = "No";
		String planType = "";
		String planTypeSUP = "";
		boolean restorative = false;// restorative can come twice
		String familytermDate = "";
		String termDate = "";
		String familyEffectiveDate = "";
		String effectiveDate = "";
		Date cDatewithNoTime=null;
		String planEffectiveDateMonthAccum="";
		try {
		cDatewithNoTime=Constants.SIMPLE_DATE_FORMAT.parse(Constants.SIMPLE_DATE_FORMAT.format(new Date()));
		}catch(Exception d) {
			
		}

		for (WebElement divOffMax : divOfMaximum) {

			try {
				if (divOffMax.getAttribute("summary").equals("Benefits and Covered Services")) {

					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Restorative")) {
							try {
								List<WebElement> tds = tr.findElements(By.tagName("td"));// Last two columns fetch..
								// if (tds.size()==0) tds = tr.findElements(By.className("x26f"));
								String dt = tds.get(tds.size() - 2).getText();// tds.get(1).getText();
								String de = tds.get(tds.size() - 1).getText();
								if (dt.equals(""))
									continue;
								if (tr.getText().toLowerCase().contains("amalgam") || !restorative) {
									dtemp.setBasicWaitingPeriod(
											DateUtils.getDiffBetweenMonthsFullDateInParsing(dt, de) + "");// 20
									if (DateUtils.waitingPeroidCheck(cDatewithNoTime,Constants.SIMPLE_DATE_FORMAT.parse(de))) {
										dtemp.setBasicWaitingPeriod("No");
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Prosthodontics; Removable")) {
							try {
								List<WebElement> tds = tr.findElements(By.tagName("td"));
								// if (tds.size()==0) tds = tr.findElements(By.className("x26f"));
								String dt = tds.get(tds.size() - 2).getText();// tds.get(1).getText();
								if (dt.equals(""))
									continue;
								String de = tds.get(tds.size() - 1).getText();// need Logic
								dtemp.setMajorWaitingPeriod(
										DateUtils.getDiffBetweenMonthsFullDateInParsing(dt, de) + "");// 21
								if (DateUtils.waitingPeroidCheck(cDatewithNoTime,Constants.SIMPLE_DATE_FORMAT.parse(de))) {
									dtemp.setMajorWaitingPeriod("No");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Orthodontics")) {
							try {
								List<WebElement> tds = tr.findElements(By.tagName("td"));
								// if (tds.size()==0) tds = tr.findElements(By.className("x26f"));
								String dt = tds.get(tds.size() - 2).getText();// tds.get(1).getText();
								if (dt.equals(""))
									continue;
								String de = tds.get(tds.size() - 1).getText();// need Logic
								dtemp.setOrthoWaitingPeriod(
										DateUtils.getDiffBetweenMonthsFullDateInParsing(dt, de) + "");// 116
								if (DateUtils.waitingPeroidCheck(cDatewithNoTime,Constants.SIMPLE_DATE_FORMAT.parse(de))) {
									dtemp.setOrthoWaitingPeriod("No");
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Preventive")) {
							try {
								List<WebElement> tds = tr.findElements(By.tagName("td"));
								// if (tds.size()==0) tds = tr.findElements(By.className("x26f"));
								String dt = tds.get(tds.size() - 2).getText();// tds.get(1).getText();
								if (dt.equals(""))
									continue;
								String de = tds.get(tds.size() - 1).getText();// need Logic
								dtemp.setWaitingPeriod4(DateUtils.getDiffBetweenMonthsFullDateInParsing(dt, de) + "");// Preventive
																														// waiting
																														// Period
								if (DateUtils.waitingPeroidCheck(cDatewithNoTime,Constants.SIMPLE_DATE_FORMAT.parse(de))) {
									dtemp.setWaitingPeriod4("No");
								}

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
							if (tr.getText().startsWith("Eligibility status:")) {
								List<WebElement> tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26e"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								if (!(tds.get(0).getText().equals("Active"))) {
									temp.setStatus(Constants.PATIENT_NOT_ACTIVE);
									return;
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (tr.getText().startsWith("Program type:")) {
								System.out.println("Program type: found");
								List<WebElement> tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26e"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));

								dtemp.setPlanType(tds.get(0).getText().replace("@", ""));
								planType = tds.get(0).getText().replace("@", "");
								try {
									planTypeSUP = tds.get(0).findElement(By.tagName("sup")).getText();
								} catch (Exception e) {
									// TODO: handle exception
								}
								if (planType.equals(""))
									planType = "Delta Dental";

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Plan name:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));// 138
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26d"));// 142
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setEmployerName(tds.get(0).getText());

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Plan number:")) {

								List<WebElement> tds = tr.findElements(By.className("x26d"));// 139
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setGroup(tds.get(0).getText());

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (tr.getText().startsWith("Enrollee name:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));// 142
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26d"));// 142
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setPolicyHolder(tds.get(0).getText());

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Date of birth:")) {

								List<WebElement> tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setPolicyHolderDOB(tds.get(0).getText());// 146
								String[] s = tds.get(0).getText().split("/");
								try {
									dtemp.setPolicyHolderDOB(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]));// 146

								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Enrollee ID:")) {

								List<WebElement> tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setMemberId(tds.get(0).getText());// PolicyHolderDOB(tds.get(0).getText());//

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Effective date:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setPlanEffectiveDate(tds.get(0).getText());// 144
								String[] s = tds.get(0).getText().split("/");
								try {
									effectiveDate = s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]);
									// dtemp.setPlanEffectiveDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" +
									// s[0])
									// + "-" + (s[1].length() == 2 ? s[1] : "0" + s[1]));// 144

								} catch (Exception e) {
									// TODO: handle exception
									// dtemp.setPlanEffectiveDate("");
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Family member effective date:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setPlanEffectiveDate(tds.get(0).getText());// 144
								String[] s = tds.get(0).getText().split("/");
								try {
									familyEffectiveDate = s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]);
									// dtemp.setPlanEffectiveDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" +
									// s[0])
									// + "-" + (s[1].length() == 2 ? s[1] : "0" + s[1]));// 144

								} catch (Exception e) {
									// TODO: handle exception
									// dtemp.setPlanEffectiveDate("");
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("End date:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setPlanTermedDate(tds.get(0).getText());// 143
								String[] s = tds.get(0).getText().split("/");
								try {
									termDate = s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]);
									// dtemp.setPlanTermedDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0])
									// + "-"
									// + (s[1].length() == 2 ? s[1] : "0" + s[1]));// 143

								} catch (Exception e) {
									// TODO: handle exception
									// dtemp.setPlanTermedDate("");
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (tr.getText().startsWith("Family member end date:")) {

								List<WebElement> tds = tr.findElements(By.className("x26a"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className("x26d"));
								if (tds.size() == 0)
									tds = tr.findElements(By.tagName("td"));
								dtemp.setPlanTermedDate(tds.get(0).getText());// 143
								String[] s = tds.get(0).getText().split("/");
								try {
									familytermDate = s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
											+ (s[1].length() == 2 ? s[1] : "0" + s[1]);
									// dtemp.setPlanTermedDate(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0])
									// + "-"
									// + (s[1].length() == 2 ? s[1] : "0" + s[1]));// 143

								} catch (Exception e) {
									// TODO: handle exception
									// dtemp.setPlanTermedDate("");
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					//
				}

				if (divOffMax.getAttribute("summary").equals("Maximums")) {
					boolean found = false;
					///Logic for classname See first td first class then ..first span then  class="x265" and class="x265"+2 ==> class="x267"
					String className = getClassNameforSummaryTags(divOffMax, "Maximums");
					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Contract Individual Maximum")
								|| tr.getText().startsWith("Calendar Individual Maximum")
								|| tr.getText().startsWith("Carryover Individual Maximum")) {
							try {
								if (tr.getText().contains(dtemp.getPlanType())) {
									found = true;
									if (!(dtemp.getPlanAnnualMax().equals("0")
											|| (tr.getText().toLowerCase().contains("restorative")
													|| tr.getText().toLowerCase().contains("preventive")))) {
										continue;
									}
									List<WebElement> tds = tr.findElements(By.className("x264"));
									// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));

									dtemp.setPlanAnnualMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 1
									dtemp.setPlanAnnualMaxRemaining(
											tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 2

								} else if (dtemp.getPlanAnnualMax().equals("0")) {
									List<WebElement> tds = tr.findElements(By.className("x264"));
									// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));

									dtemp.setPlanAnnualMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 1
									dtemp.setPlanAnnualMaxRemaining(
											tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 2

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Lifetime Individual Maximum")) {
							if (tr.getText().contains(dtemp.getPlanType())) {
								found = true;
							}
						}

						if (tr.getText().startsWith("Lifetime Individual Maximum")
								&& tr.getText().contains("Orthodontics")) {
							try {
								if (tr.getText().contains(dtemp.getPlanType())) {
									List<WebElement> tds = tr.findElements(By.className("x264"));
									// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));

									dtemp.setOrthoRemaining(
											tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 115
								} else if (dtemp.getOrthoRemaining().equals("")) {
									List<WebElement> tds = tr.findElements(By.className("x264"));
									// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));

									dtemp.setOrthoRemaining(
											tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 115
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						if (tr.getText().startsWith("Lifetime Individual Maximum")
								&& tr.getText().contains("Orthodontics")) {
							try {
								if (tr.getText().contains(dtemp.getPlanType())) {
									List<WebElement> tds = tr.findElements(By.className("x264"));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));
									dtemp.setOrthoMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 91
								} else if (dtemp.getOrthoMax().equals("")) {
									List<WebElement> tds = tr.findElements(By.className("x264"));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));
									dtemp.setOrthoMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 91
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} // for LOOP
					if (!found) {
						planType = "Denta Dental";
						if (dtemp.getPlanType().contains("PPO"))
							planType = "Delta Dental PPO";
						for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

							if (tr.getText().startsWith("Contract Individual Maximum")
									|| tr.getText().startsWith("Calendar Individual Maximum")
									|| tr.getText().startsWith("Carryover Individual Maximum")) {
								try {
									if (tr.getText().contains(planType)) {
										if (!(dtemp.getPlanAnnualMax().equals("0")
												|| (tr.getText().toLowerCase().contains("restorative")
														|| tr.getText().toLowerCase().contains("preventive")))) {
											continue;
										}
										List<WebElement> tds = tr.findElements(By.className("x264"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										dtemp.setPlanAnnualMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 1
										dtemp.setPlanAnnualMaxRemaining(
												tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 2
									} else if (dtemp.getPlanAnnualMax().equals("0")) {
										List<WebElement> tds = tr.findElements(By.className("x264"));
										// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));

										dtemp.setPlanAnnualMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 1
										dtemp.setPlanAnnualMaxRemaining(
												tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 2

									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							if (tr.getText().startsWith("Lifetime Individual Maximum")) {
								if (tr.getText().contains(planType)) {
								}
							}

							if (tr.getText().startsWith("Lifetime Individual Maximum")
									&& tr.getText().contains("Orthodontics")) {
								try {
									if (tr.getText().contains(planType)) {
										List<WebElement> tds = tr.findElements(By.className("x264"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										dtemp.setOrthoRemaining(tds.get(1).getText().replace("$", "").replace(",", ""));// 115
									} else if (dtemp.getOrthoRemaining().equals("")) {
										List<WebElement> tds = tr.findElements(By.className("x264"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										dtemp.setOrthoRemaining(
												tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 115
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							if (tr.getText().startsWith("Lifetime Individual Maximum")
									&& tr.getText().contains("Orthodontics")) {
								try {
									if (tr.getText().contains(planType)) {
										List<WebElement> tds = tr.findElements(By.className("x264"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										dtemp.setOrthoMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 91
									} else if (dtemp.getOrthoMax().equals("")) {
										List<WebElement> tds = tr.findElements(By.className("x264"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										dtemp.setOrthoMax(tds.get(0).getText().replace("$", "").replace(",", ""));// 91
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						} // for LOOP }
							//
					} // maximums
				}
				if (divOffMax.getAttribute("summary").equals("Deductibles")) {
					boolean found = false;
					String className = getClassNameforSummaryTags(divOffMax, "Deductibles");
					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Contract Individual Deductible")
								|| tr.getText().startsWith("Calendar Individual Deductible")
								|| tr.getText().startsWith("Carryover Individual Deductible")) {
							try {
								if (tr.getText().contains(dtemp.getPlanType())) {
									found = true;
									List<WebElement> tdsLa=	tr.findElements(By.tagName("td"));
									String cl = tdsLa.get(tdsLa.size()-1).findElement(By.tagName("span")).getAttribute("class");
									System.out.println("CCCCCCCCCC--"+cl);
								//String cl= tr.findElements(By.tagName("td")).get(0).findElements(By.tagName("span")).get(0).getAttribute("class");
								//cl="x265";
									List<WebElement> tds = tr.findElements(By.className(cl));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));
									dtemp.setPlanIndividualDeductible(
											tds.get(0).getText().replace("$", "").replace(",", ""));// 3
									dtemp.setPlanIndividualDeductibleRemaining(
											tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 4
								}/* else if (dtemp.getPlanIndividualDeductible().equals("0")) {
									List<WebElement> tds = tr.findElements(By.className("x264"));
									if (tds.size() == 0)
										tds = tr.findElements(By.className(className));
									dtemp.setPlanIndividualDeductible(
											tds.get(0).getText().replace("$", "").replace(",", ""));// 3
									dtemp.setPlanIndividualDeductibleRemaining(
											tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 4
								}*/
								if (found) {
								String wholeText = tr.getText();
								String[] wholeTextArray = wholeText.split("Accumulation period for this");
								try {
									String [] t=wholeText.split("Accumulation period for this program");
									planEffectiveDateMonthAccum=t[1].split("/")[0].split("\\(")[1];
									}catch(Exception dcheck) {
										
								}
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

								if (tr.getText().contains("Orthodontics")) {
									lifetimeindorthodics = "Yes";
								} else {
									lifetimeindorthodics = "No";
								}
								
								if (tr.getText().contains("Diagnostic") && tr.getText().contains("Preventive")) {
									dtemp.setpAXRaysSubDed("Yes");
								}

								if (wholeTextArray.length >= 3) {
									try {
										String [] t=wholeText.split("Accumulation period for this program");
										planEffectiveDateMonthAccum=t[1].split("/")[0].split("\\(")[1];
										}catch(Exception dcheck) {
											
										}
									// Case where We have multiple value of Accumulation period for this example
									// Patient id =12937 Jasper
									try {
										String classNameFor= getClassNameforDeductiblesTags(tr);
										List<WebElement> tds = tr.findElements(By.className(classNameFor));
										System.out.println("classNameFor-"+classNameFor);
										// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										String id = tds.get(0).findElement(By.xpath("..")).getAttribute("id");
										System.out.println("((((("+id);
										id = id.split("column")[0] + "column"
												+ (Integer.parseInt(id.split("column")[1].split("x")[0]) + 3)
												+ "xx";
										System.out.println("((((("+id);
										WebElement tdsPlanName = tr.findElement(By.id(id));
										 System.out.println(tdsPlanName.getText()+"---))))))))");
										int ct = -1;
										List<WebElement> tdsPnames = tdsPlanName.findElements(By.className("x263"));
										if (tdsPnames.size() == 0)
											tdsPnames = tdsPlanName.findElements(By.className("x265"));
										if (tdsPnames.size() == 0)
											tdsPnames = tdsPlanName.findElements(By.className(className));
										for (WebElement tdsPname : tdsPnames) {
											ct = ct + 1;
											if (tdsPname.getText().contains(planType)) {
												break;
											}
										}

										String textT = tr.findElements(By.tagName("td")).get(1).getText()
												.split("Accumulation period for this program")[ct + 1];
										if (textT.contains("Preventive")) {
											dtemp.setPreventiveSubDed("Yes");// 118
										} else {
											dtemp.setPreventiveSubDed("No");// 118
										}
										if (textT.contains("Diagnostic")) {
											dtemp.setDiagnosticSubDed("Yes");// 109
										} else {
											dtemp.setDiagnosticSubDed("No");// 109
										}
										if (textT.contains("Restorative")) {
											dtemp.setBasicSubjectDeductible("Yes");// 6
										} else {
											dtemp.setBasicSubjectDeductible("No");// 6
										}
										if (textT.contains("Prosthodontics")) {
											dtemp.setMajorSubjectDeductible("Yes");// 8
										} else {
											dtemp.setMajorSubjectDeductible("No");// 8
										}
										if (textT.contains("Endodontics")) {
											dtemp.setEndoSubjectDeductible("Yes");// 10
										} else {
											dtemp.setEndoSubjectDeductible("No");// 10
										}
										if (tr.getText().contains("Endodontics")) {
											dtemp.setPerioSurgerySubjectDeductible("Yes");// 12
										} else {
											dtemp.setPerioSurgerySubjectDeductible("No");// 12
										}

										if (tr.getText().contains("Orthodontics")) {
											lifetimeindorthodics = "Yes";
										} else {
											lifetimeindorthodics = "No";
										}
										if (tr.getText().contains("Diagnostic") && tr.getText().contains("Preventive")) {
											dtemp.setpAXRaysSubDed("Yes");
										}

									} catch (Exception e) {

									}
								}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						if (tr.getText().startsWith("Lifetime Individual Deductible")) {
							lifetimeindpresent = true;
							try {
								// List<WebElement> tds = tr.findElements(By.className("x264"));
								// dtemp.setOrthoSubjectDeductible(tds.get(0).getText().replace("$",
								// "").replace(",", ""));// 93
								if (tr.getText().contains("Orthodontics")) {
									dtemp.setOrthoSubjectDeductible("Yes");// 93
								} else {
									dtemp.setOrthoSubjectDeductible("No");// 93
								}
							} catch (Exception e) {
								// TODO: handle exception
							}

						}

					} // for loop
					if (!found) {
						boolean found2=false; 
						for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

							if (tr.getText().startsWith("Contract Individual Deductible")
									|| tr.getText().startsWith("Calendar Individual Deductible")
									|| tr.getText().startsWith("Carryover Individual Deductible")) {
								try {
									// System.out.println("dddddd"+tr.getText());
									// System.out.println(tr.getText().contains(planType));
									if (tr.getText().contains(planType)) {
										found2 = true;
										List<WebElement> tdsLa=	tr.findElements(By.tagName("td"));
										String cl = tdsLa.get(tdsLa.size()-1).findElement(By.tagName("span")).getAttribute("class");
									System.out.println("CCCCCC->"+cl);
										//String cl= tr.findElements(By.tagName("td")).get(0).findElements(By.tagName("span")).get(0).getAttribute("class");
									//cl="x265";
										List<WebElement> tds = tr.findElements(By.className(cl));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										dtemp.setPlanIndividualDeductible(
												tds.get(0).getText().replace("$", "").replace(",", ""));// 3
										dtemp.setPlanIndividualDeductibleRemaining(
												tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 4
									}/* else if (dtemp.getPlanIndividualDeductible().equals("0")) {
										List<WebElement> tds = tr.findElements(By.className("x264"));
										// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
										if (tds.size() == 0)
											tds = tr.findElements(By.className(className));
										dtemp.setPlanIndividualDeductible(
												tds.get(0).getText().replace("$", "").replace(",", ""));// 3
										dtemp.setPlanIndividualDeductibleRemaining(
												tds.get(1).getAttribute("innerText").replace("$", "").replace(",", ""));// 4
									}*/

									String wholeText = tr.getText();
									String[] wholeTextArray = wholeText.split("Accumulation period for this");
									try {
										String [] t=wholeText.split("Accumulation period for this program");
										
									 planEffectiveDateMonthAccum=t[1].split("/")[0].split("\\(")[1];
									}catch(Exception dcheck) {
										dcheck.printStackTrace();
									}
									if (found2) {
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

									if (tr.getText().contains("Orthodontics")) {
										lifetimeindorthodics = "Yes";
									} else {
										lifetimeindorthodics = "No";
									}
									if (tr.getText().contains("Diagnostic") && tr.getText().contains("Preventive")) {
										dtemp.setpAXRaysSubDed("Yes");
									}
									if (wholeTextArray.length >= 3) {
										try {
											String [] t=wholeText.split("Accumulation period for this program");
											planEffectiveDateMonthAccum=t[1].split("/")[0].split("\\(")[1];
											}catch(Exception dcheck) {
												
											}
										// Case where We have multiple value of Accumulation period for this example
										// Patient id =12937 Jasper
										try {
											String classNameFor= getClassNameforDeductiblesTags(tr);
											List<WebElement> tds = tr.findElements(By.className(classNameFor));
											System.out.println("classNameFor-"+classNameFor);
											// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
											if (tds.size() == 0)
												tds = tr.findElements(By.className(className));
											String id = tds.get(0).findElement(By.xpath("..")).getAttribute("id");
											System.out.println("((((("+id);
											id = id.split("column")[0] + "column"
													+ (Integer.parseInt(id.split("column")[1].split("x")[0]) + 3)
													+ "xx";
											System.out.println("((((("+id);
											WebElement tdsPlanName = tr.findElement(By.id(id));
											 System.out.println(tdsPlanName.getText()+"---))))))))");
											int ct = -1;
											List<WebElement> tdsPnames = tdsPlanName.findElements(By.className("x263"));
											if (tdsPnames.size() == 0)
												tdsPnames = tdsPlanName.findElements(By.className("x265"));
											if (tdsPnames.size() == 0)
												tdsPnames = tdsPlanName.findElements(By.className(className));
											for (WebElement tdsPname : tdsPnames) {
												ct = ct + 1;
												if (tdsPname.getText().contains(planType)) {
													break;
												}
											}

											String textT = tr.findElements(By.tagName("td")).get(1).getText()
													.split("Accumulation period for this program")[ct + 1];
											if (textT.contains("Preventive")) {
												dtemp.setPreventiveSubDed("Yes");// 118
											} else {
												dtemp.setPreventiveSubDed("No");// 118
											}
											if (textT.contains("Diagnostic")) {
												dtemp.setDiagnosticSubDed("Yes");// 109
											} else {
												dtemp.setDiagnosticSubDed("No");// 109
											}
											if (textT.contains("Restorative")) {
												dtemp.setBasicSubjectDeductible("Yes");// 6
											} else {
												dtemp.setBasicSubjectDeductible("No");// 6
											}
											if (textT.contains("Prosthodontics")) {
												dtemp.setMajorSubjectDeductible("Yes");// 8
											} else {
												dtemp.setMajorSubjectDeductible("No");// 8
											}
											if (textT.contains("Endodontics")) {
												dtemp.setEndoSubjectDeductible("Yes");// 10
											} else {
												dtemp.setEndoSubjectDeductible("No");// 10
											}
											if (textT.contains("Endodontics")) {
												dtemp.setPerioSurgerySubjectDeductible("Yes");// 12
											} else {
												dtemp.setPerioSurgerySubjectDeductible("No");// 12
											}

											if (tr.getText().contains("Orthodontics")) {
												lifetimeindorthodics = "Yes";
											} else {
												lifetimeindorthodics = "No";
											}
											if (tr.getText().contains("Diagnostic") && tr.getText().contains("Preventive")) {
												dtemp.setpAXRaysSubDed("Yes");
											}
										} catch (Exception e) {
                                            e.printStackTrace();
                                            
										}
									}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}

						} // for loop
					}
					//
				}
				if (divOffMax.getAttribute("summary").equals("Other Provisions")) {
					String className = getClassNameforSummaryTags(divOffMax, "Other Provisions");
					for (WebElement tr : divOffMax.findElements(By.tagName("tr"))) {

						if (tr.getText().startsWith("Missing Tooth Coverage")) {
							try {
								List<WebElement> tds = tr.findElements(By.className("x264"));
								// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className(className));
								dtemp.setMissingToothClause(tds.get(0).getText().replace("$", "").replace(",", ""));// 16
							    if (dtemp.getMissingToothClause().contains("not a benefit of the program")) {
							    	dtemp.setMissingToothClause("No");
							    }
							    if (dtemp.getMissingToothClause().length()>50) {
							    	dtemp.setMissingToothClause(dtemp.getMissingToothClause().substring(0,48));
							    }
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
								// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className(className));
								dtemp.setPlanDependentsCoveredtoAge(
										tds.get(0).getText().replace("(Division has no rule)", "").trim());// 127
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (tr.getText().startsWith("Assignment of Benefits")) {
							try {
								List<WebElement> tds = tr.findElements(By.className("x264"));
								// if (tds.size()==0) tds = tr.findElements(By.className("x265"));
								if (tds.size() == 0)
									tds = tr.findElements(By.className(className));

								if (tds.get(0) != null && tds.get(0).getText() != null && tds.get(0).getText()
										.toLowerCase().contains("group accepts assignment of benefits"))
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

		if (familytermDate.equals(""))
			dtemp.setPlanTermedDate(termDate);
		else
			dtemp.setPlanTermedDate(familytermDate);

		if (familyEffectiveDate.equals(""))
			dtemp.setPlanEffectiveDate(effectiveDate);
		else
			dtemp.setPlanEffectiveDate(familyEffectiveDate);

		if (!lifetimeindpresent) {
			dtemp.setOrthoSubjectDeductible(lifetimeindorthodics);// 93
		}
        String  planEffectiveDateMonth="";
        //System.out.println("**********************");
        //System.out.println(dtemp.getPlanEffectiveDate());
        
        
		if (!dtemp.getPlanEffectiveDate().equals(Constants.SCRAPPING_MANDATORY_WARNING)) {
			String[] ped= dtemp.getPlanEffectiveDate().split("-");
			if (ped.length>1) {
				planEffectiveDateMonth=ped[1];
			}
		}
	     //System.out.println("MON-"+planEffectiveDateMonth);
	     //System.out.println("CUM-"+planEffectiveDateMonthAccum);
	     
	      	
		//check if Frequency PY of FY
		String frequenCYFYPY="FY";
		try {
			  if (planEffectiveDateMonthAccum.equals("1") || planEffectiveDateMonthAccum.equals("01")) { 
				  frequenCYFYPY="CY";
			  }else if (Integer.parseInt(planEffectiveDateMonthAccum)==Integer.parseInt(planEffectiveDateMonth)) {
		    	  frequenCYFYPY="PY";
			}
		}catch(Exception dcheck) {
			
		}
	     System.out.println("frequencyCY="+frequenCYFYPY);
		
		System.out.println("getPlanAnnualMax()");
		System.out.println(dtemp.getPlanAnnualMax());
		System.out.println(dtemp.getPlanAnnualMaxRemaining());
		System.out.println(dtemp.getBasicSubjectDeductible());
		System.out.println("Diag--"+dtemp.getDiagnosticSubDed());
		System.out.println(dtemp.getPlanIndividualDeductible());
		System.out.println("-->"+dtemp.getPlanIndividualDeductibleRemaining());
		
		List<WebElement> spans = driver.findElements(By.className("x24l"));
		// if (spans.size()==0) spans = driver.findElements(By.className("x24l"));
		String claim = "";
		for (WebElement span : spans) {
			claim = claim + " " + span.getText().trim();
		}
		if (!claim.equals("")) {
			dtemp.setInsAddress(claim);// 140
			try {
				dtemp.setPayerId(claim.split("Claim payer ID :")[1]);// 141
				dtemp.setInsAddress(claim.split("Claim payer ID :")[0]);// 141
			} catch (Exception p) {

			}
		}
		dtemp.setOrthoAgeLimit("0");
		if (!ageLimtId.equals("")) {
			try {
				boolean clickMePresent = false;
				List<WebElement> aas = driver.findElement(By.id(ageLimtId)).findElements(By.tagName("a"));
				for (WebElement aa : aas) {
					if (aa.getText() != null && aa.getText().equals("Click here")) {
						clickMePresent = true;
						aa.click();
						Thread.sleep(15000);
						List<WebElement> tabs = driver.findElements(By.tagName("table"));
						try {
							for (WebElement tab : tabs) {
								if (tab.getAttribute("summary") != null
										&& tab.getAttribute("summary").equals("Ortho Age informat")) {
									dtemp.setOrthoAgeLimit(tab.findElements(By.tagName("td")).get(1).getText());// 92
									if (dtemp.getOrthoAgeLimit() != null
											&& dtemp.getOrthoAgeLimit().toLowerCase().contains("no age limit")) {
										dtemp.setOrthoAgeLimit("99");
									}
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
					if (!clickMePresent) {
						// TO DO IT CAPLINE SAYS
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		// String planType = dtemp.getPlanType();
		temp.setPlanTypeinSite(false);
		openSideBarFirst(driver, "Benefit details", true,
				new String[] { "Endodontics", "Periodontics", "Preventive", "Diagnostic", "Prosthodontics; Removable" },
				true, temp);
		String majorMaxMin1=fetchBenefitDetails(MIN_MAX_VAL, temp, driver, "", "Prosthodontics; Removable", "", planType, false, true);
		/*String majorMax=fetchBenefitDetails(MAX_VAL, temp, driver, "", "Prosthodontics; Removable", "", planType, true, true);
		String majorCom="";
		if (!(majorMin.equals(majorMax))) {
			majorCom="Major is between "+majorMin+"-"+majorMax+"% ";
		}*/
		dtemp.setMajorPercentage(majorMaxMin1.split(MIN_MAX_VAL_SPLIT)[0]);// 7
		
		if (!temp.isPlanTypeinSite()) {
			if (planType.contains("PPO"))
				planType = "Delta Dental PPO";
			else
				planType = "Delta Dental";
			majorMaxMin1=fetchBenefitDetails(MIN_MAX_VAL, temp, driver, "", "Prosthodontics; Removable", "", planType, false, true);
			/*majorMax=fetchBenefitDetails(MAX_VAL, temp, driver, "", "Prosthodontics; Removable", "", planType, true, true);
			if (!(majorMin.equals(majorMax))) {
				majorCom="Major is between "+majorMin+"-"+majorMax+"% ";
			}*/
			
			dtemp.setMajorPercentage(majorMaxMin1.split(MIN_MAX_VAL_SPLIT)[0]);// 7
		}
        try {
			
        	int a=Integer.parseInt(dtemp.getMajorPercentage());
        	if (a>0)dtemp.setReplacementClause("Yes");
        	else dtemp.setReplacementClause("No");
        	
		}catch(Exception e) {
			dtemp.setReplacementClause("No");
		}

		String majorMaxMin2=fetchBenefitDetails(MIN_MAX_VAL, temp, driver, "", "Endodontics", "", planType, false, true);
		dtemp.setEndodonticsPercentage(majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0]);//9

		// 10
		String majorMaxMin3=fetchBenefitDetails(MIN_MAX_VAL, temp, driver, "", "Periodontics", "Surgical Services", planType, false, true);
		dtemp.setPerioSurgeryPercentage(majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[0]);// 11

		// 12
		String majorMaxMin4=fetchBenefitDetails(MIN_MAX_VAL, temp, driver, "", "Preventive", "", planType, false, true);
		dtemp.setPreventivePercentage(majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[0]);// 13
		
		String majorMaxMin5=fetchBenefitDetails(MIN_MAX_VAL, temp, driver, "", "Diagnostic", "", planType, false, true);
		dtemp.setDiagnosticPercentage(majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[0]);// 14

		dtemp.setpAXRaysPercentage(fetchBenefitDetails("D0220", temp, driver, "", "Diagnostic",
				"Radiographs/Diagnostic Imaging", planType, true, true));// 15

	       try {
				
	        	int a=Integer.parseInt(dtemp.getpAXRaysPercentage());
	        	if (a>0)dtemp.setxRaysBundling("Yes");
	        	else dtemp.setxRaysBundling("No");
	        	
			}catch(Exception e) {
				dtemp.setxRaysBundling("No");
			}

	       System.out.println("Benefit search ....");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,400)");
		// js.executeScript("window.scrollBy(0,-500)");

		openSideBarFirst(driver, "Benefit search", false, null, false, temp);
		HashMap<String, DentaBenefitScrapDto> benefitInfoMap = new HashMap<>();
		DentaBenefitScrapDto dt = null;
		HashMap<String, String> values = new HashMap<>();

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 50, 51, 52
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4910", dt);// a

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 55 56
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4346", dt);// b

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });
		dt.setMandatory(new boolean[] { true, true });// 5 78 79
		dt.setAge(age);
		benefitInfoMap.put("D2391", dt);// c
		//////////////////////////////////////

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 81 82
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D2740", dt);// d

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 19 121
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D9944", dt);// e --------add benefitContract if needed for D9944

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract,benefitLimitation });// 22
		dt.setMandatory(new boolean[] { false,false });
		dt.setAge(age);
		benefitInfoMap.put("D2930", dt);// f

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract ,benefitLimitation });// 23
		dt.setMandatory(new boolean[] { false,false });
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

		dtemp.setComments("");
		String[] info = fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, false, null, true,frequenCYFYPY);// call
																													// for
		// first 10
		dtemp.setComments(info[4] + "\n" + info[5]);
		System.out.println("setCommentssetComments+" + dtemp.getComments());
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
		dt.setTypes(new String[] { benefitContract, benefitAgeLimit, benefitLimitation });// 37 38 39 40 41 42 43
		dt.setMandatory(new boolean[] { false, false, false });
		dt.setAge(age);
		benefitInfoMap.put("D1351", dt);// h

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 44
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D1110", dt);// i

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });// 45
		dt.setMandatory(new boolean[] { false });
		dt.setAge(age);
		benefitInfoMap.put("D1120", dt);// j

		String[] info2 = fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info, false,frequenCYFYPY);// call
																													// for
																													// second
																													// 10
		if (!info2[5].equals(""))
			dtemp.setComments(dtemp.getComments() + "\n" + info2[5]);
		benefitInfoMap = new HashMap<>();
		// 46
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 47 48
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4341", dt);// a

		// 49 50 (51 and 52 are downward)
		// 53
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 53 54
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4355", dt);// b

		// 55 56 upwards

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

		info2 = fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info, false,frequenCYFYPY);// call for
																											// third 10
		if (!info2[5].equals(""))
			dtemp.setComments(dtemp.getComments() + "\n" + info2[5]);
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
		benefitInfoMap.put("D5820", dt);// d

		// 72
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
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 114 122
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D9945", dt);// j

		info2 = fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info, false,frequenCYFYPY);// call for
																											// fourth 10
		if (!info2[5].equals(""))
			dtemp.setComments(dtemp.getComments() + "\n" + info2[5]);
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
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });// 90
		dt.setMandatory(new boolean[] { false });
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
		dt.setTypes(new String[] { benefitContract, benefitLimitation });// 51 52 53
		dt.setMandatory(new boolean[] { false, false });
		dt.setAge(age);
		benefitInfoMap.put("D4910", dt);// g

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { true, true });// For D2391 > D2140 //( 5 78 79)
		benefitInfoMap.put("D2140", dt);// h

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract,benefitLimitation });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { false,false });
		benefitInfoMap.put("D0330", dt);

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract,benefitLimitation });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { false,false });//
		benefitInfoMap.put("D2934", dt);// this is 10 th

		// 117
		// 119
		// 123 124 125 126
		// 128 129 130
		// 132 133 134 135

		info2 = fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info, false,frequenCYFYPY);// call for
																											// Fifth 10
		if (!info2[5].equals(""))
			dtemp.setComments(dtemp.getComments() + "\n" + info2[5]);
		
		benefitInfoMap = new HashMap<>();
		
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract,benefitLimitation });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { false,false });
		benefitInfoMap.put("D0160", dt);

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { false,false });//
		benefitInfoMap.put("D4381", dt);// this is 1

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract, benefitLimitation });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { false,false });//
		benefitInfoMap.put("D3330", dt);// this is 2
		
		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitContract });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { false });//
		benefitInfoMap.put("D0350", dt);// this is 3

		dt = new DentaBenefitScrapDto();
		dt.setTypes(new String[] { benefitLimitation });
		dt.setAge(age);
		dt.setMandatory(new boolean[] { false });//
		benefitInfoMap.put("D1330", dt);// this is 4
		
		info2 = fetchBenefitSearchProcedure(benefitInfoMap, values, driver, planType, true, info, false,frequenCYFYPY);// call for
																											// sixth 2
		if (!info2[5].equals(""))
			dtemp.setComments(dtemp.getComments() + "\n" + info2[5]);
		/*if (!majorCom.equals(""))
			dtemp.setComments(dtemp.getComments() + "\n" + majorCom);*/
		benefitInfoMap = new HashMap<>();
		// for patient Adrian Flores -->Sinton 1833 this is not mandatory
		dtemp.setBasicPercentage(getBenefitProcedureValueFromMap("D2391", benefitContract, values, false));// 5
		boolean checkforD2391D2140 = true;
		//System.out.println("PPPPP--"+dtemp.getBasicPercentage()+"--");
		if (dtemp.getBasicPercentage().contains(Constants.SCRAPPING_NOT_FOUND)
				|| dtemp.getBasicPercentage().contains(Constants.SCRAPPING_MANDATORY_WARNING)
				|| dtemp.getBasicPercentage().trim().equals("") || dtemp.getBasicPercentage().trim().equals("0")) {
			dtemp.setBasicPercentage(getBenefitProcedureValueFromMap("D2140", benefitContract, values, true)); // 5
			checkforD2391D2140 = false;
			//System.out.println("PPPPP--HERE"+dtemp.getBasicPercentage()+"--");
		}

		// 8
		dtemp.setNightGuardsD9944Fr(getBenefitProcedureValueFromMap("D9944", benefitLimitation, values, false));// 19

		dtemp.setsSCD2930FL(getBenefitProcedureValueFromMap("D2930", benefitLimitation, values, false));// 22
		
		
		dtemp.setsSCD2931FL(getBenefitProcedureValueFromMap("D2931", benefitLimitation, values, false));// 23

		dtemp.setExamsD0120FL(getBenefitProcedureValueFromMap("D0120", benefitLimitation, values, false));// 24

		dtemp.setExamsD0140FL(getBenefitProcedureValueFromMap("D0140", benefitLimitation, values, false));// 25

		dtemp.seteExamsD0145FL(getBenefitProcedureValueFromMap("D0145", benefitLimitation, values, false));// 26

		dtemp.setExamsD0150FL(getBenefitProcedureValueFromMap("D0150", benefitLimitation, values, false));// 27

		dtemp.setxRaysBWSFL(getBenefitProcedureValueFromMap("D0272", benefitLimitation, values, false));// 28

		String D0220F = getBenefitProcedureValueFromMap("D0220", benefitLimitation, values, false);// 29
		// System.out.println("**(*(*(*(*(*(*(*(**((*");
		// System.out.println("**(*(*(*(*(*(*(*(**((*"+D0220F);

		if (D0220F.equalsIgnoreCase("Pre-D") || D0220F.equalsIgnoreCase("benefit is limited")) {
			dtemp.setxRaysPAD0220FL("No Frequency");// 29
		} else {
			dtemp.setxRaysPAD0220FL(D0220F);// 29
		}

		String D0230F = getBenefitProcedureValueFromMap("D0230", benefitLimitation, values, false);// 30
		// System.out.println("**(*(*(*(*(*(*(*(**((*"+D0230F+"----");
		if (D0230F.equalsIgnoreCase("Pre-D") || D0230F.equalsIgnoreCase("benefit is limited")) {
			dtemp.setxRaysPAD0230FL("No Frequency");// 30
		} else {
			dtemp.setxRaysPAD0230FL(D0230F);// 30
		}
		// dtemp.setxRaysPAD0230FL(getBenefitProcedureValueFromMap("D0230",
		// benefitLimitation, values, false));// 30

		dtemp.setxRaysFMXFL(getBenefitProcedureValueFromMap("D0210", benefitLimitation, values, false));// 31
		// 32

		dtemp.setFlourideD1208FL(getBenefitProcedureValueFromMap("D1208", benefitLimitation, values, false));// 33

		dtemp.setFlourideAgeLimit(getBenefitProcedureValueFromMap("D1208", benefitAgeLimit, values, false));// 34

		dtemp.setVarnishD1206FL(getBenefitProcedureValueFromMap("D1206", benefitLimitation, values, false));// 35

		dtemp.setVarnishD1206AgeLimit(getBenefitProcedureValueFromMap("D1206", benefitAgeLimit, values, false));// 36

		dtemp.setSealantsD1351Percentage(getBenefitProcedureValueFromMap("D1351", benefitContract, values, false));// 37

		dtemp.setSealantsD1351FL(getBenefitProcedureValueFromMap("D1351", benefitLimitation, values, false));// 38

		dtemp.setSealantsD1351AgeLimit(getBenefitProcedureValueFromMap("D1351", benefitAgeLimit, values, false));// 39

		String lim = getBenefitProcedureValueFromMap("D1351", benefitLimitation + "_TOOTH", values, false);
		System.out.println("LIMITS--" + lim);
		if (lim != null)
			lim = lim.replace(" ", "");
		dtemp.setSealantsD1351PrimaryMolarsCovered(FreqencyUtils.checkForteehIntext(siteName, lim, "A,B,I,J,K,L,S,T"));// 40
		dtemp.setSealantsD1351PreMolarsCovered(
				FreqencyUtils.checkForteehIntext(siteName, lim, "4,5,12,13,20,21,28,29"));// 41
		dtemp.setSealantsD1351PermanentMolarsCovered(
				FreqencyUtils.checkForteehIntext(siteName, lim, "1,2,3,14,15,16,17,18,19,30,31,32"));// 42

		dtemp.setProphyD1110FL(getBenefitProcedureValueFromMap("D1110", benefitLimitation, values, false));// 43

		dtemp.setProphyD1120FL(getBenefitProcedureValueFromMap("D1120", benefitLimitation, values, false));// 44
		// 45
		dtemp.setsRPD4341Percentage(getBenefitProcedureValueFromMap("D4341", benefitContract, values, false));// 46

		dtemp.setsRPD4341FL(getBenefitProcedureValueFromMap("D4341", benefitLimitation, values, false));// 47

		// 48 49
		dtemp.setPerioMaintenanceD4910Percentage(
				getBenefitProcedureValueFromMap("D4910", benefitContract, values, false));// 50

		dtemp.setPerioMaintenanceD4910FL(getBenefitProcedureValueFromMap("D4910", benefitLimitation, values, false));// 51

		String cd = getBenefitProcedureValueFromMap("D4910", benefitLimitation + "_FULL", values, false);
		if (cd.contains("D1110") || cd.contains("D1120"))
			cd = "Yes";
		else
			cd = "No";

		dtemp.setPerioMaintenanceD4910AltWProphyD0110(cd);// 52

		dtemp.setFMDD4355Percentage(getBenefitProcedureValueFromMap("D4355", benefitContract, values, false));// 53

		dtemp.setfMDD4355FL(getBenefitProcedureValueFromMap("D4355", benefitLimitation, values, false));// 54

		dtemp.setGingivitisD4346Percentage(getBenefitProcedureValueFromMap("D4346", benefitContract, values, false));// 55

		dtemp.setGingivitisD4346FL(getBenefitProcedureValueFromMap("D4346", benefitLimitation, values, false));// 56

		dtemp.setNitrousD9230Percentage(getBenefitProcedureValueFromMap("D9230", benefitContract, values, false));// 57

		dtemp.setiVSedationD9243Percentage(getBenefitProcedureValueFromMap("D9243", benefitContract, values, false));// 58

		dtemp.setiVSedationD9248Percentage(getBenefitProcedureValueFromMap("D9248", benefitContract, values, false));// 59

		dtemp.setExtractionsMinorPercentage(getBenefitProcedureValueFromMap("D7210", benefitContract, values, false));// 60

		dtemp.setExtractionsMajorPercentage(getBenefitProcedureValueFromMap("D7240", benefitContract, values, false));// 61

		dtemp.setCrownLengthD4249Percentage(getBenefitProcedureValueFromMap("D4249", benefitContract, values, false));// 62

		dtemp.setCrownLengthD4249FL(getBenefitProcedureValueFromMap("D4249", benefitLimitation, values, false));// 63

		lim = getBenefitProcedureValueFromMap("D7311", benefitAlveoloplasty, values, false);
		// if (lim.toLowerCase().contains("alveoloplasty in conjunction with
		// extractions"))
		// lim = "Yes";d
		// else
		// lim = "No";
		System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
		System.out.println(lim);
		dtemp.setAlveoD7311CoveredWithEXT(lim);// 64

		dtemp.setAlveoD7311FL(getBenefitProcedureValueFromMap("D7311", benefitLimitation, values, false));// 65

		lim = getBenefitProcedureValueFromMap("D7310", benefitAlveoloplasty, values, false);
		// if (lim.toLowerCase().contains("alveoloplasty in conjunction with
		// extractions"))
		// lim = "Yes";
		// else
		// lim = "No";
		System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
		System.out.println(lim);

		dtemp.setAlveoD7310CoveredWithEXT(lim);// 66

		dtemp.setAlveoD7310FL(getBenefitProcedureValueFromMap("D7310", benefitLimitation, values, false));// 67

		dtemp.setCompleteDenturesD5110D5120FL(
				getBenefitProcedureValueFromMap("D5110", benefitLimitation, values, false));// 68

		dtemp.setImmediateDenturesD5130D5140FL(
				getBenefitProcedureValueFromMap("D5130", benefitLimitation, values, false));// 69

		dtemp.setPartialDenturesD5213D5214FL(
				getBenefitProcedureValueFromMap("D5213", benefitLimitation, values, false));// 70

		dtemp.setInterimPartialDenturesD5214FL(
				getBenefitProcedureValueFromMap("D5820", benefitLimitation, values, false));// 71 This field is actually
																							// for the frequency of
																							// D5820 and not D5214. The
																							// header of the column in
																							// the RDBMS was labeled
																							// incorrectly."
		// 72
		dtemp.setBoneGraftsD7953FL(getBenefitProcedureValueFromMap("D7953", benefitLimitation, values, false));// 73

		dtemp.setImplantCoverageD6010Percentage(
				getBenefitProcedureValueFromMap("D6010", benefitContract, values, false));// 74

		dtemp.setImplantCoverageD6057Percentage(
				getBenefitProcedureValueFromMap("D6057", benefitContract, values, false));// 75

		dtemp.setImplantCoverageD6190Percentage(
				getBenefitProcedureValueFromMap("D6190", benefitContract, values, false));// 76

		dtemp.setImplantSupportedPorcCeramicD6065Percentage(
				getBenefitProcedureValueFromMap("D6065", benefitContract, values, false));// 77

		if (checkforD2391D2140) {
			dtemp.setPostCompositesD2391Percentage(
					getBenefitProcedureValueFromMap("D2391", benefitContract, values, false));// 78

			dtemp.setPostCompositesD2391FL(getBenefitProcedureValueFromMap("D2391", benefitLimitation, values, false));// 79
			dtemp.setCrownsD2750D2740Downgrade("No");
		} else {
			dtemp.setPostCompositesD2391Percentage(
					getBenefitProcedureValueFromMap("D2140", benefitContract, values, false));// 78

			dtemp.setPostCompositesD2391FL(getBenefitProcedureValueFromMap("D2140", benefitLimitation, values, false));// 79
			dtemp.setCrownsD2750D2740Downgrade("Yes");

		}
		// 80
		dtemp.setCrownsD2750D2740Percentage(getBenefitProcedureValueFromMap("D2740", benefitContract, values, false));// 81

		dtemp.setCrownsD2750D2740FL(getBenefitProcedureValueFromMap("D2740", benefitLimitation, values, false));// 82
		// 83
		dtemp.setNightGuardsD9940Percentage(getBenefitProcedureValueFromMap("D9944", benefitContract, values, false)); // 84

		dtemp.setD9310Percentage(getBenefitProcedureValueFromMap("D9310", benefitContract, values, false));// 85

		dtemp.setD9310FL(getBenefitProcedureValueFromMap("D9310", benefitLimitation, values, false));// 86

		dtemp.setBuildUpsD2950Covered(getBenefitProcedureValueFromMap("D2950", benefitContract, values, false));// 87

		dtemp.setBuildUpsD2950FL(getBenefitProcedureValueFromMap("D2950", benefitLimitation, values, false));// 88
		// 89
		dtemp.setOrthoPercentage(getBenefitProcedureValueFromMap("D8090", benefitContract, values, false));// 90

		// 93
		dtemp.setBridges1(getBenefitProcedureValueFromMap("D6245", benefitContract, values, false));// 102

		dtemp.setBridges2(getBenefitProcedureValueFromMap("D6245", benefitLimitation, values, false));// 103
		// 96 97 98 99 100 101 102 103 104

		dtemp.setDen5225Per(getBenefitProcedureValueFromMap("D5225", benefitContract, values, false));// 105

		dtemp.setDen5226Per(getBenefitProcedureValueFromMap("D5226", benefitContract, values, false));// 107

		dtemp.setDenf5225FR(getBenefitProcedureValueFromMap("D5225", benefitLimitation, values, false));// 106

		dtemp.setDenf5226Fr(getBenefitProcedureValueFromMap("D5226", benefitLimitation, values, false));// 108

		dtemp.setImplantsFrD6010(getBenefitProcedureValueFromMap("D6010", benefitLimitation, values, false));// 110

		dtemp.setImplantsFrD6057(getBenefitProcedureValueFromMap("D6057", benefitLimitation, values, false));// 111

		dtemp.setImplantsFrD6065(getBenefitProcedureValueFromMap("D6065", benefitLimitation, values, false));// 112

		dtemp.setImplantsFrD6190(getBenefitProcedureValueFromMap("D6190", benefitLimitation, values, false));// 113

		dtemp.setNightGuardsD9945Percentage(getBenefitProcedureValueFromMap("D9945", benefitContract, values, false));// 114

		dtemp.setFmxPer(getBenefitProcedureValueFromMap("D0210", benefitContract, values, false));// 120

		dtemp.setNightGuardsD9944Fr(getBenefitProcedureValueFromMap("D9944", benefitLimitation, values, false));// 121

		dtemp.setNightGuardsD9945Fr(getBenefitProcedureValueFromMap("D9945", benefitLimitation, values, false));// 122

		dtemp.setPano1(getBenefitProcedureValueFromMap("D0330", benefitContract, values, false));//
		dtemp.setD0330Freq(getBenefitProcedureValueFromMap("D0330", benefitLimitation, values, false));//
		
		dtemp.setPedo2(getBenefitProcedureValueFromMap("D2934", benefitContract, values, false));//
		dtemp.setFreqD2934(getBenefitProcedureValueFromMap("D2934", benefitLimitation, values, false));//
		//
		
		dtemp.setPedo1(getBenefitProcedureValueFromMap("D0160", benefitContract, values, false));//
		dtemp.setD0160Freq(getBenefitProcedureValueFromMap("D0160", benefitLimitation, values, false));//
		

		dtemp.setD4381(getBenefitProcedureValueFromMap("D4381", benefitContract, values, false));//
		dtemp.setD4381Freq(getBenefitProcedureValueFromMap("D4381", benefitLimitation, values, false));//
		
		dtemp.setD3330(getBenefitProcedureValueFromMap("D3330", benefitContract, values, false));//
		dtemp.setD3330Freq(getBenefitProcedureValueFromMap("D3330", benefitLimitation, values, false));//

		PatientDetailTemp2 p2 = new PatientDetailTemp2();
		p2.setD0350(getBenefitProcedureValueFromMap("D0350", benefitContract, values, false));//
		p2.setD1330(getBenefitProcedureValueFromMap("D1330", benefitLimitation, values, false));//
		p2.setD2930(getBenefitProcedureValueFromMap("D2930", benefitContract, values, false));// 160
		
		
		

		// CHECK FOR FREQENCY CORRECT LOGIC....

		// By default we have Diagnostic open..
		// //template1:r1:1:r1:1:r1:0:t1:3:commandLink1

		// System.out.println("dd" + dd);
		List<PatientHistoryTemp> hisSet = temp.getPatientHistory();
		openSideBarFirst(driver, "Treatment history", false, null, false, temp);
		fetchHistoryformation(driver, hisSet);

		dtemp.setCkD0120("No");
		dtemp.setCkD0140("No");
		dtemp.setCkD0145("No");
		dtemp.setCkD0150("No");
		dtemp.setCkD0160("No");

		dtemp.setCkD210("No");
		dtemp.setCkD220("No");
		dtemp.setCkD230("No");
		dtemp.setCkD330("No");
		dtemp.setCkD274("No");

		for (PatientHistoryTemp ht : hisSet) {
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D0120"))
				dtemp.setCkD0120("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D0140"))
				dtemp.setCkD0140("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D0145"))
				dtemp.setCkD0145("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D0150"))
				dtemp.setCkD0150("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D0160"))
				dtemp.setCkD0160("Yes");

			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D210"))
				dtemp.setCkD210("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D220"))
				dtemp.setCkD220("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D230"))
				dtemp.setCkD230("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D330"))
				dtemp.setCkD330("Yes");
			if (ht.getHistoryCode() != null && ht.getHistoryCode().equalsIgnoreCase("D274"))
				dtemp.setCkD274("Yes");

		}
		// System.out.println("999");
		// int ccc = 0;
		// if (ccc == 0)
		// return;

		// 119
		// 123 //124 //125 //126
		if (!majorMaxMin1.equals("") &&
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
			    !majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0].equals(majorMaxMin1.split(MIN_MAX_VAL_SPLIT)[1]))
			dtemp.setComments(dtemp.getComments() + "\n Major Percentage:" + majorMaxMin1.split(MIN_MAX_VAL_SPLIT)[0] +"-"+majorMaxMin1.split(MIN_MAX_VAL_SPLIT)[1]+"%");
		if (!majorMaxMin2.equals("") &&
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0].equals(majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[1]))
			dtemp.setComments(dtemp.getComments() + "\n Endodontics Percentage:" + majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[0] +"-"+majorMaxMin2.split(MIN_MAX_VAL_SPLIT)[1]+"%");
		if (!majorMaxMin3.equals("") &&
				!majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
			!majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[0].equals(majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[1]))
			dtemp.setComments(dtemp.getComments() + "\n PerioSurgery Percentage:" + majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[0] +"-"+majorMaxMin3.split(MIN_MAX_VAL_SPLIT)[1]+"%");
		if (!majorMaxMin4.equals("") &&
				!majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[0].equals(majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[1]))
			dtemp.setComments(dtemp.getComments() + "\n Preventive Percentage:" + majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[0] +"-"+majorMaxMin4.split(MIN_MAX_VAL_SPLIT)[1]+"%");
		if (!majorMaxMin5.equals("") &&
				!majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[0].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_NOT_FOUND) &&
				!majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[1].equals(Constants.SCRAPPING_MANDATORY_WARNING) && 
				!majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[0].equals(majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[1]))
			dtemp.setComments(dtemp.getComments() + "\n Diagnostic Percentage:" + majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[0] +"-"+majorMaxMin5.split(MIN_MAX_VAL_SPLIT)[1]+"%");

		
		System.out.println("COMMMMM---" + dtemp.getComments());
		dtemp.setPlanType(dtemp.getPlanType().replace(planTypeSUP, ""));
		dtemp.setPatientDetails2(p2);
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
			if (type.equals(benefitAgeLimit)) {
				val = val.replaceAll("[a-zA-Z]", "").trim();
				ret = val;
				if (ret.equals(""))
					ret = "99";
			} else if (type.equals(benefitAlveoloplasty)) {
				if (val.contains("Alveoloplasty in conjunction with extractions"))
					ret = "Yes";
				else
					ret = "No";
			} else if (type.equals(benefitContract)) {
				ret = val.split(" ")[0];
				if (ret.equals(""))
					ret = "0";
			} else if (type.equals(benefitLimitation)) {
				ret = val.split("----")[0];
				if (ret.equals("") && val.split("----").length > 1)
					ret = val.split("----")[1];
				if (ret.equals(""))
					ret = "0";// as per email date 6 aug :Re: Denta Dental - Scrapping Issue (Part 2)
			} else if (type.equals(benefitLimitation + "_TOOTH")) {
				ret = val;
			} else if (type.equals(benefitLimitation + "_FULL")) {
				ret = val;
			}
		} else if (val == null) {
			if (type.equals(benefitAgeLimit)) {
				ret = "0";
			}
		}
		System.out.println("VAL RET--" + ret);

		return ret;
	}

	//Close feedBack..
	private void closeFeedBack(WebDriver driver) throws InterruptedException {
		
		try {
		Thread.sleep(26000);
		//driver.findElement(By.className("QSIInfoBar")).findElement(By.tagName("img")).click();
		//Thread.sleep(1000);
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String bluff = "document.getElementsByClassName(\"QSIInfoBar\")[0].remove()";
		js.executeScript(bluff);
		Thread.sleep(15000);
		
		}catch (Exception e) {
			System.out.println("NO QSIInfoBar ");
			
		}
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
		Thread.sleep(4000);
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
					// String counterLink = li.getAttribute("id");
					// System.out.println(li.getAttribute("id"));
					// asdadad anscetor logic
					//
					li.click();
					Thread.sleep(8000);
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

	private void fetchHistoryformation(WebDriver driver, List<PatientHistoryTemp> setHis) throws InterruptedException {
		Thread.sleep(3000);
		// System.out.println(driver.getCurrentUrl());

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
							t.setHistoryDOS(dt.trim());
							String[] s = dt.trim().split("/");
							try {
								t.setHistoryDOS(s[2] + "-" + (s[0].length() == 2 ? s[0] : "0" + s[0]) + "-"
										+ (s[1].length() == 2 ? s[1] : "0" + s[1]));

							} catch (Exception e) {
								// TODO: handle exception
								// dtemp.setPlanTermedDate("");
							}
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
	 * private String fetchBenefitDetails(String name, PatientTemp temp, WebDriver
	 * driver, String type, String firstA, String secondA, String planType, boolean
	 * mainOpen, boolean mandatory) throws InterruptedException {
	 * System.out.println("fetchBenefitDetails -" + name); System.out.println("IN-"
	 * + name + "-" + new Date()); String value = Constants.SCRAPPING_NOT_FOUND; if
	 * (mandatory) value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
	 * else value = ""; Thread.sleep(1000); try { if (!mainOpen) { //
	 * System.out.println(firstA); //
	 * System.out.println(counterElementMap.get(firstA));
	 * 
	 * driver.findElement(By.id(temp.getCounterElementMap().get(firstA))).click();
	 * Thread.sleep(7000); } List<WebElement> as =
	 * driver.findElements(By.tagName("a")); for (WebElement a : as) { if
	 * (a.getText().equals(secondA)) { a.click(); Thread.sleep(7000); break; } }
	 * 
	 * List<WebElement> tdss = driver.findElements(By.tagName("td")); WebElement
	 * table = null; for (WebElement td : tdss) { if (td.getText().equals(name)) {
	 * table = td.findElement(By.xpath("..")).findElement(By.xpath("..")); break; }
	 * 
	 * } if (table == null) return Constants.SCRAPPING_NOT_FOUND; // WebElement
	 * table =driver.findElement(By.id(thirdA));
	 * 
	 * // boolean found = false; try { // String clas=table.getAttribute("class");
	 * // System.out.println("DD"+clas); // if (clas!=null &&
	 * clas.equalsIgnoreCase("x22t")) { List<WebElement> ths =
	 * table.findElements(By.tagName("th")); // count the Th // if
	 * (type.equals(benefitContract) || type.equals(benefitAgeLimit)) { int counter
	 * = 0; for (WebElement th : ths) { try { if (th.getText() != null &&
	 * th.getText().contains(" " + planType)) { // found = true; try { counter =
	 * counter + Integer.parseInt(th.getAttribute("colspan")); } catch (Exception x)
	 * { counter = counter + 1; } break;
	 * 
	 * } try { counter = counter + Integer.parseInt(th.getAttribute("colspan")); }
	 * catch (Exception x) { counter = counter + 1; } } catch (Exception e) { //
	 * TODO: handle exception
	 * 
	 * } // if (th.getText().contains(planType)) break;
	 * 
	 * } // if (!found) continue; List<WebElement> trs =
	 * table.findElements(By.tagName("tr")); int x = 0; for (WebElement tr : trs) {
	 * if (x == 0 || x == 1) { x++; continue; } // System.out.println("&&&--" + x);
	 * List<WebElement> tds = tr.findElements(By.tagName("td")); //
	 * System.out.println("99999--" + counter); if (tds != null && tds.size() > 0) {
	 * // System.err.println(tds.get(0).getText()); if
	 * (tds.get(0).getText().equals(name)) { //
	 * System.err.println(tds.get(0).getText()); value = tds.get(counter - 1 -
	 * 1).getText().replace("%", "").split(" ")[0];
	 * 
	 * break; }
	 * 
	 * } }
	 * 
	 * // }
	 * 
	 * // } } catch (Exception e) { e.printStackTrace(); // TODO: handle exception }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return value + " " +
	 * Constants.SCRAPPING_ISSUE_FETCHING; } // if (toggle != null) //
	 * togggle.click(); // System.out.println("value--" + value);
	 * System.out.println("OUT-" + new Date()); return val ue; }
	 */
	private String getClassNameforSummaryTags(WebElement divOffMax, String nameofSummary) {
		String className = "x267";
		System.out.println("nameofSummary-" + nameofSummary);
		try {
			WebElement tr = divOffMax.findElements(By.tagName("tr")).get(1);
			List<WebElement> tds = tr.findElements(By.tagName("td"));
			// System.out.println("00"+tds.get(tds.size()-2).findElement(By.tagName("span")).getAttribute("class"));
			if (tds.get(tds.size() - 2).findElement(By.tagName("span")).getAttribute("class") != null && !tds
					.get(tds.size() - 2).findElement(By.tagName("span")).getAttribute("class").toString().equals("")) {
				className = tds.get(tds.size() - 2).findElement(By.tagName("span")).getAttribute("class");
			}

		} catch (Exception cna) {
			// cna.printStackTrace();
			try {
				WebElement tr = divOffMax.findElements(By.tagName("tr")).get(0);
				List<WebElement> tds = tr.findElements(By.tagName("td"));
				// System.out.println("00"+tds.get(0).getAttribute("class"));
				if (tds.get(0).getAttribute("class") != null
						&& !tds.get(0).getAttribute("class").toString().equals("")) {
					className = tds.get(0).getAttribute("class");
				}
			} catch (Exception next) {
				next.printStackTrace();
			}

		}
		return className;
	}

	private String getClassNameforDeductiblesTags(WebElement divOfDed) {
		String className = "x265";
		
		try {
		className= divOfDed.findElements(By.tagName("td")).get(0).findElements(By.tagName("span")).get(0).getAttribute("class");
		}catch(Exception c) {
			
		}
		return className;
	}

	private String fetchBenefitDetails(String name, PatientTemp temp, WebDriver driver, String type, String firstA,
			String secondA, String planType, boolean mainOpen, boolean mandatory) throws InterruptedException {
		System.out.println("fetchBenefitDetails TEST -" + name + " -ss" + secondA + " -firstA" + firstA);
		// System.out.println("IN-" + name + "-" + new Date());
		String value = Constants.SCRAPPING_NOT_FOUND;
		String valueNext = Constants.SCRAPPING_NOT_FOUND;
		
		if (mandatory)
			value = value + ". " + Constants.SCRAPPING_MANDATORY_WARNING;
		else
			value = "";
		Thread.sleep(5000);
		String showHide = "Show all +";
		try {
			if (!mainOpen) {
				// System.out.println(firstA);
				// System.out.println(counterElementMap.get(firstA));

				driver.findElement(By.id(temp.getCounterElementMap().get(firstA))).click();
				Thread.sleep(17000);
			} else {
				showHide = "Hide all -";
			}

			// SHOW ALL LINK..
			// WebElement main=null;
			List<String> linkNamesinUi = new ArrayList<>();
			List<WebElement> aas = driver.findElements(By.tagName("a"));
			for (WebElement aa : aas) {
				if (aa.getText() != null && aa.getText().equals(showHide)) {
					List<WebElement> trs = aa.findElement(By.xpath("..")).findElement(By.xpath(".."))
							.findElement(By.xpath("..")).findElement(By.xpath("..")).findElement(By.xpath(".."))
							.findElement(By.xpath("..")).findElement(By.xpath("..")).findElements(By.tagName("tr"));
					for (WebElement tr : trs) {
						// System.out.println(tr.getText());
						if (tr.getText().contains(showHide)) {
							// main=tr.findElement(By.xpath("..")).findElement(By.xpath("..")).findElement(By.xpath(".."))
							// .findElement(By.xpath("..")).findElement(By.xpath("..")).findElement(By.xpath(".."))
							// .findElement(By.xpath("..")).findElement(By.xpath(".."));
							continue;
						}
						// System.out.println(tr.getText());
						if (tr.getText() != null) {
							// System.out.println("linkNamesinUi--" + tr.getText()+"--");
							linkNamesinUi.add(tr.getText());
						}
					}
					if (!mainOpen) {
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("window.scrollBy(0,500)");
						Thread.sleep(1000);
						aa.click();
						Thread.sleep(15000);
					}

					break;
				}
			}
			System.out.println("%%%%%%%%%" + planType);
			List<WebElement> tabss = driver.findElements(By.tagName("table"));
			// System.out.println("size=="+tabss.size());
			boolean br = false;
			boolean found = false;
			int counter2 = 0;
			for (WebElement tab : tabss) {
				if (tab.getAttribute("summary") != null
						&& tab.getAttribute("summary").equals("The ProcCode tables on the ShowAll click action")) {
					WebElement mainDiv = tab.findElement(By.xpath("..")).findElement(By.xpath(".."));
					mainDiv = mainDiv.findElements(By.tagName("div")).get(counter2);
					counter2 = counter2 + 7;
					// System.out.println("***************"+mainDiv.getText());
					String divHeading = mainDiv.getText();
					boolean moveForward = false;

					for (String linkName : linkNamesinUi) {
						// System.out.println("HERE--"+linkName+"-"+secondA+'-'+divHeading+"-");
						if ((divHeading.startsWith(linkName) && linkName.equals(secondA)) || secondA.equals("")) {
							moveForward = true;
						}
						if (moveForward) {
							WebElement table = tab;// mainDiv.findElement(By.tagName("table"));
							// System.out.println("table" + table);
							List<WebElement> ths = table.findElements(By.tagName("th"));
							// System.out.println("ths" + ths);
							// WebElement acTable=null;
							int counter = 0;
							for (WebElement th : ths) {
								try {
									// System.out.println("99999999 1" + th.getText() + "--");
									// System.out.println("99999999 2" + planType + "--");
									String text = th.getText();
									if (text != null && text.trim().equals(""))
										text = th.getAttribute("innerText");

									if (text != null && text.contains(planType)) {
										temp.setPlanTypeinSite(true);
										// found = true;
										try {
											counter = counter + Integer.parseInt(th.getAttribute("colspan"));
											// acTable=th.findElement(By.xpath("..")).findElement(By.xpath(".."));
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

							// *[@id="template1:r1:1:r1:1:r1:1:t3"]/tbody/tr[3]
							// System.out.println("//*[@id=\""+table.getAttribute("id")+"\"]/tbody/tr[3]");

							List<WebElement> trs = driver
									.findElements(By.xpath("//*[@id=\"" + table.getAttribute("id") + "\"]/tbody/tr"));// table.findElements(By.tagName("tr"));
							if (counter >= 8)
								counter = counter + 2;
							int x = 0;
							for (WebElement tr : trs) {
								// System.err.println(tr.getText());
								if (x == 0 || x == 1) {
									x++;
									continue;
								}
								// System.out.println("UUUUUUUU");
								// System.out.println("--->"+tr.getText());
								// if (tr.findElements(By.tagName("th")).size()>0)
								// continue;
								// if (tr.getText().startsWith("Delta Dental"))
								// continue;
								// if (tr.getText().contains("Contract Benefit Level Age limit"))
								// continue;
								// System.out.println("&&&--" + x);
								List<WebElement> tds = tr.findElements(By.tagName("td"));

								// System.out.println("99999--" + tds.size());
								if (tds != null && tds.size() > 0) {
									// System.err.println("-"+tds.get(0).getText()+"-");
									// System.err.println("-"+name+"-");
									if (tds.get(0).getText().equals(name)) {

										try {
											WebElement tableIn = tds.get(counter - 1 - 1)
													.findElement(By.tagName("table"));
											String id = tds.get(counter - 1 - 1).findElement(By.tagName("table"))
													.getAttribute("id");
											JavascriptExecutor js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,3500)");
											String script = "return document.getElementById('" + id + "').innerText";
											value = ((String) ((JavascriptExecutor) driver).executeScript(script,
													tableIn)).replace("%", "").split("%")[0].split("	")[0]
															.replaceAll(" ", "");
										} catch (Exception t) {
											value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
										}
										found = true;
										break;
									} else if (name.equals(MIN_VAL)) {
										try {
											WebElement tableIn = tds.get(counter - 1 - 1)
													.findElement(By.tagName("table"));
											String id = tds.get(counter - 1 - 1).findElement(By.tagName("table"))
													.getAttribute("id");
											JavascriptExecutor js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,3500)");
											String script = "return document.getElementById('" + id + "').innerText";
											String tp=((String) ((JavascriptExecutor) driver).executeScript(script,
													tableIn)).split("%")[0].split("	")[0].replaceAll(" ", "");
											//value = ((String) ((JavascriptExecutor) driver).executeScript(script,
											//		tableIn)).split("%")[0].split("	")[0].replaceAll(" ", "");
											//System.out.println("TTTPP--"+tp);
											//System.out.println("TTTPP--"+tp+"-"+tds.get(0).getText());
											
											if (value.equals(Constants.SCRAPPING_NOT_FOUND) ||
													value.equals(Constants.SCRAPPING_MANDATORY_WARNING) ||
													value.equals("") ||
													Integer.parseInt(tp) < Integer.parseInt(value)) value=tp;
										} catch (Exception t) {
											value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
										}
                                               
									}else if (name.equals(MAX_VAL)) {
										try {
											WebElement tableIn = tds.get(counter - 1 - 1)
													.findElement(By.tagName("table"));
											String id = tds.get(counter - 1 - 1).findElement(By.tagName("table"))
													.getAttribute("id");
											JavascriptExecutor js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,3500)");
											String script = "return document.getElementById('" + id + "').innerText";
											String tp=((String) ((JavascriptExecutor) driver).executeScript(script,
													tableIn)).split("%")[0].split("	")[0].replaceAll(" ", "");
											//value = ((String) ((JavascriptExecutor) driver).executeScript(script,
											//		tableIn)).split("%")[0].split("	")[0].replaceAll(" ", "");
											//System.out.println("TTTPP--"+tp);
											//System.out.println("TTTPP--"+tp+"-"+tds.get(0).getText());
											
											if (value.equals(Constants.SCRAPPING_NOT_FOUND) ||
													value.equals(Constants.SCRAPPING_MANDATORY_WARNING) ||
													value.equals("") ||
													Integer.parseInt(tp) > Integer.parseInt(value)) value=tp;
										} catch (Exception t) {
											value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
										}
                                               
									} else if (name.equals(MIN_MAX_VAL)) {
										try {
											WebElement tableIn = tds.get(counter - 1 - 1)
													.findElement(By.tagName("table"));
											String id = tds.get(counter - 1 - 1).findElement(By.tagName("table"))
													.getAttribute("id");
											JavascriptExecutor js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,3500)");
											String script = "return document.getElementById('" + id + "').innerText";
											String tp=((String) ((JavascriptExecutor) driver).executeScript(script,
													tableIn)).split("%")[0].split("	")[0].replaceAll(" ", "");
											//value = ((String) ((JavascriptExecutor) driver).executeScript(script,
											//		tableIn)).split("%")[0].split("	")[0].replaceAll(" ", "");
											//System.out.println("TTTPP--"+tp);
											//System.out.println("TTTPP--"+tp+"-"+tds.get(0).getText());
											
											if (value.equals(Constants.SCRAPPING_NOT_FOUND) ||
													value.equals(Constants.SCRAPPING_MANDATORY_WARNING) ||
													value.equals("") ||
													Integer.parseInt(tp) < Integer.parseInt(value)) value=tp;
											
											if (valueNext.equals(Constants.SCRAPPING_NOT_FOUND) ||
													valueNext.equals(Constants.SCRAPPING_MANDATORY_WARNING) ||
													valueNext.equals("") ||
													Integer.parseInt(tp) > Integer.parseInt(valueNext)) valueNext=tp;
										} catch (Exception t) {
											value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
											valueNext = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
										}
                                               
									} else if (name.equals("")) {
										try {
											WebElement tableIn = tds.get(counter - 1 - 1)
													.findElement(By.tagName("table"));
											String id = tds.get(counter - 1 - 1).findElement(By.tagName("table"))
													.getAttribute("id");
											JavascriptExecutor js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,3500)");
											String script = "return document.getElementById('" + id + "').innerText";
											value = ((String) ((JavascriptExecutor) driver).executeScript(script,
													tableIn)).split("%")[0].split("	")[0].replaceAll(" ", "");
										} catch (Exception t) {
											value = tds.get(counter - 1 - 1).getText().replace("%", "").split(" ")[0];
										}

										// System.out.println("ccc999-" + value);
										found = true;
										// if (!value.equals(""))
										break;
									}

								}
							}

							// }
							if (linkName.equals(secondA)) {
								br = true;
								break;
							}
							if (secondA.equals("") && found) {
								br = true;
								break;
							}

						} // move forward
					} // for link name
				}
				if (br)
					break;
			} // for
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		if (name.equals(MIN_MAX_VAL)) {
			value=value+MIN_MAX_VAL_SPLIT+valueNext;
		}
		System.out.println("value=" + value);
		return value;
	}

	// return TABLE
	private String[] fetchBenefitSearchProcedure(HashMap<String, DentaBenefitScrapDto> dataMap,
			HashMap<String, String> values, WebDriver driver, String planType, boolean clear, String[] divInfo,
			boolean checkComment,String frequenCYFYPY) throws InterruptedException {
		// System.out.println("fetchBenefitInformation" + name);

		Thread.sleep(4000);
		String[] info = new String[6];
		info[5] = "";// Used for comment
		if (clear) {
			if (CLEAR_ID.equals("")) {					
			List<WebElement> el = driver.findElements(By.tagName("a"));
			for (WebElement e : el) {
				if (e.getText() != null && e.getText().equals("Clear All")) {
					//CLEAR_ID=e.getAttribute("id");//Apply this logic latter if needed
					e.click();
					Thread.sleep(6000);
					break;
				}
			 }
			}
			else {
				Thread.sleep(2000);
				System.out.println("CLEAR_ID--"+CLEAR_ID);
				WebElement e =driver.findElement(By.id(CLEAR_ID));
				e.click();
				Thread.sleep(5000);
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
			Thread.sleep(9000);
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
			Thread.sleep(8000);

			//
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,3500)");
			// js.executeScript("window.scrollBy(0,500)");
			// js.executeScript("window.scrollBy(0,500)");
			Thread.sleep(3000);
			//
			// System.out.println("LL:" + tableBd.size());
			info[4] = "";
			int counterTofind = 3;
			WebElement table = null;
			if (divInfo == null) {
				List<WebElement> divMain = driver.findElements(By.tagName("div"));
				for (WebElement divMain1 : divMain) {
					if (divMain1.getAttribute("id").endsWith("tablePanelGrp")) {
						if (checkComment) {
							List<WebElement> fos = driver.findElements(By.tagName("font"));
							for (WebElement fo : fos) {
								String f = fo.getAttribute("size");
								if (f != null && f.equals("3")) {
									info[4] = fo.getText();
									break;
								}
							}
						}
						info[2] = divMain1.getAttribute("id");
						// table= driver.findElement(By.xpath("//*[@id=\""+info[2]+"\"]/div[7]/table"));
						// System.out.println("----------------------------------------");
						// System.out.println("info[2]");
						// System.out.println(info[2]);
						break;
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
					// System.err.println("FOR ---" + entry.getKey());
					// for (WebElement table : tableBd)
					{
						// System.err.println("FOR -INNN--" + entry.getKey() + "-" + type);
						boolean found = false;
						try {
							if (values.get(entry.getKey() + "_" + type) != null)
								break;
							String clas = table.getAttribute("class");
							// System.out.println("DD" + clas+"-");

							if (clas != null && clas.equalsIgnoreCase("x22u")) {
								List<WebElement> ths = driver
										.findElement(By.xpath("//*[@id=\"" + info[2] + "\"]/div[7]/table"))
										.findElements(By.tagName("th"));// table.findElements(By.tagName("th"));
								if (type.equals(benefitContract) || type.equals(benefitAgeLimit)
										|| type.equals(benefitAlveoloplasty)) {
									int counter = 0;
									for (WebElement th : ths) {
										try {
											if (th.getText() != null && th.getText().contains(planType)) {
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
									// System.out.println("counterTofind--" + counterTofind);
									try {
										List<WebElement> trL = driver.findElements(
												By.xpath("//*[@id=\"" + info[2] + "\"]/div[7]/table/tbody/tr"));
										// dataMap.size()

										// System.out.println("SSSSSSSS--"+trL.size());
										for (int x = 3; x < trL.size() + 3; x++) {
											WebElement tr = driver.findElement(By.xpath(
													"//*[@id=\"" + info[2] + "\"]/div[7]/table/tbody/tr[" + x + "]"));
											List<WebElement> tds = tr.findElements(By.tagName("td"));
											// System.out.println("^^11^^"+tr.getText());
											// System.out.println("99999--" + counter);
											// System.err.println(tds.get(0).getText());
											if (!tds.get(0).getText().equals(entry.getKey()))
												continue;
											if (values.get(entry.getKey() + "_" + type) != null)
												break;
											// System.out.println("^^^^"+tr.getText());
											if (type.equals(benefitContract)) {
												if (counter >= 8) {
													value = tds.get(counter + 3 - 1 - 1).getText().replace("%", "");
												//value = tds.get(counter + 3 - 1 - 1).getAttribute("innerText").replace("%", "");
												}else {
													value = tds.get(counter - 1 - 1).getText().replace("%", "");
											     //value = tds.get(counter - 1 - 1).getAttribute("innerText").replace("%", "");;
												}
											//System.out.println(entry.getKey()+"---"+benefitContract+"-->"+value);	
											}
											if (type.equals(benefitAgeLimit)) {
												// System.out.println("888888888888888888");

												value = tds.get(counter - 1 - 1).getText().trim();
												// System.out.println("8888 1"+ value);
												if (value.contains(" ")) {// 100% 1 -- 100%
													System.out.println("8888 2" + value);
													value = tds.get(counter - 1 + 4).getText();

												} else if (value.equals("")) {
													// System.out.println("8888 3"+ value);
													value = tds.get(counter - 1).getText();
												} else {
													// System.out.println("8888 4"+ value);
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
										values.put((entry.getKey() + "_" + type), "");
									}
									// break;

								}
								if (type.equals(benefitLimitation)) {
									boolean fd = false;
									// List<WebElement> trone =
									// driver.findElement(By.xpath("//*[@id=\"template1:r1:1:r1:1:r1:0:r1:1:tablePanelGrp\"]/div[7]/table")).findElements(By.tagName("tr"));

									try {
										List<WebElement> trL = driver.findElements(
												By.xpath("//*[@id=\"" + info[2] + "\"]/div[7]/table/tbody/tr"));
										// dataMap.size()
										// System.out.println("SSSSSSS777S--"+trL.size());
										for (int x = 3; x < trL.size() + 3; x++) {// 12
											WebElement tr = driver.findElement(By.xpath(
													"//*[@id=\"" + info[2] + "\"]/div[7]/table/tbody/tr[" + x + "]"));
											List<WebElement> tds = tr.findElements(By.tagName("td"));
											// System.err.println("@@@@"+tds.get(0).getText());
											if (!tds.get(0).getText().equals(entry.getKey()))
												continue;
											if (values.get(entry.getKey() + "_" + type) != null)
												break;
											// System.err.println("@&&&@@"+tr.getText());
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
											// System.out.println("5555:" + tds.get(0).getText());

											// System.out.println("44444:" + value);
											fd = true;
											if (value.contains("[Limitations Apply]")) {
												try {
													// Click on Link
													td.findElements(By.tagName("a")).get(0).click();
													Thread.sleep(5000);
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
														List<WebElement> trs = container
																.findElements(By.tagName("table")).get(1)
																.findElements(By.tagName("tr"));
														trs.remove(0);

														for (WebElement tr1 : trs) {
															List<WebElement> tds1 = tr1.findElements(By.tagName("td"));
															ar.add(tds1.get(0).getText() + "----"
																	+ tds1.get(1).getText() + "----"
																	+ tds1.get(2).getText());
														}
													} catch (Exception nn) {
														nn.printStackTrace();
													}
													String tempVal = "";
													String tempDef = "";
													String tempValT = "";
													String tempDefT = "";
													int extracomment = -1;
													for (String ap : ar) {
														String[] dd = ap.split("----");
														try {

															// values.put((entry.getKey()+"_"+type),dd[0]);
															if (extracomment == -1) {
																if (dd[1].equals("No")) {
																	tempVal = dd[0];
																	try {
																		tempValT = dd[2];// Not sure if tooth exits or
																							// not
																	} catch (Exception e) {

																	}

																	// break;no need to break...
																}
															}
															System.out.println("KEY- " + entry.getKey());
															System.out.println("AGE -" + data.getAge());
															System.out.println(
																	"--" + dd[1].replace("years and older", "").trim()
																			+ "---");
															String te = dd[1].replace("years and older", "").trim();
															te = te.replace("Child up to and not including age", "")
																	.trim();
															if (te.equals("No"))
																te = "0";
															System.out.println("te--" + te + "---");
															if (entry.getKey().equals("D0145")) {
																System.out.println("9");
															}
															boolean check = data.getAge() >= Integer.parseInt(te);
															if (dd[1].contains("Child up to and not including age")) {
																check = data.getAge() < Integer.parseInt(te);
															}
															check = true;// No need to check age
															if (check) {
																System.out.println("extracomment::" + entry.getKey()
																		+ ":" + extracomment);
																if (extracomment == -1) {
																	extracomment = 0;
																	tempVal = dd[0];
																	try {
																		tempValT = dd[2];// Not sure if tooth exits or
																							// not
																	} catch (Exception e) {

																	}
																} else {
																	// Mark in comments for duplicate Frequency
																	// (Beaumont Maliah Grisham for code- D0150 email 7
																	// aug 2020)
																	String val = FreqencyUtils
																			.convertFrequecyDentaString("", dd[0],frequenCYFYPY);
																	String ret = val.split("----")[0];
																	if (ret.equals("") && val.split("----").length > 1)
																		ret = val.split("----")[1];
																	if (ret.equals(""))
																		ret = "0";// as per email date 6 aug :Re: Denta
																					// Dental - Scrapping Issue (Part 2)

																	String oldV = values
																			.get(entry.getKey() + "_" + type);
																	if (!(oldV != null && oldV.equals(ret))) {// remove
																												// Duplicate
																		info[5] = entry.getKey() + " Frequency :" + ret
																				+ "\n" + info[5];
																	}
																}
																// break;no need to break..
															}

														} catch (Exception ew) {
															ew.printStackTrace();
															System.out.println("extracomment--%%%" + extracomment);
															if (extracomment == -1) {
																// extracomment=0;
																tempDef = "";// dd[0];// Where we do not have text
																				// "years and older"
																try {
																	// Child up to and not including age 3
																	tempDefT = "";// dd[2];// Not sure if tooth exits or
																					// not
																} catch (Exception e) {

																}
															} else {

																// Mark in comments for duplicate Frequency (Beaumont
																// Maliah Grisham for code- D0150 email 7 aug 2020)
																// String
																// val=FreqencyUtils.convertFrequecyDentaString("",
																// dd[0]);
																// String ret = val.split("----")[0];
																// if (ret.equals("") && val.split("----").length > 1)
																// ret = val.split("----")[1];
																// if (ret.equals("")) ret="0";//as per email date 6 aug
																// :Re: Denta Dental - Scrapping Issue (Part 2)

																// info[5] =entry.getKey()+" Frequency :"+ret +"\n"+
																// info[5];// leave for now
															}
															// values.put((entry.getKey()+"_"+type),"");
															// break;
														}
														// data.getAge();
													}
													if (tempVal.equals("")) {
														try {
															values.put((entry.getKey() + "_" + type), FreqencyUtils
																	.convertFrequecyDentaString("", tempDef,frequenCYFYPY));
															values.put((entry.getKey() + "_" + type + "_TOOTH"),
																	tempDefT);
															// values.put((entry.getKey()+"_"+type),ar.get(ar.size()-1).split("----")[0]);
														} catch (Exception ew) {

														}
													} else {
														values.put((entry.getKey() + "_" + type),
																FreqencyUtils.convertFrequecyDentaString("", tempVal,frequenCYFYPY));
														values.put((entry.getKey() + "_" + type + "_TOOTH"), tempValT);
													}

												} catch (Exception e) {
													// TODO: handle exception
												}
												List<WebElement> buts = driver.findElements(By.tagName("button"));
												for (WebElement but : buts) {
													if (but.getText().equals("Close")) {
														but.click();
														// System.out.println("CLOSED TRUE");
														Thread.sleep(4000);
														break;

													}
												}
												break;
											} // LIMITATIONS
											System.out.println("KEY-" + entry.getKey() + "---" + value);
											values.put((entry.getKey() + "_" + type),
													FreqencyUtils.convertFrequecyDentaString("", value,frequenCYFYPY));
											values.put((entry.getKey() + "_" + type + "_FULL"), value);
											// break;
											// }//FOR td0s
											if (fd)
												break;
										}
										// if (fd) break;
									} catch (Exception e) {
										values.put(entry.getKey() + "_" + type, "0");// populate with default value;
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
		Thread.sleep(25000);
		driver.manage().window().maximize();
		Thread.sleep(2000);
		closeFeedBack(driver);
        List<WebElement> eles = driver.findElements(By.className("myToolsTable2"));// myToolsTable for Eligibility &
																					// benefits
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
		System.out.println("taskkill /f /im chromedriver.exe");
		
		System.out.println(Integer.parseInt("09"));
		String [] t="Accumulation period for this Program 23/sdsd/sd".split("Accumulation period for this Program");
		System.out.println(t[1].split("/")[0]);
		try {
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
		dto.setPassword("Elgin%2019");
		dto.setUserName("Elgin5478");

		PatientScrapSearchDto psc = new PatientScrapSearchDto();
		List<PatientScrapSearchDto> l = new ArrayList<>();
		psc.setDob("06/30/1990");// 03/20/1992 12/26/1988
		psc.setFirstName("Brett");// Heather Griffith - Dean Dornak Ellen Keck
		psc.setLastName("Currier");// Patient ID -8909
		psc.setMemberId("123493431201");// 1125727908.. 632307605
		psc.setSsnNumber("123493431201");
		psc.setSubscribersFirstName("");
		psc.setSubscribersLastName("");
		psc.setSubscribersDob("");

		l.add(psc);
		// dto.setPassword("Smile123");

		psc = new PatientScrapSearchDto();
		psc.setDob("03/20/1992");
		psc.setFirstName("Jared");
		psc.setLastName("Grisham");
		psc.setMemberId("");// 632307605
		psc.setSsnNumber("");

		// l.add(psc);

		dto.setDto(l);
		dto.setSiteUrl("https://www1.deltadentalins.com/login.html");
		DeltaDentalServiceImpl i = new DeltaDentalServiceImpl(null, null, f, dto, null, null, 1, "",null,
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
