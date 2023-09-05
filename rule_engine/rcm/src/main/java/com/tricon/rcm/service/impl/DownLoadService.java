package com.tricon.rcm.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.PendencyWithOfficeOnlyDto;
import com.tricon.rcm.dto.download.AllPendancyDownloadDto;
import com.tricon.rcm.dto.download.AllPendancySortedDownloadDto;
import com.tricon.rcm.dto.download.ClaimDetailsDownloadDto;
import com.tricon.rcm.dto.download.IssueClaimDownloadDto;
import com.tricon.rcm.dto.download.IvfDownloadDto;
import com.tricon.rcm.dto.download.ListOfClaimDownloadDto;
import com.tricon.rcm.dto.download.OthersTeamWorkDownloadDto;
import com.tricon.rcm.dto.download.PendancyDownloadDto;
import com.tricon.rcm.dto.download.ProductionDownloadDto;
import com.tricon.rcm.dto.download.TreatmentPlanDownloadDto;
import com.tricon.rcm.pdfDto.AllPendancyPdfDto;
import com.tricon.rcm.pdfDto.TotalCount;
import com.tricon.rcm.util.Constants;
import com.tricon.rcm.util.DtoToXmlConverted;


@Service
public class DownLoadService {
	
	@Value("${list_claim.xslt.file}")
	private String LIST_CLAIM_XSLT_FILE;
	
	@Value("${Issue_claim.xslt.file}")
	private String ISSUE_CLAIM_XSLT_FILE;
	
	@Value("${Claim_Details.xslt.file}")
	private String CLAIM_DETAILS_XSLT_FILE;
	
	@Value("${Production.xslt.file}")
	private String PRODUCTION_XSLT_FILE;
	
	@Value("${Pendancy.xslt.file}")
	private String PENDANCY_XSLT_FILE;
	
	@Value("${rcm.xslt.path}")
	private String XSLT_PATH;
	
	@Value("${All_Pendancy.xslt.file}")
	private String ALL_PENDANCY_XSLT_FILE;
	
	@Value("${Tp_Plan_Link.xslt.file}")
	private String TP_PLAN_XSLT_FILE;
	
	
	@Value("${Ivf_Link.xslt.file}")
	private String IVF_XSLT_FILE;
	
	@Value("${others_team.xslt.file}")
	private String OTHERS_TEAM_XSLT_FILE;
	
	@Autowired
	ClaimServiceImpl claimServiceImpl;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
    private Environment environment;
	
	
	public Object[] generatePdfForListOfClaims(ListOfClaimDownloadDto data) {
		
		ByteArrayOutputStream o = null;
		Object[] obj=new Object[2]; 
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
		String filePath = xml.convertToXMLForListOfClaims(data, XSLT_PATH);
		File file = new File(filePath);
		String xslt=LIST_CLAIM_XSLT_FILE;
		o= xml.createPdfStream(

				xml.createHtml(filePath, xslt), "");
	    
		if (file!=null) file.delete(); 
		
		obj[1]=o;
		}catch(Exception c) {
			
		}
		return obj;
	}
	
	
	public Object[]  generateClaimPdf(String claimUuid,PartialHeader partialHeader) {
		
		
		
		
		FreshClaimDataImplDto dto = claimServiceImpl.fetchIndividualClaim(claimUuid, partialHeader, true);
		
		
		
		
		ByteArrayOutputStream o = null;
		Object[] obj=new Object[2]; 
		/*
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
		String filePath = xml.convertToXML(data, XSLT_PATH);
		File file = new File(filePath);
		String xslt=LIST_CLAIM_XSLT_FILE;
		o= xml.createPdfStream(

				xml.createHtml(filePath, xslt), "");
	    
		if (file!=null) file.delete(); 
		
		obj[1]=o;
		}catch(Exception c) {
			
		}*/
		return obj;
	}
	
	public Object[] generatePdfForIssueClaim(IssueClaimDownloadDto data) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForIssueClaim(data, XSLT_PATH);
			File file = new File(filePath);
			String xslt = ISSUE_CLAIM_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}


	public Object[] generatePDFForclaimDetails(ClaimDetailsDownloadDto dto) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		String claimUuid=dto.getData().stream().map(x->x.getUuid()).findFirst().orElse(null);
