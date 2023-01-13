import { Component, OnInit } from '@angular/core';
import { BaseService } from '../service/base-service.service';

@Component({
  selector: 'app-user-setting',
  templateUrl: './user-setting.component.html',
  styleUrls: ['./user-setting.component.scss']
})
export class UserSettingComponent implements OnInit {

  user: any = { 'username': '', 'showChangePassword': false, 'showStatus': false, 'changedPassword': '', 'uuid': '' };
  allUser: any = [];
  showActionPopup: boolean = false;
  userRole: any;
  userName:any;
  userStatus:any= {'enable':[],'disable':[]}
  constructor(private _baseService: BaseService) { }

  ngOnInit(): void {
    this.userRole = localStorage.getItem("roles");
    this.userName = localStorage.getItem("currentUser")
  }
  logout() {
    localStorage.clear();
    window.location.href = "/"
  }

  findUser() {
    this._baseService.findUser({ "username": this.user.username }, (callback: any) => {
      if (callback.result.status == 200 && callback.result.data) {
        this.showActionPopup = true;
        this.user.uuid = callback.result.data.uuid;
      } else {
        this.showActionPopup = false;
        alert(callback.result.message)
      }
    })
    // console.log(this.user.username)
  }

  changePassword() {
    this._baseService.changePassword({ "uuid": this.user.uuid, "password": this.user.changedPassword }, (callback: any) => {
      if (callback.result.status == 200) {
        console.log(callback)
        alert(callback.result.message)
        localStorage.clear();
        window.location.href= "/"
      }
    })
  }

  userAction(event: any) {
    if (event.target.value === 'changePass') {
      this.user.showChangePassword = true;
    } else if (event.target.value === 'status') {
      this.user.showStatus = true;
    }
  }

  findAllUser() {
    this._baseService.findAllUser((callback: any) => {
      if (callback.result.status == 200) {
        this.allUser = callback.result.data;
      }
    })
  }

  // updateStatus(uuid: any, status: any) {
  //   this.allUser.find((e: any) => {
  //     if (e.uuid === uuid) {
  //       e.active == 1 ? e.active = 0 : e.active = 1;
  //       status = e.active
  //       let params: object = { "uuid": uuid, "status": status }
  //       this._baseService.updateUserStatus(params, (callback: any) => {
  //         if (callback.result.status == 200) {
  //           alert(callback.result.message)
  //         } else {
  //           console.log(callback.result)
  //         }
  //       })
  //     }
  //   })
  // }

  differentiateStatus(uuid:any){
    console.time()
    this.allUser.find((e:any)=>{
      if(e.uuid === uuid){
        if(e.active == 0){
          e.active = 1;
          if(!this.userStatus.enable.includes(uuid)){
            if(this.userStatus.disable.includes(uuid)){
              this.userStatus.disable.find((e:any,index:any)=>{
                if(e == uuid){
                  this.userStatus.disable.splice(index,1)
                }
              })
            }
              this.userStatus.enable.push(uuid)
          }
          return ;
        }
        else{
          e.active = 0;
          if(!this.userStatus.disable.includes(uuid)){
            if(this.userStatus.enable.includes(uuid)){
              this.userStatus.enable.find((e:any,index:any)=>{
                if(e == uuid){
                  this.userStatus.enable.splice(index,1)
                }
              })
            } 
              this.userStatus.disable.push(uuid)
          }
        }
        return ;
      }
      return;
    })
    console.timeEnd();
    console.log(this.userStatus)

  }

  updateUserStatus(){
    if (this.userStatus.enable.length != 0 || this.userStatus.disable.length != 0) {
      this._baseService.updateUserStatus(this.userStatus, (callback: any) => {
        if (callback.result.status == 200) {
          alert(callback.result.message)
        } else {
          console.log(callback.result)
        }
      })
    } else { 
      alert("No Changes detected")
    }
  }

  updateSingleUserStatus(){
    this.userStatus.disable.push(this.user.uuid);
    this._baseService.updateUserStatus(this.userStatus, (callback: any) => {
      if (callback.result.status == 200) {
        alert(callback.result.message)
      } else {
        console.log(callback.result)
      }
    })
  }
}
