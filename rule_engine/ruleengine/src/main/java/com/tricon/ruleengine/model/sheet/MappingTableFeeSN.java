package com.tricon.ruleengine.model.sheet;

public class MappingTableFeeSN {

	private String fSNameInIV;
	private String fsNameInES;
	
	
	public MappingTableFeeSN(String fSNameInIV, String fsNameInES) {
		super();
		this.fSNameInIV = fSNameInIV;
		this.fsNameInES = fsNameInES;
	}
	public String getfSNameInIV() {
		return fSNameInIV;
	}
	public void setfSNameInIV(String fSNameInIV) {
		this.fSNameInIV = fSNameInIV;
	}
	public String getFsNameInES() {
		return fsNameInES;
	}
	public void setFsNameInES(String fsNameInES) {
		this.fsNameInES = fsNameInES;
	}
	
	
}
