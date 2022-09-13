import {Component, OnInit, ViewEncapsulation, HostListener} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
//import {ScrapModel}  from "../../model/model.scrap";
import {ScrapLiteDataModel} from "../../model/model.scraplitedata";
import {ScrapFullPatientDataModel}  from "../../model/model.scrapfull.patientdata";
import { DatePipe } from '@angular/common';
import { DatepickerOptions } from 'ng2-datepicker';



@Component({
  selector: 'app-scrapremotelite',
  templateUrl: './scraplite.component.html',
  styleUrls: ['./scraplite.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class ScrapLiteComponent implements OnInit {

	
	
  scrap: ScrapLiteDataModel = new ScrapLiteDataModel();//
 
  errorMessage: string;
  offices:any;
  offid:string="";
  siteIn:string="";
  siteInId:string="";
  site:any;
  oName:string="";	
  showLoading: boolean = false;
  sites:any;
  showScrapMain:boolean = false;
  showLoadingD: boolean = false;
  showLoadingP: boolean = false;
  showLoadingPA: boolean = false;
  showLoadingPAA:boolean =false;
  displayResultV:boolean =false;

  rdata:any=[];	
  linkData:string="";
 
  timerRef:any;
  
 

  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute,
		  private datePipe: DatePipe) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.showLoading=true;
  }

  ngOnInit() {
		this.applicationService.getAllScrappingLiteSites(
				(result)=>{
					this.showLoading = false;
					if (result.status == 'OK') {
						this.sites=result.data;
						this.setSite(this.sites[0]);
					}
				 }
				);
	      }
  
   setSite(site){
	   console.log(site.name);
	   this.site=site;
	   this.site.full=site.name+"("+site.url+")";
	   this.site.name=site.name;
	   this.siteIn=site.name+"("+site.url+")";
	   this.siteInId=site.id;
	   
	   
	  
	}
   
   fetchDetails(){
	  // alert(this.siteIn+"-"+this.siteIn);
	   if (this.siteIn!='' && this.offid!=''){
	   this.oName = this.offices.filter(o => o.uuid === this.offid)[0].name;
	   this.showLoadingD=true;
	   this.showLoadingP=true;
       let d={'siteId':this.siteInId,'officeId':this.offid};
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
						this.scrap.googleSheetIdDb="1KVaZbAfaYOGMbYRZuGH-4VVxeZQPIvML0YThPsPNTnw";
						this.scrap.siteId=this.site.id;
						this.scrap.officeId=this.offid;
						this.createDate();
					}
				 }
				);
	      }
    }
   
   
   goBack(){
	   let th=this;
	   th.showLoadingD=false;
	   th.showScrapMain=false;
	   th.showLoadingPA=false;
	   th.showLoadingPAA=false;
	   th.showLoadingP=false;
	   th.displayResultV=false;
	   th.rdata=[];
   }
  
   parseSite(){
	   this.linkData="";
	   //console.log('this.timerRef');
	   //console.log(this.timerRef);
	   
	   if (this.timerRef){
		   clearTimeout(this.timerRef);
	   }
	   this.showLoadingP=true;
	   this.showLoadingPA=true;
	   this.showLoadingPAA=false;
	   this.scrap.siteName=this.site.name;
	   this.scrap.siteUrl=this.site.url;
	   //this.scrap.googleSheetIdDb=this.site.googleSheetId;
	   //console.log(this.scrap.dto);
	  
	   this.showLoadingPAA=true;
	   this.applicationService.postData(this.scrap,"/parseLitedata",
				(result)=>{
					  if (result.status == 'OK') {
						  //this.showLoadingPAA=false;
						  //this.showLoadingP=false;
						  this.showLoadingPAA=true;
						  this.showLoadingPA=false;
						  //console.log(result.data);
						  if (result.data.indexOf('Started')<0){
							  alert(result.data);
						  }else{
							  this.checkData(result.data.split("Started-")[1],1000*60*4);
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
   
  validateData(){
  
  }
  
 
  checkData(x,t){
	  let ths=this;
	  ths.timerRef=setTimeout(() => {
		  ths.applicationService.postData({id:x},"/parsefulldataProcessInfo",
					(result)=>{
						  if (result.status == 'OK') {
							  if (result.data.count==0){
								  alert('Data Scrapped..!!');
								  ths.displayResultV=true;
								  ths.displayResult(result.data.listPatient);
							  }else{
								  ths.checkData(x,30*1000);
							  }
						}
					 }
					);
		
	}, t);
  }
  
  displayResult(d){
	  
	  this.rdata=d;
	  

	  
  }
  
  
  createDate(){
	  /*
	  let d  = new Date();
	  let od = new Date();
	  let ld = new Date(od.setDate(od.getDate()-90));
	  
	  let mon:any=(d.getMonth()+1);
	  let dt:any=d.getDate();
	  if (mon<10) mon="0"+mon;
	  if (dt<10) dt="0"+dt;
	  
	  let lmon:any=(ld.getMonth()+1);
	  let ldt:any=ld.getDate();
	  if (lmon<10) lmon="0"+lmon;
	  if (ldt<10) ldt="0"+ldt;
	  
	  
	  let datestring = mon  + "/" + dt + "/" + d.getFullYear();
	  let ldatestring = lmon  + "/" + ldt + "/" + ld.getFullYear();
	  this.scrap.endDate=datestring;
	  this.scrap.startDate=ldatestring;
	  */
  }
}
