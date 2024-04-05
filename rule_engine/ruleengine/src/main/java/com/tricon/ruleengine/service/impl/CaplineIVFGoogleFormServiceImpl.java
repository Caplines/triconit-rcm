package com.tricon.ruleengine.service.impl;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.RcmClaimDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.CaplineDataReplicationDto;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.GoogleReportsRDDTO;
import com.tricon.ruleengine.dto.RcmClaimDto;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.exception.RuleEngineException;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.pdf.CaplineIVFFormDtoToXML;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.IVFFormConversionUtil;

//import io.jsonwebtoken.lang.Arrays;

@Transactional(rollbackOn = Exception.class)
@Service
public class CaplineIVFGoogleFormServiceImpl implements CaplineIVFGoogleFormService {

	static Class<?> clazz = CaplineIVFGoogleFormServiceImpl.class;
	
	@Value("${re.admin}")
	private String amdinUserName;

	@Value("${re.xslt.path}")
	private String XSLT_PATH;

	@Value("${re.xslt.file}")
	private String XSLT_FILE;
	
	@Value("${re.xsltos.file}")
	private String XSLT_FILE_OS;
	
	@Value("${re.xsltortho.file}")
	private String XSLT_FILE_ORTHO;
	
	

	@Value("${re.xslt.filenew}")
	private String XSLT_FILE_NEW;
	
	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;


	@Autowired
	OfficeDao od;

	@Autowired
	UserDao userDao;

	@Autowired
	PatientDao patientDao;
	
	@Autowired
	RcmClaimDao rcmClaimDao;
	

	@Override
	public Object[] saveIVFFormData(CaplineIVFFormDto d,Office office,boolean ivf,IVFormType iVFormType) throws Exception {

		// copy value to DB Object
		/*
		 * As there is no logged-IN user Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication(); User user = null; if
		 * (authentication != null) { user =
		 * userDao.findUserByUsername(authentication.getName()); //
		 * Hibernate.initialize(user.getOffice()); } else { user =
		 * userDao.findUserByEmail(""); }
		 */
		Date date = new Date();
		User user = userDao.findUserByUsername(amdinUserName);
		Patient pat=null;
		//if (iVFormType.getName().equals(Constants.IV_GENERAL_FORM_NAME))
		 pat = IVFFormConversionUtil.copyValueToPatientFROMIVOSGoogle(d, office, date,iVFormType);
		//else if (iVFormType.getName().equals(Constants.IV_ORAL_SURGERY_FORM_NAME)) 
		//	pat = IVFFormConversionUtil.copyValueToPatientFROMOSGoogle(d, office, date,iVFormType);
		return saveAllData (pat,office,date,user,ivf,false,iVFormType);
	}
	
	@Override
	public Object[] saveIVFFormCheck(CaplineIVFFormDto d,Office office,boolean ivf,IVFormType iVFormType) throws Exception {

		// copy value to DB Object
		/*
		 * As there is no logged-IN user Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication(); User user = null; if
		 * (authentication != null) { user =
		 * userDao.findUserByUsername(authentication.getName()); //
		 * Hibernate.initialize(user.getOffice()); } else { user =
		 * userDao.findUserByEmail(""); }
		 */
		Date date = new Date();
		User user = userDao.findUserByUsername(amdinUserName);
		Patient pat=null;
		//if (iVFormType.getName().equals(Constants.IV_GENERAL_FORM_NAME))
		 pat = IVFFormConversionUtil.copyValueToPatientFROMIVOSGoogle(d, office, date,iVFormType);
		//else if (iVFormType.getName().equals(Constants.IV_ORAL_SURGERY_FORM_NAME)) 
		//	pat = IVFFormConversionUtil.copyValueToPatientFROMOSGoogle(d, office, date,iVFormType);
		return checkExistingIV (pat,office,date,user,ivf,false,iVFormType);
	}

