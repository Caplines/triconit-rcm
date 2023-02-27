import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {UserSettingRoutingModule} from "./user-setting-routing.module"
import { UserSettingComponent } from '../user-setting.component';
import {FormsModule} from "@angular/forms"
import { HeaderComponent } from 'src/app/header/header-component/header.component';


@NgModule({
  declarations: [UserSettingComponent],
  imports: [
    CommonModule,
    UserSettingRoutingModule,
    FormsModule,
    HeaderComponent
  ]
})
export class UserSettingModule { }
