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
  isBillPage:boolean=false;
  isIssueClaimsPage:boolean=false;
  constructor(private title: Title, public baseService: BaseService,private _service:ApplicationServiceService) {
    this.title.setTitle('Rcm Tool');
    if (localStorage.getItem("token")) {
      this.isLoggedIn = true;
    }
  }

  ngOnInit(): void {

    if (Utils.isSessionSet()) {
      this.baseService.generateRefreshTokenCB((res: any) => {
        Utils.setSession(res);
        //   }
      });
    }
    
    if(window.location.pathname.includes("billing-claims")){
      this.isBillPage = true;
    }
    if(window.location.pathname.includes("issue-claims")){
     this.isIssueClaimsPage=true;
    }
  }

}

