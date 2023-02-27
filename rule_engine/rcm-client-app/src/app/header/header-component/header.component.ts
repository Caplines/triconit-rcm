import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {Router, RouterModule } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
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


  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private _baseService: BaseService) {
   }

  ngOnInit(): void {
   // if(!this.loggedInUserRole){
      this.loggedInUserRole =  localStorage.getItem("roles")
  //  }
    this.loggedInUserName = localStorage.getItem("name")
  }

 
  logout(){
    localStorage.clear();
    window.location.href="/login";
  }

  get staticUtil() {
    return Utils;
  }

 
}
