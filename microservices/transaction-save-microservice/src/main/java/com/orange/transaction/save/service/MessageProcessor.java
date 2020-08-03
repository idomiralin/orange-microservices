package com.orange.transaction.save.service;

import com.orange.event.Event;
import com.orange.helper.dto.TransactionDto;
import com.orange.controller.save.TransactionSaveController;
import com.orange.helper.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final TransactionSaveController service;

    @Autowired
    public MessageProcessor(TransactionSaveController service) {
        this.service = service;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, TransactionDto> event) {

        LOG.info("Process message created at {}...", event.getEventCreationTimestamp());

        switch (event.getEventType()) {

        case CREATE:
            TransactionDto transactionDto = event.getData();
            LOG.info("Create transaction with ID: {}", transactionDto.getTransactionId());
            service.saveTransaction(transactionDto);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
