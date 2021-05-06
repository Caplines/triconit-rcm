package com.tricon.ruleengine.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Collections2;
import com.tricon.ruleengine.api.enums.HighLevelReportMessageStatusEnum;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.ReportDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dto.DigitizationRuleEngineResult;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleMessageDetailDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.dto.RuleReportResponseDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.ReportService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;

@Transactional
@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	ReportDao rd;
	
	@Autowired
	OfficeDao od;
	
	@Autowired
	TreatmentValidationDao tvd;

	@Autowired
	@Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

	
	
	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	

	@Override
	public List<ReportResponseDto> getReports(ReportDto dto) {

		List<ReportResponseDto> reports = rd.getReports(dto);
		// TODO Auto-generated method stub

		return reports;

	}

	@Override
	public List<?> getEnancedReport(EnhancedReportDto dto) {
		List<?> reports = rd.getEnancedReport(dto);
		// TODO Auto-generated method stub
		return reports;
	}
	

	@Override
	public RuleReportResponseDto getRuleReport(RuleReportDto dto) {

		List<RuleMessageDetailDto> li = rd.getRuleReports(dto);
		RuleReportResponseDto d = new RuleReportResponseDto();
		d.setUniqueAllPats(0);
		d.setUniqueFailPats(0);
		d.setUniquePassPats(0);
		d.setUniqueAlertPats(0);
		List<RuleMessageDetailDto> fi= new ArrayList<>();
		List<RuleMessageDetailDto> fiFail= new ArrayList<>();
		
		if (li != null) {
			Set<String> all = new HashSet<>();
			Set<String> fail = new HashSet<>();
			Set<String> pass = new HashSet<>();
			Set<String> alert = new HashSet<>();
            
            //Check query and 	RuleMessageDetailDto equals method for any issue		
			for (RuleMessageDetailDto dt : li) {

				all.add(dt.getPatientId()+"-"+dt.getOfficeName());
				if (!fi.contains(dt)) {
					fi.add(dt);
			    }

			}
			for (RuleMessageDetailDto f : fi) {
				if (f.getMessageType() == 2) {
					pass.add(f.getPatientId()+"-"+f.getOfficeName());
				}else if (f.getMessageType() == 1) {
					if (!fiFail.contains(f))
						fiFail.add(f);
					fail.add(f.getPatientId()+"-"+f.getOfficeName());
				}else if (f.getMessageType() == 3) {
					alert.add(f.getPatientId());
				}
			}
			
			
			
			/*
			
			for (RuleMessageDetailDto dt : li) {

				all.add(dt.getPatientId());
				if (dt.getMessageType() == 2) {
					pass.add(dt.getPatientId());
				} else if (dt.getMessageType() == 1) {
					if (!fi.contains(dt))
						fi.add(dt);
					fail.add(dt.getPatientId());
				} else if (dt.getMessageType() == 3) {
					alert.add(dt.getPatientId());
				}
			}
			*/	

			
			d.setUniqueAllPats(all.size());
			d.setUniqueFailPats(fail.size());
			d.setUniquePassPats(pass.size());
			d.setUniqueAlertPats(alert.size());
			d.setData(fiFail);
		}

		return d;
	}
	
	@Override
	public List<DigitizationRuleEngineResult> getReportsForGoogleSheet(ReportDto dto){
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;
		
		String sheetName="";
		if (dto.getSheetTabId()==Constants.Digitization_of_RE_Results_TP) {
			sheetName="Tx. Plan Validation (Pass/Fail)";
		}
		else if (dto.getSheetTabId()==Constants.Digitization_of_RE_Results_Cl) {
			sheetName="Claim Validation (Pass/Fail)";
			dto.setmType("c");
		}
		else if (dto.getSheetTabId()==Constants.Digitization_of_RE_Results_pat) {
			sheetName="Patient ID Wise Search";
		}
		List<ReportResponseDto> reports = null;
		List<DigitizationRuleEngineResult> finalData=  new ArrayList<>();
		
		try {
	    List<Rules> ruleList = tvd.getAllActiveRules();	
		dto=ConnectAndReadSheets.readGoogleReportsDigitationSheet(Constants.Digitization_of_RE_Results_SpreadSheeId,
			sheetName, dto.getSheetTabId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, dto);
		Office off = od.getOfficeByName(dto.getOffficeName(),user.getCompany().getUuid());
		dto.setOfficeId(off.getUuid());
		
		reports = rd.getReports(dto);
		
		Map<Integer,List<String[]>> ruleMap = new HashMap<>();
		   //List<DigitizationRuleEngineResult> finalData1= new ArrayList<>();
		   DigitizationRuleEngineResult r= new DigitizationRuleEngineResult();
		   
		   if (dto.getSheetTabId()==Constants.Digitization_of_RE_Results_TP) {
			   r.setPatientId("Patient ID");
			   r.setDocumentId("Tx. Plan ID");
			   r.setDos("Tx. Plan Date");
			   r.setRunDate("RE Run Date");
			   r.setId("IV ID");
			      
		   }else if (dto.getSheetTabId()==Constants.Digitization_of_RE_Results_Cl) {
			   r.setPatientId("Patient ID");
			   r.setDocumentId("Claim ID");
			   r.setDos("Date of Service");
			   r.setRunDate("RE Run Date");
			   r.setId("IV ID");
			      
		   }else if (dto.getSheetTabId()==Constants.Digitization_of_RE_Results_pat) {
			   r.setPatientId("Patient ID");
			   r.setDocumentId("Claim ID /\n Tx Plan ID");
			   r.setDos("Date of Service /\n Tx. Planning Date");
			   r.setRunDate("RE Run Date");
			   r.setId("IV ID");
			      
		   }
		   r.setMessageType(4);// for Heading..
		   for(Rules rule:ruleList) {
			   if (rule.getId()==20) continue;
			   List<String[]> x= new ArrayList<>();
			   x.add(new String[] {6+"" ,rule.getName()});
			   ruleMap.put(rule.getId(), x);
			   
			   
		   }
		   r.setRuleMap(ruleMap);
		   finalData.add(r);
		   String inv=" DATA ";
		   String k = "";
		   Map<String, List<ReportResponseDto>> map = new LinkedHashMap<>();
		   List<ReportResponseDto> a = new ArrayList<>();
		      
		   if (reports != null) {
			   for (ReportResponseDto d : reports) {
					k = d.getRd_group_run() + "). Patient ID- " + d.getPatient_id() + " Patient Name- "
							+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " " + inv + d.getTreatement_plan_id()
							+ " Run By-" + d.getName();
					if (map.containsKey(k)) {
						// if the key has already been used,
						// we'll just grab the array list and add the value to it
						a = (List<ReportResponseDto>) map.get(k + "");
						d.setError_message(d.getError_message().replaceAll("\\<.*?\\>", ""));//Remove all HTML TAGS
					//System.out.println("AAA+"+d.getError_message());
						a.add(d);
					} else {
						// if the key hasn't been used yet,
						// we'll create a new ArrayList<String> object, add the value
						// and put it in the array list with the new key
						a = new ArrayList<>();
						d.setError_message(d.getError_message().replaceAll("\\<.*?\\>", ""));//Remove all HTML TAGS
						a.add(d);
						//System.out.println("BBB+"+d.getError_message());
						map.put(k + "", a);
					}

				}
				
				for (Map.Entry<String, List<ReportResponseDto>> entry : map.entrySet()) {
					
					//String codeH=entry.getKey();
				     List<ReportResponseDto> eL=entry.getValue();
			    	 DigitizationRuleEngineResult res= new DigitizationRuleEngineResult();	
			    	 ruleMap = new HashMap<>();
			    	 for(Rules rule:ruleList) {
				    	 
				    	 if (eL!=null && eL.size()>0) {
				    		 
				    		 res.setId(eL.get(0).getIvf_form_id()); 
				    		 res.setName(eL.get(0).getName());
				    		 res.setRunDate(eL.get(0).getRd_created_date());
				    		 res.setOfficeName(eL.get(0).getOffice_name());
				    		 res.setDocumentId(eL.get(0).getTreatement_plan_id());
				    		 res.setPatientId(eL.get(0).getPatient_id());
				    		 res.setDos(eL.get(0).getDos());
				    		 res.setMessageType(eL.get(0).getMessageType().intValue());
				    		 
				    	 }
					     Collection<ReportResponseDto> ruleGen = Collections2.filter(eL,
				 				el -> (el.getRule_id() == rule.getId()
				 						));
				    	 if (ruleGen!=null && ruleGen.size()>0) {
				    	 for(ReportResponseDto rdto:ruleGen) {
				    		 res.setMessage(rdto.getError_message());
				    		 if (ruleMap.containsKey(rule.getId())) {
				    			 List<String[]> dr=ruleMap.get(rule.getId());
				    			 dr.add(new String[] {rdto.getMessageType().intValue()+"",rdto.getError_message()});
				    		 }else {
				    			 List<String[]>  dr= new ArrayList<>();
				    			 dr.add(new String[] {rdto.getMessageType().intValue()+"",rdto.getError_message()});
				    			 ruleMap.put(rule.getId(), dr);
				    		 }
				    		 /*
				    		 List<String> dr=ruleMap.get(rule.getId());
					    	 if (dr==null) {
					    		 dr= new ArrayList<>();
					    		 ruleMap.put(rule.getId(),dr);
					    	 }
					    	 dr.add(rdto.getError_message());
					    	 */
					    	
				    		 
				    	 }
				    	 
				     }else {
				    	 //res.setMessageType(0);
				    	 //that rule is missing // so write not Present
				    	// DigitizationRuleEngineResult res= new DigitizationRuleEngineResult();	 
				    	 if (ruleMap.containsKey(rule.getId())) {
			    			 List<String[]> dr=ruleMap.get(rule.getId());
			    			 dr.add(new String[] {"7","No  Applicable."});
			    		 }else {
			    			 List<String[]>  dr= new ArrayList<>();
			    			 dr.add(new String[] {"7","No  Applicable."});
			    			 ruleMap.put(rule.getId(), dr);
			    		 }
				     }
				    	
				     }
				     res.setRuleMap(ruleMap);
			    	 //if (inv.equals("TR. ID-")) finalData1.add(res);
			    	 //else  finalData2.add(res);
			    	 finalData.add(res);

				     
				     
					
				}
		   }
		   
		   ConnectAndReadSheets.updateGoogleReportsDigitationSheet(Constants.Digitization_of_RE_Results_SpreadSheeId,
				   sheetName,dto.getSheetTabId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, finalData);
		   
		

		}catch (Exception e) {
			e.printStackTrace();
		}
		return finalData;
	}

}
