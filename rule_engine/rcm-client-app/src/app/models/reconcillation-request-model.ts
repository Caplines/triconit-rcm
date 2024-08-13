export class ReconcilltationRequestModel {
    officeUuid?: string;
    startDate?: any;
    endDate?: any;
}

export class ReconcilltationResponseModel {
    title?: string;
    office?: string;
    claimsES?: number;
    claimsRCM?: number;
    claimsNotFoundRCM?: any[];
    claimInUploadErrors?: any[];
    claimsNotFoundPMS?: any[];
    claimArchived?: any[];
}