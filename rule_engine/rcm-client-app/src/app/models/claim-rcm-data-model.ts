
export interface  ClaimRcmDataModel {

    uuid?:string;
	claimId:string;
	dos?:Date;
	patientDob?:Date;
	patientId?:string;
    patientName?:string;
	pending?:boolean;
	primDateSent?:Date;
	primeStatus?:string;
	primeTotalPaid?:number;
	source?:string;
	secDateSend?:Date;
	secStatus?:string;
	secSubmittedTotal?:number;
	submittedTotal?:number;
	simeFilLimitDay?:string;
	officeName?:string;
	officeUuid?:string;
	claimStatus?:number;
	lastTeam?:string;
	currentTeam?:string;
	primInsurance?:string;
	secInsurance?:string;
	groupNumber?:string;
	primePolicyHolder?:string;
	primeSecSubmittedTotal?:number;
	policyHolderDob?:Date;
	createdDate?:Date;
	assignedTo?:string;
	eEmail?:string;
	firstName?:string;
	lastName?:string;
	primaryInsType?:string;
	secondaryInsType?:string;
	clientName?:string;
    regenerated?:boolean;
    secMemberId?:string;
    linkedClaims?:Array<string>;
	providerId?:string;
	pulledDate?:Date;
	timeFilLimitDay?:string;
	secPolicyHolder?:string;

}
