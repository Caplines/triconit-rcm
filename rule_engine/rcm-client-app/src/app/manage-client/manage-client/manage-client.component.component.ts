import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { BaseService } from 'src/app/service/base-service.service';


@Component({
  selector: 'app-manage-client',
  templateUrl: './manage-client.component.component.html',
    styleUrls: ['./manage-client.component.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ManageClientComponent implements OnInit {
 
  clientData:any=[];
  officeData:any=[];
  showLoader:boolean=false;
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};
  companyUuid:any;


  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private _base: BaseService,public appService:ApplicationServiceService ,private title : Title) { 
    title.setTitle("Manage Office");
  }

  ngOnInit(): void {
    this.getcompanyData();
    this.appService.setPaddingRightContainer();
  }

  getcompanyData(){
    this.appService.fetchClient((callback:any)=>{
      if(callback.status){
        this.clientData = callback.data;
      //  this.clientData =  this.clientData[0].header.push(
      //         {
      //           "name":"Revenue Sheet",
      //           "google_sheet_id":"45v2liaL5DGZAXKRd4Vt-nFmxttrDO_hC4k8Hm-mMKtKZ",
      //           "google_sheet_sub_id":"600",
      //           "google_sheet_sub_name":"RS"
      //         }
      //       )  
      //     }
      //     this.clientData = callback.data
          this.clientData = this.clientData.map((x:any)=>({...x,'editable':false}))
          console.log(this.clientData)
      }
        })
  }
 
  enableEditing(office:any){
      office.editable = !office.editable;
      console.log(office.name)
  }


  saveClientName(clientDetails: any) {
    if(clientDetails.header[0].google_sheet_id !=='' && clientDetails.clientName !=='' && clientDetails.header[0].google_sheet_sub_id!=='' &&  clientDetails.header[0].google_sheet_sub_name !==''){
    let { editable, newField, ...client } = clientDetails;
    if (!client.companyUuid) {

      this.appService.addNewClients(client, (callback: any) => {
        if (callback.status == 200) {
          console.log(callback);
          clientDetails['companyUuid'] = callback.data;
          clientDetails['editable']=false;
          clientDetails['newField']=false;
          callback.message == '' ? callback.message = "New Client Added" : callback.message;
          this.showAlertPopup(callback);
        } else{
          this.showAlertPopup(callback);
        }
      })
    } else{
      this.appService.editClient(client,(callback:any)=>{
        if(callback.status==200){
          console.log(callback);
          clientDetails['editable']=false;
          clientDetails['newField']=false;
          callback.message == ''? callback.message = "Record has been updated" : callback.message;
          this.showAlertPopup(callback);
        }else{
          this.showAlertPopup(callback);
        }
      })
    }
  } else{ 
    this.alert.showAlertPopup = true;
    this.alert.isError = true;
    this.alert.alertMsg = "Field Cannot Be Empty";
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2500);
    scrollTo(0,0);
  }

  }

  addNewClient(){
    this.clientData.unshift({'clientName':'','companyUuid':'',
    'header': [ {
      "name":"Timely Filling Sheet",
      "google_sheet_id":"",
      "google_sheet_sub_id":"",
      "google_sheet_sub_name":""
    }],
    'editable':true,
    'newField':true
  })
  }

  showAlertPopup(res:any){
    this.alert.showAlertPopup = true;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
    res.status==400 ? this.alert.isError=true : this.alert.isError=false;
    this.alert.alertMsg = res.message ? res.message : res.result.message;
    scrollTo(0,0);
  }

  // addNewHeader(header:any,idx:any){
  //   console.log(header,idx)

  // }
}