	public Object[] saveAllData (Patient pat, Office office, Date date,User user,boolean ivf,boolean onlyInsert,IVFormType iVFormType) {
		
		Patient patd = patientDao.checkforPatientWithIdAndOfficeAndGeneralDate(pat.getPatientId(), office,pat,true,pat.getPatientDetails().iterator().next().getGeneralDateIVwasDone());
		if (patd==null) {
			patd = patientDao.checkforPatientWithId(pat.getPatientId(), office);
		}
		Object[] ob= new Object[2];
		ob[1]="Success";
		
		Integer r = 0;
		String generalDate="";
		String insuranceName="";
		String employerName="";
		int ivFormTypeId=0;
		boolean dumpCheck=false;
		if (pat.getPatientDetails()!=null && pat.getPatientDetails().size()>0) {
			Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
			PatientDetail x = iter.next();
			generalDate =x.getGeneralDateIVwasDone();
			insuranceName = x.getInsName();
			employerName = x.getEmployerName();
			ivFormTypeId=x.getiVFormType().getId();
		}
		try {
			if (patd == null) {

				patd = patientDao.savePatientDataWithDetailsAndHistory(pat, office, user, date);
			} else {
				//delete old history logic
				int oldPdid=-1;
				if (ivf) {//this is false for DUMP
				Set<PatientHistory> pholdset= patd.getPatientHistory();
				for (PatientDetail pd : patd.getPatientDetails()) {
					if (pd.getGeneralDateIVwasDone().equals(generalDate) && pd.getiVFormType().getId()==ivFormTypeId  
							&& pd.getEmployerName().toLowerCase().trim().equals(employerName.toLowerCase().trim())
							&& pd.getInsName().toLowerCase().trim().equals(insuranceName.toLowerCase().trim())
							) {
						oldPdid=pd.getId();	
					}
					
				}
				if (pholdset!=null && pholdset.size()>0) {
					List<String> l= new ArrayList<>();
					boolean fd=false;
					for(PatientHistory phold:pholdset) {
						if (phold.getPd()!=null && oldPdid ==phold.getPd().getId()) {
						l.add(phold.getId()+"");
						fd=true;
						}
				  }
					if(fd) {
						patientDao.deletePatientHistoryByIds(l.stream().toArray(String[]::new));	
						
					}
					patd.setPatientHistory(new HashSet<>());
			    }
				
				}//End
				patd.setUpdatedBy(user);
				patd.setUpdatedDate(date);
				patd.setDob(pat.getDob());
				patd.setFirstName(pat.getFirstName());
				patd.setLastName(pat.getLastName());
				patd.setSalutation(pat.getSalutation());
				if (ivf) patientDao.updateOnlyPatient(patd, office, user);
				boolean detailsSave = false;
				if (patd.getPatientDetails() != null && patd.getPatientDetails().size() > 0
						&& pat.getPatientDetails() != null && pat.getPatientDetails().size() > 0) {
					Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
					PatientDetail pdN = iter.next();
					boolean oldDetailMatched = false;
					for (PatientDetail pd : patd.getPatientDetails()) {
						boolean CheckCondition=pd.getGeneralDateIVwasDone().equals(pdN.getGeneralDateIVwasDone())
								&& 	pd.getEmployerName().toLowerCase().trim().equals(pdN.getEmployerName().toLowerCase().trim())
								&& 	pd.getInsName().toLowerCase().trim().equals(pdN.getInsName().toLowerCase().trim())
								&& pd.getiVFormType().getId()==pdN.getiVFormType().getId();
						/*if (!ivf) CheckCondition=pd.getGeneralDateIVwasDone().equals(pdN.getGeneralDateIVwasDone())
								  && 	pd.getInsName().toLowerCase().trim().equals(pdN.getInsName().toLowerCase().trim());*/
						if (CheckCondition) { 
							pdN.setId(pd.getId());
							pdN.setCreatedBy(pd.getCreatedBy());
							pdN.setCreatedDate(pd.getCreatedDate());
							pdN.setPatient(patd);
							pdN.setUpdatedBy(user);
							pdN.setUpdatedDate(date);
							Set<PatientDetail> l = new HashSet<>();
							l.add(pdN);
							patd.setPatientDetails(l);
							oldDetailMatched = true;
							dumpCheck=true;
							break;
						}
					}
					if (!oldDetailMatched) {
						Set<PatientDetail> savedD = patd.getPatientDetails();
						savedD.clear();
						pdN.setPatient(patd);
						pdN.setCreatedBy(user);
						savedD.add(pdN);
						patd.setPatientDetails(savedD);
						detailsSave = true;
					}

				}else {
					detailsSave=true;
					Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
					PatientDetail pdN = iter.next();
					pdN.setPatient(patd);
					//Set<PatientDetail> l = new HashSet<>();
					//l.add(pdN);
			
				    patd.setPatientDetails(pat.getPatientDetails());	
				}
				if (pat.getPatientHistory() != null && pat.getPatientDetails().size() > 0) {
					
					RuleEngineLogger.generateLogs(clazz, "Entering Service ..To save History  patient from DUMP/IVF.."
							+pat.getPatientId(), Constants.rule_log_debug, null);
						
					Set<PatientHistory> result1 = new HashSet<>();
					Set<PatientHistory> newPH = pat.getPatientHistory();
					Set<PatientHistory> phl = patd.getPatientHistory();
					if (newPH != null && newPH.size() > 0) {
						if (phl == null)
							phl = new HashSet<>();
						boolean added = false;
						for (PatientHistory n : newPH) {
							//System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
							added = true;
							for (PatientHistory o : phl) {
								//System.out.println("CODE"+o.getHistoryCode()+":"+n.getHistoryCode());
								//System.out.println("TOOTH"+o.getHistoryTooth()+":"+n.getHistoryTooth());
								//System.out.println("Surfce"+o.getHistorySurface()+":"+n.getHistorySurface());
								//System.out.println("DOS"+o.getHistoryDOS()+":"+n.getHistoryDOS());
								
								//oldPdid=0;
								//if (o.getPd()!=null && o.getPd().getGeneralDateIVwasDone().equals(generalDate)) {
								if (oldPdid!=-1) {
								if (o.getHistoryCode().equals(n.getHistoryCode())
										&& o.getHistoryTooth().equals(n.getHistoryTooth())
										&& o.getHistorySurface().equals(n.getHistorySurface())
										&& o.getHistoryDOS().equals(n.getHistoryDOS())) {
									added = false;
									
								}
								}
								
								/*
								if (o.getPd()!=null && o.getPd().getGeneralDateIVwasDone().equals(generalDate)) {
									added = false;
								}else if (o.getPd()==null){
									added = true;	
								}
								*/
							}
							if (added)
								result1.add(n);
						}

						patd.setPatientHistory(result1);

					}else {
						patd.setPatientHistory(result1);
					}

				}
				//patientDao.updatePatientDataWithDetailsAndHistory(patd, office, user, detailsSave);
				if (ivf) {
				patientDao.updatePatientDataWithDetailsAndHistory1(patd);//Only updates Patient table
				Patient pp=patientDao.updatePatientDataWithDetailsAndHistory2(patd,detailsSave,onlyInsert);//Only save in Details  never Update in case of DUMP
				Iterator<PatientDetail> iter = pp.getPatientDetails().iterator();
				PatientDetail ppd = iter.next();
			
				Set<PatientDetail> s=new HashSet<>();
				s.add(ppd);
				patd.setPatientDetails(s);
				
				patientDao.updatePatientDataWithDetailsAndHistory3(patd, office);
				}else {//FOR DUMP
					 if (dumpCheck) {
						 ob[1]="DUPLICATE";
					 }else {
						 
						    patientDao.updatePatientDataWithDetailsAndHistory1(patd);//Only updates Patient table
						    Iterator<PatientDetail> iterFirst = pat.getPatientDetails().iterator();
							PatientDetail pd = iterFirst.next();
							pd.setiVFormType(iVFormType);
							Patient pp=patientDao.updatePatientDataWithDetailsAndHistory2(patd,detailsSave,onlyInsert);//Only save in Details  never Update
							Iterator<PatientDetail> iter = pp.getPatientDetails().iterator();
							PatientDetail ppd = iter.next();
						
							Set<PatientDetail> s=new HashSet<>();
							s.add(ppd);
							patd.setPatientDetails(s);
							
							patientDao.updatePatientDataWithDetailsAndHistory3(patd, office);
					 }
					
				}
				
				r=0;
			}
			r = patd.getPatientDetails().iterator().next().getId();
		} catch (Exception c) {
			StringWriter sw = new StringWriter();
            c.printStackTrace(new java.io.PrintWriter(sw));
            String exceptionAsString = sw.toString();
           c.printStackTrace();
           ob[1]=exceptionAsString;
		}
		ob[0]=r;
		return ob;
		
	}

