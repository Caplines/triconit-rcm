import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterNewUserComponent } from '../register-new-user.component';
const routes: Routes = [
  { path: '', component: RegisterNewUserComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RegisterNewUserRoutingModule { }
