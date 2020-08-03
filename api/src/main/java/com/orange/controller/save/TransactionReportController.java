package com.orange.controller.save;

import com.orange.helper.dto.ReportCompleteDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Api(description = "REST API for getting reports from database")
public interface TransactionReportController {

    /**
     * Sample usage: curl $HOST:$PORT/transactions-report/cnpPayer=1801121240214?delay=3?faultPercent=25
     *
     * @return the report
     */
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value    = "/transactions-report-from-database",
            produces = "application/json")
    Mono<ReportCompleteDto> getTransactionsReport(
            @RequestParam(value = "cnpPayer", required = true) String cnpPayer,
            @RequestParam(value = "delay",required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent);
}
