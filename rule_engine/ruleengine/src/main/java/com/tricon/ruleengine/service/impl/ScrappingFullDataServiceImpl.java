package com.tricon.ruleengine.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.service.ScrappingFullDataService;
import com.tricon.ruleengine.service.scrapfull.BCBSDnoaconnect;
import com.tricon.ruleengine.service.scrapfull.DeltaDentalServiceImpl;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.ConstantsScrapping;

@Service
public class ScrappingFullDataServiceImpl implements ScrappingFullDataService{

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	
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
	public String parseFullDataAndSaveDetails(ScrappingFullDataDetailDto dto) {
		
		Map<String, List<?>> map = new HashMap<>();
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userDao.findUserByUsername(authentication.getName());
		boolean continueParsing=false;
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
		String exMess=continueParsing?"Started":"Scrap Procedure Already Running.\n Please wait till it finishes.";
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
			if (continueParsing){
				ExecutorService service = Executors.newCachedThreadPool();
				if (dto.getDto() != null) {
					
				 //Add All Sites Here....	
				 if (dto.getSiteName().equals("Delta Dental")) {
					 //service.submit(new DeltaDentalServiceImpl(patDao,full,dto,user,off));
					 /*map.put(ConstantsScrapping.SCRAPPING_INIT + dto.getSheetId() + ConstantsScrapping.NAME_Separator
								+ dto.getSheetSubId(), null);*/
				  } else if (dto.getSiteName().equals("BCBS")) {
					 service.submit(new BCBSDnoaconnect(patDao,dataDoa ,full,dto,user,off));
				  }
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
		return exMess;
	}

}
