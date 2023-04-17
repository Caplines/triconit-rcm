package com.tricon.ruleengine.service.scrapfull.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.PatientScrapSearchDto;
import com.tricon.ruleengine.dto.RemoteLiteData;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagment;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.service.impl.BaseScrappingServiceImpl;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;

public class RemoteLiteImpl extends BaseScrappingServiceImpl implements Callable<Boolean>{
	
	@Autowired
	ScrapingFullDataDoa dataDoa;

	@Autowired
	PatientDao patDao;

	private ScrappingSiteDetailsFull scrappingSiteDetails;
	private String CLIENT_SECRET_DIR;
	private String  CREDENTIALS_FOLDER;
	// this.mapData=mapData;
	// this.updateSheet=updateSheet;
	private ScrappingFullDataDetailDto dto;
	private Office office;
	private User user;
	//private String driverLocation;
	private int processId;
	private String taxId;
	private IVFormType ivFormType;
	
	private static final int PAGE_RECORD_1=50;
	private static final int PAGE_RECORD_2=100;
	
	private static final int PAGE_RECORD_INDEX=2;
	private static final String SCRAP_TYPE_1="1";
	private static final String SCRAP_TYPE_2="2";
	

	private static String siteName = "";
	
	public RemoteLiteImpl(PatientDao patDao, ScrapingFullDataDoa dataDoa,
			ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto, User user, Office office,
			int processId,String taxId,IVFormType ivFormType, String driverLocation,String CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER) {

		this.patDao = patDao;
		this.dataDoa = dataDoa;
		this.scrappingSiteDetails = scrappingSiteDetails;
		this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		// this.mapData=mapData;
		// this.updateSheet=updateSheet;
		this.dto = dto;
		this.office = office;
		siteName = dto.getSiteName();
		this.user = user;
		this.driverLocation = driverLocation;
		this.processId = processId;
		this.taxId=taxId;
		this.ivFormType=ivFormType;
		// store parameter for later user
	}
	
	
	public boolean loginToSiteRemote(ScrappingFullDataDetailDto dto, WebDriver driver)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		boolean navigate = true;
		driver.manage().window().maximize();
		driver.get(dto.getSiteUrl());
		Thread.sleep(6000);// Need to keep this number high for Linux issue.
		WebElement userNameElement = driver.findElement(By.id("username"));
		userNameElement.sendKeys(dto.getUserName().trim());
		WebElement passwordElement = driver.findElement(By.id("Password"));

		passwordElement.sendKeys(dto.getPassword().trim());
		WebElement loginButonElement = driver.findElement(
				By.id("login-btn-primary"));

		loginButonElement.click();
		Thread.sleep(1000);// Need to keep this number high for Linux issue.
		try {
			WebElement p = driver
					.findElement(By.id("resp-error-span"));
			if (p.getText().startsWith("Invalid login attempt")) {
				navigate = false;
			}
		} catch (Exception p) {

		}

