package com.orange.controller.save;

import com.orange.helper.dto.TransactionDto;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Api(description = "REST API for CRUD  operations")
public interface TransactionSaveController {

    TransactionDto saveTransaction(@RequestBody TransactionDto body);

    @GetMapping(
            value    = "/transaction/{transactionId}",
            produces = "application/json")
    Mono<TransactionDto> getTransaction(
            @PathVariable(value = "transactionId", required = true) int transactionId);

}