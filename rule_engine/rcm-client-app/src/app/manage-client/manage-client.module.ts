import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from "../service/auth-service.service";
//import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { FormsModule } from '@angular/forms';
import { ManageClientComponent } from './manage-client/manage-client.component.component';
import { ManageClientRoutingModule } from './manage-client-routing.module';
import { HeaderComponent } from '../header/header-component/header.component';


@NgModule({
  declarations: [
    ManageClientComponent
  ],
  imports: [
    CommonModule,
    ManageClientRoutingModule, FormsModule,
    HeaderComponent

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
