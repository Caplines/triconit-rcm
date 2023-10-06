package com.tricon.rcm.util;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.tricon.rcm.dto.SearchClaimResponseDto;

@Repository
public class SearchClaimUtil {

	@PersistenceContext
	private EntityManager entityManager;

	private final String selectColumns = "select off.name as officeName,claims.claim_id as claimId,claims.patient_id as patientId,claims.dos as dos"
			+ ",claims.patient_name as patientName,claims.claim_status_type_id as statusType"
			+ ",insurance.name as primaryInsurance,secinsurance.name as secondaryInsurance"
			+ ",insuranceT.name prName,secinsuranceT.name secName"
			+ ",case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge"
			+ ",timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount"
			+ ",claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal"
			+ ",prime_sec_submitted_total primeSecSubmittedTotal ";
	
	private final String countColumn = "select count(*) ";

	private final String fromClause = "from rcm_claims claims "
			+ "left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ "left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id "
			+ "left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ "left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "inner join office off on off.uuid=claims.office_id "
			+ "inner join company comp on comp.uuid=off.company_id "
			+ "inner join rcm_claim_assignment assign on claims.claim_uuid=assign.claim_id";

	public StringBuffer setClientUuid(List<String> clientUuid, StringBuffer searchQuery) {
		searchQuery.append(" where comp.uuid in(");
		for (int i = 0; i < clientUuid.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(clientUuid.get(i));
			searchQuery.append("'");
			if (i < clientUuid.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")")
				.append(" and pending=true and assign.active=1 and (primary_status = "
						+ Constants.Primary_Status_Primary + " or primary_status = "
						+ Constants.Primary_Status_Primary_submit + " )");
		return searchQuery;
	}

	public StringBuffer setOfficeUuid(List<String> officeUuid, StringBuffer searchQuery) {
		searchQuery.append(" and off.uuid in(");
		for (int i = 0; i < officeUuid.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(officeUuid.get(i));
			searchQuery.append("'");
			if (i < officeUuid.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")");
		return searchQuery;
	}

	public StringBuffer setPatientId(String patientId, StringBuffer searchQuery) {

		return searchQuery.append(" and claims.patient_id='" + patientId + "'");
	}

	public StringBuffer setClaimId(String claimId, StringBuffer searchQuery) {

		return searchQuery.append(
				" and (claims.claim_id like '%" + claimId + "%_P' OR claims.claim_id like '%" + claimId + "%_S')");
	}


	public StringBuffer setDateRange(String startDate, String endDate, StringBuffer searchQuery) {

		return searchQuery.append(" and CAST(dos as DATE) between STR_TO_DATE('" + startDate
				+ "', '%Y-%m-%d') and  STR_TO_DATE('" + endDate + "', '%Y-%m-%d')");
	}
	
	public StringBuffer setArchiveStatus(Boolean showArchive, StringBuffer searchQuery) {
		int archiveStatus = 0;
		if (showArchive) {
			archiveStatus = Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED;
		} else {
			archiveStatus = Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED;
		}
		return searchQuery.append(" and claims.current_state=" + archiveStatus + "");
	}

	public List<SearchClaimResponseDto> buildSearchQuery(StringBuffer searchQuery, int pageNumber,
			int totalRecordsperPage) {
		String finalQuery = this.generateFinalQuery(searchQuery);
		List<SearchClaimResponseDto> searchDto = null;
		try {
			synchronized (entityManager) {
				Query query = entityManager.createNativeQuery(finalQuery);
				query.setFirstResult((pageNumber - 1) * totalRecordsperPage);
				query.setMaxResults(totalRecordsperPage);
				searchDto = query.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchDto;
	}

	private String generateFinalQuery(StringBuffer searchQuery) {
		return selectColumns + fromClause + searchQuery.toString();
	}
	
	public long generateCountQuery(StringBuffer searchQuery) {
		String finalQuery = countColumn + fromClause + searchQuery.toString();
		long counts = 0;
		try {
			synchronized (entityManager) {
			Query query = entityManager.createNativeQuery(finalQuery);
			BigInteger countResult = (BigInteger) query.getSingleResult();
			return countResult.longValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return counts;
	}


}
