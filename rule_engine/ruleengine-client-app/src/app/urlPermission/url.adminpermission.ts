import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

@Injectable()
export class UrlAdminPermission implements CanActivate {

  constructor(private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (localStorage.getItem('currentUser') && localStorage.getItem('roles').indexOf("ROLE_ADMIN")>0) {
      // logged with admin  so return true
      return true;
    }

    if (localStorage.getItem('currentUser')) {
        //this.router.navigate(['/ivf'], { queryParams: { returnUrl: state.url }});
        this.router.navigate(['/ivf']);
        return false;
      }
    // not logged in so redirect to login page with the return url
    this.router.navigate(['/login']);
    return false;
  }
}
