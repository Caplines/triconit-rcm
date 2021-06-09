import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { SealantelegbComponent} from '../components/sealantelegb/sealantelegb.component';
import { ApplicationService} from "../services/application.service";
import { NgDatepickerModule } from 'ng2-datepicker';

import { AuthService } from "../services/auth.service";
import { OfficeResolve } from "../resolver/office_resolver";
import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { HttpClientXsrfModule} from "@angular/common/http";
import {SealantelegbAppRoutingModule} from '../components/sealantelegb/sealantelegb.routing';
import { TokenInterceptor } from '../auth/token.interceptor';
import { FormsModule } from '@angular/forms';
import { CommonModule} from '@angular/common';

@NgModule({
    imports: [ CommonModule ,FormsModule, HttpClientModule,NgDatepickerModule, SealantelegbAppRoutingModule],
    declarations: [SealantelegbComponent],
    bootstrap: [SealantelegbComponent],
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

export class SealantelegbAppModule { }