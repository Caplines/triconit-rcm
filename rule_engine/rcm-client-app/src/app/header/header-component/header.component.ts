import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {Router, RouterModule } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { SwitchAccountModel } from '../../models/switch.account.model';
import Utils from '../../util/utils';

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

  modelElement:any={'modal':'','span':''};
  cwModel:SwitchAccountModel;
  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private _baseService: BaseService) {

    this.cwModel= {};
   }

  ngOnInit(): void {
   // if(!this.loggedInUserRole){
      this.loggedInUserRole =  localStorage.getItem("roles");
  //  }
    this.loggedInUserName = localStorage.getItem("name");
    this.openPopUp(false);
    

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
     let t:any= document.getElementById("selectTeamAC");
     let c:any= document.getElementById("selectClientAC");
     let r:any= document.getElementById("selectRolesAC");
     this.modelElement.modal = document.getElementById("switch-modal");
     this.modelElement.modal.style.display = "none";
     this.staticUtil.setLocalStoragePartial(c.value,r.value,t.value);
  }

  openPopUp(open:boolean){
    if (!open) this.modelTitle="Select Account";
    else  this.modelTitle="Switch Account";
    if (open || this.staticUtil.isAccountpopupNeeded()){
      this.cwModel.companies=this.staticUtil.getClientsFromLS();
      this.cwModel.teams=this.staticUtil.getTeamsFromLS();
      this.cwModel.roles=this.staticUtil.getRolesFromLS();
      
      this.modelElement.modal = document.getElementById("switch-modal");
      this.modelElement.modal.style.display = "block";
    }
  }
 
}
