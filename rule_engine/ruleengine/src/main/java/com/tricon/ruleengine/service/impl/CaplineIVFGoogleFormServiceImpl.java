package com.tricon.ruleengine.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.utils.Constants;

@Transactional(rollbackOn = Exception.class)
@Service
public class CaplineIVFGoogleFormServiceImpl implements CaplineIVFGoogleFormService {

	@Value("${re.admin}")
	private String amdinUserName;

	@Autowired
	OfficeDao od;

	@Autowired
	UserDao userDao;

	@Autowired
	PatientDao patientDao;

	@Override
	public Integer saveIVFFormData(CaplineIVFFormDto d) throws Exception {

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
		Office off = od.getOfficeByName(d.getBasicInfo1());
		off = od.getOfficeByName("Jasper");

		Patient pat = copyValueToPatient(d, off, date);
		Patient patd = patientDao.checkforPatientWithIdAndOffice(pat.getPatientId(), off);
		Integer r = 0;
		try {
			if (patd == null) {

				patd = patientDao.savePatientDataWithDetailsAndHistory(pat, off, user, date);
			} else {
				patd.setUpdatedBy(user);
				patd.setUpdatedDate(date);
				boolean detailsSave=false;
				if (patd.getPatientDetails() != null && patd.getPatientDetails().size() > 0
						&& pat.getPatientDetails() != null && pat.getPatientDetails().size() > 0) {
					Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
					PatientDetail pdN = iter.next();
					boolean oldDetailMatched=false;
					for (PatientDetail pd : patd.getPatientDetails()) {
						if (pd.getGeneralDateIVwasDone().equals(pdN.getGeneralDateIVwasDone())) {
							pdN.setId(pd.getId());
							pdN.setCreatedBy(pd.getCreatedBy());
							pdN.setCreatedDate(pd.getCreatedDate());
							pdN.setPatient(patd);
							pdN.setUpdatedBy(user);
							pdN.setUpdatedDate(date);
							Set<PatientDetail> l = new HashSet<>();
							l.add(pdN);
							patd.setPatientDetails(l);
							oldDetailMatched=true;
							break;
						}
					}
					if (!oldDetailMatched) {
						Set<PatientDetail> savedD=patd.getPatientDetails();
						savedD.clear();
						pdN.setPatient(patd);
						pdN.setCreatedBy(user);
						savedD.add(pdN);
						patd.setPatientDetails(savedD);
						detailsSave=true;
					}

				}
				if (pat.getPatientHistory() != null && pat.getPatientDetails().size() > 0) {

					Set<PatientHistory> result1 = new HashSet<>();
					Set<PatientHistory> newPH = pat.getPatientHistory();
					Set<PatientHistory> phl = patd.getPatientHistory();
					if (newPH != null && newPH.size() > 0) {
						if (phl == null)
							phl = new HashSet<>();
						boolean added = false;
						for (PatientHistory n : newPH) {
							added = true;
							for (PatientHistory o : phl) {
								if (o.getHistoryCode().equals(n.getHistoryCode())
										&& o.getHistoryTooth().equals(n.getHistoryTooth())
										&& o.getHistorySurface().equals(n.getHistorySurface())
										&& o.getHistoryDOS().equals(n.getHistoryDOS())) {
									added = false;
								}
							}
							if (added)
								result1.add(n);
						}

						patd.setPatientHistory(result1);

					}

				}
				patientDao.updatePatientDataWithDetailsAndHistory(patd, off, user,detailsSave);
			}
			r = patd.getId();
		} catch (Exception c) {

		}

		return r;
	}

