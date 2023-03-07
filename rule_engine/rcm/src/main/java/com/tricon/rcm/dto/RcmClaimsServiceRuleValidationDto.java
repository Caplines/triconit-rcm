package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RcmClaimsServiceRuleValidationDto {

	private String remarkUuid;

	private String serviceCode;

	private String name;

	private String description;

	private String value;

	private int messageType;

	private String remark;

}
