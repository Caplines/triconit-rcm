import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseService } from '../service/base-service.service';
@Component({
  selector: 'app-register-new-user',
  templateUrl: './register-new-user.component.html',
  styleUrls: ['./register-new-user.component.scss']
})
export class RegisterNewUserComponent implements OnInit {

  userDetails:any={"firstName":'',"lastName":"","email":"","userName":"","password":"","officeId":"","teamId":'',"userRole":""}
  teamData:any=[];
  officeData:any=[];
  userRoles:any=[];
  constructor(private router: Router, private _baseService:BaseService) { }

  ngOnInit(): void {
    this.getTeamsData();
    this.getOfficeData();
    this.getUserRoleData();
    console.log(localStorage.getItem("token"))
  }

  location(location:any){
    console.log(location.value)
    this.router.navigate([`/${location.value}`])
  }

  submitUser(){
    this._baseService.registerUser(this.userDetails,(callback:any)=>{
      if(callback.status){
        console.log(callback)
      }
    })
    console.log(this.userDetails)
  }
  
  getTeamsData(){
    this._baseService.getTeamsData((callback:any)=>{
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
    this._baseService.getUserRoleData((callback:any)=>{
      if(callback.status){
        this.userRoles = callback.result.data
      }
    })
  }

  logout(){
    localStorage.clear();
    window.location.href="/login";
  }
}
