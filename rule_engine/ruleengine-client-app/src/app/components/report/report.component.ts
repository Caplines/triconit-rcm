import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { DatepickerOptions } from 'ng2-datepicker';
import { ReportModel } from '../../model/model.report';
import { ApplicationService } from '../../services/application.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ClaimTreatmentTextModel } from '../../model/model.claimtreatmenttext';

@Component({
	selector: 'app-report',
	templateUrl: './report.component.html',
	styleUrls: ['./report.component.css'],
	encapsulation: ViewEncapsulation.None,
	providers: [DatePipe]
})
export class ReportComponent implements OnInit {
	report: ReportModel = new ReportModel();
	errorMessage: string;
    mainReportName:string;
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
    showLoadingMoreDate:boolean = false;
    oldSubscriptions:any=[];
	reportDataInd: any;
    rdata1=[];
    rdata=[];
    
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
		ivfRDBMSWebsiteParse:false,ruledatasheet:false,sealantElig:false,
		Teamwise: false
	}

	constructor(public applicationService: ApplicationService, public router: Router, private datePipe: DatePipe, private route: ActivatedRoute) {
		this.offices = this.route.snapshot.data['offsAndIVType'].data.offices;
		this.ivformTypes=this.route.snapshot.data['offsAndIVType'].data.ivforms;
		this.getAllusers();
		if (this.route.snapshot.url[0].path == 'reportcl') {
			this.hd1 = ClaimTreatmentTextModel.claimId;
			this.ur = "/reportcl";
		} else {
			this.hd1 = ClaimTreatmentTextModel.treatmentPlanId;
		}
	}

	ngOnInit() {
		this.dateOptions.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.dateOptions1.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.dateOptions2.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.dateOptions3.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
	}

	reportParam(value,e) {
		//console.log(e);
		this.mainReportName=e.target.text;
		this.report = new ReportModel();
		let filter = this.showParam;
		Object.keys(filter).forEach(function (key, result) {
			if (key == value) {
				filter[key] = true;
			} else {
				filter[key] = false;
			}
			return filter;
		});
		this.showReportForm = true;
		this.report.reportType = value;
		/*
		 * setTimeout(function(){ if (document.getElementById("datePicker")){
		 * 
		 * document.getElementById("datePicker").readOnly=false; } }, 100);
		 */
	}

	showCalendar() {

	}

	runReport() {
		let rep = this.report;
		this.reportData={};
		
		this.rdata1=[];
		this.rdata=[];
		this.showLoadingPDFS= false;
		let submit: boolean = false;
		submit = rep.officeId && rep.reportField1;
		if (this.report.reportType == 'sealantElig'){
			if (rep.patientId == '' && rep.officeId == '') {
				submit = false;
				
				
			} else {
				submit = true;
				if (this.report.reportField1 !='')this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
				if (this.report.reportField2 !='')this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
				if (this.report.ivDate !='')this.report.ivDate = this.datePipe.transform(this.report.ivDate, 'MM/dd/yyyy');
				if (!rep.officeId) {
					submit = false;
				}
			}
		}
		if (this.report.reportType == 'ivfRDBMS') {
			if (rep.reportField1 == '' && rep.employerName == '' && rep.generalDateRun == '' && rep.patientName == '' && rep.officeId == '') {
				submit = false;

			} else {
				submit = true;
				if (!rep.officeId) {
					submit = false;
				}
			}

		}
		if (this.report.reportType == 'ivfRDBMSWebsiteParse') {
			if (rep.reportField1 == '' && rep.employerName == '' && rep.patientName == '' && rep.officeId == '') {
				submit = false;

			} else {
				submit = true;
				if (!rep.officeId) {
					submit = false;
				}
			}

		}
		if (this.report.reportType == 'UserName') {
			if (rep.employerName == '' || rep.officeId == '') {
				submit = false;
			} else {
				submit = true;
			}
		}
		if (this.report.reportType == 'DateFromTo') {
			if (rep.reportField1 == '' || rep.reportField2 == '' || rep.officeId == '') {
				submit = false;

			} else {
				this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
				this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
				submit = true;
			}
		}
		if (this.report.reportType == 'ruledatasheet') {
			if (rep.sheetTabId == '') {
				submit = false;

			} else {
				//this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
				//this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
				submit = true;
			}
		}
        //console.log("submit",submit);
		if (this.report.reportType == 'DateFromToUserName') {
			if (rep.employerName == '' || rep.reportField1 == '' || rep.reportField2 == '' || rep.officeId == '') {
				submit = false;

			} else {
				this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
				this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
				submit = true;
			}
		}
		if (this.report.reportType == 'Teamwise') {
			if (this.dateRange(15)){
				submit = false;
			}
			else if (rep.reportField1 == '' || rep.reportField2 == '') {
				submit = false;

			} else {
				this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
				this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
				submit = true;
			}
		}
		this.arrayOfKeys=[];
		if (submit) {
			if (this.showParam.Date) {
				this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
			}
			this.showLoading = true;
			this.applicationService.validateReport(this.report, this.ur, (result) => {
				this.showLoading = false;
				let ths=this;
				if (result.status === 'OK') {
					this.reportData = result.data;
					if (this.report.reportType === 'ruledatasheet' ){
						
						this.showDetailsData=false;
						this.showReportData = true;
						return ;
					}
					
                   if (this.report.reportType === 'ivfRDBMS' || this.report.reportType === 'ivfRDBMSWebsiteParse') {
                	   if (this.report.reportType === 'ivfRDBMSWebsiteParse'){
                		   this.reportData= this.convertLocalTime(this.reportData);
                       }
						this.arrayOfKeys.push(this.reportData);
						//console.log("v",this.arrayOfKeys);
					} else if (this.report.reportType === 'sealantElig'){
						this.parseData(result.data);

					} else {
						this.arrayOfKeys = Object.keys(this.reportData);

					}
					
					
					
					if (this.report.reportType == 'DateFromTo' || this.report.reportType == 'DateFromToUserName'){
						let date1:Date = new Date(this.report.reportField1);
						let date2:Date = new Date(this.report.reportField2);
						let diffTime: number = Math.abs(date2.getTime() - date1.getTime());
						let diffDays: number = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
						if (!this.isEmpty(this.reportData)){
							this.showReportData = true;
							this.showLoading = false;
						}else{
							this.showLoading = true;
							this.showReportData = false;
						}
						if(diffDays>0){
							this.showLoadingMoreDate=true;
							this.loadMoreforMutipleDates(diffDays,this.report.reportField1);
						}else{
							this.showLoading = false;
							this.showReportData = true;
						}
					}else {
						this.showReportData = true;
						if (this.isEmpty(this.reportData)) {
							alert("No Data Found.");
							this.showReportData = false;
						} else {

						}
						
					}
				} else {
					this.showReportData = false;
				}
			});
		}
	}

	 loadMoreforMutipleDates(n:number,oldD:any){
		 let ths=this;
		 //ths.showLoadingMoreDate=true;
		 if (n>0){
			 let date1 = new Date(ths.report.reportField1);
			 let date3 = date1.setDate(date1.getDate()+1);
			   let mm= new Date(date3);
			   console.log(mm.getDate());
			   ths.report.reportField1=(mm.getMonth()+1)+"/"+mm.getDate()+"/"+mm.getFullYear();
			   console.log("this.showLoadingMoreDate",this.showLoadingMoreDate);
			   if (!this.showLoadingMoreDate) {
				   return;//in case back is pressed.
			   }
			   let old=	ths.applicationService.validateReport(ths.report, ths.ur, (result) => {
				
		        ths.reportData ={...ths.reportData, ...result.data};
		        if (!this.isEmpty(this.reportData)){
					this.showReportData = true;
					this.showLoading = false;
				}else{
					this.showLoading = true;
					this.showReportData = false;
				}
		        ths.arrayOfKeys = Object.keys(ths.reportData);
				ths.loadMoreforMutipleDates(n-1,oldD);
			});
			  
		 }else{
			 ths.report.reportField1=oldD;
			 ths.showLoadingMoreDate=false;
			 if (!this.isEmpty(this.reportData)){
					this.showReportData = true;
					this.showLoading = false;
				}else{
					this.showLoading = true;
					this.showReportData = false;
				}
			 if (this.isEmpty(this.reportData)) {
					alert("No Data Found.");
					this.showReportData = false;
					this.showLoading = false;
				} else {

			}
		 }
	}
	 
	isEmpty(obj) {
		for (var key in obj) {
			if (obj.hasOwnProperty(key))
				return false;
		}
		return true;
	}

	fixMessage(){
		let tabs:any= document.getElementsByClassName("table");
	    for(var tab of tabs){
		//let tab:any = document.getElementById(name);
		let ro =tab.rows;
		for(var r of ro){
			var cells=r.cells;
			for(var cell of cells){
				//console.log(cell.innerHTML);
				if (cell.innerHTML.indexOf("MAND.DAT.MISS")>-1  || 
						cell.innerHTML.indexOf("NOTFOUND")>-1  || 
						cell.innerHTML.indexOf("ISS.FETCH")>-1  ||
						cell.innerHTML.indexOf("CODE_ISSUE")>-1  ||
						cell.innerHTML.indexOf("MAIN_CON_MET")>-1  
						
						)
					cell.classList.add("colorred");
			}
		}
	}
	}
	
	showDetailsDataF(data) {
		let ths = this;
		ths.showDetailsData = true;
		ths.reportDataInd = data;
		setTimeout(() => {
			ths.fixMessage();
		}, 2000);

	}

	downloadPDF(data) {


		this.applicationService.downloadIVFPDF({ "reportField1": data.patDid, "officeId": this.report.officeId,"ivformTypeId":data.ivFormTypeId },"/genereatePdf", (result) => {
			this.showLoading = false;
			if (result.status == '200') {
				//const filename = result.headers.get('filename');
				//console.log(result);
				// this.downloadFile(result.body);
				this.saveBolbData(result.body, data.basicInfo2 + ".pdf");
			}
		});
	}

	downloadFile(data: Response) {

		const blob = new Blob([<any>data], { type: 'application/pdf' });
		const url: string = window.URL.createObjectURL(blob);
		window.open(url);
	}


	saveBolbData(data: Response, fileName: string) {
		const a: any = document.createElement('a');
		document.body.appendChild(a);
		a.style = 'display: none';
		const blob = new Blob([<any>data], { type: 'application/pdf' }),
			url = window.URL.createObjectURL(blob);
		a.href = url;
		a.download = fileName;
		a.click();
		window.URL.revokeObjectURL(url);

	}

	getAllusers() {
		let ths = this;
		ths.applicationService.getAllUserNames((result) => {
			if (result.status == 'OK') {

				ths.users = result.data
			}
		})
	}
	
	convertLocalTime(rpdata:any){
		//console.log(rpdata);
		if (rpdata){
		rpdata.forEach(function (value) {  
			  //console.log(value.createdDate);
			  if (value.createdDate!=''){
				  let a =value.createdDate.split(" ");
				  let a0=a[0].split("/");
				  let dt:any=new Date(a0[1]+"/"+a0[0]+'/'+a0[2]+" "+a[1]+" UTC");
				  //console.log(dt);
				  value.createdDate=dt.getDate()+"/"+(dt.getMonth()+1)+"/"+(dt.getYear()+1900)+" "+dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds();
			  }
			}); 
	 }
		return rpdata;
	}
	
	
