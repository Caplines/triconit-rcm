import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppConstants } from '../constants/app.constants';
import { TeamModel } from '../models/team.module';

@Injectable()
export class CheckUserLoggedInState implements CanActivate {

  constructor(private router: Router, private appConstants: AppConstants) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    console.log(localStorage.getItem('currentUser'));
    if (localStorage.getItem('currentUser')) {
      let ut: any = localStorage.getItem('teamId');
      let ntKey: Number = new Number(ut).valueOf();
      let team: any = this.appConstants.TEAMS_CONFIG.get(ntKey);
      let teamM: TeamModel = (<TeamModel>team);
      let ph = teamM.paths.find(x =>
        x === state.url);
      //in case wrong url is accessed
      if (typeof ph == "undefined") {
        this.router.navigate([teamM.defaultpath]);
        return false;
      }
      return false;
    }



    return true;
  }
}
