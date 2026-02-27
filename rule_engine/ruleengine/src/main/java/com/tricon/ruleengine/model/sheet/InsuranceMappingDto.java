package com.tricon.ruleengine.model.sheet;

public class InsuranceMappingDto {

	private String sno;
	private String providers;
	private String providerStatus;
	private String d9230;
	private String d9248;
	private String d9245;
	private String empty;
	private String d0145;
	
	public InsuranceMappingDto(String sno, String providers, String providerStatus, String d9230, String d9245,String d9248,
			String empty, String d0145) {
		super();
		this.sno = sno;
		this.providers = providers;
		this.providerStatus = providerStatus;
		this.d9230 = d9230;
		this.d9245 = d9245;
		this.d9248 = d9248;
		this.empty = empty;
		this.d0145 = d0145;
	}


	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getProviders() {
		return providers;
	}
	public void setProviders(String providers) {
		this.providers = providers;
	}
	public String getProviderStatus() {
		return providerStatus;
	}
	public void setProviderStatus(String providerStatus) {
		this.providerStatus = providerStatus;
	}
	public String getD9230() {
		return d9230;
	}
	public void setD9230(String d9230) {
		this.d9230 = d9230;
	}
	public String getD9248() {
		return d9248;
	}
	public void setD9248(String d9248) {
		this.d9248 = d9248;
	}
	public String getD9245() {
		return d9245;
	}
	public void setD9245(String d9245) {
		this.d9245 = d9245;
	}
	public String getEmpty() {
		return empty;
	}
	public void setEmpty(String empty) {
		this.empty = empty;
	}
	public String getD0145() {
		return d0145;
	}
	public void setD0145(String d0145) {
		this.d0145 = d0145;
	}
	
	
	
}
