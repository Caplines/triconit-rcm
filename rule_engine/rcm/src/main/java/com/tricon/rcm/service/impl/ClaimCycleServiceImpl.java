package com.tricon.rcm.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.ClaimCycle;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmTeam;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.ClaimSteps;
import com.tricon.rcm.enums.ClaimStatusEnum;
import com.tricon.rcm.jpa.repository.ClaimCycleRepo;
import com.tricon.rcm.util.ClaimUtil;

@Service
public class ClaimCycleServiceImpl {

	private final Logger logger = LoggerFactory.getLogger(ClaimCycleServiceImpl.class);

	@Autowired
	ClaimCycleRepo claimCycleRepo;

	public void createNewClaimCycle(RcmClaims claim,String status,String nextAction, RcmTeam team,
			RcmUser user) {
		
		ClaimCycle newcycle =ClaimUtil.createCycle(claim, status,nextAction, team, user);
		claimCycleRepo.save(newcycle);
	}

	public ClaimCycle createNewClaimCycleWithOldStatus(RcmClaims claim, RcmTeam team, RcmUser user,String newStatus,
			String nextAction) {
		String status = newStatus;
		if (status==null) {
			ClaimStatusEnum oldStatus = ClaimStatusEnum.getById(claim.getCurrentStatus());
			if (oldStatus!=null) {
				status=oldStatus.getType();
			}
		}
		if (nextAction==null) {
			ClaimStatusEnum oldNextAction = ClaimStatusEnum.getById(claim.getNextAction());
			if (oldNextAction!=null) {
				nextAction=oldNextAction.getType();
			}
		}
		
		ClaimCycle newcycle =ClaimUtil.createCycle(claim, status,nextAction, team, user);
		newcycle.setId(claimCycleRepo.save(newcycle).getId());
		logger.info("new Cycle Created.. with Status:"+ status);
		return newcycle;

	}
	
	public List<ClaimSteps> getClaimCycle(String claimUUid) {
		return claimCycleRepo.getClaimCycle(claimUUid);
	}

}
