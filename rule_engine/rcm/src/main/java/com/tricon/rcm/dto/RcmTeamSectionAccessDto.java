package com.tricon.rcm.dto;

import java.util.List;

import com.tricon.rcm.db.entity.RcmSectionCategory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmTeamSectionAccessDto {

	private Integer teamId;
	private String teamName;
	private List<SectionData> sectionData;
	
	public static class SectionData{
		
		private Integer sectionId;
		private String sectionDisplayName;
		private String sectionName;
		private RcmSectionCategory sectionCategory;
		private Boolean editAccess;
		private Boolean viewAccess;
		private Boolean editAccessGlobalLevel;
		private Boolean viewAccessGlobalLevel;
		public Integer getSectionId() {
			return sectionId;
		}
		public void setSectionId(Integer sectionId) {
			this.sectionId = sectionId;
		}
		public Boolean getEditAccess() {
			return editAccess;
		}
		public void setEditAccess(Boolean editAccess) {
			this.editAccess = editAccess;
		}
		public Boolean getViewAccess() {
			return viewAccess;
		}
		public void setViewAccess(Boolean viewAccess) {
			this.viewAccess = viewAccess;
		}
		public String getSectionName() {
			return sectionName;
		}
		public void setSectionName(String sectionName) {
			this.sectionName = sectionName;
		}
		
		
		public RcmSectionCategory getSectionCategory() {
			return sectionCategory;
		}
		public void setSectionCategory(RcmSectionCategory sectionCategory) {
			this.sectionCategory = sectionCategory;
		}
		
		public String getSectionDisplayName() {
			return sectionDisplayName;
		}
		public void setSectionDisplayName(String sectionDisplayName) {
			this.sectionDisplayName = sectionDisplayName;
		}
		public Boolean getEditAccessGlobalLevel() {
			return editAccessGlobalLevel;
		}
		public void setEditAccessGlobalLevel(Boolean editAccessGlobalLevel) {
			this.editAccessGlobalLevel = editAccessGlobalLevel;
		}
		public Boolean getViewAccessGlobalLevel() {
			return viewAccessGlobalLevel;
		}
		public void setViewAccessGlobalLevel(Boolean viewAccessGlobalLevel) {
			this.viewAccessGlobalLevel = viewAccessGlobalLevel;
		}
		@Override
		public String toString() {
			return "SectionData [sectionId=" + sectionId + ", sectionDisplayName=" + sectionDisplayName
					+ ", sectionName=" + sectionName + ", sectionCategory=" + sectionCategory + ", editAccess="
					+ editAccess + ", viewAccess=" + viewAccess + ", editAccessGlobalLevel=" + editAccessGlobalLevel
					+ ", viewAccessGlobalLevel=" + viewAccessGlobalLevel + "]";
		}
		
		
		
	}
	
}
