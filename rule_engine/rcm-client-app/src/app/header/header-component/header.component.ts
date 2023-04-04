import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {Router, RouterModule } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { SwitchAccountModel } from '../../models/switch.account.model';
import Utils from '../../util/utils';
import { ApplicationServiceService } from 'src/app/service/application-service.service';

@Component({
  selector: 'app-header-component',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  standalone:true,
  imports: [ CommonModule, RouterModule, FormsModule],
  providers: [AuthService],
})
export class HeaderComponent implements OnInit {
  form: any = {
    username: null,
    password: null,
  };
  loggedInUserRole:any;
  loggedInUserName:any;
  modelTitle="";

  showPopup:boolean=false;

  modelElement:any={'modal':'','span':''};
  cwModel:SwitchAccountModel;
  userInfo:any={'currentClientName':'','currentRoleName':'','currentTeamId':''}
  //https://www.bezkoder.com/angular-13-jwt-auth/
  selectedTeam:any='';
  selectedClient:any='';
  selectedRole:any='';
  roleData:any=[];
  loginUserType:any='';
  btnDisabled:boolean=true;
  teamData:any=[];
  constructor(private appSer: ApplicationServiceService) {

    this.cwModel= {};
    this.teamData=[{"teamName":"Internal Audit","teamId":3},{"teamName":"Aging","teamId":4},{"teamName":"Posting","teamId":5},{"teamName":"Quality","teamId":6},{"teamName":"Billing","teamId":7}];
   }

  ngOnInit(): void {
   // if(!this.loggedInUserRole){
      this.userInfo.currentClientName =  localStorage.getItem("selected_clientName");
      this.userInfo.currentRoleName =  localStorage.getItem("selected_roleName");
      this.userInfo.currentTeamId =  localStorage.getItem("selected_teamId");
      this.loginUserType = localStorage.getItem("loginAs");
  //  }
     this.loggedInUserName = localStorage.getItem("name");
     console.log(1221);
     
     if(this.roleData.length==0){
        this.getRoles();
     }
    

  }
  getRoles(){
    this.appSer.fetchRoles((res:any)=>{
      if(res.status){
        this.roleData = res.data;
        this.openPopUp(false);
        this.setRoleName();
        this.setTeamName();
      }
    })
  }

  setRoleName(){
    this.userInfo.currentRoleName = this.roleData.find((e:any)=>{                  //Returns Role Name from Curernt Role ID.
      if(e.roleId == this.userInfo.currentRoleName){
        this.userInfo.currentRoleName = e.roleName;
        return e.roleName;
      }
    })
  }
 
  setTeamName(){
    this.userInfo.currentTeamId = this.teamData.find((e:any)=>{
      if(e.teamId == this.userInfo.currentTeamId){
        this.userInfo.currentTeamId = e.teamName;
        return e.teamName;
      }
    })
  }

  logout(){
    localStorage.clear();
    window.location.href="/login";
  }

  get staticUtil() {
    return Utils;
  }

  closeModal(){
    this.modelElement.modal.style.display = "none";
  }

  switchAccount(){
     this.modelElement.modal.style.display = "none";
     if(this.selectedTeam == ''){
      this.selectedTeam = '-1'
     }
     this.staticUtil.setLocalStoragePartial(this.selectedClient,this.selectedRole,this.selectedTeam);
      this.showPopup= false;
     window.location.reload();
  }

  openPopUp(open:boolean){
    if (!open) {
      this.modelTitle="Select Account";
    }
    else {
      this.modelTitle="Switch Account";
    } 
    if (open || this.staticUtil.isAccountpopupNeeded()){
      this.cwModel.companies=this.staticUtil.getClientsFromLS();
      this.cwModel.teams=this.staticUtil.getTeamsFromLS();
      this.cwModel.roles=this.staticUtil.getRolesFromLS();
      
      this.cwModel.roles = this.cwModel.roles.map((roleId: any) => {
        let foundRole = this.roleData.find((role: any) => role.roleId === roleId.substring(5));
        return foundRole ? { roleName: foundRole.roleName, roleId: foundRole.roleId } : null;
      }).filter((role: any) => role !== null);    //removes ROLE_ prefix from localstorage: roles and then retrived matched role name from role Data.

      this.modelElement.modal = document.getElementById("switch-modal");
      this.modelElement.modal.style.display = "block";
      this.showPopup= true;
    }
  }

  selectLoginType(event:any){
    this.loginUserType = event.target.value;
    if(this.loginUserType == 'Normal') {
      this.btnDisabled = true;
    } else if(this.loginUserType == 'Admin'){
      this.selectedTeam = '';
      this.checkValidationSuperAdmin();
    }
  }

  checkBtnDisabled(){
    if(this.selectedRole == 'SUPER_ADMIN'){
      this.cwModel.teams = this.teamData;
      this.checkValidationSuperAdmin();
    }
   else if(this.selectedRole == 'ADMIN' ||  this.selectedRole == 'REPORTING'){
     this.checkValidationAdminReporting();
    }
    else if(this.selectedRole != 'SUPER_ADMIN' || this.selectedRole != 'ADMIN' || this.selectedRole != 'REPORTING'){
    this.checkValidationNormal();
    }
    else{
      this.btnDisabled =  true;
    }
  }
  
  checkValidationSuperAdmin(){
      if(this.cwModel?.companies?.length>0 && this.cwModel?.roles?.length>0 && this.selectedClient && this.selectedRole && this.loginUserType){
        localStorage.setItem("loginAs",this.loginUserType)
        this.btnDisabled =  false;
      }else { 
        this.btnDisabled =  true;
      }
   
  }
 
  checkValidationAdminReporting(){
      if(this.cwModel?.companies?.length>0 && this.cwModel?.roles?.length>0 && this.selectedClient && this.selectedRole){
        this.btnDisabled =  false;
        this.loginUserType='';
      }else { 
        this.btnDisabled =  true;
      }
  }
 
  checkValidationNormal(){
    
      if(this.cwModel?.companies?.length>0 && this.cwModel?.roles?.length>0 && this.cwModel?.teams?.length>0  &&  this.selectedClient && this.selectedRole && this.selectedTeam){
        this.btnDisabled =  false;
        this.loginUserType='';
      }else { 
        this.btnDisabled =  true;
      }
    } 
  
}
