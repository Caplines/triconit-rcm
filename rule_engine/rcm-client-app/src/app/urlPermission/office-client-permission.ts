import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import Utils from '../util/utils';
import { AppConstants } from '../constants/app.constants';


@Injectable()
export class OfficeClientPermission implements CanActivate {

  constructor(private router: Router, private appConstants: AppConstants) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot
  ) {
    let ut: any = localStorage.getItem("selected_teamId");
    if (Utils.isLoggedIn()) {

      if ((Utils.checkRoleSuperAdmin() && ut == '-1') || (Utils.checkRoleAdmin() && ut == '-1') && (state.url != "/manage-client" && state.url != "/manage-office")) {
        return true;
      }
      else {
        this.router.navigate(['/register']);
        return false;
      }
      // }else{
      //   this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      //   return false;
      // }
    }

  }
}

