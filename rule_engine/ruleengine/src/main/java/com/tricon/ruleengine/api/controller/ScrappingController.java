package com.tricon.ruleengine.api.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.ScrappingInputDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.service.ScrappingService;
import com.tricon.ruleengine.utils.Constants;

@RestController
public class ScrappingController {
	
	static Class<?> clazz = ScrappingController.class;
	//static class ScrappingInputDtoList extends ArrayList<ScrappingInputDto> { };
	
	@Autowired ScrappingService sService;
	
	
	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/scrapsite", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> scrapSite(@RequestBody ScrappingInputDto dto)  {
     //@RequestBody ScrappingInputDto dto
		//ScrappingInputDto dto=new ScrappingInputDto();
		// dto.setTreatmentPlanId("22095");
		//dto.setOfficeId("be2c3847-aaae-11e8-8544-8c16451459cd");
		//dto.setSiteId(1);
		
		
		Map<String, List<?>> map=null;
		try {
			map = sService.scrapSite(dto);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RuleEngineLogger.generateLogs(clazz, "ScrappingController", Constants.rule_log_debug, null);
			 return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));

	}



}
