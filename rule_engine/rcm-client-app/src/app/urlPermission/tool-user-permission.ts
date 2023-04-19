import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppConstants } from '../constants/app.constants';


@Injectable()
export class ToolUserPermission implements CanActivate {

  constructor(private router: Router,private appConstants: AppConstants) { }

  canActivate( ) {
    
    let ut: any = localStorage.getItem('selected_teamId');
    let roleAsso:any= localStorage.getItem("selected_roleName");
    let routes:any=['/claim-assignment','/all-pendency','/production','/tool-update','/fetch-claims']; // these page are not allowed to Associate.
    
    let isPageAllowed = routes.some((e:any)=>e==window.location.pathname);   

    if (!ut) {
      this.router.navigate(['/']);
      return false;
    }
    
    else if(roleAsso == "ASSO" && ut == 7  && !isPageAllowed && (ut != -1 || ut != '-1')){
      return true;
    }

    else if (roleAsso == "ASSO"  && ut == 7 && isPageAllowed && (ut != -1 || ut != '-1')){
        window.location.href= "/list-of-claims";
    }

    else if(ut == 7 && (ut != -1 || ut != '-1')){
        return true;
    }
    
    else if (ut != 7 && (ut != -1 || ut != '-1')){
      this.router.navigate(['/update-pass']);
      return false;
    }
    else{
      this.router.navigate(['/login']) 
      return false;
    }

      }
    }