	@Override
	public Object convertPatientDataToIVFSheetData(Set<String> patIds,String officeName) throws Exception {
		// TODO Auto-generated method stub
		Office off = od.getOfficeByName(officeName);
		
		off = od.getOfficeByName("Jasper");
		
		List<CaplineIVFFormDto> capD=createData(null, off,patIds);
		Map<String,List<IVFTableSheet>> map= new HashMap<>();
		IVFTableSheet sheet = null;
		for(CaplineIVFFormDto cap:capD) {
			
			sheet = new IVFTableSheet();
			sheet.setUniqueID(cap.getBasicInfo1()+"_"+cap.getId());
			copyValueToIVFSheet(cap, off);
			sheet.setAlveoD7310CoveredWithEXT(cap.getOral5());
			List<IVFTableSheet> sheList =map.get(cap.getBasicInfo1()+"_"+cap.getId());
			if (sheList==null) {
				sheList = new ArrayList<>();
				sheList.add(sheet);
				map.put(cap.getBasicInfo1()+"_"+cap.getId(), sheList);
			}else {
				sheList.add(sheet);
			}
		}
		return map;
	}

	
private List<CaplineIVFFormDto> createData(CaplineIVFQueryFormDto d,Office off,Set<String> patIds) {
	
	List<CaplineIVFFormDto> capD=patientDao.searchPatientDetailFromIVF(d, off,null);
	Set<String> patientIds=null;
	List<CaplineIVFFormDto> cList=null;
	
	Map<String,List<CaplineIVFFormDto>> map=new HashMap<>();
	for(CaplineIVFFormDto dto:capD) {
		
		dto.setBasicInfo1(off.getName());
		if (patientIds==null) patientIds= new HashSet<>();
		
		patientIds.add(dto.getBasicInfo21());
		if (map.get(dto.getBasicInfo21()) ==null) {
			cList= new ArrayList<>();
			cList.add(dto);
			map.put(dto.getBasicInfo21(), cList);
		}else {
		List<CaplineIVFFormDto>	x=map.get(dto.getBasicInfo21());
		x.add(dto);
		}
		
	}
	if (patientIds!=null) {
		//List<Patient> pats= patientDao.searchPatientByPatientId(patientIds, off);
		List<PatientHistory> patsH =patientDao.searchPatientHistoryForPatient(patientIds, off);
		/*for(Patient p:pats) {
			List<CaplineIVFFormDto> c=map.get(p.getPatientId());
		    for(CaplineIVFFormDto form:c) {
		    	form.setBasicInfo2(p.getFirstName()+" " +(p.getLastName()==null?"":p.getLastName()));
		    	form.setBasicInfo6(p.getDob());
		    }
		}*/
		for(PatientHistory ph:patsH) {
			List<CaplineIVFFormDto> c=map.get(ph.getPatient().getPatientId());
		    for(CaplineIVFFormDto form:c) {
		    	List<String> his=form.getHistory();
		    	String l=((ph.getHistoryCode().equals(""))?"BLANK":ph.getHistoryCode())+"======!!!!======"+
		    			((ph.getHistoryTooth().equals(""))?"BLANK":ph.getHistoryTooth())+"======!!!!======"+
		    			((ph.getHistorySurface().equals(""))?"BLANK":ph.getHistorySurface())+"======!!!!======"+
		    			((ph.getHistoryDOS().equals(""))?"BLANK":ph.getHistoryDOS());
		    	if (his==null) his=new ArrayList<>();
		    	his.add(l);
		    	form.setHistory(his);
		    	}
		   
		}
	}
	
	return capD;

}
	
	
	@Override
	public Object searchIVFData(CaplineIVFQueryFormDto d) throws Exception {
		// TODO Auto-generated method stub
		Office off = od.getOfficeByName(d.getOfficeNameDB());
		off = od.getOfficeByName("Jasper");
		
		List<CaplineIVFFormDto> capD=createData(d, off,null);
		return capD;
	}

	
	/*
	 * if any changes done here make sure to verify same in copyValueToIVFSheet
	 */
	private Patient copyValueToPatient(CaplineIVFFormDto d, Office off, Date date) {

		Patient p = new Patient();
		PatientDetail pd = new PatientDetail();

		// Office off =od.getOfficeByName(d.getBasicInfo1());
		// off.getName();

		pd.setAlveoD7310CoveredWithEXT(d.getOral5());
		pd.setAlveoD7310FL(d.getOral6());
		pd.setAlveoD7311CoveredWithEXT(d.getOral3());
		pd.setAlveoD7311FL(d.getOral4());

		pd.setAptDate(d.getBasicInfo17());
		pd.setBasicPercentage(d.getPercentages1());
		pd.setBasicSubjectDeductible(d.getPercentages2());
		pd.setBasicWaitingPeriod(d.getWaitingPeriod1());
		pd.setBoneGraftsD7953CoveredWithEXT(d.getDentures5());
		pd.setBoneGraftsD7953FL(d.getDentures6());
		pd.setBuildUpsD2950Covered(d.getPosterior10());
		pd.setBuildUpsD2950FL(d.getPosterior11());
		pd.setBuildUpsD2950SameDayCrown(d.getPosterior12());
		pd.setClaimFillingLimit(d.getPercentages12());
		pd.setcOBStatus(d.getBasicInfo15());
		pd.setComments(d.getComments());
		pd.setCompleteDenturesD5110D5120FL(d.getDentures1());
		pd.setContTxRecallNP(d.getBasicInfo11());
		//policy18//policy19/policy20 -- need to verify
		pd.setCraRequired(d.getPolicy17());
		pd.setCrownLengthD4249FL(d.getOral2());
		pd.setCrownLengthD4249Percentage(d.getOral1());
		pd.setCrownsD2750D2740Downgrade(d.getPosterior6());
		pd.setCrownsD2750D2740FL(d.getPosterior5());
		pd.setCrownsD2750D2740PaysPrepSeatDate(d.getProsthetics3());
		pd.setCrownsD2750D2740Percentage(d.getPosterior4());

		pd.setcSRName(d.getBasicInfo8());
		
		pd.setD9310FL(d.getPosterior9());
		pd.setD9310Percentage(d.getPosterior8());
		pd.setDiagnosticPercentage(d.getPercentages10());
		pd.seteExamsD0145FL(d.getExams3());
		pd.setEmployerName(d.getBasicInfo10());
		pd.setEndodonticsPercentage(d.getPercentages5());
		pd.setEndoSubjectDeductible(d.getPercentages6());
		pd.setExamsD0120FL(d.getExams1());
		pd.setExamsD0140FL(d.getExams2());
		pd.setExamsD0150FL(d.getExams4());
		pd.setExtractionsMajorPercentage(d.getExtractions2());
		pd.setExtractionsMinorPercentage(d.getExtractions1());
		pd.setFillingsBundling(d.getFillings());
		pd.setFlourideAgeLimit(d.getFluroide2());
		pd.setFlourideD1208FL(d.getFluroide1());
		pd.setfMDD4355FL(d.getPerioMnt5());
		pd.setFMDD4355Percentage(d.getPerioMnt4());
		pd.setGeneralBenefitsVerifiedBy(d.getBenefits());
		if (d.getDate().equals(""))
			pd.setGeneralDateIVwasDone(getDateFromCurrentDate(date));
		else
			pd.setGeneralDateIVwasDone(d.getDate());//// need to vertify

		pd.setGingivitisD4346FL(d.getPerioMnt7());
		pd.setGingivitisD4346Percentage(d.getPerioMnt6());
		pd.setGroup(d.getBasicInfo14());
		pd.setImmediateDenturesD5130D5140FL(d.getDentures2());
		pd.setImplantCoverageD6010Percentage(d.getImplants1());
		pd.setImplantCoverageD6057Percentage(d.getImplants2());
		pd.setImplantCoverageD6190Percentage(d.getImplants3());
		pd.setImplantSupportedPorcCeramicD6065Percentage(d.getImplants4());
		pd.setInsAddress(d.getBasicInfo20());
		pd.setInsContact(d.getBasicInfo7());
		pd.setInsName(d.getBasicInfo3());
		
		
		pd.setInterimPartialDenturesD5214FL(d.getDentures4());//Cross check
		
		
		pd.setiVSedationD9243Percentage(d.getSedations2());
		pd.setiVSedationD9248Percentage(d.getSedations3());
		pd.setMajorPercentage(d.getPercentages3());
		pd.setMajorSubjectDeductible(d.getPercentages4());
		pd.setMajorWaitingPeriod(d.getWaitingPeriod2());
		pd.setMemberId(d.getBasicInfo16());
		pd.setMemberSSN(d.getBasicInfo13());
		pd.setMissingToothClause(d.getProsthetics1());
		pd.setName1201110RollOverAgYe(d.getRollage());
		pd.setNightGuardsD9940FL(d.getProsthetics4());
		pd.setNightGuardsD9940Percentage(d.getPosterior7());
		pd.setNitrousD9230Percentage(d.getSedations1());
		pd.setOffice(off);
		pd.setOrthoAgeLimit(d.getOrtho3());
		pd.setOrthoMax(d.getOrtho2());
		pd.setOrthoPercentage(d.getOrtho1());
		pd.setOrthoSubjectDeductible(d.getOrtho4());
		pd.setPartialDenturesD5213D5214FL(d.getDentures3());
		pd.setPatient(p);
		pd.setpAXRaysPercentage(d.getPercentages11());
		pd.setPayerId(d.getBasicInfo18());
		pd.setPerioMaintenanceD4910AltWProphyD0110(d.getPerioMnt3());
		pd.setPerioMaintenanceD4910FL(d.getPerioMnt2());
		pd.setPerioMaintenanceD4910Percentage(d.getPerioMnt1());
		pd.setPerioSurgeryPercentage(d.getPercentages7());
		pd.setPerioSurgerySubjectDeductible(d.getPercentages8());
		pd.setPlanAnnualMax(d.getPolicy7());
		pd.setPlanAnnualMaxRemaining(d.getPolicy8());
		pd.setPlanAssignmentofBenefits(d.getPolicy15());
		pd.setPlanCalendarFiscalYear(d.getPolicy6());
		pd.setPlanCoverageBook(d.getPolicy16());
		pd.setPlanDependentsCoveredtoAge(d.getPolicy11());
		pd.setPlanEffectiveDate(d.getPolicy5());
		pd.setPlanFeeScheduleName(d.getPolicy4());
		pd.setPlanFullTimeStudentStatus(d.getPolicy14());
		pd.setPlanIndividualDeductible(d.getPolicy9());
		pd.setPlanIndividualDeductibleRemaining(d.getPolicy10());
		pd.setPlanNetwork(d.getPolicy3());
		pd.setPlanNonDuplicateClause(d.getPolicy13());
		pd.setPlanPreDMandatory(d.getPolicy12());
		pd.setPlanTermedDate(d.getPolicy2());
		pd.setPlanType(d.getPolicy1());
		pd.setPolicyHolder(d.getBasicInfo5());
		pd.setPolicyHolderDOB(d.getBasicInfo9());
		pd.setPostCompositesD2391FL(d.getPosterior2());
		pd.setPostCompositesD2391Percentage(d.getPosterior1());
		pd.setPosteriorCompositesD2391Downgrade(d.getPosterior3());
		pd.setPreventivePercentage(d.getPercentages9());
		pd.setProphyD1110FL(d.getProphy1());
		pd.setProphyD1120FL(d.getProphy2());
		pd.setProviderName(d.getBasicInfo19());
		pd.setRef(d.getBasicInfo12());
		pd.setReplacementClause(d.getProsthetics2());

		pd.setSealantsD1351AgeLimit(d.getSealants2());
		pd.setSealantsD1351FL(d.getSealants1());
		pd.setSealantsD1351Percentage(d.getSealantsD());
		pd.setSealantsD1351PermanentMolarsCovered(d.getSealants5());
		pd.setSealantsD1351PrimaryMolarsCovered(d.getSealants3());
		pd.setSealantsD1351PreMolarsCovered(d.getSealants4());

		pd.setsRPD4341DaysBwTreatment(d.getPerio4());
		pd.setsRPD4341FL(d.getPerio2());
		pd.setsRPD4341Percentage(d.getPerio1());
		pd.setsRPD4341QuadsPerDay(d.getPerio3());
		pd.setsSCD2930FL(d.getSsc1());
		pd.setsSCD2931FL(d.getSsc2());
		pd.setTaxId(d.getBasicInfo4());
		if (off != null)
			pd.setUniqueID(off.getName() + "_");// -- will set latter;
		pd.setVarnishD1206AgeLimit(d.getFluroide4());
		pd.setVarnishD1206FL(d.getFluroide3());
		pd.setxRaysBundling(d.getXrays5());
		pd.setxRaysBWSFL(d.getXrays1());
		pd.setxRaysFMXFL(d.getXrays4());
		pd.setxRaysPAD0220FL(d.getXrays2());
		pd.setxRaysPAD0230FL(d.getXrays3());
		///////////////////////////

		p.setDob(d.getBasicInfo6());
		String fname = d.getBasicInfo2();
		if (fname != null) {
			String[] f = fname.split(" ");
			p.setFirstName(f[0]);
			if (f.length > 1) {
				p.setLastName(fname.replace(f[0] + " ", ""));
			}
		}
		p.setPatientId(d.getBasicInfo21());
		p.setSalutation("");
		Set<PatientDetail> pl = new HashSet<>();
		pl.add(pd);
		p.setPatientDetails(pl);
		p.setOffice(off);

		List<String> hl = d.getHistory();
		Set<PatientHistory> phl = new HashSet<PatientHistory>();
		PatientHistory ph = null;
		int x = 0;
		for (String h : hl) {
			if (x == 0)
				ph = new PatientHistory();
			x++;
			if (x == 1) {
				ph.setHistoryCode(h);
			} else if (x == 2) {
				if (h.split("-").length == 2)
					ph.setHistorySurface(h.split("-")[1]);
				else
					ph.setHistorySurface("");
				ph.setHistoryTooth(h.split("-")[0]);
			} else if (x == 3) {
				ph.setHistoryDOS(h);
				x = 0;
				if (!ph.getHistoryDOS().equals(""))
					phl.add(ph);
			}

		}
		p.setPatientHistory(phl);
		// history logic.d.

		return p;
	}
	
