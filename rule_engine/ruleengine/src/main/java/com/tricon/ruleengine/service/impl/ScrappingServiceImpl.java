package com.tricon.ruleengine.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.api.enums.HighLevelReportMessageStatusEnum;
import com.tricon.ruleengine.dao.IVformTypeDao;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.ScrappingDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.ScrappingInputDto;
import com.tricon.ruleengine.dto.ScrappingSiteDetailsDto;
import com.tricon.ruleengine.dto.ScrappingUserDataInputDto;
import com.tricon.ruleengine.dto.SealantInputDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.IVFDefaultValue;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.db.SealantEligibilityRule;
import com.tricon.ruleengine.model.sheet.CommonDataCheck;
import com.tricon.ruleengine.model.sheet.CommonDataDetails;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;
import com.tricon.ruleengine.pdf.CaplineIVFFormDtoToXML;
import com.tricon.ruleengine.pdf.SelantPdfMainDto;
import com.tricon.ruleengine.pdf.SelantPdfPatDto;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.service.ScrappingService;
import com.tricon.ruleengine.service.SealantEligibilityRuleService;
import com.tricon.ruleengine.service.TreatmentPlanService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.ConstantsScrapping;
import com.tricon.ruleengine.utils.IVFFormConversionUtil;
import com.tricon.ruleengine.utils.RuleBook;

@Transactional
@Service
public class ScrappingServiceImpl implements ScrappingService {

	private static Locale locale = new Locale("en");
	static Class<?> clazz = ScrappingServiceImpl.class;

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;
	
	@Value("${rule.save.report.data}")
	private String SAVE_REPORT_DATA;
	
	@Value("${re.xslt.path}")
	private String XSLT_PATH;

	
	@Value("${re.xslt.sealant}")
	private String XSLT_FILE_SEAL;

	@Autowired
	TreatmentValidationDao tvd;
	
	@Autowired
	MessageSource messageSource;


	@Autowired
	ScrappingDao sDao;
	// @Autowired MCNARosterScrappingService mservice;
	@Autowired
	OfficeDao officeDoa;
	// @Autowired MCNAEligibilityScrappingService meservice;
	//@Autowired
	//DentaQEligibilityScrappingService deservice;
	
	@Autowired
	@Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private CaplineIVFGoogleFormService caplineIVFGoogleFormService;
	
	@Autowired
	private SealantEligibilityRuleService sealantEligibilityRuleService;
	
	@Autowired
	IVformTypeDao iVformTypeDao;
	
	@Autowired
	TreatmentPlanService treatmentPlanService;

	

