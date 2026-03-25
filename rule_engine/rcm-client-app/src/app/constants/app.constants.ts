import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "id": 1, "name": "NA", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client', '/tool-update', '/tool-update/issue-claims', "/reconciliation"], "defaultpath": "/register"
        }],
        [2, {
            "id": 2, "name": "Admin", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client', "/reconciliation", "/unopenedclaim"], "defaultpath": "/register"
        }],
        [3, {
            "id": 3, "name": "Internal Audit Team", "paths": ["/list-of-claims", "/claim-assignment", "/update-pass", "/all-pendency", "/tool-update", "/production", "/search-claims", '/tool-update/issue-claims', "/reconciliation"], "defaultpath": "/claim-assignment"
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
            "id": 7, "name": "Billing Team", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims", '/tool-update/issue-claims', "/reconciliation"], "defaultpath": "/claim-assignment"
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

        [9, {
            "id": 9, "name": "Reporting", "paths": ["/update-pass", "/search-claims", "/reconciliation", "/tool-update", '/tool-update/issue-claims'], "defaultpath": "/tool-update"
        }],

    ]);

    public BILLING_ID = 1;
    public RE_BILLING_ID = 2;
    public NEED_TO_RE_BILL = "Need to re-bill";
    public static INTERNAL_AUDIT_TEAM = 3;
    public static BILLING_TEAM = 7;
    public static CDP_TEAM = 11;
    public CLOSED_CLAIM_STATUS = 30;
    public static AGING_ID = 20;

    public static LC3_TEAM = 4;
    public static OFFICE_TEAM = 5;
    public static PATIENT_CALLING_TEAM = 6;
    public static ORTHO_TEAM = 10;
    public static PAYMENT_POSTING_TEAM = 12;
    public static PPO_IV_TEAM = 13;
    public static MEDICAID_IV_TEAM = 14;
    public static NEED_TO_HOLD_TEAM = 15;
    public static QUALITY_TEAM = 16;
    public static PATIENT_STATEMENT_TEAM = 18;
    public static CREDENTIALING_TEAM = 19;
    public static AGING_TEAM = 20;
    public LIST_CLAIM_REBILL = 1;
    public static commonPath: any = ["/claim-assignment", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims", "/reconciliation", '/tool-update', '/tool-update/issue-claims'];
    // public static commonPath: any = ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims", '/tool-update/issue-claims'];
    public RecreatedSection_ONE = 1;
    public RecreatedSection_REST = 2;

    public static VALID_SERVICE_CODE_LENGTH=5;

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
        { "id": 18, "value": "Sedation Certification (D9245)" },
        { "id": 19, "value": "Sedation Record" },
        { "id": 20, "value": "X-ray: Bitewings" },
        { "id": 21, "value": "X-ray: Cephlo" },
        { "id": 22, "value": "X-ray: FMX" },
        { "id": 23, "value": "X-ray: Pano" },
        { "id": 24, "value": "X-ray: Periapical" },
        { "id": 25, "value": "X-ray: Post-Op" },
        { "id": 26, "value": "X-ray: Pre-Op" },
        { "id": 27, "value": "COB Form" },
        { "id": 28, "value": "Refferal form" },
        { "id": 29, "value": "Emergency Certification" },
        { "id": 30, "value": "Pathalogy Reports" }]


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
        { reasonName: "Benefit Maximum Met" },
        { reasonName: "COB Info Required" },
        { reasonName: "Frequency Limitation" },
        { reasonName: "Member Not Eligible" },
        { reasonName: "Coinsurance Applied" },
        { reasonName: "Deductible Applied" },
        { reasonName: "Alternate Benefit Applied" },
        { reasonName: "Provider Out of Network" },
        { reasonName: "Service Not Covered" },
        { reasonName: "Provider not authorized for service" },
        { reasonName: "Waiting Period Applied" },
        { reasonName: "Medical Necessity Not Met" },
        { reasonName: "Copay Applied" },
        { reasonName: "Services Bundled" },
        { reasonName: "Other" },
        { reasonName: "Pre-auth Denied" },
        { reasonName: "Payment issued to patient" },
        { reasonName: "N/A" },
        { reasonName: "Pre Auth Missing" },
        { reasonName: "Additional information required" },
        { reasonName: "Dependent Not Covered" },
        { reasonName: "Plan Limitation" },
        { reasonName: "Untimely filing" },
        { reasonName: "Need Primary EOB" },
        { reasonName: "Policy Termed" },
        { reasonName: "Member Not Found" },
        { reasonName: "Invalid or Missing Claim Details" }

    ];

    public extClaimInitialDenialReason: any = [
        { reasonName: "Benefit Maximum Met" },
        { reasonName: "COB Info Required" },
        { reasonName: "Frequency Limitation" },
        { reasonName: "Member Not Eligible" },
        { reasonName: "Coinsurance Applied" },
        { reasonName: "Deductible Applied" },
        { reasonName: "Alternate Benefit Applied" },
        { reasonName: "Provider Out of Network" },
        { reasonName: "Service Not Covered" },
        { reasonName: "Provider not authorized for service" },
        { reasonName: "Waiting Period Applied" },
        { reasonName: "Medical Necessity Not Met" },
        { reasonName: "Copay Applied" },
        { reasonName: "Services Bundled" },
        { reasonName: "Other" },
        { reasonName: "Pre-auth Denied" },
        { reasonName: "Payment issued to patient" },
        { reasonName: "N/A" },
        { reasonName: "Pre Auth Missing" },
        { reasonName: "Additional information required" },
        { reasonName: "Dependent Not Covered" },
        { reasonName: "Plan Limitation" },
        { reasonName: "Untimely filing" },
        { reasonName: "Need Primary EOB" },
        { reasonName: "Policy Termed" },
        { reasonName: "Member Not Found" },
        { reasonName: "Invalid or Missing Claim Details" },
        { reasonName: "Claim Not On File "},
        { reasonName: "Missing Xrays" },
        { reasonName: "Incorrect Member ID" },
        { reasonName: "W9 Form Needed" },
        { reasonName: "Dental Office Information Mismatch" },
        { reasonName: "Duplicate Claim" },
        { reasonName: "Patient History Needed"},
        { reasonName: "Primary insurance paid more than secondary's allowed amount" },
        { reasonName: "Incorrect Patient Info" },
        { reasonName: "Narratives Required" },
        { reasonName: "Perio Chart Required" },
        { reasonName: "Age Limitation" },
        { reasonName: "Incorrect NPI/TAX ID/TAXONOMY" },
        { reasonName: "Missing NPI/TAX ID/TAXONOMY" },
        { reasonName: "Need to Send NOA" },
        { reasonName: "DOS Beyond NOA Approved Period" },
        { reasonName: "Need Medical Coverage EOB" },
        { reasonName: "RTD Issued" },
        { reasonName: "Incorrect DOS" },
        { reasonName: "Incorrect Place of Service" },
        { reasonName: "Dental Readiness Class Number missing" },
        { reasonName: "Prep Date/Seat Date required" },
        { reasonName: "Incorrect CDT" },
        { reasonName: "Professional Courtesy Applied" },
        { reasonName: "Crown information required" },
        { reasonName: "Extraction date required" },
        { reasonName: "Referral required" },
        { reasonName: "Incorrect/Zero billed amount" },
        { reasonName: "Missing primary procedure" },
        { reasonName: "Information requested from patient" },
        { reasonName: "Information requested from provider" },
        { reasonName: "Considered under Primary Procedure" },
        { reasonName: "Missing Tooth Clause" },
        { reasonName: "Missing CRA Code" },
        { reasonName: "CIF Required" },
        { reasonName: "Invalid Claim Form" },
        { reasonName: "SRP History Required" },
        { reasonName: "Provider Discount Applied" },
        { reasonName: "Appointment/Treatment Duration Required" },
        { reasonName: "Insurance Premium Not Paid" },
    ]

    public claimStatusRcm: any = [
        { name: "Insurance Claim Closed" },
        { name: "Case Closed" },
        { name: "Voided" },
        { name: "Insurance Claim in Process" },
        { name: "Statement Sent" },
        { name: "Case sent to Collections" },
        { name: "Claim Denied" },
        { name: "Payment Promised" },
        { name: "Appeal in Process" },
        { name: "Appeal Rejected" },
        { name: "Paid in full" },
        { name: "Partial paid" },
        { name: "Claim not on File" },
        { name: "Claim Incorrectly Denied" },
        { name: "Claim on Hold" },
        { name: "Claim Incorrectly Processed" },
        { name: "Processed Under Capitation" },
        { name: "Amount Recouped For Some Other Claim" },
        { name: "Claim Processed in PMS but not billed" },
        { name: "Payment Issued to Patient" },
        { name: "EOB Requested" },
        { name: "Appeal Upheld" },
        { name: "Appeal Upheld" },
        { name: "Remotelite Rejection" },
        { name: "Claim not received by insurance" },
        { name: "Appeal Approved" },
        { name: "Appeal not on file" },
        { name: "Appeal Partially Paid" },
        { name: "Information required from Patient" },
        { name: "Prosthetic code not delivered" },
        { name: "Additional information required" },

    ];




    public Need_to_call_Insurance: string = "Need to Call Insurance";
    public Need_to_bill_Secondary_Insurance: string = "Need to Bill Secondary Insurance";
    public nextActionStatusRcm: any = [
        { name: "Need to Void Partial Claim" },
        { name: "Need to Void Full Claim" },
        { name: "Close the Claim" },
        { name: "Need to Follow up" },
        { name: "Need to Call Insurance for Follow up" },
        { name: "Need to send Appeal" },
        { name: "Need to Follow up on Appeal" },
        { name: "Need to Review" },
        { name: "Need to Adjust post Approval" },
        { name: "Need to Post" },
        { name: "Need to Send Statement" },
        { name: "Need to Send Text to Patient for Payment" },
        { name: "No Action Needed" },
        { name: "Need to send to Collections" },
        { name: "Need to Call Insurance for EOB" },
        { name: "Need to Check Payment Status" },
        { name: "Need to Check Claim Status on Web" },
        { name: "Need to Bill Secondary Insurance" },
        { name: "Need to follow up for Void Request" },
        { name: "Need to get Provider Credentialed" },
        { name: "Need to call Insurance for reprocessing" },
        { name: "Need to Bill" },
        { name: "Need to followup on void" },
        { name: "Need to Bill" },
        { name: "Need to follow up on void" },
        { name: "Need to call patient for Information" },
        { name: "Need to Fix the Walkout" },
        { name: "Awaiting prosthetic code delivery" },
        { name: "Need to Bill to Patient" },
        { name: "Need to Adjust Off" },

    ]

    public extNextActionStatusRcm: any = [
        { name: "Need to Void Partial Claim" },
        { name: "Need to Void Full Claim" },
        { name: "Close the Claim" },
        { name: "Need to Follow up" },
        { name: "Need to Call Insurance for Follow up" },
        { name: "Need to send Appeal" },
        { name: "Need to Follow up on Appeal" },
        { name: "Need to Review" },
        { name: "Need to Adjust post Approval" },
        { name: "Need to Post" },
        { name: "Need to Send Statement" },
        { name: "Need to Send Text to Patient for Payment" },
        { name: "No Action Needed" },
        { name: "Need to send to Collections" },
        { name: "Need to Call Insurance for EOB" },
        { name: "Need to Check Payment Status" },
        { name: "Need to Check Claim Status on Web" },
        { name: "Need to Bill Secondary Insurance" },
        { name: "Need to follow up for Void Request" },
        { name: "Need to get Provider Credentialed" },
        { name: "Need to call Insurance for reprocessing" },
        { name: "Need to Bill" },
        { name: "Need to followup on void" },
        { name: "Need to Bill" },
        { name: "Need to follow up on void" },
        { name: "Need to call patient for Information" },
        { name: "Need to Fix the Walkout" },
        { name: "Awaiting prosthetic code delivery" },
        { name: "Need to Bill to Patient" },
        { name: "Need to Adjust Off" },
        { name: "Transferred to Posting Team" },
        { name: "Need to Rebill" },
        { name: "Need to send 2nd Level Appeal" },
        { name: "2nd Level Appeal Sent" },
        { name: "Appeal/X-Ray/Narrative/Perio Sent (Fax)" },
        { name: "Appeal/X-Ray/Narrative/Perio Sent (Web)" },
        { name: "Appeal/X-Ray/Narrative/Perio Sent (Paper)" },
        { name: "Installment Received & Need to Followup" },
        { name: "Info required from Office" },
        { name: "Claim Closed" },
        { name: "Adjusted Off" },
        { name: "Posted" },
        { name: "Billed to Patient" },
        { name: "Posted and Recreated" },
        { name: "Review Today (Please see Billing Remarks)" },
        { name: "Rebilled (Paper)" },
        { name: "Rebilled (Fax)" },
        { name: "Rebilled (Web)" },
        { name: "Rebilled (PMS)" },
        { name: "Need to send CIF" },
        { name: "Transferred to Aging Team" },
        { name: "Review Today (Please see Posting Remarks)" },
        { name: "Billed to Secondary" },
        { name: "EOB Requested" },
        { name: "Need adjustment approval" },
        { name: "EOB Required from Office" },
        { name: "Re-Processed while on call" },
        { name: "Need Second Level Review" },
        { name: "CIF Sent to Office" },
        { name: "Posting Confirmation/Approval Required from Office" },
        { name: "Claim Voided" },
        { name: "Transferred to Credentialing Team" },
        { name: "NOA Sent to Office" },
        { name: "On Hold" },
        { name: "Rebilled (Email)" },
        { name: "Appeal Sent" },
        { name: "NOA Sent to Insurance" },
        { name: "Need to Raise Void Request" },
        { name: "CIF Mailed to Dentical" },
        { name: "Sent to Patient Collections" },
    ]

    public claimClosedId: number = 30;

    public statusESUpdated: any = {
        "C": "Closed",
        "E": "Unbilled",
        "P": "Unbilled",
        "O": "Open",
        "U": "Primary Open - Secondary Unsubmitted",
        "Closed": "Closed",
        "Unbilled": "Unbilled",
        "Open": "Open",
        "Billed": "Billed"

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

    public requestRebillingRequirement: any = [
        {
            name: 'Pre-Auth',
            checked: false
        },
        {
            name: 'Clinical / Insurance Documents',
            checked: false
        },
        {
            name: 'Perio Chart',
            checked: false
        },
        {
            name: 'Bitewings',
            checked: false
        },
        {
            name: 'PAs',
            checked: false
        },
        {
            name: 'FMX',
            checked: false
        },
        {
            name: 'PANO',
            checked: false
        },
        {
            name: 'Pre-Op X-ray',
            checked: false
        },
        {
            name: 'Post-Op X-ray',
            checked: false
        },
        {
            name: 'Intra Oral Photos',
            checked: false
        },
        {
            name: 'Cephlo',
            checked: false
        },
        {
            name: 'CRA Code',
            checked: false
        },
        {
            name: 'CRA Form',
            checked: false
        },
        {
            name: 'Lab Slip',
            checked: false
        },
        {
            name: 'Sedation Record',
            checked: false
        },
        {
            name: 'Other',
            checked: false
        },
    ]


    public btpReason: any = [
        { reasonName: "Benefit Maximum Met" },
        { reasonName: "Frequency Limitation" },
        { reasonName: "Member Not Eligible" },
        { reasonName: "Coinsurance Applied" },
        { reasonName: "Deductible Applied" },
        { reasonName: "Alternate Benefit Applied" },
        { reasonName: "Provider Out of Network" },
        { reasonName: "Service Not Covered" },
        { reasonName: "Provider not authorized for service" },
        { reasonName: "Waiting Period Applied" },
        { reasonName: "Medical Necessity Not Met" },
        { reasonName: "Copay Applied" },
        { reasonName: "Services Bundled" },
        { reasonName: "Other" },
        { reasonName: "Pre-auth Denied" },
        { reasonName: "Payment issued to patient" },
        { reasonName: "Age Limitation" },
        {reasonName: "Dependent Not Covered" },
    ];

    public adjustmentReason: any = [
        { reasonName: "Benefit Maximum Met" },
        { reasonName: "Frequency Limitation" },
        { reasonName: "Member Not Eligible - Medicaid" },
        { reasonName: "Coinsurance Applied" },
        { reasonName: "Deductible Applied" },
        { reasonName: "Alternate Benefit Applied" },
        { reasonName: "Provider Out of Network" },
        { reasonName: "Service Not Covered" },
        { reasonName: "Provider not authorized for service" },
        { reasonName: "Waiting Period" },
        { reasonName: "Medical Necessity Not Met" },
        { reasonName: "Copay" },
        { reasonName: "Service Bundled" },
        { reasonName: "Processed under Capitaion" },
        { reasonName: "Untimely Filing" },
        { reasonName: "Pre-auth Denied" },
        { reasonName: "Patient Not Assigned To The Facility" },
        { reasonName: "Incorrect FS" },
        { reasonName: "Incorrect Fee" },
        { reasonName: "Other" },
        { reasonName: "Interest Receivced" },
        { reasonName: "Pre-auth Missing" },
        { reasonName: "Exam & Xray Denial" },
        { reasonName: "INSADJ - 25" },
        { reasonName: "INSADJ - 26" },

    ];

    public desposition: any = [
        { reasonName: "Voice Mail Left" },
        { reasonName: "Call not Connected" },
        { reasonName: "Payment Promised" },
        { reasonName: "Full Payment Made" },
        { reasonName: "Wrong No." },
        { reasonName: "Not Ready to Pay" },
        { reasonName: "Statement Requested" },
        { reasonName: "Call Back Requested" },
        { reasonName: "Statement Emailed" },
        { reasonName: "Patient is Bankrupt" },
        { reasonName: "Partial Payment Made" },
        { reasonName: "Deceased/Passed Away" },
        { reasonName: "Patient Disputed Balance" },
        { reasonName: "Patient speaks other Language " },
        { reasonName: "Others" },
    ];

    public reasonForRebilling: any = [
        { reasonName: "COB Info Required" },
        { reasonName: "Additional Info Needed" },
        { reasonName: "Claim not on file" },
        { reasonName: "Need Primary EOB" },
        { reasonName: "Incorrect Billing" },
        { reasonName: "Incorrect patient/Insurance" },

    ];

    public actionToPerformButtons: any = {
        'assignToOtherTeam': 'Assign to other team',
        'assignToSameTeam': 'Assign to Same Team',
        'assignToTeamLead': 'Assign to Team Lead',
        'archive': 'Archive',
    }

    public patientStatementStatus: any = [
        { status: "Statement Not Required" },
        { status: "Pending with Posting Team" },
        { status: "Pending with LC3 Team" },
        { status: "Pending with CDP team" },
        { status: "Amount Adjusted" },
        { status: "Pending with Office" },
        { status: "Pending with Financing Option" },
        { status: "Statement Created" },
        { status: "Ortho Patient" },
        { status: "Pending with Offshore" },
    ];

    public paymentFrequency: any = [
        { frequency: "Monthly" },
        { frequency: "Quaterly" },
        { frequency: "Half - Yearly" },
        { frequency: "Yearly" },
    ];

    public serviceCodeActionTaken: any = [
        { action: "Rebilled" },
        { action: "Appealed" },
        { action: "Reprocessed After Denial" },
        { action: "Processed - EOB Not Received" }
    ];

    public serviceCodeAdditionalInfoProvided: any = [
        { addtional: "Perio Chart" },
        { addtional: "Bitewings" },
        { addtional: "PAs" },
        { addtional: "FMX" },
        { addtional: "PANO" },
        { addtional: "Pre-Op X-ray" },
        { addtional: "Post-Op X-ray" },
        { addtional: "Intra Oral Photos" },
        { addtional: "Cephlo" },
        { addtional: "CRA Code" },
        { addtional: "CRA Form" },
        { addtional: "Lab Slip" },
        { addtional: "Sedation Record" },
        { addtional: "Other" }
    ];

    public eOBInsuranceLetter: any = [
        { dType: "EOB" },
        { dType: "Void Letter" },
        { dType: "Appeal Determination Letter" },
        { dType: "Recoupment Letter" },
        { dType: "Other" }
    ];
}
