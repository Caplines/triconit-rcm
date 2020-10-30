import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {RuleReportModel} from "../../model/model.rulereport";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
import { DatepickerOptions } from 'ng2-datepicker';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-rule-report',
  templateUrl: './rule_report.component.html',
  styleUrls: ['./rule_report.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class RuleReportComponent implements OnInit {
	
offices:any;
showReportData:any=false;
showLoadingPA:any=false;
rname:any="";
ur: string = "/rulereportdata";
reportData:any;
reportModel: RuleReportModel = new RuleReportModel();

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



constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute
		, private datePipe: DatePipe) {
	  
	this.offices =this.route.snapshot.data['offs'].data;
  }
  
	ngOnInit() {
		
		this.dateOptions1.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
		this.dateOptions2.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
  }
	
	runReport(){
		 let ths=this;
		 ths.showReportData=false;
		 ths.showLoadingPA=false;
		 if (ths.reportModel.startDate=='' || ths.reportModel.endDate==''){
			 return;
		 }
		 
	    ths.rname="MVP Rule Result";	
		//From service
		 ths.showLoadingPA=true;
		 ths.reportModel.startDate = ths.datePipe.transform(ths.reportModel.startDate, 'MM/dd/yyyy');
		 ths.reportModel.endDate = ths.datePipe.transform(ths.reportModel.endDate, 'MM/dd/yyyy');
		 ths.applicationService.callPostAPI(this.reportModel, ths.ur, (result) => {
			 ths.showReportData=true;
				if (result.status === 'OK') {
					ths.reportData = result.data;
				}
					
			});
	}
	
	goBack(){
		 this.showReportData=this.showLoadingPA=false;
		 this.reportData=[];
			
	}
}
