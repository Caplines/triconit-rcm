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
    *store user details  in local storage to keep user logged in between page refreshes
    * @param data
    * @param token
    */
   static setLocalStorage(data:any,token:string){
       localStorage.setItem('currentUser', data.userName);
       //localStorage.setItem('userType', data.userType);
       localStorage.setItem('token', token);
       localStorage.setItem('teamId', data.teamId);
       localStorage.setItem('roles', Utils.getRoles(data.authorities));
       
   }
   
   /**
    * Reset Local Storage
    */

   static resetLocalStorage(){
       localStorage.removeItem('currentUser');
       localStorage.removeItem('teamId');
       localStorage.removeItem('token');
       localStorage.removeItem('roles');
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
	    if (ls.getItem('currentUser') && ls.getItem('roles').indexOf("ROLE_ADMIN")>0) {
	        return true;
	     }
        return false;
   }

}
