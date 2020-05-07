package com.tricon.ruleengine.service.scrapfull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dto.PatientScrapSearchDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.scrapping.EligibilityDto;
import com.tricon.ruleengine.dto.scrapping.FullWebsiteScrapDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.PatientDetailTemp;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.sheet.FullWebsiteDataParsingSheet;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;
import com.tricon.ruleengine.service.impl.BaseScrappingServiceImpl;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.ConstantsScrapping;
import com.tricon.ruleengine.utils.DateUtils;

public class DeltaDentalServiceImpl extends BaseScrappingServiceImpl implements  Callable<Boolean>{
	
	static {
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		
	}
	
	private ScrappingSiteDetailsFull scrappingSiteDetails=null;
	private ScrappingFullDataDetailDto dto=null;
	//private String CLIENT_SECRET_DIR,CREDENTIALS_FOLDER;
	//private Map<String, List<Object>> mapData=null;
	//private boolean updateSheet=false;
	private Office office;
	private PatientDao dao;
	private User user;
	
	
	

	public ScrappingSiteDetailsFull getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}


	public void setScrappingFullDataDetailDto(ScrappingFullDataDetailDto dto) {
		this.dto = dto;
	}
	
	public void setScrappingSiteDetails(ScrappingSiteDetailsFull scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}

	public DeltaDentalServiceImpl(PatientDao dao,ScrappingSiteDetailsFull scrappingSiteDetails,ScrappingFullDataDetailDto dto,
			User user,Office office) {
		this.scrappingSiteDetails=scrappingSiteDetails;
		//this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		//this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		//this.mapData=mapData;
		//this.updateSheet=updateSheet;
		this.dto = dto;
		this.office = office;
		this.dao=dao;
		this.user=user;
	       // store parameter for later user
	   }

	@Override
	public Boolean call() throws Exception {
		scrapSite(scrappingSiteDetails,dto);
		return true;
	}
	
	private List<PatientTemp> scrapSite(ScrappingSiteDetailsFull scrappingSiteDetail
			,ScrappingFullDataDetailDto dto){
		//java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
		//java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF); 
		//java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(Level.OFF);
		//java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF);
		//System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		//WebDriver driver = new HtmlUnitDriver(true);
		WebClient driver = new WebClient();
		driver.getOptions().setJavaScriptEnabled(true);
		driver.getOptions().setCssEnabled(false);
		driver.getOptions().setRedirectEnabled(true);
		driver.getOptions().setThrowExceptionOnScriptError(false);
	// TODO Auto-generated method stub
		setProps(scrappingSiteDetails.getProxyPort());
		//List<PatientTemp> eList = new ArrayList<>();
		try {
			loginToSiteDeltaDental(dto, driver);

			for (PatientScrapSearchDto data : dto.getDto()) {
				boolean issueNo =navigatetoMainSite(driver);
					PatientTemp d=	parsePage(driver,data,scrappingSiteDetail.getScrappingSite().getSiteName(),issueNo);
					if (d!=null) {
						//eList.add(d);
						//Update the Data in Database
						updateDatainDB(d);
						//dao.checkforPatientWithIdAndOfficeTemp(d.getPatientId(), office);
						/*if (updateSheet) {
								try {
									List<PatientTemp> rListC = new ArrayList<>();
									rListC.add(d);
									ConnectAndReadSheets.updateFullScrapSheet(scrappingSiteDetails.getGoogleSheetId(),
											  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,rListC,ConstantsScrapping.NO_WRITE);
								
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//size=size+rList.size();
								
						}*/
						
						
						
					}
				
			}
			// scrappingSiteDetails.get
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			driver.close();
			}catch (Exception e) {
				// TODO: handle exception
			}
		return null;
	}
	
	private PatientTemp parsePage(WebClient driver, PatientScrapSearchDto sh, String webSiteName,boolean issueNo) {
		PatientTemp temp= new PatientTemp();
		temp.setDob(sh.getDob());
		temp.setPatientId(DateUtils.createPatientIdByDate(sh.getPatientId()));
		temp.setFirstName(sh.getFirstName());
		temp.setWebsiteName(webSiteName);
		temp.setLastName(sh.getLastName());
		temp.setOffice(this.office);
		temp.setStatus("Parsing Start");
		if (!issueNo) {
			
			temp.setStatus("Check Password.");
		}
		PatientDetailTemp t= new PatientDetailTemp();
		t.setPatient(temp);
		t.setOffice(office);
		Set<PatientDetailTemp> s= new HashSet<>();
		s.add(t);
		temp.setPatientDetails(s);
		/////Logic to fetch data from Site...
		
		return temp;
	}
	
	
	
	private void updateDatainDB(PatientTemp data) throws Exception {
		
		dao.savePatientTempDataWithDetailsAndHistory(data, this.office, this.user);		
	}
	/*
	private boolean searchPatient(WebClient driver, String firstName,String lastName) {
		
		return true;
		
	}*/
	private boolean navigatetoMainSite(WebClient driver) throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		//driver.navigate().to("https://www.deltadentalins.com/providertools/faces/Main.jspx?_afrLoop=17372642215991282&_afrWindowMode=0&_afrWindowId=null&_adf.ctrl-state=idxrbvpo8_1");
		HtmlPage page =driver.getPage("https://www.deltadentalins.com/providertools/faces/Main.jspx?_afrLoop=16744899782812514&_afrWindowMode=0&_afrWindowId=bt36yf8zt&_adf.ctrl-state=12przn28ii_219");
		Thread.sleep(5000);
		System.out.println(page.getVisibleText());
		
		//DomElement element3 = page.getElementById("myToolsTable2");
		List<HtmlAnchor> ch = page.getByXPath("//a[@class='x22c xfg p_AFIconOnly']");
		//System.out.println(driver.getTitle());
		System.out.println(")))))))))))))))))))))))))))))))");
		System.out.println(ch);
		try {
		ch.get(1).click();
		}catch (Exception e) {
			return false;
			// TODO: handle exception
		}
		Thread.sleep(1000);
		/*
		HtmlInput i= page.getElementByName("template1:r1:1:r1:0:it2::content");//p_AFHoverTarget x28o x1u
		i.setValueAttribute("Kimberly crow");
		
		System.out.println(ch.size());
		//"template1:r1:1:r1:0:commandButton1"//Search Button..
		System.out.println(")))))))))))))))))))))))))))))))");
		DomElement b = page.getElementById("template1:r1:0:cl2");
		b.click();
		//System.out.println(driver.getPageSource());
		
		//List<WebElement> ee=driver.findElements(By.className("myToolsTable2"));
		int x=0;
		if (x==0) {
			int j=1;
		}
		*/
		return true;
	}
	/**
	 * Link:  https://www.deltadentalins.com/
              Username:  Crosbyfamilydental
              Password:  Crosby000
              Kimberly crow
115296917101
	 * @param main
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	
	public static void main(String[] main) throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.openqa.selenium.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		
		DeltaDentalServiceImpl i= new DeltaDentalServiceImpl(null,null,null,null,null);
		System.out.println("ssda");
		WebClient driver = new WebClient();
		driver.getOptions().setJavaScriptEnabled(true);
		driver.getOptions().setCssEnabled(false);
		driver.getOptions().setRedirectEnabled(true);
		driver.getOptions().setThrowExceptionOnScriptError(false);
		//webClient.setThrowExceptionOnFailingStatusCode(false);
		//webClient.setTimeout(20000);
		//HtmlUnitDriver driver = new HtmlUnitDriver(true);
		//driver.setJavascriptEnabled(true);
		// TODO Auto-generated method stub
		i.setProps("9500");
		ScrappingSiteDetails d= new ScrappingSiteDetails();
		d.setGoogleSheetId("");
		ScrappingFullDataDetailDto dto= new ScrappingFullDataDetailDto();
		dto.setPassword("Crosby000");
		dto.setSiteUrl("https://www.deltadentalins.com");
		dto.setUserName("Crosbyfamilydental1");		
		BaseScrappingServiceImpl vv= new BaseScrappingServiceImpl();
		i.loginToSiteDeltaDental(dto, driver);
		/*
		d.setGoogleSheetName(googleSheetName);
		d.setOffice(office);

		i.scrapSite(d, mapData);
		*/
	}
	
	public void loginToSiteDeltaDental(ScrappingFullDataDetailDto dto, WebClient driver) throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {

		//driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		
		//driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		HtmlPage page =driver.getPage(dto.getSiteUrl());
		System.out.println("-----------SSSSSSS--------------------");
       System.out.println(page.getVisibleText());
       System.out.println("------------SSSSSS-------------------");
      DomElement element3 = page.getElementById("loginButton");
		DomElement element = page.getElementById("username");
		element.setAttribute("value",dto.getUserName());
		/*WebClient client=null;
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setPrintContentOnFailingStatusCode(false);*/
		DomElement element2 = page.getElementById("password");
		element2.setAttribute("value",dto.getPassword());
		element3.click();
		Thread.sleep(8000);
		System.out.println("-------------------------------");

		//System.out.println(page.getVisibleText());
		System.out.println("-------------------------------");
		navigatetoMainSite(driver);

	}


}
