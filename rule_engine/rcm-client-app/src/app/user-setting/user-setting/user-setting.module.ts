import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {UserSettingRoutingModule} from "./user-setting-routing.module"
import { UserSettingComponent } from '../user-setting.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms"
import { HeaderComponent } from 'src/app/header/header-component/header.component';
import { MultiSelectDropdownModule } from 'src/app/shared/multi-select-dropdown/multi-select-dropdown/multi-select-dropdown.module';


@NgModule({
  declarations: [UserSettingComponent],
  imports: [
    CommonModule,
    UserSettingRoutingModule,
    FormsModule,
    HeaderComponent,
    ReactiveFormsModule,
    MultiSelectDropdownModule
  ]
})
export class UserSettingModule { }
