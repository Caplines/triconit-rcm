import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import Utils from '../util/utils';
import { AppConstants } from '../constants/app.constants';
import { TeamModel } from '../models/team.model';


@Injectable()
export class AdminPermission implements CanActivate {

  constructor(private router: Router,private appConstants: AppConstants) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot
  ) {
    let ut: any = localStorage.getItem('teamId');
    if (!ut) {
      this.router.navigate(['/']);
      return false;
    }
    if (localStorage.getItem('currentUser')) {
      let ntKey:Number=new Number(ut).valueOf();
      let team :any = this.appConstants.TEAMS_CONFIG.get(ntKey);

      let teamM: TeamModel=  (<TeamModel>team);

      
      let ph= teamM.paths.find(x => 
           x === state.url);
     //in case wrong url is accessed
      if (typeof ph=="undefined"){
        this.router.navigate([teamM.defaultpath]);
        return false;
      }
      console.log("Utils.isSmilePoint() && Utils.checkAdmin()",Utils.isSmilePoint() && Utils.checkAdmin());
      if (Utils.isSmilePoint() && Utils.checkAdmin()){
        return true;
      }
      return false;
    }

    // not logged in so redirect to login page with the return url
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}

