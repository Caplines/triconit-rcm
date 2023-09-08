package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmClaimAppointmentRootDto {

	String message;
    public List<RcmClaimAppointmentDatas> datas = new ArrayList<>();
}
