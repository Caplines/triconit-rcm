import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from "../service/auth-service.service";
import { TokenInterceptor } from '../auth/token.interceptor';
//import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { ManageOfficeComponent } from './manage-office/manage-office.component.component';
import { ManageOfficeRoutingModule } from './manage-office-routing.module';
import { HeaderModule } from '../header/header.module';


@NgModule({
  declarations: [
    ManageOfficeComponent
  ],
  imports: [
    CommonModule,
    ManageOfficeRoutingModule, FormsModule,
    HeaderModule

  ],
  providers: [AuthService]
    //OfficeResolve,
   /* {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }]*/
})
export class ManageOfficeModule { }
