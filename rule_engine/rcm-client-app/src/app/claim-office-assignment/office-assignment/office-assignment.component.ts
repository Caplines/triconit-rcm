import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import {ClaimAssignmentDataModel} from '../../models/claim-assignmen-data-model';
import {ClaimAssignmentModel} from '../../models/claim-assignment.model';
import {ClaimAssignmentPullModel} from '../../models/claim-assignment-pull-model';
import {BillingList} from '../../models/billing-list-model';


@Component({
  selector: 'claim-office-assignment',
  templateUrl: './office-assignment.component.html',
  styleUrls: ['./office-assignment.component.scss']
})
export class OfficeAssignmentComponent implements OnInit {

  //claimAssignmentModel: ClaimAssignmentModel = new ClaimAssignmentModel();
  claimAssigmentPullModel:ClaimAssignmentPullModel = new ClaimAssignmentPullModel();
  bl:BillingList = new BillingList();

  claimData : Array<ClaimAssignmentDataModel>;

  bType:string="-1";
  insType:string="PPO";

  loader:boolean=false;
  
  constructor(private appService: ApplicationServiceService) { 

    this.claimData = [];//{} as FreshClaimPLogs;
  }

  ngOnInit(): void {
    this.fetchClaimAssignments();
  }

  fetchClaimAssignments(){
    let ths=this;
    ths.claimAssigmentPullModel.claimType=[];
    ths.claimAssigmentPullModel.insuranceType=[];
    debugger;
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


       }else{
         //ERROR
       }
      
     });
  }


  

  
 //
 saveAssignments(){
  
 }

 
 

}
