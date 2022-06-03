import { NgModule } from '@angular/core';
import { Routes, RouterModule, Router, NavigationEnd} from '@angular/router';
import {ScrapLiteComponent} from "./scraplite.component";
const routes: Routes = [
    { path: '', component :ScrapLiteComponent}

]

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ScrapLiteAppRoutingModule {

    
}
