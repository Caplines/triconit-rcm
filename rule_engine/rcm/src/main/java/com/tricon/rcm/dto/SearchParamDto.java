package com.tricon.rcm.dto;

import java.util.Set;

import lombok.Data;

@Data
public class SearchParamDto {

	Set<String> insuranceNames;
	Set<String> insuranceTypes;
	Set<String> providerTypes;//Specialty
	Set<String> providerNames;
	
}
