import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "id": 1, "name": "NA", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client', '/tool-update', '/tool-update/issue-claims'], "defaultpath": "/register"
        }],
        [2, {
            "id": 2, "name": "Admin", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client'], "defaultpath": "/register"
        }],
        [3, {
            "id": 3, "name": "Internal Audit Team", "paths": ["/list-of-claims", "/claim-assignment", "/update-pass", "/all-pendency", "/tool-update", "/production", "/search-claims", '/tool-update/issue-claims'], "defaultpath": "/claim-assignment"
        }],
        [4, {
            "id": 4, "name": "LC3", "paths": AppConstants.commonPath, "defaultpath": "/update-pass"
        }],
        [5, {
            "id": 5, "name": "Office", "paths": AppConstants.commonPath, "defaultpath": "/update-pass"
        }],
        [6, {
            "id": 6, "name": "Patient Calling", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [7, {
            "id": 7, "name": "Billing Team", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims", '/tool-update/issue-claims'], "defaultpath": "/claim-assignment"
        }],
        [8, {
            "id": 8, "name": "Super Admin", "paths": AppConstants.commonPath, "defaultpath": "/claim-assignment"
        }],
        // [9, {
        //     "name": "Reporting", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass","/list-of-claims"], "defaultpath": "/claim-assignment"
        // }],
        [10, {
            "id": 10, "name": "Ortho", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [11, {
            "id": 11, "name": "CDP", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [12, {
            "id": 12, "name": "Payment Posting", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [13, {
            "id": 13, "name": "PPO IV", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [14, {
            "id": 14, "name": "Medicaid IV", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [15, {
            "id": 15, "name": "Need to hold", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [16, {
            "id": 16, "name": "Quality", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [17, {
            "id": 17, "name": "AR", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [18, {
            "id": 18, "name": "Patient Statement", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [19, {
            "id": 19, "name": "Credentialing", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [20, {
            "id": 20, "name": "Aging", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],

        // [9,{
        //     "name":"Reporting","paths":["/update-pass"],"defaultpath":"/update-pass"
        // }],

    ]);

    public BILLING_ID = 1;
    public RE_BILLING_ID = 2;
    public static INTERNAL_AUDIT_TEAM = 3;
    public static BILLING_TEAM = 7;
    public static CDP_TEAM = 11;
    public CLOSED_CLAIM_STATUS = 30;
    public static commonPath: any = ["/claim-assignment", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims"];
    // public static commonPath: any = ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims", '/tool-update/issue-claims'];


    public TEAMS_ID_CONFIG = new Map<number, string>([
        [2, "ADMIN"],
        [3, "INTERNAL_AUDIT"],
        [4, "LC3"],
        [5, "OFFICE"],
        [6, "PATIENT_CALLING"],
        [7, "BILLING"],
        // [9,"REPORTING"],

    ]);

    public teamData: any = [
        { "unFormatedName": 'INTERNAL_AUDIT', "count": 0, "teamName": "Internal Audit", "teamId": 3 },
        { "unFormatedName": 'LC3', "count": 0, "teamName": "LC3", "teamId": 4 },
        { "unFormatedName": 'OFFICE', "count": 0, "teamName": "Office", "teamId": 5 },
        { "unFormatedName": 'PATIENT_CALLING', "count": 0, "teamName": "Patient Calling", "teamId": 6 },
        { "unFormatedName": 'BILLING', "count": 0, "teamName": "Billing", "teamId": 7 },
        { "unFormatedName": 'ORTHO', "count": 0, "teamName": "Ortho", "teamId": 10 },
        { "unFormatedName": 'CDP', "count": 0, "teamName": "CDP", "teamId": 11 },
        { "unFormatedName": 'PAYMENT_POSTING', "count": 0, "teamName": "Payment Posting", "teamId": 12 },
        { "unFormatedName": 'PPO_IV', "count": 0, "teamName": "PPO IV", "teamId": 13 },
        { "unFormatedName": 'MEDICAID_IV', "count": 0, "teamName": "Medicaid IV", "teamId": 14 },
        { "unFormatedName": 'NEED_TO_HOLD', "count": 0, "teamName": "Need to hold", "teamId": 15 },
        { "unFormatedName": 'QUALITY', "count": 0, "teamName": "Quality", "teamId": 16 },
        //{ "unFormatedName": 'AR', "count": 0, "teamName": "AR", "teamId": 17 },
        { "unFormatedName": 'PATIENT_STATEMENT', "count": 0, "teamName": "Patient Statement", "teamId": 18 },
        { "unFormatedName": 'CREDENTIALING', "count": 0, "teamName": "Credentialing", "teamId": 19 },
        { "unFormatedName": 'AGING', "count": 0, "teamName": "Aging", "teamId": 20 },
    ];

    public attachmentType =
        [{ "id": 1, "value": "Consent Form" },
        { "id": 2, "value": "Consent Form for Major Service" },
        { "id": 3, "value": "Consult Form" },
        { "id": 4, "value": "CRA Form" },
        { "id": 5, "value": "EOB" },
        { "id": 6, "value": "FDH Certification" },
        { "id": 7, "value": "Insurance ID Card" },
        { "id": 8, "value": "Intraoral Photos" },
        { "id": 9, "value": "IV" },
        { "id": 10, "value": "Lab Slip" },
        { "id": 11, "value": "Narratives" },
        { "id": 12, "value": "NCS Form" },
        { "id": 13, "value": "Nitrous Certification (D9230)" },
        { "id": 14, "value": "Nitrous Pre operative Checklist" },
        { "id": 15, "value": "Others" },
        { "id": 16, "value": "Perio Chart" },
        { "id": 17, "value": "Pre-Auth" },
        { "id": 18, "value": "Sedation Certification (D9248)" },
        { "id": 19, "value": "Sedation Record" },
        { "id": 20, "value": "X-ray: Bitewings" },
        { "id": 21, "value": "X-ray: Cephlo" },
        { "id": 22, "value": "X-ray: FMX" },
        { "id": 23, "value": "X-ray: Pano" },
        { "id": 24, "value": "X-ray: Periapical" },
        { "id": 25, "value": "X-ray: Post-Op" },
        { "id": 26, "value": "X-ray: Pre-Op" }]


    public ARCHIVE_PREFIX: string = "arc_";

    public claimStatus: any =
        [
            {
                'name': 'Unbilled',
                'checked': false,
            },
            /*{
                'name': 'Open',
                'checked': false,
            },
            {
                'name': 'Closed',
                'checked': false,
            },
            {
                'name': 'Primary Closed - Secondary Unbilled',
                'checked': false,
            },
            {
                'name': 'Primary Closed - Secondary Open',
                'checked': false,
            },*/
            {
                'name': 'Billed',
                'checked': false,
            },

        ];

    public ageCategory: any = [
        {
            'name': '0-30',
            checked: false,
            value: 1
        },
        {
            'name': '31-60',
            checked: false,
            value: 2
        },
        {
            'name': '61-90',
            checked: false,
            value: 3
        },
        {
            'name': '90+',
            checked: false,
            value: 4
        },
    ]

    //attachwithremarks
    public static ATTACH_WITH_REMARKS: string = "AttachWithRemarks";

    public static inputRequiredForTeams: any = { "Aging": false, "CDP": false, "Payment Posting": false, "Patient Statement": false }

    public claimInitialDenialReason: any = [
        { reasonName: "Service not covered" },
        { reasonName: "Benefit Maximum Met" },
        { reasonName: "Benefits assigned to member" },
        { reasonName: "COB Info Required" },
        { reasonName: "Freqency Limitation & Coinsurance" },
        { reasonName: "Frequency Limit & Deductibles" },
        { reasonName: "Frequency Limitation" },
        { reasonName: "Member Not Eligible" },
        { reasonName: "Coinsurance applied" },
        { reasonName: "Deductible applied" },
        { reasonName: "Deductibles & Coinsurance" },
        { reasonName: "Alternate Benefit Applied" },
        { reasonName: "Provider Out of Network" },
        { reasonName: "Service Not Covered" },
        { reasonName: "Provider not authorized for service" },
        { reasonName: "Service Not Covered & Coinsurance" },
        { reasonName: "Waiting Period" },
        { reasonName: "Medical Necessity" },
        { reasonName: "Accounts payment received" },
        { reasonName: "Copay" },
        { reasonName: "Coins+ Deductible+Freq Limit" },
        { reasonName: "Coins+Deductible +Est 0" },
        { reasonName: "Provider out of network+Ded+Coins" },
        { reasonName: "Alt Benefit+Deductible+Coins" },
        { reasonName: "Frequency limitation" },
        { reasonName: "In Acc To Primary Eob" },
        { reasonName: "Coins+Alt Benefit" },
        { reasonName: "Estimated amount is $0+Deductible" },
        { reasonName: "Coins+Deductible+Alt Benefit" },
        { reasonName: "Provider Out Of Network+Coins" },
        { reasonName: "Benefit Maxed Out" },
        { reasonName: "Bundled Service Applied" },
        { reasonName: "Appeal Upheld" },
        { reasonName: "N/A" },
        { reasonName: "Processed under Capitaion" },
        { reasonName: "Medical Records (CRA) Missed" },
        { reasonName: "Additional Info Needed" },
        { reasonName: "Incorrect Billing" },
        { reasonName: "Incorrect denial" },
        { reasonName: "Incorrect Provider Info" },
        { reasonName: "Untimely Filing" },
        { reasonName: "Claim not on file" },
        { reasonName: "Pre-auth unavailable" },
        { reasonName: "Crown Not Paid" },
        { reasonName: "Crown Not Walked Out" },
        { reasonName: "Pre-auth Denied" },
        { reasonName: "Duplicate Claim" },
        { reasonName: "Patient Not Assigned To The Facility" },
        { reasonName: "Provider is not PCD" },
        { reasonName: "Incorrect Walkout" },

    ];

    public claimStatusRcm: any = [
        { name: "Pending For Review" },
        { name: "Pending For Billing" },
        { name: "Billed" },
        { name: "In Process" },
        { name: "Closed" },
        { name: "Voided" },
        { name: "Re-billing" },
        { name: "Reviewed" },
        { name: "Posted" },
        { name: "Submitted" }
    ];


    public Need_to_call_Insurance: string = "Need to Call Insurance";
    public nextActionStatusRcm: any = [
        { name: "Review" },
        { name: "Billing" },
        { name: "Need to void" },
        { name: "Close the Claim" },
        { name: "EOB Required" },
        { name: this.Need_to_call_Insurance },
        { name: "Need to send Appeal" },
        { name: "Need to Follow up on Appeal" },
        { name: "Need to Review" },
        { name: "Adjustment Approval Needed" },
        { name: "Need to Post" },
        { name: "Send Statement" },
        { name: "Send Text" },
        { name: "No Action Needed" },
        { name: "Need to send to IC system" },
        { name: "Need to Void" },
        { name: "Need to Post" },
        { name: "Pending For Billing" },
        { name: "Need to Audit" }

    ]

    public claimClosedId: number = 30;

    public statusESUpdated: any = {
        C: "Closed",
        E: "Unbilled (E)",
        P: "Unbilled (P)",
        O: "Open",
        U: "Primary Open - Secondary Unsubmitted",
      };

    public helpLinks: any = {
        "/tool-update": "/tool-update",
        "/claim-assignment": "/claim-assignment",
        "/all-pendency": "/all-pendency",
        "/list-of-claims": "/list-of-claims",
        "/production": "/production",
        "/search-claims": "/search-claims",
        "/register": "/register",
        "/user-setting": "/user-setting",
        "/manage-office": "/manage-office",
        "/users-status": "/users-status",
        "/manage-client": "/manage-client",
        "/manage-section": "/manage-section",
        "/reconciliation": "/reconciliation"
    };
    
      public requestRebillingRequirement:any=[
        {
            name:'Perio Chart',
            checked:false
        },
        {
            name:'Bitewings',
            checked:false
        },
        {
            name:'PAs',
            checked:false
        },
        {
            name:'FMX',
            checked:false
        },
        {
            name:'PANO',
            checked:false
        },
        {
            name:'Pre-Op X-ray',
            checked:false
        },
        {
            name:'Post-Op X-ray',
            checked:false
        },
        {
            name:'Intra Oral Photos',
            checked:false
        },
        {
            name:'Cephlo',
            checked:false
        },
        {
            name:'CRA Code',
            checked:false
        },
        {
            name:'CRA Form',
            checked:false
        },
        {
            name:'Lab Slip',
            checked:false
        },
        {
            name:'Sedation Record',
            checked:false
        },
      ]


      public btpReason: any = [
        { reasonName: "Service not covered" },
        { reasonName: "Benefit Maximum Met" },
        { reasonName: "Benefits assigned to member" },
        { reasonName: "COB Info Required" },
        { reasonName: "Freqency Limitation & Coinsurance" },
        { reasonName: "Frequency Limit & Deductibles" },
        { reasonName: "Frequency Limitation" },
        { reasonName: "Member Not Eligible" },
        { reasonName: "Coinsurance applied" },
        { reasonName: "Deductible applied" },
        { reasonName: "Deductibles & Coinsurance" },
        { reasonName: "Alternate Benefit Applied" },
        { reasonName: "Provider Out of Network" },
        { reasonName: "Provider not authorized for service" },
        { reasonName: "Service Not Covered & Coinsurance" },
        { reasonName: "Waiting Period" },
        { reasonName: "Medical Necessity" },
        { reasonName: "Accounts payment received" },
        { reasonName: "Copay" },
        { reasonName: "Coins+ Deductible+Freq Limit" },
        { reasonName: "Coins+Deductible +Est 0" },
        { reasonName: "Provider out of network+Ded+Coins" },
        { reasonName: "Alt Benefit+Deductible+Coins" },
        { reasonName: "Frequency limitation" },
        { reasonName: "In Acc To Primary Eob" },
        { reasonName: "Coins+Alt Benefit" },
        { reasonName: "Estimated amount is $0+Deductible" },
        { reasonName: "Coins+Deductible+Alt Benefit" },
        { reasonName: "Provider Out Of Network+Coins" },
        { reasonName: "Benefit Maxed Out" },
        { reasonName: "Bundled Service Applied" },
        { reasonName: "Appeal Upheld" },
        { reasonName: "N/A" },
        { reasonName: "Processed under Capitaion" },
        { reasonName: "Medical Records (CRA) Missed" },
        { reasonName: "Additional Info Needed" },
        { reasonName: "Incorrect Billing" },
        { reasonName: "Incorrect denial" },
        { reasonName: "Incorrect Provider Info" },
        { reasonName: "Untimely Filing" },
        { reasonName: "Claim not on file" },
        { reasonName: "Pre-auth unavailable" },
        { reasonName: "Crown Not Paid" },
        { reasonName: "Crown Not Walked Out" },
        { reasonName: "Pre-auth Denied" },
        { reasonName: "Duplicate Claim" },
        { reasonName: "Patient Not Assigned To The Facility" },
        { reasonName: "Provider is not PCD" },
        { reasonName: "Incorrect Walkout" },
    ];

    public adjustmentReason: any = [
        { reasonName: "Service not covered" },
        { reasonName: "Benefit Maximum Met" },
        { reasonName: "Benefits assigned to member" },
        { reasonName: "COB Info Required" },
        { reasonName: "Freqency Limitation & Coinsurance" },
        { reasonName: "Frequency Limit & Deductibles" },
        { reasonName: "Frequency Limitation" },
        { reasonName: "Member Not Eligible" },
        { reasonName: "Coinsurance applied" },
        { reasonName: "Deductible applied" },
        { reasonName: "Deductibles & Coinsurance" },
        { reasonName: "Alternate Benefit Applied" },
        { reasonName: "Provider Out of Network" },
        { reasonName: "Provider not authorized for service" },
        { reasonName: "Service Not Covered & Coinsurance" },
        { reasonName: "Waiting Period" },
        { reasonName: "Medical Necessity" },
        { reasonName: "Accounts payment received" },
        { reasonName: "Copay" },
        { reasonName: "Coins+ Deductible+Freq Limit" },
        { reasonName: "Coins+Deductible +Est 0" },
        { reasonName: "Provider out of network+Ded+Coins" },
        { reasonName: "Alt Benefit+Deductible+Coins" },
        { reasonName: "Frequency limitation" },
        { reasonName: "In Acc To Primary Eob" },
        { reasonName: "Coins+Alt Benefit" },
        { reasonName: "Estimated amount is $0+Deductible" },
        { reasonName: "Coins+Deductible+Alt Benefit" },
        { reasonName: "Provider Out Of Network+Coins" },
        { reasonName: "Benefit Maxed Out" },
        { reasonName: "Bundled Service Applied" },
        { reasonName: "Appeal Upheld" },
        { reasonName: "N/A" },
        { reasonName: "Processed under Capitaion" },
        { reasonName: "Medical Records (CRA) Missed" },
        { reasonName: "Additional Info Needed" },
        { reasonName: "Incorrect Billing" },
        { reasonName: "Incorrect denial" },
        { reasonName: "Incorrect Provider Info" },
        { reasonName: "Untimely Filing" },
        { reasonName: "Claim not on file" },
        { reasonName: "Pre-auth unavailable" },
        { reasonName: "Crown Not Paid" },
        { reasonName: "Crown Not Walked Out" },
        { reasonName: "Pre-auth Denied" },
        { reasonName: "Duplicate Claim" },
        { reasonName: "Patient Not Assigned To The Facility" },
        { reasonName: "Need Primary EOB" },
        { reasonName: "Incorrect Walkout" },
        { reasonName: "Incorrect Fee Schedule" },

    ];
}
