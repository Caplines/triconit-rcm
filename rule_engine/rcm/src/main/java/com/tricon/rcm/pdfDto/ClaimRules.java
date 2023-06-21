package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class ClaimRules {
	private int ruleId;
	private String message;
	private int messageType;
	private String ruleName;
	private String ruleDesc;
	private String manualAuto;
	private String remark;
	private String sectionName;
	private String ruleType;
	private int srNo;
}
