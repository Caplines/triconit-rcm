
export interface ClaimRcmDataModel {

	uuid?: string;
	claimId: string;
	dos?: Date;
	patientDob?: Date;
	patientId?: string;
	patientName?: string;
	pending?: boolean;
	primDateSent?: Date;
	primeStatus?: string;
	primeTotalPaid?: number;
	source?: string;
	secDateSend?: Date;
	secStatus?: string;
	secSubmittedTotal?: number;
	submittedTotal?: number;
	simeFilLimitDay?: string;
	officeName?: string;
	officeUuid?: string;
	claimStatus?: number;
	lastTeam?: string;
	currentTeam?: string;
	currentTeamId?: number;
	primInsurance?: string;
	secInsurance?: string;
	primaryInsCode?: string;
	secondaryInsCode?: string;
	groupNumber?: string;
	primePolicyHolder?: string;
	primeSecSubmittedTotal?: number;
	policyHolderDob?: Date;
	createdDate?: Date;
	assignedTo?: string;
	eEmail?: string;
	firstName?: string;
	lastName?: string;
	primaryInsType?: string;
	secondaryInsType?: string;
	clientName?: string;
	regenerated?: boolean;
	secMemberId?: string;
	linkedClaims?: Array<string>;
	providerId?: string;
	pulledDate?: Date;
	timeFilLimitDay?: string;
	secPolicyHolder?: string;
	assoicatedClaimUuid?: string;
	assoicatedClaimStatus?: string;
	primary?: boolean;
	claimNotes?: Array<ClaimNotesModel>;
	claimRemarks?: string;
	allowEdit?: boolean;
	assignedToName?: string;
	assignedToUuid?: string;
	dateLastUpdatedES?: string;
	statusES?: string;
	estSecondaryES?: string;
	descriptionES?: string;
	assignedToTeam?: number;
	assignedToRoleName?: string;
	assignedToTeamName?: string;
	ivfId?: string;
	tpId?: string;
	ivDos?: string;
	tpDos?: string;
	billedAmount?: number;
	treatingProvider?: String;
	providerOnClaim?: string;
	primePolicyHolderDob?: string;
	secPolicyHolderDob?: string;
	claimType?: string;//this is Specialty not confuse with actual Type (primary/secondary)
	firstTeam?: string;
	firstTeamId?: number;
	primaryEob?: string;
	providerOnClaimFromSheet?: string
	ssn?: string

}


export interface ClaimNotesModel {

	key?: string;
	value?: string;
	id?: number;
}


export interface ClaimEditModel {

	claimUuid?: string;
	claimNoteDtoList?: Array<ClaimNotesModel>;
	claimRemark?: string;
	serCVDto?: Array<ServiceLevelCodeDataModel>;
	submissionDto?: SubmissionDetailModel;
	ruleRemarkDto?: Array<ClaimRuleRemarkModel>;
	submission?: boolean;
	//Ayush
	assignToTL?: boolean;
	assignToOtherTeam?: boolean;
	assignTouuid?: string;
	assignToTeam?: number;
	assignToComment?: string;


}

export interface ServiceLevelCodeDataModel {

	remarkUuid?: string;
	serviceCode?: string;
	name?: string;
	description?: string;
	value?: string;
	messageType?: number;
	remark?: string;
	manualAuto?: string;
	answer?: string;
	message?: string;
	ruleId?: number;
}

export interface ServiceLevelCodeModel {

	claimFound: boolean;
	dto?: Array<ServiceLevelCodeDataModel>;
	esDate?: string;

}

export interface SubmissionDetailModel {

	claimUuid?: string;
	esDate?: Date;
	channel?: string;
	attachmentSend?: boolean;
	preauth?: boolean;
	refferalLetter?: boolean;
	claimNumber?: string;
	preauthNo?: string;
	providerRefNo?: string;
	esTime?: string;
}

export interface ClaimRuleModel {

	ruleId?: number;
	message: string;
	messageType: number;
	ruleName: string;
	ruleDesc: string;
	manualAuto: string;
	remark?: string;
	sectionName: string;
	ruleType: string;
	sNo: number;
}



export interface ClaimRuleRemarkModel {

	remark?: string;
	cd?: Date;
	fName?: string;
	lLName?: string;
	ruleId?: number;
	sectionName?: string;

}

export class ClaimRuleRemarkModelS implements ClaimRuleRemarkModel {
	remark?: string;
	cd?: Date;
	fName?: string;
	lLName?: string;
	ruleId?: number;
	sectionName?: string;

	constructor(remark: string, cd: Date, fName: string, lLName: string, ruleId: number,
		sectionName: string) {
		this.remark = remark;
		this.cd = cd;
		this.fName = fName;
		this.lLName = lLName;
		this.ruleId = ruleId;
		this.sectionName = sectionName;
	}
}
export interface RuleEngineValModel {

	message?: string;
	surface?: string;
	ruleName?: string;
	ivDate?: string;
	tooth?: string;
	mtype?: string;
	officeName?: string;
	patientId?: string;
	claimId?: string;
	insuranceType?: string;
	codes?: string;
	ruleId?: string;
	ivId?: string;
	dos?: string;
	patientName?: string;
	remark?: string;//Used For Saving Remark
}

export interface TLUser {

	uuid?: string;
	active?: number;
	email?: string;
	fullName?: string;
	firstName?: string;
	lastName?: string;
}

export interface TeamsM {

	teamName?: string;
	teamId?: number;
}

export interface OtherTeamRem {

	comment?: string;
	cd?: Date;
	fName?: string;
	lName?: string;
	teamName?: string;
}