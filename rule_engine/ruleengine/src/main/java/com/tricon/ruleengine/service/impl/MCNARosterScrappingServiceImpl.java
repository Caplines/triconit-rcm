package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.tricon.ruleengine.dto.ScrappingInputDto;
import com.tricon.ruleengine.dto.scrapping.RosterDetails;
import com.tricon.ruleengine.model.db.PatientTemp;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.ConstantsScrapping;

/**
 * 
 * @author Deepak.Dogra
 *
 */

public class MCNARosterScrappingServiceImpl  extends BaseScrappingServiceImpl implements  Callable<List<?>>{
	
	private ScrappingSiteDetails scrappingSiteDetails=null;
	private String CLIENT_SECRET_DIR,CREDENTIALS_FOLDER;
	private ScrappingInputDto dto;
	//private int finalSize=0;
	//private int size=0;
	private final int  max=20;
	private int  ctALL=0;
	//HtmlUnitDriver driver = null;
	WebDriver driver = null;
	//private int  ctALLO=0;
		
	
	

	public ScrappingSiteDetails getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}


	public void setScrappingSiteDetails(ScrappingSiteDetails scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}


    MCNARosterScrappingServiceImpl(ScrappingSiteDetails scrappingSiteDetails,
    		String	CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER,ScrappingInputDto dto,String driverLocation) {
		this.scrappingSiteDetails=scrappingSiteDetails;
		this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		this.dto=dto;
		this.driverLocation=driverLocation;
	       // store parameter for later user
	   }
	
	
	@Override
    public List<RosterDetails> call() {
		List<RosterDetails> r=null;
		  try {
			r=scrapSite(scrappingSiteDetails,dto);
			int q=1+(max*ctALL);
			//if (q>1) q=q+1+ctALLO;
			//ctALLO=ctALLO+1;
			
			ConnectAndReadSheets.updateSheetRoster(scrappingSiteDetails.getGoogleSheetId(),
					  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<RosterDetails>)r,scrappingSiteDetails.getRowCount(),q,"NO");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				ConnectAndReadSheets.updateSheetMCNADentaRunStatus(scrappingSiteDetails.getGoogleSheetId(), scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, e.getMessage(), ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS, ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

			return r;
    }
	

	public List<RosterDetails> scrapSite(ScrappingSiteDetails scrappingSiteDetails,ScrappingInputDto dto) {
		this.driver = getBrowserDriverOpenActualBrowser();
		//driver.setJavascriptEnabled(true);//
		final Logger logger = LogManager.getLogger("com.gargoylesoftware");
		final org.apache.logging.log4j.Level OFF = org.apache.logging.log4j.Level.OFF;
		logger.log(OFF, "NO LOG"); // use the custom VERBOSE level

		
		setProps(scrappingSiteDetails.getProxyPort());
		List<RosterDetails> rList = new ArrayList<>();
		try {
			driver.manage().window().maximize();
			loginToSiteMCNA(scrappingSiteDetails, driver);
			
			char c;
			//int size=0;
			for (c = dto.getStart().toLowerCase().charAt(0); c <= dto.getEnd().toLowerCase().charAt(0); ++c) {
				navigatetoRoster();
				System.out.println("*********************************"       +c +"-"+rList.size());
				/*if (rList.size()>40) {
					try {
					//	List<RosterDetails> rListC = new ArrayList<>(rList);
						
						ConnectAndReadSheets.updateSheetRoster(scrappingSiteDetails.getGoogleSheetId(),
								  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<RosterDetails>)rList,scrappingSiteDetails.getRowCount(),size+1,"YES");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					size=size+rList.size();
					rList.clear();
				}*/
				clickOnNameLink( c + "", rList,true,1,2);
			}
			//size=size+rList.size();
		   //finalSize=size;
			/*
			 * for(RosterDetails z:rList) { System.out.println(z.getPatFName());
			 * System.out.println(z.getTelephone());
			 * System.out.println(z.getSubscriberId());
			 * System.out.println("----------------");
			 * 
			 * }
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				ConnectAndReadSheets.updateSheetMCNADentaRunStatus(scrappingSiteDetails.getGoogleSheetId(), scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "Check password --"+e.getMessage(), ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS, ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS+1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			if (driver!=null) driver.close();
			try {
			ArrayList<String> tabsOld = new ArrayList<String> (driver.getWindowHandles());
			if (tabsOld.size()==1) {
				driver.switchTo().window(tabsOld.get(0));
				driver.close();
				driver.quit();
			}
			}catch(Exception n) {
				
			}
			
 		}

		return rList;

	}



	private void navigatetoRoster() throws InterruptedException {
			this.driver.navigate().to("https://portal.mcna.net/provider/members_roster");
			Thread.sleep(5000);
			
	}
	
	private void clickOnNameLink( String name, List<RosterDetails> rList,boolean page,int  pagging,int initial)  {
		boolean cont=false;
		
		try {
		if (page) {
		try {
		Thread.sleep(10000);
		WebElement element = driver.findElement(By.xpath("//*[@id=\"" + name + "\"]"));
		if (element != null) {
			element.click();
			cont=true;
				Thread.sleep(2000);
				
			 }
			}catch (Exception e) {
			e.printStackTrace();
		}
		}else {
			//false case 
			//check for pagination //continue if we find links 
			try {                                           
			WebElement element =driver.findElement(By.xpath("/html/body/div[7]/div[2]/div[2]/div[2]/div/div"));//Increase 6  to 7
			element = element.findElement(By.linkText(pagging+""));
			element.click();
			cont=true;
			Thread.sleep(2000);
			}catch (Exception e) {
				//System.out.println("UUUUUUUUUUUU");
			}
			
			
		}
			int ct = initial;//2;
			int newCt=7;//6 old //Check pagination also
			if (cont) {
			boolean breakonExp=false;	
			for (;;) {
				try {
				List<WebElement> elements = driver
						.findElements(By.xpath("/html/body/div["+newCt+"]/div[2]/div[2]/div[2]/table/tbody/tr[" + ct + "]/td"));
				if (elements != null && elements.size() > 0) {
					RosterDetails rd = new RosterDetails();
					for (int x = 1; x <= elements.size(); x++) {
						//to close pop up..
						// path("/html/body/div[8]/div/div/div/div[1]/a"));
						WebElement element231 = null;// path("/html/body/div[8]/div/div/div/div[1]/a"));
						if (x!=1) {
						try {
						//WebElement element23 = driver.findElements(By.className("closeInfoBox")).get(1);
						//element23.click();
						}catch(Exception p){
							p.printStackTrace();
						}
						}
						WebElement element2 =null;
						try {
						element2 = driver.findElement(By.xpath(
								"/html/body/div["+newCt+"]/div[2]/div[2]/div[2]/table/tbody/tr[" + ct + "]/td[" + x + "]"));
						if (x == 1)
							rd.setPatFName(element2.getText());
						if (x == 2)
							rd.setCity(element2.getText());
						if (x == 3)
							rd.setAssignedDentistF(element2.getText());
						//if --***1 Start
						if (x == elements.size() - 1) {
							//
							//
							String providerFacilityId=driver.findElement(By.id("facilityId")).getAttribute("value");
							
							String patId=element2.findElement(By.tagName("div")).getAttribute("id");
							ArrayList<String> tabsOld = new ArrayList<String> (driver.getWindowHandles());
							if (tabsOld.size()==1) {
								
							String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);
							//driver.switchTo().newWindow(WindowType.WINDOW);
							driver.findElement(By.linkText("Acceptable Use Policy")).sendKeys(selectLinkOpeninNewTab);//to open empty tab
							tabsOld = new ArrayList<String> (driver.getWindowHandles());
							
							 }
							driver.switchTo().window(tabsOld.get(1)); // switch new Tab to main screen
							driver.navigate().to("https://portal.mcna.net/provider/get_member_info.json?id="+patId+"&providerFacilityId="+providerFacilityId);
							Thread.sleep(8000);
							parserPopupDetailPage(rd);
							 //driver.close();
							 driver.switchTo().window(tabsOld.get(0));
							//
							//element2.findElement(By.tagName("div")).click();
							//Thread.sleep(8000);
							 
							try {
							//String z="moreInfoBox";	
							//	if (name.equalsIgnoreCase("B")) z="moreInfoBox1";//for test
									
							//WebElement element21 = driver.findElement(By.id(z));
							// element21.findElement(By.className(className))
							//((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
								//	element21);
							//element231 = driver.findElement(By.className("header"));// path("/html/body/div[8]/div/div/div/div[1]/a"));
							
							// element23.click();
							//parseSubdetail(rd, element21.getAttribute("textContent"),element21);
//							String n=null;
//							
//							if (name.equals("a") && ct ==15) {
//							  if (n.equals("Zz")) {
//								  element2 = driver.findElement(By.xpath(
//											"/html/body/div[6]/div[2]/div[2]/div[2]/table/tbody/tr[" + 1000000000 + "]/td[" + x + "]"));
//											  
//							  }
//							}
							}catch(Exception x1) {
								reloginAndNavigate();
								/*
								try {
									ConnectAndReadSheets.updateSheetMCNADentaRunStatus(scrappingSiteDetails.getGoogleSheetId(), scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "NAME issue (moreInfoBox): "+name+"----", ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS, ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS+1+1);
									clickOnNameLink(name, rList, page,pagging,ct);
									breakonExp=true;
									break;
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								*/
							}
							rList.add(rd);
							// WebElement element21 = driver.findElement(By.id("moreInfoBox"));
							// WebElement element22
							// =driver.findElement(By.xpath("/html/body/div[8]/div/div/div/div[1]/a"));
							// ((JavascriptExecutor)
							// driver).executeScript("arguments[0].scrollIntoView(true);", element22);
							//if (element231!=null) element231.click();
							
							if (rList.size()>=max) {
								try {
									List<RosterDetails> rListC = new ArrayList<>(rList);
									int q=1+(max*ctALL);
									//if (q>1) q=q+1+ctALLO;
									//ctALLO=ctALLO+1;
									ConnectAndReadSheets.updateSheetRoster(scrappingSiteDetails.getGoogleSheetId(),
											  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<RosterDetails>)rListC,scrappingSiteDetails.getRowCount(),q,"YES");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//size=size+rList.size();
								ctALL=ctALL+1;
								rList.clear();
							}else {
							 //size=size+1;
							 //finalSize=size;
							}

							
							
							
						}//if --***1 stop
					}catch (Exception e) {
						try {
							ConnectAndReadSheets.updateSheetMCNADentaRunStatus(scrappingSiteDetails.getGoogleSheetId(), scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "NAME issue (moreInfoBox): "+name+"----"+e.getMessage(), ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS, ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS+1+1+1);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
					}
					if (breakonExp) 	break; //break on Exception from main Loop...

				} else {
					break;
				}
				}catch (Exception e) {
					e.printStackTrace();
					break;
				}
				ct = ct + 1;

			 }//for
		    }
			if (cont)clickOnNameLink( name, rList, false,++pagging,2);
		}
		 catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

	private void parseSubdetail(RosterDetails rd, String data,WebElement moreInfoBox) {
		String aa[] = data.split("\\r?\\n");
		try {
		for (int x = 0; x < aa.length; x++) {
			if (aa[x].trim().equals("close"))
				continue;
			aa[x] = aa[x].replace("Subscriber ID:", "").replace("Address:", "").replace("Date of Birth:", "")
					.replace("Telephone:", "").replaceAll("            ", "").trim();
			if (x == 4)
				rd.setSubscriberId(aa[x]);
			else if (x == 7)
				rd.setAddress1(aa[x]);
			else if (x == 11)
				rd.setAddress2(aa[x]);
			else if (x == 13)
				rd.setDob(aa[x]);
			else if (x == 16)
				rd.setTelephone(aa[x]);
		 }
		}catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	private void reloginAndNavigate() throws InterruptedException {
		this.driver.close();
		this.driver = getBrowserDriverOpenActualBrowser();
		//this.driver.setJavascriptEnabled(true);//
		loginToSiteMCNA(scrappingSiteDetails, this.driver);
		navigatetoRoster();

	}
	
	
	
	private void parserPopupDetailPage(RosterDetails rd) {
		
			//System.out.println("CALLED--URL...");
			String data = "";
			try {
				data = driver.getPageSource();
				System.out.println("data");
				System.out.println(data);
				data = data.replace("</pre></body></html>", "");
				data = "{\"get_member_info\":"+data.split("\"get_member_info\":")[1];
				////data.
				JSONObject jsonObj = new JSONObject(data);
				JSONObject subData =(JSONObject) jsonObj.get("get_member_info");
				rd.setAddress1(subData.get("address1").toString());
				rd.setTelephone(subData.get("telephone").toString());
				rd.setAddress2(subData.get("csz").toString());
				rd.setSubscriberId(subData.get("subscriber_id").toString());
				rd.setDob(subData.get("dob").toString());
				
			} catch (Exception e) {
				e.printStackTrace();
				data = "";
				// TODO: handle exception
			}
			
		
	}

}
