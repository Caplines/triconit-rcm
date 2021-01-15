package com.tricon.ruleengine.service.impl;

import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.IVFDumpDto;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.sheet.IVFHistorySheet;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.IVFOldDataService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.DateUtils;
import com.tricon.ruleengine.utils.IVFFormConversionUtil;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;

@Service
public class IVFOldDataServiceImpl implements IVFOldDataService {

	@Autowired
	OfficeDao od;

	@Autowired
	Environment env;
	
	@Autowired
	CaplineIVFGoogleFormService caplineIVFGoogleFormService;
	
	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;
	
	@Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
	
	@Autowired
	UserDao userDao;

	@Override
	public String dumpOldData(IVFDumpDto dto,IVFormType iVFormType) {
        String p="";
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser juser = (JwtUser) userDetails;
		
		Office office = od.getOfficeByUuid(dto.getOfficeId(),juser.getCompany().getUuid());

				Map<String, List<Object>> ivfMap = null;
		try {
			//no concept of old sheet now
			//if (!dto.isNewColumns())
			//https://docs.google.com/spreadsheets/d/1POWJC8as3b3MvhN8EtLacUwLu3ABpI88JUECMQ2Ts10/edit#gid=898165103
			dto.setSheetId("1POWJC8as3b3MvhN8EtLacUwLu3ABpI88JUECMQ2Ts10");//do this hard code 
			String sheetSubid="";
			/*
			ivfMap = ConnectAndReadSheets.readSheetNewDump(dto.getSheetId(), "TEST", null,
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, office.getName(), false, true);
		    */
			ivfMap = ConnectAndReadSheets.readSheetNewDump(dto.getSheetId(), office.getName() + " " + dto.getSheetName(), null,
					CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, office.getName(), false, true);
					
			// ivfMap = ConnectAndReadSheets.readSheetNewDump(dto.getSheetId(), "TEST", null,
			//		CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, office.getName(), false, true);
			IVFTableSheet sh = null;
			User user = userDao.findUserByUsername(((UserDetails)principal).getUsername());
			if (ivfMap != null) {
				int ss=0;
				//Correct the Date formats
				for (Map.Entry<String, List<Object>> entry : ivfMap.entrySet()) {

					//String key = entry.getKey();
					List<Object> obL = entry.getValue();
					for (Object obj : obL) {
						sh = (IVFTableSheet) obj;
						if (ss==0)sheetSubid=sh.getSheetSubId();
						ss=1;
						sh.setPatientDOB(DateUtils.correctDateformat(sh.getPatientDOB()));
						sh.setPolicyHolderDOB(DateUtils.correctDateformat(sh.getPolicyHolderDOB()));
						sh.setPlanEffectiveDate(DateUtils.correctDateformat(sh.getPlanEffectiveDate()));
						sh.setGeneralDateIVwasDone(DateUtils.correctDateformat(sh.getGeneralDateIVwasDone()));
						sh.setAptDate(DateUtils.correctDateformat(sh.getAptDate()));
										//sh.getHs()
						//Default make if Primary
						//sh.setInsuranceType(Constants.INSURANCE_TPYE_IVF_PRIMARY);
						if (sh.getHs() != null) {
							updateHistortList(sh.getHs(), sh.getiVFHistorySheetList());
//null from tooth surface is needed...
						}
					//System.out.println("dfdddd--"+sh.getPatientId());
						//p=sh.getPatientId()+"-"+sh.getPlanAnnualMax();
						Object[] objR=caplineIVFGoogleFormService.saveAllData(IVFFormConversionUtil.copyValueToPatient(sh, office,iVFormType), office, new Date(), user,false,true,iVFormType);
						if (!((String)objR[1]).equals("Success")) {
						p=p+"PatId: "+sh.getPatientId()+" GeneralDate: "+sh.getGeneralDateIVwasDone()+"  Reason: - <div class='error'>"+(String)objR[1]+"</div><br>";
						sh.setStatusDump("This IV Already Exists in the RDBMS.");
						}else {
							sh.setStatusDump("DONE");
						}
						
					}
					
				}
				ConnectAndReadSheets.updateDumpSheet(dto.getSheetId(), sheetSubid,	CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, ivfMap);
				
			}
		} catch (Exception c) {
			
			StringWriter sw = new StringWriter();
            c.printStackTrace(new java.io.PrintWriter(sw));
            String exceptionAsString = sw.toString();
            c.printStackTrace();
             p=p+"-"+exceptionAsString;

              //p=p+"-"+ex.getMessage()+" Please contact Admin..";
		}
		if (p.equals("")) p="Success";
  return p;
	}

	private static void test() throws Exception{
		try {
			String d=null;
			System.out.println(d.equals("d"));
		}finally {
			System.out.println("999");
		}
		
	}
	private static void updateHistortList(IVFHistorySheet his,List<IVFHistorySheet> li) {
		int noOFhistory = Constants.history_codes_size;
		Class<?> c2=null;
		PropertyDescriptor pd;
		try {
			c2 = Class.forName("com.tricon.ruleengine.model.sheet.IVFHistorySheet");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// --DONE
		if (his != null) {
				 //his.setHistory10DOS(correctDateformat(his.getHistory10DOS()));
			 	 for (int i = 1; i <= noOFhistory; i++) {
			 		try {
						String hd = "getHistory" + i + "DOS";
					    String hc = "getHistory" + i + "Code";
						String ht = "getHistory" + i + "Tooth";
						String hs = "getHistory" + i + "Surface";
						Method hcm = c2.getMethod(hc);
						Method htm = c2.getMethod(ht);
						Method hdm = c2.getMethod(hd);
						Method hss = c2.getMethod(hs);	
						
						String  code =    hcm.invoke(his)==null?"":(String) hcm.invoke(his);
						String	dos =     hdm.invoke(his)==null?"":(String) hdm.invoke(his);
						String	tooth =   htm.invoke(his)==null?"":(String) htm.invoke(his);
						String	surface = hss.invoke(his)==null?"":(String) hss.invoke(his);
						
						String shc = "history" + i + "Code";
						String shs = "history" + i + "Surface";
						String sht = "history" + i + "Tooth";
						String shd = "history" + i + "DOS";
						
						pd = new PropertyDescriptor(shd, c2);
						pd.getWriteMethod().invoke(his,DateUtils.correctDateformat(dos));
						pd = new PropertyDescriptor(shc, c2);
						pd.getWriteMethod().invoke(his, code);
						pd = new PropertyDescriptor(sht, c2);
						pd.getWriteMethod().invoke(his, tooth);
						pd = new PropertyDescriptor(shs, c2);
						pd.getWriteMethod().invoke(his, surface);
						
						
				     }catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}	
					}
			 	 /*
			 	PatientHistory ph = null;
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
					ph.setHistoryCode((String) hcm.invoke(his));
					ph.setHistoryDOS((String) hdm.invoke(his));
					ph.setHistorySurface((String) hss.invoke(his));
					ph.setHistoryTooth((String) htm.invoke(his));
					if (!(ph.getHistoryCode().equals("") && ph.getHistoryDOS().equals("")
						&& ph.getHistorySurface().equals("") && ph.getHistoryTooth().equals("")	))
					phl.add(ph);

				}
				*/
			 }
		
		
	}

	public static void main(String[] aa) {
		System.out.println(DateUtils.correctDateformat("4/12/2019"));
		System.out.println(DateUtils.correctDateformat("2018-10-17"));
		try {
		test();
		}catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}