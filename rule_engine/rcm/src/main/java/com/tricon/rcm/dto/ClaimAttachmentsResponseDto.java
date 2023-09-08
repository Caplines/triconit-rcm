package com.tricon.rcm.dto;

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
	private String msg;
	private boolean status;
	
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
