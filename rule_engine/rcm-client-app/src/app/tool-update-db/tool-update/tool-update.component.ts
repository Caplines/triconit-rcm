import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import {FreshClaimPullModel} from '../../models/fresh.claim.pull.model';
import {FreshClaimPLogs} from '../../models/fresh.claim.log';
import {ClientModel} from '../../models/client.model';

@Component({
  selector: 'tool-update-db',
  templateUrl: './tool-update.component.html',
  styleUrls: ['./tool-update.component.scss']
})
export class ToolUpdateComponent implements OnInit {

  freshClaimPullModel: FreshClaimPullModel = new FreshClaimPullModel();

  log : Array<FreshClaimPLogs>;
  clients : Array<ClientModel>;
  smilePoint: ClientModel;
  esAgent:any;
  googleSheet:any;
  loader:boolean=false;
  cName:string="-1";
  sourceType:string="";
  
  constructor(private appService: ApplicationServiceService) { 

    this.log = [];//{} as FreshClaimPLogs;
    this.clients = [];
    this.smilePoint={};
  }

  ngOnInit(): void {
    this.fetchAllClients();
   // this.fetchLatesClaimLLogs();
  }

  fetchLatesClaimLLogs(){
    let ths=this;
/* Sample Response
{"message":"","data":[{"source":"EAGLESOFT","status":"1","newClaimsCount":28,"officeUuid":"0078ceb5-5c6b-11e9-8320-0627f79ab72a","cd":"2022-12-28T17:29:25.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":27,"officeUuid":"fa484adb-ff40-11eb-882d-06f98f9a5400","cd":"2023-01-25T12:57:33.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":9,"officeUuid":"fc1d7afd-7df2-11e8-8432-8c16451459cd","cd":"2023-01-25T13:19:42.000+00:00"},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"f015515d-7df2-11e8-8432-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e5eec389-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c04a2dbe-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"a0f6c4ec-5bd6-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"065b3c52-57b5-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"be2c3847-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"da0c77a8-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e9f3d445-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"cc450da8-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e25ac8c4-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f64f25-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"dec0c6f2-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f73cbd-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"edc6f1c5-aaaf-11e8-8544-8c16451459cd","cd":null}],"status":200}
*/
console.log(this.cName);
     ths.appService.fetchLatesClaimLLogs(this.cName,(res:any)=>{
       
       if (res.status=== 200){
        ths.log=res.data;
        console.log(ths.log);


       }else{
         //ERROR
       }
      
     });
  }


  pullFreshClaims(){
    let ths=this;
    ths.freshClaimPullModel.officeuuids=[];
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
           debugger;
          res.data.forEach((d:FreshClaimPLogs) => {
             
             let filteredData:Array<FreshClaimPLogs> = ths.log.filter((l) => l.officeUuid === d.officeUuid);
             filteredData[0].source=d.source;
             filteredData[0].officeName=d.officeName;
             filteredData[0].cd=d.cd;
             filteredData[0].status=d.status;
             filteredData[0].newClaimsCount=d.newClaimsCount;
             console.log(d);
             
          });
        }else{
          //ERROR
        }
      
      });
  }
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
  if (ths.sourceType=='') ths.freshClaimPullModel.source='GOOGLESHEET';
 /* if (ths.esAgent || ths.googleSheet){
    if (ths.esAgent) ths.freshClaimPullModel.source="EAGLESOFT";
    else ths.freshClaimPullModel.source="GOOGLESHEET";
 }*/
 }

 fetchAllClients(){
  let ths= this;
  ths.appService.getAllClients((res:any)=>{

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

}