	/*
	 * if any changes done here make sure to verify same in copyValueToPatient
	 */
	private IVFTableSheet copyValueToIVFSheet(CaplineIVFFormDto d, Office off) {

		//Patient p = new Patient();
		IVFTableSheet pd = new IVFTableSheet();

		// Office off =od.getOfficeByName(d.getBasicInfo1());
		// off.getName();

		pd.setAlveoD7310CoveredWithEXT(d.getOral5());
		pd.setAlveoD7310FL(d.getOral6());
		pd.setAlveoD7311CoveredWithEXT(d.getOral3());
		pd.setAlveoD7311FL(d.getOral4());

		pd.setAptDate(d.getBasicInfo17());
		pd.setBasicPercentage(d.getPercentages1());
		pd.setBasicSubjectDeductible(d.getPercentages2());
		pd.setBasicWaitingPeriod(d.getWaitingPeriod1());
		pd.setBoneGraftsD7953CoveredWithEXT(d.getDentures5());
		pd.setBoneGraftsD7953FL(d.getDentures6());
		pd.setBuildUpsD2950Covered(d.getPosterior10());
		pd.setBuildUpsD2950FL(d.getPosterior11());
		pd.setBuildUpsD2950SameDayCrown(d.getPosterior12());
		pd.setClaimFillingLimit(d.getPercentages12());
		pd.setcOBStatus(d.getBasicInfo15());
		pd.setComments(d.getComments());
		pd.setCompleteDenturesD5110D5120FL(d.getDentures1());
		pd.setContTxRecallNP(d.getBasicInfo11());
		//policy18//policy19/policy20 -- need to verify
		pd.setCraRequired(d.getPolicy17());
		pd.setCrownLengthD4249FL(d.getOral2());
		pd.setCrownLengthD4249Percentage(d.getOral1());
		pd.setCrownsD2750D2740Downgrade(d.getPosterior6());
		pd.setCrownsD2750D2740FL(d.getPosterior5());
		pd.setCrownsD2750D2740PaysPrepSeatDate(d.getProsthetics3());
		pd.setCrownsD2750D2740Percentage(d.getPosterior4());

		pd.setcSRName(d.getBasicInfo8());
		
		pd.setD9310FL(d.getPosterior9());
		pd.setD9310Percentage(d.getPosterior8());
		pd.setDiagnosticPercentage(d.getPercentages10());
		pd.seteExamsD0145FL(d.getExams3());
		pd.setEmployerName(d.getBasicInfo10());
		pd.setEndodonticsPercentage(d.getPercentages5());
		pd.setEndoSubjectDeductible(d.getPercentages6());
		pd.setExamsD0120FL(d.getExams1());
		pd.setExamsD0140FL(d.getExams2());
		pd.setExamsD0150FL(d.getExams4());
		pd.setExtractionsMajorPercentage(d.getExtractions2());
		pd.setExtractionsMinorPercentage(d.getExtractions1());
		pd.setFillingsBundling(d.getFillings());
		pd.setFlourideAgeLimit(d.getFluroide2());
		pd.setFlourideD1208FL(d.getFluroide1());
		pd.setfMDD4355FL(d.getPerioMnt5());
		pd.setFMDD4355Percentage(d.getPerioMnt4());
		pd.setGeneralBenefitsVerifiedBy(d.getBenefits());
		
		pd.setGeneralDateIVwasDone(d.getDate());//// need to vertify

		pd.setGingivitisD4346FL(d.getPerioMnt7());
		pd.setGingivitisD4346Percentage(d.getPerioMnt6());
		pd.setGroup(d.getBasicInfo14());
		pd.setImmediateDenturesD5130D5140FL(d.getDentures2());
		pd.setImplantCoverageD6010Percentage(d.getImplants1());
		pd.setImplantCoverageD6057Percentage(d.getImplants2());
		pd.setImplantCoverageD6190Percentage(d.getImplants3());
		pd.setImplantSupportedPorcCeramicD6065Percentage(d.getImplants4());
		pd.setInsAddress(d.getBasicInfo20());
		pd.setInsContact(d.getBasicInfo7());
		pd.setInsName(d.getBasicInfo3());
		
		
		pd.setInterimPartialDenturesD5214FL(d.getDentures4());//Cross check
		
		
		pd.setiVSedationD9243Percentage(d.getSedations2());
		pd.setiVSedationD9248Percentage(d.getSedations3());
		pd.setMajorPercentage(d.getPercentages3());
		pd.setMajorSubjectDeductible(d.getPercentages4());
		pd.setMajorWaitingPeriod(d.getWaitingPeriod2());
		pd.setMemberId(d.getBasicInfo16());
		pd.setMemberSSN(d.getBasicInfo13());
		pd.setMissingToothClause(d.getProsthetics1());
		pd.setName1201110RollOverAgYe(d.getRollage());
		pd.setNightGuardsD9940FL(d.getProsthetics4());
		pd.setNightGuardsD9940Percentage(d.getPosterior7());
		pd.setNitrousD9230Percentage(d.getSedations1());
		pd.setOfficeName(off.getName());
		pd.setOrthoAgeLimit(d.getOrtho3());
		pd.setOrthoMax(d.getOrtho2());
		pd.setOrthoPercentage(d.getOrtho1());
		pd.setOrthoSubjectDeductible(d.getOrtho4());
		pd.setPartialDenturesD5213D5214FL(d.getDentures3());
		pd.setPatientName(d.getBasicInfo2());///-----
		pd.setPatientDOB(d.getBasicInfo6());///-----
		pd.setPatientId(d.getBasicInfo21());///---
		pd.setpAXRaysPercentage(d.getPercentages11());
		pd.setPayerId(d.getBasicInfo18());
		pd.setPerioMaintenanceD4910AltWProphyD0110(d.getPerioMnt3());
		pd.setPerioMaintenanceD4910FL(d.getPerioMnt2());
		pd.setPerioMaintenanceD4910Percentage(d.getPerioMnt1());
		pd.setPerioSurgeryPercentage(d.getPercentages7());
		pd.setPerioSurgerySubjectDeductible(d.getPercentages8());
		pd.setPlanAnnualMax(d.getPolicy7());
		pd.setPlanAnnualMaxRemaining(d.getPolicy8());
		pd.setPlanAssignmentofBenefits(d.getPolicy15());
		pd.setPlanCalendarFiscalYear(d.getPolicy6());
		pd.setPlanCoverageBook(d.getPolicy16());
		pd.setPlanDependentsCoveredtoAge(d.getPolicy11());
		pd.setPlanEffectiveDate(d.getPolicy5());
		pd.setPlanFeeScheduleName(d.getPolicy4());
		pd.setPlanFullTimeStudentStatus(d.getPolicy14());
		pd.setPlanIndividualDeductible(d.getPolicy9());
		pd.setPlanIndividualDeductibleRemaining(d.getPolicy10());
		pd.setPlanNetwork(d.getPolicy3());
		pd.setPlanNonDuplicateClause(d.getPolicy13());
		pd.setPlanPreDMandatory(d.getPolicy12());
		pd.setPlanTermedDate(d.getPolicy2());
		pd.setPlanType(d.getPolicy1());
		pd.setPolicyHolder(d.getBasicInfo5());
		pd.setPolicyHolderDOB(d.getBasicInfo9());
		pd.setPostCompositesD2391FL(d.getPosterior2());
		pd.setPostCompositesD2391Percentage(d.getPosterior1());
		pd.setPosteriorCompositesD2391Downgrade(d.getPosterior3());
		pd.setPreventivePercentage(d.getPercentages9());
		pd.setProphyD1110FL(d.getProphy1());
		pd.setProphyD1120FL(d.getProphy2());
		pd.setProviderName(d.getBasicInfo19());
		pd.setRef(d.getBasicInfo12());
		pd.setReplacementClause(d.getProsthetics2());

		pd.setSealantsD1351AgeLimit(d.getSealants2());
		pd.setSealantsD1351FL(d.getSealants1());
		pd.setSealantsD1351Percentage(d.getSealantsD());
		pd.setSealantsD1351PermanentMolarsCovered(d.getSealants5());
		pd.setSealantsD1351PrimaryMolarsCovered(d.getSealants3());
		pd.setSealantsD1351PreMolarsCovered(d.getSealants4());

		pd.setsRPD4341DaysBwTreatment(d.getPerio4());
		pd.setsRPD4341FL(d.getPerio2());
		pd.setsRPD4341Percentage(d.getPerio1());
		pd.setsRPD4341QuadsPerDay(d.getPerio3());
		pd.setsSCD2930FL(d.getSsc1());
		pd.setsSCD2931FL(d.getSsc2());
		pd.setTaxId(d.getBasicInfo4());
		
		if (off != null)
			pd.setUniqueID(off.getName() + "_");// -- will set latter;
		pd.setVarnishD1206AgeLimit(d.getFluroide4());
		pd.setVarnishD1206FL(d.getFluroide3());
		pd.setxRaysBundling(d.getXrays5());
		pd.setxRaysBWSFL(d.getXrays1());
		pd.setxRaysFMXFL(d.getXrays4());
		pd.setxRaysPAD0220FL(d.getXrays2());
		pd.setxRaysPAD0230FL(d.getXrays3());
		///////////////////////////

		//p.setDob(d.getBasicInfo6());
		/*String fname = d.getBasicInfo2();
		if (fname != null) {
			String[] f = fname.split(" ");
			p.setFirstName(f[0]);
			if (f.length > 1) {
				p.setLastName(fname.replace(f[0] + " ", ""));
			}
		}*/
		//p.setPatientId(d.getBasicInfo21());
		//p.setSalutation("");
		Set<IVFTableSheet > pl = new HashSet<>();
		pl.add(pd);
		//p.setPatientDetails(pl);
		//p.setOffice(off);

		List<String> hl = d.getHistory();
		Set<IVFHistorySheet> phl = new HashSet<IVFHistorySheet>();
		IVFHistorySheet ph = null;
		int x = 0;
		for (String h : hl) {
			if (x == 0)
				ph = new IVFHistorySheet();
			x++;
			if (x == 1) {
				ph.setHistoryCode(h);
			} else if (x == 2) {
				if (h.split("-").length == 2)
					ph.setHistorySurface(h.split("-")[1]);
				else
					ph.setHistorySurface("");
				ph.setHistoryTooth(h.split("-")[0]);
			} else if (x == 3) {
				ph.setHistoryDOS(h);
				x = 0;
				if (!ph.getHistoryDOS().equals(""))
					phl.add(ph);
			}

		}
		// history logic.d.

		return pd;
	}

	
	private String getDateFromCurrentDate(Date date) {
		
			return Constants.SIMPLE_DATE_FORMAT_IVF.format(date);

	}

}
