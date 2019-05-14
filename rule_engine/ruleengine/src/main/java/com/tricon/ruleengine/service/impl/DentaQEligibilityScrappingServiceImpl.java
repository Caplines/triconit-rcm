package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import com.tricon.ruleengine.dto.scrapping.EligibilityDto;
import com.tricon.ruleengine.dto.scrapping.HistoryDto;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.ConstantsScrapping;

public class DentaQEligibilityScrappingServiceImpl extends BaseScrappingServiceImpl implements  Callable<List<?>>	 {
	
	private ScrappingSiteDetails scrappingSiteDetails=null;
	private String CLIENT_SECRET_DIR,CREDENTIALS_FOLDER;
	private Map<String, List<Object>> mapData=null;
	private boolean updateSheet=false;
	private final int  max=5;
	private int  ctALL=0;

	public ScrappingSiteDetails getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}


	public void setScrappingSiteDetails(ScrappingSiteDetails scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}


	DentaQEligibilityScrappingServiceImpl(ScrappingSiteDetails scrappingSiteDetails,
    		String	CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER,Map<String, List<Object>> mapData,boolean updateSheet) {
		this.scrappingSiteDetails=scrappingSiteDetails;
		this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		this.mapData=mapData;
		this.updateSheet=updateSheet;
	       // store parameter for later user
	   }
	
	
	@Override
    public List<EligibilityDto> call() {
		List<EligibilityDto> r=scrapSite(scrappingSiteDetails,mapData);
		  try {
			 if (updateSheet) ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
					  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<EligibilityDto>)r,scrappingSiteDetails.getRowCount(),"NO","D");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			return r;
    }
	public List<EligibilityDto> scrapSite(ScrappingSiteDetails scrappingSiteDetails,
			Map<String, List<Object>> mapData) {
		WebDriver driver = new HtmlUnitDriver(true);
		// TODO Auto-generated method stub
		setProps(scrappingSiteDetails.getProxyPort());
		List<EligibilityDto> eList = new ArrayList<>();
		try {
			loginToSiteDenta(scrappingSiteDetails, driver);

			for (Map.Entry<String, List<Object>> entry : mapData.entrySet()) {
				List<Object> x = (List<Object>) entry.getValue();
				for (Object obj : x) {
					MCNADentaSheet sh = (MCNADentaSheet) obj;
					EligibilityDto d=	parsePage(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(), sh.getfName(), sh.getZip(),scrappingSiteDetails.getLocationProvider(),true);
					if (d!=null && d.getMessage().equals(ConstantsScrapping.SUBSCRIBER_NOT_FOUND)) {
						d=	parsePage(driver, sh.getDob(), sh.getSubscriberId(), sh.getlName(), sh.getfName(), sh.getZip(),scrappingSiteDetails.getLocationProvider(),false);
					}
					if (d!=null) {
						d.setMcnaSheet(sh);
						eList.add(d);
						
						
						if (updateSheet) {
							if (eList.size()>=max) {
								try {
									List<EligibilityDto> rListC = new ArrayList<>(eList);
									//int q=1+(max*ctALL);
									//if (q>1) q=q+1+ctALLO;
									//ctALLO=ctALLO+1;
									
									ConnectAndReadSheets.updateSheetMCNADenta(scrappingSiteDetails.getGoogleSheetId(),
											  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<EligibilityDto>)rListC,scrappingSiteDetails.getRowCount(),"YES","D");

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//size=size+rList.size();
								ctALL=ctALL+1;
								eList.clear();
							}else {
							 //size=size+1;
							 //finalSize=size;
							}	
						}
						
						
					}
				}
			}
			// scrappingSiteDetails.get
		} catch (Exception e) {
			// TODO: handle exception
		}
		return eList;

	}
	
	private void navigatetoEligiblity(WebDriver driver) throws InterruptedException {
		driver.navigate().to(
				"https://govservices.dentaquest.com/Router.jsp?source=SearchMember&component=Members&code=MEMBER_ELIG_SEARCH&targetLink=true");
		Thread.sleep(8000);
	}
	
	
	private EligibilityDto parsePage(WebDriver driver,String dob,String subscriberId,
			String verifyLastName,String verifyFirstName,String zip,String locationProvider,boolean checkSub) throws Exception{
		navigatetoEligiblity(driver);
		EligibilityDto dto= new EligibilityDto();
		String[] dobA=dob.split("/");
		// Select select box with any option here we have selected last one.
		List<WebElement> wList = driver
				.findElements(By.xpath("/html/body/table[3]/tbody/tr/td[3]/form/div[2]/select/option"));
		
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
		element3.sendKeys(dobA[0]+"/"+dobA[1]+"/"+dobA[2]);

		element3 = driver.findElement(By.id("Q061MEMBER0memberNo"));
		element3.clear();
		
		if (checkSub &&  (!subscriberId.equalsIgnoreCase("NA")) && !subscriberId.trim().equals("")) {
			element3.sendKeys(subscriberId);
		}
		
		element3 = driver.findElement(By.id("Q061MEMBER0lastName"));
		element3.clear();
		if (!subscriberId.equalsIgnoreCase("NA")) {
 		element3.sendKeys(verifyLastName);
		}
		
		element3 = driver.findElement(By.id("Q061MEMBER0firstName"));
		element3.clear();
		
		if (!subscriberId.equalsIgnoreCase("NA")) {
		element3.sendKeys(verifyFirstName);
		}
		
		driver.findElement(By.name("Search")).click();
		Thread.sleep(10000);
		//Fetch Data
		wList=driver.findElements(By.className("results"));
		boolean eligble =false;
		if (wList.size()>0) {
			//Active
			List<WebElement> wListChild=wList.get(0).findElements(By.tagName("tr"));
			int x=0;
			//boolean intervention=false;
			//System.out.println(driver.getPageSource());
			int interventionCount=0;
			for(WebElement child:wListChild) {
				if (x==0) {	x++;
			if (child.getText().contains(" Intervention ")) {
				//intervention=true;
				interventionCount=interventionCount + 1;
			}
				continue;
				}
				if (child.getText().equals("No Results Found")) {
					break;
				}else {
					eligble=true;
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_FOUND);
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_Eligible);
					List<WebElement> wListChildTD=child.findElements(By.tagName("td"));
					//if (wListChildTD.size()>0)System.out.println(wListChildTD.get(0).getText());
					if (wListChildTD.size()>1) {
						//dto.setS
						//System.out.println("Service Date-"+wListChildTD.get(1).getText());
					}
					if (wListChildTD.size()>2) {
						//System.out.println("Subscriber-"+wListChildTD.get(2).getText());
						//dto.setS
					}
					if (wListChildTD.size()>3) {
						//System.out.println("DOB-"+wListChildTD.get(3).getText());
					}
					if (wListChildTD.size()>(4+interventionCount)) {
						//System.out.println("Name-"+wListChildTD.get(4+interventionCount).getText());
					}
					if (wListChildTD.size()>(7+interventionCount)) {
						dto.setEmployerName(wListChildTD.get(7+interventionCount).getText());
						//System.out.println("Plan -"+wListChildTD.get(7+interventionCount).getText());
					}
					if (wListChildTD.size()>(13+interventionCount)) {
						//System.out.println("Provider Name.-"+wListChildTD.get(13+interventionCount).getText());
						dto.setProviderName(wListChildTD.get(13+interventionCount).getText());
					}
					
					if (wListChildTD.size()>4) {
						//click on  Name:
						wListChildTD.get(4).findElement(By.tagName("a")).click();
						Thread.sleep(5000);
						//History click
					   driver.findElement(By.xpath("/html/body/table[3]/tbody/tr/td[3]/form/div[3]/table[3]/tbody/tr/td/a[3]")).click();;
					   Thread.sleep(5000);
						//parse History
					   List<WebElement> wListChildTDHis=driver.findElements(By.xpath("/html/body/table[3]/tbody/tr/td[3]/form/div[2]/table[4]/tbody/tr"));
					   for(WebElement ele:wListChildTDHis) {
						HistoryDto hd= new HistoryDto();
						List<WebElement> eL=   ele.findElements(By.tagName("td"));
					   if (eL.size()>0) {
						   hd.setCode(eL.get(0).getText());
						   
					   }
					   if (eL.size()>1) {
						   hd.setDescription(eL.get(1).getText());
					   }
					   if (eL.size()>2) {
						   hd.setTooth(eL.get(2).getText());
					   }
					   if (eL.size()>4) {
						   hd.setDos(eL.get(4).getText());
					   }
					   
					   dto.getHistoryList().add(hd);
					   }
					   driver.navigate().to("https://govservices.dentaquest.com/Router.jsp?source=MemberDetail&component=MemberDetails&breadcrumb=true");
					   Thread.sleep(5000);
					   //Click on view Benefit max..
					   driver.findElement(By.xpath("/html/body/table[3]/tbody/tr/td[3]/form/div[3]/table[3]/tbody/tr/td/a[1]")).click();
					   Thread.sleep(5000);
					   //copay and Remaining Benefit.
					   if (driver.findElement(By.xpath("/html/body/table[3]/tbody/tr/td[3]/form/table[8]/tbody/tr"))!=null) {
						   
						   String cp[]= driver.findElement(By.xpath("/html/body/table[3]/tbody/tr/td[3]/form/table[8]/tbody/tr")).getText().
								   replace("\n", "").replace("\r","").split("Benefit: ");
						  if (cp.length>1 && cp[1].contains("Co-pay")) {
							  cp=cp[1].split(" Co-pay");
							  cp=cp[0].split("\\$");
							  if (cp.length>1) {
								  dto.setCopay("$"+cp[1]);
								  	  
							  }
						  }else if (cp.length>1 && cp[1].contains(" Copay")) {
					    	  cp=cp[1].split(" Copay");
							  cp=cp[0].split("\\$");
							  if (cp.length>1) {
								  dto.setCopay("$"+cp[1]);
								  	  
							  }
						  }
					   }
					  
					   wListChildTD=driver.findElements(By.xpath("/html/body/table[3]/tbody/tr/td[3]/form/table[9]/tbody/tr"));
					   for(WebElement ele:wListChildTD) {
						   if (ele.getText().equalsIgnoreCase("No Results Found")) {
							  // System.out.println("Remaining benefits not found....");
						   }else {
							  List<WebElement> wListChildTDD=   ele.findElements(By.tagName("td"));
							  if (wListChildTDD.size()>8) {
								//  System.out.println("Remaining ----"+ wListChildTDD.get(8).getText());
								  dto.setBenefitRemaining(wListChildTDD.get(8).getText());
							  }
						   }
					   }
					   
					}
					//Parse sub data
				}
			}
		}
		if (wList.size()>1 && !eligble) {
			//Ineligible
			List<WebElement> wListChild=wList.get(1).findElements(By.tagName("tr"));
			int x=0;
			for(WebElement child:wListChild) {
				if (x==0) {
					x++;
					continue;
				}
				if (child.getText().equals("No Results Found")) {
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
					//System.out.println(child.getText()+" ( Ineligible)");
						
					break;
				}else {
					//System.out.println("PAt is Ineligible");
					dto.setEligible(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_Eligible);
					eligble=true;
				}
			}
		}
		if (wList.size()>2 && !eligble) {
			//not found
			List<WebElement> wListChild=wList.get(2).findElements(By.tagName("tr"));
			int x=0;
			for(WebElement child:wListChild) {
				if (x==0) {
					x++;
					continue;
				}
				if (child.getText().equals("No Results Found Ineligible")) {
					//System.out.println(child.getText());
						
					break;
				}else {
					//System.out.println("Pat not found ....");
					dto.setMessage(ConstantsScrapping.SUBSCRIBER_NOT_FOUND);
				}
			}
		}

		return dto;
	}
	

}
