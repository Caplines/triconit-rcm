package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceLevelTotalAmountDto {

	private double totalAdjustmentAmount;
	private double totalBtpAmount;
	private double totalPaidAmount;
	private double balanceFromEsBeforePosting;
	private double balanceFromEsAfterPosting;
	private boolean reconciliationPass;
	private double totalCreditAdjustmentAmount;
	private double totalDebitAdjustmentAmount;
	private List<ServiceLevelRequestBodyDto> serviceLevelBody;
}