	public Object[] checkExistingIV (Patient pat, Office office, Date date,User user,boolean ivf,boolean onlyInsert,IVFormType iVFormType) {
		
		Patient patd = patientDao.checkforPatientWithIdAndOfficeAndGeneralDate(pat.getPatientId(), office,pat,true,pat.getPatientDetails().iterator().next().getGeneralDateIVwasDone());
		Object[] ob= new Object[2];
		ob[0]="Success";
		
		Integer r = 0;
		String generalDate="";
		String insuranceName="";
		String employerName="";
		int ivFormTypeId=0;
		if (pat.getPatientDetails()!=null && pat.getPatientDetails().size()>0) {
			Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
			PatientDetail x = iter.next();
			generalDate =x.getGeneralDateIVwasDone();
			insuranceName = x.getInsName();
			employerName = x.getEmployerName();
			ivFormTypeId=x.getiVFormType().getId();
		}
		try {
			if (patd == null) {
				ob[0]="Success";
				       
			} else {
				//delete old history logic
				for (PatientDetail pd : patd.getPatientDetails()) {
					if (pd.getGeneralDateIVwasDone().equals(generalDate) && pd.getiVFormType().getId()==ivFormTypeId  
								&& pd.getEmployerName().toLowerCase().trim().equals(employerName.toLowerCase().trim())
								&& pd.getInsName().toLowerCase().trim().equals(insuranceName.toLowerCase().trim())
								) {	
						ob[0]="No Success-"+pd.getId();
						r=pd.getId();
						
						break;
					}
					
				
				
				}//End
					}
		} catch (Exception c) {
			StringWriter sw = new StringWriter();
            c.printStackTrace(new java.io.PrintWriter(sw));
            String exceptionAsString = sw.toString();
           c.printStackTrace();
           ob[0]=exceptionAsString;
		}
		ob[1]=r;
		return ob;
		
	}

