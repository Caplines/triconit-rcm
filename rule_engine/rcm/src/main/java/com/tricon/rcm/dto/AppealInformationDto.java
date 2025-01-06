package com.tricon.rcm.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AppealInformationDto {
 
	 private String modeOfAppeal;
	 private String aiToolUsed;
	 private String remarks;
	 private String appealDocument;
	 private int sectionId;
	 private int id;
	 private Date createdDate;
}
