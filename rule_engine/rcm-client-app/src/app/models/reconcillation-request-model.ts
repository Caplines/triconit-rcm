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
    claimsNotFoundRCM?: string[];
    claimInUploadErrors?: any[];
    discrepancies?: any[];
    discrepanciesAll?: any[];
}