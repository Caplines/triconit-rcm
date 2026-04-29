package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CredentialDataAnesthesia {

	String srNo;            //A
	String providers;       //B
	String providerCodes;   //C
	String provideStatus;   //D
	String d9230Nirtrous;   //E
	String d9248Anesthesia; //F
	String d9245Sedation;   //G - NEW
	String empty;           //H - was G
	String firstHomeD0145;  //I - was H
	String fDHEffectiveDate;//J - was I
	String updatedWithMCNA; //K - was J
	String updatedWithDQ;   //L - was K
	String updatedWithUHC;  //M - was L
	String remark;          //N - was M

	public CredentialDataAnesthesia() {
	}

	public CredentialDataAnesthesia(String srNo, String providers, String providerCodes, String provideStatus,
			String d9230Nirtrous, String d9248Anesthesia, String d9245Sedation, String empty, String firstHomeD0145,
			String fDHEffectiveDate, String updatedWithMCNA, String updatedWithDQ, String updatedWithUHC,
			String remark) {
		super();
		this.srNo = srNo;
		this.providers = providers;
		this.providerCodes = providerCodes;
		this.provideStatus = provideStatus;
		this.d9230Nirtrous = d9230Nirtrous;
		this.d9248Anesthesia = d9248Anesthesia;
		this.d9245Sedation = d9245Sedation;
		this.empty = empty;
		this.firstHomeD0145 = firstHomeD0145;
		this.fDHEffectiveDate = fDHEffectiveDate;
		this.updatedWithMCNA = updatedWithMCNA;
		this.updatedWithDQ = updatedWithDQ;
		this.updatedWithUHC = updatedWithUHC;
		this.remark = remark;
	}
}