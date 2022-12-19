package com.tricon.rcm.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RcmInsuranceRootDto {

	String message;
	public List<RcmInsuranceDatas> datas = new ArrayList<>();
}
