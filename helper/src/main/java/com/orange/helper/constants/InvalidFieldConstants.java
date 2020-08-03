package com.orange.helper.constants;

import org.springframework.stereotype.Component;

public class InvalidFieldConstants {
    private InvalidFieldConstants() {}

    public static final String INVALID_AMOUNT = "Invalid Amount. It should be greater than 0!";
    public static final String MISSING_DESCRIPTION = "Description is missing. It should not be empty!";
    public static final String MISSING_NAME_PAYER = "Payer Name is missing. The name of the payer should not be empty!";
    public static final String MISSING_NAME_PAYEE = "Payee Name is missing. The name of the payee should not be empty!";
    public static final String INVALID_TRANSACTION_TYPE = "Invalid transaction type. The transaction type should not be empty!";
    public static final String INVALID_CNP_PAYER = "Invalid payer CNP!";
    public static final String INVALID_CNP_PAYEE = "Invalid payee CNP!";
    public static final String INVALID_IBAN_PAYER = "Invalid payer IBAN!";
    public static final String INVALID_IBAN_PAYEE = "Invalid payee IBAN!";
    public static final String ALL_FIELDS_ARE_VALID = "All transaction fields are valid! The transaction got created!";
}
