package com.orange.transaction.validation.service;

import com.orange.helper.constants.InvalidFieldConstants;
import com.orange.helper.dto.TransactionDto;
import org.springframework.stereotype.Service;

import static com.orange.helper.constants.InvalidFieldConstants.*;

@Service
public class CNPValidationServiceImpl {

    String areCNPsValid(TransactionDto transactionDto) {
        String payerCNP = transactionDto.getCnpPayer();
        String payeeCNP = transactionDto.getCnpPayee();

        boolean isPayerCNPValid = isCNPValid(payerCNP);
        boolean isPayeeCNPValid = isCNPValid(payeeCNP);

        if (!isPayerCNPValid) {
            return INVALID_CNP_PAYER;
        }

        if (!isPayeeCNPValid) {
            return INVALID_CNP_PAYEE;
        }

        return null;
    }

    boolean isCNPValid(String cnp) {
        long digitControl = calculateDigitControl(cnp);
        int lastDigitOfIban = cnp.charAt(cnp.length() - 1) - '0';
        if (digitControl == lastDigitOfIban) {
            return true;
        }
        return false;
    }

    public long calculateDigitControl(String cnp) {
        long sumControl = 0;
        String controlTemplate = "279146358279";
        //multiply each digit and make the sum
        for (int i = 0; i < cnp.length() - 1; i++) {
                int cnpCurrentDigit = cnp.charAt(i) - '0';
                int controlTemplateCurrentDigit = controlTemplate.charAt(i) - '0';
                sumControl += cnpCurrentDigit * controlTemplateCurrentDigit;
        }

        long sumControlRest = sumControl % 11;
        long digitControl;
        if (sumControlRest == 10) {
            digitControl = 1;
        } else {
            digitControl = sumControlRest;
        }
        return digitControl;
    }
}
