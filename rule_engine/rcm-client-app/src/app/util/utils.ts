export default class Utils {
    
   /**
    * Get roles from Authorities
    * @param auth
    */
    static getRoles(auth:any):any{
        let role:any=[];
        auth.forEach(function (value:any) {
            role.push(value.authority);
          });
        return role;
    }


     /**
    * Get Clients from companies
    * @param clients
    */
     static getClients(clients:any):any{
      let cpm:any=[];
      clients.forEach(function (value:any) {
         cpm.push({"name":value.name,"id":value.uuid});
        });
        
      return JSON.stringify(cpm);
    }

    /**
    *Get Clients from LS
    */
     static getClientsFromLS():any{
      return JSON.parse(localStorage.getItem("clients"));
    }
    /**
    *Get Clients from LS
    */
    static getTeamsFromLS():any{
      return JSON.parse(localStorage.getItem("teams"));
    }
      /**
    * Get Clients from companies
    * @param auth
    */
      static getTeams(teams:any):any{
         let tm:any=[];
         teams.forEach(function (value:any) {
            tm.push({"name":value.name,"id":value.id,"nameId":value.nameId});
           });
           return JSON.stringify(tm);
     }
   
   /**
    *store user details  in local storage to keep user logged in between page refreshes
    * @param data
    * @param token
    */
   static setLocalStorage(data:any,token:string){
      console.log(data);
       localStorage.setItem('currentUser', data.userName);
       //localStorage.setItem('userType', data.userType);
       localStorage.setItem('token', token);
       localStorage.setItem('roles', Utils.getRoles(data.authorities));
       localStorage.setItem('name', data.firstName);
       localStorage.setItem('clients', Utils.getClients(data.companies));
       localStorage.setItem('teams', Utils.getTeams(data.teams));
       
   }

   static setLocalStoragePartial(clientName:string,roleName:any,teamId:number){
    
       localStorage.setItem('selected_clientName', clientName);
       localStorage.setItem('selected_roleName', roleName);
       localStorage.setItem('selected_teamId',teamId+"");
     
       
   }
   
   static setRefreshToken(data:any){

    localStorage.setItem("token", (<any>data)[0].token);
    localStorage.setItem('roles', Utils.getRoles((<any>data)[1]));

   }
   
   /**
    * Reset Local Storage
    */

   static resetLocalStorage(){
       localStorage.removeItem('currentUser');
       localStorage.removeItem('teamId');
       localStorage.removeItem('token');
       localStorage.removeItem('roles');
       localStorage.removeItem('name');
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
   static checkAdmin(){
    let ls:any=localStorage;
	    if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_ADMIN")>-1) {
	        return true;
	     }
        return false;
   }

   static isBillingLead(){
    let ls:any=localStorage;
       if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_BILLING_TL")>-1) {
	        return true;
	     }
        return false;
   }

   static isClientManager(){
    let ls:any=localStorage;
       if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_CLIENT_CLIENT_MANAGER")>-1) {
	        return true;
	     }
        return false;
   }

   static isBillingAsso(){
    let ls:any=localStorage;
       if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_BILLING_ASSO")>-1) {
	        return true;
	     }
        return false;
   }

   static isSmilePoint(){
    let ls:any=localStorage;
       if (ls.getItem('currentUser') && ls.getItem('cname')==='Smilepoint') {
	        return true;
	     }
        return false;
   }

   static isAccountpopupNeeded(){
      let ls:any=localStorage;
      if (ls.getItem('selected_clientName')==null ||
           ls.getItem('selected_roleName') ==null ||
          ls.getItem('selected_teamId') ==null){
            return true;
          }
      return false;   
   }

   static getRolesFromLS():any{
      return  localStorage.getItem("roles").split(",");
    }

  

}
