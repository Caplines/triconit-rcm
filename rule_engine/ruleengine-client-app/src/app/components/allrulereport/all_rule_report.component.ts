import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {AllRuleReportModel} from "../../model/model.allrulereport";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
import { DatepickerOptions } from 'ng2-datepicker';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-all-rule-report',
  templateUrl: './all_rule_report.component.html',
  styleUrls: ['./all_rule_report.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class AllRuleReportComponent implements OnInit {
	
offices:any;
showReportData:any=false;
showLoadingPA:any=false;
rname:any="";
ur: string = "/rulereportdataAllMess";
reportData:any;
reportModel: AllRuleReportModel = new AllRuleReportModel();

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

    rulesArray:Array<Object> = [
        {id: 21, name: "Frequency Limitation"},
        {id: 8, name: "Age Limitation"},
        {id: 39, name: "Age Limitation Prophylaxis"},
        {id: 59, name: "BWX Age Limitation"},
        {id: 60, name: "Exams Age Limitation"}
    ];

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
		if (ths.reportModel.startDate=='' || ths.reportModel.endDate=='' || ths.reportModel.officeId=='' ){
			 return;
		 }
		 
		 let al: Array<any>= ths.rulesArray
         .filter((al: any) => al.id === ths.reportModel.ruleId);
		 
	    ths.rname=al[0].name;
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
