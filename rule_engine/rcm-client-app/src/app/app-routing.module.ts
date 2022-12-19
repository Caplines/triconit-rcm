import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CheckUserLoggedInState } from './urlPermission/url.checkloginstate';
import { UrlPermission } from './urlPermission/url.permission';

const routes: Routes = [

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
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
