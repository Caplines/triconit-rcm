import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  
  isLoggedIn:Boolean=false;
  isBillingClaimsPage:boolean=false;
  constructor(private title:Title){
    this.title.setTitle('Rcm Tool');
    if(localStorage.getItem("token")){
      this.isLoggedIn = true;
    }
    if(window.location.pathname.includes("billing-claims")){
      this.isBillingClaimsPage=true;
    }
  }
}

