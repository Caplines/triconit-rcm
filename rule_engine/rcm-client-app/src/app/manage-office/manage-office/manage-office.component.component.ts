import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
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

  constructor(private _baseService: BaseService) { }

  ngOnInit(): void {
    this.getcompanyData();
  }

  getcompanyData(){
    this._baseService.getCompanyData((callback:any)=>{
      if(callback.status){
        this.companyData = callback.result.data.data;
        console.log(callback.result)
      }
    })
  }
  getOfficesByCompany(event:any){
    this.showLoader=true;
    this.companyData.find((e:any)=>{
      if(e.name === event.target.value){
        this.companyUuid = e.companyUuid;
        this._baseService.getOfficeByCompany(e.companyUuid,(callback:any)=>{
            if(callback.status){
              this.showLoader=false;
              this.officeData = callback.result.data.data;
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
        this._baseService.editOfficeName(params, (callback: any) => {
          if (callback.status == 200 || callback.result?.data?.status == 200) {
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = callback.result.message ? callback.result.message : callback.result.data.message;
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
        this._baseService.addNewOffice(params, (callback: any) => {
          if (callback.status == 200 || callback.result?.data?.status == 200) {
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = callback.result.message ? callback.result.message : callback.result.data.message;
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
