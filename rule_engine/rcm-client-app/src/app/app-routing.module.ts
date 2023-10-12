import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CheckUserLoggedInState } from './urlPermission/url.checkloginstate';
import { ReigsterPermission } from './urlPermission/register-permission';
import { ToolUserPermission } from './urlPermission/tool-user-permission';
import { ClaimAssingnmentActivate } from './urlPermission/claim-assign-permission';
import { OfficeClientPermission } from './urlPermission/office-client-permission';

const routes: Routes = [

  {
    path: '', pathMatch: 'full', redirectTo: "/login"
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
    canActivate: [CheckUserLoggedInState]
  },
  // {
  //   path: 'fetch-claims',
  //   loadChildren: () => import('./fetch-claims/fetch-claims.module').then(m => m.FetchClaimsModule),
  //   canActivate: [ToolUserPermission]
  // },
  {
    path: 'register',
    loadChildren: () => import("./register-new-user/register-new-user/register-new-user.module").then(m => m.RegisterNewUserModule),
    canActivate: [ReigsterPermission]
  },
  {
    path: 'user-setting',
    loadChildren: () => import("./user-setting/user-setting/user-setting.module").then(m => m.UserSettingModule),
    canActivate: [ReigsterPermission]
  },
  {
    path: 'tool-update',
    loadChildren: () => import("./tool-update-db/tool-update.module").then(m => m.ToolUpdateModule),
    canActivate: [ToolUserPermission]

  },
  {
    path: 'manage-office',
    loadChildren: () => import("./manage-office/manage-office.module").then(m => m.ManageOfficeModule),
    canActivate: [OfficeClientPermission]
  },
  {
    path: 'claim-assignment',
    loadChildren: () => import("./claim-office-assignment/office-assignment.module").then(m => m.ClaimAssignmentModule),
    canActivate: [ClaimAssingnmentActivate]
  },
  {
    path: 'users-status',
    loadChildren: () => import("./users-status/users-status/users-status.module").then(m => m.UserStatusModule),
    canActivate: [ReigsterPermission]

  },
  {
    path: 'manage-client',
    loadChildren: () => import("./manage-client/manage-client.module").then(m => m.ManageClientModule),
    canActivate: [OfficeClientPermission]

  },

  {
    path: 'billing-claims/:uuid',
    loadChildren: () => import("./claims/claims.module").then(m => m.ClaimsModule),
  },
  {
    path: 'billing-claims/:uuid/ivf',
    loadChildren: () => import("./report/report.module").then(m => m.ReportModule)
  },
  {
    path: 'billing-claims/:uuid/tp',
    loadChildren: () => import("./treatment-plan/treatment-plan/treatment-plan.module").then(m => m.TreatmentPlanModule)
  },
  {
    path: 'update-pass',
    loadChildren: () => import("./update-pass/update-pass/update-pass.module").then(m => m.UpdatePasswordModule)
  },
  {
    path: 'production',
    loadChildren: () => import("./production/production.module").then(m => m.ProductionModule),
    canActivate: [ClaimAssingnmentActivate]
  },
  {
    path: 'list-of-claims',
    loadChildren: () => import("./list-of-claims/list-of-claims.module").then(m => m.ListOfClaimsModule),
    canActivate: [ClaimAssingnmentActivate]
  },
  {
    path: 'all-pendency',
    loadChildren: () => import("./allPendency/all-pendency/all-pendency.module").then(m => m.AllPendencyModule),
    canActivate: [ClaimAssingnmentActivate]
  },
  {
    path: 'tool-update/issue-claims',
    loadChildren: () => import("./issue-claims/issue-claims/issue-claims.module").then(m => m.IssueClaimCModule),
    canActivate: [ClaimAssingnmentActivate]
  },
  {
    path: 'other-teams-work',
    loadChildren: () => import("./other-teams-work/other-teams-work.module").then(m => m.OtherTeamsWorkModule),
    canActivate: [ClaimAssingnmentActivate]
  },
  {
    path: 'search-claims',
    loadChildren: () => import("./search-claims/search-claims.module").then(m => m.SearchClaimsModule)
  },


  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
