package com.tricon.ruleengine.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagment;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFullMaster;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.service.ScrappingFullDataService;
import com.tricon.ruleengine.service.scrapfull.impl.BCBSDnoaconnectImpl;
import com.tricon.ruleengine.service.scrapfull.impl.DeltaDentalServiceImpl;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.ConstantsScrapping;

@Service
public class ScrappingFullDataServiceImpl implements ScrappingFullDataService{

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@Autowired
	private Environment env;
	
	@Autowired
	ScrapingFullDataDoa dataDoa;
	
	@Autowired
	OfficeDao offDoa;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	OfficeDao officeDao;
	
	@Autowired
	PatientDao patDao;
	
	
	@Override
	public List<ScrappingFullDataDto> getSiteNames() {
		return dataDoa.getSiteNames();
	}

	@Override
	public ScrappingFullDataDetailDto getScrappingDetails(int siteId,String offId) {
		
		return dataDoa.getScrappingDetails(siteId,offDoa.getOfficeByUuid(offId));

	}

	@Override
	public String findRunningStatus(ScrappingFullDataDetailDto dto) {
		String run= dataDoa.findAnyRunnigfullScrapBSiteName(dto.getSiteName());
		return run;
	}
	
	@Override
	public String parseFullDataAndSaveDetails(ScrappingFullDataDetailDto dto,String userName) {
		
		Map<String, List<?>> map = new HashMap<>();
		// TODO Auto-generated method stub
		User user = userDao.findUserByUsername(userName);
		boolean continueParsing=false;
		String run= "";//findRunningStatus(dto);
		String exMess="One Scrap Procedure Already Running for "+dto.getSiteName()+".\n Please wait till it finishes.";
		if (run.equals("")) {
		ScrappingSiteDetailsFull full = dataDoa.findScrappingDetailsById(dto.getSiteDetailId());
		Office off=officeDao.getOfficeByUuid(dto.getOfficeId());
		if (full==null) {
			ScrappingSiteDetailsFull fd=new ScrappingSiteDetailsFull();
			ScrappingSiteFull f=dataDoa.findScrappingSiteFullById(dto.getSiteId());
			int port = dataDoa.findMaxProxyPort(dto.getSiteDetailId());
			if (port==0) port = 9500;
			else port=port+1;
			
			fd.setCreatedBy(user);
			fd.setUserName(dto.getUserName());
//			fd.setGoogleSheetId(dto.getSheetId());
//			fd.setGoogleSheetName(off.getName());
//			fd.setGoogleSubId(dto.getSheetSubId());
			fd.setOffice(off);
			fd.setPassword(dto.getPassword());
			fd.setScrappingSite(f);
			fd.setProxyPort(port+"");
			fd.setRunning(true);
			continueParsing=true;
			
			dataDoa.saveScrappingDetailsById(fd);
		}
		else {
			full.setUpdatedBy(user);
//			full.setGoogleSheetId(dto.getSheetId());
//			full.setGoogleSubId(dto.getSheetSubId());
			full.setPassword(dto.getPassword());
			full.setUserName(dto.getUserName());
			if (!full.isRunning()) { 
				continueParsing = true;
			}
			full.setRunning(true);
			dataDoa.updateScrappingDetailsById(full);
		}
		
		//start the parsing....
		ScrappingFullDataManagment manage =dataDoa.getScrappingFullDataManagmentData();
		continueParsing=false;
		if (manage.getMaxCount()==-1) {
			continueParsing=true;
		}else if (manage.getMaxCount()>manage.getProcessCount()) {
			continueParsing=true;
		}
		//increase the count
		manage.setProcessCount(manage.getProcessCount()+dto.getDto().size());
		dataDoa.increasecrapCount(manage);
		ScrappingFullDataManagmentProcess manageP=  new ScrappingFullDataManagmentProcess();
		manageP.setCount(dto.getDto().size());
		manageP.setCreatedBy(user);
		manageP.setCreatedDate(new Date());
		int x=(int) dataDoa.createScrappingSiteManagementProcess(manageP);
		exMess=continueParsing?("Started-"+x):manage.getProcessCount()+" Scrap Procedures Already Running.\n Please wait till it finishes.";
		String taxId= dataDoa.getTaxmapping(off, "PPO");
		try {
			//update Status in Google Sheet about running a status
			/*ConnectAndReadSheets.updateSheetMCNADentaRunStatus(dto.getSheetId(), dto.getSheetSubId(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "YES", ConstantsScrapping.ELE_ROW_INDEX_STATUS,
					ConstantsScrapping.ELE_COLUMN_INDEX_STATUS);
			*/		
			//Map<String, List<Object>> mapData = null;
			/*mapData = ConnectAndReadSheets.readSheeFullWebsiteParsing(dto.getSheetId(),
					off.getName()+" Database", CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
			*/
			//DeltaDentalServiceImpl i= new DeltaDentalServiceImpl(null,null,null,null,null,true);
			if (true){//continueParsing for testing done true if ant issue change to  continueParsing also correct line no 126 exMess
				if (dto.getDto() != null) {
					
				 //Add All Sites Here....
				ExecutorService service = Executors.newCachedThreadPool();	
				
				System.out.println("8888888888--"+dto.getSiteName());
				 if (dto.getSiteName().equals("Delta Dental")) {
							// service.submit(new DeltaDentalServiceImpl(patDao,full,dto,user,off));
					 /*map.put(ConstantsScrapping.SCRAPPING_INIT + dto.getSheetId() + ConstantsScrapping.NAME_Separator
								+ dto.getSheetSubId(), null);*/
					 System.out.println("';"+env.getProperty("google.chorme.driver"));
					 service.submit(new DeltaDentalServiceImpl(patDao,dataDoa ,full,dto,user,off,x,taxId,env.getProperty("google.chorme.driver")));
				  } else if (dto.getSiteName().equals("BCBS")) {
					  service.submit(new BCBSDnoaconnectImpl(patDao,dataDoa ,full,dto,user,off,x,taxId,env.getProperty("google.chorme.driver")));
					  //service.submit(new BCBSDnoaconnectImpl(full,dto,user,off));
				  }
				// exMess="Done"; 
				}
		}
			
		}catch(Exception ex) {
			exMess="-"+ex.getMessage();
			
		}finally {
			/*try {
			ConnectAndReadSheets.updateSheetMCNADentaRunStatus(dto.getSheetId(), dto.getSheetSubId(),
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "No"+exMess, ConstantsScrapping.ELE_ROW_INDEX_STATUS,
					ConstantsScrapping.ELE_COLUMN_INDEX_STATUS);
			}catch(Exception ex) {
				
			}*/
			
		}
		}
		return exMess;
	}

	@Override
	public int getScrappingFullDataManagmentProcessCount(int id) {
		// TODO Auto-generated method stub
		return dataDoa.getScrappingFullDataManagmentDataProcess(id).getCount();
	}

}
