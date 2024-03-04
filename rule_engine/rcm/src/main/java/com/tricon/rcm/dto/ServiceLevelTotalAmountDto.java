package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceLevelTotalAmountDto {

	private double totalAdjustmentAmount;
	private double totalBtpAmount;
	private List<ServiceLevelRequestBodyDto> serviceLevelBody;
}
