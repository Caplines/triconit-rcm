package com.tricon.rcm.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ClaimConditionEnum {

	
	BTP_GT_0(1,"BTP > 0"),
	BTP_LT_0(2,"BTP <= 0"),
	NOT_SETTLED_IN_TIME(3,"Not settled in time"),
	Adjustment_GT_500(4,"Adjustment > $500"),
	Adjustment_Approval_Pending(5,"Adjustment > $500"),
	Claim_having_primary_secondary_insurance(6,"Claim having primary and secondary insurance");
	
	final private String remark;
	final private int id;
	
	private ClaimConditionEnum(int id,String remark) {
	   this.remark = remark;
		this.id = id;
	}

	

	public String getRemark() {
		return remark;
	}



	public int getId() {
		return id;
	}

	public static ClaimConditionEnum getClaimConditionEnumById(int id) {
		Optional<ClaimConditionEnum> claimConditionEnum = Arrays.stream(values()).filter(x -> x.getId() == id).findFirst();
		if (claimConditionEnum.isPresent())
			return claimConditionEnum.get();
		else
			return null;
	}
	
	
	
}
