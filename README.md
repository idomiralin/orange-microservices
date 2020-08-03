# orange microservices validation and save

In order to start the microservices environment, you need to run following commands:

1. Build the microservices and run the tests
   gradlew clean build

2. Download and build docker images
   docker-compose build
   
3. Start the docker containers
   docker-compose up
   
-------------------

In order to test the business logic, following REST APIs can be called:

Following API validates that all fields are correct and then saves the transaction in 
the mongo database
http://localhost:8080/validate-transaction

Body

{
   "transactionId":1,
   "ibanPayer":"RO09BCYP0000001234567890",
   "ibanPayee":"RO09BCYP0000001234567890",
   "cnpPayer":"2750331323927",
    "cnpPayee":"2750331323927",
    "namePayer":"Ronaldinho",
    "namePayee":"Gheorghe Hagi",
    "description":"Transfer datorie",
    "transactionType":"IBAN_TO_IBAN",
    "amount":123
    }

Following API displays report of all transaction types performed by a payer with mentioned
CNP:

http://localhost:8080/transactions-report?cnpPayer={cnpPayer}

For resilience testing 

http://localhost:8080/transactions-report?cnpPayer={cnpPayer}&delay={delay}&faultPercent={faultPercent}

-------------------------

The microservices can be run also locally as follows:
 1. By commenting out defined microservices 
 2. Starting all the containers(mongodb, rabbit/kafka, zookeeper) needed by the microservices -> docker-compose up
 3. Starting the main application classes
 4. In order to test the REST APIs, the above calls will be made, but the local port 9000
    will be used. 
    
--------------------------------