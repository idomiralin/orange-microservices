package com.orange.transaction.save.controller;

import com.orange.helper.dto.ReportCompleteDto;
import com.orange.helper.dto.ReportPerTypeDto;
import com.orange.helper.dto.TransactionDto;
import com.orange.helper.dto.TransactionType;
import com.orange.controller.save.TransactionReportController;
import com.orange.transaction.save.service.TransactionMapper;
import com.orange.transaction.save.persistence.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static reactor.core.publisher.Flux.empty;
import static reactor.core.publisher.Flux.concatDelayError;

@RestController
public class TransactionReportControllerImpl implements TransactionReportController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionReportControllerImpl.class);

    private final TransactionRepository repository;

    private final TransactionMapper mapper;

    @Autowired
    public TransactionReportControllerImpl(TransactionRepository repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ReportCompleteDto> getTransactionsReport(String cnpPayer, int delay, int faultPercent) {
        if (delay > 0) simulateDelay(delay);

        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        return Mono.zip(
                values -> buildReportComplete((List<TransactionDto>) values[0]),
                getTransactionsFromDb(cnpPayer).collectList())
                .doOnError(ex -> empty())
                .log();
    }

    private void simulateDelay(int delay) {
        LOG.debug("Sleeping for {} seconds...", delay);
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException ex) {
        }
        LOG.debug("Sleeping is over. Moving on...");
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        int randomThreshold = getRandomNumber(1, 100);
        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occured, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.debug("Bad luck, an error occured, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("something went wrong...");
        }
    }

    private final Random randomNumberGenerator = new Random();

    private int getRandomNumber(int min, int max) {
        if (max < min) {
            throw new RuntimeException("Max must be greater than min");
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }

    private Flux<TransactionDto> getTransactionsFromDb(String cnpPayer) {
        return repository.findByCnpPayer(cnpPayer)
                .map(t -> mapper.entityToDto(t))
                .log()
                .onErrorResume(error -> empty());
    }

    private ReportCompleteDto buildReportComplete(List<TransactionDto> transactionDtos) {
        ReportCompleteDto reportCompleteDto = new ReportCompleteDto();
        if (CollectionUtils.isEmpty(transactionDtos)) {
            return reportCompleteDto;
        }
        for(TransactionDto transactionDto : transactionDtos) {
            fillReport(reportCompleteDto, transactionDto);
        }
        return reportCompleteDto;
    }

    private void fillReport(ReportCompleteDto reportCompleteDto, TransactionDto transactionDto) {
        if (reportCompleteDto.getCnpPayer() == null) {
            reportCompleteDto.setCnpPayer(transactionDto.getCnpPayer());
        }
        if (reportCompleteDto.getIbanPayer() == null) {
            reportCompleteDto.setIbanPayer(transactionDto.getIbanPayer());
        }
        if (reportCompleteDto.getNamePayer() == null) {
            reportCompleteDto.setNamePayer(transactionDto.getNamePayer());
        }
        switch (transactionDto.getTransactionType()) {
            case IBAN_TO_IBAN:
                reportCompleteDto.getIbanToIbanReport().setType(TransactionType.IBAN_TO_IBAN);
                this.buildReportPerType(reportCompleteDto.getIbanToIbanReport(), transactionDto);
                break;
            case WALLET_TO_IBAN:
                reportCompleteDto.getWalletToIbanReport().setType(TransactionType.WALLET_TO_IBAN);
                this.buildReportPerType(reportCompleteDto.getWalletToIbanReport(), transactionDto);
                break;
            case IBAN_TO_WALLET:
                reportCompleteDto.getIbanToWalletReport().setType(TransactionType.IBAN_TO_WALLET);
                this.buildReportPerType(reportCompleteDto.getIbanToWalletReport(), transactionDto);
                break;
            case WALLET_TO_WALLET:
                reportCompleteDto.getWalletToWalletReport().setType(TransactionType.WALLET_TO_WALLET);
                this.buildReportPerType(reportCompleteDto.getWalletToWalletReport(), transactionDto);
                break;
        }
    }

    private void buildReportPerType(ReportPerTypeDto reportPerTypeDto, TransactionDto transactionDto) {
        reportPerTypeDto.setTotalNumber(reportPerTypeDto != null ? reportPerTypeDto.getTotalNumber() + 1 : 0);
        reportPerTypeDto.setTotalSum(reportPerTypeDto != null ? reportPerTypeDto.getTotalSum() + transactionDto.getAmount() : 0);
        List<TransactionDto> reportPerTypetransactionsList = reportPerTypeDto.getTransactionDtos();
        if (reportPerTypetransactionsList == null) {
            reportPerTypetransactionsList = new ArrayList<>();
        }
        reportPerTypetransactionsList.add(transactionDto);
        reportPerTypeDto.setTransactionDtos(reportPerTypetransactionsList);
    }
}
