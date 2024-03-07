package com.tricon.rcm.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.rcm.service.impl.RuleEngineService;

@Component
public class PostingBucketScheduler {

	private final Logger logger = LoggerFactory.getLogger(PostingBucketScheduler.class);
	
	
	@Scheduled(cron = "${scheduler.posting.startcron}")
	@Transactional(rollbackFor = Exception.class)
	public void moveClaimsToPosting() {
		
		logger.info("Claim Posting Scheduler Run at :-" + new Date());
		
	}
	
}
