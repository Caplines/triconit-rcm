import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {UserSettingRoutingModule} from "./user-setting-routing.module"
import { UserSettingComponent } from '../user-setting.component';
import {FormsModule} from "@angular/forms"


@NgModule({
  declarations: [UserSettingComponent],
  imports: [
    CommonModule,
    UserSettingRoutingModule,
    FormsModule
  ]
})
export class UserSettingModule { }
