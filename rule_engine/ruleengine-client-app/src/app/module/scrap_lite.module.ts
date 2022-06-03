import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ScrapLiteComponent} from '../components/scrap_lite/scraplite.component';
import { ApplicationService} from "../services/application.service";

import { AuthService } from "../services/auth.service";
import { OfficeResolve } from "../resolver/office_resolver";
import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { HttpClientXsrfModule} from "@angular/common/http";
import {ScrapLiteAppRoutingModule} from '../components/scrap_lite/scrap_lite.routing';
import { TokenInterceptor } from '../auth/token.interceptor';
//import { CommonModule  } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { CommonModule} from '@angular/common';

@NgModule({
    imports: [ CommonModule ,FormsModule, HttpClientModule, ScrapLiteAppRoutingModule],
    declarations: [ScrapLiteComponent],
    bootstrap: [ScrapLiteComponent],
    providers: [AuthService,ApplicationService,
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

export class ScrapLiteAppModule { }