package com.orange.helper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportPerTypeDto {

    private TransactionType type;
    private int totalNumber;
    private int totalSum;
    List<TransactionDto> transactionDtos;

}