	/*
	@Override
	public Object convertPatientDataToIVFSheetData(Set<String> patIds, String officeName) throws Exception {
		// TODO Auto-generated method stub
		Office off = od.getOfficeByName(officeName);

		
		List<CaplineIVFFormDto> capD = createData(null, off, patIds,true);
		Map<String, List<IVFTableSheet>> map = new HashMap<>();
		IVFTableSheet sheet = null;
		for (CaplineIVFFormDto cap : capD) {

			sheet = new IVFTableSheet();
			sheet.setUniqueID(cap.getBasicInfo1() + "_" + cap.getId());
			sheet = IVFFormConversionUtil.copyValueToIVFSheet(cap, off,false);
			//sheet.setAlveoD7310CoveredWithEXT(cap.getOral5());
			List<IVFTableSheet> sheList = map.get(cap.getBasicInfo1() + "_" + cap.getId());
			if (sheList == null) {
				sheList = new ArrayList<>();
				sheList.add(sheet);
				map.put(cap.getBasicInfo1() + "_" + cap.getId(), sheList);
			} else {
				sheList.add(sheet);
			}
		}
		return map;
	}
    */
	private List<CaplineIVFFormDto> createData(CaplineIVFQueryFormDto d, Office off, Set<String> patIds,boolean temp) {

		List<CaplineIVFFormDto> capD = patientDao.searchPatientDetailFromIVF(d, off, patIds,temp);
		Set<String> patientIds = null;
		Set<String> patientIdsDB = null;
		
		List<CaplineIVFFormDto> cList = null;
        Set<String> pdId=new HashSet<>();
        
		Map<String, List<CaplineIVFFormDto>> map = new HashMap<>();
		for (CaplineIVFFormDto dto : capD) {
			pdId.add(dto.getId()+"");
			dto.setPatDid(dto.getId().intValue());
			dto.setBasicInfo1(off.getName());
			if (!temp && !dto.getBasicInfo6().trim().equals("")) {
				try {
				String[] s=dto.getBasicInfo6().trim().split("/");
				 dto.setBasicInfo6(s[2]+"-"+(s[0].length()==2?s[0]:"0"+s[0])+"-"+(s[1].length()==2?s[1]:"0"+s[1]));
				}catch(Exception e) {
					
				}
				}
			if (patientIds == null) {
				patientIds = new HashSet<>();
				patientIdsDB  = new HashSet<>();
			}

			patientIds.add(dto.getBasicInfo21());
			patientIdsDB.add(dto.getPidDB()+"");
			if (map.get(dto.getPatDid()+"") == null) {
				cList = new ArrayList<>();
				cList.add(dto);
				map.put(dto.getPatDid()+"", cList);
			} else {
				List<CaplineIVFFormDto> x = map.get(dto.getPatDid()+"");
				x.add(dto);
			}

		}
		if (patientIdsDB != null && patientIdsDB.size()>0 && pdId.size()> 0) {
			// List<Patient> pats= patientDao.searchPatientByPatientId(patientIds, off);
			List<PatientHistory> patsH = patientDao.searchPatientHistoryForPatient(patientIdsDB, off,pdId,temp);
			/*
			 * for(Patient p:pats) { List<CaplineIVFFormDto> c=map.get(p.getPatientId());
			 * for(CaplineIVFFormDto form:c) { form.setBasicInfo2(p.getFirstName()+" "
			 * +(p.getLastName()==null?"":p.getLastName())); form.setBasicInfo6(p.getDob());
			 * } }
			 */
			for (PatientHistory ph : patsH) {
				List<CaplineIVFFormDto> c = map.get(ph.getPdid()+"");
				for (CaplineIVFFormDto form : c) {
					List<String> his = form.getHistory();
					if (ph.getHistoryTooth()==null) ph.setHistoryTooth("");
					if (ph.getHistorySurface()==null) ph.setHistorySurface("");
					
					String l = ((ph.getHistoryCode().equals("")) ? "BLANK" : ph.getHistoryCode()) + Constants.PATH_SEPERATOR_XML_IVF
							+ ((ph.getHistoryTooth().equals("")) ? "BLANK" : ph.getHistoryTooth()) + Constants.PATH_SEPERATOR_XML_IVF
							+ ((ph.getHistorySurface().equals("")) ? "BLANK" : ph.getHistorySurface())
							+ Constants.PATH_SEPERATOR_XML_IVF + ((ph.getHistoryDOS().equals("")) ? "BLANK" : ph.getHistoryDOS());
					if (his == null)
						his = new ArrayList<>();
					//Remove duplicates
					if (!his.contains(l)) his.add(l);
					form.setHistory(his);
				}

			}
		}

		return capD;

	}

