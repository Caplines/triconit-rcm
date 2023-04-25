import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import {ClaimAssignmentDataModel} from '../../models/claim-assignmen-data-model';
import {ClaimAssignmentModel} from '../../models/claim-assignment.model';
import {ClaimAssignmentPullModel} from '../../models/claim-assignment-pull-model';
import {BillingList} from '../../models/billing-list-model';
import { Title } from '@angular/platform-browser';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { Router } from '@angular/router';

@Component({
  selector: 'claim-office-assignment',
  templateUrl: './office-assignment.component.html',
  styleUrls: ['./office-assignment.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class OfficeAssignmentComponent implements OnInit {

  // claimAssignmentModel: ClaimAssignmentModel = new ClaimAssignmentModel();
  claimAssigmentPullModel:ClaimAssignmentPullModel = new ClaimAssignmentPullModel();
  bl:BillingList = new BillingList();

  claimData : Array<ClaimAssignmentDataModel>;

  bType:string="-1";
  insType:string="PPO";

  isSorted:boolean=false;
  teamId:any;
  userByTeam:any=[];
  assignOfficeDetails:any={'assignOfficeDetails':[],'teamId':''};
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};
  loader:any= {'showLoader':false,'exportPDFLoader':false,'exportCSVLoader':false};
  showExportLoader:boolean=false;
  totalClaimData:any={'oldestOpdt':'','oldestOpdos':'','totalCount':0,'totalRemLiteReject':0,'totalcountAndRemLiteReject':0}
  clientName:string='';
  date:any;
  constructor(private appService: ApplicationServiceService,private title:Title,private router:Router) { 
    title.setTitle("Claim-Office Assignment");
    this.claimData = [];//{} as FreshClaimPLogs;
    console.log(this.router.url)
  }

  ngOnInit(): void {
    this.fetchClaimAssignments();
    this.teamId = localStorage.getItem("selected_teamId");
    this.clientName = localStorage.getItem("selected_clientName");
    this.getUserByTeamId();
    this.assignOfficeDetails.teamId = this.teamId;
  }

  fetchClaimAssignments(){
    let ths=this;
    ths.loader.showLoader = true;
    ths.claimAssigmentPullModel.claimType=[];
    ths.claimAssigmentPullModel.insuranceType=[];
    ths.totalClaimData.totalCount = ths.totalClaimData.totalRemLiteReject = ths.totalClaimData.totalcountAndRemLiteReject = 0;
    if (ths.bType=='-1'){
      ths.bl.bills.forEach(e => {
        if (e.key !='-1') 
        ths.claimAssigmentPullModel.claimType.push(Number(e.key));
      });
    }else{
      ths.claimAssigmentPullModel.claimType.push(Number(ths.bType));
    }
    ths.claimAssigmentPullModel.insuranceType.push(ths.insType);

    ths.appService.fetchClaimAssignments(ths.claimAssigmentPullModel,(res:any)=>{
       
       if (res.status=== 200){
        ths.claimData= res.data;
        ths.calcCount(ths.claimData)
        ths.calcRemLiteReject(ths.claimData)
        ths.calcCountAndRemLiteReject(ths.claimData)
        ths.loader.showLoader=false;
       }else{
         //ERROR
       }
      
     });
  }

  getCompany(){
    this.appService.fetchCompanyNameData((callback:any)=>{
      if(callback){
        console.log(callback);
      }
    })
  }

  getUserByTeamId(){
    this.appService.fetchUserByTeamId((callback:any)=>{
      if(callback){
        this.userByTeam = callback.data
      }
    })
  }

  selectNewAssignedUser(evt:any,officeUuid:any){
    
    if(this.assignOfficeDetails.assignOfficeDetails.length==0){
      this.assignOfficeDetails.assignOfficeDetails.push(
        {
          'userId':evt.target.value,
          'officeId':officeUuid
        })
      }else{
        this.assignOfficeDetails.assignOfficeDetails.find((e:any)=>{
          if(e.officeId === officeUuid){
              e.userId = evt.target.value;
          }
        })
        let officeIdExist = this.assignOfficeDetails.assignOfficeDetails.some((e:any)=>e.officeId === officeUuid);
        if(!officeIdExist){
          this.assignOfficeDetails.assignOfficeDetails.push(
            {
              'userId':evt.target.value,
              'officeId':officeUuid
            });
        }

      }
      console.log(this.assignOfficeDetails.assignOfficeDetails)
    }
  
 saveAssignments(){
  this.appService.assignOffice(this.assignOfficeDetails,(callback:any)=>{
    if(callback.status == 200){
      this.showAlertPopup(callback);
      scrollTo(0,0);
      this.assignOfficeDetails.assignOfficeDetails= [];
    }else{
      this.showAlertPopup(callback);
      scrollTo(0,0);
    }
  })
 }

 saveToPdf(divName:any){
   this.loader.exportPDFLoader= true;
   let m:any=document.querySelector(".table-wrapper-scroll-y");
   m.classList.remove('table-wrapper-scroll-y');
   m.classList.remove('table-inner-scrollbar');
  html2canvas(<any>document.getElementById(divName)).then(canvas => {
  const content = canvas.toDataURL('image/png');
  let pdf= new jsPDF('p','mm','a4');
  let width= pdf.internal.pageSize.getWidth();
  let height = canvas.height  * width / canvas.width;
  console.log(width,height)
  pdf.addImage(content,"PNG",0,0,width,height)
  this.date = new Date();
  this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
  pdf.save(`${this.clientName}_Pendency_${this.date}`);
  this.loader.exportPDFLoader = false;
  m.classList.add('table-wrapper-scroll-y')
  m.classList.add('table-inner-scrollbar')
 });

}

sortData(data:any,sortProp:string,order:any,sortType:string){
  this.appService.sortData(data,sortProp,order,sortType);
}


 calcCount(data:any){
  data.forEach((e:any)=>{
      this.totalClaimData.totalCount = this.totalClaimData.totalCount + e.count;
 });
}

calcRemLiteReject(data:any){
  data.forEach((e:any)=>{
    this.totalClaimData.totalRemLiteReject = this.totalClaimData.totalRemLiteReject + e.remoteLiteRejections;
 });
}

calcCountAndRemLiteReject(data:any){
  data.forEach((e:any)=>{
    this.totalClaimData.totalcountAndRemLiteReject  = this.totalClaimData.totalcountAndRemLiteReject+ e.count + e.remoteLiteRejections;
 });
}

exportToCsv(){
  this.loader.exportCSVLoader=true;
  let options:any={
    showLabels:true,
    headers: ["Office Name","Office Assigned To", "Oldest Pending Date","Oldest Pending DOS","Number of Pending Claims to be Billed","No. of RemoteLite Rejections Pending to be Handled","Total (Pending For Billing & Remote Lite Rejections)"],
  }
  let excelData:any;
  excelData = this.claimData.forEach((e:any)=>
  {
    e['officeAssignedTo'] = e.fname ? e.fname+ " "+ e.lname : "-";
    e.opdos == null ? e.opdos = '-' : e.opdos;
    if(e.opdt){
      let date:Date = new Date(e.opdt);
      e.opdt =  `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()}`;
    }else{
        e.opdt = '-';
    }
    e['totalBillingRejection'] = e.remoteLiteRejections+e.count;
  })

  excelData = this.claimData.map(
    ({officeUuid,assignedUser,...newClaimData})=> newClaimData);

  excelData = excelData.map((e:any)=>{
    return{
      "OfficeName":e.officeName,
      "officeAssignedTo":e.officeAssignedTo,
      "OldestPendingDate":e.opdt,
      "OldestPending DOS":e.opdos,
      "NumberofPendingClaimstobeBilled":e.count,
      "NoofRemoteLiteRejectionsPendingtobeHandled" : e.remoteLiteRejections,
      "Total(Pending For Billing & Remote Lite Rejections)": e.count ? e.count+e.remoteLiteRejections : '0'
    }
  })  
    this.date = new Date();
    this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`
    new ngxCsv(excelData, `${this.clientName}_Pendency_${this.date}`,options);
    this.loader.exportCSVLoader=false;
    this.fetchClaimAssignments();
    
}

showAlertPopup(res:any){
  this.alert.showAlertPopup = true;
  setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
  res.status==400 ? this.alert.isError=true : this.alert.isError=false;
  this.alert.alertMsg = res.message ? res.message : res.result.message;
}
}