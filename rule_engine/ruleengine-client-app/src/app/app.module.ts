import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { FormsModule } from '@angular/forms';
import { AuthService } from "./services/auth.service";
import {HttpModule} from "@angular/http";
import {AccountService} from "./services/account.service";
import { ProfileComponent } from './components/profile/profile.component';
import { IVFComponent } from './components/ivf/ivf.component';

import {routing} from "./app.routing";
import {UrlPermission} from "./urlPermission/url.permission";


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    IVFComponent

  ],
  imports: [
    BrowserModule,HttpModule,FormsModule,routing
  ],
  providers: [AuthService,AccountService,UrlPermission],
  bootstrap: [AppComponent]
})
export class AppModule { }