		// System.out.println(p.getText());
		return navigate;
	}

	private boolean navigatetoMainSite(WebDriver driver, boolean navigate,String type)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		if (!navigate)
			return navigate;
		if (type==null)driver.get("https://rpractice.com/Rlo");
		else if (type!=null && type.equals("1"))driver.get("https://rpractice.com/Rlo");
		else if (type!=null && type.equals("2"))driver.get("https://rpractice.com/Claims");
		Thread.sleep(4000);
		return true;
	}
	
	
	public String scrapSite(ScrappingSiteDetailsFull scrappingSiteDetails, ScrappingFullDataDetailDto dto, User user,
			Office office) {
		// WebDriver driver = null;
		setProps(scrappingSiteDetails.getProxyPort());
		System.out.println("MEM 1-" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

		try {
			
			try {
				
					Thread thread = new Thread() {
						public void run() {
							//System.out.println("Thread Running");
							String s="0";
							WebDriver driver = getBrowserDriver();// new HtmlUnitDriver(true);// getBrowserDriver();
							try {
								System.out.println("MEM 2-"
										+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

								boolean navigate = loginToSiteRemote(dto, driver);

								boolean issueNo = navigatetoMainSite(driver, navigate,scrappingSiteDetails.getScrapSubType());
								//System.out.println("888888888888- STARTED...");
								parsePage(driver, siteName, issueNo, office,scrappingSiteDetails.getScrapSubType());
								//System.out.println("888888888888 -END " + d.getPatientId());
								
								
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
								String os =manageP.getStatus();
								if (os.equals("")) manageP.setStatus(s); 
								else {
									manageP.setStatus(os+","+s);
								}
								manageP.setCount(0);
								manageP.setUpdatedBy(user);
								manageP.setUpdatedDate(new Date());
								dataDoa.updateScrappingFullDataManagmentProcess(manageP);
								if (manage.getProcessCount() > 0) {
									manage.setProcessCount(0);
									dataDoa.increasecrapCount(manage);
								}

								dataDoa.updateScrappingDetailsById(scrappingSiteDetails);
							}
						}
					};

					thread.start();
					Thread.sleep(10000);
				

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
	
	
	private void parsePage(WebDriver driver, String webSiteName, boolean issueNo,
			Office office,String scrapSubType) throws InterruptedException {
		
		//There are 2 types of site Data
		if (scrapSubType==null) scrapSubType=SCRAP_TYPE_1;
		if (scrapSubType.equals("")) scrapSubType=SCRAP_TYPE_1; 
		Thread.sleep(5000);
		try {
		  if (scrapSubType.equals(SCRAP_TYPE_2))driver.findElement(By.id("rc-tabs-0-tab-Sent")).click();
		}catch(Exception s) {
			System.out.println("NO SEND");
		}
		Thread.sleep(5000);
		List<RemoteLiteData> dList = new ArrayList<>();
		try {
		//Show hidden claims /view Hidden Claims
		/*
		WebElement showHiddenClaims = driver.findElements(By.className("grid-control-icon-container")).get(0);
		Actions action = new Actions(driver);
		action.moveToElement(showHiddenClaims).perform();
		*/
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			if (scrapSubType.equals(SCRAP_TYPE_1)) {
				executor.executeScript("document.getElementById('hide-ignored').click();"); 
		      driver.findElement(By.id("dateFilter")).click();//Click on Date
			}
			if (scrapSubType.equals(SCRAP_TYPE_2)) { 
				driver.findElement(By.className("ant-picker-input-active")).click();//Click on Date
			}
		//driver.findElements(By.className("ant-picker-input")).get(0).click();
		
		//driver.findElement(By.id("dateSpan1")).click();
		Thread.sleep(5000); 
		//90 Days
		if (scrapSubType.equals(SCRAP_TYPE_1)) {
		String days=dto.getDays();
		if (days==null) days="Today";
		if (!days.equals("Today"))  days="Last "+ dto.getDays()+" Days";
		WebElement sBar=  driver.findElement(By.className("ranges"));
		List<WebElement> spans= sBar.findElements(By.tagName("li"));
		for(WebElement sp:spans) {
			if (sp.getText()!=null && sp.getText().equals(days)) {
				sp.click();
				break;
			}
			
		}
		}
		if (scrapSubType.equals(SCRAP_TYPE_2)) {
			String days=dto.getDays();
			if (days==null) days="Today";
			if (!days.equals("Today"))  days=dto.getDays()+" Days";
			WebElement sBar=  driver.findElement(By.className("ant-picker-ranges"));
			List<WebElement> spans= sBar.findElements(By.tagName("li"));
			for(WebElement sp:spans) {
				if (sp.getText()!=null && sp.getText().equals(days)) {
					sp.click();
					break;
				}
				
			}
			}
		//driver.findElement(By.xpath("//*[@id=\"layout-body--side-bar\"]/div[7]/div/div/div/div[2]/div[2]/ul/li[5]/span")).click();
		Thread.sleep(10000);

		 if (scrapSubType.equals(SCRAP_TYPE_2)) {
			 try {
			 driver.findElement(By.className("anticon-eye")).click();//Click on Eye
			 Thread.sleep(5000);
			 }catch(Exception n) {
				 
			 }
        }
		Thread.sleep(1000);
		boolean serviceDate=false;
		boolean treatingSignature=false;
		boolean lastUpdate=false;
		
		
		if (scrapSubType.equals(SCRAP_TYPE_1)) {
		WebElement tableHeader=  driver.findElement(By.id("datatable-claims")); 
		System.out.println(tableHeader.getText());
		if (tableHeader.getText().contains("Service Date")) {
			serviceDate =true;
		//	return ;//if header already There
		}
		if (tableHeader.getText().contains("Treating Signature")) {
			treatingSignature =true;
		//	return ;//if header already There
		 }
		if (tableHeader.getText().contains("Last Update")) {
			lastUpdate =true;
		//	return ;//if header already There
		 }
		}
		if (scrapSubType.equals(SCRAP_TYPE_2)) {
			WebElement tableHeader=  driver.findElement(By.className("ant-table-thead")); 
			System.out.println(tableHeader.getText());
			
			if (tableHeader.getText().contains("Service Date")) {
				serviceDate =true;
			//	return ;//if header already There
			}
			if (tableHeader.getText().contains("Treating Signature")) {
				treatingSignature =true;
			//	return ;//if header already There
			 }
			if (tableHeader.getText().contains("Last Update")) {
				lastUpdate =true;
			//	return ;//if header already There
			 }
		}
		
		if (scrapSubType.equals(SCRAP_TYPE_1)) {
			if (!treatingSignature || !serviceDate) {
			driver.findElement(By.id("columnDropdown")).click();
			Thread.sleep(5000);
			
			if (!serviceDate) {
				executor.executeScript("document.getElementById('dosCheckbox').click();");
			//driver.findElement(By.id("dosCheckbox")).click();//Service Date Click..
			Thread.sleep(5000);
			}
			if (!treatingSignature) {
				executor.executeScript("document.getElementById('treatinG_SIGNATURECheckbox').click();");
			//driver.findElement(By.id("dosCheckbox")).click();//Service Date Click..
			Thread.sleep(5000);
			}
			if (!lastUpdate) {
				executor.executeScript("document.getElementById('lasT_UPDATE_DATECheckbox').click();");
			//driver.findElement(By.id("dosCheckbox")).click();//Service Date Click..
			Thread.sleep(5000);
			}
			}
		}
		
		if (scrapSubType.equals(SCRAP_TYPE_2)) {
			if (!treatingSignature || !serviceDate || !lastUpdate) {
				driver.findElement(By.className("anticon-insert-row-right")).click();
				Thread.sleep(1000); 
			List<WebElement> leftPanel=driver.findElements(By.className("column-select-row"));
			for(WebElement le:leftPanel) {
				if (le.getText().contains("Treating Signature")) {
				WebElement but = le.findElement(By.tagName("button"));
				if (but.getAttribute("aria-checked").equals("false")) {
					but.click();
					break;
				}
				//System.out.println(but.getAttribute("aria-checked"));
				}
				
				
			  }
			leftPanel=driver.findElements(By.className("column-select-row"));
			for(WebElement le:leftPanel) {
				
				if (le.getText().contains("Service Date")) {
					WebElement but = le.findElement(By.tagName("button"));
					if (but.getAttribute("aria-checked").equals("false")) {
						but.click();
						break;
					}
					//System.out.println(but.getAttribute("aria-checked"));
				}
				
			  }
			
			leftPanel=driver.findElements(By.className("column-select-row"));
			for(WebElement le:leftPanel) {
				
				if (le.getText().contains("Last Update")) {
					WebElement but = le.findElement(By.tagName("button"));
					if (but.getAttribute("aria-checked").equals("false")) {
						but.click();
						break;
					}
					//System.out.println(but.getAttribute("aria-checked"));
				}
				
			  }
			
			}
		}
		/*
		sBar=  driver.findElement(By.className("ant-drawer-body"));//.findElement(By.className("ant-row"));
		List<WebElement> divs= sBar.findElements(By.className("ant-row"));
		
		for(WebElement sp:divs) {
			if (sp.getText()!=null && sp.getText().contains("Service Date")) {
				if (sp.findElement(By.className("ant-switch-inner")).getText().equals("No")) {
					sp.findElement(By.tagName("button")).click();
					break;
				}
				
			}
			
		}
		*/
		//driver.findElement(By.xpath("//*[@id=\"layout-body--side-bar\"]/div[3]/div/div[2]/div/div/div[2]/div/div[12]/div[3]/button")).click();//dos 
		Thread.sleep(1000);          //*[@id="layout-body--side-bar"]/div[3]/div/div[2]/div/div/div[2]/div/div[23]/div[2]/button
		//sBar=  driver.findElement(By.id("layout-body--side-bar"));
		//List<WebElement> buts= sBar.findElements(By.tagName("button"));
		//for(WebElement sp:buts) {
			//if (sp.getText()!=null && sp.getText().equals("Apply")) {
				//sp.click();
				//break;
		//	}
			
		//}
		//driver.findElement(By.xpath("//*[@id=\"layout-body--side-bar\"]/div[3]/div/div[2]/div/div/div[2]/div/div[23]/div[2]/button")).click();//Apply
		Thread.sleep(2000);
		//TABLE
		//select to records to display
		if (scrapSubType.equals(SCRAP_TYPE_1)) {
		Select select = new Select(driver.findElement(By.className("select-claim-count")));
		select.selectByVisibleText(PAGE_RECORD_1+"");
		select.selectByValue(PAGE_RECORD_1+"");
		Thread.sleep(5000);
		fetchTableData(driver,1,dList,SCRAP_TYPE_1);
		}
		if (scrapSubType.equals(SCRAP_TYPE_2)) {
			driver.findElement(By.className("ant-btn-primary")).click();
			Thread.sleep(5000);
			driver.findElement(By.className("ant-pagination-options-size-changer")).click();
		List<WebElement> vs=driver.findElements(By.className("ant-select-item-option-content"));
			for (WebElement v:vs) {
				System.out.println(v.getText());
				if (v.getText().contains(PAGE_RECORD_2+"")) {
					v.click();
					break;
				}
				
			}
			Thread.sleep(5000);	
			fetchTableData(driver,1,dList,SCRAP_TYPE_2);
		}
		System.out.println("***********REMOTE LITE********************");
		System.out.println("*******************************");
		System.out.println(dList.size());
		
		ConnectAndReadSheets.updateRemoteLiteScrapSheetGoogleSheet(dto.getGoogleSheetIdDb(),
				dto.getGoogleSubId(),
				this.CLIENT_SECRET_DIR, this.CREDENTIALS_FOLDER, null,"FINISH",2);
		}catch(Exception b) {
			try {
			ConnectAndReadSheets.updateRemoteLiteScrapSheetGoogleSheet(dto.getGoogleSheetIdDb(),
					dto.getGoogleSubId(),
					this.CLIENT_SECRET_DIR, this.CREDENTIALS_FOLDER, null,"SOME EEROR"+b.getMessage(),2);
			}catch(Exception n ) {
				
			}
		}
		
		//update Google Sheet..
		
		
		
		 
	}
	
	private void pagnation1(WebDriver driver,int pageNo,List<RemoteLiteData> dList) throws InterruptedException {
		System.out.println("PAGENO>>"+pageNo);         //*[@id=\"claim-history-root\"]/div/div[4]/div/div/ul
		//*[@id="datatable-claims_paginate"]/ul
		 WebElement pagUL=driver.findElement(By.xpath("//*[@id=\"datatable-claims_paginate\"]/ul"));
		 List<WebElement> lis= pagUL.findElements(By.tagName("a"));
		 boolean skipRow=true;
		 try {
		 for (WebElement li:lis) {
			   if (skipRow) {
				   skipRow=false;
				   continue;
			   }
			   System.out.println("FROM PAGE NO>>"+li.getText()+"<--");
			   String tx=li.getText().replace("..", "");
			   System.out.println("FROM PAGE NO REP>>"+tx+"<--");
			   int cp=-1;
			   try {
			   cp=Integer.valueOf(tx);
			   }catch(Exception n) {
				   n.printStackTrace();
			   }
			   //if (li.getText().equals(Integer.valueOf(pageNo).toString())) continue;
			   if (cp!=pageNo) continue;
			   try {
			   li.click();
			   }catch(Exception n) {
				   n.printStackTrace();
			   }
			   Thread.sleep(5000);
			   fetchTableData(driver,pageNo,dList,SCRAP_TYPE_1);
			 
		 }
		 }catch(Exception n) {
			 n.printStackTrace();
			 System.out.println("REMOTELITE ERROR");
		 }
		 
	}
	
	
	private void pagnation2(WebDriver driver,int pageNo,List<RemoteLiteData> dList) throws InterruptedException {
		System.out.println("PAGENO>>"+pageNo);         //*[@id=\"claim-history-root\"]/div/div[4]/div/div/ul
		//*[@id="datatable-claims_paginate"]/ul
		// WebElement pagUL=driver.findElements(By.className("ant-table-pagination-right"));
		
		 //boolean skipRow=true;
		 try {
			 
			  WebElement lis=driver.findElement(By.className("ant-pagination-item-"+pageNo));

               lis.click();
               Thread.sleep(5000);
			   fetchTableData(driver,pageNo,dList,SCRAP_TYPE_2);
		 
		 }catch(Exception n) {
			 n.printStackTrace();
			 System.out.println("REMOTELITE ERROR");
		 }
		 
	}
	
	private void fetchTableData(WebDriver driver,int pageNo,List<RemoteLiteData> dataList,String scrapSubType) throws InterruptedException{
		 WebElement tableBody=null;
		 WebElement tableBodyHead=null;
		 int[] position= {0,1,2,3,4,5,6,7,8,9,10};
		 if (scrapSubType.equals(SCRAP_TYPE_1)) {
			 tableBody= driver.findElement(By.xpath("//*[@id=\"datatable-claims\"]/tbody"));
		 }
		 if (scrapSubType.equals(SCRAP_TYPE_2)) {
			 tableBody= driver.findElement(By.className("ant-table-body"));
			 //Added Logic  for SCRAP_TYPE_2 as Position can be changed
			 tableBodyHead= driver.findElement(By.className("ant-table-thead"));
			 WebElement trs= tableBodyHead.findElement(By.tagName("tr"));
			 
				 List<WebElement> tds= trs.findElements(By.tagName("th"));
				 for(int index = 0; index < tds.size();index++) {
					   System.out.println("INDEX-->"+index+"-"+tds.get(index).getAttribute("innerText"));
					   if (tds.get(index).getAttribute("innerText").equals("Sent Date")) {
							position[2]=index;
						}
						if (tds.get(index).getAttribute("innerText").equals("Patient Name")) {
							position[3]=index;
						}
						if (tds.get(index).getAttribute("innerText").equals("Subscriber Name")) {
							position[4]=index;
						}
						if (tds.get(index).getAttribute("innerText").equals("Carrier")) {
							position[5]=index;
						}
						if (tds.get(index).getText().equals("Status")) {
							position[6]=index;
						}
						if (tds.get(index).getAttribute("innerText").equals("Status Description")) {
							position[7]=index;
						}
						if (tds.get(index).getAttribute("innerText").equals("Service Date")) {
							position[8]=index;
						}
						if (tds.get(index).getAttribute("innerText").equals("Treating Signature")) {
							position[9]=index;
						}
						if (tds.get(index).getAttribute("innerText").equals("Last Update")) {
							position[10]=index;
						}
					   
					}
			 
			 
		 }
		 
		
		 
		List<WebElement> trs= tableBody.findElements(By.tagName("tr"));
		//RemoteLiteData data=null;
		 boolean skipRow=true;
		 for (WebElement tr:trs) {
			   if (skipRow && scrapSubType.equals(SCRAP_TYPE_2)) {
				 
				   skipRow=false;
				   continue;
			   }
			   String hiddenClaims="False";
			   String className= tr.getAttribute("class");
			   if (className!=null && (className.contains("row-ignore") || className.contains("ignored-row")) ) {
				   hiddenClaims="True";
			   }
			   List<WebElement> tds= tr.findElements(By.tagName("td"));
			   if (tds!=null) {
				   RemoteLiteData data=new RemoteLiteData();
				   data.setHiddenClaims(hiddenClaims);
				   
				   data.setSendDate(tds.get(position[2]).getText());
				   data.setName(tds.get(position[3]).getText());
				   data.setSubscriberName(tds.get(position[4]).getText());
				   data.setCarrier(tds.get(position[5]).getText());
				   data.setStatus(tds.get(position[6]).getText());
				   data.setDescription(tds.get(position[7]).getText());
				   data.setServiceDate(tds.get(position[8]).getText());
				   if (scrapSubType.equals(SCRAP_TYPE_2)) {
				   data.setTreatingSignature(tds.get(position[9]).getText());
				   data.setLastUpdate(tds.get(position[10]).getText());
				   }else {
					   data.setTreatingSignature(tds.get(position[10]).getText());
					   data.setLastUpdate(tds.get(position[9]).getText());
				   }
				   /*IntStream.range(0, tds.size())
					.forEach(index -> {
						System.out.println(tds.get(index).getText());
					//	if (data==null) data= new RemoteLiteData();
						if (index==2) {
							data.setSendDate(tds.get(index).getText());
							System.out.println("Date-->"+tds.get(index).getText());
						}
						if (index==3) {
							data.setName(tds.get(index).getText());
							System.out.println("Name-->"+tds.get(index).getText());
						}
						if (index==4) {
							data.setSubscriberName(tds.get(index).getText());
							System.out.println("Sus.-->"+tds.get(index).getText());
						}
						if (index==5) {
							data.setCarrier(tds.get(index).getText());
							System.out.println("Carr-->"+tds.get(index).getText());
						}
						if (index==6) {
							data.setStatus(tds.get(index).getText());
							System.out.println("STA-->"+tds.get(index).getText());
						}
						if (index==7) {
							data.setDescription(tds.get(index).getText());
							System.out.println("Des-->"+tds.get(index).getText());
						}
						if (index==8) {
							data.setServiceDate(tds.get(index).getText());
							System.out.println("Date SOS-->"+tds.get(index).getText());
						}
						if (index==9) {
							data.setTreatingSignature(tds.get(index).getText());
							System.out.println("Date TS-->"+tds.get(index).getText());
						}
						if (index==10) {
							data.setLastUpdate(tds.get(index).getText());
							System.out.println("last Update-->"+tds.get(index).getText());
						}
						  
					}
					);*/
			    //for (WebElement td:tds) {
				 //  System.out.println("-->"+td.getText());
			    //}
				   //data.setProcessedDate(driver.findElements(By.className("ant-picker-input")).get(0).findElement(By.tagName("input")).getAttribute("value"));
				  if (scrapSubType.equals(SCRAP_TYPE_1)) {
					  data.setProcessedDate(driver.findElement(By.id("dateFilter")).getText());
				  }
				  if (scrapSubType.equals(SCRAP_TYPE_2)) {
					  
					 List<WebElement> inps=driver.findElements(By.tagName("input"));
					 for(WebElement inp:inps) {
						if (inp.getAttribute("placeholder")!=null) {
							if (inp.getAttribute("placeholder").equals("Start date")) {
								data.setProcessedDate(inp.getAttribute("value"));
							}
							if (inp.getAttribute("placeholder").equals("End date")) {
								data.setProcessedDate(data.getProcessedDate()+"-"+inp.getAttribute("value"));
								break;
							}
						}
					 }
					 
				  }
				   //data.setProcessedDate(data.getProcessedDate()+"-"+ driver.findElements(By.className("ant-picker-input")).get(1).findElement(By.tagName("input")).getAttribute("value"));
				   dataList.add(data);
			   }
			   
			 
		 }
		 
		 try {
			 System.out.println("updateRemoteLiteScrapSheetGoogleSheet for page"+pageNo);
			 //System.out.println("updateRemoteLiteScrapSheetGoogleSheet ==>"+(((pageNo-1)*PAGE_RECORD)+2));
		//make sure sheet has rows more than the no of records	 
			 System.out.println("----->"+dataList.size());
			 if (scrapSubType.equals(SCRAP_TYPE_1))ConnectAndReadSheets.updateRemoteLiteScrapSheetGoogleSheet(dto.getGoogleSheetIdDb(),
					dto.getGoogleSubId(),
					this.CLIENT_SECRET_DIR, this.CREDENTIALS_FOLDER, dataList,"Progress PAGE -"+pageNo,pageNo==1?2:((pageNo-1)*PAGE_RECORD_1)+2);
		 else ConnectAndReadSheets.updateRemoteLiteScrapSheetGoogleSheet(dto.getGoogleSheetIdDb(),
					dto.getGoogleSubId(),
					this.CLIENT_SECRET_DIR, this.CREDENTIALS_FOLDER, dataList,"Progress PAGE -"+pageNo,pageNo==1?2:((pageNo-1)*PAGE_RECORD_2)+2);
		 dataList.clear();
		 System.out.println("-----<"+dataList.size());
		 }catch(Exception m) {
			 
		 }
		 if (scrapSubType.equals(SCRAP_TYPE_1)) {
			 pagnation1(driver,++pageNo,dataList);
	     }
		 if (scrapSubType.equals(SCRAP_TYPE_2)) {
			 pagnation2(driver,++pageNo,dataList);
	     }
		 
	}
	
	public static void main(String... a) {
		
		ScrappingSiteDetailsFull f = new ScrappingSiteDetailsFull();
		ScrappingSiteFull fu = new ScrappingSiteFull();
		fu.setSiteName("Remote lite");
		f.setScrappingSite(fu);
		f.setProxyPort("9500");
		f.setGoogleSheetId("1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw");
		f.setScrapSubType("2");
		ScrappingFullDataDetailDto dto = new ScrappingFullDataDetailDto();
		dto.setSiteUrl("https://rpractice.com");
		dto.setPassword("Remotelite@123");//Aransas.credentialing@smilepoint.us
		dto.setUserName("billing@smilepoint.us");//jasper.credentialing@smilepoint.us
		dto.setSiteName("Remote Lite");
		dto.setDays("7");
		RemoteLiteImpl i = new RemoteLiteImpl(null, null, f, dto, null, null, 0, "",
				null, "D:/Project/Tricon/linkedinapp/linkedinbit/linkedinapp/lib/chromedriver.exe",
				"1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw","0");
		i.CLIENT_SECRET_DIR="E:/Project/Tricon/files/client_secret.json";
		i.CREDENTIALS_FOLDER="E:/Project/Tricon/files";
		i.setProps("9500");
		//i.scrapSite(f, dto, null, null);
		List<RemoteLiteData> dList= new ArrayList<>();
		RemoteLiteData v= new RemoteLiteData();
		v.setCarrier("dd");
		dList.add(v);
		i.scrapSite(f, dto, null, null);
		try {
//		ConnectAndReadSheets.updateRemoteLiteScrapSheetGoogleSheet(i.scrappingSiteDetails.getGoogleSheetId(),
//				i.scrappingSiteDetails.getGoogleSubId(),
//				i.CLIENT_SECRET_DIR, i.CREDENTIALS_FOLDER, dList,"FINISH");
		}catch(Exception o) {
			o.printStackTrace();
		}
		
	}

}
