import { NgModule } from '@angular/core';
import { Routes, RouterModule, Router, NavigationEnd} from '@angular/router';
import {RuleReportComponent} from "./rule_report.component";
const routes: Routes = [
    { path: '', component :RuleReportComponent}

]

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class RuleReportRoutingModule {

    
}
