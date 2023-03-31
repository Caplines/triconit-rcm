import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import Utils from '../util/utils';
import { AppConstants } from '../constants/app.constants';
import { TeamModel } from '../models/team.model';


@Injectable()
export class ReigsterPermission implements CanActivate {

  constructor(private router: Router,private appConstants: AppConstants) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot
  ) {
   
    if (Utils.isLoggedIn()){
    if (Utils.checkAdmin() || Utils.checkSuperAdmin()){
      return true;
    }else{
      this.router.navigate(['/']);
      return false;
    }
    }else{
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }
   
 }
}

