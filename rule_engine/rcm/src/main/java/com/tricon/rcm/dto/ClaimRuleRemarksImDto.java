package com.tricon.rcm.dto;

import java.util.Date;

import com.tricon.rcm.dto.customquery.ClaimRuleRemarksDto;

import lombok.Data;


public class ClaimRuleRemarksImDto implements ClaimRuleRemarksDto{

	
	String remark;
	Date cd;
	String fName;
	String lName;
	String ruleId;



	@Override
	public String getFName() {
		// TODO Auto-generated method stub
		return fName;
	}

	@Override
	public String getLName() {
		// TODO Auto-generated method stub
		return lName;
	}

	@Override
	public String getRemark() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getCd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRuleId() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
