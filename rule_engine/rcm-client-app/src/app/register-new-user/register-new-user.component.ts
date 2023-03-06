import { Component, OnInit } from '@angular/core';
import {FormBuilder,Validators} from "@angular/forms";
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';

@Component({
  selector: 'app-register-new-user',
  templateUrl: './register-new-user.component.html',
  styleUrls: ['./register-new-user.component.css']
})
export class RegisterNewUserComponent implements OnInit {

  userDetails:any;
  teamData:any=[];
  officeData:any=[];
  defaulUserRoleData:any=[];
  companyData:any=[];
  userRoles:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':'','isError':false};
  showLoader:boolean=false;
  userRoleByTeam:any=[];
  showPassword:boolean=false;
  setPaddingContainer:boolean=false;
  constructor(private fb : FormBuilder, private appService: ApplicationServiceService,private title : Title) {
    title.setTitle("Register New User");
    this.userDetails = this.fb.group({
      'firstName' : ['',[Validators.required,Validators.minLength(3),Validators.maxLength(25),Validators.pattern("[a-zA-Z]*")]],
      'lastName' : ['',[Validators.required,Validators.minLength(3),Validators.maxLength(25),Validators.pattern("[a-zA-Z]*")]],
      'email' : ['',[Validators.required,Validators.email,Validators.maxLength(100),Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      'password': [ '', [ Validators.required, Validators.minLength(6), Validators.maxLength(20),]
     ],
      'companyName' : ['',Validators.required],
      'teamId' : ['',Validators.required],
      'userRole' : ['',Validators.required],
    })
   }

  ngOnInit(): void {
    this.getcompanyData();
    this.userDetails.reset();
    this.setPaddingRightContainer();
  }

  registerNewUser(){
    let isValid:any = this.checkFieldsAreValid();
    if(isValid.status && this.userDetails.valid){
      if(this.userRoles.includes("ADMIN") && (!this.userRoles.includes("TL") || !this.userRoles.includes("ASSO"))){
        this.userDetails.value.teamId="";
      }
    this.appService.registerUser(this.userDetails.value,(callback:any)=>{
      if(callback.status == 200){
        console.log(callback)
        this.showAlertPopup(callback);
        this.companyData = this.userRoles = this.defaulUserRoleData = this.officeData = this.teamData = this.userRoleByTeam = [];
        this.userDetails.reset();
        this.getcompanyData();
      } else if(callback.status == 400) { 
        this.showAlertPopup(callback);
      }
    })
  }
  else{
        if(isValid.field !== undefined && isValid.field == 'userRole'){
          this.showAlertPopup({'status':400,'message':"Please Select User Role"})
        }
        else{
          this.showAlertPopup({'status':400,'message':"Please Check Fields Again"})
          }
  }
    }

    checkFieldsAreValid(){
      if(this.userDetails.value.teamId != null && (this.userDetails.value.userRole == '' || this.userDetails.value.userRole == null || this.userDetails.value.userRole.length==0)){
        return {'status':false,'field':'userRole'};
      }
      if(this.userDetails.value.teamId !=null && this.userDetails.value.teamId != '' && (this.userRoles.includes('UPLOAD_CLAIMS') && (!this.userRoles.includes("TL") && !this.userRoles.includes("ASSO")))){
        return {'status':false,'field':'userRole'};
      }
       else{
        return {'status':true};
      }
      
    }

  getTeamsData(event:any){
    this.showLoader=true;
    this.appService.fetchTeamsNameData(event.target.value,(callback:any)=>{
      if(callback.status){
        this.showLoader = false;
        this.teamData = callback.data;
        this.userRoleByTeam=[];
        console.log(this.userRoles)
      }
    })
  }

  getOfficeData(){
    this.showLoader=true;
    this.appService.fetchOfficeData((callback:any)=>{
      if(callback.status){
        this.showLoader = false;
        this.officeData = callback.data
      }
    })
  }

  getcompanyData(){
    this.appService.fetchCompanyNameData((callback:any)=>{
      if(callback.status){
        this.companyData = callback.data.data;
      }
    })
  }

  selectDefaultUserRole(event:any){
    
    if((event.target.value == "true" && event.target.id == "ADMIN") || (event.target.checked == true && event.target.id=="UPLOAD_CLAIMS")){
      if(this.userRoles.length==0){
        this.userRoles.push(event.target.id);
      }else { 
        let alreadyExist = this.userRoles.some((e:any)=> e == event.target.id);
        if(!alreadyExist){
          this.userRoles.push(event.target.id);
        }else{
          for(let i=0;i<this.userRoles.length;i++){
            if(this.userRoles[i] == event.target.id){
              this.userRoles.splice(i,1);
              break;
            }
          }
        }
      }
      if(event.target.id == "ADMIN"){
        let indAsso = this.userRoles.indexOf("ASSO");
        indAsso !== -1 ? this.userRoles.splice(indAsso,1) : '';
        let indTL = this.userRoles.indexOf("TL");
        indTL  !== -1 ? this.userRoles.splice(indTL,1) : '';
        this.teamData=this.userRoleByTeam=[];
      }
    }else if((event.target.value == 'false' && event.target.id == "ADMIN") || (event.target.checked == false && event.target.id=="UPLOAD_CLAIMS")){
      for(let i=0;i<this.userRoles.length;i++){
        if(this.userRoles[i] == event.target.id){
          this.userRoles.splice(i,1);
          break;
        }
      }
      if(event.target.id == "ADMIN"){
        let evt:any =  {'target':{'value':this.userDetails.controls.companyName.value}}
        this.getTeamsData(evt);
        if(this.userRoles.includes("TL") || this.userRoles.includes("ASSO")){
          let indAsso = this.userRoles.indexOf("ASSO");
          indAsso !== -1 ? this.userRoles.splice(indAsso,1) : '';
          let indTL = this.userRoles.indexOf("TL");
          indTL  !== -1 ? this.userRoles.splice(indTL,1) : '';
        }
      }
    }
    this.userDetails.controls.userRole.setValue(this.userRoles);
    if (this.userRoles.includes("ADMIN")) {
      this.userDetails.controls.teamId.setValidators('');
      this.userDetails.controls.teamId.updateValueAndValidity();
    } if ((event.target.checked == true && event.target.id == "UPLOAD_CLAIMS")) {
      this.userDetails.controls.teamId.setValidators('');
      this.userDetails.controls.teamId.setValue('');
      let select_box: any = document.getElementById("select");
      select_box.selectedIndex = 0;
      this.userDetails.controls.teamId.updateValueAndValidity();
    }
    if ((event.target.checked == false && event.target.id == "UPLOAD_CLAIMS")) {
      this.userDetails.controls.teamId.setValidators('');
      this.userDetails.controls.teamId.setValue('');
      let select_box: any = document.getElementById("select");
      select_box.selectedIndex = 0;
      this.userDetails.controls.teamId.updateValueAndValidity();
    }
    if (event.target.checked == false && (this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
      this.userDetails.controls.teamId.setValidators(Validators.required);
      this.userDetails.controls.teamId.updateValueAndValidity();
    }
    if (event.target.checked == true && (this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
      this.userDetails.controls.teamId.setValidators(Validators.required);
      this.userDetails.controls.teamId.updateValueAndValidity();
    }

    console.log(this.userRoles);
  }

  selectUserRole(event:any){
    if(this.userRoles.length>0){
      if(event.target.id == "TL"){
        let alreadyExist = this.userRoles.some((e:any)=> e == event.target.id);
        if(!alreadyExist){
          this.userRoles.push(event.target.id);
          if(!this.userRoles.includes("ASSO")){
            this.userRoles.push("ASSO");
          }
        } else{
          for(let i=0;i<this.userRoles.length;i++){
            if(this.userRoles[i] == event.target.id){
              this.userRoles.splice(i,1);
              if(event.target.id == "TL"){
                let indAsso = this.userRoles.indexOf("ASSO")
                indAsso !== -1 ? this.userRoles.splice(indAsso,1) : '';
              }
              break;
            }
          }
        }
      }else{
        if(this.userRoles.includes("ASSO")){
          for(let i=0;i<this.userRoles.length;i++){
            if(this.userRoles[i] == event.target.id){
              this.userRoles.splice(i,1);
              break;
            }
          }
        }else{
         let alreadyExist = this.userRoles.some((e:any)=> e == event.target.id);
        if(!alreadyExist){
          this.userRoles.push(event.target.id);
        } else{
          for(let i=0;i<this.userRoles.length;i++){
            if(this.userRoles[i] == event.target.id){
              this.userRoles.splice(i,1);
              break;
            }
          }
        }
        }
      }
    }else if(this.userRoles.length == 0){
      if(event.target.id == "TL"){
        let alreadyExist = this.userRoles.some((e:any)=> e == event.target.id);
        if(!alreadyExist){
          this.userRoles.push(event.target.id);
          if(!this.userRoles.includes("ASSO")){
            this.userRoles.push("ASSO");
          }
        } else{
          for(let i=0;i<this.userRoles.length;i++){
            if(this.userRoles[i] == event.target.id){
              this.userRoles.splice(i,1);
              break;
            }
          }
        }
    }
    else{
      if(this.userRoles.includes("ASSO")){
        for(let i=0;i<this.userRoles.length;i++){
          if(this.userRoles[i] == event.target.id){
            this.userRoles.splice(i,1);
            break;
          }
        }
      }else{
        let alreadyExist = this.userRoles.some((e:any)=> e == event.target.id);
        if(!alreadyExist){
          this.userRoles.push(event.target.id);
        } else{
          for(let i=0;i<this.userRoles.length;i++){
            if(this.userRoles[i] == event.target.id){
              this.userRoles.splice(i,1);
              break;
            }
          }
        }
      }
    }
  }
    this.userDetails.controls.userRole.setValue(this.userRoles);
    if(this.userRoles.includes("TL") || this.userRoles.includes("ASSO")){
      this.userDetails.controls.teamId.setValidators(Validators.required);
      this.userDetails.controls.teamId.updateValueAndValidity();
    }
      console.log(this.userRoles)
  }

  
  changeTeamMandatoryStatus(role: any) {
    let k = this.defaulUserRoleData;
    k.find((e: any) => {
      if (e.roleId === role) {
        if (e.teamMandatory && (this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
          this.userDetails.controls.teamId.setValidators([Validators.required]);
          this.userDetails.controls.teamId.updateValueAndValidity();
        } else if (!(this.userRoles.includes("TL") || this.userRoles.includes("ASSO"))) {
          this.userDetails.controls.teamId.setValidators();
          this.userDetails.controls.teamId.updateValueAndValidity();
        } else {
          this.userDetails.controls.teamId.setValidators([Validators.required]);
          this.userDetails.controls.teamId.updateValueAndValidity();
        }

      }
    })

  }

  selectCompany(e:any){
    this.userDetails.controls.companyName.setValue(e.target.value);
    if(this.userDetails.controls.companyName.value !== "Smilepoint"){
      this.getTeamsData(e);
    }else{
      this.teamData = this.userRoles=this.userRoleByTeam=[];
      this.userDetails.controls.teamId.setValue('');
    }
  }

  selectTeamName(e:any){
    this.userDetails.controls.teamId.setValue(e.target.value);
    if(this.userRoles.includes("TL")||this.userRoles.includes("ASSO"))
    {
      let indAsso = this.userRoles.indexOf("ASSO");
      indAsso !== -1 ? this.userRoles.splice(indAsso,1) : '';
      let indTL = this.userRoles.indexOf("TL");
      indTL  !== -1 ? this.userRoles.splice(indTL,1) : '';
    }
    if(this.userRoles.includes("CLIENT_MANAGER") || this.userRoles.includes("CLIENT_VIEW_ONLY")){
      let cm = this.userRoles.indexOf("CLIENT_MANAGER");
      cm !== -1 ? this.userRoles.splice(cm,1) : '';
      let cvo = this.userRoles.indexOf("CLIENT_VIEW_ONLY");
      cvo  !== -1 ? this.userRoles.splice(cvo,1) : '';
      this.userRoles=[];
    }
    this.getRolesByTeam(e);
  }

  getRolesByCompany(event:any){
    this.appService.fetchRolesByCompany(event.target.value,(callback:any)=>{
      if(callback.status){
        this.userRoles =[];
        this.userDetails.controls.userRole.setValue('');
        this.defaulUserRoleData = callback.data;
      }
    })
  }

  getRolesByTeam(event:any){
    if(event.target.value != ''){
      this.appService.fetchRolesByTeam(event.target.value,(callback:any)=>{
        if(callback.status){
          this.userRoleByTeam = callback.data;
      }
    })
  }
  }

  showAlertPopup(res:any){
    this.alert.showAlertPopup = true;
    res.status==400 ? this.alert.isError=true : this.alert.isError=false;
    this.alert.alertMsg = res.message ? res.message : res.result.message;
    setTimeout(() => {this.alert.showAlertPopup=false;}, 2500);
    scrollTo(0,0);
  }

  setPaddingRightContainer(){
    let m:any = document.getElementsByClassName("gray-bar");
    console.log(m[0].clientHeight)
    if(m[0].clientHeight>55){
      this.setPaddingContainer=true;
    }
  }
}