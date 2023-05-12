import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import {FreshClaimPullModel} from '../../models/fresh.claim.pull.model';
import {FreshClaimPLogs} from '../../models/fresh.claim.log';
import {ClientModel} from '../../models/client.model';
import {IssueClaimModel} from '../../models/issue.claim.model';
import { Title } from '@angular/platform-browser';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';
import Utils from '../../util/utils';

@Component({
  selector: 'tool-update-db',
  templateUrl: './tool-update.component.html',
  styleUrls: ['./tool-update.component.scss']
})
export class ToolUpdateComponent implements OnInit {

  freshClaimPullModel: FreshClaimPullModel = new FreshClaimPullModel();

  // log : Array<FreshClaimPLogs>;
  log : any=[];
  clients : Array<ClientModel>;
  issueCl : Array<IssueClaimModel>;
  smilePoint: ClientModel;
  esAgent:any;
  googleSheet:any;
  loader:any={'showLoader':false,'updateClaims':false,'exportPDFLoader':false,'exportCSVLoader':false};
  cName:string="-1";
  sourceType:string="";
  expandCollapse:any={'expandClaim':true,'expandTeamRemarks':true}
  hasUpdateClaims:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':''}
  issueClientName:any='';
  ele:any={'modal':'','span':''}
  date:any;
  gsLink:any='';
  clientName:string='';
  
  constructor(public appService: ApplicationServiceService,private title:Title) { 

    this.log = [];//{} as FreshClaimPLogs;
    this.clients = [];
    this.smilePoint={};
    this.issueCl=[];
    title.setTitle(Utils.defaultTitle + "Tool to Update Database");
  }

  ngOnInit(): void {
    this.fetchAllClients();
    this.appService.setPaddingRightContainer();
    // this.fetchLatesClaimLLogs();
    this.clientName = localStorage.getItem("selected_clientName");
  }

