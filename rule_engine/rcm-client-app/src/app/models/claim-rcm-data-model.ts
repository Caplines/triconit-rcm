
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
	linkedClaims?: Array<linkedClaim>;
	providerId?: string;
	pulledDate?: Date;
	timeFilLimitDay?: string;
	secPolicyHolder?: string;
	assoicatedClaimUuid?: string;
	assoicatedClaimStatus?: string;
	assoicatedClaimCurrentStatus?: number;
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
	ssn?: string;
	assignmentOfBenefits?: string;
	currentState?: number;
	preferredModeOfSubmission?: string;
	claimCmpMatchesLoggedCmp?: boolean;
	ruleEngineRunRemark?: string;
	currentStatus?: number;
	nextAction?: number;
	insuranceContactNo?: string;
	patientContactNo?: string;

	currentStatusName?: String;
	nextActionName?: String;
	rebilledStatus?: boolean;
	billingUserUuid?: String;
	btp?: number;
	adjustment?: number;

	paymentReceived?: number;

	firstPostingDate?: Date;

	paidAmount?: number;

	firstRebilledDate?: Date;

	balanceFromEsBeforePosting?: number;

	balanceFromEsAfterPosting?: number;

	reconciliationPass?: boolean;
	amountCollectedClaims?: number;
	statusESUpdated?: string;
	nextFollowUpDate?: String;

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
	ruleEngineRunRemark?: string
	serCVDto?: Array<ServiceLevelCodeDataModel>;
	submissionDto?: SubmissionDetailModel;
	ruleRemarkDto?: Array<ClaimRuleRemarkModel>;
	submission?: boolean;
	assignToTL?: boolean;
	assignToOtherTeam?: boolean;
	assignTouuid?: string;
	assignToTeam?: number;
	assignToComment?: string;
	byPassPendingCheck?: boolean;
	actionName?: string;

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
	details?: Array<ClaimDetailModel>;
	esDate?: string;

}

export interface SubmissionDetailModel {

	claimUuid?: string;
	esDate?: any;
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
	runDate?: Date
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
	canWorkBeforeSubmssion?: boolean;
}

export interface OtherTeamRem {

	comment?: string;
	cd?: Date;
	fName?: string;
	lName?: string;
	teamName?: string;
	attchmentsWithRemarks?: number
}

export interface ClaimDetailModel {

	serviceCode?: string;
	surface?: string;
	tooth?: string;
}

export interface SectonRightDataModel {
	editAccess?: boolean;
	sectionCategory?: string;
	sectionId?: number;
	sectionName?: string;
	viewAccess?: boolean;

}
export interface ClaimSettingDataModel {
	clientName?: string;
	clientUuid?: string;
	userUuid?: string;
	teamsWithSections?: Array<TeamRight>;

}

export interface TeamRight {
	teamId?: number;
	teamName?: string;
	userUuid?: string;
	sectionData?: Array<SectonRightDataModel>;

}

export interface ClaimStep {
	dt?: Date;
	name?: string;
	status?: string;
}

export interface linkedClaim {
	claimId?: string;
	claimUuid?: string;
}
