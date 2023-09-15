import { Component, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import Utils from '../util/utils';

@Component({
  selector: 'app-search-claims',
  templateUrl: './search-claims.component.html',
  styleUrls: ['./search-claims.component.scss'],
  encapsulation:ViewEncapsulation.None
})
export class SearchClaimsComponent {

  loader:any={};
  companyData:any=[];

  constructor(public appService:ApplicationServiceService ,private title : Title){
    title.setTitle(Utils.defaultTitle + "Search Claims");
  }

  ngOnInit(): void {
    this.getcompanyData();
  }


  getcompanyData() {
    this.appService.fetchClientsByUser((callback: any) => {
      if (callback.status) {
        this.companyData = callback.data;
      }
    })
  }
}
