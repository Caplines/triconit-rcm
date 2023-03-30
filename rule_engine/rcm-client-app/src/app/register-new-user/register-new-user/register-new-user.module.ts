import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule} from "@angular/forms"
import { RegisterNewUserRoutingModule } from './register-new-user-routing.module';
import { RegisterNewUserComponent } from '../register-new-user.component';
import { HeaderComponent } from 'src/app/header/header-component/header.component';
import { MultiSelectDropdownModule } from 'src/app/shared/multi-select-dropdown/multi-select-dropdown/multi-select-dropdown.module';

@NgModule({
  declarations: [RegisterNewUserComponent],
  imports: [
    CommonModule,
    RegisterNewUserRoutingModule,
    FormsModule,
    ReactiveFormsModule ,
    HeaderComponent,
    MultiSelectDropdownModule
  ],
})
export class RegisterNewUserModule { }
