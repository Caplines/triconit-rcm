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
 
  isManageClient:boolean=true;
  clientData:any=[];
  officeData:any=[];
  showLoader:boolean=false;
  alert:any={'showAlertPopup':false,'alertMsg':''}
  companyUuid:any;


  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private _base: BaseService,private appService:ApplicationServiceService ,private title : Title) { 
    title.setTitle("Manage Office");
  }

  ngOnInit(): void {
    this.getcompanyData();
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
      // office['newField']= !office['newField']
      console.log(office.name)
  }


  saveClientName(clientDetails: any) {
    let { editable, newField, ...client } = clientDetails;
    if (!client.companyUuid) {

      this.appService.addNewClients(client, (callback: any) => {
        if (callback.status == 200) {
          console.log(callback);
          clientDetails['companyUuid'] = callback.data;
          clientDetails['editable']=false;
          clientDetails['newField']=false;
          this.alert.showAlertPopup = true;
          this.alert.alertMsg = callback.message == '' ? "New Client Added" : callback.message;
        }
      })
    } else{
      this.appService.editClient(client,(callback:any)=>{
        if(callback.status==200){
          console.log(callback);
          this.alert.showAlertPopup=true;
          this.alert.alertMsg = callback.message == ''? "Edit Successfully" : callback.message;
        }
      })
    }

    // if (office.name) {
    //   if (office.uuid) {
    //     let params: any = {
    //       "officeUuid": office.uuid,
    //       "officeName": office.name,
    //       'companyUuid':this.companyUuid
    //     }
    //     this.appService.editOfficeName(params, (callback: any) => {
    //       if (callback.result.status == 200) {
    //         this.alert.showAlertPopup = true;
    //         this.alert.alertMsg = callback.message ? callback.message : callback.result.message;
    //         console.log(callback)
    //         office.editable = false;
    //         office['newField']=false;
    //       }else if(callback.result.status == 400){
    //         this.alert.showAlertPopup = true;
    //         this.alert.alertMsg =  callback.result.message
    //           console.log(callback)
    //       }
    //     })
    //   }
    //   else {
    //     let params: any = {
    //       "companyUuid": this.companyUuid,
    //       "name": office.name
    //     }
    //     this.appService.addNewOffice(params, (callback: any) => {
    //       if (callback.result.status == 200) {
    //         this.alert.showAlertPopup = true;
    //         this.alert.alertMsg = callback.message ? callback.message : callback.result.message;
    //         console.log(callback)
    //         office.editable = false;
    //         office['newField']=false;
    //         //this.officeData.push({'name':office.name,'uuid':callback.result.data});
    //       } else if(callback.result.status == 400){
    //         console.log(callback)
    //         this.alert.showAlertPopup = true;
    //         this.alert.alertMsg = callback.message ? callback.message : callback.result.message;
    //       }else {
    //         console.log(callback)
    //       }
    //     })
    //   }
    // }
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


  // addNewHeader(header:any,idx:any){
  //   console.log(header,idx)

  // }
}

