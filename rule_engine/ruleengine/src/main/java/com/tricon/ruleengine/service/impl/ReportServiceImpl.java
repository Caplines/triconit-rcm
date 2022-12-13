package com.tricon.ruleengine.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import com.tricon.ruleengine.dao.CompanyDao;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.ReportDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.DigitizationRuleEngineResult;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleMessageDetailDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.dto.RuleReportResponseDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.model.db.Company;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.pdf.CaplineIVFFormDtoToXML;
import com.tricon.ruleengine.pdf.SelantPdfMainDto;
import com.tricon.ruleengine.pdf.SelantPdfPatDto;
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
	CompanyDao companyDao;

	@Autowired
	@Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

	@Value("${re.xslt.path}")
	private String XSLT_PATH;

	@Value("${re.xslt.sealant}")
	private String XSLT_FILE_SEAL;

	
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
	public RuleReportResponseDto getRuleReportAllMessage(RuleReportDto dto) {

		List<RuleMessageDetailDto> li = rd.getRuleReportsAll(dto);
		RuleReportResponseDto d = new RuleReportResponseDto();
		d.setUniqueAllPats(0);
		d.setUniqueFailPats(0);
		d.setUniquePassPats(0);
		d.setUniqueAlertPats(0);
		List<RuleMessageDetailDto> fi= new ArrayList<>();
		List<RuleMessageDetailDto> allMessagaes= new ArrayList<>();
		
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
			//Only Failed Messages are needed.
			for (RuleMessageDetailDto f : fi) {
				if (f.getMessageType() == 2) {
					//if (!allMessagaes.contains(f))
					//   allMessagaes.add(f);
					pass.add(f.getPatientId()+"-"+f.getOfficeName());
				}else if (f.getMessageType() == Constants.FAIL_MESSAGE_TYPE) {
					if (!allMessagaes.contains(f))
						allMessagaes.add(f);
					fail.add(f.getPatientId()+"-"+f.getOfficeName());
				}else if (f.getMessageType() == 3) {
					//if (!allMessagaes.contains(f))
				//		allMessagaes.add(f);
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
			d.setData(allMessagaes);
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
	    List<Rules> ruleListRed = new ArrayList<>();	
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
			   if (rule.getId()==20 || rule.getId()==2 || rule.getId()==3 
				||  rule.getId()==45 || rule.getId()==41 
				||rule.getId()==0 || rule.getId()==12 ||rule.getId()==51 || rule.getId()==52 || rule.getId()==43
				||rule.getId()==69 || rule.getId()==70 ||rule.getId()==71 || rule.getId()==72 || rule.getId()==73
				||rule.getId()==68) {
				   continue;
			   }
			   ruleListRed.add(rule);
			   List<String[]> x= new ArrayList<>();
			   x.add(new String[] {6+"" ,rule.getShortName()});
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
			    	 for(Rules rule:ruleListRed) {
				    	 
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
					     System.out.println("getRule_id---"+ rule.getId()+"----" +"****"+ruleGen.size() );
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
			    			 dr.add(new String[] {"7",""});
			    		 }else {
			    			 List<String[]>  dr= new ArrayList<>();
			    			 dr.add(new String[] {"7",""});
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
	
	
	public  Map<String,List<ReportResponseDto>> getReportsForSealant(ReportDto dto,boolean pdf){
		
		String pid="";
		 String cm="";
		 String[] pids=dto.getPatientId().split(",");
		 for(String p:pids) {
			 pid=pid+cm+ "'"+p+"'" ;
			 cm=",";
		 }
		dto.setPatientId(pid); 
		List<ReportResponseDto> reports = rd.getReportsForSealant(dto);
		Map<String,List<ReportResponseDto>> map = new HashMap<>();
		List<ReportResponseDto> a = new ArrayList<>();
		      
		   if (reports != null) {
			   for (ReportResponseDto d : reports) {
					if (map.containsKey(d.getPatient_id())) {
						a = (List<ReportResponseDto>) map.get(d.getPatient_id() + "");
						if (pdf)d.setError_message(d.getError_message().replaceAll("\\<.*?\\>", ""));//Remove all HTML TAGS
						a.add(d);
					} else {
						// if the key hasn't been used yet,
						// we'll create a new ArrayList<String> object, add the value
						// and put it in the array list with the new key
						a = new ArrayList<>();
						if (pdf)d.setError_message(d.getError_message().replaceAll("\\<.*?\\>", ""));//Remove all HTML TAGS
						a.add(d);
						//System.out.println("BBB+"+d.getError_message());
						map.put(d.getPatient_id() + "", a);
					}

				}
				
		
	   }
	 return map;
	}   
	
	@Override
	public Object[] generateSealntPDF(ReportDto dto) {
		
		Map<String,List<ReportResponseDto>> reports=getReportsForSealant(dto,true);
		Company cmp = companyDao.getCompanyByName(Constants.COMPANY_NAME);
		Office office = od.getOfficeByUuid(dto.getOfficeId(),cmp.getUuid());
		
		ByteArrayOutputStream o = null;
		SelantPdfMainDto mainDto= new SelantPdfMainDto();
		Map<String,SelantPdfPatDto> mpdf= new HashMap<>();
		Map<String,SelantPdfPatDto> mpdf68= new HashMap<>();
		Map<String,SelantPdfPatDto> mpdf73= new HashMap<>();
		String pid="";
		String cm="";
		 String[] pids=dto.getPatientId().split(",");
		 for(String p:pids) {
			 pid=pid+cm+ "'"+p+"'" ;
			 cm=",";
		 }
		
		for (Map.Entry<String, List<ReportResponseDto>> entry : reports.entrySet()) {
		     String paid=entry.getKey();
		     List<ReportResponseDto> eL=entry.getValue();
		     for(ReportResponseDto re:eL) {
		    	
		    	  
		    if (re.getRule_id()==69 || re.getRule_id()==70 || re.getRule_id()==71 || re.getRule_id()==72){
		    	if (mpdf.get(paid)==null) {
	    			 SelantPdfPatDto dt= new SelantPdfPatDto();
	    			 dt.setIvDate(re.getIv_date());
	    			 dt.setPatientId(re.getPatient_id());
	    			 dt.setName(re.getPatient_name());
	    			 dt.setfName(office.getName());
	    			 dt.setTe("");
	    			 mpdf.put(paid, dt);
	    	}
		       if (re.getRule_id()==69) {//Constants.RULE_ID_69
		    	   mpdf.get(paid).setTe(re.getError_message().replaceAll(",", ", "));
		    	   mpdf.get(paid).setMessType(re.getMessageType());
		    	}
		       if (re.getRule_id()==70) {//Constants.RULE_ID_70
		    	   mpdf.get(paid).setTne(re.getError_message().replaceAll(",", ", "));
		    	   mpdf.get(paid).setMessType(re.getMessageType());
		    	}
		       if (re.getRule_id()==71) {//Constants.RULE_ID_71
		    	   mpdf.get(paid).setTnea(re.getError_message().replaceAll(",", ", "));
		    	   mpdf.get(paid).setMessType(re.getMessageType());
		    	}
		       if (re.getRule_id()==72) {//Constants.RULE_ID_72
		    	   mpdf.get(paid).setTnef(re.getError_message().replaceAll(",", ", "));
		    	   mpdf.get(paid).setMessType(re.getMessageType());
		    	}
		     }
		        /*
		        if (re.getRule_id()==73) {//Constants.RULE_ID_73
		        	mpdf73.get(paid).setTe(re.getError_message());
		        	mpdf73.get(paid).setMessType(re.getMessageType());
			    }
			    */
		        if (re.getRule_id()==68) {//Constants.RULE_ID_68
		        		if (mpdf68.get(paid)==null) {
			    			 SelantPdfPatDto dt= new SelantPdfPatDto();
			    			 dt.setIvDate(re.getIv_date());
			    			 dt.setPatientId(re.getPatient_id());
			    			 dt.setName(re.getPatient_name());
			    			 dt.setfName(office.getName());
			    			 dt.setTe("");
			    			 mpdf68.put(paid, dt);
		        	}
		        	mpdf68.get(paid).setTe(mpdf68.get(paid).getTe()+re.getError_message()+"<br/>");
		        	mpdf68.get(paid).setMessType(re.getMessageType());
			    }
		     }
		     
		}//for map++
		
		for(String p:pids) {
			if (mpdf.get(p.replaceAll("'", ""))==null) {
				SelantPdfPatDto dt= new SelantPdfPatDto();
				dt.setPatientId(p.replaceAll("'", ""));
				dt.setTe("IV not available. Run the Scraping Tool"); 
				dt.setfName(office.getName());
				mpdf73.put(p.replaceAll("'", ""), dt);
			}
		}
		
		
		List<SelantPdfPatDto> lall= new ArrayList<>();
		List<SelantPdfPatDto> l73= new ArrayList<>();
		List<SelantPdfPatDto> l68= new ArrayList<>();
		for (Map.Entry<String, SelantPdfPatDto> entry : mpdf.entrySet()) {
			lall.add(entry.getValue());
		}
		mainDto.setDto(lall);
		
		for (Map.Entry<String, SelantPdfPatDto> entry : mpdf68.entrySet()) {
			l68.add(entry.getValue());
		}
		mainDto.setDto68(l68);
		
		for (Map.Entry<String, SelantPdfPatDto> entry : mpdf73.entrySet()) {
			l73.add(entry.getValue());
		}
		mainDto.setDto73(l73);
		
		Object[] obj=new Object[2]; 
		try {
				CaplineIVFFormDtoToXML xml = new CaplineIVFFormDtoToXML();
				String filePath = xml.convertToXML(mainDto, XSLT_PATH);
				File file = new File(filePath);
				String xslt=XSLT_FILE_SEAL;
				
				o = xml.createPdfStream(

						xml.createHtml(filePath, xslt), "");
				//for HTML
				//o=xml.createHtmlOut(filePath, xslt);
				
			    if (file!=null) file.delete(); 
				//To test html for issues
			   //o=xml.createHtmlOut(filePath, XSLT_FILE_SEAL);
				
				obj[1]=o;

		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				o.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return obj;
	}
	
	@Override
	public Object[] generateSealntPDByUIData(HashMap<String,List<TPValidationResponseDto>> rdto) {
		Object[] obj=new Object[2];
		 try {
			 SelantPdfMainDto mainDto= new SelantPdfMainDto();
			 mainDto.setcDate(Constants.SIMPLE_DATE_FORMAT.format(new Date()));
				Map<String,SelantPdfPatDto> mpdf= new HashMap<>();
				Map<String,SelantPdfPatDto> mpdf68= new HashMap<>();
				Map<String,SelantPdfPatDto> mpdf73= new HashMap<>();
			 String pid="";
			 for(Entry<String, List<TPValidationResponseDto>> entry : rdto.entrySet()) {
				 pid=pid+","+entry.getKey();
			 }
		   pid.replaceFirst(",", "");
		   for(Entry<String, List<TPValidationResponseDto>> entry : rdto.entrySet()) {
			List<TPValidationResponseDto> li=entry.getValue();
			 //for (Map.Entry<String, List<TPValidationResponseDto>> entry5 : map5.entrySet()) {
			//	List<TPValidationResponseDto >list= entry5.getValue();
			    //entry.getKey();
				for (TPValidationResponseDto re: li) {
	   			 	  
					if (re.getRuleId()==69 || re.getRuleId()==70 || re.getRuleId()==71 || re.getRuleId()==72){
						SelantPdfPatDto dt= new SelantPdfPatDto();
						   	if (mpdf.get(entry.getKey())==null) {
			    			 dt.setIvDate(re.getIvDone());
			    			 dt.setPatientId(entry.getKey());
			    			 dt.setName(re.getPatientName());
			    			 dt.setfName(re.getOff());
			    			 dt.setTe("");
			    			 mpdf.put(entry.getKey(), dt);
			    	}
				    if (re.getRuleId()==69 ) {
				    	 mpdf.get(entry.getKey()).setTe(re.getMessage().replaceAll("\\<.*?\\>", "").replaceAll(",", ", "));
				    	//dt.setTe("");
				    }else if (re.getRuleId()==70 ) {
				    	 mpdf.get(entry.getKey()).setTne(re.getMessage().replaceAll("\\<.*?\\>", "").replaceAll(",", ", "));
				    }else if (re.getRuleId()==71 ) {
				    	 mpdf.get(entry.getKey()).setTnea(re.getMessage().replaceAll("\\<.*?\\>", "").replaceAll(",", ", "));
				    }else if (re.getRuleId()==72 ) {
				    	 mpdf.get(entry.getKey()).setTnef(re.getMessage().replaceAll("\\<.*?\\>", "").replaceAll(",", ", "));
				    }
				    
					 
					  
				  }else if (re.getRuleId()==73){
					  if (mpdf73.get(entry.getKey())==null) {
						    SelantPdfPatDto dt= new SelantPdfPatDto();
						    dt.setIvDate(re.getIvDone());
			    			dt.setPatientId(entry.getKey());
			    			dt.setName(re.getPatientName());
			    			dt.setfName(re.getOff());
			    			dt.setTe("IV not available. Run the Scraping Tool"); 
							dt.setfName(re.getOff());
							mpdf73.put(entry.getKey(), dt);
			      }	
			    }else if (re.getRuleId()==68){
					  if (mpdf68.get(entry.getKey())==null) {
						  SelantPdfPatDto dt= new SelantPdfPatDto();
						    dt.setIvDate(re.getIvDone());
			    			dt.setPatientId(entry.getKey());
			    			dt.setName(re.getPatientName());
			    			dt.setfName(re.getOff());
						  mpdf68.put(entry.getKey(),dt);
					  }
						 mpdf68.get(entry.getKey()).setTe(mpdf68.get(entry.getKey()).getTe()+re.getMessage().replaceAll("\\<.*?\\>", "")+"<br/>");
				         mpdf68.get(entry.getKey()).setMessType(BigInteger.valueOf(HighLevelReportMessageStatusEnum.FAIL.getStatus()));
				         
			    }
			 
		     }
				
				List<SelantPdfPatDto> lall= new ArrayList<>();
				List<SelantPdfPatDto> l73= new ArrayList<>();
				List<SelantPdfPatDto> l68= new ArrayList<>();
				for (Map.Entry<String, SelantPdfPatDto> entryM : mpdf.entrySet()) {
					lall.add(entryM.getValue());
				}
				mainDto.setDto(lall);
				
				for (Map.Entry<String, SelantPdfPatDto> entryM : mpdf68.entrySet()) {
					l68.add(entryM.getValue());
				}
				mainDto.setDto68(l68);
				
				for (Map.Entry<String, SelantPdfPatDto> entryM : mpdf73.entrySet()) {
					l73.add(entryM.getValue());
				}
				
				if (l73.size()==0) {
					SelantPdfPatDto d= new SelantPdfPatDto();
					//d.setTe("No Patient found");
					//l73.add(d);
					
				}
				mainDto.setDto73(l73);
				
				ByteArrayOutputStream o = null;
				try {
						CaplineIVFFormDtoToXML xml = new CaplineIVFFormDtoToXML();
						String filePath = xml.convertToXML(mainDto, XSLT_PATH);
						File file = new File(filePath);
						String xslt=XSLT_FILE_SEAL;
						
						o = xml.createPdfStream(

								xml.createHtml(filePath, xslt), "");
						//for HTML
						//o=xml.createHtmlOut(filePath, xslt);
						
					    if (file!=null) file.delete(); 
						//To test html for issues
					   //o=xml.createHtmlOut(filePath, XSLT_FILE_SEAL);
						
						obj[1]=o;

				
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						o.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				} 
		 }catch (Exception e) {
				// TODO: handle exception
				 //return null;
			 e.printStackTrace();
			}

     return obj;
	}	


}
