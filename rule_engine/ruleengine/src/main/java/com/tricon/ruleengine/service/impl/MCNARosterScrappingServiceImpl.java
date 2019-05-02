package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.tricon.ruleengine.dto.ScrappingInputDto;
import com.tricon.ruleengine.dto.scrapping.RosterDetails;
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
	HtmlUnitDriver driver = null;
	
	//private int  ctALLO=0;
		
	
	

	public ScrappingSiteDetails getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}


	public void setScrappingSiteDetails(ScrappingSiteDetails scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}


    MCNARosterScrappingServiceImpl(ScrappingSiteDetails scrappingSiteDetails,
    		String	CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER,ScrappingInputDto dto) {
		this.scrappingSiteDetails=scrappingSiteDetails;
		this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
		this.dto=dto;
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
		this.driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);//
		final Logger logger = LogManager.getLogger("com.gargoylesoftware");
		final org.apache.logging.log4j.Level OFF = org.apache.logging.log4j.Level.OFF;
		logger.log(OFF, "NO LOG"); // use the custom VERBOSE level

		
		setProps(scrappingSiteDetails.getProxyPort());
		List<RosterDetails> rList = new ArrayList<>();
		try {
			loginToSiteMCNA(scrappingSiteDetails, driver);
			navigatetoRoster();
			char c;
			//int size=0;
			for (c = dto.getStart().toLowerCase().charAt(0); c <= dto.getEnd().toLowerCase().charAt(0); ++c) {
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
			WebElement element =driver.findElement(By.xpath("/html/body/div[6]/div[2]/div[2]/div[2]/div/div"));
			element = element.findElement(By.linkText(pagging+""));
			element.click();
			cont=true;
			Thread.sleep(2000);
			}catch (Exception e) {
				//System.out.println("UUUUUUUUUUUU");
			}
			
			
		}
			int ct = initial;//2;
			if (cont) {
			boolean breakonExp=false;	
			for (;;) {
				try {
				List<WebElement> elements = driver
						.findElements(By.xpath("/html/body/div[6]/div[2]/div[2]/div[2]/table/tbody/tr[" + ct + "]/td"));
				if (elements != null && elements.size() > 0) {
					RosterDetails rd = new RosterDetails();
					for (int x = 1; x <= elements.size(); x++) {
						//to close pop up..
						WebElement element23 = driver.findElement(By.className("header"));// path("/html/body/div[8]/div/div/div/div[1]/a"));
						WebElement element231 = null;// path("/html/body/div[8]/div/div/div/div[1]/a"));
						element23.click();
						WebElement element2 =null;
						try {
						element2 = driver.findElement(By.xpath(
								"/html/body/div[6]/div[2]/div[2]/div[2]/table/tbody/tr[" + ct + "]/td[" + x + "]"));
						if (x == 1)
							rd.setPatFName(element2.getText());
						if (x == 2)
							rd.setCity(element2.getText());
						if (x == 3)
							rd.setAssignedDentistF(element2.getText());

						if (x == elements.size() - 1) {
							element2.findElement(By.tagName("div")).click();
							Thread.sleep(5000);
							try {
							String z="moreInfoBox";	
							//	if (name.equalsIgnoreCase("B")) z="moreInfoBox1";//for test
									
							WebElement element21 = driver.findElement(By.id(z));
							// element21.findElement(By.className(className))
							((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
									element21);
							element231 = driver.findElement(By.className("header"));// path("/html/body/div[8]/div/div/div/div[1]/a"));
							
							// element23.click();
							parseSubdetail(rd, element21.getAttribute("textContent"));
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
							if (element231!=null) element231.click();
							
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

							
							
							
						}
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

	private void parseSubdetail(RosterDetails rd, String data) {
		String aa[] = data.split("\\r?\\n");
		try {
		for (int x = 0; x < aa.length; x++) {
			if (aa[x].trim().equals("close"))
				continue;
			aa[x] = aa[x].replace("Subscriber ID:", "").replace("Address:", "").replace("Date of Birth:", "")
					.replace("Telephone:", "").replaceAll("            ", "").trim();
			if (x == 2)
				rd.setSubscriberId(aa[x]);
			else if (x == 3)
				rd.setAddress1(aa[x]);
			else if (x == 4)
				rd.setAddress2(aa[x]);
			else if (x == 5)
				rd.setDob(aa[x]);
			else if (x == 6)
				rd.setTelephone(aa[x]);
		}
		}catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	private void reloginAndNavigate() throws InterruptedException {
		this.driver.close();
		this.driver = new HtmlUnitDriver();
		this.driver.setJavascriptEnabled(true);//
		loginToSiteMCNA(scrappingSiteDetails, this.driver);
		navigatetoRoster();

	}

}
