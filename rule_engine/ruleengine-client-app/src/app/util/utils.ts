export default class Utils {
    
   /**
    * Get roles from Authorities
    * @param auth
    */
    static getRoles(auth:any):any{
        let role=[];
        auth.forEach(function (value) {
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
       localStorage.setItem('token', token);
       localStorage.setItem('roles', Utils.getRoles(data.authorities));

   }
   
   /**
    * Reset Local Storage
    */

   static resetLocalStorage(){
       localStorage.removeItem('currentUser');
       localStorage.removeItem('token');
       localStorage.removeItem('roles');
   }
}  

