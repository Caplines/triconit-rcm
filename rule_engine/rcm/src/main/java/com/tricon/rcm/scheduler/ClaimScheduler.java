package com.tricon.rcm.scheduler;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.ClaimSourceDto;

import com.tricon.rcm.dto.RcmOfficeDto;
import com.tricon.rcm.dto.TimelyFilingLimitDto;
import com.tricon.rcm.enums.ClaimSourceEnum;
import com.tricon.rcm.enums.ClaimTypeEnum;
import com.tricon.rcm.enums.RcmTeamEnum;
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
		RcmCompany comp= compRepo.findByName(Constants.COMPANY_NAME);
		dto.setCompanyuuid(comp.getUuid());
		List<TimelyFilingLimitDto> li= ruleEngineService.pullTimelyFilingLmtMappingFromSheet(comp);
		for(RcmOfficeDto officeDto: commonsService.getAllOffices()) {
			logger.info("ClaimScheduler For  " + officeDto.getName());
			dto.setOfficeuuid(officeDto.getUuid());
			dto.setSource(ClaimSourceEnum.EAGLESOFT.toString());
			int log=-1;
			//ruleEngineService.pullAndSaveInsuranceFromRE(dto,user);
			String[] status = ruleEngineService.pullAndSaveClaimFromRE(dto,user,li,ClaimTypeEnum.P,comp,null,log).split("___");
			
			try {
			if (status.length==2) log=Integer.parseInt(status[1]);
			}catch(Exception p) {
				
			}
			ruleEngineService.pullAndSaveClaimFromRE(dto,user,li,ClaimTypeEnum.S,comp,null,log);
			//ruleEngineService.pullAndSaveRemoteLiteData(dto,user,logId);
			ruleEngineService.pullClaimDetailsFromES(comp,null);
			//break;
		}
		
		//Assign unassinedClaims
		ruleEngineService.assignedUnsAssignedClaimsByTeam(comp.getUuid(),user,RcmTeamEnum.BILLING.getId());
		//Pull Claim Details From Rue Engine
		logger.info("ClaimScheduler End at :-" + new Date());
	}

}
