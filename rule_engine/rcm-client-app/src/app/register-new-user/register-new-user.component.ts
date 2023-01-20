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
  constructor(private router: Router, private _baseService:BaseService ,private fb : FormBuilder) {

    this.userDetails = this.fb.group({
      'firstName' : ['',[Validators.required,Validators.minLength(3)]],
      'lastName' : ['',[Validators.required,Validators.minLength(3)]],
      'email' : ['',Validators.email],
      'password' : ['',[Validators.required,Validators.minLength(6)]],
      'companyName' : ['',Validators.required],
      'officeId' : [''],
      'teamId' : ['',Validators.required],
      'userRole' : ['',Validators.required],
    })
   }

  ngOnInit(): void {
    this.getcompanyData();
    this.userDetails.reset();
  }

  registerNewUser(){
    this._baseService.registerUser(this.userDetails.value,(callback:any)=>{
      if(callback.status && callback.result.message !=''){
        console.log(callback)
        this.alert.showAlertPopup = true;
        this.alert.alertMsg = callback.result.message;
        if(callback.result.message === "User has been created"){
          this.userRoles = this.userRolesData = this.officeData = this.teamData = [];
          this.userDetails.reset();
        }
      } 
    })
    }
  
  getTeamsData(){
    let isSmilePoint: boolean = false;
    if (this.userDetails.value.companyName === "Capline") {
      isSmilePoint = true;
    }
    this._baseService.getTeamsData(isSmilePoint,(callback:any)=>{
      console.log(callback)
      if(callback.status){
        this.teamData = callback.result.data
      }
    })
  }

  getOfficeData(){
    this._baseService.getOfficeData((callback:any)=>{
      if(callback.status){
        this.officeData = callback.result.data
      }
    })
  }

  getUserRoleData(){
    let isSmilePoint: boolean = false;
    if (this.userDetails.value.companyName === "Capline") {
      isSmilePoint = true;
    }
    this._baseService.getUserRoleData(isSmilePoint,(callback:any)=>{
      if(callback.status){
        this.userRolesData = callback.result.data
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
  }

  getOfficesByCompany(event:any){
      this.companyData.find((e:any)=>{
        if(e.name === event.target.value){
          this._baseService.getOfficeByCompany(e.companyUuid,(callback:any)=>{
              if(callback.status){
                this.officeData = callback.result.data.data;
                this.getUserRoleData();
                this.getTeamsData();
              }
          })
        }
      })
  }

}
