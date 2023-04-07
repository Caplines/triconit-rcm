import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppConstants } from '../constants/app.constants';


@Injectable()
export class ToolUserPermission implements CanActivate {

  constructor(private router: Router,private appConstants: AppConstants) { }

  canActivate( ) {
    
    let ut: any = localStorage.getItem('selected_teamId');
    if (!ut) {
      this.router.navigate(['/']);
      return false;
    }
    else if(ut != -1 || ut != '-1'){
      return true;
    }
    else{
      this.router.navigate(['/login']) 
      return false;
    }

      }
    }

