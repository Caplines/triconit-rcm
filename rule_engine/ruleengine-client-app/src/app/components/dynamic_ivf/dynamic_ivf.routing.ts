import { NgModule } from '@angular/core';
import { Routes, RouterModule, Router, NavigationEnd} from '@angular/router';
import {DynamicIVFComponent} from "./dynamic_ivf.component";
const routes: Routes = [
    { path: '', component :DynamicIVFComponent}

]

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class DynamicIVFAppRoutingModule {

    
}
