import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';
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

  constructor(private _base: BaseService,private appService:ApplicationServiceService ,private title : Title) { 
    title.setTitle("Manage Office");
  }

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
  getOfficesByCompany(companyUuid?:any){
    this.showLoader=true;
        this.appService.fetchOfficeByCompany(companyUuid,(callback:any)=>{
            if(callback.status){
              this.showLoader=false;
              this.officeData = callback.data.data;
             this.officeData =  this.officeData.map((e:any)=>({...e,'editable':false}))
            }
    })
}
  selectCompany(event:any){
      this.companyUuid = event.target.value;
      this.getOfficesByCompany(this.companyUuid)
  }

  selectOffice(e:any){

  }
 
  editOfficeName(office:any){
      office.editable = !office.editable;
      // office['newField']= !office['newField']
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
          if (callback.result.status == 200) {
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = callback.message ? callback.message : callback.result.message;
            console.log(callback)
            office.editable = false;
            office['newField']=false;
          }else{
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = 'Something Went Wrong'
              console.log(callback)
          }
        })
      }
      else {
        let params: any = {
          "companyUuid": this.companyUuid,
          "name": office.name
        }
        this.appService.addNewOffice(params, (callback: any) => {
          if (callback.result.status == 200) {
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = callback.message ? callback.message : callback.result.message;
            console.log(callback)
            office.editable = false;
            office['newField']=false;
            this.officeData.push({'name':office.name,'uuid':callback.result.data});
          } else if(callback.result.data.status == 400){
            console.log(callback)
            this.alert.showAlertPopup = true;
            this.alert.alertMsg = callback.message ? callback.message : callback.result.message;
          }else {
            console.log(callback)
          }
        })
      }
    }
  }

  addNewOffice(){
    this.officeData.unshift({'name':'','editable':true,'newField':true})
  }
}

