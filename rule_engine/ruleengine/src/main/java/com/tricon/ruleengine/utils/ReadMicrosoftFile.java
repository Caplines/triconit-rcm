package com.tricon.ruleengine.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.collect.Collections2;
import com.monitorjbl.xlsx.StreamingReader;
import com.tricon.ruleengine.dto.MicroSoftSheetJson;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.model.sheet.EagleSoftEmployerMaster;
import com.tricon.ruleengine.model.sheet.EagleSoftFeeShedule;
import com.tricon.ruleengine.model.sheet.EagleSoftPatient;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;
import com.tricon.ruleengine.model.sheet.TreatmentPlan;
import com.tricon.ruleengine.model.sheet.TreatmentPlanDetails;
import com.tricon.ruleengine.model.sheet.TreatmentPlanPatient;

public class ReadMicrosoftFile {

	private Object[] getFileFromEagelSoft(String urlStr, String sheetName) throws IOException {

		URL url = new URL(urlStr);

		// InputStream is = new FileInputStream(new File("/path/to/workbook.xlsx"));
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		Workbook workbook = StreamingReader.builder().rowCacheSize(3000) // number of rows to keep in memory (defaults
																			// to
																			// 10)
				.bufferSize(2048) // buffer size to use when reading InputStream to file (defaults to 1024)
				.open(bis); // InputStream or File for XLSX file (required)

		// Workbook workbook = new XSSFWorkbook(bis);
		Sheet datatypeSheet = workbook.getSheet(sheetName);
		Iterator<Row> iterator = datatypeSheet.iterator();
		Object[] obj = new Object[] { bis, workbook, iterator };
		return obj;
	}

