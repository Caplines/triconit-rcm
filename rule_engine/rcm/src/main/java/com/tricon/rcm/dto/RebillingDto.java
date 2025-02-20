package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


public class RebillingDto extends RebillingDtoMain{
	private List<RebillingDtoSub> allRebillingDto= new ArrayList<>();

	public List<RebillingDtoSub> getAllRebillingDto() {
		return allRebillingDto;
	}

	public void setAllRebillingDto(List<RebillingDtoSub> allRebillingDto) {
		this.allRebillingDto = allRebillingDto;
	}
	
	
}