	@Override
	public Object searchIVFData(CaplineIVFQueryFormDto d,Office office) throws Exception {
		// TODO Auto-generated method stub
		List<CaplineIVFFormDto> capD = createData(d, office, null,true);
		return capD;
	}

	@Override
	public Object searchIVFDataTemp(CaplineIVFQueryFormDto d,Office office) throws Exception {
		// TODO Auto-generated method stub
		List<CaplineIVFFormDto> capD = createData(d, office, null,false);
		return capD;
	}

	@Override
	public Object searchIVFDataPat(CaplineIVFQueryFormDto d,Office office,Set<String> patIds) throws Exception {
		// TODO Auto-generated method stub
		List<CaplineIVFFormDto> capD = createData(d, office, patIds,true);
		return capD;
	}

	@Override
	public Object searchIVFDataForGoogleSheet(CaplineIVFQueryFormDto d,Office office) throws Exception {
		// TODO Auto-generated method stub
		
		List<Object> data=patientDao.searchPatientDetailFromIVFGivenColumns(d, office);
		List<GoogleReportsRDDTO> beanList = new ArrayList<>();
		GoogleReportsRDDTO dt= null;
		
		for(Object o:data) {
			dt = new GoogleReportsRDDTO();
			int x=-1;
			if (o!=null) {
				Object [] a=(Object []) o;
				for(Object f:a) {
					if (x==-2) {
						x++;
						continue ;
					}
					setUPResponseData(dt, ++x,f);	
					
				}
			}
			beanList.add(dt);
		}
		return beanList;
	}

	@Override
	public Object searchIVFHistoryDataForGoogleSheet(CaplineIVFQueryFormDto d,Office office) throws Exception {
		// TODO Auto-generated method stub
		
		List<Object> data=patientDao.searchPatientHistoryFromIVFGivenColumns(d, office);
		List<GoogleReportsRDDTO> beanList = new ArrayList<>();
		GoogleReportsRDDTO dt= null;
		
		for(Object o:data) {
			dt = new GoogleReportsRDDTO();
			int x=-1;
			if (o!=null) {
				Object [] a=(Object []) o;
				for(Object f:a) {
					if (x==-2) {
						x++;
						continue ;
					}
					setUPResponseData(dt, ++x,f);	
					
				}
			}
			beanList.add(dt);
		}
		return beanList;
	}

