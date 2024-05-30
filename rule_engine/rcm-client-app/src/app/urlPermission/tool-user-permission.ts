import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppConstants } from '../constants/app.constants';
import { TeamModel } from '../models/team.model';


@Injectable()
export class ToolUserPermission implements CanActivate {

  constructor(private router: Router, private appConstants: AppConstants) { }

  canActivate() {
    let ut: any = localStorage.getItem('selected_teamId');
    let roleAsso: any = localStorage.getItem("selected_roleName");

    if (!ut) {
      this.router.navigate(['/']);
      return false;
    }
    if (ut != '-1') {

      let ntKey: Number = new Number(ut).valueOf();
      let team: any = this.appConstants.TEAMS_CONFIG.get(ntKey);
      let teamM: TeamModel = (<TeamModel>team);

      let ph = teamM.paths.find(x =>
        x === window.location.pathname);
      //in case wrong url is accessed
      if (typeof ph == "undefined") {
        if (roleAsso === "ASSO") {
          window.location.href = "/list-of-claims";
        } else {
          window.location.href = teamM.defaultpath;
        }
        return false;
      }

      if (ph && roleAsso === "ASSO") {
        if (window.location.pathname == "/list-of-claims") {
          return true;
        } else {
          window.location.href = "/list-of-claims";
        }
      }
      return true;
    }

    else if (ut=='-1' && roleAsso=='REPORTING') {
      let ntKey: Number = new Number(9).valueOf();
      let team: any = this.appConstants.TEAMS_CONFIG.get(ntKey);
      let teamM: TeamModel = (<TeamModel>team);

      let ph = teamM.paths.find(x =>
        x === window.location.pathname);
      //in case wrong url is accessed
      if (typeof ph == "undefined") {
        if (roleAsso === "ASSO") {
          window.location.href = "/list-of-claims";
        } else {
          window.location.href = teamM.defaultpath;
        }
        return false;
      }

      if (ph && roleAsso === "ASSO") {
        if (window.location.pathname == "/list-of-claims") {
          return true;
        } else {
          window.location.href = "/list-of-claims";
        }
      }
      return true;
    }

    this.router.navigate(['/login'], { queryParams: { returnUrl: window.location.pathname } });
    return false;
  }
}

