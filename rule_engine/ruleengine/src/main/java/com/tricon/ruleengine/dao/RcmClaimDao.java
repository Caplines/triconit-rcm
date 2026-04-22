package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.RCMQuerySubData;
import com.tricon.ruleengine.dto.RCMQuerySubData1;
import com.tricon.ruleengine.dto.RcmClaimDto;
import com.tricon.ruleengine.model.db.Office;

public interface RcmClaimDao {

	public List<Object> getRcmClaimData(RcmClaimDto d,Office office);
	
	//'Sedation Record Availibility','Consent Form for Major Service','Provider Notes','CRA Form Availability
	public List<RCMQuerySubData> getAuditQueryFieldsFromClaimData_1(String claimIds);
	
	//Is Patients assigned to our facility? 320 YES - 3
	//Consent Form 319 Attached =2 , Not Available 3 Other 1
	public List<RCMQuerySubData> getAuditQueryFieldsFromClaimData_2(String claimIds);
	
	//For NOTES
	public List<RCMQuerySubData1> getAuditQueryFieldsFromClaimData_3(String claimIds);
		
	public int getTeamIdByName(String teamName);

	/**
	 * Looks up provider fields stored in {@code rcm_claims} using patient ID and
	 * office UUID, preferring an exact {@code tp_id} match when available.
	 * Returns a two-element String array: [provider_on_claim, provider_on_claim_from_sheet].
	 * Either element may be null if the row or column is empty.
	 * Returns null if no matching row is found.
	 */
	String[] getProviderFieldsByTpId(String tpId, String patientId, String officeId);
}
