import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { DynamicIVFComponent} from '../components/dynamic_ivf/dynamic_ivf.component';
import { ApplicationService} from "../services/application.service";

import { AuthService } from "../services/auth.service";
import { OfficeResolve } from "../resolver/office_resolver";
import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { HttpClientXsrfModule} from "@angular/common/http";
import {DynamicIVFAppRoutingModule} from '../components/dynamic_ivf/dynamic_ivf.routing';
import { TokenInterceptor } from '../auth/token.interceptor';
import { NgDragDropModule } from 'ng-drag-drop';
import { NgDragDropService } from 'ng-drag-drop/src/services/ng-drag-drop.service';
//import { CommonModule  } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { CommonModule} from '@angular/common';

@NgModule({
    imports: [ CommonModule ,FormsModule, HttpClientModule, DynamicIVFAppRoutingModule,NgDragDropModule],
    declarations: [DynamicIVFComponent],
    bootstrap: [DynamicIVFComponent],
    providers: [AuthService,ApplicationService,NgDragDropService,
  	  OfficeResolve,
      {
         provide: HTTP_INTERCEPTORS,
         useClass: TokenInterceptor,
         multi: true
     }
     /*,
     {
         provide: XSRFStrategy,
         useValue: new CookieXSRFStrategy('XSRF-TOKEN', 'X-XSRF-TOKEN')
        
     }
     */
 ]
})

export class DynamicIVFAppModule { }