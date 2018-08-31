import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {RegisterComponent} from "./components/register/register.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {IVFComponent} from "./components/ivf/ivf.component";
import {IVFBatchPreComponent} from "./components/ivfbatchpre/ivfbatchpre.component";
import {IVFBatchComponent} from "./components/ivfbatch/ivfbatch.component";
import {LogoutComponent} from "./components/logout/logout.component";
import {HelpComponent} from "./components/help/help.component";
import {ReportComponent} from "./components/report/report.component";
import {TreatmentPlanComponent} from "./components/treatmentplan/treatmentplan.component";

import {UrlPermission} from "./urlPermission/url.permission";
import {UrlAdminPermission} from "./urlPermission/url.adminpermission";
import {UrlLoggedInCheck} from "./urlPermission/url.checkloginstate";
import { OfficeResolve } from "./resolver/office_resolver";




const appRoutes: Routes = [
  { path: 'profile', component: ProfileComponent ,canActivate: [UrlPermission] },
  { path: 'ivf',
	  resolve: {
		    offs: OfficeResolve
		  },
	  component: IVFComponent ,canActivate: [UrlPermission] },
  { path: 'ivfbatch',
		  resolve: {
			    offs: OfficeResolve
			  },
	  component: IVFBatchComponent ,canActivate: [UrlPermission] },
  { path: 'ivfbatchpre',
		  resolve: {
			    offs: OfficeResolve
			  },
	  component: IVFBatchPreComponent ,canActivate: [UrlPermission] },
  { path: 'login', component: LoginComponent ,canActivate: [UrlLoggedInCheck]},
  { path: 'register', component: RegisterComponent,canActivate: [UrlAdminPermission]  },
  { path: 'report', 
	  resolve: {
		    offs: OfficeResolve
		  },
  component: ReportComponent,canActivate: [UrlPermission]  },
  { path: 'ivftreatmentplan',
	  resolve: {
		    offs: OfficeResolve
		  },
  component: TreatmentPlanComponent,canActivate: [UrlPermission]  },
  { path: 'logout', component: LogoutComponent  },
  { path: 'help', component: HelpComponent  },
  // otherwise redirect to profile
  { path: '**', redirectTo: '/login' }
];

export const routing = RouterModule.forRoot(appRoutes);
