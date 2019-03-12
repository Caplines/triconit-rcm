import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";
import {EnReportsModel} from "../../model/model.enreports";
import {DatepickerOptions} from 'ng2-datepicker';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-enreports',
  templateUrl: './enreports.component.html',
  styleUrls: ['./enreports.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class EnReportsComponent implements OnInit {
  enreports: EnReportsModel = new EnReportsModel();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
  
  reportData: any;
  arrayOfKeys: any;
  showLoading: boolean = false;
  showReportForm: boolean = false;
  showReportData: boolean = false;
  dateOptions: DatepickerOptions = {
	displayFormat: 'MM/DD/YYYY',
	placeholder: 'Click to select a date'
  };
  showParam:any = {IvBatch: false, TxPlan: false}
  
  
 
  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute, private datePipe: DatePipe) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.offices.push({"name":"All OFFICES","uuid":"All offices"});
  }

  ngOnInit() {
	this.dateOptions.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
  }
  
  reportParam(value) {
    this.enreports = new EnReportsModel();
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
	this.enreports.reportType = value;
  }
  
}