	/**
	 * 
	 * @param urlStr
	 * @param sheetName
	 * @param type
	 * @param dto
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<?>> downloadAndReadUsingStream(String urlStr, String sheetName, String type,
			String[] treatmentPlanIds, List<String> codes, Map<String, List<Object>> ivMap,
			Map<String, List<EagleSoftPatient>> patientsMap) throws IOException {

		Object[] objArray = getFileFromEagelSoft(urlStr, sheetName);
		Iterator<Row> iterator = (Iterator<Row>) objArray[2];
		BufferedInputStream bis = (BufferedInputStream) objArray[0];
		Workbook workbook = (Workbook) objArray[1];

		// Iterator<Row> iterator = datatypeSheet.iterator();
		Map<String, List<?>> map = null;

		if (Constants.microsoft_treatement_sheet_name.equals(type))
			map = readTreatmentPlan(iterator, treatmentPlanIds);
		else if (Constants.microsoft_emp_master.equals(type))
			map = readEmpmaster(iterator, patientsMap);
		else if (Constants.microsoft_feeSchedule_master.equals(type))
			map = readFeeSchedule(iterator, patientsMap);
		else if (Constants.microsoft_patient.equals(type))
			map = readPatient(iterator, ivMap);

		//
		bis.close();
		workbook.close();

		return map;
	}

	public List<TreatmentPlan> downloadAndReadUsingStreamForTreatOnly(String urlStr, String sheetName,
			String patientId) throws IOException {

		Object[] objArray = getFileFromEagelSoft(urlStr, sheetName);
		Iterator<Row> iterator = (Iterator<Row>) objArray[2];
		BufferedInputStream bis = (BufferedInputStream) objArray[0];
		Workbook workbook = (Workbook) objArray[1];

		// Iterator<Row> iterator = datatypeSheet.iterator();
		
		List<TreatmentPlan> list = readTreatmentPlanForPatient(iterator, patientId);

		//
		bis.close();
		workbook.close();

		return list;
	}

	/*
	 * public Map<String, List<Object>> downloadAndReadUsingStreamMap(String urlStr,
	 * String sheetName, String type, List<Object> ivfSheet, Map<String,
	 * List<EagleSoftPatient>> pats) throws IOException { URL url = new URL(urlStr);
	 * Map<String, List<Object>> map = null; BufferedInputStream bis = new
	 * BufferedInputStream(url.openStream()); Workbook workbook =
	 * StreamingReader.builder().rowCacheSize(100) // number of rows to keep in
	 * memory (defaults to // 10) .bufferSize(2048) // buffer size to use when
	 * reading InputStream to file (defaults to 1024) .open(bis); // InputStream or
	 * File for XLSX file (required)
	 * 
	 * Sheet datatypeSheet = workbook.getSheet(sheetName); Iterator<Row> iterator =
	 * datatypeSheet.iterator(); List<Object> list = null;
	 * 
	 * if (Constants.microsoft_emp_master.equals(type)) {
	 * 
	 * for (Map.Entry<String, List<EagleSoftPatient>> entry : pats.entrySet()) {
	 * System.out.println("Key = " + entry.getKey() + ", Value = " +
	 * entry.getValue()); List<EagleSoftPatient> x = entry.getValue(); if (x !=
	 * null) { list = readEmpmaster(iterator, x.get(0).getEmployerId()); if (list !=
	 * null) { if (map == null) map = new HashMap<>(); if
	 * (map.get(x.get(0).getEmployerId()) == null) map.put(x.get(0).getEmployerId(),
	 * list); } } } } else if (Constants.microsoft_patient.equals(type)) { for
	 * (Object obj : ivfSheet) { IVFTableSheet ivf = (IVFTableSheet) obj; list =
	 * readPatient(iterator, ivf); if (list != null) { if (map == null) map = new
	 * HashMap<>(); map.put(ivf.getUniqueID(), list); } } }
	 * 
	 * // bis.close(); workbook.close();
	 * 
	 * return map; }
	 * 
	 */
	private Map<String, List<?>> readTreatmentPlan(Iterator<Row> iterator, String[] treatmentPlanIds) {
		Map<String, List<?>> map = null;
		List<Object> list = null;
		List<String> treatmentPlanIdsL = Arrays.asList(treatmentPlanIds);
		TreatmentPlan tp = null;
		TreatmentPlanPatient patient = null;
		TreatmentPlanDetails treatmentPlanDetails = null;
		int rowCt = -1;
		while (iterator.hasNext()) {
			rowCt++;
			Row currentRow = iterator.next();
			if (rowCt == 0) {
				continue;
			} // Ignore First Row
			Iterator<Cell> cellIterator = currentRow.iterator();
			int colCt = -1;
			tp = new TreatmentPlan();
			patient = new TreatmentPlanPatient();
			treatmentPlanDetails = new TreatmentPlanDetails();
			boolean added = false;
			while (cellIterator.hasNext()) {
				colCt++;
				Cell currentCell = cellIterator.next();
				if (colCt == 0)
					tp.setApptId(currentCell.getStringCellValue());
				else if (colCt == 1)
					patient.setId(currentCell.getStringCellValue());
				else if (colCt == 2)
					patient.setName(currentCell.getStringCellValue());
				else if (colCt == 3)
					patient.setLastName(currentCell.getStringCellValue());
				else if (colCt == 4)
					tp.setLineItem(currentCell.getStringCellValue());
				else if (colCt == 5)
					tp.setServiceCode(currentCell.getStringCellValue());
				else if (colCt == 6)
					tp.setDescription(currentCell.getStringCellValue());
				else if (colCt == 7) {
					added = false;
					tp.setId(currentCell.getStringCellValue());
					final String a = tp.getId();
					Collection<String> ruleGen = Collections2.filter(treatmentPlanIdsL, name -> name.equals(a));
					if (ruleGen != null && ruleGen.size() > 0) {

					} else {
						break;
					}
					// if (!tp.getId().equals(treatmentPlanIds))
					// break;
					// else
					added = true;
				} else if (colCt == 8)
					treatmentPlanDetails.setDateLastUpdated(currentCell.getStringCellValue());
				else if (colCt == 9)
					tp.setSurface(currentCell.getStringCellValue());
				else if (colCt == 10)
					tp.setTooth(currentCell.getStringCellValue());
				else if (colCt == 11)
					tp.setStatus(currentCell.getStringCellValue());
				else if (colCt == 12)
					treatmentPlanDetails.setStatus(currentCell.getStringCellValue());
				else if (colCt == 13)
					tp.setFee(currentCell.getStringCellValue());
				else if (colCt == 14)
					tp.setEstPrimary(currentCell.getStringCellValue());

				else if (colCt == 15)
					treatmentPlanDetails.setEstSecondary(currentCell.getStringCellValue());
				else if (colCt == 16)
					treatmentPlanDetails.setDescription(currentCell.getStringCellValue());
				else if (colCt == 17)
					tp.setEstInsurance(currentCell.getStringCellValue());
				else if (colCt == 18)
					tp.setPatientPortion(currentCell.getStringCellValue());
				//
				if (tp.getTooth() == null)
					tp.setTooth("NA");
				else if (tp.getTooth() != null && tp.getTooth().trim().equals(""))
					tp.setTooth("NA");// NA Mean All Tooth.. like cleaning..

			}
			if (added) {
				// if (list == null) list = new ArrayList<>();
				tp.setTreatmentPlanDetails(treatmentPlanDetails);
				tp.setPatient(patient);
				// list.add(tp);
				if (map == null)
					map = new HashMap<>();
				if (map.containsKey(tp.getId())) {
					// if the key has already been used,
					// we'll just grab the array list and add the value to it
					list = (List<Object>) (List<?>) map.get(tp.getId());
					list.add(tp);
				} else {
					// if the key hasn't been used yet,
					// we'll create a new ArrayList<String> object, add the value
					// and put it in the array list with the new key
					list = new ArrayList<>();
					list.add(tp);
					map.put(tp.getId(), list);
				}

				// System.out.println(tp.getId());
			}

		}
		return map;
	}

