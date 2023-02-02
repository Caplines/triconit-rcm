import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageOfficeComponent } from './manage-office/manage-office.component.component';
const routes: Routes = [
  { path: '', component: ManageOfficeComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManageOfficeRoutingModule {

}
