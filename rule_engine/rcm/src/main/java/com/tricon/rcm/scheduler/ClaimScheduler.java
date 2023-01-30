package com.tricon.rcm.scheduler;

import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.RemoteLiteDataDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.jpa.repository.RCMUserRepository;
import com.tricon.rcm.jpa.repository.RcmCompanyRepo;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.service.impl.RuleEngineService;
import com.tricon.rcm.util.Constants;

@Component
public class ClaimScheduler {

	private final Logger logger = LoggerFactory.getLogger(ClaimScheduler.class);

	@Autowired
	RuleEngineService ruleEngineService;
	
	@Autowired
	RCMUserRepository userRepo;
	
	@Autowired
	RcmCompanyRepo compRepo;
	
	@Autowired
	RcmCommonServiceImpl commonsService;

	@Scheduled(cron = "${scheduler.startcron}")
	@Transactional(rollbackFor = Exception.class)
	public void updateClaimAndInsuranceWithUnBilledClaimsFromRE() {

		logger.info("ClaimScheduler Run at :-" + new Date());
		RcmUser user= userRepo.findByEmail(Constants.SYSTEM_USER_EMAIL);
		
		ClaimSourceDto dto = new ClaimSourceDto();
		dto.setCompanyuuid(compRepo.findByName(Constants.COMPANY_NAME).getUuid());
		for(RcmOfficeDto officeDto: commonsService.getAllOffices()) {
			logger.info("ClaimScheduler For  " + officeDto.getName());
			dto.setOfficeuuid(officeDto.getUuid());
			dto.setSource(ClaimSourceEnum.EAGLESOFT.toString());
			
			ruleEngineService.pullAndSaveInsuranceFromRE(dto,user);
			ruleEngineService.pullAndSaveClaimFromRE(dto,user);
			//ruleEngineService.pullAndSaveRemoteLiteData(dto,user,logId);
			
			break;
		}
		
		
		logger.info("ClaimScheduler End at :-" + new Date());
	}

}