	private List<TreatmentPlan> readTreatmentPlanForPatient(Iterator<Row> iterator, String patientId) {
		List<TreatmentPlan> list = null;
		TreatmentPlan tp = null;
		TreatmentPlanPatient patient = null;
		TreatmentPlanDetails treatmentPlanDetails = null;
		int rowCt = -1;
		while (iterator.hasNext()) {
			rowCt++;
			Row currentRow = iterator.next();
			if (rowCt == 0) {
				continue;
			} // Ignore First Row
			Iterator<Cell> cellIterator = currentRow.iterator();
			int colCt = -1;
			tp = new TreatmentPlan();
			patient = new TreatmentPlanPatient();
			treatmentPlanDetails = new TreatmentPlanDetails();
			boolean added = false;
			while (cellIterator.hasNext()) {
				colCt++;
				Cell currentCell = cellIterator.next();
				if (colCt == 0)
					tp.setApptId(currentCell.getStringCellValue());
				else if (colCt == 1) {
					patient.setId(currentCell.getStringCellValue());
					
					if (patient.getId()!=null && patient.getId().trim().equalsIgnoreCase(patientId)) {
						added=true;	
					} else {
						break;
					}

				} else if (colCt == 2)
					patient.setName(currentCell.getStringCellValue());
				else if (colCt == 3)
					patient.setLastName(currentCell.getStringCellValue());
				else if (colCt == 4)
					tp.setLineItem(currentCell.getStringCellValue());
				else if (colCt == 5)
					tp.setServiceCode(currentCell.getStringCellValue());
				else if (colCt == 6)
					tp.setDescription(currentCell.getStringCellValue());
				else if (colCt == 7) {
					tp.setId(currentCell.getStringCellValue());
				} else if (colCt == 8)
					treatmentPlanDetails.setDateLastUpdated(currentCell.getStringCellValue());
				else if (colCt == 9)
					tp.setSurface(currentCell.getStringCellValue());
				else if (colCt == 10)
					tp.setTooth(currentCell.getStringCellValue());
				else if (colCt == 11)
					tp.setStatus(currentCell.getStringCellValue());
				else if (colCt == 12)
					treatmentPlanDetails.setStatus(currentCell.getStringCellValue());
				else if (colCt == 13)
					tp.setFee(currentCell.getStringCellValue());
				else if (colCt == 14)
					tp.setEstPrimary(currentCell.getStringCellValue());

				else if (colCt == 15)
					treatmentPlanDetails.setEstSecondary(currentCell.getStringCellValue());
				else if (colCt == 16)
					treatmentPlanDetails.setDescription(currentCell.getStringCellValue());
				else if (colCt == 17)
					tp.setEstInsurance(currentCell.getStringCellValue());
				else if (colCt == 18)
					tp.setPatientPortion(currentCell.getStringCellValue());
				//
				if (tp.getTooth() == null)
					tp.setTooth("NA");
				else if (tp.getTooth() != null && tp.getTooth().trim().equals(""))
					tp.setTooth("NA");// NA Mean All Tooth.. like cleaning..

			}
			if (added) {
				// if (list == null) list = new ArrayList<>();
				tp.setTreatmentPlanDetails(treatmentPlanDetails);
				tp.setPatient(patient);
				// list.add(tp);
				if (list == null)
					list = new ArrayList();
				list.add(tp);
				

				// System.out.println(tp.getId());
			}

		}
		return list;
	}

