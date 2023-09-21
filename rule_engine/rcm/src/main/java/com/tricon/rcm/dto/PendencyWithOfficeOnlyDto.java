package com.tricon.rcm.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class PendencyWithOfficeOnlyDto {

	String officeName;
	//List<Integer> counts;
	//List<Date> dates;
	HashMap<String, Object> counts1;
	HashMap<String, Object> dates1;
	HashMap<String, Object> datesPending;
	String clientName;
	
}
