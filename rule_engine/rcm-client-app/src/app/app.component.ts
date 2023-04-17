import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { BaseService } from './service/base-service.service';
import Utils from './util/utils';
import { ApplicationServiceService } from './service/application-service.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  isLoggedIn: Boolean = false;
  isBillingClaimsPage: boolean = false;
  isTpIvfPage:boolean=false;
  constructor(private title: Title, public baseService: BaseService,private _service:ApplicationServiceService) {
    this.title.setTitle('Rcm Tool');
    if (localStorage.getItem("token")) {
      this.isLoggedIn = true;
    }
    if (window.location.pathname.includes("billing-claims")) {
      this.isBillingClaimsPage = true;
    }
  }

  ngOnInit(): void {
    this._service.message$.subscribe((event:any)=>{
        this.isTpIvfPage = event;
        console.log(this.isTpIvfPage);
        
    })

    if (Utils.isSessionSet()) {
      this.baseService.generateRefreshTokenCB((res: any) => {
        Utils.setSession(res);
        //   }
      });
    }
  }



}

