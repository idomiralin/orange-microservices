package com.orange.helper.builder;

import com.orange.helper.dto.ReportCompleteDto;
import com.orange.helper.dto.ReportPerTypeDto;
import com.orange.helper.dto.TransactionDto;
import com.orange.helper.dto.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportBuilder {
    @Autowired
    TransactionBuilder transactionBuilder;

    public ReportPerTypeDto buildIBANToIBANFallbackReport() {
        TransactionDto transactionDto = transactionBuilder.createTransactionDto(1, TransactionType.IBAN_TO_IBAN);
        List<TransactionDto> transactionDtos = new ArrayList<>();
        transactionDtos.add(transactionDto);

        ReportPerTypeDto ibanToIbanReport = new ReportPerTypeDto(TransactionType.IBAN_TO_IBAN, 1, 15, transactionDtos);
        return ibanToIbanReport;
    }

    public ReportPerTypeDto buildIBANToWalletFallbackReport() {
        TransactionDto transactionDto = transactionBuilder.createTransactionDto(1, TransactionType.IBAN_TO_IBAN);
        List<TransactionDto> transactionDtos = new ArrayList<>();
        transactionDtos.add(transactionDto);

        ReportPerTypeDto iBANToWalletReport = new ReportPerTypeDto(TransactionType.IBAN_TO_WALLET, 1, 5, transactionDtos);
        return iBANToWalletReport;
    }

    public ReportPerTypeDto buildWalletToIBANFallbackReport() {
        TransactionDto transactionDto = transactionBuilder.createTransactionDto(1, TransactionType.WALLET_TO_IBAN);
        List<TransactionDto> transactionDtos = new ArrayList<>();
        transactionDtos.add(transactionDto);

        ReportPerTypeDto walletToIBANReport = new ReportPerTypeDto(TransactionType.WALLET_TO_IBAN, 1, 7, transactionDtos);
        return walletToIBANReport;
    }

    public ReportPerTypeDto buildWalletToWalletFallbackReport() {
        TransactionDto transactionDto = transactionBuilder.createTransactionDto(1, TransactionType.WALLET_TO_WALLET);
        List<TransactionDto> transactionDtos = new ArrayList<>();
        transactionDtos.add(transactionDto);

        ReportPerTypeDto walletToWalletReport = new ReportPerTypeDto(TransactionType.WALLET_TO_WALLET, 1, 17, transactionDtos);
        return walletToWalletReport;
    }

    public ReportCompleteDto buildCompleteFallbackReport() {
        ReportPerTypeDto ibanToIbanReport = buildIBANToIBANFallbackReport();
        ReportPerTypeDto ibanToWalletReport = buildIBANToWalletFallbackReport();
        ReportPerTypeDto walletToIbanReport = buildWalletToIBANFallbackReport();
        ReportPerTypeDto walletToWalletReport = buildWalletToWalletFallbackReport();

        return new ReportCompleteDto("Gheorghe Hagi","CNP Fallback", "IBAN Fallback", ibanToIbanReport,
                ibanToWalletReport, walletToIbanReport,walletToWalletReport);
    }

}
