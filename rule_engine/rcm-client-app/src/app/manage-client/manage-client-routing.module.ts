import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageClientComponent } from './manage-client/manage-client.component.component';
const routes: Routes = [
  { path: '', component: ManageClientComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManageClientRoutingModule {

}
