import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseService } from '../service/base-service.service';
import {FormBuilder,Validators} from "@angular/forms";

@Component({
  selector: 'app-register-new-user',
  templateUrl: './register-new-user.component.html',
  styleUrls: ['./register-new-user.component.css']
})
export class RegisterNewUserComponent implements OnInit {

  userDetails:any;
  teamData:any=[];
  officeData:any=[];
  userRolesData:any=[];
  companyData:any=[];
  userRoles:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':''}
  showLoader:boolean=false;
  constructor(private router: Router, private _baseService:BaseService ,private fb : FormBuilder) {

    this.userDetails = this.fb.group({
      'firstName' : ['',[Validators.required,Validators.minLength(3)]],
      'lastName' : ['',[Validators.required,Validators.minLength(3)]],
      'email' : ['',Validators.email],
      'password' : ['',[Validators.required,Validators.minLength(6)]],
      'companyName' : ['',Validators.required],
      'officeId' : [''],
      'teamId' : ['0',Validators.required],
      'userRole' : ['',Validators.required],
    })
   }

  ngOnInit(): void {
    this.getcompanyData();
    this.userDetails.reset();
  }

  registerNewUser(){
    if(this.userDetails.value.teamId == undefined || this.userDetails.value.teamId == null){this.userDetails.controls.teamId.setValue(0);}
    this._baseService.registerUser(this.userDetails.value,(callback:any)=>{
      if(callback.status && callback.result.message !=''){
        console.log(callback)
        this.alert.showAlertPopup = true;
        this.alert.alertMsg = callback.result.message;
        if(callback.result.message === "User has been created"){
          this.companyData =this.userRoles = this.userRolesData = this.officeData = this.teamData = [];
          this.userDetails.reset();
          this.getcompanyData();
        }
      } else { 
        if(callback.result.message === ''){
          this.alert.showAlertPopup = true;
          this.alert.alertMsg = 'Something Went Wrong';
        }
      }
    })
    }
  
  getTeamsData(companyName:any){
    this.showLoader=true;
    this._baseService.getTeamsData(companyName,(callback:any)=>{
      if(callback.status){
        this.showLoader = false;
        this.teamData = callback.result.data;
      }
    })
  }

  getOfficeData(){
    this.showLoader=true;
    this._baseService.getOfficeData((callback:any)=>{
      if(callback.status){
        this.showLoader = false;
        this.officeData = callback.result.data
      }
    })
  }

  getcompanyData(){
    this._baseService.getCompanyData((callback:any)=>{
      if(callback.status){
        this.companyData = callback.result.data.data;
      }
    })
  }

  selectUserRole(event:any){
      if(this.userRoles.includes(event.target.id)){
        this.userRoles.find((e:any,index:any)=>{
          if(e == event.target.id){
            this.userRoles.splice(index,1)
          }
        })
      }else{
        this.userRoles.push(event.target.id)
      }
      this.userDetails.controls.userRole.setValue(this.userRoles);
      this.changeTeamMandatoryStatus(event.target.id);
  }

  
  changeTeamMandatoryStatus(role: any) {

    let k = this.userRolesData;
    k.find((e: any) => {
      if (e.roleId === role) {
        if (e.teamMandatory && (this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
          this.userDetails.controls.teamId.setValidators([Validators.required]);
          this.userDetails.controls.teamId.updateValueAndValidity();
        } else if (!(this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
          this.userDetails.controls.teamId.setValidators();
          this.userDetails.controls.teamId.updateValueAndValidity();
        } else {
          this.userDetails.controls.teamId.setValidators([Validators.required]);
          this.userDetails.controls.teamId.updateValueAndValidity();
        }

      }
    })

  }

  selectCompany(e:any){
    this.userDetails.controls.companyName.setValue(e.target.value);
  }

  selectOffice(e:any){
    this.userDetails.controls.officeId.setValue(e.target.value);
  }

  selectTeamName(e:any){
    this.userDetails.controls.teamId.setValue(e.target.value);
  }

  getOfficesByCompany(event:any){
      this.companyData.find((e:any)=>{
        if(e.name === event.target.value){
          this._baseService.getOfficeByCompany(e.companyUuid,(callback:any)=>{
              if(callback.status){
                this.userDetails.controls.officeId.setValue('');
                this.userDetails.controls.teamId.setValue('');
                this.officeData = callback.result.data.data;
                this.getTeamsData(event.target.value);
                this.userRoles= [];
              }
          })
        }
      })
  }

  getRolesByCompany(event:any){
    this._baseService.getRolesByCompany(event.target.value,(callback:any)=>{
      if(callback.status){
        this.userRoles =[];
        this.userDetails.controls.userRole.setValue('');
        this.userRolesData = callback.result.data;
      }
    })
  }

  getRolesByTeam(event:any){
    this._baseService.getRolesByTeam(event.target.value,(callback:any)=>{
      if(callback.status){
        this.userRoles = [];
        this.userDetails.controls.userRole.setValue('');
        this.userRolesData = callback.result.data;
      }
    })
  }

}