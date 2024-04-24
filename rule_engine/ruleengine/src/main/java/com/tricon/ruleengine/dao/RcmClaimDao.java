package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.RCMQuerySubData;
import com.tricon.ruleengine.dto.RcmClaimDto;
import com.tricon.ruleengine.model.db.Office;

public interface RcmClaimDao {

	public List<Object> getRcmClaimData(RcmClaimDto d,Office office);
	
	//'Sedation Record Availibility','Consent Form for Major Service','Provider Notes','CRA Form Availability
	public List<RCMQuerySubData> getAuditQueryFieldsFromClaimData_1(String claimIds);
	
	//Is Patients assigned to our facility? 320 YES - 3
	//Consent Form 319 Attached =2 , Not Available 3 Other 1
	public List<RCMQuerySubData> getAuditQueryFieldsFromClaimData_2(String claimIds);
}
