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
	
	public void saveClaimLog(RcmClaimLog log, RcmUser user, RcmOffice off, String source, int logStatus,
			int newClaimCt) {
		log.setCreatedBy(user);
		log.setOffice(off);
		log.setSource(source);
		log.setStatus(logStatus);
		log.setNewClaimsCount(newClaimCt);
		rcmClaimLogRepo.save(log);
	}
	
}
