import {BillingModel} from './bill.model';

export class  BillingList {

  bills: Array<BillingModel>=[];//Billing, Re-Billing
  insTypes: Array<BillingModel>=[];//PPO , Medicaid
 
    constructor(){
    	//super("","");
    	this.setUpBills();
    	this.setUpInsurance();
    }
    
    setUpBills(){
    	
    	this.bills.push(new BillingModel("-1","All"));
    	this.bills.push(new BillingModel("1","Billing"));
    	this.bills.push(new BillingModel("2","Re-Billing"));
    	
    	
    }
    setUpInsurance(){
    	
      
    	this.insTypes.push(new BillingModel("All","All"));
    	this.insTypes.push(new BillingModel("Medicaid","Medicaid"));
    	this.insTypes.push(new BillingModel("PPO","PPO"));
    	
    	
    }
    
}
