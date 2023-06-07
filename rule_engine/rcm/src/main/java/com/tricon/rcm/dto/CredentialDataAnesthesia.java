package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CredentialDataAnesthesia {

	String srNo;//A
	String providers;//B
	String providerCodes;//C
	String provideStatus;//D
	String d9230Nirtrous;//E
	String d9248Anesthesia;//F
	String empty;//G
	String firstHomeD0145;//H
	String fDHEffectiveDate;//I
	String updatedWithMCNA;//J
	String updatedWithDQ;//K
	String updatedWithUHC;//L
	String remark;////M
	
	public CredentialDataAnesthesia() {
		
	}
	public CredentialDataAnesthesia(String srNo, String providers, String providerCodes, String provideStatus,
			String d9230Nirtrous, String d9248Anesthesia, String empty, String firstHomeD0145, String fDHEffectiveDate,
			String updatedWithMCNA, String updatedWithDQ, String updatedWithUHC, String remark) {
		super();
		this.srNo = srNo;
		this.providers = providers;
		this.providerCodes = providerCodes;
		this.provideStatus = provideStatus;
		this.d9230Nirtrous = d9230Nirtrous;
		this.d9248Anesthesia = d9248Anesthesia;
		this.empty = empty;
		this.firstHomeD0145 = firstHomeD0145;
		this.fDHEffectiveDate = fDHEffectiveDate;
		this.updatedWithMCNA = updatedWithMCNA;
		this.updatedWithDQ = updatedWithDQ;
		this.updatedWithUHC = updatedWithUHC;
		this.remark = remark;
	}
	
	

	
	
}
