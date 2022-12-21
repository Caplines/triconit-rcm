package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class RemoteLietStatusCount {

	int acceptedCount;
	int rejectedCount;
	int duplicateCount;
	int printedCount;
}
