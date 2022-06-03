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
	
	private static final int PAGE_RECORD=50;
	private static final int PAGE_RECORD_INDEX=2;

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
				By.xpath("//*[@id=\"loginForm\"]/div[3]/div[3]/button"));

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

	private boolean navigatetoMainSite(WebDriver driver, boolean navigate)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		if (!navigate)
			return navigate;
		driver.get("https://rpractice.com/Rlo");
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

								boolean issueNo = navigatetoMainSite(driver, navigate);
								//System.out.println("888888888888- STARTED...");
								parsePage(driver, siteName, issueNo, office);
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
			Office office) throws InterruptedException {
		
		Thread.sleep(5000);
		try {
		driver.findElement(By.id("rc-tabs-0-tab-Sent")).click();
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
			executor.executeScript("document.getElementById('hide-ignored').click();"); 
			/*
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(
			ExpectedConditions.visibilityOfElementLocated(By.id("hide-ignored")));
			driver.findElement(By.id("hide-ignored")).click();
			*/
			
		//WebElement showHiddenClaims=driver.findElement(By.id("hide-ignored"));
		//if (showHiddenClaims.getAttribute("checked")==null) showHiddenClaims.click();
		
		
		//driver.findElement(By.xpath("//*[@id=\"claim-history-root\"]/div/div[3]/div[3]/span/span")).click();
		//Thread.sleep(5000);
		driver.findElement(By.id("dateFilter")).click();//Click on Date
		//driver.findElements(By.className("ant-picker-input")).get(0).click();
		
		//driver.findElement(By.id("dateSpan1")).click();
		Thread.sleep(5000); 
		//90 Days
		
		WebElement sBar=  driver.findElement(By.className("ranges"));
		List<WebElement> spans= sBar.findElements(By.tagName("li"));
		for(WebElement sp:spans) {
			if (sp.getText()!=null && sp.getText().equals("Last 90 Days")) {
				sp.click();
				break;
			}
			
		}
		//driver.findElement(By.xpath("//*[@id=\"layout-body--side-bar\"]/div[7]/div/div/div/div[2]/div[2]/ul/li[5]/span")).click();
		Thread.sleep(10000);
		//driver.findElement(By.id("hide-ignored")).click();
		//Thread.sleep(1000);        anticon-insert-row-right   
		//driver.findElement(By.className("anticon-insert-row-right")).click();//Column
		//driver.findElement(By.xpath("//*[@id=\"claim-history-root\"]/div/div[3]/div[7]/span")).click();//Column
		Thread.sleep(1000);
		
		WebElement tableHeader=  driver.findElement(By.id("datatable-claims")); 
		System.out.println(tableHeader.getText());
		if (tableHeader.getText().contains("Service Date")) return ;//if header already There
		
		driver.findElement(By.id("columnDropdown")).click();
		Thread.sleep(5000);
		executor.executeScript("document.getElementById('dosCheckbox').click();");
		//driver.findElement(By.id("dosCheckbox")).click();//Service Date Click..
		Thread.sleep(5000);
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
		Select fruits = new Select(driver.findElement(By.className("select-claim-count")));
		fruits.selectByVisibleText(PAGE_RECORD+"");
		fruits.selectByValue(PAGE_RECORD+"");
		Thread.sleep(5000);
		fetchTableData(driver,1,dList);
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
	
	private void pagnation(WebDriver driver,int pageNo,List<RemoteLiteData> dList) throws InterruptedException {
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
			   fetchTableData(driver,pageNo,dList);
			 
		 }
		 }catch(Exception n) {
			 n.printStackTrace();
			 System.out.println("REMOTELITE ERROR");
		 }
		 
	}
	
	private void fetchTableData(WebDriver driver,int pageNo,List<RemoteLiteData> dataList) throws InterruptedException{
		 WebElement tableBody=driver.findElement(By.xpath("//*[@id=\"datatable-claims\"]/tbody"));
			
		List<WebElement> trs= tableBody.findElements(By.tagName("tr"));
		//RemoteLiteData data=null;
		 boolean skipRow=true;
		 for (WebElement tr:trs) {
			   if (skipRow) {
				   //skipRow=false;
				   //continue;
			   }
			   
			   List<WebElement> tds= tr.findElements(By.tagName("td"));
			   if (tds!=null) {
				   RemoteLiteData data=new RemoteLiteData();
				   IntStream.range(0, tds.size())
					.forEach(index -> {
						
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
							System.out.println("STA-->"+tds.get(index).getText());
						}
						if (index==8) {
							data.setServiceDate(tds.get(index).getText());
							System.out.println("Date SOS-->"+tds.get(index).getText());
						}
						  
					}
					);
			    //for (WebElement td:tds) {
				 //  System.out.println("-->"+td.getText());
			    //}
				   //data.setProcessedDate(driver.findElements(By.className("ant-picker-input")).get(0).findElement(By.tagName("input")).getAttribute("value"));
				   data.setProcessedDate(driver.findElement(By.id("dateFilter")).getText());
				   //data.setProcessedDate(data.getProcessedDate()+"-"+ driver.findElements(By.className("ant-picker-input")).get(1).findElement(By.tagName("input")).getAttribute("value"));
				   dataList.add(data);
			   }
			   
			 
		 }
		 try {
			 System.out.println("updateRemoteLiteScrapSheetGoogleSheet for page"+pageNo);
			 System.out.println("updateRemoteLiteScrapSheetGoogleSheet ==>"+(((pageNo-1)*PAGE_RECORD)+2));
		//make sure sheet has rows more than the no of records	 
		 ConnectAndReadSheets.updateRemoteLiteScrapSheetGoogleSheet(dto.getGoogleSheetIdDb(),
					dto.getGoogleSubId(),
					this.CLIENT_SECRET_DIR, this.CREDENTIALS_FOLDER, dataList,"Progress PAGE -"+pageNo,pageNo==1?2:((pageNo-1)*PAGE_RECORD)+2);
		 dataList.clear();
		 }catch(Exception m) {
			 
		 }
		 pagnation(driver,++pageNo,dataList);
	}
	
	public static void main(String... a) {
		
		ScrappingSiteDetailsFull f = new ScrappingSiteDetailsFull();
		ScrappingSiteFull fu = new ScrappingSiteFull();
		fu.setSiteName("Remote lite");
		f.setScrappingSite(fu);
		f.setProxyPort("9500");
		f.setGoogleSheetId("1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw");
		
		ScrappingFullDataDetailDto dto = new ScrappingFullDataDetailDto();
		dto.setSiteUrl("https://rpractice.com");
		dto.setPassword("Remotelite@1234");//Aransas.credentialing@smilepoint.us
		dto.setUserName("jasper.credentialing@smilepoint.us");//jasper.credentialing@smilepoint.us
		dto.setSiteName("Remote Lite");
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
