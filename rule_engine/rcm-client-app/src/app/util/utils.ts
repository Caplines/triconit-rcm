import { AppConstants } from '../constants/app.constants';
export default class Utils {

   constructor(public appConstants: AppConstants) { }
   /**
    * Get roles from Authorities
    * @param auth
    */
   static getRoles(auth: any): any {
      let role: any = [];
      auth.forEach(function (value: any) {
         role.push(value.authority);
      });
      return role;
   }


   /**
  * Get Clients from companies
  * @param clients
  */
   static getClients(clients: any): any {
      let cpm: any = [];
      clients.forEach(function (value: any) {
         cpm.push({ "name": value.name, "id": value.uuid });
      });

      return JSON.stringify(cpm);
   }

   /**
   *Get Clients from LS
   */
   static getClientsFromLS(): any {
      return JSON.parse(localStorage.getItem("clients"));
   }
   /**
   *Get Clients from LS
   */
   static getTeamsFromLS(): any {
      return JSON.parse(localStorage.getItem("teams"));
   }
   /**
 * Get Clients from companies
 * @param auth
 */
   static getTeams(teams: any): any {
      let tm: any = [];
      teams.forEach(function (value: any) {
         tm.push({ "name": value.name, "id": value.id, "nameId": value.nameId });
      });
      return JSON.stringify(tm);
   }

   /**
    *store user details  in local storage to keep user logged in between page refreshes
    * @param data
    * @param token
    */
   static setLocalStorage(data: any, token: string) {
      console.log(data);
      localStorage.setItem('currentUser', data.userName);
      //localStorage.setItem('userType', data.userType);
      localStorage.setItem('token', token);

      localStorage.setItem('name', data.firstName);
      localStorage.setItem('lastName', data.lastName);
      this.setLocalStoragePhase2(data.authorities, data.companies, data.teams);

   }

   static setLocalStoragePhase2(auth: any, comp: any, teams: any) {
      localStorage.setItem('roles', Utils.getRoles(auth));
      localStorage.setItem('clients', Utils.getClients(comp));
      localStorage.setItem('teams', Utils.getTeams(teams));
   }

   static setLocalStoragePartial(clientName: string, roleName: any, teamId: number) {

      localStorage.setItem('selected_clientName', clientName);
      localStorage.setItem('selected_roleName', roleName);
      localStorage.setItem('selected_teamId', teamId + "");


   }

   static setRefreshToken(data: any) {

      localStorage.setItem("token", (<any>data)[0].token);
      this.setLocalStoragePhase2(data[1], data[3], data[2]);

   }

   /**
    * Reset Local Storage
    */

   static resetLocalStorage() {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('teamId');
      localStorage.removeItem('token');
      localStorage.removeItem('roles');
      localStorage.removeItem('name');
      localStorage.removeItem('lastName');
      localStorage.removeItem('clientName');
      localStorage.removeItem('selected_clientName')
      localStorage.removeItem('selected_roleName');
      localStorage.removeItem('selected_teamId');

   }
   /*
   static fetchUserTypeFromLocalStorage():string{
       let ut=localStorage.getItem('userType');
       if (!ut) ut="1";
       return ut;
       
   }
   */
   static checkAdmin() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_ADMIN") > -1) {
         return true;
      }
      return false;
   }
   static checkAdminLoginRole() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('loginAs').indexOf("Admin") > -1) {
         return true;
      }
      return false;
   }

   static checkRoleAdmin() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('selected_roleName') && ls.getItem('selected_roleName')?.indexOf("ADMIN") > -1) {
         return true;
      }
      return false;
   }


   static checkRoleSuperAdmin() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('selected_roleName') && ls.getItem('selected_roleName')?.indexOf("SUPER_ADMIN") > -1) {
         return true;
      }
      return false;
   }



   static isRoleLead() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('selected_roleName') && ls.getItem('selected_roleName')?.indexOf("TL") > -1) {
         return true;
      }
      return false;
   }

   static isClientManager() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_CLIENT_CLIENT_MANAGER") > -1) {
         return true;
      }
      return false;
   }

   static isBillingAsso() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_BILLING_ASSO") > -1) {
         return true;
      }
      return false;
   }

   static isRoleAsso() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('selected_roleName') && ls.getItem('selected_roleName')?.indexOf("ASSO") > -1) {
         return true;
      }
      return false;
   }

   static isSmilePoint() {
      let ls: any = localStorage;
      if (ls.getItem('currentUser') && ls.getItem('selected_clientName') && ls.getItem('selected_clientName') === this.getDefaultClient()) {
         return true;
      }
      return false;
   }

   static isAccountpopupNeeded() {
      let ls: any = localStorage;
      if (ls.getItem('selected_clientName') == null ||
         ls.getItem('selected_roleName') == null ||
         ls.getItem('selected_teamId') == null) {
         return true;
      }
      return false;
   }

   static getRolesFromLS(): any {
      return localStorage.getItem("roles").split(",");
   }

   static isLoggedIn() {
      if (localStorage.getItem('currentUser')) {
         return true;
      } else {
         return false;
      }
   }

   static isSessionSet() {
      if (localStorage.getItem('selected_clientName')) {
         return true;
      } else {
         return false;
      }
   }

   static setSession(data: any) {
      this.setRefreshToken(data);
      this.setLocalStoragePhase2(data[1], data[3], data[1]);
   }

   static isRegisterVisible() {
      localStorage.getItem('selected_clientName');
   }


   static logout() {
      localStorage.clear();
      this.setLastPageVisited();
      window.location.href = "/login";
   }

   static setLastPageVisited() {
      localStorage.setItem("lastVisitedPage", location.pathname);
   }

   static clearLastPageVisited() {
      localStorage.removeItem("lastVisitedPage");
   }

   static selectedTeam() {
      return Number(localStorage.getItem('selected_teamId'));
   }

   static getDefaultClient() {
      return "Smilepoint";
   }

   static isBilling(): boolean {
      return (Number(localStorage.getItem('selected_teamId')) == AppConstants.BILLING_TEAM);
   }

   static isInternalAudit(): boolean {
      return (Number(localStorage.getItem('selected_teamId')) == AppConstants.INTERNAL_AUDIT_TEAM);
   }

   static isCDP(): boolean {
      return (Number(localStorage.getItem('selected_teamId')) == AppConstants.CDP_TEAM);
   }


   static getTimeZone(): string {
      return new Date().toString().split("GMT")[1].split(" (")[0]; // timezone, i.e. -0700
   }

   static defaultTitle: string = 'RCM Tool - ';

   static currentUserEmail() {
      return (localStorage.getItem("currentUser"));
   }

   static isNotTeamOffice() {
      return <any>localStorage.getItem("selected_teamId") != 5 ? true : false;
   }

   static getLoggedInUserFirstName(): string {
      let ls: any = localStorage;
      if (ls.getItem('name')) {
         return ls.getItem('name');
      }
      return '';
   }

   static getLoggedInUserLastName(): string {
      let ls: any = localStorage;
      if (ls.getItem('lastName')) {
         return ls.getItem('lastName');
      }
      return '';
   }

   static getLoggedInUserFirstAndLastName(): string {
      return this.getLoggedInUserFirstName() + " " + this.getLoggedInUserLastName();
   }

   static getSelectedClientName(): string {
      let ls: any = localStorage;
      if (ls.getItem('selected_clientName')) {
         return ls.getItem('selected_clientName');
      }
      return '';
   }
}
