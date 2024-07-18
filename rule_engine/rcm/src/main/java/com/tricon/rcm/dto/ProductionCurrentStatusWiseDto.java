package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ProductionCurrentStatusWiseDto {

	private String officeName;
	private String associateName;
	private int pendingForReviewCount;
	private int PendingForBillingCount;
	private int billedCount;
	private int closedCount;
	private int voidedCount;
	private int reBillingCount;
	private int reviewedCount;
	private int submittedCount;
	public ProductionCurrentStatusWiseDto(String officeName,String associateName, int pendingForReviewCount, int pendingForBillingCount,
			int billedCount, int closedCount, int voidedCount, int reBillingCount,
			int reviewedCount, int submittedCount) {
		super();
		this.officeName = officeName;
		this.associateName = associateName;
		this.pendingForReviewCount = pendingForReviewCount;
		PendingForBillingCount = pendingForBillingCount;
		this.billedCount = billedCount;
		this.closedCount = closedCount;
		this.voidedCount = voidedCount;
		this.reBillingCount = reBillingCount;
		this.reviewedCount = reviewedCount;
		this.submittedCount = submittedCount;
	}
	
	

}
