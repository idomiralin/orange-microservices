package com.orange.transaction.save.persistence;

import com.orange.helper.builder.TransactionBuilder;
import com.orange.helper.model.TransactionEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceLayerTests {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionBuilder transactionBuilder;

    private TransactionEntity savedEntity;

    @Before
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
        int transactionId = 4444;
        TransactionEntity entity = transactionBuilder.createTransactionEntity(transactionId);
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return transactionBuilder.areTransactionsEqual(entity, savedEntity);
                })
                .verifyComplete();
    }


    @Test
    public void testTransactionCreated() {

        int transactionId = 22222;
        TransactionEntity newEntity = transactionBuilder.createTransactionEntity(transactionId);

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(createdEntity -> newEntity.getTransactionId() == createdEntity.getTransactionId())
                .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> transactionBuilder.areTransactionsEqual(newEntity, foundEntity))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
    public void testTransactionUpdated() {
        savedEntity.setNamePayer("n2");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getNamePayer().equals("n2"))
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getNamePayer().equals("n2"))
                .verifyComplete();
    }

    @Test
    public void testTransactionDeleted() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
    public void testFindByTransactionId() {

        StepVerifier.create(repository.findByTransactionId(savedEntity.getTransactionId()))
                .expectNextMatches(foundEntity -> transactionBuilder.areTransactionsEqual(savedEntity, foundEntity))
                .verifyComplete();
    }

    @Test
    public void testDuplicateError() {
        int alreadySavedTransactionId = savedEntity.getTransactionId();
        TransactionEntity entity = transactionBuilder.createTransactionEntity(alreadySavedTransactionId);
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
    public void testOptimisticLockError() {

        // Store the saved entity in two separate entity objects
        TransactionEntity entity1 = repository.findById(savedEntity.getId()).block();
        TransactionEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setNamePayer("Michael Jordan");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getNamePayer().equals("Michael Jordan"))
                .verifyComplete();
    }


}
