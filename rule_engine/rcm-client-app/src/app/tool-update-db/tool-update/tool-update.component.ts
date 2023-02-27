import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import {FreshClaimPullModel} from '../../models/fresh.claim.pull.model';
import {FreshClaimPLogs} from '../../models/fresh.claim.log';
import {ClientModel} from '../../models/client.model';
import {IssueClaimModel} from '../../models/issue.claim.model';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'tool-update-db',
  templateUrl: './tool-update.component.html',
  styleUrls: ['./tool-update.component.scss']
})
export class ToolUpdateComponent implements OnInit {

  freshClaimPullModel: FreshClaimPullModel = new FreshClaimPullModel();

  log : Array<FreshClaimPLogs>;
  clients : Array<ClientModel>;
  issueCl : Array<IssueClaimModel>;
  smilePoint: ClientModel;
  esAgent:any;
  googleSheet:any;
  loader:boolean=false;
  cName:string="-1";
  sourceType:string="";
  expandCollapse:any={'expandClaim':true,'expandTeamRemarks':true}
  hasUpdateClaims:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':''}
  issueClientName:string='';
  ele:any={'modal':'','span':''}
  
  constructor(private appService: ApplicationServiceService,private title:Title) { 

    this.log = [];//{} as FreshClaimPLogs;
    this.clients = [];
    this.smilePoint={};
    this.issueCl=[];
    title.setTitle("RCM - Tool to update database.");
  }

  ngOnInit(): void {
    this.fetchAllClients();
   // this.fetchLatesClaimLLogs();
   
  }

  modal(){
      for(let cli of this.clients){
        if(cli.uuid===this.cName){
          this.issueClientName = cli.clientName;
          break;
      }
    }
      this.ele.modal = document.getElementById("myModal");
      this.ele.span = document.getElementsByClassName("close")[0];
      this.ele.modal.style.display = "block";
  }

    closeModal(){
      this.ele.modal.style.display = "none";
    }
   

  fetchLatesClaimLLogs(){
   
    let ths=this;
    ths.alert.showAlertPopup=false;
/* Sample Response
{"message":"","data":[{"source":"EAGLESOFT","status":"1","newClaimsCount":28,"officeUuid":"0078ceb5-5c6b-11e9-8320-0627f79ab72a","cd":"2022-12-28T17:29:25.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":27,"officeUuid":"fa484adb-ff40-11eb-882d-06f98f9a5400","cd":"2023-01-25T12:57:33.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":9,"officeUuid":"fc1d7afd-7df2-11e8-8432-8c16451459cd","cd":"2023-01-25T13:19:42.000+00:00"},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"f015515d-7df2-11e8-8432-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e5eec389-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c04a2dbe-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"a0f6c4ec-5bd6-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"065b3c52-57b5-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"be2c3847-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"da0c77a8-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e9f3d445-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"cc450da8-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e25ac8c4-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f64f25-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"dec0c6f2-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f73cbd-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"edc6f1c5-aaaf-11e8-8544-8c16451459cd","cd":null}],"status":200}
*/
this.sourceType="";
     ths.appService.fetchLatesClaimLLogs(this.cName,(res:any)=>{
       
       if (res.status=== 200){
        ths.log=res.data;


       }else{
         //ERROR
       }
      
     });
  }

  isUpdateClaimsChecked(value:any,update:any){
        let alreadyExist:any= this.log.some((e:any)=>{
          return !update;
        })
        if(!alreadyExist){
        this.hasUpdateClaims.push(value);
        }else{
          if(this.hasUpdateClaims.includes(value)){
            this.hasUpdateClaims.forEach((e:any,index:any)=>{
              if(e==value){
                this.hasUpdateClaims.splice(index,1)
              }
            })
          }
        }

  }

  pullFreshClaims(){
    let ths=this;
    ths.freshClaimPullModel.officeuuids=[];
   
    if (this.sourceType =='' &&  this.smilePoint.uuid==this.cName) {
      console.log("Please select Source");
      scrollTo(0,0)
      this.alert.showAlertPopup= true;
      this.alert.isError=true;
      this.alert.alertMsg= "Please Select Source to Update Database from";
      
      return;
    } 
    ths.setSource();
    ths.log.forEach(e => {
      if (e.update) ths.freshClaimPullModel.officeuuids.push(e.officeUuid);

    });
    ths.freshClaimPullModel.companyuuid=this.cName;
    if (ths.freshClaimPullModel.officeuuids.length>0){
      ths.loader=true;
      this.appService.pullFreshClaims(ths.freshClaimPullModel,(res:any)=>{
        if (res.status=== 200){
          ths.loader=false;
          res.data.forEach((d:FreshClaimPLogs) => {
             
             let filteredData:Array<FreshClaimPLogs> = ths.log.filter((l) => l.officeUuid === d.officeUuid);
             filteredData[0].source=d.source;
             filteredData[0].officeName=d.officeName;
             filteredData[0].cd=d.cd;
             filteredData[0].status=d.status;
             filteredData[0].newClaimsCount=d.newClaimsCount;
             console.log(d);
             
          });
          res.message == '' ? res.message = "Updated Successfully" : res.message;
          this.showAlertPopup(res);
          ths.log.forEach(e => {
            if (e.update) e.update=false;
            this.hasUpdateClaims=[];
          });
        }else{
          //ERROR
        }
      
      });
  }
 }

  selectSource(source: any) {
    this.sourceType = source;
  }
  
 //
 updateClaims(){
  console.log(this.esAgent);
  let ths= this;
  if (ths.esAgent || ths.googleSheet){
     ths.setSource();
     ths.pullFreshClaims();

  }
 }

 setSource(){
  let ths= this;
  console.log(ths.sourceType);
   if (ths.sourceType=='') ths.freshClaimPullModel.source='GOOGLESHEET';
   else  ths.freshClaimPullModel.source=ths.sourceType;
 /* if (ths.esAgent || ths.googleSheet){
    if (ths.esAgent) ths.freshClaimPullModel.source="EAGLESOFT";
    else ths.freshClaimPullModel.source="GOOGLESHEET";
 }*/
 }

 fetchAllClients(){
  let ths= this;
  ths.appService.getClientsName((res:any)=>{

    if (res.status=== 200){
      ths.clients=res.data;
      ths.clients.forEach((x)=>{
        if (x.clientName=='Smilepoint') {
          ths.smilePoint=x;
        } 
      });
      }
   });
 }

 expandCollapseBox(el:any){
  if(el === 'claimDetails'){
    this.expandCollapse.expandClaim = !this.expandCollapse.expandClaim
  }
  else if(el === 'teamRemarks'){
    this.expandCollapse.expandTeamRemarks = !this.expandCollapse.expandTeamRemarks
  }
}

selectAll(isAllSelected:any){
  if(isAllSelected){
    this.log.forEach((e:any)=>{
      console.log(e.update)
      if(!e.update){
        e.update = true;
        this.hasUpdateClaims.push(e.officeUuid)
      }
    })
  }else{
    this.log.forEach((e:any)=>{
      console.log(e.update)
      if(e.update){
        e.update = false;
        this.hasUpdateClaims=[];
      }
    })
  }
}

fetchIssueClaims(){

let ths= this;
ths.appService.fetchIssueClaims(ths.cName,(res:any)=>{

  if (res.status=== 200){
    ths.issueCl=res.data;
      this.modal();
    }
 });

 }

 showAlertPopup(res:any){
  this.alert.showAlertPopup = true;
  res.status==400 ? this.alert.isError=true : this.alert.isError=false;
  this.alert.alertMsg = res.message ? res.message : res.result.message;
}

}
