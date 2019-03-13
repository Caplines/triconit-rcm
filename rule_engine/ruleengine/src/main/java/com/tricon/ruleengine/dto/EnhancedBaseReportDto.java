package com.tricon.ruleengine.dto;

import com.tricon.ruleengine.api.enums.HighLevelReportMessageStatusEnum;
import com.tricon.ruleengine.utils.Constants;

public class EnhancedBaseReportDto {

	private Integer pass;
	private Integer fail;
	private Integer alert;
	private String resultsum;
	private String office;

	public Integer getPass() {
		return pass;
	}

	public void setPass(Integer pass) {
		this.pass = pass;
	}

	public Integer getFail() {
		return fail;
	}

	public void setFail(Integer fail) {
		this.fail = fail;
	}

	public Integer getAlert() {
		return alert;
	}

	public void setAlert(Integer alert) {
		this.alert = alert;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getResultsum() {
		return resultsum;
	}

	public void setResultsum(String resultsum) {
		//12TYPE1;1TYPE3;35TYPE2
		this.fail=this.pass=this.alert=0;
		String[] ct=resultsum.split(Constants.EN_REP_COUNT_SEP);
		if (ct!= null && ct.length>0) {
			for(String c:ct) {
				
				String[] x=c.split(Constants.EN_REP_TYPE_SEP);
                if (c.contains(Constants.EN_REP_TYPE_SEP+HighLevelReportMessageStatusEnum.PASS.getStatus()+"")) {
				    this.pass=Integer.parseInt(x[0]);	
				}else if (c.contains(Constants.EN_REP_TYPE_SEP+HighLevelReportMessageStatusEnum.FAIL.getStatus()+"")) {
					this.fail=Integer.parseInt(x[0]);	
				}else if (c.contains(Constants.EN_REP_TYPE_SEP+HighLevelReportMessageStatusEnum.ALERT.getStatus()+"")) {
					this.alert=Integer.parseInt(x[0]);	
				}
			}
		}
		this.resultsum = "";//set it blank;
	}
	
	
	
	
}
