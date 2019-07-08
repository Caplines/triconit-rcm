import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

@Injectable()
export class UrlPermission implements CanActivate {

  constructor(private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
	  
	//console.log("state.url",state.url);
	let ut=localStorage.getItem('userType');
    if (!ut) {
    	this.router.navigate(['/logout']);
    }
    if (localStorage.getItem('currentUser')) {
      // logged in so return true
      if(ut=='2' && (state.url=='/ivf' || state.url=='/ivftreatmentplan'
    	  || state.url=='/ivfbatch' || state.url=='/ivfbatchpre')){
    		  this.router.navigate(['/ivfcl']);
    		  return false;
    	  } 	
      if(ut=='1' && (state.url=='/ivfclaimid' || state.url=='/ivfcl'
    	  || state.url=='/ivfclbatch')){
    		  this.router.navigate(['/ivf']);
    		  return false;
    	  } 	
      return true;
    }

    // not logged in so redirect to login page with the return url
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
    return false;
  }
}
