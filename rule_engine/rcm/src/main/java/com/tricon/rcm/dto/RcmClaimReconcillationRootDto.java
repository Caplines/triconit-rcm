package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmClaimReconcillationRootDto {

	String message;
    public List<RcmReconcillationDatas> datas = new ArrayList<>();
}
