package com.tricon.esdatareplication.util;

public class DataStatus {

	private StatusEnum statusEnum;

	public enum StatusEnum {

		DATA_CLOUD_STATUS(1, 0),
		CODE_WRITTEN_STATUS(1, 0),
		LOCAL_DATA_UPLOADED(1, 0),
		STATIC_TABLE(1,0);
		public final int YES;
		public final int NO;

		private StatusEnum(int YES, int NO) {

			this.YES = YES;
			this.NO = NO;
		}


	}

	public StatusEnum getStatusEnum() {
		return statusEnum;
	}

	public void setStatusEnum(StatusEnum statusEnum) {
		this.statusEnum = statusEnum;
	}

}
