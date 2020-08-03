package com.orange.helper.builder;

import com.orange.helper.dto.TransactionType;
import com.orange.helper.dto.TransactionDto;
import com.orange.helper.model.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionBuilder {

    private static TransactionDto.TransactionDtoBuilder buildTransactionDto(int transactionId) {
        return new Builder().buildTransactionDto(transactionId);
    }
    
    private static TransactionEntity.TransactionEntityBuilder buildTransactionEntity(int transactionId) {
        return new Builder().buildTransactionEntity(transactionId);
    }

    public TransactionDto createTransactionDto(int transactionId) {
        TransactionDto.TransactionDtoBuilder transactionDtoBuilder = buildTransactionDto(transactionId);
        transactionDtoBuilder.transactionType(TransactionType.IBAN_TO_IBAN);
        TransactionDto transactionDto = transactionDtoBuilder.build();

        return transactionDto;
    }

    public TransactionDto createTransactionDto(int transactionId, TransactionType transactionType) {
        TransactionDto.TransactionDtoBuilder transactionDtoBuilder = buildTransactionDto(transactionId);
        transactionDtoBuilder.transactionType(transactionType);
        TransactionDto transactionDto = transactionDtoBuilder.build();

        return transactionDto;
    }

    public TransactionEntity createTransactionEntity(int transactionId) {
        TransactionEntity.TransactionEntityBuilder transactionEntityBuilder = buildTransactionEntity(transactionId);
        transactionEntityBuilder.transactionType(TransactionType.IBAN_TO_IBAN);
        TransactionEntity transactionEntity = transactionEntityBuilder.build();

        return transactionEntity;
    }

    public TransactionEntity createTransactionEntity(int transactionId, TransactionType transactionType) {
        TransactionEntity.TransactionEntityBuilder transactionEntityBuilder = buildTransactionEntity(transactionId);
        transactionEntityBuilder.transactionType(transactionType);
        TransactionEntity transactionEntity = transactionEntityBuilder.build();

        return transactionEntity;
    }

    public boolean areTransactionsEqual(TransactionEntity actualEntity, TransactionEntity expectedEntity) {
        boolean areActualAndExpectedEntitiesEqual =
                        (actualEntity.getId().equals(expectedEntity.getId())) &&
                        (actualEntity.getVersion() == expectedEntity.getVersion()) &&
                        (actualEntity.getTransactionId() == expectedEntity.getTransactionId()) &&
                        (actualEntity.getNamePayer().equals(expectedEntity.getNamePayer())) &&
                        (actualEntity.getNamePayee().equals(expectedEntity.getNamePayee())) &&
                        (actualEntity.getCnpPayer().equals(expectedEntity.getCnpPayer())) &&
                        (actualEntity.getCnpPayee().equals(expectedEntity.getCnpPayee())) &&
                        (actualEntity.getIbanPayer().equals(expectedEntity.getIbanPayer())) &&
                        (actualEntity.getIbanPayee().equals(expectedEntity.getIbanPayee())) &&
                        (actualEntity.getDescription().equals(expectedEntity.getDescription())) &&
                        (actualEntity.getAmount() == expectedEntity.getAmount());

        return areActualAndExpectedEntitiesEqual;
    }
}
