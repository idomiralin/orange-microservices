package com.orange.helper.model;

import com.orange.helper.dto.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="transactions")
public class TransactionEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
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