	@Override
	public Object searchIVFDataforApp(CaplineIVFQueryFormDto d,Office off) throws Exception {
		// TODO Auto-generated method stub
		//Office off = od.getOfficeByName(d.getOfficeNameDB());
		
		List<CaplineIVFFormDto> capD = createData(d, off, null,true);
		if (capD!=null && capD.size() > 0) {
			for(CaplineIVFFormDto form :capD) {
			if (form.getHistory() != null) {
				int ct = 0;
				List<ToothHistoryDto> hdto = new ArrayList<>();
				ToothHistoryDto hd = null;

				for (String h : form.getHistory()) {
					String sp[] = h.split(Constants.PATH_SEPERATOR_XML_IVF);

					hd = new ToothHistoryDto(sp[0].equals("BLANK") ? "" : sp[0], sp[3].equals("BLANK") ? "" : sp[3],
							sp[1].equals("BLANK") ? "" : sp[1], sp[2].equals("BLANK") ? "" : sp[2]);
					if (ct == 0)
						hd.setClassName("classname" + ct);
					if (ct == 1)
						hd.setClassName("classname" + ct);
					if (ct == 2) {
						hd.setClassName("classname" + ct);
						ct = -1;
					}
					if (!hd.getSurfaceTooth().equals(""))
						hd.setHistoryTooth(hd.getHistoryTooth() + "-" + hd.getSurfaceTooth());
					hdto.add(hd);
					ct++;

				}
				form.setHdto(hdto);
				if (form.getHistoryCount()==null)form.setHistoryCount(hdto.size()+"");
				form.setHistory(null);
			 }
			}
		 }
		return capD;
	}
	/*
	 * if any changes done here make sure to verify same in copyValueToIVFSheet
	 */

