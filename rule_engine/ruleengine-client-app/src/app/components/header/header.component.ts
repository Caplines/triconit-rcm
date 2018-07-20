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
  userType: any;

  constructor(public authService :AuthService, public router: Router) {
      this.authService.changeEmitted$.subscribe(
        text => {
			if(text == 'login') {
				this.setUser();
			} else if(text == 'logout') {
				this.userName = '';
				this.userType = '';
			}
        });
  }

  ngOnInit() {
	this.setUser();
  }

  
  setUser() {
	if(localStorage.length) {
		this.userName = localStorage.getItem('currentUser');
		this.userType = localStorage.getItem('roles').indexOf("ROLE_ADMIN")>0;
	}
	
  }
  
}
