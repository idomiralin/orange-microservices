package com.orange.transaction.validation.service;

import static com.orange.helper.constants.InvalidFieldConstants.*;
import com.orange.helper.dto.TransactionDto;
import org.apache.commons.validator.routines.IBANValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IbanValidationServiceImpl {

    IBANValidator ibanValidator = IBANValidator.getInstance();

    public String areIbansValid(TransactionDto transactionDto) {
        String payerIban = transactionDto.getIbanPayer();
        String payeeIban = transactionDto.getIbanPayee();

        boolean isPayerIbanValid = isIbanValid(payerIban);
        boolean isPayeeIbanValid = isIbanValid(payeeIban);

        if (!isPayerIbanValid) {
            return INVALID_IBAN_PAYER;
        }

        if (!isPayeeIbanValid) {
            return INVALID_IBAN_PAYEE;
        }

        return null;
    }

    private boolean isIbanValid(String iban) {
        if (ibanValidator.isValid(iban)) {
            return true;
        }

        return false;
    }
}

