import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { BaseService  } from './service/base-service.service';
import Utils from './util/utils';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  
  isLoggedIn:Boolean=false;
  isBillingClaimsPage:boolean=false;
  constructor(private title:Title,public baseService:BaseService){
    this.title.setTitle('Rcm Tool');
    if(localStorage.getItem("token")){
      this.isLoggedIn = true;
    }
    if(window.location.pathname.includes("billing-claims")){
      this.isBillingClaimsPage=true;
    }
  }

  ngOnInit(): void {
    if (Utils.isSessionSet()){
    this.baseService.generateRefreshTokenCB((res:any)=>{
        Utils.setSession(res);
      //   }
     });    
    }
   
  }

}

