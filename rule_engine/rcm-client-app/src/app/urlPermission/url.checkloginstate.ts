import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppConstants } from '../constants/app.constants';
import { TeamModel } from '../models/team.model';
import Utils from '../util/utils';

@Injectable()
export class CheckUserLoggedInState implements CanActivate {

  constructor(private router: Router, private appConstants: AppConstants) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    console.log(localStorage.getItem('currentUser'));
   
    let roleAsso: any = localStorage.getItem("selected_roleName");
    if (localStorage.getItem('currentUser')) {
      let ut: any = localStorage.getItem('selected_teamId');//selected_roleName//selected_teamId
     if (ut!=null && ut!= "-1"){
      let ntKey: Number = new Number(ut).valueOf();
      let team: any = this.appConstants.TEAMS_CONFIG.get(ntKey);
      let teamM: TeamModel = (<TeamModel>team);
      let ph = teamM.paths.find(x =>
        x === state.url);
      if (typeof ph == "undefined") {
          this.router.navigate([teamM.defaultpath]);
          return false;
        } else{
          return true;
        }
     
         //return false;
     
    } else if (ut == '-1' && roleAsso=='REPORTING'){
      let ntKey: Number = new Number(9).valueOf();
      let team: any = this.appConstants.TEAMS_CONFIG.get(ntKey);
      let teamM: TeamModel = (<TeamModel>team);

      let ph = teamM.paths.find(x =>
          x === window.location.pathname);
      //in case wrong url is accessed
      if (typeof ph == "undefined") {
          window.location.href = teamM.defaultpath;
          return false;
      }
      return true;
    }
    }else{
      if(state.url == "/login"){
        return true;
      } else{
        this.router.navigate(['/login']);
        return false;
      } 
      }
    }
  
}
