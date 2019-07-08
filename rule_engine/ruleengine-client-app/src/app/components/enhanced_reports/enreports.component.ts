import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";
import {EnReportsModel} from "../../model/model.enreports";
import {DatepickerOptions} from 'ng2-datepicker';
import { DatePipe } from '@angular/common';
import {ClaimTreatmentTextModel} from "../../model/model.claimtreatmenttext"; 
import Utils from '../../util/utils';


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
  
  enreportData: any;
  showLoading: boolean = false;
  showReportForm: boolean = false;
  showEnReportPopup: boolean = false;
//claim and Treatment Text 
hd1:string="";
//hd2:string="";
reportType:any;
ur:string="/enreport";
ut:string="1";
  dateOptionsS: DatepickerOptions = {
	displayFormat: 'MM/DD/YYYY',
	placeholder: 'Click to select a Start date'
  };
 dateOptionsE: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a End date'
	  };
  showParam:any = { TxPlan : false, IvBatch : false, IvBatchNumber : false, TxPlanNumber : false }
  typeMap: any = {
        'TxPlan': 1,
        'IvBatch': 2,
        'IvBatchNumber': 4,
        'TxPlanNumber': 3,
    };


  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute, private datePipe: DatePipe) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.offices.push({"name":"All OFFICES","uuid":"All"});
	  this.ut =Utils.fetchUserTypeFromLocalStorage();
  }

  ngOnInit() {
	this.dateOptionsS.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
	this.dateOptionsE.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
	if (this.route.snapshot.url[0].path=='enreportscl'){
		  this.hd1=ClaimTreatmentTextModel.claim;
		  this.ur="/enreportcl";
	  }else{
		  this.hd1=ClaimTreatmentTextModel.txPlan;
	  }
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
	this.enreports.reportType = this.typeMap[value];
	this.reportType = this.enreports.reportType;
  }
  
  
  dateCheck(){
	  let x=true;
	  if (this.enreports.startDate!='' && this.enreports.endDate==''){
		  x=false;
	  }
	  if (this.enreports.startDate=='' && this.enreports.endDate!=''){
		  x=false;
	  }
	 return x; 
  }
  
  runEnReport()
  {
	  if(this.dateCheck() && (this.enreports.tpId || this.enreports.ivfId || this.enreports.pId || this.enreports.officeId ||
			  (this.dateCheck() && (this.enreports.endDate > this.enreports.startDate)))
	    )
	  {
		  if(this.enreports.startDate!='')
			  {this.enreports.startDate = this.datePipe.transform(this.enreports.startDate, 'MM/dd/yyyy');
		  this.enreports.endDate = this.datePipe.transform(this.enreports.endDate, 'MM/dd/yyyy');}
		  this.showLoading = true;
		  this.accountService.validateEnReport(this.enreports,this.ur,(result) =>{
			  this.showLoading = false;
				if (result.status=='OK'){
					this.enreportData = result.data;
					this.showEnReportPopup = true;
					if (this.isEmpty(this.enreportData)){
						alert("No Data Found.");
							this.showEnReportPopup = false;
						}else{
							
						}
					} else {
						this.showEnReportPopup = false;
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
  
  receiveChildrenEmitter(event) {
		if(event['action'] == "showEnReportPopup" ) {
			this.showEnReportPopup = event['value'];
		}
  } 
  
}
