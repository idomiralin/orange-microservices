package com.orange.helper.builder;

import com.orange.helper.dto.TransactionDto;
import com.orange.helper.model.TransactionEntity;

interface TransactionDtoBuilder {
    TransactionDto.TransactionDtoBuilder buildTransactionDto(int transactionId);
}

interface TransactionEntityBuilder {
    TransactionEntity.TransactionEntityBuilder buildTransactionEntity(int transactionId);
}

class Builder implements TransactionDtoBuilder, TransactionEntityBuilder {
    @Override
    public TransactionDto.TransactionDtoBuilder buildTransactionDto(int transactionId) {
        TransactionDto.TransactionDtoBuilder transactionDtoBuilder = TransactionDto.builder()
                .transactionId(transactionId)
                .cnpPayer("2750331323927")
                .cnpPayee("2750331323927")
                .ibanPayer("RO09BCYP0000001234567890")
                .ibanPayee("RO09BCYP0000001234567890")
                .namePayer("Gheorghe Hagi")
                .namePayee("Ronaldinho")
                .amount(1000000)
                .description("Bani de buzunar");

        return transactionDtoBuilder;
    }

    @Override
    public TransactionEntity.TransactionEntityBuilder buildTransactionEntity(int transactionId) {
        TransactionEntity.TransactionEntityBuilder transactionEntityBuilder = TransactionEntity.builder()
                .transactionId(transactionId)
                .cnpPayer("2750331323927")
                .cnpPayee("2750331323927")
                .ibanPayer("RO09BCYP0000001234567890")
                .ibanPayee("RO09BCYP0000001234567890")
                .namePayer("Gheorghe Hagi")
                .namePayee("Ronaldinho")
                .amount(1000000)
                .description("Bani de buzunar");

        return transactionEntityBuilder;
    }
}

