import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { ApplicationService } from '../services/application.service';
@Injectable()
export class OfficeResolve implements Resolve<any> {

  constructor(private accountService: ApplicationService) {}

  resolve(route: ActivatedRouteSnapshot) {
    return  this.accountService.getOfficesPrior();
  }
}

///https://blog.thoughtram.io/angular/2016/10/10/resolving-route-data-in-angular-2.html
//https://www.callibrity.com/blog/angular-2-route-resolves