import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
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
  offName:string;
  userName: any;
  userType: any;
  showLoading: boolean = false;
  showScrapForm: boolean = false;
  showScrapPopup: boolean = false;
  pd:boolean=false;
  lp:boolean=false;

  showParam:any = { Roster : false, MCNADENTAL : false, MCNADENTALUI : false, Dentaq : false, DentaqUI : false };

dateOptions: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a DOB'
	  };
  typeMap: any = { 'Roster': "2", 'MCNADENTAL': "1", 'MCNADENTALUI': "1", 'Dentaq': "3", 'DentaqUI': "3" };
  typeMapD: any = { 'Roster': "a", 'MCNADENTAL': "a", 'MCNADENTALUI': "b", 'Dentaq': "a", 'DentaqUI': "b" };

  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute, private datePipe: DatePipe) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  //this.offices.push({"name":"All OFFICES","uuid":"All"});
	  this.scrap.start="A";
	  this.scrap.end="Z";
	  
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
	  this.pd=false;
	  this.lp=false;
	  this.scrap.officeId="";
  }
  
  runScrapReport() {
	  if (this.scrap.listUd[0].dob!='')this.scrap.listUd[0].dob = this.datePipe.transform(this.scrap.listUd[0].dob, 'MM/dd/yyyy');
	  //console.log(this.scrap);
	  
	  if(this.scrap.officeId) {
	  this.offName=this.offices.find(x=>x.uuid == this.scrap.officeId).name;
	  this.showLoading = true;
	  this.showScrapPopup= true;
	  this.scrap.isdataFromUi=false;
	  if (this.scrapTypeD == "b"){
		  this.scrap.isdataFromUi=true;
	    }
	  }
	
	}
  
  onOfficeChange(value){
	  this.showLoading=true;
	  this.pd=false;
	  this.lp=false;
	  if(this.scrap.officeId) {
		  this.getSdetails(value);
		 
	  }else{
		  this.showLoading=false;
	  }
  }
  
  getSdetails(value) {
	this.scrap.username=this.scrap.password="";
	  this.applicationService.getSdDetails(
			  (result)=>{
				  if (result.status=='OK' && result.data){
						if (result.data.data){
							this.scrap.username=result.data.data.userName;
							this.scrap.password=result.data.data.password;
							this.scrap.locationProvider=result.data.data.locationProvider;
							this.scrap.location=result.data.data.location;
							this.showLoading=false;
							this.pd=true;
							if (result.data.data.sid==3) this.lp=true;
						}
				  }else{
					  alert("Office not set up..");
				  }
				  
			  }
	  ,value
	  ,this.scrap.scrapType)
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
