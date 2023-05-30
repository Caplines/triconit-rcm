package com.tricon.rcm.dto;

import java.util.List;

import com.tricon.rcm.dto.customquery.AllPendencyDateDto;
import com.tricon.rcm.dto.customquery.AllPendencyDto;

import lombok.Data;

@Data
public class AllPendencyReportDto {

	List<AllPendencyDto> count;
	List<AllPendencyDateDto> dateCount;
	List<RcmOfficeDto> offices;
	List<PendencyDataCountDto> header;
	List<PendencyDataCountDto> rowData;
	List<Integer> headerNew;
	List<PendencyWithOfficeOnlyDto>  onlyOffice;
	
}
