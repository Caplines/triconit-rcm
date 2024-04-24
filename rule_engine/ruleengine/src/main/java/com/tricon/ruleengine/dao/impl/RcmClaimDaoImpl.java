package com.tricon.ruleengine.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.RcmClaimDao;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.RCMQuerySubData;
import com.tricon.ruleengine.dto.RcmClaimDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.utils.Constants;

@Repository
public class RcmClaimDaoImpl extends BaseDaoImpl implements RcmClaimDao{

	static Class<?> clazz = RcmClaimDaoImpl.class;
	@Override
	public List<Object> getRcmClaimData(RcmClaimDto d, Office office) {
		Session s=null;
		List<Object> data=new ArrayList<>();
		String finalQuery="",queryFor="";
		queryFor=d.getQueryName();
		switch(queryFor)
		{
		    case Constants.QUERY_FOR_RCMCALIM_1:
		    	finalQuery="select off.name,SUBSTRING_INDEX(SUBSTRING_INDEX(cl.claim_id, '_', 1), ' ', -1) AS claim_id,cl.patient_id,cl.dos,"+
		    			"CASE  WHEN cl.claim_id LIKE '%_P' THEN prime_sec_submitted_total ELSE sec_submitted_total  END  estimatedamount,"+
		    			"CASE  WHEN cl.claim_id LIKE '%_P' THEN 'Primary' ELSE 'Secondary'   END as claimType,"+
		    			"CASE  WHEN cl.claim_id LIKE '%_P' THEN pins.name ELSE sins.name   END as insurancename,"+
		    			"CASE  WHEN cl.claim_id LIKE '%_P' THEN pinst.name ELSE sinst.name   END as insurancetype,"+
		    			"CASE  WHEN rcsd.updated_date is null THEN  DATE_FORMAT( rcsd.created_date, '%m/%d/%Y') ELSE DATE_FORMAT( rcsd.updated_date, '%m/%d/%Y')   END as submissiodate,"+
		    			"us.first_name,us.last_name,CASE  WHEN rc.id is null THEN 'Rule Not Run' ELSE 'Rule Ran'   END as runenginerun "+
		    			" from rcm_claims cl inner join office off on off.uuid=cl.office_id "+
		    			" inner join company cmp on cmp.uuid=off.company_id "+
		    			" inner join rcm_claims_submission_details rcsd on rcsd.claim_id=cl.claim_uuid "+
		    			" inner join rcm_user us on rcsd.submitted_by=us.uuid "+
		    			" left join rcm_insurance pins on pins.id = cl.prim_insurance_company_id  left join "+
		    			" rcm_insurance sins on sins.id = cl.sec_insurance_company_id  left join "+
		    			" rcm_insurance_type pinst on pins.insurance_type_id = pinst.id "+
		    			" left join rcm_insurance_type sinst on sins.insurance_type_id = sinst.id "+
		    			" left join reports_claim rc on rc.claim_id=SUBSTRING_INDEX(SUBSTRING_INDEX(cl.claim_id, '_', 1), ' ', -1) and rc.patient_id=cl.patient_id "+
		    			" where cl.pending =false and cl.current_state=0 and cmp.name='"+d.getClient()+"' "+(office==null?"":" and cl.office_id='"+office.getUuid()+"' ")+" and IF(rcsd.updated_date is null, rcsd.created_date, rcsd.updated_date)"+
		    			" between  STR_TO_DATE('"+d.getDate1()+" 00:00:00', '%m/%d/%Y %H:%i:%s') AND STR_TO_DATE('"+d.getDate2()+" 23:59:59', '%m/%d/%Y %H:%i:%s')" +
		    			" "; 
		    	break;
		    case Constants.QUERY_FOR_RCMCALIM_AUDITED:
		    	finalQuery="select off.name,SUBSTRING_INDEX(SUBSTRING_INDEX(cl.claim_id, '_', 1), ' ', -1) AS claim_id,cl.patient_id,cl.dos,"+
		    			"DATE_FORMAT( rca.created_date, '%m/%d/%Y') as reviewDate,cl.claim_uuid as uuid, '' as e1,'' as e2,'' as e3,'' as e4,'' as e5,'' as e6,'' as e7,'' as e8 "+//added e's to Populate Empty Array  
		    			" from rcm_claim_assignment rca inner join rcm_claims cl on cl.claim_uuid=rca.claim_id inner join office off on off.uuid=cl.office_id "+
		    			" inner join company cmp on cmp.uuid=off.company_id "+
		    			" where cl.current_state=0 and cmp.name='"+d.getClient()+"' "+(office==null?"":" and cl.office_id='"+office.getUuid()+"' ")+
		    			" and rca.active=false and rca.action_name = 'Reviewed' and rca.current_team_id=7 and rca.System_comment='Claim Transfered To Team( From 3 to 7)' "+
		    			(d.getDateCheckType().equals("dos")? "and cl.dos between  STR_TO_DATE('"+d.getDate1()+" 00:00:00', '%m/%d/%Y %H:%i:%s') AND STR_TO_DATE('"+d.getDate2()+" 23:59:59', '%m/%d/%Y %H:%i:%s')":" and rca.created_date between  STR_TO_DATE('"+d.getDate1()+" 00:00:00', '%m/%d/%Y %H:%i:%s') AND STR_TO_DATE('"+d.getDate2()+" 23:59:59', '%m/%d/%Y %H:%i:%s')") +
		    			" "; 
		    	break;
		    case Constants.QUERY_FOR_RCMCALIM_FROM_A_TO_B:
		    	finalQuery="select off.name,SUBSTRING_INDEX(SUBSTRING_INDEX(cl.claim_id, '_', 1), ' ', -1) AS claim_id,cl.patient_id,cl.dos,"+
		    			"DATE_FORMAT( rca.created_date, '%m/%d/%Y') as reviewDate,cl.claim_uuid as uuid, '' as e1,'' as e2,'' as e3,'' as e4,'' as e5,'' as e6,'' as e7,'' as e8 "+//added e's to Populate Empty Array  
		    			" from rcm_claim_assignment rca inner join rcm_claims cl on cl.claim_uuid=rca.claim_id inner join office off on off.uuid=cl.office_id "+
		    			" inner join company cmp on cmp.uuid=off.company_id "+
		    			" where cl.current_state=0 and cmp.name='"+d.getClient()+"' "+(office==null?"":" and cl.office_id='"+office.getUuid()+"' ")+
		    			" and rca.active=false  and rca.System_comment='Claim Transfered To Team( From "+d.getTeam1()+" to "+d.getTeam2()+")' "+
		    			(d.getDateCheckType().equals("dos")? "and cl.dos between  STR_TO_DATE('"+d.getDate1()+" 00:00:00', '%m/%d/%Y %H:%i:%s') AND STR_TO_DATE('"+d.getDate2()+" 23:59:59', '%m/%d/%Y %H:%i:%s')":" and rca.created_date between  STR_TO_DATE('"+d.getDate1()+" 00:00:00', '%m/%d/%Y %H:%i:%s') AND STR_TO_DATE('"+d.getDate2()+" 23:59:59', '%m/%d/%Y %H:%i:%s')") +
		    			" "; 
		    	break;
	   }
		
		try {
	          if(finalQuery!=null && !finalQuery.isEmpty()) {
	          s=getSession();
			  Query q=s.createSQLQuery(finalQuery);
			  data=q.list();
			  }
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally{
				if(s!=null) {
				closeSession(s);
				}
			}
			return data;
	}
	@Override
	public List<RCMQuerySubData> getAuditQueryFieldsFromClaimData_1(String claims) {
		Session session = getSession();
		List<RCMQuerySubData> cL = null;
		try {
            String query="SELECT claim_id,name,service_code,case when answer='Complete' Then 'Attached' when answer='Not Available' Then 'Not Available' else 'Other' End as val"+
            		" FROM rcm_claims_service_rule_val r where name in ('Sedation Record Availibility','Consent Form for Major Service','Provider Notes','CRA Form Availability') "+
            		" and claim_id in ("+claims+")";
			cL=session.createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(RCMQuerySubData.class)). list();
		} finally {
			closeSession(session);
		}
		return cL;
	}
	
	@Override
	public List<RCMQuerySubData> getAuditQueryFieldsFromClaimData_2(String claims) {
		Session session = getSession();
		List<RCMQuerySubData> cL = null;
		try {
            String query="SELECT claim_id,rule_id,case when rule_id = 320 and message_type=3 Then 'Yes'"+
            		" when  rule_id = 320 and message_type<>3 Then 'No'"+
            		" when  rule_id = 319 and message_type=1 Then 'Other'"+
            		" when  rule_id = 319 and message_type=2 Then 'Attached'"+
            		" when  rule_id = 319 and message_type=3 Then 'Not Available' "+
            		" else '' End as message_type"+
            		" FROM rcm_claim_rule_validation r where rule_id in (319,320) and claim_id in ("+claims+")";
			cL=session.createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(RCMQuerySubData.class)). list();
		} finally {
			closeSession(session);
		}
		return cL;
	}
	
	

}
