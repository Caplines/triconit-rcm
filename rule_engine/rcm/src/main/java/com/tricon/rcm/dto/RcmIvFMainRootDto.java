package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RcmIvFMainRootDto {

	String message;
	List<CaplineIVFFormDto> data;
}
