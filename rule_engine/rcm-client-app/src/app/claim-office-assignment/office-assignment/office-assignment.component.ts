import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import {ClaimAssignmentDataModel} from '../../models/claim-assignmen-data-model';
import {ClaimAssignmentModel} from '../../models/claim-assignment.model';
import {ClaimAssignmentPullModel} from '../../models/claim-assignment-pull-model';
import {BillingList} from '../../models/billing-list-model';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'claim-office-assignment',
  templateUrl: './office-assignment.component.html',
  styleUrls: ['./office-assignment.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class OfficeAssignmentComponent implements OnInit {

  //claimAssignmentModel: ClaimAssignmentModel = new ClaimAssignmentModel();
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
  showLoader:boolean=false;
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
    ths.showLoader = true;
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
        ths.showLoader=false;
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

//  saveToPdf(divName:any){
// let el:any = document.getElementById(divName);
//  }

//  assignUserToClaimData(evt:any){
//   evt  = evt.target.value.split(",")
//   this.claimData.forEach((e:any)=>{
//     if(evt[1] == e.assignedUser || evt[2] === e.officeUuid){
//       e.fname = evt[0];
//       e.assignedUser= evt[1];
//     }
//   })
//  }

 sortData(data:any,sortingColm:any,order:any,sortingType:any){
  this.appService.sortData(data,sortingColm,order,sortingType)
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

}
