export class ReconcilltationRequestModel {
    officeUuid?: string;
    startDate?: Date;
    endDate?: Date;
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