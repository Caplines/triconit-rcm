import {StatusList} from '../util/status.list.component';
import {IgnoreDataModel} from './model.ignoredata';
export class UserInputModel extends StatusList {
  treatmentPlanId: string="";
  officeId:string="";
  ivfId:string="";
  inputMode:any;
  debugMode:boolean=false;
  inputModeD:boolean=false;//This is never used in Back-end
  ignoreData:Array<IgnoreDataModel>=[]; 
}
