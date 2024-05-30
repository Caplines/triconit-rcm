import { Injectable, inject } from '@angular/core';
import { CanActivateFn, CanActivateChildFn, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

import { AppConstants } from '../constants/app.constants';
import { TeamModel } from '../models/team.model';

export const ClaimAssingnmentActivate: CanActivateFn = (
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
) => {

    const router = inject(Router);
    const appConstants = inject(AppConstants);

    let ut: any = localStorage.getItem('selected_teamId');
    let roleAsso: any = localStorage.getItem("selected_roleName");
    if (!ut) {
        router.navigate(['/']);
        return false;
    }
    if (ut != '-1') {

        let ntKey: Number = new Number(ut).valueOf();
        let team: any = appConstants.TEAMS_CONFIG.get(ntKey);
        let teamM: TeamModel = (<TeamModel>team);

        let ph = teamM.paths.find(x =>
            x === window.location.pathname);
        //in case wrong url is accessed
        if (typeof ph == "undefined") {
            window.location.href = teamM.defaultpath;
            return false;
        }


        return true;
    }
    else if (ut == '-1' && roleAsso=='REPORTING'){
        let ntKey: Number = new Number(9).valueOf();
        let team: any = appConstants.TEAMS_CONFIG.get(ntKey);
        let teamM: TeamModel = (<TeamModel>team);

        let ph = teamM.paths.find(x =>
            x === window.location.pathname);
        //in case wrong url is accessed
        if (typeof ph == "undefined") {
            window.location.href = teamM.defaultpath;
            return false;
        }
        return true;
    }

    router.navigate(['/login'], { queryParams: { returnUrl: window.location.pathname } });
    return false;


    /*
        const authService = inject(AuthenticationService);
    
    
        return authService.checkLogin().pipe(
            map(() => true),
            catchError(() => {
                router.navigate(['/']);
                return of(false);
            })
        );
        */
};

export const canActivateChild: CanActivateChildFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => ClaimAssingnmentActivate(route, state);