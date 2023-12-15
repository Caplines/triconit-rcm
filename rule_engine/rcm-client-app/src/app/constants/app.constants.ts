import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "NA", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client', '/tool-update', '/tool-update/issue-claims'], "defaultpath": "/register"
        }],
        [2, {
            "name": "Admin", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client'], "defaultpath": "/register"
        }],
        [3, {
            "name": "Internal Audit Team", "paths": ["/list-of-claims", "/claim-assignment", "/update-pass", "/all-pendency", "/tool-update", "/production", "/search-claims", '/tool-update/issue-claims'], "defaultpath": "/claim-assignment"
        }],
        [4, {
            "name": "LC3", "paths": AppConstants.commonPath, "defaultpath": "/update-pass"
        }],
        [5, {
            "name": "Office", "paths": AppConstants.commonPath, "defaultpath": "/update-pass"
        }],
        [6, {
            "name": "Patient Calling", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [7, {
            "name": "Billing Team", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims", '/tool-update/issue-claims'], "defaultpath": "/claim-assignment"
        }],
        [8, {
            "name": "Super Admin", "paths": AppConstants.commonPath, "defaultpath": "/claim-assignment"
        }],
        // [9, {
        //     "name": "Reporting", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass","/list-of-claims"], "defaultpath": "/claim-assignment"
        // }],
        [10, {
            "name": "Ortho", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [11, {
            "name": "CDP", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [12, {
            "name": "Payment Posting", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [13, {
            "name": "PPO IV", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [14, {
            "name": "Medicaid IV", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [15, {
            "name": "Need to hold", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [16, {
            "name": "Quality", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [17, {
            "name": "AR", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [18, {
            "name": "Patient Statement", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [19, {
            "name": "Credentialing", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],
        [20, {
            "name": "Aging", "paths": AppConstants.commonPath, "defaultpath": "/list-of-claims"
        }],

        // [9,{
        //     "name":"Reporting","paths":["/update-pass"],"defaultpath":"/update-pass"
        // }],

    ]);

    public BILLING_ID = 1;
    public RE_BILLING_ID = 2;
    public INTERNAL_AUDIT_TEAM = 3;
    public BILLING_TEAM = 7;
    public static commonPath: any = ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass", "/search-claims", '/tool-update/issue-claims'];


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
        { "unFormatedName": 'AR', "count": 0, "teamName": "AR", "teamId": 17 },
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
    public static ATTACH_WITH_REMARKS:string="AttachWithRemarks";
}
