import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { TokenStorageService } from '../../service/token-storage.service';


@Component({
  selector: 'app-manage-office',
  templateUrl: './manage-office.component.component.html',
    styleUrls: ['./manage-office.component.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ManageOfficeComponent implements OnInit {
 
  isManageOffice:boolean=true;
  companyData:any=[];
  officeData:any=[];
  showLoader:boolean=false;
  alert:any={'showAlertPopup':false,'alertMsg':''}
  companyUuid:any;


  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private _base: BaseService,private appService:ApplicationServiceService) { }

  ngOnInit(): void {
    this.getcompanyData();
  }

  getcompanyData(){
    this.appService.fetchCompanyNameData((callback:any)=>{
      if(callback.status){
        this.companyData = callback.data.data;
        console.log(callback)
      }
    })
  }
  getOfficesByCompany(event:any){
    this.showLoader=true;
    this.companyData.find((e:any)=>{
      if(e.name === event.target.value){
        this.companyUuid = e.companyUuid;
        this.appService.fetchOfficeByCompany(e.companyUuid,(callback:any)=>{
            if(callback.status){
              this.showLoader=false;
              this.officeData = callback.data.data;
             this.officeData =  this.officeData.map((e:any)=>({...e,'editable':false}))
            }
        })
      }
    })
}
  selectCompany(e:any){

  }

  selectOffice(e:any){

  }
 
  editOfficeName(office:any,index:any){
      office.editable = !office.editable;
      
      console.log(office.name)
  }

  saveOfficeName(office: any) {
    if (office.name) {
      if (office.uuid) {
        let params: any = {
          "officeUuid": office.uuid,
          "officeName": office.name
        }
        this.appService.editOfficeName(params, (callback: any) => {
          if (callback.result.status == 200 || callback.result?.data?.status == 200) {
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = callback.message ? callback.message : callback.result.data.message;
            console.log(callback)
            office.editable = false;
          }else{
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = 'Something Went Wrong'
          }
        })
      }
      else {
        let params: any = {
          "companyUuid": this.companyUuid,
          "name": office.name
        }
        this.appService.addNewOffice(params, (callback: any) => {
          if (callback.result.status == 200 || callback.result?.data?.status == 200) {
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = callback.message ? callback.message : callback.result.data.message;
            console.log(callback)
            office.editable = false;
          } else{
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = 'Something Went Wrong'
          }
        })
      }
    }
  }

  addNewOffice(){
    this.officeData.unshift({'name':'','editable':true})
  }
}

