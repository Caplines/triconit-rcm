package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class ServiceLaevelCodeDataModel {

	private String remarkUuid;
	private String serviceCode;
	private String name;
	private String description;
	private String value;
	private int messageType;
	private String remark;
	private String manualAuto;
	private String answer;
	private String message;
	private int ruleId;
}
