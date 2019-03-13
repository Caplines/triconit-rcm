package com.tricon.ruleengine.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.tricon.ruleengine.utils.Constants;

/**
 * 
 * @author Deepak.Dogra
 *
 */
public class EnhancedValidateTxNumReportDto extends EnhancedBaseReportDto {

	private String ct;

	private int ctCount;

	public String getCt() {
		return ct;
	}

	public void setCt(String ct) {
		this.ct = ct;
		this.ctCount = 0;
		if (this.ct != null) {
			Set<String> s = new HashSet<>();
			s.addAll(Arrays.asList(this.ct.split(Constants.EN_REP_COUNT_SEP)));
			this.ctCount = s.size();
			s = null;
		}
		this.ct = "";
	}

	public int getCtCount() {
		return ctCount;
	}

	public void setCtCount(int ctCount) {
		this.ctCount = ctCount;
	}

}
