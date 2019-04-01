package com.tricon.ruleengine.dto;

/**
 * 
 * @author Deepak.Dogra
 *
 */
public class EnhancedValidateBatchReportDto  extends EnhancedBaseReportDto{
	
	private String ivfId;
	private String pid;
	private String pname;
	private String name;
	
	
	public String getIvfId() {
		return ivfId;
	}

	public void setIvfId(String ivfId) {
		this.ivfId = ivfId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
