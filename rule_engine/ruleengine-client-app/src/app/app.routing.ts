import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {RegisterComponent} from "./components/register/register.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {IVFComponent} from "./components/ivf/ivf.component";
import {LogoutComponent} from "./components/logout/logout.component";
import {ReportComponent} from "./components/report/report.component";

import {UrlPermission} from "./urlPermission/url.permission";
import {UrlAdminPermission} from "./urlPermission/url.adminpermission";
import {UrlLoggedInCheck} from "./urlPermission/url.checkloginstate";



const appRoutes: Routes = [
  { path: 'profile', component: ProfileComponent ,canActivate: [UrlPermission] },
  { path: 'ivf', component: IVFComponent ,canActivate: [UrlPermission] },
  { path: 'login', component: LoginComponent ,canActivate: [UrlLoggedInCheck]},
  { path: 'register', component: RegisterComponent,canActivate: [UrlAdminPermission]  },
  { path: 'report', component: ReportComponent,canActivate: [UrlAdminPermission]  },
  { path: 'logout', component: LogoutComponent  },

  // otherwise redirect to profile
  { path: '**', redirectTo: '/login' }
];

export const routing = RouterModule.forRoot(appRoutes);
