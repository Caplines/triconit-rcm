import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from "../service/auth-service.service";
//import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { FormsModule } from '@angular/forms';
import { HeaderModule } from '../header/header.module';
import { ManageClientComponent } from './manage-client/manage-client.component.component';
import { ManageClientRoutingModule } from './manage-client-routing.module';


@NgModule({
  declarations: [
    ManageClientComponent
  ],
  imports: [
    CommonModule,
    ManageClientRoutingModule, FormsModule,
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
export class ManageClientModule { }