	@Override
	public Map<String, List<?>> scrapSite(ScrappingInputDto dto) throws InterruptedException, ExecutionException {

		//boolean updateStatusinSheet = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser juser = (JwtUser) userDetails;
		
		Office off = officeDoa.getOfficeByUuid(dto.getOfficeId(),juser.getCompany().getUuid());
		// List<?> data=null;
		Map<String, List<?>> map = new HashMap<>();
		// ScrappingSite st= sDao.getScrappingSiteDetails(dto.getSiteId(), off);
		ScrappingSiteDetails sd = sDao.getScrappingSiteDetailsDetail(dto.getScrapType(), off);
		
		// Hibernate.initialize(st.getSiteSiteDetails());
		RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl 1", Constants.rule_log_debug, null);
		/*
		 * Set<ScrappingSiteDetails> sdL=
		 * st.getSiteSiteDetails();//sDao.getScrappingSiteDetailsDetail(dto.
		 * getSiteDetailId(), off); ScrappingSiteDetails sd=null;
		 * 
		 * for(ScrappingSiteDetails sd1 :sdL) { sd=sd1; break; }
		 */
		List<IVFDefaultValue> iVFDefaultValues=null;//sealantEligibilityRuleService.getActiveIVFDefaultValues();
		List<SealantEligibilityRule> sealantEligibilityRules=null;//sealantEligibilityRuleService.getActiveSealantEligibilityRuleData();
		IVFormType iVFormType= iVformTypeDao.getIVFormTypeByName(Constants.IV_GENERAL_FORM_NAME);
		if (sd == null) {
			map.put(ConstantsScrapping.OFFICE_NOT_SET, null);
			return map;
		}
		RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl  " + sd.isRunning(), Constants.rule_log_debug,
				null);
		sd.setPassword(dto.getPassword());
		sd.setUserName(dto.getUsername());
		if (dto.getLocationProvider()==null) dto.setLocationProvider("");
		sd.setLocationProvider(dto.getLocationProvider());
		sd.setLocation(dto.getLocation());
		if (!sd.isRunning()) {
			sd.setRunning(true);
			sDao.updateScrappingSiteRunningStatus(sd);

			try {
				
				/*
				sd.setGoogleSheetId("1VX7370QvZXM8Vv_8YHm0fEzeRWzvaBdXezVTHvIcB4A");
				sd.setGoogleSheetName("ttt");
				sd.setGoogleSubId("0");
				sd.setPassword("Devine%1245976");
				sd.setUserName("Devin13458");
				*/
				RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl  " + sd.getScrappingSite().getDescription(),
						Constants.rule_log_debug, null);
				if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.ROSTER_FET)) {
					//updateStatusinSheet = true;
					ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
							CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "YES",
							ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS,
							ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS);
					// MCNARosterScrappingServiceImpl m= new MCNARosterScrappingServiceImpl();
					// List<?> r=m.scrapSite(sd);
					// Runnable rr = new MCNARosterScrappingServiceImpl(sd);
					ExecutorService service = Executors.newCachedThreadPool();// FixedThreadPool(1);

					// Future<List<?>> rr =
					service.submit(new MCNARosterScrappingServiceImpl(sd, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER,dto,env.getProperty("google.chorme.driver")));
					map.put(ConstantsScrapping.SCRAPPING_INIT + sd.getGoogleSheetId() + ConstantsScrapping.NAME_Separator + sd.getGoogleSubId(),
							null);

					// List<?> r= rr.get();
					/*
					 * ConnectAndReadSheets.updateSheetRoster(sd.getGoogleSheetId(),
					 * sd.getGoogleSubId(), CLIENT_SECRET_DIR,
					 * CREDENTIALS_FOLDER,(List<RosterDetails>)r,sd.getRowCount());
					 */
					/*
					 * map.put("Done",n); if (r!=null) { sd.setRowCount(r.size()); }else {
					 * sd.setRowCount(0); }
					 */
				} else if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.MCNA_ELIG)
						|| sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.DENTA_ELIG)) {
					Map<String, List<Object>> mapData = null;
					ExecutorService service = Executors.newCachedThreadPool();// FixedThreadPool(1);
					if (!dto.isIsdataFromUi() || !dto.isOnlyDisplay()) {
						ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
								CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "YES", ConstantsScrapping.ELE_ROW_INDEX_STATUS,
								ConstantsScrapping.ELE_COLUMN_INDEX_STATUS);
					}
					if (!dto.isIsdataFromUi())
						mapData = ConnectAndReadSheets.readSheetMcnaDenta(sd.getGoogleSheetId(),
								sd.getGoogleSheetName(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					else {
						if (dto.getListUd() != null && dto.getListUd().size() > 0) {
							int x = 0;
							MCNADentaSheet mcna = null;
							for (ScrappingUserDataInputDto d : dto.getListUd()) {

								if (!d.getDob().equals("")) {
									if (d.getSubscriberId().equals("") && d.getLastName().equals("")) {
										// do not add in map
									} else {
										if (mapData == null)
											mapData = new HashMap<>();
										mcna = new MCNADentaSheet("","","",d.getFirstName(), d.getLastName(),
												d.getSubscriberId(), d.getDob(), d.getInsuranceName(), x + "");
										List<Object> l = new ArrayList<>();
										l.add(mcna);
										mapData.put((x++) + "", l);
									}

								}
							}
						}
					}
					if (mapData != null) {

						List<?> r = null;
						boolean update = false;
						if (!dto.isIsdataFromUi()) {
							update = true;
						}
						// Future<List<?>> rr =
						if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.MCNA_ELIG)) {
							if (update) {
								service.submit(new MCNAEligibilityScrappingServiceImpl(sd, CLIENT_SECRET_DIR,
										CREDENTIALS_FOLDER, mapData, update,iVFDefaultValues,sealantEligibilityRules,caplineIVFGoogleFormService,iVFormType,off,env.getProperty("google.chorme.driver")));
								map.put(ConstantsScrapping.SCRAPPING_INIT + sd.getGoogleSheetId() + ConstantsScrapping.NAME_Separator
										+ sd.getGoogleSubId(), null);

							} else {
								Future<List<?>> rr = service.submit(new MCNAEligibilityScrappingServiceImpl(sd,
										CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, mapData, update,iVFDefaultValues,sealantEligibilityRules,caplineIVFGoogleFormService,iVFormType,off,env.getProperty("google.chorme.driver")));
								r = rr.get();
								map.put("Done", r);
							}
						} else if (sd.getScrappingSite().getDescription()
								.equalsIgnoreCase(ConstantsScrapping.DENTA_ELIG)) {
							

							if (update) {
								service.submit(new DentaQEligibilityScrappingServiceImpl(sd, CLIENT_SECRET_DIR,
										CREDENTIALS_FOLDER, mapData, update,dto.getSiteType(),off.getName(),env.getProperty("google.chorme.driver"),iVFDefaultValues,
										sealantEligibilityRules,caplineIVFGoogleFormService,iVFormType,off));
								map.put(ConstantsScrapping.SCRAPPING_INIT + sd.getGoogleSheetId() + ConstantsScrapping.NAME_Separator
										+ sd.getGoogleSubId(), null);
							} else {
								Future<List<?>> rr = service.submit(new DentaQEligibilityScrappingServiceImpl(sd,
										CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, mapData, update,dto.getSiteType(),off.getName(),env.getProperty("google.chorme.driver"),
										iVFDefaultValues,sealantEligibilityRules,caplineIVFGoogleFormService,iVFormType,off));
								r = rr.get();
								map.put("Done", r);
							}
						}

						if (r != null) {
							sd.setRowCount(r.size());
						} else {
							sd.setRowCount(0);
						}

					}
				}

			} catch (IOException e) {
				RuleEngineLogger.generateLogs(clazz,
						"ScrappingServiceImpl EE " + sd.getScrappingSite().getDescription(), Constants.rule_log_debug,
						null);
				try {
					if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.ROSTER_FET)) {
						ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
								CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "NO",
								ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS,
								ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS);

					}
					if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.MCNA_ELIG)
							|| sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.DENTA_ELIG)) {
						ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
								CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "NO", ConstantsScrapping.ELE_ROW_INDEX_STATUS,
								ConstantsScrapping.ELE_COLUMN_INDEX_STATUS);

					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				e.printStackTrace();
			} finally {
				sd.setRunning(false);
				sDao.updateScrappingSiteRunningStatus(sd);

			}

		} else {
			RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl Already Running ", Constants.rule_log_debug,
					null);
			map.put("Already Running", null);

		}

		return map;
	}


	@Override
	public void updateScrapRunStatus() {

		List<ScrappingSiteDetails> list = sDao.getScrappingSiteDetailDetails();
		if (list != null) {
			for (ScrappingSiteDetails sd : list) {
				sd.setRunning(false);
				sDao.updateScrappingSiteRunningStatus(sd);
			}
		}
	}


	@Override
	public void updateScrapRunStatus(int siteId, Office office) {
		
		
	}


	@Override
	public ScrappingSiteDetails getScrapsiteDetialsByOfficeAndType(int scrapType, String officeUUid) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser juser = (JwtUser) userDetails;
		Office off = officeDoa.getOfficeByUuid(officeUUid,juser.getCompany().getUuid());
		ScrappingSiteDetails sd = sDao.getScrappingSiteDetailsDetail(scrapType, off);

		return sd;
	}


	@Override
	public ScrappingSiteDetailsDto getScrappingSiteDetailsDetailSDto(int scrapType, String officeUUid) {

		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser juser = (JwtUser) userDetails;
		
		Office off = officeDoa.getOfficeByUuid(officeUUid,juser.getCompany().getUuid());
		return sDao.getScrappingSiteDetailsDetailSDto(scrapType, off);
	}


	@Override
	public Map<String, List<?>> runSealantRule(SealantInputDto dto) throws InterruptedException, ExecutionException {
		
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser juser = (JwtUser) userDetails;
		
		Office off = officeDoa.getOfficeByUuid(dto.getOfficeId(),juser.getCompany().getUuid());
		// List<?> data=null;
		Map<String, List<?>> map= new HashMap<>();
		// ScrappingSite st= sDao.getScrappingSiteDetails(dto.getSiteId(), off);
		ScrappingSiteDetails sd = sDao.getScrappingSiteDetailsDetail(dto.getSid(), off);
		
		// Hibernate.initialize(st.getSiteSiteDetails());
		RuleEngineLogger.generateLogs(clazz, "runSealantRule", Constants.rule_log_debug, null);
		/*
		 * Set<ScrappingSiteDetails> sdL=
		 * st.getSiteSiteDetails();//sDao.getScrappingSiteDetailsDetail(dto.
		 * getSiteDetailId(), off); ScrappingSiteDetails sd=null;
		 * 
		 * for(ScrappingSiteDetails sd1 :sdL) { sd=sd1; break; }
		 */
		//List<IVFDefaultValue> iVFDefaultValues=sealantEligibilityRuleService.getActiveIVFDefaultValues();
		List<SealantEligibilityRule> sealantEligibilityRules=sealantEligibilityRuleService.getActiveSealantEligibilityRuleData();
		IVFormType iVFormType= iVformTypeDao.getIVFormTypeByName(Constants.IV_GENERAL_FORM_NAME);
		if (sd == null) {
			map.put(ConstantsScrapping.OFFICE_NOT_SET, null);
			return map;
		}
		RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl  " + sd.isRunning(), Constants.rule_log_debug,
				null);
		sd.setPassword(dto.getPassword());
		sd.setUserName(dto.getUserName());
		if (dto.getLocationProvider()==null) dto.setLocationProvider("");
		sd.setLocationProvider(dto.getLocationProvider());
		sd.setLocation(dto.getLocation());
		if (!sd.isRunning()) {
			//sd.setRunning(true);
			//sDao.updateScrappingSiteRunningStatus(sd);//For latter

		}
		
		CaplineIVFQueryFormDto d=new CaplineIVFQueryFormDto();
		List<ScrappingUserDataInputDto> pts=dto.getListUd();
		Set<String> patIds= new HashSet<>();
		List<CaplineIVFFormDto> allO=new ArrayList<>();
		for (ScrappingUserDataInputDto pt:pts) {
			//map.put(pt.getPatientId(),null);
			patIds.add(pt.getPatientId());
			d.setPatientIdDB(pt.getPatientId());
			d.setForSelantData("sealant");
			try {
			Object a=	caplineIVFGoogleFormService.searchIVFDataPat(d,off,null);
			if (a!=null &&  ((List<CaplineIVFFormDto>) a).size()==1)
			allO.addAll((List<CaplineIVFFormDto>) a);
			}catch(Exception p) {
				
			}
		}
		//d.setPatientIdDB(null);
		try {
		  //Object obj =caplineIVFGoogleFormService.searchIVFDataPat(d,off,patIds);
		  if (allO!=null) {
		  //List<CaplineIVFFormDto> capD=  allO;
		  Rules rule=null;
		  Object[] o = null;
		  BufferedWriter bw = null;
		  o = treatmentPlanService.logFileToAppendData(off.getName().trim());
		  List<Rules> rules = tvd.getAllActiveRules();
		  //fName = (String) o[0];
			bw = (BufferedWriter) o[1];
		  String mode=Constants.sealanthmode;	
		  IVFTableSheet ivfSheet=null;
		  RuleBook book= new RuleBook();
		  for (CaplineIVFFormDto cap:allO) {
			  String paid=cap.getBasicInfo21();
			  List<TPValidationResponseDto> list=new ArrayList<>();
			  //cap.getId()
			  CommonDataCheck cdc= new CommonDataCheck();
			  CommonDataDetails cdd= new CommonDataDetails();
			if (cap.getDate()!=null && !cap.getDate().equals("")){
				
				//cap.setDate(Constants.SIMPLE_DATE_FORMAT.format(Constants.SIMPLE_DATE_FORMAT_IVF.parse(cap.getDate())));
			}
			  cdc.setCdDetails(cdd);
			ivfSheet= IVFFormConversionUtil.copyValueToIVFSheet(cap, off, true);
			rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_68);
			Object[] oData=null;
			Set<TPValidationResponseDto> dtoRL=null;
			//TPValidationResponseDto dtoR=null;
			//add in Report table iv..Date
			oData= book.Rule68(ivfSheet, sealantEligibilityRules, messageSource, rule, bw,true);
			dtoRL=(Set<TPValidationResponseDto>)oData[4];
			if (dtoRL != null) {
				//Primary
				list.addAll(dtoRL);
				SelantPdfPatDto dt= new SelantPdfPatDto();
				 dt.setIvDate(cap.getDate());
	   			 dt.setPatientId(paid);
	   			 dt.setName(cap.getBasicInfo2());
	   			 dt.setfName(cap.getBasicInfo1());
	   			 dt.setTe("");
	   			 dt.setMessType(BigInteger.valueOf(HighLevelReportMessageStatusEnum.FAIL.getStatus()));
				 
	   			/*for (TPValidationResponseDto t : dtoRL) {
					new TPValidationResponseDto(rule.getId(), rule.getName(), t.getMessage(),
							t.getResultType(),t.getSurface(),t.getTooth(),t.getServiceCode());
					// saveReports(authentication, rule, t, dto, (IVFTableSheet) (ivfList.get(0)));
				}*/
			}
			
			//treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, new ArrayList<>((Set<TPValidationResponseDto>)oData[4]), off, iVFormType,mode);
			//69= Valid teeth
			if (oData[0]!=null && ((String) oData[0]).length()>0) {
		    	rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_69);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.pass.message2", new Object[] { oData[0]}, locale), Constants.PASS, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setPatientName(cap.getBasicInfo2());
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
			}
			else {
				//mpdf.get(paid).setTe("");
				rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_69);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.fail.message3", new Object[] {}, locale), Constants.FAIL, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
			}
			//////70 non valid Teeth
			if (oData[1]!=null && ((String) oData[1]).length()>0) {

				rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_70);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.error.message4", new Object[] {oData[1]} , locale), Constants.FAIL, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
			}else {

				rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_70);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.pass.message3", new Object[] {}, locale), Constants.PASS, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
			}
			/////////71 In valid teeth age  
			if (oData[2]!=null && ((String) oData[2]).length()>0) {
				
				rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_71);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.error.message4", new Object[] { oData[2]}, locale), Constants.FAIL, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
			}else {
		    	
				rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_71);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.pass.message3", new Object[] {}, locale), Constants.PASS, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
				
			}
			/////72 in invalid teeth Freq.
			if (oData[3]!=null && ((String) oData[3]).length()>0) {

		    	rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_72);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.error.message4", new Object[] {oData[3] }, locale), Constants.FAIL, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
			}else {

		    	rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_72);
				TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(), 
						messageSource.getMessage("rule68.pass.message3", new Object[] { }, locale), Constants.PASS, "", "", "",cap.getBasicInfo2(),cap.getDate());
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  res.setiName(cap.getBasicInfo3());
				  sv.add(res);
				  list.add(res);
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
				
			}
			map.put(cap.getBasicInfo21(), list);
			if (list.size()>0) {
				  treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, list, off, iVFormType,mode);
			  }
		  }//for loop
		  for(String x:patIds) {
			  //73 .other issue 
			  if (map.get(x)==null) {
				  rule = treatmentPlanService.getRulesFromList(rules, Constants.RULE_ID_73);
					TPValidationResponseDto res= new TPValidationResponseDto(rule.getId(),rule.getName(),
							 messageSource.getMessage("rule68.error.message1", new Object[] { }, locale), Constants.FAIL, "", "", "");
				  List<TPValidationResponseDto> sv=new ArrayList<>();
				  res.setOff(off.getName());
				  sv.add(res);
				  map.put(x, sv);
				  ivfSheet=new IVFTableSheet();
				  ivfSheet.setPatientId(x);
				  ivfSheet.setUniqueID(off.getName()+"_");
				  SelantPdfPatDto dt= new SelantPdfPatDto();
					dt.setPatientId(x.replaceAll("'", ""));
					dt.setTe("IV not available. Run the Scraping Tool"); 
					dt.setfName(off.getName());
				  //No need to store
				  //treatmentPlanService.saveReportsListBatch(authentication, rules, ivfSheet, sv, off, iVFormType,mode);
			  }
		  }
		  
		 
		 }
		  
			/*
			Object[] obj=new Object[2]; 
			try {
					CaplineIVFFormDtoToXML xml = new CaplineIVFFormDtoToXML();
					String filePath = xml.convertToXML(mainDto, XSLT_PATH);
					File file = new File(filePath);
					String xslt=XSLT_FILE_SEAL;
					
					//o = xml.createPdfStream(

						//	xml.createHtml(filePath, xslt), "");
					//for HTML
					//o=xml.createHtmlOut(filePath, xslt);
					
				    if (file!=null) file.delete(); 
					//To test html for issues
				   //o=xml.createHtmlOut(filePath, XSLT_FILE_SEAL);
					
					//obj[1]=o;

			
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
		}catch(Exception p) {
			p.printStackTrace();
		}
		
		return map;
	}

}
