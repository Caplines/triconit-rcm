import { Injectable } from '@angular/core';
import { BaseService } from './base-service.service';
import { HttpClient } from '@angular/common/http';
import { ClaimDownloadModel } from '../models/download/claim-dw-model';
import { TokenStorageService } from '../service/token-storage.service';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class DownLoadService extends BaseService {
    setPaddingContainer: boolean = false;

    constructor(router: Router, http: HttpClient, tokenStorage: TokenStorageService) {
        super(router, http, tokenStorage);
    }



    dowbnloadListofClaimPdfCsv(params: any, pdfcsv: string, callback: any) {
        this.postDataPdf(params, this.httpUrl['listOfClaim'] + "/" + pdfcsv, callback);
    }


    saveBolbData(data: Response, fileName: string) {
        const a: any = document.createElement('a');
        document.body.appendChild(a);
        console.log(data);
        a.style = 'display: none';
        const blob = new Blob([<any>data], { type: 'application/pdf' }),
            url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url);

    }
    /*example
    saveToPdf(divName: any) {
        console.log(this.filteredItems);
        let data = { "fileName": "test", "data": this.filteredItems };

        this.downLoadService.dowbnloadListofClaimPdfCsv(data, "pdf",
            (res: any) => {
                if (res.status) {

                    this.downLoadService.saveBolbData(res.body, "nameoffile.pdf");
                }
            });
        }
    } */
}
