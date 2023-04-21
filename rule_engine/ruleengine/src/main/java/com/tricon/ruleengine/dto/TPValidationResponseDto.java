package com.tricon.ruleengine.dto;

public class TPValidationResponseDto {

	private int ruleId;
	private String ruleName;
	private String message;
	private String resultType;
	private String surface;
	private String tooth;
	private String serviceCode;
	private String patientName;
	private String ivDone;
	private String off;
	private String iName;
	
	
	
	
	public TPValidationResponseDto() {
		super();
		
	}
	
	public TPValidationResponseDto(int ruleId, String ruleName, String message, String resultType,
			String surface,String tooth,String serviceCode) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.message = message;
		this.resultType = resultType;
		this.surface=surface;
		this.tooth=tooth;
		this.serviceCode=serviceCode;
		
	}

	public TPValidationResponseDto(int ruleId, String ruleName, String message, String resultType,
			String surface,String tooth,String serviceCode,String patientName,String ivDone) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.message = message;
		this.resultType = resultType;
		this.surface=surface;
		this.tooth=tooth;
		this.serviceCode=serviceCode;
		this.patientName=patientName;
		this.ivDone=ivDone;
		
		
		
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getSurface() {
		return surface;
	}

	public void setSurface(String surface) {
		this.surface = surface;
	}

	public String getTooth() {
		return tooth;
	}

	public void setTooth(String tooth) {
		this.tooth = tooth;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getIvDone() {
		return ivDone;
	}

	public void setIvDone(String ivDone) {
		this.ivDone = ivDone;
	}

	public String getOff() {
		return off;
	}

	public void setOff(String off) {
		this.off = off;
	}

	public String getiName() {
		return iName;
	}

	public void setiName(String iName) {
		this.iName = iName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iName == null) ? 0 : iName.hashCode());
		result = prime * result + ((ivDone == null) ? 0 : ivDone.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((off == null) ? 0 : off.hashCode());
		result = prime * result + ((patientName == null) ? 0 : patientName.hashCode());
		result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
		result = prime * result + ruleId;
		result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
		result = prime * result + ((serviceCode == null) ? 0 : serviceCode.hashCode());
		result = prime * result + ((surface == null) ? 0 : surface.hashCode());
		result = prime * result + ((tooth == null) ? 0 : tooth.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TPValidationResponseDto other = (TPValidationResponseDto) obj;
		if (iName == null) {
			if (other.iName != null)
				return false;
		} else if (!iName.equals(other.iName))
			return false;
		if (ivDone == null) {
			if (other.ivDone != null)
				return false;
		} else if (!ivDone.equals(other.ivDone))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (off == null) {
			if (other.off != null)
				return false;
		} else if (!off.equals(other.off))
			return false;
		if (patientName == null) {
			if (other.patientName != null)
				return false;
		} else if (!patientName.equals(other.patientName))
			return false;
		if (resultType == null) {
			if (other.resultType != null)
				return false;
		} else if (!resultType.equals(other.resultType))
			return false;
		if (ruleId != other.ruleId)
			return false;
		if (ruleName == null) {
			if (other.ruleName != null)
				return false;
		} else if (!ruleName.equals(other.ruleName))
			return false;
		if (serviceCode == null) {
			if (other.serviceCode != null)
				return false;
		} else if (!serviceCode.equals(other.serviceCode))
			return false;
		if (surface == null) {
			if (other.surface != null)
				return false;
		} else if (!surface.equals(other.surface))
			return false;
		if (tooth == null) {
			if (other.tooth != null)
				return false;
		} else if (!tooth.equals(other.tooth))
			return false;
		return true;
	}
	
	

}
