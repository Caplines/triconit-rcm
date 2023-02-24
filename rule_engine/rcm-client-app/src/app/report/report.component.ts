import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { DatepickerOptions } from 'ng2-datepicker';
import { ReportModel } from '../models/model.report';
import { Router, ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ClaimTreatmentTextModel } from '../models/model.claimtreatmenttext';
import { ApplicationService } from '../service/application.service';

@Component({
	selector: 'app-report',
	templateUrl: './report.component.html',
	styleUrls: ['./report.component.css'],
	encapsulation: ViewEncapsulation.None,
	providers: [DatePipe]
})
export class ReportComponent implements OnInit {
	report: ReportModel = new ReportModel();
	errorMessage: string='';
    mainReportName:string='';
	offices: any;
    ivformTypes: any;
	users: any;
	reportData: any;
	arrayOfKeys: any;
	showLoading: boolean = false;
	showReportForm: boolean = false;
	showReportData: boolean = false;
	showDetailsData: boolean = false;
    showLoadingPDFS:boolean = false;
	reportDataInd: any;
    rdata1:any=[];
    rdata:any=[];
    
	// claim and Treatment Text
	hd1: string = "";
	// hd2:string="";
	ur: string = "/report";
	dateOptions: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a date',
		fieldId: 'datePicker',
		maxDate: new Date(Date.now()),
		useEmptyBarTitle: false

	};
	dateOptions1: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a From date',
		fieldId: 'datePicker1',
		maxDate: new Date(Date.now()),
		useEmptyBarTitle: false
	};
	dateOptions2: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a To date',
		fieldId: 'datePicker2',
		maxDate: new Date(Date.now()),
		useEmptyBarTitle: false
	};
    dateOptions3: DatepickerOptions = {
    		displayFormat: 'MM/DD/YYYY',
    		placeholder: 'Click to select a IV date',
    		fieldId: 'datePicker2',
    		maxDate: new Date(Date.now()),
    		useEmptyBarTitle: false
    	};
	showParam: any = {
		TreatmentId: false, IvfId: false, Date: false, PatientName: false,
		ivfRDBMS: false, DateFromTo: false, UserName: false, DateFromToUserName: false,
		ivfRDBMSWebsiteParse:false,ruledatasheet:false,sealantElig:false
	}

	constructor(public applicationService: ApplicationService, public router: Router, private datePipe: DatePipe, private route: ActivatedRoute) {
		// this.offices = this.route.snapshot.data['offsAndIVType'].data.offices;
		// this.ivformTypes=this.route.snapshot.data['offsAndIVType'].data.ivforms;
		// this.getAllusers();
		// if (this.route.snapshot.url[0].path == 'reportcl') {
		// 	this.hd1 = ClaimTreatmentTextModel.claimId;
		this.ur = "/reportcl";
		// } else {
			// this.hd1 = ClaimTreatmentTextModel.treatmentPlanId;
		// }
	}

	ngOnInit() {
		this.dateOptions.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.dateOptions1.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.dateOptions2.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.dateOptions3.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.reportDataInd = {
			"createdDate": "26/11/19 08:40:28",
			"status": null,
			"gradePay": null,
			"patDid": 196041,
			"pidDB": 19648,
			"passwordRE": null,
			"id": 196041,
			"basicInfo1": "Beaumont",
			"basicInfo2": "Duncan Conner",
			"basicInfo3": "Horizon BCBS of NJ",
			"basicInfo4": "463-812-538",
			"basicInfo5": "Duncan Conner",
			"basicInfo6": "1983-09-23",
			"basicInfo7": "800-433-6825",
			"basicInfo8": "MDB + TRACY",
			"basicInfo9": "1983-09-23",
			"basicInfo10": "Self",
			"basicInfo11": "Recall",
			"basicInfo12": "84063560",
			"basicInfo13": "",
			"basicInfo14": "076279-0000",
			"basicInfo15": "No Information",
			"basicInfo21": "7431",
			"basicInfo16": "3HZN44311320",
			"basicInfo17": "2021-11-15",
			"basicInfo18": "22099",
			"basicInfo19": "Geetika Rastogi",
			"basicInfo20": "PO Box 1311, Minneapolis, MN 55440.",
			"policy1": "PPO",
			"policy18": "",
			"policy19": "",
			"policy20": null,
			"policy17": "No",
			"policy2": "",
			"policy3": "IN",
			"policy4": "UniCare 100_(01/21)",
			"policy5": "2020-01-01",
			"policy6": "CY",
			"policy7": "1500",
			"policy8": "773.8",
			"policy9": "25",
			"policy10": "0",
			"policy11": "26",
			"policy12": "No",
			"policy13": "No",
			"policy14": "No",
			"policy15": "No",
			"policy16": "",
			"percentages1": "80",
			"percentages2": "Yes",
			"percentages3": "50",
			"percentages4": "Yes",
			"percentages5": "80",
			"percentages6": "Yes",
			"percentages7": "80",
			"percentages8": "Yes",
			"percentages9": "100",
			"percentages10": "100",
			"percentages11": "100",
			"percentages16": "100",
			"percentages12": "12 MONTHS",
			"prosthetics1": "No",
			"prosthetics2": "Yes",
			"prosthetics3": "Seat",
			"prosthetics4": null,
			"waitingPeriod1": "No",
			"waitingPeriod2": "No",
			"ssc1": "1xLT",
			"ssc2": "1x6Mo",
			"exams1": "1x6Mo_1D",
			"exams2": "1x6Mo_1D",
			"exams3": "1x6Mo_1D",
			"exams4": "1x36Mo",
			"xrays1": "1x6Mo_1D",
			"xrays2": "No Frequency",
			"xrays3": "No Frequency",
			"xrays4": "1x36Mo_1D",
			"xrays5": "Yes",
			"fluroide1": "1x6Mo_1D",
			"fluroide2": "19",
			"fluroide3": "1x6Mo_1D",
			"fluroide4": "19",
			"sealantsD": "100",
			"sealants1": "1x36Mo",
			"sealants2": "14",
			"sealants3": "No",
			"sealants4": "No",
			"sealants5": "YES",
			"prophy1": "1x6Mo",
			"prophy2": "1x6Mo",
			"rollage": "15",
			"perio1": "80",
			"perio2": "1x12Mo",
			"perio3": "2",
			"perio4": "1",
			"perioMnt1": "80",
			"perioMnt2": "1x6Mo_1D",
			"perioMnt3": "No",
			"fillings": "",
			"perioMnt4": "80",
			"perioMnt5": "1x36Mo",
			"perioMnt6": "100",
			"perioMnt7": "1x12Mo",
			"sedations1": "0",
			"sedations2": "0",
			"sedations3": "0",
			"extractions1": "80",
			"extractions2": "80",
			"oral1": "80",
			"oral2": "1x6Mo",
			"oral3": "Yes",
			"oral4": "1xLT",
			"oral5": "Yes",
			"oral6": "No Frequency",
			"dentures1": "1x60Mo",
			"dentures2": null,
			"dentures3": "1x60Mo",
			"dentures4": null,
			"dentures5": "Yes",
			"dentures6": "1x60Mo",
			"implants1": "0",
			"implants2": "0",
			"implants3": "0",
			"implants4": "50",
			"posterior1": "80",
			"posterior2": "1x6Mo",
			"posterior3": "No",
			"posterior4": "50",
			"posterior5": "1x60Mo",
			"posterior6": "No",
			"posterior7": "80",
			"posterior8": "100",
			"posterior9": "1x6Mo_1D",
			"posterior10": "50",
			"posterior11": "1x60Mo",
			"posterior12": "Yes",
			"ortho1": "50",
			"ortho2": "1500",
			"ortho3": "19",
			"ortho4": "No",
			"den5225": "50",
			"denf5225": "1x60Mo",
			"den5226": "50",
			"denf5226": "1x60Mo",
			"bridges1": "50",
			"bridges2": "1x60Mo",
			"cdowngrade": "No",
			"implants5": "0",
			"implants6": "0",
			"implants7": "1x60Mo",
			"implants8": "0",
			"ortho5": "1500",
			"waitingPeriod3": "No",
			"posterior17": "-",
			"percentages13": "No",
			"percentages14": "No",
			"percentages15": "No",
			"posterior18": "80",
			"posterior19": "1x24Mo",
			"posterior20": "1x24Mo",
			"fill1": "-",
			"extr1": "-",
			"crn1": "-",
			"waitingPeriod4": "No",
			"shareFr": "Yes",
			"pedo1": "100",
			"pedo2": "50",
			"pano1": "100",
			"pano2": "Yes",
			"d4381": "0",
			"ckD0120": "Yes",
			"ckD0140": "No",
			"ckD0145": "No",
			"ckD0150": "Yes",
			"ckD0160": "No",
			"ckD210": "Yes",
			"ckD220": "Yes",
			"ckD230": "Yes",
			"ckD330": "No",
			"ckD274": "Yes",
			"d0160Freq": "1xLT",
			"d2391Freq": "",
			"d0330Freq": "1x36Mo",
			"d4381Freq": "0",
			"d3330": "80",
			"d3330Freq": "1xLT",
			"freqD2934": "1xLT",
			"npi": null,
			"licence": null,
			"radio3": null,
			"radio4": null,
			"radio5": null,
			"radio1": null,
			"radio2": null,
			"corrdOfBenefits": null,
			"whatAmountD7210": null,
			"allowAmountD7240": null,
			"ivSedation": null,
			"d7210": null,
			"d7220": null,
			"d7230": null,
			"d7240": null,
			"d7250": null,
			"d7310": null,
			"d7311": null,
			"d7320": null,
			"d7321": null,
			"d7473": null,
			"d9239": null,
			"d4263": null,
			"d4264": null,
			"d6104": null,
			"d7953": null,
			"d3310": null,
			"d3320": null,
			"d33300": null,
			"d3346": null,
			"d3347": null,
			"d3348": null,
			"d6058": null,
			"d7951": null,
			"d4266": null,
			"d4267": null,
			"d4273": null,
			"d7251": null,
			"d7210fr": null,
			"d7220fr": null,
			"d7230fr": null,
			"d7240fr": null,
			"d7250fr": null,
			"d7310fr": null,
			"d7311fr": null,
			"d7320fr": null,
			"d7321fr": null,
			"d7473fr": null,
			"sedations1fr": null,
			"sedations3fr": null,
			"d9239fr": null,
			"sedations2fr": null,
			"d4263fr": null,
			"d4264fr": null,
			"d6104fr": null,
			"d7953fr": null,
			"d3310fr": null,
			"d3320fr": null,
			"d3346fr": null,
			"d3347fr": null,
			"d3348fr": null,
			"d6058fr": null,
			"oral1fr": null,
			"d7951fr": null,
			"d4266fr": null,
			"d4267fr": null,
			"perio1fr": null,
			"d4273fr": null,
			"d7251fr": null,
			"d7472": null,
			"d7472fr": null,
			"d7280": null,
			"d7280fr": null,
			"d7282": null,
			"d7282fr": null,
			"d7283": null,
			"d7283fr": null,
			"d7952": null,
			"d7952fr": null,
			"d7285": null,
			"d7285fr": null,
			"d6114": null,
			"d6114fr": null,
			"d5860": null,
			"d5860fr": null,
			"d5110": null,
			"d5110fr": null,
			"d5130": null,
			"d5130fr": null,
			"d0140": null,
			"sRemarks": null,
			"mPolicy": null,
			"mMIP": null,
			"esBcbs": null,
			"obtainMPN": "No",
			"ivFormTypeId": 1,
			"waitingPeriodDuration": null,
			"d0350": "0",
			"d1330": "0",
			"d2930": "50",
			"srpd4341": "Basic",
			"majord72101": "Basic",
			"fmxSubjectToDed": null,
			"d1510": null,
			"d1510Freq": null,
			"d1516": null,
			"d1516Freq": null,
			"d1517": null,
			"d1517Freq": null,
			"d3220": null,
			"d3220Freq": null,
			"outNetworkMessage": null,
			"osPlanType": null,
			"smAgeLimit": null,
			"perioD4921": null,
			"d4921Frequency": null,
			"perioD4266": null,
			"d4266Frequency": null,
			"perioD9910": null,
			"d9910Frequency": null,
			"oonbenfits": null,
			"pdfAlert": null,
			"d9630": null,
			"d9630fr": null,
			"d0431": null,
			"d0431fr": null,
			"d4999": null,
			"d4999fr": null,
			"d2962": null,
			"d2962fr": null,
			"historyCount": null,
			"comments": "History as per rep and DB.\r\n2930,2931 & 2934 - Only for age under 16 yrs.\r\nRep denied to give the Group-name and allowed amounts.\r\nPRED for downgrade of 6245. \r\nD0160 share freq. with other exams. \r\nAll Major services should be Pre-determinated. \r\n",
			"commentsRows": 248,
			"benefits": "Khushboo Gupta",
			"history": [
				"D0220======!!!!======BLANK======!!!!======BLANK======!!!!======2021-08-31",
				"D0230======!!!!======BLANK======!!!!======BLANK======!!!!======2021-08-31",
				"D0274======!!!!======BLANK======!!!!======BLANK======!!!!======2021-08-31",
				"D0150======!!!!======BLANK======!!!!======BLANK======!!!!======2021-08-31",
				"D1999======!!!!======BLANK======!!!!======BLANK======!!!!======2021-01-26",
				"D4342======!!!!======LL======!!!!======BLANK======!!!!======2021-01-26",
				"D0120======!!!!======BLANK======!!!!======BLANK======!!!!======2021-01-26",
				"D0274======!!!!======BLANK======!!!!======BLANK======!!!!======2021-01-26",
				"D4342======!!!!======LR======!!!!======BLANK======!!!!======2021-01-26",
				"D0230======!!!!======12======!!!!======BLANK======!!!!======2021-01-25",
				"D1999======!!!!======BLANK======!!!!======BLANK======!!!!======2021-01-25",
				"D4342======!!!!======UR======!!!!======BLANK======!!!!======2021-01-25",
				"D2392======!!!!======4======!!!!======BLANK======!!!!======2021-01-25",
				"D2392======!!!!======3======!!!!======BLANK======!!!!======2021-01-25",
				"D2391======!!!!======2======!!!!======BLANK======!!!!======2020-06-29",
				"D0230======!!!!======14======!!!!======BLANK======!!!!======2020-06-29",
				"D0274======!!!!======BLANK======!!!!======BLANK======!!!!======2020-06-29",
				"D2393======!!!!======29======!!!!======BLANK======!!!!======2020-06-29",
				"D1999======!!!!======BLANK======!!!!======BLANK======!!!!======2020-06-29",
				"D2391======!!!!======15======!!!!======BLANK======!!!!======2020-06-29",
				"D2392======!!!!======BLANK======!!!!======BLANK======!!!!======2020-06-29",
				"D1110======!!!!======BLANK======!!!!======BLANK======!!!!======2020-06-29",
				"D0120======!!!!======BLANK======!!!!======BLANK======!!!!======2020-06-29",
				"D2391======!!!!======3======!!!!======BLANK======!!!!======2020-06-29",
				"D2391======!!!!======4======!!!!======BLANK======!!!!======2020-06-29",
				"D0220======!!!!======8======!!!!======BLANK======!!!!======2020-06-29",
				"D7210======!!!!======5======!!!!======BLANK======!!!!======2019-11-22",
				"D7210======!!!!======13======!!!!======BLANK======!!!!======2019-11-22",
				"D7210======!!!!======17======!!!!======BLANK======!!!!======2019-11-22",
				"D2392======!!!!======3======!!!!======BLANK======!!!!======2019-11-22",
				"D4346======!!!!======BLANK======!!!!======BLANK======!!!!======2019-11-22",
				"D0150======!!!!======BLANK======!!!!======BLANK======!!!!======2019-11-20",
				"D0210======!!!!======BLANK======!!!!======BLANK======!!!!======2019-11-20"
			],
			"date": "2021-11-15",
			"hdto": null,
			"hdto1": null,
			"hdto2": null,
			"hdto3": null
		};

	}

	// reportParam(value:any,e:any) {
	// 	//console.log(e);
	// 	this.mainReportName=e.target.text;
	// 	this.report = new ReportModel();
	// 	let filter = this.showParam;
	// 	Object.keys(filter).forEach(function (key, result) {
	// 		if (key == value) {
	// 			filter[key] = true;
	// 		} else {
	// 			filter[key] = false;
	// 		}
	// 		return filter;
	// 	});
	// 	this.showReportForm = true;
	// 	this.report.reportType = value;
	// 	/*
	// 	 * setTimeout(function(){ if (document.getElementById("datePicker")){
	// 	 * 
	// 	 * document.getElementById("datePicker").readOnly=false; } }, 100);
	// 	 */
	// }

	// showCalendar() {

	// }

	// runReport() {
	// 	let rep = this.report;
	// 	this.rdata1=[];
	// 	this.rdata=[];
	// 	this.showLoadingPDFS= false;
	// 	let submit: boolean = false;
	// 	submit = rep.officeId && rep.reportField1;
	// 	if (this.report.reportType == 'sealantElig'){
	// 		if (rep.patientId == '' && rep.officeId == '') {
	// 			submit = false;
				
				
	// 		} else {
	// 			submit = true;
	// 			if (this.report.reportField1 !='')this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
	// 			if (this.report.reportField2 !='')this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
	// 			if (this.report.ivDate !='')this.report.ivDate = this.datePipe.transform(this.report.ivDate, 'MM/dd/yyyy');
	// 			if (!rep.officeId) {
	// 				submit = false;
	// 			}
	// 		}
	// 	}
	// 	if (this.report.reportType == 'ivfRDBMS') {
	// 		if (rep.reportField1 == '' && rep.employerName == '' && rep.generalDateRun == '' && rep.patientName == '' && rep.officeId == '') {
	// 			submit = false;

	// 		} else {
	// 			submit = true;
	// 			if (!rep.officeId) {
	// 				submit = false;
	// 			}
	// 		}

	// 	}
	// 	if (this.report.reportType == 'ivfRDBMSWebsiteParse') {
	// 		if (rep.reportField1 == '' && rep.employerName == '' && rep.patientName == '' && rep.officeId == '') {
	// 			submit = false;

	// 		} else {
	// 			submit = true;
	// 			if (!rep.officeId) {
	// 				submit = false;
	// 			}
	// 		}

	// 	}
	// 	if (this.report.reportType == 'UserName') {
	// 		if (rep.employerName == '' || rep.officeId == '') {
	// 			submit = false;
	// 		} else {
	// 			submit = true;
	// 		}
	// 	}
	// 	if (this.report.reportType == 'DateFromTo') {
	// 		if (rep.reportField1 == '' || rep.reportField2 == '' || rep.officeId == '') {
	// 			submit = false;

	// 		} else {
	// 			this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
	// 			this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
	// 			submit = true;
	// 		}
	// 	}
	// 	if (this.report.reportType == 'ruledatasheet') {
	// 		if (rep.sheetTabId == '') {
	// 			submit = false;

	// 		} else {
	// 			//this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
	// 			//this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
	// 			submit = true;
	// 		}
	// 	}
    //     //console.log("submit",submit);
	// 	if (this.report.reportType == 'DateFromToUserName') {
	// 		if (rep.employerName == '' || rep.reportField1 == '' || rep.reportField2 == '' || rep.officeId == '') {
	// 			submit = false;

	// 		} else {
	// 			this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
	// 			this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
	// 			submit = true;
	// 		}
	// 	}

	// 	if (submit) {
	// 		if (this.showParam.Date) {
	// 			this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
	// 		}
	// 		this.showLoading = true;
	// 		this.applicationService.validateReport(this.report, this.ur, (result:any) => {
	// 			this.showLoading = false;
	// 			if (result.status === 'OK') {
	// 				this.reportData = result.data;
					
	// 				if (this.report.reportType === 'ruledatasheet' ){
						
	// 					this.showDetailsData=false;
	// 					this.showReportData = true;
	// 					return ;
	// 				}
					
    //                if (this.report.reportType === 'ivfRDBMS' || this.report.reportType === 'ivfRDBMSWebsiteParse') {
    //             	   if (this.report.reportType === 'ivfRDBMSWebsiteParse'){
    //             		   this.reportData= this.convertLocalTime(this.reportData);
    //                    }
	// 					this.arrayOfKeys = this.reportData;
	// 					//console.log("v",this.arrayOfKeys);
	// 				} else if (this.report.reportType === 'sealantElig'){
	// 					this.parseData(result.data);

	// 				} else {
	// 					this.arrayOfKeys = Object.keys(this.reportData);

	// 				}
	// 				this.showReportData = true;
	// 				if (this.isEmpty(this.reportData)) {
	// 					alert("No Data Found.");
	// 					this.showReportData = false;
	// 				} else {

	// 				}
	// 			} else {
	// 				this.showReportData = false;
	// 			}
	// 		});
	// 	}
	// }

	// isEmpty(obj:any) {
	// 	for (var key in obj) {
	// 		if (obj.hasOwnProperty(key))
	// 			return false;
	// 	}
	// 	return true;
	// }

	// fixMessage(){
	// 	let tabs:any= document.getElementsByClassName("table");
	//     for(var tab of tabs){
	// 	//let tab:any = document.getElementById(name);
	// 	let ro =tab.rows;
	// 	for(var r of ro){
	// 		var cells=r.cells;
	// 		for(var cell of cells){
	// 			//console.log(cell.innerHTML);
	// 			if (cell.innerHTML.indexOf("MAND.DAT.MISS")>-1  || 
	// 					cell.innerHTML.indexOf("NOTFOUND")>-1  || 
	// 					cell.innerHTML.indexOf("ISS.FETCH")>-1  ||
	// 					cell.innerHTML.indexOf("CODE_ISSUE")>-1  ||
	// 					cell.innerHTML.indexOf("MAIN_CON_MET")>-1  
						
	// 					)
	// 				cell.classList.add("colorred");
	// 		}
	// 	}
	// }
	// }
	
	// showDetailsDataF(data:any) {
	// 	let ths = this;
	// 	ths.showDetailsData = true;
	// 	setTimeout(() => {
	// 		ths.fixMessage();
	// 	}, 2000);

	// }

	// downloadPDF(data:any) {


	// 	this.applicationService.downloadIVFPDF({ "reportField1": data.patDid, "officeId": this.report.officeId,"ivformTypeId":data.ivFormTypeId },"/genereatePdf", (result:any) => {
	// 		this.showLoading = false;
	// 		if (result.status == '200') {
	// 			//const filename = result.headers.get('filename');
	// 			//console.log(result);
	// 			// this.downloadFile(result.body);
	// 			this.saveBolbData(result.body, data.basicInfo2 + ".pdf");
	// 		}
	// 	});
	// }

	// downloadFile(data: Response) {

	// 	const blob = new Blob([<any>data], { type: 'application/pdf' });
	// 	const url: string = window.URL.createObjectURL(blob);
	// 	window.open(url);
	// }


	// saveBolbData(data: Response, fileName: string) {
	// 	const a: any = document.createElement('a');
	// 	document.body.appendChild(a);
	// 	a.style = 'display: none';
	// 	const blob = new Blob([<any>data], { type: 'application/pdf' }),
	// 		url = window.URL.createObjectURL(blob);
	// 	a.href = url;
	// 	a.download = fileName;
	// 	a.click();
	// 	window.URL.revokeObjectURL(url);

	// }

	getAllusers() {
		let ths = this;
		ths.applicationService.getAllUserNames((result:any) => {
			if (result.status == 'OK') {

				ths.users = result.data
			}
		})
	}
	
	// convertLocalTime(rpdata:any){
	// 	//console.log(rpdata);
	// 	if (rpdata){
	// 	rpdata.forEach(function (value:any) {  
	// 		  //console.log(value.createdDate);
	// 		  if (value.createdDate!=''){
	// 			  let a =value.createdDate.split(" ");
	// 			  let a0=a[0].split("/");
	// 			  let dt:any=new Date(a0[1]+"/"+a0[0]+'/'+a0[2]+" "+a[1]+" UTC");
	// 			  //console.log(dt);
	// 			  value.createdDate=dt.getDate()+"/"+(dt.getMonth()+1)+"/"+(dt.getYear()+1900)+" "+dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds();
	// 		  }
	// 		}); 
	//  }
	// 	return rpdata;
	// }
	
	
