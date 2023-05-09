
export interface ClaimAssociateDetailModel {

    uuid: string;
    officeName: string;
    claimId: string;
    dos: Date;
    patientName: string;
    patientId: string;
    statusType: number;
    primaryInsurance: string;
    secondaryInsurance: string;
    prName: string;
    secName: string;
    lastTeam: string;
    claimAge: number;
    timelyFilingLimitData: string;
    billedAmount: number;
    primTotal: number;
    secTotal: number;
    primeSecSubmittedTotal: number;
}
