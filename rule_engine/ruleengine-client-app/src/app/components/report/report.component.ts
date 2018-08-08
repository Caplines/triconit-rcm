import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {DatepickerOptions} from 'ng2-datepicker';
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";
import {Report} from "../../model/model.report";

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ReportComponent implements OnInit {
  report: Report = new Report();
  errorMessage: string;
  showReportForm: boolean = false;
  reportParamId: boolean = false;
  reportParamDate: boolean = false;
  reportParamName: boolean = false;
	dateOptions: DatepickerOptions = {
    displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a date',
  };
  
  
  constructor(public accountService: AccountService, public router: Router) {
   //this.report.date=new Date();
  }

  ngOnInit() {
	
  }

  reportParam(value) {
	this.showReportForm = true;
	if(value == 'id') {
		this.reportParamId = true;
		this.reportParamDate = false;
		this.reportParamName = false;
	}
	if(value == 'date') {
		this.reportParamDate = true;
		this.reportParamId = false;
		this.reportParamName = false;
		
	}
	if(value == 'name') {
		this.reportParamName = true;
		this.reportParamId = false;
		this.reportParamDate = false;
	}
  }
  
  showCalendar(){
  
  }


}
