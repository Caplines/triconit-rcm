package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dto.scrapping.RosterDetails;
import com.tricon.ruleengine.model.db.ScrappingSite;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.service.MCNARosterScrappingService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;

/**
 * 
 * @author Deepak.Dogra
 *
 */

public class MCNARosterScrappingServiceImpl  extends BaseScrappingServiceImpl implements  Callable<List<?>>{
	
	private ScrappingSiteDetails scrappingSiteDetails=null;
	private String CLIENT_SECRET_DIR,CREDENTIALS_FOLDER;
	

	public ScrappingSiteDetails getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}


	public void setScrappingSiteDetails(ScrappingSiteDetails scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}


    MCNARosterScrappingServiceImpl(ScrappingSiteDetails scrappingSiteDetails,
    		String	CLIENT_SECRET_DIR,String CREDENTIALS_FOLDER) {
		this.scrappingSiteDetails=scrappingSiteDetails;
		this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
		this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
	       // store parameter for later user
	   }
	
	
	@Override
    public List<RosterDetails> call() {
		List<RosterDetails> r=scrapSite(scrappingSiteDetails);
		  try {
			ConnectAndReadSheets.updateSheetRoster(scrappingSiteDetails.getGoogleSheetId(),
					  scrappingSiteDetails.getGoogleSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,(List<RosterDetails>)r,scrappingSiteDetails.getRowCount());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			return r;
    }
	

	public List<RosterDetails> scrapSite(ScrappingSiteDetails scrappingSiteDetails) {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);//
		final Logger logger = LogManager.getLogger("com.gargoylesoftware");
		final org.apache.logging.log4j.Level OFF = org.apache.logging.log4j.Level.OFF;
		logger.log(OFF, "NO LOG"); // use the custom VERBOSE level

		
		setProps(scrappingSiteDetails.getProxyPort());
		List<RosterDetails> rList = new ArrayList<>();
		try {
			loginToSiteMCNA(scrappingSiteDetails, driver);
			navigatetoRoster(driver);
			char c;
			for (c = 'a'; c <= 'z'; ++c) {
				clickOnNameLink(driver, c + "", rList);
			}
			/*
			 * for(RosterDetails z:rList) { System.out.println(z.getPatFName());
			 * System.out.println(z.getTelephone());
			 * System.out.println(z.getSubscriberId());
			 * System.out.println("----------------");
			 * 
			 * }
			 */
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rList;

	}



	private void navigatetoRoster(WebDriver driver) throws InterruptedException {
			driver.navigate().to("https://portal.mcna.net/provider/members_roster");
			Thread.sleep(5000);
			
	}
	
	private void clickOnNameLink(WebDriver driver, String name, List<RosterDetails> rList) throws InterruptedException {
		WebElement element = driver.findElement(By.xpath("//*[@id=\"" + name + "\"]"));
		if (element != null) {
			element.click();
			Thread.sleep(2000);
			int ct = 2;
			for (;;) {
				List<WebElement> elements = driver
						.findElements(By.xpath("/html/body/div[6]/div[2]/div[2]/div[2]/table/tbody/tr[" + ct + "]/td"));
				if (elements != null && elements.size() > 0) {
					RosterDetails rd = new RosterDetails();
					for (int x = 1; x <= elements.size(); x++) {
						WebElement element2 = driver.findElement(By.xpath(
								"/html/body/div[6]/div[2]/div[2]/div[2]/table/tbody/tr[" + ct + "]/td[" + x + "]"));
						if (x == 1)
							rd.setPatFName(element2.getText());
						if (x == 2)
							rd.setCity(element2.getText());
						if (x == 3)
							rd.setAssignedDentistF(element2.getText());

						if (x == elements.size() - 1) {
							element2.findElement(By.tagName("div")).click();
							Thread.sleep(4000);
							WebElement element21 = driver.findElement(By.id("moreInfoBox"));

							// element21.findElement(By.className(className))
							((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
									element21);

							WebElement element23 = driver.findElement(By.className("header"));// path("/html/body/div[8]/div/div/div/div[1]/a"));
							// element23.click();
							parseSubdetail(rd, element21.getAttribute("textContent"));
							rList.add(rd);
							// WebElement element21 = driver.findElement(By.id("moreInfoBox"));
							// WebElement element22
							// =driver.findElement(By.xpath("/html/body/div[8]/div/div/div/div[1]/a"));
							// ((JavascriptExecutor)
							// driver).executeScript("arguments[0].scrollIntoView(true);", element22);
							element23.click();
						}
					}

				} else {
					break;
				}
				ct = ct + 1;

			}
		}

	}

	private void parseSubdetail(RosterDetails rd, String data) {
		String aa[] = data.split("\\r?\\n");
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

	}

	

}
