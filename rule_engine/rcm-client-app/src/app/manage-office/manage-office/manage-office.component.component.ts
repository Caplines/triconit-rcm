import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { BaseService } from 'src/app/service/base-service.service';


@Component({
  selector: 'app-manage-office',
  templateUrl: './manage-office.component.component.html',
    styleUrls: ['./manage-office.component.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ManageOfficeComponent implements OnInit {
 
  companyData:any=[];
  officeData:any=[];
  showLoader:boolean=false;
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};
  selectedCompany:any;


  constructor(private appService:ApplicationServiceService ,private title : Title) { 
    title.setTitle("Manage Office");
  }

  ngOnInit(): void {
    this.getcompanyData();
  }

  getcompanyData(){
    this.appService.fetchCompanyNameData((callback:any)=>{
      if(callback.status){
        this.companyData = callback.data.data;
      
      }
    })
  }
  getOfficesByCompany(companyUuid?:any){
    this.showLoader=true;
        this.appService.fetchOfficeByCompany(companyUuid,(callback:any)=>{
            if(callback.status){
              this.showLoader=false;
              this.officeData = callback.data.data.map((e:any)=>({...e,'editable':false}));
            }
    })
}
  selectCompany(event:any){
    
      this.selectedCompany =this.companyData.filter((el:any) => el.companyUuid==event.target.value)[0];
      this.getOfficesByCompany(this.selectedCompany.companyUuid);
  }

  selectOffice(e:any){

  }
 
  editOfficeName(office:any){
      office.editable = !office.editable;
      // office['newField']= !office['newField']
      
  }

  saveOfficeName(office: any) {
    if (office.name) {
      if (office.uuid) {
        let params: any = {
          "officeUuid": office.uuid,
          "officeName": office.name,
          'companyUuid':this.selectedCompany.companyUuid
        }
        this.appService.editOfficeName(params, (callback: any) => {
          if (callback.status == 200) {
            this.showAlertPopup(callback);
            office.editable = false;
            office['newField']=false;
          }else if(callback.status == 400){
            this.showAlertPopup(callback);
          }
        })
      }
      else {
        let params: any = {
          "companyUuid": this.selectedCompany.companyUuid,
          "name": office.name
        }
        this.appService.addNewOffice(params, (callback: any) => {
          if (callback.status == 200) {
            this.showAlertPopup(callback);
            office.editable = false;
            office['newField']=false;
            this.officeData.forEach((e:any)=>{
              if(e.name == office.name){
                this.officeData.push({'name':office.name,'uuid':callback.data.officeUuid,'key':callback.data.id,'editable':false,'newField':false})
              }
            })
            this.officeData.splice(0,1);
          } else if(callback.status == 400){
            this.showAlertPopup(callback);
          }else {
           }
        })
      }
    } else{
      this.alert.showAlertPopup = true;
      this.alert.isError = true;
      this.alert.alertMsg = "Field Cannot Be Empty";
      setTimeout(() => {this.alert.showAlertPopup=false;}, 2500);

    }
  }

  addNewOffice(){
    this.officeData.unshift({'name':'','editable':true,'newField':true})
  }

  showAlertPopup(res:any){
    this.alert.showAlertPopup = true;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2000);
    res.status==400 ? this.alert.isError=true : this.alert.isError=false;
    this.alert.alertMsg = res.message ? res.message : res.result.message;
  }
}

