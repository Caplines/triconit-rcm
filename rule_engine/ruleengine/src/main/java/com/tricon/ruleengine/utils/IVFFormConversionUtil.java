package com.tricon.ruleengine.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.OrthoGoogleSheetDto;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientDetail2;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

public class IVFFormConversionUtil {

	/* Copy from Google Form and DUMP Sheet to IVF RDBMS database 
	 * if any changes done here make sure to verify same in copyValueToIVFSheet method defined below
	 */
	public static Patient copyValueToPatientFROMIVOSGoogle(CaplineIVFFormDto d, Office off, Date date, IVFormType iVFormType) {

		Patient p = new Patient();
		PatientDetail pd = new PatientDetail();
		PatientDetail2 pd2= new PatientDetail2();
		pd.setiVFormType(iVFormType);
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
		pd.setD0120(d.getPolicy18());
		pd.setD2391(d.getPolicy19());
		
		// policy18//policy19/policy20 -- need to verify
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
			pd.setGeneralDateIVwasDone(getIVFDateFromatofDate(date));
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

		pd.setInterimPartialDenturesD5214FL(d.getDentures4());// Cross check D5820

		pd.setiVSedationD9243Percentage(d.getSedations2());
		pd.setiVSedationD9248Percentage(d.getSedations3());
		pd.setMajorPercentage(d.getPercentages3());
		pd.setMajorSubjectDeductible(d.getPercentages4());
		pd.setMajorWaitingPeriod(d.getWaitingPeriod2());
		pd.setMemberId(d.getBasicInfo16());
		pd.setMemberSSN(d.getBasicInfo13());
		pd.setMissingToothClause(d.getProsthetics1());
		pd.setName1201110RollOverAgYe(d.getRollage());
		pd.setNightGuardsD9940FL(d.getProsthetics4());//This is not here..
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
		
		//new in IVF html form
		pd.setPreventiveSubDed(d.getPercentages13());//percentages13
		pd.setDiagnosticSubDed(d.getPercentages14());//percentages14
		pd.setpAXRaysSubDed(d.getPercentages15());//percentages15
		
		pd.setDen5225Per(d.getDen5225());// den5225
		pd.setDenf5225FR(d.getDenf5225());//denf5225 
		pd.setDen5226Per(d.getDen5226());//den5226 
		pd.setDenf5226Fr(d.getDenf5226());//denf5226 
		pd.setBridges1(d.getBridges1());//bridges1 
		pd.setBridges2(d.getBridges2());//bridges2 
		pd.setWillDowngradeApplicable(d.getCdowngrade());//cdowngrade
		pd.setImplantsFrD6010(d.getImplants5());//implants5
		pd.setImplantsFrD6057(d.getImplants6());//implants6
		pd.setImplantsFrD6065(d.getImplants7());//implants7
		pd.setImplantsFrD6190(d.getImplants8());;//implants8
		pd.setOrthoRemaining(d.getOrtho5());//ortho5
		pd.setOrthoWaitingPeriod(d.getWaitingPeriod3());//??waitingPeriod3
		//pd.setWillCrowngrade("");//posterior16
		pd.setCrowngradeCode(d.getPosterior17());//posterior17	
		pd.setNightGuardsD9945Percentage(d.getPosterior18());//posterior18
		pd.setFmxPer(d.getPercentages16());//percentages16 
		
		pd.setNightGuardsD9944Fr(d.getPosterior19());
		pd.setNightGuardsD9945Fr(d.getPosterior20());
		pd.setWaitingPeriod4(d.getWaitingPeriod4());
		pd.setShareFr(d.getShareFr());
		pd.setPedo1(d.getPedo1());//pedo1
		pd.setPedo2(d.getPedo2());//pedo2
		pd.setPano1(d.getPano1());//pano1
		pd.setPano2(d.getPano2());//pano2
		pd.setD4381(d.getD4381());//d4381
		//pd.setInsuranceType(d.getInsuranceType()); //Insurance Type
		pd.setFillingsInYear(d.getFill1());//fill1
		pd.setExtractionsInYear(d.getExtr1());//extr1
		pd.setCrownsInYear(d.getCrn1());//crn1
		 
		pd.setCkD0120(d.getCkD0120());//ckD0120
		pd.setCkD0140(d.getCkD0140());//ckD0140
		pd.setCkD0145(d.getCkD0145());//ckD0145
		pd.setCkD0150(d.getCkD0150());//ckD0150
		pd.setCkD0160(d.getCkD0160());//ckD0160
		pd.setCkD210(d.getCkD210());//ckD210
		pd.setCkD220(d.getCkD220());//ckD220
		pd.setCkD230(d.getCkD230());//ckD230
		pd.setCkD330(d.getCkD330());//ckD330
		pd.setCkD274(d.getCkD274());//ckD274

		pd.setD0160Freq(d.getD0160Freq());//d0160Freq
		pd.setD2391Freq(d.getD2391Freq());//d2391Freq
		pd.setD0330Freq(d.getD0330Freq());//d0330Freq
		pd.setD4381Freq(d.getD4381Freq());//d4381Freq
		pd.setD3330(d.getD3330());//d3330
		pd.setD3330Freq(d.getD3330Freq());//d3330Freq
		pd.setFreqD2934(d.getFreqD2934());
		pd.setHistoryCount(d.getHistoryCount());
		pd2.setNpi(d.getNpi());
		pd2.setLicence(d.getLicence());
		pd2.setRadio3(d.getRadio3());
		pd2.setRadio4(d.getRadio4());
		pd2.setRadio5(d.getRadio5());
		pd2.setRadio1(d.getRadio1());
		pd2.setRadio2(d.getRadio2());
		pd2.setCorrdOfBenefits(d.getCorrdOfBenefits());
		pd2.setWhatAmountD7210(d.getWhatAmountD7210());
		pd2.setAllowAmountD7240(d.getAllowAmountD7240());
		pd2.setD7210(d.getD7210());
		pd2.setD7220(d.getD7220());
		pd2.setD7230(d.getD7230());
		pd2.setD7240(d.getD7240());
		pd2.setD7250(d.getD7250());
		pd2.setD7310(d.getD7310());
		pd2.setD7311(d.getD7311());
		pd2.setD7320(d.getD7320());
		pd2.setD7321(d.getD7321());
		pd2.setD7473(d.getD7473());

		pd2.setD9239(d.getD9239());
		pd2.setD4263(d.getD4263());
		pd2.setD4264(d.getD4264());
		pd2.setD6104(d.getD6104());
		pd2.setD7953(d.getD7953());
		pd2.setD3310(d.getD3310());
		pd2.setD3320(d.getD3320());
		pd2.setD3330(d.getD33300());
		pd2.setD3346(d.getD3346());
		pd2.setD3347(d.getD3347());
		pd2.setD3348(d.getD3348());
		pd2.setD6058(d.getD6058());
		pd2.setD7951(d.getD7951());
		pd2.setD4266(d.getD4266());
		pd2.setD4267(d.getD4267());
		pd2.setD4273(d.getD4273());
		pd2.setD7251(d.getD7251());
		
		pd2.setD7210fr(d.getD7210fr());
		pd2.setD7220fr(d.getD7220fr());
		pd2.setD7230fr(d.getD7230fr());
		pd2.setD7240fr(d.getD7240fr());
		pd2.setD7250fr(d.getD7250fr());
		pd2.setD7310fr(d.getD7310fr());
		pd2.setD7311fr(d.getD7311fr());
		pd2.setD7320fr(d.getD7320fr());
		pd2.setD7321fr(d.getD7321fr());
		pd2.setD7473fr(d.getD7473fr());
		pd2.setSedations1fr(d.getSedations1fr());
		pd2.setSedations3fr(d.getSedations3fr());
		pd2.setD9239fr(d.getD9239fr());
		pd2.setSedations2fr(d.getSedations2fr());
		pd2.setD4263fr(d.getD4263fr());
		pd2.setD4264fr(d.getD4264fr());
		pd2.setD6104fr(d.getD6104fr());
		pd2.setD7953fr(d.getD7953fr());
		pd2.setD3310fr(d.getD3310fr());
		pd2.setD3320fr(d.getD3320fr());
		pd2.setD3346fr(d.getD3346fr());
		pd2.setD3347fr(d.getD3347fr());
		pd2.setD3348fr(d.getD3348fr());
		pd2.setD6058fr(d.getD6058fr());
		pd2.setOral1fr(d.getOral1fr());
		pd2.setD7951fr(d.getD7951fr());
		pd2.setD4266fr(d.getD4266fr());
		pd2.setD4267fr(d.getD4267fr());
		pd2.setPerio1fr(d.getPerio1fr());
		pd2.setD4273fr(d.getD4273fr());
		pd2.setD7251fr(d.getD7251fr());
		
		pd2.setD7210fr(d.getD7210fr());
		pd2.setD7220fr(d.getD7220fr());
		pd2.setD7230fr(d.getD7230fr());
		pd2.setD7240fr(d.getD7240fr());
		pd2.setD7250fr(d.getD7250fr());
		pd2.setD7310fr(d.getD7310fr());
		pd2.setD7311fr(d.getD7311fr());
		pd2.setD7320fr(d.getD7320fr());
		pd2.setD7321fr(d.getD7321fr());
		pd2.setD7473fr(d.getD7473fr());
		pd2.setSedations1fr(d.getSedations1fr());
		pd2.setSedations3fr(d.getSedations3fr());
		pd2.setD9239fr(d.getD9239fr());
		pd2.setSedations2fr(d.getSedations2fr());
		pd2.setD4263fr(d.getD4263fr());
		pd2.setD4264fr(d.getD4264fr());
		pd2.setD6104fr(d.getD6104fr());
		pd2.setD7953fr(d.getD7953fr());
		pd2.setD3310fr(d.getD3310fr());
		pd2.setD3320fr(d.getD3320fr());
		pd2.setD3346fr(d.getD3346fr());
		pd2.setD3347fr(d.getD3347fr());
		pd2.setD3348fr(d.getD3348fr());
		pd2.setD6058fr(d.getD6058fr());
		pd2.setOral1fr(d.getOral1fr());
		pd2.setD7951fr(d.getD7951fr());
		pd2.setD4266fr(d.getD4266fr());
		pd2.setD4267fr(d.getD4267fr());
		pd2.setPerio1fr(d.getPerio1fr());
		pd2.setD4273fr(d.getD4273fr());
		pd2.setD7251fr(d.getD7251fr());

		pd2.setD7472(d.getD7472());
		pd2.setD7472fr(d.getD7472fr());
		pd2.setD7280(d.getD7280());
		pd2.setD7280fr(d.getD7280fr());
		pd2.setD7282(d.getD7282());
		pd2.setD7282fr(d.getD7282fr());
		pd2.setD7283(d.getD7283());
		pd2.setD7283fr(d.getD7283fr());
		pd2.setD7952(d.getD7952());
		pd2.setD7952fr(d.getD7952fr());
		pd2.setD7285(d.getD7285());
		pd2.setD7285fr(d.getD7285fr());
		pd2.setD6114(d.getD6114());
		pd2.setD6114fr(d.getD6114fr());
		pd2.setD5860(d.getD5860());
		pd2.setD5860fr(d.getD5860fr());
		pd2.setD5110(d.getD5110());
		pd2.setD5110fr(d.getD5110fr());
		pd2.setD5130(d.getD5130());
		pd2.setD5130fr(d.getD5130fr());
		pd2.setD0140(d.getD0140());
		pd2.setIvSedation(d.getIvSedation());
		pd2.setsRemarks(d.getsRemarks());
		pd2.setmPolicy(d.getmPolicy());
		pd2.setmMIP(d.getmMIP());
		pd2.setEsBcbs(d.getEsBcbs());
		pd2.setObtainMPN(d.getObtainMPN());
		pd2.setWaitingPeriodDuration(d.getWaitingPeriodDuration());
		pd2.setD0350(d.getD0350());
		pd2.setD1330(d.getD1330());
		pd2.setD2930(d.getD2930());
		pd2.setSrpd4341(d.getSrpd4341());
		pd2.setMajord72101(d.getMajord72101());
		
		pd2.setFmxSubjectToDed(d.getFmxSubjectToDed());
		pd2.setD7250(d.getD7250());
		pd2.setD1510(d.getD1510());
		pd2.setD1510Freq(d.getD1510Freq());
		pd2.setD1516(d.getD1516());
		pd2.setD1516Freq(d.getD1516Freq());
		pd2.setD1517(d.getD1517());
		pd2.setD1517Freq(d.getD1517Freq());
		pd2.setD3220(d.getD3220());
		pd2.setD3220Freq(d.getD3220Freq());
		pd2.setOutNetworkMessage(d.getOutNetworkMessage());
		pd2.setOsPlanType(d.getOsPlanType());
		pd2.setSmAgeLimit(d.getSmAgeLimit());
		pd2.setPerioD4921(d.getPerioD4921());
		pd2.setD4921Frequency(d.getD4921Frequency());
		pd2.setPerioD4266(d.getPerioD4266());
		pd2.setD4266Frequency(d.getD4266Frequency());
		pd2.setPerioD9910(d.getPerioD9910());
		pd2.setD9910Frequency(d.getD9910Frequency());
		pd2.setOonbenfits(d.getOonbenfits());
		pd2.setD9630(d.getD9630());
		pd2.setD9630fr(d.getD9630fr());
		pd2.setD0431(d.getD0431());
		pd2.setD0431fr(d.getD0431fr());
		pd2.setD4999(d.getD4999());
		pd2.setD4999fr(d.getD4999fr());
		pd2.setD2962(d.getD2962());
		pd2.setD2962fr(d.getD2962fr());
		
		pd2.setD0145(d.getD0145());
		pd2.setD0150(d.getD0150());
		pd2.setD2750(d.getD2750());
		pd2.setD2750fr(d.getD2750fr());
		pd2.setD0220(d.getD0220());
		pd2.setD0220Freq(d.getD0220Freq());
		pd2.setD0230(d.getD0230());
		pd2.setBwx(d.getBwx());
		pd2.setD0210(d.getD0210());
		pd2.setD0210Freq(d.getD0210Freq());
		pd2.setD0350Freq(d.getD0350Freq());
		pd2.setBwxFreq(d.getBwxFreq());
		pd2.setD2931(d.getD2931());
		pd2.setD1206(d.getD1206());
		pd2.setD1208(d.getD1208());
		pd2.setbWhichCode(d.getbWhichCode());
		pd2.setD5110_20(d.getD5110_20());
		pd2.setD1330Freq(d.getD1330Freq());
		pd2.setD5111_12_13_14(d.getD5111_12_13_14());
		pd2.setD5130_40(d.getD5130_40());
		pd2.setD5810_c(d.getD5810_c());
		pd2.setD5225_26_c(d.getD5225_26_c());
		pd2.setExtractions1fr(d.getExtractions1fr());
		pd2.setExtractions2fr(d.getExtractions2fr());
		pd2.setImplantsC(d.getImplantsC());
		pd2.setD1520_26_27(d.getD1520_26_27());
		pd2.setD1520_26_27_fr(d.getD1520_26_27_fr());
		pd2.setWaitingPeriod(d.getWaitingPeriod());
		pd2.setWip(d.getWip());
		pd2.setInsBillingC(d.getInsBillingC());
		pd2.setBenefitPeriod(d.getBenefitPeriod());
		pd2.setWaitingPeriodDrop(d.getWaitingPeriodDrop());
		pd2.setD8070(d.getD8070());
		pd2.setD8080(d.getD8080());
		pd2.setD8090(d.getD8090());
		pd2.setD8670(d.getD8670());
		pd2.setD8680(d.getD8680());
		pd2.setD8690(d.getD8690());
		pd2.setD8070fr(d.getD8070fr());
		pd2.setD8080fr(d.getD8080fr());
		pd2.setD8090fr(d.getD8090fr());
		pd2.setD8670fr(d.getD8670fr());
		pd2.setD8680fr(d.getD8680fr());
		pd2.setD8690fr(d.getD8690fr());
		pd2.setApptype(d.getApptype());
		pd2.setSecProviderName(d.getSecProviderName());
		pd2.setSecProvNetwork(d.getSecProvNetwork());
		pd2.setYesNoAssignToffice(d.getYesNoAssignToffice());
		pd2.setD1120(d.getD1120());
		pd2.setD1110(d.getD1110());
		pd2.setPolicy18(d.getD0120());
		pd2.setD7953Extraction(d.getD7953Extraction());
		
		pd2.setD8660(d.getD8660());
		pd2.setD8660fr(d.getD8660fr());
		pd2.setD8210(d.getD8210());
		pd2.setD8210fr(d.getD8210fr());
		pd2.setD8220(d.getD8220());
		pd2.setD8220fr(d.getD8220fr());
		pd2.setD8020(d.getD8020());
		pd2.setD8020fr(d.getD8020fr());
		pd2.setD8692(d.getD8692());
		pd2.setD8692fr(d.getD8692fr());
		pd2.setImplantsCPercentage(d.getImplantsCPercentage());
		pd2.setDoesExamShareFreq(d.getDoesExamShareFreq());
		pd2.setD511020Percentage(d.getD511020Percentage());
		pd2.setD513040Percentage(d.getD513040Percentage());
		pd2.setD5810CPercentage(d.getD5810CPercentage());
		
		pd.setPatientDetails2(pd2);
		
		Set<PatientDetail2> p2Set = new HashSet<>();
		p2Set.add(pd2);
		
		p.setPatientDetails2(p2Set);
		//END		
		
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
		pd.setComments(d.getComments());
        pd.setGeneralBenefitsVerifiedBy(d.getBenefits());
        
		p.setDob(d.getBasicInfo6());
		
		
		String fname = d.getBasicInfo2();
		if (fname != null) {
			String[] f = fname.split(" ");
			if (f.length>0) {
			p.setFirstName(f[0]);
			if (f.length > 1) {
				p.setLastName(fname.replace(f[0] + " ", ""));
			}
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
	 * if any changes done here make sure to verify same in copyValueToPatient method defined above
	 */
	public static IVFTableSheet copyValueToIVFSheet(CaplineIVFFormDto d, Office off,boolean setIVFHis) {

		// Patient p = new Patient();
		IVFTableSheet pd = new IVFTableSheet();

		// Office off =od.getOfficeByName(d.getBasicInfo1());
		// off.getName();
		//pd.setivf
		pd.setIvFormTypeId(d.getIvFormTypeId());
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
		pd.setD0120(d.getPolicy18());
		pd.setD2391(d.getPolicy19());
		// policy18//policy19/policy20 -- need to verify
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

		pd.setGeneralDateIVwasDone(d.getDate());//// need to verify

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

		pd.setInterimPartialDenturesD5214FL(d.getDentures4());// Cross check D5820

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
		pd.setPatientName(d.getBasicInfo2());/// -----
		pd.setPatientDOB(d.getBasicInfo6());/// -----
		pd.setPatientId(d.getBasicInfo21());/// ---
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

		
		//new in IVF html form
				pd.setPreventiveSubDed(d.getPercentages13());//percentages13
				pd.setDiagnosticSubDed(d.getPercentages14());//percentages14
				pd.setpAXRaysSubDed(d.getPercentages15());//percentages15
				
				pd.setDen5225Per(d.getDen5225());// den5225
				pd.setDenf5225FR(d.getDenf5225());//denf5225 
				pd.setDen5226Per(d.getDen5226());//den5226 
				pd.setDenf5226Fr(d.getDenf5226());//denf5226 
				pd.setBridges1(d.getBridges1());//bridges1 
				pd.setBridges2(d.getBridges2());//bridges2 
				pd.setWillDowngradeApplicable(d.getCdowngrade());//cdowngrade
				pd.setImplantsFrD6010(d.getImplants5());//implants5
				pd.setImplantsFrD6057(d.getImplants6());//implants6
				pd.setImplantsFrD6065(d.getImplants7());//implants7
				pd.setImplantsFrD6190(d.getImplants8());;//implants8
				pd.setOrthoRemaining(d.getOrtho5());//ortho5
				pd.setOrthoWaitingPeriod(d.getWaitingPeriod3());//??waitingPeriod3
				//pd.setWillCrowngrade(d.getPosterior16());//posterior16
				pd.setCrowngradeCode(d.getPosterior17());//posterior17
				pd.setNightGuardsD9945Percentage(d.getPosterior18());//posterior18
				pd.setFmxPer(d.getPercentages16());//percentages16 
				
				pd.setNightGuardsD9944Fr(d.getPosterior19());
				pd.setNightGuardsD9945Fr(d.getPosterior20());
				pd.setWaitingPeriod4(d.getWaitingPeriod4());
				pd.setShareFr(d.getShareFr());
				pd.setPedo1(d.getPedo1());//pedo1
				pd.setPedo2(d.getPedo2());//pedo2
				pd.setPano1(d.getPano1());//pano1
				pd.setPano2(d.getPano2());//pano2
				pd.setD4381(d.getD4381());//d4381
				//pd.setInsuranceType(d.getInsuranceType()); //Insurance Type
				pd.setFillingsInYear(d.getFill1());//fill1
				pd.setExtractionsInYear(d.getExtr1());//extr1
				pd.setCrownsInYear(d.getCrn1());//crn1

				//END			
				pd.setCkD0120(d.getCkD0120());//ckD0120
				pd.setCkD0140(d.getCkD0140());//ckD0140
				pd.setCkD0145(d.getCkD0145());//ckD0145
				pd.setCkD0150(d.getCkD0150());//ckD0150
				pd.setCkD0160(d.getCkD0160());//ckD0160
				pd.setCkD210(d.getCkD210());//ckD210
				pd.setCkD220(d.getCkD220());//ckD220
				pd.setCkD230(d.getCkD230());//ckD230
				pd.setCkD330(d.getCkD330());//ckD330
				pd.setCkD274(d.getCkD274());//ckD274
				pd.setD0160Freq(d.getD0160Freq());//d0160Freq
				pd.setD2391Freq(d.getD2391Freq());//d2391Freq
				pd.setD0330Freq(d.getD0330Freq());//d0330Freq
				pd.setD4381Freq(d.getD4381Freq());//d4381Freq
				pd.setD3330(d.getD3330());//d3330
				pd.setD3330Freq(d.getD3330Freq());//d3330Freq
				pd.setFreqD2934(d.getFreqD2934());
				pd.setHistoryCount(d.getHistoryCount());
				pd.setNpi(d.getNpi());
				pd.setLicence(d.getLicence());
				pd.setRadio3(d.getRadio3());
				pd.setRadio4(d.getRadio4());
				pd.setRadio5(d.getRadio5());
				pd.setRadio1(d.getRadio1());
				pd.setRadio2(d.getRadio2());
				pd.setCorrdOfBenefits(d.getCorrdOfBenefits());
				pd.setWhatAmountD7210(d.getWhatAmountD7210());
				pd.setAllowAmountD7240(d.getAllowAmountD7240());
				pd.setD7210(d.getD7210());
				pd.setD7220(d.getD7220());
				pd.setD7230(d.getD7230());
				pd.setD7240(d.getD7240());
				pd.setD7250(d.getD7250());
				pd.setD7310(d.getD7310());
				pd.setD7311(d.getD7311());
				pd.setD7320(d.getD7320());
				pd.setD7321(d.getD7321());
				pd.setD7473(d.getD7473());

				pd.setD9239(d.getD9239());
				pd.setD4263(d.getD4263());
				pd.setD4264(d.getD4264());
				pd.setD6104(d.getD6104());
				pd.setD7953(d.getD7953());
				pd.setD3310(d.getD3310());
				pd.setD3320(d.getD3320());
				pd.setD3330(d.getD33300());
				pd.setD3346(d.getD3346());
				pd.setD3347(d.getD3347());
				pd.setD3348(d.getD3348());
				pd.setD6058(d.getD6058());
				pd.setD7951(d.getD7951());
				pd.setD4266(d.getD4266());
				pd.setD4267(d.getD4267());
				pd.setD4273(d.getD4273());
				pd.setD7251(d.getD7251());
				
				pd.setD7472(d.getD7472());
				pd.setD7472fr(d.getD7472fr());
				pd.setD7280(d.getD7280());
				pd.setD7280fr(d.getD7280fr());
				pd.setD7282(d.getD7282());
				pd.setD7282fr(d.getD7282fr());
				pd.setD7283(d.getD7283());
				pd.setD7283fr(d.getD7283fr());
				pd.setD7952(d.getD7952());
				pd.setD7952fr(d.getD7952fr());
				pd.setD7285(d.getD7285());
				pd.setD7285fr(d.getD7285fr());
				pd.setD6114(d.getD6114());
				pd.setD6114fr(d.getD6114fr());
				pd.setD5860(d.getD5860());
				pd.setD5860fr(d.getD5860fr());
				pd.setD5110(d.getD5110());
				pd.setD5110fr(d.getD5110fr());
				pd.setD5130(d.getD5130());
				pd.setD5130fr(d.getD5130fr());
				pd.setD0140(d.getD0140());
				pd.setIvSedation(d.getIvSedation());
	
				pd.setsRemarks(d.getsRemarks());
				pd.setmPolicy(d.getmPolicy());
				pd.setmMIP(d.getmMIP());
				pd.setEsBcbs(d.getEsBcbs());
				pd.setObtainMPN(d.getObtainMPN());
				pd.setWaitingPeriodDuration(d.getWaitingPeriodDuration());
				pd.setD0350(d.getD0350());
				pd.setD1330(d.getD1330());
				pd.setD2930(d.getD2930());
				pd.setSrpd4341(d.getSrpd4341());
				pd.setMajord72101(d.getMajord72101());
                pd.setFmxSubjectToDed(d.getFmxSubjectToDed());
				pd.setD7250(d.getD7250());
				pd.setD1510(d.getD1510());
				pd.setD1510Freq(d.getD1510Freq());
				pd.setD1516(d.getD1516());
				pd.setD1516Freq(d.getD1516Freq());
				pd.setD1517(d.getD1517());
				pd.setD1517Freq(d.getD1517Freq());
				pd.setD3220(d.getD3220());
				pd.setD3220Freq(d.getD3220Freq());
				pd.setOutNetworkMessage(d.getOutNetworkMessage());
				pd.setOsPlanType(d.getOsPlanType());
				pd.setSmAgeLimit(d.getSmAgeLimit());
				pd.setPerioD4921(d.getPerioD4921());
				pd.setD4921Frequency(d.getD4921Frequency());
				pd.setPerioD4266(d.getPerioD4266());
				pd.setD4266Frequency(d.getD4266Frequency());
				pd.setPerioD9910(d.getPerioD9910());
				pd.setD9910Frequency(d.getD9910Frequency());
				
				pd.setOonbenfits(d.getOonbenfits());
				pd.setD9630(d.getD9630());
				pd.setD9630fr(d.getD9630fr());
				pd.setD0431(d.getD0431());
				pd.setD0431fr(d.getD0431fr());
				pd.setD4999(d.getD4999());
				pd.setD4999fr(d.getD4999fr());
				pd.setD2962(d.getD2962());
				pd.setD2962fr(d.getD2962fr());
				
				pd.setD0145(d.getD0145());
				pd.setD0150(d.getD0150());
				pd.setD2750(d.getD2750());
				pd.setD2750fr(d.getD2750fr());
				pd.setD0220(d.getD0220());
				pd.setD0220Freq(d.getD0220Freq());
				pd.setD0230(d.getD0230());
				pd.setBwx(d.getBwx());
				pd.setD0210(d.getD0210());
				pd.setD0210Freq(d.getD0210Freq());
				pd.setD0350Freq(d.getD0350Freq());
				pd.setBwxFreq(d.getBwxFreq());
				pd.setD2931(d.getD2931());
				pd.setD1206(d.getD1206());
				pd.setD1208(d.getD1208());
				pd.setbWhichCode(d.getbWhichCode());
				pd.setD5110_20(d.getD5110_20());
				pd.setD1330Freq(d.getD1330Freq());
				pd.setD5111_12_13_14(d.getD5111_12_13_14());
				pd.setD5130_40(d.getD5130_40());
				pd.setD5810_c(d.getD5810_c());
				pd.setD5225_26_c(d.getD5225_26_c());
				pd.setExtractions1fr(d.getExtractions1fr());
				pd.setExtractions2fr(d.getExtractions2fr());
				pd.setImplantsC(d.getImplantsC());
				pd.setD1520_26_27(d.getD1520_26_27());
				pd.setD1520_26_27_fr(d.getD1520_26_27_fr());
				pd.setWaitingPeriod(d.getWaitingPeriod());
				pd.setWip(d.getWip());
				pd.setInsBillingC(d.getInsBillingC());
				pd.setBenefitPeriod(d.getBenefitPeriod());
				pd.setWaitingPeriodDrop(d.getWaitingPeriodDrop());
				pd.setD8070(d.getD8070());
				pd.setD8080(d.getD8080());
				pd.setD8090(d.getD8090());
				pd.setD8670(d.getD8670());
				pd.setD8680(d.getD8680());
				pd.setD8690(d.getD8690());
				pd.setD8070fr(d.getD8070fr());
				pd.setD8080fr(d.getD8080fr());
				pd.setD8090fr(d.getD8090fr());
				pd.setD8670fr(d.getD8670fr());
				pd.setD8680fr(d.getD8680fr());
				pd.setD8690fr(d.getD8690fr());
				pd.setApptype(d.getApptype());
				pd.setSecProviderName(d.getSecProviderName());
				pd.setSecProvNetwork(d.getSecProvNetwork());
				pd.setYesNoAssignToffice(d.getYesNoAssignToffice());
				pd.setD1120(d.getD1120());
				pd.setD1110(d.getD1110());
				pd.setPolicy18(d.getD0120());
				pd.setD7953Extraction(d.getD7953Extraction());
				pd.setD8660(d.getD8660());
				pd.setD8660fr(d.getD8660fr());
				pd.setD8210(d.getD8210());
				pd.setD8210fr(d.getD8210fr());
				pd.setD8220(d.getD8220());
				pd.setD8220fr(d.getD8220fr());
				pd.setD8020(d.getD8020());
				pd.setD8020fr(d.getD8020fr());
				pd.setD8692(d.getD8692());
				pd.setD8692fr(d.getD8692fr());
				pd.setImplantsCPercentage(d.getImplantsCPercentage());
				pd.setDoesExamShareFreq(d.getDoesExamShareFreq());
				pd.setD511020Percentage(d.getD511020Percentage());
				pd.setD513040Percentage(d.getD513040Percentage());
				pd.setD5810CPercentage(d.getD5810CPercentage());

		if (off != null)
			pd.setUniqueID(off.getName() + "_"+d.getId());// -- will set latter;
		pd.setVarnishD1206AgeLimit(d.getFluroide4());
		pd.setVarnishD1206FL(d.getFluroide3());
		pd.setxRaysBundling(d.getXrays5());
		pd.setxRaysBWSFL(d.getXrays1());
		pd.setxRaysFMXFL(d.getXrays4());
		pd.setxRaysPAD0220FL(d.getXrays2());
		pd.setxRaysPAD0230FL(d.getXrays3());
		///////////////////////////
		pd.setComments(d.getComments());
        pd.setGeneralBenefitsVerifiedBy(d.getBenefits());
        
		// p.setDob(d.getBasicInfo6());
		/*
		 * String fname = d.getBasicInfo2(); if (fname != null) { String[] f =
		 * fname.split(" "); p.setFirstName(f[0]); if (f.length > 1) {
		 * p.setLastName(fname.replace(f[0] + " ", "")); } }
		 */
		// p.setPatientId(d.getBasicInfo21());
		// p.setSalutation("");
		Set<IVFTableSheet> pl = new HashSet<>();
		pl.add(pd);
		// p.setPatientDetails(pl);
		// p.setOffice(off);

		List<String> hl = d.getHistory();
		List<IVFHistorySheet> phl = new ArrayList<IVFHistorySheet>();
		IVFHistorySheet ph = null;
		int x = 0;
		if (hl!=null)
		for (String hh : hl) {
			String[] ab=hh.split(Constants.PATH_SEPERATOR_XML_IVF);
			for(String h:ab) {
				
			if (h.equals("BLANK"))h="";
			if (x == 0)
				ph = new IVFHistorySheet();
			x++;
			if (x == 1) {
				ph.setHistoryCode(h);
				
			} else if (x == 2) {
				ph.setHistoryTooth(h);
			} else if (x == 3) {
					ph.setHistorySurface(h);
			} else if (x == 4) {
				ph.setHistoryDOS(h);
				if (!h.equals("")) phl.add(ph);
			    x=0;
			}

		 }
		}
		pd.setiVFHistorySheetList(phl);
		//pd.setHistory(history);
		// history logic.d.

		return pd;
	}

	public static Map<String, List<Object>> copyValueToIVFSheet(List<CaplineIVFFormDto> civfD, Office off) {

		Map<String, List<Object>> map = null;
		for (CaplineIVFFormDto d : civfD) {

			IVFTableSheet s = copyValueToIVFSheet(d, off,true);
            String uni=s.getUniqueID().split("_")[1];
			if (map == null) map = new HashMap<>();
			if(map.get(uni)==null) {
				List<Object> sl= new ArrayList<>();
				sl.add(s);
				map.put(uni,sl);
			}else {
				List<Object> sl =map.get(uni);
				sl.add(s);
			}

		}
		return map;
	}

	public static String getIVFDateFromatofDate(Date date) {

		return Constants.SIMPLE_DATE_FORMAT_IVF.format(date);

	}
	public static String getIVFDateFromatofDate(String date) {

		return Constants.SIMPLE_DATE_FORMAT_IVF.format(date);

	}
	
	public static void main(String []a) {
		//qwe======!!!!======11======!!!!======11======!!!!======2019-10-08
		//List<IVFHistorySheet> phl = new ArrayList<IVFHistorySheet>();
		IVFHistorySheet ph = null;
		int x = 0;
		List<String> hl = new ArrayList<>();
		hl.add("qwe======!!!!======11======!!!!======11======!!!!======2019-10-08");
		hl.add("SSSSSSS======!!!!======11======!!!!======11======!!!!======2019-10-08");
		if (hl!=null)
		for (String hh : hl) {
			String[] ab=hh.split(Constants.PATH_SEPERATOR_XML_IVF);
			for(String h:ab) {
				
			if (h.equals("BLANK"))h="";
			if (x == 0)
				ph = new IVFHistorySheet();
			x++;
			if (x == 1) {
				ph.setHistoryCode(h);
				
			} else if (x == 2) {
				ph.setHistoryTooth(h);
			} else if (x == 3) {
					ph.setHistorySurface(h);
			} else if (x == 4) {
				ph.setHistoryDOS(h);
			    x=0;
			}

		 }
		}
		
	}
	
	//data Dump only for IVF GENERAL FORM... for  now
	public static Patient copyValueToPatient(IVFTableSheet d, Office off,IVFormType iVFormType) {

		Patient p = new Patient();
		PatientDetail pd = new PatientDetail();
		PatientDetail2 pd2= new PatientDetail2();
        pd.setiVFormType(iVFormType);
         //Could have used Beanutil but avoided for safer side;
		// Office off =od.getOfficeByName(d.getBasicInfo1());
		// off.getName();
		pd.setAlveoD7310CoveredWithEXT(d.getAlveoD7310CoveredWithEXT());
		pd.setAlveoD7310FL(d.getAlveoD7310FL());
		pd.setAlveoD7311CoveredWithEXT(d.getAlveoD7311CoveredWithEXT());
		pd.setAlveoD7311FL(d.getAlveoD7311FL());

		pd.setAptDate(d.getAptDate());
		pd.setBasicPercentage(d.getBasicPercentage());
		pd.setBasicSubjectDeductible(d.getBasicSubjectDeductible());
		pd.setBasicWaitingPeriod(d.getBasicWaitingPeriod());
		pd.setBoneGraftsD7953CoveredWithEXT(d.getBoneGraftsD7953CoveredWithEXT());
		pd.setBoneGraftsD7953FL(d.getBoneGraftsD7953FL());
		pd.setBuildUpsD2950Covered(d.getBuildUpsD2950Covered());
		pd.setBuildUpsD2950FL(d.getBuildUpsD2950FL());
		pd.setBuildUpsD2950SameDayCrown(d.getBuildUpsD2950SameDayCrown());
		pd.setClaimFillingLimit(d.getClaimFillingLimit());
		pd.setcOBStatus(d.getcOBStatus());
		pd.setComments(d.getComments());
		pd.setCompleteDenturesD5110D5120FL(d.getCompleteDenturesD5110D5120FL());
		pd.setContTxRecallNP(d.getContTxRecallNP());
		pd.setD0120(d.getD0120());
		pd.setD2391(d.getD2391());
		
		// policy18//policy19/policy20 -- need to verify
		pd.setCraRequired(d.getCraRequired());
		pd.setCrownLengthD4249FL(d.getCrownLengthD4249FL());
		pd.setCrownLengthD4249Percentage(d.getCrownLengthD4249Percentage());
		pd.setCrownsD2750D2740Downgrade(d.getCrownsD2750D2740Downgrade());
		pd.setCrownsD2750D2740FL(d.getCrownsD2750D2740FL());
		pd.setCrownsD2750D2740PaysPrepSeatDate(d.getCrownsD2750D2740PaysPrepSeatDate());
		pd.setCrownsD2750D2740Percentage(d.getCrownsD2750D2740Percentage());

		pd.setcSRName(d.getcSRName());

		pd.setD9310FL(d.getD9310FL());
		pd.setD9310Percentage(d.getD9310Percentage());
		pd.setDiagnosticPercentage(d.getDiagnosticPercentage());
		pd.seteExamsD0145FL(d.geteExamsD0145FL());
		pd.setEmployerName(d.getEmployerName());
		pd.setEndodonticsPercentage(d.getEndodonticsPercentage());
		pd.setEndoSubjectDeductible(d.getEndoSubjectDeductible());
		pd.setExamsD0120FL(d.getExamsD0120FL());
		pd.setExamsD0140FL(d.getExamsD0140FL());
		pd.setExamsD0150FL(d.getExamsD0150FL());
		pd.setExtractionsMajorPercentage(d.getExtractionsMajorPercentage());
		pd.setExtractionsMinorPercentage(d.getExtractionsMinorPercentage());
		pd.setFillingsBundling(d.getFillingsBundling());
		pd.setFlourideAgeLimit(d.getFlourideAgeLimit());
		pd.setFlourideD1208FL(d.getFlourideD1208FL());
		pd.setfMDD4355FL(d.getfMDD4355FL());
		pd.setFMDD4355Percentage(d.getFMDD4355Percentage());
		pd.setGeneralBenefitsVerifiedBy(d.getGeneralBenefitsVerifiedBy());
		/*
		if (d.getGeneralDateIVwasDone().equals(""))
			pd.setGeneralDateIVwasDone(getIVFDateFromatofDate(date));
		else
		*/
		pd.setGeneralDateIVwasDone(d.getGeneralDateIVwasDone());//// need to vertify

		pd.setGingivitisD4346FL(d.getGingivitisD4346FL());
		pd.setGingivitisD4346Percentage(d.getGingivitisD4346Percentage());
		pd.setGroup(d.getGroup());
		pd.setImmediateDenturesD5130D5140FL(d.getImmediateDenturesD5130D5140FL());
		pd.setImplantCoverageD6010Percentage(d.getImplantCoverageD6010Percentage());
		pd.setImplantCoverageD6057Percentage(d.getImplantCoverageD6057Percentage());
		pd.setImplantCoverageD6190Percentage(d.getImplantCoverageD6190Percentage());
		pd.setImplantSupportedPorcCeramicD6065Percentage(d.getImplantSupportedPorcCeramicD6065Percentage());
		pd.setInsAddress(d.getInsAddress());
		pd.setInsContact(d.getInsContact());
		pd.setInsName(d.getInsName());

		pd.setInterimPartialDenturesD5214FL(d.getInterimPartialDenturesD5214FL());// Cross check

		pd.setiVSedationD9243Percentage(d.getiVSedationD9243Percentage());
		pd.setiVSedationD9248Percentage(d.getiVSedationD9248Percentage());
		pd.setMajorPercentage(d.getMajorPercentage());
		pd.setMajorSubjectDeductible(d.getMajorSubjectDeductible());
		pd.setMajorWaitingPeriod(d.getMajorWaitingPeriod());
		pd.setMemberId(d.getMemberId());
		pd.setMemberSSN(d.getMemberSSN());
		pd.setMissingToothClause(d.getMissingToothClause());
		pd.setName1201110RollOverAgYe(d.getName1201110RollOverAgYe());
		pd.setNightGuardsD9940FL(d.getNightGuardsD9940FL());
		pd.setNightGuardsD9940Percentage(d.getNightGuardsD9940Percentage());
		pd.setNitrousD9230Percentage(d.getNitrousD9230Percentage());
		pd.setOffice(off);
		pd.setOrthoAgeLimit(d.getOrthoAgeLimit());
		pd.setOrthoMax(d.getOrthoMax());
		pd.setOrthoPercentage(d.getOrthoPercentage());
		pd.setOrthoSubjectDeductible(d.getOrthoSubjectDeductible());
		pd.setPartialDenturesD5213D5214FL(d.getPartialDenturesD5213D5214FL());
		pd.setPatient(p);
		pd.setpAXRaysPercentage(d.getpAXRaysPercentage());
		pd.setPayerId(d.getPayerId());
		pd.setPerioMaintenanceD4910AltWProphyD0110(d.getPerioMaintenanceD4910AltWProphyD0110());
		pd.setPerioMaintenanceD4910FL(d.getPerioMaintenanceD4910FL());
		pd.setPerioMaintenanceD4910Percentage(d.getPerioMaintenanceD4910Percentage());
		pd.setPerioSurgeryPercentage(d.getPerioSurgeryPercentage());
		pd.setPerioSurgerySubjectDeductible(d.getPerioSurgerySubjectDeductible());
		pd.setPlanAnnualMax(d.getPlanAnnualMax());
		pd.setPlanAnnualMaxRemaining(d.getPlanAnnualMaxRemaining());
		pd.setPlanAssignmentofBenefits(d.getPlanAssignmentofBenefits());
		pd.setPlanCalendarFiscalYear(d.getPlanCalendarFiscalYear());
		pd.setPlanCoverageBook(d.getPlanCoverageBook());
		pd.setPlanDependentsCoveredtoAge(d.getPlanDependentsCoveredtoAge());
		pd.setPlanEffectiveDate(d.getPlanEffectiveDate());
		pd.setPlanFeeScheduleName(d.getPlanFeeScheduleName());
		pd.setPlanFullTimeStudentStatus(d.getPlanFullTimeStudentStatus());
		pd.setPlanIndividualDeductible(d.getPlanIndividualDeductible());
		pd.setPlanIndividualDeductibleRemaining(d.getPlanIndividualDeductibleRemaining());
		pd.setPlanNetwork(d.getPlanNetwork());
		pd.setPlanNonDuplicateClause(d.getPlanNonDuplicateClause());
		pd.setPlanPreDMandatory(d.getPlanPreDMandatory());
		pd.setPlanTermedDate(d.getPlanTermedDate());
		pd.setPlanType(d.getPlanType());
		pd.setPolicyHolder(d.getPolicyHolder());
		pd.setPolicyHolderDOB(d.getPolicyHolderDOB());
		pd.setPostCompositesD2391FL(d.getPostCompositesD2391FL());
		pd.setPostCompositesD2391Percentage(d.getPostCompositesD2391Percentage());
		pd.setPosteriorCompositesD2391Downgrade(d.getPosteriorCompositesD2391Downgrade());
		pd.setPreventivePercentage(d.getPreventivePercentage());
		pd.setProphyD1110FL(d.getProphyD1110FL());
		pd.setProphyD1120FL(d.getProphyD1120FL());
		pd.setProviderName(d.getProviderName());
		pd.setRef(d.getRef());
		pd.setReplacementClause(d.getReplacementClause());

		pd.setSealantsD1351AgeLimit(d.getSealantsD1351AgeLimit());
		pd.setSealantsD1351FL(d.getSealantsD1351FL());
		pd.setSealantsD1351Percentage(d.getSealantsD1351Percentage());
		pd.setSealantsD1351PermanentMolarsCovered(d.getSealantsD1351PermanentMolarsCovered());
		pd.setSealantsD1351PrimaryMolarsCovered(d.getSealantsD1351PrimaryMolarsCovered());
		pd.setSealantsD1351PreMolarsCovered(d.getSealantsD1351PreMolarsCovered());

		pd.setsRPD4341DaysBwTreatment(d.getsRPD4341DaysBwTreatment());
		pd.setsRPD4341FL(d.getsRPD4341FL());
		pd.setsRPD4341Percentage(d.getsRPD4341Percentage());
		pd.setsRPD4341QuadsPerDay(d.getsRPD4341QuadsPerDay());
		pd.setsSCD2930FL(d.getsSCD2930FL());
		pd.setsSCD2931FL(d.getsSCD2931FL());
		pd.setTaxId(d.getTaxId());
		
		//new in IVF html form
		pd.setPreventiveSubDed(d.getPreventiveSubDed());//percentages13
		pd.setDiagnosticSubDed(d.getDiagnosticSubDed());//percentages14
		pd.setpAXRaysSubDed(d.getpAXRaysSubDed());//percentages15
		
		pd.setDen5225Per(d.getDen5225Per());// den5225
		pd.setDenf5225FR(d.getDenf5225FR());//denf5225 
		pd.setDen5226Per(d.getDen5226Per());//den5226 
		pd.setDenf5226Fr(d.getDenf5226Fr());//denf5226 
		pd.setBridges1(d.getBridges1());//bridges1 
		pd.setBridges2(d.getBridges2());//bridges2 
		pd.setWillDowngradeApplicable(d.getWillDowngradeApplicable());//cdowngrade
		pd.setImplantsFrD6010(d.getImplantsFrD6010());//implants5
		pd.setImplantsFrD6057(d.getImplantsFrD6057());//implants6
		pd.setImplantsFrD6065(d.getImplantsFrD6065());//implants7
		pd.setImplantsFrD6190(d.getImplantsFrD6190());;//implants8
		pd.setOrthoRemaining(d.getOrthoRemaining());//ortho5
		pd.setOrthoWaitingPeriod(d.getOrthoWaitingPeriod());//??waitingPeriod3
		//pd.setWillCrowngrade(d.getWillCrowngrade());//posterior16
		pd.setCrowngradeCode(d.getCrowngradeCode());//posterior17
		pd.setNightGuardsD9945Percentage(d.getNightGuardsD9945Percentage());//posterior18
		pd.setFmxPer(d.getFmxPer());//percentages16 
		
		pd.setNightGuardsD9944Fr(d.getNightGuardsD9944Fr());
		pd.setNightGuardsD9945Fr(d.getNightGuardsD9945Fr());
		//pd.setInsuranceType(d.getInsuranceType()); //Insurance Type
		pd.setFillingsInYear(d.getFillingsInYear());//fill1
		pd.setExtractionsInYear(d.getExtractionsInYear());//extr1
		pd.setCrownsInYear(d.getCrownsInYear());//crn1
		pd.setWaitingPeriod4(d.getWaitingPeriod4());//waitingPeriod4
		pd.setShareFr(d.getShareFr());//shareFr
		pd.setPedo1(d.getPedo1());//pedo1
		pd.setPedo2(d.getPedo2());//pedo2
		pd.setPano1(d.getPano1());//pano1
		pd.setPano2(d.getPano2());//pano2
		pd.setD4381(d.getD4381());//d4381
		
		//END		
		pd.setCkD0120(d.getCkD0120());//ckD0120
		pd.setCkD0140(d.getCkD0140());//ckD0140
		pd.setCkD0145(d.getCkD0145());//ckD0145
		pd.setCkD0150(d.getCkD0150());//ckD0150
		pd.setCkD0160(d.getCkD0160());//ckD0160
		pd.setCkD210(d.getCkD210());//ckD210
		pd.setCkD220(d.getCkD220());//ckD220
		pd.setCkD230(d.getCkD230());//ckD230
		pd.setCkD330(d.getCkD330());//ckD330
		pd.setCkD274(d.getCkD274());//ckD274
		pd.setD0160Freq(d.getD0160Freq());//d0160Freq
		pd.setD2391Freq(d.getD2391Freq());//d2391Freq
		pd.setD0330Freq(d.getD0330Freq());//d0330Freq
		pd.setD4381Freq(d.getD4381Freq());//d4381Freq
		pd.setD3330(d.getD3330());//d3330
		pd.setD3330Freq(d.getD3330Freq());//d3330Freq
		pd.setFreqD2934(d.getFreqD2934());
		pd.setHistoryCount(d.getHistoryCount());
		
		pd2.setNpi(d.getNpi());
		pd2.setLicence(d.getLicence());
		pd2.setRadio3(d.getRadio3());
		pd2.setRadio4(d.getRadio4());
		pd2.setRadio5(d.getRadio5());
		pd2.setRadio1(d.getRadio1());
		pd2.setRadio2(d.getRadio2());
		pd2.setCorrdOfBenefits(d.getCorrdOfBenefits());
		pd2.setWhatAmountD7210(d.getWhatAmountD7210());
		pd2.setAllowAmountD7240(d.getAllowAmountD7240());
		pd2.setD7210(d.getD7210());
		pd2.setD7220(d.getD7220());
		pd2.setD7230(d.getD7230());
		pd2.setD7240(d.getD7240());
		pd2.setD7250(d.getD7250());
		pd2.setD7310(d.getD7310());
		pd2.setD7311(d.getD7311());
		pd2.setD7320(d.getD7320());
		pd2.setD7321(d.getD7321());
		pd2.setD7473(d.getD7473());

		pd2.setD9239(d.getD9239());
		pd2.setD4263(d.getD4263());
		pd2.setD4264(d.getD4264());
		pd2.setD6104(d.getD6104());
		pd2.setD7953(d.getD7953());
		pd2.setD3310(d.getD3310());
		pd2.setD3320(d.getD3320());
		pd2.setD3330(d.getD33300());
		pd2.setD3346(d.getD3346());
		pd2.setD3347(d.getD3347());
		pd2.setD3348(d.getD3348());
		pd2.setD6058(d.getD6058());
		pd2.setD7951(d.getD7951());
		pd2.setD4266(d.getD4266());
		pd2.setD4267(d.getD4267());
		pd2.setD4273(d.getD4273());
		pd2.setD7251(d.getD7251());
		
		pd2.setD7210fr(d.getD7210fr());
		pd2.setD7220fr(d.getD7220fr());
		pd2.setD7230fr(d.getD7230fr());
		pd2.setD7240fr(d.getD7240fr());
		pd2.setD7250fr(d.getD7250fr());
		pd2.setD7310fr(d.getD7310fr());
		pd2.setD7311fr(d.getD7311fr());
		pd2.setD7320fr(d.getD7320fr());
		pd2.setD7321fr(d.getD7321fr());
		pd2.setD7473fr(d.getD7473fr());
		pd2.setSedations1fr(d.getSedations1fr());
		pd2.setSedations3fr(d.getSedations3fr());
		pd2.setD9239fr(d.getD9239fr());
		pd2.setSedations2fr(d.getSedations2fr());
		pd2.setD4263fr(d.getD4263fr());
		pd2.setD4264fr(d.getD4264fr());
		pd2.setD6104fr(d.getD6104fr());
		pd2.setD7953fr(d.getD7953fr());
		pd2.setD3310fr(d.getD3310fr());
		pd2.setD3320fr(d.getD3320fr());
		pd2.setD3346fr(d.getD3346fr());
		pd2.setD3347fr(d.getD3347fr());
		pd2.setD3348fr(d.getD3348fr());
		pd2.setD6058fr(d.getD6058fr());
		pd2.setOral1fr(d.getOral1fr());
		pd2.setD7951fr(d.getD7951fr());
		pd2.setD4266fr(d.getD4266fr());
		pd2.setD4267fr(d.getD4267fr());
		pd2.setPerio1fr(d.getPerio1fr());
		pd2.setD4273fr(d.getD4273fr());
		pd2.setD7251fr(d.getD7251fr());
		
		pd2.setD7472(d.getD7472());
		pd2.setD7472fr(d.getD7472fr());
		pd2.setD7280(d.getD7280());
		pd2.setD7280fr(d.getD7280fr());
		pd2.setD7282(d.getD7282());
		pd2.setD7282fr(d.getD7282fr());
		pd2.setD7283(d.getD7283());
		pd2.setD7283fr(d.getD7283fr());
		pd2.setD7952(d.getD7952());
		pd2.setD7952fr(d.getD7952fr());
		pd2.setD7285(d.getD7285());
		pd2.setD7285fr(d.getD7285fr());
		pd2.setD6114(d.getD6114());
		pd2.setD6114fr(d.getD6114fr());
		pd2.setD5860(d.getD5860());
		pd2.setD5860fr(d.getD5860fr());
		pd2.setD5110(d.getD5110());
		pd2.setD5110fr(d.getD5110fr());
		pd2.setD5130(d.getD5130());
		pd2.setD5130fr(d.getD5130fr());
		pd2.setD0140(d.getD0140());
		pd2.setIvSedation(d.getIvSedation());
		pd2.setsRemarks(d.getsRemarks());
		pd2.setmPolicy(d.getmPolicy());
		pd2.setmMIP(d.getmMIP());
		pd2.setEsBcbs(d.getEsBcbs());
		pd2.setObtainMPN(d.getObtainMPN());
		pd2.setWaitingPeriodDuration(d.getWaitingPeriodDuration());
		pd2.setD0350(d.getD0350());
		pd2.setD1330(d.getD1330());
		pd2.setD2930(d.getD2930());
		pd2.setSrpd4341(d.getSrpd4341());
		pd2.setMajord72101(d.getMajord72101());
		
		pd2.setFmxSubjectToDed(d.getFmxSubjectToDed());
		pd2.setD1510(d.getD1510());
		pd2.setD1510Freq(d.getD1510Freq());
		pd2.setD1516(d.getD1516());
		pd2.setD1516Freq(d.getD1516Freq());
		pd2.setD1517(d.getD1517());
		pd2.setD1517Freq(d.getD1517Freq());
		pd2.setD3220(d.getD3220());
		pd2.setD3220Freq(d.getD3220Freq());
		pd2.setOutNetworkMessage(d.getOutNetworkMessage());
		pd2.setOsPlanType(d.getOsPlanType());
		pd2.setSmAgeLimit(d.getSmAgeLimit());
		pd2.setPerioD4921(d.getPerioD4921());
		pd2.setD4921Frequency(d.getD4921Frequency());
		pd2.setPerioD4266(d.getPerioD4266());
		pd2.setD4266Frequency(d.getD4266Frequency());
		pd2.setPerioD9910(d.getPerioD9910());
		pd2.setD9910Frequency(d.getD9910Frequency());
		
		pd2.setOonbenfits(d.getOonbenfits());
		pd2.setD9630(d.getD9630());
		pd2.setD9630fr(d.getD9630fr());
		pd2.setD0431(d.getD0431());
		pd2.setD0431fr(d.getD0431fr());
		pd2.setD4999(d.getD4999());
		pd2.setD4999fr(d.getD4999fr());
		pd2.setD2962(d.getD2962());
		pd2.setD2962fr(d.getD2962fr());
		
		pd2.setD0145(d.getD0145());
		pd2.setD0150(d.getD0150());
		pd2.setD2750(d.getD2750());
		pd2.setD2750fr(d.getD2750fr());
		pd2.setD0220(d.getD0220());
		pd2.setD0220Freq(d.getD0220Freq());
		pd2.setD0230(d.getD0230());
		pd2.setBwx(d.getBwx());
		pd2.setD0210(d.getD0210());
		pd2.setD0210Freq(d.getD0210Freq());
		pd2.setD0350Freq(d.getD0350Freq());
		pd2.setBwxFreq(d.getBwxFreq());
		pd2.setD2931(d.getD2931());
		pd2.setD1206(d.getD1206());
		pd2.setD1208(d.getD1208());
		pd2.setbWhichCode(d.getbWhichCode());
		pd2.setD5110_20(d.getD5110_20());
		pd2.setD1330Freq(d.getD1330Freq());
		pd2.setD5111_12_13_14(d.getD5111_12_13_14());
		pd2.setD5130_40(d.getD5130_40());
		pd2.setD5810_c(d.getD5810_c());
		pd2.setD5225_26_c(d.getD5225_26_c());
		pd2.setExtractions1fr(d.getExtractions1fr());
		pd2.setExtractions2fr(d.getExtractions2fr());
		pd2.setImplantsC(d.getImplantsC());
		pd2.setD1520_26_27(d.getD1520_26_27());
		pd2.setD1520_26_27_fr(d.getD1520_26_27_fr());
		pd2.setWaitingPeriod(d.getWaitingPeriod());
		pd2.setWip(d.getWip());
		pd2.setInsBillingC(d.getInsBillingC());
		pd2.setBenefitPeriod(d.getBenefitPeriod());
		pd2.setWaitingPeriodDrop(d.getWaitingPeriodDrop());
		pd2.setD8070(d.getD8070());
		pd2.setD8080(d.getD8080());
		pd2.setD8090(d.getD8090());
		pd2.setD8670(d.getD8670());
		pd2.setD8680(d.getD8680());
		pd2.setD8690(d.getD8690());
		pd2.setD8070fr(d.getD8070fr());
		pd2.setD8080fr(d.getD8080fr());
		pd2.setD8090fr(d.getD8090fr());
		pd2.setD8670fr(d.getD8670fr());
		pd2.setD8680fr(d.getD8680fr());
		pd2.setD8690fr(d.getD8690fr());
		pd2.setApptype(d.getApptype());
		pd2.setSecProviderName(d.getSecProviderName());		
		pd2.setSecProvNetwork(d.getSecProvNetwork());
		pd2.setYesNoAssignToffice(d.getYesNoAssignToffice());
		pd2.setD1120(d.getD1120());
		pd2.setD1110(d.getD1110());
		pd2.setPolicy18(d.getD0120());
		pd2.setD7953Extraction(d.getD7953Extraction());
		pd2.setD8660(d.getD8660());
		pd2.setD8660fr(d.getD8660fr());
		pd2.setD8210(d.getD8210());
		pd2.setD8210fr(d.getD8210fr());
		pd2.setD8220(d.getD8220());
		pd2.setD8220fr(d.getD8220fr());
		pd2.setD8020(d.getD8020());
		pd2.setD8020fr(d.getD8020fr());
		pd2.setD8692(d.getD8692());
		pd2.setD8692fr(d.getD8692fr());
		pd2.setImplantsCPercentage(d.getImplantsCPercentage());
		pd2.setDoesExamShareFreq(d.getDoesExamShareFreq());
		pd2.setD511020Percentage(d.getD511020Percentage());
		pd2.setD513040Percentage(d.getD513040Percentage());
		pd2.setD5810CPercentage(d.getD5810CPercentage());
		
		pd.setPatientDetails2(pd2);
		Set<PatientDetail2> p2Set = new HashSet<>();
		p2Set.add(pd2);
		
		p.setPatientDetails2(p2Set);

		if (off != null)
			pd.setUniqueID(off.getName() + "_");// -- will set latter;
		pd.setVarnishD1206AgeLimit(d.getVarnishD1206AgeLimit());
		pd.setVarnishD1206FL(d.getVarnishD1206FL());
		pd.setxRaysBundling(d.getxRaysBundling());
		pd.setxRaysBWSFL(d.getxRaysBWSFL());
		pd.setxRaysFMXFL(d.getxRaysFMXFL());
		pd.setxRaysPAD0220FL(d.getxRaysPAD0220FL());
		pd.setxRaysPAD0230FL(d.getxRaysPAD0230FL());
		///////////////////////////
		pd.setComments(d.getComments());
        pd.setGeneralBenefitsVerifiedBy(d.getGeneralBenefitsVerifiedBy());
        
		p.setDob(d.getPatientDOB());
		String fname = d.getPatientName();
		if (fname != null) {
			String[] f = fname.split(" ");
			p.setFirstName(f[0]);
			if (f.length > 1) {
				p.setLastName(fname.replace(f[0] + " ", ""));
			}
		}
		p.setPatientId(d.getPatientId());
		p.setSalutation("");
		Set<PatientDetail> pl = new HashSet<>();
		pl.add(pd);
		p.setPatientDetails(pl);
		p.setOffice(off);

		IVFHistorySheet his = d.getHs();
		Set<PatientHistory> phl = new HashSet<PatientHistory>();
		PatientHistory ph = null;
		
		//int x = 0;
		if (his != null) {
			try {
			Class<?> c2;
			c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");//--DONE
			int noOFhistory = Constants.history_codes_size;
			 	 for (int i = 1; i <= noOFhistory; i++) {
					 String hc = "getHistory" + i + "Code";
						String hd = "getHistory" + i + "DOS";
						String ht = "getHistory" + i + "Tooth";
						String hs = "getHistory" + i + "Surface";
						Method hcm = c2.getMethod(hc);
						Method htm = c2.getMethod(ht);
						Method hdm = c2.getMethod(hd);
						Method hss = c2.getMethod(hs);	
						//String code = (String) hcm.invoke(his);
						ph = new PatientHistory();
						ph.setHistoryCode((String) hcm.invoke(his));
						ph.setHistoryDOS((String) hdm.invoke(his));
						ph.setHistorySurface((String) hss.invoke(his));
						ph.setHistoryTooth((String) htm.invoke(his));
						if (!(ph.getHistoryCode().equals("") && ph.getHistoryDOS().equals("")
							&& ph.getHistorySurface().equals("") && ph.getHistoryTooth().equals("")	))
						phl.add(ph);
					}
			 	 
			 	for (IVFHistorySheet hisShee: d.getiVFHistorySheetList()) {
					String hc = "getHistoryCode";
					String hd = "getHistoryDOS";
					String ht = "getHistoryTooth";
					String hs = "getHistorySurface";
					Method hcm = c2.getMethod(hc);
					Method htm = c2.getMethod(ht);
					Method hdm = c2.getMethod(hd);
					Method hss = c2.getMethod(hs);	
					String code = (String) hcm.invoke(hisShee);
					String dt = (String) hdm.invoke(hisShee);
					
					if (code.equals("")) continue ;
					if (dt.equals("")) continue ;
					ph = new PatientHistory();
					ph.setHistoryCode((String) hcm.invoke(hisShee));
					ph.setHistoryDOS((String) hdm.invoke(hisShee));
					ph.setHistorySurface((String) hss.invoke(hisShee));
					ph.setHistoryTooth((String) htm.invoke(hisShee));
					if (!(ph.getHistoryCode().equals("") && ph.getHistoryDOS().equals("")
						&& ph.getHistorySurface().equals("") && ph.getHistoryTooth().equals("")	))
					phl.add(ph);

				} 	 
			 
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		}
		p.setPatientHistory(phl);
		// history logic.d.

		return p;
	}
	
	
	    //data Dump only for IVF ORTHO FORM... for  now
		public static Patient copyValueToPatient(OrthoGoogleSheetDto d,IVFormType iVFormType) {

			Patient p = new Patient();
			PatientDetail pd = new PatientDetail();
			PatientDetail2 pd2= new PatientDetail2();
	        pd.setiVFormType(iVFormType);
	         //Could have used Beanutil but avoided for safer side;
			p.setPatientId(d.getEsid());
			pd.setOrthoAgeLimit(d.getAgeLimitForOrtho());
			pd.setAptDate(d.getAppointmentDate());
			pd2.setBenefitPeriod(d.getCalender());//need to see
			pd.setGeneralDateIVwasDone(d.getCompletionDate());
			d.getCoordinationBenefits();//Question
			
			d.getCsrName();//Question
			pd2.setD8070(d.getD8070());
			pd2.setD8080(d.getD8080());
			pd2.setD8670(d.getD8670());
			pd2.setD8090(d.getD8090());
			pd2.setD8690(d.getD8690());
			pd.setPlanIndividualDeductible(d.getDeductibleForOrtho());
			pd.setPlanDependentsCoveredtoAge(d.getDependentCoveredUpToAge());
			pd.setPlanEffectiveDate(d.getEffectiveDate());
			pd.setEmployerName(d.getEmployerName());
			pd.setGroup(d.getGroupNo());
			pd.setInsAddress(d.getInsuranceAddress());
			pd2.setInsBillingC(d.getInsuranceBillingCycle());
			pd.setInsContact(d.getInsuranceContactNo());
			pd.setInsName(d.getInsuranceName());
			pd.setiVFormType(iVFormType);
			//d.getiVType();
			pd.setGeneralBenefitsVerifiedBy(d.getIVUpdatedBy());
			pd.setMemberId(d.getMemberID());
			pd.setMemberSSN(d.getSsn());
			pd.setPlanNetwork(d.getNetwork());
			pd.setOffice(d.getOfficeDb());
			d.getOrthoCoverragePer();//Question
			pd.setOrthoMax(d.getOrthoMax());
			pd.setOrthoRemaining(d.getOrthoMaxRemaining());
			p.setDob(d.getPatientDOB());
			pd.setPayerId(d.getPayerId());
			pd.setPolicyHolderDOB(d.getPolicyHolderDOB());
			pd.setPolicyHolder(d.getPolicyHolderName());
			pd.setPlanTermedDate(d.getPolicyTermDate());
			pd.setProviderName(d.getProviderName());
			pd.setRef(d.getReferenceNo());
			pd.setComments(d.getRemarks());
			
			d.getStatus();//Question
			pd.setClaimFillingLimit(d.getTimelyFilingLimit());
			//d.getUniqId();
			pd2.setWaitingPeriod(d.getWaitingPeriodForOrtho());
	 	    pd2.setWip(d.getWorkInProgress());
			pd.setPatientDetails2(pd2);
			Set<PatientDetail2> p2Set = new HashSet<>();
			p2Set.add(pd2);
			
			p.setPatientDetails2(p2Set);

			pd.setUniqueID(d.getOffice() + "_");// -- will set latter;
			
			String fname = d.getPatientName();
			if (fname != null) {
				String[] f = fname.split(" ");
				p.setFirstName(f[0]);
				if (f.length > 1) {
					p.setLastName(fname.replace(f[0] + " ", ""));
				}
			}
			p.setPatientId(d.getEsid());
			p.setSalutation("");
			Set<PatientDetail> pl = new HashSet<>();
			pl.add(pd);
			p.setPatientDetails(pl);
			p.setOffice(d.getOfficeDb());

			return p;
		}


}
