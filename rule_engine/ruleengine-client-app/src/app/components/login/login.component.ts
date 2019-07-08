import { Component, OnInit, ViewEncapsulation, Output, EventEmitter } from '@angular/core';
import {User} from "../../model/model.user";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router"; 
import Utils from '../../util/utils';


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
    	let ut =Utils.fetchUserTypeFromLocalStorage(); 
        if (ut=='1')this.router.navigate(['/ivf']);
        else this.router.navigate(['/ivfcl']);
        this.authService.emitChange('login');
        },err=>{
        this.errorMessage="Error :  Username or password is incorrect";
        }
      )
  }
}
