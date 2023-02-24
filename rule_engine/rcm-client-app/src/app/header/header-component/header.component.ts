import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { TokenStorageService } from '../../service/token-storage.service';
import Utils from '../../util/utils';

@Component({
  selector: 'app-header-component',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  form: any = {
    username: null,
    password: null,
  };
  loggedInUserRole:any;
  loggedInUserName:any;
  @Input() isUserSetting:any;
  @Input() isRegister:any;
  @Input() isManageOffice:any;
  @Input() isClaimAssign:any;
  @Input() isUsersStatus:any;
  @Input() isManageClient:any;
  @Input() isToolUpdate:any;
  @Input() isFetchClaims:any;
  @Input() isUpdatePass:any;


  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private _baseService: BaseService) { }

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
