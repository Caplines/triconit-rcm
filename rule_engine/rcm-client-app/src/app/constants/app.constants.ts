import { Injectable } from '@angular/core';
import { TeamModel } from '../models/team.model';


@Injectable({
    providedIn: 'root'
})
export class AppConstants {

    //Make id using db table - RCM_TEAM

    public TEAMS_CONFIG = new Map<Number, TeamModel>([
        [1, {
            "name": "NA", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client', '/tool-update'], "defaultpath": "/register"
        }],
        [2, {
            "name": "Admin", "paths": ["/register", '/user-setting', '/manage-office', '/users-status', '/manage-client'], "defaultpath": "/register"
        }],
        [3, {
            "name": "Internal Audit Team", "paths": ["/list-of-claims", "/claim-assignment", "/update-pass", "/all-pendency", "/tool-update","/production"], "defaultpath": "/claim-assignment"
        }],
        [4, {
            "name": "Lc3", "paths": ["/update-pass"], "defaultpath": "/update-pass"
        }],
        [5, {
            "name": "Office", "paths": ["/update-pass"], "defaultpath": "/update-pass"
        }],
        [6, {
            "name": "Patient Calling", "paths": ["/update-pass"], "defaultpath": "/update-pass"
        }],
        [7, {
            "name": "Billing Team", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass"], "defaultpath": "/claim-assignment"
        }],
        // [8, {
        //     "name": "Super Admin", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass","/other-teams-work"], "defaultpath": "/claim-assignment"
        // }],
        // [9, {
        //     "name": "Reporting", "paths": ["/claim-assignment", "/tool-update", "/list-of-claims", "/fetch-claims", "/production", "/all-pendency", "/update-pass","/other-teams-work"], "defaultpath": "/claim-assignment"
        // }],
        // [10, {
        //     "name": "Ortho", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [11, {
        //     "name": "CDP", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [12, {
        //     "name": "Payment Posting", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [13, {
        //     "name": "PPO IV", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [14, {
        //     "name": "Medicaid IV", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [15, {
        //     "name": "Need to hold", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [16, {
        //     "name": "Quality", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [17, {
        //     "name": "AR", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [18, {
        //     "name": "Patient Statement", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],
        // [19, {
        //     "name": "Credentialing", "paths": ["/other-teams-work"], "defaultpath": "/update-pass"
        // }],

        // [9,{
        //     "name":"Reporting","paths":["/update-pass"],"defaultpath":"/update-pass"
        // }],

    ]);

    public BILLING_ID = 1;
    public RE_BILLING_ID = 2;
    public INTERNAL_AUDIT_TEAM = 3;
    public BILLING_TEAM = 7;


    public TEAMS_ID_CONFIG = new Map<number, string>([
        [2, "ADMIN"],
        [3, "INTERNAL_AUDIT"],
        [4, "LC3"],
        [5, "OFFICE"],
        [6, "PATIENT_CALLING"],
        [7, "BILLING"],
        // [9,"REPORTING"],

    ]);

    public teamData: any = [{ "teamName": "Internal Audit", "teamId": 3 }, { "teamName": "Lc3", "teamId": 4 }, { "teamName": "Office", "teamId": 5 }, { "teamName": "Patient Calling", "teamId": 6 }, { "teamName": "Billing", "teamId": 7 }];

    public attachmentType =
        [{"id":1,"value":"Consent Form"},
        {"id":2,"value":"Consent Form for Major Service"},
        {"id":3,"value":"Consult Form"},
        {"id":4,"value":"CRA Form"},
        {"id":5,"value":"EOB"},
        {"id":6,"value":"FDH Certification"},
        {"id":7,"value":"Insurance ID Card"},
        {"id":8,"value":"Intraoral Photos"},
        {"id":9,"value":"IV"},
        {"id":10,"value":"Lab Slip"},
        {"id":11,"value":"Narratives"},
        {"id":12,"value":"NCS Form"},
        {"id":13,"value":"Nitrous Certification (D9230)"},
        {"id":14,"value":"Nitrous Pre operative Checklist"},
        {"id":15,"value":"Others"},
        {"id":16,"value":"Perio Chart"},
        {"id":17,"value":"Pre-Auth"},
        {"id":18,"value":"Sedation Certification (D9248)"},
        {"id":19,"value":"Sedation Record"},
        {"id":20,"value":"X-ray: Bitewings"},
        {"id":21,"value":"X-ray: Cephlo"},
        {"id":22,"value":"X-ray: FMX"},
        {"id":23,"value":"X-ray: Pano"},
        {"id":24,"value":"X-ray: Periapical"},
        {"id":25,"value":"X-ray: Post-Op"},
        {"id":26,"value":"X-ray: Pre-Op"}]


}