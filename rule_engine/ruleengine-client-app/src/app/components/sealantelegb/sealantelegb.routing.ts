import { NgModule } from '@angular/core';
import { Routes, RouterModule, Router, NavigationEnd} from '@angular/router';
import {SealantelegbComponent} from "./sealantelegb.component";
const routes: Routes = [
    { path: '', component :SealantelegbComponent}

]

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SealantelegbAppRoutingModule {

    
}
