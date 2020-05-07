package com.tricon.ruleengine.service.impl;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;

//@Service 
public class BaseScrappingServiceImpl {
	
	
	public void loginToSiteMCNA(ScrappingSiteDetails scrappingSiteDetails, WebDriver driver) throws InterruptedException {

		driver.get(scrappingSiteDetails.getScrappingSite().getSiteUrl());
		Thread.sleep(4000);// Need to keep this number high for Linux issue.
		WebElement element3 = driver.findElement(By.id("loginButton"));
		WebElement element = driver.findElement(By.name("loginUsername"));
		element.sendKeys(scrappingSiteDetails.getUserName());
		WebElement element2 = driver.findElement(By.id("loginPasswordPlain"));

		element2.sendKeys(scrappingSiteDetails.getPassword());
		WebElement element5 = driver.findElement(By.id("loginPassword"));
		element5.sendKeys(scrappingSiteDetails.getPassword());

		element3.click();
		Thread.sleep(4000);

	}

	public void loginToSiteDenta(ScrappingSiteDetails scrappingSiteDetails, WebDriver driver) throws InterruptedException {

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://govservices.dentaquest.com/Logon.jsp");
		WebElement element3 = driver.findElement(By.name("Submit"));
		System.out.println(element3.isSelected());
		WebElement element = driver.findElement(By.name("USERSXUSERNAME"));
		element.sendKeys(scrappingSiteDetails.getUserName());
		WebElement element2 = driver.findElement(By.id("USERSXPASSWORD"));
		element2.sendKeys(scrappingSiteDetails.getPassword());
		element3.click();
		Thread.sleep(8000);

	}
	
	
	public void setProps(String proxyPort) {
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", proxyPort);
		// for capturing HTTPS traffic
		System.setProperty("https.proxyHost", "127.0.0.1");
		System.setProperty("https.proxyPort", proxyPort);

	}


	/**
	 * FULL WEBSITE SCRAP..
	 * @param scrappingSiteDetails
	 * @param driver
	 * @throws InterruptedException
	 */
	/*
	 * Link:  https://www.deltadentalins.com/
Username:  Crosbyfamilydental
Password:  Crosby000
There are some fields that are blank in the sheet, I am working on it but I think it has enough data to start the work
ok
Share me Share me some Sample Patient
Patient's Name:  Heather Griffith
Member ID:  632307605
DOB: 03/20/1992
Although, you will be able to find patient with just name
	 */
	
}
