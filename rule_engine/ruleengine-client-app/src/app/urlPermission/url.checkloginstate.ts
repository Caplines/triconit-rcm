import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import Utils from '../util/utils';

@Injectable()
export class UrlLoggedInCheck implements CanActivate {

  constructor(private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (localStorage.getItem('currentUser')) {
    	let ut =Utils.fetchUserTypeFromLocalStorage(); 
        if (ut=='1')this.router.navigate(['/ivf']);
        else this.router.navigate(['/ivfcl']);
      return false;
    }

    return true;
  }
}
