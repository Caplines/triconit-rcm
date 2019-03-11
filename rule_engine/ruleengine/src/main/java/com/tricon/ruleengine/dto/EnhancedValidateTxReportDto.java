package com.tricon.ruleengine.dto;

/**
 * 
 * @author Deepak.Dogra
 *
 */
public class EnhancedValidateTxReportDto extends EnhancedBaseReportDto {

	
	private String ivfId;
	private String txP;
	private String pid;
	private String pname;
	
	
	public String getIvfId() {
		return ivfId;
	}
	public void setIvfId(String ivfId) {
		this.ivfId = ivfId;
	}
	public String getTxP() {
		return txP;
	}
	public void setTxP(String txP) {
		this.txP = txP;
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
	
	
	
	
}
