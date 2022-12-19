package com.tricon.rcm.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tricon.rcm.dto.ClaimSourceDto;
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

	// @Scheduled(cron = "0 51 21 * * *") //
	@Scheduled(cron = "${scheduler.startcron}")
	public void updateClaimAndInsuranceWithUnBilledClaimsFromRE() {

		ClaimSourceDto dto = new ClaimSourceDto();
		dto.setOfficeuuid(commonsService.getAllOffices().get(0).getUuid());
		dto.setSource(ClaimSourceEnum.EAGLESOFT.toString());
        System.out.println(commonsService.getAllOffices().get(0).getName());
		ruleEngineService.pullInsuranceFromRE(dto);
		logger.info("ClaimScheduler Run at :-" + new Date());
		ruleEngineService.pullClaimFromRE(dto);
		ruleEngineService.pullRemoteLiteDate(dto);
	}

}
