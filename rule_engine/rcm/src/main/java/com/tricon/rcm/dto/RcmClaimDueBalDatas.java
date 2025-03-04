package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmClaimDueBalDatas {

	String officeName;
    List<DueBalDto> data= new ArrayList<>();
}