	private Map<String, List<?>> readFeeSchedule(Iterator<Row> iterator,  Map<String, List<EagleSoftPatient>> patientMap) {
		Map<String, List<?>> map = null;
		List<Object> list = null;
		EagleSoftFeeShedule fn = null;
		int rowCt = 0;
		while (iterator.hasNext()) {

			Row currentRow = iterator.next();
			if (rowCt == 0) {
				rowCt++;
				continue;
			} // Ignore First Row
			Iterator<Cell> cellIterator = currentRow.iterator();
			int colCt = -1;
			fn = new EagleSoftFeeShedule();
			while (cellIterator.hasNext()) {
				colCt++;

				Cell currentCell = cellIterator.next();
				// currentCell.setCellType(CellType.STRING);
				if (colCt == 0)
					fn.setFeeId(currentCell.getStringCellValue());
				else if (colCt == 1)
					fn.setName(currentCell.getStringCellValue());
				else if (colCt == 2)
					fn.setFeesServiceCode(currentCell.getStringCellValue());
				else if (colCt == 3)
					fn.setFeesFee(currentCell.getStringCellValue());

			}
			//final String cd = fn.getFeesServiceCode();
			if (patientMap != null) {
				for (Map.Entry<String, List<EagleSoftPatient>> entry : patientMap.entrySet()) {
					if (entry.getValue() != null) {
						EagleSoftPatient es = ((EagleSoftPatient) (entry.getValue().get(0)));
						if (fn.getFeeId().equals(es.getFeeScheduleId())) {

							if (map == null)
								map = new HashMap<>();
							if (map.containsKey(es.getFeeScheduleId())) {
								// if the key has already been used,
								// we'll just grab the array list and add the value to it
								list = (List<Object>) (List<?>) map.get(es.getFeeScheduleId());
								list.add(fn);
							} else {
								// if the key hasn't been used yet,
								// we'll create a new ArrayList<String> object, add the value
								// and put it in the array list with the new key
								list = new ArrayList<>();
								list.add(fn);
								map.put(es.getFeeScheduleId(), list);
							}
						}

					}
				}
			}
		}
		return map;
	}

	private Map<String, List<?>> readEmpmaster(Iterator<Row> iterator, Map<String, List<EagleSoftPatient>> patientMap) {
		Map<String, List<?>> map = null;
		List<Object> list = null;
		EagleSoftEmployerMaster fn = null;
		int rowCt = 0;
		while (iterator.hasNext()) {

			Row currentRow = iterator.next();
			if (rowCt == 0) {
				rowCt++;
				continue;
			} // Ignore First Row
			Iterator<Cell> cellIterator = currentRow.iterator();
			int colCt = -1;
			fn = new EagleSoftEmployerMaster();
			while (cellIterator.hasNext()) {
				colCt++;
				Cell currentCell = cellIterator.next();
				// currentCell.setCellType(CellType.STRING);
				if (colCt == 0)
					fn.setEmployerId(currentCell.getStringCellValue());
				else if (colCt == 1)
					fn.setEmployerName(currentCell.getStringCellValue());
				else if (colCt == 2)
					fn.setEmployerGroupNumber(currentCell.getStringCellValue());
				else if (colCt == 3)
					fn.setEmployerMaximumCoverage(currentCell.getStringCellValue());
				else if (colCt == 4)
					fn.setServiceTtypeId(currentCell.getStringCellValue());
				else if (colCt == 5)
					fn.setServiceTypeDescription(currentCell.getStringCellValue());
				if (colCt == 6)
					fn.setPercentage(currentCell.getStringCellValue());
				else if (colCt == 7)
					fn.setDeductibleApplies(currentCell.getStringCellValue());

			}

			if (patientMap != null) {
				for (Map.Entry<String, List<EagleSoftPatient>> entry : patientMap.entrySet()) {
					if (entry.getValue() != null) {
						EagleSoftPatient es = ((EagleSoftPatient) (entry.getValue().get(0)));
						if (fn.getEmployerId().equals(es.getEmployerId())) {

							if (map == null)
								map = new HashMap<>();
							if (map.containsKey(es.getEmployerId())) {
								// if the key has already been used,
								// we'll just grab the array list and add the value to it
								list = (List<Object>) (List<?>) map.get(es.getEmployerId());
								list.add(fn);
							} else {
								// if the key hasn't been used yet,
								// we'll create a new ArrayList<String> object, add the value
								// and put it in the array list with the new key
								list = new ArrayList<>();
								list.add(fn);
								map.put(es.getEmployerId(), list);
							}
						}

					}
				}
			}
			/*
			 * final String cd=fn.getServiceTypeServiceCode(); Collection<String> ruleGen =
			 * Collections2.filter(code, name -> name.equals(cd)); if (ruleGen.size()==1) {
			 * if (list==null) list=new ArrayList<>(); list.add(fn); }
			 */
		}
		return map;
	}

