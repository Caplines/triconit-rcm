package com.tricon.ruleengine.dao;

import java.util.List;

import com.tricon.ruleengine.dto.RcmClaimDto;
import com.tricon.ruleengine.model.db.Office;

public interface RcmClaimDao {

	public List<Object> getRcmClaimData(RcmClaimDto d,Office office);
}
