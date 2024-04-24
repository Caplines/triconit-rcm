package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmReconcillationDatas {

	String officeName;
    List<ClaimReconcillationDto> data= new ArrayList<>();
}
