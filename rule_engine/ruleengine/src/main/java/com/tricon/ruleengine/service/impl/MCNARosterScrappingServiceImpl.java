package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
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
				String providerFacilityId=driver.findElement(By.id("facilityId")).getAttribute("value");
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
				clickOnNameLink( c + "", rList,true,1,2,providerFacilityId);
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
	
	private void clickOnNameLink( String name, List<RosterDetails> rList,boolean page,int  pagging,int initial,String providerFacilityId)  {
		//boolean cont=false;
		
		ArrayList<String> tabsOld = new ArrayList<String> (driver.getWindowHandles());
		if (tabsOld.size()==1) {
			
			String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);
			//driver.switchTo().newWindow(WindowType.WINDOW);
			driver.findElement(By.linkText("Acceptable Use Policy")).sendKeys(selectLinkOpeninNewTab);//to open empty tab
			tabsOld = new ArrayList<String> (driver.getWindowHandles());
			
			 }
		
		try {
		//https://portal.mcna.net/provider/members_roster_list.json?alpha=g&providerFacilityId=24979
		driver.navigate().to("https://portal.mcna.net/provider/members_roster_list.json?alpha="+name+"&providerFacilityId="+providerFacilityId);	
		Thread.sleep(4000);
		List<RosterDetails> l= parserAlphabetApi();
		int ct=0;
		for(RosterDetails rd:l) {
			
			String patId=rd.getId();
			ct++;
			if (ct==61) {
				navigatetoRoster();
				ct=0;
			}
			
			driver.switchTo().window(tabsOld.get(1)); // switch new Tab to main screen
			driver.navigate().to("https://portal.mcna.net/provider/get_member_info.json?id="+patId+"&providerFacilityId="+providerFacilityId);
			Thread.sleep(8000);
			parserPopupDetailPage(rd);
			 //driver.close();
			 driver.switchTo().window(tabsOld.get(0));
			 rList.add(rd);
			 
			 if (rList.size()>=max) {
					try {
						List<RosterDetails> rListC = new ArrayList<>(rList);
						int q=1+(max*ctALL);
						//if (q>1) q=q+1+ctALLO;
						//ctALLO=ctALLO+1;
						ConnectAndReadSheets.updateSheetRoster(scrappingSiteDetails.getGoogleSheetId(),
								  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<RosterDetails>)rListC,scrappingSiteDetails.getRowCount(),q,"YES");
					System.out.println("Qqqqqqqq"+q);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//size=size+rList.size();
					ctALL=ctALL+1;
					rList.clear();
					System.out.println("rList--"+rList.size());
				}
			 
		}
		}catch(Exception v) {
			
		}
		
	}

	/*private void parseSubdetail(RosterDetails rd, String data,WebElement moreInfoBox) {
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

	}*/
	
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
	
	public static void main(String ss[]) {
		MCNARosterScrappingServiceImpl sd= new MCNARosterScrappingServiceImpl(null, null, null, null, null);
		sd.parserAlphabetApi();
	}
	
	private List<RosterDetails> parserAlphabetApi() {
	
		//String data="<html><head><meta name=\"color-scheme\" content=\"light dark\"></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">{\"members_roster_list\":{\"response_code\":\"200\",\"response_message\":\"OK\",\"practice_state\":\"TX\",\"members\":[{\"id\":\"6698556\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ABDULLAH\",\"prov_mname\":\" \",\"city\":\"HOUSTON\",\"prov_title\":\"DMD\",\"fname\":\"AHMED\",\"mname\":\"M\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"38756809\",\"prov_lname\":\"GHAZAL\",\"effective_date\":null,\"lname\":\"ACRES\",\"prov_mname\":\" \",\"city\":\"BEAUMONT\",\"prov_title\":\"DDS\",\"fname\":\"RYAN\",\"mname\":\"ODIN\",\"suffix\":\" \",\"prov_fname\":\"TARIQ \"},{\"id\":\"25921795\",\"prov_lname\":\"SHAH\",\"effective_date\":null,\"lname\":\"ADAMS\",\"prov_mname\":\" \",\"city\":\"LIBERTY\",\"prov_title\":\"DMD\",\"fname\":\"SAVANNA\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"15619747\",\"prov_lname\":\"SHAH\",\"effective_date\":null,\"lname\":\"AGUILAR\",\"prov_mname\":\" \",\"city\":\"CLEVELAND\",\"prov_title\":\"DMD\",\"fname\":\"ANALICIA\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"13854296\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALEMAN\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"IZABELLA\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"6026166\",\"prov_lname\":\"SHAH\",\"effective_date\":null,\"lname\":\"ALEMAN\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"JULIAN\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"10659163\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALEMAN\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"DAVID\",\"mname\":\"A\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"6050171\",\"prov_lname\":\"SHAH\",\"effective_date\":null,\"lname\":\"ALEMAN\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"MIA\",\"mname\":\"ANDREA\",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"6106761\",\"prov_lname\":\"SHAH\",\"effective_date\":null,\"lname\":\"ALEMAN\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"FRANK\",\"mname\":\"GERARDO\",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"18264201\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALEMAN\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"VICTORIA\",\"mname\":\"LETICIA\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"15121301\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALEXANDER\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"KENZINGTON\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"19323154\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFARO LOPEZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"ERVIN\",\"mname\":\"MANUEL\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"15332303\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFARO LOPEZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"LEYRI\",\"mname\":\"MELISSA\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"14678328\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFARO MARTINEZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"ANDREZ\",\"mname\":\"ANTONIO\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"35275359\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFARO MARTINEZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"NOAH\",\"mname\":\"ELI\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"18269441\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFARO MARTINEZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"SAMANTHA\",\"mname\":\"ROSE\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"20425894\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFRED\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"BRANDON\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"42577582\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFRED\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"RIYA\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"25095690\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALFRED\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"BRAYA\",\"mname\":\"J'CHELLE\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"5770776\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALGERE\",\"prov_mname\":\" \",\"city\":\"BAYTOWN\",\"prov_title\":\"DMD\",\"fname\":\"KELAN\",\"mname\":\"ANTHQEON\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"6023168\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALGERE\",\"prov_mname\":\" \",\"city\":\"BAYTOWN\",\"prov_title\":\"DMD\",\"fname\":\"KILANI\",\"mname\":\"SHANJAYA\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"7877389\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALLEN\",\"prov_mname\":\" \",\"city\":\"HIGHLANDS\",\"prov_title\":\"DMD\",\"fname\":\"SAMARA\",\"mname\":\"M\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"26480071\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALMOND\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"MALLORY\",\"mname\":\"JANE\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"25092175\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALVAREZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"JOVANNY\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"29081246\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALVAREZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"MAILEEN\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"5950347\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ALVAREZ\",\"prov_mname\":\" \",\"city\":\"HOUSTON\",\"prov_title\":\"DMD\",\"fname\":\"AMY\",\"mname\":\"SAMANTHA\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"43016143\",\"prov_lname\":\"ROSSI\",\"effective_date\":null,\"lname\":\"AMEZQUITA\",\"prov_mname\":\"ALAN\",\"city\":\"CROSBY\",\"prov_title\":\"DDS\",\"fname\":\"AVA\",\"mname\":\"MARIEL\",\"suffix\":\" \",\"prov_fname\":\"DOUGLAS\"},{\"id\":\"28575061\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ANAYA VELAZQUEZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"YATZIRY\",\"mname\":\"EDITH\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"14106066\",\"prov_lname\":\"SHAH\",\"effective_date\":null,\"lname\":\"ANAYA-VELAZQUEZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"ZAHORY\",\"mname\":\"ABRIL\",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"29942245\",\"prov_lname\":\"TEPPA SANCHEZ\",\"effective_date\":null,\"lname\":\"ANDERSON\",\"prov_mname\":\"F\",\"city\":\"CENTER\",\"prov_title\":\"DMD\",\"fname\":\"KAYLA\",\"mname\":\"RENEE\",\"suffix\":\" \",\"prov_fname\":\"JOSE\"},{\"id\":\"29475787\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ANDERSON\",\"prov_mname\":\" \",\"city\":\"HUFFMAN\",\"prov_title\":\"DMD\",\"fname\":\"CECILYA\",\"mname\":\"ROSE\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"28576950\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ANDREWS\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"GWYNN\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"14227127\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ANDREWS\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"ROBERT\",\"mname\":\"AXEL\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"38314424\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ANTHONY\",\"prov_mname\":\" \",\"city\":\"HOUSTON\",\"prov_title\":\"DMD\",\"fname\":\"XAYLEE\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"25926921\",\"prov_lname\":\"SHAH\",\"effective_date\":null,\"lname\":\"ARCHIE\",\"prov_mname\":\" \",\"city\":\"HOUSTON\",\"prov_title\":\"DMD\",\"fname\":\"KYMAURI\",\"mname\":\"ANIYAH\",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"7822771\",\"prov_lname\":\"SHAH\",\"effective_date\":\"11/29/2022\",\"lname\":\"ARIAS\",\"prov_mname\":\" \",\"city\":\"HOUSTON\",\"prov_title\":\"DMD\",\"fname\":\"AGUSTIN\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"NISARG\"},{\"id\":\"6356608\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ARMENDARIZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"AIDEN\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"6903532\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ARMENDARIZ\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"IVAN\",\"mname\":\"MANUEL\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"43027562\",\"prov_lname\":\"GANDHI\",\"effective_date\":null,\"lname\":\"ARMSTRONG\",\"prov_mname\":\"S\",\"city\":\"CROSBY\",\"prov_title\":\"DDS\",\"fname\":\"AUBREY\",\"mname\":\"MICHELLE\",\"suffix\":\" \",\"prov_fname\":\"RAHUL\"},{\"id\":\"6696012\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ARREDONDO\",\"prov_mname\":\" \",\"city\":\"BAYTOWN\",\"prov_title\":\"DMD\",\"fname\":\"MARIAH\",\"mname\":\"A\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"6696005\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ARREDONDO\",\"prov_mname\":\" \",\"city\":\"BAYTOWN\",\"prov_title\":\"DMD\",\"fname\":\"JORDAN\",\"mname\":\"LOREN\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"12381856\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ARROYO\",\"prov_mname\":\" \",\"city\":\"HIGHLANDS\",\"prov_title\":\"DMD\",\"fname\":\"YALITZA\",\"mname\":\" \",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"},{\"id\":\"4204407\",\"prov_lname\":\"GANDHI\",\"effective_date\":null,\"lname\":\"ARSOLA\",\"prov_mname\":\"S\",\"city\":\"HUFFMAN\",\"prov_title\":\"DDS\",\"fname\":\"DAMIEN\",\"mname\":\"NOEL\",\"suffix\":\" \",\"prov_fname\":\"RAHUL\"},{\"id\":\"4292457\",\"prov_lname\":\"RASTOGI\",\"effective_date\":null,\"lname\":\"ASHBY\",\"prov_mname\":\" \",\"city\":\"CROSBY\",\"prov_title\":\"DMD\",\"fname\":\"CARTER\",\"mname\":\"JAMES\",\"suffix\":\" \",\"prov_fname\":\"GEETIKA\"}],\"num_plans\":\"2\",\"num_recs\":\"44\"}}</pre></body></html>";
		
		//System.out.println("CALLED--URL...");
		String data = "";
		List<RosterDetails> li = new ArrayList<>();
		try {
			data = driver.getPageSource();
			System.out.println("data");
			System.out.println(data);
			data = data.replace("</pre></body></html>", "");
			data = "{\"members_roster_list\":"+data.split("\"members_roster_list\":")[1];
			////data.
			JSONObject jsonObj = new JSONObject(data);
			JSONObject subData =(JSONObject) jsonObj.get("members_roster_list");
			JSONArray arr = subData.getJSONArray("members");
			
			for (int i=0; i<arr.length(); i++) {
			    JSONObject obj = arr.getJSONObject(i);
			    RosterDetails rd= new RosterDetails();
			    try {
			    rd.setPatFName(obj.get("fname").toString());
				rd.setPatLName(obj.get("lname").toString());
				rd.setPatFName(rd.getPatLName()+", "+rd.getPatFName());
				rd.setCity(obj.get("city").toString());
				rd.setAssignedDentistF(obj.get("prov_fname").toString());
				rd.setAssignedDentistL(obj.get("prov_lname").toString());
				rd.setId(obj.get("id").toString());
				li.add(rd);
			    }catch(Exception c) {
			    	c.printStackTrace();
			    }
			}
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return li;
	
}

}
