import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";
import {ScrapModel}  from "../../model/model.scrap";
import {ScrapUserDataModel} from "../../model/model.scrapuserdata";

import {DatepickerOptions} from 'ng2-datepicker';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-scrap',
  templateUrl: './scrap.component.html',
  styleUrls: ['./scrap.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class ScrapComponent implements OnInit {
  scrap: ScrapModel = new ScrapModel();
  scrapTypeD:any;
  showScrapMain:boolean= false;
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
  showLoading: boolean = false;
  showScrapForm: boolean = false;
  showScrapPopup: boolean = false;
  showParam:any = { Roster : false, MCNADENTAL : false, MCNADENTALUI : false, Dentaq : false, DentaqUI : false };

dateOptions: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a DOB'
	  };
  typeMap: any = { 'Roster': "2", 'MCNADENTAL': "1", 'MCNADENTALUI': "1", 'Dentaq': "3", 'DentaqUI': "3" };
  typeMapD: any = { 'Roster': "a", 'MCNADENTAL': "a", 'MCNADENTALUI': "b", 'Dentaq': "a", 'DentaqUI': "b" };

  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute, private datePipe: DatePipe) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  //this.offices.push({"name":"All OFFICES","uuid":"All"});
	  this.scrap.listUd.push(new ScrapUserDataModel());
  }

  ngOnInit() {
		this.dateOptions.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
	  }
  
  scrapParam(value) {
	  //this.scrap = new ScrapModel();
	  let filter = this.showParam;
	  Object.keys(filter).forEach(function(key, result) {
		  if(key == value) {
		  	filter[key] = true;
		  } else {
		  	filter[key] = false;
		  }
		  return filter;
		 });
	  this.showScrapForm = true;
	  this.scrapTypeD = this.typeMapD[value];
	  this.scrap.scrapType = this.typeMap[value];
	  
  }
  
  runScrapReport() {
	  //console.log(this.scrap);
	  //debugger;
	  if (this.scrap.listUd[0].dob!='')this.scrap.listUd[0].dob = this.datePipe.transform(this.scrap.listUd[0].dob, 'MM/dd/yyyy');
	  if(this.scrap.officeId) {
	  this.showLoading = true;
	  this.showScrapPopup= true;
	  this.scrap.isdataFromUi=false;
	  if (this.scrapTypeD == "b"){
		  this.scrap.isdataFromUi=true;
	  }
	  }
	  console.log(this.scrap);
		
	  /*
	  this.accountService.scrapSite(this.scrap, 'scrapsite', (result) => {		
			if (result.status=='OK'){
			    //this.showLoading = false;
				this.scrapData = result.data;
				this.showScrapPopup=true;
			}
		  });
	  */
	  
	  }
  
  
  
  receiveChildrenEmitter(event) {
		if(event['action'] == "showLoading" ) {
			this.showLoading = event['value'];
		}else if(event['action'] == "showScrapPopup") {
			this.showScrapPopup = event['value'];
			if(!this.showScrapPopup) {
				this.showScrapPopup = false;
				this.showScrapMain=false;
			}else{
				this.showScrapMain=true	;
			}
		}

  }
}
