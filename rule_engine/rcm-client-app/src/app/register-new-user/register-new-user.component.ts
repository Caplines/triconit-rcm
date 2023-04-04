import { Component, OnInit } from '@angular/core';
import {FormBuilder,Validators} from "@angular/forms";
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';

@Component({
  selector: 'app-register-new-user',
  templateUrl: './register-new-user.component.html',
  styleUrls: ['./register-new-user.component.css']
})
export class RegisterNewUserComponent implements OnInit {

  userDetails:any;
  teamData:any=[];
  companyData:any=[];
  userRoleId:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};
  showLoader:boolean=false;
  userRoleByTeam:any=[];
  showPassword:boolean=false;
  activeUserClients:any=[];
  constructor(private fb : FormBuilder, public appService: ApplicationServiceService,private title : Title) {
    title.setTitle("Register New User");
    this.userDetails = this.fb.group({
      'firstName' : ['',[Validators.required,Validators.minLength(3),Validators.maxLength(25),Validators.pattern("[a-zA-Z]*")]],
      'lastName' : ['',[Validators.required,Validators.minLength(3),Validators.maxLength(25),Validators.pattern("[a-zA-Z]*")]],
      'email' : ['',[Validators.required,Validators.email,Validators.maxLength(100),Validators.pattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")]],
      'password': [ '', [ Validators.required, Validators.minLength(6), Validators.maxLength(20),]
     ],
      'companyUuid' : ['',Validators.required],
      'teamId' : ['',Validators.required],
      'userRole' : ['',Validators.required],
    })
   }

   teamId:any=[];
   clientId:any=[];
   currentRoleName:string='';
  ngOnInit(): void {
    this.getCompanyData();
    this.getTeamsData();
    this.getRoles();
    this.userDetails.reset();
    this.appService.setPaddingRightContainer();
    this.currentRoleName = localStorage.getItem("selected_roleName");
  }

  registerNewUser(){
    if(this.userDetails.value.teamId == null ){
      this.userDetails.value.teamId = [];
    }
    this.appService.registerUser(this.userDetails.value,(callback:any)=>{
      if(callback.status == 200){
        console.log(callback)
        this.showAlertPopup(callback);
        this.companyData = this.userRoleId = this.teamData = this.userRoleByTeam = [];
        this.userDetails.reset();
        setTimeout(() => {
          location.reload();
        }, 0);
      } else if(callback.status == 400) { 
        this.showAlertPopup(callback);
      }
    })
  }

  getTeamsData(){
    this.showLoader=true;
    this.appService.fetchTeamsNameData((callback:any)=>{
      if(callback.status){
        this.showLoader = false;
        this.teamData = callback.data;
      }
    })
  }

  getCompanyData(){
    this.appService.fetchClients((callback:any)=>{
      if(callback.status){
        this.activeUserClients = this.appService.getActiveClients();
        // this.companyData = [...this.activeUserClients,...callback.data];
        this.companyData = callback.data.filter((objB:any) => this.activeUserClients.some((objA:any) => objA.id === objB.uuid));
        console.log(this.companyData);
        
      }
    })
  }
  
  getRoles(){
      this.appService.fetchRoles((callback:any)=>{
        if(callback.status){
          this.userRoleByTeam = callback.data;
      }
    })
  }
  
  selectUserRole(event:any){
    if(event.target.value == "ADMIN" || event.target.value == "REPORTING"){
      this.userDetails.controls.teamId.clearValidators();
      this.userDetails.controls.teamId.updateValueAndValidity();
      this.userDetails.controls.teamId.setValue([]);
      this.userDetails.controls.userRole.setValue(event.target.value);
    }
    else if(event.target.value == "SUPER_ADMIN"){
      this.userDetails.controls.teamId.clearValidators();
      this.userDetails.controls.teamId.updateValueAndValidity();
      this.userDetails.controls.teamId.setValue([]);
      this.userDetails.controls.companyUuid.clearValidators();
      this.userDetails.controls.companyUuid.updateValueAndValidity();
      this.userDetails.controls.companyUuid.setValue([]);
      this.userDetails.controls.userRole.setValue(event.target.value);
    }else{
  
      this.userDetails.controls.teamId.setValidators(Validators.required);
      this.userDetails.controls.companyUuid.setValidators(Validators.required);
      this.userDetails.controls.companyUuid.updateValueAndValidity();
      this.userDetails.controls.teamId.updateValueAndValidity();
  
      this.userDetails.controls.userRole.setValue(event.target.value);
      this.userDetails.controls.teamId.setValue(this.teamId);
      this.userDetails.controls.companyUuid.setValue(this.clientId);
    }
  }

  shareCheckedList(event:any){
    if(event.action == 'team'){
      this.teamId = event.value.map(({teamName,checked,...newData}:any)=>newData);
      this.teamId = [].concat(...this.teamId.map((team:any) => team.teamId));
      this.userDetails.controls.teamId.setValue(this.teamId);
    }
    else if(event.action == 'client'){
      this.clientId =  event.value.map(({adddress,checked,createdDate,name,updatedDate,...newData}:any)=>newData);
      this.clientId = [].concat(...this.clientId.map((team:any) => team.uuid));
      this.userDetails.controls.companyUuid.setValue(this.clientId);
    }
  }

  showAlertPopup(res:any){
    this.alert.showAlertPopup = true;
    res.status==400 ? this.alert.isError=true : this.alert.isError=false;
    this.alert.alertMsg = res.message ? res.message : res.result.message;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2500);
    scrollTo(0,0);
  }
}

  