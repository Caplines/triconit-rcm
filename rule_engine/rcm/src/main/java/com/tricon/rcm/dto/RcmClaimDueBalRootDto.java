package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmClaimDueBalRootDto {

	String message;
    public List<RcmClaimDueBalDatas> datas = new ArrayList<>();
}
