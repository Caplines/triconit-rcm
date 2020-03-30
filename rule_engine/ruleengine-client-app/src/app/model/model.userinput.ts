import {StatusList} from '../util/status.list.component';

export class UserInputModel extends StatusList {
  treatmentPlanId: string="";
  officeId:string="";
  ivfId:string="";
  inputMode:any;
  debugMode:boolean=false;
  inputModeD:boolean=false;//This is never used in Back-end
}
