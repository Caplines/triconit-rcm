import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CheckUserLoggedInState } from './urlPermission/url.checkloginstate';
import { UrlPermission } from './urlPermission/url.permission';

const routes: Routes = [

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
    canActivate: [UrlPermission]
  },
  {
    path:'register',
    loadChildren :()=> import("./register-new-user/register-new-user/register-new-user.module").then(m=>m.RegisterNewUserModule),
    canActivate: [CheckUserLoggedInState]
  },
  {
    path:'user-setting',
    loadChildren:()=> import("./user-setting/user-setting/user-setting.module").then(m=>m.UserSettingModule),
    // canActivate: [CheckUserLoggedInState]
  },
  {
    path:'tool-update',
    loadChildren :()=> import("./tool-update-db/tool-update.module").then(m=>m.ToolUpdateModule),
   
  },
  {
    path:'manage-office',
    loadChildren :()=> import("./manage-office/manage-office.module").then(m=>m.ManageOfficeModule),
   
  },
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
