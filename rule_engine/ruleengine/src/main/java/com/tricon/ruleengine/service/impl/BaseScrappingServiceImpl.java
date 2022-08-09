package com.tricon.ruleengine.service.impl;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//@Service 
public class BaseScrappingServiceImpl {
	
	protected String driverLocation;

	public void loginToSiteMCNA(ScrappingSiteDetails scrappingSiteDetails, WebDriver driver) throws InterruptedException {

		driver.get(scrappingSiteDetails.getScrappingSite().getSiteUrl());
		Thread.sleep(5000);
        //driver.findElement(By.id("recaptcha-anchor")).click();
		
		WebElement element = driver.findElement(By.name("loginUsername"));
		element.sendKeys(scrappingSiteDetails.getUserName());
		//System.out.println(driver.findElements(By.tagName("iframe")).size());
		//WebElement iframe =driver.findElements(By.tagName("iframe")).get(0);
		//System.out.println(iframe.getText());
		//WebElement sss =iframe.findElement(By.id("recaptcha-anchor"));
		//System.out.println(driver.findElements(By.tagName("iframe")).get(1).getText());
		//System.out.println(driver.findElements(By.tagName("iframe")).get(2).getText());
		//driver.switchTo().frame(0);
		Thread.sleep(5000);

		//driver.switchTo().parentFrame();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		js.executeScript("document.getElementById('loginPasswordPlain').value='"+scrappingSiteDetails.getPassword()+"';");
		js.executeScript("document.getElementById('loginPassword').value='"+scrappingSiteDetails.getPassword()+"';");
		/*WebElement element2 = driver.findElement(By.id("loginPasswordPlain"));
		element2.sendKeys(scrappingSiteDetails.getPassword());
		WebElement element5 = driver.findElement(By.id("loginPassword"));
		element5.sendKeys(scrappingSiteDetails.getPassword());
		*/
		WebElement element3 = driver.findElement(By.id("loginButton"));
		Thread.sleep(10000);
		Thread.sleep(8000);// Need to keep this number high for Linux issue.
		element3.click();
		Thread.sleep(4000);

	}

	public void loginToSiteDentaOld(ScrappingSiteDetails scrappingSiteDetails, WebDriver driver) throws InterruptedException {

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://govservices.dentaquest.com/Logon.jsp");
		WebElement element3 = driver.findElement(By.name("Submit"));
		//System.out.println(element3.isSelected());
		WebElement element = driver.findElement(By.name("USERSXUSERNAME"));
		element.sendKeys(scrappingSiteDetails.getUserName());
		WebElement element2 = driver.findElement(By.id("USERSXPASSWORD"));
		element2.sendKeys(scrappingSiteDetails.getPassword());
		element3.click();
		Thread.sleep(8000);

	}
	
	public void loginToSiteDentaNew(ScrappingSiteDetails scrappingSiteDetails, WebDriver driver) throws InterruptedException {

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://connectsso.dentaquest.com/authsso/providersso/SSOProviderLogin.aspx?TYPE=33554433&REALMOID=06-6a4c193d-7520-4f3d-b194-83367a3ef454&GUID=&SMAUTHREASON=0&METHOD=POST&SMAGENTNAME=-SM-imZolSjcs1FQR%2fH0k3NSK1Uvx4zWgziEWSOuwqcKGG1C%2bW%2fQdG3dRa7BVqGyOpNh&TARGET=-SM-https%3a%2f%2fconnectsso%2edentaquest%2ecom%2fprovideraccessv2%2findex%2ehtml");
		WebElement element3 = driver.findElement(By.name("btnSubmit"));
		WebElement element = driver.findElement(By.name("USER"));
		element.sendKeys(scrappingSiteDetails.getUserName());
		WebElement element2 = driver.findElement(By.id("PASSWORD"));
		element2.sendKeys(scrappingSiteDetails.getPassword());
		element3.click();
		Thread.sleep(5000);

	}
	
	public void setProps(String proxyPort) {
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", proxyPort);
		// for capturing HTTPS traffic
		System.setProperty("https.proxyHost", "127.0.0.1");
		System.setProperty("https.proxyPort", proxyPort);

	}
	
	protected WebDriver getBrowserDriver() {
		// System.setProperty("webdriver.gecko.driver",
		// "D:/Project/Tricon/linkedinapp/linkedin/lib/geckodriver.exe");
		// for chrome
		// webClient = new WebClient();
		ChromeOptions options = new ChromeOptions();
		try {
			// https://chromedriver.chromium.org/downloads
			//System.out.println("getBrowserDriver" + driverLocation);
			System.setProperty("webdriver.chrome.driver", driverLocation);
			// ChromeOptions options = new ChromeOptions();
			// System.out.println("555");
			options.addArguments("-disable-infobars");
			options.addArguments("--headless");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.setExperimentalOption("useAutomationExtension", false);
			// System.out.println("8888");
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
			// System.out.println("1118888");
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		// System.out.println("getBrowserDriver:" + 88888);
		ChromeDriverService chromeDriverService = ChromeDriverService.createDefaultService();
		return new ChromeDriver(chromeDriverService, options);

	}
	
	protected WebDriver getBrowserDriverOpenActualBrowser() {
		// System.setProperty("webdriver.gecko.driver",
		// "D:/Project/Tricon/linkedinapp/linkedin/lib/geckodriver.exe");
		// for chrome
		// webClient = new WebClient();
		ChromeOptions options = new ChromeOptions();
		try {
			// https://chromedriver.chromium.org/downloads
			//System.out.println("getBrowserDriver" + driverLocation);
			System.setProperty("webdriver.chrome.driver", driverLocation);
			// ChromeOptions options = new ChromeOptions();
			// System.out.println("555");
			options.addArguments("-disable-infobars");
			//options.addArguments("--headless");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.setExperimentalOption("useAutomationExtension", false);
			// System.out.println("8888");
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
			// System.out.println("1118888");
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		// System.out.println("getBrowserDriver:" + 88888);
		ChromeDriverService chromeDriverService = ChromeDriverService.createDefaultService();
		return new ChromeDriver(chromeDriverService, options);

	}


	
	   public void invokeSetter(Object obj, String propertyName, Object variableValue)
	    {
	        PropertyDescriptor pd;
	        try {
	            pd = new PropertyDescriptor(propertyName, obj.getClass());
	            Method setter = pd.getWriteMethod();
	            try {
	                setter.invoke(obj,variableValue);
	            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	                e.printStackTrace();
	            }
	        } catch (IntrospectionException e) {
	            e.printStackTrace();
	        }
	 
	    }
	 
	    public void invokeGetter(Object obj, String variableName)
	    {
	        try {
	            PropertyDescriptor pd = new PropertyDescriptor(variableName, obj.getClass());
	            Method getter = pd.getReadMethod();
	            Object f = getter.invoke(obj);
	            //System.out.println(f);
	        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
	            e.printStackTrace();
	        }
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