parseData(d:any){
		  let ths=this;
		  let rep = ths.report;
		  let p=rep.patientId.split(",");
		  p.forEach((j:any,v:any)=>{
			  let o:any={};
			  let o1:any={};
			  //o['o']=oName;
			  //o['pid']=j.patientId;
			  //o1['o']=oName;
			  //o1['pid']=j.patientId;
			  o1['mess']="";
			 // console.log(d[j]);
			  if (typeof d[j]!='undefined'){
			  d[j].forEach((y:any,z:any)=>{
				if (y.rule_id==69){
					o['o']=y.office_name;
					o['pid']=j.patient_id;
					o['pn']=y.name;
					o['ivDone']=y.iv_date;
					o['te']=y.error_message;
				}
				if (y.rule_id==70){
					o['o']=y.office_name;
					o['pn']=y.patient_name;
					o['ivDone']=y.iv_date;
					o['pid']=j.patient_id;
					o['tne']=y.error_message;
				}
				if (y.rule_id==71){
					o['o']=y.office_name;
					o['pn']=y.patient_name;
					o['pid']=j.patient_id;
					o['ivDone']=y.iv_date;
					o['tnea']=y.error_message;
				}
				if (y.rule_id==72){
					o['o']=y.office_name;
					o['pn']=y.patient_name;
					o['pid']=j.patient_id;
					o['ivDone']=y.iv_date;
					o['tnef']=y.error_message;
				}
				if (y.rule_id==73){
					o1['o']=y.office_name;
					o1['pid']=j.patient_id;
					o1['mess']=y.error_message;
				}
				if (y.rule_id==68){
					o1['o']=y.office_name;
					o1['mess']=o1['mess']+y.error_message+"</br>";
					o1['pn']=y.patient_name;
					o1['pid']=j.patient_id;
					o['ivDone']=y.iv_date;
					
				}
			});
		  }else{
			 // ths.rdata.push({"o":'Data not found'})
		  }
			  if (typeof o['pn']!='undefined')ths.rdata.push(o);
			  ths.rdata1.push(o1);
		  });
		  
	  }
	  
// downloadPdfSeal(){
		  
// 		  if (this.showLoadingPDFS) return; 
// 		  let ths=this;
// 		  let rep = ths.report;
// 		  //ths.displayResultV=true;
// 		  let p=rep.patientId.split(",");
// 		  let x="";
// 		  let cm="";
// 		   for (let dt of p){
// 			   x=x+cm+dt.trim();
// 			   cm=",";
// 		   }
// 		  if (cm=="") return;
// 		  this.showLoadingPDFS= true; 
// 		  this.applicationService.downloadIVFPDF({"officeId":this.report.officeId,"patientId":x},"/genereatePdfSealant", (result:any) => {
// 				this.showLoading = false;
// 				this.showLoadingPDFS= false; 
// 				if (result.status == '200') {
// 					//const filename = result.headers.get('filename');
// 					//console.log(result);
// 					// this.downloadFile(result.body);
// 					this.saveBolbData(result.body, "selant" + ".pdf");
// 				}
// 			});
// 		}
	  
	 


}
