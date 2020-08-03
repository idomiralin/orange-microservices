package com.orange.controller.validation;

import com.orange.helper.dto.TransactionDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Api(description = "REST API for validating transactions.")
public interface TransactionValidateController {

    /**
     * Example
     *
     * Create a post request
     * curl -X POST $HOST:$PORT/validate-transaction \
     *   -H "Content-TransactionType: application/json" --data \
     *   for body use following:
     *   '{
     *    "transactionId":3,
     *    "ibanPayer":"RO09BCYP0000001234567890",
     *    "ibanPayee":"RO09BCYP0000001234567890",
     *    "cnpPayer":"2750331323927",
     *     "cnpPayee":"2750331323927",
     *     "namePayer":"Idomir Alin",
     *     "namePayee":"Gheorghe Hagi",
     *     "description":"Transfer datorie",
     *     "transactionType":"IBAN_TO_IBAN",
     *     "amount":123}'
     *
     * @param body
     */
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad Request, request format is invalid"),
        @ApiResponse(code = 422, message = "Entity cannot be processed, because of incorrect input parameters")
    })
    @PostMapping(
        value    = "/validate-transaction",
        consumes = "application/json")
    String validateAndCreateTransaction(@Valid @RequestBody TransactionDto body);

}