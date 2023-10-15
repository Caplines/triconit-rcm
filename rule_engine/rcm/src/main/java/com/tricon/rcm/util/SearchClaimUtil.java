package com.tricon.rcm.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tricon.rcm.enums.AgeBracketEnum;
import com.tricon.rcm.enums.ClaimStatusSearchEnum;

public class SearchClaimUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchClaimUtil.class);

	private static final String selectColumns = "select off.name as officeName,claims.claim_uuid as claimUuid,claims.claim_id as claimId,claims.patient_id as patientId,claims.dos as dos"
			+ ",claims.patient_name as patientName,claims.claim_status_type_id as statusType"
			+ ",insurance.name as primaryInsurance,secinsurance.name as secondaryInsurance"
			+ ",insuranceT.name prName,secinsuranceT.name secName"
			+ ",case when claims.dos is not null then DATEDIFF(sysdate(),claims.dos) else -1 end as claimAge"
			+ ",timely_fil_lmt_dt as timelyFilingLimitData,claims.submitted_total as billedAmount"
			+ ",claims.prim_total_paid primTotal,claims.sec_submitted_total secTotal"
			+ ",prime_sec_submitted_total primeSecSubmittedTotal ";

	private static final String countColumn = "select count(*) ";
	
	private static final String orderBy = " order by claims.dos asc ";

	private static final String fromClause = "from rcm_claims claims "
			+ "left join rcm_insurance insurance on insurance.id=claims.prim_insurance_company_id "
			+ "left join rcm_insurance_type insuranceT on insuranceT.id=insurance.insurance_type_id "
			+ "left join rcm_insurance secinsurance on secinsurance.id=claims.sec_insurance_company_id "
			+ "left join rcm_insurance_type secinsuranceT on secinsuranceT.id=secinsurance.insurance_type_id "
			+ "left join rcm_team team on team.id=claims.current_team_id "
			+ "inner join office off on off.uuid=claims.office_id "
			+ "inner join company comp on comp.uuid=off.company_id ";

	public static StringBuilder setClientUuid(List<String> clientUuid, StringBuilder searchQuery) {
		searchQuery.append(" where comp.uuid in(");
		for (int i = 0; i < clientUuid.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(clientUuid.get(i));
			searchQuery.append("'");
			if (i < clientUuid.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")");
		return searchQuery;
	}

	public static StringBuilder setOfficeUuid(List<String> officeUuid, StringBuilder searchQuery) {
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
	
	public static StringBuilder setClaimStatus(List<String> claimStatus, StringBuilder searchQuery) {
		//For now we only have billed -> pending =0 and unbilled -> pending=1
		ClaimStatusSearchEnum.getStatusByStatus("s");
		searchQuery.append(" and claims.pending in(");
		for (int i = 0; i < claimStatus.size(); i++) {
			//searchQuery.append("'");
			searchQuery.append(ClaimStatusSearchEnum.getStatusByStatus(claimStatus.get(i)));
			//searchQuery.append("'");
			if (i < claimStatus.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")");
		return searchQuery;
	}

	public static StringBuilder setPatientId(String patientId, StringBuilder searchQuery) {

		return searchQuery.append(" and claims.patient_id='" + patientId + "'");
	}

	public static StringBuilder setClaimId(String claimId, StringBuilder searchQuery) {

		return searchQuery.append(" and (claims.claim_id ='" + claimId + "_P' OR claims.claim_id = '" + claimId + "_S' "
				+ " OR claims.claim_id like '%" + Constants.HYPHEN + Constants.ARCHIVE_PREFIX + claimId
				+ "_P%' OR claims.claim_id like '%" + Constants.HYPHEN + Constants.ARCHIVE_PREFIX + claimId + "_S%')");
	}

	public static StringBuilder setDateRange(String startDate, String endDate, StringBuilder searchQuery) {

		return searchQuery.append(" and CAST(dos as DATE) between STR_TO_DATE('" + startDate
				+ "', '%Y-%m-%d') and  STR_TO_DATE('" + endDate + "', '%Y-%m-%d')");
	}

	public static StringBuilder setArchiveStatus(Boolean showArchive, StringBuilder searchQuery) {
		if (showArchive) {
			int archiveStatus = Constants.CLAIM_ARCHIVE_PREFIX_CANNOT_SUBMITED;
			searchQuery.append(" and claims.current_state=" + archiveStatus + "");
		} else {
			// archiveStatus = Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED;
		}
		return searchQuery;
	}

	public static StringBuilder setInsuranceName(List<String> insuranceName, StringBuilder searchQuery) {
		searchQuery.append(" and (LOWER(insurance.name) in(");
		for (int i = 0; i < insuranceName.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(insuranceName.get(i).toLowerCase());
			searchQuery.append("'");
			if (i < insuranceName.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(") or (LOWER(secinsurance.name) in(");
		for (int i = 0; i < insuranceName.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(insuranceName.get(i).toLowerCase());
			searchQuery.append("'");
			if (i < insuranceName.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")))");
		return searchQuery;
	}

	public static StringBuilder setInsuranceType(List<String> insuranceType, StringBuilder searchQuery) {
		searchQuery.append(" and (LOWER(insuranceT.name) in(");
		for (int i = 0; i < insuranceType.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(insuranceType.get(i).toLowerCase());
			searchQuery.append("'");
			if (i < insuranceType.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(") or (LOWER(secinsuranceT.name) in(");
		for (int i = 0; i < insuranceType.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(insuranceType.get(i).toLowerCase());
			searchQuery.append("'");
			if (i < insuranceType.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append("))) ");
		return searchQuery;
	}

	public static StringBuilder setProviderName(List<String> providerName, StringBuilder searchQuery) {
		searchQuery.append(" and LOWER(claims.provider_on_claim) in(");
		for (int i = 0; i < providerName.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(providerName.get(i).toLowerCase());
			searchQuery.append("'");
			if (i < providerName.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")");
		return searchQuery;
	}

	public static StringBuilder setProviderType(List<String> providerType, StringBuilder searchQuery) {
		searchQuery.append(" and LOWER(claims.claim_type) in(");
		for (int i = 0; i < providerType.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(providerType.get(i).toLowerCase());
			searchQuery.append("'");
			if (i < providerType.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")");
		return searchQuery;
	}

	public static StringBuilder setResponsibleTeam(List<Integer> responsibleTeam, StringBuilder searchQuery) {
		searchQuery.append(" and claims.current_team_id in(");
		for (int i = 0; i < responsibleTeam.size(); i++) {
			searchQuery.append("'");
			searchQuery.append(responsibleTeam.get(i));
			searchQuery.append("'");
			if (i < responsibleTeam.size() - 1) {
				searchQuery.append(", ");
			}
		}
		searchQuery.append(")");
		return searchQuery;
	}

	public static StringBuilder setAgeCategory(List<Integer> ageCategory, StringBuilder searchQuery) {

		// if length is zero then return
		if (ageCategory.size() == 0) {
			return searchQuery;
		}
		if (ageCategory.size() == 1) {
			int ageType = AgeBracketEnum.getAgeByValue(ageCategory.get(0));
			if (ageType == 1) {
				searchQuery.append(" and (DATEDIFF(CURDATE(), claims.dos) <="
						+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE) + ")");
			} else if (ageType == 2) {
				searchQuery.append(" and (DATEDIFF(CURDATE(), claims.dos) > "
						+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE)
						+ " AND DATEDIFF(CURDATE(), claims.dos) <= "
						+ AgeBracketEnum.getRangeByValue(ageType, Constants.MAX_RANGE) + ")");
			} else if (ageType == 3) {

				searchQuery.append(" and (DATEDIFF(CURDATE(), claims.dos) > "
						+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE)
						+ " AND DATEDIFF(CURDATE(), claims.dos) <= "
						+ AgeBracketEnum.getRangeByValue(ageType, Constants.MAX_RANGE) + ")");
			} else if (ageType == 4) {
				searchQuery.append(" and (DATEDIFF(CURDATE(), claims.dos) >"
						+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE) + ") ");
			} else {
			}
		} else {
			int index = 0;
			for (int age : ageCategory) {
				if (index == 4) // we have only 4 ageBrackets so.
					break;
				if (index == 0)
					searchQuery.append(" and("); // first time we add and
				else
					searchQuery.append(" or "); // second time we add or and combine the query with or
				int ageType = AgeBracketEnum.getAgeByValue(age);
				if (ageType == 1) {

					searchQuery.append(" (DATEDIFF(CURDATE(), claims.dos) <="
							+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE) + ")");
				}
				if (ageType == 2) {

					searchQuery.append(" (DATEDIFF(CURDATE(), claims.dos) > "
							+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE)
							+ " AND DATEDIFF(CURDATE(), claims.dos) <= "
							+ AgeBracketEnum.getRangeByValue(ageType, Constants.MAX_RANGE) + ")");
				}
				if (ageType == 3) {

					searchQuery.append(" (DATEDIFF(CURDATE(), claims.dos) > "
							+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE)
							+ " AND DATEDIFF(CURDATE(), claims.dos) <= "
							+ AgeBracketEnum.getRangeByValue(ageType, Constants.MAX_RANGE) + ")");
				}
				if (ageType == 4) {

					searchQuery.append(" (DATEDIFF(CURDATE(), claims.dos) > "
							+ AgeBracketEnum.getRangeByValue(ageType, Constants.MIN_RANGE) + ")");
				}
				index++;
			}

			searchQuery.append(" )");

		}
		return searchQuery;
	}

	public static String generateFinalQuery(StringBuilder searchQuery) {
		
		logger.info("FinalQuery:"+selectColumns + fromClause + searchQuery.toString());
		return selectColumns + fromClause + searchQuery.toString()  + orderBy ;
	}

	public static String generateCountQuery(StringBuilder searchQuery) {
		
		logger.info("CountQuery:"+countColumn + fromClause + searchQuery.toString());
		return countColumn + fromClause + searchQuery.toString();
	}

}
