import { NgModule } from '@angular/core';
import { Routes, RouterModule, Router, NavigationEnd} from '@angular/router';
import {AllRuleReportComponent} from "./all_rule_report.component";
const routes: Routes = [
    { path: '', component :AllRuleReportComponent}

]

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AllRuleReportRoutingModule {

    
}
