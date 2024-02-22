package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class EobLink {

	String claimUuid;
	String link;
	String status;
	boolean saved;
	String extension;

}
