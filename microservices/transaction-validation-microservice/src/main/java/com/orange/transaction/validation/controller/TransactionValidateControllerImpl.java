package com.orange.transaction.validation.controller;

import com.orange.helper.dto.TransactionDto;
import com.orange.controller.validation.TransactionValidateController;
import com.orange.transaction.validation.integration.TransactionIntegrationImpl;
import com.orange.transaction.validation.service.TransactionFieldsValidationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static com.orange.helper.constants.InvalidFieldConstants.ALL_FIELDS_ARE_VALID;

@RestController
public class TransactionValidateControllerImpl implements TransactionValidateController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionValidateControllerImpl.class);

    @Autowired
    TransactionIntegrationImpl transactionValidationIntegration;

    @Autowired
    TransactionFieldsValidationServiceImpl transactionFieldsValidationService;

    @Override
    public String validateAndCreateTransaction(TransactionDto transactionDto) {
        String fieldValidityMessage = null;
        try {
            int transId = transactionDto.getTransactionId();
            LOG.debug("Validate transaction with id:  {}", transId);

            fieldValidityMessage = transactionFieldsValidationService.areAllTransactionFieldsValid(transactionDto);

            if (fieldValidityMessage.equals(ALL_FIELDS_ARE_VALID)) {
                LOG.debug("Create transaction with id {}", transId);
                transactionValidationIntegration.saveTransaction(transactionDto);
            }
        } catch (RuntimeException ex) {
            LOG.error("Creation of transaction has failed", ex.toString());
            throw ex;
        }
        return fieldValidityMessage;
    }

}