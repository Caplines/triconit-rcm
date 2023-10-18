package com.tricon.rcm.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClaimAttachmentsResponseDto {

	private File file;
	private boolean isDeleted;
	private Integer id;
	private Integer attachmentId;
	private String message;
	private boolean status;
	private String uploadedBy;
	private Date uploadedDate;
	private String uploadedByTeam;
	private String uploadedByUserUuid;
	
	public class File{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}
