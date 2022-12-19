package com.tricon.ruleengine.dto;

import java.util.ArrayList;

public class RcmClaimRootDto {

	public String message;
	public ArrayList<RcmClaimDataDto> datas;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<RcmClaimDataDto> getDatas() {
		return datas;
	}

	public void setDatas(ArrayList<RcmClaimDataDto> datas) {
		this.datas = datas;
	}

}
