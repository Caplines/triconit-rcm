package com.tricon.rcm.pdfDto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceLevelCodeDataModel {
	private List<ServiceLaevelCodeDataModel>dto;
	private boolean claimFound;
	private String esDate;
}
