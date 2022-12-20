package com.tricon.rcm.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.ClaimSourceDto;
import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.service.impl.RcmCommonServiceImpl;
import com.tricon.rcm.service.impl.RuleEngineService;

@Component
public class ClaimScheduler {

	private final Logger logger = LoggerFactory.getLogger(ClaimScheduler.class);

	@Autowired
	RuleEngineService ruleEngineService;
	
	@Autowired
	RcmCommonServiceImpl commonsService;

	@Scheduled(cron = "${scheduler.startcron}")
	public void updateClaimAndInsuranceWithUnBilledClaimsFromRE() {

		logger.info("ClaimScheduler Run at :-" + new Date());
		
		ClaimSourceDto dto = new ClaimSourceDto();
		for(RcmOfficeDto officeDto: commonsService.getAllOffices()) {
			logger.info("ClaimScheduler For  " + officeDto.getName());
			dto.setOfficeuuid(officeDto.getUuid());
			dto.setSource(ClaimSourceEnum.EAGLESOFT.toString());
			
			ruleEngineService.pullRemoteLiteDate(dto);
	        ruleEngineService.pullIAndSaveInsuranceFromRE(dto);
			ruleEngineService.pullClaimFromRE(dto);
			
			break;
		}
		
		
		logger.info("ClaimScheduler End at :-" + new Date());
	}

}
