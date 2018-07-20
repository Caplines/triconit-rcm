import { Component, OnInit, ViewEncapsulation, Output, EventEmitter } from '@angular/core';
import {User} from "../../model/model.user";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent implements OnInit {
  user: User=new User();
  errorMessage:string;
  @Output() emitToParent = new EventEmitter<any>();
  constructor(private authService :AuthService, private router: Router) { }



  ngOnInit() {
  }

  login(){
    this.authService.logIn(this.user)
      .subscribe(data=>{
        this.router.navigate(['/ivf']);
        this.authService.emitChange('login');
        },err=>{
        this.errorMessage="Error :  Username or password is incorrect";
        }
      )
  }
}
