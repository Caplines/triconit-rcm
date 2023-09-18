import { Component, ViewEncapsulation } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import Utils from '../util/utils';
import { AppConstants } from '../constants/app.constants';

@Component({
  selector: 'app-search-claims',
  templateUrl: './search-claims.component.html',
  styleUrls: ['./search-claims.component.scss'],
  encapsulation:ViewEncapsulation.None
})
export class SearchClaimsComponent {

  loader:any={};
  clients:any=[];
  offices:any=[];
  teamData:any = this.constants.teamData;

  constructor(public appService:ApplicationServiceService ,private title : Title,private constants:AppConstants){
    title.setTitle(Utils.defaultTitle + "Search Claims");
  }

  ngOnInit(): void {
    this.getcompanyData();
  }


  getcompanyData() {
    this.appService.fetchClientsByUser((callback: any) => {
      if (callback.status) {
        this.clients = callback.data;
      }
    })
  }

}
