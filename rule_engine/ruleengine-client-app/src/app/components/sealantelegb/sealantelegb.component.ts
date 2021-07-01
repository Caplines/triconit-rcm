import {Component, OnInit, ViewEncapsulation, HostListener} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
//import {ScrapModel}  from "../../model/model.scrap";// 
import {SealantEleDataModel} from "../../model/model.sealant.ele.data";
import {SealantElegPatientDataModel}  from "../../model/model.sealant.eleg.patientdata";
import { DatePipe } from '@angular/common';
import { DatepickerOptions } from 'ng2-datepicker';



@Component({
  selector: 'app-sealantdata',
  templateUrl: './sealantelegb.component.html',
  styleUrls: ['./sealantelegb.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class SealantelegbComponent implements OnInit {

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
	
  scrap: SealantEleDataModel = new SealantEleDataModel();//
 
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
  displayResultV:boolean =false;
  otpValidated:boolean =false;
  calledOTPGeneration = false;	
  rdata:any=[];	
  rdata1:any=[];
  rdataALL:any=[];
  showLoadingPDF:boolean=false;
  insName:string="";
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
  this.sites=[{name:"MCNA Dental",id:1},{name:"Dentaquest",id:3}];
  this.showLoading = false;
  }
  
   setSite(site){
	   for (var val of this.sites) {
		   if (val.id==site) {
			   this.site=val;
			   this.site.full=val.name;
			   this.site.name=val.name;
			}
		   
		 }
	  
	}
   
   fetchDetails(){
	   this.siteIn="1";//hard code fr now
	   if (this.offid!=''){
	   //if (this.siteIn!='' && this.offid!=''){
	   this.oName = this.offices.filter(o => o.uuid === this.offid)[0].name;
	   this.showLoadingD=true;
	   this.showLoadingP=true;
		this.applicationService.getSdDetails(
				(result)=>{
					this.showLoading = false;
					if (result.status == 'OK') {
						this.showScrapMain=true;
						//this.showLoadingD=false;
						this.showLoadingP=false;
						if (result.data) this.scrap=result.data.data;
						else {
							this.scrap.siteDetailId='-1';
						}
						this.scrap.listUd= new Array() as Array<SealantElegPatientDataModel>;
						this.scrap.siteId=this.siteIn;
						this.scrap.officeId=this.offid;
						this.rowCounter=1;
						this.createBlankRow(0);
						this.otpValidated=false;
					}
				 },this.offid,this.siteIn);
	      }
    }
   
   createBlankRow(n){
	 let i=this.rowCounter;
	 if (i>10) {
		 alert('Cannot add more Patients..');
		 return ;
	 }
	 let  mod:SealantElegPatientDataModel= new SealantElegPatientDataModel(); 
	  this.scrap.listUd.push(mod);
	  let thtml="";
	  let thtmlR="";
	  if (n==0){
		 thtml=thtml+"<th  class='data-scroll-thd'>Patient Id</th>";
		 thtml=thtml+"<th  class='data-scroll-thd'>First Name</th>";
		 thtml=thtml+"<th  class='data-scroll-thd'>Last Name</th>";
		 thtml=thtml+"<th  class='data-scroll-thd'>DOB</th>";
		 thtml=thtml+"<th  class='data-scroll-thd'>Subscriber ID</th>";
		 setTimeout(() => {
			 if (n==0){
				 document.getElementById("data-scroll-tr1").innerHTML=thtml;	
			 }
			 	 
		}, 0);
	  
      }  
		 this.rowCounter++;	
   }
   goBack(){
	   let th=this;
	   th.showLoadingD=false;
	   th.showScrapMain=false;
	   th.showLoadingPA=false;
	   th.showLoadingPAA=false;
	   th.showLoadingP=false;
	   th.displayResultV=false;
	   this.showLoadingPDF= false; 
	   th.rdata=[];
	   th.rdata1=[];
	   th.rdataALL=[];   
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
	   //this.scrap.siteName=this.site.name;
	   //this.scrap.siteUrl=this.site.url;
	   if(this.validateData()){
	   this.showLoadingPAA=true;
	   this.applicationService.postData(this.scrap,"/runsealantRules",
				(result)=>{
					  if (result.status == 'OK') {
						  //this.showLoadingPAA=false;
						  //this.showLoadingP=false;
						  this.showLoadingPAA=true;
						  this.showLoadingPA=false;
						  this.parseData(result.data);
						  
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
	  let dtoo=this.scrap.listUd;
	  let ax:any=[];
	  let x=0;
	   for (let dt of dtoo){
		   /*dt.firstName= dt.firstName.trim();
		   dt.lastName=dt.lastName.trim();
		   dt.patientId=dt.patientId;//.trim();
		   dt.dob=dt.dob.trim?dt.dob.trim():dt.dob;
		   if (!dt.dob.trim) dt.dob = ths.datePipe.transform(dt.dob, 'MM/dd/yyyy');
		   dt.subscriberId=dt.subscriberId.trim();*/
		   
		    if (document.getElementById("pati"+x))document.getElementById("pati"+x).setAttribute("style", "border-color: ;");
		   /* if (document.getElementById("patf"+x))document.getElementById("patf"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patl"+x))document.getElementById("patl"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("patd"+x))document.getElementById("patd"+x).setAttribute("style", "border-color: ;");
		    if (document.getElementById("paten"+x))document.getElementById("paten"+x).setAttribute("style", "border-color: ;")*/
		 /*if(!(dt.firstName=="" && dt.lastName=="" &&
		   dt.patientId=="" && dt.dob=="" &&
		   dt.subscriberId=="" */
		 if(!(  dt.patientId==""	   
		    )){
			 ax.push(dt);
			 
			/*	 if(dt.dob==""){
					 document.getElementById("patd"+x).setAttribute("style", "border-color: red;");
					 ret =false;
				 }*/
			 
		 }
		 
		 x++;
		 
	   }//for
	   if (ax.length==0){
		    if (document.getElementById("pati"+0))document.getElementById("pati"+0).setAttribute("style", "border-color: red;");
		    //if (document.getElementById("patss"+0))document.getElementById("patss"+0).setAttribute("style", "border-color: red;");
		    //if (document.getElementById("patmem"+0))document.getElementById("patmem"+0).setAttribute("style", "border-color: red;");
		    ret =false;
		   
	   }
	   
	   if (ret)this.scrap.listUd=ax;
	   //console.log(ret);
	   return ret;
  }
  
  removeText(i){
	   let a:any= document.getElementById("datepicker-"+i);
       a.value="";
	   a=document.getElementById("patd"+i);a.value="";
	   let dtoo=this.scrap.listUd;
		dtoo[i].dob="";  
	  
  }
  parseData(d){
	  let ths=this;
	  ths.displayResultV=true;
	  ths.rdataALL=d;
	  ths.scrap.listUd.forEach((j,v)=>{
		  let o={};
		  let o1={};
		  
		  o['o']=ths.oName;
		  o['pid']=j.patientId;
		  o1['o']=ths.oName;
		  o1['pid']=j.patientId;
		  //o['in']=j.iName;
		  //o1['in']=j.iName;
		   o1['mess']="";
		  d[j.patientId].forEach((y,z)=>{
			  if (y.iName!=null) ths.insName=y.iName;
			  if (y.ruleId==69){
				o['pn']=y.patientName;
				o['ivDone']=y.ivDone;
				o['te']=y.message;
				o['ruleId']=y.ruleId;
			}
			if (y.ruleId==70){
				o['pn']=y.patientName;
				o['ivDone']=y.ivDone;
				o['tne']=y.message;
				o['ruleId']=y.ruleId;
			}
			if (y.ruleId==71){
				o['pn']=y.patientName;
				o['ivDone']=y.ivDone;
				o['tnea']=y.message;
				o['ruleId']=y.ruleId;
			}
			if (y.ruleId==72){
				o['pn']=y.patientName;
				o['ivDone']=y.ivDone;
				o['tnef']=y.message;
				o['ruleId']=y.ruleId;
			}
			if (y.ruleId==73){
				o1['mess']=y.message;
				o1['ruleId']=y.ruleId;
			}
			if (y.ruleId==68){
				o1['mess']=o1['mess']+y.message+"</br>";
				o1['pn']=y.patientName;
				o1['ruleId']=y.ruleId;
			}
		});
		  if (typeof o['pn']!='undefined')ths.rdata.push(o);
		  else if (o1['ruleId']!=68) ths.rdata1.push(o1);
	  });
	  //console.log()
	  //if (ths.rdata1.length==0) ths.rdata1.push({mess:'<b style="color:red" class="error-message-api">No Patient Found</b>'}); 
  }
  
  downloadPdf(){
	  
	  if (this.showLoadingPDF) return; 
	  let dtoo=this.scrap.listUd;
	  let x="";
	  let cm="";
	   for (let dt of dtoo){
		   x=x+cm+dt.patientId;
		   cm=",";
	   }
	  
	  if (cm=="") return;
	  this.showLoadingPDF= true;
	  let dp=[];
	  dp.push(this.rdata);
	  dp.push(this.rdata1);
	  
	  this.applicationService.downloadIVFPDF(this.rdataALL,"/genereatePdfSealantUI", (result) => {
			this.showLoading = false;
			this.showLoadingPDF= false; 
			if (result.status == '200') {
				//const filename = result.headers.get('filename');
				//console.log(result);
				// this.downloadFile(result.body);
				this.saveBolbData(result.body, "Sealant Report - "+this.oName+" - "+this.convertDate(new Date().toDateString()) + ".pdf");
			}
		});
	}
  
  saveBolbData(data: Response, fileName: string) {
		const a: any = document.createElement('a');
		document.body.appendChild(a);
		a.style = 'display: none';
		const blob = new Blob([<any>data], { type: 'application/pdf' }),
			url = window.URL.createObjectURL(blob);
		a.href = url;
		a.download = fileName;
		a.click();
		window.URL.revokeObjectURL(url);

	}
  
  formatDate(date, format) {
	    const map = {
	        mm: date.getMonth() + 1,
	        dd: date.getDate(),
	        yy: date.getFullYear().toString().slice(-2),
	        yyyy: date.getFullYear()
	    }

	    return format.replace(/mm|dd|yy|yyy|yyyy/gi, matched => map[matched])
	}
    
  convertDate(inputFormat) {
	  function pad(s) { return (s < 10) ? '0' + s : s; }
	  var d = new Date(inputFormat)
	  return [pad(d.getDate()), pad(d.getMonth()+1), d.getFullYear()].join('-')
	}
}
