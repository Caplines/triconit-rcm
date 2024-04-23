package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ProductionCurrentStatusWiseDto {

	private String officeName;
	private int pendingForReviewCount;
	private int PendingForBillingCount;
	private int billedCount;
	private int currentStatus4Count;
	private int closedCount;
	private int voidedCount;
	private int reBillingCount;
	private int ReviewedCount;
	private int submittedCount;
	public ProductionCurrentStatusWiseDto(String officeName, int pendingForReviewCount, int pendingForBillingCount,
			int billedCount, int currentStatus4Count, int closedCount, int voidedCount, int reBillingCount,
			int reviewedCount, int submittedCount) {
		super();
		this.officeName = officeName;
		this.pendingForReviewCount = pendingForReviewCount;
		PendingForBillingCount = pendingForBillingCount;
		this.billedCount = billedCount;
		this.currentStatus4Count = currentStatus4Count;
		this.closedCount = closedCount;
		this.voidedCount = voidedCount;
		this.reBillingCount = reBillingCount;
		ReviewedCount = reviewedCount;
		this.submittedCount = submittedCount;
	}
	
	

}