	@Override
	public Object[] generatePDF(CaplineIVFQueryFormDto dto,Office office,IVFormType iVFormType) {
		ByteArrayOutputStream o = null;
		Object[] obj=new Object[2]; 
		try {
			dto.setIvformTypeId(iVFormType.getId()+"");
			List<CaplineIVFFormDto> li = (List<CaplineIVFFormDto>) searchIVFData(dto,office);
			if (li!=null  && li.size() > 0) {
				CaplineIVFFormDto form = li.get(0);
				obj[0]=form.getBasicInfo2();
				form.setPdfAlert("");
				if (form.getPolicy4()!=null)form.setPolicy4(WordUtils.wrap(form.getPolicy4(), 10, " ", true));
				if (form.getPolicy16()!=null)form.setPolicy16(WordUtils.wrap(form.getPolicy16(), 10, " ", true));
				if (form.getBasicInfo3()!=null) {
					String insName=form.getBasicInfo3().toLowerCase();
					if (insName.contains("geha") || insName.contains("connection dental") ||
							insName.contains("fep blue dental") || insName.contains("fed blue") ) {
						form.setPdfAlert("Patients have BCBS Medical policy as primary & we can directly bill them even if we do not have insurance details.");	
					}
				}
				//remove $ sign
				
				if (form.getHistory() != null) {
					int ct = 0;
					List<ToothHistoryDto> hdto = new ArrayList<>();
					ToothHistoryDto hd = null;

					for (String h : form.getHistory()) {
						String sp[] = h.split(Constants.PATH_SEPERATOR_XML_IVF);

						hd = new ToothHistoryDto(sp[0].equals("BLANK") ? "" : sp[0], sp[3].equals("BLANK") ? "" : sp[3],
								sp[1].equals("BLANK") ? "" : sp[1], sp[2].equals("BLANK") ? "" : sp[2]);
						if (ct == 0)
							hd.setClassName("classname" + ct);
						if (ct == 1)
							hd.setClassName("classname" + ct);
						if (ct == 2) {
							hd.setClassName("classname" + ct);
							ct = -1;
						}
						if (!hd.getSurfaceTooth().equals(""))
							hd.setHistoryTooth(hd.getHistoryTooth() + "-" + hd.getSurfaceTooth());
						hdto.add(hd);
						ct++;

					}
					form.setHdto(hdto);
					form.setHistory(null);
					int x=0;
					List<ToothHistoryDto> l1= new ArrayList<>();
					List<ToothHistoryDto> l2= new ArrayList<>();
					List<ToothHistoryDto> l3= new ArrayList<>();
					
					for(ToothHistoryDto h:form.getHdto()) {
						if (x==0)   l1.add(h);
						if (x==1)   l2.add(h);
						if (x==2) {
							l3.add(h);
							x=-1;
						}
						x++;
					}
					form.setHdto1(l1);
					form.setHdto2(l2);
					form.setHdto3(l3);
					form.setHdto(null);
					if (form.getHistoryCount()==null) form.setHistoryCount((form.getHdto1().size()+form.getHdto2().size()+form.getHdto3().size())+"");
				//Remove $ sign	
				if (form.getSealantsD()!=null)	form.setSealantsD(form.getSealantsD().replaceAll("\\$", ""));
				if (form.getPerioMnt1()!=null)	form.setPerioMnt1(form.getPerioMnt1().replaceAll("\\$", ""));
				if (form.getPerioMnt4()!=null)	form.setPerioMnt4(form.getPerioMnt4().replaceAll("\\$", ""));
				if (form.getPerio1()!=null)	form.setPerio1(form.getPerio1().replaceAll("\\$", ""));
				if (form.getPerioMnt6()!=null)	form.setPerioMnt6(form.getPerioMnt6().replaceAll("\\$", ""));
				if (form.getSedations1()!=null)	form.setSedations1(form.getSedations1().replaceAll("\\$", ""));
				if (form.getSedations2()!=null)	form.setSedations2(form.getSedations2().replaceAll("\\$", ""));
				if (form.getExtractions1()!=null)	form.setExtractions1(form.getExtractions1().replaceAll("\\$", ""));
				if (form.getExtractions2()!=null)	form.setExtractions2(form.getExtractions2().replaceAll("\\$", ""));
				if (form.getOral1()!=null)	form.setOral1(form.getOral1().replaceAll("\\$", ""));
				if (form.getImplants4()!=null)	form.setImplants4(form.getImplants4().replaceAll("\\$", ""));
				
				}
				CaplineIVFFormDtoToXML xml = new CaplineIVFFormDtoToXML();
				String filePath = xml.convertToXML(form, XSLT_PATH);
				File file = new File(filePath);
				if (dto.getPdf()==null) {
				String xslt=XSLT_FILE;
				if (iVFormType.getName().equals(Constants.IV_ORAL_SURGERY_FORM_NAME)) {
					xslt=XSLT_FILE_OS ;
				}
				if (iVFormType.getName().equals(Constants.IV_ORTHO_FORM_NAME)) {
					xslt=XSLT_FILE_ORTHO ;
				}
				if (iVFormType.getName().equals(Constants.IV_GENERAL_FORM_NAME)) {
					xslt=XSLT_FILE_NEW ;
				}
				if (dto.getNewFormat().equals(""))o = xml.createPdfStream(

						xml.createHtml(filePath, xslt), "");
				else  o= xml.createPdfStream(

						xml.createHtml(filePath, XSLT_FILE_NEW), "");
			    }
				if (file!=null) file.delete(); 
				//To test html for issues
				if (dto.getPdf()!=null) {
					if (dto.getNewFormat().equals(""))o=xml.createHtmlOut(filePath, XSLT_FILE);
					else  o=xml.createHtmlOut(filePath, XSLT_FILE_NEW);
				}
				obj[1]=o;

			}
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
	public void fillUpGoogleSheet(CaplineIVFQueryFormDto dto,Office office) {
		Object[] obj=new Object[2]; 
		try {
			
			List<CaplineIVFFormDto> li = (List<CaplineIVFFormDto>) searchIVFData(dto,office);
			if (li!=null && li.size() > 0) {
				
				for(CaplineIVFFormDto form:li){
				
				//CaplineIVFFormDto form = li.get(0);
				obj[0]=form.getBasicInfo2();
				if (form.getHistory() != null) {
					int ct = 0;
					List<ToothHistoryDto> hdto = new ArrayList<>();
					ToothHistoryDto hd = null;

					for (String h : form.getHistory()) {
						String sp[] = h.split(Constants.PATH_SEPERATOR_XML_IVF);

						hd = new ToothHistoryDto(sp[0].equals("BLANK") ? "" : sp[0], sp[3].equals("BLANK") ? "" : sp[3],
								sp[1].equals("BLANK") ? "" : sp[1], sp[2].equals("BLANK") ? "" : sp[2]);
						if (ct == 0)
							hd.setClassName("classname" + ct);
						if (ct == 1)
							hd.setClassName("classname" + ct);
						if (ct == 2) {
							hd.setClassName("classname" + ct);
							ct = -1;
						}
						if (!hd.getSurfaceTooth().equals(""))
							hd.setHistoryTooth(hd.getHistoryTooth() + "-" + hd.getSurfaceTooth());
						hdto.add(hd);
						ct++;

					}
					form.setHdto(hdto);
					form.setHistory(null);
					
				}
			  }
				ConnectAndReadSheets.updateIVFGoogleSheet(dto.getSheetId(),  dto.getSheetSubId(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,li);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//return obj;
	}

	
	@Override
	public Object searchIVFDataforAppScrap(CaplineIVFQueryFormDto d,Office off) throws Exception {
		// TODO Auto-generated method stub
		//Office off = od.getOfficeByName(d.getOfficeNameDB());
		
		List<CaplineIVFFormDto> capD = createData(d, off, null,false);
		if (capD!=null && capD.size() > 0) {
			for(CaplineIVFFormDto form :capD) {
			if (form.getHistory() != null) {
				int ct = 0;
				List<ToothHistoryDto> hdto = new ArrayList<>();
				ToothHistoryDto hd = null;

				for (String h : form.getHistory()) {
					String sp[] = h.split(Constants.PATH_SEPERATOR_XML_IVF);

					hd = new ToothHistoryDto(sp[0].equals("BLANK") ? "" : sp[0], sp[3].equals("BLANK") ? "" : sp[3],
							sp[1].equals("BLANK") ? "" : sp[1], sp[2].equals("BLANK") ? "" : sp[2]);
					if (ct == 0)
						hd.setClassName("classname" + ct);
					if (ct == 1)
						hd.setClassName("classname" + ct);
					if (ct == 2) {
						hd.setClassName("classname" + ct);
						ct = -1;
					}
					if (!hd.getSurfaceTooth().equals(""))
						hd.setHistoryTooth(hd.getHistoryTooth() + "-" + hd.getSurfaceTooth());
					hdto.add(hd);
					ct++;

				}
				form.setHdto(hdto);
				form.setHistory(null);
			 }
			}
		 }
		return capD;
	}

	private GoogleReportsRDDTO setUPResponseData(GoogleReportsRDDTO dataBean, int x, Object d) throws ClassNotFoundException {
		if (d==null || ((d!=null && d.equals("null")))) d="-NO-DATA-";
		else d=d+"";
		String data=(String) d;
		data=data.replaceAll("\\\\u000", "-");
		PropertyDescriptor pd;
		if (dataBean != null) {
			Class<?> c2;
			c2 = Class.forName("com.tricon.ruleengine.dto.GoogleReportsRDDTO");
				String hc = "c" + (x+1);
				try {
				pd = new PropertyDescriptor(hc, c2);
				pd.getWriteMethod().invoke(dataBean, data);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
		}	
		return dataBean;
	}

	/**
	 * This Method gets data from searchPatientHistoryFromGivenColumns method and holds data into list of object
	 * hereafter data dumps in googlesheet into each column
	 */
	@Override
	public Object searchCaplineDataReplication(CaplineDataReplicationDto d,Office office) throws Exception{
		
		List<Object> data=patientDao.replicationQueries(d,office);
		 if(!data.isEmpty()) {
		 List<GoogleReportsRDDTO> beanList = new ArrayList<>();
			GoogleReportsRDDTO dt= null;		
			for(Object o:data) {
				dt = new GoogleReportsRDDTO();
				int x=-1;
				if (o!=null) {
					Object [] a=(Object []) o;
					for(Object f:a) {
						if (x==-2) {
							x++;
							continue ;
						}
						setUPResponseData(dt, ++x,f);	
					}
				}
				beanList.add(dt);
			}
			return beanList;
		 }else{
			 return data;
		 }
	}
	
	@Override
	public Object searchRcmClaimData(RcmClaimDto d,Office office) throws Exception{
		
		List<Object> data=rcmClaimDao.getRcmClaimData(d, office);
		 if(!data.isEmpty()) {
		 List<GoogleReportsRDDTO> beanList = new ArrayList<>();
			GoogleReportsRDDTO dt= null;		
			for(Object o:data) {
				dt = new GoogleReportsRDDTO();
				int x=-1;
				if (o!=null) {
					Object [] a=(Object []) o;
					for(Object f:a) {
						if (x==-2) {
							x++;
							continue ;
						}
						setUPResponseData(dt, ++x,f);	
					}
				}
				beanList.add(dt);
			}
			return beanList;
		 }else{
			 return data;
		 }
	}
}
