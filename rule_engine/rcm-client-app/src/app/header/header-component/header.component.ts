import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { TokenStorageService } from '../../service/token-storage.service';


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
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];
  loggedInUserDetail:any;

  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private _baseService: BaseService) { }

  ngOnInit(): void {
    if(!this.loggedInUserDetail){
      this.loggedInUserDetail =  localStorage.getItem("roles")
    }
    console.log(this.loggedInUserDetail)
  }

 
  logout(){
    localStorage.clear();
    window.location.href="/login";
  }

 
}
