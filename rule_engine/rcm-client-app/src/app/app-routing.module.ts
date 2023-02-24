import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CheckUserLoggedInState } from './urlPermission/url.checkloginstate';
import { UrlPermission } from './urlPermission/url.permission';
import { UrlToolUpdatePermission } from './urlPermission/url.tool-update.permission';
import { ClaimAssignPermission } from './urlPermission/claim-assign-permission';
import { AdminPermission } from './urlPermission/admin-permission';

const  routes: Routes = [

  {
    path:'',pathMatch:'full',redirectTo:"/login"
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
    canActivate: [CheckUserLoggedInState]
  },
  {
    path: 'fetch-claims',
    loadChildren: () => import('./fetch-claims/fetch-claims.module').then(m => m.FetchClaimsModule),
    //canActivate: [UrlPermission]
  },
  {
    path:'register',
    loadChildren :()=> import("./register-new-user/register-new-user/register-new-user.module").then(m=>m.RegisterNewUserModule),
    canActivate: [AdminPermission]
  },
  {
    path:'user-setting',
    loadChildren:()=> import("./user-setting/user-setting/user-setting.module").then(m=>m.UserSettingModule),
    canActivate: [AdminPermission]
  },
  {
    path:'tool-update',
    loadChildren :()=> import("./tool-update-db/tool-update.module").then(m=>m.ToolUpdateModule),
    canActivate: [UrlToolUpdatePermission]
   
  },
  {
    path:'manage-office',
    loadChildren :()=> import("./manage-office/manage-office.module").then(m=>m.ManageOfficeModule),
    canActivate: [AdminPermission]
  },
  {
    path:'claim-assignment',
    loadChildren :()=> import("./claim-office-assignment/office-assignment.module").then(m=>m.ClaimAssignmentModule),
    canActivate: [ClaimAssignPermission]
  },
  {
    path:'users-status',
    loadChildren :()=> import("./users-status/users-status/users-status.module").then(m=>m.UserStatusModule),
    canActivate: [AdminPermission]
  },
  {
    path:'manage-client',
    loadChildren :()=> import("./manage-client/manage-client.module").then(m=>m.ManageClientModule),
    canActivate: [AdminPermission]
  },

  {
    path:'billing-claims/:uuid',
    loadChildren :()=> import("./billing-claims/billing-claims.module").then(m=>m.BillingClaimsModule),
  },
  {
    path:'billing-claims/:uuid/ivf',
    loadChildren:()=>import("./report/report.module").then(m=>m.ReportModule)
  },
  {
    path:'update-pass',
    loadChildren:()=>import("./update-pass/update-pass/update-pass.module").then(m=>m.UpdatePasswordModule)
  },

  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
