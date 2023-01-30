import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import {FreshClaimPullModel} from '../../models/fresh.claim.pull.model';
import {FreshClaimPLogs} from '../../models/fresh.claim.log';


@Component({
  selector: 'tool-update-db',
  templateUrl: './tool-update.component.html',
  styleUrls: ['./tool-update.component.scss']
})
export class ToolUpdateComponent implements OnInit {

  freshClaimPullModel: FreshClaimPullModel = new FreshClaimPullModel();

  log : Array<FreshClaimPLogs>;

  
  constructor(private appService: ApplicationServiceService) { 

    this.log = [];//{} as FreshClaimPLogs;
  }

  ngOnInit(): void {
    this.pullFreshClaims();
    this.fetchLatesClaimLLogs();
  }

  fetchLatesClaimLLogs(){
/* Sample Response
{"message":"","data":[{"source":"EAGLESOFT","status":"1","newClaimsCount":28,"officeUuid":"0078ceb5-5c6b-11e9-8320-0627f79ab72a","cd":"2022-12-28T17:29:25.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":27,"officeUuid":"fa484adb-ff40-11eb-882d-06f98f9a5400","cd":"2023-01-25T12:57:33.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":9,"officeUuid":"fc1d7afd-7df2-11e8-8432-8c16451459cd","cd":"2023-01-25T13:19:42.000+00:00"},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"f015515d-7df2-11e8-8432-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e5eec389-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c04a2dbe-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"a0f6c4ec-5bd6-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"065b3c52-57b5-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"be2c3847-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"da0c77a8-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e9f3d445-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"cc450da8-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e25ac8c4-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f64f25-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"dec0c6f2-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f73cbd-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"edc6f1c5-aaaf-11e8-8544-8c16451459cd","cd":null}],"status":200}
*/
     this.appService.fetchLatesClaimLLogs((res:any)=>{
       if (res.status=== 200){
        this.log=res.data;
        console.log(this.log);
       }else{
         //ERROR
       }
      
     });
  }


  pullFreshClaims(){

    let ths=this;
    ths.freshClaimPullModel.companyuuid="";
    ths.freshClaimPullModel.officeuuids.push("fc1d7afd-7df2-11e8-8432-8c16451459cd");
    ths.freshClaimPullModel.source="EAGLESOFT";//GOOGLESHEET
    this.appService.pullFreshClaims(ths.freshClaimPullModel,(res:any)=>{
      if (res.status=== 200){

      }else{
        //ERROR
      }
     
    });
 }

  

}