	private Map<String, List<?>> readPatient(Iterator<Row> iterator, Map<String, List<Object>> ivMap) {
		Map<String, List<?>> map = null;
		List<Object> list = null;
		EagleSoftPatient fn = null;
		int rowCt = 0;
		while (iterator.hasNext()) {

			Row currentRow = iterator.next();
			if (rowCt == 0) {
				rowCt++;
				continue;
			} // Ignore First Row
			Iterator<Cell> cellIterator = currentRow.iterator();
			int colCt = -1;
			fn = new EagleSoftPatient();
			while (cellIterator.hasNext()) {
				colCt++;
				Cell currentCell = cellIterator.next();
				String c = currentCell.getStringCellValue();
				
				//System.out.println("ii--"+c);
				if (c == null)
					c = "";
				// currentCell.setCellType(CellType.STRING);
				if (colCt == 0) {
					fn.setPatientId(c);// A
				/*
					if (fn.getPatientId().equals("11196")) {
					System.out.println("ss");
				}
				*/
				}
				else if (colCt == 1)
					fn.setFirstName(c);// B
				else if (colCt == 2)
					fn.setLastName(c);// C
				else if (colCt == 3) {

					if (currentCell.getDateCellValue() != null) {
						fn.setBirthDate(Constants.SIMPLE_DATE_FORMAT.format(currentCell.getDateCellValue()));
					} else {
						fn.setBirthDate("");// D
					}
				} else if (colCt == 4)
					fn.setSocialSecurity(c);// E
				else if (colCt == 5)
					fn.setPrimMemberId(c);// F
				else if (colCt == 6)
					fn.setStatus(c);// G
				else if (colCt == 7)
					fn.setResponsiblePartyStatus(c);// H
				else if (colCt == 8)
					fn.setResponsibleParty(c);// I
				else if (colCt == 9)
					fn.setMaximumCoverage(c);// J
				else if (colCt == 10)
					fn.setPrimBenefitsRemaining(c);// K
				else if (colCt == 11)
					fn.setPrimRemainingDeductible(c);// L
				else if (colCt == 12)
					fn.setSecBenefitsRemaining(c);// M
				else if (colCt == 13)
					fn.setSecRemainingDeductible(c);// N
				else if (colCt == 14)
					fn.setEmployerId(c);// O
				else if (colCt == 15)
					fn.setEmployerName(c);// P
				else if (colCt == 16)
					fn.setFeeScheduleId(c);// Q
				else if (colCt == 17)
					fn.setFeeScheduleName(c);// R
				else if (colCt == 18) {
					fn.setCovBookHeaderId(c);// S
				} else if (colCt == 19) {
					fn.setCovBookHeaderName(c);// T
				}
			}

			try {

				// using for-each loop for iteration over Map.entrySet()
				if (ivMap != null) {
					for (Map.Entry<String, List<Object>> entry : ivMap.entrySet()) {
						if (entry.getValue() != null) {
							IVFTableSheet ivfSheet = ((IVFTableSheet) entry.getValue().get(0));
							// System.out.println(fn.getBirthDate() + fn.getFirstName() + " " +
							// fn.getLastName());
							// System.out.println(Constants.SIMPLE_DATE_FORMAT
							// .format(Constants.SIMPLE_DATE_FORMAT.parse(ivfSheet.getPatientDOB()))
							// + ivfSheet.getPatientName());

							// if ((fn.getBirthDate() + fn.getFirstName() + " " + fn.getLastName())
							// .equalsIgnoreCase(Constants.SIMPLE_DATE_FORMAT
							// .format(Constants.SIMPLE_DATE_FORMAT.parse(ivfSheet.getPatientDOB()))
							// + ivfSheet.getPatientName())) {
							if ((fn.getPatientId().trim().equalsIgnoreCase(ivfSheet.getPatientId()))) {
								if (map == null)
									map = new HashMap<>();
								if (map.containsKey(ivfSheet.getUniqueID())) {
									// if the key has already been used,
									// we'll just grab the array list and add the value to it
									list = (List<Object>) (List<?>) map.get(ivfSheet.getUniqueID());
									list.add(fn);
								} else {
									// if the key hasn't been used yet,
									// we'll create a new ArrayList<String> object, add the value
									// and put it in the array list with the new key
									list = new ArrayList<>();
									list.add(fn);
									// map.put(ivfSheet.getUniqueID().split("_")[1], list);
									map.put(ivfSheet.getUniqueID(), list);
								}

							}
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} // while First

		return map;
	}

	// BELOW all code not used ******************* MAY BE SECOND PHASE***
	public List<Object> downloadAndReadUsingRestAPI(String sheetId, String sheetName, String token, String type,
			Class clazz, TreatmentPlanValidationDto dto) throws IOException {
		String urlStr = Constants.graphRangeUrl;
		urlStr = urlStr.replace("--id--", sheetId).replace("--name--", sheetName.replace(" ", "%20"));
		// URL url = new URL(urlStr);
		// System.out.println(0000000);
		List<Object> list = null;
		Object obj = SendPostAndReadJSon.sendGetDataGraphDownloadJson(token, urlStr, clazz);
		if (Constants.microsoft_treatement_sheet_name.equals(type)) {
			list = readTreatmentPlanJson(obj, dto);
		} else if (Constants.microsoft_feeSchedule_master.equals(type)) {
			list = readFeeScheduleJson(obj, dto);
		} else if (Constants.microsoft_emp_master.equals(type)) {
			list = readEmployerMasterJson(obj, dto);
		} else if (Constants.microsoft_patient.equals(type)) {
			// list = readPatientJson(obj, dto);//
		}
		//
		return list;
	}

	private List<Object> readTreatmentPlanJson(Object obj, TreatmentPlanValidationDto dto) {
		List<Object> list = null;
		MicroSoftSheetJson graph = (MicroSoftSheetJson) obj;
		TreatmentPlan tp = null;
		TreatmentPlanPatient patient = null;
		TreatmentPlanDetails treatmentPlanDetails = null;
		for (Object o : graph.getData()) {
			List<Object> l = (ArrayList<Object>) o;
			int ctr = -1;
			tp = new TreatmentPlan();
			patient = new TreatmentPlanPatient();
			treatmentPlanDetails = new TreatmentPlanDetails();
			boolean added = false;
			for (Object x : l) {
				ctr++;
				String val = x.toString();
				// System.out.println(val);
				if (ctr == 0)
					tp.setApptId(val);
				else if (ctr == 1)
					patient.setId(val);
				else if (ctr == 2)
					patient.setName(val);
				else if (ctr == 3)
					patient.setLastName(val);
				else if (ctr == 4)
					tp.setLineItem(val);
				else if (ctr == 5)
					tp.setServiceCode(val);
				else if (ctr == 6)
					tp.setDescription(val);
				else if (ctr == 7) {
					if (dto.getTreatmentPlanId().equals(val)) {
						if (list == null)
							list = new ArrayList<>();
						tp.setId(val);
						added = true;
					} else {
						// tp=null;
						// patient=null;
						// treatmentPlanDetails=null;
						break;
					}
				} else if (ctr == 8)
					treatmentPlanDetails.setDateLastUpdated(val);
				else if (ctr == 9)
					tp.setSurface(val);
				else if (ctr == 10)
					tp.setTooth(val);
				else if (ctr == 11)
					tp.setStatus(val);
				else if (ctr == 12)
					treatmentPlanDetails.setStatus(val);
				else if (ctr == 13)
					tp.setFee(val);
				else if (ctr == 14)
					tp.setEstPrimary(val);
				else if (ctr == 15)
					treatmentPlanDetails.setEstSecondary(val);
				else if (ctr == 16)
					tp.setEstInsurance(val);
				else if (ctr == 17)
					tp.setPatientPortion(val);

			}
			if (tp != null && added) {
				tp.setTreatmentPlanDetails(treatmentPlanDetails);
				tp.setPatient(patient);
				list.add(tp);
			}
			// System.out.println(o.getClass());
		}
		return list;
	}

	private List<Object> readFeeScheduleJson(Object obj, TreatmentPlanValidationDto dto) {
		List<Object> list = null;
		MicroSoftSheetJson graph = (MicroSoftSheetJson) obj;
		EagleSoftFeeShedule fn = null;
		int rowCt = 0;
		for (Object o : graph.getData()) {
			List<Object> l = (ArrayList<Object>) o;
			if (rowCt == 0) {
				rowCt++;
				continue;
			} // Ignore First Row
			if (list == null) {
				list = new ArrayList<>();
			}

			int colCt = -1;
			fn = new EagleSoftFeeShedule();
			for (Object x : l) {
				colCt++;
				String val = x.toString();
				if (colCt == 0)
					fn.setFeeId(val);
				else if (colCt == 1)
					fn.setName(val);
				else if (colCt == 2)
					fn.setFeesServiceCode(val);
				else if (colCt == 3)
					fn.setFeesFee(val);

			}
			if (list != null)
				list.add(fn);
		}
		return list;
	}

	private List<Object> readEmployerMasterJson(Object obj, TreatmentPlanValidationDto dto) {
		List<Object> list = null;
		MicroSoftSheetJson graph = (MicroSoftSheetJson) obj;
		EagleSoftEmployerMaster fn = null;
		int rowCt = 0;
		for (Object o : graph.getData()) {
			List<Object> l = (ArrayList<Object>) o;
			if (rowCt == 0) {
				rowCt++;
				continue;
			} // Ignore First Row
			if (list == null) {
				list = new ArrayList<>();
			}

			int colCt = -1;
			fn = new EagleSoftEmployerMaster();
			for (Object x : l) {
				colCt++;
				String val = x.toString();
				if (colCt == 0)
					fn.setEmployerId(val);
				else if (colCt == 1)
					fn.setEmployerName(val);
				else if (colCt == 2)
					fn.setEmployerGroupNumber(val);
				else if (colCt == 3)
					fn.setEmployerMaximumCoverage(val);
				else if (colCt == 4)
					fn.setServiceTtypeId(val);
				else if (colCt == 5)
					fn.setServiceTypeDescription(val);
				if (colCt == 6)
					fn.setPercentage(val);
				else if (colCt == 7)
					fn.setDeductibleApplies(val);

			}
			if (list != null)
				list.add(fn);
		}
		return list;
	}

	/*
	 * private List<Object> readPatientJson(Object obj, TreatmentPlanValidationDto
	 * dto) { List<Object> list = null; MicroSoftSheetJson graph =
	 * (MicroSoftSheetJson) obj; EagleSoftPatient fn = null; int rowCt = 0; for
	 * (Object o : graph.getData()) { List<Object> l = (ArrayList<Object>) o; if
	 * (rowCt == 0) { rowCt++; continue; } // Ignore First Row if (list == null) {
	 * list = new ArrayList<>(); }
	 * 
	 * int colCt = -1; fn = new EagleSoftPatient(); for (Object x : l) { colCt++;
	 * String val = x.toString(); if (colCt == 0) fn.setPatientId(val); else if
	 * (colCt == 1) fn.setFirstName(val); else if (colCt == 2) fn.setLastName(val);
	 * else if (colCt == 3) fn.setBirthDate(val); else if (colCt == 4)
	 * fn.setSocialSecurity(val); else if (colCt == 5) fn.setPrimMemberId(val); else
	 * if (colCt == 6) fn.setResponsiblePartyStatus(val); else if (colCt == 7)
	 * fn.setResponsibleParty(val); else if (colCt == 8)
	 * fn.setPrimMaximumCcoverage(val); else if (colCt == 9)
	 * fn.setPrimBenefitsRemaining(val); else if (colCt == 10)
	 * fn.setPrimRemainingDeductible(val); else if (colCt == 11)
	 * fn.setSecBenefitsRemaining(val); else if (colCt == 12)
	 * fn.setSecRemainingDeductible(val); else if (colCt == 13)
	 * fn.setPlannedServicesServiceCode(val); else if (colCt == 14)
	 * fn.setPlannedServicesFee(val); else if (colCt == 15) fn.setEmployerId(val);
	 * else if (colCt == 16) fn.setEmployerName(val); else if (colCt == 17)
	 * fn.setPlannedServicesCompletionDate(val); else if (colCt == 18)
	 * fn.setFeeScheduleId(val); else if (colCt == 19) fn.setFeeScheduleName(val);
	 * else if (colCt == 20) fn.setCovBookHeaderName(val);
	 * 
	 * } if (list != null) list.add(fn); } return list; }
	 */
}
