import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { DatepickerOptions } from 'ng2-datepicker';
import { ReportModel } from '../../model/model.report';
import { AccountService } from '../../services/account.service';
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
	offices: any;
	users: any;
	reportData: any;
	arrayOfKeys: any;
	showLoading: boolean = false;
	showReportForm: boolean = false;
	showReportData: boolean = false;
	showDetailsData: boolean = false;
	reportDataInd: any;
	// claim and Treatment Text
	hd1: string = "";
	// hd2:string="";
	ur: string = "/report";
	dateOptions: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a date',
		fieldId: 'datePicker',
	};
	dateOptions1: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a From date',
		fieldId: 'datePicker1',
	};
	dateOptions2: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a To date',
		fieldId: 'datePicker2',
	};
	showParam: any = {
		TreatmentId: false, IvfId: false, Date: false, PatientName: false,
		ivfRDBMS: false, DateFromTo: false, UserName: false, DateFromToUserName: false
	}

	constructor(public accountService: AccountService, public router: Router, private datePipe: DatePipe, private route: ActivatedRoute) {
		this.offices = this.route.snapshot.data['offs'].data;
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
	}

	reportParam(value) {
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
		let submit: boolean = false;
		submit = rep.officeId && rep.reportField1;
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
		if (this.report.reportType == 'DateFromToUserName') {
			if (rep.employerName == '' || rep.reportField1 == '' || rep.reportField2 == '' || rep.officeId == '') {
				submit = false;

			} else {
				this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
				this.report.reportField2 = this.datePipe.transform(this.report.reportField2, 'MM/dd/yyyy');
				submit = true;
			}
		}

		if (submit) {
			if (this.showParam.Date) {
				this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
			}
			this.showLoading = true;
			this.accountService.validateReport(this.report, this.ur, (result) => {
				this.showLoading = false;
				if (result.status === 'OK') {
					this.reportData = result.data;

					if (this.report.reportType === 'ivfRDBMS') {
						this.arrayOfKeys = this.reportData;
						//console.log("v",this.arrayOfKeys);
					} else {
						this.arrayOfKeys = Object.keys(this.reportData);

					}
					this.showReportData = true;
					if (this.isEmpty(this.reportData)) {
						alert("No Data Found.");
						this.showReportData = false;
					} else {

					}
				} else {
					this.showReportData = false;
				}
			});
		}
	}

	isEmpty(obj) {
		for (var key in obj) {
			if (obj.hasOwnProperty(key))
				return false;
		}
		return true;
	}

	showDetailsDataF(data) {
		let ths = this;
		ths.showDetailsData = true;
		ths.reportDataInd = data;

	}

	downloadPDF(data) {


		this.accountService.downloadIVFPDF({ "reportField1": data.patDid, "officeId": this.report.officeId }, (result) => {
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
		ths.accountService.getAllUserNames((result) => {
			if (result.status == 'OK') {

				ths.users = result.data
			}
		})
	}

}
