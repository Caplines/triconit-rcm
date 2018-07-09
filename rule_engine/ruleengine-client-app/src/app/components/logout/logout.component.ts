import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {User} from "../../model/model.user";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-logout',
  template: ''
})
export class LogoutComponent implements OnInit {
  user: User=new User();
  errorMessage:string;
  constructor(private authService :AuthService, private router: Router) { }



  ngOnInit() {
      this.logout();
  }

  logout(){
    this.authService.logOut(()=>{this.router.navigate(['/login'])});
      
  }
}
