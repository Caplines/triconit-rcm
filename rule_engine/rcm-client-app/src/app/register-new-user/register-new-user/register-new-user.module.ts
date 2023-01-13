import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule} from "@angular/forms"
import { RegisterNewUserRoutingModule } from './register-new-user-routing.module';
import { RegisterNewUserComponent } from '../register-new-user.component';


@NgModule({
  declarations: [RegisterNewUserComponent],
  imports: [
    CommonModule,
    RegisterNewUserRoutingModule,
    FormsModule
  ]
})
export class RegisterNewUserModule { }
