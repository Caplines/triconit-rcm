import {Component, OnInit, ViewEncapsulation, HostListener} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
//import {ScrapModel}  from "../../model/model.scrap";
import {ScrapFullDataModel} from "../../model/model.scrapfulldata";
import {ScrapFullPatientDataModel}  from "../../model/model.scrapfull.patientdata";
import { DatePipe } from '@angular/common';
import { DatepickerOptions } from 'ng2-datepicker';



@Component({
  selector: 'app-scrapfulldata',
  templateUrl: './scrapfulldata.component.html',
  styleUrls: ['./scrapfulldata.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class ScrapFullDataComponent implements OnInit {

	@HostListener('document:click', ['$event'])
	  clickout(event) {
		//console.log(event.target);
		//console.log(event.target.className);
		//console.log(typeof event.target.className);
		
		if (document.getElementById("data-scroll")){
			if (event.target &&  event.target.className && (typeof event.target.className==='string')) {
			if (event.target.className.indexOf("ngx-datepicker-input")>-1 || event.target.className.indexOf("topbar-title")>-1 ||
				event.target.className.indexOf("year-unit")>-1	|| event.target.className.indexOf("slimscroll-bar")>-1	 ||
				event.target.className.indexOf("slimscroll-wrapper")>-1 || event.target.className.indexOf("topbar-container")>-1 ||
				event.target.className.indexOf("day-unit")>-1
		     ){
			  document.getElementById("data-scroll").style.overflow="visible";
		     }else  document.getElementById("data-scroll").style.overflow="auto";
	      }else if (document.getElementById("data-scroll") && event.target && event.target.className && (typeof event.target.className==='object')  ){
			document.getElementById("data-scroll").style.overflow="visible";
	       }else document.getElementById("data-scroll").style.overflow="auto";
	  
     }
		
	}
	
  scrap: ScrapFullDataModel = new ScrapFullDataModel();//
 
  errorMessage: string;
  offices:any;
  offid:string="";
  siteIn:string="";
  site:any;
  oName:string="";	
  showLoading: boolean = false;
  sites:any;
  showScrapMain:boolean = false;
  showLoadingD: boolean = false;
  showLoadingP: boolean = false;
  showLoadingPA: boolean = false;
  showLoadingPAA:boolean =false;
  linkData:string="";
  rowCounter:number=1;
  timerRef:any;	
  dateOptions: DatepickerOptions = {
		minYear: 1850,
		maxYear: 2030,  
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'MM/DD/YYYY',
		maxDate: new Date(Date.now()),
		addClass: 'login-signup-form-field form-control heightdef',
		useEmptyBarTitle: false
		
	};


  constructor(public applicationService: ApplicationService, public router: Router,private route: ActivatedRoute,
		  private datePipe: DatePipe) {
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
			   this.site.name=val.name;
			}
		   
		 }
	  
	}
   
   fetchDetails(){
	   if (this.siteIn!='' && this.offid!=''){
	   this.oName = this.offices.filter(o => o.uuid === this.offid)[0].name;
	   console.log(this.oName);
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
	 if (i>10) {
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
	   this.showLoadingP=false;
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
	   //console.log(this.scrap.dto);
	   if(this.validateData()){
	   this.showLoadingPAA=true;
	   this.applicationService.postData(this.scrap,"/parsefulldata",
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
	      }else{
	    	   this.showLoadingPA=false;
	    	   this.showLoadingP=false;
	   	       this.showLoadingPAA=false;
	   	   
	      }
        }
   
  validateData(){
	  let ret =true;
	  let ths=this;
	  let dtoo=this.scrap.dto;
	  let ax:any=[];
	  let x=0;
	   for (let dt of dtoo){
		   //console.log(dt);
		   dt.firstName= dt.firstName.trim();
		   dt.lastName=dt.lastName.trim();
		   dt.patientId=dt.patientId.trim();
		   dt.dob=dt.dob.trim?dt.dob.trim():dt.dob;
		   if (!dt.dob.trim) dt.dob = ths.datePipe.transform(dt.dob, 'MM/dd/yyyy');
		   dt.locationProvider= dt.locationProvider.trim();
		   dt.memberId= dt.memberId.trim();
		   dt.patientId=dt.patientId.trim(); 
		   dt.ssnNumber=dt.ssnNumber.trim();
		   dt.enrolleeId=dt.enrolleeId.trim();
		    if (document.getElementById("pati"+x))document.getElementById("pati"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patf"+x))document.getElementById("patf"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patl"+x))document.getElementById("patl"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patd"+x))document.getElementById("patd"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("paten"+x))document.getElementById("paten"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patss"+x))document.getElementById("patss"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patloc"+x))document.getElementById("patloc"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patmem"+x))document.getElementById("patmem"+x).setAttribute("style", "border-color: ;");
		    
		    
		 if(!(dt.firstName=="" && dt.lastName=="" &&
		   dt.patientId=="" && dt.dob=="" &&
		   dt.locationProvider=="" && dt.memberId=="" &&
		   dt.patientId==""  &&  dt.ssnNumber=="" &&
		   dt.enrolleeId=="")){
			 ax.push(dt);
			 //console.log(this.site.name);
			 if (this.site.name=='BCBS'){
				 
				 if(dt.dob==""){
					 document.getElementById("patd"+x).setAttribute("style", "border-color: red;");
					 ret =false;
				 }
				 if(dt.memberId=="" && dt.ssnNumber==""){
					 document.getElementById("patss"+x).setAttribute("style", "border-color: red;");
					 document.getElementById("patmem"+x).setAttribute("style", "border-color: red;");
					 
					 ret =false;
				 }
			 }
			 //console.log("--"+this.site.name+"--");
             if (this.site.name=='Delta Dental'){
				 
				 if(dt.dob==""){
					 document.getElementById("patd"+x).setAttribute("style", "border-color: red;");
					 ret =false;
				 }
				 if(dt.firstName==""){
					 document.getElementById("patf"+x).setAttribute("style", "border-color: red;");
					 ret =false;
				 }
				 if(dt.lastName==""){
					 document.getElementById("patl"+x).setAttribute("style", "border-color: red;");
					 ret =false;
				 }
				 /*
				 if(dt.memberId=="" && dt.ssnNumber==""){
					 document.getElementById("patss"+x).setAttribute("style", "border-color: red;");
					 document.getElementById("patmem"+x).setAttribute("style", "border-color: red;");
					 
					 ret =false;
				 }
				 */
			 } 
			 
		 }
		 
		 x++;
		 
	   }//for
	   if (ax.length==0){
		   if (this.site.name=='BCBS'){
		    if (document.getElementById("patd"+0))document.getElementById("patd"+0).setAttribute("style", "border-color: red;");
		    if (document.getElementById("patss"+0))document.getElementById("patss"+0).setAttribute("style", "border-color: red;");
		    if (document.getElementById("patmem"+0))document.getElementById("patmem"+0).setAttribute("style", "border-color: red;");
		    ret =false;
		   }
	   }
	   if (ax.length==0){
		   if (this.site.name=='Delta Dental'){
		    if (document.getElementById("patd"+0))document.getElementById("patd"+0).setAttribute("style", "border-color: red;");
		    if (document.getElementById("patf"+0))document.getElementById("patf"+0).setAttribute("style", "border-color: red;");
		    if (document.getElementById("patl"+0))document.getElementById("patl"+0).setAttribute("style", "border-color: red;");
		    ret =false;
		   }
	   }
	   if (ret)this.scrap.dto=ax;
	   //console.log(ret);
	   return ret;
  }
  
  removeText(i){
	   let a:any= document.getElementById("datepicker-"+i);
       a.value="";
	   a=document.getElementById("patd"+i);a.value="";
	   let dtoo=this.scrap.dto;
		dtoo[i].dob="";  
	  
  }
  checkData(x,t){
	  let ths=this;
	  ths.timerRef=setTimeout(() => {
		  ths.applicationService.postData({id:x},"/parsefulldataProcessInfo",
					(result)=>{
						  if (result.status == 'OK') {
							  if (result.data==0){
								  alert('Data Scrapped..!!');
							  }else{
								  ths.checkData(x,30*1000);
							  }
						}
					 }
					);
		
	}, t);
  }
  
}
