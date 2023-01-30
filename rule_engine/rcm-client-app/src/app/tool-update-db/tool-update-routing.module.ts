import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ToolUpdateComponent } from './tool-update/tool-update.component';
const routes: Routes = [
  { path: '', component: ToolUpdateComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ToolUpdateClaimsRoutingModule{ }
