import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import { ClaimService  } from '../../service/claim.service';

import { AppConstants } from '../../constants/app.constants';
import { ClaimRcmDataModel ,ClaimEditModel,ServiceLevelCodeModel ,SubmissionDetailModel,
    ClaimRuleModel ,ClaimRuleRemarkModel,RuleEngineValModel,
    ClaimRuleRemarkModelS} from '../../models/claim-rcm-data-model';
import {ClaimRulesPullDataModel} from '../../models/claim-rules-pull-data-model';

import { Title } from '@angular/platform-browser';

import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-billing-claims',
  templateUrl: './billing-claims.component.html',
  styleUrls: ['./billing-claims.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class BillingClaimsComponent implements OnInit {



  claimRcm: ClaimRcmDataModel;
  claimARulesPullDataModel:ClaimRulesPullDataModel={};
  claimEditModel:ClaimEditModel;
  claimServiceLevelModel:ServiceLevelCodeModel;
  submissionDto:SubmissionDetailModel={};
  claimRules:Array<ClaimRuleModel>=[];
  claimRuleRemarks:Array<ClaimRuleRemarkModel>=[];
  reheading:string;

  ruleEngineReport:Array<RuleEngineValModel>=[];
  
  claimUUid:string="";
  ruleData:any=[];
  count:any={'pass':0,'fail':0,'alert':0};
  mtype:string='1';//Fail By Default.
  ivfData:any=[];

    constructor(public appService: ApplicationServiceService,public appConstants: AppConstants,
        private claimService: ClaimService,
      private route: ActivatedRoute,private title : Title) {
   this.claimRcm ={claimId:""};
   title.setTitle("RCM tool - Claim Detail");

   }

  
  ngOnInit(): void {

    this.route.paramMap.subscribe(params => {
      this.claimUUid=params.get('uuid') || '';
       this.fetchClaimsByUuid(this.claimUUid);
      });
    
   
  }

  fetchClaimsByUuid(uuid:string){

    let ths=this;
    ths.claimService.fetchBillingClaimsByUuid(uuid,(res:any)=>{
      if (res.status=== 200){
       ths.claimRcm= res.data;
       ths.fetchClaimNotes();
       ths.getServiceLevelCodes();
       ths.getSubmissionDetails();
       ths.getClaimRuleData();
       ths.runAutoRules(false);
      }
     
    });
  }

  fetchClaimNotes(){
    this.claimService.fetchClaimNotes(this.claimRcm.uuid,(res:any)=>{
        if (res.status=== 200){
           // ths.claimRcm= res.data;
           this.claimRcm.claimNotes=res.data;
           
           }
    })
   
  }
  fetchClaim(uuid:string,primary:boolean,type:string){
    let ths= this;
    if (type === 'Primary' &&  primary ) return;//NO Need to reclick
    if (type === 'Secondary' &&  !primary ) return;////NO Need to reclick
    if (uuid){
        ths.resetOldApiData();
        ths.fetchClaimsByUuid(uuid);
        
      }
  }

  resetOldApiData(){
    let ths= this;
    ths.claimRcm = {claimId:""};
    ths.claimServiceLevelModel={claimFound:false};
    ths.submissionDto={};
    ths.claimRules=[];
    ths.claimRuleRemarks=[];
    ths.ruleEngineReport=[];
    ths.reheading="";
  }
  getIVFData(){

    let ths=this;
   
    // ths.appService.fetchivfDataForClaim(ths.claimUUid,(res:any)=>{
    //   if (res.status=== 200){
      
    //   }
     
    // });
    ths.ivfData = 'dsf'
  }

  saveClaim(type:string){
    let ths = this;
    ths.claimEditModel={};
    ths.claimEditModel.claimUuid=ths.claimRcm.uuid;
    ths.claimEditModel.claimNoteDtoList=ths.claimRcm.claimNotes;
    ths.claimEditModel.claimRemark=ths.claimRcm.claimRemarks;
    ths.claimEditModel.serCVDto=ths.claimServiceLevelModel.dto;
    ths.claimEditModel.submissionDto=ths.submissionDto;
    ths.claimEditModel.ruleRemarkDto=[];
    ths.claimRules.forEach(x=>{
        if (x.remark!=null && x.remark.trim()!='') ths.claimEditModel.ruleRemarkDto.push(x);
      });
    ths.ruleEngineReport.forEach(x=>{

        let c= new ClaimRuleRemarkModelS(x.remark,null,null,null,Number(x.ruleId));
        if (x.remark!=null &&  x.remark.trim()!='') ths.claimEditModel.ruleRemarkDto.push(c);
    });
        
    if (type==='latter'){
        ths.claimService.saveClaimData(ths.claimEditModel,()=>{

        });
    }
    else if (type==='submit'){
        //do From Validation
        let valid= ths.validateData();
    } 
    else if (type==='assign'){
       
    } 
  }
  
  addErrorDisplay(el:HTMLElement){
    el.classList.add('bg-error');
  }

  removeErrorDisplay(el:HTMLElement){
    el.classList.remove('bg-error');
  }

  removeErrorDisplayKey(event:any){
    console.log(event.target.value);
    if (event.target.value.trim()!==''){
        event.target.classList.remove('bg-error');
    }

  }

  removeErrorDisplayKeyById(id:any,sub?:any){
    console.log(id);
    console.log(sub);
    console.log(id+(sub!=undefined?sub:""));
    this.removeErrorDisplay(document.getElementById(id+(sub!=undefined?sub:"")));

  }

  validateData():boolean{
    let ths = this;
     let valid:boolean=true;

     ths.claimRcm.claimNotes.forEach(no=>{
       if (no.value==null || no.value.trim()===''){
        ths.addErrorDisplay(document.getElementById("CL_N_"+no.id));
        valid=false;
        
       }  
     });

     
    ths.claimRules.forEach(x=>{
        if ((x.remark==null || x.remark.trim()==='') && x.messageType===1) {//on NO only
            ths.addErrorDisplay(document.getElementById("CL_RU"+x.ruleId));
            valid=false;
        }
        if (x.messageType===0) {//mark the Yes or No
            ths.addErrorDisplay(document.getElementById("CL_P_F_"+x.ruleId));
            valid=false;
        }
    });

    if (Object.keys(ths.submissionDto).length==0){
        ths.addErrorDisplay(document.getElementById("SUB_DET_CHA"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_ATT"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_PRE"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_REF"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_CLA"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_PRENO"));
        valid=false;
   }else{
        
       if (ths.submissionDto.channel===undefined)   ths.addErrorDisplay(document.getElementById("SUB_DET_CHA"));
       if (ths.submissionDto.attachmentSend===undefined)   ths.addErrorDisplay(document.getElementById("SUB_DET_ATT"));
       if (ths.submissionDto.preauth===undefined)   ths.addErrorDisplay(document.getElementById("SUB_DET_PRE"));
       if (ths.submissionDto.refferalLetter===undefined)   ths.addErrorDisplay(document.getElementById("SUB_DET_REF"));
       let SUB_DET_CLA:any = document.getElementById("SUB_DET_CLA");
       if (SUB_DET_CLA.value.trim()==='')   ths.addErrorDisplay(document.getElementById("SUB_DET_CLA"));
       let SUB_DET_PRENO:any = document.getElementById("SUB_DET_PRENO");
       if (SUB_DET_PRENO.value.trim()==='')   ths.addErrorDisplay(document.getElementById("SUB_DET_PRENO"));
       
 
    }

    if (ths.claimServiceLevelModel.claimFound){
      
        ths.claimServiceLevelModel.dto.forEach((x,i)=>{
            if ((x.remark==null || x.remark.trim()==='') && x.messageType===1) {//on NO only
                ths.addErrorDisplay(document.getElementById("SERV_C_V_"+i));
                valid=false;
            }
           
        });
    }else{
        ths.addErrorDisplay(document.getElementById("serviceCodeValidations"));
    }

    ths.ruleEngineReport.forEach(x=>{

        
        if (x.mtype==='1' &&  x.remark.trim()===''){
            ths.addErrorDisplay(document.getElementById("ENG_REP_"+x.ruleId));
            valid=false;
        }
    });
    

     console.log("valid",valid);
     return valid;

  }

  switchType(type:any){
      if(type=='pass'){
         this.mtype='2';
      }else if(type=='fail'){
        this.mtype='1';
     }else if(type==='alert'){
        this.mtype='3'
     }
  }



  showHide(index:any){
    let el:any = document.querySelectorAll(".bold-b-text");
    for(let i =0 ; i<el.length ; i++){
        if(i == index){
            el[i].classList.toggle("collapsed");
            el[i].nextElementSibling.classList.toggle("show");
        }
    }
  }

  getServiceLevelCodes(){
    let ths=this;
    ths.claimService.getServiceLevelCodes(ths.claimRcm.uuid,(res:any)=>{
        if (res.status=== 200){
           
            ths.claimServiceLevelModel=res.data;
        }
    })

  }

  getSubmissionDetails(){
    let ths=this;
    ths.claimService.getSubmissionDetails(ths.claimRcm.uuid,(res:any)=>{
        if (res.status=== 200){
            ths.submissionDto=res.data;
            if (ths.submissionDto==null) ths.submissionDto={};
        }
    })
  }

  getRulesClaimdata(){
    let ths=this;
    ths.claimService.getRulesClaimdata(ths.claimRcm.uuid,(res:any)=>{
        if (res.status=== 200){
            ths.claimRules=res.data;
            ths.getRuleRemarks();
           // if (ths.submissionDto==null) ths.submissionDto={};
        }
    })
  }

  getRuleRemarks(){
    
    let ths=this;
    ths.claimService.getRuleRemarks(ths.claimRcm.uuid,(res:any)=>{
        if (res.status=== 200){
            ths.claimRuleRemarks=res.data;
            if (ths.claimRuleRemarks.length>0){
                //put in 
                ths.claimRules.forEach(x=>{
                  let filter:Array<ClaimRuleRemarkModel>=  ths.claimRuleRemarks.filter(s=>s.ruleId===x.ruleId);
                  if (filter && filter.length==1){
                    x.remark=filter[0].remark;
                  }
                });

                ths.ruleEngineReport.forEach(x=>{
                    let filter:Array<ClaimRuleRemarkModel>=  ths.claimRuleRemarks.filter(s=>s.ruleId===Number(x.ruleId));
                    if (filter && filter.length==1){
                      x.remark=filter[0].remark;
                    }
                  });
                
            }
           
        }
    })

  }

  runAutoRules(reReun:boolean){
    
    let ths=this;
    ths.claimService.runAutoRules(ths.claimRcm.uuid,reReun,(res:any)=>{
        if (res.status=== 200){
            ths.getRulesClaimdata();
        }
    })
  }

  getClaimRuleData(){
    let ths=this;
    ths.claimARulesPullDataModel.claimId="15927";///ths.claimRcm.claimId.split("_")[0];
    ths.claimARulesPullDataModel.officeId="cc450da8-aaae-11e8-8544-8c16451459cd";//ths.claimRcm.officeUuid;
    ths.claimARulesPullDataModel.patientId="6602";//ths.claimRcm.patientId;

    ths.claimService.getClaimRuleData(ths.claimARulesPullDataModel,(res:any)=>{
        if (res.status=== 200){
            ths.ruleEngineReport=res.data;
            if (ths.ruleEngineReport.length>0)  
            ths.generateRuleEngineReportHeading(ths.ruleEngineReport[0]);

            this.count.pass=this.count.fail=this.count.alert=0;
            this.ruleEngineReport.forEach((e:any) => {
            if(e.mtype==1){
                this.count.fail = this.count.fail + 1;
            }
            else if(e.mtype==2){
                this.count.pass= this.count.pass +1;
            }else if(e.mtype==3){
                this.count.alert=this.count.alert+1;
            }
            });
            
        }
    })
  }

  generateRuleEngineReportHeading(rule:RuleEngineValModel){

    this.reheading='<span>Claim ID: <span>'+rule.claimId+' of '+rule.officeName+' office</span></span>'+
    '<span> | Pt.ID: <span>'+rule.patientId+'</span></span>'+
    '<span> | IV.ID: <span>'+rule.ivId+'</span></span>'+
    '<span> | Pt.Name: <span>'+rule.patientName+'</span></span>'+
    '<span> | DOS: <span>'+rule.dos+'</span></span>'+
    '<span> | Tx plan Date: <span>'+rule.dos+'</span></span>'+
    '<span> | Ins.Type : <span>'+rule.insuranceType+'</span></span>';
    
  }

  passFail(t:any,type:number){
      t=type;
  }
}
