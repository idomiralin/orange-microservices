package com.orange.helper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private int transactionId;
    private TransactionType transactionType;
    private String ibanPayer;
    private String ibanPayee;
    private String cnpPayer;
    private String cnpPayee;
    private String namePayer;
    private String namePayee;
    private String description;
    private int amount;
}
