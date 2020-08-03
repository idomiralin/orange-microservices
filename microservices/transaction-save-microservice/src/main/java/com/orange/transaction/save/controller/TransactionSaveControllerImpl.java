package com.orange.transaction.save.controller;

import com.orange.helper.dto.ReportCompleteDto;
import com.orange.helper.dto.TransactionDto;
import com.orange.controller.save.TransactionSaveController;
import com.orange.helper.exceptions.NotFoundException;
import com.orange.helper.model.TransactionEntity;
import com.orange.transaction.save.service.TransactionMapper;
import com.orange.transaction.save.persistence.TransactionRepository;
import com.orange.helper.exceptions.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

@RestController
public class TransactionSaveControllerImpl implements TransactionSaveController {

    private final TransactionRepository repository;

    private final TransactionMapper mapper;

    @Autowired
    public TransactionSaveControllerImpl(TransactionRepository repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TransactionDto saveTransaction(TransactionDto body) {

        if (body.getTransactionId() < 1) throw new InvalidInputException("Invalid transactionId: " + body.getTransactionId());

        TransactionEntity entity = mapper.dtoToEntity(body);
        Mono<TransactionDto> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, Transaction Id: " + body.getTransactionId()))
            .map(e -> mapper.entityToDto(e));

        return newEntity.block();

    }

    @Override
    public Mono<TransactionDto> getTransaction(int transactionId) {
        if (transactionId < 1) throw new InvalidInputException("Invalid transactionId: " + transactionId);

        return repository.findByTransactionId(transactionId)
                .switchIfEmpty(error(new NotFoundException("No transaction found for transactionId: " + transactionId)))
                .log()
                .map(transactionEntity -> mapper.entityToDto(transactionEntity));
    }


}