//		if(!dto.getTpId().trim().equals("") && !dto.getTpId().isEmpty() ){	
//			try {
//				ExecutorService emailExecutor = Executors.newCachedThreadPool();
//				emailExecutor.execute(new Runnable() {
//					@Override
//					public void run() {
//						TreatmentPlanDownloadDto tpdto=new TreatmentPlanDownloadDto();
//						tpdto.setData(dto.getTpData());
//						tpdto.setFee(dto.getFee());
//						tpdto.setIns(dto.getIns());
//						tpdto.setPat(dto.getPat());
//						tpdto.setClaimUuid(claimUuid);
//						tpdto.setFileName("TPlan_Link");
//						generatePDFForTpPlan(tpdto);
//					}
//				});
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//		}
//		if(!dto.getIvId().trim().equals("") && !dto.getIvId().isEmpty() ){	
//			try {
//				ExecutorService emailExecutor = Executors.newCachedThreadPool();
//				emailExecutor.execute(new Runnable() {
//					@Override
//					public void run() {
//						IvfDownloadDto ivdto=new IvfDownloadDto();
//						ivdto.setData(dto.getReportData());
//						ivdto.setClaimUuid(claimUuid);
//						ivdto.setFileName("Ivf");
//						generatePDFForIvf(ivdto);
//					}
//				});
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//		}
			try {
	
//			String destinationFolder =this.setDestinationPath();
//			dto.setTpLinkPath(destinationFolder);
			dto.setPath(this.isDevProfileActive());
			String filePath = xml.convertToXMLForclaimDetails(dto, XSLT_PATH);		
			File file = new File(filePath);
			String xslt = CLAIM_DETAILS_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}
	
	public Object[] generatePDFForProduction(ProductionDownloadDto dto) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForProduction(dto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = PRODUCTION_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}
	
	public Object[] generatePDFForPendancy(PendancyDownloadDto dto) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForPendancy(dto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = PENDANCY_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}


	public Object[] generatePDFForAllPendancy(AllPendancyDownloadDto dto) {
		AllPendancySortedDownloadDto newDto=new AllPendancySortedDownloadDto();
		newDto.setClientName(dto.getClientName());		
		newDto.setCurrentTeamId(dto.getCurrentTeamId());
		newDto.setCurrentTeamName(dto.getCurrentTeamName());
		newDto.setFileName(dto.getFileName());
		newDto.setTabSwitch(dto.getTabSwitch());		
		newDto.setTeamsData(dto.getTeamsData());	
		List<AllPendancyPdfDto>newData=new ArrayList<>();
		if (dto.getTabSwitch().equalsIgnoreCase("withoutDos")) {
			Map<String, Object> sortedCounts1 = null;
			AllPendancyPdfDto pdfdto=null;
			HashMap<String, Object> getcounts1 = new HashMap<>();
			for (PendencyWithOfficeOnlyDto copyDto : dto.getData()) {
				pdfdto=new AllPendancyPdfDto();
				getcounts1 = copyDto.getCounts1();
				sortedCounts1 = new TreeMap<>(getcounts1);
				pdfdto.setSortedCounts1(sortedCounts1);
				pdfdto.setOfficeName(copyDto.getOfficeName());				
				newData.add(pdfdto);		
				}
			List<TotalCount>totalCount=dto.getTotalCount();
			totalCount=totalCount.stream()
			        .sorted(Comparator.comparing(TotalCount::getTeamName))
			        .peek(totalCounts -> totalCounts.setTeamName(totalCounts.getTeamName().toUpperCase()))
			        .collect(Collectors.toList());
			newDto.setData(newData);	
			newDto.setSortedTotalCount(totalCount);
			}
		if (dto.getTabSwitch().equalsIgnoreCase("withDOS")) {
			Map<String, Object> sortedDos = null;
			AllPendancyPdfDto pdfdto=null;
			HashMap<String, Object> getDos = new HashMap<>();
			for (PendencyWithOfficeOnlyDto copyDto : dto.getData()) {
				pdfdto=new AllPendancyPdfDto();
				getDos = copyDto.getDates1();
				sortedDos = new TreeMap<>(getDos);
				pdfdto.setSortedDates1(sortedDos);
				pdfdto.setOfficeName(copyDto.getOfficeName());				
				newData.add(pdfdto);		
				}
			newDto.setData(newData);						
		}
		if (dto.getTabSwitch().equalsIgnoreCase("withDOP")) {
			Map<String, Object> sortedDop = null;
			AllPendancyPdfDto pdfdto=null;
			HashMap<String, Object> getDop = new HashMap<>();
			for (PendencyWithOfficeOnlyDto copyDto : dto.getData()) {
				pdfdto=new AllPendancyPdfDto();
				getDop = copyDto.getDatesPending();
				sortedDop = new TreeMap<>(getDop);
				pdfdto.setSortedPending(sortedDop);
				pdfdto.setOfficeName(copyDto.getOfficeName());				
				newData.add(pdfdto);		
				}
			newDto.setData(newData);						
		}
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForAllPendancy(newDto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = ALL_PENDANCY_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}
	
	public Object[] generatePDFForTpPlan(TreatmentPlanDownloadDto dto) {
		ByteArrayOutputStream o = null;
		//String destinationFolder =null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForTpPlan(dto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = TP_PLAN_XSLT_FILE;
			o = xml.createPdfStream(xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
//			destinationFolder = this.setDestinationPath();
//			Path destinationFile = Paths.get(destinationFolder+dto.getClaimUuid().concat("_tp")+ ".pdf");
//			Files.write(destinationFile, o.toByteArray());
		} catch (Exception c) {
             c.printStackTrace();
		}
//		finally {
//			if(o!=null) {
//				try {
//					o.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		return obj;
	}
	
//	private String setDestinationPath() {
//		Resource resource = resourceLoader.getResource("classpath:");
//		String fullPath=null;
//		String filePath = null;
//		try {
//			fullPath=resource.getFile().getAbsolutePath();
//			int endIndex = fullPath.indexOf("rule_engine");
//			String extractedString = fullPath.substring(0, endIndex-1);
//			filePath = extractedString.concat(File.separator).concat("ivf Form").concat(File.separator);			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return filePath;
//	}


	public Object[] generatePDFForIvf(IvfDownloadDto data) {
		ByteArrayOutputStream o = null;
		//String destinationFolder =null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForIvf(data, XSLT_PATH);
			File file = new File(filePath);
			String xslt = IVF_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
//			destinationFolder = this.setDestinationPath();
//			Path destinationFile = Paths.get(destinationFolder+data.getClaimUuid().concat("_iv")+ ".pdf");
//			Files.write(destinationFile, o.toByteArray());
		} catch (Exception c) {
                    c.printStackTrace();
		}
//		finally {
//			if(o!=null) {
//				try {
//					o.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		return obj;
	}
	

	public String isDevProfileActive() {
		String[] activeProfiles = environment.getActiveProfiles();
		for (String profile : activeProfiles) {
			if (profile.equals("dev")) {
				return Constants.DEV_URL;
			} else {
				return Constants.PROD_URL;
			}
		}
		return null;
	}
	
    public Object[] generatePdfForOthersTeamWorks(OthersTeamWorkDownloadDto data) {
		
		ByteArrayOutputStream o = null;
		Object[] obj=new Object[2]; 
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
		String filePath = xml.convertToXMLForOthersTeamWork(data, XSLT_PATH);
		File file = new File(filePath);
		String xslt=OTHERS_TEAM_XSLT_FILE;
		o= xml.createPdfStream(

				xml.createHtml(filePath, xslt), "");
	    
		if (file!=null) file.delete(); 
		
		obj[1]=o;
		}catch(Exception c) {
			
		}
		return obj;
	}
}
