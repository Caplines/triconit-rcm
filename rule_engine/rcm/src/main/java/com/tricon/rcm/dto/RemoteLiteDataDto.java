package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class RemoteLiteDataDto {

	List<RemoteLiteDto> dataList;
	RemoteLietStatusCount statusCount;
}
