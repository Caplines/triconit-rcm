package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ProductionAgeWiseDto {

	private String officeName;
	private int countForAgeRange1; // age 0-30
	private int countForAgeRange2;// age 31-60
	private int countForAgeRange3;// age 61-90
	private int countForAgeRange4;// age 90+
	
	public ProductionAgeWiseDto(String officeName, int countForRange1, int countForRange2, int countForRange3,
			int countForRange4) {
		super();
		this.officeName = officeName;
		this.countForAgeRange1 = countForRange1;
		this.countForAgeRange2 = countForRange2;
		this.countForAgeRange3 = countForRange3;
		this.countForAgeRange4 = countForRange4;
	}
	
}
