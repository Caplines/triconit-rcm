package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceValidationDataDto {

   boolean claimFound;
   List<RcmClaimsServiceRuleValidationDto> dto;
   String esDate;
   String esTime;
}
