import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {RegisterComponent} from "./components/register/register.component";
import {UserSettingsComponent} from "./components/usersettings/usersettings.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {IVFComponent} from "./components/ivf/ivf.component";
import {IVFBatchPreComponent} from "./components/ivfbatchpre/ivfbatchpre.component";
import {IVFBatchComponent} from "./components/ivfbatch/ivfbatch.component";
import {LogoutComponent} from "./components/logout/logout.component";
import {HelpComponent} from "./components/help/help.component";
import {ReportComponent} from "./components/report/report.component";
import {TreatmentPlanComponent} from "./components/treatmentplan/treatmentplan.component";
import {UserInputComponent} from "./components/userinput/userinput.component";
import {ResetPasswordComponent} from "./components/resetpassword/resetpassword.component";
import {DynamicIVFComponent} from "./components/dynamic_ivf/dynamic_ivf.component";
import {DiagnosticComponent} from "./components/diagnostic/diagnostic.component";
import {EnReportsComponent} from "./components/enhanced_reports/enreports.component";
import {ScrapComponent} from "./components/scrap/scrap.component";
import {ScrapFullDataComponent} from "./components/scrapfulldata/scrapfulldata.component";
import {IVFDumpComponent} from "./components/ivfdump/ivfdump.component";
import {SealantelegbComponent} from "./components/sealantelegb/sealantelegb.component";



import {UrlPermission} from "./urlPermission/url.permission";
import {UrlDumpPermission} from "./urlPermission/url.dumppermission";

import {UrlAdminPermission} from "./urlPermission/url.adminpermission";
import {UrlLoggedInCheck} from "./urlPermission/url.checkloginstate";
import { OfficeResolve } from "./resolver/office_resolver";
import { OfficeAndIVFormTypeResolve } from "./resolver/office_ivformtype_resolver";



const appRoutes: Routes = [
  { path: 'profile', component: ProfileComponent ,canActivate: [UrlPermission] },
  { path: 'ivf',
	  resolve: {
		    offs: OfficeResolve
		  },
	  component: IVFComponent ,canActivate: [UrlPermission] },
  { path: 'dumpOldIVFData',
		  resolve: {
			    offs: OfficeResolve
			  },
		  component: IVFDumpComponent ,canActivate: [UrlDumpPermission] },	  
  { path: 'ivfcl',
		  resolve: {
			    offs: OfficeResolve
			  },
		  component: IVFComponent ,canActivate: [UrlPermission] },
  { path: 'ivfbatch',
		  resolve: {
			    offs: OfficeResolve
			  },
	  component: IVFBatchComponent ,canActivate: [UrlPermission] },
  { path: 'ivfclbatch',
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
  { path: 'usersettings', component: UserSettingsComponent,canActivate: [UrlAdminPermission]  },
  { path: 'resetpassword', component: ResetPasswordComponent,canActivate: [UrlPermission]  },
   { path: 'report', 
	  resolve: {
		    offsAndIVType: OfficeAndIVFormTypeResolve
		  },
  component: ReportComponent,canActivate: [UrlPermission]  },
  { path: 'reportcl', 
	  resolve: {
		  offsAndIVType: OfficeAndIVFormTypeResolve
		  },
  component: ReportComponent,canActivate: [UrlPermission]  },
  { path: 'ivftreatmentplan',
	  resolve: {
		    offs: OfficeResolve
		  },
  component: TreatmentPlanComponent,canActivate: [UrlPermission]  },
  { path: 'ivfclaimid',
	  resolve: {
		    offs: OfficeResolve
		  },
  component: TreatmentPlanComponent,canActivate: [UrlPermission]  },
  { path: 'userinput',
	  resolve: {
		    offs: OfficeResolve
		  },
  component: UserInputComponent,canActivate: [UrlPermission]  },
  { path: 'diagnosticcheck',
	  resolve: {
		    offs: OfficeResolve
		  },
  component: DiagnosticComponent,canActivate: [UrlPermission]  },
  { path: 'enreports',
	  resolve: {
		  offsAndIVType: OfficeAndIVFormTypeResolve
		  },
  component: EnReportsComponent,canActivate: [UrlPermission]  },
  { path: 'enreportscl',
	  resolve: {
		  offsAndIVType: OfficeAndIVFormTypeResolve
		  },
  component: EnReportsComponent,canActivate: [UrlPermission]  },
  { path: 'scrap',
	  resolve: {
		    offs: OfficeResolve
		  },
  component: ScrapComponent,canActivate: [UrlPermission]  },
  { path: 'scrapfulldata',
	  resolve: {
		    offs: OfficeResolve
		  },
  component: ScrapFullDataComponent,canActivate: [UrlPermission]  },
  { path: 'logout', component: LogoutComponent  },
  { path: 'help', component: HelpComponent  },
  { path: 'extIVF',
	  resolve: {
		    offs: OfficeResolve
	  },
	  loadChildren: './module/dynamicIvf.module#DynamicIVFAppModule' },
  { path: 'rulereport',
		  resolve: {
			    offs: OfficeResolve
		  },
		  loadChildren: './module/rule_report.module#RuleReportAppModule' },
{ path: 'sealant',
		  resolve: {
			    offs: OfficeResolve
		  },
		  loadChildren: './module/selants_ele.module#SealantelegbAppModule' },		  
  // otherwise redirect to profile
  
  { path: 'scrapremotelite',
	  resolve: {
		    offs: OfficeResolve
	  },
	  loadChildren: './module/scrap_lite.module#ScrapLiteAppModule' },
	  { path: '**', redirectTo: '/login' }
  
  
];

export const routing = RouterModule.forRoot(appRoutes);