parseData(d){
		  let ths=this;
		  let rep = ths.report;
		  let p=rep.patientId.split(",");
		  p.forEach((j,v)=>{
			  let o={};
			  let o1={};
			  //o['o']=oName;
			  //o['pid']=j.patientId;
			  //o1['o']=oName;
			  //o1['pid']=j.patientId;
			  o1['mess']="";
			 // console.log(d[j]);
			  if (typeof d[j]!='undefined'){
			  d[j].forEach((y,z)=>{
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
	  
downloadPdfSeal(){
		  
		  if (this.showLoadingPDFS) return; 
		  let ths=this;
		  let rep = ths.report;
		  //ths.displayResultV=true;
		  let p=rep.patientId.split(",");
		  let x="";
		  let cm="";
		   for (let dt of p){
			   x=x+cm+dt.trim();
			   cm=",";
		   }
		  if (cm=="") return;
		  this.showLoadingPDFS= true; 
		  this.applicationService.downloadIVFPDF({"officeId":this.report.officeId,"patientId":x},"/genereatePdfSealant", (result) => {
				this.showLoading = false;
				this.showLoadingPDFS= false; 
				if (result.status == '200') {
					//const filename = result.headers.get('filename');
					//console.log(result);
					// this.downloadFile(result.body);
					this.saveBolbData(result.body, "selant" + ".pdf");
				}
			});
		}
	  
   goBack(){
	this.showReportData=false;
	this.showLoadingMoreDate=false;
	this.reportData={};
	
   }

   dateRange(range:number){
	   let ths= this;
	   if (ths.report.reportField1== '' || ths.report.reportField2 ==''){
		   return true;
	   }
	   let date1:any = new Date(ths.report.reportField1);
	   let date2:any = new Date(ths.report.reportField2);
	   const diffTime:any = Math.abs(date2 - date1);
	   const diffDays:any = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
	   //console.log(diffTime + " milliseconds");
	   //console.log(diffDays + " days");
	   //console.log(date1);
	   if (diffDays>range) return true;
	   return false;
   }
   
   downloadTeamWiseCSV(){
	   this.showLoading = true;
	   this.applicationService.downloadExcelTeamData({ "data":  this.reportData, "d1": this.report.reportField1,"d2":this.report.reportField2 },"/generateTeamwiseExcel", (result) => {
			this.showLoading = false;
			if (result.status == '200') {
				//const filename = result.headers.get('filename');
				//console.log(result);
				// this.downloadFile(result.body);
				this.saveBolbData(result.body,  "TeamwiseReport.xlsx");
			}
		});
   }


}
