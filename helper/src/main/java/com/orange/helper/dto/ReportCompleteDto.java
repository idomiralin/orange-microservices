package com.orange.helper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class ReportCompleteDto {
    private String namePayer;
    private String cnpPayer;
    private String ibanPayer;
    private ReportPerTypeDto ibanToIbanReport;
    private ReportPerTypeDto ibanToWalletReport;
    private ReportPerTypeDto walletToIbanReport;
    private ReportPerTypeDto walletToWalletReport;

    public ReportCompleteDto() {
        ibanToIbanReport = new ReportPerTypeDto();
        ibanToWalletReport = new ReportPerTypeDto();
        walletToIbanReport = new ReportPerTypeDto();
        walletToWalletReport = new ReportPerTypeDto();
    }
}