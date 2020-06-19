package com.tricon.ruleengine.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.OnlyId;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingInputDto;
import com.tricon.ruleengine.dto.ScrappingSiteDetailsDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.service.ScrappingFullDataService;
import com.tricon.ruleengine.service.ScrappingService;
import com.tricon.ruleengine.utils.Constants;

@RestController
public class ScrappingController {
	
	static Class<?> clazz = ScrappingController.class;
	//static class ScrappingInputDtoList extends ArrayList<ScrappingInputDto> { };
	
	@Autowired ScrappingService sService;
	@Autowired ScrappingFullDataService  fullService;
	
	
	
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

	@CrossOrigin
	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/scrapsiteud/{stype}/{uuid}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> scrapSite(@PathVariable String uuid,@PathVariable int stype)  {
		Map<String, ScrappingSiteDetailsDto> map=new HashMap<>();
		ScrappingSiteDetailsDto sd=  sService.getScrappingSiteDetailsDetailSDto(stype, uuid);
		if (sd!=null) map.put("data", sd);
		else map.put("no data", null);
		
		RuleEngineLogger.generateLogs(clazz, "ScrappingController", Constants.rule_log_debug, null);
			 return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", map));

	}


	@CrossOrigin
	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/getsitenametoparsefulldata", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> getSiteNametoParseFulldata()  {
		List<?> list=null;
		try {
			list = fullService.getSiteNames();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		RuleEngineLogger.generateLogs(clazz, "ScrappingController", Constants.rule_log_debug, null);
			 return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", list));

	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/getsitedetailstoparsefulldata", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> getSiteDetailstoParseFulldata(@RequestBody ScrappingFullDataDetailDto dto)  {
		ScrappingFullDataDetailDto data=null;
		try {
			data = fullService.getScrappingDetails(dto.getSiteId(),dto.getOfficeId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		RuleEngineLogger.generateLogs(clazz, "ScrappingController", Constants.rule_log_debug, null);
			 return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", data));

	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/parsefulldata", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> parseFullData(@RequestBody ScrappingFullDataDetailDto dto)  {
		 String data=null;
		try {
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String res ="";//fullService.findRunningStatus(dto);//no need for now uncomment if server has issue
			if (res.equals("")) {
				data=fullService.parseFullDataAndSaveDetails(dto, authentication.getName());
				//data="Started";
			}else  data= "One Scrap Procedure Already Running for "+dto.getSiteName()+".\n Please wait till it finishes.";;
				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		RuleEngineLogger.generateLogs(clazz, "ScrappingController", Constants.rule_log_debug, null);
			 return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", data));

	}

	@CrossOrigin
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@RequestMapping(value = "/parsefulldataProcessInfo", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> parsefulldataProcessInfo(@RequestBody OnlyId onlyId)  {
		RuleEngineLogger.generateLogs(clazz, "ScrappingController", Constants.rule_log_debug, null);
			 return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", fullService.getScrappingFullDataManagmentProcessCount(onlyId.getId())+""));

	}

}
