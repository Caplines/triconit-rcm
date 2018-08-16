import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {DatepickerOptions} from 'ng2-datepicker';
import {Office} from "../../model/model.office";
import {ReportModel} from "../../model/model.report";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ReportComponent implements OnInit {
  report: ReportModel = new ReportModel();
  errorMessage: string;
  offices:any;
  reportData: any;
  arrayOfKeys: any;
  showLoading: boolean = false;
  showReportForm: boolean = false;
  showReportData: boolean = false;
  dateOptions: DatepickerOptions = {
	displayFormat: 'MM/DD/YYYY',
	placeholder: 'Click to select a date',
  };
  showParam:any = {TreatmentId: false, IvfId: false, Date: false, PatientName: false}
  
  constructor(public accountService: AccountService, public router: Router) {
	this.accountService.getOffices((result) => {
      this.offices=result;
    });
  }

  ngOnInit() {
	
  }

  reportParam(value) {
    this.report = new ReportModel();
	let filter = this.showParam;
	Object.keys(filter).forEach(function(key, result) {
	  if(key == value) {
	  	filter[key] = true;
	  } else {
	  	filter[key] = false;
	  }
	  return filter;
	});		
	this.showReportForm = true;
	this.report.reportType = value;
  }
  
  showCalendar(){
  
  }
  
  runReport() {
	this.showLoading = true;
	if(this.report.officeId && this.report.reportField1) {
		this.accountService.validateReport(this.report,(result) => {
			this.showLoading = false;
			if (result.status=='OK'){
				this.reportData = result.data;
				this.arrayOfKeys = Object.keys(this.reportData);
				this.showReportData = true;
			}
		});
	}else{
		this.showLoading = false;
	}
	console.log(this.arrayOfKeys);
	console.log(this.reportData);
  }


}
