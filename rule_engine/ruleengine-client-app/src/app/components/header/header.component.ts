import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class HeaderComponent implements OnInit {
  userName: any;
  userTypeAdmin: any;
  userTypeRegular: any;

  constructor(public authService :AuthService, public router: Router) {
      this.authService.changeEmitted$.subscribe(
        text => {
			if(text == 'login') {
				this.setUser();
			} else if(text == 'logout') {
				this.userName = '';
				this.userTypeAdmin = '';
				this.userTypeRegular = '';
			}
        });
  }

  ngOnInit() {
	this.setUser();
  }

  
  setUser() {
	if(localStorage.length) {
		this.userName = localStorage.getItem('currentUser');
		this.userTypeAdmin = localStorage.getItem('roles').indexOf("ROLE_ADMIN")>0;
		this.userTypeRegular = "ROLE_USER";
	}
	
  }
  
}
