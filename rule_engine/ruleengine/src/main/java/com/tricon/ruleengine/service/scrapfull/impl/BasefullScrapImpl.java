package com.tricon.ruleengine.service.scrapfull.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;

import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagment;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.User;

public class BasefullScrapImpl {
	
	@Autowired
	ScrapingFullDataDoa dataDoa;

	@Autowired
	PatientDao patDao;

	protected ScrappingSiteDetailsFull scrappingSiteDetails;
	// this.CLIENT_SECRET_DIR=CLIENT_SECRET_DIR;
	// this.CREDENTIALS_FOLDER=CREDENTIALS_FOLDER;
	// this.mapData=mapData;
	// this.updateSheet=updateSheet;
	protected ScrappingFullDataDetailDto dto;
	protected Office office;
	protected User user;
	protected String driverLocation;
	protected int processId;
	protected String taxId;
	protected IVFormType ivFormType;

	protected static String siteName = "";

	public ScrapingFullDataDoa getDataDoa() {
		return dataDoa;
	}

	public void setDataDoa(ScrapingFullDataDoa dataDoa) {
		this.dataDoa = dataDoa;
	}

	public PatientDao getPatDao() {
		return patDao;
	}

	public void setPatDao(PatientDao patDao) {
		this.patDao = patDao;
	}

	public ScrappingSiteDetailsFull getScrappingSiteDetails() {
		return scrappingSiteDetails;
	}

	public void setScrappingSiteDetails(ScrappingSiteDetailsFull scrappingSiteDetails) {
		this.scrappingSiteDetails = scrappingSiteDetails;
	}

	public ScrappingFullDataDetailDto getDto() {
		return dto;
	}

	public void setDto(ScrappingFullDataDetailDto dto) {
		this.dto = dto;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDriverLocation() {
		return driverLocation;
	}

	public void setDriverLocation(String driverLocation) {
		this.driverLocation = driverLocation;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public static String getSiteName() {
		return siteName;
	}

	public static void setSiteName(String siteName) {
		BasefullScrapImpl.siteName = siteName;
	}
	
	

	public IVFormType getIvFormTypeId() {
		return ivFormType;
	}

	public void setIvFormTypeId(IVFormType ivFormType) {
		this.ivFormType = ivFormType;
	}

	protected void setProps(String proxyPort) {
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
			System.out.println("getBrowserDriver" + driverLocation);
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
		int port = chromeDriverService.getUrl().getPort();
		System.out.println("PORT---" + port);
		try {
			int chromeDriverProcessID = GetChromeDriverProcessID(port);
			System.out.println("detected chromedriver process id " + chromeDriverProcessID);
			System.out.println("detected chrome process id " + GetChromeProcesID(chromeDriverProcessID));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ChromeDriver(chromeDriverService, options);

	}

	
	protected static int GetChromeDriverProcessID(int aPort) throws IOException, InterruptedException {
		String[] commandArray = new String[3];

		if (SystemUtils.IS_OS_LINUX) {
			commandArray[0] = "/bin/sh";
			commandArray[1] = "-c";
			commandArray[2] = "netstat -anp | grep LISTEN | grep " + aPort;
		} else if (SystemUtils.IS_OS_WINDOWS) {
			commandArray[0] = "cmd";
			commandArray[1] = "/c";
			commandArray[2] = "netstat -aon | findstr LISTENING | findstr " + aPort;
		} else {
			System.out.println("platform not supported");
			System.exit(-1);
		}

		System.out.println("running command " + commandArray[2]);

		Process p = Runtime.getRuntime().exec(commandArray);
		p.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}

		String result = sb.toString().trim();

		System.out.println("parse command response line:");
		System.out.println(result);

		return SystemUtils.IS_OS_LINUX ? ParseChromeDriverLinux(result) : ParseChromeDriverWindows(result);
	}

	protected static int GetChromeProcesID(int chromeDriverProcessID) throws IOException, InterruptedException {
		String[] commandArray = new String[3];

		if (SystemUtils.IS_OS_LINUX) {
			commandArray[0] = "/bin/sh";
			commandArray[1] = "-c";
			commandArray[2] = "ps -efj | grep google-chrome | grep " + chromeDriverProcessID;
		} else if (SystemUtils.IS_OS_WINDOWS) {
			commandArray[0] = "cmd";
			commandArray[1] = "/c";
			commandArray[2] = "wmic process get processid,parentprocessid,executablepath | find \"chrome.exe\" |find \""
					+ chromeDriverProcessID + "\"";
		} else {
			System.out.println("platform not supported");
			System.exit(-1);
		}

		System.out.println("running command " + commandArray[2]);

		Process p = Runtime.getRuntime().exec(commandArray);
		p.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (SystemUtils.IS_OS_LINUX && line.contains("/bin/sh")) {
				continue;
			}

			sb.append(line + "\n");
		}

		String result = sb.toString().trim();

		System.out.println("parse command response line:");
		System.out.println(result);

		return SystemUtils.IS_OS_LINUX ? ParseChromeLinux(result) : ParseChromeWindows(result);
	}

	protected static int ParseChromeLinux(String result) {
		String[] pieces = result.split("\\s+");
		// root 20780 20772 20759 15980 9 11:04 pts/1 00:00:00
		// /opt/google/chrome/google-chrome.........
		// the second one is the chrome process id
		return Integer.parseInt(pieces[1]);
	}

	protected static int ParseChromeWindows(String result) {
		String[] pieces = result.split("\\s+");
		// C:\Program Files (x86)\Google\Chrome\Application\chrome.exe 14304 19960
		return Integer.parseInt(pieces[pieces.length - 1]);
	}

	protected static int ParseChromeDriverLinux(String netstatResult) {
		String[] pieces = netstatResult.split("\\s+");
		String last = pieces[pieces.length - 1];
		// tcp 0 0 127.0.0.1:2391 0.0.0.0:* LISTEN 3333/chromedriver
		return Integer.parseInt(last.substring(0, last.indexOf('/')));
	}

	protected static int ParseChromeDriverWindows(String netstatResult) {
		String[] pieces = netstatResult.split("\\s+");
		// TCP 127.0.0.1:26599 0.0.0.0:0 LISTENING 22828
		return Integer.parseInt(pieces[pieces.length - 1]);
	}
	
	protected void finalSetUpCall() {
		scrappingSiteDetails.setRunning(false);
		ScrappingFullDataManagment manage = dataDoa.getScrappingFullDataManagmentData();
		ScrappingFullDataManagmentProcess manageP = dataDoa
				.getScrappingFullDataManagmentDataProcess(processId);
		manageP.setCount(manageP.getCount() - 1);
		manageP.setUpdatedBy(user);
		manageP.setUpdatedDate(new Date());
		try {
		Thread.sleep(1000);
		dataDoa.updateScrappingFullDataManagmentProcess(manageP);
		if (manage.getProcessCount() > 0) {
			manage.setProcessCount(manage.getProcessCount() - 1);
			dataDoa.increasecrapCount(manage);
		}
		Thread.sleep(1000);
		dataDoa.updateScrappingDetailsById(scrappingSiteDetails);
		}catch (Exception e) {
			e.printStackTrace();
		}

	}



}
