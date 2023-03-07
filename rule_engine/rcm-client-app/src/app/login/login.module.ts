import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LoginRoutingModule } from './login-routing.module';
import { LoginComponent } from './login-component/login-component.component';
import { AuthService } from "../service/auth-service.service";
import { TokenInterceptor } from '../auth/token.interceptor';
//import { HttpModule , XSRFStrategy, CookieXSRFStrategy, Http} from "@angular/http";
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { RECAPTCHA_SETTINGS, RecaptchaFormsModule, RecaptchaModule, RecaptchaSettings } from 'ng-recaptcha';
import { environment } from '../../environments/environment';

@NgModule({
  declarations: [
    LoginComponent
  ],
  imports: [
    CommonModule,
    LoginRoutingModule, FormsModule,RecaptchaModule
  ],
  providers: [AuthService ,{
    provide: RECAPTCHA_SETTINGS,
    useValue: {
      siteKey: environment.recaptcha.siteKey,
    } as RecaptchaSettings,
  }]

})
export class LoginModule { }
