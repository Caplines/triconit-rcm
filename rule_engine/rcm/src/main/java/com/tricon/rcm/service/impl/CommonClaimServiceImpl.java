package com.tricon.rcm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.rcm.db.entity.RcmClaimLog;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.jpa.repository.RcmClaimLogRepo;

@Service
public class CommonClaimServiceImpl {

	@Autowired
	RcmClaimLogRepo rcmClaimLogRepo;
	
	/**
	 * 
	 * @param log
	 * @param user
	 * @param off
	 * @param source
	 * @param logStatus
	 * @param newClaimCt
	 * @param newPrimaryCt
	 * @param newSecondayCt
	 * @return
	 */
	public int saveClaimLog(RcmClaimLog log, RcmUser user, RcmOffice off, String source, int logStatus,
			int newClaimCt,int newPrimaryCt,int newSecondayCt,String message) {
		log.setCreatedBy(user);
		log.setOffice(off);
		log.setSource(source);
		log.setStatus(logStatus);
		log.setNewClaimsCount(newClaimCt);
		log.setNewClaimsPrimaryCount(newPrimaryCt);
		log.setNewClaimsSecodaryCount(newSecondayCt);
		log.setMessage(message);
		return rcmClaimLogRepo.save(log).getId();
	}
	
	
	
	
}
