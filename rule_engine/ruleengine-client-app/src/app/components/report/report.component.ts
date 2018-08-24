import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {DatepickerOptions} from 'ng2-datepicker';
import {Office} from "../../model/model.office";
import {ReportModel} from "../../model/model.report";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";
import { DatePipe } from '@angular/common';

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
  offices:any;
  reportData: any;
  arrayOfKeys: any;
  showLoading: boolean = false;
  showReportForm: boolean = false;
  showReportData: boolean = false;
  dateOptions: DatepickerOptions = {
	displayFormat: 'MM/DD/YYYY',
	placeholder: 'Click to select a date'
  };
  showParam:any = {TreatmentId: false, IvfId: false, Date: false, PatientName: false}
  
  constructor(public accountService: AccountService, public router: Router, private datePipe: DatePipe) {
	this.accountService.getOffices((result) => {
      this.offices=result;
    });
  }

  ngOnInit() {
	this.dateOptions.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
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
	if(this.report.officeId && this.report.reportField1) {
		if(this.showParam.Date) {
			this.report.reportField1 = this.datePipe.transform(this.report.reportField1, 'MM/dd/yyyy');
		}
		this.showLoading = true;
		this.accountService.validateReport(this.report,(result) => {
			this.showLoading = false;
			if (result.status=='OK'){
				this.reportData = result.data;
				this.arrayOfKeys = Object.keys(this.reportData);
				this.showReportData = true;
				if (this.isEmpty(this.reportData)){
					alert("No Data Found.");
					this.showReportData = false;
				}else{
					
				}
			} else {
				this.showReportData = false;
			}
		});
	}
  }
	
  isEmpty(obj) {
    for(var key in obj) {
      if(obj.hasOwnProperty(key))
        return false;
    }
    return true;
  }

}
