package com.orange.transaction.save.service;

import com.orange.helper.dto.TransactionDto;
import com.orange.helper.builder.TransactionBuilder;
import com.orange.helper.model.TransactionEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class MapperTests {

    private TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Autowired
    TransactionBuilder transactionBuilder;

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        int transactionId = 142341234;

        TransactionDto transactionDto1 = transactionBuilder.createTransactionDto(transactionId);

        TransactionEntity entity = mapper.dtoToEntity(transactionDto1);

        assertThat(entity).isNotNull();
        assertEquals(transactionDto1.getTransactionId(), entity.getTransactionId());
        assertEquals(transactionDto1.getTransactionType(), entity.getTransactionType());
        assertEquals(transactionDto1.getIbanPayer(), entity.getIbanPayer());
        assertEquals(transactionDto1.getIbanPayee(), entity.getIbanPayee());
        assertEquals(transactionDto1.getCnpPayer(), entity.getCnpPayer());
        assertEquals(transactionDto1.getCnpPayee(), entity.getCnpPayee());
        assertEquals(transactionDto1.getNamePayer(), entity.getNamePayer());
        assertEquals(transactionDto1.getDescription(), entity.getDescription());
        assertEquals(transactionDto1.getAmount(), entity.getAmount());

        TransactionDto transactionDto2 = mapper.entityToDto(entity);

        assertThat(transactionDto2).isNotNull();
        assertEquals(transactionDto2.getTransactionId(), transactionDto2.getTransactionId());
        assertEquals(transactionDto2.getTransactionType(), entity.getTransactionType());
        assertEquals(transactionDto2.getIbanPayer(), entity.getIbanPayer());
        assertEquals(transactionDto2.getIbanPayee(), entity.getIbanPayee());
        assertEquals(transactionDto2.getCnpPayer(), entity.getCnpPayer());
        assertEquals(transactionDto2.getCnpPayee(), entity.getCnpPayee());
        assertEquals(transactionDto2.getNamePayer(), entity.getNamePayer());
        assertEquals(transactionDto2.getDescription(), entity.getDescription());
        assertEquals(transactionDto2.getAmount(), entity.getAmount());
    }
}
