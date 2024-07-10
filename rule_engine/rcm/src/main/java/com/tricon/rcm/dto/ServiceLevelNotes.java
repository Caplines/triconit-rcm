package com.tricon.rcm.dto;

import java.util.Objects;

import lombok.Data;

@Data
public class ServiceLevelNotes {

	private String serviceCode;
	private String notes;
	private String createdBy;
	private String teamName;
	private String createdDate;
	private String tooth;
	private String surface;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceLevelNotes other = (ServiceLevelNotes) obj;
		return Objects.equals(createdBy, other.createdBy) && Objects.equals(notes, other.notes)
				&& Objects.equals(serviceCode, other.serviceCode) && Objects.equals(surface, other.surface)
				&& Objects.equals(tooth, other.tooth);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(createdBy, notes, serviceCode, surface, tooth);
	}
	
}
