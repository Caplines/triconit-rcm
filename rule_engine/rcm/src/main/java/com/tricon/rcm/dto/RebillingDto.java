package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RebillingDto extends RebillingDtoMain{
	private List<RebillingDtoSub> allRebillingDto= new ArrayList<>();
}
