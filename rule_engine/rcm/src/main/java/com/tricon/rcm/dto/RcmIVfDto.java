package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmIVfDto {

    private String claimUuid;
    private String ivfId;
    private String tpId;
}
