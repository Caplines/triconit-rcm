import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
//import {ScrapModel}  from "../../model/model.scrap";
import {ScrapFullDataModel} from "../../model/model.scrapfulldata";
import {ScrapFullPatientDataModel}  from "../../model/model.scrapfull.patientdata";

@Component({
  selector: 'app-scrapfulldata',
  templateUrl: './scrapfulldata.component.html',
  styleUrls: ['./scrapfulldata.css'],
  encapsulation: ViewEncapsulation.None
})
export class ScrapFullDataComponent implements OnInit {
  scrap: ScrapFullDataModel = new ScrapFullDataModel();//
 
  errorMessage: string;
  offices:any;
  offid:string="";
  siteIn:string="";
  site:any;
  showLoading: boolean = false;
  sites:any;
  showScrapMain:boolean = false;
  showLoadingD: boolean = false;
  showLoadingP: boolean = false;
  showLoadingPA: boolean = false;
  showLoadingPAA:boolean =false;
  linkData:string="";
  rowCounter:number=1;
  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.showLoading=true;
  }

  ngOnInit() {
		this.applicationService.getAllScrappingFullDataSites(
				(result)=>{
					this.showLoading = false;
					if (result.status == 'OK') {
						this.sites=result.data;
					}
				 }
				);
	      }
  
   setSite(site){
	   for (var val of this.sites) {
		   if (val.id==site) {
			   this.site=val;
			   this.site.full=val.name+"("+val.url+")";
		   }
		   
		 }
	}
   
   fetchDetails(){
	   if (this.siteIn!='' && this.offid!=''){
	   this.showLoadingD=true;
	   this.showLoadingP=true;
       let d={'siteId':this.siteIn,'officeId':this.offid};
		this.applicationService.postData(d,"/getsitedetailstoparsefulldata",
				(result)=>{
					this.showLoading = false;
					if (result.status == 'OK') {
						this.showScrapMain=true;
						//this.showLoadingD=false;
						this.showLoadingP=false;
						//console.log(result.data);
						if (result.data) this.scrap=result.data;
						else {
							this.scrap.siteDetailId='-1';
						}
						this.scrap.siteId=this.site.id;
						this.scrap.officeId=this.offid;
						this.rowCounter=1;
						this.createBlankRow(0);
					}
				 }
				);
	      }
    }
   
   createBlankRow(n){
	 let i=this.rowCounter;
	 if (i>5) {
		 alert('Cannot add more Patients..');
		 return ;
	 }
	 let sc=this.scrap;  
	 let  mod:ScrapFullPatientDataModel= new ScrapFullPatientDataModel(); 
	  this.scrap.dto.push(mod);
	  let thtml="";
	  let thtmlR="";
	  if (n==0){
		 if (sc.patientId) thtml=thtml+"<th  class='data-scroll-thd'>Patient Id</th>";
		 if (sc.firstName) thtml=thtml+"<th  class='data-scroll-thd'>First Name</th>";
		 if (sc.lastName) thtml=thtml+"<th  class='data-scroll-thd'>Last Name</th>";
		 if (sc.dob) thtml=thtml+"<th  class='data-scroll-thd'>DOB</th>";
		 if (sc.enrolleeId) thtml=thtml+"<th  class='data-scroll-thd'>EnrolleeId</th>";
		 if (sc.ssnNumber) thtml=thtml+"<th  class='data-scroll-thd'>SSN Number</th>";
		 if (sc.locationProvider) thtml=thtml+"<th  class='data-scroll-thd'>Location Provider</th>";
		 if (sc.memberId) thtml=thtml+"<th  class='data-scroll-thd'>Member Id</th>";
		 setTimeout(() => {
			 if (n==0){
				 document.getElementById("data-scroll-tr1").innerHTML=thtml;	
			 }
			 	 
		}, 0);
	  
      }  
		 this.rowCounter++;	
   }
   goBack(){
	   this.showLoadingD=false;
	   this.showScrapMain=false;
	   this.showLoadingPA=false;
	   this.showLoadingPAA=false;
   }
  
   parseSite(){
	   this.linkData="";
	   this.showLoadingPA=true;
	   this.showLoadingPAA=false;
	   this.scrap.siteName=this.site.name;
	   this.scrap.siteUrl=this.site.url;
	   this.applicationService.postData(this.scrap,"/parsefulldata",
				(result)=>{
					  if (result.status == 'OK') {
						  //this.showLoadingPAA=false;
						  this.showLoadingPAA=true;
						  this.showLoadingPA=false;
						  console.log(result.data);
						  if (result.data!='Started'){
							  alert(result.data);
						  }
						  /*if ( Object.keys(result.data)[0].indexOf("Scrapping Initiated - ")>-1){
							    let links=Object.keys(result.data)[0].split("Scrapping Initiated - ")[1];
							    let link=links.split("-_-_-");
							    this.linkData="https://docs.google.com/spreadsheets/d/"+link[0]+"/edit#gid="+link[1];
							}*/
							 	
					}
				 }
				);
	      }
   

  receiveChildrenEmitter(event) {
		if(event['action'] == "showLoading" ) {
		}else if(event['action'] == "showScrapPopup") {
		}

  }
}
