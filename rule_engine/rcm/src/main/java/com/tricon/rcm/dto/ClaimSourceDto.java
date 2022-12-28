package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

/**
 * This DTo will be used for Fetching Claims and Related Data From Rule Engine
 * @author Deepak.Dogra
 *
 */
@Data 
public class ClaimSourceDto {

	String officeuuid;
	String source;
	String password;
	List<String> officeuuids;
	
}
