package com.tricon.ruleengine.api.enums;

public class ReportTypeEnum {

	
	private ReportType status;
    public enum ReportType {
        TreatmentId,
        PatientName,
        IvfId,
        //OfficeId,
        Date,
        DateFromTo,
        UserName,
        DateFromToUserName,
        ruledatasheet,
        sealant,
        Teamwise,
        TeamwiseDOS;
    }
 
    /*
    public boolean isDeliverable() {
        if (getStatus() == ReportType.READY) {
            return true;
        }
        return false;
    }
    */
	public ReportType getStatus() {
		return status;
	}

	public void setStatus(ReportType status) {
		this.status = status;
	}
    
    
}
