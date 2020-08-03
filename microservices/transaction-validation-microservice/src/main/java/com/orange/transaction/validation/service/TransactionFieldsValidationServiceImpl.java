package com.orange.transaction.validation.service;

import com.orange.helper.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.orange.helper.constants.InvalidFieldConstants.*;

@Service
public class TransactionFieldsValidationServiceImpl {

    @Autowired
    CNPValidationServiceImpl cnpValidationService;

    @Autowired
    IbanValidationServiceImpl ibanValidationService;

    public String areAllTransactionFieldsValid(TransactionDto transactionDto) {
        if (transactionDto.getAmount() < 0) {
            return INVALID_AMOUNT;
        }

        if (transactionDto.getDescription() == null || transactionDto.getDescription().length() == 0) {
            return MISSING_DESCRIPTION;
        }

        if (transactionDto.getNamePayer() == null || transactionDto.getNamePayer().length() == 0) {
            return MISSING_NAME_PAYER;
        }

        if (transactionDto.getNamePayee() == null || transactionDto.getNamePayee().length() == 0) {
            return MISSING_NAME_PAYEE;
        }

        if (transactionDto.getTransactionType() == null) {
            return INVALID_TRANSACTION_TYPE;
        }

        String invalidCNP = cnpValidationService.areCNPsValid(transactionDto);
        if (invalidCNP != null) {
            return invalidCNP;
        }

        String invalidIBAN = ibanValidationService.areIbansValid(transactionDto);
        if (invalidIBAN != null) {
            return invalidIBAN;
        }

        return ALL_FIELDS_ARE_VALID;
    }
}