  fetchLatesClaimLLogs(cUuid:any){
    let ths=this;
    ths.alert.showAlertPopup=false;
/* Sample Response
{"message":"","data":[{"source":"EAGLESOFT","status":"1","newClaimsCount":28,"officeUuid":"0078ceb5-5c6b-11e9-8320-0627f79ab72a","cd":"2022-12-28T17:29:25.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":27,"officeUuid":"fa484adb-ff40-11eb-882d-06f98f9a5400","cd":"2023-01-25T12:57:33.000+00:00"},{"source":"EAGLESOFT","status":"1","newClaimsCount":9,"officeUuid":"fc1d7afd-7df2-11e8-8432-8c16451459cd","cd":"2023-01-25T13:19:42.000+00:00"},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"f015515d-7df2-11e8-8432-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e5eec389-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c04a2dbe-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"a0f6c4ec-5bd6-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"065b3c52-57b5-11e9-8320-0627f79ab72a","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"be2c3847-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"da0c77a8-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e9f3d445-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"cc450da8-aaae-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"e25ac8c4-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f64f25-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"dec0c6f2-aaaf-11e8-8544-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"c0f73cbd-9bc5-11e8-9f0b-8c16451459cd","cd":null},{"source":"","status":"0","newClaimsCount":0,"officeUuid":"edc6f1c5-aaaf-11e8-8544-8c16451459cd","cd":null}],"status":200}
*/
this.sourceType="";

     ths.appService.fetchLatesClaimLLogs(cUuid,(res:any)=>{
       
       if (res.status=== 200){
        ths.log=res.data;
        this.loader.showLoader = false;
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
    ths.loader.updateClaims=true;
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
    ths.log.forEach((e:any) => {
      if (e.update) ths.freshClaimPullModel.officeuuids.push(e.officeUuid);

    });
    ths.freshClaimPullModel.companyuuid=this.cName;
    if (ths.freshClaimPullModel.officeuuids.length>0){
      ths.loader.showLoader=true;
      ths.loader.updateClaims=false;
      this.appService.pullFreshClaims(ths.freshClaimPullModel,(res:any)=>{
        if (res.status=== 200){
          ths.loader.showLoader=false;
          res.data.forEach((d:FreshClaimPLogs) => {
             
             let filteredData:Array<FreshClaimPLogs> = ths.log.filter((l:any) => l.officeUuid === d.officeUuid);
             filteredData[0].source=d.source;
             filteredData[0].officeName=d.officeName;
             filteredData[0].cd=d.cd;
             filteredData[0].status=d.status;
             filteredData[0].newClaimsCount=d.newClaimsCount;
             console.log(d);
             

          });
          res.message == '' ? res.message = "Updated Successfully" : res.message;
          this.showAlertPopup(res);
          ths.log.forEach((e:any) => {
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
  updateClaims() {
    console.log(this.esAgent);
    let ths = this;
    if (ths.esAgent || ths.googleSheet) {
      ths.setSource();
      ths.pullFreshClaims();

    }
  }

  setSource() {
    let ths = this;
    console.log(ths.sourceType);
    if (ths.sourceType == '') ths.freshClaimPullModel.source = 'GOOGLESHEET';
    else ths.freshClaimPullModel.source = ths.sourceType;
    /* if (ths.esAgent || ths.googleSheet){
       if (ths.esAgent) ths.freshClaimPullModel.source="EAGLESOFT";
       else ths.freshClaimPullModel.source="GOOGLESHEET";
    }*/
  }

  fetchAllClients() {
    let ths = this;
    this.loader.showLoader = true;
    ths.appService.getClientsName((res: any) => {

      if (res.status === 200) {
        ths.clients = res.data;
        ths.clients.forEach((x) => {
          if (x.clientName == 'Smilepoint') {
            ths.smilePoint = x;
          }
        });
        let loginUserType = localStorage.getItem('loginAs');
      if(ths.clients && loginUserType!= 'Admin') {
          let clientName = localStorage.getItem('selected_clientName');
          ths.clients.find((e:any)=> {
            if(clientName == e.clientName)
            this.cName = e.uuid;
          });
          this.fetchLatesClaimLLogs(this.cName);
        }
      }
    });
  }

  expandCollapseBox(el: any) {
    if (el === 'claimDetails') {
      this.expandCollapse.expandClaim = !this.expandCollapse.expandClaim
    }
    else if (el === 'teamRemarks') {
      this.expandCollapse.expandTeamRemarks = !this.expandCollapse.expandTeamRemarks
    }
  }

  selectAll(isAllSelected: any) {
    if (isAllSelected) {
      this.log.forEach((e: any) => {
        console.log(e.update)
        if (!e.update) {
          e.update = true;
          this.hasUpdateClaims.push(e.officeUuid)
        }
      })
    } else {
      this.log.forEach((e: any) => {
        console.log(e.update)
        if (e.update) {
          e.update = false;
          this.hasUpdateClaims = [];
        }
      })
    }
  }

  saveToPdf(divName: any) {
    this.loader.exportPDFLoader=true;
    let m:any=document.querySelector(".table-wrapper-scroll-y");
    m.classList.remove('table-wrapper-scroll-y');
    m.classList.remove('table-inner-scrollbar');
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
       // Insert office name
       pdf.setFontSize(10);  // Adjust the font size as needed
       pdf.text(`RCM Tool-${this.clientName}`, 3, 10);
       pdf.addImage(content, "PNG", 0, 15, width, height);
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_Tool_To_Update_Database_${this.date}`);
      this.loader.exportPDFLoader=false;
      m.classList.add('table-wrapper-scroll-y');
      m.classList.add('table-inner-scrollbar');
    });

  }

  exportToCsv() {
    this.loader.exportCSVLoader=true;
    let options: any = {
      showLabels: true,
      headers: ["Office Name", "Source","Database Updation Done","Last Attempted On","Total No. of New Claims Added", ]
    }
    let excelData: any;
    excelData = [...this.log];
    excelData = excelData.map((e: any) => {
      if (e.cd) {
        let date: Date = new Date(e.cd);
        e = { ...e, cd: `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}` }
      } else {
        e = {...e,cd : '-'};
      }
      if (e.status == 1) {
        e = { ...e, status: "YES" }
      } else if (e.status == 0) {
        e = { ...e, status: "NO" };
      }
      if (e.source == "") {
        return { ...e, source: "EAGLESOFT" };
      }
      return e;
    })
    excelData = excelData.map(({ officeUuid, ...newData }: any) => newData) //to remove required properties in excel
    excelData = excelData.map((e:any)=>{
      return{
        "OfficeName":e.officeName,
        "Source":e.source,
        "DatabaseUpdationDone":e.status,
        "Last Attempted On":e.cd,
        "TotalNo.ofNewClaimsAdded":e.newClaimsCount
      }
    })
    this.date = new Date();
    this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_Tool_To_Update_Database_${this.date}`, options);
    this.loader.exportCSVLoader=false;

  }

  fetchGSheet(){
    if(!this.gsLink){
      this.appService.fetchGSheet((res:any)=>{
        if(res.status==200){
        res.data.find((e:any)=>{
          if(e.name === 'RCM DataBase'){
            this.gsLink = e.google_sheet_id;
            window.open(`https://docs.google.com/spreadsheets/d/${this.gsLink}`,"_blank");
            return;
          }
        })
      }
    })
  }else{
    window.open(`https://docs.google.com/spreadsheets/d/${this.gsLink}`,"_blank");
  }
}

 showAlertPopup(res:any){
  this.alert.showAlertPopup = true;
  setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
  res.status==400 ? this.alert.isError=true : this.alert.isError=false;
  this.alert.alertMsg = res.message ? res.message : res.result.message;
}

}
