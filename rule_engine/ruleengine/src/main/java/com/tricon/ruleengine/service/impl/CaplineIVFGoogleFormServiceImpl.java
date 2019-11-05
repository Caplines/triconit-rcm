package com.tricon.ruleengine.service.impl;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
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
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.PatientDao;
import com.tricon.ruleengine.dao.UserDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.GoogleReportsRDDTO;
import com.tricon.ruleengine.dto.ToothHistoryDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.model.db.PatientDetail;
import com.tricon.ruleengine.model.db.PatientHistory;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.pdf.CaplineIVFFormDtoToXML;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.IVFFormConversionUtil;

@Transactional(rollbackOn = Exception.class)
@Service
public class CaplineIVFGoogleFormServiceImpl implements CaplineIVFGoogleFormService {

	@Value("${re.admin}")
	private String amdinUserName;

	@Value("${re.xslt.path}")
	private String XSLT_PATH;

	@Value("${re.xslt.file}")
	private String XSLT_FILE;

	@Autowired
	OfficeDao od;

	@Autowired
	UserDao userDao;

	@Autowired
	PatientDao patientDao;

	@Override
	public Integer saveIVFFormData(CaplineIVFFormDto d,Office office) throws Exception {

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
		Patient pat = IVFFormConversionUtil.copyValueToPatient(d, office, date);
		Patient patd = patientDao.checkforPatientWithIdAndOffice(pat.getPatientId(), office);
		Integer r = 0;
		try {
			if (patd == null) {

				patd = patientDao.savePatientDataWithDetailsAndHistory(pat, office, user, date);
			} else {
				patd.setUpdatedBy(user);
				patd.setUpdatedDate(date);
				boolean detailsSave = false;
				if (patd.getPatientDetails() != null && patd.getPatientDetails().size() > 0
						&& pat.getPatientDetails() != null && pat.getPatientDetails().size() > 0) {
					Iterator<PatientDetail> iter = pat.getPatientDetails().iterator();
					PatientDetail pdN = iter.next();
					boolean oldDetailMatched = false;
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
							oldDetailMatched = true;
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
				patientDao.updatePatientDataWithDetailsAndHistory(patd, office, user, detailsSave);
			}
			r = patd.getPatientDetails().iterator().next().getId();
		} catch (Exception c) {
           c.printStackTrace();
		}

		return r;
	}

	@Override
	public Object convertPatientDataToIVFSheetData(Set<String> patIds, String officeName) throws Exception {
		// TODO Auto-generated method stub
		Office off = od.getOfficeByName(officeName);

		
		List<CaplineIVFFormDto> capD = createData(null, off, patIds);
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

	private List<CaplineIVFFormDto> createData(CaplineIVFQueryFormDto d, Office off, Set<String> patIds) {

		List<CaplineIVFFormDto> capD = patientDao.searchPatientDetailFromIVF(d, off, null);
		Set<String> patientIds = null;
		List<CaplineIVFFormDto> cList = null;

		Map<String, List<CaplineIVFFormDto>> map = new HashMap<>();
		for (CaplineIVFFormDto dto : capD) {

			dto.setBasicInfo1(off.getName());
			if (patientIds == null)
				patientIds = new HashSet<>();

			patientIds.add(dto.getBasicInfo21());
			if (map.get(dto.getBasicInfo21()) == null) {
				cList = new ArrayList<>();
				cList.add(dto);
				map.put(dto.getBasicInfo21(), cList);
			} else {
				List<CaplineIVFFormDto> x = map.get(dto.getBasicInfo21());
				x.add(dto);
			}

		}
		if (patientIds != null) {
			// List<Patient> pats= patientDao.searchPatientByPatientId(patientIds, off);
			List<PatientHistory> patsH = patientDao.searchPatientHistoryForPatient(patientIds, off);
			/*
			 * for(Patient p:pats) { List<CaplineIVFFormDto> c=map.get(p.getPatientId());
			 * for(CaplineIVFFormDto form:c) { form.setBasicInfo2(p.getFirstName()+" "
			 * +(p.getLastName()==null?"":p.getLastName())); form.setBasicInfo6(p.getDob());
			 * } }
			 */
			for (PatientHistory ph : patsH) {
				List<CaplineIVFFormDto> c = map.get(ph.getPatient().getPatientId());
				for (CaplineIVFFormDto form : c) {
					List<String> his = form.getHistory();
					String l = ((ph.getHistoryCode().equals("")) ? "BLANK" : ph.getHistoryCode()) + Constants.PATH_SEPERATOR_XML_IVF
							+ ((ph.getHistoryTooth().equals("")) ? "BLANK" : ph.getHistoryTooth()) + Constants.PATH_SEPERATOR_XML_IVF
							+ ((ph.getHistorySurface().equals("")) ? "BLANK" : ph.getHistorySurface())
							+ Constants.PATH_SEPERATOR_XML_IVF + ((ph.getHistoryDOS().equals("")) ? "BLANK" : ph.getHistoryDOS());
					if (his == null)
						his = new ArrayList<>();
					his.add(l);
					form.setHistory(his);
				}

			}
		}

		return capD;

	}

	@Override
	public Object searchIVFData(CaplineIVFQueryFormDto d,Office office) throws Exception {
		// TODO Auto-generated method stub
		List<CaplineIVFFormDto> capD = createData(d, office, null);
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
	public Object searchIVFDataforApp(CaplineIVFQueryFormDto d) throws Exception {
		// TODO Auto-generated method stub
		Office off = od.getOfficeByName(d.getOfficeNameDB());
		
		List<CaplineIVFFormDto> capD = createData(d, off, null);
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
	/*
	 * if any changes done here make sure to verify same in copyValueToIVFSheet
	 */

	@Override
	public ByteArrayOutputStream generatePDF(CaplineIVFQueryFormDto dto,Office office) {
		ByteArrayOutputStream o = null;
		try {
			
			List<CaplineIVFFormDto> li = (List<CaplineIVFFormDto>) searchIVFData(dto,office);
			if (li.size() > 0) {
				CaplineIVFFormDto form = li.get(0);
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
				CaplineIVFFormDtoToXML xml = new CaplineIVFFormDtoToXML();
				String filePath = xml.convertToXML(form, XSLT_PATH);

				o = xml.createPdfStream(

						xml.createHtml(filePath, XSLT_FILE), "");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return o;
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

}
