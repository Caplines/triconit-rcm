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
  isClaimAssign:boolean=true;
  teamId:any;
  userByTeam:any=[];
  assignOfficeDetails:any={'assignOfficeDetails':[],'teamId':''};
  alert:any={'showAlertPopup':false,'alertMsg':''};
  loader:any= {'showLoader':false,'exportPDFLoader':false,'exportCSVLoader':false}
  showExportLoader:boolean=false;
  totalClaimData:any={'oldestOpdt':'','oldestOpdos':'','totalCount':0,'totalRemLiteReject':0,'totalcountAndRemLiteReject':0}
  constructor(private appService: ApplicationServiceService,private title:Title) { 
    title.setTitle("Claim-Office Assignment");
    this.claimData = [];//{} as FreshClaimPLogs;
  }

  ngOnInit(): void {
    this.fetchClaimAssignments();
    this.teamId = localStorage.getItem("teamId");
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
        // let k = ths.claimData.forEach((e:any,idx:any)=>{
        //   if(!e.fname && idx % 2 == 0 ){
        //     e.fname = "Puneet"
        //   }else{
        //     if(!e.fname && idx %2 !== 0)
        //     e.fname= "Kuldeep"
        //   }
        // })
        // console.log(k)

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
    this.appService.fetchUserByTeamId(this.teamId,(callback:any)=>{
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
  
  
 //
 saveAssignments(){
  this.appService.assignOffice(this.assignOfficeDetails,(callback:any)=>{
    if(callback.status == 200){
      this.alert.showAlertPopup = true;
      this.alert.alertMsg = callback.message; 
      scrollTo(0,0);
      this.assignOfficeDetails.assignOfficeDetails= [];
    }else{
      this.alert.showAlertPopup = true;
      this.alert.alertMsg = callback.message; 
      scrollTo(0,0);
    }
  })
 }

 saveToPdf(divName:any){
   this.loader.exportPDFLoader= true;
   let m:any=document.querySelector(".table-wrapper-scroll-y");
   m.classList.remove('table-wrapper-scroll-y')
   m.classList.remove('table-inner-scrollbar')
  html2canvas(<any>document.getElementById(divName)).then(canvas => {
  const content = canvas.toDataURL('image/png');
  let pdf= new jsPDF('p','mm','a4');
  let width= pdf.internal.pageSize.getWidth();
  pdf.text('Paranyan loves jsPDF',10,10)
  let height = canvas.height  * width / canvas.width;
  console.log(width,height)
  pdf.addImage(content,"PNG",0,0,width,height)
  pdf.save("output.pdf")
  this.loader.exportPDFLoader = false;
  m.classList.add('table-wrapper-scroll-y')
  m.classList.add('table-inner-scrollbar')
 });

}

 sortData(data:any,sortProp:string,sortingColm:any,order:any,sortingType:any,sortType:string){
  this.appService.sortData(data,sortProp,sortingColm,order,sortingType,sortType);
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
    headers: ["Office Name", "Oldest Pending Date","Oldest Pending DOS","Number of Pending Claims to be Billed","No. of RemoteLite Rejections Pending to be Handled","Office Assigned To"],
  }
  let excelData:any 
  excelData = this.claimData.forEach((e:any)=>
  {
    e['office Assigned To'] = e.fname+" "+e.lname;
    e.opdos == null ? e.opdos = '' : e.opdos;
    if(e.opdt){
      let date:Date = new Date(e.opdt);
      e.opdt =  `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()}`;
    }else{
        e.opdt = '';
    }
  })
  excelData = this.claimData.map(
    ({officeUuid,assignedUser,fname,lname,...newClaimData})=> newClaimData);
    new ngxCsv(excelData, 'My Report',options);
    this.loader.exportCSVLoader=false;
    this.fetchClaimAssignments();
    
}
}