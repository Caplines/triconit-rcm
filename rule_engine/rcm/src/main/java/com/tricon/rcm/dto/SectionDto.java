package com.tricon.rcm.dto;

import com.tricon.rcm.db.entity.RcmSectionCategory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class SectionDto {

	private String sectionName;
	private Integer sectionId;
	private RcmSectionCategory sectionCategory;
	private String sectionDisplayName;
	private boolean active;
}
