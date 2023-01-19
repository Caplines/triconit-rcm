import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule} from "@angular/forms"
import { RegisterNewUserRoutingModule } from './register-new-user-routing.module';
import { RegisterNewUserComponent } from '../register-new-user.component';
import { HeaderModule } from 'src/app/header/header.module';

@NgModule({
  declarations: [RegisterNewUserComponent],
  imports: [
    CommonModule,
    RegisterNewUserRoutingModule,
    FormsModule,
    ReactiveFormsModule ,
    HeaderModule
  ]
})
export class RegisterNewUserModule { }
